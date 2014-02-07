# -*- coding: utf-8 -*-
"""
Copyright (C) 2013-2014 TopCoder Inc., All Rights Reserved.
This module defines the web controller classes by using cherrypy.
This module resides in Python source file httpservices.py
Thread Safety:
The implementation  should be thread safe.

v1.1 - Healthcare Fraud Prevention Release Assembly v1.0
     - updated for added StudyID in data request handler
     - updated for analysis result handler

@author:  TCSASSEMBLER
@version: 1.1
"""
import os,json,base64,zlib,urllib,tempfile,mmap,logging
import cherrypy
import isodate
from threading import Thread
from datetime import datetime
from datetime import timezone
import xml.etree.ElementTree as ET
from errors import PartnerClientError
from settings import HFPP_NODE_HTTP_SERVICE_BASE_URL
from settings import HFPP_PARTNER_USERNAME
from settings import HFPP_PARTNER_PASSWORD
from settings import CA_CERTIFICATE_FILE
from settings import PARTNER_IMMEDIATE_FULLFIL
from settings import STUDY_REPORT_DIRECTORY
from datafulfillment import can_fulfill_data_request
from dataappliance import query_data
from dataconversion import convert_data
from logginghelper import method_enter
from logginghelper import method_exit
from logginghelper import method_error
from validationhelper import check_string
from validationhelper import check_datetime
from validationhelper import check_bool

class DataRequestHandler:
    """
    DataRequestHandler class defines the contract to handle a received data request.
    This class resides in Python source file httpservices.py
    Thread Safety:
    This class is thread safe because it is immutable.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    def handle_data_request(self,request_id, study_id, query,expiration_time,
                            cache_available=False,cache_timestamp=None,
                            force_fullfil=False):
        """
           This method is used to handle data request.
           This method will not throw exceptions. Any error should be caught and logged.
           @param self the DataRequestHandler itself, it should be DataRequestHandler
           @param request_id the request ID,it is supposed to be a non-None/empty str. Required.
           @param study_id the study ID,it is supposed to be a non-None/empty str. Required. 
           @param query the query string,it is supposed to be a non-None/empty str. Required.
           @param expiration_time the request expiration time,it is supposed to be a non-None datetime. Required.
           @param cache_available whether cache is available,it is supposed to be a bool. Optional, default to False.
           @param cache_timestamp the cache timestamp,it is supposed to be a datetime. Optional, default to None.
           @param force_fullfil this parameter is set to True when this method is called by decision module.
        """
        signature='hfppnetwork.partner.httpservices.DataRequestHandler.handle_data_request'
        method_enter(signature,{
            "self":self,
            "request_id":request_id,
            "study_id":study_id,
            "query":query,
            "expiration_time":expiration_time,
            "cache_available":cache_available,
            "cache_timestamp":cache_timestamp
        })
        # Dictionary to hold data query result file names
        query_result_file_names = {}
        try:
            #check input arguments
            check_string("request_id",request_id)
            check_string("study_id",study_id)
            check_string("query",query)
            check_datetime("expiration_time",expiration_time)
            check_bool("cache_available",cache_available)
            if cache_timestamp is not None:
               check_datetime("cache_timestamp",cache_timestamp)
             # Parse the query string
            try:
                query_dict = json.loads(query)
            except ValueError as e:
                 query_dict = None
                 method_error(signature, e)
            # Check if we can fulfill the data request
            can_fulfill_request = can_fulfill_data_request(request_id, study_id, query,
                                                           expiration_time, cache_available,
                                                           cache_timestamp, force_fullfil)
            # Dictionary to hold data conversion result file names
            conversion_result_file_names = {}
            # Data Response XML file name
            response_xml_file_name = None
            # Compressed file name
            compressed_file_name = None
            logging.debug('%s:%s', 'can_fulfill_request', can_fulfill_request)
            #can_fulfill_request
            if query_dict is not None and 'file_types' in query_dict \
                and 'logical_expressions' in query_dict and can_fulfill_request:
                # Can fulfill the request, create temporary files
                for file_type in query_dict['file_types']:
                    query_result_file_names[file_type] = tempfile.NamedTemporaryFile(delete=False).name
                    conversion_result_file_names[file_type] = tempfile.NamedTemporaryFile(delete=False).name
                response_xml_file_name = tempfile.NamedTemporaryFile(delete=False).name
                compressed_file_name = tempfile.NamedTemporaryFile(delete=False).name
                # Query data
                use_cache = query_data(query_dict['file_types'], query_dict['logical_expressions'],
                                       query_result_file_names,
                                       cache_timestamp if cache_available else None)
                with open(response_xml_file_name, 'ab') as response_xml_file:
                    # Write XML
                    xml = '<?xml version="1.0" encoding="utf-8"?>' \
                        '<DataResponse>' \
                        '<RequestID>{request_id}</RequestID>' \
                        '<RequestDenied>false</RequestDenied>' \
                        '<ErrorMessage></ErrorMessage>' \
                        '<Data useCache="{use_cache}"><![CDATA['.\
                        format(request_id=request_id, use_cache='true' if use_cache else 'false')
                    response_xml_file.write(xml.encode('utf-8'))
                    if not use_cache:
                        logging.debug('not use cache will use result from converted data')
                        # Convert data
                        for file_type in query_dict['file_types']:
                            convert_data(file_type, query_result_file_names[file_type],
                                         conversion_result_file_names[file_type])
                        # Aggregate and compress data
                        compressor = zlib.compressobj(level=9)
                        with open(compressed_file_name, 'wb') as out_file:
                            for file_type in query_dict['file_types']:
                                with open(conversion_result_file_names[file_type], 'rb') as in_file:
                                    out_file.write(compressor.compress(in_file.read()))
                            out_file.write(compressor.flush())
                        # Encode in Base64
                        with open(compressed_file_name, 'rb') as in_file:
                            base64.encode(in_file, response_xml_file)
                    # Write XML
                    response_xml_file.write(']]></Data></DataResponse>'.encode('utf-8'))
            # POST XML to Network Node /data_response service
            if datetime.now(timezone.utc) < expiration_time:
                logging.debug('post to data response url %s%s',
                              HFPP_NODE_HTTP_SERVICE_BASE_URL ,'/data_response')
                # Only POST the XML if the request has not been expired
                request = urllib.request.Request(HFPP_NODE_HTTP_SERVICE_BASE_URL + '/data_response')
                request.add_header('Content-Type','application/xml;charset=utf-8')
                request.add_header('x-hfpp-username', HFPP_PARTNER_USERNAME)
                request.add_header('x-hfpp-password', HFPP_PARTNER_PASSWORD)
                if response_xml_file_name is not None and can_fulfill_request:
                    with open(response_xml_file_name, 'rb') as in_file,\
                        mmap.mmap(in_file.fileno(), 0, access=mmap.ACCESS_READ) as data_response_xml:
                        try:
                            resp = urllib.request.urlopen(request, data_response_xml,
                                                          cafile=CA_CERTIFICATE_FILE, cadefault=True)
                              # Parse response XML
                            resp_content = resp.read().decode('utf-8')
                            logging.debug('response code:%s',resp.getcode())
                            logging.debug('response:%s',resp_content)
                        except urllib.error.HTTPError as e:
                                method_error(signature, e)
                                self._handle_error_response(e)
                else:
                    data_response_xml = '<?xml version="1.0" encoding="utf-8"?>' \
                        '<DataResponse>' \
                        '<RequestID>{request_id}</RequestID>' \
                        '<RequestDenied>true</RequestDenied>' \
                        '<ErrorMessage>{waitApproval}</ErrorMessage>' \
                        '<Data></Data>' \
                        '</DataResponse>'.format(request_id=request_id,
                        waitApproval=('' if PARTNER_IMMEDIATE_FULLFIL else 'Waiting Approval'))
                    logging.debug('post data response xml %s', data_response_xml)
                    try:
                         resp = urllib.request.urlopen(request, data_response_xml.encode('utf-8'),
                                                       cafile=CA_CERTIFICATE_FILE, cadefault=True)
                         # Parse response XML
                         resp_content = resp.read().decode('utf-8')
                         logging.debug('response code:%s',resp.getcode())
                         logging.debug('response:%s',resp_content)
                    except urllib.error.HTTPError as e:
                         method_error(signature, e)
                         self._handle_error_response(e)
            else:
                # Request expired, log error
                logging.error('Request expired')
            method_exit(signature)
        except Exception as e:
                # log error
                method_error(signature, e)
        finally:
            if query_dict is not None and 'file_types' in  query_dict:
                # Remove temporary files
                for file_type in query_dict['file_types']:
                    if file_type in query_result_file_names:
                        self._remove_file(query_result_file_names[file_type])
                    if file_type in conversion_result_file_names:
                        self._remove_file(conversion_result_file_names[file_type])
            self._remove_file(compressed_file_name)
            self._remove_file(response_xml_file_name)

    def _remove_file(self,file_name):
        """
           This method is used to remove file.
           @param self the DataRequestHandler itself, it should be DataRequestHandler
           @param file_name the file name,it is supposed to be a str, can be None/empty.
           @throw Exception Any error should be raised to caller.
        """
        signature='hfppnetwork.partner.httpservices.DataRequestHandler._remove_file'
        method_enter(signature,{
            "self":self,
            "file_name":file_name
        })
        try:
            if file_name is not None:
                check_string("file_name",file_name)
                if file_name and os.path.exists(file_name):
                    os.remove(file_name)
            method_exit(signature)
        except Exception as e:
            method_error(signature, e)

    def _handle_error_response(self,e):
         """
           This method is used to handle http error.
           @param self the DataRequestHandler itself, it should be DataRequestHandler
           @param e the http error.
           @throw Exception Any error should be raised to caller.
        """
         logging.error('http error code:%s',e.code)
          # Not succeeded
          # 400, 401, 403 or 500
         resp_content = e.read().decode('utf-8')
         logging.error('error response:%s',resp_content)
         root = ET.fromstring(resp_content)
         error_code = root.findtext('./ErrorCode')
         error_message = root.findtext('./ErrorMessage')
         # Log error code and error message
         logging.error('error code:%s',error_code)
         logging.error('error message:%s',error_message)

def handle_deny_operation(request_id):
    '''
    Handle the manually deny operation.
    
    @param request_id: The request_id to response to.
    '''
    logging.debug('post to data response url %s%s',
                  HFPP_NODE_HTTP_SERVICE_BASE_URL ,'/data_response')
    request = urllib.request.Request(HFPP_NODE_HTTP_SERVICE_BASE_URL + '/data_response')
    request.add_header('Content-Type','application/xml;charset=utf-8')
    request.add_header('x-hfpp-username', HFPP_PARTNER_USERNAME)
    request.add_header('x-hfpp-password', HFPP_PARTNER_PASSWORD)
    data_response_xml = '<?xml version="1.0" encoding="utf-8"?>' \
        '<DataResponse>' \
        '<RequestID>{request_id}</RequestID>' \
        '<RequestDenied>true</RequestDenied>' \
        '<ErrorMessage>This request was denied manually.</ErrorMessage>' \
        '<Data></Data>' \
        '</DataResponse>'.format(request_id=request_id[0])
    logging.debug('post data response xml %s', data_response_xml)
    try:
         resp = urllib.request.urlopen(request, data_response_xml.encode('utf-8'),
                                       cafile=CA_CERTIFICATE_FILE, cadefault=True)
         # Parse response XML
         resp_content = resp.read().decode('utf-8')
         logging.debug('response code:%s',resp.getcode())
         logging.debug('response:%s',resp_content)
    except urllib.error.HTTPError as e:
         logging.exception()

class AnalysisResultHandler():
    """
    AnalysisResultHandler class defines the contract to handle a received analysis result request.
    This class resides in Python source file httpservices.py
    Thread Safety:
    This class is thread safe because it is immutable.
    @author:  TCSASSEMBLER
    @version: 1.0
    @since Healthcare Fraud Prevention Release Assembly v1.0
    """
    def handle_analysis_result(self, request_id, study_id, result):
        """
        This method is used to handle analysis result.
        This method will not throw exceptions. Any error should be caught and logged.
        @param self the AnalysisResultHandler itself, it should be AnalysisResultHandler
        @param request_id the request ID,it is supposed to be a non-None/empty str. Required.
        @param study_id the study ID,it is supposed to be a non-None/empty str. Required.
        @param result the analysis result. Required
        """
        signature='hfppnetwork.partner.httpservices.AnalysisResultHandler.handle_analysis_result'
        method_enter(signature,{
            "self":self,
            "request_id":request_id,
            "result":result
        })
        uncompressed_file = tempfile.NamedTemporaryFile(delete=False).name
        try:
            #check input arguments
            check_string("request_id",request_id)
            check_string("study_id", study_id)
            check_string("result",result)

            decoded_data = zlib.decompress(base64.b64decode(result))
            file_name = STUDY_REPORT_DIRECTORY + "/" + study_id + ".xlsx"
            with open(file_name, "wb") as out_file:
                out_file.write(decoded_data)

            method_exit(signature)
        except Exception as e:
            # log error
            method_error(signature, e)
        finally:
            os.remove(uncompressed_file)
            
class PartnerHTTPServices:
    """
    PartnerHTTPServices class defines the CherryPy handler to serve Partner Client HTTP services.
    This class resides in Python source file httpservices.py
    Thread Safety:
    This class is thread safe because it is immutable.
    CherryPy makes use of thread local data for HTTP request/response data, hence the use of CherryPy module is safe.
    
    v1.1 - Healthcare Fraud Prevention Release Assembly v1.0
         - added method analysis_result
    
    @author:  TCSASSEMBLER
    @version: 1.1
    """
    @cherrypy.expose()
    @cherrypy.tools.allow(methods=['POST'])
    @cherrypy.tools.accept(media=['application/xml'])
    def data_request(self):
      """
       This method is used to serve data request partner client http service.
       @param self the PartnerHTTPServices itself, it should be PartnerHTTPServices
       @throws PartnerClientError throws if request body is empty
       @throws Exception any error should be raised to caller.
       CherryPy will handle the error and translate to HTTP code 500 (refer to partnercli#handle_error)
      """
      signature='hfppnetwork.partner.httpservices.PartnerHTTPServices.data_request'
      method_enter(signature,{"self":self})
      # Read the data request XML
      request_body = cherrypy.request.body.read().decode("utf-8")
      logging.debug('%s:%s', 'request_body', request_body)
      if len(request_body)==0:
        raise PartnerClientError("request body can not be empty")
      # Parse data request XML
      root = ET.fromstring(request_body)
      request_id = root.findtext('./RequestID')
      study_id = root.findtext('./StudyID')
      query = root.findtext('./Query')
      expiration_time = isodate.parse_datetime(root.findtext('./ExpirationTime'))
      #CacheAvailable and CacheTimestamp could not exist
      cache_available = 'true' == root.findtext('./CacheAvailable')
      cache_timestamp = None
      if root.findtext('./CacheTimestamp'):
          cache_timestamp =isodate.parse_datetime(root.findtext('./CacheTimestamp'))
      # Kick off a new thread to handle the request
      handler = DataRequestHandler()
      t = Thread(target=handler.handle_data_request, args=(request_id, study_id, query,
                                                           expiration_time, cache_available, cache_timestamp,))
      t.daemon = False
      t.start()
      method_exit(signature)

    @cherrypy.expose()
    @cherrypy.tools.allow(methods=['POST'])
    @cherrypy.tools.accept(media=['application/xml'])
    def analysis_result(self):
        """
        This method is used to serve analysis result request partner client http service.
        @param self the PartnerHTTPServices itself, it should be PartnerHTTPServices
        @throws PartnerClientError throws if request body is empty
        @throws Exception any error should be raised to caller.
        CherryPy will handle the error and translate to HTTP code 500 (refer to partnercli#handle_error)
        """
        signature='hfppnetwork.partner.httpservices.PartnerHTTPServices.analysis_result'
        method_enter(signature,{"self":self})
        # Read the data request XML
        request_body = cherrypy.request.body.read().decode("utf-8")
        logging.debug('%s:%s', 'request_body', request_body)
        if len(request_body)==0:
            raise PartnerClientError("request body can not be empty")
        # Parse data request XML
        root = ET.fromstring(request_body)
        request_id = root.findtext('./RequestID')
        study_id = root.findtext('./StudyID')
        result = root.findtext('./Result')

        # Kick off a new thread to handle the request
        handler = AnalysisResultHandler()
        t = Thread(target=handler.handle_analysis_result, args=(request_id, study_id, result))
        t.daemon = False
        t.start()
        method_exit(signature)

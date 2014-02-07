# -*- coding: utf-8 -*-
"""
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.

This is module that represents all the views.

@author:  TCSASSEMBLER
@version: 1.0
"""
from django.http.response import HttpResponse
from django.views.decorators.http import require_http_methods
from xml.etree import ElementTree
import urllib
from hfppnetwork.settings import DATA_REQUEST_URL
import sys
import tempfile
import gzip
import os
import base64
from threading import Thread
import logging

logger = logging.getLogger(__name__)

response_queue = []

def send_data_response():
    global response_queue
    logger.debug('data_response %s,%s'%(response_queue, DATA_REQUEST_URL))

    for xmlstr in response_queue:
        request_to_node = urllib.request.Request(DATA_REQUEST_URL)
        request_to_node.add_header('Content-Type','application/xml;charset=utf-8')
        urllib.request.urlopen(request_to_node, xmlstr.encode())
    
    response_queue = []

@require_http_methods(["GET", "POST"])
def data_request(request):
    global response_queue
    data_request_xml = request.body
    logger.debug('data_request %s', request.readline())
    root = ElementTree.fromstring(data_request_xml)
    data_request_id = root.findtext('./RequestID')
    if root.tag == 'AnalysisResult':
        logger.debug('AnalysisResult %s'%data_request_id);
        return HttpResponse('<?xml version="1.0" encoding="UTF-8" ?><result>OK</result>', content_type='application/xml')
    query = root.findtext('./Query')
    partnerIds = root.findall('./RequestedPartners/PartnerID')
    r = '<?xml version="1.0" encoding="UTF-8" ?><DataResponse><RequestID>%s</RequestID><RespondentID>%s</RespondentID><RequestDenied>%s</RequestDenied><Data><![CDATA[ %s ]]></Data></DataResponse>'
    
    for partnerId in partnerIds:
        if 'Blood' in query or partnerId == 'hfpp_partner_9':
            logger.debug('pending response for %s'%partnerId);
            continue #mock pending if query for Blood Deductible Liability Amount
        filename = 'test_files/default.xml'
        denied = 'false'
        lastchar = query.strip()[-1]
        if not lastchar.isdigit() or partnerId.text == 'hfpp_partner_10':
            denied = 'true' #mock denied if query end without digit
            logger.debug('will response with denied for %s'%partnerId);
        elif lastchar == '9':
            if partnerId.text == 'hfpp_partner_1':
                filename = 'test_files/beneficiary_summary.xml'
                logger.debug('will response with beneficiary_summary for %s'%partnerId)
            elif partnerId.text == 'hfpp_partner_2':
                filename = 'test_files/carrier_claim.xml'
                logger.debug('will response with carrier_claim for %s'%partnerId)
            elif partnerId.text == 'hfpp_partner_3':
                filename = 'test_files/inpatient_claim.xml'
                logger.debug('will response with inpatient_claim for %s'%partnerId)
            elif partnerId.text == 'hfpp_partner_4':
                filename = 'test_files/outpatient_claim.xml'
                logger.debug('will response with outpatient claim for %s'%partnerId)

        with open(filename, 'rb') as in_file:
            data = base64.encodebytes(gzip.compress(in_file.read())).decode()
        xmlstr = r%(data_request_id, partnerId.text, denied, data)
        response_queue.append(xmlstr)

    return HttpResponse('<?xml version="1.0" encoding="UTF-8" ?><result>OK</result>', content_type='application/xml')

@require_http_methods(["GET", "POST"])
def data_response(request):
    send_data_response()
    return HttpResponse('<?xml version="1.0" encoding="UTF-8" ?><result>OK</result>', content_type='application/xml')

@require_http_methods(["GET", "POST"])
def general_service(request):
    data_request_xml = request.body
    root = ElementTree.fromstring(data_request_xml)
    partnerID = root.findtext('./PartnerID')
    logger.info('general_service for partnerID:%s'%partnerID)
    xmlstr = '<?xml version="1.0" encoding="UTF-8" ?><ResponseData>'\
                '<NumberOfDataRequestsReceived>1</NumberOfDataRequestsReceived>' \
                '<NumberOfDataRequestsInitiated>1</NumberOfDataRequestsInitiated>' \
                '<NumberOfDataRequestsResponded>1</NumberOfDataRequestsResponded>' \
                '</ResponseData>'
    return HttpResponse(xmlstr, content_type='application/xml')
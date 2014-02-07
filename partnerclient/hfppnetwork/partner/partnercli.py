# -*- coding: utf-8 -*-
"""
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.

This is the module that defines command line interface (CLI), it is the __main__ script.
It defines handle_error() function for handling errors in CherryPy handlers, and the __main__ code block.
This module resides in Python source file partnercli.py
Thread Safety:
It is thread safe because no module-level variable is used.
This script will be called to start a CherryPy daemon thread to listen for data requests.
@author:  TCSASSEMBLER
@version: 1.0
"""
import cherrypy
import logging
import logging.config
import os
import uuid
import sys
from settings import HFPP_NODE_HTTP_SERVICE_BASE_URL
from settings import HFPP_PARTNER_USERNAME
from settings import HFPP_PARTNER_PASSWORD
from settings import HFPP_PARTNER_ID
from settings import CA_CERTIFICATE_FILE
from settings import PARTNER_CLIENT_HTTP_SERVICE_PORT
from settings import PARTNER_CERTIFICATE, PARTNER_PRIVATE_KEY
from settings import PARTNER_IMMEDIATE_FULLFIL, DECISION_MODULE_URL
from settings import STUDY_REPORT_DIRECTORY
from logginghelper import method_enter
from logginghelper import method_exit
from logginghelper import method_error
from validationhelper import check_string, check_file
from validationhelper import check_port, check_bool
from httpservices import PartnerHTTPServices
from cherrypy.wsgiserver import CherryPyWSGIServer, WSGIPathInfoDispatcher
from cherrypy.wsgiserver.ssl_builtin import BuiltinSSLAdapter

def handle_error():
    """
      This function is used to handle errors in CherryPy handlers.
      This function sends 500 response.
    """
    signature='hfppnetwork.partner.partnercli.handle_error'
    method_enter(signature)
    cherrypy.response.status = 500
    logging.exception('')
    method_exit(signature)

if __name__ == '__main__':
    """
     This is  the "if __name__ == '__main__':" code block.
     This code block configures and starts the cherrypy
    """
    #configure logging
    logging.config.fileConfig(os.path.join(os.path.dirname(__file__),'logging.conf'))
    signature='hfppnetwork.partner.partnercli.main'
    method_enter(signature)
    try:
        check_string('HFPP_NODE_HTTP_SERVICE_BASE_URL',HFPP_NODE_HTTP_SERVICE_BASE_URL)
        check_string('HFPP_PARTNER_USERNAME',HFPP_PARTNER_USERNAME)
        check_string('HFPP_PARTNER_PASSWORD',HFPP_PARTNER_PASSWORD)
        check_file('CA_CERTIFICATE_FILE',CA_CERTIFICATE_FILE)
        check_file('PARTNER_CERTIFICATE', PARTNER_CERTIFICATE)
        check_file('PARTNER_PRIVATE_KEY', PARTNER_PRIVATE_KEY)
        check_string('HFPP_PARTNER_ID',HFPP_PARTNER_ID)
        check_port('PARTNER_CLIENT_HTTP_SERVICE_PORT',PARTNER_CLIENT_HTTP_SERVICE_PORT)
        check_bool('PARTNER_IMMEDIATE_FULLFIL', PARTNER_IMMEDIATE_FULLFIL)
        check_string('DECISION_MODULE_URL', DECISION_MODULE_URL)
        check_file('STUDY_REPORT_DIRECTORY', STUDY_REPORT_DIRECTORY)
        #badly formed hexadecimal UUID string will throw if not uuid string
        partner_id = uuid.UUID(HFPP_PARTNER_ID)
        logging.debug('hfpp partner id:%s',partner_id)
    except (TypeError,ValueError) as e:
        method_error(signature, e)
        sys.exit(-1)
    conf = {'global': {
            'request.error_response' : handle_error
        }
    }
    #configure cherrypy
    cherrypy.config.update(conf)
    
    wsgi_app = cherrypy.Application(PartnerHTTPServices(), '/callbacks')
    dispatcher = WSGIPathInfoDispatcher({'/': wsgi_app})
    server = CherryPyWSGIServer(('0.0.0.0', PARTNER_CLIENT_HTTP_SERVICE_PORT), dispatcher)
    sslAdapter = BuiltinSSLAdapter(PARTNER_CERTIFICATE, PARTNER_PRIVATE_KEY)
    server.ssl_adapter = sslAdapter
    try:
        server.start()
    except KeyboardInterrupt:
        server.stop()
    method_exit(signature)


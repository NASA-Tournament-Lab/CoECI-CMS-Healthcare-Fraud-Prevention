'''
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
'''
'''
This is the module that defines all the views which will respond to client requests.

Thread Safety:
The implementation is not thread safe but it will be used in a thread-safe manner.

v1.1 - Healthcare Fraud Prevention Release Assembly v1.0
     - updated for added StudyID

@author: TCSASSEMBLER
@version: 1.1
'''
from django.template.loader import get_template
from django.template import Context
from django.utils.decorators import method_decorator
from django.http import HttpResponseRedirect
from django.http import HttpResponse
from django.views.decorators.http import require_http_methods
from urllib.parse import urlencode
from decision_module import helper
from httpservices import DataRequestHandler, handle_deny_operation
from threading import Thread
from validationhelper import check_string
from appliance.config import dbconfig
from appliance.request_persistence import MySQLRequestPersistence
from appliance.request_persistence import RedisRequestPersistence
import isodate
import logging

def translateRequests(requests):
    '''
    This method translate requests list of sequent type into of map type.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.
    
    @param requests: The partner requests.
    @return: The mapped partner requests.
    '''
    nRequests = []
    requestColumns = ['request_id', 'study_id', 'query', 'expiration_time',
                      'cache_available', 'cache_timestamp', 'status']
    for req in requests:
        nReq = {}
        idx = 0
        if dbconfig["type"] == "mysql":
            req = req[1:]
        for field in requestColumns:
            nReq[field] = req[idx]
            idx = idx + 1
        nRequests.append(nReq)
    return nRequests

def get_request_persistence():
    """
    Get appropriate db persistence object from config.
    """
    if dbconfig["type"]=='redis':
        return RedisRequestPersistence()
    elif dbconfig["type"] == "mysql":
        return MySQLRequestPersistence()
    else:
        raise ValueError("Invalid db type: " + config.dbconfig["type"])

@require_http_methods(["GET"])
def list_partner_requests(request):
    '''
    This is the view function for listing one partner request.
    
    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.
    
    @param request: the http request
    @return: the http response
    '''
    CLASS_NAME = 'decision_module.views'
    LOGGER = logging.getLogger(CLASS_NAME)
    # Do logging
    signature = CLASS_NAME + '.list_partner_requests'
    helper.log_entrance(LOGGER, signature, {'request': request})

    p = get_request_persistence()
    p.connectionConfig = dbconfig
    pending = []
    approved = []
    denied = []
    try:
        p.begin()
        if dbconfig["type"]=='redis':
            pending = translateRequests(p.queryRequests('status=pending', None, None))
            approved = translateRequests(p.queryRequests('status=approved', None, None))
            denied = translateRequests(p.queryRequests('status=denied', None, None))
        else:# MySQL, no other possibilities, otherwise exceptin would be raise before
            pending = translateRequests(p.queryRequests('status="pending"', None, None))
            approved = translateRequests(p.queryRequests('status="approved"', None, None))
            denied = translateRequests(p.queryRequests('status="denied"', None, None))
    finally:
        if p.connection:
            p.close()
    
    # Render templates
    t = get_template('RequestList.html')
    ret = HttpResponse(t.render(Context(
                      {'pending': pending,
                       'approved': approved if len(approved) > 0 else None,
                       'denied': denied if len(denied) > 0 else None})))
    # Do logging
    helper.log_exit(LOGGER, signature, [ret])
    return ret

@require_http_methods(["POST"])
def create_partner_request(request):
    '''
    This is the view function for creating one partner request.
    
    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.
    
    @param request: the http request
    @return: the http response
    '''
    CLASS_NAME = 'decision_module.views'
    LOGGER = logging.getLogger(CLASS_NAME)
    # Do logging
    signature = CLASS_NAME + '.create_partner_request'
    helper.log_entrance(LOGGER, signature, {'request': request})
    
    # Check posted values
    try:
        check_string('request_id', request.POST['request_id'])
        check_string('study_id', request.POST['study_id'])
        check_string('query', request.POST['query'])
        check_string('expiration_time', request.POST['expiration_time'])
        check_string('cache_available', request.POST['cache_available'])
        if request.POST['cache_available'] == 'true':
            check_string('cache_timestamp', request.POST['cache_timestamp'])
        check_string('status', request.POST['status'])
    except Exception as e:
        helper.log_exception(LOGGER, signature, e)
    
    if dbconfig["type"]=='redis':
        fields = [request.POST['request_id'], request.POST['study_id'], request.POST['query'],
                  request.POST['expiration_time'], request.POST['cache_available'],
                  request.POST['cache_timestamp'], request.POST['status'],]
    else:# MySQL - SQL statements must translate ' to double ', or sql statement is illegal.
        fields = [request.POST['request_id'], request.POST['study_id'], request.POST['query'].replace("'", "''"),
                  request.POST['expiration_time'], request.POST['cache_available'],
                  request.POST['cache_timestamp'], request.POST['status'],]
    p = get_request_persistence()
    p.connectionConfig = dbconfig
    try:
        p.begin()
        p.createRequest(fields)
        p.commit()
    except:
        p.rollback()
    finally:
        if p.connection:
            p.close()
    
    # Redirect to /partner_tags
    # ret = HttpResponseRedirect('/')
    ret = HttpResponse(status=200)
    # Do logging
    helper.log_exit(LOGGER, signature, [ret])
    return ret

@require_http_methods(["POST"])
def approval_partner_request(request):
    '''
    This is the view function for approval one partner request.
    
    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.
    
    @param request: the http request
    @return: the http response
    '''
    CLASS_NAME = 'decision_module.views'
    LOGGER = logging.getLogger(CLASS_NAME)
    # Do logging
    signature = CLASS_NAME + '.approval_partner_request'
    helper.log_entrance(LOGGER, signature, {'request': request})
    
    request_id = request.POST['request_id']
    p = get_request_persistence()
    p.connectionConfig = dbconfig
    req = []
    try:
        p.begin()
        if dbconfig["type"]=='redis':
            req = p.queryRequests('request_id={0}'.format(request_id), None, None)
            if len(req) > 0:
                req = req[0]
            p.updateRequests('status=approved', 'request_id={0}'.format(request_id))
            p.commit()
        else:# MySQL, no other possibilities, otherwise exceptin would be raise before
            req = p.queryRequests('request_id="{0}"'.format(request_id), None, None)
            if len(req) > 0:
                req = req[0]
            p.updateRequests('status="approved"', 'request_id="{0}"'.format(request_id))
            p.commit()
    except:
        p.rollback()
    finally:
        if p.connection:
            p.close()
    
    # Kick off a new thread to handle the request
    try:
        if len(req) == 8:
            req = req[1:]
        if len(req) < 7:
            raise ValueError('Request misses parameters')
        request_id = req[0]
        study_id = req[1]
        query = req[2]
        expiration_time = isodate.parse_datetime(req[3])
        cache_available = 'true' == req[4]
        cache_timestamp = None
        if req[5] and len(req[5]) > 0:
            cache_timestamp = isodate.parse_datetime(req[5])
        
        handler = DataRequestHandler()
        t = Thread(target=handler.handle_data_request, args=(request_id, study_id, query,
                                          expiration_time, cache_available,
                                          cache_timestamp, True))
        t.daemon = False
        t.start()
        
    except Exception as e:
        helper.log_exception(LOGGER, signature, e)
    
    # Redirect to /partner_tags
    ret = HttpResponseRedirect('/')
    # Do logging
    helper.log_exit(LOGGER, signature, [ret])
    return ret

@require_http_methods(["POST"])
def deny_partner_request(request):
    '''
    This is the view function for denying one partner request.
    
    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.
    
    @param request: the http request
    @return: the http response
    '''
    CLASS_NAME = 'decision_module.views'
    LOGGER = logging.getLogger(CLASS_NAME)
    # Do logging
    signature = CLASS_NAME + '.deny_partner_request'
    helper.log_entrance(LOGGER, signature, {'request': request})
    
    request_id = request.POST['request_id']
    p = get_request_persistence()
    p.connectionConfig = dbconfig
    req = []
    try:
        p.begin()
        if dbconfig["type"]=='redis':
            req = p.queryRequests('request_id={0}'.format(request_id), None, None)
            if len(req) > 0:
                req = req[0]
            p.updateRequests('status=denied', 'request_id={0}'.format(request_id))
            p.commit()
        else:# MySQL, no other possibilities, otherwise exceptin would be raise before
            req = p.queryRequests('request_id="{0}"'.format(request_id), None, None)
            if len(req) > 0:
                req = req[0]
            p.updateRequests('status="denied"', 'request_id="{0}"'.format(request_id))
            p.commit()
    except:
        p.rollback()
    finally:
        if p.connection:
            p.close()
    
    # Kick off a new thread to handle the request
    try:
        if len(req) == 8:
            req = req[1:]
        if len(req) < 7:
            raise ValueError('Request misses parameters')
        request_id = req[0]
        t = Thread(target=handle_deny_operation, args=([request_id],))
        t.daemon = False
        t.start()
        
    except Exception as e:
        helper.log_exception(LOGGER, signature, e)
    
    # Redirect to /partner_tags
    ret = HttpResponseRedirect('/')
    # Do logging
    helper.log_exit(LOGGER, signature, [ret])
    return ret

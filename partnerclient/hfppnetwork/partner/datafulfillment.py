# -*- coding: utf-8 -*-
"""
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.

This is the module that defines the interface to decide if the partner should fulfill the data request.
It defines a function to decide if the partner should fulfill the data request.
Note that only the function declaration is designed, the actual implementation will be determined later.
This module resides in Python source file datafulfillment.py
Thread Safety:
The implementation should be thread safe.
@author:  TCSASSEMBLER
@version: 1.0
"""
from logginghelper import method_enter
from logginghelper import method_exit
from settings import PARTNER_IMMEDIATE_FULLFIL, DECISION_MODULE_URL
import urllib.parse
import urllib.request
import isodate

def can_fulfill_data_request(request_id, study_id, query, expiration_time,
                             cache_available=False, cache_timestamp=None,
                             force_fullfil=False):
    """
      This function is used to decide if the partner should fulfill the data request.
      @param request_id the request ID - it is supposed to be a non-None/empty str. Required.
      @param study_id the study ID - it is supposed to be a non-None/empty str. Required.
      @param query  the query string - it is supposed to be a non-None/empty str. Required.
      @param expiration_time the request expiration time - it is supposed to be a non-None datetime. Required.
      @param cache_available whether cache is available - it is supposed to be a bool. Optional, default to False.
      @param cache_timestamp  the cache timestamp - it is supposed to be a datetime. Optional, default to None.
      @param force_fullfil this parameter is set to True when this method is called by decision module.
      @return True if the partner client can fulfill the data request, False otherwise.
      @throws TypeError throws if any argument isn't of right type
      @throws ValueError throws if any argument isn't valid (refer to the argument documentation)
      @throws PartnerClientError throws if any other error occurred during the operation
    """
    signature = 'hfppnetwork.partner.httpservices.datafulfillment.can_fulfill_data_request'
    method_enter(signature,{
        "request_id":request_id,
        "study_id":study_id,
        "query":query,
        "expiration_time":expiration_time,
        "cache_available":cache_available,
        "cache_timestamp":cache_timestamp
    })
    
    if not PARTNER_IMMEDIATE_FULLFIL and not force_fullfil:
        url = DECISION_MODULE_URL
        values = {'request_id':request_id,
                'study_id':study_id,
        	    'query':query,
              'expiration_time':isodate.datetime_isoformat(expiration_time),
              'cache_available':('true' if cache_available else 'false'),
              'cache_timestamp':('' if cache_timestamp is None else isodate.datetime_isoformat(cache_timestamp)),
              'status':'pending'}
        data = urllib.parse.urlencode(values).encode('utf-8')
        urllib.request.urlopen(url, data)
    
    ret = PARTNER_IMMEDIATE_FULLFIL or force_fullfil
    method_exit(signature,ret)
    return ret


# -*- coding: utf-8 -*-
"""
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.

This is module that represents all the jobs.

@author:  TCSASSEMBLER
@version: 1.0
"""
from hfppnetwork.settings import STUDY_FINALIZATION_JOB_INTERVAL,\
    PULL_PARTNER_STATISTICS_JOB_INTERVAL, HFPP_NODE_HTTP_SERVICE_BASE_URL,\
    HFPP_PARTNER_USERNAME, HFPP_PARTNER_PASSWORD, CA_CERTIFICATE_FILE,\
    CA_DEFAULT, PARTNER_REQUEST_TIMEOUT, INITIAL_RESPONDED_REQUESTS_VALUE
from django.db import transaction
from hfppnetwork.sms.models import Study, Partner
import datetime
import urllib
from xml.etree import ElementTree
import logging
from apscheduler.scheduler import Scheduler
from hfppnetwork.sms import helper, logginghelper

logger = logging.getLogger(__name__)

sched = Scheduler()

@sched.interval_schedule(seconds=STUDY_FINALIZATION_JOB_INTERVAL)
@transaction.commit_on_success 
def run_study_finalization_job():
    """
    Run study finalization job.
    
    Parameters:
    None
    
    Returns:
    None
    """
    signature = "run_study_finalization_job()"
    logginghelper.method_enter(logger, signature)
    completed_on = datetime.datetime.now()
    Study.objects.filter(expiration_time__lt=datetime.datetime.now(), status__exact=1).update(status=2, completed_on=completed_on)
    logginghelper.method_exit(logger, signature)

@sched.interval_schedule(seconds=PULL_PARTNER_STATISTICS_JOB_INTERVAL)
def run_pull_partner_statistics_job():
    """
    Run pull partner statistics job.
    
    Parameters:
    None
    
    Returns:
    None
    """
    signature = "run_pull_partner_statistics_job()"
    logginghelper.method_enter(logger, signature)
    for partner in Partner.objects.all():
        pull_partner_statistics(partner.hfpp_network_id)
    logginghelper.method_exit(logger, signature)
        
@transaction.commit_on_success 
def pull_partner_statistics(partner_id):
    """
    This method pulls partner statistics.
    
    Parameters:
    - partner_id : the partner ID
    
    Returns:
    None
    """
    signature = "pull_partner_statistics(partner_id)"
    logginghelper.method_enter(logger, signature, partner_id)
    # Send request to HFPP network node
    request_to_node = urllib.request.Request(HFPP_NODE_HTTP_SERVICE_BASE_URL + '/general_service')
    request_to_node.add_header('Content-Type','application/xml;charset=utf-8')
    request_to_node.add_header('x-hfpp-username', HFPP_PARTNER_USERNAME)
    request_to_node.add_header('x-hfpp-password', HFPP_PARTNER_PASSWORD)
    
    request_xml = '<?xml version="1.0" encoding="utf-8"?>' \
        '<PartnerStatisticsRequest>' \
        '<PartnerID>{partner_id}</PartnerID>' \
        '</PartnerStatisticsRequest>'.format(partner_id=partner_id)

    try:
        response_from_node = urllib.request.urlopen(request_to_node, request_xml.encode(), timeout=PARTNER_REQUEST_TIMEOUT, cafile=CA_CERTIFICATE_FILE, cadefault=CA_DEFAULT)
    
        resp_content = response_from_node.read().decode('utf-8')
        logger.debug('response:%s',resp_content)
        root = ElementTree.fromstring(resp_content)
        count_of_data_requests_received = int(root.findtext('./NumberOfDataRequestsReceived'))
        count_of_data_requests_sent = int(root.findtext('./NumberOfDataRequestsInitiated'))
        count_of_data_requests_responded = int(root.findtext('./NumberOfDataRequestsResponded')) - INITIAL_RESPONDED_REQUESTS_VALUE
        count_of_data_requests_declined = int(root.findtext('./NumberOfDataRequestsDeclined'))
        count_of_data_requests_pending = count_of_data_requests_received - count_of_data_requests_responded - count_of_data_requests_declined
        reciprocity = count_of_data_requests_responded * 1.0 / count_of_data_requests_received if count_of_data_requests_received > 0 else 0
        Partner.objects.filter(hfpp_network_id=partner_id).update(
            count_of_data_requests_received=count_of_data_requests_received,
            count_of_data_requests_sent=count_of_data_requests_sent,
            count_of_data_requests_responded=count_of_data_requests_responded,
            count_of_data_requests_declined=count_of_data_requests_declined,
            count_of_data_requests_pending=count_of_data_requests_pending,
            reciprocity=reciprocity
        )
    except urllib.error.HTTPError as e:
    
        # Parse response XML
        resp_content = e.read().decode('utf-8')
        logger.debug('response:%s',resp_content)
        try:
            root = ElementTree.fromstring(resp_content)
            # Not succeeded
            # 400, 401, 403 or 500
            error_code = root.findtext('./ErrorCode')
            error_message = root.findtext('./ErrorMessage')
            # Log error code and error message
            logging.error('error code:%s',error_code)
            logging.error('error message:%s',error_message)
        except Exception as e:
            logging.exception("")
    logginghelper.method_exit(logger, signature)

sched.configure()
sched.start()

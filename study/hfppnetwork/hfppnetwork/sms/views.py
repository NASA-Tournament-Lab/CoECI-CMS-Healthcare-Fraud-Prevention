# -*- coding: utf-8 -*-
"""
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.

This is module that represents all the views.

@author:  TCSASSEMBLER
@version: 1.0
"""

import datetime
import isodate
import csv
from django.utils import timezone

from django.utils.decorators import method_decorator
from django.contrib.auth.decorators import login_required
from django.views.generic.list import ListView
from hfppnetwork.sms.forms import StudySearchForm, PartnerSearchForm, StudyForm,\
    StudyDataRequestFormSet, StudyChartForm, CreateStudyForm,\
    ClaimDataSearchForm
from hfppnetwork.sms.models import Study, StudyChart, DARCEmail, Partner,\
    PartnerTag, StudyDataRequest, BeneficiaryClaimData, CarrierClaimData,\
    InpatientClaimData, OutpatientClaimData, PrescriptionClaimData, STATES
from django.views.generic.detail import SingleObjectMixin, DetailView
from django.views.generic.edit import UpdateView, CreateView, FormView
from django.db import transaction
from django.http.response import HttpResponseRedirect, HttpResponse
from django.views.decorators.http import require_http_methods
from xml.etree import ElementTree
import json
import os
from django.template.loader import render_to_string
import email
import smtplib
import zlib
import urllib
from hfppnetwork.settings import HFPP_NODE_HTTP_SERVICE_BASE_URL,\
    HFPP_PARTNER_USERNAME, HFPP_PARTNER_PASSWORD, EMAIL_SENDER_EMAIL_ADDRESS,\
    SMTP_SERVER_PORT, SMTP_SERVER_HOST, SMTP_SERVER_USER_NAME, SMTP_SERVER_USER_PASSWORD,SMTP_SERVER_HTTPS,STUDY_REPORT_EMAIL_BODY,\
    STUDY_REPORT_EMAIL_SUBJECT, STUDY_REPORT_DIRECTORY, CA_CERTIFICATE_FILE,\
    DEFAULT_STUDY_EXPIRATION_TIME, CA_DEFAULT, CLAIM_DATA_FIELDS, HFPP_PARTNER_ID,\
    INITIAL_RESPONDED_REQUESTS_VALUE
import logging
import uuid
import tempfile
import gzip
from hfppnetwork.sms import externals, helper
from django.contrib.auth.forms import AuthenticationForm
from django.views.generic.base import RedirectView
from django.contrib.auth.views import login, logout
import sys
from django.views.decorators.csrf import csrf_exempt
import base64
import weasyprint

from django.views.generic import FormView
from django.views.generic import CreateView
from django.views.generic import UpdateView
from django.views.generic import RedirectView
from django.views.generic import DetailView
from django.utils.decorators import method_decorator
from hfppnetwork.sms.forms import DARCEmailSearchForm
from hfppnetwork.sms.forms import DARCEmailForm
from hfppnetwork.sms.forms import PartnerTagSearchForm
from hfppnetwork.sms.forms import PartnerTagForm
from hfppnetwork.sms.forms import PartnerForm
from hfppnetwork.sms.forms import PartnerContactFormSet
from hfppnetwork.sms.models import DARCEmail
from hfppnetwork.sms.models import PartnerTag
from hfppnetwork.sms.models import Partner
from django.db import transaction
from django.contrib.auth.models import User
from urllib.parse import urlencode
from email.utils import COMMASPACE
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.base import MIMEBase
from email import encoders
from email.utils import format_datetime
from django.forms.models import model_to_dict
import xlsxwriter

PARTNER_SEARCH_PAGE_SIZE = 10
MAX_PAGE_SIZE = 65535

logger = logging.getLogger(__name__)


@login_required
@transaction.commit_on_success
@require_http_methods(["POST"])
def execute_studies(request):
    """
    This is the view function for executing one or more studies.

    Parameters:
    - request : the HTTP request

    Returns:
    the HTTP response
    """
    for study_id in request.POST.getlist('ids'):
        study = Study.objects.get(id=int(study_id))
        if study.owner == request.user or request.user.is_staff is True:
            if not execute_study(study):
                return HttpResponse(status=500)

    # Redirect to /studies
    return HttpResponseRedirect('/studies')

@login_required
@transaction.commit_on_success
@require_http_methods(["POST"])
def complete_studies(request):
    """
    This is the view function for finalizing one or more studies.

    Parameters:
    - request : the HTTP request

    Returns:
    the HTTP response
    """
    for study_id in request.POST.getlist('ids'):
        study = Study.objects.get(id=int(study_id))
        if study.owner == request.user or request.user.is_staff is True and study.status == 1:
            Study.objects.filter(pk=int(study_id), status__exact=1).update(status=2, completed_on=timezone.localtime(timezone.now()))

    # Redirect to /studies
    return HttpResponseRedirect('/studies')

@login_required
@transaction.commit_on_success
@require_http_methods(["POST"])
def archive_studies(request):
    """
    This is the view function for archiving one or more studies.

    Parameters:
    - request : the HTTP request

    Returns:
    the HTTP response
    """
    for study_id in request.POST.getlist('ids'):
        study = Study.objects.get(id=int(study_id))
        if (study.owner == request.user or request.user.is_staff is True) and study.status == 2:
            Study.objects.filter(pk=int(study_id), status__exact=2).update(status=3);

    # Redirect to /studies
    return HttpResponseRedirect('/studies')

@login_required
@transaction.commit_on_success
@require_http_methods(["POST"])
def delete_studies(request):
    """
    This is the view function for deleting one or more studies.

    Parameters:
    - request : the HTTP request

    Returns:
    the HTTP response
    """
    for study_id in request.POST.getlist('ids'):
        study = Study.objects.get(id=int(study_id))
        if (study.owner == request.user or request.user.is_staff is True) and study.status==0:
            study.delete()

    # Redirect to /studies
    return HttpResponseRedirect('/studies')

@login_required
@transaction.commit_on_success
@require_http_methods(["POST"])
def delete_study_charts(request):
    """
    This is the view function for deleting one or more study charts.

    Parameters:
    - request : the HTTP request

    Returns:
    the HTTP response
    """
    for chart_id in request.POST.getlist('ids'):
        chart = StudyChart.objects.get(id=int(chart_id))
        if chart.study.owner == request.user or request.user.is_staff is True:
            chart.delete()

    return HttpResponse(status=200)

@login_required
@transaction.commit_on_success
@require_http_methods(["POST"])
def delete_darc_emails(request):
    """
    This is the view function for deleting one or more DARC emails.

    Parameters:
    - request : the HTTP request

    Returns:
    the HTTP response
    """
    for email_id in request.POST['ids']:
        email = DARCEmail.objects.get(id=email_id)
        if (request.user.is_staff is True):
            email.delete()

    # Redirect to /darc_emails
    return HttpResponseRedirect('/darc_emails')

@login_required
@transaction.commit_on_success
@require_http_methods(["POST"])
def delete_partner_tags(request):
    """
    This is the view function for deleting one or more partner tags.

    Parameters:
    - request : the HTTP request

    Returns:
    the HTTP response
    """
    for tag_id in request.POST['ids']:
        tag = PartnerTag.objects.get(id=tag_id)
        if tag.owner == request.user or request.user.is_staff is True:
            tag.delete()

    # Redirect to /partner_tags
    return HttpResponseRedirect('/partner_tags')

@login_required
@require_http_methods(["GET"])
def download_study_report(request, study_id):
    """
    This is the view function for download a study report.

    Parameters:
    - request : the HTTP request
    - study_id : the study ID.

    Returns:
    the HTTP response
    """
    study = Study.objects.get(id__exact=int(study_id))
    fp = open(study.report, 'rb')
    response = HttpResponse(fp.read())
    fp.close()
    response['Content-Type'] = 'application/vnd.ms-excel'
    response['Content-Disposition'] = 'attachment; filename=' + study_id + '.xlsx';
    return response

@transaction.commit_on_success
@require_http_methods(["POST"])
@csrf_exempt
def data_response(request):
    """
    This is the view function for receiving Data Responses.

    Parameters:
    - request : the HTTP request

    Returns:
    the HTTP response
    """
    try:
        data_response_xml = request.body
        root = ElementTree.fromstring(data_response_xml)
        data_request_id = root.findtext('./RequestID')
        respondent_id = root.findtext('./RespondentID')
        is_denied = root.findtext('./RequestDenied')
        data = root.findtext('./Data')
        error_message = root.findtext('./ErrorMessage')
        if error_message is not None:
            print("error_message "+error_message)
        # Find the StudyDataRequest
        study = Study.objects.filter(data_request_id__exact=data_request_id)
        partner = Partner.objects.filter(hfpp_network_id__exact=respondent_id)
        if study and error_message is None:
            study_data_request = StudyDataRequest.objects.get(study__in=study, partner__in=partner)
            if study_data_request:
                # Update the status
                if is_denied == 'true':
                    StudyDataRequest.objects.filter(study__in=study, partner__in=partner).update(status=2, response_data=data)
                else:
                    # Parse and import the response data
                    import_response_data(data, study[0])
                    StudyDataRequest.objects.filter(study__in=study, partner__in=partner).update(status=1, response_data=data)
                # return OK response
                return HttpResponse(status=200)
    except Exception as e:
        logger.exception("")
    # Bad request
    return HttpResponse(status=400)

@login_required
@transaction.commit_on_success
@require_http_methods(["POST"])
def generate_study_report(request, study_id):
    """
    This is the view function for generate study report.

    The payload of this view is JSON, as follows:

    {
        "summary" : "This is a sample summary",
        "potential_fraudulent_claims" : {
            "beneficiary" : [ 123, 234, 456],"carrier" : [ 888],"inpatient" : [ 999 ],"outpatient" : [ 123, 234, 456],"prescription" : []
        },"included_charts" : [1456, 5879],"is_sent_to_darc" : true,
        "is_sent_to_contributed_partners" : true
    }


    Parameters:
    - request : the HTTP request
    - study_id : the study ID

    Returns:
    the HTTP response
    """
    try:
        study = Study.objects.get(id__exact=int(study_id))

        # Parse JSON object from request
        json_object = json.loads(request.body.decode())

        # Load claim data
        beneficiary_claim_data = BeneficiaryClaimData.objects.filter(study=study)
        carrier_claim_data = CarrierClaimData.objects.filter(study=study)
        inpatient_claim_data = InpatientClaimData.objects.filter(study=study)
        outpatient_claim_data = OutpatientClaimData.objects.filter(study=study)
        prescription_claim_data = PrescriptionClaimData.objects.filter(study=study)
        claim_data = [beneficiary_claim_data,
                      carrier_claim_data,
                      inpatient_claim_data,
                      outpatient_claim_data,
                      prescription_claim_data]
        claim_types = ['Beneficiary', 'Carrier', 'Inpatient', 'Outpatient', 'Prescription']
        claim_names = ['BeneficiarySummary', 'CarrierClaims', 'InpatientClaims', 'OutpatientClaims', 'PrescriptionEvents']
        
        # Generate report
        file_name = os.path.join(STUDY_REPORT_DIRECTORY, study_id + '.xlsx')

        workbook = xlsxwriter.Workbook(file_name)
        worksheet = workbook.add_worksheet(name="summary")
        worksheet.write('A1', 'StudyID')
        worksheet.write('A2', 'Study Description')
        worksheet.write('A3', 'Total Requests')
        worksheet.write('A4', 'Total Satisfied Requests')
        worksheet.write('A5', 'Pending Requests')
        worksheet.write('A6', 'Declined Requests')
        worksheet.write('A7', 'Reciprocity')
        worksheet.write('B1', study_id)
        worksheet.write('B2', study.description)
        worksheet.write('B3', study.count_of_data_requests_sent)
        worksheet.write('B4', study.count_of_data_requests_satisfied)
        worksheet.write('B5', study.count_of_data_requests_pending)
        worksheet.write('B6', study.count_of_data_requests_declined)
        worksheet.write('B7', '%.2f%%'%(study.count_of_data_requests_satisfied * 100.0 / study.count_of_data_requests_sent \
                if study.count_of_data_requests_sent > 0 else 0))

        

        for i, claim_type in enumerate(claim_types):
            worksheet = workbook.add_worksheet(name=claim_names[i])
            fields = helper.getClaimFieldConfig(claim_type)
            cidx = 0
            ridx = 0
            for field in fields:
                worksheet.write(ridx, cidx, field['column_name'])
                cidx += 1
            ridx = 1
            for record in claim_data[i]:
                cidx = 0
                for field in fields:
                    v = str(getattr(record, field['field_name']))
                    if field['type'] == 'date' and v == '9999-12-31':
                        v = 'N/A'
                    worksheet.write(ridx, cidx, v)
                    cidx += 1
                ridx += 1

        workbook.close()

        if json_object['is_sent_to_darc'] ==  True:
            # Send report to DARC via email
            send_to = []
            for darc_email in DARCEmail.objects.all():
                send_to.append(darc_email.email)
            if send_to:
                study_dict = model_to_dict(study, fields=[], exclude=[])
                msg = MIMEMultipart()
                msg['From'] = EMAIL_SENDER_EMAIL_ADDRESS
                msg['To'] = COMMASPACE.join(send_to)
                msg['Date'] = format_datetime(datetime.datetime.now())
                msg['Subject'] = STUDY_REPORT_EMAIL_SUBJECT.format(study=study_dict)
    
                msg.attach(MIMEText(STUDY_REPORT_EMAIL_BODY.format(study=study_dict)))
    
                part = MIMEBase('application', "octet-stream")
                part.set_payload( open(file_name,"rb").read() )
                encoders.encode_base64(part)
                part.add_header('Content-Disposition', 'attachment; filename="%s"' % os.path.basename(file_name))
                msg.attach(part)
    
                smtp = smtplib.SMTP(SMTP_SERVER_HOST, SMTP_SERVER_PORT)
                smtp.set_debuglevel(1)
                if SMTP_SERVER_HTTPS:
                    smtp.ehlo()
                    smtp.starttls()
                    smtp.ehlo
                if SMTP_SERVER_USER_NAME:
                    smtp.login(SMTP_SERVER_USER_NAME, SMTP_SERVER_USER_PASSWORD)
                smtp.sendmail(EMAIL_SENDER_EMAIL_ADDRESS, send_to, msg.as_string())
                smtp.close()

        # Send report to partners
        # Compress and encode to base64
        with open(file_name, 'rb') as in_file:
            report_content = base64.encodebytes(zlib.compress(in_file.read())).decode()

        request_to_node = urllib.request.Request(HFPP_NODE_HTTP_SERVICE_BASE_URL + '/analysis_result')
        request_to_node.add_header('Content-Type','application/xml;charset=utf-8')
        request_to_node.add_header('x-hfpp-username', HFPP_PARTNER_USERNAME)
        request_to_node.add_header('x-hfpp-password', HFPP_PARTNER_PASSWORD)

        data_request_xml = '<?xml version="1.0" encoding="utf-8"?>' \
            '<AnalysisResult>' \
            '<RequestID>{data_request_id}</RequestID>' \
            '<StudyID>{study_id}</StudyID>' \
            '<Result><![CDATA[ {report_content} ]]></Result>' \
            '</AnalysisResult>'.format(data_request_id=study.data_request_id, study_id=study_id, report_content=report_content)

        response_from_node = urllib.request.urlopen(request_to_node, data_request_xml.encode(), cafile=CA_CERTIFICATE_FILE, cadefault=CA_DEFAULT)

        resp_content = response_from_node.read().decode('utf-8')

        # Parse response XML
        root = ElementTree.fromstring(resp_content)
        if not 200 == response_from_node.getcode():
            # Not succeeded
            # 400, 401, 403 or 500
            error_code = root.findtext('./ErrorCode')
            error_message = root.findtext('./ErrorMessage')
            # Log error code and error message
            logger.error('error code:%s',error_code)
            logger.error('error message:%s',error_message)

        # Update study report
        Study.objects.filter(id__exact=int(study_id)).update(report=file_name)

        return HttpResponse(status=200)
    except:
        logger.exception("")
        return HttpResponse(status=500)

def execute_study(study):
    """
    This is function to execute a study.

    Parameters:
    - study : the study to execute

    Returns:
    true if the execution is accepted, false otherwise.
    """
    if study.status == 0:
        # Send request to HFPP network node
        data_request_id = str(uuid.uuid4())
        request_to_node = urllib.request.Request(HFPP_NODE_HTTP_SERVICE_BASE_URL + '/data_request')
        request_to_node.add_header('Content-Type','application/xml;charset=utf-8')
        request_to_node.add_header('x-hfpp-username', HFPP_PARTNER_USERNAME)
        request_to_node.add_header('x-hfpp-password', HFPP_PARTNER_PASSWORD)

        data_request_xml = '<?xml version="1.0" encoding="utf-8"?>' \
            '<DataRequest>' \
            '<RequestID>{data_request_id}</RequestID>' \
            '<StudyID>{study_id}</StudyID>'.format(data_request_id=data_request_id, study_id=study.id)
        if study.on_behalf_of:
            data_request_xml += '<OriginalRequesterID>{original_requester_id}</OriginalRequesterID>'.format(original_requester_id=study.on_behalf_of.hfpp_network_id)
        else:
            data_request_xml += '<OriginalRequesterID>{original_requester_id}</OriginalRequesterID>'.format(original_requester_id=HFPP_PARTNER_ID)

        data_request_xml += '<Query><![CDATA[ {transformed_query} ]]></Query><RequestedPartners>'.format(transformed_query=externals.transform_query(study.beneficiary_query, study.carrier_query, study.inpatient_query, study.outpatient_query, study.prescription_query))
        # Query StudyDataRequest's
        study_data_requests = StudyDataRequest.objects.all().filter(study=study)
        if study_data_requests:
            for study_data_request in StudyDataRequest.objects.all().filter(study=study):
                data_request_xml += '<PartnerID>{partner_id}</PartnerID>'.format(partner_id=study_data_request.partner.hfpp_network_id)
        else:
            for partner in Partner.objects.all():
                data_request_xml += '<PartnerID>{partner_id}</PartnerID>'.format(partner_id=partner.hfpp_network_id)

        data_request_xml += '</RequestedPartners>'
        data_request_xml +='<ExpirationTime>{expiration_time}</ExpirationTime>' \
            '<CacheSafe>{cache_safe}</CacheSafe>'.format(expiration_time=isodate.datetime_isoformat(timezone.localtime(timezone.now()) + datetime.timedelta(0, DEFAULT_STUDY_EXPIRATION_TIME)), cache_safe='true' if study.is_cache_safe else 'false')#time zone study.expiration_time.isoformat()

        data_request_xml += '</DataRequest>'
        logger.debug('data request %s', data_request_xml)
        try:
            response_from_node = urllib.request.urlopen(request_to_node, data_request_xml.encode(), cafile=CA_CERTIFICATE_FILE, cadefault=CA_DEFAULT)
            resp_content = response_from_node.read().decode('utf-8')
            logger.debug('response:%s',resp_content)
        except urllib.error.HTTPError as http_error:
            # Not succeeded
            # 400, 401, 403 or 500
            # Parse response XML
            resp_content = http_error.read().decode('utf-8')
            logger.error('error response:%s',resp_content)
            try:
                root = ElementTree.fromstring(resp_content)
                error_code = root.findtext('./ErrorCode')
                error_message = root.findtext('./ErrorMessage')
                # Log error code and error message
                logger.error('error code:%s',error_code)
                logger.error('error message:%s',error_message)
                #handle waiting approval message
                if error_message=='Waiting Approval':
                    return True
            except Exception as e:
                logger.exception('')
            return False
        # Success, update study status
        Study.objects.filter(pk=study.id).update(status=1, data_request_id=data_request_id, executed_on=timezone.localtime(timezone.now()))
        return True
    else:
        return False

def import_response_data(data, study):
    uncompressed_file = tempfile.NamedTemporaryFile(delete=False).name
    try:
        # Decode base64
        decoded_data = zlib.decompress(base64.b64decode(data))
        # logger.debug(decoded_data)
        #with open(uncompressed_file, 'wb') as out_file:
        #    out_file.write(decoded_data)
        lines = decoded_data.decode().split('<?xml version="1.0" encoding="UTF-8"?>')
        
        for line in lines[1:]:
            # Parse data XML & import to database
            root = ElementTree.fromstring(line)#parse(uncompressed_file)
            for beneficiary_summary in root.findall('.//BeneficiarySummary'):
                properties = helper.parseBeneficiaryClaim({}, beneficiary_summary)
                BeneficiaryClaimData.objects.create(study=study, **properties)
            for carrier_claim in root.findall('.//CarrierClaim'):
                properties = helper.parseCarrierClaimData({}, carrier_claim)
                CarrierClaimData.objects.create(study=study, **properties)
            for inpatient_claim in root.findall('.//InpatientClaim'):
                properties = helper.parseInpatientClaimData({}, inpatient_claim)
                InpatientClaimData.objects.create(study=study, **properties)
            for outpatient_claim in root.findall('.//OutpatientClaim'):
                properties = helper.parseOutpatientClaimData({}, outpatient_claim)
                OutpatientClaimData.objects.create(study=study, **properties)
            for prescription_claim in root.findall('.//PrescriptionEvent'):
                properties = helper.parsePrescriptionClaimData({}, prescription_claim)
                PrescriptionClaimData.objects.create(study=study, **properties)
    except Exception as e:
        logger.exception("")
    finally:
        # Remove temporary files
        # os.remove(compressed_file)
        os.remove(uncompressed_file)

#search partner
def search_partner(request):
    search_key = request.GET.get('company_name', '')
    partner_id = None
    partner_tag_id = None
    if search_key.startswith('c__'):
        partner_id = search_key[len('c__'):]
    elif search_key.startswith('t__'):
        partner_tag_id = search_key[len('t__'):]
    else:
        company_name = search_key
    page = int(request.GET.get('page', 0))
    pageSize = PARTNER_SEARCH_PAGE_SIZE
    if request.GET.get('pageSize') == 'All':
        pageSize = MAX_PAGE_SIZE
    else:
        pageSize = int(request.GET.get('pageSize', PARTNER_SEARCH_PAGE_SIZE))
    page = page;
    if partner_id:
        partners = [Partner.objects.get(pk=partner_id)]
    elif partner_tag_id:
        partners = Partner.objects.filter(tags__id=partner_tag_id)
    elif company_name=="":
        pageSize = MAX_PAGE_SIZE
        partners = Partner.objects.all()
    else:
        partners = list(Partner.objects.filter(company_name__contains=company_name))
    total = len(partners)
    totalPages = (total // pageSize) + ((total % pageSize) > 0 and 1 or 0)
    totalPages = max(1, totalPages)
    if page >= totalPages:
        page = totalPages - 1
    start = page * pageSize
    end = min(total, start + pageSize)
    partners = partners[start:end]
    partnerData = []
    for partner in partners:
        partnerData.append({'id': partner.id, 'name':partner.company_name})
    result = {'total': total, 'page': page, 'pageSize': pageSize, 'totalPages': totalPages, 'partners': partnerData}
    return HttpResponse(json.dumps(result), content_type='application/json')

class LoginRequiredMixin():
    '''
    This is a utility mixin class which requires login for view operations.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @author: caoweiquan322
    @version: 1.0
    '''
    # Here we setup the member variables for logging.
    CLASS_NAME = 'hfppnetwork.sms.views.LoginRequiredMixin'
    LOGGER = logging.getLogger(CLASS_NAME)

    @method_decorator(login_required)
    def dispatch(self, *args, **kwargs):
        '''
        Dispatch the operations.

        @param self: the object itself
        @param args: the arguments without key words
        @param kwargs: key word argumens
        @return: dispatched result
        '''
        # Do logging
        signature = self.CLASS_NAME + '.dispatch'
        helper.log_entrance(self.LOGGER, signature, {'args': args, 'kwargs': kwargs})

        ret = super(LoginRequiredMixin, self).dispatch(*args, **kwargs)

        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret
class SuperuserRequiredMixin(object):
    '''
    This is a utility mixin class which requires login for partners view operations.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @author: muzehyun
    @version: 1.0
    '''
    def dispatch(self, request, *args, **kwargs):
        if not request.user.is_superuser:
            return HttpResponse('Unauthorized', status=401)
        return super(SuperuserRequiredMixin, self).dispatch(request, *args, **kwargs)

class JSONResponseMixin(object):
    '''
    This is a utility mixin class which is able to respond with JSON.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @author: caoweiquan322
    @version: 1.0
    '''
    # Here we setup the member variables for logging.
    CLASS_NAME = 'hfppnetwork.sms.views.JSONResponseMixin'
    LOGGER = logging.getLogger(CLASS_NAME)

    def render_to_json_response(self, context, **response_kwargs):
        '''
        This method is used to render response to JSON.

        @param self: the object itself
        @param context: context of the server
        @param response_kwargs: key word arguments to respond to client
        @return: the http response
        '''
        # Do logging
        signature = self.CLASS_NAME + '.render_to_json_response'
        helper.log_entrance(self.LOGGER, signature,
                           {'context': context, 'response_kwargs': response_kwargs})

        data = json.dumps(context)
        response_kwargs['content_type'] = 'application/json'
        ret = HttpResponse(data, **response_kwargs)

        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret

    def form_invalid(self, form):
        '''
        This method will be called if the submitted form is invalid.

        @param self: the object itself
        @param form: the invalid form
        '''
        # Do logging
        signature = self.CLASS_NAME + '.form_invalid'
        helper.log_entrance(self.LOGGER, signature, {'form': form})

        if self.request.is_ajax():
            ret = self.render_to_json_response({'errors': form.errors}, status=400)
        else:
            ret = super(JSONResponseMixin, self).form_invalid(form)
        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret

    def form_valid(self, form):
        '''
        This method will be called if the submitted form is valid.

        @param self: the object itself
        @param form: the validated form
        '''
        # Do logging
        signature = self.CLASS_NAME + '.form_valid'
        helper.log_entrance(self.LOGGER, signature, {'form': form})

        form.instance.owner = self.request.user;
        response = super(JSONResponseMixin, self).form_valid(form)
        if self.request.is_ajax():
            data = {
                'pk': self.object.pk,
            }
            ret = self.render_to_json_response(data)
        else:
            ret = response
        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret
class ListStudyView(LoginRequiredMixin, ListView):
    """
    This is the Django view implementation to list studies.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    #Represents the page size. Default to 10.
    paginate_by = 10
    #Represents the template name. This value isn't supposed to change.
    template_name = "studies/list.html"
    #Represents the context object name for studies. This value isn't supposed to change.
    context_object_name = "studies"

    def get_paginate_by(self, queryset):
        """
        Get the page size.

        Parameters:
        - self : the object itself
        - queryset : the query set

        Returns:
        the page size
        """
        return self.request.REQUEST.get('page_size', self.paginate_by)

    def get_search_form(self):
        """
        Return the StudySearchForm on the page.

        Parameters:
        - self : the object itself

        Returns:
        the StudySearchForm on the page.
        """
        search_form = self.form = StudySearchForm(self.request.REQUEST)
        return search_form

    def get_queryset(self):
        """
        Return the query set for the view.

        Parameters:
        - self : the object itself

        Returns:
        the query set
        """
        #Filter by role
        if self.request.user.is_staff == True:
            qs = Study.objects.all()
        else:
            qs = Study.objects.filter(owner__exact=self.request.user)

        #Filter by search form
        search_form = self.get_search_form()
        if search_form.is_valid():
            if search_form.cleaned_data.get("id"):
                qs = qs.filter(id__exact=search_form.cleaned_data.get("id"))
            if search_form.cleaned_data.get("description"):
                qs = qs.filter(description__contains=search_form.cleaned_data.get("description"))
            if search_form.cleaned_data.get("status"):
                qs = qs.filter(status__exact=search_form.cleaned_data.get("status"))
            else:
                qs = qs.filter(status__exact=0)
            if search_form.cleaned_data.get("created_on_from"):
                qs = qs.filter(created_on__gte=datetime.datetime.combine(search_form.cleaned_data.get("created_on_from"),
                                                                         datetime.datetime.min.time()))
            if search_form.cleaned_data.get("created_on_to"):
                qs = qs.filter(created_on__lte=datetime.datetime.combine(search_form.cleaned_data.get("created_on_to"),
                                                                         datetime.datetime.max.time()))
            if search_form.cleaned_data.get("last_modified_on_from"):
                qs = qs.filter(last_modified_on__gte=datetime.datetime.combine(
                                search_form.cleaned_data.get("last_modified_on_from"),datetime.datetime.min.time()))
            if search_form.cleaned_data.get("last_modified_on_to"):
                qs = qs.filter(last_modified_on__lte=datetime.datetime.combine(
                                search_form.cleaned_data.get("last_modified_on_to"),datetime.datetime.max.time()))
            if search_form.cleaned_data.get("executed_on_from"):
                qs = qs.filter(executed_on__gte=datetime.datetime.combine(search_form.cleaned_data.get("executed_on_from"),
                                                                          datetime.datetime.min.time()))
            if search_form.cleaned_data.get("executed_on_to"):
                qs = qs.filter(executed_on__lte=datetime.datetime.combine(search_form.cleaned_data.get("executed_on_to"),
                                                                          datetime.datetime.max.time()))
            if search_form.cleaned_data.get("completed_on_from"):
                qs = qs.filter(completed_on__gte=datetime.datetime.combine(search_form.cleaned_data.get("completed_on_from"),
                                                                           datetime.datetime.min.time()))
            if search_form.cleaned_data.get("completed_on_to"):
                qs = qs.filter(completed_on__lte=datetime.datetime.combine(search_form.cleaned_data.get("completed_on_to"),
                                                                           datetime.datetime.max.time()))

        #Return the query
        return qs

    def get_context_data(self, **kwargs):
        context = super(ListStudyView, self).get_context_data(**kwargs)
        # Process paging data
        get = self.request.GET.copy()
        page = get.pop("page", None)
        extra = '&'+get.urlencode()
        context['page'] = page
        context['extra_vars'] = extra
        context['search_form'] = self.get_search_form()
        # Staff(Admin) can view all studies
        if self.request.user.is_staff == True:
            context['count_of_draft_studies'] = Study.objects.filter(status__exact=0).count()
            context['count_of_in_progress_studies'] = Study.objects.filter(status__exact=1).count()
            context['count_of_analysis_studies'] = Study.objects.filter(status__exact=2).count()
            context['count_of_archived_studies'] = Study.objects.filter(status__exact=3).count()
        # Regular user can view own studies
        else:
            context['count_of_draft_studies'] = Study.objects.filter(owner__exact=self.request.user).filter(status__exact=0).count()
            context['count_of_in_progress_studies'] = Study.objects.filter(owner__exact=self.request.user).filter(status__exact=1).count()
            context['count_of_analysis_studies'] = Study.objects.filter(owner__exact=self.request.user).filter(status__exact=2).count()
            context['count_of_archived_studies'] = Study.objects.filter(owner__exact=self.request.user).filter(status__exact=3).count()
        return context

class ViewStudyTransactionsView(LoginRequiredMixin, ListView):
    """
    This is the Django view implementation to list study transactions.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    #Represents the page size. Default to 10.
    paginate_by = 10
    #Represents the template name. This value isn't supposed to change.
    template_name = "studies/transactions.html"
    #Represents the context object name for transactions. This value isn't supposed to change.
    context_object_name = "transactions"

    def get_paginate_by(self, queryset):
        """
        Get the page size.

        Parameters:
        - self : the object itself
        - queryset : the query set

        Returns:
        the page size
        """
        return self.request.REQUEST.get('page_size', self.paginate_by)

    def get_context_data(self, **kwargs):
        """
        Return the context data.

        Parameters:
        - self : the object itself
        - kwargs : the key/value arguments

        Returns:
        the context data
        """
        context = super(ViewStudyTransactionsView, self).get_context_data(**kwargs)
        # Process paging data
        get = self.request.GET.copy()
        page = get.pop("page", None)
        extra = '&'+get.urlencode()
        context['page'] = page
        context['extra_vars'] = extra
        context['search_form'] = self.get_search_form()
        response_rate = float(0)
        count_of_data_responsed = self.study.count_of_data_requests_sent - self.study.count_of_data_requests_pending
        if self.study.count_of_data_requests_sent:
            response_rate = float(count_of_data_responsed) / self.study.count_of_data_requests_sent
        context['count_of_data_responsed'] = count_of_data_responsed
        context['response_rate'] = response_rate * 100
        # Study object
        context['study'] = self.study
        return context

    def get_queryset(self):
        """
        Return the query set for the view.

        Parameters:
        - self : the object itself

        Returns:
        the query set
        """

        self.study = Study.objects.get(pk=self.kwargs['pk'])
        qs = StudyDataRequest.objects.filter(study=self.study)

        # Filter by search form
        search_form = self.get_search_form()
        if search_form.is_valid():
            if search_form.cleaned_data.get("company_name") is not None:
                qs = qs.filter(partner__company_name__contains=search_form.cleaned_data.get("company_name"))

        # Return the query
        return qs


    def get_search_form(self):
        """
        Return the PartnerSearchForm on the page.

        Parameters:
        - self : the object itself

        Returns:
        the PartnerSearchForm on the page.
        """
        search_form = self.form = PartnerSearchForm(self.request.REQUEST)
        return search_form

class EditStudyView(LoginRequiredMixin, UpdateView):
    """
    This is the Django view implementation to edit study.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    #Represents the model class for the view. This value isn't supposed to change.
    model = Study
    #Represents the template name. This value isn't supposed to change.
    template_name = "studies/edit.html"
    #Represents the form class for the view. This value isn't supposed to change.
    form_class = StudyForm
    #Represents the context object name for the study. This value isn't supposed to change.
    context_object_name = 'study'

    @transaction.commit_on_success
    def form_valid(self, form):
        context = self.get_context_data()
        form.instance.owner = self.request.user;
        # Get the formsets for data request
        study_data_request_formset = context['study_data_request_formset']
        if study_data_request_formset.is_valid():
            StudyDataRequest.objects.filter(study=self.object).delete()
            exist_study = self.object
            status = form.instance.status
            form.instance.status = 0
            form.instance.created_on = exist_study.created_on
            form.instance.last_modified_on = exist_study.last_modified_on
            form.instance.executed_on = exist_study.executed_on
            form.instance.completed_on = exist_study.completed_on
            form.instance.expiration_time = exist_study.expiration_time
            # Set default expiration time if not specified
            if form.cleaned_data['expiration_time'] is None:
                form.instance.expiration_time = form.instance.created_on + datetime.timedelta(seconds=DEFAULT_STUDY_EXPIRATION_TIME)
                form.cleaned_data['expiration_time'] = form.instance.expiration_time
            self.object = form.save()
            study_data_request_formset.instance = self.object
            study_data_request_formset.save()
            operate_result = 'saved'
            if status == 1:
                # Execute study
                execute_study(self.object)
                operate_result = 'executed'

            return self.render_to_response(self.get_context_data(form=form, operate_result=operate_result))
        else:
            return self.render_to_response(self.get_context_data(form=form, operate_result=''))

    def form_invalid(self, form):
        """
        This method will be called if the submitted form is invalid.

        Parameters:
        - self : the object itself
        - form : the submitted form

        Returns:
        the response
        """
        return self.render_to_response(self.get_context_data(form=form, operate_result=''))

    def get_context_data(self, **kwargs):
        """
        Return the context data.

        Parameters:
        - self : the object itself
        - kwargs : the key/value arguments

        Returns:
        the context data
        """
        context = super(EditStudyView, self).get_context_data(**kwargs)
        # inline formsets
        if self.request.POST:
            context['study_data_request_formset'] = StudyDataRequestFormSet(self.request.POST, instance=self.object)
        else:
            context['study_data_request_formset'] = StudyDataRequestFormSet(instance=self.object)
            context['partners'] = Partner.objects.all()
            context['partner_tags'] = PartnerTag.objects.all()

        return context

    def get_queryset(self):
        """
        Return the query set for the view.

        Parameters:
        - self : the object itself

        Returns:
        the query set
        """
        qs = super(UpdateView, self).get_queryset().filter(status__exact=0)
        # Staff(Admin) can view all studies
        if self.request.user.is_staff == True:
            return qs;
        # Regular user can view own studies
        else:
            return qs.filter(owner__exact=self.request.user)

class CreateStudyChartView(LoginRequiredMixin, CreateView, JSONResponseMixin):
    """
    This is the Django view implementation to create study chart.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    #Represents the model class for the view. This value isn't supposed to change.
    model = StudyChart
    #Represents the form class for the view. This value isn't supposed to change.
    form_class = StudyChartForm

    def form_valid(self, form):
        """
        This method will be called if the submitted form is valid.

        Parameters:
        - self : the object itself
        - form : the submitted form

        Returns:
        the response
        """
        self.object = form.save()
        if self.request.is_ajax():
            data = {
                'pk': self.object.pk,
            }
            return self.render_to_json_response(data)
        else:
            return HttpResponseRedirect('/studies')

class CreateStudyView(LoginRequiredMixin, CreateView):
    """
    This is the Django view implementation to create study.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    #Represents the model class for the view. This value isn't supposed to change.
    model = Study
    #Represents the template name. This value isn't supposed to change.
    template_name = "studies/create.html"
    #Represents the form class for the view. This value isn't supposed to change.
    form_class = CreateStudyForm

    @transaction.commit_on_success
    def form_valid(self, form):
        context = self.get_context_data()
        form.instance.owner = self.request.user;
        # Get the formsets for data request
        study_data_request_formset = context['study_data_request_formset']
        if study_data_request_formset.is_valid():
            now = timezone.localtime(timezone.now()) #datetime.datetime.now()
            # Set default expiration time if not specified
            if form.cleaned_data['expiration_time'] is None:
                form.instance.expiration_time = now + datetime.timedelta(seconds=DEFAULT_STUDY_EXPIRATION_TIME)
                form.cleaned_data['expiration_time'] = form.instance.expiration_time
            status = form.instance.status
            form.instance.description = 'beneficiary: ' + form.instance.beneficiary_query + '\n'
            form.instance.description = form.instance.description + 'carrier: ' + form.instance.carrier_query + '\n'
            form.instance.description = form.instance.description + 'inpatient: ' + form.instance.inpatient_query + '\n'
            form.instance.description = form.instance.description + 'outpatient: ' + form.instance.outpatient_query + '\n'
            form.instance.description = form.instance.description + 'prescription: ' + form.instance.prescription_query + '\n'
            form.instance.status = 0
            form.instance.created_on = now
            form.instance.last_modified_on = now
            self.object = form.save()
            if study_data_request_formset.total_form_count():
                study_data_request_formset.instance = self.object
                study_data_request_formset.save()
            else:
                for partner in Partner.objects.all():
                    StudyDataRequest.objects.create(study=self.object, partner=partner, status=0, response_data='')
            operate_result = 'saved'
            if status == 1:
                # Execute study
                execute_study(self.object)
                operate_result = 'executed'

            return self.render_to_response(self.get_context_data(form=form, operate_result=operate_result, study_id=self.object.id))
        else:
            return self.render_to_response(self.get_context_data(form=form, operate_result='', study_id=''))

    def form_invalid(self, form):
        """
        This method will be called if the submitted form is invalid.

        Parameters:
        - self : the object itself
        - form : the submitted form

        Returns:
        the response
        """
        return self.render_to_response(self.get_context_data(form=form, operate_result='', study_id=''))

    """
    Return the context data.

    Parameters:
    - self : the object itself
    - kwargs : the key/value arguments

    Returns:
    the context data
    """
    def get_context_data(self, **kwargs):
        context = super(CreateStudyView, self).get_context_data(**kwargs)
        # inline formsets
        if self.request.POST:
            context['study_data_request_formset'] = StudyDataRequestFormSet(self.request.POST, instance=self.object)
        else:
            context['study_data_request_formset'] = StudyDataRequestFormSet()
            context['partners'] = Partner.objects.all()
            context['partner_tags'] = PartnerTag.objects.all()

        return context

class ViewStudyBusinessRuleView(LoginRequiredMixin, DetailView):
    """
    This is the Django view implementation to view study business rule.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    #Represents the model class for the view. This value isn't supposed to change.
    model = Study
    #Represents the template name. This value isn't supposed to change.
    template_name = "studies/business_rule.html"
    #Represents the context object name for the study. This value isn't supposed to change.
    context_object_name = "study"

    def get_context_data(self, **kwargs):
        context = super(ViewStudyBusinessRuleView, self).get_context_data(**kwargs)
        context['transactions'] = self.transactions
        return context

    def get_queryset(self):
        """
        Return the query set for the view.

        Parameters:
        - self : the object itself

        Returns:
        the query set
        """
        self.transactions = StudyDataRequest.objects.filter(study__id=self.kwargs['pk'])

        qs = super(DetailView, self).get_queryset()
        # Staff(Admin) can view all studies
        if self.request.user.is_staff == True:
            return qs;
        # Regular user can view own studies
        else:
            return qs.filter(owner__exact=self.request.user)

class ViewStudyResultsView(LoginRequiredMixin, DetailView):
    #Represents the model class for the view. This value isn't supposed to change.
    model = Study
    #Represents the template name. This value isn't supposed to change.
    template_name = "studies/results.html"
    #Represents the context object name for study. This value isn't supposed to change.
    context_object_name = "study"

    def get_context_data(self, **kwargs):
        """
        Return the context data.

        Parameters:
        - self : the object itself
        - kwargs : the key/value arguments

        Returns:
        the context data
        """
        context = super(ViewStudyResultsView, self).get_context_data(**kwargs)

        # Form
        context['search_form'] = self.get_search_form()
        context['study_chart_form'] = StudyChartForm()

        # Claim data
        claim_data_dict = externals.filter_claim_data(context['search_form'])
        context['beneficiary_claims'] = helper.createClaimDataDetails('Beneficiary', claim_data_dict['Beneficiary'].filter(study=self.object))
        context['carrier_claims'] = helper.createClaimDataDetails('Carrier', claim_data_dict['Carrier'].filter(study=self.object))
        context['inpatient_claims'] = helper.createClaimDataDetails('Inpatient', claim_data_dict['Inpatient'].filter(study=self.object))
        context['outpatient_claims'] = helper.createClaimDataDetails('Outpatient', claim_data_dict['Outpatient'].filter(study=self.object))
        context['prescription_claims'] = helper.createClaimDataDetails('Prescription', claim_data_dict['Prescription'].filter(study=self.object))

        # Study charts
        context['study_charts'] = StudyChart.objects.filter(study=self.object)

        selected_fields = self.request.GET.getlist('selected_beneficiary_claim_data_fields')
        if not selected_fields:
            selected_fields = sorted(CLAIM_DATA_FIELDS['Beneficiary'].keys())
        context['beneficiary_claim_data_fields'] = helper.getClaimColumns('Beneficiary', selected_fields)
        context['selected_beneficiary_claim_data_fields'] = [CLAIM_DATA_FIELDS['Beneficiary'][x] for x in selected_fields]
        context['selected_beneficiary_claim_data_fields'].sort(key=lambda v:v['order'])

        selected_fields = self.request.GET.getlist('selected_carrier_claim_data_fields')
        if not selected_fields:
            selected_fields = sorted(CLAIM_DATA_FIELDS['Carrier'].keys())
        context['carrier_claim_data_fields'] = helper.getClaimColumns('Carrier', selected_fields)
        context['selected_carrier_claim_data_fields'] = [CLAIM_DATA_FIELDS['Carrier'][x] for x in selected_fields]
        context['selected_carrier_claim_data_fields'].sort(key=lambda v:v['order'])


        selected_fields = self.request.GET.getlist('selected_inpatient_claim_data_fields')
        if not selected_fields:
            selected_fields = sorted(CLAIM_DATA_FIELDS['Inpatient'].keys())
        context['inpatient_claim_data_fields'] = helper.getClaimColumns('Inpatient', selected_fields)
        context['selected_inpatient_claim_data_fields'] = [CLAIM_DATA_FIELDS['Inpatient'][x] for x in selected_fields]
        context['selected_inpatient_claim_data_fields'].sort(key=lambda v:v['order'])


        selected_fields = self.request.GET.getlist('selected_outpatient_claim_data_fields')
        if not selected_fields:
            selected_fields = sorted(CLAIM_DATA_FIELDS['Outpatient'].keys())
        context['outpatient_claim_data_fields'] = helper.getClaimColumns('Outpatient', selected_fields)
        context['selected_outpatient_claim_data_fields'] = [CLAIM_DATA_FIELDS['Outpatient'][x] for x in selected_fields]
        context['selected_outpatient_claim_data_fields'].sort(key=lambda v:v['order'])


        selected_fields = self.request.GET.getlist('selected_prescription_claim_data_fields')
        if not selected_fields:
            selected_fields = sorted(CLAIM_DATA_FIELDS['Prescription'].keys())
        context['prescription_claim_data_fields'] = helper.getClaimColumns('Prescription', selected_fields)
        context['selected_prescription_claim_data_fields'] = [CLAIM_DATA_FIELDS['Prescription'][x] for x in selected_fields]
        context['selected_prescription_claim_data_fields'].sort(key=lambda v:v['order'])

        # DARC emails
        context['darc_emails'] = DARCEmail.objects.all()

        return context

    def get_queryset(self):
        """
        Return the query set for the view.

        Parameters:
        - self : the object itself

        Returns:
        the query set
        """
        qs = super(DetailView, self).get_queryset()
        # Staff(Admin) can view all studies
        if self.request.user.is_staff == True:
            return qs;
        # Regular user can view own studies
        else:
            return qs.filter(owner__exact=self.request.user)

    def get_search_form(self):
        """
        Return the ClaimDataSearchForm on the page.

        Parameters:
        - self : the object itself

        Returns:
        the ClaimDataSearchForm on the page.
        """
        search_form = self.form = ClaimDataSearchForm(self.request.REQUEST)
        return search_form

class LoginView(FormView):
    '''
    This is the Django view implementation for user login.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @author: caoweiquan322
    @version: 1.0
    '''
    form_class = AuthenticationForm
    template_name = "login.html"
    # Here we setup the member variables for logging.
    CLASS_NAME = 'hfppnetwork.sms.views.LoginView'
    LOGGER = logging.getLogger(CLASS_NAME)

    def form_valid(self, form):
        '''
        This method will be called if the submitted form is valid.

        @param self: the object itself
        @param form: the validated form
        @return: result of its parent
        '''
        # Do logging
        signature = self.CLASS_NAME + '.form_valid'
        helper.log_entrance(self.LOGGER, signature, {'form': form})

        login(self.request, form.get_user())
        ret = super(LoginView, self).form_valid(form)

        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret

    def get_success_url(self):
        '''
        Returns the success URL.

        @param self: the object itself
        @return: the success URL
        '''
        # Do logging
        signature = self.CLASS_NAME + '.get_success_url'
        helper.log_entrance(self.LOGGER, signature, None)

        if len(self.request.GET.get("next", "")) > 0:
            ret = self.request.GET.get("next")
        elif self.request.user.is_staff:
            ret = "/admin/auth/user"
        else:
            ret = "/studies"

        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret

    def get_context_data(self, **kwargs):
        '''
        Return the context data.
        The context data will contain the "next" page URL after successful login.

        @param self: the object itself
        @param kwarg: the key word arguments
        @return: context of the server
        '''
        # Do logging
        signature = self.CLASS_NAME + '.get_context_data'
        helper.log_entrance(self.LOGGER, signature, {'kwargs': kwargs})

        context = super(LoginView, self).get_context_data(**kwargs)
        context["next"] = self.request.GET.get("next", "")
        ret = context

        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret

class LogoutView(RedirectView):
    '''
    This is the Django view implementation for user logout.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @author: caoweiquan322
    @version: 1.0
    '''
    # Here we setup the member variables for logging.
    CLASS_NAME = 'hfppnetwork.sms.views.LogoutView'
    LOGGER = logging.getLogger(CLASS_NAME)

    def get(self, request, *args, **kwargs):
        '''
        This method is used to serve GET request.

        @param self: the object itself
        @param request: http request
        @param args: arguments without key words
        @param kwarg: key word arguments
        @return: result for serving GET request
        '''
        # Do logging
        signature = self.CLASS_NAME + '.get'
        helper.log_entrance(self.LOGGER, signature, {'args': args, 'kwargs': kwargs})

        logout(request)
        ret = super(LogoutView, self).get(request, *args, **kwargs)

        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret

    def get_redirect_url(self, **kwargs):
        '''
        Returns the redirect URL.

        @param self: the object itself
        @param kwarg: key word arguments
        @return: the redirect URL
        '''
        # Do logging
        signature = self.CLASS_NAME + '.get_redirect_url'
        helper.log_entrance(self.LOGGER, signature, {'kwargs': kwargs})

        ret = "/login"

        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret

class ListDARCEmailView(LoginRequiredMixin, ListView):
    '''
    This is the Django view implementation to list DARC emails.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @author: caoweiquan322
    @version: 1.0
    '''
    paginate_by = 10
    template_name = "darc_emails/list.html"
    context_object_name = "darc_emails"
    # Here we setup the member variables for logging.
    CLASS_NAME = 'hfppnetwork.sms.views.ListDARCEmailView'
    LOGGER = logging.getLogger(CLASS_NAME)

    def get_paginate_by(self, queryset):
        '''
        Get the page size.

        @param self: the object itself
        @param queryset: the queryset
        @return: number of items per page
        '''
        # Do logging
        signature = self.CLASS_NAME + '.get_paginate_by'
        helper.log_entrance(self.LOGGER, signature, {'queryset': queryset})

        page_size = helper.get_page_size(self)

        # Do logging
        helper.log_exit(self.LOGGER, signature, [page_size])
        return page_size

    def get_search_form(self):
        '''
        Return the DARCEmailSearchForm on the page.

        @param self: the object itself
        @return: the search form
        '''
        # Do logging
        signature = self.CLASS_NAME + '.get_search_form'
        helper.log_entrance(self.LOGGER, signature, None)

        search_form = self.form = DARCEmailSearchForm(self.request.REQUEST)

        # Do logging
        helper.log_exit(self.LOGGER, signature, [search_form])
        return search_form

    def get_queryset(self):
        '''
        Return the query set for the view.

        @param self: the object itself
        @return: the query set for the view
        '''
        # Do logging
        signature = self.CLASS_NAME + '.get_queryset'
        helper.log_entrance(self.LOGGER, signature, None)

        # Filter by role
        if self.request.user.is_staff == True:
            qs = DARCEmail.objects.all()
        else:
            qs = DARCEmail.objects.filter(id__exact=None)
        # Filter by search form
        search_form = self.get_search_form()
        if search_form.is_valid():
            search_name = search_form.cleaned_data.get("email")
            if search_name is not None and len(search_name.strip()) > 0:
                qs = qs.filter(email__contains=search_name.strip())

        # Do logging
        helper.log_exit(self.LOGGER, signature, [qs])
        return qs

    def get_context_data(self, **kwargs):
        '''
        Return the context data.

        @param self: the object itself
        @param kwargs: key word arguments
        @return: the context data
        '''
        # Do logging
        signature = self.CLASS_NAME + '.get_context_data'
        helper.log_entrance(self.LOGGER, signature, {'kwargs': kwargs})

        context = super(ListDARCEmailView, self).get_context_data(**kwargs)
        # Process paging data
        get = self.request.REQUEST.copy()
        page = get.get("page", None)
        extra = helper.remove_url_para(urlencode(get), 'page')
        if extra.strip() != '':
            extra = '&' + extra
        context['page'] = page
        context['extra_vars'] = extra
        context['search_form'] = self.get_search_form()
        # If user is not staff, the ADD_EMAIL button won't show
        context['user_is_staff'] = self.request.user.is_staff

        # Do logging
        helper.log_exit(self.LOGGER, signature, [context])
        return context

class CreateDARCEmailView(LoginRequiredMixin, CreateView):
    '''
    This is the Django view implementation to create DARC email.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @author: caoweiquan322
    @version: 1.0
    '''
    model = DARCEmail
    template_name = "darc_email/create.html"
    form_class = DARCEmailForm
    # Here we setup the member variables for logging.
    CLASS_NAME = 'hfppnetwork.sms.views.CreateDARCEmailView'
    LOGGER = logging.getLogger(CLASS_NAME)

    @transaction.commit_on_success
    def form_valid(self, form):
        '''
        This method is called when form is validated.

        @param self: the object itself
        @param form: the validate form
        @return: the http response
        '''
        # Do logging
        signature = self.CLASS_NAME + '.form_valid'
        helper.log_entrance(self.LOGGER, signature, {'form': form})

        # Check input parameters again.
        other_errors = {}
        if DARCEmail.objects.filter(email__exact=form.cleaned_data.get("email").strip()).count() > 0:
            other_errors['email'] = 'The Email you have entered is already in the list'
        if len(other_errors) > 0:
            ret = super(CreateDARCEmailView, self).render_to_response(
                                        self.get_context_data(form=form, errors = other_errors))
        else:
            self.object = form.save()
            ret = HttpResponseRedirect('/darc_emails')

        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret

class ListPartnerTagView(LoginRequiredMixin, SuperuserRequiredMixin, ListView):
    '''
    This is the Django view implementation to list partner tags.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @author: caoweiquan322
    @version: 1.0
    '''
    paginate_by = 10
    template_name = "partner_tags/list.html"
    context_object_name = "partner_tags"
    # Here we setup the member variables for logging.
    CLASS_NAME = 'hfppnetwork.sms.views.ListPartnerTagView'
    LOGGER = logging.getLogger(CLASS_NAME)

    def get_paginate_by(self, queryset):
        '''
        Get the page size.

        @param self: the object itself
        @param queryset: the queryset
        @return: number of items in each page
        '''
        # Do logging
        signature = self.CLASS_NAME + '.get_paginate_by'
        helper.log_entrance(self.LOGGER, signature, {'queryset': queryset})

        page_size = helper.get_page_size(self)

        # Do logging
        helper.log_exit(self.LOGGER, signature, [page_size])
        return page_size

    def get_search_form(self):
        '''
        Return the PartnerTagSearchForm on the page.

        @param self: the object itself
        @return: the search form
        '''
        # Do logging
        signature = self.CLASS_NAME + '.get_search_form'
        helper.log_entrance(self.LOGGER, signature, None)

        search_form = self.form = PartnerTagSearchForm(self.request.REQUEST)

        # Do logging
        helper.log_exit(self.LOGGER, signature, [search_form])
        return search_form

    def get_queryset(self):
        '''
        Return the query set for the view.

        @param self: the object itself
        @return: the query set for the view
        '''
        # Do logging
        signature = self.CLASS_NAME + '.get_queryset'
        helper.log_entrance(self.LOGGER, signature, None)

        # Filter by role
        if self.request.user.is_staff == True:
            qs = PartnerTag.objects.all()
        else:
            qs = PartnerTag.objects.filter(owner__exact=self.request.user)

        # Filter by search form
        search_form = self.get_search_form()
        if search_form.is_valid():
            search_name = search_form.cleaned_data.get("name")
            if search_name is not None and len(search_name.strip()) > 0:
                qs = qs.filter(name__contains=search_name.strip())


        # Do logging
        helper.log_exit(self.LOGGER, signature, [qs])
        return qs

    def get_context_data(self, **kwargs):
        '''
        Get the context data.

        @param self: the object itself
        @param kwargs: key word arguments
        @return: the context data
        '''
        # Do logging
        signature = self.CLASS_NAME + '.get_context_data'
        helper.log_entrance(self.LOGGER, signature, {'kwargs': kwargs})

        context = super(ListPartnerTagView, self).get_context_data(**kwargs)
        # Process paging data
        get = self.request.REQUEST.copy()
        page = get.get("page", None)
        extra = helper.remove_url_para(urlencode(get), 'page')
        if extra.strip() != '':
            extra = '&' + extra
        context['page'] = page
        context['extra_vars'] = extra
        context['search_form'] = self.get_search_form()
        # Do logging
        helper.log_exit(self.LOGGER, signature, [context])
        return context

class CreatePartnerTagView(LoginRequiredMixin, SuperuserRequiredMixin, JSONResponseMixin, CreateView):
    '''
    This is the Django view implementation to create partner tag.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @author: caoweiquan322
    @version: 1.0
    '''
    model = PartnerTag
    form_class = PartnerTagForm
    # Here we setup the member variables for logging.
    CLASS_NAME = 'hfppnetwork.sms.views.CreatePartnerTagView'
    LOGGER = logging.getLogger(CLASS_NAME)

    def get_success_url(self):
        '''
        Redirect the url when update succeed.

        @param self: the object itself
        @return: the redirected url
        '''
        # Do logging
        signature = self.CLASS_NAME + '.get_success_url'
        helper.log_entrance(self.LOGGER, signature, None)

        ret = '/partner_tags'

        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret

    def form_valid(self, form):
        '''
        This method will be called if the submitted form is valid.

        @param self: the object itself
        @param form: the validated form
        '''
        # Do logging
        signature = self.CLASS_NAME + '.form_valid'
        helper.log_entrance(self.LOGGER, signature, {'form': form})

        other_errors = {}
        if len(form.cleaned_data.get("name").strip()) == 0:
            other_errors['name'] = 'This field is required'
        if PartnerTag.objects.filter(name__exact=form.cleaned_data.get("name").strip()).count() > 0:
            other_errors['name'] = 'There is title with the same name'
        if len(form.cleaned_data.get("description").strip()) == 0:
            other_errors['description'] = 'This field is required'
        if len(other_errors) > 0:
            if self.request.is_ajax():
                ret = self.render_to_json_response(
                        {'errors': other_errors}, status=400)
            else:
                ret = super(CreatePartnerTagView, self).form_invalid(form)
        else:
            ret = super(CreatePartnerTagView, self).form_valid(form)
        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret

class EditPartnerTagView(LoginRequiredMixin, SuperuserRequiredMixin, JSONResponseMixin, UpdateView):
    '''
    This is the Django view implementation to create partner tag.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @author: caoweiquan322
    @version: 1.0
    '''
    model = PartnerTag
    form_class = PartnerTagForm
    # Here we setup the member variables for logging.
    CLASS_NAME = 'hfppnetwork.sms.views.EditPartnerTagView'
    LOGGER = logging.getLogger(CLASS_NAME)

    def get_queryset(self):
        '''
        Return the query set.

        @param self: the object itself
        @return: the query set
        '''
        # Do logging
        signature = self.CLASS_NAME + '.get_queryset'
        helper.log_entrance(self.LOGGER, signature, None)

        qs = super(UpdateView, self).get_queryset()
        # Staff(Admin) can view all tags
        if self.request.user.is_staff == True:
            ret = qs;
        # Regular user can view own tags
        else:
            ret = qs.filter(owner__exact=self.request.user)

        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret

    def get_success_url(self):
        '''
        Redirect the url when update succeed.

        @param self: the object itself
        @return: the redirected url
        '''
        # Do logging
        signature = self.CLASS_NAME + '.get_success_url'
        helper.log_entrance(self.LOGGER, signature, None)

        ret = '/partner_tags'

        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret

class ListPartnerView(LoginRequiredMixin, SuperuserRequiredMixin, ListView):
    '''
    This is the Django view implementation to list partners.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @author: caoweiquan322
    @version: 1.0
    '''
    paginate_by = 10
    template_name = "partners/list.html"
    context_object_name = "partners"
    # Here we setup the member variables for logging.
    CLASS_NAME = 'hfppnetwork.sms.views.ListPartnerView'
    LOGGER = logging.getLogger(CLASS_NAME)

    def get_paginate_by(self, queryset):
        '''
        Get the page size.

        @param self: the object itself
        @param queryset: the query set
        @return: number of items in each page
        '''
        signature = self.CLASS_NAME + '.get_paginate_by'
        helper.log_entrance(self.LOGGER, signature, {'queryset': queryset})

        page_size = helper.get_page_size(self)

        # Do logging
        helper.log_exit(self.LOGGER, signature, [page_size])
        return page_size

    def get_search_form(self):
        '''
        Return the PartnerSearchForm on the page.

        @param self: the object itself
        @return:the search form for this page
        '''
        signature = self.CLASS_NAME + '.get_search_form'
        helper.log_entrance(self.LOGGER, signature, None)

        search_form = self.form = PartnerSearchForm(self.request.REQUEST)

        # Do logging
        helper.log_exit(self.LOGGER, signature, [search_form])
        return search_form

    def get_queryset(self):
        '''
        Return the query set for the view.

        @param self: the object itself
        @return: the query set for the view
        '''
        signature = self.CLASS_NAME + '.get_queryset'
        helper.log_entrance(self.LOGGER, signature, None)

        # Filter by role
        if self.request.user.is_staff == True:
            qs = Partner.objects.all()
        else:
            qs = Partner.objects.filter(owner__exact=self.request.user)
        # Filter by search form
        search_form = self.get_search_form()
        if search_form.is_valid():
            search_company_name = search_form.cleaned_data.get("company_name")
            if search_company_name is not None and len(search_company_name.strip()) > 0:
                qs = qs.filter(company_name__contains=search_company_name.strip())

        # Do logging
        helper.log_exit(self.LOGGER, signature, [qs])
        return qs

    def get_context_data(self, **kwargs):
        '''
        Return the context data.

        @param self: the object itself
        @param kwargs: key word arguments
        @return: the context data
        '''
        signature = self.CLASS_NAME + '.get_context_data'
        helper.log_entrance(self.LOGGER, signature, {'kwargs': kwargs})

        context = super(ListPartnerView, self).get_context_data(**kwargs)
        # Process paging data
        get = self.request.REQUEST.copy()
        page = get.get("page", None)
        extra = helper.remove_url_para(urlencode(get), 'page')
        if extra.strip() != '':
            extra = '&' + extra
        context['page'] = page
        context['active_partner_id'] = get.get("active", "")
        context['extra_vars'] = extra
        context['search_form'] = self.get_search_form()
        if self.request.user.is_staff == True:
            context['partner_tags'] = PartnerTag.objects.all()
        else:
            context['partner_tags'] = PartnerTag.objects.filter(owner__exact=self.request.user)
        context['role_list'] = helper.pull_hub_roles()
        context['states'] = STATES
        # Do logging
        helper.log_exit(self.LOGGER, signature, [context])
        return context

class CreatePartnerView(LoginRequiredMixin, SuperuserRequiredMixin, JSONResponseMixin, CreateView):
    '''
    This is the Django view implementation to create partner.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @author: caoweiquan322
    @version: 1.0
    '''
    model = Partner
    form_class = PartnerForm
    # Here we setup the member variables for logging.
    CLASS_NAME = 'hfppnetwork.sms.views.CreatePartnerView'
    LOGGER = logging.getLogger(CLASS_NAME)

    @transaction.commit_on_success
    def form_valid(self, form):
        '''
        This method will be called if the submitted form is valid.

        @param self: the object itself
        @param form: the validated form
        @return: the http response
        '''
        signature = self.CLASS_NAME + '.form_valid'
        helper.log_entrance(self.LOGGER, signature, {'form': form})

        context = self.get_context_data()
        form.instance.owner = self.request.user
        form.instance.count_of_data_requests_received = 0
        form.instance.count_of_data_requests_sent = 0
        form.instance.count_of_data_requests_declined = 0
        form.instance.count_of_data_requests_pending = 0
        form.instance.count_of_data_requests_responded = INITIAL_RESPONDED_REQUESTS_VALUE

        # Check input values again
        other_errors = {}
        if len(form.cleaned_data.get("company_name").strip()) == 0:
            other_errors['company_name'] = 'This field is required'
        if len(form.cleaned_data.get("city").strip()) == 0:
            other_errors['city'] = 'This field is required'
        if len(form.cleaned_data.get("division").strip()) == 0:
            other_errors['division'] = 'This field is required'
        if len(form.cleaned_data.get("region").strip()) == 0:
            other_errors['region'] = 'This field is required'
        if len(form.cleaned_data.get("network_username").strip()) == 0:
            other_errors['network_username'] = 'This field is required'
        if len(form.cleaned_data.get("network_password").strip()) == 0:
            other_errors['network_password'] = 'This field is required'
        if len(form.cleaned_data.get("network_organization_name").strip()) == 0:
            other_errors['network_organization_name'] = 'This field is required'
        if len(form.cleaned_data.get("network_role").strip()) == 0:
            other_errors['network_role'] = 'This field is required'
            
        # Get the formsets for contacts
        partner_contact_formset = context['partner_contact_formset']
        if partner_contact_formset.is_valid():
            network_id = helper.add_hub_partner(form.cleaned_data.get("network_username").strip(), 
                form.cleaned_data.get("network_organization_name").strip(),
                form.cleaned_data.get("network_role").strip(),
                str(form.cleaned_data.get("network_auto_retrieve_cached_data")).lower(),
                form.cleaned_data.get("network_password").strip())
            if not network_id:
                other_errors['network_username'] = 'duplicated or error happens in hub/node'
            else:
                form.instance.hfpp_network_id = network_id
                form.cleaned_data['hfpp_network_id'] = network_id
                self.object = form.save()
                partner_contact_formset.instance = self.object
                partner_contact_formset.save()
        else:
            other_errors['contacts'] = 'Invalid contacts, Check the email/phone'

        if len(other_errors) > 0:
            if self.request.is_ajax():
                ret = self.render_to_json_response(
                        {'errors': other_errors}, status=400)
            else:
                ret = super(CreatePartnerView, self).form_invalid(form)
        else:
            ret = self.render_to_response(self.get_context_data(form=form))

        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret

    def get_context_data(self, **kwargs):
        '''
        Return the context data.

        @param self: the object itself
        @return: the context data
        '''
        signature = self.CLASS_NAME + '.get_context_data'
        helper.log_entrance(self.LOGGER, signature, {'kwargs': kwargs})

        context = super(CreatePartnerView, self).get_context_data(**kwargs)
        # inline formsets
        if self.request.POST:
            context['partner_contact_formset'] = PartnerContactFormSet(self.request.POST,
                                                                       instance=self.object)
        else:
            context['partner_contact_formset'] = PartnerContactFormSet()

        # Do logging
        helper.log_exit(self.LOGGER, signature, [context])
        return context

    def render_to_response(self, context, **response_kwargs):
        '''
        Overwrite to render to response with json when appropriate.

        @param self: the object itself
        @param context: the context to render template
        @param response_kwargs: the key word arguments for response
        '''
        if self.request.is_ajax():
            json_context = {}
            formset = context.get('partner_contact_formset', None)
            if formset:
                json_context['partner_contact_formset_managementForm'] = str(formset.management_form)
                strForms = ''
                for oneForm in formset.forms:
                    strForms = strForms + '<table>' + str(oneForm) + '</table>'
                json_context['partner_contact_formset_forms'] = strForms
                json_context['partner_contact_formset_prefix'] = formset.prefix
            ret = self.render_to_json_response(json_context)
        else:
            ret = super(CreatePartnerView, self).render_to_response(context, **response_kwargs)
        return ret

class EditPartnerView(LoginRequiredMixin, SuperuserRequiredMixin, JSONResponseMixin, UpdateView):
    '''
    This is the Django view implementation to update partner.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @author: caoweiquan322
    @version: 1.0
    '''
    model = Partner
    form_class = PartnerForm
    # Here we setup the member variables for logging.
    CLASS_NAME = 'hfppnetwork.sms.views.EditPartnerView'
    LOGGER = logging.getLogger(CLASS_NAME)

    @transaction.commit_on_success
    def form_valid(self, form):
        '''
        This method will be called if the submitted form is valid.

        @param self: the object itself
        @param form: the validated form
        @return: the http response
        '''
        signature = self.CLASS_NAME + '.form_valid'
        helper.log_entrance(self.LOGGER, signature, {'form': form})

        context = self.get_context_data()
        form.instance.owner = self.request.user;
        other_errors = {}
        # Get the formsets for contacts
        partner_contact_formset = context['partner_contact_formset']
        if partner_contact_formset.is_valid():
            network_id = helper.edit_hub_partner(form.cleaned_data.get("hfpp_network_id").strip(),
                form.cleaned_data.get("network_username").strip(), 
                form.cleaned_data.get("network_organization_name").strip(),
                form.cleaned_data.get("network_role").strip(),
                str(form.cleaned_data.get("network_auto_retrieve_cached_data")).lower(),
                form.cleaned_data.get("network_password").strip())
            if not network_id:
                other_errors['network_username'] = 'duplicated or error happens in hub/node'
            self.object = form.save()
            partner_contact_formset.instance = self.object
            partner_contact_formset.save()

        if len(other_errors) > 0:
            if self.request.is_ajax():
                ret = self.render_to_json_response(
                        {'errors': other_errors}, status=400)
            else:
                ret = super(CreatePartnerView, self).form_invalid(form)
        else:
            ret = self.render_to_response(self.get_context_data(form=form))
        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret

    def get_context_data(self, **kwargs):
        '''
        Return the context data.

        @param self: the object itself
        @param kwargs: key word arguments
        @return: the context data
        '''
        signature = self.CLASS_NAME + '.get_context_data'
        helper.log_entrance(self.LOGGER, signature, {'kwargs': kwargs})

        context = super(EditPartnerView, self).get_context_data(**kwargs)
        # inline formsets
        if self.request.POST:
            context['partner_contact_formset'] = PartnerContactFormSet(self.request.POST,
                                                                       instance=self.object)
        else:
            # Get the contact formset corresponding to this partner
            try:
                formset = PartnerContactFormSet(instance =
                       Partner.objects.get(id=int(self.kwargs.get('pk', None))))
            except:
                formset = PartnerContactFormSet()
            context['partner_contact_formset'] = formset

        # Do logging
        helper.log_exit(self.LOGGER, signature, [context])
        return context

    def get_queryset(self):
        '''
        Return the query set.

        @param self: the object itself
        @return: the query set
        '''
        signature = self.CLASS_NAME + '.get_queryset'
        helper.log_entrance(self.LOGGER, signature, None)

        qs = super(UpdateView, self).get_queryset()
        # Staff(Admin) can view all partners
        if self.request.user.is_staff == True:
            ret = qs;
        # Regular user can view own partners
        else:
            ret = qs.filter(owner__exact=self.request.user)

        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret
    def render_to_response(self, context, **response_kwargs):
        '''
        Overwrite to render to response with json when appropriate.

        @param self: the object itself
        @param context: the context for template
        @param response_kwargs: the key word arguments for response
        '''
        if self.request.is_ajax():
            json_context = {}
            formset = context.get('partner_contact_formset', None)
            if formset:
                json_context['partner_contact_formset_managementForm'] = str(formset.management_form)
                strForms = ''
                for oneForm in formset.forms:
                    strForms = strForms + '<table>' + str(oneForm) + '</table>'
                json_context['partner_contact_formset_forms'] = strForms
                json_context['partner_contact_formset_prefix'] = formset.prefix
            ret = self.render_to_json_response(json_context)
        else:
            ret = super(CreatePartnerView, self).render_to_response(context, **response_kwargs)
        return ret

class ViewUserProfileView(LoginRequiredMixin, DetailView):
    '''
    This is the Django view implementation to view user profile detail.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @author: caoweiquan322
    @version: 1.0
    '''
    model = User
    template_name = "users/detail.html"
    context_object_name = "user"
    # Here we setup the member variables for logging.
    CLASS_NAME = 'hfppnetwork.sms.views.ViewUserProfileView'
    LOGGER = logging.getLogger(CLASS_NAME)

    def get_queryset(self):
        '''
        Return the query set for the view.

        @param self: the object itself
        @return: the query set
        '''
        # Do logging
        signature = self.CLASS_NAME + '.get_queryset'
        helper.log_entrance(self.LOGGER, signature, None)

        self.kwargs['pk'] = self.request.user.id
        ret = User.objects.filter(id__exact=self.request.user.id)

        # Do logging
        helper.log_exit(self.LOGGER, signature, [ret])
        return ret

@login_required
@transaction.commit_on_success
@require_http_methods(["POST"])
def delete_darc_emails(request):
    '''
    This is the view function for deleting one or more DARC emails.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @param request: the http request
    @return: the http response
    '''
    CLASS_NAME = 'hfppnetwork.sms'
    LOGGER = logging.getLogger(CLASS_NAME)
    # Do logging
    signature = CLASS_NAME + '.delete_darc_emails'
    helper.log_entrance(LOGGER, signature, {'request': request})

    email = DARCEmail.objects.get(id=request.POST['ids'])
    if email and request.user.is_staff is True:
        email.delete()

    # Redirect to /darc_emails
    ret = HttpResponseRedirect('/darc_emails')
    # Do logging
    helper.log_exit(LOGGER, signature, [ret])
    return ret

@login_required
@transaction.commit_on_success
@require_http_methods(["POST"])
def delete_partners(request):
    '''
    This is the view function for deleting one or more partners.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @param request: the http request
    @return: the http response
    '''
    CLASS_NAME = 'hfppnetwork.sms'
    LOGGER = logging.getLogger(CLASS_NAME)
    # Do logging
    signature = CLASS_NAME + '.delete_partners'
    helper.log_entrance(LOGGER, signature, {'request': request})

    # only superuser can request delete
    if not request.user.is_superuser:
        return HttpResponse('Unauthorized', status=401)

    for partner_id in request.POST.getlist('ids'):
        partner = Partner.objects.get(id=int(partner_id))
        if partner.owner == request.user or request.user.is_staff is True:
            helper.delete_hub_partner(partner.hfpp_network_id)
            partner.delete()

    # Redirect to /partners
    ret = HttpResponseRedirect('/partners')
    # Do logging
    helper.log_exit(LOGGER, signature, [ret])
    return ret

@login_required
@transaction.commit_on_success
@require_http_methods(["POST"])
def delete_partner_tags(request):
    '''
    This is the view function for deleting one or more partner tags.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @param request: the http request
    @return: the http response
    '''
    CLASS_NAME = 'hfppnetwork.sms'
    LOGGER = logging.getLogger(CLASS_NAME)
    # Do logging
    signature = CLASS_NAME + '.delete_partner_tags'
    helper.log_entrance(LOGGER, signature, {'request': request})

    # only superuser can request delete
    if not request.user.is_superuser:
        return HttpResponse('Unauthorized', status=401)

    tag = PartnerTag.objects.get(id=request.POST['ids'])
    if tag.owner == request.user or request.user.is_staff is True:
        tag.delete()

    # Redirect to /partner_tags
    ret = HttpResponseRedirect('/partner_tags')
    # Do logging
    helper.log_exit(LOGGER, signature, [ret])
    return ret

def redirect_to_admin_auth_user(request):
    '''
    Redirect all related auth urls to /admin/auth/user, so that some unwanted
    pages won't come out.

    Thread Safety:
    The implementation is not thread safe but it will be used in a thread-safe manner.

    @param request: the http request
    @return: the http response
    '''
    CLASS_NAME = 'hfppnetwork.sms'
    LOGGER = logging.getLogger(CLASS_NAME)
    # Do logging
    signature = CLASS_NAME + '.redirect_to_admin_auth_user'
    helper.log_entrance(LOGGER, signature, {'request': request})

    # Redirect to /partner_tags
    ret = HttpResponseRedirect('/admin/auth/user')
    # Do logging
    helper.log_exit(LOGGER, signature, [ret])
    return ret

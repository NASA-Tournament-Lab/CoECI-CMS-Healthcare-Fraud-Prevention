# -*- coding: utf-8 -*-
"""
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.

This is module that represents all the models.

@author:  TCSASSEMBLER
@version: 1.0
"""
import copy

from django.db import models
from django.core.validators import RegexValidator, MaxValueValidator,\
    MinValueValidator
from django.contrib.auth.models import User

"""
This is the tuple represents the study data request statuses.
"""
STUDY_DATA_REQUEST_STATUSES = (
    (0, 'Pending'),
    (1, 'Satisfied'),
    (2, 'Declined')
)

"""
This is the tuple represents the study statuses.
"""
STUDY_STATUSES = (
    (0, 'Draft'),
    (1, 'In Progress'),
    (2, 'Analysis'),
    (3, 'Archived')
)

"""
This is the tuple represents the US states.
"""
STATES = (
    (1, 'Alabama'),
    (2, 'Alaska'),
    (3, 'Arizona'),
    (4, 'Arkansas'),
    (5, 'California'),
    (6, 'Colorado'),
    (7, 'Connecticut'),
    (8, 'Delaware'),
    (9, 'Florida'),
    (10, 'Georgia'),
    (11, 'Hawaii'),
    (12, 'Idaho'),
    (13, 'Illinois'),
    (14, 'Indiana'),
    (15, 'Iowa'),
    (16, 'Kansas'),
    (17, 'Kentucky'),
    (18, 'Louisiana'),
    (19, 'Maine'),
    (20, 'Maryland'),
    (21, 'Massachusetts'),
    (22, 'Michigan'),
    (23, 'Minnesota'),
    (24, 'Mississippi'),
    (25, 'Missouri'),
    (26, 'Montana'),
    (27, 'Nebraska'),
    (28, 'Nevada'),
    (29, 'New Hampshire'),
    (30, 'New Jersey'),
    (31, 'New Mexico'),
    (32, 'New York'),
    (33, 'North Carolina'),
    (34, 'North Dakota'),
    (35, 'Ohio'),
    (36, 'Oklahoma'),
    (37, 'Oregon'),
    (38, 'Pennsylvania'),
    (39, 'Rhode Island'),
    (40, 'South Carolina'),
    (41, 'South Dakota'),
    (42, 'Tennessee'),
    (43, 'Texas'),
    (44, 'Utah'),
    (45, 'Vermont'),
    (46, 'Virginia'),
    (47, 'Washington'),
    (48, 'West Virginia'),
    (49, 'Wisconsin'),
    (50, 'Wyoming')
)

YES_NO_CHOICES = (
    (1, 'Yes'),
    (2, 'No')
)

MAX_DIGITS = 10

class PartnerTag(models.Model):
    """
    The Django data model that represents a partner tag.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    #Represents the name.
    name = models.CharField(max_length=100)
    #Represents the description.
    description = models.TextField(max_length=1024)
    #Represents the owner.
    owner = models.ForeignKey(User)

class Partner(models.Model):
    """
    The Django data model that represents a partner.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    #Represents the HFPP network ID of the partner.
    hfpp_network_id = models.CharField(max_length=100)
    #Represents the company name.
    company_name = models.CharField(max_length=100)
    #Represents the city.
    city = models.CharField(max_length=100)
    #Represents the state.
    state = models.IntegerField(choices=STATES)
    #Represents the division.
    division = models.CharField(max_length=100)
    #Represents the region.
    region = models.CharField(max_length=100)
    #Represents the number of insured.
    number_of_insured = models.PositiveIntegerField()
    #Represents the network username
    network_username = models.CharField(max_length=50)
    #Represents the network password
    network_password = models.CharField(max_length=50, blank=True, null=True)
    #Represents the network organization
    network_organization_name = models.CharField(max_length=50)
    #Represents the network role
    network_role = models.CharField(max_length=50)
    #Represents the network auto retrieve cached data
    network_auto_retrieve_cached_data = models.BooleanField(default=True)
    #Represents the tags.
    tags = models.ManyToManyField(PartnerTag)
    #Represents the count of data requests received.
    count_of_data_requests_received = models.IntegerField()
    #Represents the count of data requests sent.
    count_of_data_requests_sent = models.IntegerField()
    #Represents the count of data requests declined.
    count_of_data_requests_declined = models.IntegerField()
    #Represents the count of data requests responded.
    count_of_data_requests_responded = models.IntegerField()
    #Represents the count of data requests pending.
    count_of_data_requests_pending = models.IntegerField()
    #Represents the reciprocity.
    reciprocity = models.FloatField()
    #Represents the owner.
    owner = models.ForeignKey(User)
    
    def __str__(self):
        return self.company_name
    
class PartnerContact(models.Model):
    """
    The Django data model that represents a partner contact.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    #Represents the partner.
    partner = models.ForeignKey(Partner)
    #Represents the full name.
    full_name = models.CharField(max_length=100)
    #Represents the job title.
    job_title = models.CharField(max_length=100)
    #Represents the company name.
    company_name = models.CharField(max_length=100)
    #Represents the office address.
    office_address = models.CharField(max_length=100)
    #Represents the email.
    email = models.EmailField()
    #Represents the phone.
    phone = models.CharField(max_length=50)
    #Represents the notes.
    notes = models.TextField(max_length=500)

class Study(models.Model):
    """
    The Django data model that represents a study.
    @author:  TCSASSEMBLER
    @version: 1.0
    """

    #Represents the description.
    description = models.TextField(max_length=1024, blank=True)
    #Represents the query.
    beneficiary_query = models.TextField(max_length=1024, blank=True)
    carrier_query = models.TextField(max_length=1024, blank=True)
    inpatient_query = models.TextField(max_length=1024, blank=True)
    outpatient_query = models.TextField(max_length=1024, blank=True)
    prescription_query = models.TextField(max_length=1024, blank=True)
    #Represents the expiration time.
    expiration_time = models.DateTimeField(blank=True)
    #Represents the status.
    status = models.IntegerField(choices=STUDY_STATUSES, default=0)
    #Represents the HFPP network data request ID.
    data_request_id = models.CharField(max_length=64, blank=True)
    #Indicates whether the  request is cache safe.
    is_cache_safe = models.BooleanField()
    #Represents the original requester.
    on_behalf_of = models.ForeignKey(Partner, blank=True, null=True)
    #Represents the owner of this study.
    owner = models.ForeignKey(User)
    #Represents the created on timestamp.
    created_on = models.DateTimeField(auto_now_add=True)
    #Represents the last modified on timestamp.
    last_modified_on = models.DateTimeField(auto_now=True)
    #Represents the executed on timestamp.
    executed_on = models.DateTimeField(null=True, blank=True)
    #Represents the completed on timestamp.
    completed_on = models.DateTimeField(null=True, blank=True)
    #Represents the report.
    report = models.FilePathField(blank=True, null=True)

    def _get_count_of_data_requests_sent(self):
        """
        This function is used to calculate the count of data requests sent.
        
        Parameters:
        - self : this object.
        
        Returns:
        the count
        """
        return StudyDataRequest.objects.filter(study__exact=self).count()
    
    def _get_count_of_data_requests_pending(self):
        """
        This function is used to calculate the count of data requests pending.
        
        Parameters:
        - self : this object.
        
        Returns:
        the count
        """
        return StudyDataRequest.objects.filter(study__exact=self, status__exact=0).count()
    
    def _get_count_of_data_requests_satisfied(self):
        """
        This function is used to calculate the count of data requests satisfied.
        
        Parameters:
        - self : this object.
        
        Returns:
        the count
        """
        return StudyDataRequest.objects.filter(study__exact=self, status__exact=1).count()
    
    def _get_count_of_data_requests_declined(self):
        """
        This function is used to calculate the count of data requests declined.
        
        Parameters:
        - self : this object.
        
        Returns:
        the count
        """
        return StudyDataRequest.objects.filter(study__exact=self, status__exact=2).count()

    #Represents the count of data requests sent.
    count_of_data_requests_sent = property(_get_count_of_data_requests_sent)
    #Represents the count of data requests pending.
    count_of_data_requests_pending = property(_get_count_of_data_requests_pending)
    #Represents the count of data requests satisfied.
    count_of_data_requests_satisfied = property(_get_count_of_data_requests_satisfied)
    #Represents the count of data requests declined.
    count_of_data_requests_declined = property(_get_count_of_data_requests_declined)

class StudyChart(models.Model):
    """
    The Django data model that represents a chart generated for a study.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    #Represents the study.
    study = models.ForeignKey(Study)
    #Represents the chart name.
    name = models.CharField(max_length=255)
    #Represents the SVG content of the chart.
    svg = models.TextField()

class StudyDataRequest(models.Model):
    """
    The Django data model that represents a data request for a study.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    #Represents the study.
    study = models.ForeignKey(Study)
    #Represents the requested partner.
    partner = models.ForeignKey(Partner)
    #Represents the status of the data request.
    status = models.IntegerField(choices=STUDY_DATA_REQUEST_STATUSES, default=0)
    #Represents the response data.
    response_data = models.TextField(blank=True)

class StudyClaimData(models.Model):
    """
    This is the base class for study claim data.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    study = models.ForeignKey(Study)
    class Meta:
        abstract = True

class OutpatientClaimData(StudyClaimData):
    """
    Represents the Outpatient claim data.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    desynpuf_id = models.CharField(max_length=16, validators=[RegexValidator(regex='^[0-9A-Fa-f]{16}$', message='Must be 16 Char of Hexadecimal', code='nomatch')])
    clm_id = models.CharField(max_length=14, validators=[RegexValidator(regex='^[0-9]{14}$', message='Must be 14 Char of digit', code='nomatch')])
    segment = models.IntegerField()
    clm_from_dt = models.DateField()
    clm_thru_dt = models.DateField()
    prvdr_num = models.CharField(max_length=6)
    clm_pmt_amt = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    nch_prmry_pyr_clm_pd_amt = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    at_physn_npi = models.CharField(max_length=10)
    op_physn_npi = models.CharField(max_length=10)
    ot_physn_npi = models.CharField(max_length=10)
    nch_bene_blood_ddctbl_lblty_am = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    icd9_dgns_cd_1 = models.CharField(max_length=9)
    icd9_dgns_cd_2 = models.CharField(max_length=9) 
    icd9_dgns_cd_3 = models.CharField(max_length=9) 
    icd9_dgns_cd_4 = models.CharField(max_length=9) 
    icd9_dgns_cd_5 = models.CharField(max_length=9) 
    icd9_dgns_cd_6 = models.CharField(max_length=9) 
    icd9_dgns_cd_7 = models.CharField(max_length=9) 
    icd9_dgns_cd_8 = models.CharField(max_length=9) 
    icd9_dgns_cd_9 = models.CharField(max_length=9)
    icd9_dgns_cd_10 = models.CharField(max_length=9) 
    icd9_prcdr_cd_1 = models.CharField(max_length=9)
    icd9_prcdr_cd_2 = models.CharField(max_length=9)
    icd9_prcdr_cd_3 = models.CharField(max_length=9)
    icd9_prcdr_cd_4 = models.CharField(max_length=9)
    icd9_prcdr_cd_5 = models.CharField(max_length=9)
    icd9_prcdr_cd_6 = models.CharField(max_length=9)
    nch_bene_ptb_ddctbl_amt = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    nch_bene_ptb_coinsrnc_amt = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    admtng_icd9_dgns_cd = models.CharField(max_length=5)
    hcpcs_cd_1 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_2 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_3 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_4 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_5 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_6 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_7 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_8 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_9 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_10 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_11 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_12 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_13 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_14 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_15 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_16 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_17 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_18 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_19 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_20 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_21 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_22 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_23 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_24 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_25 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_26 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_27 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_28 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_29 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_30 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_31 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_32 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_33 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_34 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_35 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_36 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_37 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_38 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_39 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_40 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_41 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_42 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_43 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_44 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])
    hcpcs_cd_45 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [0-9A-Z]', code='nomatch')])

class InpatientClaimData(StudyClaimData):
    """
    Represents the InPatient claim data.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    desynpuf_id = models.CharField(max_length=16, validators=[RegexValidator(regex='^[0-9A-Fa-f]{16}$', message='Must be 16 Char of Hexadecimal', code='nomatch')])
    clm_id = models.CharField(max_length=14, validators=[RegexValidator(regex='^[0-9]{14}$', message='Must be 14 Char of digit', code='nomatch')])
    segment = models.IntegerField()
    clm_from_dt = models.DateField()
    clm_thru_dt = models.DateField()
    prvdr_num = models.CharField(max_length=16)
    clm_pmt_amt = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    nch_prmry_pyr_clm_pd_amt = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    at_physn_npi = models.CharField(max_length=10, blank=True)
    op_physn_npi = models.CharField(max_length=10, blank=True)
    ot_physn_npi = models.CharField(max_length=10, blank=True)
    clm_admsn_dt = models.DateField()
    admtng_icd9_dgns_cd = models.CharField(max_length=9, blank=True)
    clm_pass_thru_per_diem_amt = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    nch_bene_ip_ddctbl_amt = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    nch_bene_pta_coinsrnc_lblty_am = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    nch_bene_blood_ddctbl_lblty_am = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    clm_utlztn_day_cnt = models.IntegerField()
    nch_bene_dschrg_dt = models.DateField()
    clm_drg_cd = models.CharField(max_length=3, blank=True)
    icd9_dgns_cd_1 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    icd9_dgns_cd_2 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    icd9_dgns_cd_3 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    icd9_dgns_cd_4 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    icd9_dgns_cd_5 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    icd9_dgns_cd_6 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    icd9_dgns_cd_7 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    icd9_dgns_cd_8 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    icd9_dgns_cd_9 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    icd9_dgns_cd_10 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    icd9_prcdr_cd_1 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    icd9_prcdr_cd_2 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    icd9_prcdr_cd_3 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    icd9_prcdr_cd_4 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    icd9_prcdr_cd_5 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    icd9_prcdr_cd_6 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_1 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_2 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_3 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_4 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_5 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_6 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_7 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_8 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_9 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_10 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_11 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_12 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_13 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_14 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_15 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_16 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_17 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_18 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_19 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_20 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_21 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_22 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_23 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_24 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_25 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_26 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_27 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_28 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_29 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_30 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_31 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_32 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_33 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_34 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_35 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_36 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_37 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_38 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_39 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_40 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_41 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_42 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_43 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_44 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])
    hcpcs_cd_45 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{0,9}$', message='Must be less than 10 Char of [0-9]', code='nomatch')])

class BeneficiaryClaimData(StudyClaimData):
    """
    Represents the Beneficiary Summary claim data.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    desynpuf_id = models.CharField(max_length=16, validators=[RegexValidator(regex='^[0-9A-Fa-f]{16}$', message='Must be 16 Char of Hexadecimal', code='nomatch')])
    bene_birth_dt = models.DateField()
    bene_death_dt = models.DateField()
    bene_sex_ident_cd = models.IntegerField(choices=((1, '1'), (2, '2')), default=1)
    bene_race_cd = models.IntegerField(choices=((1, 'White'), (2, 'Black'), (3, 'Others'), (5, 'Hispanic')))
    bene_esrd_ind = models.CharField(max_length=1, validators=[RegexValidator(regex='^[0Y]$', message='Must be 0 or Y', code='nomatch')])
    sp_state_code = models.CharField(max_length=2, validators=[RegexValidator(regex='^[0-9]{2}$', message='Must be 2 Char of [0-9]', code='nomatch')])
    bene_county_cd = models.CharField(max_length=3, validators=[RegexValidator(regex='^[0-9]{3}$', message='Must be 3 Char of [0-9]', code='nomatch')])
    bene_hi_cvrage_tot_mons = models.IntegerField(default=0, validators=[MinValueValidator(0), MaxValueValidator(12)])
    bene_smi_cvrage_tot_mons = models.IntegerField(default=0, validators=[MinValueValidator(0), MaxValueValidator(12)])
    bene_hmo_cvrage_tot_mons = models.IntegerField(default=0, validators=[MinValueValidator(0), MaxValueValidator(12)])
    plan_cvrg_mos_num = models.IntegerField(default=0, validators=[MinValueValidator(0), MaxValueValidator(12)])
    sp_alzhdmta = models.IntegerField(choices=YES_NO_CHOICES, default=2)
    sp_chf = models.IntegerField(choices=YES_NO_CHOICES, default=2)
    sp_chrnkidn = models.IntegerField(choices=YES_NO_CHOICES, default=2)
    sp_cncr = models.IntegerField(choices=YES_NO_CHOICES, default=2)
    sp_copd = models.IntegerField(choices=YES_NO_CHOICES, default=2)
    sp_depressn = models.IntegerField(choices=YES_NO_CHOICES, default=2)
    sp_diabetes = models.IntegerField(choices=YES_NO_CHOICES, default=2)
    sp_ischmcht = models.IntegerField(choices=YES_NO_CHOICES, default=2)
    sp_osteoprs = models.IntegerField(choices=YES_NO_CHOICES, default=2)
    sp_ra_oa = models.IntegerField(choices=YES_NO_CHOICES, default=2)
    sp_strketia = models.IntegerField(choices=YES_NO_CHOICES, default=2)
    medreimb_ip = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    benres_ip = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    pppymt_ip = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    medreimb_op = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    benres_op = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    pppymt_op = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    medreimb_car = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    benres_car = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    pppymt_car = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])

class CarrierClaimData(StudyClaimData):
    """
    Represents the Carrier claim data.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    desynpuf_id = models.CharField(max_length=16, validators=[RegexValidator(regex='^[0-9A-Fa-f]{16}$', message='Must be 16 Char of Hexadecimal', code='nomatch')])
    clm_id = models.CharField(max_length=14, validators=[RegexValidator(regex='^[0-9]{14}$', message='Must be 14 Char of digit', code='nomatch')])
    clm_from_dt = models.DateField()
    clm_thru_dt = models.DateField()
    icd9_dgns_cd_1 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{0,9}$', message='Must be less than 10 Chars of [A-Z0-9]', code='nomatch')])
    icd9_dgns_cd_2 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{0,9}$', message='Must be less than 10 Chars of [A-Z0-9]', code='nomatch')])
    icd9_dgns_cd_3 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{0,9}$', message='Must be less than 10 Chars of [A-Z0-9]', code='nomatch')])
    icd9_dgns_cd_4 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{0,9}$', message='Must be less than 10 Chars of [A-Z0-9]', code='nomatch')])
    icd9_dgns_cd_5 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{0,9}$', message='Must be less than 10 Chars of [A-Z0-9]', code='nomatch')])
    icd9_dgns_cd_6 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{0,9}$', message='Must be less than 10 Chars of [A-Z0-9]', code='nomatch')])
    icd9_dgns_cd_7 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{0,9}$', message='Must be less than 10 Chars of [A-Z0-9]', code='nomatch')])
    icd9_dgns_cd_8 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{0,9}$', message='Must be less than 10 Chars of [A-Z0-9]', code='nomatch')])
    prf_physn_npi_1 = models.CharField(max_length=10, blank=True, validators=[RegexValidator(regex='^[0-9]{10}$', message='Must be 10 Chars of [0-9]', code='nomatch')])
    prf_physn_npi_2 = models.CharField(max_length=10, blank=True, validators=[RegexValidator(regex='^[0-9]{10}$', message='Must be 10 Chars of [0-9]', code='nomatch')])
    prf_physn_npi_3 = models.CharField(max_length=10, blank=True, validators=[RegexValidator(regex='^[0-9]{10}$', message='Must be 10 Chars of [0-9]', code='nomatch')])
    prf_physn_npi_4 = models.CharField(max_length=10, blank=True, validators=[RegexValidator(regex='^[0-9]{10}$', message='Must be 10 Chars of [0-9]', code='nomatch')])
    prf_physn_npi_5 = models.CharField(max_length=10, blank=True, validators=[RegexValidator(regex='^[0-9]{10}$', message='Must be 10 Chars of [0-9]', code='nomatch')])
    prf_physn_npi_6 = models.CharField(max_length=10, blank=True, validators=[RegexValidator(regex='^[0-9]{10}$', message='Must be 10 Chars of [0-9]', code='nomatch')])
    prf_physn_npi_7 = models.CharField(max_length=10, blank=True, validators=[RegexValidator(regex='^[0-9]{10}$', message='Must be 10 Chars of [0-9]', code='nomatch')])
    prf_physn_npi_8 = models.CharField(max_length=10, blank=True, validators=[RegexValidator(regex='^[0-9]{10}$', message='Must be 10 Chars of [0-9]', code='nomatch')])
    prf_physn_npi_9 = models.CharField(max_length=10, blank=True, validators=[RegexValidator(regex='^[0-9]{10}$', message='Must be 10 Chars of [0-9]', code='nomatch')])
    prf_physn_npi_10 = models.CharField(max_length=10, blank=True, validators=[RegexValidator(regex='^[0-9]{10}$', message='Must be 10 Chars of [0-9]', code='nomatch')])
    prf_physn_npi_11 = models.CharField(max_length=10, blank=True, validators=[RegexValidator(regex='^[0-9]{10}$', message='Must be 10 Chars of [0-9]', code='nomatch')])
    prf_physn_npi_12 = models.CharField(max_length=10, blank=True, validators=[RegexValidator(regex='^[0-9]{10}$', message='Must be 10 Chars of [0-9]', code='nomatch')])
    prf_physn_npi_13 = models.CharField(max_length=10, blank=True, validators=[RegexValidator(regex='^[0-9]{10}$', message='Must be 10 Chars of [0-9]', code='nomatch')])
    tax_num_1 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{9}$', message='Must be 9 Chars of [0-9]', code='nomatch')])
    tax_num_2 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{9}$', message='Must be 9 Chars of [0-9]', code='nomatch')])
    tax_num_3 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{9}$', message='Must be 9 Chars of [0-9]', code='nomatch')])
    tax_num_4 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{9}$', message='Must be 9 Chars of [0-9]', code='nomatch')])
    tax_num_5 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{9}$', message='Must be 9 Chars of [0-9]', code='nomatch')])
    tax_num_6 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{9}$', message='Must be 9 Chars of [0-9]', code='nomatch')])
    tax_num_7 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{9}$', message='Must be 9 Chars of [0-9]', code='nomatch')])
    tax_num_8 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{9}$', message='Must be 9 Chars of [0-9]', code='nomatch')])
    tax_num_9 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{9}$', message='Must be 9 Chars of [0-9]', code='nomatch')])
    tax_num_10 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{9}$', message='Must be 9 Chars of [0-9]', code='nomatch')])
    tax_num_11 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{9}$', message='Must be 9 Chars of [0-9]', code='nomatch')])
    tax_num_12 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{9}$', message='Must be 9 Chars of [0-9]', code='nomatch')])
    tax_num_13 = models.CharField(max_length=9, blank=True, validators=[RegexValidator(regex='^[0-9]{9}$', message='Must be 9 Chars of [0-9]', code='nomatch')])
    hcpcs_cd_1 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    hcpcs_cd_2 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    hcpcs_cd_3 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    hcpcs_cd_4 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    hcpcs_cd_5 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    hcpcs_cd_6 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    hcpcs_cd_7 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    hcpcs_cd_8 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    hcpcs_cd_9 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    hcpcs_cd_10 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    hcpcs_cd_11 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    hcpcs_cd_12 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    hcpcs_cd_13 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    line_nch_pmt_amt_1 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_nch_pmt_amt_2 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_nch_pmt_amt_3 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_nch_pmt_amt_4 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_nch_pmt_amt_5 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_nch_pmt_amt_6 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_nch_pmt_amt_7 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_nch_pmt_amt_8 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_nch_pmt_amt_9 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_nch_pmt_amt_10 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_nch_pmt_amt_11 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_nch_pmt_amt_12 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_nch_pmt_amt_13 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_ptb_ddctbl_amt_1 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_ptb_ddctbl_amt_2 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_ptb_ddctbl_amt_3 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_ptb_ddctbl_amt_4 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_ptb_ddctbl_amt_5 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_ptb_ddctbl_amt_6 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_ptb_ddctbl_amt_7 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_ptb_ddctbl_amt_8 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_ptb_ddctbl_amt_9 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_ptb_ddctbl_amt_10 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_ptb_ddctbl_amt_11 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_ptb_ddctbl_amt_12 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_ptb_ddctbl_amt_13 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_prmry_pyr_pd_amt_1 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_prmry_pyr_pd_amt_2 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_prmry_pyr_pd_amt_3 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_prmry_pyr_pd_amt_4 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_prmry_pyr_pd_amt_5 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_prmry_pyr_pd_amt_6 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_prmry_pyr_pd_amt_7 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_prmry_pyr_pd_amt_8 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_prmry_pyr_pd_amt_9 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_prmry_pyr_pd_amt_10 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_prmry_pyr_pd_amt_11 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_prmry_pyr_pd_amt_12 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_bene_prmry_pyr_pd_amt_13 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_coinsrnc_amt_1 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_coinsrnc_amt_2 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_coinsrnc_amt_3 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_coinsrnc_amt_4 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_coinsrnc_amt_5 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_coinsrnc_amt_6 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_coinsrnc_amt_7 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_coinsrnc_amt_8 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_coinsrnc_amt_9 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_coinsrnc_amt_10 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_coinsrnc_amt_11 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_coinsrnc_amt_12 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_coinsrnc_amt_13 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_alowd_chrg_amt_1 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_alowd_chrg_amt_2 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_alowd_chrg_amt_3 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_alowd_chrg_amt_4 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_alowd_chrg_amt_5 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_alowd_chrg_amt_6 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_alowd_chrg_amt_7 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_alowd_chrg_amt_8 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_alowd_chrg_amt_9 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_alowd_chrg_amt_10 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_alowd_chrg_amt_11 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_alowd_chrg_amt_12 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_alowd_chrg_amt_13 = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    line_prcsg_ind_cd_1 = models.CharField(max_length=2, blank=True, validators=[RegexValidator(regex='^.{1,2}$', message='Must be 1 or 2 Chars of any symbol', code='nomatch')])
    line_prcsg_ind_cd_2 = models.CharField(max_length=2, blank=True, validators=[RegexValidator(regex='^.{1,2}$', message='Must be 1 or 2 Chars of any symbol', code='nomatch')])
    line_prcsg_ind_cd_3 = models.CharField(max_length=2, blank=True, validators=[RegexValidator(regex='^.{1,2}$', message='Must be 1 or 2 Chars of any symbol', code='nomatch')])
    line_prcsg_ind_cd_4 = models.CharField(max_length=2, blank=True, validators=[RegexValidator(regex='^.{1,2}$', message='Must be 1 or 2 Chars of any symbol', code='nomatch')])
    line_prcsg_ind_cd_5 = models.CharField(max_length=2, blank=True, validators=[RegexValidator(regex='^.{1,2}$', message='Must be 1 or 2 Chars of any symbol', code='nomatch')])
    line_prcsg_ind_cd_6 = models.CharField(max_length=2, blank=True, validators=[RegexValidator(regex='^.{1,2}$', message='Must be 1 or 2 Chars of any symbol', code='nomatch')])
    line_prcsg_ind_cd_7 = models.CharField(max_length=2, blank=True, validators=[RegexValidator(regex='^.{1,2}$', message='Must be 1 or 2 Chars of any symbol', code='nomatch')])
    line_prcsg_ind_cd_8 = models.CharField(max_length=2, blank=True, validators=[RegexValidator(regex='^.{1,2}$', message='Must be 1 or 2 Chars of any symbol', code='nomatch')])
    line_prcsg_ind_cd_9 = models.CharField(max_length=2, blank=True, validators=[RegexValidator(regex='^.{1,2}$', message='Must be 1 or 2 Chars of any symbol', code='nomatch')])
    line_prcsg_ind_cd_10 = models.CharField(max_length=2, blank=True, validators=[RegexValidator(regex='^.{1,2}$', message='Must be 1 or 2 Chars of any symbol', code='nomatch')])
    line_prcsg_ind_cd_11 = models.CharField(max_length=2, blank=True, validators=[RegexValidator(regex='^.{1,2}$', message='Must be 1 or 2 Chars of any symbol', code='nomatch')])
    line_prcsg_ind_cd_12 = models.CharField(max_length=2, blank=True, validators=[RegexValidator(regex='^.{1,2}$', message='Must be 1 or 2 Chars of any symbol', code='nomatch')])
    line_prcsg_ind_cd_13 = models.CharField(max_length=2, blank=True, validators=[RegexValidator(regex='^.{1,2}$', message='Must be 1 or 2 Chars of any symbol', code='nomatch')])
    line_icd9_dgns_cd_1 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    line_icd9_dgns_cd_2 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    line_icd9_dgns_cd_3 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    line_icd9_dgns_cd_4 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    line_icd9_dgns_cd_5 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    line_icd9_dgns_cd_6 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    line_icd9_dgns_cd_7 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    line_icd9_dgns_cd_8 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    line_icd9_dgns_cd_9 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    line_icd9_dgns_cd_10 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    line_icd9_dgns_cd_11 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    line_icd9_dgns_cd_12 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])
    line_icd9_dgns_cd_13 = models.CharField(max_length=5, blank=True, validators=[RegexValidator(regex='^[0-9A-Z]{5}$', message='Must be 5 Chars of [A-Z0-9]', code='nomatch')])

class PrescriptionClaimData(StudyClaimData):
    """
    Represents the Prescription claim data.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    desynpuf_id = models.CharField(max_length=16, validators=[RegexValidator(regex='^[0-9A-Fa-f]{16}$', message='Must be 16 Char of Hexadecimal', code='nomatch')])
    pde_id = models.CharField(max_length=15, validators=[RegexValidator(regex='^[0-9A-Z]{15}$', message='Must be 15 Chars of [0-9]', code='nomatch')])
    srvc_dt = models.DateField()
    prod_srvc_id = models.CharField(max_length=11, validators=[RegexValidator(regex='^[0-9A-Z]{11}$', message='Must be 11 Chars of [0-9]', code='nomatch')])
    qty_dspnsd_num = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    days_suply_num = models.IntegerField()
    ptnt_pay_amt = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])
    tot_rx_cst_amt = models.DecimalField(max_digits=MAX_DIGITS, decimal_places=2, default=0.00, validators=[MinValueValidator(0)])

class DARCEmail(models.Model):
    """
    The Django data model that represents a DARC email address.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    #Represents the email address.
    email = models.EmailField()

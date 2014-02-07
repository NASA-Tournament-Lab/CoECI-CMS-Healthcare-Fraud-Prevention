# -*- coding: utf-8 -*-
"""
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.

This is module that represents all the forms.

@author:  TCSASSEMBLER
@version: 1.0
"""

from django import forms
from django.forms.models import inlineformset_factory
from hfppnetwork.sms.models import Study, StudyChart, StudyDataRequest
from hfppnetwork.sms.models import PartnerTag
from hfppnetwork.sms.models import Partner
from hfppnetwork.sms.models import PartnerContact
from hfppnetwork.sms.models import DARCEmail

class StudySearchForm(forms.Form):
    """
    This is the Form used to search studies.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    #Represents the ID.
    id = forms.CharField(required=False)
    #Represents the status.
    status = forms.IntegerField(required=False)
    #Represents the description.
    description = forms.CharField(required=False)
    #Represents the "created on" range from.
    created_on_from = forms.DateField(required=False)
    #Represents the "created on" range to.
    created_on_to = forms.DateField(required=False)
    #Represents the "last modified on" range from.
    last_modified_on_from = forms.DateField(required=False)
    #Represents the "last modified on" range to.
    last_modified_on_to = forms.DateField(required=False)
    #Represents the "executed on" range from.
    executed_on_from = forms.DateField(required=False)
    #Represents the "executed on" range to.
    executed_on_to = forms.DateField(required=False)
    #Represents the "completed on" range from.
    completed_on_from = forms.DateField(required=False)
    #Represents the "completed on" range from.
    completed_on_to = forms.DateField(required=False)

class StudyForm(forms.ModelForm):
    """
    This is the ModelForm for study.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    class Meta:
        model = Study
        exclude = ["owner", "created_on", "last_modified_on", "executed_on", "completed_on"]
        
class CreateStudyForm(forms.ModelForm):
    """
    This is the ModelForm for study.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    class Meta:
        model = Study
        exclude = ["owner", "created_on", "last_modified_on", "executed_on", "completed_on"]

class StudyChartForm(forms.ModelForm):
    """
    This is the ModelForm for study chart.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    class Meta:
        model = StudyChart

#Represents the inline form for StudyDataRequest.
StudyDataRequestFormSet = inlineformset_factory(Study, StudyDataRequest, extra=0)

class ClaimDataSearchForm(forms.Form):
    """
    This is the Form used to search claim data.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    #Represents the query expression for Beneficiary claim data.
    beneficiary_query = forms.CharField(required=False)
    #Represents the query expression for Carriers claim data.
    carrier_query = forms.CharField(required=False)
    #Represents the query expression for Inpatient claim data.
    inpatient_query = forms.CharField(required=False)
    #Represents the query expression for Outpatient claim data.
    outpatient_query = forms.CharField(required=False)
    #Represents the query expression for Prescription claim data.
    prescription_query = forms.CharField(required=False)
    #Represents the selected fields for Beneficiary claim data.
    beneficiary_fields = forms.CharField(required=False)
    #Represents the selected fields for Carrier claim data.
    carrier_fields = forms.CharField(required=False)
    #Represents the selected fields for Inpatient claim data.
    inpatient_fields = forms.CharField(required=False)
    #Represents the selected fields for Outpatient claim data.
    outpatient_fields = forms.CharField(required=False)
    #Represents the selected fields for Prescription claim data.
    prescription_fields = forms.CharField(required=False)

class PartnerSearchForm(forms.Form):
    """
    This is the Form used to search partners.
    @author:  TCSASSEMBLER
    @version: 1.0
    """
    #Represents the company name.
    company_name = forms.CharField(required=False)
    
class PartnerTagForm(forms.ModelForm):
    '''
    This is the ModelForm for partner tag.

    Thread Safety:
    The implementation is thread safe.

    @author: caoweiquan322
    @version: 1.0
    '''
    class Meta:
        model = PartnerTag
        exclude = ["owner"]

class PartnerTagSearchForm(forms.Form):
    '''
    This is the Form used to search partner tags.

    Thread Safety:
    The implementation is thread safe.

    @author: caoweiquan322
    @version: 1.0
    '''
    name = forms.CharField(required=False)

class PartnerForm(forms.ModelForm):
    '''
    This is the ModelForm for partner.

    Thread Safety:
    The implementation is thread safe.

    @author: caoweiquan322
    @version: 1.0
    '''
    class Meta:
        model = Partner
        exclude = ["count_of_data_requests_received", "count_of_data_requests_sent",
                   "count_of_data_requests_declined", "count_of_data_requests_pending",
                   "count_of_data_requests_responded", "owner"]

'''
Represents the inline form for PartnerContact.
'''
PartnerContactFormSet = inlineformset_factory(Partner, PartnerContact, extra=1)


class DARCEmailForm(forms.ModelForm):
    '''
    This is the ModelForm for DARC email.

    Thread Safety:
    The implementation is thread safe.

    @author: caoweiquan322
    @version: 1.0
    '''
    class Meta:
        model = DARCEmail

class DARCEmailSearchForm(forms.Form):
    '''
    This is the Form used to search DARC emails.

    Thread Safety:
    The implementation is thread safe.

    @author: caoweiquan322
    @version: 1.0
    '''
    # email set to CharField so that any invalid input can be handled.
    email = forms.CharField(required=False)

# -*- coding: utf-8 -*-
"""
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.

This is module that represents all the externals.
Currently it is mock implementation.

@author:  TCSASSEMBLER
@version: 1.0
"""
from hfppnetwork.sms.models import BeneficiaryClaimData, CarrierClaimData,\
    InpatientClaimData, OutpatientClaimData, PrescriptionClaimData
import json
import re
def transform_query(beneficiary_query, carrier_query, inpatient_query, outpatient_query, prescription_query):
    """
    This method is used to transform query.
    
    Parameters:
    - query : the query
    
    Assertions:
    - query can't be null
    
    Returns:
    the transformed query
    """
    if beneficiary_query == None:
        raise ValueError("query can be empty but cannot be None");
    if carrier_query == None:
        raise ValueError("query can be empty but cannot be None");
    if inpatient_query == None:
        raise ValueError("query can be empty but cannot be None");
    if outpatient_query == None:
        raise ValueError("query can be empty but cannot be None");
    if prescription_query == None:
        raise ValueError("query can be empty but cannot be None");
    # currently just use fixed value
    beneficiary_query = beneficiary_query.replace('$','')
    carrier_query = carrier_query.replace('$','')
    inpatient_query = inpatient_query.replace('$','')
    outpatient_query = outpatient_query.replace('$','')
    prescription_query = prescription_query.replace('$','')
    file_types = []
    logical_expressions = []
    if len(beneficiary_query.strip()) > 0:
        file_types.append("beneficiary")
        logical_expressions.append(transform_date_str(beneficiary_query.strip()))
    if len(carrier_query.strip()) > 0:
        file_types.append("carrier")
        logical_expressions.append(transform_date_str(carrier_query.strip()))
    if len(inpatient_query.strip()) > 0:
        file_types.append("inpatient")
        logical_expressions.append(transform_date_str(inpatient_query.strip()))
    if len(outpatient_query.strip()) > 0:
        file_types.append("outpatient")
        logical_expressions.append(transform_date_str(outpatient_query.strip()))
    if len(prescription_query.strip()) > 0:
        file_types.append("prescription")
        logical_expressions.append(transform_date_str(prescription_query.strip()))
    json_data = {"file_types":file_types,"logical_expressions":logical_expressions}
    return json.dumps(json_data)
    #return query #mock impl

def transform_date_str(query):
    """
    This method is used to transform MM/dd/yyyy to yyyyMMdd.
    
    Parameters:
    - query : the query
    
    Assertions:
    - query can't be null
    
    Returns:
    the transformed query
    """
    pattern = re.compile(r'(\d{2})/(\d{2})/(\d{4})')
    return pattern.sub(r'\3\1\2', query)

def filter_claim_data(form):
    """
    This method is used to query claim data.
    
    Parameters:
    - form : the ClaimDataSearchForm
    
    Assertions:
    - form can't be null
    
    Returns:
    the query result as a dict.
    
    The key will be
    - 'Beneficiary'
    - 'Carrier'
    - 'Inpatient'
    - 'Outpatient'
    - 'Prescription'
    
    The value will be QuerySet for the type of claim data.
    """
    if form == None:
        raise ValueError('form cannot be None');
    result = {}
    result['Beneficiary'] = BeneficiaryClaimData.objects.all()
    result['Carrier'] = CarrierClaimData.objects.all()
    result['Inpatient'] = InpatientClaimData.objects.all()
    result['Outpatient'] = OutpatientClaimData.objects.all()
    result['Prescription'] = PrescriptionClaimData.objects.all()
    return result #mock impl

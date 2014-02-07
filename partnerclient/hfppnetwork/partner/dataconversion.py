# -*- coding: utf-8 -*-
"""
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.

This is the module that wraps the Data Conversion functionality.
It defines a function to convert data file.
Note that only the function declaration is designed, the actual implementation will be determined later.
This module resides in Python source file dataconversion.py
Thread Safety:
The implementation should be thread safe.
@author:  TCSASSEMBLER
@version: 1.0
"""
import os
from logginghelper import method_enter
from logginghelper import method_exit
from conversion.converter import csv2xml
from validationhelper import check_string
from errors import DataConversionError

def convert_data(file_type, input_file_name, output_file_name):
    """
      This function is used to convert data file.
      @param file_type: the file type - it is supposed to be a str, not None/empty. Required.
      @param input_file_name: the input file name (including full path),
      this function will assume the file exists - it is supposed to be a str, not None/empty. Required.
      @param output_file_name: the output file name (including full path), this function will assume the file exists,
      hence it will not create the file - it is supposed to be a str, not None/empty. Required.
      @throws TypeError throws if any argument isn't of right type
      @throws ValueError throws if any argument isn't valid (refer to the argument documentation)
      @throws DataConversionError throws if any other error occurred during the operation
    """
    signature = 'hfppnetwork.partner.httpservices.dataconversion.convert_data'
    method_enter(signature,{
        'file_type':file_type,
        'input_file_name':input_file_name,
        'output_file_name':output_file_name
       })
    
    # Acceptable file types
    types_mapping = {
        'beneficiary': 'BeneficiarySummary',
        'carrier': 'CarrierClaim',
        'inpatient': 'InpatientClaim',
        'outpatient': 'OutpatientClaim',
        'prescription': 'PrescriptionEvent'}
    check_string('file_type', file_type)
    if not file_type in types_mapping:
    	raise ValueError('File type "' + file_type + '" is not acceptable. Use '
    										+ str(types_mapping))
    check_string('input_file_name', input_file_name)
    check_string('output_file_name', output_file_name)
    if os.path.exists(input_file_name) is False:
    	raise ValueError('input_file_name should be valid file path')
    if os.path.exists(output_file_name) is False:
    	raise ValueError('output_file_name should be valid file path')
    try:
    	csv2xml(input_file_name, output_file_name, types_mapping[file_type])
    except:
    	raise DataConversionError('Data conversion internal error.')
    method_exit(signature)



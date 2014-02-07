# -*- coding: utf-8 -*-
"""
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.

This is the module that define custom errors.
Thread Safety:
It is not thread safe because base class Exception is not thread safe.
@author:  TCSASSEMBLER
@version: 1.0
"""

"""
This is the base exception in Partner Client module.
"""
class PartnerClientError(Exception):
    pass

"""
This exception will be raised by convert_data function
in data conversion module to indicate error during data conversion.
"""
class DataConversionError(PartnerClientError):
    pass

"""
This exception will be raised by query_data function
in data appliance module to indicate error during data query.
"""
class DataQueryError(PartnerClientError):
    pass
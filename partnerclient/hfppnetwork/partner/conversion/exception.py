##
# Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
##

"""
Custom exceptions.
"""
__author__ = 'Easyhard'
__version__ = '1.0'

class DataConverterException(Exception):
    """It is thrown if there is any error of data converter."""
    def __init__(self, msg):
        self.msg = msg
    def __str__(self):
        return repr(self.msg)

class DataReaderException(Exception):
    """It is thrown if there is any error of data reader."""
    def __init__(self, msg):
        self.msg = msg
    def __str__(self):
        return repr(self.msg)

class DataWriterException(Exception):
    """It is thrown if there is any error of data writer."""
    def __init__(self, msg):
        self.msg = msg
    def __str__(self):
        return repr(self.msg)

class ConfigurationException(Exception):
    """It is thrown if there is any configuration error."""
    def __init__(self, msg):
        self.msg = msg
    def __str__(self):
        return repr(self.msg)

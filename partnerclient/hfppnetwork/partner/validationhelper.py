# -*- coding: utf-8 -*-
"""
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.

This is the module that defines validation helper methods
This module resides in Python source file validationhelper.py
Thread Safety:
It is thread safe because no module-level variable is used.
@author:  TCSASSEMBLER
@version: 1.0
"""
from datetime import  datetime
import os

def check_string(name, value):
    """
      This function is used to check string.
      @param name the param name
      @param value the param value
      @throws TypeError throws if value is not string
      @throws ValueError throws if value is none or  empty
    """
    if type(value) is not str:
        raise TypeError("{0} is not string type".format(name))
    if len(value.strip()) == 0:
        raise ValueError("{0} should be non-None/empty str".format(name))

def check_file(name, value):
    """
      This function is used to check if given value is a valid file path.
      @param name the param name
      @param value the param value
      @throws TypeError throws if value is not string
      @throws ValueError throws if value is none or  empty
      @throws ValueError throws if value is not a valid file path
    """
    check_string(name, value)
    if os.path.exists(value) is False:
        raise ValueError(name + ' should be valid file path')

def check_datetime(name, value):
    """
      This function is used to check date time.
      @param name the param name
      @param value the param value
      @throws TypeError throws if value is not datetime
    """
    if type(value) is not datetime:
        raise TypeError("{0} is not datetime type".format(name))

def check_bool(name, value):
    """
      This function is used to check bool.
      @param name the param name
      @param value the param value
      @throws TypeError throws if value is not bool
    """
    if type(value) is not bool:
        raise TypeError("{0} is not bool type".format(name))

def check_port(name, value):
    """
      This function is used to check port.
      @param name the param name
      @param value the param value
      @throws TypeError throws if value is not bool
      @throws ValueError throws if value not in range [0, 65535]
    """
    if type(value) is not int:
        raise TypeError("{0} is not int type".format(name))
    if value <0 or value >65535:
         raise TypeError("{0} should be in range [0, 65535]".format(name))

def check_str_list(name, value):
    """
      This function is used to check string list.
      @param name the param name
      @param value the param value
      @throws TypeError throws if value is not string list
      @throws ValueError throws if elements are not non-empty string
    """
    if type(value) is not list:
        raise TypeError("{0} is not list type".format(name))
    for ele in value:
    	check_string("{0} elements".format(name), ele)

def check_dict(name, value):
    """
      This function is used to check dictionary.
      @param name the param name
      @param value the param value
      @throws TypeError throws if value is not dictionary
    """
    if type(value) is not dict:
        raise TypeError("{0} is not dictionary type".format(name))


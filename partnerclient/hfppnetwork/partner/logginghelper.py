# -*- coding: utf-8 -*-
"""
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.

This is the module that defines logging helper methods
This module resides in Python source file logginghelper.py
Thread Safety:
It is thread safe because no module-level variable is used.
@author:  TCSASSEMBLER
@version: 1.0
"""
import logging

def method_enter(signature, params=None):
    """
      This function is used to logging when enter method .
      @param signature the method signature
      @param params the method params
    """
    logging.debug('Entering method %s', signature)
    logging.debug('Input parameters:[%s]',('' if params is None else params))

def method_exit(signature, ret=None):
    """
      This function is used to logging when exit method.
      @param signature the method signature
      @param ret the method return value
    """
    logging.debug('Exiting method %s', signature)
    logging.debug('Output parameters:%s',ret)

def method_error(signature, details):
    """
      This function is used to logging when error happen.
      @param signature the method signature
      @param details the error details
    """
    logging.error('Error in  method %s', signature)
    logging.error('Details:%s',details)
    #log error stack
    logging.exception('')
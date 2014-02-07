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

def method_enter(logger, signature, params=None):
    """
      This function is used to logging when enter method .
      @param signature the method signature
      @param params the method params
    """
    logger.debug('Entering method %s', signature)
    logger.debug('Input parameters:[%s]',('' if params is None else params))

def method_exit(logger, signature, ret=None):
    """
      This function is used to logging when exit method.
      @param signature the method signature
      @param ret the method return value
    """
    logger.debug('Exiting method %s', signature)
    logger.debug('Output parameters:%s',ret)

def method_error(logger, signature, details):
    """
      This function is used to logging when error happen.
      @param signature the method signature
      @param details the error details
    """
    logger.error('Error in  method %s', signature)
    logger.error('Details:%s',details)
    #log error stack
    logger.exception('')
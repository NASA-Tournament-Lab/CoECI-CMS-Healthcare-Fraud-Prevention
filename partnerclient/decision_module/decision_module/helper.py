'''
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
'''
'''
This is the module that provides useful helper functions.

Thread Safety:
The implementation is thread safe.

@author: TCSASSEMLBER
@version: 1.0
'''

def log_entrance(logger, signature, parasMap):
    '''
    Logs for entrance into public methods at DEBUG level.
    
    @param logger: the logger object
    @param signature: the method signature
    @param parasMap: the passed parameters
    '''
    logger.debug('[Entering method ' + signature + ']')
    if parasMap != None and len(parasMap.items()) > 0:
        paraStr = '[Input parameters['
        for (k,v) in parasMap.items():
            paraStr += (str(k) + ':' + str(v) + ', ')
        paraStr += ']]'
        logger.debug(paraStr)

def log_exit(logger, signature, parasList):
    '''
    Logs for exit from public methods at DEBUG level.
    
    @param logger: the logger object
    @param signature: the method signature
    @param parasList: the objects to return
    '''
    logger.debug('[Exiting method ' + signature + ']')
    if parasList != None and len(parasList) > 0:
        logger.debug('[Output parameter ' + str(parasList) + ']')

def log_exception(logger, signature, e):
    '''
    Logging exception at ERROR level.
    
    @param logger: the logger object
    @param signature: the method signature
    @param e: the error
    '''
    # This will log the traceback.
    logger.error('[Error in method ' + signature + ': Details ' + str(e) + ']')
    return e

'''
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
'''
'''
Only the logger needs to be initialized.

@author: TCSASSEMLBER
@version: 1.0
'''
import logging

packageLogger = logging.getLogger('decision_module')
packageLogger.setLevel(logging.DEBUG)
fileHandler = logging.FileHandler('log.txt')
fileHandler.setLevel(logging.DEBUG)
consoleHandler = logging.StreamHandler()
consoleHandler.setLevel(logging.DEBUG)
packageLogger.addHandler(fileHandler)
packageLogger.addHandler(consoleHandler)
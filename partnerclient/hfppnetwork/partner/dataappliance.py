# -*- coding: utf-8 -*-
"""
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.

This is the module that wraps the Data Appliance functionality.
It defines a function to query data.
Note that only the function declaration is designed, the actual implementation will be determined later.
This module resides in Python source file dataappliance.py
Thread Safety:
The implementation should be thread safe.
@author:  TCSASSEMBLER
@version: 1.0
"""
from logginghelper import method_enter
from logginghelper import method_exit
from errors import DataQueryError
import logging
from query.generator import SQLGenerator
from query.parser import Parser, ParserError
from appliance.claim_file_processor import ClaimFileProcessor
from appliance.claim_file import CSVClaimFile
from appliance.mysql_claim_persistence import MySQLClaimPersistence
from appliance.redis_claim_persistence import RedisClaimPersistence
from appliance import config
from validationhelper import check_str_list, check_dict
from validationhelper import check_string, check_datetime
import isodate

def query_data(file_types,query_logical_expressions,
                                output_file_names, cache_timestamp=None):
       """
          This function is used to query data.
          @param file_types the file types,it is supposed to be a str array,
          each item in the array should be a non-None/empty str. Required.
          @param query_logical_expressions the query logical expressions,
          it is supposed to be a str array,each item in the array should be
          a non-None/empty str. Required.
          @param output_file_names the output file names (including full paths),
          this function will assume the files exist,
          it is supposed to be a non-None/empty dictionary,
          key is file type(non-None/empty str,must be one of
          the strings in file_types),value is the output file
          names (non-None/empty str, including full path). Required.
          @param cache_timestamp the cache timestamp,
          it is supposed to be None or a non-None datetime. Optional, default to None.
          @return True if use cache, False otherwise
          @throws TypeError throws if any argument isn't of right type
          @throws ValueError throws if any argument isn't valid (refer to the argument documentation)
          @throws DataQueryError: if any other error occurred during the operation
       """
       signature = 'hfppnetwork.partner.httpservices.dataappliance.query_data'
       method_enter(signature,{
        "file_types":file_types,
        "query_logical_expressions":query_logical_expressions,
        "output_file_names":output_file_names,
        "cache_timestamp":cache_timestamp
       })

       # Parameters checking
       acceptableTypes = ['beneficiary', 'carrier', 'inpatient', 'outpatient', 'prescription']
       check_str_list('file_types', file_types)
       for one_type in file_types:
        if not one_type in acceptableTypes:
            raise ValueError('File type ' + one_type + ' not acceptable.')
       check_str_list('query_logical_expressions', query_logical_expressions)
       if not len(query_logical_expressions) == len(file_types):
        raise ValueError('query_logical_expressions and file_types length not match.')
       check_dict('output_file_names', output_file_names)
       if not len(output_file_names) == len(file_types):
        raise ValueError('output_file_names and file_types length not match.')
       if cache_timestamp is not None:
        check_datetime('cache_timestamp', cache_timestamp)

       # Check if cache needs to be updated
       try:
        use_cache = check_use_cache(file_types, cache_timestamp)
       except:
        raise DataQueryError('Error occurs during checking cache data.')
       # Loading data from database if not use cache
       if not use_cache:
        try:
            type_index = 0
            for one_type in file_types:
                # Parse and generate query string
                parser = Parser()
                generator = SQLGenerator()
                parse_tree = parser.parse(query_logical_expressions[type_index].strip())
                sql = generator.visit(parse_tree)
                logging.debug("sql %s ",sql)
                # Query data
                 # Instantiate relevant classes
                if config.dbconfig["type"]=='redis':
                    claimPersistence = RedisClaimPersistence()
                elif config.dbconfig["type"] == "mysql":
                    claimPersistence = MySQLClaimPersistence()
                else:
                    raise ValueError("Invalid db type: " + config.dbconfig["type"])
                claimPersistence.connectionConfig = config.dbconfig
                claimFile = CSVClaimFile()
                processor = ClaimFileProcessor()
                processor.claimPersistence = claimPersistence
                processor.claimFile = claimFile
                processor.queryData(one_type,sql if (sql and len(sql) > 0) else '1=1',
                                                        0, 0,
                                                        output_file_names[one_type])
                # Update loop index
                type_index = type_index + 1
        except ParserError:
            raise DataQueryError('Error occurs during parsing query string.')
        except:
            raise DataQueryError('Error occurs during querying data.')

       method_exit(signature,use_cache)
       return use_cache

def check_use_cache(file_types, cache_timestamp):
    """
    Check if cache at HUB server is still usable.

    @param file_types: the file types,it is supposed to be a str array,
    each item in the array should be a non-None/empty str. Required.
    @param cache_timestamp: the cache timestamp,
    it is supposed to be None or a non-None datetime. Optional, default to None.
    """
    if cache_timestamp is None:
        return False
    if config.dbconfig["type"]=='redis':
        claimPersistence = RedisClaimPersistence()
    elif config.dbconfig["type"] == "mysql":
        claimPersistence = MySQLClaimPersistence()
    else:
        raise ValueError("Invalid db type: " + config.dbconfig["type"])
    claimPersistence.connectionConfig = config.dbconfig
    claimFile = CSVClaimFile()
    processor = ClaimFileProcessor()
    processor.claimPersistence = claimPersistence
    processor.claimFile = claimFile
    for one_type in file_types:
        if processor.queryDataSize(one_type, "LAST_MODIFY > " + cache_timestamp.strftime('%Y%m%d%H%M%S') + "") > 0:
            return False
    return True

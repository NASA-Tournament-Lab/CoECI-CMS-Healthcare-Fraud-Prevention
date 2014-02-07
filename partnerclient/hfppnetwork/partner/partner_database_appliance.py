"""
Copyright (C) 2010 - 2013 TopCoder Inc., All Rights Reserved.

This file contains the main function.
Main function analyze the command line options and invoke processor's method to handle the request.

@version 1.0 (Healthcare Fraud Prevention - Partner Database Appliance - Assembly)
@author: zeadom, TCSASSEMBLER
@since 2013-12-10
"""

import os, sys

from appliance.partner_database_appliance_exception import IllegalArgumentException
from appliance.claim_file_processor import ClaimFileProcessor
from appliance.claim_file import CSVClaimFile
from appliance.mysql_claim_persistence import MySQLClaimPersistence
from appliance.redis_claim_persistence import RedisClaimPersistence

from appliance import config

def main():
    """
    The main execution section that will accept user parameters and either query for data and put it into a file,
    or query for data row count, or write a file into the DB. The writing can accept a directory instead, but all
    files must be of the same claim type.
    """
    # Obtain parameters
    import argparse
    parser = argparse.ArgumentParser(description='Healthcare Fraud Prevention')
    parser.add_argument("-o", dest="operation", help="operation type")
    parser.add_argument("-t", dest="claimType", help="claim type")
    parser.add_argument("-f", dest="filename", help="file name")
    parser.add_argument("-q", dest="query", help="query string")
    parser.add_argument("-n", dest="pageNumber", help="page number")
    parser.add_argument("-s", dest="pageSize", help="page size")
    args = parser.parse_args()

    # Instantiate relevant classes
    if config.dbconfig["type"] == "redis":
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

    if not args.operation:
        raise ValueError("Operation type is required")
    elif args.operation == "write":
        processor.writeFile(args.claimType, args.filename)
    elif args.operation == "query":
        processor.queryData(args.claimType, args.query, args.pageNumber, args.pageSize, args.filename)
    elif args.operation == "querySize":
        rowCount = processor.queryDataSize(args.claimType, args.query)
        print(rowCount)
    else:
        raise IllegalArgumentException("Unsupported operation %s" % args.operation)

if __name__ == "__main__":
    main()

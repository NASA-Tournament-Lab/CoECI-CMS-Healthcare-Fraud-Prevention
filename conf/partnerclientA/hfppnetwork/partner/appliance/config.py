"""
Copyright (C) 2010 - 2013 TopCoder Inc., All Rights Reserved.

This file contains configuration necessary for this application.

@version 1.0 (Healthcare Fraud Prevention - Partner Database Appliance - Assembly)
@author: zeadom, TCSASSEMBLER
@since 2013-12-10
"""

# Test MySQL config
mysql_config = {
    "type" : "mysql",
    "user" : "root",
    "passwd" : "123456",
    "host" : "127.0.0.1",
    "port" : 3306,
    "db" : "erd",
    "connect_timeout" : 1,
}

# Test Redis config
redis_config = {
    "type" : "redis",
    "host" : "localhost",
    "port" : 6379,
    "db" : 0,
}

# Test db config
dbconfig = mysql_config # Or change to redis_config

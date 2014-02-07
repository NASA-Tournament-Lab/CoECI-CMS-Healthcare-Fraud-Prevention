"""
Copyright (C) 2010 - 2013 TopCoder Inc., All Rights Reserved.

This file contains MySQL claim persistence.

@version 1.0 (Healthcare Fraud Prevention - Partner Database Appliance - Assembly)
@author: zeadom, TCSASSEMBLER
@since 2013-12-10
"""

import os
import sys

import cymysql as dbdriver
from cymysql import DatabaseError

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from partner_database_appliance_exception import PersistenceException
from claim_type import ClaimTypeFactory
from claim_persistence import ClaimPersistence
from datetime import datetime

class MySQLClaimPersistence(ClaimPersistence):
    """
    Claim persistence class

    This class provides the access to the persistence where the files are stored then queried.
    This class is meant to be used as a session, so it must not be used by multiple threads.
    A session must begin and be closed by either a commit or a rollback.
    Not meant to be called outside the module, so there is no parameter check in the methods.

    Attributes:
        connectionConfig: Represents the connection config
        connections: Represents the connection object. This will be set in begin, and closed in commit/rollback,
                     and use in the business methods
        cursor: Represents the cursor object. This will be set in begin, and closed in commit/rollback,
                and use in the business methods
    """

    def __init__(self):
        """ Init MySQLClaimPersistence """
        self.connectionConfig = {}
        self.connection = None
        self.cursor = None

    def begin(self):
        """
        Begins the persistence connection session

        Raises:
            PersistenceException: There is DB error
        """
        try:
            self.connection = dbdriver.connect(host=self.connectionConfig["host"],
                                               user=self.connectionConfig["user"],
                                               passwd=self.connectionConfig["passwd"],
                                               db=self.connectionConfig["db"],
                                               port=self.connectionConfig["port"],
                                               connect_timeout=self.connectionConfig["connect_timeout"])
            self.cursor = self.connection.cursor()
        except DatabaseError as e:
            raise PersistenceException(*e.args)

    def commit(self):
        """
        Commits the session

        Raises:
            PersistenceException: There is DB error
        """
        try:
            self.connection.commit()
        except DatabaseError as e:
            raise PersistenceException(*e.args)

    def rollback(self):
        """
        Rolls back the session

        Raises:
            PersistenceException: There is DB error
        """
        try:
            self.connection.rollback()
        except DatabaseError as e:
            raise PersistenceException(*e.args)

    def close(self):
        """
        Closes the session

        Raises:
            PersistenceException: There is DB error
        """
        try:
            if self.cursor:
                self.cursor.close()
                self.cursor = None
            if self.connection:
                self.connection.close()
                self.connection = None
        except DatabaseError as e:
            raise PersistenceException(*e.args)

    def createClaim(self, claimType, fields):
        """
        Creates claim

        Creates the claim of the given type with the given data in the persistence

        Args:
            claimType: the claimType
            fields: the data of the claim

        Raises:
            PersistenceException: There is DB error
        """
        try:
            claimType = ClaimTypeFactory.load(claimType)
            columns = []
            data = []
            for i in range(len(fields)):
                if fields[i]:
                    data.append(fields[i])
                    columns.append(claimType.columns[i])
            # Appending LAST_MODIFY column, fields always won't provide 'LAST_MODIFY' field
            columns.append('LAST_MODIFY')
            data.append(str(datetime.now()))
            sql = "insert into %s(`%s`) values('%s')" % (claimType.tablename, "`, `".join(columns), "', '".join(data))
            self.cursor.execute(sql)
        except DatabaseError as e:
            raise PersistenceException(*e.args)

    def queryClaims(self, claimType, query, pageNumber, pageSize):
        """
        Queries claims

        Queries the data according the the given filtering options from the given claim type, and returns the list of
        records (as lists) of data for the given claim type

        Args:
            claimType: the claimType
            query: the query string
            pageNumber: the page number
            pageSize: the page size

        Returns:
            The list of records of data for the given claim type

        Raises:
            PersistenceException: There is DB error
        """
        try:
            claimType = ClaimTypeFactory.load(claimType)
            # Here we remove the last 'LAST_MODIFY' column by [0:-1]
            columns = "`%s`" % "`, `".join(claimType.columns[0:-1])
            tablename = claimType.tablename
            where = query
            limit = ""
            if pageNumber:
                offset = (pageNumber-1) * pageSize
                limit = "limit %s,%s" % (offset, pageSize)
            sql = "select %s from %s where %s %s" % (columns, tablename, where, limit)
            self.cursor.execute(sql)
            claims = self.cursor.fetchall()
            return claims
        except DatabaseError as e:
            raise PersistenceException(*e.args)

    def queryClaimSize(self, claimType, query):
        """
        Queries claim's size

        Queries the data according the the given query and returns the number of records retrieved from the query.

        Args:
            claimType: the claimType
            query: the query string

        Returns:
            The number of records retrieved from the query

        Raises:
            PersistenceException: There is DB error
        """
        try:
            claimType = ClaimTypeFactory.load(claimType)
            tablename = claimType.tablename
            where = query
            sql = "select count(*) from %s where %s" % (tablename, where)
            self.cursor.execute(sql)
            cnt = self.cursor.fetchone()[0]
            return cnt
        except DatabaseError as e:
            raise PersistenceException(*e.args)

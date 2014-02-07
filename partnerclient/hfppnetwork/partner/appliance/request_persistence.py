'''
Copyright (C) 2010 - 2013 TopCoder Inc., All Rights Reserved.

This file contains request persistence.
RequestPersistence provides the access to the persistence request to partner client for later approval.

v1.1 - Healthcare Fraud Prevention Release Assembly v1.0
     - update createRequest and queryRequests for added StudyID

@version 1.1 ()
@author: TCSASSEMBLER
@since Healthcare Fraud Prevention - Partner Database Appliance - Assembly
'''

import os, sys
import redis
from redis.exceptions import RedisError

import abc
import cymysql as dbdriver
from cymysql import DatabaseError
from datetime import datetime

from appliance.partner_database_appliance_exception import PersistenceException
from appliance.claim_type import ClaimTypeFactory
from appliance.redis_query_parser import RedisQueryParser, RedisQueryParserError
from appliance.redis_query_executor import RedisQueryExecutor

class RequestPersistence(object):
    '''Request persistence class

    This class provides the access to the persistence request to partner client for later approval.
    This class is meant to be used as a session, so it must not be used by multiple threads. A session
    must begin and be closed by either a commit or a rollback.
    Not meant to be called outside the module, so there is no parameter check in the methods.
    '''
    __metaclass__ = abc.ABCMeta

    @abc.abstractmethod
    def begin(self):
        '''Begins the persistence connection session

        Raises:
            PersistenceException: There is DB error
        '''
        return

    @abc.abstractmethod
    def commit(self):
        '''Commits the sssion

        Raises:
            PersistenceException: There is DB error
        '''
        return

    @abc.abstractmethod
    def rollback(self):
        '''Rolls back the session

        Raises:
            PersistenceException: There is DB error
        '''
        return

    @abc.abstractmethod
    def close(self):
        '''Closes the session

        Raises:
            PersistenceException: There is DB error
        '''
        return

    @abc.abstractmethod
    def createRequest(self, fields):
        '''Creates request.

        Creates the request of the given type with the given data in the persistence

        Args:
            fields: the data of the request

        Raises:
            PersistenceException: There is DB error
        '''
        return

    @abc.abstractmethod
    def queryRequests(self, query, pageNumber, pageSize):
        '''Queries requests

        Queries the data according the the given filtering options, and returns the list of
        records (as lists) of data.

        Args:
            query: the query string
            pageNumber: the page number
            pageSize: the page size

        Returns:
            The list of records of data

        Raises:
            PersistenceException: There is DB error
        '''
        return

    @abc.abstractmethod
    def updateRequests(self, setStr, query):
        '''Updates requests

        Updates the data according the the given filtering options.

        Args:
            setStr: the updated fields and values
            query: the query string

        Raises:
            PersistenceException: There is DB error
        '''
        return

class MySQLRequestPersistence(RequestPersistence):
    '''Request persistence class

    This class provides the access to the persistence request to partner client for later approval.
    This class is meant to be used as a session, so it must not be used by multiple threads. A session
    must begin and be closed by either a commit or a rollback.
    Not meant to be called outside the module, so there is no parameter check in the methods.

    Attributes:
        connectionConfig: Represents the connection config
        connections: Represents the connection object. This will be set in begin, and closed in commit/rollback, and use in the business methods
        cursor: Represents the cursor object. This will be set in begin, and closed in commit/rollback, and use in the business methods
    '''

    def __init__(self):
        '''Init MySQLRequestPersistence'''
        self.connectionConfig = {}
        self.connection = None
        self.cursor = None

    def begin(self):
        '''Begins the persistence connection session

        Raises:
            PersistenceException: There is DB error
        '''
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
        '''Commits the sssion

        Raises:
            PersistenceException: There is DB error
        '''
        try:
            self.connection.commit()
        except DatabaseError as e:
            raise PersistenceException(*e.args)

    def rollback(self):
        '''Rolls back the session

        Raises:
            PersistenceException: There is DB error
        '''
        try:
            self.connection.rollback()
        except DatabaseError as e:
            raise PersistenceException(*e.args)

    def close(self):
        '''Closes the session

        Raises:
            PersistenceException: There is DB error
        '''
        try:
            if self.cursor:
                self.cursor.close()
                self.cursor = None
            if self.connection:
                self.connection.close()
                self.connection = None
        except DatabaseError as e:
            raise PersistenceException(*e.args)

    def createRequest(self, fields):
        '''Creates request

        Creates the request with the given data in the persistence

        Args:
            fields: the data of the request

        Raises:
            PersistenceException: There is DB error
        '''
        try:
            requestTablename = 'PartnerRequests'
            requestColumns = ['request_id', 'study_id', 'query', 'expiration_time',
                              'cache_available', 'cache_timestamp', 'status']
            columns = []
            data = []
            for i in range(len(fields)):
                if fields[i]:
                    data.append(fields[i])
                    columns.append(requestColumns[i])
            sql = "insert into %s(`%s`) values('%s')" % (requestTablename, "`, `".join(columns), "', '".join(data))
            print(sql)
            self.cursor.execute(sql)
        except DatabaseError as e:
            raise PersistenceException(*e.args)

    def queryRequests(self, query, pageNumber, pageSize):
        '''Queries requests

        Queries the data according the the given filtering options, and returns the list of
        records (as lists) of data.

        Args:
            query: the query string
            pageNumber: the page number
            pageSize: the page size

        Returns:
            The list of records of data

        Raises:
            PersistenceException: There is DB error
        '''
        try:
            requestTablename = 'PartnerRequests'
            requestColumns = ['id', 'request_id', 'study_id', 'query', 'expiration_time',
                              'cache_available', 'cache_timestamp', 'status']
            columns = "`%s`" % "`, `".join(requestColumns)
            tablename = requestTablename
            where = query
            limit = ""
            if pageNumber:
                offset = (pageNumber-1) * pageSize
                limit = "limit %s,%s" % (offset, pageSize)
            sql = "select %s from %s where %s %s" % (columns, tablename, where, limit)
            self.cursor.execute(sql)
            requests = self.cursor.fetchall()
            return requests
        except DatabaseError as e:
            raise PersistenceException(*e.args)

    def updateRequests(self, setStr, query):
        '''Updates requests

        Updates the data according the the given filtering options.

        Args:
            setStr: the updated fields and values
            query: the query string

        Raises:
            PersistenceException: There is DB error
        '''
        try:
            requestTablename = 'PartnerRequests'
            tablename = requestTablename
            where = query
            sql = "update %s set %s where %s" % (tablename, setStr, where)
            self.cursor.execute(sql)
        except DatabaseError as e:
            raise PersistenceException(*e.args)

class RedisRequestPersistence(RequestPersistence):
    '''Request persistence class

    This class provides the access to the persistence request to partner client for later approval.
    This class is meant to be used as a session, so it must not be used by multiple threads. A session
    must begin and be closed by either a commit or a rollback.
    Not meant to be called outside the module, so there is no parameter check in the methods.

    Attributes:
        connectionConfig: Represents the connection config
        connections: Represents the connection object. This will be set in begin, and closed in commit/rollback, and use in the business methods
        cursor: Represents the cursor object. This will be set in begin, and closed in commit/rollback, and use in the business methods
    '''

    def __init__(self):
        '''Init MySQLRequestPersistence'''
        self.connectionConfig = {}
        self.connection = None
        self.cursor = None

    def begin(self):
        '''Begins the persistence connection session

        Raises:
            PersistenceException: There is DB error
        '''
        try:
            self.connection = redis.StrictRedis(host=self.connectionConfig["host"],
                                                port=self.connectionConfig["port"],
                                                db=self.connectionConfig["db"])
            self.connection.ping()
            self.cursor = self.connection.pipeline()
            self.cursor.multi()
        except RedisError as e:
            raise PersistenceException(*e.args)

    def commit(self):
        '''Commits the sssion

        Raises:
            PersistenceException: There is DB error
        '''
        try:
            self.cursor.execute()
        except RedisError as e:
            raise PersistenceException(*e.args)

    def rollback(self):
        '''Rolls back the session

        Raises:
            PersistenceException: There is DB error
        '''
        pass

    def close(self):
        '''Closes the session

        Raises:
            PersistenceException: There is DB error
        '''
        try:
            if self.cursor:
                self.cursor.reset()
                self.cursor = None
            if self.connection:
                self.connection = None
        except RedisError as e:
            raise PersistenceException(*e.args)

    def createRequest(self, fields):
        '''Creates request

        Creates the request with the given data in the persistence

        Args:
            fields: the data of the request

        Raises:
            PersistenceException: There is DB error
        '''
        try:
            mapping = {}
            requestTablename = 'PartnerRequests'
            requestColumns = ['request_id', 'study_id', 'query', 'expiration_time',
                              'cache_available', 'cache_timestamp', 'status']
            
            for i in range(len(fields)):
                mapping[requestColumns[i]] = fields[i]
            id = self.connection.incr(requestTablename + ":lastid")
            self.cursor.sadd(requestTablename, id)
            self.cursor.hmset(requestTablename + ":" + str(id), mapping)
            for i in range(len(fields)):
                # All columns are varchar
                self.cursor.sadd(requestTablename + ":" + requestColumns[i] + ":" + fields[i], id)
        except RedisError as e:
            raise PersistenceException(*e.args)

    def queryRequests(self, query, pageNumber, pageSize):
        '''Queries requests

        Queries the data according the the given filtering options, and returns the list of
        records (as lists) of data.

        Args:
            query: the query string
            pageNumber: the page number
            pageSize: the page size

        Returns:
            The list of records of data

        Raises:
            PersistenceException: There is DB error
        '''
        try:
            claimType = ClaimTypeFactory.load("partner_request")

            query_parser = RedisQueryParser(claimType)
            parse_tree = query_parser.parse(query)

            query_executor = RedisQueryExecutor(self.connection, claimType)
            object_ids = query_executor.visit(parse_tree)
            if pageNumber:
                start = (pageNumber-1) * pageSize
                end = start + pageSize
                object_ids = object_ids[start:end]

            for id in object_ids:
                self.cursor.hgetall(claimType.tablename + ":" + id.decode())
            claims = self.cursor.execute()

            for i in range(len(claims)):
                claims[i] = self._convert_to_tuple(claimType, claims[i])
            return claims
        except RedisQueryParserError as e:
            raise PersistenceException(*e.args)
        except RedisError as e:
            raise PersistenceException(*e.args)

    def updateRequests(self, setStr, query):
        '''Updates requests

        Updates the data according the the given filtering options.

        Args:
            setStr: the updated fields and values
            query: the query string

        Raises:
            PersistenceException: There is DB error
        '''
        try:
            claimType = ClaimTypeFactory.load("partner_request")

            query_parser = RedisQueryParser(claimType)
            parse_tree = query_parser.parse(query)

            query_executor = RedisQueryExecutor(self.connection, claimType)
            object_ids = query_executor.visit(parse_tree)

            set_key = setStr.split('=')[0].encode()
            set_val = setStr.split('=')[1].encode()

            for id in object_ids:
                self.cursor.hgetall(claimType.tablename + ":" + id.decode())
                claims = self.cursor.execute()
                for claim in claims:
                    if claim is not None:
                        self.cursor.srem(claimType.tablename + ":" + set_key.decode() + ":" + claim[set_key].decode(), int(id.decode()))
                        self.cursor.sadd(claimType.tablename + ":" + set_key.decode() + ":" + set_val.decode(), int(id.decode()))
                        claim[set_key] = set_val
                        self.cursor.hmset(claimType.tablename + ":" + id.decode(), self._convert_to_decode(claim))
            
        except RedisQueryParserError as e:
            raise PersistenceException(*e.args)
        except RedisError as e:
            raise PersistenceException(*e.args)

    def _convert_to_decode(self, claim):
        """Convert redis encoded string to decoded string."""
        new_claim = {}
        for key in claim:
            new_claim[key.decode()] = claim[key].decode()
        return new_claim

    def _convert_to_tuple(self, claimType, claim):
        """ Convert a hash returned from Redis to a tuple """
        values = []
        for i in range(len(claimType.columns)):
            column = claimType.columns[i].encode()
            value = claim[column].decode()
            if value and claimType.columnTypes[i] == "integer":
                value = int(value)
            elif value and claimType.columnTypes[i] == "decimal":
                value = Decimal(value)
            values.append(value)
        # don't remove LAST_MODIFY, it doesn't contain that
        return tuple(values)




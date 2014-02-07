"""
Copyright (C) 2010 - 2013 TopCoder Inc., All Rights Reserved.

This file contains Redis claim persistence.

@version 1.0 (Healthcare Fraud Prevention - Partner Database Integration Assembly)
@author: TCSASSEMBLER
"""

import os
import sys
import redis
from redis.exceptions import RedisError
from decimal import Decimal

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from partner_database_appliance_exception import PersistenceException
from claim_type import ClaimTypeFactory
from claim_persistence import ClaimPersistence
from redis_query_parser import RedisQueryParser, RedisQueryParserError
from redis_query_executor import RedisQueryExecutor
from datetime import datetime

class RedisClaimPersistence(ClaimPersistence):
    """
    RedisClaimPersistence class

    This class provides the access to the Redis persistence backend where the files are stored then queried.
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
        """Init RedisClaimPersistence"""
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
            self.connection = redis.StrictRedis(host=self.connectionConfig["host"],
                                                port=self.connectionConfig["port"],
                                                db=self.connectionConfig["db"])
            self.connection.ping()
            self.cursor = self.connection.pipeline()
            self.cursor.multi()
        except RedisError as e:
            raise PersistenceException(*e.args)

    def commit(self):
        """
        Commits the session

        Raises:
            PersistenceException: There is DB error
        """
        try:
            self.cursor.execute()
        except RedisError as e:
            raise PersistenceException(*e.args)

    def rollback(self):
        """
        Rolls back the session

        Leave this method blank because Redis doesn't support rollback.

        Raises:
            PersistenceException: There is DB error
        """
        pass

    def close(self):
        """
        Closes the session

        Raises:
            PersistenceException: There is DB error
        """
        try:
            if self.cursor:
                self.cursor.reset()
                self.cursor = None
            if self.connection:
                self.connection = None
        except RedisError as e:
            raise PersistenceException(*e.args)

    def createClaim(self, claimType, fields):
        """
        Creates claim

        Creates the claim of the given type with the given data in the persistence

        Redis is a key-value store so we have to maintain indexes on our own.
        Each object has an auto-incremented id and is stored as a hash value in Redis.
        Numerical attributes are indexed in a sorted set with key "tablename:column".
        Textual attributes are indexed in separate sets with key "tablename:column:textvalue".
        Thus numerical attributes can have range queries, while textual attributes can only have equality queries.

        Args:
            claimType: the claimType
            fields: the data of the claim

        Raises:
            PersistenceException: There is DB error
        """
        try:
            claimType = ClaimTypeFactory.load(claimType)
            mapping = {}
            # Adding additional field LAST_MODIFY
            # Restricted by redis, we use a integer-like datetime here
            fields.append(datetime.now().strftime('%Y%m%d%H%M%S'))
            
            for i in range(len(fields)):
                mapping[claimType.columns[i]] = fields[i]
            id = self.connection.incr(claimType.tablename + ":lastid")
            self.cursor.sadd(claimType.tablename, id)
            self.cursor.hmset(claimType.tablename + ":" + str(id), mapping)
            for i in range(len(fields)):
                if claimType.columnTypes[i] == "integer":
                    if fields[i]:
                        self.cursor.zadd(claimType.tablename + ":" + claimType.columns[i], int(fields[i]), id)
                elif claimType.columnTypes[i] == "decimal":
                    if fields[i]:
                        self.cursor.zadd(claimType.tablename + ":" + claimType.columns[i], Decimal(fields[i]), id)
                elif claimType.columnTypes[i] == "datetime":
                    if fields[i]:
                        self.cursor.zadd(claimType.tablename + ":" + claimType.columns[i], int(fields[i]), id)
                elif claimType.columnTypes[i] == "varchar":
                    self.cursor.sadd(claimType.tablename + ":" + claimType.columns[i] + ":" + fields[i], id)
                else:
                    raise ValueError("Invalid column type: " + claimType.columnTypes[i])
        except RedisError as e:
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
        # remove LAST_MODIFY
        return tuple(values[0:-1])

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

            query_parser = RedisQueryParser(claimType)
            parse_tree = query_parser.parse(query)

            query_executor = RedisQueryExecutor(self.connection, claimType)
            object_ids = query_executor.visit(parse_tree)

            cnt = len(object_ids)
            return cnt
        except RedisQueryParserError as e:
            raise PersistenceException(*e.args)
        except RedisError as e:
            raise PersistenceException(*e.args)

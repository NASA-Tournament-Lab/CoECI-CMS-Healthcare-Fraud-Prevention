"""
Copyright (C) 2010 - 2013 TopCoder Inc., All Rights Reserved.

This file contains claim persistence.
ClaimPersistence provides the access to the persistence where the files are stored then queried.

@version 1.0 (Healthcare Fraud Prevention - Partner Database Appliance - Assembly)
@author: zeadom, TCSASSEMBLER
@since 2013-12-10
"""

import abc

class ClaimPersistence(object):
    """
    Claim persistence class

    This class provides the access to the persistence where the files are stored then queried.
    This class is meant to be used as a session, so it must not be used by multiple threads.
    A session must begin and be closed by either a commit or a rollback.
    Not meant to be called outside the module, so there is no parameter check in the methods.
    """
    __metaclass__ = abc.ABCMeta

    @abc.abstractmethod
    def begin(self):
        """
        Begins the persistence connection session

        Raises:
            PersistenceException: There is DB error
        """
        return

    @abc.abstractmethod
    def commit(self):
        """
        Commits the sssion

        Raises:
            PersistenceException: There is DB error
        """
        return

    @abc.abstractmethod
    def rollback(self):
        """
        Rolls back the session

        Raises:
            PersistenceException: There is DB error
        """
        return

    @abc.abstractmethod
    def close(self):
        """
        Closes the session

        Raises:
            PersistenceException: There is DB error
        """
        return

    @abc.abstractmethod
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
        return

    @abc.abstractmethod
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
        return

    @abc.abstractmethod
    def queryClaims(self, claimType, query, pageNumber, pageSize):
        """
        Queries claims

        Queries the data according the the given filtering options from the given claim type,
        and returns the list of records (as lists) of data for the given claim type

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
        return

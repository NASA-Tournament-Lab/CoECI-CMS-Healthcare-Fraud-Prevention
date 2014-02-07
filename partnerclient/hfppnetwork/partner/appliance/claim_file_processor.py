"""
Copyright (C) 2010 - 2013 TopCoder Inc., All Rights Reserved.

This file contains claim processor.  ClaimProcessor is the processor that provides the public methods for writing file
data into the persistence and querying data from the persistence into a file. The processor is meant to be the utility
used from another library if not running the file directly.

@version 1.0 (Healthcare Fraud Prevention - Partner Database Appliance - Assembly)
@author: zeadom, TCSASSEMBLER
@since 2013-12-10
"""

import os, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from partner_database_appliance_exception import PartnerDatabaseApplianceException
from partner_database_appliance_exception import IllegalArgumentException, NonexistentClaimTypeException
from claim_type import ClaimTypeFactory
from claim_file import ClaimFile

class ClaimFileProcessor(object):
    """
    ClaimFileProcessor

    The class that provides the public methods for writing file data into the persistence and querying data from the
    persistence into a file.
    The processor is meant to be the utility used from another library if not running the file directly.

    Attributes:
        claimPersistence: Represents the ClaimPersistence instance that encapsulates the persistence
        claimFile: Represents the ClaimFile instance that will wrap the file to be processed
    """

    def _checkClaimType(self, claimType):
        """
        Check if claim type is legal

        Args:
            claimType: the claimType

        Raises:
            IllegalArgumentException: ClaimType is None or empty.
            NonexistentClaimTypeException: ClaimType not in (beneficiary, carrier, inpatient, outpatient, prescription)
        """
        if not claimType:
            raise IllegalArgumentException("the claim type can not be null.")
        elif claimType not in ClaimTypeFactory.claimTypeList:
            raise NonexistentClaimTypeException("Nonexistent claim type %s" % claimType)

    def _getFileList(self, filename):
        """
        Check filename and get file list by filename

        Args:
            filename: the name f the source file

        Returns:
            A list of filenames. Each filename represent a file.
            If filename is a file, return [filename],
            else if filename is a directory, return [all files in the directory]
            example:
                ["dir/a.csv", "dir/b.csv"]

        Raises:
            IllegalArgumentException: Filename is None or empty or filename is not a dir nor a file.
        """
        filenames = None
        if not filename:
            raise IllegalArgumentException("Filename can not be null.")
        elif os.path.isdir(filename):
            filenames = self.claimFile.findFilenames(filename)
            if not filenames:
                raise IllegalArgumentException("Empty path: %s" % filename)
        elif os.path.isfile(filename):
            filenames = [filename]
        else:
            raise IllegalArgumentException("Nonexistent file: %s" % filename)
        return filenames

    def writeFile(self, claimType, filename):
        """
        Write file

        Reads the records in the given file for the given claim type and adds them to the database.

        Args:
            claimType: the claimType
            filename: the name f the source file

        Raises:
            IllegalArgumentException:
                claimType is None or empty.
                filename is None or empty or filename is not a dir nor a file.
            NonexistentClaimTypeException: claimType not in (beneficiary, carrier, inpatient, outpatient, prescription)
            PartnerDatabaseApplianceException: Any other errors
        """
        try:
            self._checkClaimType(claimType)
            filenames = self._getFileList(filename)
            self.claimPersistence.begin()
            for filename in filenames:
                try:
                    self.claimFile.openFile(filename, ClaimFile.READ)
                    # The first line in csv file is title, which shouldn't be added to database
                    fields = self.claimFile.readNextLine(claimType)
                    numCreate = 0
                    while True:
                        fields = self.claimFile.readNextLine(claimType)
                        # If runs out of lines, break
                        if not fields:
                            break
                        self.claimPersistence.createClaim(claimType, fields)
                        numCreate = numCreate + 1
                        if numCreate%1000 == 0:
                            print(str(numCreate) + ', ')
                            self.claimPersistence.commit()
                except Exception as e:
                    sys.stderr.write("Error occurs in processing file %s: %s" % (filename, str(e)))
                    self.claimPersistence.rollback()
                finally:
                    if self.claimFile.fileObject:
                        self.claimFile.closeFile()
            self.claimPersistence.commit()
        except Exception as e:
            if isinstance(e, PartnerDatabaseApplianceException):
                raise
            else:
                raise PartnerDatabaseApplianceException(*e.args)
        finally:
            if self.claimPersistence.connection:
                self.claimPersistence.close()

    def queryData(self, claimType, query, pageNumber, pageSize, filename = None):
        """
        Query data

        Queries the data according the the given filtering options from the given claim type, and writes the result
        into the file with the given name.

        Args:
            claimType: the claimType
            query: the query string
            pageNumber: the page number
            pageSize: the page size
            filename: the name of the destination file. If None, default output filename would be used.

        Raises:
            IllegalArgumentException:
                claimType is None or empty.
                query is None or empty.
                pageNumber is not a number or is less than 0
                pageNumber is not 0 and pageSize is not a positive number
            NonexistentClaimTypeException: claimType not in (beneficiary, carrier, inpatient, outpatient, prescription)
            PartnerDatabaseApplianceException: Any other errors
        """
        try:
            self._checkClaimType(claimType)
            if not query:
                raise IllegalArgumentException("The query should not be None or Empty")
            try:
                pageNumber = int(pageNumber) if pageNumber else 0
                if pageNumber == 0:
                    # If pageNumber is 0, then no paging is done and pageSize will be ignored.
                    # Set pageSize to a legal positive number in case that an illegal pageSize may cause other errors.
                    pageSize = 1
                else:
                    pageSize = int(pageSize) if pageSize else 0
            except ValueError as e:
                raise IllegalArgumentException(*e.args)
            if pageNumber < 0:
                raise IllegalArgumentException("Illegal page number %s" % pageNumber)
            if pageSize <= 0:
                raise IllegalArgumentException("Illegal page size %s" % pageSize)

            self.claimPersistence.begin()
            self.claimFile.openFile(filename, ClaimFile.WRITE)
            records = self.claimPersistence.queryClaims(claimType, query, pageNumber, pageSize)
            self.claimFile.write(claimType, records)
        except Exception as e:
            if isinstance(e, PartnerDatabaseApplianceException):
                raise
            else:
                raise PartnerDatabaseApplianceException(*e.args)
        finally:
            if self.claimFile.fileObject:
                self.claimFile.closeFile()
            if self.claimPersistence.connection:
                self.claimPersistence.close()


    def queryDataSize(self, claimType, query):
        """
        Query data size

        Queries the data according the the given query and returns the number of records retrieved from the query.

        Args:
            claimType: the claimType
            query: the query string

        Returns:
            The number of records retrieved from the query

        Raises:
            IllegalArgumentException:
                claimType is None or empty.
                query is None or empty.
            NonexistentClaimTypeException: claimType not in (beneficiary, carrier, inpatient, outpatient, prescription)
            PartnerDatabaseApplianceException: Any other errors
        """
        try:
            self._checkClaimType(claimType)
            if not query:
                raise IllegalArgumentException("The query should not be None or Empty")
            self.claimPersistence.begin()
            rowCount = self.claimPersistence.queryClaimSize(claimType, query)
            return rowCount
        except Exception as e:
            if isinstance(e, PartnerDatabaseApplianceException):
                raise
            else:
                raise PartnerDatabaseApplianceException(*e.args)
        finally:
            if self.claimPersistence.connection:
                self.claimPersistence.close()

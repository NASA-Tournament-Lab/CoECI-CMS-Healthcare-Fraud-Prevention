"""
Copyright (C) 2010 - 2013 TopCoder Inc., All Rights Reserved.

This file contains claim file.
ClaimFile provides the access to the file

@version 1.0 (Healthcare Fraud Prevention - Partner Database Appliance - Assembly)
@author: zeadom, TCSASSEMBLER
@since 2013-12-10
"""

import os, sys
import abc
import csv
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from partner_database_appliance_exception import PartnerDatabaseApplianceException
from claim_type import ClaimTypeFactory

class ClaimFile(object):
    """
    Claim file abstract class

    This class provides the access to the file
    Not meant to be called outside the module, so there is no parameter check in the methods.

    Attributes:
        fileObject: Represents the current file object. This will be set in openFile, and closed in write,
                    and use in the business methods.
        fileType: Represents the file type being used. Will be set by concrete implementations.
        defaultFilename: Represents the default name of the file that will contain the output.
    """

    __metaclass__ = abc.ABCMeta

    READ = "read"
    WRITE = "write"

    def __init__(self):
        """ Init ClaimFile """
        self.fileObject = None
        self.fileType = ""
        self.defaultFilename = "output"

    def openFile(self, filename, mode):
        """
        Opens the file for processing

        Args:
            filename: the name of the file to open
            mode: the mode of the operation (read, or write)

        Raises:
            PartnerDatabaseApplianceException: There is an error during the execution of this method.
        """
        try:
            if not filename:
                filename = "%s.%s" % (self.defaultFilename, self.fileType)
            if mode == self.READ:
                self.fileObject = open(filename, "r")
            elif mode == self.WRITE:
                self.fileObject = open(filename, "w")
        except Exception as e:
            raise PartnerDatabaseApplianceException(*e.args)

    def closeFile(self):
        """
        Closes the file

        Raises:
            PartnerDatabaseApplianceException: There is an error during the execution of this method.
        """
        try:
            if self.fileObject:
                self.fileObject.close()
                self.fileObject = None
        except Exception as e:
            raise PartnerDatabaseApplianceException(*e.args)

    @abc.abstractmethod
    def readNextLine(self, claimType):
        """
        Read next line

        This is a abstract method, and should be implemented in subclass.
        Reads the next line from the open file of the given claim type and returns the field values

        Args:
            claimType: the claimType

        Returns:
            The next record

        Raises:
            PartnerDatabaseApplianceException: There is an error during the execution of this method.
        """
        return

    @abc.abstractmethod
    def write(self, claimType, records):
        """
        Writes the records into the open file

        This is a abstract method, and should be implemented in subclass.

        Args:
            claimType: the claimType
            records: the records to write

        Raises:
            PartnerDatabaseApplianceException: There is an error during the execution of this method.
        """
        return

    def findFilenames(self, dirPath):
        """
        File filenames

        Helper method to get all files in the given directory for the requested file type recursively

        Args:
            dirPath: the path to the directory

        Returns:
            A list of filenames for the requested file type.
            example:
                ["dir/a.csv", "dir/b.csv", "dir/anotherdir/c.csv"]
        """
        filenames = os.listdir(dirPath)
        ret = []
        for filename in filenames:
            absfilename = os.path.join(dirPath, filename)
            if os.path.isdir(absfilename):
                ret.extend(self.findFilenames(absfilename))
            elif filename.endswith("." + self.fileType):
                ret.append(absfilename)
        return ret

class CSVClaimFile(ClaimFile):
    """
    CSV Claim File Class

    This class provides the access to the CSV file.
    USing the csv module in the standard library
    Not meant to be called outside the module, so there is no parameter check in the methods.

    Attributes:
        fileObject: Represents the current file object. This will be set in openFile, and closed in write,
                    and use in the business methods.
        fileType: Represents the file type being used. Will be set by concrete implementations.
        defaultFilename: Represents the default name of the file that will contain the output.
        fileReader: Represents the current file reader object. Will be used in readNextLine
    """

    def __init__(self):
        """ Init CSVClaimFile """
        super(CSVClaimFile, self).__init__()
        self.fileType = "csv"
        self.fileReader= None

    def openFile(self, filename, mode):
        """
        Opens the file for processing

        Override the superclass's openFile method

        Args:
            filename: the name of the file to open
            mode: the mode of the operation (read, or write)

        Raises:
            PartnerDatabaseApplianceException: There is an error during the execution of this method.
        """
        try:
            if not filename:
                filename = "%s.%s" % (self.defaultFilename, self.fileType)
            if mode == self.READ:
                self.fileObject = open(filename, "r")
                # fileReader will be used in readNextLine
                self.fileReader = csv.reader(self.fileObject, delimiter=",")
            elif mode == self.WRITE:
                self.fileObject = open(filename, "w", newline="")
        except Exception as e:
            raise PartnerDatabaseApplianceException(*e.args)

    def readNextLine(self, claimType):
        """
        Read next line

        Reads the next line from the open file of the given claim type and returns the field values

        Args:
            claimType: the claimType

        Returns:
            The next record

        Raises:
            PartnerDatabaseApplianceException: There is an error during the execution of this method.
        """
        try:
            return next(self.fileReader)
        except StopIteration:
            return None
        except Exception as e:
            raise PartnerDatabaseApplianceException(*e.args)

    def write(self, claimType, records):
        """
        Writes the records into the open file

        Args:
            claimType: the claimType
            records: the records to write

        Raises:
            PartnerDatabaseApplianceException: There is an error during the execution of this method.
        """
        try:
            fileWriter = csv.writer(self.fileObject, delimiter=",")
            claimType = ClaimTypeFactory.load(claimType)
            # Remove LAST_MODIFY
            fileWriter.writerow(claimType.columns[0:-1])
            fileWriter.writerows(records)
        except Exception as e:
            raise PartnerDatabaseApplianceException(*e.args)

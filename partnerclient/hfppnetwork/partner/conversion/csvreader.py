##
# Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
##

"""
CSVReader is designed to read data from CSV file and construct entity classes.
"""
__author__ = 'Easyhard'
__version__ = '1.0'

from conversion.datareader import DataReader
import csv
from datetime import date, datetime

from conversion import entities
from conversion.schema import csv as csvdata
from conversion.exception import DataReaderException

class CSVReader(DataReader):
    """Read from CSV file and construct entity classes on-demand row by row"""
    # CSV reader for parsing csv file
    _reader = None
    # Headers of the parsing csv file
    _headers = None
    _last = None
    # name of type that matching the parsing file of this reader
    filetype = None

    def __init__(self, file_object, filetype, dialect='unix'):
        """
        Initlize a new CSV Reader by file object given by `file_object`.
        If the content of `file_object` matches any type of csv, if
        will set self.filetype to correct name and success,
        otherwise, a exception will be thrown.
        """
        self.filetype = filetype
        self._reader = csv.reader(file_object, dialect=dialect)
        try:
            self._headers = csvdata[filetype]['default_order']
        except KeyError as e:
            raise DataReaderException('bad csv schema for class %s' % filetype)

        # detecting whether there is a header
        try:
            self._last = next(self._reader)
        except StopIteration as e:
            # no even one row
            return
        contain_header = True
        for element in self._last:
            if element not in self._headers:
                contain_header = False
                break
        if contain_header:
            # using the found header's order
            self._headers = self._last
            # discard the header line
            self._last = None

    def next_entity(self):
        """
        Return the next entity that corresponds to the current row of the csv,
        and move the cursor to the next row.
        If any field is invalid, a exception will be thrown.
        """
        try:
            csv_attrs = csvdata[self.filetype]
        except KeyError as e:
            raise DataReaderException('bad csv schema for class %s' % filetype)
        current_row = None
        if self._last:
            current_row = self._last
            self._last = None
        else:
            try:
                current_row = next(self._reader)
            except:
                return None
        # filetype is the same as class name.
        # Logic could be added to mapping filetypes to entity classes.
        # getattr will return the corresponding entity class. Then a new
        # instance is constructed.
        entity = getattr(entities, self.filetype)()
        for item, header in zip(current_row, self._headers):
            try:
                field_name = csvdata[self.filetype]['mapping'][header]
            except KeyError as e:
                raise DataReaderException('bad csv schema for class %s' % filetype)
            setattr(entity, field_name, None)
            if item:
                if entity.typeof(field_name) == 'date':
                    # for date type, reader need to construct it to datetime.date
                    try:
                        newdate = datetime.strptime(item, '%Y%m%d').date()
                    except ValueError as e:
                        raise DataReaderException('bad cell for formatting date: %s' % item)
                    setattr(entity, field_name, newdate)
                else:
                    # for other data types, setter method will convert it correctly.
                    # or throw an exception
                    try:
                        setattr(entity, field_name, item)
                    except ValueError as e:
                        raise DataReaderException('item cannot convert to require data type: %s' % str(item))
        return entity

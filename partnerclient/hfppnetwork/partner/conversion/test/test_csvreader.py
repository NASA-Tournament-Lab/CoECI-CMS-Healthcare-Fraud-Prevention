##
# Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
##
__author__ = 'Easyhard'
__version__ = '1.0'


from csvreader import CSVReader
from schema import entity
import unittest
from datetime import date
from exception import DataReaderException
from io import StringIO

class TestCSVReader(unittest.TestCase):
    """Testcases for csvreader.py"""
    def setUp(self):
        pass

    def test_init(self):
        """Testing __init__"""
        file_object = StringIO()
        for class_name in entity.keys():
            reader = CSVReader(file_object, class_name)
        with self.assertRaises(DataReaderException):
            reader = CSVReader(file_object, 'Foo')

    def test_next_entity_end(self):
        """next_entity should return None when ending"""
        file_object = StringIO()
        reader = CSVReader(file_object, 'Testing')
        self.assertIsNone(reader.next_entity())

    def reader_once(self, string, filetype):
        """Helper function for only read once testcases"""
        file_object = StringIO(string.strip())
        reader = CSVReader(file_object, filetype)
        instance = reader.next_entity()
        return instance

    def test_csv_no_header(self):
        """using default order if no header"""
        instance = self.reader_once('"abc","99991231","23","1.1"',
                                    'Testing')
        self.assertEqual(instance.f1, 'abc')
        self.assertEqual(instance.f2, date.max)
        self.assertEqual(instance.f3, 23)
        self.assertEqual(instance.f4, 1.1)

    def test_csv_with_header(self):
        """using header's order if there is one"""
        instance = self.reader_once("""
"FIELD_1","FIELD_2","FIELD_3","FIELD_4"
"abc","99991231","23","1.1"
        """,
        'Testing')
        self.assertEqual(instance.f1, 'abc')
        self.assertEqual(instance.f2, date.max)
        self.assertEqual(instance.f3, 23)
        self.assertEqual(instance.f4, 1.1)
        instance = self.reader_once("""
"FIELD_4","FIELD_3","FIELD_2","FIELD_1"
"1.1","23","99991231","abc"
        """,
        'Testing')
        self.assertEqual(instance.f1, 'abc')
        self.assertEqual(instance.f2, date.max)
        self.assertEqual(instance.f3, 23)
        self.assertEqual(instance.f4, 1.1)

    def test_csv_exception(self):
        """Raising exception if cell's data is invalid"""
        with self.assertRaises(DataReaderException):
            self.reader_once('"abc","9999-12-31","23","1.1"',
                             'Testing')
        with self.assertRaises(DataReaderException):
            self.reader_once('"abc","99991231","23.a","1.1"',
                             'Testing')
        with self.assertRaises(DataReaderException):
            self.reader_once('"abc","99991231","23","1.1a"',
                             'Testing')

if __name__ == '__main__':
    unittest.main()

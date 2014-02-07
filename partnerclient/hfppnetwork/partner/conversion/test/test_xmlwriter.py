##
# Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
##
__author__ = 'Easyhard'
__version__ = '1.0'


from xmlwriter import XMLWriter
from schema import entity
from entities import Testing
import unittest
from datetime import date
from exception import DataWriterException
from io import StringIO

class TestXMLWriter(unittest.TestCase):
    """Testcases for xmlwriter.py"""
    def setUp(self):
        pass

    def test_init(self):
        """Testing __init__"""
        file_object = StringIO()
        for class_name in entity.keys():
            reader = XMLWriter(file_object, class_name)
        with self.assertRaises(DataWriterException):
            reader = XMLWriter(file_object, 'Foo')

    def test_default_value(self):
        """Testing default value"""
        file_object = StringIO()
        writer = XMLWriter(file_object, 'Testing')
        instance = Testing()
        writer.start()
        writer.write_entity(instance)
        writer.end()
        result = file_object.getvalue()
        self.assertEqual(result.strip(),
                         """
<?xml version="1.0" encoding="UTF-8"?>
<Testings>
    <Testing>
        <f1></f1>
        <f2>9999-12-31</f2>
        <f3>-1</f3>
        <f4>-1.0</f4>
    </Testing>
</Testings>""".strip())

    def test_dump(self):
        """Testing dumping two instance"""
        file_object = StringIO()
        writer = XMLWriter(file_object, 'Testing')
        writer.start()

        instance = Testing()
        instance.f1 = 'aaa'
        import datetime
        instance.f2 = datetime.datetime(1999, 2, 3).date()
        instance.f3 = 1
        instance.f4 = 1
        writer.write_entity(instance)

        instance.f1 = 'bbb'
        import datetime
        instance.f2 = datetime.datetime(2009, 2, 3).date()
        instance.f3 = 2
        instance.f4 = 2
        writer.write_entity(instance)

        writer.end()
        result = file_object.getvalue()
        self.assertEqual(result.strip(),
"""
<?xml version="1.0" encoding="UTF-8"?>
<Testings>
    <Testing>
        <f1>aaa</f1>
        <f2>1999-02-03</f2>
        <f3>1</f3>
        <f4>1.0</f4>
    </Testing>
    <Testing>
        <f1>bbb</f1>
        <f2>2009-02-03</f2>
        <f3>2</f3>
        <f4>2.0</f4>
    </Testing>
</Testings>
""".strip())

if __name__ == '__main__':
    unittest.main()

##
# Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
##
__author__ = 'Easyhard'
__version__ = '1.0'


from xmlwriter import XMLPrettyGenerator
from xml.sax.xmlreader import AttributesNSImpl
import unittest
from io import StringIO

class TestXMLPrettyGenerator(unittest.TestCase):
    """Testcases for XMLPrettyGenerator"""
    def setUp(self):
        pass

    def test_nest(self):
        """Testing deep nest"""
        file_object = StringIO()
        writer = XMLPrettyGenerator(file_object, 'UTF-8')
        writer.startDocument()
        attrs = AttributesNSImpl({}, {})
        writer.startElementNS((None, 'root'), 'root', attrs)
        writer.startElementNS((None, 'a'), 'a', attrs)
        writer.startElementNS((None, 'b'), 'b', attrs)
        writer.startElementNS((None, 'c'), 'c', attrs)
        writer.endElementNS((None, 'c'), 'c')
        writer.endElementNS((None, 'b'), 'b')
        writer.endElementNS((None, 'a'), 'a')
        writer.endElementNS((None, 'root'), 'root')
        writer.endDocument()
        result = file_object.getvalue()
        self.assertEqual(result.strip(),
"""
<?xml version="1.0" encoding="UTF-8"?>
<root>
    <a>
        <b>
            <c></c>
        </b>
    </a>
</root>
""".strip())

if __name__ == '__main__':
    unittest.main()

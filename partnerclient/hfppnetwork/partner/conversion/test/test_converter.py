##
# Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
##
__author__ = 'Easyhard'
__version__ = '1.0'

from converter import csvobj2xmlobj
import unittest
from io import StringIO

class TestConverter(unittest.TestCase):
    """Testcases of converter"""
    def setUp(self):
        pass

    def test_bs(self):
        """Testing for converting BeneficiarySummary"""
        result = StringIO()
        infile = open('test/data/bs.csv')
        # the content of bs.xml is validated against the xsd schema via xmllint,
        # an opensource command-line tools
        expecting_file = open('test/data/bs.xml', 'r')
        expecting = expecting_file.read()
        expecting_file.close()
        csvobj2xmlobj(infile, result, 'BeneficiarySummary')
        infile.close()
        self.assertEqual(result.getvalue(), expecting)

    def test_ic(self):
        """Testing for converting InpatientClaim"""
        result = StringIO()
        infile = open('test/data/ic.csv')
        # the content of ic.xml is validated against the xsd schema via xmllint,
        # an opensource command-line tools
        expecting_file = open('test/data/ic.xml', 'r')
        expecting = expecting_file.read()
        expecting_file.close()
        csvobj2xmlobj(infile, result, 'InpatientClaim')
        infile.close()
        self.assertEqual(result.getvalue(), expecting)

    def test_oc(self):
        """Testing for converting OutpatientClaim"""
        result = StringIO()
        infile = open('test/data/oc.csv')
        # the content of oc.xml is validated against the xsd schema via xmllint,
        # an opensource command-line tools
        expecting_file = open('test/data/oc.xml', 'r')
        expecting = expecting_file.read()
        expecting_file.close()
        csvobj2xmlobj(infile, result, 'OutpatientClaim')
        infile.close()
        self.assertEqual(result.getvalue(), expecting)

    def test_cc(self):
        """Testing for converting CarrierClaim"""
        result = StringIO()
        infile = open('test/data/cc.csv')
        # the content of cc.xml is validated against the xsd schema via xmllint,
        # an opensource command-line tools
        expecting_file = open('test/data/cc.xml', 'r')
        expecting = expecting_file.read()
        expecting_file.close()
        csvobj2xmlobj(infile, result, 'CarrierClaim')
        infile.close()
        self.assertEqual(result.getvalue(), expecting)

    def tearDown(self):
        pass
if __name__ == '__main__':
    unittest.main()

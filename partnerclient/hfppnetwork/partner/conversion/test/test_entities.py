##
# Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
##
__author__ = 'Easyhard'
__version__ = '1.0'


import entities
from schema import entity
import unittest
from datetime import date
from exception import ConfigurationException

class TestEntities(unittest.TestCase):
    """Testcases for entities.py"""
    def setUp(self):
        pass

    def test_entity_class(self):
        """Testing Entity class's method"""
        testing = entities.Testing()
        self.assertEqual(testing.get_field_list(), entity['Testing']['fields'])

        self.assertEqual(testing.typeof('f1'), 'string')
        self.assertEqual(testing.typeof('f2'), 'date')
        self.assertEqual(testing.typeof('f3'), 'int')
        self.assertEqual(testing.typeof('f4'), 'decimal')

    def test_propertylist(self):
        """Checker if generated all properties."""
        for class_name, data in entity.items():
            instance = getattr(entities, class_name)()
            for name, stype, doc in data['fields']:
                a = getattr(instance, name)
                self.assertIsNone(a)
        self.assertIsNotNone(instance.typeof)

    def test_property_setget(self):
        """Checker all properties' getter and setter"""
        for class_name, data in entity.items():
            instance = getattr(entities, class_name)()
            for name, stype, doc in data['fields']:
                if stype == 'date':
                    setattr(instance, name, date.min)
                    self.assertEqual(getattr(instance, name), date.min)
                if stype == 'string':
                    setattr(instance, name, 'testing')
                    self.assertEqual(getattr(instance, name), 'testing')
                    setattr(instance, name, 123)
                    # automatic convert to string
                    self.assertEqual(getattr(instance, name), '123')
                if stype == 'int':
                    setattr(instance, name, 232)
                    self.assertEqual(getattr(instance, name), 232)
                    setattr(instance, name, 12.3)
                    # automiatic convert to int
                    self.assertEqual(getattr(instance, name), 12)
                    # raise exception if caonnot convert
                    with self.assertRaises(ValueError):
                        setattr(instance, name, 'abc')
                if stype == 'decimal':
                    setattr(instance, name, 232)
                    self.assertEqual(getattr(instance, name), 232.0)
                    setattr(instance, name, 12.3)
                    # automiatic convert to int
                    self.assertEqual(getattr(instance, name), 12.3)
                    # raise exception if caonnot convert
                    with self.assertRaises(ValueError):
                        setattr(instance, name, 'abc')

    def test_exceptions(self):
        """Testing raising of ConfigurationException"""
        class Foo(object):
            pass
        with self.assertRaises(ConfigurationException):
            Foo = entities.entity_Class(Foo)

if __name__ == '__main__':
    unittest.main()

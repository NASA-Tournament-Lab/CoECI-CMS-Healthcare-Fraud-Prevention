##
# Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
##

"""
Entity Classes.

Classes which representing different type of data.

Here some meta-programming is wantted becauses different entity classes
could be generated in the same way. It can't be done by OOP because OOP
only provides capacity to abstract classes which have same attributes and
functions, not the way of generating them.

In python3 there is two way to do it, metaclass and class decorator. metaclass
is able to inherit but also harder to understand. Since what we need is just
a simple entity class that containing data fields, class decorator is enough
and it it easier for maintainer.

Here is some notes for people not fimilar with class decorator.
In short, it is just like function decorator, i.e,

@decorator
class Foo(object):pass

is the same as

class Foo(object):pass
Foo = decorator(Foo)

And by modifying the class object, new properties, attributes and methods
could be added to instances of the class.
For details, you can refer http://www.python.org/dev/peps/pep-3129/
"""
__author__ = 'Easyhard'
__version__ = '1.0'

from conversion.schema import entity
from conversion.exception import ConfigurationException

def tuple_replace(t, index, value):
    """replace tuple `t`'s t[index] to `value`"""
    return t[:index] + (value,) + t[index+1:]

# Decorators for different type of property setter.
# In general, it will try to convert given value to correct type
# and throw exception if failed.
def int_check(func):
    """Decorator for int type data field"""
    def wrapper(*args, **kw):
        # args[0] is self, args[1] is newvalue
        newargs = args
        if args[1]:
            newargs = tuple_replace(args, 1, int(args[1]))
        return func(*newargs, **kw)
    return wrapper

def decimal_check(func):
    """Decorator for decimal type data field"""
    def wrapper(*args, **kw):
        newargs = args
        if args[1]:
            newargs = tuple_replace(args, 1, float(args[1]))
        return func(*newargs, **kw)  # Call hello
    return wrapper

def string_check(func):
    """Decorator for string type data field"""
    def wrapper(*args, **kw):
        newargs = args
        if args[1]:
            newargs = tuple_replace(args, 1, str(args[1]))
        return func(*newargs, **kw)  # Call hello
    return wrapper

def identical(func):
    """Decorator that does nothing, for date type data field,
    since we don't know how to convert different file format's
    data string. It leaves to the reader"""
    return func

# This is a class decorator, a new feature of python3.
def entity_Class(cls):
    """Class decorator for automatic generating entity classes
    based on infomation in schema.py. It will create properties with
    proper getter/setter according to the type of field, collect fields'
    infomation into a dict, and generating some public functions.
    """
    # for collecting infomation of fields.
    cls._info = {}
    try:
        entity_list = entity[cls.__name__]['fields']
    except KeyError as e:
        raise ConfigurationException('bad entity schema for class %s' % cls.__name__)
    for name, stype, doc in entity_list:
        cls._info[name] = (stype, doc)
        # adding private attribute for current field.
        setattr(cls, '_'+name, None)
        def fixer(name):
            """This function is used to fix the value in variable name"""
            def getx(self):
                return getattr(self, "_"+name)
            def setx(self, value):
                setattr(self, "_"+name, value)
            def delx(self):
                delattr(self, "_"+name)
            return getx, setx, delx
        getx, setx, delx = fixer(name)
        type_setx_funcs = {
            'int': int_check,
            'decimal': decimal_check,
            'string': string_check,
            # Different file format may use different date format. There is no
            # universal way to convert all formatted date string to a date class
            # So it is corresponding reader's responsibility to construct
            # correct date class.
            'date': identical,
        }
        # manually decorate setter with corresponding type's checking function
        setx = type_setx_funcs[stype](setx)
        # adding property for current field.
        setattr(cls, name, property(getx, setx, delx, doc))
    return cls

# Entity classes are declared here
class Entity(object):
    """
    Base class of entity classes. This class is designed as a broker between
    reader classes and writer classes. It also provides common methods.
    """
    def get_field_list(self):
        """
        Return a list of triple which contains fields of internal
        claim classes.
        Reader should set up these fields according to its
        supporting format of record, via setattr etc.
        Writer could use this list to keep track of fields needed to be exported
        """
        # write out each fields which record on schema.py
        try:
            entity_dict = entity[self.__class__.__name__]
        except KeyError as e:
            raise DataWriterException('No entity class schema for class %s' % type_class)
        try:
            field_list = entity_dict['fields']
        except KeyError as e:
            raise DataWriterException('No entity class fields schema for class %s' % type_class)
        return field_list
    def typeof(self, fieldname):
        """Return a string of type for `fieldname`"""
        # public function for users of entity classes.
        # the _info is construct by subclass
        return self._info[fieldname][0]

@entity_Class
class BeneficiarySummary(Entity):pass

@entity_Class
class Testing(Entity):pass

@entity_Class
class CarrierClaim(Entity):pass

@entity_Class
class InpatientClaim(Entity):pass

@entity_Class
class OutpatientClaim(Entity):pass

@entity_Class
class PrescriptionEvent(Entity):pass

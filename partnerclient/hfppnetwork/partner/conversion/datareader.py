##
# Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
##

"""
Abstract interface of reader class. Clarifying interface and duty of reader classes.
"""
__author__ = 'Easyhard'
__version__ = '1.0'

class DataReader(object):
    """
    Abstract interface of reader class.
    A reader class should be able to construct new instance of class Entity's
    subclass, based on external data source.
    """

    def next_entity(self):
        """This method should return a new, constructed instance of entity class
        each time called, fill all field listed in entity.get_field_list.
        Return None if the external data source has been ran out.
        This method must be implemented for any subclass of reader"""
        raise NotImplementedError()

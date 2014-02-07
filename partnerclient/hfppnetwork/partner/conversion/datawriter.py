##
# Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
##

"""
Abstract interface of writer class. Clarifying interface and duty of writer classes.
"""
__author__ = 'Easyhard'
__version__ = '1.0'

class DataWriter(object):
    """
    Abstract interface of writer class.
    A writer class should be able to export internal representation, i.e,
    subclass of Entity Class, as external format like XML, CSV, etc.
    """

    def start(self):
        """This method gives writer class a chance for initiation. It will
        be called at the beginning of exporting."""
        raise NotImplementedError()

    def end(self):
        """
        Notify that writing is finished. Writer class should finish any unfinished
        job in this method.
        """
        raise NotImplementedError()

    def write_entity(self, entity):
        """
        Each time this method is called, writer should export all fields returning
        from entity.get_field_list() correctly.
        """
        raise NotImplementedError()

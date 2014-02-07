##
# Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
##

"""
XML Writer

This class is able to convert entity classes into XML.
"""
__author__ = 'Easyhard'
__version__ = '1.0'

from conversion.exception import DataWriterException
# Using sax's XMLGenerator rather than etree because etree can not
# generating the xml on the fly. In another word, etree need to construct
# the whole dom tree and then generating XML, which is memory consuming.
from xml.sax.saxutils import XMLGenerator
from xml.sax.xmlreader import AttributesNSImpl
from datetime import date

from conversion.datawriter import DataWriter

class XMLPrettyGenerator(XMLGenerator):
    """
    Based on XMLGenerator and make it generating pretty format XML.
    """
    # recording the current indent level
    indent_level = 0
    # what to use to make indent
    indent_string = "    "
    # Whether the last action is ending an element
    last_end_element = False
    # Whether it is the first element written
    first_element = True
    def startElementNS(self, name, qname, attrs):
        """
        Begin of a Element with NS. Try to add indent and CR for pretty print.
        """
        if self.first_element:
            # Do not need a new line, since the <?xml...> line is generated.
            self.first_element = False
        else:
            self._write('\n')
        self._write(self.indent_level * self.indent_string)
        self.indent_level = self.indent_level + 1
        self.last_end_element = False
        super().startElementNS(name, qname, attrs)

    def endElementNS(self, name, qname):
        self.indent_level = self.indent_level - 1
        if self.last_end_element:
            # The last action is ending an element, so this ending tag
            # should begin a new line.
            self._write('\n' + self.indent_level * self.indent_string)
        self.last_end_element = True
        super().endElementNS(name, qname)

# Data struct for helping XMLWriter.
# For each type of entity, it records the root tag of the XML document.
xml = {
    'OutpatientClaim': {
        'root_tag': 'OutpatientClaims'
    },
    'InpatientClaim': {
        'root_tag': 'InpatientClaims',
    },
    'CarrierClaim': {
        'root_tag': 'CarrierClaims',
    },
    'BeneficiarySummary': {
        'root_tag': 'BeneficiarySummaries',
    },
    'PrescriptionEvent': {
        'root_tag': 'PrescriptionEvents',
    },
    'Testing': {
        'root_tag': 'Testings',
    },
}

class XMLWriter(DataWriter):
    """
    Writer for generating XML format output.
    """
    def __init__(self, file_object, type_class):
        """
        file_object: a file object which opens with 'w' mode,
        generated xml will be written into it.
        type_class: type's name of the entity class which is writing.
        """
        self._file_object = file_object
        self._type_class = type_class
        self._writer = XMLPrettyGenerator(file_object, "UTF-8")
        # get root tag of the xml document of the corresponding entity type.
        try:
            xmldict = xml[type_class]
        except KeyError as e:
            raise DataWriterException('No xml schema for class %s' % type_class)
        try:
            self._root = xmldict['root_tag']
        except KeyError as e:
            raise DataWriterException('No xml root tag schema for class %s' % type_class)

    def start(self):
        """
        Start to writer out the XML.
        """
        self._writer.startDocument()
        attrs = AttributesNSImpl({}, {})
        self._writer.startElementNS((None, self._root), self._root, attrs)

    def end(self):
        """
        Notify that writing is finished. The file object will not be closed.
        It is up to the caller to close it.
        """
        self._writer.endElementNS((None, self._root), self._root)
        self._writer.endDocument()

    def write_entity(self, entity):
        """
        Write a entity out to the XML.
        """
        # default value for None
        type_default_values = {
            'int': -1,
            'decimal': -1.0,
            'string': '',
            'date': date.max.isoformat()
        }
        xmlattrs = AttributesNSImpl({}, {})
        self._writer.startElementNS((None, self._type_class), self._type_class, xmlattrs)
        field_list = entity.get_field_list()

        for field_name, stype, doc in field_list:
            self._writer.startElementNS((None, field_name), field_name, xmlattrs)
            # same as entity_attr = entity.`field_name`
            entity_attr = getattr(entity, field_name)
            attr_type = entity.typeof(field_name)
            if entity_attr == None:
                self._writer.characters(str(type_default_values[attr_type]))
            else:
                if attr_type == 'date':
                    # isoformat is YYYY-MM-DD.
                    self._writer.characters(entity_attr.isoformat())
                else:
                    self._writer.characters(str(entity_attr))
            self._writer.endElementNS((None, field_name), field_name)
        self._writer.endElementNS((None, self._type_class), self._type_class)

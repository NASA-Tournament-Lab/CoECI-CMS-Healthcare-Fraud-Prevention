##
# Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
##

"""
Different Data format converter. Check function convert for details.
"""
__author__ = 'Easyhard'
__version__ = '1.0'

from conversion.exception import DataConverterException

def convert(reader, writer):
    """
    Convert the content via reader and writer.
    `reader` should be subclass of DataReader,
    `writer` should be subclass of DataWriter,
    Currently only CSVReader and XMLWriter are implemented.
    """
    writer.start()
    entity = reader.next_entity()
    while entity:
        writer.write_entity(entity)
        entity = reader.next_entity()
    writer.end()

def csv2xml(infile_path, outfile_path, filetype):
    """Convert csv file to xml file. Take input file and output file's
    filepath and file's type as arguments"""
    try:
        infile = open(infile_path)
        outfile = open(outfile_path, 'w')
        csvobj2xmlobj(infile, outfile, filetype)
    except IOError as e:
        raise DataConverterException('Cannot open file')
    finally:
        infile.close()
        outfile.close()

def csvobj2xmlobj(infile, outfile, filetype):
    """Convert csv file to xml file. Take input file and output file's
    file object and file's type as arguments"""
    from conversion.csvreader import CSVReader
    from conversion.xmlwriter import XMLWriter
    reader = CSVReader(infile, filetype)
    writer = XMLWriter(outfile, filetype)
    writer.start()
    entity = reader.next_entity()
    while entity != None:
        writer.write_entity(entity)
        entity = reader.next_entity()
    writer.end()


if __name__ == "__main__":
    import sys
    bad_params = False
    try:
        infile_path = sys.argv[2]
        outfile_path = sys.argv[3]
        filetype = sys.argv[1]
        csv2xml(infile_path, outfile_path, filetype)
    except IndexError as e:
        print("Please do it as following. Usage:")
        print("%s <FILE_TYPE> <PATH_TO_CSV_FILE> <PATH_TO_XML_FILE>" % sys.argv[0])
        print("")
        # use this to avoid raising exception during handling the current one.
        # Python will print some confusing information for end-users.
        bad_params = True
    if bad_params:
        raise DataConverterException('bad params')

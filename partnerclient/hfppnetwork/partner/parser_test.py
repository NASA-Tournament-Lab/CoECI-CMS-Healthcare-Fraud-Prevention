#!/usr/bin/env python3
"""
Copyright (C) 2010 - 2013 TopCoder Inc., All Rights Reserved.

This module defines the test for the parser.

@version 1.0 (Healthcare Fraud Prevention - Query Parsing and Query Generation)
@author: TCSASSEMBLER
"""
import os
import random
import sys
from query.parser import Parser, ParserError

def random_test():
    """
    Randomly generate positive cases and test against the parser.
    The parser should accept all the generated cases.
    """
    times = int(sys.argv[2])
    print("Randomly test " + str(times) + " times")

    gen = RandomQueryGenerator(10)
    parser = Parser()
    success = 0

    for i in range(times):
        max_depth = random.randint(1, 5)
        random_query = gen.query(max_depth)
        try:
            parse_tree = parser.parse(random_query)
            print("Success, length: " + str(len(random_query)))
            success += 1
        except ParserError:
            print("Failure: " + random_query)

    print("End of test, " + str(success) + "/" + str(times) + " passed")

def positive_test():
    """
    Read from valid_queries.txt line by line and test against the parser.
    The parser should accept all the queries.
    """
    dirname = os.path.dirname(os.path.realpath(__file__))
    filename = os.path.join(dirname, "valid_queries.txt")
    with open(filename) as f:
        parser = Parser()
        success = 0
        times = 0
        for line in f.readlines():
            times += 1
            try:
                parse_tree = parser.parse(line.strip())
                print("Success, length: " + str(len(line.strip())))
                print(repr(parse_tree))
                success += 1
            except ParserError:
                print("Failure: " + line.strip())
        print("End of test, " + str(success) + "/" + str(times) + " passed")

def negative_test():
    """
    Read from invalid_queries.txt line by line and test against the parser.
    The parser should reject all the queries.
    """
    dirname = os.path.dirname(os.path.realpath(__file__))
    filename = os.path.join(dirname, "invalid_queries.txt")
    with open(filename) as f:
        parser = Parser()
        success = 0
        times = 0
        for line in f.readlines():
            times += 1
            try:
                parse_tree = parser.parse(line.strip())
                print("Failure: " + line.strip())
                print(repr(parse_tree))
            except ParserError:
                print("Success, length: " + str(len(line.strip())))
                success += 1
        print("End of test, " + str(success) + "/" + str(times) + " passed")

if __name__ == "__main__":
    category = "positive"
    if category == "random":
        random_test()
    elif category == "positive":
        positive_test()
    elif category == "negative":
        negative_test()
    else:
        print("First argument should be random, positive or negative.")

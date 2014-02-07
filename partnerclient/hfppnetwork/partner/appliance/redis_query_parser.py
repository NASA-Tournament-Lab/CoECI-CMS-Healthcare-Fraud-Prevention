"""
Copyright (C) 2010 - 2013 TopCoder Inc., All Rights Reserved.

This module defines the Parser class and the ParserError class.  See their
documentation below for details.

@version 1.0 (Healthcare Fraud Prevention - Partner Database Integration Assembly)
@author: TCSASSEMBLER
"""
import os, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from redis_query_treenode import *

class RedisQueryParserError(Exception):
    """
    Indicates a syntax error in the program being parsed.
    Also used by the parser member methods to reduce if statements for
    checking failed cases, since exceptions propagate automatically.
    """
    pass

class RedisQueryParser:
    """
    This is a recursive descent parser.  For each production rule in the
    grammar, there is a member method in this class, which consumes the input
    program, and either returns a syntax tree node as the succesfully parsed
    result, or raises ParserError to indicate the parsing cannot be completed.

    This implementation of Parser is NOT thread safe, since it keeps internal
    mutable states `self.program`, which is changed every time `parse` method
    is called, and `self.pointer`, which is changed over the time of parsing.
    You can reuse the same Parser instance in a single thread, and should use
    multiple instances for multiple threads.
    """
    # Edit characters to adapt to new set of characters.
    characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._*%-"

    # Edit operators to adapt to new set of operators.  Sort them by length.
    operators = ["<=", ">=", "=", "<", ">"]

    def __init__(self, claimType):
        """
        Pass in claimType to create a specialized parser.
        """
        self.claimType = claimType

    def parse(self, program):
        """
        Pass in the program to parse as a string parameter `program`.  The
        parser will scan this program string character by character using the
        integer index `pointer` defined below.
        """
        self.program = program
        self.pointer = 0
        query = self.parse_query()
        self.skip_spaces()
        if self.pointer < len(self.program):
            raise RedisQueryParserError
        return query

    def peek(self):
        """
        This is a helper function, which peeks the next character indicated by
        `self.pointer`.  Return the empty string "" if no more characters are
        available (see the documentation of skip_spaces() for a reason).
        """
        if self.pointer >= len(self.program):
            return ""
        else:
            return self.program[self.pointer]

    def skip_spaces(self):
        """
        This is a helper function, which skips whitespaces and increases the
        pointer at the same time.  This is used by other parsing methods only.
        Note that we do not need to compare the pointer with len(self.program)
        because peek() returns the empty string "" when the input is totally
        consumed, and "".isspace() returns False.
        """
        while self.peek().isspace():
            self.pointer += 1

    def accept(self, token):
        """
        Pass in the token string to be accepted.  Look ahead from the position
        indicated by `self.pointer` and compare the program string with the
        token string.  Return True if matched, or False otherwise.  Ignore
        whitespaces when necessary (commonly at the beginning).
        """
        self.skip_spaces()

        if self.pointer + len(token) > len(self.program):
            return False

        for i in range(len(token)):
            if token[i] != self.program[self.pointer + i]:
                return False

        self.pointer += len(token)
        return True

    def expect(self, token):
        """
        This is a helper function, which takes as the parameter the token
        string to be accepted, and raises ParserError if not accepted.
        """
        if not self.accept(token):
            raise RedisQueryParserError

    def parse_query(self):
        """
        Grammar:
                Query -> Clause | Group | And_sequence | Or_sequence

        Both Group and sequences are parsed in parse_sequence.  See its
        documentation for details.
        """
        try:
            return Query(self.parse_clause())
        except RedisQueryParserError:
            return Query(self.parse_sequence())

    def parse_sequence(self):
        """
        Grammar:
                And_sequence -> Group "AND" And_sequence_tail
                Or_sequence  -> Group "OR"  Or_sequence_tail

        And_sequence and Or_sequence are parsed together with Group, since
        And_sequence/Or_sequence is just a sequence of Groups joined by AND/OR.
        Such structures are parsed together in a recursive descent parser.
        """
        group = self.parse_group()
        if self.accept("AND"):
            return AndSequence(group, self.parse_and_sequence_tail())
        elif self.accept("OR"):
            return OrSequence(group, self.parse_or_sequence_tail())
        else:
            return group

    def parse_and_sequence_tail(self):
        """
        Grammar:
                And_sequence_tail -> Group | Group "AND" And_sequence_tail
        """
        group = self.parse_group()
        if self.accept("AND"):
            return AndSequenceTail(group, self.parse_and_sequence_tail())
        else:
            return AndSequenceTail(group)

    def parse_or_sequence_tail(self):
        """
        Grammar:
                Or_sequence_tail  -> Group | Group "OR" Or_sequence_tail
        """
        group = self.parse_group()
        if self.accept("OR"):
            return OrSequenceTail(group, self.parse_or_sequence_tail())
        else:
            return OrSequenceTail(group)

    def parse_group(self):
        """
        Grammar:
                Group -> Affirmative_group | Negated_group
        """
        try:
            return Group(self.parse_affirmative_group())
        except RedisQueryParserError:
            return Group(self.parse_negated_group())

    def parse_affirmative_group(self):
        """
        Grammar:
                Affirmative_group -> "(" Query ")"
        """
        self.expect("(")
        query = self.parse_query()
        self.expect(")")
        return AffirmativeGroup(query)

    def parse_negated_group(self):
        """
        Grammar:
                Negated_group -> "NOT" "(" Query ")"
        """
        self.expect("NOT")
        self.expect("(")
        query = self.parse_query()
        self.expect(")")
        return NegatedGroup(query)

    def parse_clause(self):
        """
        Grammar:
                Clause -> Attribute Operator Value
        """
        attribute = self.parse_attribute()
        operator = self.parse_operator()
        value = self.parse_value()
        return Clause(attribute, operator, value)

    def parse_attribute(self):
        """
        Attributes are column names.
        """
        for attribute in self.claimType.columns:
            if self.accept(attribute):
                return attribute
        raise RedisQueryParserError

    def parse_operator(self):
        """
        Operators are configured in self.operators.
        """
        for operator in self.operators:
            if self.accept(operator):
                return operator
        return RedisQueryParserError

    def parse_value(self):
        """
        Value is a series of non-space characters.
        """
        self.skip_spaces()
        if not self.is_character(self.peek()):
            raise RedisQueryParserError
        characters = ""
        while self.is_character(self.peek()):
            characters += self.peek()
            self.pointer += 1
        return characters

    def is_character(self, character):
        """
        Check whether parameter `character` is a character.
        """
        if len(character) != 1:
            return False
        try:
            self.characters.index(character)
            return True
        except ValueError:
            return False

"""
Copyright (C) 2010 - 2013 TopCoder Inc., All Rights Reserved.

This module defines the Parser class and the ParserError class.  See their
documentation below for details.

@version 1.0 (Healthcare Fraud Prevention - Query Parsing and Query Generation)
@author: TCSASSEMBLER
"""
from query.parser_config import ParserConfig
from query.treenode import *

class ParserError(Exception):
    """
    Indicates a syntax error in the program being parsed.
    Also used by the parser member methods to reduce if statements for
    checking failed cases, since exceptions propagate automatically.
    """
    pass

class Parser(ParserConfig):
    """
    This is a recursive descent parser.  For each production rule in the
    grammar, there is a member method in this class, which consumes the input
    program, and either returns a syntax tree node as the succesfully parsed
    result, or raises ParserError to indicate the parsing cannot be completed.

    This class extends ParserConfig to access configurable details of the parser
    including numerical/logical/textual attribute names and CMS codes, numerical
    operators, digits, and characters.

    This implementation of Parser is NOT thread safe, since it keeps internal
    mutable states `self.program`, which is changed every time `parse` method
    is called, and `self.pointer`, which is changed over the time of parsing.
    You can reuse the same Parser instance in a single thread, and should use
    multiple instances for multiple threads.
    """
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
            raise ParserError
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
            raise ParserError

    def parse_query(self):
        """
        Grammar:
                Query -> Clause | Group | And_sequence | Or_sequence

        Both Group and sequences are parsed in parse_sequence.  See its
        documentation for details.
        """
        try:
            return Query(self.parse_clause())
        except ParserError:
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
        except ParserError:
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
                Clause -> Numerical_clause | Logical_clause | Textual_clause
        """
        try:
            return Clause(self.parse_numerical_clause())
        except ParserError:
            try:
                return Clause(self.parse_logical_clause())
            except ParserError:
                return Clause(self.parse_textual_clause())

    def parse_numerical_clause(self):
        """
        Grammar:
                Numerical_clause ->
                    Numerical_attribute Numerical_operator Numerical_value
                  | Numerical_attribute "between" Numerical_value "to" Numerical_value
        """
        attribute = self.parse_numerical_attribute()
        if self.accept("between"):
            value1 = self.parse_numerical_value()
            self.expect("to")
            value2 = self.parse_numerical_value()
            return BetweenClause(attribute, value1, value2)
        else:
            operator = self.parse_numerical_operator()
            value = self.parse_numerical_value()
            return NumericalClause(attribute, operator, value)

    def parse_numerical_attribute(self):
        """
        Grammar:
                Numerical_attribute -> "County code" | "State code" | ...
        """
        for attribute, code in self.numerical_attributes:
            if self.accept(attribute):
                return NumericalAttribute(attribute, code)
        raise ParserError

    def parse_numerical_operator(self):
        """
        Grammar:
                Numerical_operator -> "equal to" | "less than"
                                    | "less than or equal to" | "greater than"
                                    | "greater than or equal to"
        """
        for operator, code in self.numerical_operators:
            if self.accept(operator):
                return NumericalOperator(operator, code)
        raise ParserError

    def parse_numerical_value(self):
        """
        Grammar:
                Numerical_value -> Integer_value | Negative_integer_value
                                 | Real_value | Negative_real_value
                Negative_integer_value -> "-" Integer_value
                Negative_real_value -> "-" Real_value

        Look ahead one character for the minus sign "-" to determine whether it
        is a negative number.  Use to_negative of RealValue and IntegerValue to
        improve code structure and readability.
        """
        if self.accept("-"):
            return self.parse_positive_value().to_negative()
        else:
            return self.parse_positive_value()

    def parse_positive_value(self):
        """
        Grammar:
                Real_value -> Integer_value "." Integer_value

        Fall through to Integer_value if the dot "." is not there.
        """
        integer_value = self.parse_integer_value()
        if self.accept("."):
            return RealValue(integer_value, self.parse_integer_value())
        else:
            return integer_value

    def parse_integer_value(self):
        """
        Grammar:
                Integer_value -> Digit+
                Digit -> "0" | "1" | "2" | "3" | ... | "9"

        This function does the lexical analysis for integer values.  This is
        different from other parse_xxx methods because it operates on the input
        program directly via peek() and raises ParserError explicitly.
        """
        self.skip_spaces()
        if not self.is_digit(self.peek()):
            raise ParserError
        digits = ""
        while self.is_digit(self.peek()):
            digits += self.peek()
            self.pointer += 1
        return IntegerValue(digits)

    def is_digit(self, character):
        """
        Check whether parameter `character` is a digit.
        """
        if len(character) != 1:
            return False
        try:
            self.digits.index(character)
            return True
        except ValueError:
            return False

    def parse_logical_clause(self):
        """
        Grammar:
                Logical_clause -> Logical_attribute "is" Logical_value

        Although currently the only valid operator is "is", we still parse the
        operator as a standalone constituent for extension in the future.
        """
        attribute = self.parse_logical_attribute()
        operator = self.parse_logical_operator()
        value = self.parse_logical_value()
        return LogicalClause(attribute, operator, value)

    def parse_logical_attribute(self):
        """
        Grammar:
                Logical_attribute -> "End stage renal disease indicator"
                                   | "Chronic condition: heart failure" | ...
        """
        for attribute, code in self.logical_attributes:
            if self.accept(attribute):
                return LogicalAttribute(attribute, code)
        raise ParserError

    def parse_logical_operator(self):
        """
        Although currently the only valid operator is "is", we still parse the
        operator as a standalone constituent for extension in the future.  The
        operator does not have its own syntax tree node, but is only a vanilla
        Python string.
        """
        self.expect("is")
        return "is"

    def parse_logical_value(self):
        """
        Grammar:
                Logical_value -> "true" | "false"
        """
        if self.accept("true"):
            return LogicalValue("true")
        if self.accept("false"):
            return LogicalValue("false")
        raise ParserError

    def parse_textual_clause(self):
        """
        Grammar:
                Textual_clause ->
                    Textual_attribute Textual_operator Textual_value
                  | Textual_attribute "between" Textual_value "to" Textual_value
        """
        attribute = self.parse_textual_attribute()
        if self.accept("between"):
            value1 = self.parse_textual_value()
            self.expect("to")
            value2 = self.parse_textual_value()
            return BetweenClause(attribute, value1, value2)
        else:
            operator = self.parse_textual_operator()
            value = self.parse_textual_value()
            return TextualClause(attribute, operator, value)

    def parse_textual_attribute(self):
        """
        Grammar:
                Textual_attribute -> "Beneficiary code" | "Date of birth" | ...
        """
        for attribute, code in self.textual_attributes:
            if self.accept(attribute):
                return TextualAttribute(attribute, code)
        raise ParserError

    def parse_textual_operator(self):
        """
        Grammar:
                Textual_operator -> "is" | "matches"

        If you want to add more operators, make sure you take the longest
        match, or sort the operators beforehand.
        """
        if self.accept("is"):
            return TextualOperator("is")
        if self.accept("matches"):
            return TextualOperator("matches")
        raise ParserError

    def parse_textual_value(self):
        """
        Grammar:
                Textual_value -> Character+
                Character -> "a" | "b" | ...

        This function does the lexical analysis for textual values.  This is
        different from other parse_xxx methods because it operates on the input
        program directly via peek() and raises ParserError explicitly.
        """
        self.skip_spaces()
        if not self.is_character(self.peek()):
            raise ParserError
        characters = ""
        while self.is_character(self.peek()):
            characters += self.peek()
            self.pointer += 1
        return TextualValue(characters)

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

if __name__ == "__main__":
    import sys
    parser = Parser()
    try:
        parse_tree = parser.parse(str(sys.argv[1]))
        print(repr(parse_tree))
    except ParserError:
        print("syntax error")

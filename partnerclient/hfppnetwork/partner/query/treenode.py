"""
Copyright (C) 2010 - 2013 TopCoder Inc., All Rights Reserved.

This module contains the definitions of syntax tree nodes.  Each node class
defines the __repr__ method to support repr() actions on these node objects
for debugging purposes, which converts a syntax tree to a linear form.

In order to support future extensions to the syntax tree, we employ the visitor
pattern in this module, so that each node class defines the accept method, which
takes a visitor object as the parameter.  Because Python cannot dispatch method
calls with respect to parameter types (as in Java), we call different visit_xxx
methods instead of one single visit method.

@version 1.0 (Healthcare Fraud Prevention - Query Parsing and Query Generation)
@author: TCSASSEMBLER
"""
class Query:
    """
    Query -> Clause | Group | And_sequence | Or_sequence
    """
    def __init__(self, query):
        self.query = query

    def __repr__(self):
        return "Query(" + repr(self.query) + ")"

    def accept(self, visitor):
        return visitor.visit_query(self)

class AndSequence:
    """
    And_sequence -> Group "AND" And_sequence_tail
    """
    def __init__(self, group, tail):
        self.group = group
        self.tail = tail

    def __repr__(self):
        return "AndSequence(" + repr(self.group) + "," + repr(self.tail) + ")"

    def accept(self, visitor):
        return visitor.visit_and_sequence(self)

class AndSequenceTail:
    """
    And_sequence_tail -> Group | Group "AND" And_sequence_tail
    """
    def __init__(self, group, tail=None):
        self.group = group
        self.tail = tail

    def __repr__(self):
        if self.tail is None:
            return "AndSequenceTail(" + repr(self.group) + ")"
        else:
            return "AndSequenceTail(" + repr(self.group) + "," + \
                                        repr(self.tail) + ")"

    def accept(self, visitor):
        return visitor.visit_and_sequence_tail(self)

class OrSequence:
    """
    Or_sequence -> Group "OR" Or_sequence_tail
    """
    def __init__(self, group, tail):
        self.group = group
        self.tail = tail

    def __repr__(self):
        return "OrSequence(" + repr(self.group) + "," + repr(self.tail) + ")"

    def accept(self, visitor):
        return visitor.visit_or_sequence(self)

class OrSequenceTail:
    """
    Or_sequence_tail -> Group | Group "OR" Or_sequence_tail
    """
    def __init__(self, group, tail=None):
        self.group = group
        self.tail = tail

    def __repr__(self):
        if self.tail is None:
            return "OrSequenceTail(" + repr(self.group) + ")"
        else:
            return "OrSequenceTail(" + repr(self.group) + "," + \
                                       repr(self.tail) + ")"

    def accept(self, visitor):
        return visitor.visit_or_sequence_tail(self)

class Group:
    """
    Group -> Affirmative_group | Negated_group
    """
    def __init__(self, group):
        self.group = group

    def __repr__(self):
        return "Group(" + repr(self.group) + ")"

    def accept(self, visitor):
        return visitor.visit_group(self)

class AffirmativeGroup:
    """
    Affirmative_group -> "(" Query ")"
    """
    def __init__(self, query):
        self.query = query

    def __repr__(self):
        return "AffirmativeGroup(" + repr(self.query) + ")"

    def accept(self, visitor):
        return visitor.visit_affirmative_group(self)

class NegatedGroup:
    """
    Negated_group -> "NOT" "(" Query ")"
    """
    def __init__(self, query):
        self.query = query

    def __repr__(self):
        return "NegatedGroup(" + repr(self.query) + ")"

    def accept(self, visitor):
        return visitor.visit_negated_group(self)

class Clause:
    """
    Clause -> Numerical_clause | Logical_clause | Textual_clause
    """
    def __init__(self, clause):
        self.clause = clause

    def __repr__(self):
        return "Clause(" + repr(self.clause) + ")"

    def accept(self, visitor):
        return visitor.visit_clause(self)

class NumericalClause:
    """
    Numerical_clause -> Numerical_attribute Numerical_operator Numerical_value
    """
    def __init__(self, attribute, operator, value):
        self.attribute = attribute
        self.operator = operator
        self.value = value

    def __repr__(self):
        return "NumericalClause(" + repr(self.attribute) + "," \
                                  + repr(self.operator) + "," \
                                  + repr(self.value) + ")"

    def accept(self, visitor):
        return visitor.visit_numerical_clause(self)

class NumericalAttribute:
    """
    Numerical_attribute -> "County code" | "State code" | ...
    """
    def __init__(self, attribute, code):
        self.attribute = attribute
        self.code = code

    def __repr__(self):
        return "NumericalAttribute(" + repr(self.attribute) + ")"

    def accept(self, visitor):
        return visitor.visit_numerical_attribute(self)

class NumericalOperator:
    """
    Numerical_operator -> "equal to" | "less than" | "less than or equal to"
                        | "greater than" | "greater than or equal to"
    """
    def __init__(self, operator, code):
        self.operator = operator
        self.code = code

    def __repr__(self):
        return "NumericalOperator(" + repr(self.operator) + ")"

    def accept(self, visitor):
        return visitor.visit_numerical_operator(self)

class NumericalValue:
    """
    Numerical_value -> Integer_value | Negative_integer_value
                     | Real_value | Negative_real_value
    """
    def __init__(self, value):
        self.value = value

    def __repr__(self):
        return "NumericalValue(" + repr(self.value) + ")"

    def accept(self, visitor):
        return visitor.visit_numerical_value(self)

class NegativeIntegerValue:
    """
    Negative_integer_value -> "-" Integer_value
    """
    def __init__(self, integer_value):
        self.integer_value = integer_value

    def __repr__(self):
        return "NegativeIntegerValue(" + repr(self.integer_value) + ")"

    def accept(self, visitor):
        return visitor.visit_negative_integer_value(self)

class NegativeRealValue:
    """
    Negative_real_value -> "-" Real_value
    """
    def __init__(self, real_value):
        self.real_value = real_value

    def __repr__(self):
        return "NegativeRealValue(" + repr(self.real_value) + ")"

    def accept(self, visitor):
        return visitor.visit_negative_real_value(self)

class RealValue:
    """
    Real_value -> Integer_value "." Integer_value
    """
    def __init__(self, integer_part, fractional_part):
        self.integer_part = integer_part
        self.fractional_part = fractional_part

    def __repr__(self):
        return "RealValue(" + repr(self.integer_part) + "," \
                            + repr(self.fractional_part) + ")"

    def to_negative(self):
        """
        This is a helper function, which wraps the current value as the
        negative counterpart.  This is used in Parser.parse_numerical_value for
        better code structure and readability.
        """
        return NegativeRealValue(self)

    def accept(self, visitor):
        return visitor.visit_real_value(self)

class IntegerValue:
    """
    Integer_value -> Digit+
    Digit -> "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
    """
    def __init__(self, digits):
        self.digits = digits

    def __repr__(self):
        return "IntegerValue(" + repr(self.digits) + ")"

    def to_negative(self):
        """
        This is a helper function, which wraps the current value as the
        negative counterpart.  This is used in Parser.parse_numerical_value for
        better code structure and readability.
        """
        return NegativeIntegerValue(self)

    def accept(self, visitor):
        return visitor.visit_integer_value(self)

class LogicalClause:
    """
    Logical_clause -> Logical_attribute "is" Logical_value

    Although currently the only valid operator is "is", we still pass in an
    operator as the parameter for extension in the future.
    """
    def __init__(self, attribute, operator, value):
        self.attribute = attribute
        self.operator = operator
        self.value = value

    def __repr__(self):
        return "LogicalClause(" + repr(self.attribute) + "," \
                                + repr(self.operator) + "," \
                                + repr(self.value) + ")"

    def accept(self, visitor):
        return visitor.visit_logical_clause(self)

class LogicalAttribute:
    """
    Logical_attribute -> "End stage renal disease indicator" | ...
    """
    def __init__(self, attribute, code):
        self.attribute = attribute
        self.code = code

    def __repr__(self):
        return "LogicalAttribute(" + repr(self.attribute) + ")"

    def accept(self, visitor):
        return visitor.visit_logical_attribute(self)

class LogicalValue:
    """
    Logical_value -> "true" | "false"
    """
    def __init__(self, value):
        self.value = value

    def __repr__(self):
        return "LogicalValue(" + repr(self.value) + ")"

    def accept(self, visitor):
        return visitor.visit_logical_value(self)

class TextualClause:
    """
    Textual_clause -> Textual_attribute Textual_operator Textual_value
    """
    def __init__(self, attribute, operator, value):
        self.attribute = attribute
        self.operator = operator
        self.value = value

    def __repr__(self):
        return "TextualClause(" + repr(self.attribute) + "," \
                                + repr(self.operator) + "," \
                                + repr(self.value) + ")"

    def accept(self, visitor):
        return visitor.visit_textual_clause(self)

class TextualAttribute:
    """
    Textual_attribute -> "Beneficiary code" | "Date of birth" | ...
    """
    def __init__(self, attribute, code):
        self.attribute = attribute
        self.code = code

    def __repr__(self):
        return "TextualAttribute(" + repr(self.attribute) + ")"

    def accept(self, visitor):
        return visitor.visit_textual_attribute(self)

class TextualOperator:
    """
    Textual_operator -> "is" | "matches" | "between"
    """
    def __init__(self, operator):
        self.operator = operator

    def __repr__(self):
        return "TextualOperator(" + repr(self.operator) + ")"

    def accept(self, visitor):
        return visitor.visit_textual_operator(self)

class TextualValue:
    """
    Textual_value -> Character+
    Character -> "a" | "b" | ...
    """
    def __init__(self, characters):
        self.characters = characters

    def __repr__(self):
        return "TextualValue(" + repr(self.characters) + ")"

    def accept(self, visitor):
        return visitor.visit_textual_value(self)

class BetweenClause:
    """
    For the updated grammar case: attribute "between" value "to" value
    """
    def __init__(self, attribute, value1, value2):
        self.attribute = attribute
        self.value1 = value1
        self.value2 = value2

    def __repr__(self):
        return "BetweenClause(" + repr(self.attribute) + "," \
                                + repr(self.value1) + "," \
                                + repr(self.value2) + ")"

    def accept(self, visitor):
        return visitor.visit_between_clause(self)

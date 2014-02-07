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

@version 1.0 (Healthcare Fraud Prevention - Partner Database Integration Assembly)
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
    Clause -> Attribute Operator Value
    """
    def __init__(self, attribute, operator, value):
        self.attribute = attribute
        self.operator = operator
        self.value = value

    def __repr__(self):
        return "Clause(" + repr(self.attribute) + "," \
                         + repr(self.operator) + "," \
                         + repr(self.value) + ")"

    def accept(self, visitor):
        return visitor.visit_clause(self)

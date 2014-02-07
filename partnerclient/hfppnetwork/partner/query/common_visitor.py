"""
Copyright (C) 2010 - 2013 TopCoder Inc., All Rights Reserved.

This module includes the CommonVisitor class.

@version 1.0 (Healthcare Fraud Prevention - Query Parsing and Query Generation)
@author: TCSASSEMBLER
"""
class CommonVisitor:
    """
    This class defines common visitor methods (visit_xxx).  Specific visitors
    can extend this class if some of the visit_xxx methods are not going to be
    implemented, or copy the source code of this class to start coding quickly.
    """
    def visit_query(self, query):
        pass

    def visit_and_sequence(self, sequence):
        pass

    def visit_and_sequence_tail(self, tail):
        pass

    def visit_or_sequence(self, sequence):
        pass

    def visit_or_sequence_tail(self, tail):
        pass

    def visit_group(self, group):
        pass

    def visit_affirmative_group(self, group):
        pass

    def visit_negated_group(self, group):
        pass

    def visit_clause(self, clause):
        pass

    def visit_numerical_clause(self, clause):
        pass

    def visit_numerical_attribute(self, attribute):
        pass

    def visit_numerical_operator(self, operator):
        pass

    def visit_numerical_value(self, value):
        pass

    def visit_negative_integer_value(self, value):
        pass

    def visit_negative_real_value(self, value):
        pass

    def visit_real_value(self, value):
        pass

    def visit_integer_value(self, value):
        pass

    def visit_logical_clause(self, clause):
        pass

    def visit_logical_attribute(self, attribute):
        pass

    def visit_logical_value(self, value):
        pass

    def visit_textual_clause(self, clause):
        pass

    def visit_textual_attribute(self, attribute):
        pass

    def visit_textual_operator(self, operator):
        pass

    def visit_textual_value(self, value):
        pass

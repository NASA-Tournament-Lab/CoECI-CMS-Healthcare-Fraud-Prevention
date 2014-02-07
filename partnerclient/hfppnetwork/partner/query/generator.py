"""
Copyright (C) 2010 - 2013 TopCoder Inc., All Rights Reserved.

This module defines the SQL generator, which works according to the visitor
pattern, and converts a syntax tree to the SQLite 3 dialect.  Conversion
details are covered in method documentations.

@version 1.0 (Healthcare Fraud Prevention - Query Parsing and Query Generation)
@author: TCSASSEMBLER
"""
from query.common_visitor import CommonVisitor

class SQLGenerator(CommonVisitor):
    """
    This implementation of SQLGenerator is thread safe and performs read-only
    operations.  A single instance of this class can be used concurrently for
    multiple syntax trees and by multiple clients.
    """
    def visit(self, node):
        """
        Invoke this method to start generating SQL for node.
        """
        return node.accept(self)

    def visit_query(self, query):
        """
        Query -> Clause | Group | And_sequence | Or_sequence

        Parameter `query` is an instance of treenode.Query, which has one
        member `query`.
        """
        return query.query.accept(self)

    def visit_and_sequence(self, sequence):
        """
        And_sequence -> Group "AND" And_sequence_tail

        Parameter `sequence` is an instance of treenode.AndSequence, which has
        two members `group` and `tail`.
        """
        return sequence.group.accept(self) + " AND " + \
               sequence.tail.accept(self)

    def visit_and_sequence_tail(self, tail):
        """
        And_sequence_tail -> Group | Group "AND" And_sequence_tail

        Parameter `tail` is an instance of treenode.AndSequenceTail, which has
        two members `group` and `tail`, where `tail` could be None.
        """
        if tail.tail is None:
            return tail.group.accept(self)
        else:
            return tail.group.accept(self) + " AND " + tail.tail.accept(self)

    def visit_or_sequence(self, sequence):
        """
        Or_sequence -> Group "OR" Or_sequence_tail

        Parameter `sequence` is an instance of treenode.OrSequence, which has
        two members `group` and `tail`.
        """
        return sequence.group.accept(self) + " OR " + \
               sequence.tail.accept(self)

    def visit_or_sequence_tail(self, tail):
        """
        Or_sequence_tail -> Group | Group "OR" Or_sequence_tail

        Parameter `tail` is an instance of treenode.OrSequenceTail, which has
        two members `group` and `tail`, where `tail` could be None.
        """
        if tail.tail is None:
            return tail.group.accept(self)
        else:
            return tail.group.accept(self) + " OR " + tail.tail.accept(self)

    def visit_group(self, group):
        """
        Group -> Affirmative_group | Negated_group

        Parameter `group` is an instance of treenode.Group, which has one
        member `group`.
        """
        return group.group.accept(self)

    def visit_affirmative_group(self, group):
        """
        Affirmative_group -> "(" Query ")"

        Parameter `group` is an instance of treenode.AffirmativeGroup, which
        has one member `query`.
        """
        return "(" + group.query.accept(self) + ")"

    def visit_negated_group(self, group):
        """
        Negated_group -> "NOT" "(" Query ")"

        Parameter `group` is an instance of treenode.NegatedGroup, which has
        one member `query`.
        """
        return "NOT(" + group.query.accept(self) + ")"

    def visit_clause(self, clause):
        """
        Clause -> Numerical_clause | Logical_clause | Textual_clause

        Parameter `clause` is an instance of treenode.Clause, which has one
        member `clause`.
        """
        return clause.clause.accept(self)

    def visit_numerical_clause(self, clause):
        """
        Numerical_clause ->
                Numerical_attribute Numerical_operator Numerical_value

        Parameter `clause` is an instance of treenode.NumericalClause, which
        has three members `attribute`, `operator`, and `value`.
        """
        return clause.attribute.accept(self) + " " + \
               clause.operator.accept(self) + " " + \
               clause.value.accept(self)

    def visit_numerical_attribute(self, attribute):
        """
        Numerical_attribute -> "County code" | "State code" | ...

        Parameter `attribute` is an instance of treenode.NumericalAttribute,
        which has two members `attribute` and `code`.
        """
        return attribute.code

    def visit_numerical_operator(self, operator):
        """
        Numerical_operator -> "equal to" | "less than"
                            | "less than or equal to" | "greater than"
                            | "greater than or equal to"

        Parameter `operator` is an instance of treenode.NumericalOperator,
        which has two members `operator` and `code`.
        """
        return operator.code

    def visit_numerical_value(self, value):
        """
        Numerical_value -> Integer_value | Negative_integer_value
                           | Real_value | Negative_real_value

        Parameter `value` is an instance of treenode.NumericalValue, which has
        one member `value`.
        """
        return value.value.accept(self)

    def visit_negative_integer_value(self, value):
        """
        Negative_integer_value -> "-" Integer_value

        Parameter `value` is an instance of treenode.NegativeIntegerValue,
        which has one member `integer_value`.
        """
        return "-" + value.integer_value.accept(self)

    def visit_negative_real_value(self, value):
        """
        Negative_real_value -> "-" Real_value

        Parameter `value` is an instance of treenode.NegativeRealValue, which
        has one member `real_value`.
        """
        return "-" + value.real_value.accept(self)

    def visit_real_value(self, value):
        """
        Real_value -> Integer_value "." Integer_value

        Parameter `value` is an instance of treenode.RealValue, which has two
        members `integer_part` and `fractional_part`.
        """
        return value.integer_part.accept(self) + "." + \
               value.fractional_part.accept(self)

    def visit_integer_value(self, value):
        """
        Integer_value -> Digit+
        Digit -> "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"

        Parameter `value` is an instance of treenode.IntegerValue, which has
        one member `digits` of string type.  The parser guarantees that all the
        characters in `digits` are indeed numerical digits, so there is no need
        to perform any kind of escaping here.
        """
        return value.digits

    def visit_logical_clause(self, clause):
        """
        Logical_clause -> Logical_attribute "is" Logical_value

        Parameter `clause` is an instance of treenode.LogicalClause, which has
        three members `attribute`, `operator`, and `value`.  Although currently
        the only valid operator is "is", we still have the member `operator`
        for extension purposes in the future.  However, `operator` does not
        have its own syntax tree node, but is a vanilla Python string "is".
        """
        return clause.attribute.accept(self) + " " + clause.operator + " " + \
               clause.value.accept(self)

    def visit_logical_attribute(self, attribute):
        """
        Logical_attribute -> "End stage renal disease indicator" | ...

        Parameter `attribute` is an instance of treenode.LogicalAttribute,
        which has two members `attribute` and `code`.
        """
        return attribute.code

    def visit_logical_value(self, value):
        """
        Logical_value -> "true" | "false"

        Parameter `value` is an instance of treenode.LogicalValue, which has
        one member `value` of string type.

        According to the description of SQLite datatypes, which is available at
        http://www.sqlite.org/datatype3.html, SQLite does not have a separate
        Boolean storage class.  Instead, Boolean values are stored as integers
        0 (false) and 1 (true).
        """
        if value.value == "false":
            return "0"
        if value.value == "true":
            return "1"
        raise ValueError("unexpected logical value " + value.value)

    def visit_textual_clause(self, clause):
        """
        Textual_clause -> Textual_attribute Textual_operator Textual_value

        Parameter `clause` is an instance of treenode.TextualClause, which has
        three attributes `attribute`, `operator`, and `value`.
        """
        return clause.attribute.accept(self) + " " + \
               clause.operator.accept(self) + " " + \
               clause.value.accept(self)

    def visit_textual_attribute(self, attribute):
        """
        Textual_attribute -> "Beneficiary code" | "Date of birth" | ...

        Parameter `attribute` is an instance of treenode.TextualAttribute,
        which has two members `attribute` and `code`.
        """
        return attribute.code

    def visit_textual_operator(self, operator):
        """
        Textual_operator -> "is" | "matches"

        Parameter `operator` is an instance of treenode.TextualOperator, which
        has one member `operator` of string type.  "matches" is translated to
        "like" according to http://www.sqlite.org/lang_expr.html
        """
        if operator.operator == "is":
            return "="
        if operator.operator == "matches":
            return "LIKE"
        raise ValueError("unexpected textual operator " + operator.operator)

    def visit_textual_value(self, value):
        """
        According to the definition of the SQLite query language, which is
        available at http://www.sqlite.org/lang_expr.html, a string constant is
        formed by enclosing the string in single quotes ('), and a single quote
        within the string can be encoded by putting two single quotes in a row.
        C-style escapes using the backslash character are not supported because
        they are not standard SQL.

        However, other databases may support other string escape mechanisms.
        MySQL, for instance, also permits using the backslash character to
        escape a quote.  Pay attention to this when migrating to other systems.
        """
        return "'" + self.sqlite_quote_string(value.characters) + "'"

    def sqlite_quote_string(self, s):
        """
        This is a helper function, which escapes single quote characters in the
        given string parameter `s`.  Escaped result is returned via `r` below.
        """
        r = ""
        for c in s:
            if c == "'":
                r += "''"
            else:
                r += c
        return r

    def visit_between_clause(self, between):
        return between.attribute.accept(self) + " BETWEEN " + \
               between.value1.accept(self) + " AND " + \
               between.value2.accept(self)

if __name__ == "__main__":
    import sys
    from .parser import Parser, ParserError
    parser = Parser()
    generator = SQLGenerator()
    try:
        parse_tree = parser.parse(str(sys.argv[1]))
        print(generator.visit(parse_tree))
    except ParserError:
        print("syntax error")

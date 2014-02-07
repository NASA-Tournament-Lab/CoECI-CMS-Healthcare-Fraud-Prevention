"""
Copyright (C) 2010 - 2013 TopCoder Inc., All Rights Reserved.

This module executes queries against Redis persistence.

@version 1.0 (Healthcare Fraud Prevention - Partner Database Integration Assembly)
@author: TCSASSEMBLER
"""

class RedisQueryExecutor:
    """
    This implementation of query executor is thread safe.
    A single instance of this class can be used concurrently for
    multiple syntax trees and by multiple clients.
    """
    def __init__(self, redis, claimType):
        """
        Pass in redis connection and claimType.
        """
        self.redis = redis
        self.claimType = claimType

    """
    This stores the cached value of all object ids.
    """
    _all_object_ids = None

    def all_object_ids(self):
        """
        Caches and returns the set of all object ids of current claim type.
        Used in negated group.
        """
        if self._all_object_ids:
            return self._all_object_ids
        self._all_object_ids = self.redis.smembers(self.claimType.tablename)
        return self._all_object_ids

    def visit(self, node):
        """
        Invoke this method to start query execution for node.
        All the other visit_xxx methods return sets of object ids.
        """
        return list(node.accept(self))

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
        return set.intersection(sequence.group.accept(self), sequence.tail.accept(self))

    def visit_and_sequence_tail(self, tail):
        """
        And_sequence_tail -> Group | Group "AND" And_sequence_tail

        Parameter `tail` is an instance of treenode.AndSequenceTail, which has
        two members `group` and `tail`, where `tail` could be None.
        """
        if tail.tail is None:
            return tail.group.accept(self)
        else:
            return set.intersection(tail.group.accept(self), tail.tail.accept(self))

    def visit_or_sequence(self, sequence):
        """
        Or_sequence -> Group "OR" Or_sequence_tail

        Parameter `sequence` is an instance of treenode.OrSequence, which has
        two members `group` and `tail`.
        """
        return set.union(sequence.group.accept(self), sequence.tail.accept(self))

    def visit_or_sequence_tail(self, tail):
        """
        Or_sequence_tail -> Group | Group "OR" Or_sequence_tail

        Parameter `tail` is an instance of treenode.OrSequenceTail, which has
        two members `group` and `tail`, where `tail` could be None.
        """
        if tail.tail is None:
            return tail.group.accept(self)
        else:
            return set.union(tail.group.accept(self), tail.tail.accept(self))

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
        return group.query.accept(self)

    def visit_negated_group(self, group):
        """
        Negated_group -> "NOT" "(" Query ")"

        Parameter `group` is an instance of treenode.NegatedGroup, which has
        one member `query`.
        """
        return set.difference(self.all_object_ids(), group.query.accept(self))

    def visit_clause(self, clause):
        """
        Wrapper for _visit_clause to turn list to set.
        """
        return set(self._visit_clause(clause))

    def _visit_clause(self, clause):
        """
        Clause -> Attribute Operator Value

        Parameter `clause` is an instance of treenode.Clause, which has three
        members `attribute`, `operator`, and `value`.
        """
        columnIndex = self.claimType.findColumnIndex(clause.attribute)
        columnType = self.claimType.columnTypes[columnIndex]
        if columnType == "varchar":
            if clause.operator == "=":
                key = self.claimType.tablename + ":" + clause.attribute + ":" + clause.value
                return self.redis.smembers(key)
            else:
                raise ValueError("Invalid string operator: " + clause.operator)
        elif columnType == "integer" or columnType == "decimal":
            key = self.claimType.tablename + ":" + clause.attribute
            if clause.operator == "=":
                return self.redis.zrangebyscore(key, clause.value, clause.value)
            elif clause.operator == "<":
                return self.redis.zrangebyscore(key, "-inf", "(" + clause.value)
            elif clause.operator == "<=":
                return self.redis.zrangebyscore(key, "-inf", clause.value)
            elif clause.operator == ">":
                return self.redis.zrangebyscore(key, "(" + clause.value, "+inf")
            elif clause.operator == ">=":
                return self.redis.zrangebyscore(key, clause.value, "+inf")
            else:
                raise ValueError("Invalid number operator: " + clause.operator)
        # Handle the 'LAST_MODIFY' field
        elif columnType == "datetime":
            key = self.claimType.tablename + ":" + clause.attribute
            if clause.operator == "=":
                return self.redis.zrangebyscore(key, clause.value, clause.value)
            elif clause.operator == "<":
                return self.redis.zrangebyscore(key, "-inf", "(" + clause.value)
            elif clause.operator == "<=":
                return self.redis.zrangebyscore(key, "-inf", clause.value)
            elif clause.operator == ">":
                return self.redis.zrangebyscore(key, "(" + clause.value, "+inf")
            elif clause.operator == ">=":
                return self.redis.zrangebyscore(key, clause.value, "+inf")
            else:
                raise ValueError("Invalid number operator: " + clause.operator)
        else:
            raise ValueError("Invalid column type: " + self.claimType.columnTypes[columnIndex])

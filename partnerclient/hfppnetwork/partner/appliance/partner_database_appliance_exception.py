"""
Copyright (C) 2010 - 2013 TopCoder Inc., All Rights Reserved.

This file defines the appliance exceptions.

@version 1.0 (Healthcare Fraud Prevention - Partner Database Appliance - Assembly)
@author: zeadom
"""

class PartnerDatabaseApplianceException(Exception):
    """It is used by all service methods to signal a general error."""
    pass

class PersistenceException(PartnerDatabaseApplianceException):
    """It is used by applicable service methods to signal that a persistence-based error occurred"""
    pass

class IllegalArgumentException(PartnerDatabaseApplianceException):
    """It is used by service methods to signal that a passed argument is not valid"""
    pass

class NonexistentClaimTypeException(PartnerDatabaseApplianceException):
    """It is used by service methods to signal that the requested claim type does not exist"""
    pass

"""
Copyright (C) 2010 - 2013 TopCoder Inc., All Rights Reserved.

This file contains claim type's detail info.

@version 1.0 (Healthcare Fraud Prevention - Partner Database Appliance - Assembly)
@author: zeadom, TCSASSEMBLER
@since 2013-12-10
"""

import os, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from partner_database_appliance_exception import NonexistentClaimTypeException

class ClaimType(object):
    """
    Claim type class

    To store claim type's detail info.

    Attributes:
        name: claim type's name
        tablename: claim type's tablename in database
        columns: claim type's table's columns
        columnTypes: claim type's table's column types
    """
    def __init__(self, name = None, tablename = None, columns = None):
        """ Init ClaimType """
        self.name = name or ""
        self.tablename = tablename or ""
        self.columns = []
        self.columnTypes = []
        for typ, col in columns:
            self.columns.append(col)
            self.columnTypes.append(typ)

    def findColumnIndex(self, column):
        return self.columns.index(column)

# claim type beneficiary
Beneficiary = ClaimType(
    name = "beneficiary",
    tablename = "Beneficiaries",
    columns = [
        ("varchar", "DESYNPUF_ID"),
        ("integer", "BENE_BIRTH_DT"),
        ("integer", "BENE_DEATH_DT"),
        #change from BENE_SEX to BENE_SEX_IDENT_CD
        ("integer", "BENE_SEX_IDENT_CD"),
        ("integer", "BENE_RACE_CD"),
        ("varchar", "BENE_ESRD_IND"),
        ("varchar", "SP_STATE_CODE"),
        ("varchar", "BENE_COUNTY_CD"),
        ("integer", "BENE_HI_CVRAGE_TOT_MONS"),
        ("integer", "BENE_SMI_CVRAGE_TOT_MONS"),
        ("integer", "BENE_HMO_CVRAGE_TOT_MONS"),
        ("integer", "PLAN_CVRG_MOS_NUM"),
        ("integer", "SP_ALZHDMTA"),
        ("integer", "SP_CHF"),
        ("integer", "SP_CHRNKIDN"),
        ("integer", "SP_CNCR"),
        ("integer", "SP_COPD"),
        ("integer", "SP_DEPRESSN"),
        ("integer", "SP_DIABETES"),
        ("integer", "SP_ISCHMCHT"),
        ("integer", "SP_OSTEOPRS"),
        ("integer", "SP_RA_OA"),
        ("integer", "SP_STRKETIA"),
        ("decimal", "MEDREIMB_IP"),
        ("decimal", "BENRES_IP"),
        ("decimal", "PPPYMT_IP"),
        ("decimal", "MEDREIMB_OP"),
        ("decimal", "BENRES_OP"),
        ("decimal", "PPPYMT_OP"),
        ("decimal", "MEDREIMB_CAR"),
        ("decimal", "BENRES_CAR"),
        ("decimal", "PPPYMT_CAR"),
        ("datetime", "LAST_MODIFY"),
    ]
)

# claim type carrier
Carrier = ClaimType(
    name = "carrier",
    tablename = "Carriers",
    columns = [
        ("varchar", "DESYNPUF_ID"),
        ("varchar", "CLM_ID"),
        ("integer", "CLM_FROM_DT"),
        ("integer", "CLM_THRU_DT"),
        ("varchar", "ICD9_DGNS_CD_1"),
        ("varchar", "ICD9_DGNS_CD_2"),
        ("varchar", "ICD9_DGNS_CD_3"),
        ("varchar", "ICD9_DGNS_CD_4"),
        ("varchar", "ICD9_DGNS_CD_5"),
        ("varchar", "ICD9_DGNS_CD_6"),
        ("varchar", "ICD9_DGNS_CD_7"),
        ("varchar", "ICD9_DGNS_CD_8"),
        ("varchar", "PRF_PHYSN_NPI_1"),
        ("varchar", "PRF_PHYSN_NPI_2"),
        ("varchar", "PRF_PHYSN_NPI_3"),
        ("varchar", "PRF_PHYSN_NPI_4"),
        ("varchar", "PRF_PHYSN_NPI_5"),
        ("varchar", "PRF_PHYSN_NPI_6"),
        ("varchar", "PRF_PHYSN_NPI_7"),
        ("varchar", "PRF_PHYSN_NPI_8"),
        ("varchar", "PRF_PHYSN_NPI_9"),
        ("varchar", "PRF_PHYSN_NPI_10"),
        ("varchar", "PRF_PHYSN_NPI_11"),
        ("varchar", "PRF_PHYSN_NPI_12"),
        ("varchar", "PRF_PHYSN_NPI_13"),
        ("varchar", "TAX_NUM_1"),
        ("varchar", "TAX_NUM_2"),
        ("varchar", "TAX_NUM_3"),
        ("varchar", "TAX_NUM_4"),
        ("varchar", "TAX_NUM_5"),
        ("varchar", "TAX_NUM_6"),
        ("varchar", "TAX_NUM_7"),
        ("varchar", "TAX_NUM_8"),
        ("varchar", "TAX_NUM_9"),
        ("varchar", "TAX_NUM_10"),
        ("varchar", "TAX_NUM_11"),
        ("varchar", "TAX_NUM_12"),
        ("varchar", "TAX_NUM_13"),
        ("varchar", "HCPCS_CD_1"),
        ("varchar", "HCPCS_CD_2"),
        ("varchar", "HCPCS_CD_3"),
        ("varchar", "HCPCS_CD_4"),
        ("varchar", "HCPCS_CD_5"),
        ("varchar", "HCPCS_CD_6"),
        ("varchar", "HCPCS_CD_7"),
        ("varchar", "HCPCS_CD_8"),
        ("varchar", "HCPCS_CD_9"),
        ("varchar", "HCPCS_CD_10"),
        ("varchar", "HCPCS_CD_11"),
        ("varchar", "HCPCS_CD_12"),
        ("varchar", "HCPCS_CD_13"),
        ("decimal", "LINE_NCH_PMT_AMT_1"),
        ("decimal", "LINE_NCH_PMT_AMT_2"),
        ("decimal", "LINE_NCH_PMT_AMT_3"),
        ("decimal", "LINE_NCH_PMT_AMT_4"),
        ("decimal", "LINE_NCH_PMT_AMT_5"),
        ("decimal", "LINE_NCH_PMT_AMT_6"),
        ("decimal", "LINE_NCH_PMT_AMT_7"),
        ("decimal", "LINE_NCH_PMT_AMT_8"),
        ("decimal", "LINE_NCH_PMT_AMT_9"),
        ("decimal", "LINE_NCH_PMT_AMT_10"),
        ("decimal", "LINE_NCH_PMT_AMT_11"),
        ("decimal", "LINE_NCH_PMT_AMT_12"),
        ("decimal", "LINE_NCH_PMT_AMT_13"),
        ("decimal", "LINE_BENE_PTB_DDCTBL_AMT_1"),
        ("decimal", "LINE_BENE_PTB_DDCTBL_AMT_2"),
        ("decimal", "LINE_BENE_PTB_DDCTBL_AMT_3"),
        ("decimal", "LINE_BENE_PTB_DDCTBL_AMT_4"),
        ("decimal", "LINE_BENE_PTB_DDCTBL_AMT_5"),
        ("decimal", "LINE_BENE_PTB_DDCTBL_AMT_6"),
        ("decimal", "LINE_BENE_PTB_DDCTBL_AMT_7"),
        ("decimal", "LINE_BENE_PTB_DDCTBL_AMT_8"),
        ("decimal", "LINE_BENE_PTB_DDCTBL_AMT_9"),
        ("decimal", "LINE_BENE_PTB_DDCTBL_AMT_10"),
        ("decimal", "LINE_BENE_PTB_DDCTBL_AMT_11"),
        ("decimal", "LINE_BENE_PTB_DDCTBL_AMT_12"),
        ("decimal", "LINE_BENE_PTB_DDCTBL_AMT_13"),
        ("decimal", "LINE_BENE_PRMRY_PYR_PD_AMT_1"),
        ("decimal", "LINE_BENE_PRMRY_PYR_PD_AMT_2"),
        ("decimal", "LINE_BENE_PRMRY_PYR_PD_AMT_3"),
        ("decimal", "LINE_BENE_PRMRY_PYR_PD_AMT_4"),
        ("decimal", "LINE_BENE_PRMRY_PYR_PD_AMT_5"),
        ("decimal", "LINE_BENE_PRMRY_PYR_PD_AMT_6"),
        ("decimal", "LINE_BENE_PRMRY_PYR_PD_AMT_7"),
        ("decimal", "LINE_BENE_PRMRY_PYR_PD_AMT_8"),
        ("decimal", "LINE_BENE_PRMRY_PYR_PD_AMT_9"),
        ("decimal", "LINE_BENE_PRMRY_PYR_PD_AMT_10"),
        ("decimal", "LINE_BENE_PRMRY_PYR_PD_AMT_11"),
        ("decimal", "LINE_BENE_PRMRY_PYR_PD_AMT_12"),
        ("decimal", "LINE_BENE_PRMRY_PYR_PD_AMT_13"),
        ("decimal", "LINE_COINSRNC_AMT_1"),
        ("decimal", "LINE_COINSRNC_AMT_2"),
        ("decimal", "LINE_COINSRNC_AMT_3"),
        ("decimal", "LINE_COINSRNC_AMT_4"),
        ("decimal", "LINE_COINSRNC_AMT_5"),
        ("decimal", "LINE_COINSRNC_AMT_6"),
        ("decimal", "LINE_COINSRNC_AMT_7"),
        ("decimal", "LINE_COINSRNC_AMT_8"),
        ("decimal", "LINE_COINSRNC_AMT_9"),
        ("decimal", "LINE_COINSRNC_AMT_10"),
        ("decimal", "LINE_COINSRNC_AMT_11"),
        ("decimal", "LINE_COINSRNC_AMT_12"),
        ("decimal", "LINE_COINSRNC_AMT_13"),
        ("decimal", "LINE_ALOWD_CHRG_AMT_1"),
        ("decimal", "LINE_ALOWD_CHRG_AMT_2"),
        ("decimal", "LINE_ALOWD_CHRG_AMT_3"),
        ("decimal", "LINE_ALOWD_CHRG_AMT_4"),
        ("decimal", "LINE_ALOWD_CHRG_AMT_5"),
        ("decimal", "LINE_ALOWD_CHRG_AMT_6"),
        ("decimal", "LINE_ALOWD_CHRG_AMT_7"),
        ("decimal", "LINE_ALOWD_CHRG_AMT_8"),
        ("decimal", "LINE_ALOWD_CHRG_AMT_9"),
        ("decimal", "LINE_ALOWD_CHRG_AMT_10"),
        ("decimal", "LINE_ALOWD_CHRG_AMT_11"),
        ("decimal", "LINE_ALOWD_CHRG_AMT_12"),
        ("decimal", "LINE_ALOWD_CHRG_AMT_13"),
        ("varchar", "LINE_PRCSG_IND_CD_1"),
        ("varchar", "LINE_PRCSG_IND_CD_2"),
        ("varchar", "LINE_PRCSG_IND_CD_3"),
        ("varchar", "LINE_PRCSG_IND_CD_4"),
        ("varchar", "LINE_PRCSG_IND_CD_5"),
        ("varchar", "LINE_PRCSG_IND_CD_6"),
        ("varchar", "LINE_PRCSG_IND_CD_7"),
        ("varchar", "LINE_PRCSG_IND_CD_8"),
        ("varchar", "LINE_PRCSG_IND_CD_9"),
        ("varchar", "LINE_PRCSG_IND_CD_10"),
        ("varchar", "LINE_PRCSG_IND_CD_11"),
        ("varchar", "LINE_PRCSG_IND_CD_12"),
        ("varchar", "LINE_PRCSG_IND_CD_13"),
        ("varchar", "LINE_ICD9_DGNS_CD_1"),
        ("varchar", "LINE_ICD9_DGNS_CD_2"),
        ("varchar", "LINE_ICD9_DGNS_CD_3"),
        ("varchar", "LINE_ICD9_DGNS_CD_4"),
        ("varchar", "LINE_ICD9_DGNS_CD_5"),
        ("varchar", "LINE_ICD9_DGNS_CD_6"),
        ("varchar", "LINE_ICD9_DGNS_CD_7"),
        ("varchar", "LINE_ICD9_DGNS_CD_8"),
        ("varchar", "LINE_ICD9_DGNS_CD_9"),
        ("varchar", "LINE_ICD9_DGNS_CD_10"),
        ("varchar", "LINE_ICD9_DGNS_CD_11"),
        ("varchar", "LINE_ICD9_DGNS_CD_12"),
        ("varchar", "LINE_ICD9_DGNS_CD_13"),
        ("datetime", "LAST_MODIFY"),
    ]
)

# claim type inpatient
Inpatient = ClaimType(
    name = "inpatient",
    tablename = "Inpatients",
    columns = [
        ("varchar", "DESYNPUF_ID"),
        ("varchar", "CLM_ID"),
        ("integer", "SEGMENT"),
        ("integer", "CLM_FROM_DT"),
        ("integer", "CLM_THRU_DT"),
        ("varchar", "PRVDR_NUM"),
        ("decimal", "CLM_PMT_AMT"),
        ("decimal", "NCH_PRMRY_PYR_CLM_PD_AMT"),
        ("varchar", "AT_PHYSN_NPI"),
        ("varchar", "OP_PHYSN_NPI"),
        ("varchar", "OT_PHYSN_NPI"),
        ("integer", "CLM_ADMSN_DT"),
        ("varchar", "ADMTNG_ICD9_DGNS_CD"),
        ("decimal", "CLM_PASS_THRU_PER_DIEM_AMT"),
        ("decimal", "NCH_BENE_IP_DDCTBL_AMT"),
        ("decimal", "NCH_BENE_PTA_COINSRNC_LBLTY_AM"),
        ("decimal", "NCH_BENE_BLOOD_DDCTBL_LBLTY_AM"),
        ("integer", "CLM_UTLZTN_DAY_CNT"),
        ("integer", "NCH_BENE_DSCHRG_DT"),
        ("varchar", "CLM_DRG_CD"),
        ("varchar", "ICD9_DGNS_CD_1"),
        ("varchar", "ICD9_DGNS_CD_2"),
        ("varchar", "ICD9_DGNS_CD_3"),
        ("varchar", "ICD9_DGNS_CD_4"),
        ("varchar", "ICD9_DGNS_CD_5"),
        ("varchar", "ICD9_DGNS_CD_6"),
        ("varchar", "ICD9_DGNS_CD_7"),
        ("varchar", "ICD9_DGNS_CD_8"),
        ("varchar", "ICD9_DGNS_CD_9"),
        ("varchar", "ICD9_DGNS_CD_10"),
        ("varchar", "ICD9_PRCDR_CD_1"),
        ("varchar", "ICD9_PRCDR_CD_2"),
        ("varchar", "ICD9_PRCDR_CD_3"),
        ("varchar", "ICD9_PRCDR_CD_4"),
        ("varchar", "ICD9_PRCDR_CD_5"),
        ("varchar", "ICD9_PRCDR_CD_6"),
        ("varchar", "HCPCS_CD_1"),
        ("varchar", "HCPCS_CD_2"),
        ("varchar", "HCPCS_CD_3"),
        ("varchar", "HCPCS_CD_4"),
        ("varchar", "HCPCS_CD_5"),
        ("varchar", "HCPCS_CD_6"),
        ("varchar", "HCPCS_CD_7"),
        ("varchar", "HCPCS_CD_8"),
        ("varchar", "HCPCS_CD_9"),
        ("varchar", "HCPCS_CD_10"),
        ("varchar", "HCPCS_CD_11"),
        ("varchar", "HCPCS_CD_12"),
        ("varchar", "HCPCS_CD_13"),
        ("varchar", "HCPCS_CD_14"),
        ("varchar", "HCPCS_CD_15"),
        ("varchar", "HCPCS_CD_16"),
        ("varchar", "HCPCS_CD_17"),
        ("varchar", "HCPCS_CD_18"),
        ("varchar", "HCPCS_CD_19"),
        ("varchar", "HCPCS_CD_20"),
        ("varchar", "HCPCS_CD_21"),
        ("varchar", "HCPCS_CD_22"),
        ("varchar", "HCPCS_CD_23"),
        ("varchar", "HCPCS_CD_24"),
        ("varchar", "HCPCS_CD_25"),
        ("varchar", "HCPCS_CD_26"),
        ("varchar", "HCPCS_CD_27"),
        ("varchar", "HCPCS_CD_28"),
        ("varchar", "HCPCS_CD_29"),
        ("varchar", "HCPCS_CD_30"),
        ("varchar", "HCPCS_CD_31"),
        ("varchar", "HCPCS_CD_32"),
        ("varchar", "HCPCS_CD_33"),
        ("varchar", "HCPCS_CD_34"),
        ("varchar", "HCPCS_CD_35"),
        ("varchar", "HCPCS_CD_36"),
        ("varchar", "HCPCS_CD_37"),
        ("varchar", "HCPCS_CD_38"),
        ("varchar", "HCPCS_CD_39"),
        ("varchar", "HCPCS_CD_40"),
        ("varchar", "HCPCS_CD_41"),
        ("varchar", "HCPCS_CD_42"),
        ("varchar", "HCPCS_CD_43"),
        ("varchar", "HCPCS_CD_44"),
        ("varchar", "HCPCS_CD_45"),
        ("datetime", "LAST_MODIFY"),
    ]
)

# claim type outpatient
Outpatient = ClaimType(
    name = "outpatient",
    tablename = "Outpatients",
    columns = [
        ("varchar", "DESYNPUF_ID"),
        ("varchar", "CLM_ID"),
        ("integer", "SEGMENT"),
        ("integer", "CLM_FROM_DT"),
        ("integer", "CLM_THRU_DT"),
        ("varchar", "PRVDR_NUM"),
        ("decimal", "CLM_PMT_AMT"),
        ("decimal", "NCH_PRMRY_PYR_CLM_PD_AMT"),
        ("varchar", "AT_PHYSN_NPI"),
        ("varchar", "OP_PHYSN_NPI"),
        ("varchar", "OT_PHYSN_NPI"),
        ("decimal", "NCH_BENE_BLOOD_DDCTBL_LBLTY_AM"),
        ("varchar", "ICD9_DGNS_CD_1"),
        ("varchar", "ICD9_DGNS_CD_2"),
        ("varchar", "ICD9_DGNS_CD_3"),
        ("varchar", "ICD9_DGNS_CD_4"),
        ("varchar", "ICD9_DGNS_CD_5"),
        ("varchar", "ICD9_DGNS_CD_6"),
        ("varchar", "ICD9_DGNS_CD_7"),
        ("varchar", "ICD9_DGNS_CD_8"),
        ("varchar", "ICD9_DGNS_CD_9"),
        ("varchar", "ICD9_DGNS_CD_10"),
        ("varchar", "ICD9_PRCDR_CD_1"),
        ("varchar", "ICD9_PRCDR_CD_2"),
        ("varchar", "ICD9_PRCDR_CD_3"),
        ("varchar", "ICD9_PRCDR_CD_4"),
        ("varchar", "ICD9_PRCDR_CD_5"),
        ("varchar", "ICD9_PRCDR_CD_6"),
        ("decimal", "NCH_BENE_PTB_DDCTBL_AMT"),
        ("decimal", "NCH_BENE_PTB_COINSRNC_AMT"),
        ("varchar", "ADMTNG_ICD9_DGNS_CD"),
        ("varchar", "HCPCS_CD_1"),
        ("varchar", "HCPCS_CD_2"),
        ("varchar", "HCPCS_CD_3"),
        ("varchar", "HCPCS_CD_4"),
        ("varchar", "HCPCS_CD_5"),
        ("varchar", "HCPCS_CD_6"),
        ("varchar", "HCPCS_CD_7"),
        ("varchar", "HCPCS_CD_8"),
        ("varchar", "HCPCS_CD_9"),
        ("varchar", "HCPCS_CD_10"),
        ("varchar", "HCPCS_CD_11"),
        ("varchar", "HCPCS_CD_12"),
        ("varchar", "HCPCS_CD_13"),
        ("varchar", "HCPCS_CD_14"),
        ("varchar", "HCPCS_CD_15"),
        ("varchar", "HCPCS_CD_16"),
        ("varchar", "HCPCS_CD_17"),
        ("varchar", "HCPCS_CD_18"),
        ("varchar", "HCPCS_CD_19"),
        ("varchar", "HCPCS_CD_20"),
        ("varchar", "HCPCS_CD_21"),
        ("varchar", "HCPCS_CD_22"),
        ("varchar", "HCPCS_CD_23"),
        ("varchar", "HCPCS_CD_24"),
        ("varchar", "HCPCS_CD_25"),
        ("varchar", "HCPCS_CD_26"),
        ("varchar", "HCPCS_CD_27"),
        ("varchar", "HCPCS_CD_28"),
        ("varchar", "HCPCS_CD_29"),
        ("varchar", "HCPCS_CD_30"),
        ("varchar", "HCPCS_CD_31"),
        ("varchar", "HCPCS_CD_32"),
        ("varchar", "HCPCS_CD_33"),
        ("varchar", "HCPCS_CD_34"),
        ("varchar", "HCPCS_CD_35"),
        ("varchar", "HCPCS_CD_36"),
        ("varchar", "HCPCS_CD_37"),
        ("varchar", "HCPCS_CD_38"),
        ("varchar", "HCPCS_CD_39"),
        ("varchar", "HCPCS_CD_40"),
        ("varchar", "HCPCS_CD_41"),
        ("varchar", "HCPCS_CD_42"),
        ("varchar", "HCPCS_CD_43"),
        ("varchar", "HCPCS_CD_44"),
        ("varchar", "HCPCS_CD_45"),
        ("datetime", "LAST_MODIFY"),
    ]
)

# claim type prescription
Prescription = ClaimType(
    name = "prescription",
    tablename = "Prescriptions",
    columns = [
        ("varchar", "DESYNPUF_ID"),
        ("varchar", "PDE_ID"),
        ("integer", "SRVC_DT"),
        ("varchar", "PROD_SRVC_ID"),
        ("decimal", "QTY_DSPNSD_NUM"),
        ("integer", "DAYS_SUPLY_NUM"),
        ("decimal", "PTNT_PAY_AMT"),
        ("decimal", "TOT_RX_CST_AMT"),
        ("datetime", "LAST_MODIFY"),
    ]
)

# Use claim type to store partner request
Request = ClaimType(
    name = "partner_request",
    tablename = "PartnerRequests",
    columns = [
        ("varchar", "request_id"),
        ("varchar", "study_id"),
        ("varchar", "query"),
        ("varchar", "expiration_time"),
        ("varchar", "cache_available"),
        ("varchar", "cache_timestamp"),
        ("varchar", "status"),
    ]
)

class ClaimTypeFactory(object):
    """
    Claim type's factory
    """

    claimTypeMap = {
        "beneficiary" : Beneficiary,
        "carrier" : Carrier,
        "inpatient" : Inpatient,
        "outpatient" : Outpatient,
        "prescription" : Prescription,
        "partner_request": Request,
    }
    claimTypeList = list(claimTypeMap.keys())

    @classmethod
    def load(cls, claimType):
        """
        load claimType

        load ClaimType object according to the claimType

        Args:
            claimType: the claimType, e.g. "beneficiary"

        Returns:
            A corresponding ClaimType object

        Raises:
            NonexistentClaimTypeException: ClaimType not in (beneficiary, carrier, inpatient, outpatient, prescription)
        """
        if claimType not in cls.claimTypeMap:
            raise NonexistentClaimTypeException("Nonexistent claim type %s" % claimType)
        return cls.claimTypeMap.get(claimType)

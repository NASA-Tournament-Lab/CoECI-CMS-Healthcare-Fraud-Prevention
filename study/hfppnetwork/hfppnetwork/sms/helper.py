# -*- coding: utf-8 -*-
"""
Copyright (C) 2013-2014 TopCoder Inc., All Rights Reserved.

This is module that represents the helper methods.

v1.1 - Healthcare Fraud Prevention Release Assembly v1.0
     - added method pull_hub_roles

@author:  TCSASSEMBLER
@version: 1.1
"""
from hfppnetwork.sms.models import BeneficiaryClaimData, Study, CarrierClaimData,\
    InpatientClaimData, OutpatientClaimData, PrescriptionClaimData
import datetime
import urllib
import logging
from django.db import models
from xml.etree import ElementTree
from hfppnetwork.settings import CLAIM_DATA_FIELDS,\
    HFPP_NODE_HTTP_SERVICE_BASE_URL, HFPP_PARTNER_USERNAME,\
    HFPP_PARTNER_PASSWORD, CA_DEFAULT, CA_CERTIFICATE_FILE
from hfppnetwork.sms import logginghelper

logger = logging.getLogger(__name__)

"""
BeneficiaryClaim xml mapping
"""
BeneficiaryClaimMappings = {
'desynpuf_id':'BeneficiaryCode',
'bene_birth_dt':'DateOfBirth',
'bene_death_dt':'DateOfDeath',
'bene_sex_ident_cd':'Sex',
'bene_race_cd':'BeneficiaryRaceCode',
'bene_esrd_ind':'EndStageRenalDiseaseIndicator',
'sp_state_code':'StateCode',
'bene_county_cd':'CountyCode',
'bene_hi_cvrage_tot_mons':'PartACoverageTotalMonths',
'bene_smi_cvrage_tot_mons':'PartBCoverageTotalMonths',
'bene_hmo_cvrage_tot_mons':'HMOCoverageTotalMonths',
'plan_cvrg_mos_num':'PartDPlanCoverageTotalMonths',
'sp_alzhdmta':'AlzheimerOrRelatedDisordersOrSenile',
'sp_chf':'HeartFailure',
'sp_chrnkidn':'ChronicKidneyDisease',
'sp_cncr':'Cancer',
'sp_copd':'ChronicObstructivePulmonaryDisease',
'sp_depressn':'Depression',
'sp_diabetes':'Diabetes',
'sp_ischmcht':'IschemicHeartDisease',
'sp_osteoprs':'Osteoporosis',
'sp_ra_oa':'RheumatoidArthritisAndOsteoarthritis',
'sp_strketia':'StrokeIschemicAttack',
'medreimb_ip':'InpatientAnnualMedicareReimbursementAmount',
'benres_ip':'InpatientAnnualBeneficiaryResponsibilityAmount',
'pppymt_ip':'InpatientAnnualPrimaryPayerReimbursementAmount',
'medreimb_op':'OutpatientInstitutionalAnnualMedicareReimbursementAmount',
'benres_op':'OutpatientInstitutionalAnnualBeneficiaryResponsibilityAmount',
'pppymt_op':'OutpatientInstitutionalAnnualPrimaryPayerReimbursementAmount',
'medreimb_car':'CarrierAnnualMedicareReimbursementAmount',
'benres_car':'CarrierAnnualBeneficiaryResponsibilityAmount',
'pppymt_car':'CarrierAnnualPrimaryPayerReimbursementAmount',
}

"""
CarrierClaim xml mapping
"""
CarrierClaimMappings = {
'desynpuf_id':'BeneficiaryCode',
'clm_id':'ClaimId',
'clm_from_dt':'StartDate',
'clm_thru_dt':'EndDate',
'icd9_dgns_cd_1':'ClaimDiagnosisCode1',
'icd9_dgns_cd_2':'ClaimDiagnosisCode2',
'icd9_dgns_cd_3':'ClaimDiagnosisCode3',
'icd9_dgns_cd_4':'ClaimDiagnosisCode4',
'icd9_dgns_cd_5':'ClaimDiagnosisCode5',
'icd9_dgns_cd_6':'ClaimDiagnosisCode6',
'icd9_dgns_cd_7':'ClaimDiagnosisCode7',
'icd9_dgns_cd_8':'ClaimDiagnosisCode8',
'prf_physn_npi_1':'ProviderPhysicianNationalProviderId1',
'prf_physn_npi_2':'ProviderPhysicianNationalProviderId2',
'prf_physn_npi_3':'ProviderPhysicianNationalProviderId3',
'prf_physn_npi_4':'ProviderPhysicianNationalProviderId4',
'prf_physn_npi_5':'ProviderPhysicianNationalProviderId5',
'prf_physn_npi_6':'ProviderPhysicianNationalProviderId6',
'prf_physn_npi_7':'ProviderPhysicianNationalProviderId7',
'prf_physn_npi_8':'ProviderPhysicianNationalProviderId8',
'prf_physn_npi_9':'ProviderPhysicianNationalProviderId9',
'prf_physn_npi_10':'ProviderPhysicianNationalProviderId10',
'prf_physn_npi_11':'ProviderPhysicianNationalProviderId11',
'prf_physn_npi_12':'ProviderPhysicianNationalProviderId12',
'prf_physn_npi_13':'ProviderPhysicianNationalProviderId13',
'tax_num_1':'ProviderInstitutionTaxNumber1',
'tax_num_2':'ProviderInstitutionTaxNumber2',
'tax_num_3':'ProviderInstitutionTaxNumber3',
'tax_num_4':'ProviderInstitutionTaxNumber4',
'tax_num_5':'ProviderInstitutionTaxNumber5',
'tax_num_6':'ProviderInstitutionTaxNumber6',
'tax_num_7':'ProviderInstitutionTaxNumber7',
'tax_num_8':'ProviderInstitutionTaxNumber8',
'tax_num_9':'ProviderInstitutionTaxNumber9',
'tax_num_10':'ProviderInstitutionTaxNumber10',
'tax_num_11':'ProviderInstitutionTaxNumber11',
'tax_num_12':'ProviderInstitutionTaxNumber12',
'tax_num_13':'ProviderInstitutionTaxNumber13',
'hcpcs_cd_1':'LineHCFACommonProcedureCodingSystem1',
'hcpcs_cd_2':'LineHCFACommonProcedureCodingSystem2',
'hcpcs_cd_3':'LineHCFACommonProcedureCodingSystem3',
'hcpcs_cd_4':'LineHCFACommonProcedureCodingSystem4',
'hcpcs_cd_5':'LineHCFACommonProcedureCodingSystem5',
'hcpcs_cd_6':'LineHCFACommonProcedureCodingSystem6',
'hcpcs_cd_7':'LineHCFACommonProcedureCodingSystem7',
'hcpcs_cd_8':'LineHCFACommonProcedureCodingSystem8',
'hcpcs_cd_9':'LineHCFACommonProcedureCodingSystem9',
'hcpcs_cd_10':'LineHCFACommonProcedureCodingSystem10',
'hcpcs_cd_11':'LineHCFACommonProcedureCodingSystem11',
'hcpcs_cd_12':'LineHCFACommonProcedureCodingSystem12',
'hcpcs_cd_13':'LineHCFACommonProcedureCodingSystem13',
'line_nch_pmt_amt_1':'LineNCHPaymentAmount1',
'line_nch_pmt_amt_2':'LineNCHPaymentAmount2',
'line_nch_pmt_amt_3':'LineNCHPaymentAmount3',
'line_nch_pmt_amt_4':'LineNCHPaymentAmount4',
'line_nch_pmt_amt_5':'LineNCHPaymentAmount5',
'line_nch_pmt_amt_6':'LineNCHPaymentAmount6',
'line_nch_pmt_amt_7':'LineNCHPaymentAmount7',
'line_nch_pmt_amt_8':'LineNCHPaymentAmount8',
'line_nch_pmt_amt_9':'LineNCHPaymentAmount9',
'line_nch_pmt_amt_10':'LineNCHPaymentAmount10',
'line_nch_pmt_amt_11':'LineNCHPaymentAmount11',
'line_nch_pmt_amt_12':'LineNCHPaymentAmount12',
'line_nch_pmt_amt_13':'LineNCHPaymentAmount13',
'line_bene_ptb_ddctbl_amt_1':'LineBeneficiaryPartBDeductibleAmount1',
'line_bene_ptb_ddctbl_amt_2':'LineBeneficiaryPartBDeductibleAmount2',
'line_bene_ptb_ddctbl_amt_3':'LineBeneficiaryPartBDeductibleAmount3',
'line_bene_ptb_ddctbl_amt_4':'LineBeneficiaryPartBDeductibleAmount4',
'line_bene_ptb_ddctbl_amt_5':'LineBeneficiaryPartBDeductibleAmount5',
'line_bene_ptb_ddctbl_amt_6':'LineBeneficiaryPartBDeductibleAmount6',
'line_bene_ptb_ddctbl_amt_7':'LineBeneficiaryPartBDeductibleAmount7',
'line_bene_ptb_ddctbl_amt_8':'LineBeneficiaryPartBDeductibleAmount8',
'line_bene_ptb_ddctbl_amt_9':'LineBeneficiaryPartBDeductibleAmount9',
'line_bene_ptb_ddctbl_amt_10':'LineBeneficiaryPartBDeductibleAmount10',
'line_bene_ptb_ddctbl_amt_11':'LineBeneficiaryPartBDeductibleAmount11',
'line_bene_ptb_ddctbl_amt_12':'LineBeneficiaryPartBDeductibleAmount12',
'line_bene_ptb_ddctbl_amt_13':'LineBeneficiaryPartBDeductibleAmount13',
'line_bene_prmry_pyr_pd_amt_1':'LineBeneficiaryPrimaryPayerPaidAmount1',
'line_bene_prmry_pyr_pd_amt_2':'LineBeneficiaryPrimaryPayerPaidAmount2',
'line_bene_prmry_pyr_pd_amt_3':'LineBeneficiaryPrimaryPayerPaidAmount3',
'line_bene_prmry_pyr_pd_amt_4':'LineBeneficiaryPrimaryPayerPaidAmount4',
'line_bene_prmry_pyr_pd_amt_5':'LineBeneficiaryPrimaryPayerPaidAmount5',
'line_bene_prmry_pyr_pd_amt_6':'LineBeneficiaryPrimaryPayerPaidAmount6',
'line_bene_prmry_pyr_pd_amt_7':'LineBeneficiaryPrimaryPayerPaidAmount7',
'line_bene_prmry_pyr_pd_amt_8':'LineBeneficiaryPrimaryPayerPaidAmount8',
'line_bene_prmry_pyr_pd_amt_9':'LineBeneficiaryPrimaryPayerPaidAmount9',
'line_bene_prmry_pyr_pd_amt_10':'LineBeneficiaryPrimaryPayerPaidAmount10',
'line_bene_prmry_pyr_pd_amt_11':'LineBeneficiaryPrimaryPayerPaidAmount11',
'line_bene_prmry_pyr_pd_amt_12':'LineBeneficiaryPrimaryPayerPaidAmount12',
'line_bene_prmry_pyr_pd_amt_13':'LineBeneficiaryPrimaryPayerPaidAmount13',
'line_coinsrnc_amt_1':'LineCoinsuranceAmount1',
'line_coinsrnc_amt_2':'LineCoinsuranceAmount2',
'line_coinsrnc_amt_3':'LineCoinsuranceAmount3',
'line_coinsrnc_amt_4':'LineCoinsuranceAmount4',
'line_coinsrnc_amt_5':'LineCoinsuranceAmount5',
'line_coinsrnc_amt_6':'LineCoinsuranceAmount6',
'line_coinsrnc_amt_7':'LineCoinsuranceAmount7',
'line_coinsrnc_amt_8':'LineCoinsuranceAmount8',
'line_coinsrnc_amt_9':'LineCoinsuranceAmount9',
'line_coinsrnc_amt_10':'LineCoinsuranceAmount10',
'line_coinsrnc_amt_11':'LineCoinsuranceAmount11',
'line_coinsrnc_amt_12':'LineCoinsuranceAmount12',
'line_coinsrnc_amt_13':'LineCoinsuranceAmount13',
'line_alowd_chrg_amt_1':'LineAllowedChargeAmount1',
'line_alowd_chrg_amt_2':'LineAllowedChargeAmount2',
'line_alowd_chrg_amt_3':'LineAllowedChargeAmount3',
'line_alowd_chrg_amt_4':'LineAllowedChargeAmount4',
'line_alowd_chrg_amt_5':'LineAllowedChargeAmount5',
'line_alowd_chrg_amt_6':'LineAllowedChargeAmount6',
'line_alowd_chrg_amt_7':'LineAllowedChargeAmount7',
'line_alowd_chrg_amt_8':'LineAllowedChargeAmount8',
'line_alowd_chrg_amt_9':'LineAllowedChargeAmount9',
'line_alowd_chrg_amt_10':'LineAllowedChargeAmount10',
'line_alowd_chrg_amt_11':'LineAllowedChargeAmount11',
'line_alowd_chrg_amt_12':'LineAllowedChargeAmount12',
'line_alowd_chrg_amt_13':'LineAllowedChargeAmount13',
'line_prcsg_ind_cd_1':'LineProcessingIndicatorCode1',
'line_prcsg_ind_cd_2':'LineProcessingIndicatorCode2',
'line_prcsg_ind_cd_3':'LineProcessingIndicatorCode3',
'line_prcsg_ind_cd_4':'LineProcessingIndicatorCode4',
'line_prcsg_ind_cd_5':'LineProcessingIndicatorCode5',
'line_prcsg_ind_cd_6':'LineProcessingIndicatorCode6',
'line_prcsg_ind_cd_7':'LineProcessingIndicatorCode7',
'line_prcsg_ind_cd_8':'LineProcessingIndicatorCode8',
'line_prcsg_ind_cd_9':'LineProcessingIndicatorCode9',
'line_prcsg_ind_cd_10':'LineProcessingIndicatorCode10',
'line_prcsg_ind_cd_11':'LineProcessingIndicatorCode11',
'line_prcsg_ind_cd_12':'LineProcessingIndicatorCode12',
'line_prcsg_ind_cd_13':'LineProcessingIndicatorCode13',
'line_icd9_dgns_cd_1':'LineDiagnosisCode1',
'line_icd9_dgns_cd_2':'LineDiagnosisCode2',
'line_icd9_dgns_cd_3':'LineDiagnosisCode3',
'line_icd9_dgns_cd_4':'LineDiagnosisCode4',
'line_icd9_dgns_cd_5':'LineDiagnosisCode5',
'line_icd9_dgns_cd_6':'LineDiagnosisCode6',
'line_icd9_dgns_cd_7':'LineDiagnosisCode7',
'line_icd9_dgns_cd_8':'LineDiagnosisCode8',
'line_icd9_dgns_cd_9':'LineDiagnosisCode9',
'line_icd9_dgns_cd_10':'LineDiagnosisCode10',
'line_icd9_dgns_cd_11':'LineDiagnosisCode11',
'line_icd9_dgns_cd_12':'LineDiagnosisCode12',
'line_icd9_dgns_cd_13':'LineDiagnosisCode13',
}

"""
InpatientClaim xml mapping
"""
InPatientClaimMappings = {
'desynpuf_id':'BeneficiaryCode',
'clm_id':'ClaimId',
'segment':'ClaimLineSegment',
'clm_from_dt':'StartDate',
'clm_thru_dt':'EndDate',
'prvdr_num':'ProviderInstitution',
'clm_pmt_amt':'ClaimPaymentAmount',
'nch_prmry_pyr_clm_pd_amt':'NCHPrimaryPayerClaimPaidAmount',
'at_physn_npi':'AttendingPhysicianNationalProviderId',
'op_physn_npi':'OperatingPhysicianNationalProviderId',
'ot_physn_npi':'OtherPhysicianNationalProviderId',
'clm_admsn_dt':'InpatientAdmissionDate',
'admtng_icd9_dgns_cd':'ClaimAdmittingDiagnosisCode',
'clm_pass_thru_per_diem_amt':'ClaimPassThruPerDiemAmount',
'nch_bene_ip_ddctbl_amt':'NCHBeneficiaryInpatientDeductibleAmount',
'nch_bene_pta_coinsrnc_lblty_am':'NCHBeneficiaryPartACoinsuranceLiabilityAmount',
'nch_bene_blood_ddctbl_lblty_am':'NCHBeneficiaryBloodDeductibleLiabilityAmount',
'clm_utlztn_day_cnt':'ClaimUtilizationDayCount',
'nch_bene_dschrg_dt':'InpatientDischargedDate',
'clm_drg_cd':'ClaimDiagnosisRelatedGroupCode',
'icd9_dgns_cd_1':'ClaimDiagnosisCode1',
'icd9_dgns_cd_2':'ClaimDiagnosisCode2',
'icd9_dgns_cd_3':'ClaimDiagnosisCode3',
'icd9_dgns_cd_4':'ClaimDiagnosisCode4',
'icd9_dgns_cd_5':'ClaimDiagnosisCode5',
'icd9_dgns_cd_6':'ClaimDiagnosisCode6',
'icd9_dgns_cd_7':'ClaimDiagnosisCode7',
'icd9_dgns_cd_8':'ClaimDiagnosisCode8',
'icd9_dgns_cd_9':'ClaimDiagnosisCode9',
'icd9_dgns_cd_10':'ClaimDiagnosisCode10',
'icd9_prcdr_cd_1':'ClaimProcedureCode1',
'icd9_prcdr_cd_2':'ClaimProcedureCode2',
'icd9_prcdr_cd_3':'ClaimProcedureCode3',
'icd9_prcdr_cd_4':'ClaimProcedureCode4',
'icd9_prcdr_cd_5':'ClaimProcedureCode5',
'icd9_prcdr_cd_6':'ClaimProcedureCode6',
'hcpcs_cd_1':'RevenueCenterHCFACommonProcedureCodingSystem1',
'hcpcs_cd_2':'RevenueCenterHCFACommonProcedureCodingSystem2',
'hcpcs_cd_3':'RevenueCenterHCFACommonProcedureCodingSystem3',
'hcpcs_cd_4':'RevenueCenterHCFACommonProcedureCodingSystem4',
'hcpcs_cd_5':'RevenueCenterHCFACommonProcedureCodingSystem5',
'hcpcs_cd_6':'RevenueCenterHCFACommonProcedureCodingSystem6',
'hcpcs_cd_7':'RevenueCenterHCFACommonProcedureCodingSystem7',
'hcpcs_cd_8':'RevenueCenterHCFACommonProcedureCodingSystem8',
'hcpcs_cd_9':'RevenueCenterHCFACommonProcedureCodingSystem9',
'hcpcs_cd_10':'RevenueCenterHCFACommonProcedureCodingSystem10',
'hcpcs_cd_11':'RevenueCenterHCFACommonProcedureCodingSystem11',
'hcpcs_cd_12':'RevenueCenterHCFACommonProcedureCodingSystem12',
'hcpcs_cd_13':'RevenueCenterHCFACommonProcedureCodingSystem13',
'hcpcs_cd_14':'RevenueCenterHCFACommonProcedureCodingSystem14',
'hcpcs_cd_15':'RevenueCenterHCFACommonProcedureCodingSystem15',
'hcpcs_cd_16':'RevenueCenterHCFACommonProcedureCodingSystem16',
'hcpcs_cd_17':'RevenueCenterHCFACommonProcedureCodingSystem17',
'hcpcs_cd_18':'RevenueCenterHCFACommonProcedureCodingSystem18',
'hcpcs_cd_19':'RevenueCenterHCFACommonProcedureCodingSystem19',
'hcpcs_cd_20':'RevenueCenterHCFACommonProcedureCodingSystem20',
'hcpcs_cd_21':'RevenueCenterHCFACommonProcedureCodingSystem21',
'hcpcs_cd_22':'RevenueCenterHCFACommonProcedureCodingSystem22',
'hcpcs_cd_23':'RevenueCenterHCFACommonProcedureCodingSystem23',
'hcpcs_cd_24':'RevenueCenterHCFACommonProcedureCodingSystem24',
'hcpcs_cd_25':'RevenueCenterHCFACommonProcedureCodingSystem25',
'hcpcs_cd_26':'RevenueCenterHCFACommonProcedureCodingSystem26',
'hcpcs_cd_27':'RevenueCenterHCFACommonProcedureCodingSystem27',
'hcpcs_cd_28':'RevenueCenterHCFACommonProcedureCodingSystem28',
'hcpcs_cd_29':'RevenueCenterHCFACommonProcedureCodingSystem29',
'hcpcs_cd_30':'RevenueCenterHCFACommonProcedureCodingSystem30',
'hcpcs_cd_31':'RevenueCenterHCFACommonProcedureCodingSystem31',
'hcpcs_cd_32':'RevenueCenterHCFACommonProcedureCodingSystem32',
'hcpcs_cd_33':'RevenueCenterHCFACommonProcedureCodingSystem33',
'hcpcs_cd_34':'RevenueCenterHCFACommonProcedureCodingSystem34',
'hcpcs_cd_35':'RevenueCenterHCFACommonProcedureCodingSystem35',
'hcpcs_cd_36':'RevenueCenterHCFACommonProcedureCodingSystem36',
'hcpcs_cd_37':'RevenueCenterHCFACommonProcedureCodingSystem37',
'hcpcs_cd_38':'RevenueCenterHCFACommonProcedureCodingSystem38',
'hcpcs_cd_39':'RevenueCenterHCFACommonProcedureCodingSystem39',
'hcpcs_cd_40':'RevenueCenterHCFACommonProcedureCodingSystem40',
'hcpcs_cd_41':'RevenueCenterHCFACommonProcedureCodingSystem41',
'hcpcs_cd_42':'RevenueCenterHCFACommonProcedureCodingSystem42',
'hcpcs_cd_43':'RevenueCenterHCFACommonProcedureCodingSystem43',
'hcpcs_cd_44':'RevenueCenterHCFACommonProcedureCodingSystem44',
'hcpcs_cd_45':'RevenueCenterHCFACommonProcedureCodingSystem45',
}

"""
OutpatientClaim xml mappings
"""
OutPatientClaimMappings = {
'desynpuf_id':'BeneficiaryCode',
'clm_id':'ClaimId',
'segment':'ClaimLineSegment',
'clm_from_dt':'StartDate',
'clm_thru_dt':'EndDate',
'prvdr_num':'ProviderInstitution',
'clm_pmt_amt':'ClaimPaymentAmount',
'nch_prmry_pyr_clm_pd_amt':'NCHPrimaryPayerClaimPaidAmount',
'at_physn_npi':'AttendingPhysicianNationalProviderId',
'op_physn_npi':'OperatingPhysicianNationalProviderId',
'ot_physn_npi':'OtherPhysicianNationalProviderId',
'nch_bene_blood_ddctbl_lblty_am':'NCHBeneficiaryBloodDeductibleLiabilityAmount',
'icd9_dgns_cd_1':'ClaimDiagnosisCode1',
'icd9_dgns_cd_2':'ClaimDiagnosisCode2',
'icd9_dgns_cd_3':'ClaimDiagnosisCode3',
'icd9_dgns_cd_4':'ClaimDiagnosisCode4',
'icd9_dgns_cd_5':'ClaimDiagnosisCode5',
'icd9_dgns_cd_6':'ClaimDiagnosisCode6',
'icd9_dgns_cd_7':'ClaimDiagnosisCode7',
'icd9_dgns_cd_8':'ClaimDiagnosisCode8',
'icd9_dgns_cd_9':'ClaimDiagnosisCode9',
'icd9_dgns_cd_10':'ClaimDiagnosisCode10',
'icd9_prcdr_cd_1':'ClaimProcedureCode1',
'icd9_prcdr_cd_2':'ClaimProcedureCode2',
'icd9_prcdr_cd_3':'ClaimProcedureCode3',
'icd9_prcdr_cd_4':'ClaimProcedureCode4',
'icd9_prcdr_cd_5':'ClaimProcedureCode5',
'icd9_prcdr_cd_6':'ClaimProcedureCode6',
'nch_bene_ptb_ddctbl_amt':'NCHBeneficiaryPartBDeductibleAmount',
'nch_bene_ptb_coinsrnc_amt':'NCHBeneficiaryPartBCoinsuranceAmount',
'admtng_icd9_dgns_cd':'ClaimAdmittingDiagnosisCode',
'hcpcs_cd_1':'RevenueCenterHCFACommonProcedureCodingSystem1',
'hcpcs_cd_2':'RevenueCenterHCFACommonProcedureCodingSystem2',
'hcpcs_cd_3':'RevenueCenterHCFACommonProcedureCodingSystem3',
'hcpcs_cd_4':'RevenueCenterHCFACommonProcedureCodingSystem4',
'hcpcs_cd_5':'RevenueCenterHCFACommonProcedureCodingSystem5',
'hcpcs_cd_6':'RevenueCenterHCFACommonProcedureCodingSystem6',
'hcpcs_cd_7':'RevenueCenterHCFACommonProcedureCodingSystem7',
'hcpcs_cd_8':'RevenueCenterHCFACommonProcedureCodingSystem8',
'hcpcs_cd_9':'RevenueCenterHCFACommonProcedureCodingSystem9',
'hcpcs_cd_10':'RevenueCenterHCFACommonProcedureCodingSystem10',
'hcpcs_cd_11':'RevenueCenterHCFACommonProcedureCodingSystem11',
'hcpcs_cd_12':'RevenueCenterHCFACommonProcedureCodingSystem12',
'hcpcs_cd_13':'RevenueCenterHCFACommonProcedureCodingSystem13',
'hcpcs_cd_14':'RevenueCenterHCFACommonProcedureCodingSystem14',
'hcpcs_cd_15':'RevenueCenterHCFACommonProcedureCodingSystem15',
'hcpcs_cd_16':'RevenueCenterHCFACommonProcedureCodingSystem16',
'hcpcs_cd_17':'RevenueCenterHCFACommonProcedureCodingSystem17',
'hcpcs_cd_18':'RevenueCenterHCFACommonProcedureCodingSystem18',
'hcpcs_cd_19':'RevenueCenterHCFACommonProcedureCodingSystem19',
'hcpcs_cd_20':'RevenueCenterHCFACommonProcedureCodingSystem20',
'hcpcs_cd_21':'RevenueCenterHCFACommonProcedureCodingSystem21',
'hcpcs_cd_22':'RevenueCenterHCFACommonProcedureCodingSystem22',
'hcpcs_cd_23':'RevenueCenterHCFACommonProcedureCodingSystem23',
'hcpcs_cd_24':'RevenueCenterHCFACommonProcedureCodingSystem24',
'hcpcs_cd_25':'RevenueCenterHCFACommonProcedureCodingSystem25',
'hcpcs_cd_26':'RevenueCenterHCFACommonProcedureCodingSystem26',
'hcpcs_cd_27':'RevenueCenterHCFACommonProcedureCodingSystem27',
'hcpcs_cd_28':'RevenueCenterHCFACommonProcedureCodingSystem28',
'hcpcs_cd_29':'RevenueCenterHCFACommonProcedureCodingSystem29',
'hcpcs_cd_30':'RevenueCenterHCFACommonProcedureCodingSystem30',
'hcpcs_cd_31':'RevenueCenterHCFACommonProcedureCodingSystem31',
'hcpcs_cd_32':'RevenueCenterHCFACommonProcedureCodingSystem32',
'hcpcs_cd_33':'RevenueCenterHCFACommonProcedureCodingSystem33',
'hcpcs_cd_34':'RevenueCenterHCFACommonProcedureCodingSystem34',
'hcpcs_cd_35':'RevenueCenterHCFACommonProcedureCodingSystem35',
'hcpcs_cd_36':'RevenueCenterHCFACommonProcedureCodingSystem36',
'hcpcs_cd_37':'RevenueCenterHCFACommonProcedureCodingSystem37',
'hcpcs_cd_38':'RevenueCenterHCFACommonProcedureCodingSystem38',
'hcpcs_cd_39':'RevenueCenterHCFACommonProcedureCodingSystem39',
'hcpcs_cd_40':'RevenueCenterHCFACommonProcedureCodingSystem40',
'hcpcs_cd_41':'RevenueCenterHCFACommonProcedureCodingSystem41',
'hcpcs_cd_42':'RevenueCenterHCFACommonProcedureCodingSystem42',
'hcpcs_cd_43':'RevenueCenterHCFACommonProcedureCodingSystem43',
'hcpcs_cd_44':'RevenueCenterHCFACommonProcedureCodingSystem44',
'hcpcs_cd_45':'RevenueCenterHCFACommonProcedureCodingSystem45',
}

"""
PrescriptionClaim xml mapping
"""
PrescriptionClaimMappings = {
'desynpuf_id':'BeneficiaryCode',
'pde_id':'CCWPartDEventNumber',
'srvc_dt':'RXServiceDate',
'prod_srvc_id':'ProductServiceID',
'qty_dspnsd_num':'QuantityDispensed',
'days_suply_num':'DaysSupply',
'ptnt_pay_amt':'PatientPayAmount',
'tot_rx_cst_amt':'GrossDrugCost',
}

"""
BeneficiaryClaimData instance
"""
beneficiaryClaimDataInstance = BeneficiaryClaimData()
"""
CarrierClaimData instance
"""
carrierClaimDataInstance = CarrierClaimData()
"""
InpatientClaimData instance
"""
inpatientClaimDataInstance = InpatientClaimData()
"""
OutpatientClaimData instance
"""
outpatientClaimDataInstance = OutpatientClaimData()
"""
PrescriptionClaimData instance
"""
prescriptionClaimDataInstance = PrescriptionClaimData()

"""
Claim data instance mappings
"""
ClaimDataInstances = {
    'Beneficiary': beneficiaryClaimDataInstance,
    'Carrier': carrierClaimDataInstance,
    'Inpatient': inpatientClaimDataInstance,
    'Outpatient': outpatientClaimDataInstance,
    'Prescription': prescriptionClaimDataInstance
}

def parseFloat(elem, path):
    """
    Parse element text as float
    Parameters:
    - elem : the element
    - path : the element path 
    
    Return float value if parsable, otherwise None
    """
    text = elem.findtext(path)
    if text != None:
        return float(text)
    else:
        return None

def parseInt(elem, path):
    """
    Parse element text as int
    Parameters:
    - elem : the element
    - path : the element path 
    
    Return int value if parsable, otherwise None
    """
    text = elem.findtext(path)
    if text != None:
        return int(text)
    else:
        return None

def parseStr(elem, path):
    """
    Parse element text as string
    Parameters:
    - elem : the element
    - path : the element path 
    
    Return string value if parsable, otherwise None
    """
    text = elem.findtext(path)
    if text != None:
        return text
    else:
        return None

def parseDate(elem, path):
    """
    Parse element text as date
    Parameters:
    - elem : the element
    - path : the element path 
    
    Return date value if parsable, otherwise None
    """
    text = elem.findtext(path)
    if text != None:
        try:
            return datetime.datetime.strptime(text, '%Y-%m-%d')
        except:
            try:
                return datetime.datetime.strptime(text, '%Y%m%d')
            except:
                return None
    else:
        return None
    
def parseDateTime(elem, path):
    """
    Parse element text as datetime
    Parameters:
    - elem : the element
    - path : the element path 
    
    Return datetime value if parsable, otherwise None
    """
    text = elem.findtext(path)
    if text != None:
        return datetime.datetime.strptime('%Y-%m-%d %H:%M:%S', text)
    else:
        return None

def parseData(obj, elem, classInstance, mappings):
    """
    Parse claim data from xml.
    
    Parameters:
    - obj : the claim data object
    - elem : the xml element containing claim data
    - classInstance : claim data model instance
    - mappings : xml mapping config
    
    Return None
    """
    for fieldName, elemName in mappings.items():
        field = getattr(classInstance, fieldName)
        path = './' + elemName
        tp = getClaimFieldType(fieldName)
        if tp == 'int':
            obj[fieldName] = parseInt(elem, path)
        elif tp == 'float':
            obj[fieldName] = parseFloat(elem, path)
        elif tp == 'date':
            obj[fieldName] = parseDate(elem, path)
        elif tp == 'datetime':
            obj[fieldName] = parseDateTime(elem, path)
        else:
            obj[fieldName] = parseStr(elem, path)

def getClaimFieldType(fieldName):
    """
    Get type of fields of claims.
    
    Parameters:
    - fieldName: name of the field
    
    Return the type
    """
    tp = None
    try:
        tp = CLAIM_DATA_FIELDS['Beneficiary'][fieldName]['type']
    except:
        pass
    if tp is not None:
        return tp
    try:
        tp = CLAIM_DATA_FIELDS['Carrier'][fieldName]['type']
    except:
        pass
    if tp is not None:
        return tp
    try:
        tp = CLAIM_DATA_FIELDS['Inpatient'][fieldName]['type']
    except:
        pass
    if tp is not None:
        return tp
    try:
        tp = CLAIM_DATA_FIELDS['Outpatient'][fieldName]['type']
    except:
        pass
    if tp is not None:
        return tp
    try:
        tp = CLAIM_DATA_FIELDS['Prescription'][fieldName]['type']
    except:
        pass
    if tp is not None:
        return tp
    return 'unknown'

def parseBeneficiaryClaim(obj, elem):
    """
    Parse BeneficiaryClaim data from xml element.
    
    Parameters:
    - obj : the claim data object
    - elem : the xml element containing claim data
    
    Return the claim data object
    """
    parseData(obj, elem, beneficiaryClaimDataInstance, BeneficiaryClaimMappings)
    return obj

def parseCarrierClaimData(obj, elem):
    """
    Parse CarrierClaim data from xml element.
    
    Parameters:
    - obj : the claim data object
    - elem : the xml element containing claim data
    
    Return the claim data object
    """
    parseData(obj, elem, carrierClaimDataInstance, CarrierClaimMappings)
    return obj

def parseInpatientClaimData(obj, elem):
    """
    Parse InpatientClaim data from xml element.
    
    Parameters:
    - obj : the claim data object
    - elem : the xml element containing claim data
    
    Return the claim data object
    """
    parseData(obj, elem, inpatientClaimDataInstance, InPatientClaimMappings)
    return obj
    
def parseOutpatientClaimData(obj, elem):
    """
    Parse OutpatientClaim data from xml element.
    
    Parameters:
    - obj : the claim data object
    - elem : the xml element containing claim data
    
    Return the claim data object
    """
    parseData(obj, elem, outpatientClaimDataInstance, OutPatientClaimMappings)
    return obj

def parsePrescriptionClaimData(obj, elem):
    """
    Parse PrescriptionClaim data from xml element.
    
    Parameters:

    - obj : the claim data object
    - elem : the xml element containing claim data
    
    Return the claim data object
    """
    parseData(obj, elem, prescriptionClaimDataInstance, PrescriptionClaimMappings)
    return obj

"""
Claim data field configurations.
"""
claimFieldConfigs = {}

def getClaimFieldConfig(claimType):
    """
    Get claim data field configurations of given type
    Parameters:
    - claimType : claim date type
    
    Return claim data field configurations of given type
    """
    global claimFieldConfigs
    config = claimFieldConfigs.get(claimType)
    if config:
        return config
    else:
        config = [x for x in CLAIM_DATA_FIELDS[claimType].values()]
        config.sort(key=lambda v:v['order'])
        claimFieldConfigs[claimType] = config
        return config

def createClaimDataDetail(claimType, claimData):
    """
    Create claim data detail for template usage
    
    Parameters:
    - claimType : claim data type
    - claimData : the claim data object
    
    Return claim data detail for template usage
    """
    detail = []
    config = getClaimFieldConfig(claimType)
    i = 0
    for i in range(len(config)):
        if (i % 2) == 0:
            detail.append([None, None]);
        detail[i // 2][i % 2] = {'column_name': config[i]['column_name'], 'value': getattr(claimData, config[i]['field_name'])}
        
    if i and (i % 2) == 0:
        detail[i // 2][1] = {'column_name':'', 'value':''}
    
    return detail

def createClaimDataDetails(claimType, claimDataList):
    """
    Create claim data details for template usage
    
    Parameters:
    - claimType : claim data type
    - claimDataList: claim data list
    
    Return the modified claim data list
    """
    for claimData in claimDataList:
        claimData.detail = createClaimDataDetail(claimType, claimData)
    return claimDataList

def getClaimColumns(claimType, selected_fields):
    """
    Create claim columns for template usage to config selected fields
    
    Parameters:
    - request : the HTTP request
    - study_id : the study ID
    
    Return claim columns for template usage to config selected fields
    """
    columns = []
    config = getClaimFieldConfig(claimType)
    rowcnt = (len(config) // 6) + (((len(config) % 6) > 0) and 1 or 0)
    for _ in range(6):
        columns.append([None] * rowcnt);
    i = 0
    for i in range(len(config)):
        columns[i % 6][i // 6] = {'field_name': config[i]['field_name'], 'column_name': config[i]['column_name'], 'selected': config[i]['field_name'] in selected_fields};
    return columns


def check_not_none_or_empty(val, name):
    '''
    Check if the given value is None or empty string.

    @param val: the given value to check
    @param name: name of val
    @raise TypeError: if val is not of type string
    @raise ValueError: if val is None or empty string
    '''
    if val == None:
        raise ValueError('Object ' + name + ' should not be None.')
    if not isinstance(val, str):
        raise TypeError('Object ' + name + ' should be a string.')
    if len(val.strip()) == 0:
        raise ValueError('Object ' + name + ' should not empty string.')

def get_page_size(instance):
    '''
    Get page_size parameter from request parameter.

    @param instance: the django view object
    @return: the number of items to show in each page
    '''
    page_size = instance.request.REQUEST.get('page_size', instance.paginate_by)
    if isinstance(page_size, str) and page_size.lower() == 'all':
        page_size = 65536

    return page_size

def log_entrance(logger, signature, parasMap):
    '''
    Logs for entrance into public methods at DEBUG level.

    @param logger: the logger object
    @param signature: the method signature
    @param parasMap: the passed parameters
    '''
    logger.debug('[Entering method ' + signature + ']')
    if parasMap != None and len(parasMap.items()) > 0:
        paraStr = '[Input parameters['
        for (k,v) in parasMap.items():
            paraStr += (str(k) + ':' + str(v) + ', ')
        paraStr += ']]'
        logger.debug(paraStr)

def log_exit(logger, signature, parasList):
    '''
    Logs for exit from public methods at DEBUG level.

    @param logger: the logger object
    @param signature: the method signature
    @param parasList: the objects to return
    '''
    logger.debug('[Exiting method ' + signature + ']')
    if parasList != None and len(parasList) > 0:
        logger.debug('[Output parameter ' + str(parasList) + ']')

def log_exception(logger, signature, e):
    '''
    Logging exception at ERROR level.

    @param logger: the logger object
    @param signature: the method signature
    @param e: the error
    '''
    # This will log the traceback.
    logger.error('[Error in method ' + signature + ': Details ' + str(e) + ']')
    return e

def remove_url_para(url, keyToRemove):
    '''
    Remove one parameter from the prepared url parameters.

    @param url: the url query string that may contains one key matchs keyToRemove
    @param keyToRemove: the key to remove from url
    @return: the result url
    '''
    paras = url.split('&')
    nparas = []
    for para in paras:
        if not para.startswith(keyToRemove + '='):
            nparas.append(para)
    return '&'.join(nparas)

def handleHTTPError(e, signature):
    """
    Handle HTTP error
    
    Parameters:
    - claimType : claim data type
    """
    # Parse response XML
    resp_content = e.read().decode('utf-8')
    logger.debug('response:%s',resp_content)
    try:
        root = ElementTree.fromstring(resp_content)
        # Not succeeded
        # 400, 401, 403 or 500
        error_code = root.findtext('./ErrorCode')
        error_message = root.findtext('./ErrorMessage')
        # Log error code and error message
        logginghelper.method_exit(logger, signature, \
            'HTTP error code:%s, error message:%s'%(error_code, error_message))
    except Exception as e:
        logger.exception("")

def create_node_request(service_name):
    '''
    Create a request to node
    
    Parameters:
    - service_name : the service name
    
    Return a request to node
    @since 1.1
    '''
    request_to_node = urllib.request.Request(HFPP_NODE_HTTP_SERVICE_BASE_URL + service_name)
    request_to_node.add_header('Content-Type','application/xml;charset=utf-8')
    request_to_node.add_header('x-hfpp-username', HFPP_PARTNER_USERNAME)
    request_to_node.add_header('x-hfpp-password', HFPP_PARTNER_PASSWORD)
    return request_to_node

def pull_hub_roles():
    '''
    Pull role list from hub
    
    Return role list of hub
    @since 1.1
    '''
    signature = 'pull_hub_roles'
    logginghelper.method_enter(logger, signature)
    request_to_node = create_node_request('/general_service')

    request_xml = '<?xml version="1.0" encoding="utf-8"?>' \
        '<PartnerRoleListRequest>' \
        '</PartnerRoleListRequest>'
    try:
        response_from_node = urllib.request.urlopen(request_to_node, request_xml.encode(), \
            cafile=CA_CERTIFICATE_FILE, cadefault=CA_DEFAULT)
        resp_content = response_from_node.read().decode('utf-8')
        logger.debug('response:%s',resp_content)
        root = ElementTree.fromstring(resp_content)
        roleElements = root.findall('./Role')
        roles = []
        for roleElement in roleElements:
            role_id = roleElement.findtext('./RoleID')
            name = roleElement.findtext('./Name')
            roles.append({'id':role_id, 'name': name})
        logginghelper.method_exit(logger, signature, roles)
        return roles
    except urllib.error.HTTPError as e:
        handleHTTPError(e, signature)

def pull_hub_partner(partner_id):
    '''
    Pull partner from hub
    
    Parameters:
    - partner_id : the partner id
    
    Return partner of hub
    @since 1.1
    '''
    signature = 'pull_hub_partner(partner_id)'
    logginghelper.method_enter(logger, signature, {'partner_id':partner_id})
    request_to_node = create_node_request('/general_service')

    request_xml = '<?xml version="1.0" encoding="utf-8"?>' \
        '<PartnerGetRequest>' \
        '<PartnerID>{partner_id}</PartnerID>' \
        '</PartnerGetRequest>'.format(partner_id=partner_id)
    try:
        response_from_node = urllib.request.urlopen(request_to_node, request_xml.encode(), \
            cafile=CA_CERTIFICATE_FILE, cadefault=CA_DEFAULT)
        resp_content = response_from_node.read().decode('utf-8')
        logger.debug('response:%s',resp_content)
        root = ElementTree.fromstring(resp_content)
        username = root.findtext('./Username')
        organization_name = root.findtext('./OrganizationName')
        role_id = root.findtext('./RoleID')
        auto_retrieve_cached_data = root.findtext('./AutoRetrieveCachedData')
        partner = {'username':username,'organization_name':organization_name,'role_id':role_id, \
            'auto_retrieve_cached_data':auto_retrieve_cached_data}
        logginghelper.method_exit(logger, signature, partner)
        return partner
    except urllib.error.HTTPError as e:
        handleHTTPError(e, signature)

def add_hub_partner(username, organization_name, role_id, auto_retrieve_cached_data, password):
    '''
    Add partner to hub
    
    Parameters:
    - username : the username
    - organization_name : the organization nae
    - role_id : the role id
    - auto_retrieve_cached_data : whether auto retrieve cached data
    - password : the password
    
    Return partner id if success
    @since 1.1
    '''
    signature = 'add_hub_partner(username, organization_name, role_id, auto_retrieve_cached_data, password)'
    logginghelper.method_enter(logger, signature, {'username':username, 'organization_name': organization_name, \
        'role_id': role_id, 'auto_retrieve_cached_data': auto_retrieve_cached_data, 'password': password})
    request_to_node = create_node_request('/general_service')

    request_xml = '<?xml version="1.0" encoding="utf-8"?>' \
        '<PartnerAddRequest>' \
        '<Username>{username}</Username>' \
        '<Password>{password}</Password>' \
        '<OrganizationName>{organization_name}</OrganizationName>' \
        '<RoleID>{role_id}</RoleID>' \
        '<AutoRetrieveCachedData>{auto_retrieve_cached_data}</AutoRetrieveCachedData>' \
        '</PartnerAddRequest>'.format(username=username,organization_name=organization_name,role_id=role_id,\
            auto_retrieve_cached_data=auto_retrieve_cached_data,password=password)
    try:
        response_from_node = urllib.request.urlopen(request_to_node, request_xml.encode(), \
            cafile=CA_CERTIFICATE_FILE, cadefault=CA_DEFAULT)
        resp_content = response_from_node.read().decode('utf-8')
        logger.debug('response:%s',resp_content)
        root = ElementTree.fromstring(resp_content)
        partner_id = root.findtext('./PartnerID')
        logginghelper.method_exit(logger, signature, partner_id)
        return partner_id
    except urllib.error.HTTPError as e:
        handleHTTPError(e, signature)

def edit_hub_partner(partner_id, username, organization_name, role_id, auto_retrieve_cached_data, password = ''):
    '''
    Edit partner of hub

    Parameters:
    - partner_id : the partner id
    - username : the username
    - organization_name : the organization nae
    - role_id : the role id
    - auto_retrieve_cached_data : whether auto retrieve cached data
    - password : the password

    Return partner id if success
    @since 1.1
    '''
    signature = 'edit_hub_partner(partner_id, username, organization_name, role_id, auto_retrieve_cached_data, password)'
    logginghelper.method_enter(logger, signature, {'partner_id': partner_id, 'username':username, \
        'organization_name': organization_name, 'role_id': role_id, \
        'auto_retrieve_cached_data': auto_retrieve_cached_data, 'password': password})
    request_to_node = create_node_request('/general_service')

    request_xml = '<?xml version="1.0" encoding="utf-8"?>' \
        '<PartnerEditRequest>' \
        '<PartnerID>{partner_id}</PartnerID>' \
        '<Username>{username}</Username>' \
        '<Password>{password}</Password>' \
        '<OrganizationName>{organization_name}</OrganizationName>' \
        '<RoleID>{role_id}</RoleID>' \
        '<AutoRetrieveCachedData>{auto_retrieve_cached_data}</AutoRetrieveCachedData>' \
        '</PartnerEditRequest>'.format(partner_id=partner_id,username=username,organization_name=organization_name,\
            role_id=role_id,auto_retrieve_cached_data=auto_retrieve_cached_data,password=password)
    try:
        response_from_node = urllib.request.urlopen(request_to_node, request_xml.encode(), \
            cafile=CA_CERTIFICATE_FILE, cadefault=CA_DEFAULT)
        resp_content = response_from_node.read().decode('utf-8')
        logger.debug('response:%s',resp_content)
        root = ElementTree.fromstring(resp_content)
        partner_id = root.findtext('./PartnerID')
        logginghelper.method_exit(logger, signature, partner_id)
        return partner_id
    except urllib.error.HTTPError as e:
        handleHTTPError(e, signature)

def delete_hub_partner(partner_id):
    '''
    Pull partner from hub
    
    Parameters:
    - partner_id : the partner id
        
    Return partner id if success
    @since 1.1
    '''
    signature = 'delete_hub_partner(partner_id)'
    logginghelper.method_enter(logger, signature, {'partner_id':partner_id})
    request_to_node = create_node_request('/general_service')

    request_xml = '<?xml version="1.0" encoding="utf-8"?>' \
        '<PartnerDeleteRequest>' \
        '<PartnerID>{partner_id}</PartnerID>' \
        '</PartnerDeleteRequest>'.format(partner_id=partner_id)
    try:
        response_from_node = urllib.request.urlopen(request_to_node, request_xml.encode(), \
            cafile=CA_CERTIFICATE_FILE, cadefault=CA_DEFAULT)
        resp_content = response_from_node.read().decode('utf-8')
        logger.debug('response:%s',resp_content)
        root = ElementTree.fromstring(resp_content)
        partner_id = root.findtext('./PartnerID')
        logginghelper.method_exit(logger, signature, partner_id)
        return partner_id

    except urllib.error.HTTPError as e:
        handleHTTPError(e, signature)

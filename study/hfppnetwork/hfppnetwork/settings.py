'''
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
Django settings for hfppnetwork project.

@author: TCSASSEMBLER
@version: 1.0
'''

import os
BASE_DIR = os.path.dirname(os.path.dirname(__file__))

DEBUG = True
TEMPLATE_DEBUG = DEBUG

ADMINS = (
    # ('Your Name', 'your_email@example.com'),
)

MANAGERS = ADMINS

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'hfpp_study',
        'USER': 'root',
        'PASSWORD': 'mysql',
        'HOST': 'localhost',
        'PORT': '3306',
    }
}

# Hosts/domain names that are valid for this site; required if DEBUG is False
# See https://docs.djangoproject.com/en/1.5/ref/settings/#allowed-hosts
ALLOWED_HOSTS = []

# Local time zone for this installation. Choices can be found here:
# http://en.wikipedia.org/wiki/List_of_tz_zones_by_name
# although not all choices may be available on all operating systems.
# In a Windows environment this must be set to your system time zone.
TIME_ZONE = 'America/Chicago'

# Language code for this installation. All choices can be found here:
# http://www.i18nguy.com/unicode/language-identifiers.html
LANGUAGE_CODE = 'en-us'

SITE_ID = 1

# If you set this to False, Django will make some optimizations so as not
# to load the internationalization machinery.
USE_I18N = True

# If you set this to False, Django will not format dates, numbers and
# calendars according to the current locale.
USE_L10N = True

# If you set this to False, Django will not use timezone-aware datetimes.
USE_TZ = True

# Absolute filesystem path to the directory that will hold user-uploaded files.
# Example: "/var/www/example.com/media/"
MEDIA_ROOT = ''

# URL that handles the media served from MEDIA_ROOT. Make sure to use a
# trailing slash.
# Examples: "http://example.com/media/", "http://media.example.com/"
MEDIA_URL = ''

# Absolute path to the directory static files should be collected to.
# Don't put anything in this directory yourself; store your static files
# in apps' "static/" subdirectories and in STATICFILES_DIRS.
# Example: "/var/www/example.com/static/"
STATIC_ROOT = ''

# URL prefix for static files.
# Example: "http://example.com/static/", "http://static.example.com/"
STATIC_URL = '/static/'

# Additional locations of static files
STATICFILES_DIRS = (
    # Put strings here, like "/home/html/static" or "C:/www/django/static".
    # Always use forward slashes, even on Windows.
    # Don't forget to use absolute paths, not relative paths.
)

# List of finder classes that know how to find static files in
# various locations.
STATICFILES_FINDERS = (
    'django.contrib.staticfiles.finders.FileSystemFinder',
    'django.contrib.staticfiles.finders.AppDirectoriesFinder',
#    'django.contrib.staticfiles.finders.DefaultStorageFinder',
)

# Make this unique, and don't share it with anybody.
SECRET_KEY = 'znqu1xi07wy$alksdgl@m8q2o!tx$9#li2)e0*ktufn1e+os9&'

# List of callables that know how to import templates from various sources.
TEMPLATE_LOADERS = (
    'django.template.loaders.filesystem.Loader',
    'django.template.loaders.app_directories.Loader',
#     'django.template.loaders.eggs.Loader',
)

MIDDLEWARE_CLASSES = (
    'django.middleware.common.CommonMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    # Uncomment the next line for simple clickjacking protection:
    # 'django.middleware.clickjacking.XFrameOptionsMiddleware',
)

ROOT_URLCONF = 'hfppnetwork.urls'

# Python dotted path to the WSGI application used by Django's runserver.
WSGI_APPLICATION = 'hfppnetwork.wsgi.application'

TEMPLATE_DIRS = (
    # Put strings here, like "/home/html/django_templates" or "C:/www/django/templates".
    # Always use forward slashes, even on Windows.
    # Don't forget to use absolute paths, not relative paths.
    'templates',
)

INSTALLED_APPS = (
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.sites',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'django.contrib.admin',
    # Uncomment the next line to enable admin documentation:
    # 'django.contrib.admindocs',
    'widget_tweaks',
    'hfppnetwork.sms',
)

SESSION_SERIALIZER = 'django.contrib.sessions.serializers.JSONSerializer'

# A sample logging configuration. The only tangible logging
# performed by this configuration is to send an email to
# the site admins on every HTTP 500 error when DEBUG=False.
# See http://docs.djangoproject.com/en/dev/topics/logging for
# more details on how to customize your logging configuration.
LOGGING = {
    'version': 1,
    'disable_existing_loggers': False,
    'formatters': {
        'verbose': {
            'format': '%(levelname)s %(asctime)s %(module)s %(process)d %(thread)d %(message)s'
        },
        'simple': {
            'format': '%(levelname)s %(message)s'
        },
    },
    'filters': {
        'require_debug_false': {
            '()': 'django.utils.log.RequireDebugFalse'
        }
    },
    'handlers': {
        'mail_admins': {
            'level': 'ERROR',
            'filters': ['require_debug_false'],
            'class': 'django.utils.log.AdminEmailHandler'
        },
        'console':{
            'level': 'DEBUG',
            'class': 'logging.StreamHandler',
            'formatter': 'simple'
        },
    },
    'loggers': {
        'django.request': {
            'handlers': ['mail_admins'],
            'level': 'ERROR',
            'propagate': True,
        },
        'hfppnetwork': {
            'handlers': ['console'],
            'level': 'DEBUG',
            'propagate': True,
        },
    }
}


# Set the static path so that django can find the js/css/png files
STATIC_PATH = 'media'

"""
Represents the base URL of HFPP Network Node HTTP Services.
It should be non-None/empty str.
Required.
"""
HFPP_NODE_HTTP_SERVICE_BASE_URL = 'https://ec2-184-72-145-164.compute-1.amazonaws.com/NetworkNode1/services'
"""
Represents the CA certificate file path used
as the SSL certificate for accessing HFPP Network Node HTTP Services via HTTPS.
It should be non-None/empty str.
Required.
"""
CA_CERTIFICATE_FILE = '/usr/local/tomcat/conf/cert.cer'
"""
Represents the CA default indicator
"""
CA_DEFAULT = True
"""
Represents the HFPP Network Partner ID of this partner client.
It should be non-None/empty str. It is supposed to be a UUID.
Required.
"""
HFPP_PARTNER_ID = '091f80d7-8ecb-429c-8f0b-caeaae18dcd8'
"""
Represents the HFPP Network username of this partner client.
It should be non-None/empty str.
Required
"""
HFPP_PARTNER_USERNAME = 'user1'
"""
Represents the HFPP Network password of this partner client.
It should be non-None/empty str.
Required.
"""
HFPP_PARTNER_PASSWORD = 'pass1'
"""
Represents the HFPP Network port of this partner client.
It should be an integer in range [0, 65535]
Required.
"""
PARTNER_CLIENT_HTTP_SERVICE_PORT = 8071

"""
The "Study Finalization" job execution interval (in seconds).
Integer.
Required.
"""
STUDY_FINALIZATION_JOB_INTERVAL = 6000

"""
The "Pull Partner Statistics" job execution interval (in seconds).
Integer.
Required.
"""
PULL_PARTNER_STATISTICS_JOB_INTERVAL = 60

"""
The default study expiration time (in seconds).
Integer.
Required.
"""
DEFAULT_STUDY_EXPIRATION_TIME = 12000

"""
The default partner request timeout.
Integer.
Required.
"""
PARTNER_REQUEST_TIMEOUT = 10

"""
The directory to save the study report files.
String.
Required.
"""
STUDY_REPORT_DIRECTORY = "study"

"""
The email sender address.
String.
Required.
"""
EMAIL_SENDER_EMAIL_ADDRESS = "gctest589@gmail.com"

"""
The subject of the study report email.
Can use variable {study[xxx]} to include study information.
String.
Required.
"""
STUDY_REPORT_EMAIL_SUBJECT = "test email subject"

"""
The body of the study report email.
Can use variable {study[xxx]} to include study information.
String.
Required.
"""
STUDY_REPORT_EMAIL_BODY = "report for query {study[description]}"

"""
The SMTP server host.
String.
Required.
"""
SMTP_SERVER_HOST = "smtp.gmail.com"

"""
The SMTP server port.
Integer in range [0, 65535]
Required.
"""
SMTP_SERVER_PORT = 587

"""
The SMTP server user name.
Optional.
"""
SMTP_SERVER_USER_NAME ="gctest589@gmail.com"


"""
The SMTP server user password.
Optional.
"""
SMTP_SERVER_USER_PASSWORD = 'tc0pc0AD'

"""
The SMTP server use https or not.
Required.
"""
SMTP_SERVER_HTTPS = True

"""
The initial responded requests value.
This should be the same value as initialRespondedRequestsValue in build.properties of hub.
Required.
"""
INITIAL_RESPONDED_REQUESTS_VALUE = 1

"""
The claim data field configuration.
"""
CLAIM_DATA_FIELDS = {
    'Beneficiary' : {
        'desynpuf_id' : {'order': 1, 'column_name': 'Beneficiary Code', 'field_name': 'desynpuf_id', 'type': 'str'},
        'bene_birth_dt' : {'order': 2, 'column_name': 'Date of Birth', 'field_name': 'bene_birth_dt', 'type': 'date'},
        'bene_death_dt' : {'order': 3, 'column_name': 'Date of Death', 'field_name': 'bene_death_dt', 'type': 'date'},
        'bene_sex_ident_cd': {'order': 4, 'column_name': 'Sex', 'field_name': 'bene_sex_ident_cd', 'type': 'str'},
        'bene_race_cd': {'order': 5, 'column_name': 'Race Code', 'field_name': 'bene_race_cd', 'type': 'str'},
        'bene_esrd_ind': {'order': 6, 'column_name': 'End Stage Renal Disease Indicator', 'field_name': 'bene_esrd_ind', 'type': 'str'},
        'sp_state_code': {'order': 7, 'column_name': 'State Code', 'field_name': 'sp_state_code', 'type': 'str'},
        'bene_county_cd': {'order': 8, 'column_name': 'County Code', 'field_name': 'bene_county_cd', 'type': 'str'},
        'bene_hi_cvrage_tot_mons': {'order': 9, 'column_name': 'PartA Coverage Total Months', 'field_name': 'bene_hi_cvrage_tot_mons', 'type': 'int'},
        'bene_smi_cvrage_tot_mons': {'order': 10, 'column_name': 'PartB Coverage Total Months', 'field_name': 'bene_smi_cvrage_tot_mons', 'type': 'int'},
        'bene_hmo_cvrage_tot_mons': {'order': 11, 'column_name': 'HMO Coverage Total Months', 'field_name': 'bene_hmo_cvrage_tot_mons', 'type': 'int'},
        'plan_cvrg_mos_num': {'order': 12, 'column_name': 'PartD Plan Coverage Total Months', 'field_name': 'plan_cvrg_mos_num', 'type': 'int'},
        'sp_alzhdmta': {'order': 13, 'column_name': 'Alzheimer Or Related Disorders Or Senile', 'field_name': 'sp_alzhdmta', 'type': 'str'},
        'sp_chf': {'order': 14, 'column_name': 'Heart Failure', 'field_name': 'sp_chf', 'type': 'str'},
        'sp_chrnkidn': {'order': 15, 'column_name': 'Chronic Kidney Disease', 'field_name': 'sp_chrnkidn', 'type': 'str'},
        'sp_cncr': {'order': 16, 'column_name': 'Cancer', 'field_name': 'sp_cncr', 'type': 'str'},
        'sp_copd': {'order': 17, 'column_name': 'Chronic Obstructive Pulmonary Disease', 'field_name': 'sp_copd', 'type': 'str'},
        'sp_depressn': {'order': 18, 'column_name': 'Depression', 'field_name': 'sp_depressn', 'type': 'str'},
        'sp_diabetes': {'order': 19, 'column_name': 'Diabetes', 'field_name': 'sp_diabetes', 'type': 'str'},
        'sp_ischmcht': {'order': 20, 'column_name': 'Is chemic Heart Disease', 'field_name': 'sp_ischmcht', 'type': 'str'},
        'sp_osteoprs': {'order': 21, 'column_name': 'Osteoporosis', 'field_name': 'sp_osteoprs', 'type': 'str'},
        'sp_ra_oa': {'order': 22, 'column_name': 'Rheumatoid Arthritis And Osteoarthritis', 'field_name': 'sp_ra_oa', 'type': 'str'},
        'sp_strketia': {'order': 23, 'column_name': 'Stroke Ischemic Attack', 'field_name': 'sp_strketia', 'type': 'str'},
        'medreimb_ip': {'order': 24, 'column_name': 'Inpatient Annual Medicare Reimbursement Amount', 'field_name': 'medreimb_ip', 'type': 'float'},
        'benres_ip': {'order': 25, 'column_name': 'Inpatient Annual Beneficiary Responsibility Amount', 'field_name': 'benres_ip', 'type': 'float'},
        'pppymt_ip': {'order': 26, 'column_name': 'Inpatient Annual PrimaryPayer Reimbursement Amount', 'field_name': 'pppymt_ip', 'type': 'float'},
        'medreimb_op': {'order': 27, 'column_name': 'Outpatient Institutional Annual Medicare Reimbursement Amount', 'field_name': 'medreimb_op', 'type': 'float'},
        'benres_op': {'order': 28, 'column_name': 'Outpatient Institutional Annual Beneficiary Responsibility Amount', 'field_name': 'benres_op', 'type': 'float'},
        'pppymt_op': {'order': 29, 'column_name': 'Outpatient Institutional Annual Primary Payer Reimbursement Amount', 'field_name': 'pppymt_op', 'type': 'float'},
        'medreimb_car': {'order': 30, 'column_name': 'Carrier Annual Medicare Reimbursement Amount', 'field_name': 'medreimb_car', 'type': 'float'},
        'benres_car': {'order': 31, 'column_name': 'Carrier Annual Beneficiary Responsibility Amount', 'field_name': 'benres_car', 'type': 'float'},
        'pppymt_car': {'order': 32, 'column_name': 'Carrier Annual Primary Payer Reimbursement Amount', 'field_name': 'pppymt_car', 'type': 'float'},
        # add other fields based on requirement
    },
    'Carrier' : {
        'desynpuf_id' : {'order': 1,'column_name': 'Beneficiary Code', 'field_name': 'desynpuf_id', 'type': 'str'},
        'clm_id': {'order': 2,'column_name': 'Claim Id', 'field_name': 'clm_id', 'type': 'str'},
        'clm_from_dt': {'order': 3,'column_name': 'Start Date', 'field_name': 'clm_from_dt', 'type': 'date'},
        'clm_thru_dt': {'order': 4,'column_name': 'End Date', 'field_name': 'clm_thru_dt', 'type': 'date'},
        'icd9_dgns_cd_1': {'order': 5,'column_name': 'Diagnosis Code', 'field_name': 'icd9_dgns_cd_1', 'type': 'str'},
        'prf_physn_npi_1': {'order': 6,'column_name': 'Physician NPI', 'field_name': 'prf_physn_npi_1', 'type': 'str'},
        'tax_num_1': {'order': 7,'column_name': 'Institution Tax Number', 'field_name': 'tax_num_1', 'type': 'str'},
        'hcpcs_cd_1': {'order': 8,'column_name': 'HCPCS CODE', 'field_name': 'hcpcs_cd_1', 'type': 'str'},
        'line_nch_pmt_amt_1': {'order': 9,'column_name': 'Line NCH Payment Amount', 'field_name': 'line_nch_pmt_amt_1', 'type': 'float'},
        'line_bene_ptb_ddctbl_amt_1': {'order': 10,'column_name': 'Line Beneficiary PartB Deductible Amount', 'field_name': 'line_bene_ptb_ddctbl_amt_1', 'type': 'float'},
        'line_bene_prmry_pyr_pd_amt_1': {'order': 11,'column_name': 'Line Beneficiary Primary Payer Paid Amount', 'field_name': 'line_bene_prmry_pyr_pd_amt_1', 'type': 'float'},
        'line_coinsrnc_amt_1': {'order': 12,'column_name': 'Line Coinsurance Amount', 'field_name': 'line_coinsrnc_amt_1', 'type': 'float'},
        'line_alowd_chrg_amt_1': {'order': 13,'column_name': 'Line Allowed Charge Amount', 'field_name': 'line_alowd_chrg_amt_1', 'type': 'float'},
        'line_prcsg_ind_cd_1': {'order': 14,'column_name': 'Line Processing Indicator Code', 'field_name': 'line_prcsg_ind_cd_1', 'type': 'str'},
        'line_icd9_dgns_cd_1': {'order': 15,'column_name': 'Line Diagnosis Code', 'field_name': 'line_icd9_dgns_cd_1', 'type': 'str'},
        # add other fields based on requirement
    },
    'Inpatient' : {
        'desynpuf_id' : {'order': 1,'column_name': 'Beneficiary Code', 'field_name': 'desynpuf_id', 'type': 'str'},
        'clm_id': {'order': 2,'column_name': 'Claim Id', 'field_name': 'clm_id', 'type': 'str'},
        'clm_from_dt': {'order': 3,'column_name': 'Start Date', 'field_name': 'clm_from_dt', 'type': 'date'},
        'clm_thru_dt': {'order': 4,'column_name': 'End Date', 'field_name': 'clm_thru_dt', 'type': 'date'},
        'prvdr_num': {'order': 5,'column_name': 'Provider Institution', 'field_name': 'prvdr_num', 'type': 'str'},
        'clm_pmt_amt': {'order': 6,'column_name': 'Payment Amount', 'field_name': 'clm_pmt_amt', 'type': 'float'},
        'nch_prmry_pyr_clm_pd_amt': {'order': 7,'column_name': 'NCH Primary Payer Claim Paid Amount', 'field_name': 'nch_prmry_pyr_clm_pd_amt', 'type': 'float'},
        'at_physn_npi': {'order': 8,'column_name': 'Attending Physician NPI', 'field_name': 'at_physn_npi', 'type': 'str'},
        'op_physn_npi': {'order': 9,'column_name': 'Operating Physician NPI', 'field_name': 'op_physn_npi', 'type': 'str'},
        'ot_physn_npi': {'order': 11,'column_name': 'Other Physician NPI', 'field_name': 'ot_physn_npi', 'type': 'str'},
        'clm_admsn_dt': {'order': 12,'column_name': 'Admission Date', 'field_name': 'clm_admsn_dt', 'type': 'date'},
        'admtng_icd9_dgns_cd': {'order': 13,'column_name': 'Claim Admitting Diagnosis Code', 'field_name': 'admtng_icd9_dgns_cd', 'type': 'str'},
        'clm_pass_thru_per_diem_amt': {'order': 14,'column_name': 'Claim Pass Thru Per Diem Amount', 'field_name': 'clm_pass_thru_per_diem_amt', 'type': 'float'},
        'nch_bene_ip_ddctbl_amt': {'order': 15,'column_name': 'NCH Beneficiary Inpatient Deductible Amount', 'field_name': 'nch_bene_ip_ddctbl_amt', 'type': 'str'},
        'nch_bene_pta_coinsrnc_lblty_am': {'order': 16,'column_name': 'NCH Beneficiary PartA Coinsurance Liability Amount', 'field_name': 'nch_bene_pta_coinsrnc_lblty_am', 'type': 'float'},
        'nch_bene_blood_ddctbl_lblty_am': {'order': 17,'column_name': 'NCH Beneficiary Blood Deductible Liability Amount', 'field_name': 'nch_bene_blood_ddctbl_lblty_am', 'type': 'float'},
        'clm_utlztn_day_cnt': {'order': 18,'column_name': 'Claim Utilization Day Count', 'field_name': 'clm_utlztn_day_cnt', 'type': 'int'},
        'nch_bene_dschrg_dt': {'order': 19,'column_name': 'Inpatient Discharged Date', 'field_name': 'nch_bene_dschrg_dt', 'type': 'date'},
        'clm_drg_cd': {'order': 20,'column_name': 'Claim Diagnosis Related Group Code', 'field_name': 'clm_drg_cd', 'type': 'str'},
        'icd9_dgns_cd_1': {'order': 21,'column_name': 'Diagnosis Code', 'field_name': 'icd9_dgns_cd_1', 'type': 'str'},
        'icd9_prcdr_cd_1': {'order': 22,'column_name': 'Procedure Code', 'field_name': 'icd9_prcdr_cd_1', 'type': 'str'},
        'hcpcs_cd_1': {'order': 23,'column_name': 'HCPCS CODE', 'field_name': 'hcpcs_cd_1', 'type': 'str'},
        # add other fields based on requirement
    },
    'Outpatient' : {
        'desynpuf_id' : {'order': 1,'column_name': 'Beneficiary Code', 'field_name': 'desynpuf_id', 'type': 'str'},
        'clm_id': {'order': 2,'column_name': 'Claim Id', 'field_name': 'clm_id', 'type': 'str'},
        'clm_from_dt': {'order': 3,'column_name': 'Start Date', 'field_name': 'clm_from_dt', 'type': 'date'},
        'clm_thru_dt': {'order': 4,'column_name': 'End Date', 'field_name': 'clm_thru_dt', 'type': 'date'},
        'prvdr_num': {'order': 5,'column_name': 'Provider Institution', 'field_name': 'prvdr_num', 'type': 'str'},
        'clm_pmt_amt': {'order': 6,'column_name': 'Payment Amount', 'field_name': 'clm_pmt_amt', 'type': 'float'},
        'at_physn_npi': {'order': 7,'column_name': 'Attending Physician NPI', 'field_name': 'at_physn_npi', 'type': 'str'},
        'op_physn_npi': {'order': 8,'column_name': 'Operating Physician NPI', 'field_name': 'op_physn_npi', 'type': 'str'},
        'ot_physn_npi': {'order': 9,'column_name': 'Other Physician NPI', 'field_name': 'ot_physn_npi', 'type': 'str'},
        'nch_bene_blood_ddctbl_lblty_am': {'order': 10,'column_name': 'NCH Beneficiary Blood Deductible Liability Amount', 'field_name': 'nch_bene_blood_ddctbl_lblty_am', 'type': 'float'},
        'icd9_dgns_cd_1': {'order': 11,'column_name': 'Diagnosis Code', 'field_name': 'icd9_dgns_cd_1', 'type': 'str'},
        'icd9_prcdr_cd_1': {'order': 12,'column_name': 'Procedure Code', 'field_name': 'icd9_prcdr_cd_1', 'type': 'str'},
        'nch_bene_ptb_ddctbl_amt': {'order': 13,'column_name': 'NCH Beneficiary PartB Deductible Amount', 'field_name': 'nch_bene_ptb_ddctbl_amt', 'type': 'float'},
        'nch_bene_ptb_coinsrnc_amt': {'order': 14,'column_name': 'NCH Beneficiary PartB Coinsurance Amount', 'field_name': 'nch_bene_ptb_coinsrnc_amt', 'type': 'str'},
        'admtng_icd9_dgns_cd': {'order': 15,'column_name': 'Claim Admitting Diagnosis Code', 'field_name': 'admtng_icd9_dgns_cd', 'type': 'str'},
        'hcpcs_cd_1': {'order': 16,'column_name': 'HCPCS CODE', 'field_name': 'hcpcs_cd_1', 'type': 'str'},      
        # add other fields based on requirement
    },
    'Prescription' : {
        'desynpuf_id' : {'order': 1,'column_name': 'Beneficiary Code', 'field_name': 'desynpuf_id', 'type': 'str'},
        'pde_id' : {'order': 2,'column_name': 'CCW PartD Event', 'field_name': 'pde_id', 'type': 'str'},
        'srvc_dt' : {'order': 3,'column_name': 'RX Service Date', 'field_name': 'srvc_dt', 'type': 'date'},
        'prod_srvc_id' : {'order': 4,'column_name': 'Product Service ID', 'field_name': 'prod_srvc_id', 'type': 'str'},
        'qty_dspnsd_num' : {'order': 5,'column_name': 'Quantity Dispensed', 'field_name': 'qty_dspnsd_num', 'type': 'float'},
        'days_suply_num' : {'order': 6,'column_name': 'Days Supply', 'field_name': 'days_suply_num', 'type': 'int'},
        'ptnt_pay_amt' : {'order': 7,'column_name': 'Patient Pay Amount', 'field_name': 'ptnt_pay_amt', 'type': 'float'},
        'tot_rx_cst_amt' : {'order': 8,'column_name': 'Gross Drug Cost', 'field_name': 'tot_rx_cst_amt', 'type': 'float'},
    }
}

# handle the re-direct to /login, not to /accounts/login
LOGIN_URL = '/login/'

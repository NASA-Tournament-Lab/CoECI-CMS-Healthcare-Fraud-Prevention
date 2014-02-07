#!/bin/bash

cd ../partnerclientA/hfppnetwork/partner
python3 partner_database_appliance.py -o write -t beneficiary -f $HOME/hfpp/test_data/BeneficiarySummary.csv
python3 partner_database_appliance.py -o write -t carrier -f $HOME/hfpp/test_data/CarrierClaims.csv
python3 partner_database_appliance.py -o write -t inpatient -f $HOME/hfpp/test_data/InpatientClaims.csv
python3 partner_database_appliance.py -o write -t outpatient -f $HOME/hfpp/test_data/OutpatientClaims.csv
python3 partner_database_appliance.py -o write -t prescription -f $HOME/hfpp/test_data/PrescriptionEvents.csv

cd ../../../
cd partnerclientB/hfppnetwork/partner
python3 partner_database_appliance.py -o write -t beneficiary -f $HOME/hfpp/test_data/BeneficiarySummary.csv
python3 partner_database_appliance.py -o write -t carrier -f $HOME/hfpp/test_data/CarrierClaims.csv
python3 partner_database_appliance.py -o write -t inpatient -f $HOME/hfpp/test_data/InpatientClaims.csv
python3 partner_database_appliance.py -o write -t outpatient -f $HOME/hfpp/test_data/OutpatientClaims.csv
python3 partner_database_appliance.py -o write -t prescription -f $HOME/hfpp/test_data/PrescriptionEvents.csv


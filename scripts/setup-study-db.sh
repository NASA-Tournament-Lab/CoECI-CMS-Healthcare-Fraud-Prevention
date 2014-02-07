#!/bin/bash
echo "setup study database"
mysql -uroot hfpp_study -p < ../study/hfppnetwork/test_files/data.sql

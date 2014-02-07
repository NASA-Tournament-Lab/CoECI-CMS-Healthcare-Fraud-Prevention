# start decision module A
echo "start decision module A"
cd ../partnerclientA/decision_module
python3 manage.py syncdb
python3 manage.py runserver test.com:8010


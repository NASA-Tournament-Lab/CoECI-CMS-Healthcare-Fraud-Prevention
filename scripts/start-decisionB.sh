# start decision module B
echo "start decision module B"
cd ../partnerclientB/decision_module
python3 manage.py syncdb
python3 manage.py runserver test.com:8011




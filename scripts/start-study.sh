echo "Start study server..."
cd ../study/hfppnetwork
python3 manage.py syncdb
python3 manage.py runserver 8050

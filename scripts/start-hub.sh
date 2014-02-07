# Running hub
echo "Running hub..."
cd $HOME/hfpp/hub
chmod +x *.sh
chmod +x jsvc
./hub.sh start
echo "Wating for hub server start..."
sleep 10

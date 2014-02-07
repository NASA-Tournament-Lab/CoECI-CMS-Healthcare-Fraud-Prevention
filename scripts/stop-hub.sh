# Stopping hub
echo "Stopping hub..."
cd $HOME/hfpp/hub
chmod +x *.sh
chmod +x jsvc
./hub.sh stop
echo "Wating for hub server stop..."
sleep 10

#!/bin/sh

cd ../hub
echo "Compiling hub..."
mkdir $HOME/hfpp/hub
ant jar
sleep 2
ant db-drop
sleep 2
ant db-create
sleep 2
ant db-testdata
sleep 2
# Create qpid queue (Canceld because it's not stable)
ant create-queue
echo "Waiting for queue setup...(10 secs)"
sleep 10

echo "Install jsvc(Depended by hub server)"
cd conf/linux
unzip commons-daemon-1.0.15-native-src.zip
cd commons-daemon-1.0.15-native-src/unix
./configure --with-java=/usr/lib/jvm/java-6-openjdk-amd64/
make
cp jsvc $HOME/hfpp/hub

	

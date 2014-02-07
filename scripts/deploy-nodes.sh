echo "Copy schemas to $HOME/hfpp/node/"
mkdir $HOME/hfpp/node
mkdir $HOME/hfpp/node/schemas
cp -R ../node/conf/schemas/* $HOME/hfpp/node/schemas
sleep 2

cd ../

echo "Compiling node1..."
mkdir node1
cp -r ./node/* ./node1
cp -r ./conf/node1/* ./node1
cd node1
ant deployWeb
cd ..
sleep 10

echo "Compiling node2..."
mkdir node2
cp -r ./node/* ./node2
cp -r ./conf/node2/* ./node2
cd node2
ant deployWeb
cd ..
sleep 10

echo "Compiling node3..."
mkdir node3
cp -r ./node/* ./node3
cp -r ./conf/node3/* ./node3
cd node3
ant deployWeb
cd ..
echo "Waiting for 3 nodes fully installed on tomcat..."
sleep 10

# Go back to scripts folder
cd scripts/



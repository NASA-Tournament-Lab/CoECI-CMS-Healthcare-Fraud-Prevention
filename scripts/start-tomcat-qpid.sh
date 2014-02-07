
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/apr/lib
DOWNLOAD_DIR=$HOME/hfpp/third_party
TOMCAT_HOME=$DOWNLOAD_DIR/apache-tomcat-7.0.47
QPID_HOME=$DOWNLOAD_DIR/qpid-broker-0.24

# Start tomcat
echo "Copy SSL key and certificate to TOMCAT_HOME/conf"
cp ../conf/*.cer $TOMCAT_HOME/conf
cp ../conf/*.csr $TOMCAT_HOME/conf
cp ../conf/*.key $TOMCAT_HOME/conf
cp ../conf/server.xml $TOMCAT_HOME/conf
echo "Running tomcat..."
cd $TOMCAT_HOME/bin
chmod +x *.sh
./startup.sh
sleep 10

# Start qpid
echo "Running qpid..."
rm -r $DOWNLOAD_DIR/qpid_work
cd $QPID_HOME/bin
./qpid-server -prop "qpid.work_dir=$DOWNLOAD_DIR/qpid_work" -prop "qpid.http_port=10001" & > qpid.stdout &
echo "Please wait...(20 secs)"
sleep 20


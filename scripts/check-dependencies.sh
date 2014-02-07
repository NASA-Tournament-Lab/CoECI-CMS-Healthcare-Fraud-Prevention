# Setup variables
DOWNLOAD_DIR=$HOME/hfpp/third_party

function checkFileExist() {
    if [ -f "$DOWNLOAD_DIR/$1" ];  then
        return
    else
        echo "===> $1 is missing..."
    fi
}

# Check tools installed
gcc -v
openssl version
python3 -V
javac -version
mysql -V
ant -version


# Check python3 libraries
echo "Checking python3 libraries..."
python3 check-dependencies.py

# Check tomcat & qpid ...
checkFileExist apache-tomcat-7.0.47/bin/startup.sh
checkFileExist qpid-broker-0.24/bin/qpid-server
# apr/tomcat-native not checked

# Check redis database
checkFileExist redis-2.8.2/src/redis-cli
$DOWNLOAD_DIR/redis-2.8.2/src/redis-cli -v

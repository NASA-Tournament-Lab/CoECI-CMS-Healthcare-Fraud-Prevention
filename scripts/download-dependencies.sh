# Setup variables
DOWNLOAD_DIR=$HOME/hfpp/third_party
#JAVA_HOME=/usr/lib/jvm/java-6-openjdk-amd64

function downloadFile() {
    if [ -f "$DOWNLOAD_DIR/$1" ];  then
        echo "File $1 already downloaded"
        return
    else
        echo "Downloading $1"
        wget $2
    fi
    if [ -f "$DOWNLOAD_DIR/master" ];  then
        mv master $1
    fi
    if [ -f "$DOWNLOAD_DIR/master.zip" ];  then
        mv master.zip $1
    fi
    if [ -f "$DOWNLOAD_DIR/WeasyPrint-master" ];  then
        mv WeasyPrint-master $1
    fi
}

function installPylib() {
# Return here to cancel installation
return
    echo "Extract $2"
    cd $DOWNLOAD_DIR
    # For example:
    # tar -xjf Python3.3.2.tar.bz2
    # tar -zxvf SomeLib.tar.gz
    # unzip SomLib-master.zip
    $1 $2
    cd $DOWNLOAD_DIR/$3
    sudo python3 setup.py install
}

function installClib() {
# Return here to cancel installation
return
    echo "Extract $2"
    # For example:
    # tar -xjf Python3.3.2.tar.bz2
    # tar -zxvf SomeLib.tar.gz
    # unzip SomLib-master.zip
    $1 $2
    cd $DOWNLOAD_DIR/$3
    chmod +x configure
    ./configure
    make
    sudo make install
}

function aptgetDependencies() {
# Return here to cancel installation
return
    echo "Update ubuntu..."
    sudo apt-get update

    # Python 3.3 dependencies
    echo "Install python3.3 dependencies..."
    sudo apt-get install gcc g++ libreadline-dev zlib1g-dev openssl libssl-dev libxml2-dev libxslt-dev sqlite3 libsqlite3-dev

    # study/WeasyPrint dependencies
    echo "Install study/WeasyPrint dependencies..."
    sudo apt-get install libxml2-dev
    sudo ln -s /usr/include/libxml2/libxml /usr/include/libxml
    sudo apt-get install libxslt-dev
    # JDK/MySQL/Ant
    echo "Install openjdk..."
    sudo apt-get install openjdk-6-jdk
    echo "Install MySQL..."
    sudo apt-get install mysql-server mysql-client
    echo "Install ant compile tool..."
    sudo apt-get install ant
}

#
#echo "Prepare environments..."
#aptgetDependencies

# Make dir
echo "Downloading dependencies to $HOME/hfpp/third_party/"
mkdir $HOME/hfpp
mkdir $DOWNLOAD_DIR
cd $DOWNLOAD_DIR

# Download files
echo "Downloading dependencies for hfpp study server..."
downloadFile Python-3.3.2.tar.bz2 http://www.python.org/ftp/python/3.3.2/Python-3.3.2.tar.bz2
installClib "tar -xjf" Python-3.3.2.tar.bz2 Python-3.3.2
downloadFile Django-1.6.tar.gz https://www.djangoproject.com/m/releases/1.6/Django-1.6.tar.gz
installPylib "tar -zxvf" Django-1.6.tar.gz Django-1.6
downloadFile PyMySQL-master.zip https://codeload.github.com/PyMySQL/PyMySQL/zip/master
installPylib "unzip" PyMySQL-master.zip PyMySQL-master

downloadFile setuptools-1.4.2.tar.gz https://pypi.python.org/packages/source/s/setuptools/setuptools-1.4.2.tar.gz#md5=13951be6711438073fbe50843e7f141f
installPylib "tar -zxvf" setuptools-1.4.2.tar.gz setuptools-1.4.2
downloadFile libffi-3.0.13.tar.gz ftp://sourceware.org:/pub/libffi/libffi-3.0.13.tar.gz
installPylib "tar -zxvf" libffi-3.0.13.tar.gz libffi-3.0.13

downloadFile Pyphen-0.8.tar.gz https://pypi.python.org/packages/source/P/Pyphen/Pyphen-0.8.tar.gz
installPylib "tar -zxvf" Pyphen-0.8.tar.gz Pyphen-0.8
downloadFile six-1.4.1.tar.gz https://pypi.python.org/packages/source/s/six/six-1.4.1.tar.gz#md5=bdbb9e12d3336c198695aa4cf3a61d62
installPylib "tar -zxvf" six-1.4.1.tar.gz six-1.4.1
downloadFile cssselect-master.zip https://codeload.github.com/SimonSapin/cssselect/zip/master
installPylib "unzip" cssselect-master.zip cssselect-master
downloadFile tinycss-master.zip https://codeload.github.com/SimonSapin/tinycss/zip/master
installPylib "unzip" tinycss-master.zip tinycss-master
downloadFile html5lib-python-master.zip https://codeload.github.com/html5lib/html5lib-python/zip/master
installPylib "unzip" html5lib-python-master.zip html5lib-python-master

downloadFile lxml-3.2.4.tar.gz https://pypi.python.org/packages/source/l/lxml/lxml-3.2.4.tar.gz#md5=cc363499060f615aca1ec8dcc04df331
installPylib "tar -zxvf" lxml-3.2.4.tar.gz lxml-3.2.4
downloadFile cffi-0.8.1.tar.gz https://pypi.python.org/packages/source/c/cffi/cffi-0.8.1.tar.gz#md5=1a877bf113bfe90fdefedbf9e39310d2
installPylib "tar -zxvf" cffi-0.8.1.tar.gz cffi-0.8.1
downloadFile cairocffi-0.5.1.tar.gz https://pypi.python.org/packages/source/c/cairocffi/cairocffi-0.5.1.tar.gz#md5=a0c55240b07030f27d9fb07cf99bf705
installPylib "tar -zxvf" cairocffi-0.5.1.tar.gz cairocffi-0.5.1
downloadFile CairoSVG-1.0.3.tar.gz https://pypi.python.org/packages/source/C/CairoSVG/CairoSVG-1.0.3.tar.gz#md5=4976fd918fd53b47e4dd8c323ad24a9d
installPylib "tar -zxvf" CairoSVG-1.0.3.tar.gz CairoSVG-1.0.3
downloadFile XlsxWriter-0.5.2.tar.gz https://pypi.python.org/packages/source/X/XlsxWriter/XlsxWriter-0.5.2.tar.gz#md5=092dcadd949edb272fe5a259d82576a8
installPylib "tar -zxvf" XlsxWriter-0.5.2.tar.gz XlsxWriter-0.5.2

downloadFile WeasyPrint-master.zip https://github.com/Kozea/WeasyPrint/archive/master.zip
installPylib "unzip" WeasyPrint-master.zip WeasyPrint-master
downloadFile APScheduler-2.1.1.tar.gz https://pypi.python.org/packages/source/A/APScheduler/APScheduler-2.1.1.tar.gz
installPylib "tar -zxvf" APScheduler-2.1.1.tar.gz APScheduler-2.1.1
downloadFile django-widget-tweaks-1.3.tar.gz https://pypi.python.org/packages/source/d/django-widget-tweaks/django-widget-tweaks-1.3.tar.gz
installPylib "tar -zxvf" django-widget-tweaks-1.3.tar.gz django-widget-tweaks-1.3

echo "Downloading dependencies for hfpp hub/node server..."
downloadFile apache-tomcat-7.0.47.zip http://archive.apache.org/dist/tomcat/tomcat-7/v7.0.47/bin/apache-tomcat-7.0.47.zip
# Only extract tomcat
#cd $HOME/hfpp/third_party
#unzip apache-tomcat-7.0.47.zip
downloadFile qpid-java-broker-0.24.tar.gz http://mirror.bit.edu.cn/apache/qpid/0.24/qpid-java-broker-0.24.tar.gz
# Only extract qpid broker
#cd $HOME/hfpp/third_party
#tar -zxvf qpid-java-broker-0.24.tar.gz
downloadFile apr-1.5.0.tar.gz http://mirror.esocc.com/apache//apr/apr-1.5.0.tar.gz
installClib "tar -zxvf" apr-1.5.0.tar.gz apr-1.5.0
downloadFile tomcat-native-1.1.29-src.tar.gz http://apache.fayea.com/apache-mirror//tomcat/tomcat-connectors/native/1.1.29/source/tomcat-native-1.1.29-src.tar.gz
#cd $HOME/hfpp/third_party
#cd tomcat-native-1.1.29-src/jni/native/
#./configure --with-apr=/usr/local/apr --with-java-home=$JAVA_HOME
#make
#sudo make install

echo "Downloading dependencies for hfpp partner client..."
downloadFile CherryPy-3.2.4.tar.gz https://pypi.python.org/packages/source/C/CherryPy/CherryPy-3.2.4.tar.gz#md5=e2c8455e15c39c9d60e0393c264a4d16
installPylib "tar -zxvf" CherryPy-3.2.4.tar.gz CherryPy-3.2.4
downloadFile isodate-0.4.9.tar.gz https://pypi.python.org/packages/source/i/isodate/isodate-0.4.9.tar.gz
installPylib "tar -zxvf" isodate-0.4.9.tar.gz isodate-0.4.9

echo "Downloading dependencies for hfpp partner client appliance module..."
# This one is redis database
downloadFile redis-2.8.2.tar.gz http://download.redis.io/releases/redis-2.8.2.tar.gz
#cd $HOME/hfpp/third_party
#tar -zxvf redis-2.8.2.tar.gz
#cd redis-2.8.2
#make
#sudo make install
#cd utils/
#sudo ./install_server.sh
# This one is redis python lib
downloadFile redis-2.8.0.tar.gz https://pypi.python.org/packages/source/r/redis/redis-2.8.0.tar.gz
installPylib "tar -zxvf" redis-2.8.0.tar.gz redis-2.8.0

downloadFile cymysql-0.6.6.tar.gz http://pypi.python.jp/cymysql/cymysql-0.6.6.tar.gz#md5=e655cd8c8d28ab420fbd4961b79c7318
installPylib "tar -zxvf" cymysql-0.6.6.tar.gz cymysql-0.6.6
downloadFile argparse-1.2.1.tar.gz http://argparse.googlecode.com/files/argparse-1.2.1.tar.gz
installPylib "tar -zxvf" argparse-1.2.1.tar.gz argparse-1.2.1


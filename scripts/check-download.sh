
# Setup variables
DOWNLOAD_DIR=$HOME/hfpp/third_party

function downloadFile() {
    if [ -f "$DOWNLOAD_DIR/$1" ];  then
        return
    else
        echo "===> $1 is missing..."
    fi
}

function installPylib() {
    echo "Extract $2"
    # For example:
    # tar -xjf Python3.3.2.tar.bz2
    # tar -zxvf SomeLib.tar.gz
    # unzip SomLib-master.zip
    $1 $2
    cd $3
    sudo python3 setup.py install
}

function installClib() {
    echo "Extract $2"
    # For example:
    # tar -xjf Python3.3.2.tar.bz2
    # tar -zxvf SomeLib.tar.gz
    # unzip SomLib-master.zip
    $1 $2
    cd $3
    chmod +x configure
    ./configure
    make
    sudo make install
}

function aptDependencies() {
    # Python 3.3 dependencies
    echo "Install python3.3 dependencies..."
    sudo apt-get install gcc g++ libreadline-dev zlib1g-dev openssl libssl-dev libxml2-dev libxslt-dev sqlite3 libsqlite3-dev

    # study/WeasyPrint dependencies
    echo "Install study/WeasyPrint dependencies..."
    sudo apt-get install libxml2-dev
    sudo ln -s /usr/include/libxml2/libxml /usr/include/libxml
    sudo apt-get install libxslt-dev
    # Ant
    sudo apt-get update
    sudo apt-get install ant
}

# Make dir
echo "Checking downloaded dependencies at $HOME/hfpp/third_party/"
#mkdir $DOWNLOAD_DIR
#cd $DOWNLOAD_DIR

# Download files
#echo "Downloading dependencies for hfpp study server..."
downloadFile Python-3.3.2.tar.bz2 http://www.python.org/ftp/python/3.3.2/Python-3.3.2.tar.bz2
downloadFile Django-1.6.tar.gz https://www.djangoproject.com/m/releases/1.6/Django-1.6.tar.gz
downloadFile PyMySQL-master.zip https://codeload.github.com/PyMySQL/PyMySQL/zip/master

downloadFile setuptools-1.4.2.tar.gz https://pypi.python.org/packages/source/s/setuptools/setuptools-1.4.2.tar.gz#md5=13951be6711438073fbe50843e7f141f
downloadFile libffi-3.0.13.tar.gz ftp://sourceware.org:/pub/libffi/libffi-3.0.13.tar.gz

downloadFile Pyphen-0.8.tar.gz https://pypi.python.org/packages/source/P/Pyphen/Pyphen-0.8.tar.gz
downloadFile six-1.4.1.tar.gz https://pypi.python.org/packages/source/s/six/six-1.4.1.tar.gz#md5=bdbb9e12d3336c198695aa4cf3a61d62
downloadFile cssselect-master.zip https://codeload.github.com/SimonSapin/cssselect/zip/master
downloadFile tinycss-master.zip https://codeload.github.com/SimonSapin/tinycss/zip/master
downloadFile html5lib-python-master.zip https://codeload.github.com/html5lib/html5lib-python/zip/master

downloadFile lxml-3.2.4.tar.gz https://pypi.python.org/packages/source/l/lxml/lxml-3.2.4.tar.gz#md5=cc363499060f615aca1ec8dcc04df331
downloadFile cffi-0.8.1.tar.gz https://pypi.python.org/packages/source/c/cffi/cffi-0.8.1.tar.gz#md5=1a877bf113bfe90fdefedbf9e39310d2
downloadFile cairocffi-0.5.1.tar.gz https://pypi.python.org/packages/source/c/cairocffi/cairocffi-0.5.1.tar.gz#md5=a0c55240b07030f27d9fb07cf99bf705
downloadFile CairoSVG-1.0.3.tar.gz https://pypi.python.org/packages/source/C/CairoSVG/CairoSVG-1.0.3.tar.gz#md5=4976fd918fd53b47e4dd8c323ad24a9d

downloadFile WeasyPrint-master.zip https://github.com/Kozea/WeasyPrint/archive/master.zip
downloadFile APScheduler-2.1.1.tar.gz https://pypi.python.org/packages/source/A/APScheduler/APScheduler-2.1.1.tar.gz
downloadFile django-widget-tweaks-1.3.tar.gz https://pypi.python.org/packages/source/d/django-widget-tweaks/django-widget-tweaks-1.3.tar.gz

#echo "Downloading dependencies for hfpp hub/node server..."
downloadFile apache-tomcat-7.0.47.zip http://apache.dataguru.cn/tomcat/tomcat-7/v7.0.47/bin/apache-tomcat-7.0.47.zip
downloadFile qpid-java-broker-0.24.tar.gz http://mirror.bit.edu.cn/apache/qpid/0.24/qpid-java-broker-0.24.tar.gz
downloadFile tomcat-native-1.1.29-src.tar.gz http://apache.fayea.com/apache-mirror//tomcat/tomcat-connectors/native/1.1.29/source/tomcat-native-1.1.29-src.tar.gz
downloadFile apr-1.5.0.tar.gz http://mirror.esocc.com/apache//apr/apr-1.5.0.tar.gz

#echo "Downloading dependencies for hfpp partner client..."
downloadFile CherryPy-3.2.4.tar.gz https://pypi.python.org/packages/source/C/CherryPy/CherryPy-3.2.4.tar.gz#md5=e2c8455e15c39c9d60e0393c264a4d16
downloadFile isodate-0.4.9.tar.gz https://pypi.python.org/packages/source/i/isodate/isodate-0.4.9.tar.gz
#echo "Downloading dependencies for hfpp partner client appliance module..."
downloadFile redis-2.8.2.tar.gz http://download.redis.io/releases/redis-2.8.2.tar.gz
downloadFile redis-2.8.0.tar.gz https://pypi.python.org/packages/source/r/redis/redis-2.8.0.tar.gz
downloadFile cymysql-0.6.6.tar.gz http://pypi.python.jp/cymysql/cymysql-0.6.6.tar.gz#md5=e655cd8c8d28ab420fbd4961b79c7318
downloadFile argparse-1.2.1.tar.gz http://argparse.googlecode.com/files/argparse-1.2.1.tar.gz 


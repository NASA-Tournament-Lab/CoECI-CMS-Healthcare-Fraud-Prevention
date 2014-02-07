#!/bin/sh

# Setup variables
INSTALLER_FOLDER=/home/tcsassembler/hfpp/hub
EXEC=/home/tcsassembler/hfpp/hub/jsvc
JAVA_HOME=/usr/lib/jvm/java-6-openjdk-amd64
CLASS_PATH="/home/tcsassembler/hfpp/hub/lib/commons-daemon-1.0.15.jar":"/home/tcsassembler/hfpp/hub/network_hub.jar"
CLASS=com.hfpp.network.hub.NetworkHubDaemon 
#use double // or use file:
CONFIG_FILE=//home/tcsassembler/hfpp/hub/applicationContext.xml

PID=/home/tcsassembler/hfpp/hub/network_hub.pid
LOG_OUT=/home/tcsassembler/hfpp/hub/network_hub.out
LOG_ERR=/home/tcsassembler/hfpp/hub/network_hub.err
do_exec()
{
    $EXEC -home "$JAVA_HOME" -cp $CLASS_PATH -outfile $LOG_OUT -errfile $LOG_ERR -pidfile $PID $1 $CLASS $CONFIG_FILE
}

case "$1" in
    start)
        do_exec
            ;;
    stop)
        do_exec "-stop"
            ;;
    restart)
        if [ -f "$PID" ]; then
            do_exec "-stop"
            do_exec
        else
            echo "service not running, will do nothing"
            exit 1
        fi
            ;;
    *)
            echo "usage: hub {start|stop|restart}" >&2
            exit 3
            ;;
esac
#!/bin/sh

# Setup variables
INSTALLER_FOLDER=@installer_folder@
EXEC=@installer_folder@/jsvc
JAVA_HOME=@java_home@
CLASS_PATH="@installer_folder@/lib/commons-daemon-1.0.15.jar":"@installer_folder@/network_hub.jar"
CLASS=com.hfpp.network.hub.NetworkHubDaemon 
#use double // or use file:
CONFIG_FILE=/@installer_folder@/applicationContext.xml

PID=@installer_folder@/network_hub.pid
LOG_OUT=@installer_folder@/network_hub.out
LOG_ERR=@installer_folder@/network_hub.err
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
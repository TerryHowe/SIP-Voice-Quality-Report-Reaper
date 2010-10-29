#! /bin/sh
### BEGIN INIT INFO
# Provides:          reaper
# Required-Start:    $remote_fs $syslog
# Required-Stop:     $remote_fs $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Start and stop the voice quality reaper
### END INIT INFO
STARTER=com.tt.reaper.Reaper

do_start() {
   cd /opt/reaper || exit 1
   JRE=/usr/lib/jvm/java-6-openjdk/jre/bin/java
   nohup $JRE -cp /opt/reaper/lib/reaper.jar:./lib/jain-sip-sdp-1.2.142.jar:./lib/log4j-1.2.15.jar $STARTER >/opt/reaper/log/out 2>/opt/reaper/log/err &
}
do_stop() {
    ps -eaf | grep /opt/reaper/bin/bpf | grep -v grep |
    while read USER PID ROL
    do
        kill -9 $PID
    done
    ps -eaf | grep $STARTER | grep -v grep |
    while read USER PID ROL
    do
        kill -9 $PID
    done
}
status() {
    LINE=`ps -eaf | grep $STARTER | grep -v grep`
    if [ -n "${LINE}" ]
    then
    	LINE=`ps -eaf | grep bpf | grep -v grep`
    	if [ -n "${LINE}" ]
    	then
           echo $STARTER " process running"
           exit 0
	fi
    fi
    echo $STARTER " process not found"
    exit 1
}

case "$1" in
  start|restart|force-reload|reload)
     do_stop
     do_start
     ;;
  stop)
     do_stop
     ;;
  status)
     status
     ;;
  *)
     echo "Usage: $0 {start|stop|status|restart|force-reload}" >&2
     exit 3
     ;;
esac

:

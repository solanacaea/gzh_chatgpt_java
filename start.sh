#!/bin/bash

APP_NAME=GZHGPT
LOG_FOLDER=/root/logs/gzh


usage() {
echo "Usage: sh start.sh [start|stop|restart|status]"
exit 1
}


is_exist() {
pid=`ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}' `

if [ -z "${pid}" ]
then
 return 1
else
 return 0
fi
}


start() {
is_exist
if [ $? -eq "0" ]
then
 echo "${APP_NAME} is already running. pid=${pid} ."
else
  echo "starting..."
  # nohup java -jar $APP_NAME > /dev/null 2>&1 &
  nohup java -jar gzh_chatgpt_java.jar $APP_NAME > $LOG_FOLDER/server.log 2>&1 &
  echo "starting..."
fi
}

#停止方法
stop() {
is_exist
if [ $? -eq "0" ]
then
 kill -9 $pid
 cur_dateTime="`date +%Y-%m-%d,%H:%m:%s`"
 mv $LOG_FOLDER/server.log $LOG_FOLDER/server.log_$cur_dateTime
else
 echo "${APP_NAME} is not running"
fi
}

#输出运行状态
status() {
   is_exist
if [ $? -eq "0" ]
then
     echo "${APP_NAME} is running. Pid is ${pid}"
else
     echo "${APP_NAME} is not running."
fi
}

#重启
restart() {
  stop
  start
}

#根据输入参数，选择执行对应方法，不输入则执行使用说明
case "$1" in
"start")
start
;;
"stop")
stop
;;
"status")
status
;;
"restart")
restart
;;
*)
usage
;;
esac
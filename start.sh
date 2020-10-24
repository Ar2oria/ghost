#!/bin/bash

module_name="ghost-main.jar"

process=`ps axu|grep ${module_name} |grep -v grep |awk '{print $2}'|wc -l`
if [ $process -ge 1 ];then
   ps axu | grep ${module_name} |grep -v grep |awk '{print $2}'| xargs kill
fi

exec nohup java -jar ghost-main/target/ghost-main.jar --XX:+UseG1G >/dev/null 2>&1&

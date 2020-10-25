#!/bin/bash

module_name="ghost-main.jar"

process=`ps axu|grep ${module_name} |grep -v grep |awk '{print $2}'|wc -l`
if [ $process -ge 1 ];then
   ps axu | grep ${module_name} |grep -v grep |awk '{print $2}'| xargs kill
fi


git checkout master
git branch -D online

git fetch
git checkout origin/online -b online
git branch -v

mvn clean package -Dmaven.skip.test=true

exec nohup java -jar ghost-main/target/ghost-main.jar --XX:+UseG1G >/dev/null 2>&1&

ps aux|grep ${module_name} |grep -v grep
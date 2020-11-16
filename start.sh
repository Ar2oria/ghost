#!/bin/bash

module_name="ghost-main.jar"

echo "正在寻找旧进程..."
process=`ps axu|grep ${module_name} |grep -v grep |awk '{print $2}'|wc -l`
if [ $process -ge 1 ];then
   ps axu | grep ${module_name} |grep -v grep |awk '{print $2}'| xargs kill -9
   echo "进程查找成功，正在执行kill程序..."
fi

echo "正在从github同步最新代码..."
git checkout master
git branch -D online

git fetch
git checkout origin/online -b online
echo "正在拉取远程分支到本地 [online] 分支..."
git branch -v

echo "正在重新打包项目..."
mvn clean package -Dmaven.skip.test=true
echo "项目打包成功。"

exec nohup java -jar ghost-main/target/ghost-main.jar --XX:+UseG1G >/dev/null 2>&1&

echo "程序加载成功。"
ps aux|grep ${module_name} |grep -v grep
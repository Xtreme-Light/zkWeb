#!/bin/bash

RUNDIR=`dirname $0`
PIDFILE="${RUNDIR}/$0.pid"

if [ -f ${PIDFILE} ];then
        echo "zkweb 正在执行，请勿重复运行"
        exit 1

fi

echo $$ > ${PIDFILE}

java -jar zkweb-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod > /dev/null  2>&1 &

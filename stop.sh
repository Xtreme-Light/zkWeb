#!/bin/bash


if [ -f ${PIDFILE} ]; then
        ID=`ps -ef | grep "zkweb" | grep -v "$0" | grep -v "grep" | awk '{print $2}'`
        echo "kill ${ID}"

        rm -f ${PIDFILE}
        echo "kill success"
fi
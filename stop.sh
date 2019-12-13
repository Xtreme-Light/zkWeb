#!/bin/bash



PID=$(ps -ef | grep zkweb-0.0.1-SNAPSHOT.jar | grep -v grep | awk '{ print $2 }')
if [ -z "$PID" ]
then
    echo zkweb is already stopped
else
    echo kill $PID
    kill $PID
fi
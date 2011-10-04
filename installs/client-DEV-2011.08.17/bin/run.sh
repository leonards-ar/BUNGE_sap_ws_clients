#!/bin/sh

if [ -z "$SAP_WS_CLIENT_HOME" ] ; then
  SAP_WS_CLIENT_HOME=/x/lib/webservices/client
fi

J_HOME="$JAVA_HOME"

if [ -z "$J_HOME" ] ; then
  J_HOME=/usr/java/jdk1.5.0_11
fi

CPATH=.:$SAP_WS_CLIENT_HOME/conf
for i in `ls $SAP_WS_CLIENT_HOME/lib/*.jar`
do
  CPATH=${CPATH}:${i}
done

$J_HOME/bin/java -DproxySet=true -DproxyHost=10.1.4.70 -DproxyPort=8080 -cp "$CPATH" ar.com.bunge.sapws.client.SAPWSClient "$@"

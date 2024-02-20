#!/bin/bash
x=1
while [ $x -le 25 ]
do
  namespace="edu${x}"
  echo $namespace
  #k_exec=`kubectl create clusterrolebinding elastic-agent-clusterrolebinding${x} --clusterrole=elastic-agent-clusterrole --user=${namespace}`
  k_exec=`kubectl create clusterrolebinding edu-clusterrolebinding${x} --clusterrole=edu-role --user=${namespace}`
  echo $k_exec
  sleep 1
  x=$(( $x + 1 ))
done


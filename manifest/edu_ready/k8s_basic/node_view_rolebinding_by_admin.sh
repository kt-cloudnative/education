#!/bin/bash
x=1
while [ $x -le 25 ]
do
  namespace="edu${x}"
  echo $namespace
  #namespace+="${x}"
  k_exec=`kubectl create clusterrolebinding node-view-rolebinding${x} --clusterrole=node-view-role --user=${namespace}`
  echo $k_exec
  sleep 1
  x=$(( $x + 1 ))
done
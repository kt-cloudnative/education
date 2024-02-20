#!/bin/bash
x=1
while [ $x -le 25 ]
do
  namespace="edu${x}"
  echo $namespace
  #namespace+="${x}"
  k_exec=`kubectl delete clusterrolebinding node-view-rolebinding${x}`
  echo $k_exec
  sleep 1
  x=$(( $x + 1 ))
done
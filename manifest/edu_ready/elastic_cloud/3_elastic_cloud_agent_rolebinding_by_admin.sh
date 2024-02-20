#!/bin/bash
x=1
while [ $x -le 25 ]
do
  namespace="edu${x}"
  echo $namespace
  #k_exec=`kubectl create clusterrolebinding elastic-agent-clusterrolebinding${x} --clusterrole=elastic-agent-clusterrole --user=${namespace}`
  sa="elastic-agent"
  namespace_sa="${namespace}:${sa}"
  #namespace_sa="${namespace}:elastic-agent"
  k_exec=`kubectl create clusterrolebinding elastic-agent-clusterrolebinding${x} --clusterrole=elastic-agent-clusterrole --serviceaccount=${namespace_sa}`
  echo $k_exec
  sleep 1
  x=$(( $x + 1 ))
done
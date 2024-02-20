#!/bin/bash
## sa 인 경우는 잘 사용 안함
x=1
while [ $x -le 25 ]
do
  namespace="edu${x}"
  echo $namespace
  #k_exec=`kubectl create clusterrolebinding elastic-agent-clusterrolebinding${x} --clusterrole=elastic-agent-clusterrole --user=${namespace}`
  sa="elastic-agent"
  namespace_sa="${namespace}:${sa}"
  k_exec=`kubectl create clusterrolebinding edu-sa-clusterrolebinding${x} --clusterrole=edu-role  --serviceaccount=${namespace_sa}`
  echo $k_exec
  sleep 1
  x=$(( $x + 1 ))
done


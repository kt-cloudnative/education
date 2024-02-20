#!/bin/bash
x=1
while [ $x -le 25 ]
do
  namespace="edu${x}"
  echo $namespace
  k_exec=`oc adm policy add-role-to-user monitoring-edit  ${namespace} -n ${namespace}`
  echo $k_exec
  sleep 1
  x=$(( $x + 1 ))
done
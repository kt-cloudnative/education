#!/bin/bash
x=1
while [ $x -le 24 ]
do
  namespace="edu${x}"
  echo $namespace
  k_exec=`kubectl apply -f elastic_cloud_sa.yaml -n ${namespace}`
  echo $k_exec
  sleep 1
  x=$(( $x + 1 ))
done
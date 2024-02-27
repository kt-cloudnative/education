## Prometheus 설치   

<br/>

### helm repo 추가

<br/>

```bash
root@newedu-k3s:~/monitoring# helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
```  

<br/>

```bash
root@newedu-k3s:~/monitoring# helm search  repo prometheus
NAME                                              	CHART VERSION	APP VERSION	DESCRIPTION
bitnami/kube-prometheus                           	8.28.1       	0.71.2     	Prometheus Operator provides easy monitoring de...
bitnami/prometheus                                	0.11.1       	2.49.1     	Prometheus is an open source monitoring and ale...
bitnami/wavefront-prometheus-storage-adapter      	2.3.3        	1.0.7      	DEPRECATED Wavefront Storage Adapter is a Prome...
prometheus-community/kube-prometheus-stack        	56.8.2       	v0.71.2    	kube-prometheus-stack collects Kubernetes manif...
prometheus-community/prometheus                   	25.13.0      	v2.49.1    	Prometheus is a monitoring system and time seri...
prometheus-community/prometheus-adapter           	4.9.0        	v0.11.2    	A Helm chart for k8s prometheus adapter
prometheus-community/prometheus-blackbox-exporter 	8.11.0       	v0.24.0    	Prometheus Blackbox Exporter
prometheus-community/prometheus-cloudwatch-expo...	0.25.3       	0.15.5     	A Helm chart for prometheus cloudwatch-exporter
prometheus-community/prometheus-conntrack-stats...	0.5.10       	v0.4.18    	A Helm chart for conntrack-stats-exporter
prometheus-community/prometheus-consul-exporter   	1.0.0        	0.4.0      	A Helm chart for the Prometheus Consul Exporter
prometheus-community/prometheus-couchdb-exporter  	1.0.0        	1.0        	A Helm chart to export the metrics from couchdb...
```  

<br/>



### values.yaml 수정

<br/>

values.yaml 을 추출한다.  

```bash
root@newedu-k3s:~/monitoring# helm show values prometheus-community/kube-prometheus-stack > values.yaml
```  

<br/>

```bash
root@newedu-k3s:~/monitoring# vi values.yaml
```  

<br/>  

values.yaml 화일에서 아래 부분을 수정한다.      

<br/>


```bash
 929 grafana:
 930   enabled: true
...
3195     ## Interval between consecutive scrapes.
3196     ## Defaults to 30s.
3197     ## ref: https://github.com/prometheus-operator/prometheus-operator/blob/release-0.44/pkg/prometheus/promcfg.go#L180-L183
3198     ##
3199     scrapeInterval: "15s"
...
3369     serviceMonitorSelectorNilUsesHelmValues: false # true  # serviceMonitor 생성시 helm release 값 삭제
3370
3371     ## ServiceMonitors to be selected for target discovery.
3372     ## If {}, select all ServiceMonitors
3373     ##
3374     serviceMonitorSelector: {}
3375     ## Example which selects ServiceMonitors with label "prometheus" set to "somelabel"
3376     # serviceMonitorSelector:
3377     #   matchLabels:
3378     #     prometheus: somelabel
3379
3380     ## Namespaces to be selected for ServiceMonitor discovery.
3381     ##
3382     serviceMonitorNamespaceSelector: {}
3383     ## Example which selects ServiceMonitors in namespaces with label "prometheus" set to "somelabel"
3384     # serviceMonitorNamespaceSelector:
3385     #   matchLabels:
3386     #     prometheus: somelabel
3387
3388     ## If true, a nil or {} value for prometheus.prometheusSpec.podMonitorSelector will cause the
3389     ## prometheus resource to be created with selectors based on values in the helm deployment,
3390     ## which will also match the podmonitors created
3391     ##
3392     podMonitorSelectorNilUsesHelmValues: false
...
3454     ## How long to retain metrics
3455     ##
3456     retention: 1d # 데이터 유지 기간
3457
3458     ## Maximum size of metrics
3459     ##
3460     retentionSize: "10GiB"  # pvc 사이즈 
...
3561     storageSpec: # {}
3562     ## Using PersistentVolumeClaim
3563       volumeClaimTemplate:
3564         spec:
3565           storageClassName: nfs-client
3566           accessModes: ["ReadWriteOnce"]
3567           resources:
3568             requests:
3569               storage: 10Gi
```  

<br/>

설치를 진행한다.  

```bash
root@newedu-k3s:~/monitoring# helm install prometheus prometheus-community/kube-prometheus-stack -f values.yaml -n monitoring
NAME: prometheus
LAST DEPLOYED: Tue Feb 27 03:22:53 2024
NAMESPACE: monitoring
STATUS: deployed
REVISION: 1
NOTES:
kube-prometheus-stack has been installed. Check its status by running:
  kubectl --namespace monitoring get pods -l "release=prometheus"

Visit https://github.com/prometheus-operator/kube-prometheus for instructions on how to create & configure Alertmanager and Prometheus instances using the Operator.
```  

<br/>

```bash
root@newedu-k3s:~/monitoring# kubectl get po -n monitoring
NAME                                                     READY   STATUS    RESTARTS   AGE
prometheus-kube-prometheus-operator-59b48fb79f-tqrdn     1/1     Running   0          2m2s
prometheus-prometheus-node-exporter-fsmhb                1/1     Running   0          2m2s
alertmanager-prometheus-kube-prometheus-alertmanager-0   2/2     Running   0          2m
prometheus-kube-state-metrics-5c655d58fc-pnt6w           1/1     Running   0          2m2s
prometheus-grafana-7994bfb78f-n66tl                      3/3     Running   0          2m2s
prometheus-prometheus-kube-prometheus-prometheus-0       2/2     Running   0          2m
root@newedu-k3s:~/monitoring# kubectl get svc -n monitoring
NAME                                      TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)                      AGE
prometheus-kube-prometheus-prometheus     ClusterIP   10.43.42.3      <none>        9090/TCP,8080/TCP            3m31s
prometheus-prometheus-node-exporter       ClusterIP   10.43.36.97     <none>        9100/TCP                     3m31s
prometheus-kube-prometheus-operator       ClusterIP   10.43.118.32    <none>        443/TCP                      3m31s
prometheus-kube-prometheus-alertmanager   ClusterIP   10.43.121.230   <none>        9093/TCP,8080/TCP            3m31s
prometheus-grafana                        ClusterIP   10.43.200.141   <none>        80/TCP                       3m31s
prometheus-kube-state-metrics             ClusterIP   10.43.255.119   <none>        8080/TCP                     3m31s
alertmanager-operated                     ClusterIP   None            <none>        9093/TCP,9094/TCP,9094/UDP   3m29s
prometheus-operated                       ClusterIP   None            <none>        9090/TCP                     3m29s
```  

<br/>

prometheus ingress 를 생성한다.  


```bash
root@newedu-k3s:~/monitoring# cat prometheus_ing.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: prometheus-duckdns-ingress
  annotations:
    kubernetes.io/ingress.class: "nginx"
spec:
  rules:
  - host: prometheus.kteducation.duckdns.org
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: prometheus-kube-prometheus-prometheus
            port:
              number: 9090
```  

```bash              
root@newedu-k3s:~/monitoring# kubectl apply -f prometheus_ing.yaml -n monitoring
ingress.networking.k8s.io/prometheus-duckdns-ingress created
```  

<br/>

web browser에서 http://prometheus.kteducation.duckdns.org:31860 를 입력한다. 


<br/>  

up 을 입력하고 Execute 버튼을 클릭하면 metric 을 볼수 있다.  

<img src="./assets/prometheus_k3s_up.png" style="width: 80%; height: auto;"/>

<br/>

grafana ingress 를 생성한다.  


```bash
root@newedu-k3s:~/monitoring# cat grafana_ing.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: grafana-duckdns-ingress
  annotations:
    kubernetes.io/ingress.class: "nginx"
spec:
  rules:
  - host: grafana.kteducation.duckdns.org
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: prometheus-grafana
            port:
              number: 80
```  

```bash              
root@newedu-k3s:~/monitoring# kubectl apply -f grafana_ing.yaml -n monitoring
ingress.networking.k8s.io/grafana-duckdns-ingress created
```  

<br/>

web browser에서 http://grafana.kteducation.duckdns.org:31860 를 입력한다.    

<br/>

ID를 찾는다.  

```bash
root@newedu-k3s:~/monitoring# kubectl get secrets prometheus-grafana -o jsonpath="{.data.admin-user}" -n monitoring | base64 -d
admin
```
<br/>

비밀번호를 찾는다.  

```bash
root@newedu-k3s:~/monitoring# kubectl get secrets prometheus-grafana -o jsonpath="{.data.admin-password}" -n monitoring | base64 -d
prom-operator
```  

<br/>

로그인을 한다.  

<img src="./assets/grafana_k3s.png" style="width: 80%; height: auto;"/>


<br/>

로그인을 한후 오른쪽 상단를 클릭하여 비밀번호를 변경한다.  

<img src="./assets/grafana_k3s_change_password.png" style="width: 80%; height: auto;"/>  




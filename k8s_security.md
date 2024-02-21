# K8S Security

<br/>

Kubernetes Security 구조를  이해한다.  

<br/>


1. 보안 component

2. K8S Security Overview  

3. Network Policy

4. Route 에 SSL 인증서 설정     

5. User Account vs Service Account  

6. Krew 설명 및 설치  

7. Kubernetes 보안 Components  

8. Security Context

9. Pod Security Policy ( PSP )  


<br/>

## 보안 Component 

<br/>

### 인증 기관(Certificate Authority, CA)이란?

<br/>

인증 기관(Certificate Authority, CA)이란 SSL 보안 인증서를 발급하는 기관들을 말합니다.

<img src="./assets/security_ca.png" style="width: 80%; height: auto;"/>    


<br/>

SSL 인증서를 발급 해주는 인증 기관은 여러 곳이 있습니다.  

대표적으로는 Sectigo(comodo), Digicert, GlobalSign, Let’s Encrypt 등이 있습니다.
 

- Sectigo : 백신과 방화벽 프로그램 등으로 유명한 보안 기업으로 본사는 미국에 위치하고 있으며, 인증서 발급 점유율은 가장 높습니다. 인증서 브랜드는 기존에 Comodo CA 였으나 2018년에 브랜드 명칭을 Sectigo로 변경하였습니다.  

- Digicert : 예전에 업계 1위였던 Symantec 에 이슈가 있어 하위 기관인 Thawte 와 GeoTrust 까지 인수하면서 점차 점유율이 높아진 기관입니다. 기본적으로 고가정책을 고수하고 있으며, 본사는 미국에 위치하고 있습니다.  

- GlobalSign : 벨기에에서 설립되었으나 2006년에 일본의 GMO Internet에 인수되어 현재 본사는 일본에 있습니다. 유럽 및 일본에서 점유율이 높으며, SSL 인증서뿐 아니라 PKI 등 보안 솔루션을 제공하는 업체입니다.  

- Let’s Encrypt : 2016년부터 발행을 시작하였으며, 무료 인증서를 제공하는 기관으로 이 점 때문에 점유율이 가파르게 상승하고 있습니다  

<br/>

### Let’s Encrypt 인증 기관은?  

<br/>

인증 기관 중 Let’s Encrypt는 타 CA 인증 기관과 다소 차이점이 있습니다.  

보안 인증서 발급 비용은 무료이지만 인증 기간이 3 개월로 타 인증 기관의 기간(1 년)보다 짧습니다.  

하지만 스크립트를 이용하여 자동 갱신이 가능합니다. (k8s는 cert-manager 사용 하여 가능 )    

그리고 Sectigo나 Digicert 인증서는 상품에 따라 금액에 차이가 있지만 암호화 된 SSL 통신 중 데이터가 유출되어 손해가 발생했다면 이에 대한 배상을 해주는 시스템이 있습니다.  

하지만 Let’s Encrypt 는 비영리 기관으로 배상에 대한 책임을 가지지 않습니다.  

     
<br/>

### SSL (보안 소켓 계층)이란?

<br/>

SSL 은 Secure Sockets Layer의 약자로, 일반적으로 서버의 웹사이트와 클라이언트의 브라우저 간의 통신 시에 데이터를 암호화 해주는 프로토콜을 말합니다.  

때문에 해킹 등으로 인해 데이터가 도중에 유출되더라도 내용을 보호할 수가 있게 됩니다.

<br/>
 
SSL 통신을 하게 되면 브라우저의 주소 창에 위와 같이 http 가 아닌 https 로 표시가 됩니다.  

그리고 웹 브라우저에서는 아래와 같이 경고 창이 표시되기도 합니다.  


<img src="./assets/security_1.png" style="width: 80%; height: auto;"/>  
     
<br/>

TLS는 무엇일까요?  

SSL의 버전이 올라가면서 명칭이 TLS로 변경되었지만 기존의 SSL이라는 명칭을 계속 사용하고 있습니다.  

통상적으로 SSL 과 TLS 는 같은 표현이라고 생각하면 되겠습니다.  

참고로 현재 대부분의 브라우저에서는 TLS 1.1 이하의 버전의 취약점이 노출되어 TLS 1.2 이상의 버전을 사용할 것을 권고하고 있습니다.  

<br/>

### SSL 보안 인증서란?

<br/>

보안 인증서는 기본적으로 개인 키와 공개 키로 이루어져 있습니다.  

개인 키는 비공개 키로 암호화 또는 복호화 하는데 사용이 됩니다.  

공개 키는 누구나 볼 수 있는 정보지만 해당 키는 신뢰할 수 있어야 하며, 이것을 증명해주고 발급 해주는 기관이 인증 기관, CA(Certificate Authority)라고 합니다.  

공개 키 중 CA 정보를 가지고 있는 인증서들이 있으며 루트 인증서, 중간 인증서라고 합니다.  

보안 인증서는 계층으로 연결되어 있어 이를 인증서 체인이라고 합니다.  

그리고 가운데서 연결 고리 역할을 하는 중간 인증서를 체인 인증서라고 부르기도 합니다.  

루트 및 중간 인증서는 브라우저 및 OS 등의 저장소에 저장이 되어 있습니다.   

이렇게 저장소에 등록되어있는 인증서로 웹사이트에 접속을 했을 때 검증을 하게 됩니다.  


<br/>


## K8S Security Overview 

<br/>

<img src="./assets/k8s_security_overview.png" style="width: 100%; height: auto;"/>

<br/>


## Network Policy

<br/>

nginx deployment 와 service 를 생성한다.  

```bash
[root@bastion ] # cat nginx.yaml
```  

```bash
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
  labels:
    app: nginx
spec:
  replicas: 1
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: ghcr.io/shclub/nginx:latest
        ports:
        - containerPort: 80
---
apiVersion: v1	
kind: Service	
metadata:	
  name: nginx
  labels: 
    app: nginx
spec:	
  ports:	
  - port: 80	
    targetPort: 80
    name: http
  selector:	
    app: nginx
  type: ClusterIP
```  

```bash
[root@bastion ] # kubectl apply -f nginx.yaml
[root@bastion ] # kubectl get po
NAME                                READY   STATUS    RESTARTS   AGE
nginx-deployment-56569bbd7d-btmph   1/1     Running   0          6m47s
[root@bastion elastic]# kubectl get svc -n
NAME    TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)   AGE
nginx   ClusterIP   172.30.69.135   <none>        80/TCP    39m
```  

<br/>

본인의 nginx pod로 들어가서 다른 namespace 의 service 를 호출 해본다.  

```bash
[root@bastion ] # kubectl exec -it nginx-deployment-56569bbd7d-btmph sh
```  

<br/>

다른 namespace 의 서비스를 curl 로 호출해본다.    
- <Service 명 >.<Namespace 명 >:<Port 번호>  

<br/>

```bash
~ # curl nginx.edu1
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
html { color-scheme: light dark; }
body { width: 35em; margin: 0 auto;
font-family: Tahoma, Verdana, Arial, sans-serif; }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>
```  

<br/>

기본적으로 k8s는 다른 namespace 의 리소스를 조회를 할 수는 없지만 POD에서는 다른 Namespace의 서비스를 호출 할 수 있다.  

<br/>

이런 호출 를 막기위해서는 POD 방화벽이 필요하고 그것이 Network Policy 이다.  

<br/>

아래 내용은 `edu1` namespace에서 `edu24` namespace 의 `app: nginx` label을 가진 Pod만 허용한다는 내용입니다.  

```bash
[root@bastion ] # cat network_policy_nginx.yaml
```  

```bash
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: nginx-policy
  namespace: edu1
spec:
  podSelector:
    matchLabels:
      app: nginx
  policyTypes:
    - Ingress
  ingress:
    - from:
        - namespaceSelector:
            matchLabels:
              name: edu24
      ports:
        - protocol: TCP
          port: 80
```
```bash
[root@bastion ] # kubectl apply -f network_policy_nginx.yaml
```  
Output
```bash
networkpolicy.networking.k8s.io/nginx-policy created          
```  

<br/>

`edu25` namespace에서 edu1 서비스를 호출 해봅니다.   
접속이 되지 않는것을 확인 할수 있습니다.  

```bash
[root@bastion elastic]# kubectl exec -it netshoot sh
kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl exec [POD] -- [COMMAND] instead.
~ # curl nginx.edu1
```  

<br/>

`edu24` namespace에서 edu1 서비스를 호출 해봅니다.   
정상적으로 호출이 됩니다.  

```bash  
[root@bastion elastic]# kubectl exec -it netshoot sh -n edu24
```  
```bash
~ # curl nginx.edu1
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
html { color-scheme: light dark; }
body { width: 35em; margin: 0 auto;
font-family: Tahoma, Verdana, Arial, sans-serif; }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>
```  

<br/>

참고  
- Network Policy : https://velog.io/@_zero_/%EC%BF%A0%EB%B2%84%EB%84%A4%ED%8B%B0%EC%8A%A4-%EB%84%A4%ED%8A%B8%EC%9B%8C%ED%81%AC-%EC%A0%95%EC%B1%85NetworkPolicy-%EA%B0%9C%EB%85%90-%EB%B0%8F-%EC%84%A4%EC%A0%95 
- https://lifeoncloud.kr/entry/Network-Policy  

<br/>

## Route 에 SSL 인증서 설정

<br/>

Front 를 Route로 노출한 서비스에 SSL 인증서를 설정해 본다.  

<br/>

### TLS 확인 방법  

<br/>

https://www.ssllabs.com/ 에 들어가서 TEST your Server 를 클릭한다.  


<br/>


<img src="./assets/tls_verify_0.png" style="width: 80%; height: auto;"/>

<br/>

`https://console-openshift-console.apps.okd4.ktdemo.duckdns.org` 를 입력하고 분석을 해보면 등급이 `T` 로 되어 붉은색인 위험 상태가 되어 있는 것을 볼수 있다.  


<img src="./assets/tls_verify_1.png" style="width: 80%; height: auto;"/>

<br/>

Not Trusted 상태인 것을 볼수 있다. 

<img src="./assets/tls_verify_2.png" style="width: 80%; height: auto;"/>

<br/>

TLS는 1.3 으로 설정.  

<img src="./assets/tls_verify_3.png" style="width: 80%; height: auto;"/>


<br/>

공인인증서가 적용된 사이트를 분석을 해본다.  

`https://frontend-react-edu25.apps.okd4.ktdemo.duckdns.org` 분석 결과를 보면 A 등급 인것을 확인 할 수 있고 `TLS 1.3`으로 설정 되어 있다.  

<img src="./assets/tls_verify_5.png" style="width: 80%; height: auto;"/>

<br/>

`Let's encrypt`의 `R3`  인증서 를 확인 할 수 있다.  

<img src="./assets/tls_verify_6.png" style="width: 80%; height: auto;"/>

<br/>

추가적인 정보를 보면 `Let's encrypt`의 상위 인증 기관이 `ISRG Root X1` 인것을 확인 할 수 있다.  

<img src="./assets/tls_verify_7.png" style="width: 80%; height: auto;"/>

<br/>

Chrome 에서 setting -> Privacy and security -> security 로 이동한다.

<br/>


<img src="./assets/tls_verify_8.png" style="width: 80%; height: auto;"/>

<br/>

Manage device certificates를 클릭한다.

<br/>


<img src="./assets/tls_verify_9.png" style="width: 80%; height: auto;"/>

<br/>

Mac 인 경우 key chain access 화면이 나오고 `Let's encrypt`의 상위 인증 기관인 	`ISRG Root X1` 가 keystore 에 저장이 되어 있어 `Let's encrypt` 에서 발급 받은 인증서는 유효함.   

<img src="./assets/tls_verify_10.png" style="width: 80%; height: auto;"/>

<br/>


### cert manager

<br/>

`cert-manager`는 Kubernetes안에서 TLS 인증서를 자동으로 설치하고 관리 (유효기간 만료시 갱신) 기능을 가지고 있다.  지난 교육에서 OpenTelemetry 설치시 먼저 설치가 되었다.    

https://cert-manager.io/docs/  

<br/>

사내는 대부분 폐쇠망 이기 때문에 80 port를 오픈해야 하는 cert-manager는 활용이 어렵다.  

<br/>

### 실습

<br/>

지난번에 만들었던 Frontend React UI 에 SSL 인증서를 추가하여 안전한 https 연동을 해본다.  

<br/>

순서는 아래와 같다.  

- 도메인에서 토큰 확인  
- 인증서 생성 ( 자동 ) : cert-manager를 통한 인증서 발급 및 관리 ( SKIP )
- 인중서 생성 ( 수동 ) : 멀티 인증서 발급 ( VM 에서 진행 )  
- OKD Route 에 추가
- 인증서 확인  
- Elastic Agent 설정 ( Uptime , TLS Certificate )  

<br/>
 

Duckdns 사이트 ( https://www.duckdns.org ) 에서 로그인 후 token 값을 구한다.  

<img src="./assets/okd_ssl_0.png" style="width: 100%; height: auto;"/>

<br/>


Duckdns 사이트에서 로그인 후 token 값을 구한다.  

<img src="./assets/okd_ssl_1.png" style="width: 100%; height: auto;"/>


<br/>

wildcard 도메인 인증서를 받기위해 수동 설치를 진행 합니다.  
아래 과정은 VM 에서 진행합니다. ( 강사가 사전 진행 완료. 교육생은 불필요 )  

<br/>

`letsencrypt_wildcard.sh` 스크립트에 값을 설정 한다.  
- docker 대신 podman 가 설치가 된 서버에서는 podman 으로 대체 한다.
- /certs:/app/cert : 인증서가 저장될 폴더  
- DOMAIN : wildcard 도메인 사용을 하기 위해 설정   
- DuckDNS_Token : duckdns 본인의 token 값  

<br/>

```bash
[root@bastion security]# cat letsencrypt_wildcard.sh
docker run -d --rm -v ./certs:/app/cert -e DOMAIN="*.apps.okd4.ktdemo.duckdns.org" -e DuckDNS_Token="3f309d5c-***********9434d23d6" --name=letsencrypt wnsguddk1/wildcard-duckdns-acme:1.0
```  

<br/>

`certs` 라는 폴더를 만들고 `letsencrypt_wildcard.sh` 를 실행하면 certs 폴더에 3개의 pem 화일이 생성이된다.    
- letsencrypt 는 3시간에 10 까지 인증서를 만들수가 있어서 제한이 있음.
- 테스트는 ./manifest/security/certs 밑에 있는 화일을 사용   

<br/>

```bash
[root@bastion security]# mkdir certs
[root@bastion security]# sh letsencrypt_wildcard.sh
[root@bastion security]# ls certs
wildcard-cert.pem  wildcard-fullchain.pem  wildcard-key.pem
```  

<br/>

우리가 만들어 놓은 route 의 URL를 WEB 브라우저에서 조회해 보면 `Your connection is not private` 으로 나오고 `Advanced` 버튼을 클릭하여 https 인증을 무시하고 접속을 합니다.  

URL 옆에 열쇠 아이콘이 열려져 있습니다.  


<img src="./assets/okd_ssl_2.png" style="width: 80%; height: auto;"/>

<br/>

이제 브라우저에서 인증하는 공인된 인증서를 추가하여 https 연동은 해봅니다.  
OKD Console 에서 본인의 namespace 를 선택하고 Networking -> Route 로 이동하여  `frontend-react` 를 수정합니다.  

<img src="./assets/okd_ssl_3.png" style="width: 80%; height: auto;"/>

<br/>

아래와 같이 설정합니다.  

- TLS Termination : edge  
- Insecure traffic : 선택안함   
- Certificate : wildcard-cert.pem 

<img src="./assets/okd_ssl_4.png" style="width: 80%; height: auto;"/>

<br/>

- Private key : wildcard-key.pem   
- CA Certificate : wildcard-fullchain.pem   

<img src="./assets/okd_ssl_4-1.png" style="width: 80%; height: auto;"/>  

저장 버튼을 클릭해서 저장합니다.  

<br/>

웹 브라우저에서 route URL을 입력하면 URL 옆에 열쇠 아이콘이 잠겨져 있는 것을 볼 수 있고 열쇠를 클릭합니다.  

<img src="./assets/okd_ssl_5.png" style="width: 80%; height: auto;"/>  


<br/>

`Connection is secure` 문구 를 클릭하여 들어가면  `Certificate is valid` 볼수 있습니다.  

<img src="./assets/okd_ssl_6.png" style="width: 80%; height: auto;"/>  

<br/>

`Certificate is valid` 를 클릭하면 인증서 정보를 볼 수 있습니다.  

<img src="./assets/okd_ssl_7.png" style="width: 80%; height: auto;"/>  


<br/>

인증서는 정상적으로 적용이 된 것을 확인 했고 인증서는 대부분 유효기간이 1년이고 `Let's Encrypt` 는 3개월 입니다.  

아래는 Elastic 를 활용하여 시스템 모니터링과 인증서 관리 기능을 사용 해 봅니다. 

<br/>

Elastic 에서 해당 기능을 사용하기 위해서는 heartbeat 이라는 pod를 배포해야 하고 configmap에 host 정보를 입력해야 합니다. ( 강사가 사전 작업 완료. 교육생은 불필요 )    
- 향후 직접 설치시 elastic 계정과 비밀번호 변경  

```bash
[root@bastion security]# cat elastic_heartbeat.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: heartbeat-deployment-config
  namespace: elastic
  labels:
    heartbeat: "true"
data:
  heartbeat.yml: |-                                                                     ## configmap으로 관리됨
    heartbeat.monitors:
    - id: solutions                                                                     ## monitor의 고유ID 설정
      type: http                                                                        ## monitor 종류 설정, 솔루션 uptime을 확인하기 위한 것이므로 http 사용
      hosts: [                                                                          ## 여러 솔루션 URL을 입력하여 한꺼번에 check
        'https://frontend-react-edu25.apps.okd4.ktdemo.duckdns.org/',
        'https://frontend-edu25.apps.okd4.ktdemo.duckdns.org/'
      ]
      max_redirects: 5                                                                  ## 로그인 화면 등으로 redirect되는 경우가 있어 exception 처리하는 설정
      check.response.status: [200]                                                      ## status code 값 설정, 일반적으로 200 사용
      schedule: '@every 5m'                                                             ## check 주기 설정
    processors:
      - add_kubernetes_metadata:
    output.elasticsearch:
      hosts: ['${ELASTICSEARCH_HOST:elasticsearch-master}:${ELASITCSEARCH_PORT:9200}']  ## 데이터를 수신할 Elasticsearch service host와 port 설정
      username: ${ELASTICSEARCH_USERNAME}                                               ## Elasticsearch 계정 설정
      password: ${ELASTICSEARCH_PASSWORD}                                               ## 위 계정의 비밀번호 설정
      protocol: https
      ssl.verification_mode: none
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: heartbeat
  namespace: elastic
  labels:
    heartbeat: "true"
spec:
  replicas: 1
  selector:
    matchLabels:
      heartbeat: "true"
  template:
    metadata:
      labels:
        heartbeat: "true"
    spec:
      serviceAccountName: elastic-agent   ## 별도 serviceaccount 생성 필요없이 elastic-agent serviceaccount 사용하면 됨
      hostNetwork: true
      dnsPolicy: ClusterFirstWithHostNet
      containers:
      - name: heartbeat
        image: elastic/heartbeat:8.9.2
        imagePullPolicy: "IfNotPresent"
        args: [
          "-c", "/etc/heartbeat.yml",
          "--strict.perms=false",
          "-e",
          "-d", "*"
        ]
        env:
        - name: ELASTICSEARCH_HOST
          value: elasticsearch-master
        - name: ELASTICSEARCH_PORT
          value: "9200"
        - name: ELASTICSEARCH_USERNAME
          value: "elastic"
        - name: ELASTICSEARCH_PASSWORD
          value: "Shcl********"
        - name: NODE_NAME
          valueFrom:
            fieldRef:
              fieldPath: spec.nodeName
        securityContext:
          runAsUser: 0
          privileged: true
        resources:
          limits:
            memory: 200Mi
          requests:
            cpu: 100m
            memory: 128Mi
        volumeMounts:
        - name: config
          mountPath: /etc/heartbeat.yml
          readOnly: true
          subPath: heartbeat.yml
        - name: data
          mountPath: /usr/share/heartbeat/data
      volumes:
      - name: config
        configMap:
          defaultMode: 0600
          name: heartbeat-deployment-config
      - name: data
        emptyDir: {}
      nodeSelector:                                                                     ## node selector 설정
        elastic: 'true'
        edu: 'true'
[root@bastion security]# kubectl apply -f elastic_heartbeat.yaml -n elastic
configmap/heartbeat-deployment-config created
deployment.apps/heartbeat created
[root@bastion security]# kubectl get po -n elastic
NAME                                                     READY   STATUS    RESTARTS   AGE
elastic-agent-integrations-deployment-5884d4464f-thm5f   1/1     Running   0          2d16h
elastic-agent-integrations-deployment-5884d4464f-w88b8   1/1     Running   0          5d21h
elasticsearch-master-0                                   1/1     Running   0          11d
heartbeat-84bd5f58bd-t2z9l                               1/1     Running   0          67s
```  

<br/>

Kibana 에서 아래 Path 로 이동합니다.    
- Elastic 8.9.1 (교육용 환경) : Observability -> Uptime -> Monitors  
- Elastic 8.10.1 (클라우드 환경) : Observability -> Synthetics -> Monitors   

<br/>

본인의 Cloud  Elastic 환경에서 Create Monitor 버튼을 클릭하고 아래와 같이 설정한다  
- Monitor Type : HTTP Ping  
- URL : 본인의 Frontend Route 설정 URL ( 예, https://frontend-react-edu25.apps.okd4.ktdemo.duckdns.org/ )  
- Monitor Name : 원하는 이름 ( 예, front_edu )  
- Locations : 아무거나 선택    

나머지는 변경하지 않고 Creat Monitor 클릭하여 저장   

<img src="./assets/kibana_tls_1.png" style="width: 80%; height: auto;"/>  

<br/>  

해당 URL의 status 를 확인 할 수 있다.   

<img src="./assets/kibana_tls_2.png" style="width: 80%; height: auto;"/>  

<br/>

<img src="./assets/kibana_tls_3.png" style="width: 80%; height: auto;"/>  

<br/>  

`TLS Certificates` 메뉴로 이동하면 설정된  인증서 정보를 가져오고 `Alerts and rules` 설정을 통해 만료일전에 알림을 받을 수 있다.  

<img src="./assets/kibana_tls_4.png" style="width: 80%; height: auto;"/>  

<br/>

### 자동 설치 

<br/>


secret를 생성한다.  

duckdns_secret.yaml   
```bash
apiVersion: v1
kind: Secret
metadata:
  name: duckdns-api-key-secret
type: Opaque
stringData:
  api-key: 3f309d5c************99434d23d6
```  

<br/>

```bash
[root@bastion security]# kubectl apply -f  duckdns_secret.yaml
secret/duckdns-api-key-secret created
```  

<br/>

참고 : https://dev.to/javiermarasco/https-with-ingress-controller-cert-manager-and-duckdns-in-akskubernetes-2jd1  


```bash
[root@bastion security]# git clone https://github.com/ebrianne/cert-manager-webhook-duckdns.git
Cloning into 'cert-manager-webhook-duckdns'...
remote: Enumerating objects: 390, done.
remote: Counting objects: 100% (50/50), done.
remote: Compressing objects: 100% (19/19), done.
remote: Total 390 (delta 33), reused 31 (delta 31), pack-reused 340
Receiving objects: 100% (390/390), 143.35 KiB | 5.73 MiB/s, done.
Resolving deltas: 100% (203/203), done.
[root@bastion security]# cd cert-manager-webhook-duckdns
```  


<br/>

```bash
[root@bastion cert-manager-webhook-duckdns]# helm install cert-manager-webhook-duckdns -n cert-manager --set duckdns.token='3f309d5c-c04a-4d46-b22a-cb99434d23d6' --set clusterIssuer.production.create=true --set clusterIssuer.staging.create=true --set clusterIssuer.email='shclub@gmail.com' --set logLevel=2 ./deploy/cert-manager-webhook-duckdns
WARNING: Kubernetes configuration file is group-readable. This is insecure. Location: /root/okd4/auth/kubeconfig
NAME: cert-manager-webhook-duckdns
LAST DEPLOYED: Wed Nov  1 21:01:15 2023
NAMESPACE: cert-manager
STATUS: deployed
REVISION: 1
TEST SUITE: None
[root@bastion cert-manager-webhook-duckdns]# kubectl get po -n cert-manager
NAME                                            READY   STATUS    RESTARTS          AGE
cert-manager-559b5d5b7d-xq5v9                   1/1     Running   64 (6h32m ago)    22d
cert-manager-cainjector-f5c6565d4-jw78p         1/1     Running   109 (6h32m ago)   22d
cert-manager-webhook-5f44bc85f4-dhr45           1/1     Running   1                 22d
cert-manager-webhook-duckdns-77b96b4b9b-bbwz9   1/1     Running   0                 13s
[root@bastion cert-manager-webhook-duckdns]# kubectl get sa -n cert-manager
NAME                           SECRETS   AGE
builder                        1         22d
cert-manager                   1         22d
cert-manager-cainjector        1         22d
cert-manager-webhook           1         22d
cert-manager-webhook-duckdns   1         23s
default                        1         22d
deployer                       1         22d
[root@bastion cert-manager-webhook-duckdns]# kubectl get certs -n cert-manager
NAME                                       READY   SECRET                                     AGE
cert-manager-webhook-duckdns-ca            True    cert-manager-webhook-duckdns-ca            33s
cert-manager-webhook-duckdns-webhook-tls   True    cert-manager-webhook-duckdns-webhook-tls   33s
```  

<br/>

참고 : https://velog.io/@_gyullbb/OKD-%EA%B0%9C%EC%9A%94-2

<br/>

Output
```bash
WARNING: Kubernetes configuration file is group-readable. This is insecure. Location: /root/okd4/auth/kubeconfig
NAME: cert-manager-webhook-duckdns
LAST DEPLOYED: Wed Nov  1 18:33:11 2023
NAMESPACE: edu25
STATUS: deployed
REVISION: 1
TEST SUITE: None
```  

<br/>

```bash
[root@bastion elastic]# kubectl apply -f ingress.yaml
ingress.networking.k8s.io/frontend-react-ingress created
[root@bastion elastic]# kubectl get ing
NAME                     CLASS    HOSTS                                                ADDRESS   PORTS     AGE
frontend-react-ingress   <none>   frontend-react2-edu25.apps.okd4.ktdemo.duckdns.org             80, 443   4s
[root@bastion elastic]# kubectl get challenges
NAME                                                             STATE     DOMAIN                                               AGE
cert-manager-webhook-duckdns-staging-9k9sf-266131994-147567566   pending   frontend-react2-edu25.apps.okd4.ktdemo.duckdns.org   73s            
```  

<br/>


```bash
[root@bastion elastic]# kubectl get secret
NAME                                         TYPE                                  DATA   AGE
builder-dockercfg-wxv4x                      kubernetes.io/dockercfg               1      13d
builder-token-vwzlp                          kubernetes.io/service-account-token   4      13d
cert-manager-webhook-duckdns-staging-n942s   Opaque                                1      4m9s
default-dockercfg-tptzz                      kubernetes.io/dockercfg               1      13d
default-token-z5wjx                          kubernetes.io/service-account-token   4      13d
deployer-dockercfg-swrn7                     kubernetes.io/dockercfg               1      13d
deployer-token-xssq4                         kubernetes.io/service-account-token   4      13d
duckdns-api-key-secret                       Opaque                                1      3h57m
elastic-agent-dockercfg-krzmq                kubernetes.io/dockercfg               1      4d12h
elastic-agent-token-4t97p                    kubernetes.io/service-account-token   4      4d12h
[root@bastion elastic]# kubectl describe secret cert-manager-webhook-duckdns-staging-n942s
Name:         cert-manager-webhook-duckdns-staging-n942s
Namespace:    edu25
Labels:       cert-manager.io/next-private-key=true
              controller.cert-manager.io/fao=true
Annotations:  <none>

Type:  Opaque

Data
====
tls.key:  1704 bytes
```   

<br/>

```bash
[root@bastion elastic]# kubectl get challenges
NAME                                                              STATE     DOMAIN                                               AGE
cert-manager-webhook-duckdns-production-ndx4v-407049-2411669503   pending   frontend-react2-edu25.apps.okd4.ktdemo.duckdns.org   61s
[root@bastion elastic]# kubectl get certificate
NAME                                      READY   SECRET                                    AGE
cert-manager-webhook-duckdns-production   False   cert-manager-webhook-duckdns-production   109s
```  

<br/>

## User Account vs Service Account  

<br/>  

쿠버네티스에 대한 요청이 처리되는 전체 과정을 요약하면 아래와 같습니다.


<img src="./assets/k8s_auth_1.png" style="width: 60%; height: auto;"/>

<br/>

쿠버네티스에는 쿠버네티스 내에 존재하는 자원에 대한 접근을 위한 2가지의 account 타입이 존재합니다.   

- User Account : 사용자 어카운트는 사람을 위한 것  
- Service Account :  서비스 어카운트는 파드에서 실행되는 프로세스를 위한 것

<br/>

<img src="./assets/k8s_sa_user_1.png" style="width: 60%; height: auto;"/>

<br/>

쿠버네티스에 할당된 유저가 User Account (유저 어카운트) 이고

Pod 가 다른 쿠버네티스 자원 (Pods, Services ..) 에 접근하기 위해 하는 증명 주체가 Service Account (서비스 어카운트) 입니다.  

<br/>

### Service Account 계정 생성 

<br/>

서비스 Account를 하나 생성합니다.  

```bash
root@edu25:~# cat edu-sa.yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: edu-sa
```  

<br/>

```bash
root@edu25:~# kubectl apply -f edu-sa.yaml
serviceaccount/edu-sa created
root@edu25:~# kubectl get sa
NAME            SECRETS   AGE
builder         1         16d
default         1         16d
deployer        1         16d
edu-sa          1         5s
elastic-agent   1         8d
```  

<br/>

secret 이 2개가 생성이되고 `edu-sa-token` 으로 시작 되는 secret 에 token과 인증서 정보가 들어 있다.  

```bash
root@edu25:~# kubectl get secret
NAME                            TYPE                                  DATA   AGE
edu-sa-dockercfg-k28h8          kubernetes.io/dockercfg               1      21s
edu-sa-token-sbf9n              kubernetes.io/service-account-token   4      21s
```  

<br/>

Pod를 하나 생성 하는데  `serviceAccountName` 은 앞에서 생성한 `edu-sa` 로 설정한다.  

```bash
root@edu25:~# cat nginx_sa.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: sa-example
  labels:
    app: sa-example
spec:
  replicas: 1
  selector:
    matchLabels:
      app: sa-example
  template:
    metadata:
      labels:
        app: sa-example
    spec:
      serviceAccountName: edu-sa # SA 를 지정해줍니다
      containers:
        - name: nginx
          image: ghcr.io/shclub/nginx:latest
          ports:
            - containerPort: 80
```

<br/>

nginx pod를 생성한다.  

```bash
kubectl apply -f nginx_sa.yaml
```  

<br/>

생성된 pod로 접속한다.

```bash
root@edu25:~# kubectl exec -it sa-example-7d494dd86c-ljf5d sh
```  

<br/>
`/var/run/secrets/kubernetes.io/serviceaccount` 폴더에 secret의 내용이 mount되어 있는 것을 알 수 있다.

```bash
# ls /var/run/secrets/kubernetes.io/serviceaccount
ca.crt	namespace  service-ca.crt  token
```

<br/>

폴더로 이동한후 TOKEN 값을 구하고 API Server로 pod를 조회 해본다.  

```bash
# cd /var/run/secrets/kubernetes.io/serviceaccount
# TOKEN=$(cat token)
# curl -X GET https://$KUBERNETES_SERVICE_HOST/api/v1/namespaces/edu25/pods --header "Authorization: Bearer $TOKEN" --insecure
{
  "kind": "Status",
  "apiVersion": "v1",
  "metadata": {},
  "status": "Failure",
  "message": "pods is forbidden: User \"system:serviceaccount:edu25:edu-sa\" cannot list resource \"pods\" in API group \"\" in the namespace \"edu25\"",
  "reason": "Forbidden",
  "details": {
    "kind": "pods"
  },
  "code": 403
```  

<br/>

`dafault` Service Account의  token 을 `jwt.io` 사이트에서 보면 아래와 같이 나타난다.  

<img src="./assets/k8s_jwt_token.png" style="width: 60%; height: auto;"/>

<br/>


### 과제   

<br/>

edu-sa 서비스 어카운트는 권한이 없어서 호출이 불가능 하고 default sa의 TOKEN 값으로 pod를 조회해 본다.    

- edu_default_sa_role.yaml



<br/>


## Krew 설명 및 설치  

<br/>

### 쿠버네티스 플러그인 관리자 Krew  

<br/>

kubectl을 보다 편리하게 사용할 수 있도록 해주는 플러그인 관리 도구

apt, brew와 비슷하게 kubectl 플러그인을 검색하고 설치하는 도구로, 2023년 1월 기준 210개의 kubectl 플러그인이 배포되어 있습니다.  

macOS, Linux, Windows에서 사용할 수 있으며 kubectl v1.12 이상의 버전에서 사용할 수 있습니다.  

<img src="./assets/krew_logo.png" style="width: 60%; height: auto;"/>


<br/>

### 설치    

vm 에서 아래 명령어를 실행한다.  

<br/> 

```bash
(
  set -x; cd "$(mktemp -d)" &&
  OS="$(uname | tr '[:upper:]' '[:lower:]')" &&
  ARCH="$(uname -m | sed -e 's/x86_64/amd64/' -e 's/\(arm\)\(64\)\?.*/\1\2/' -e 's/aarch64$/arm64/')" &&
  KREW="krew-${OS}_${ARCH}" &&
  curl -fsSLO "https://github.com/kubernetes-sigs/krew/releases/latest/download/${KREW}.tar.gz" &&
  tar zxvf "${KREW}.tar.gz" &&
  ./"${KREW}" install krew
)
```  

Output
```bash
++ mktemp -d
+ cd /tmp/tmp.xNBOW8MuQ4
++ tr '[:upper:]' '[:lower:]'
++ uname
+ OS=linux
++ sed -e s/x86_64/amd64/ -e 's/\(arm\)\(64\)\?.*/\1\2/' -e 's/aarch64$/arm64/'
++ uname -m
+ ARCH=amd64
+ KREW=krew-linux_amd64
+ curl -fsSLO https://github.com/kubernetes-sigs/krew/releases/latest/download/krew-linux_amd64.tar.gz
+ tar zxvf krew-linux_amd64.tar.gz
./LICENSE
./krew-linux_amd64
+ ./krew-linux_amd64 install krew
Adding "default" plugin index from https://github.com/kubernetes-sigs/krew-index.git.
Updated the local copy of plugin index.
Installing plugin: krew
Installed plugin: krew
\
 | Use this plugin:
 | 	kubectl krew
 | Documentation:
 | 	https://krew.sigs.k8s.io/
 | Caveats:
 | \
 |  | krew is now installed! To start using kubectl plugins, you need to add
 |  | krew's installation directory to your PATH:
 |  |
 |  |   * macOS/Linux:
 |  |     - Add the following to your ~/.bashrc or ~/.zshrc:
 |  |         export PATH="${KREW_ROOT:-$HOME/.krew}/bin:$PATH"
 |  |     - Restart your shell.
 |  |
 |  |   * Windows: Add %USERPROFILE%\.krew\bin to your PATH environment variable
 |  |
 |  | To list krew commands and to get help, run:
 |  |   $ kubectl krew
 |  | For a full list of available plugins, run:
 |  |   $ kubectl krew search
 |  |
 |  | You can find documentation at
 |  |   https://krew.sigs.k8s.io/docs/user-guide/quickstart/.
 | /
/
```  

Krew 실행파일의 위치를 PATH에 등록해줍니다.  

```bash
export PATH="${KREW_ROOT:-$HOME/.krew}/bin:$PATH"
``` 

업데이트한 PATH를 Shell이 알 수 있도록 Shell을 재시작합니다.    

```bash  
source ~/.bashrc # zsh을 사용하는 경우, zshrc를 입력
```  

정상적으로 설치되었는지 확인하고 업데이트도 할 겸 아래 명령어를 수행합니다.  

```bash  
kubectl krew update
```

<br/>

### Plugin 설치    

<br/>

먼저 rolesum을 설치합니다.  

- rolesum : 사용자, Service Account별 RBAC 역할에 대해 간략하게 요약 정리  

```bash
kubectl krew install rolesum
```  

Output
```bash
Updated the local copy of plugin index.
Installing plugin: rolesum
Installed plugin: rolesum
\
 | Use this plugin:
 | 	kubectl rolesum
 | Documentation:
 | 	https://github.com/Ladicle/kubectl-rolesum
/
WARNING: You installed plugin "rolesum" from the krew-index plugin repository.
   These plugins are not audited for security by the Krew maintainers.
   Run them at your own risk.
```  

<br/>

현재 context 를 확인해 보고 edu 로 context 가 아니면 변경합니다.  

```bash
root@edu25:~# kubectl config get-contexts
CURRENT   NAME                                           CLUSTER                            AUTHINFO                                 NAMESPACE
*         dev25                                          api-okd4-ktdemo-duckdns-org:6443   dev25
          edu25/api-okd4-ktdemo-duckdns-org:6443/edu25   api-okd4-ktdemo-duckdns-org:6443   edu25/api-okd4-ktdemo-duckdns-org:6443   edu25
root@edu25:~# kubectl config use-context edu25/api-okd4-ktdemo-duckdns-org:6443/edu25
Switched to context "edu25/api-okd4-ktdemo-duckdns-org:6443/edu25".
```

<br/>

edu25 namespace의 default service account 의 권한을 조회해 봅니다.  

```bash
kubectl rolesum default -n edu25
```  
```bash
ServiceAccount: edu25/default
Secrets:
• */default-dockercfg-tptzz

Policies:

• [CRB] */system:openshift:scc:anyuid ⟶  [CR] */system:openshift:scc:anyuid
  Resource                                            Name    Exclude  Verbs  G L W C U P D DC
  securitycontextconstraints.security.openshift.io  [anyuid]    [-]    [use]  ✖ ✖ ✖ ✖ ✖ ✖ ✖ ✖


• [CRB] */system:openshift:scc:hostnetwork ⟶  [CR] */system:openshift:scc:hostnetwork
  Resource                                              Name       Exclude  Verbs  G L W C U P D DC
  securitycontextconstraints.security.openshift.io  [hostnetwork]    [-]    [use]  ✖ ✖ ✖ ✖ ✖ ✖ ✖ ✖


• [CRB] */system:openshift:scc:privileged ⟶  [CR] */system:openshift:scc:privileged
  Resource                                              Name      Exclude  Verbs  G L W C U P D DC
  securitycontextconstraints.security.openshift.io  [privileged]    [-]    [use]  ✖ ✖ ✖ ✖ ✖ ✖ ✖ ✖
```  

<br/>

특정 계정의 권한을 조회해 봅니다.  

```bash
kubectl rolesum -k User edu25 -n edu25
```  

Output
```bash
User: edu25

Policies:
• [CRB] */cluster-admin-26 ⟶  [CR] */cluster-admin
  Resource  Name  Exclude  Verbs  G L W C U P D DC
  *.*       [*]     [-]     [-]   ✔ ✔ ✔ ✔ ✔ ✔ ✔ ✔


• [CRB] */edu-rolebinding ⟶  [CR] */edu-role
  Resource                                       Name  Exclude  Verbs  G L W C U P D DC
  clusterrolebindings.rbac.authorization.k8s.io  [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  clusterroles.rbac.authorization.k8s.io         [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  configmaps                                     [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  cronjobs.batch                                 [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  daemonsets.apps                                [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  deployments.apps                               [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  events                                         [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  instrumentations.opentelemetry.io              [*]     [-]     [-]   ✔ ✔ ✔ ✔ ✔ ✔ ✔ ✔
  jobs.batch                                     [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  namespaces                                     [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  nodes                                          [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  nodes/stats                                    [*]     [-]     [-]   ✔ ✖ ✖ ✖ ✖ ✖ ✖ ✖
  persistentvolumeclaims                         [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  persistentvolumes                              [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  pods                                           [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  replicasets.apps                               [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  replicasets.extensions                         [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  rolebindings.rbac.authorization.k8s.io         [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  roles.rbac.authorization.k8s.io                [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  serviceaccounts                                [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  servicemonitors.monitoring.coreos.com          [*]     [-]     [-]   ✔ ✔ ✔ ✔ ✔ ✔ ✔ ✔
  services                                       [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  statefulsets.apps                              [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  storageclasses.storage.k8s.io                  [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖


• [CRB] */node-view-rolebinding25 ⟶  [CR] */node-view-role
  Resource              Name  Exclude  Verbs  G L W C U P D DC
  nodes                 [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  nodes.metrics.k8s.io  [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
  pods.metrics.k8s.io   [*]     [-]     [-]   ✔ ✔ ✔ ✖ ✖ ✖ ✖ ✖
```  

<br/>

access-matrix : 서비스 리소스별 RBAC access 정보 확인  

```bash
root@edu25:~# kubectl krew install access-matrix
Updated the local copy of plugin index.
Installing plugin: access-matrix
Installed plugin: access-matrix
\
 | Use this plugin:
 | 	kubectl access-matrix
 | Documentation:
 | 	https://github.com/corneliusweig/rakkess
 | Caveats:
 | \
 |  | Usage:
 |  |   kubectl access-matrix
 |  |   kubectl access-matrix for pods
 | /
/
WARNING: You installed plugin "access-matrix" from the krew-index plugin repository.
   These plugins are not audited for security by the Krew maintainers.
   Run them at your own risk.
```

<br/>

```bash
root@edu25:~# kubectl access-matrix -n edu25
NAME                                                                      LIST  CREATE  UPDATE  DELETE
                                                                          ✖     ✖       ✖       ✖
                                                                          ✖     ✖       ✖       ✖
                                                                          ✖     ✖       ✖       ✖
                                                                          ✖     ✖       ✖       ✖
                                                                          ✖     ✖       ✖       ✖
                                                                          ✖     ✖       ✖       ✖
                                                                          ✖     ✖       ✖       ✖
                                                                          ✖     ✖       ✖       ✖
alertmanagerconfigs.monitoring.coreos.com                                 ✔     ✔       ✔       ✔
alertmanagers.monitoring.coreos.com                                       ✔     ✔       ✔       ✔
analysisruns.argoproj.io                                                  ✔     ✔       ✔       ✔
analysistemplates.argoproj.io                                             ✔     ✔       ✔       ✔
applicationactivities.spdx.softwarecomposition.kubescape.io               ✔     ✔       ✔       ✔
applicationprofiles.spdx.softwarecomposition.kubescape.io                 ✔     ✔       ✔       ✔
applicationprofilesummaries.spdx.softwarecomposition.kubescape.io         ✔     ✔       ✔       ✔
applications.argoproj.io                                                  ✔     ✔       ✔       ✔
applicationsets.argoproj.io                                               ✔     ✔       ✔       ✔
appliedclusterresourcequotas.quota.openshift.io                           ✔
appprojects.argoproj.io                                                   ✔     ✔       ✔       ✔
baremetalhosts.metal3.io                                                  ✔     ✔       ✔       ✔
bindings                                                                        ✔
bmceventsubscriptions.metal3.io                                           ✔     ✔       ✔       ✔
buildconfigs.build.openshift.io                                           ✔     ✔       ✔       ✔
builds.build.openshift.io                                                 ✔     ✔       ✔       ✔
catalogsources.operators.coreos.com                                       ✔     ✔       ✔       ✔
certificaterequests.cert-manager.io                                       ✔     ✔       ✔       ✔
certificates.cert-manager.io                                              ✔     ✔       ✔       ✔
challenges.acme.cert-manager.io                                           ✔     ✔       ✔       ✔
clickhouseinstallations.clickhouse.altinity.com                           ✔     ✔       ✔       ✔
clickhouseinstallationtemplates.clickhouse.altinity.com                   ✔     ✔       ✔       ✔
clickhouseoperatorconfigurations.clickhouse.altinity.com                  ✔     ✔       ✔       ✔
clusterserviceversions.operators.coreos.com                               ✔     ✔       ✔       ✔
configauditreports.aquasecurity.github.io                                 ✔     ✔       ✔       ✔
configmaps                                                                ✔     ✔       ✔       ✔
controllerrevisions.apps                                                  ✔     ✔       ✔       ✔
controlplanemachinesets.machine.openshift.io                              ✔     ✔       ✔       ✔
credentialsrequests.cloudcredential.openshift.io                          ✔     ✔       ✔       ✔
cronjobs.batch                                                            ✔     ✔       ✔       ✔
csistoragecapacities.storage.k8s.io                                       ✔     ✔       ✔       ✔
daemonsets.apps                                                           ✔     ✔       ✔       ✔
deploymentconfigs.apps.openshift.io                                       ✔     ✔       ✔       ✔
deployments.apps                                                          ✔     ✔       ✔       ✔
dnsrecords.ingress.operator.openshift.io                                  ✔     ✔       ✔       ✔
egressnetworkpolicies.network.openshift.io                                ✔     ✔       ✔       ✔
egressrouters.network.operator.openshift.io                               ✔     ✔       ✔       ✔
endpoints                                                                 ✔     ✔       ✔       ✔
endpointslices.discovery.k8s.io                                           ✔     ✔       ✔       ✔
events                                                                    ✔     ✔       ✔       ✔
events.events.k8s.io                                                      ✔     ✔       ✔       ✔
experiments.argoproj.io                                                   ✔     ✔       ✔       ✔
exposedsecretreports.aquasecurity.github.io                               ✔     ✔       ✔       ✔
firmwareschemas.metal3.io                                                 ✔     ✔       ✔       ✔
grafanadashboards.integreatly.org                                         ✔     ✔       ✔       ✔
grafanadatasources.integreatly.org                                        ✔     ✔       ✔       ✔
grafanafolders.integreatly.org                                            ✔     ✔       ✔       ✔
grafananotificationchannels.integreatly.org                               ✔     ✔       ✔       ✔
grafanas.integreatly.org                                                  ✔     ✔       ✔       ✔
hardwaredata.metal3.io                                                    ✔     ✔       ✔       ✔
horizontalpodautoscalers.autoscaling                                      ✔     ✔       ✔       ✔
hostfirmwaresettings.metal3.io                                            ✔     ✔       ✔       ✔
imagestreamimages.image.openshift.io
imagestreamimports.image.openshift.io                                           ✔
imagestreammappings.image.openshift.io                                          ✔
imagestreams.image.openshift.io                                           ✔     ✔       ✔       ✔
imagestreamtags.image.openshift.io                                        ✔     ✔       ✔       ✔
imagetags.image.openshift.io                                              ✔     ✔       ✔       ✔
infraassessmentreports.aquasecurity.github.io                             ✔     ✔       ✔       ✔
ingresscontrollers.operator.openshift.io                                  ✔     ✔       ✔       ✔
ingresses.networking.k8s.io                                               ✔     ✔       ✔       ✔
installplans.operators.coreos.com                                         ✔     ✔       ✔       ✔
instrumentations.opentelemetry.io                                         ✔     ✔       ✔       ✔
ippools.whereabouts.cni.cncf.io                                           ✔     ✔       ✔       ✔
issuers.cert-manager.io                                                   ✔     ✔       ✔       ✔
jobs.batch                                                                ✔     ✔       ✔       ✔
leases.coordination.k8s.io                                                ✔     ✔       ✔       ✔
limitranges                                                               ✔     ✔       ✔       ✔
localresourceaccessreviews.authorization.openshift.io                           ✔
localsubjectaccessreviews.authorization.k8s.io                                  ✔
localsubjectaccessreviews.authorization.openshift.io                            ✔
machineautoscalers.autoscaling.openshift.io                               ✔     ✔       ✔       ✔
machinehealthchecks.machine.openshift.io                                  ✔     ✔       ✔       ✔
machines.machine.openshift.io                                             ✔     ✔       ✔       ✔
machinesets.machine.openshift.io                                          ✔     ✔       ✔       ✔
network-attachment-definitions.k8s.cni.cncf.io                            ✔     ✔       ✔       ✔
networkpolicies.networking.k8s.io                                         ✔     ✔       ✔       ✔
opentelemetrycollectors.opentelemetry.io                                  ✔     ✔       ✔       ✔
operatorconditions.operators.coreos.com                                   ✔     ✔       ✔       ✔
operatorgroups.operators.coreos.com                                       ✔     ✔       ✔       ✔
operatorpkis.network.operator.openshift.io                                ✔     ✔       ✔       ✔
orders.acme.cert-manager.io                                               ✔     ✔       ✔       ✔
overlappingrangeipreservations.whereabouts.cni.cncf.io                    ✔     ✔       ✔       ✔
packagemanifests.packages.operators.coreos.com                            ✔
persistentvolumeclaims                                                    ✔     ✔       ✔       ✔
poddisruptionbudgets.policy                                               ✔     ✔       ✔       ✔
podmonitors.monitoring.coreos.com                                         ✔     ✔       ✔       ✔
podnetworkconnectivitychecks.controlplane.operator.openshift.io           ✔     ✔       ✔       ✔
pods                                                                      ✔     ✔       ✔       ✔
vulnerabilityreports.aquasecurity.github.io                               ✔     ✔       ✔       ✔
workloadconfigurationscans.spdx.softwarecomposition.kubescape.io          ✔     ✔       ✔       ✔
workloadconfigurationscansummaries.spdx.softwarecomposition.kubescape.io  ✔     ✔       ✔       ✔
```  


<br/>

rbac-tool : rbac 관련 lookup, whoami policy-rules 등 여러가지 확인 기능 제공    

```bash
root@edu25:~# kubectl rbac-tool lookup system:nodes
  SUBJECT      | SUBJECT TYPE | SCOPE       | NAMESPACE      | ROLE
+--------------+--------------+-------------+----------------+----------------------------------------------------------------------+
  system:nodes | Group        | ClusterRole |                | system:certificates.k8s.io:certificatesigningrequests:selfnodeclient
  system:nodes | Group        | ClusterRole |                | system:node-proxier
  system:nodes | Group        | ClusterRole |                | system:sdn-reader
  system:nodes | Group        | Role        | openshift-node | system:node-config-reader
```


<br/>

rbac-view : 웹을 통해 Cluster Roles와 Roles를 확인 (  ARM 맥은 지원 안함 ) 

```bash
kubectl rbac-view
```  
```bash
INFO[0000] Getting K8s client
INFO[0000] serving RBAC View and http://localhost:8800
```  


<br/>

VM 서버 `edu25` 번에 설치 되어 있고 포트를 오픈 했기 때문에 브라우저로  `http://211.251.238.182:8800` 로 접속하면 아래 내용을 볼수 있다.    


<img src="./assets/krew_rbac_view_1.png" style="width: 100%; height: auto;"/>  


<br/>

### 사용법

<br/>

전체 리스트 보기  
```bash
kubectl krew search
```  
<br/>

특정 플러그인 검색하기  
```bash
kubectl krew search example-plugin
```  

<br/>

Krew로 플러그인 설치하기
```bash
kubectl krew install example-plugin
```  

<br/>

Krew로 설치한 플러그인 확인하기
```bash
kubectl krew list
```  

<br/>  

Krew로 설치한 플러그인 업데이트하기
```bash
kubectl krew upgrade
```  

<br/>

Krew로 설치한 플러그인 삭제하기  
```bash
kubectl krew uninstall example-plugin
```  

<br/>

## Kubernetes 보안 Components

<br/>

참고 : https://cwal.tistory.com/18    

<br/>

아래 그림과 같이 Kubernetes에 존재하는 모든 Component 간 통신은 HTTPS를 기반으로 이루어지며, 모든 트래픽이 암호화되므로 데이터의 신뢰성과 보안을 보장할 수 있다.  

<br/>

<img src="./assets/k8s_security_component_1.png" style="width: 80%; height: auto;"/>

<br/>

HTTPS 프로토콜을 사용하기 위해선 Server와 Client 양측 모두 SSL/TLS 인증서(Certificate)가 필요하며, 모든 인증서는 신뢰할 수 있는 Root CA(Certifiacte Authority)에 의해 서명되어야 한다.  

<br/>

Kubernetes 역시 마찬가지로 각각의 Component마다 Certificate을 요구하며, 아래와 같이 독자적인 PKI(Public Key Infrastructure)를 구성한다.      

CA는 'KUBERNETES-CA'라는 Common Name을 갖고 있으며, etcd를 제외한 모든 Component의 Certificate 서명에 사용된다.

<br/>

<img src="./assets/k8s_security_component_2.png" style="width: 80%; height: auto;"/>

<br/>

kube-apiserver는 Client Certificate 내용 중 Common Name을 통해 Client를 구분할 수 있으며, 특히 kubelet은 자신이 위치한 Node의 hostname으로 세분화된다.   

다시 말해서 각각의 kubelet은 자신만의 유니크한 인증서를 갖는다는 의미이다. 이를 통해 kubelet은 자신이 속한 Node에 배정된 Pod 이외의 정보를 읽거나 쓸 수 없도록 제한된다.  

<br/>

<img src="./assets/k8s_security_component_3.png" style="width: 80%; height: auto;"/>

<br/>

Client Key list  

<img src="./assets/k8s_security_component_4.png" style="width: 80%; height: auto;"/>

<br/>


인증서 발급 API  

<br/>
매번 K8s 관리자가 Client의 인증서를 수동으로 발급할 수는 없으므로, API 서버에 해당 작업을 위임할 수 있으며 아래와 같은 순서로 이루어진다.  


- Client 측에서 Private Key로부터 CSR(Certificate Signing Request)를 생성하고, 이를 API 서버에 전달한다.  

- 해당 요청은 K8s 상에서 CertificateSigningRequest라는 별도의 리소스(로 취급되며, 관리자의 승인이 있을 때까지 Pending 상태로 남는다.  

- 관리자 승인시, Client 인증서를 발급하며 해당 Certificate으로 K8s 접근이 가능하다.

<br/>
자세한 사용 예시는 Kubernetes 공식 문서를 참고하길 바란다.  
- https://kubernetes.io/ko/docs/tasks/tls/managing-tls-in-a-cluster/  


<br/>

### 실습  ( 사용자 생성 )

<br/>

K8S에서 프로그래밍 방식으로 접근하는 ServiceAccount와는 달리 사용자(User)라는 개념이 명시적으로 존재하지 않으며, 클러스터 외부의 독립적인 서비스로부터 사용자를 제공받는 것이 일반적이다.    

<br/>

다른 Component와 마찬가지로 kube-apiserver 접근시 Authentication은 Client Ceritificate의 'Common Name(CN)' 필드값을 통해 이루어진다. 다만 클러스터 CA가 서명한 인증서만으로는 kube-apiserver 접근만 가능할 뿐, 어떠한 리소스에도 사용권한이 없기 때문에 RBAC을 통한 Authorization이 필요하다.     

OKD는 PaaS 솔루션 이기 때문에 user 생성 기능이 포함되어 있어 쉽게 user를 생성한다.   

서비스 어카운트는 JWT Token 으로 인정하지만 일반 유저는 X509 인증서로 인증한다.  


<br/>

####  SSL Key 파일 및 CSR 파일 생성


<br/>

참고   
- CSR 이란 : https://velog.io/@gadian88/Certificate-Signing-Requests-CSR    

<br/>

확인 사항 : 교육생은 본인의 순번에 맞게 dev 계정 생성 ( 예 : dev1, dev2 )

<br/>

SSL key 를 생성합니다.  

```bash
openssl genrsa -out dev25.key 2048
```  

Output
```bash
Generating RSA private key, 2048 bit long modulus (2 primes)
.................+++++
.....................+++++
e is 65537 (0x010001)
```  

<br/>

CSR 파일 생성시, Common Name에 새로 추가할 사용자의 이름을 입력하며 그 외 항목은 생략한다. 이 과정에서 dev25.key, dev25.csr 두 개의 파일이 생성된다.

```bash
openssl req -new -key dev25.key -out dev25.csr
```  

Output
```bash
139795455709632:error:2406F079:random number generator:RAND_load_file:Cannot open file:../crypto/rand/randfile.c:88:Filename=/root/.rnd
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [AU]:
State or Province Name (full name) [Some-State]:
Locality Name (eg, city) []:
Organization Name (eg, company) [Internet Widgits Pty Ltd]:
Organizational Unit Name (eg, section) []:
Common Name (e.g. server FQDN or YOUR name) []:dev25
Email Address []:

Please enter the following 'extra' attributes
to be sent with your certificate request
A challenge password []:
An optional company name []:
```

<br/>

#### K8s CSR ( CertificateSigningRequest ) 리소스 생성

<br/>

우선 다음 명령어로 CSR 파일의 내용을 base64로 인코딩할 필요가 있다.     

```bash
cat dev25.csr | base64 -w 0
```  

<br/>

Output
```bash
LS0tLS1CRUdJTiBDRVJUSUZJQ0FURSBSRVFVRVNULS0tLS0KTUlJQ21qQ0NBWUlDQVFBd1ZURUxNQWtHQTFVRUJoTUNRVlV4RXpBUkJnTlZCQWdNQ2xOdmJXVXRVM1JoZEdVeApJVEFmQmdOVkJBb01HRWx1ZEdWeWJtVjBJRmRwWkdkcGRITWdVSFI1SUV4MFpERU9NQXdHQTFVRUF3d0ZaR1YyCk1qVXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEZW1WMS8rTHdKSGduZlZyZFEKQjFRdERiaUlTY0hTbXFpYlNEdUJ4cXBoTDlNM2JQTTYrRUZ0WW9HN3R1VExBZWxvcFlnd2Z6U0p3a0tlRGNuQQpMeE80b1hlZUV5cUFxY1JvNGpzZ1RmL3J2MzRXUFJMeXZ0WlZwRGZ6bVpsWXl1M3V0aStxb0xMWnIrL1RHWDRNCkNkSVlRVVd0dldsYWdoVWhMNGNNTUFuUTZJaFdET0xVQ2ViTy9ZZWJ4OThMQVcyS25abmJ6OWxHd0ZGaTNydnkKVGNKVThYL0QzUS9YTWFXYzNJQlhSSUVIS3V5ZW04YXMvckZSTHN0Q0wvTzhRTDM4MEsxUEtENmxOVnltY1IrVApkb1FBN1JXZnZJVFlPZ3cySmRoOThEVGNrVXI2REJtMVBzRlkxK29aMlJrZXZpZmpWTEJOWVZFWW8zTkdXWVRhCk8zcTlBZ01CQUFHZ0FEQU5CZ2txaGtpRzl3MEJBUXNGQUFPQ0FRRUFRSi9CbmI3VmxBc1FLWEwvT3QyNlp4TVAKcEpTSFR1VDcwYU5rYXc0SzJuNmhqeVNwd3NLcmNyR1VpODVQeHNCVFhkSDg3MWFRckE1eHNvNGxlZUd5NkszVgorUGowRDBGZWlmclBoV2N4ZTMvdG9WTE1KZjZDYWVaWUE1aVlreS9wckRpS05QQ0l6S0I4ZWEyeUVib205aElRCm9pS1ZUTEd6NjBHTVVGWFp2SmUzVGI0aGpKZmFQbUMrMkNxYnY3T05NaEluQXBBSzhwSlNvOWtRQ3M4cWlpODQKWDNvRnpzcEUxRFVHMjR2SWZpMUxhUWllbW5pdVJWRXBiOFpycWMyemI4Sk0rK0hiR1BjMjhWUEFFL05obTVCVQpKTmpvWjloaERSZU0zOHZ5STAxaXpxMytGU0thZVZ4OVNMaEh5bENBZXJDVUQ4dkxtZ0dtT1pJWHh2c2ZvQT09Ci0tLS0tRU5EIENFUlRJRklDQVRFIFJFUVVFU1QtLS0tLQo=
```

<br/>

아래와 같은 CSR Manifest 파일을 작성하는데 request 필드에 위에서 얻은 인코딩 텍스트를 복사 하여 붙여 넣기를 한다.  

<br/>

```bash
root@edu25:~/security# cat csr.yaml
apiVersion: certificates.k8s.io/v1
kind: CertificateSigningRequest
metadata:
  name: dev25
spec:
  groups:
  - system:authenticated
  request: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURSBSRVFVRVNULS0tLS0KTUlJQ21qQ0NBWUlDQVFBd1ZURUxNQWtHQTFVRUJoTUNRVlV4RXpBUkJnTlZCQWdNQ2xOdmJXVXRVM1JoZEdVeApJVEFmQmdOVkJBb01HRWx1ZEdWeWJtVjBJRmRwWkdkcGRITWdVSFI1SUV4MFpERU9NQXdHQTFVRUF3d0ZaR1YyCk1qVXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEZW1WMS8rTHdKSGduZlZyZFEKQjFRdERiaUlTY0hTbXFpYlNEdUJ4cXBoTDlNM2JQTTYrRUZ0WW9HN3R1VExBZWxvcFlnd2Z6U0p3a0tlRGNuQQpMeE80b1hlZUV5cUFxY1JvNGpzZ1RmL3J2MzRXUFJMeXZ0WlZwRGZ6bVpsWXl1M3V0aStxb0xMWnIrL1RHWDRNCkNkSVlRVVd0dldsYWdoVWhMNGNNTUFuUTZJaFdET0xVQ2ViTy9ZZWJ4OThMQVcyS25abmJ6OWxHd0ZGaTNydnkKVGNKVThYL0QzUS9YTWFXYzNJQlhSSUVIS3V5ZW04YXMvckZSTHN0Q0wvTzhRTDM4MEsxUEtENmxOVnltY1IrVApkb1FBN1JXZnZJVFlPZ3cySmRoOThEVGNrVXI2REJtMVBzRlkxK29aMlJrZXZpZmpWTEJOWVZFWW8zTkdXWVRhCk8zcTlBZ01CQUFHZ0FEQU5CZ2txaGtpRzl3MEJBUXNGQUFPQ0FRRUFRSi9CbmI3VmxBc1FLWEwvT3QyNlp4TVAKcEpTSFR1VDcwYU5rYXc0SzJuNmhqeVNwd3NLcmNyR1VpODVQeHNCVFhkSDg3MWFRckE1eHNvNGxlZUd5NkszVgorUGowRDBGZWlmclBoV2N4ZTMvdG9WTE1KZjZDYWVaWUE1aVlreS9wckRpS05QQ0l6S0I4ZWEyeUVib205aElRCm9pS1ZUTEd6NjBHTVVGWFp2SmUzVGI0aGpKZmFQbUMrMkNxYnY3T05NaEluQXBBSzhwSlNvOWtRQ3M4cWlpODQKWDNvRnpzcEUxRFVHMjR2SWZpMUxhUWllbW5pdVJWRXBiOFpycWMyemI4Sk0rK0hiR1BjMjhWUEFFL05obTVCVQpKTmpvWjloaERSZU0zOHZ5STAxaXpxMytGU0thZVZ4OVNMaEh5bENBZXJDVUQ4dkxtZ0dtT1pJWHh2c2ZvQT09Ci0tLS0tRU5EIENFUlRJRklDQVRFIFJFUVVFU1QtLS0tLQo=
  signerName: kubernetes.io/kube-apiserver-client
  usages:
  - client auth
```  

<br/>

아래 명령어로 K8s CSR 리소스를 생성하여, 인증서 발급을 요청한다.


```bash
kubectl apply -f csr.yaml
``` 
Output
```bash
certificatesigningrequest.certificates.k8s.io/dev25 created
```  

<br/>


#### K8s CSR 확인 과 Approve

<br/>

CSR 을 확인해보면 현재 pending 되어있는 CSR를 확인 할 수 있다. 

```bash
root@edu25:~/security# kubectl get csr
NAME    AGE   SIGNERNAME                            REQUESTOR   REQUESTEDDURATION   CONDITION
dev25   9s    kubernetes.io/kube-apiserver-client   edu25       <none>              Pending
```  

<br/>  

다음 명령어로 해당 CSR을 승인하여, K8s CA가 서명한 인증서를 얻을 수 있다.

```bash
kubectl certificate approve dev25
```  

<br/>

```bash
certificatesigningrequest.certificates.k8s.io/dev25 approved
```  

<br/>


#### Client Certificate 파일 생성

<br/>

승인 완료시, 해당 CSR 리소스에 CA가 서명한 인증서 데이터가 추가되며, 다음 명령어로 이를 확인할 수 있다. 

<br/>

```bash
root@edu25:~/security# kubectl get csr dev25 -o yaml
apiVersion: certificates.k8s.io/v1
kind: CertificateSigningRequest
metadata:
  annotations:
    kubectl.kubernetes.io/last-applied-configuration: |
      {"apiVersion":"certificates.k8s.io/v1","kind":"CertificateSigningRequest","metadata":{"annotations":{},"name":"dev25"},"spec":{"groups":["system:authenticated"],"request":"LS0tLS1CRUdJTiBDRVJUSUZJQ0FURSBSRVFVRVNULS0tLS0KTUlJQ21qQ0NBWUlDQVFBd1ZURUxNQWtHQTFVRUJoTUNRVlV4RXpBUkJnTlZCQWdNQ2xOdmJXVXRVM1JoZEdVeApJVEFmQmdOVkJBb01HRWx1ZEdWeWJtVjBJRmRwWkdkcGRITWdVSFI1SUV4MFpERU9NQXdHQTFVRUF3d0ZaR1YyCk1qVXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEZW1WMS8rTHdKSGduZlZyZFEKQjFRdERiaUlTY0hTbXFpYlNEdUJ4cXBoTDlNM2JQTTYrRUZ0WW9HN3R1VExBZWxvcFlnd2Z6U0p3a0tlRGNuQQpMeE80b1hlZUV5cUFxY1JvNGpzZ1RmL3J2MzRXUFJMeXZ0WlZwRGZ6bVpsWXl1M3V0aStxb0xMWnIrL1RHWDRNCkNkSVlRVVd0dldsYWdoVWhMNGNNTUFuUTZJaFdET0xVQ2ViTy9ZZWJ4OThMQVcyS25abmJ6OWxHd0ZGaTNydnkKVGNKVThYL0QzUS9YTWFXYzNJQlhSSUVIS3V5ZW04YXMvckZSTHN0Q0wvTzhRTDM4MEsxUEtENmxOVnltY1IrVApkb1FBN1JXZnZJVFlPZ3cySmRoOThEVGNrVXI2REJtMVBzRlkxK29aMlJrZXZpZmpWTEJOWVZFWW8zTkdXWVRhCk8zcTlBZ01CQUFHZ0FEQU5CZ2txaGtpRzl3MEJBUXNGQUFPQ0FRRUFRSi9CbmI3VmxBc1FLWEwvT3QyNlp4TVAKcEpTSFR1VDcwYU5rYXc0SzJuNmhqeVNwd3NLcmNyR1VpODVQeHNCVFhkSDg3MWFRckE1eHNvNGxlZUd5NkszVgorUGowRDBGZWlmclBoV2N4ZTMvdG9WTE1KZjZDYWVaWUE1aVlreS9wckRpS05QQ0l6S0I4ZWEyeUVib205aElRCm9pS1ZUTEd6NjBHTVVGWFp2SmUzVGI0aGpKZmFQbUMrMkNxYnY3T05NaEluQXBBSzhwSlNvOWtRQ3M4cWlpODQKWDNvRnpzcEUxRFVHMjR2SWZpMUxhUWllbW5pdVJWRXBiOFpycWMyemI4Sk0rK0hiR1BjMjhWUEFFL05obTVCVQpKTmpvWjloaERSZU0zOHZ5STAxaXpxMytGU0thZVZ4OVNMaEh5bENBZXJDVUQ4dkxtZ0dtT1pJWHh2c2ZvQT09Ci0tLS0tRU5EIENFUlRJRklDQVRFIFJFUVVFU1QtLS0tLQo=","signerName":"kubernetes.io/kube-apiserver-client","usages":["client auth"]}}
  creationTimestamp: "2023-11-05T00:08:19Z"
  managedFields:
  ...
  uid: 68705a17-1dd3-47f7-a662-f3a4cf235f61
spec:
  extra:
    scopes.authorization.openshift.io:
    - user:full
  groups:
  - system:authenticated:oauth
  - system:authenticated
  request: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURSBSRVFVRVNULS0tLS0KTUlJQ21qQ0NBWUlDQVFBd1ZURUxNQWtHQTFVRUJoTUNRVlV4RXpBUkJnTlZCQWdNQ2xOdmJXVXRVM1JoZEdVeApJVEFmQmdOVkJBb01HRWx1ZEdWeWJtVjBJRmRwWkdkcGRITWdVSFI1SUV4MFpERU9NQXdHQTFVRUF3d0ZaR1YyCk1qVXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFEZW1WMS8rTHdKSGduZlZyZFEKQjFRdERiaUlTY0hTbXFpYlNEdUJ4cXBoTDlNM2JQTTYrRUZ0WW9HN3R1VExBZWxvcFlnd2Z6U0p3a0tlRGNuQQpMeE80b1hlZUV5cUFxY1JvNGpzZ1RmL3J2MzRXUFJMeXZ0WlZwRGZ6bVpsWXl1M3V0aStxb0xMWnIrL1RHWDRNCkNkSVlRVVd0dldsYWdoVWhMNGNNTUFuUTZJaFdET0xVQ2ViTy9ZZWJ4OThMQVcyS25abmJ6OWxHd0ZGaTNydnkKVGNKVThYL0QzUS9YTWFXYzNJQlhSSUVIS3V5ZW04YXMvckZSTHN0Q0wvTzhRTDM4MEsxUEtENmxOVnltY1IrVApkb1FBN1JXZnZJVFlPZ3cySmRoOThEVGNrVXI2REJtMVBzRlkxK29aMlJrZXZpZmpWTEJOWVZFWW8zTkdXWVRhCk8zcTlBZ01CQUFHZ0FEQU5CZ2txaGtpRzl3MEJBUXNGQUFPQ0FRRUFRSi9CbmI3VmxBc1FLWEwvT3QyNlp4TVAKcEpTSFR1VDcwYU5rYXc0SzJuNmhqeVNwd3NLcmNyR1VpODVQeHNCVFhkSDg3MWFRckE1eHNvNGxlZUd5NkszVgorUGowRDBGZWlmclBoV2N4ZTMvdG9WTE1KZjZDYWVaWUE1aVlreS9wckRpS05QQ0l6S0I4ZWEyeUVib205aElRCm9pS1ZUTEd6NjBHTVVGWFp2SmUzVGI0aGpKZmFQbUMrMkNxYnY3T05NaEluQXBBSzhwSlNvOWtRQ3M4cWlpODQKWDNvRnpzcEUxRFVHMjR2SWZpMUxhUWllbW5pdVJWRXBiOFpycWMyemI4Sk0rK0hiR1BjMjhWUEFFL05obTVCVQpKTmpvWjloaERSZU0zOHZ5STAxaXpxMytGU0thZVZ4OVNMaEh5bENBZXJDVUQ4dkxtZ0dtT1pJWHh2c2ZvQT09Ci0tLS0tRU5EIENFUlRJRklDQVRFIFJFUVVFU1QtLS0tLQo=
  signerName: kubernetes.io/kube-apiserver-client
  uid: e43391ac-b12c-4abe-8594-8d5d0d7c3bc2
  usages:
  - client auth
  username: edu25
status:
  certificate: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURTekNDQWpPZ0F3SUJBZ0lRUkttelViS1pkODc3d2N1c2o3eGtKekFOQmdrcWhraUc5dzBCQVFzRkFEQW0KTVNRd0lnWURWUVFEREJ0cmRXSmxMV056Y2kxemFXZHVaWEpmUURFMk9Ua3dNakV4TkRnd0hoY05Nak14TVRBMQpNREF3TkRVNFdoY05Nak14TWpBek1UUXhPVEE0V2pCVk1Rc3dDUVlEVlFRR0V3SkJWVEVUTUJFR0ExVUVDQk1LClUyOXRaUzFUZEdGMFpURWhNQjhHQTFVRUNoTVlTVzUwWlhKdVpYUWdWMmxrWjJsMGN5QlFkSGtnVEhSa01RNHcKREFZRFZRUURFd1ZrWlhZeU5UQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQU42WgpYWC80dkFrZUNkOVd0MUFIVkMwTnVJaEp3ZEthcUp0SU80SEdxbUV2MHpkczh6cjRRVzFpZ2J1MjVNc0I2V2lsCmlEQi9OSW5DUXA0TnljQXZFN2loZDU0VEtvQ3B4R2ppT3lCTi8rdS9maFk5RXZLKzFsV2tOL09abVZqSzdlNjIKTDZxZ3N0bXY3OU1aZmd3SjBoaEJSYTI5YVZxQ0ZTRXZod3d3Q2REb2lGWU00dFFKNXM3OWg1dkgzd3NCYllxZAptZHZQMlViQVVXTGV1L0pOd2xUeGY4UGREOWN4cFp6Y2dGZEVnUWNxN0o2YnhxeitzVkV1eTBJdjg3eEF2ZnpRCnJVOG9QcVUxWEtaeEg1TjJoQUR0RlorOGhOZzZERFlsMkgzd05OeVJTdm9NR2JVK3dWalg2aG5aR1I2K0orTlUKc0UxaFVSaWpjMFpaaE5vN2VyMENBd0VBQWFOR01FUXdFd1lEVlIwbEJBd3dDZ1lJS3dZQkJRVUhBd0l3REFZRApWUjBUQVFIL0JBSXdBREFmQmdOVkhTTUVHREFXZ0JSdUtvTlJnaER1Sm02TWx1d3FmUkc3VTR2VUJUQU5CZ2txCmhraUc5dzBCQVFzRkFBT0NBUUVBZG55cWszWlJFcE1RU0t2VklBcWJCTnRQaDlNRDdrVCtZQkJzWHpGYVQvTWwKK3FwaWNrMFRscEVxVDRudVVlbkhxRm1VbG9TSy9lQ0lPMHdtaVp0Mjd4Vkw5U1BlbXFSSnNQZ2taNkt2QkFQYwpFT0ZvZ1A3c3ZvRkJUY2JiN0Qxa0RSQVdWR0dKdHNqR3lzbVdMdHIvR3ZuekVvSVB2WFdaZHljdGwxUDhib2FSCmlXVkJPWGh4NlVNRXczOFJxMUJZdmkwTGZHNHlOOWVvcytSM2tFeURhRElxaEtlS0xFbDJMNjlrYlM2T0UzQjEKTHBZbjYxYThVYU0xSlpLS0tYeDZTQnVqT3gzVzNwMExRaEFDYk0vR1d0R1FjY0dpSHJqaXYvSUpBMGpVVFU1egpLU0d1d1NLOXNOaEZJUlBVMmFKeERPQUUyL205VUVTUnd4R2RGelU3eXc9PQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==
  conditions:
  - lastTransitionTime: "2023-11-05T00:09:58Z"
    lastUpdateTime: "2023-11-05T00:09:58Z"
    message: This CSR was approved by kubectl certificate approve.
    reason: KubectlApprove
    status: "True"
    type: Approved
```

<br/> 

.status.certificate 항목에 base64로 인코딩된 인증서가 위치하며, 아래 명령어로 원본 데이터인 CRT 화일을 얻을 수 있다.


```bash
echo "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURTekNDQWpPZ0F3SUJBZ0lRUkttelViS1pkODc3d2N1c2o3eGtKekFOQmdrcWhraUc5dzBCQVFzRkFEQW0KTVNRd0lnWURWUVFEREJ0cmRXSmxMV056Y2kxemFXZHVaWEpmUURFMk9Ua3dNakV4TkRnd0hoY05Nak14TVRBMQpNREF3TkRVNFdoY05Nak14TWpBek1GelU3eXc9PQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==" | base64 -d > dev25.crt
```

<br/>   

Output  
```bash
-----BEGIN CERTIFICATE-----
MIIDSzCCAjOgAwIBAgIQRKmzUbKZd877wcusj7xkJzANBgkqhkiG9w0BAQsFADAm
MSQwIgYDVQQDDBtrdWJlLWNzci1zaWduZXJfQDE2OTkwMjExNDgwHhcNMjMxMTA1
MDAwNDU4WhcNMjMxMjAzMTQxOTA4WjBVMQswCQYDVQQGEwJBVTETMBEGA1UECBMK
iWVBOXhx6UMEw38Rq1BYvi0LfG4yN9eos+R3kEyDaDIqhKeKLEl2L69kbS6OE3B1
LpYn61a8UaM1JZKKKXx6SBujOx3W3p0LQhACbM/GWtGQccGiHrjiv/IJA0jUTU5z
KSGuwSK9sNhFIRPU2aJxDOAE2/m9UESRwxGdFzU7yw==
-----END CERTIFICATE-----
```  

<br/>

이제 dev25.key, dev25.crt 두 개의 파일을 사용하여 kube-apiserver 접근이 가능하다.  

지금 까지 생성된 화일입니다.  

```bash
root@edu25:~/security# ls -al
total 24
drwxr-xr-x  2 root root 4096 Nov  5 00:13 .
drwx------ 10 root root 4096 Nov  5 00:07 ..
-rw-r--r--  1 root root 1528 Nov  5 00:07 csr.yaml
-rw-r--r--  1 root root 1204 Nov  5 00:13 dev25.crt
-rw-r--r--  1 root root  980 Nov  5 00:05 dev25.csr
-rw-------  1 root root 1679 Nov  5 00:04 dev25.key
```  

<br/>

인증서 정보를 확인해 본다.    

```bash
openssl x509 -noout  -text -in dev25.crt
```      

Output
```bash  
Certificate:
    Data:
        Version: 3 (0x2)
        Serial Number:
            44:a9:b3:51:b2:99:77:ce:fb:c1:cb:ac:8f:bc:64:27
        Signature Algorithm: sha256WithRSAEncryption
        Issuer: CN = kube-csr-signer_@1699021148
        Validity
            Not Before: Nov  5 00:04:58 2023 GMT
            Not After : Dec  3 14:19:08 2023 GMT
        Subject: C = AU, ST = Some-State, O = Internet Widgits Pty Ltd, CN = dev25
        Subject Public Key Info:
            Public Key Algorithm: rsaEncryption
                RSA Public-Key: (2048 bit)
                Modulus:
                    00:de:99:5d:7f:f8:bc:09:1e:09:df:56:b7:50:07:
                    54:2d:0d:b8:88:49:c1:d2:9a:a8:9b:48:3b:81:c6:
                    aa:61:2f:d3:37:6c:f3:3a:f8:41:6d:62:81:bb:b6:
                    e4:cb:01:e9:68:a5:88:30:7f:34:89:c2:42:9e:0d:
                    c9:c0:2f:13:b8:a1:77:9e:13:2a:80:a9:c4:68:e2:
                    3b:20:4d:ff:eb:bf:7e:16:3d:12:f2:be:d6:55:a4:
                    37:f3:99:99:58:ca:ed:ee:b6:2f:aa:a0:b2:d9:af:
                    ef:d3:19:7e:0c:09:d2:18:41:45:ad:bd:69:5a:82:
                    15:21:2f:87:0c:30:09:d0:e8:88:56:0c:e2:d4:09:
                    e6:ce:fd:87:9b:c7:df:0b:01:6d:8a:9d:99:db:cf:
                    d9:46:c0:51:62:de:bb:f2:4d:c2:54:f1:7f:c3:dd:
                    0f:d7:31:a5:9c:dc:80:57:44:81:07:2a:ec:9e:9b:
                    c6:ac:fe:b1:51:2e:cb:42:2f:f3:bc:40:bd:fc:d0:
                    ad:4f:28:3e:a5:35:5c:a6:71:1f:93:76:84:00:ed:
                    15:9f:bc:84:d8:3a:0c:36:25:d8:7d:f0:34:dc:91:
                    4a:fa:0c:19:b5:3e:c1:58:d7:ea:19:d9:19:1e:be:
                    27:e3:54:b0:4d:61:51:18:a3:73:46:59:84:da:3b:
                    7a:bd
                Exponent: 65537 (0x10001)
        X509v3 extensions:
            X509v3 Extended Key Usage:
                TLS Web Client Authentication
            X509v3 Basic Constraints: critical
                CA:FALSE
            X509v3 Authority Key Identifier:
                keyid:6E:2A:83:51:82:10:EE:26:6E:8C:96:EC:2A:7D:11:BB:53:8B:D4:05

    Signature Algorithm: sha256WithRSAEncryption
         76:7c:aa:93:76:51:12:93:10:48:ab:d5:20:0a:9b:04:db:4f:
         87:d3:03:ee:44:fe:60:10:6c:5f:31:5a:4f:f3:25:fa:aa:62:
         72:4d:13:96:91:2a:4f:89:ee:51:e9:c7:a8:59:94:96:84:8a:
         fd:e0:88:3b:4c:26:89:9b:76:ef:15:4b:f5:23:de:9a:a4:49:
         b0:f8:24:67:a2:af:04:03:dc:10:e1:68:80:fe:ec:be:81:41:
         4d:c6:db:ec:3d:64:0d:10:16:54:61:89:b6:c8:c6:ca:c9:96:
         2e:da:ff:1a:f9:f3:12:82:0f:bd:75:99:77:27:2d:97:53:fc:
         6e:86:91:89:65:41:39:78:71:e9:43:04:c3:7f:11:ab:50:58:
         be:2d:0b:7c:6e:32:37:d7:a8:b3:e4:77:90:4c:83:68:32:2a:
         84:a7:8a:2c:49:76:2f:af:64:6d:2e:8e:13:70:75:2e:96:27:
         eb:56:bc:51:a3:35:25:92:8a:29:7c:7a:48:1b:a3:3b:1d:d6:
         de:9d:0b:42:10:02:6c:cf:c6:5a:d1:90:71:c1:a2:1e:b8:e2:
         bf:f2:09:03:48:d4:4d:4e:73:29:21:ae:c1:22:bd:b0:d8:45:
         21:13:d4:d9:a2:71:0c:e0:04:db:f9:bd:50:44:91:c3:11:9d:
         17:35:3b:cb
```  

<br/>

인증서 유효 기간 확인  

```bash  
openssl x509 -in dev25.crt -noout -dates
```  

<br/>

유효기간은 12월 3일 까지 인것을 확인 할 수 있다.  

```bash
notBefore=Nov  5 00:04:58 2023 GMT
notAfter=Dec  3 14:19:08 2023 GMT
```  

유효기간은 위에서 생성시에 `openssl req -new -key dev25.key -out dev25.csr` 위의 명령어에 `-days 3650` 를 추가하면 10년짜리 인증서 발급 가능하다.  


<br/>


이제 dev25.key, dev25.crt 두 개의 파일을 사용하여 kube-apiserver 접근이 가능하다. 물론 어떠한 RBAC 설정도 되지 않은 계정이기 때문에 아직 할 수 있는 동작은 없다.   

<br/>

#### kubeconfig 설정  

<br/>

새로 추가한 사용자 계정으로 kubectl을 사용하기 위해선 kubeconfig에 해당 정보가 있어야 한다.


<br/>

현재 context를 조회해 봅니다.    

```bash
kubectl config get-contexts
```  

Output
```bash
CURRENT   NAME                                           CLUSTER                            AUTHINFO                                 NAMESPACE
*         edu25/api-okd4-ktdemo-duckdns-org:6443/edu25   api-okd4-ktdemo-duckdns-org:6443   edu25/api-okd4-ktdemo-duckdns-org:6443   edu25
```   

<br/>  

새로 추가한 사용자 계정으로 kubectl을 사용하기 위해선 kubeconfig에 해당 정보가 있어야 한다. 아래 명령어로 추가해보자.  

```bash
kubectl config set-credentials dev25 --client-key=dev25.key --client-certificate=dev25.crt --embed-certs
```     

<br/>

기존이 edu25 계정과 함께 dev25 계정이 추가 된것을 확인 할 수 있습니다.  

```bash
kubectl config view
```  

Output
```bash
apiVersion: v1
clusters:
- cluster:
    insecure-skip-tls-verify: true
    server: https://api.okd4.ktdemo.duckdns.org:6443
  name: api-okd4-ktdemo-duckdns-org:6443
contexts:
- context:
    cluster: api-okd4-ktdemo-duckdns-org:6443
    namespace: edu25
    user: edu25/api-okd4-ktdemo-duckdns-org:6443
  name: edu25/api-okd4-ktdemo-duckdns-org:6443/edu25
current-context: edu25/api-okd4-ktdemo-duckdns-org:6443/edu25
kind: Config
preferences: {}
users:
- name: dev25
  user:
    client-certificate-data: REDACTED
    client-key-data: REDACTED
- name: edu25/api-okd4-ktdemo-duckdns-org:6443
  user:
    token: REDACTED
```

<br/>

이제 K8s 클러스터와 kubectl 사용자를 매칭하는 context를 정의할 차례다.

<br/>

```bash
kubectl config set-context dev25 --user=dev25 --cluster=api-okd4-ktdemo-duckdns-org:6443
```  

Output  
```bash
Context "dev25" created.
```  
<br/>

context를 다시 조회해 본다. 신규로 추가된 것을 알 수 있다.   

```bash
kubectl config get-contexts
```  

Output
```bash
CURRENT   NAME                                           CLUSTER                            AUTHINFO                                 NAMESPACE
          dev25                                          api-okd4-ktdemo-duckdns-org:6443   dev25
*         edu25/api-okd4-ktdemo-duckdns-org:6443/edu25   api-okd4-ktdemo-duckdns-org:6443   edu25/api-okd4-ktdemo-duckdns-org:6443   edu25
```

<br/>

context 를 바꿔봅니다.    

```bash
kubectl config use-context dev25
```  

Output
```bash
Switched to context "dev25".
```  

<br/>

새로 추가한 사용자 `dev25` 으로  OKD 클러스터 에 접근할 수 있다. 다만 RBAC 설정을 하지 않은 상태이므로, 대부분의 명령어는 실패한다.  

pod 를 조회해 보면 권한이 없다고 나옵니다.  

```bash
kubectl get po -n edu25
```  
Output
```bash
Error from server (Forbidden): pods is forbidden: User "dev25" cannot list resource "pods" in API group "" in the namespace "edu25"
```  

<br/> 

#### Openssl

<br/>
openssl 은 데이터통신을 위한 TLS, SSL 프로토콜을 이용할 수 있는 오픈소스 라이브러리 입니다.

<br/>

#### x509

<br/>

x509란 ITU-T가 만든 PKI(Public Key Infrastructure, 공개키기반구조)의 표준입니다.
- https://gruuuuu.github.io/security/what-is-x509/  

<br/>

#### PEM , OpenSSH , CRT , 개인키 차이

<br/>

참고 : https://www.lesstif.com/software-architect/pem-cer-der-crt-csr-113345004.html  

<br/>

> PEM  

PEM (Privacy Enhanced Mail)은 Base64 로 인코딩한 텍스트 형식의 파일입니다.

Binary 형식의 파일을 전송할 때 손상될 수 있으므로 TEXT 로 변환하며 소스 파일은 모든 바이너리가 가능하지만 주로 인증서나 개인키가 됩니다.

AWS 에서 EC2 Instance 를 만들때 접속용으로 생성하는 개인키도 PEM 형식입니다.

어떤 바이너리 파일을 PEM 으로 변환했는지 구분하기 위해 파일의 맨 앞에 dash(-) 를 5 개 넣고 BEGIN 파일 유형을 넣고 다시 dash(-) 를 5개 뒤에 END 파일유형 구문을 사용합니다.      

<br/>

> OpenSSH Private Key  

즉 아래는 OPENSSH Private Key 를 PEM 으로 변환한 예시로 BEGIN OPENSSH PRIVATE KEY 로 시작하는 것을 확인할 수 있습니다.    

ssh-keygen 으로 생성하며 id_rsa private key이 아래와 같이 만들어 집니다.

```bash
-----BEGIN OPENSSH PRIVATE KEY-----
b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAABlwAAAAdzc2gtcn
NhAAAAAwEAAQAAAYEAsXOlJGsuzdxK6fC9DuQ473b6bwCz85Zi0AcpG9HZg1YhAyJNpl6S
2CQCd3gxi3OtdEofXbtRr9xyr7GfD5Cl8vw9d0gQ1q21mrVC+b1/lAiI5XRI9qvi4ORRSf
SwOviCse3cqAZwMlbOUhWKzynLeYF11JdTQH/uAhSSROa0wgKGlPfCdgRYo7piU7UDXHnz
t17w+CpofslmihF2gPEzRicbAmL9hkUDifwFnY/6fVuc0DSQDqgGGRLaKG32/FFX0iP4zW
yMRrCkdEo39E9wyLS3nx1xjQdYIEkjVYBxSiktWKEiYoVlmVUmBejmzNXOh/XQPs2tzUqM
Ji67bGMl5niAH9W2V5MxH7HiqZceR9ovOZuu/BajFrGP3H6EKMyNC9t9gwe9RV2bUMmrPW
+ygHoF8fIR8ZGoSv2GvoVtMBu/6QOkucp+DH+8bdHqRZNWK0muk/BEy8NPnDp2bpd/EDMV
2fJVvi1iZYPxO0vM73PZGQpoYfVEbh5fDNiBH5wbAAAFmAduPlUHbj5VAAAAB3NzaC1yc2
EAAAGBALFzpSRrLs3cSunwvQ7kOO92+m8As/OWYtAHKRvR2YNWIQMiTaZektgkAnd4MYtz
rXRKH127Ua/ccq+xnw+QpfL8PXdIENattZq1Qvm9f5QIiOV0SPar4uDkUUn0sDr4grHt3K
gGcDJWzlIVis8py3mBddSXU0B/7gIUkkTmtMIChpT3wnYEWKO6YlO1A1x587de8PgqaH7J
ZooRdoDxM0YnGwJi/YZFA4n8BZ2P+n1bnNA0kA6oBhkS2iht9vxRV9Ij+M1sjEawpHRKN/
RPcMi0t58dcY0HWCBJI1WAcUopLVihImKFZZlVJgXo5szVzof10D7Nrc1KjCYuu2xjJeZ4
gB/VtleTMR+x4qmXHkfaLzmbrvwWoxaxj9x+hCjMjQvbfYMHvUVdm1DJqz1vsoB6BfHyEf
...
1naaYgzRtVzMcQn7cKt8JBAQu44x15ocvGTKFPLM4O04nhmuobGBCzU2KuLjFjivbRU7bE
z6SuQgNn+YAHFX2BuPhkfD6TQKBzdIbLCkBZSi2ANOAyN/Vli+siDnf58cGD6K0WfDNF/1
1E/yW2sXbmiRsAAAAdamFrZWxlZUBqYWtlLU1hY0Jvb2tBaXIubG9jYWwBAgMEBQY=
-----END OPENSSH PRIVATE KEY-----
```  

<br/>

> CRT  : PKI 인증서(Certificate)는 BEGIN CERTIFICATE 구문으로 시작합니다.  

인증서를 의미하는 CERT 의 약자로 보통 PEM 형식의 인증서를 의미하며 Linux 나 Unix 계열에서 .crt 확장자를 많이 사용합니다. 

에디터로 파일을 열어서 BEGIN CERTIFICATE 구문이 있는지 확인하면 됩니다.  

```bash
cat dev25.crt
```  

Output
```bash
-----BEGIN CERTIFICATE-----
MIIDSzCCAjOgAwIBAgIQRKmzUbKZd877wcusj7xkJzANBgkqhkiG9w0BAQsFADAm
MSQwIgYDVQQDDBtrdWJlLWNzci1zaWduZXJfQDE2OTkwMjExNDgwHhcNMjMxMTA1
MDAwNDU4WhcNMjMxMjAzMTQxOTA4WjBVMQswCQYDVQQGEwJBVTETMBEGA1UECBMK
U29tZS1TdGF0ZTEhMB8GA1UEChMYSW50ZXJuZXQgV2lkZ2l0cyBQdHkgTHRkMQ4w
DAYDVQQDEwVkZXYyNTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAN6Z
XX/4vAkeCd9Wt1AHVC0NuIhJwdKaqJtIO4HGqmEv0zds8zr4QW1igbu25MsB6Wil
iDB/NInCQp4NycAvE7ihd54TKoCpxGjiOyBN/+u/fhY9EvK+1lWkN/OZmVjK7e62
L6qgstmv79MZfgwJ0hhBRa29aVqCFSEvhwwwCdDoiFYM4tQJ5s79h5vH3wsBbYqd
mdvP2UbAUWLeu/JNwlTxf8PdD9cxpZzcgFdEgQcq7J6bxqz+sVEuy0Iv87xAvfzQ
rU8oPqU1XKZxH5N2hADtFZ+8hNg6DDYl2H3wNNyRSvoMGbU+wVjX6hnZGR6+J+NU
sE1hURijc0ZZhNo7er0CAwEAAaNGMEQwEwYDVR0lBAwwCgYIKwYBBQUHAwIwDAYD
VR0TAQH/BAIwADAfBgNVHSMEGDAWgBRuKoNRghDuJm6MluwqfRG7U4vUBTANBgkq
hkiG9w0BAQsFAAOCAQEAdnyqk3ZREpMQSKvVIAqbBNtPh9MD7kT+YBBsXzFaT/Ml
+qpick0TlpEqT4nuUenHqFmUloSK/eCIO0wmiZt27xVL9SPemqRJsPgkZ6KvBAPc
EOFogP7svoFBTcbb7D1kDRAWVGGJtsjGysmWLtr/GvnzEoIPvXWZdyctl1P8boaR
iWVBOXhx6UMEw38Rq1BYvi0LfG4yN9eos+R3kEyDaDIqhKeKLEl2L69kbS6OE3B1
LpYn61a8UaM1JZKKKXx6SBujOx3W3p0LQhACbM/GWtGQccGiHrjiv/IJA0jUTU5z
KSGuwSK9sNhFIRPU2aJxDOAE2/m9UESRwxGdFzU7yw==
-----END CERTIFICATE-----
```  

<br/>

> 개인키  ( PEM 형식 )

개인키는 BEGIN RSA PRIVATE KEY 로 시작합니다.  

```bash
cat dev25.key
```  

```bash
-----BEGIN RSA PRIVATE KEY-----
MIIEpAIBAAKCAQEA3pldf/i8CR4J31a3UAdULQ24iEnB0pqom0g7gcaqYS/TN2zz
OvhBbWKBu7bkywHpaKWIMH80icJCng3JwC8TuKF3nhMqgKnEaOI7IE3/679+Fj0S
8r7WVaQ385mZWMrt7rYvqqCy2a/v0xl+DAnSGEFFrb1pWoIVIS+HDDAJ0OiIVgzi
1Anmzv2Hm8ffCwFtip2Z28/ZRsBRYt678k3CVPF/w90P1zGlnNyAV0SBByrsnpvG
rP6xUS7LQi/zvEC9/NCtTyg+pTVcpnEfk3aEAO0Vn7yE2DoMNiXYffA03JFK+gwZ
tT7BWNfqGdkZHr4n41SwTWFRGKNzRlmE2jt6vQIDAQABAoIBAQDEnnMYNnzhEMdn
nxEMf2y63wPAXmX1wOZtQsBNQU39ymCm9HVkASTJmdk+Fa7CIk4pQQ2qyLF/fTea
pFMwjmS9EOK3nfZM76etfSb8wejsM5kLy6aRBEAOJZ/GbEYnSBgiYop4DLntzpnn
vPy5ZXNOOVlyvXvxljVTusdu3H/PJfAON0MPjqScbGwx8g5A5IeVBpwUQZPAPZVV
TgWN1cBF8VYHhRVmDfH9h72CkH37FWbHB/2L3Z76vGcYap1SrSJhj/BeggbHDi2c
u60xS2oV1dN6cyCy3oxE+AKKahoZ6ri/t/ooFqIhbPl8hMr6yyekBikg9uftcmRp
TXLnFX+BAoGBAPqy3faB805A+FrkiHENRxtORDwoPY8hVrvu9XBVlk5jr/bYfCPL
GesTpJjNVRvNyLEBNL06qOvPaFHdsTVPUZ2Jwlhdcff8HhXmxkl6hY9vp7yFFVdv
0DmFpFZOrGaHhZvuayP9PZ3ZsqJmzxDLmBD/kzUrnyy+pYjugqh09VnzAoGBAONO
YjdvTbKLZcqDCV7D6CJuqmDWZPgzfLwAtBvMbYUVsFvO0HMAwbkR8wI9UFSdocPE
iWebiYSV5ArrR71yaCuQaPahrMVq35HYP/Lot6mPJgg6LnyhKgDy/7TWDgKsw23C
0PXcKG89+2oLoo1Zs7wclWlpOR3SlmOj5Suxy9SPAoGAJD2rPLF4fL2DqZAT8VPc
DaR41MF0dLZ7FVvr+ztEKTzb+TE+cOYxbvw99SDpxsUu1/e2qgxK0xv+lqcXsP8w
aze48pE/onu91aiwzXp6yEt50hTjCurNDSO2qAtjfMbml64VqvQ27hTEcBmwoVrt
NrfbjfoqXouI3oysMrIFreUCgYA4aNNm/nBBxuZUA4Dny6ZoJR6TOaGFFwH1hhcs
bucfB+rkXcbNQ3rP+uxbueudlCD4/GU9GRRfmvMk4o7DLQk9BnGGA0llFMi24Pu9
xJMPuT6u/AFdXIGYCrX6osSHVWiKbLZ+zUwbjz49avXELma0YEOUDVDnXcOEpr/Q
wCbdcQKBgQDGNhJUJz4tmjP/+Fh13sIwA6fwX+PSLdUT1mrL61vBnggQqvLCan9Y
+jrgg2IhoO6LFqGfW62QzM4l4FK/G4CW5qkCFVxC6L1pXXY3qlyP1VbrHn/0uIPn
suUrjOj+/IYpbbjTbDxfS71lQdV1ekl3C3SCkK7GABFA+EWiV7EVNw==
-----END RSA PRIVATE KEY-----
```  

<br/>

#### openshift ca 보기

<br/>

elastic 에서 kube-state-metric를 조회하기 위하여  

`openshift-monitoring`  namespace 의 secret 에서 `kube-state-metrics-token` 으로 시작하는 값을 선택 했었습니다.


<img src="./assets/elastic_metric_15.png" style="width: 80%; height: auto;"/>

<br/>

`service-ca.crt` 는 서비스 SSL CA 이고 `ca.crt` 는 API 서버의  SSL CA 이다.    

<img src="./assets/elastic_metric_16.png" style="width: 80%; height: auto;"/>  

reveal values를 해서 `ca.crt` 값을 복사해서 저장합니다.  

<br/>

```bash
openssl x509 -noout  -text -in ca.crt
```  

인증서에 대한 내용을 볼수 있고 유효기간 ( 10년 ) 과 CN 등을 확인 합니다.

```bash
Certificate:
    Data:
        Version: 3 (0x2)
        Serial Number: 3825283850589108238 (0x351623651766f80e)
        Signature Algorithm: sha256WithRSAEncryption
        Issuer: OU = openshift, CN = kube-apiserver-lb-signer
        Validity
            Not Before: Sep  1 00:36:54 2023 GMT
            Not After : Aug 29 00:36:54 2033 GMT
        Subject: OU = openshift, CN = kube-apiserver-lb-signer
        Subject Public Key Info:
            Public Key Algorithm: rsaEncryption
                RSA Public-Key: (2048 bit)
                Modulus:
                    00:e7:07:f8:8a:48:37:2b:0b:ec:a4:9f:3e:63:21:
                    49:e4:6f:d6:4f:ae:68:b5:87:5f:f0:3b:ce:70:89:
                    5f:7b:9f:6e:27:56:69:16:d1:e5:39:6d:52:a4:c2:
                    40:7b:e3:14:26:6b:78:e9:2a:eb:d4:dd:6f:6f:d2:
                    95:63:7e:0b:00:5c:51:82:a0:c1:77:61:13:3e:b6:
                    16:ad:02:2e:b3:87:bc:e5:af:2b:29:bc:d1:c8:22:
                    60:68:22:74:63:cd:b8:fa:26:19:12:ee:2d:e9:bc:
                    42:dd:9d:80:de:b9:f1:65:d0:41:b4:57:2a:5f:a0:
                    f5:4f:e1:66:96:8a:e8:58:da:7a:4f:fd:9a:d6:01:
                    30:0d:cd:d9:4c:a8:3c:59:30:00:74:35:f1:5d:c7:
                    3c:9e:a0:f2:8c:88:12:f7:0d:d2:9a:e5:06:5c:a4:
                    60:26:0b:54:8a:40:0e:84:40:6b:a0:fc:fe:01:c1:
                    34:e7:0e:6c:ec:54:a0:45:7c:4c:37:86:67:1c:14:
                    90:31:c7:b7:85:b6:c6:31:ea:fd:5e:82:aa:d7:ce:
                    35:7f:e9:46:c2:5c:7a:de:8a:2c:e7:f6:bf:e7:94:
                    a6:a6:16:1d:2f:09:97:80:43:a8:dc:9a:51:4e:d6:
                    c3:8d:79:99:17:a6:bb:7b:c5:5a:d2:71:05:1e:1c:
                    df:3d
                Exponent: 65537 (0x10001)
        X509v3 extensions:
            X509v3 Key Usage: critical
                Digital Signature, Key Encipherment, Certificate Sign
            X509v3 Basic Constraints: critical
                CA:TRUE
            X509v3 Subject Key Identifier:
                C8:13:AA:B0:A6:6E:CD:8C:9F:CD:3E:B1:D8:7F:4F:57:2D:63:24:BC
    Signature Algorithm: sha256WithRSAEncryption
         a0:1f:09:d0:ba:c3:0c:b8:ac:64:fa:5b:a4:01:d2:be:45:c6:
         0a:a5:53:9f:99:eb:b9:ef:29:2c:7e:f0:9f:a1:37:ad:b8:7f:
         10:7b:69:c0:cc:19:80:f0:a9:69:4e:fa:42:f9:9c:13:56:76:
         11:3f:d2:b6:66:dc:07:d3:5c:ec:b0:89:f7:49:50:be:5f:f0:
         54:35:f4:85:e1:5a:f5:14:59:ff:6f:fa:35:eb:5f:32:5a:6a:
         af:96:04:cd:5b:b1:e1:a2:88:7d:9a:44:f2:65:3f:e0:e5:a6:
         6c:84:71:50:b4:9c:f3:f7:1d:46:4d:4f:48:db:c4:85:d7:3f:
         f4:9f:e6:d7:b6:82:68:d2:0a:48:40:e1:37:c9:ad:21:39:18:
         bd:45:8b:9e:3c:2d:3d:6b:ea:39:fc:e5:6c:87:ed:b9:73:5c:
         cc:e7:a0:8d:d4:39:40:da:7e:97:e6:b4:c0:0a:23:a7:c6:fd:
         0f:63:4d:a3:7b:c0:de:c6:81:d9:04:b7:79:0f:b8:26:65:b4:
         84:72:50:d9:30:0a:0d:9f:8a:a1:41:b7:ac:b7:91:18:90:62:
         ab:f5:42:47:27:fc:e2:77:ec:40:32:a0:4b:6c:26:ad:89:37:
         44:36:c9:3a:74:15:9d:c4:2d:8e:82:ce:c8:49:bc:09:91:6e:
         e4:a6:a0:a7
```  

<br/>

#### 과제

<br/>

dev 계정에 pod를 조회할수 있는 role 을 생성하고 rolebining을 합니다.  
- 권한 할당을 위해서는 context switching 을 해서 edu 유저의 권한으로 주어야 함.  

<br/>

```bash
root@edu25:~# kubectl apply -f dev_user_role.yaml
role.rbac.authorization.k8s.io/dev-user-role created
rolebinding.rbac.authorization.k8s.io/dev-user-role-binding created
```  

<br/>

### Cluster Roles and Role Bindings

<br/>

Clusterrole은 cluster 전체에 영향을 주는 role 이다.   
- kubectl api-resources --namespaced=true  

Role은 namespace 에 영향을 주는 role 이다.   
- kubectl api-resources --namespaced=false


<img src="./assets/k8s_security_component_5.png" style="width: 80%; height: auto;"/>

<br/>  

클러스터롤은 모든 네임스페이스에 대한 권한을 정의하는 것이고 롤은 특정 네임스페이스에 대한 권한만을 정의하는 것입니다.
정의하는 문법은 롤은 네임스페이스를 지정해야 한다는 것 외에는 동일 합니다.  


<img src="./assets/k8s_role_1.png" style="width: 80%; height: auto;"/> 

<br/>

- `API Group/API Version` 형식으로 apiVersion을 지정하고 `kind`에 리소스의 종류를 `ClusterRole` 또는 `Role`로 합니다.  
- 클러스터 롤은 `name`만 지정하면 되고 롤은 `name`과 `namespace`를 지정 합니다.  
- `rules`는 권한을 부여할 오브젝트의 API Group, 리소스명, CRUD 권한의 세 부분으로 구성 됩니다.

<br/>

어떤 리소스에 대해 권한을 정의할 지 결정 하고
그 리소스의 API Group을 `kubectl api-resources | grep {리소스명}`로 찾음. 찾은 결과에서 API Group과 정확한 리소스명을 `apiGroups`와 `resources`에 추가함.

<br/>

<img src="./assets/k8s_role_2.png" style="width: 80%; height: auto;"/> 

<br/>

어떤 CRUD 권한을 부여할 지 정의함. CRUD별 권한명은 아래와 같음

<img src="./assets/k8s_role_3.png" style="width: 80%; height: auto;"/> 

<br/>

클러스터롤 바인딩은 모든 네임스페이스에 적용되는 클러스터롤과 바인딩하는 것이고 롤 바인딩은 특정 네임스페이스에만 적용되는 롤과 바인딩하는 것입니다  

<br/>

- 클러스터롤 바인딩과 롤 바인딩 정의의 차이는 롤 바인딩은 네임스페이스까지 정의한다는 것입니다.  
- 바인딩할 롤을 정의하는 `roleRef`에서 `kind`는 바인딩할 롤이 클러스터롤이면 `ClusterRole`이라고 하고 롤이면 `Role`이라고 정의하면 됩니다.  
- 바인딩할 대상을 정의하는 `subjects`에서 `kind`는 대상의 유형에 따라 `User`, `Group`, `ServiceAccount`로 지정할 수 있습니다.  

<br/>


<img src="./assets/k8s_role_3.png" style="width: 80%; height: auto;"/> 


<br/>

### Access Check  

<br/>

과제에서 생성한 dev 계정의 권한을 체크해 봅니다.

<br/>

접속한 유저가 create deploy 권한있는지 체크  

```bash
kubectl auth can-i create deployments 
```  

<br/>

특정 유저가 pod 조회 권한이 있는지 확인  
```bash
root@edu25:~# kubectl auth can-i list  pods --as dev25
yes
```  

<br/>

특정 service account 가 pod 조회 권한 있는지 확인

```bash
kubectl auth can-i list po --as=default -n edu25
```

<br/>

## SecurityContext  

<br/>

참고   
- https://earthly.dev/blog/k8s-cluster-security/    
- https://malwareanalysis.tistory.com/584  
- 악분일상 : https://youtu.be/2SSecGVc7SA?si=Jg2152_GQCbbIRwA

<br/>

컨테이너는 프로세스로서 노드 커널 위에 실행됩니다. 그러므로 컨테이너가 커널에 접근할 수 있습니다. 쿠버네티스와 같은 컨테이너 오케스트레이션 도구는 pod가 노드 커널에 접근하지 못하도록 보안설정을 합니다.    

<img src="./assets/k8s_security_context_1.png" style="width: 80%; height: auto;"/>


<br/>

<img src="./assets/k8s_security_context_2.png" style="width: 80%; height: auto;"/>


<br/>

### privileged

<br/>

부득이하게 pod가 커널 접근이 꼭 필요한 상황이 있습니다. 네트워크 설정을 해야 하는 Calico CNI가 대표적인 예입니다. 이런 상황을 대비하여 쿠버네티스는 pod가 커널에 접근할 수 있는 설정을 제공합니다. 그 설정이 바로 securityContext.privileged입니다.   

 
spec.containers.securityContext.privileged를 true로 설정하면 pod가 노드 커널에 접근할 수 있습니다. 디폴트로 false로 설정되어 있습니다.

<br/>

자세한 설명을 보면 host 의 root와 같은 권한이라고 나옵니다.

```bash
kubectl explain pod.spec.containers.securityContext | more
```  

```bash
KIND:     Pod
VERSION:  v1

RESOURCE: securityContext <Object>

DESCRIPTION:
     SecurityContext defines the security options the container should be run
     with. If set, the fields of SecurityContext override the equivalent fields
     of PodSecurityContext. More info:
     https://kubernetes.io/docs/tasks/configure-pod-container/security-context/

     SecurityContext holds security configuration that will be applied to a
     container. Some fields are present in both SecurityContext and
     PodSecurityContext. When both are set, the values in SecurityContext take
     precedence.

FIELDS:
   allowPrivilegeEscalation	<boolean>
     AllowPrivilegeEscalation controls whether a process can gain more
     privileges than its parent process. This bool directly controls if the
     no_new_privs flag will be set on the container process.
     AllowPrivilegeEscalation is true always when the container is: 1) run as
     Privileged 2) has CAP_SYS_ADMIN Note that this field cannot be set when
     spec.os.name is windows.

   capabilities	<Object>
     The capabilities to add/drop when running containers. Defaults to the
     default set of capabilities granted by the container runtime. Note that
     this field cannot be set when spec.os.name is windows.

   privileged	<boolean>
     Run container in privileged mode. Processes in privileged containers are
     essentially equivalent to root on the host. Defaults to false. Note that
     this field cannot be set when spec.os.name is windows.
```  

<br/>

#### 실습 ( privileged )

<br/>

현재 각 계정은 Cluster Admin 권한과 privileged 권한을 이미 할당 받았기 때문에 `privileged : true` 설정이 가능합니다.   


device 목록을 조회하기 위한 POD를 2개를 생성합니다. 
- privileged : true
- privileged : false 

<br/>

```bash
root@edu25:~# cat  privileged.yaml
# privileged:true -> 노드 디바이스 목록 조회 가능
apiVersion: v1
kind: Pod
metadata:
 name: security-privileged-true
spec:
 containers:
 - name: sec-ctx-demo-default
   image: ghcr.io/shclub/netshoot
   command: ["tail"]
   args: ["-f", "/dev/null"]
   securityContext:
     privileged: true
---
# privileged:false -> 노드 디바이스 목록 조회 불가능
apiVersion: v1
kind: Pod
metadata:
 name: security-privileged-false
spec:
 containers:
 - name: sec-ctx-demo-default
   image: ghcr.io/shclub/netshoot
   command: ["tail"]
   args: ["-f", "/dev/null"]
```  

<br/>

2개의 POD를 번갈아 호출해 보면 `security-privileged-false` pod는 pod 내의 device 목록만 나오고 `security-privileged-true` pod 는 해당 Node device 목록까지 나옵니다.  

```bash
root@edu25:~# kubectl exec -it security-privileged-false -- ls /dev
core             full             null             pts              shm              stdin            termination-log  urandom
fd               mqueue           ptmx             random           stderr           stdout           tty              zero
root@edu25:~# kubectl exec -it security-privileged-true -- ls /dev
autofs           mcelog           sda4             tty14            tty30            tty47            tty63            ttyS21           udmabuf          vcsa5
bsg              mem              sg0              tty15            tty31            tty48            tty7             ttyS22           uhid             vcsa6
btrfs-control    mqueue           sg1              tty16            tty32            tty49            tty8             ttyS23           uinput           vcsu
core             net              shm              tty17            tty33            tty5             tty9             ttyS24           urandom          vcsu1
cpu              null             snapshot         tty18            tty34            tty50            ttyS0            ttyS25           usbmon0          vcsu2
cpu_dma_latency  nvram            snd              tty19            tty35            tty51            ttyS1            ttyS26           userfaultfd      vcsu3
dma_heap         port             sr0              tty2             tty36            tty52            ttyS10           ttyS27           vcs              vcsu4
dri              ppp              stderr           tty20            tty37            tty53            ttyS11           ttyS28           vcs1             vcsu5
fb0              ptmx             stdin            tty21            tty38            tty54            ttyS12           ttyS29           vcs2             vcsu6
fd               ptp0             stdout           tty22            tty39            tty55            ttyS13           ttyS3            vcs3             vfio
full             pts              termination-log  tty23            tty4             tty56            ttyS14           ttyS30           vcs4             vga_arbiter
fuse             random           tty              tty24            tty40            tty57            ttyS15           ttyS31           vcs5             vhci
hpet             rfkill           tty0             tty25            tty41            tty58            ttyS16           ttyS4            vcs6             vhost-net
hwrng            rtc0             tty1             tty26            tty42            tty59            ttyS17           ttyS5            vcsa             vhost-vsock
input            sda              tty10            tty27            tty43            tty6             ttyS18           ttyS6            vcsa1            vmbus
kmsg             sda1             tty11            tty28            tty44            tty60            ttyS19           ttyS7            vcsa2            zero
loop-control     sda2             tty12            tty29            tty45            tty61            ttyS2            ttyS8            vcsa3
mapper           sda3             tty13            tty3             tty46            tty62            ttyS20
```

<br/>

privileged 권한은 NFS를 통한 연동시에도 필요하여 ElasticSearch 같은 솔루션에서도 대부분 필요합니다.

<br/>


### capability

<br/>


리눅스는 capability 라는 기능으로 커널권한을 분리합니다. 리눅스 초창기에는 root계정이 모든 권한이 있었고 커널 2.2부터 capability로 커널권한을 분리했습니다. 그래서 root계정이라도 capability설정이 없다면 작업이 제한됩니다. 

capabilities 목록은 상당히 많이 있습니다.  

```bash
root@edu25:~# man capabilities | more
CAPABILITIES(7)                                                                Linux Programmer's Manual                                                               CAPABILITIES(7)

NAME
       capabilities - overview of Linux capabilities

DESCRIPTION
       For  the purpose of performing permission checks, traditional UNIX implementations distinguish two categories of processes: privileged processes (whose effective user ID is 0,
       referred to as superuser or root), and unprivileged processes (whose effective UID is nonzero).  Privileged processes bypass all kernel permission checks,  while  unprivileged
       processes are subject to full permission checking based on the process's credentials (usually: effective UID, effective GID, and supplementary group list).

       Starting  with  kernel 2.2, Linux divides the privileges traditionally associated with superuser into distinct units, known as capabilities, which can be independently enabled
       and disabled.  Capabilities are a per-thread attribute.

   Capabilities list
       The following list shows the capabilities implemented on Linux, and the operations or behaviors that each capability permits:

       CAP_AUDIT_CONTROL (since Linux 2.6.11)
              Enable and disable kernel auditing; change auditing filter rules; retrieve auditing status and filtering rules.

       CAP_AUDIT_READ (since Linux 3.16)
              Allow reading the audit log via a multicast netlink socket.

       CAP_AUDIT_WRITE (since Linux 2.6.11)
              Write records to kernel auditing log.

       CAP_BLOCK_SUSPEND (since Linux 3.5)
              Employ features that can block system suspend (epoll(7) EPOLLWAKEUP, /proc/sys/wake_lock).

       CAP_CHOWN
              Make arbitrary changes to file UIDs and GIDs (see chown(2)).

       CAP_DAC_OVERRIDE
              Bypass file read, write, and execute permission checks.  (DAC is an abbreviation of "discretionary access control".)

       CAP_DAC_READ_SEARCH
              * Bypass file read permission checks and directory read and execute permission checks;
              * invoke open_by_handle_at(2);
```  


<br/>

capability는 계정과 프로세스에 설정됩니다. 계정은 capsh명령어로 확인할 수 있습니다.  

현재 root 계정의 capability를 봅니다.  Current가 현재 가진것 입니다.  

```bash
root@edu25:~# capsh --print
Current: = cap_chown,cap_dac_override,cap_dac_read_search,cap_fowner,cap_fsetid,cap_kill,cap_setgid,cap_setuid,cap_setpcap,cap_linux_immutable,cap_net_bind_service,cap_net_broadcast,cap_net_admin,cap_net_raw,cap_ipc_lock,cap_ipc_owner,cap_sys_module,cap_sys_rawio,cap_sys_chroot,cap_sys_ptrace,cap_sys_pacct,cap_sys_admin,cap_sys_boot,cap_sys_nice,cap_sys_resource,cap_sys_time,cap_sys_tty_config,cap_mknod,cap_lease,cap_audit_write,cap_audit_control,cap_setfcap,cap_mac_override,cap_mac_admin,cap_syslog,cap_wake_alarm,cap_block_suspend,cap_audit_read,38,39,40+ep
Bounding set =cap_chown,cap_dac_override,cap_dac_read_search,cap_fowner,cap_fsetid,cap_kill,cap_setgid,cap_setuid,cap_setpcap,cap_linux_immutable,cap_net_bind_service,cap_net_broadcast,cap_net_admin,cap_net_raw,cap_ipc_lock,cap_ipc_owner,cap_sys_module,cap_sys_rawio,cap_sys_chroot,cap_sys_ptrace,cap_sys_pacct,cap_sys_admin,cap_sys_boot,cap_sys_nice,cap_sys_resource,cap_sys_time,cap_sys_tty_config,cap_mknod,cap_lease,cap_audit_write,cap_audit_control,cap_setfcap,cap_mac_override,cap_mac_admin,cap_syslog,cap_wake_alarm,cap_block_suspend,cap_audit_read,38,39,40
Securebits: 00/0x0/1'b0
 secure-noroot: no (unlocked)
 secure-no-suid-fixup: no (unlocked)
 secure-keep-caps: no (unlocked)
uid=0(root)
gid=0(root)
groups=0(root)
```  

<br/>

#### 실습 하기 ( capability )   

<br/>

netshoot pod를 생성합니다. 현재 capability를 설정하지 않았습니다.  

```bash
root@edu25:~# cat netshoot_pod.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: netshoot-pod
spec:
  replicas: 1
  selector:
    matchLabels:
      app: netshoot-pod
  template:
    metadata:
      labels:
        app: netshoot-pod
    spec:
      containers:
      - name: netshoot-pod
        image: ghcr.io/shclub/netshoot
        command: ["tail"]
        args: ["-f", "/dev/null"]
      terminationGracePeriodSeconds: 0
```   

<br/>

적용합니다.  

```bash
kubectl apply -f netshoot_pod.yaml
```  

<br/>

POD 안으로 들어갑니다.  
```bash
kubectl exec -it netshoot-pod-85b5dfb564-bcpvg sh
```  

ping google.com 을 해보면 response 가 옵니다.  

```bash
~ # ping google.com
PING google.com (172.217.161.238) 56(84) bytes of data.
64 bytes from kix06s05-in-f14.1e100.net (172.217.161.238): icmp_seq=1 ttl=114 time=40.2 ms
64 bytes from kix06s05-in-f14.1e100.net (172.217.161.238): icmp_seq=2 ttl=114 time=39.6 ms
64 bytes from kix06s05-in-f14.1e100.net (172.217.161.238): icmp_seq=3 ttl=114 time=39.0 ms
64 bytes from kix06s05-in-f14.1e100.net (172.217.161.238): icmp_seq=4 ttl=114 time=38.8 ms
64 bytes from kix06s05-in-f14.1e100.net (172.217.161.238): icmp_seq=5 ttl=114 time=40.8 ms
^C
--- google.com ping statistics ---
5 packets transmitted, 5 received, 0% packet loss, time 6157ms
rtt min/avg/max/mdev = 38.780/39.675/40.758/0.733 ms
```  

<br/>

이제 capability 를 적용해 봅니다.  

deploy를 수정을 합니다.   

```bash
kubectl edit deploy  netshoot-pod 
```
<br/>

`securityContext` 에 `capabilities` 를 적용합니다.    

아래는 ping 통신에 필요한 NET_RAW 기능을 제거합니다.   

```bash
        name: netshoot-pod
        # capability제거
        securityContext:
          capabilities:
            drop: ["NET_RAW"]
        resources: {}
```

<br/>

다시 새로 생성된 POD에 접속하여 ping `google.com` 을 해봅니다.  

NET_RAW가 제거된 pod에서 ping을 날려보면 권한이 없다고 거부됩니다.

<br/>

elastic namespace 의 elastic-master pod에 들어가 보면 보안을 위해서 capabilities 를 모두 제거 했습니다.  

<img src="./assets/k8s_security_context_3.png" style="width: 80%; height: auto;"/>

<br/>

### readOnlyRootFilesystem   

<br/>

쿠버네티스 컨테이너 내부 파일을 수정하지 못하게 하려면 어떻게 해야할까요? 파일 읽기권한만 설정하면 됩니다. 쿠버네티스에서 파일읽기권한만 설정하는 것을 `readOnlyRootFilesystem` 이라고 합니다.   

`securitycontext.readOnlyRootFilesystem` 필드에 `boolean` 값으로 설정할 수 있습니다.

```bash
apiVersion: v1
kind: Pod
metadata:
  name: rootfile-readonly
spec:
  containers:
  - name: busybox
    image: busybox
    command: ["tail"]
    args: ["-f", "/dev/null"]
    securityContext:
      readOnlyRootFilesystem: true
  terminationGracePeriodSeconds: 0
```  

<br/>

rootFilesystem의미는 컨테이너마다 독립적으로 갖는 파일시스템을 말합니다.  
- 참고 : https://tech.kakaoenterprise.com/171  

<br/>

> 적용이 안되는 예외 파일  

  readOnlyRootFilesystem를 true로 설정하더라도 쓰기 가능한 파일이 존재합니다. /etc/hosts파일이 한 예입니다.  

<br/>

### allowPrivilegeEscalation   

<br/>

allowPrivilegeEscalation은 컨테이너가 실행 된 후, 컨테이너 유저 권한보다 높은 권한을 제어합니다.  

공식문서 allowPrivilegeEscalation설명을 보면 부모 프로세스보다 높은 권한을 얻는 것을 제어한다고 되어 있습니다.   

컨테이너에서 부모(최초) 프로세스는 entrypoint로 실행된 프로세스입니다. entrypoint는 Dockerfile에서 USER로 설정된 사용자로 실행됩니다.  

결국, 컨테이너 유저보다 높은 권한을 제어한다는 의미입니다.   

<br/>

### 실습 ( allowPrivilegeEscalation  ) 

<br/>

`ubuntu` 라는 폴더 이름을 생성합니다. 그 안에 Dockerfile을 생성합니다.  

```bash
mkdir ubuntu 
```  

<br/>

그 안에 Dockerfile을 생성합니다.

```bash
root@edu25:~/security/ubuntu# cat Dockerfile
FROM ubuntu:20.04

RUN apt-get update && \
    apt-get -y install sudo && \
    echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

RUN adduser --disabled-password --gecos '' testuser && \
    adduser testuser sudo

USER testuser
```  

<br/>

도커이미지를 생성하고 github 에 push 합니다.  

```bash
root@edu25:~/security/ubuntu# docker build -t ubuntu:sudo .
root@edu25:~/security/ubuntu# docker tag ubuntu:sudo ghcr.io/shclub/ubuntu:sudo
root@edu25:~/security/ubuntu# docker login ghcr.io
root@edu25:~/security/ubuntu# docker push ubuntu:sudo ghcr.io/shclub/ubuntu:sudo
```  

<br/>

POD를 실행합니다.    

`allowPrivilegeEscalation` 기본 설정은 `true` 입니다.

```bash
root@edu25:~/security# cat ubuntu-allow-sudo.yaml
# kubectl apply -f ubuntu-allow-sudo.yaml
apiVersion: v1
kind: Pod
metadata:
 name: ubuntu-allow-sudo
spec:
 containers:
 - name: main
   image: ghcr.io/shclub/ubuntu:sudo
   command: [ "sh", "-c", "sleep 1h" ]
root@edu25:~/security# kubectl apply -f ubuntu-allow-sudo.yaml
```  

<br/>

sudo 가 가능한지 확인합니다.  

```bash
kubectl exec -it ubuntu-allow-sudo -- sudo id && echo "sudo success"
```  

Output
```bash
uid=0(root) gid=0(root) groups=0(root)
sudo success
```  

<br/>

sudo 권한을 사용하지 못하도록 `allowPrivilegeEscalation:false` 를 설정합니다.

```bash
root@edu25:~/security# cat ubuntu-disallow-sudo.yaml
# kubectl apply -f ubuntu-disallow-sudo.yaml
apiVersion: v1
kind: Pod
metadata:
 name: ubuntu-disallow-sudo
spec:
 containers:
 - name: main
   image: ghcr.io/shclub/ubuntu:sudo
   command: [ "sh", "-c", "sleep 1h" ]
   securityContext:
     allowPrivilegeEscalation: false
```  

<br/>

pod에서 sudo명령어 사용이 불가능한지 확인합니다. 부모 프로세스(pid: 1)보다 높은 권한을 실행하지 못하도록 쿠버네티스가 제한합니다.    


```bash
root@edu25:~/security# kubectl apply -f ubuntu-disallow-sudo.yaml
root@edu25:~/security# kubectl exec -it ubuntu-disallow-sudo -- sudo id && echo "sudo success"
sudo: effective uid is not 0, is /usr/bin/sudo on a file system with the 'nosuid' option set or an NFS file system without root privileges?
command terminated with exit code 1
```  


  securityContext:
    runAsUser: 1000
    fsGroup: 1000


          securityContext:
        capabilities:
          drop:
            - ALL
        runAsUser: 1000
        runAsNonRoot: true
<br/>

### 추가 사항 

<br/>

참고   

-  https://moozii-study.tistory.com/entry/%EC%BF%A0%EB%B2%84%EB%84%A4%ED%8B%B0%EC%8A%A4-13-%ED%81%B4%EB%9F%AC%EC%8A%A4%ED%84%B0-%EB%85%B8%EB%93%9C%EC%99%80-%EB%84%A4%ED%8A%B8%EC%9B%8C%ED%81%AC-%EB%B3%B4%EC%95%88    

- https://snyk.io/blog/10-kubernetes-security-context-settings-you-should-understand/  

<br/>

SecurityContext란 Pod/Container의 권한을 수정하는 데 사용되는 기능으로, 프로세스의 사용자/그룹을 수정하거나 보안정책, 권한 등을 설정하게 된다.  

대표적인 몇가지만 간단히 나열하자면,  
 - hostPID/IPC : 해당 리소스가 node의 PID와 IPC를 사용할 수 있게되어 직접 통신이 가능해진다  
 - hostNetwork : 해당 리소스가 node의 network interface에 접근할 수 있게된다  
 - runAsUser: 해당 리소스의 사용자 ID(UID)를 지정한다. 디폴트 값이 0 이기에, 특정 UID를 할당해서 권한을 제한할 수 있다  
 - fsGroup : 해당 리소스(파드)의 볼륨에 대한 그룹을 할당.  
 - seLinux : 해당 리소스에 selinux 지정    
 - privileged : 해당 리소스(컨테이너)가 privileged로 생성되도록 지정. privileged가 설정되면, 노드의 모든 리소스에 접근이 가능해진다!  
 - seccomp : 해당 리소스 내의 프로세스가 사용할 수 있는 시스템콜의 종류를 제한  

<br/>

## Pod Security Policy ( PSP )  

<br/>

참고 : https://ikcoo.tistory.com/68  

<br/>

### PodSecurityPolicy(PSP) 란?
 
<br/>

SecurityContext가 Pod와 Container에 적용되는 정책 설정이라면 Pod Security Policy(PSP)는 Kubernetes Cluster 전체에 적용됨.     

<br/>

<img src="./assets/k8s_security_context_4.png" style="width: 80%; height: auto;"/>

<br/> 

- Pod Security Policy를 통해 생성 또는 업데이트 되는 Pod 내부 Container의 
권한에 대한 정책, Volume, 파일 권한 등 설정이 가능.   

<br/>

- Pod 내 Container가 불필요하게 과도한 권한을 가지지 않도록 주의 해야하는데 이를 PodSecurityPolicy 를 사용해서 권한을 조절함.  

- Kubernetes Cluster는 Default로 Pod Security Policy이 비활성화  

- Pod Security Policy를 활성화 하기 위해서는 Pod Security Policy Admission Controller를 활성화 해야함  

 - Pod의 Spec: 에 설정되는 항목에 대한 권한을 설정함.

<br/>

### Pod Security Policy(PSP)와 Security Context 관계
 
<br/>

Pod Security Policy(PSP)는 Security Context 보다 상위 보안설정.  
 

- Security Context는 Pod와 Container에 적용되는 설정이라면
Pod Security Policy 는 클러스터 전체에 적용되기 때문에
Pod Security Policy가 부여하지 않은 권한을 Security Context 가 부여할 수 없음  

- Pod Security Policy 에서 Privileged 권한을 false로 줬는데 Security Context 에서 true 로 주면 해당 컨테이너는 생성되지 않음  

<br>

## 참고 자료

<br/>

- cert manager : https://velog.io/@wanny328/Kubernetes-Cert-Manager-%EC%95%8C%EC%95%84%EB%B3%B4%EA%B8%B0  

- SSL 이란  : http://idchowto.com/%EC%9D%B8%EC%A6%9D-%EA%B8%B0%EA%B4%80certificate-authority-ca%EC%9D%B4%EB%9E%80/    

- CA :  https://aws-hyoh.tistory.com/m/59    

- Polaris : https://velog.io/@imok-_/k8s-security     



 

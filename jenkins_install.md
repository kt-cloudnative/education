## Jenkins 설치하기

### kubernetes에 Jenkins Master/Slave 구성하기


<br/>

로그인 한 후에 jenkins 폴더를 생성한다.  

<br/>

```bash
root@newedu:~# mkdir -p jenkins
root@newedu:~# cd jenkins
```

<br/>

#### 계정 생성과 RBAC 생성

<br/>

jenkins admin 계정의 secret를 생성하기 위해 계정과 비밀번호를 base64로 인코딩 한다.

<br/>

계정과 비밀번호는 admin/New1234! 로 설정

TODO 

<br/>

secret를 만들기 위해 yaml 화일을 생성한다.        

TODO  

secret은 2개의 필드가 필요하며 아래 helm jenins_values.yaml 화일의 
2개의 필드 ( 유저와 비밀번호 ) 와 이름이 같아야 한다.  

<br/>

```bash
 50   admin:
 51     existingSecret: "jenkins-admin-secret" #
 52     userKey: jenkins-admin-user
 53     passwordKey: jenkins-admin-password
```  

<br/>

data 부분에 base64 인코딩 된 값을 넣어준다.

`jenkins-admin-secret` 이름으로 secret 생성.

<br/>

TODO

<br/>

secret를 생성하고 확인한다.

<br/>

```bash
root@newedu:~/jenkins# kubectl apply -f jenkins-edu-admin-secret.yaml
secret/jenkins-admin-secret created
root@newedu:~/jenkins# kubectl get secret
NAME                                 TYPE                                  DATA   AGE
jenkins-admin-secret                 Opaque                                2      7s
my-service-account-dockercfg-4j5n7   kubernetes.io/dockercfg               1      169d
my-service-account-token-d67wk       kubernetes.io/service-account-token   4      169d
my-service-account-token-wxttf       kubernetes.io/service-account-token   4      169d
super-secret                         Opaque                                1      169d
```

<br/>

`jenkins-admin` service account 를 생성 하고  Role 과 Rolebinding 을 생성한다.  

TODO : sa 생성

<br/>


```bash
root@newedu:~/jenkins# vi jenkins_rbac.yaml
```

<br/>
jenkins-admin 권한은 최소화 한다.

<br/>

```bash
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: jenkins-admin
rules:
- apiGroups: [""]
  resources: ["pods"]
  verbs: ["create","delete","get","list","patch","update","watch"]
- apiGroups: [""]
  resources: ["pods/exec"]
  verbs: ["create","delete","get","list","patch","update","watch"]
- apiGroups: [""]
  resources: ["pods/log"]
  verbs: ["get","list","watch"]
- apiGroups: [""]
  resources: ["secrets"]
  verbs: ["get"]
- apiGroups: [""]
  resources: ["events"]
  verbs: ["get", "watch"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: jenkins-admin
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: Role
  name: jenkins-admin
subjects:
- kind: ServiceAccount
  name: jenkins-admin
```

<br/>

본인의 namespace 에 적용한다.

<br/>

```bash
root@newedu:~/jenkins# kubectl apply -f jenkins_rbac.yaml
serviceaccount/jenkins-admin created
role.rbac.authorization.k8s.io/jenkins-admin created
rolebinding.rbac.authorization.k8s.io/jenkins-admin created
```  

<br/>

생성된 resource 들을 확인한다.  

<br/>

```bash
root@newedu:~/jenkins# kubectl get sa
NAME                 SECRETS   AGE
builder              2         247d
default              2         247d
deployer             2         247d
edu                  2         7d20h
jenkins-admin        2         10s
my-service-account   2         169d
root@newedu:~/jenkins# kubectl get role
NAME            CREATED AT
developer       2022-09-27T14:16:45Z
jenkins-admin   2023-03-16T02:03:11Z
pod-role        2022-09-27T14:04:40Z
root@newedu:~/jenkins# kubectl get rolebindings
NAME                              ROLE                                          AGE
admin                             ClusterRole/admin                             247d
developer-binding-myuser          Role/developer                                169d
edu30-admin                       ClusterRole/admin                             247d
jenkins-admin                     Role/jenkins-admin                            23s
pod-rolebinding                   Role/pod-role                                 169d
pod-rolebinding2                  Role/pod-role                                 7d19h
system:deployers                  ClusterRole/system:deployer                   247d
system:image-builders             ClusterRole/system:image-builder              247d
system:image-pullers              ClusterRole/system:image-puller               247d
system:openshift:scc:privileged   ClusterRole/system:openshift:scc:privileged   182d
```  


<br/>

#### Storage 설정

<br/>


Jenkins 가 사용하는 stroage를 위해 pv / pvc 를 생성해야 하며
사전에 NFS 에 접속하여 폴더를 생성한다.   

폴더는 아래와 같이 사전 생성 되어 있음.

<br/>

```bash
[root@edu jenkins]# mkdir -p edu
[root@edu jenkins]# mkdir -p edu1
[root@edu jenkins]# ls
edu  edu1  edu10  edu11  edu12  edu13  edu14  edu15  edu16  edu17  edu18  edu19  edu2  edu20  edu21  edu3  edu4  edu5  edu6  edu7  edu8  edu9
```

<br/>

Jenkins slave 용 폴더도 생성한다.

```bash
[root@edu jenkins]# mkdir -p edu
[root@edu jenkins]# mkdir -p edu1_slave
...
```

<br/>

jenkins master / slave 용 해당 폴더의 권한을 설정한다.

pod 내에서 nfs 연결해서 권한을 줄때는   

`chown -R nfsnobody:nfsnobody edu`  

대신 아래처럼 nobody:nogroup 으로 준다.

`chown -R nobody:nogroup edu`

<br/>

```bash
[root@edu jenkins]# chown -R nfsnobody:nfsnobody edu
[root@edu jenkins]# chmod 777 edu
[root@edu jenkins]# chown -R nfsnobody:nfsnobody edu_slave
[root@edu jenkins]# chmod 777 edu_slave
```  

<br/>

Master용 PV 를 생성한다. 사이즈는 5G로 설정한다.

<br/>

이름은 아래와 같이 생성한다.  

jenkins_pv.yaml  

예)  edu1 : jenkins-edu1-pv  
     edu2 : jenkins-edu2-pv

<br/>

PV를 생성하고 Status를 확인해보면 Available 로 되어 있는 것을 알 수 있습니다.  

<br/>

```bash
root@newedu:~/jenkins# kubectl apply -f  jenkins_pv.yaml
persistentvolume/jenkins-edu-pv created
root@newedu:~/jenkins# kubectl get pv jenkins-edu-pv
NAME             CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS      CLAIM   STORAGECLASS   REASON   AGE
jenkins-edu-pv   5Gi        RWX            Retain           Available                                   10s
```

<br/>

Master용 pvc 를 생성합니다. pvc 이름을 기억합니다.

<br/>

예)  edu1 : jenkins-edu1-pvc  
     edu2 : jenkins-edu2-pvc

<br/>


Slave 용 PV 를 생성한다. 사이즈는 5G로 설정한다.

<br/>

```bash  
root@newedu:~/jenkins#  vi jenkins_slave_pv.yaml
```  

jenkins_slave_pv.yaml  

예)  edu1 : jenkins-edu1-slave-pv  
     edu2 : jenkins-edu2-slave-pv


<br/>

PV를 생성하고 Status를 확인해보면 Available 로 되어 있는 것을 알 수 있습니다.  

<br/>

```bash
root@newedu:~/jenkins# kubectl apply -f  jenkins_slave_pv.yaml
persistentvolume/jenkins-edu-slave-pv created
root@newedu:~/jenkins# kubectl get pv jenkins-edu-slave-pv
NAME                   CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM                         STORAGECLASS   REASON   AGE
jenkins-edu-slave-pv   5Gi        RWX            Retain           Bound    edu30/jenkins-edu-slave-pvc                           100s
```

<br/>

Slave 용 pvc 를 생성합니다. pvc 이름을 기억합니다.

<br/>

```bash
root@newedu:~/jenkins# vi jenkins_slave_pvc.yaml
```


예)  edu1 : jenkins-edu1-slave-pvc  
     edu2 : jenkins-edu2-slave-pvc


<br/>

PVC를 생성할 때는 namespace ( 본인의 namespace ) 를 명시해야 합니다.  
PVC 생성을 확인 해보고 다시 PV를 확인해 보면 Status가 Bound 로 되어 있는 것을 알 수 있습니다.  이제 PV 와 PVC가 연결이 되었습니다.

<br/>

```bash
root@newedu:~/jenkins# kubectl apply -f jenkins_slave_pvc.yaml
persistentvolumeclaim/jenkins-edu-slave-pvc created
root@newedu:~/jenkins# kubectl get pvc
NAME                    STATUS   VOLUME                 CAPACITY   ACCESS MODES   STORAGECLASS   AGE
app-volume              Bound    app-config             1Gi        RWX            az-c           169d
jenkins-edu-pvc         Bound    jenkins-edu-pv         5Gi        RWX                           136m
jenkins-edu-slave-pvc   Bound    jenkins-edu-slave-pv   5Gi        RWX                           3m27s
root@newedu:~/jenkins# kubectl get pv jenkins-edu-slave-pv
NAME                   CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM                         STORAGECLASS   REASON   AGE
jenkins-edu-slave-pv   5Gi        RWX            Retain           Bound    edu30/jenkins-edu-slave-pvc                           4m
```

<br/>

#### Helm  Jenkins  설정

<br/>

Jenkins 는 Helm Chart 를 이용하여 설치를 합니다.  

<br/>

현재 로컬의 helm repository 를 확인한다.   

<br/>

```bash
root@newedu:~/jenkins# helm repo list
NAME                           	URL
bitnami                        	https://charts.bitnami.com/bitnami
nfs-subdir-external-provisioner	https://kubernetes-sigs.github.io/nfs-subdir-external-provisioner/
```  

<br/>

jenkins helm repository를  아래와 같이 추가 한다.

<br/>

```bash
root@newedu:~/jenkins# helm repo add jenkins https://charts.jenkins.io --insecure-skip-tls-verify
"jenkins" has been added to your repositories
```

<br/>

helm repository를 update 한다.  

<br/>

```bash
root@newedu:~/jenkins# helm repo update
Hang tight while we grab the latest from your chart repositories...
...Successfully got an update from the "nfs-subdir-external-provisioner" chart repository
...Successfully got an update from the "jenkins" chart repository
...Successfully got an update from the "bitnami" chart repository
Update Complete. ⎈Happy Helming!⎈
```  

<br/>

jenkins helm reppository 에서 helm chart를 검색을 하고 jenkins chart를 선택합니다.  

<br/>

```bash
root@newedu:~/jenkins# helm search repo jenkins
NAME           	CHART VERSION	APP VERSION	DESCRIPTION
bitnami/jenkins	12.0.1       	2.387.1    	Jenkins is an open source Continuous Integratio...
jenkins/jenkins	4.3.8        	2.387.1    	Jenkins - Build great things at any scale! The ...
```

<br/>

jenkins/jenkins 차트에서 차트의 변수 값을 변경하기 위해 jenkins_values.yaml 화일을 추출한다.

<br/>


TODO

<br/>

vi 데이터에서 생성된 jenkins_values.yaml을 연다.  

<br/>

```bash
root@newedu:~# vi jenkins_values.yaml
```  

라인을 보기 위해 ESC 를 누른 후 `:set nu` 를 입력하면 왼쪽에 라인이 보인다.  

아래 라인을 찾아 값을 변경한다.  51번 라인에 앞에서 생성한 secret을 넣는다.   

pvc는 본인의 pvc 를 설정한다.

<br/>

```bash
 50   admin:
 51     existingSecret: "jenkins-admin-secret" #
 52     userKey: jenkins-admin-user
 53     passwordKey: jenkins-admin-password
```  

<br/>

installPlugins에서 kubernetes 값 아래와 같이 반드시 변경 필요

<br/>

```bash
244   installPlugins:
245     - kubernetes:3842.v7ff395ed0cf3 #3734.v562b_b_a_627ea_c
246     - workflow-aggregator:590.v6a_d052e5a_a_b_5
247     - git:4.13.0
248     - configuration-as-code:1569.vb_72405b_80249
...
508   # Openshift route
509   route:
510     enabled: true  # true 로 변경
511     labels: {}
512     annotations: {}
...
617 agent:
618   enabled: true
619   defaultsProviderTemplate: ""
620   # URL for connecting to the Jenkins contoller
621   jenkinsUrl:
622   # connect to the specified host and port, instead of connecting directly to the Jenkins controller
623   jenkinsTunnel:
624   kubernetesConnectTimeout: 5
625   kubernetesReadTimeout: 15
626   maxRequestsPerHostStr: "32"
627   namespace:
628   image: "jenkins/jnlp-slave" #"jenkins/inbound-agent"
629   tag: "latest-jdk11"
...
662   volumes: # []
663   # - type: ConfigMap
664   #   configMapName: myconfigmap
665   #   mountPath: /var/myapp/myconfigmap
666   # - type: EmptyDir
667   #   mountPath: /var/myapp/myemptydir
668   #   memory: false
669   # - type: HostPath
670   #   hostPath: /var/lib/containers
671   #   mountPath: /var/myapp/myhostpath
672   # - type: Nfs
673   #   mountPath: /var/myapp/mynfs
674   #   readOnly: false
675   #   serverAddress: "192.0.2.0"
676   #   serverPath: /var/lib/containers
677    - type: PVC
678      claimName: jenkins-edu-slave-pvc
679      mountPath: /var/jenkins_home
680      readOnly: false
...
691   workspaceVolume: # {}
692   ## DynamicPVC example
693   # type: DynamicPVC
694   # configMapName: myconfigmap
695   ## EmptyDir example
696   # type: EmptyDir
697   # memory: false
698   ## HostPath example
699   # type: HostPath
700   # hostPath: /var/lib/containers
701   ## NFS example
702   # type: Nfs
703   # readOnly: false
704   # serverAddress: "192.0.2.0"
705   # serverPath: /var/lib/containers
706   ## PVC example
707    type: PVC
708    claimName: jenkins-edu-slave-pvc
709    readOnly: false
710   #
710   #
...

822 persistence:
823   enabled: true
824   ## A manually managed Persistent Volume and Claim
825   ## Requires persistence.enabled: true
826   ## If defined, PVC must be created manually before volume will be bound
827   existingClaim: "jenkins-edu-pvc" #
828   ## jenkins data Persistent Volume Storage Class
829   ## If defined, storageClassName: <storageClass>
830   ## If set to "-", storageClassName: "", which disables dynamic provisioning
831   ## If undefined (the default) or set to null, no storageClassName spec is
832   ##   set, choosing the default provisioner.  (gp2 on AWS, standard on
833   ##   GKE, AWS & OpenStack)
834   ##
835   storageClass:
836   annotations: {}
837   labels: {}
838   accessMode: "ReadWriteOnce"
839   size: "5Gi"  # "8Gi"  
...

870 serviceAccount:
871   create: false #  이미 생성 했기 때문에 false 로 변경
872   # The name of the service account is autogenerated by default
873   name: "jenkins-admin" #
874   annotations: {}
875   extraLabels: {}
876   imagePullSecretName:

```  

#### Helm 으로 Jenkins 설치

<br/>

jenkins_values.yaml 를 사용하여 설치 한다.

<br/>


TODO

<br/>

설치가 완료되면 pod를 조회하여 jenkins-0 pod가 있는지 확인한다.

<br/>

```bash
root@newedu:~/jenkins# kubectl get po
NAME                                        READY   STATUS    RESTARTS   AGE
jenkins-0                                   2/2     Running   0          18h
nfs-test-589c488d6f-8lk5p                   1/1     Running   0          40h
``` 

<br/>

#### Jenkins 설정

<br/>

웹 브라우저 에서 본인의 jenkins 로 접속한다.   
route 정보를 모르면 아래 명령어로 조회한다.

<br/>

```bash
root@newedu:~/jenkins# kubectl get route
NAME      HOST/PORT                                 PATH   SERVICES   PORT   TERMINATION     WILDCARD
jenkins   jenkins-edu30.apps.211-34-231-82.nip.io          jenkins    http   edge/Redirect   None
```  

<br/>

ingress 를 생성한다.

TODO

<br/>

아래 처럼 로그인 화면이 나오면 admin 계정으로 로그인한다.

<br/>

<img src="./assets/jenkins_login.png" style="width: 80%; height: auto;"/> 

<br/>

Manage Jenkins 메뉴를 클릭한다.

<br/>

<img src="./assets/manage_jenkins1.png" style="width: 80%; height: auto;"/> 

Manage Plugins 메뉴를 클릭한다.

<br/>

<img src="./assets/manage_plugins.png" style="width: 80%; height: auto;"/> 

<br/>

Plugin은 3 가지를 설치한다.  
- git parameter
- pipeline stage view
- docker pipeline    

TODO : 플러그인 설치

<br/>
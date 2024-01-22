# 실습 프로그램 설치 

교육에 앞서 실습에 필요한 프로그램을 설치합니다.   

<br/>

1. 실습용 VM 생성  

2. VM 서버 접속 

3. Docker 설치

4. Docker compose 설치

5. openshift console 설치

6. Git 설치

7. Helm 설치

8. GitHub 계정 생성

9. DockerHub 계정 생성

10. OKD 에 Insecure Registry 설정  

11. Jenkins 설치  


<br/>

##  실습용 VM 생성  ( KT EPC Cloud  G1/G2 Zone )

<br/>

### kt cloud 에서 VM ( Ubuntu 18.04 ) 을 생성한다. 
  
<br/>


  - 기준 : 8 core , 16G  
  - 설치 예정 솔루션
  ```
   jenkins , k3s , ArgoCD , elastic , Airflow 등
  ```

서버 -> 서버 메뉴 이동 후  Create Server 선택  
zone은 KOR-Seoul M2 선택 후 서버 이름을 입력하고 사양을 선택한다.  

<br/>

오른쪽 하단에 Launch를 클릭하여 vm을 생성한다. 

<img src="./assets/kt_cloud_vm_create.png" style="width: 80%; height: auto;"/>  

<br/>

5분정도 경과하면 Alarm 아이콘에  vm생성 및 root 비밀번호를 확인 할 수 있다.  
비밀번호는 반드시 저장한다. 

<!--![](./assets/vm_created.png)-->

<img src="./assets/vm_created.png" style="width: 60%; height: auto;"/>  

<br/><br/>

### Private IP를 생성 한다.
   
zone은  VM 생성 했던 존을 선택한다. ( KOR-Seoul M2 )

<img src="./assets/private_ip_create.png" style="width: 80%; height: auto;"/>

Launch 를 클릭하면 IP가 생성이 된다.  

![](./assets/private_ip_info.png)  

생성된 IP의 오른쪽 끝을 클릭하여 식별할 수 있는 이름을 만들어준다.

<img src="./assets/private_ip_more.png" style="width: 40%; height: auto;"/>

오른쪽에 이름이 변경 된것을 확인 할 수 있다.  

![](./assets/private_ip_modify.png)  

<br/><br/>

### Port Forwarding을 설정한다.

<br/>

VM 과 Public IP를 매핑하면 외부에서 접속 가능 하다.  

서버를 선택하고  Connection String을 선택한다.
<!--![](./assets/port_forwarding1.png)-->
 <img src="./assets/port_forwarding1.png" style="width: 80%; height: auto;"/>

- 사설 포트 : vm 서버의 포트  
- 공인 포트 : 외부에 노출할 포트  
- 공인 IP : 외부에서 접속할 IP ( 위에서 생성한 Public IP ) 

 <img src="./assets/port_forwarding2.png" style="width: 80%; height: auto;"/>

<br/>

추가 버튼을 클릭하여 생성하고 아래 포트들도 반복하여 설정한다.  
권한 문제로 수강생은 불가.    

<br>

  
| 서비스 | 내부포트 | 외부포트 |
|:--------| :-----|:----|  
|ssh | 22 | 22222 |
|jenkins | 9000 | 9000 |
|테스트용 web 포트 | 40003 ~ 40010 | 좌동 |
|k8s master 포트  | 6443 | 6443 |
|k8s NodePort range  | 30000 ~ 32767 |  좌동 |

<br/><br/>

### 터미널 프로그램으로 서버에 접속하고 비밀번호를 변경한다.

   - Mac 에서는 Iterm2, 윈도우는 Putty 추천  

<br/>

터미널에서 아래 명령어를 입력하여 로그인 한다.  

로그인 후  connecting 저장 질문이 나오면 yes 를 입력한다. 

```bash
ssh root@(본인 Public ip) -p 22222
``` 

 <img src="./assets/first_login1.png" style="width: 80%; height: auto;"/>


비밀번호 입력 후 아래 명령어 실행하여 root 비밀번호를 변경한다.
```bash
passwd
```
```bash
Enter new UNIX password:
```

 <img src="./assets/first_login2.png" style="width: 80%; height: auto;"/>

   - 기존 비밀번호 물어 보는 경우도 있음  

<br/><br/>

##  VM 서버 접속

<br/>

사전에 공유 한 VM 서버 ( OS : Ubuntu ,  CPU : 8core , MEM : 16G )에 접속합니다.  

터미널에서 아래 명령어를 입력하여 로그인 한다.  

로그인 후  connecting 저장 질문이 나오면 yes 를 입력한다. 

```bash
ssh root@(본인 Public ip) 
``` 

<br/>

## Docker 설치

<br/>

> 도커 설치 : https://youtu.be/w8EVLx1_xY0

<br/> 

### 패키지 인덱스 업데이트

<br/>

```bash
apt-get update
```
<br/>

### HTTPS를 통해 repository 를 이용하기 위해 package 들을 설치

<br/>

```bash
apt-get -y install  apt-transport-https ca-certificates curl gnupg lsb-release
```
<br/>

### Docker의 Official GPG Key 를 등록합니다.

<br/>

```bash
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
```

<br/>

### stable repository를 등록합니다.  

<br/>

```bash
echo \
"deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
$(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```  
<br/>

### Docker Install

<br/>

```bash
apt-get update && apt-get install docker-ce docker-ce-cli containerd.io gnupg2 pass
```

* Ubuntu 에서 도커 로그인 버그가 있어 아래 처럼 에러가 발생하기 때문에 gnupg2 pass 라이브러리를 추가 했음.  

```bash
$ docker login -u shclub -p ******** https://index.docker.io/v1/
WARNING! Using --password via the CLI is insecure. Use --password-stdin.
Error saving credentials: error storing credentials - err: exit status 1, out: `Cannot autolaunch D-Bus without X11 $DISPLAY`
```

<br/>

### 도커 버전을 확인합니다.

<br/>

```bash
docker --version
```
<img src="./assets/docker_version.png" style="width: 60%; height: auto;"/>

<br/>

### 도커 이미지 다운로드 및 실행하기

<br/>

```bash
docker run hello-world
```
 
<img src="./assets/docker_run_world.png" style="width: 80%; height: auto;"/>


<br/>

## Docker compose 설치.

<br/>

VM 에서 docker compose를 설치 합니다.  

<br/>

```bash
apt-get update && apt-get install docker-compose
```

<br/>

중간에 추가 설치 내용이 나오면 Y를 입력하고 엔터를 친다.

<img src="./assets/docker_compose_install.png" style="width: 60%; height: auto;">

<br/>

도커 컴포즈 버전을 확인하고 아래와 같이 나오면 정상적으로 설치가 된 것이다.

<br/>

```bash
docker-compose --version
```  

<br/>

<img src="./assets/docker_compose_version.png" style="width: 60%; height: auto;">  

<br/>

## openshift console 설치

<br/>

VM 에서 아래와 같이 openshift client를 설치 합니다.  

```bash
root@edu2:~# wget https://github.com/openshift/okd/releases/download/4.7.0-0.okd-2021-09-19-013247/openshift-client-linux-4.7.0-0.okd-2021-09-19-013247.tar.gz
```   

<br/>

tar 화일을 압축을 풉니다.  

```bash
root@edu2:~# ls
cloud-init-setting.sh  openshift-client-linux-4.7.0-0.okd-2021-09-19-013247.tar.gz
root@edu2:~# tar xvfz openshift-client-linux-4.7.0-0.okd-2021-09-19-013247.tar.gz
```  

<br/>

oc 와 kubectl 화일이 생성 된 것을 확인 할수 있습니다.  
oc 는 openshift console 이고 kubectl 은 kubernetes client tool 입니다.  


```bash
root@edu2:~# ls
README.md  cloud-init-setting.sh  kubectl  oc  openshift-client-linux-4.7.0-0.okd-2021-09-19-013247.tar.gz
```  

<br/>

path를 추가 합니다.  

```bash
echo 'export PATH=$PATH:.' >> ~/.bashrc && source ~/.bashrc
```  

<br/>

oc 명령어를 입력해 봅니다.  

```bash
root@edu2:~# oc
OpenShift Client

This client helps you develop, build, deploy, and run your applications on any
OpenShift or Kubernetes cluster. It also includes the administrative
commands for managing a cluster under the 'adm' subcommand.

To familiarize yourself with OpenShift, login to your cluster and try creating a sample application:

    oc login mycluster.mycompany.com
    oc new-project my-example
    oc new-app django-psql-example
    oc logs -f bc/django-psql-example

To see what has been created, run:

    oc status

and get a command shell inside one of the created containers with:

    oc rsh dc/postgresql

To see the list of available toolchains for building applications, run:

    oc new-app -L

Since OpenShift runs on top of Kubernetes, your favorite kubectl commands are also present in oc,
allowing you to quickly switch between development and debugging. You can also run kubectl directly
against any OpenShift cluster using the kubeconfig file created by 'oc login'.

For more on OpenShift, see the documentation at https://docs.openshift.com.

To see the full list of commands supported, run 'oc --help'.
```  

<br/>

VM 에서 접속 테스트를 합니다.  
아이디는 `namespace 이름 - admin` 으로 구성이 되고 namespace 생성시에 자동 생성이 됩니다.  


<br/>

`oc login <API SERVER:포트> -u <아이디> -p <패스워드> --insecure-skip-tls-verify`

<br/>

```bash
root@edu2:~# oc login https://api.211-34-231-81.nip.io:6443 -u edu1-admin -p New1234! --insecure-skip-tls-verify
Login successful.

You have one project on this server: "edu1"

Using project "edu1".
Welcome! See 'oc help' to get started.
```  

<br/>


## Git 설치

<br/>

Git이란 소스코드를 효과적으로 관리하기 위해 개발된 '분산형 버전 관리 시스템'입니다. ( 이전 에는 SVN 많이 사용 )  

가장 대중적인 SaaS 형태는 Microsoft에서 제공하는 GitHub 이고
Private 형태로는 Gitlab을 많이 사용 함.  

<br/>

참고 사이트 :  https://backlog.com/git-tutorial/kr/intro/intro1_1.html    

<br/>


본인의 PC에 git을 설치하고 버전을 확인한다.  

```bash
git --version
``` 

<img src="./assets/git_version.png" style="width: 60%; height: auto;"/>

<br/>


## Helm 설치.

<br/>


helm 3.x 이상 버전을 설치한다.

<br/>


```bash
curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3
```  

<br/>


```bash
chmod 700 get_helm.sh
```

```bash
./get_helm.sh
```  

<img src="./assets/helm_install.png" style="width: 80%; height: auto;"/>  

버전을 확인한다.

```bash
helm version
```

<img src="./assets/helm_version.png" style="width: 80%; height: auto;"/>  

helm repository 목록을 조회합니다. 처음 설치 했을때는 아무것도 없습니다.

```bash
helm repo list
```

<br/>

## GitHub 계정 생성

<br/>

https://github.com/ 접속하고 계정 생성  

<br/>

계정 생성 후에 Repository를  생성한다.

<br/>

아래와 같이 이름 입력를 하고 README file check 를 한다

<br/>

<img src="./assets/repository_create.png" style="width: 80%; height: auto;"/>  

default 브랜치를 main에서 master로 변경한다. ( 맨 하단 setting 클릭하여 설정)

<br/>

<img src="./assets/default_branch_modify.png" style="width: 60%; height: auto;"/>

<br/>

교육용 repository인 https://github.com/shclub/edu1 폴더의 파일을 복사하여 본인이 생성한 Repository에 신규 화일을 생성한다. 

<br/>

총 4개 화일을 만들고 내용을 복사한다.  ( 향후 Git 사용법 교육 후 Git Clone 사용 )

<img src="./assets/shclub_edu_file.png" style="width: 60%; height: auto;"/>

   - 샘플은 pyhon flask 로 구성

<br/><br/>

##  Docker Hub 계정 생성 

<br/>

https://hub.docker.com/ 접속하고 계정 생성  
- 향후 사내에서 개발시는 private docker registry Nexus 사용  

<br/>

### Docker 연동 테스트를 한다.

```bash
docker tag hello-world (본인id)/hello-world
docker push (본인id)/hello-world  
```

- 권한 에러 발생시 docker login 한다
```bash
docker login 
```

<img src="./assets/docker_denied.png" style="width: 60%; height: auto;"/>

정상적으로 로그인후 push를 한다.

```bash
docker push (본인id)/hello-world  
```     

<br/>

<img src="./assets/docker_push.png" style="width: 60%; height: auto;"/>

도커허브 본인 계정에서 도커 이미지 생성 확인  

<img src="./assets/docker_hub_world.png" style="width: 60%; height: auto;"/>


도커 이미지가 Private으로 되어 있으면 Public 으로 변경한다. 
- 개인 계정은 1개의 private 만 가능

setting 으로 이동하여 Make public 클릭후 repository 이름을 입력후 Make Public 클릭  

<img src="./assets/docker_hub_make_public.png" style="width: 60%; height: auto;"/>


<br/>


## OKD 에 Insecure Registry 설정

<br/>


project.config.openshift.io/cluster 사용자 정의 리소스를 편집합니다.  
- MCP ( Machine Configuration Pool )를 적용 하는것이기 때문에 주의를 요합니다.  

<br/>

```bash
root@newedu:~# oc edit image.config.openshift.io/cluster
```  

<br/>


아래와 같이 insecureRegistries에 private docker registry를 적용합니다.

<br/>

```bash
...
spec:
  registrySources:
    insecureRegistries:
    - 211.252.85.148:40002
...
```  

<br/>

master node 부터 MCP가 적용이 되어 모든 node가 적용 되기 까지 시간이 많이 걸림. (Node 당 3분 정도)  

<br/>

아래 명령어로 조회를 해보면  UPDATED가 True가 되어야 완료가 된 것입니다.

<br/>

```bash
root@newedu:~# oc get mcp
NAME     CONFIG                                             UPDATED   UPDATING   DEGRADED   MACHINECOUNT   READYMACHINECOUNT   UPDATEDMACHINECOUNT   DEGRADEDMACHINECOUNT   AGE
master   rendered-master-abf3629f4f7d30a2463347c70fd3c097   True      False      False      3              3                   3                     0                      268d
worker   rendered-worker-e019e9db2e9c193192fdd56d1fc5b10a   True      False      False      13             13                  13                    0                      268d
```  

<br/>

OKD 의 각 worker node에 접속하여 /etc/containers/registries.conf 에  private docker registry를 설정한다.  

<br/>

```bash
[root@edu ~]# vi /etc/containers/registries.conf
```  

<br/>

아래와 같이 location에 설정을 하고 insecure 는 true로 설정한다.  

<br/>


```bash
[[registry]]
  prefix = ""
  location = "211.252.85.148:40002"
  insecure = true
```

<br/>

crio를 재기동 한다.  ( OKD 4.7 은 Docker runtime 대신 CRIO 사용 )

```bash
[root@edu ~]# systemctl restart crio
```  

<br/>

상태를 확인한다.  

<br/>

```bash
[root@edu ~]# systemctl status crio
● crio.service - Container Runtime Interface for OCI (CRI-O)
     Loaded: loaded (/usr/lib/systemd/system/crio.service; disabled; vendor preset: disabled)
    Drop-In: /etc/systemd/system/crio.service.d
             └─10-mco-default-madv.conf, 10-mco-profile-unix-socket.conf, 20-nodenet.conf
     Active: active (running) since Tue 2023-03-07 09:24:18 UTC; 7s ago
       Docs: https://github.com/cri-o/cri-o
   Main PID: 2067535 (crio)
      Tasks: 17
     Memory: 55.5M
        CPU: 3.294s
     CGroup: /system.slice/crio.service
             └─2067535 /usr/bin/crio

Mar 07 09:24:18 edu.worker05 crio[2067535]: time="2023-03-07 09:24:18.978900380Z" level=info msg="Got pod network &{Name:dns-def>
Mar 07 09:24:18 edu.worker05 crio[2067535]: time="2023-03-07 09:24:18.979083576Z" level=info msg="About to check CNI network mul>
Mar 07 09:24:18 edu.worker05 crio[2067535]: time="2023-03-07 09:24:18.979400484Z" level=info msg="Got pod network &{Name:network>
Mar 07 09:24:18 edu.worker05 crio[2067535]: time="2023-03-07 09:24:18.979582508Z" level=info msg="About to check CNI network mul>
Mar 07 09:24:18 edu.worker05 crio[2067535]: time="2023-03-07 09:24:18.979896648Z" level=info msg="Got pod network &{Name:network>
Mar 07 09:24:18 edu.worker05 crio[2067535]: time="2023-03-07 09:24:18.980059990Z" level=info msg="About to check CNI network mul>
Mar 07 09:24:18 edu.worker05 crio[2067535]: time="2023-03-07 09:24:18.980374941Z" level=info msg="Got pod network &{Name:ingress>
Mar 07 09:24:18 edu.worker05 crio[2067535]: time="2023-03-07 09:24:18.980535545Z" level=info msg="About to check CNI network mul>
Mar 07 09:24:18 edu.worker05 crio[2067535]: time="2023-03-07 09:24:18.981590475Z" level=info msg="Serving metrics on :9537"
Mar 07 09:24:18 edu.worker05 systemd[1]: Started Container Runtime Interface for OCI (CRI-O).
```  

<br/>

## Jenkins를 설치한다.

<br/>

### 저장소 키 다운로드

<br/>

   ```bash
   wget -q -O - https://pkg.jenkins.io/debian/jenkins.io.key | sudo apt-key add -
   ```
   제대로 입력 되었는지 확인 한다.  

   ```bash
   echo deb http://pkg.jenkins.io/debian-stable binary/ | sudo tee /etc/apt/sources.list.d/jenkins.list
   ```

<br/>>

### 패키지 인덱스를 업데이트 하고 라이브러를 최신 버전으로 올려준다.

<br/>

   ```bash
   apt update && apt upgrade
   ```
   중간에 계속 진행하는 것을 물어보면 Y 를 입력하여 진행 

 <img src="./assets/apt_update.png" style="width: 80%; height: auto;"/>

<br/><br/>

### root 계정으로 Jenkins 를 설치한다.  

<br/>>

현재 VM은  java가 설치되지 않아 openjdk-8-jdk를 설치 해야 한다.  

jdk를 먼저 설치한다.   

   ```bash
   apt install  openjdk-8-jdk
   ```  

jenkins를 설치한다.  

   ```bash
   apt install jenkins 
   ```

<br/>

일반 계정이면 앞에 sudo 명령어를 반드시 붙여준다  

jdk를  설치한다.  

   ```bash
   sudo apt install openjdk-8-jdk  
   sudo apt update
   ```    

<br/>

jenkins를 설치한다.   

### Jenkins 서비스 포트를 변경한다.  

<br/>

   아래 화일을 vi 에디터를 사용하여 포트를 변경 ( 8080 ->  9000 )

   ```bash
   vi /etc/default/jenkins 
   ``` 
   ```bash
   HTTP_PORT=9000
   ```
   
   < vi에디터 사용법 >
   ```
   i : 데이터 입력모드
   ```
   ```
   esc : 명령모드
   ```
   ```
     / : 찾기
     x : 한글자 삭제
     dd : 한 라인 삭제
     :wq : 저장하고 나오기
     :q! : 저장안하고 나오기
     :set nocp : 라인 밀리는 현상 방지
   ``` 

<br/>
    
### 서비스 재시작 및 상태 확인

<br/>

```bash
service jenkins restart
```
정상여부 확인
```bash
systemctl status jenkins
```
아래와 같이 active(running) 이면 정상이고 http포트가 9000으로 되어 있는지 확인한다.    

<img src="./assets/jenkins_status.png" style="width: 80%; height: auto;"/>  

- ctrl + c 를 눌러 해당 화면에서 나온다.

<br/>

위 방법으로 포트 변경이 안되면 /lib/systemd/system/jenkins.service 를 vi로 오픈 후에 
아래와 같이 변경하면 됩니다.

```bash
# Port to listen on for HTTP requests. Set to -1 to disable.
# To be able to listen on privileged ports (port numbers less than 1024),
# add the CAP_NET_BIND_SERVICE capability to the AmbientCapabilities
# directive below.
Environment="JENKINS_PORT=9000"
```  

<br/>

데몬를 reload 한다.  


```bash
systemctl daemon-reload
```  

<br/>

서비스를 restart  

```bash
service jenkins restart
```  


<br/>  

### Jenkins Admin 초기 패스워드 확인 및 복사

<br/>

 아래 명령어를 사용하여 password 를 복사하고 저장해 놓는다.

```bash
cat /var/lib/jenkins/secrets/initialAdminPassword
```
<br/><br/> 

### <a name='-1'></a>젠킨스 서버 접속
브라우져로 http://(본인서버ip):9000으로  접속하면 아래와 같은 화면이 나온다.  
패스워드에 위 명령으로 확인한 문자열을 입력한다.  

<img src="./assets/jenkins_admin_password.png" style="width: 60%; height: auto;"/>  


Install Suggested Plugin 선택하고 Plugin 설치 한다.  

<img src="./assets/jenkins_suggested_plugin.png" style="width: 60%; height: auto;"/>  

다운로드를 시작한다. 네트웍 상황에 따라 시간이 많이 소요 될 수 있다.  

<img src="./assets/jenkins_suggested_plugin2.png" style="width: 60%; height: auto;"/>  

아래 와 같이 화면이 나오면 성공.

<img src="./assets/jenkins_admin_user.png" style="width: 60%; height: auto;"/> 

Admin 유저를 생성한다. 이메일은 아무값이나 넣어준다.  
save and continue 버튼을 클릭한다

<img src="./assets/jenkins_admin_user_create.png" style="width: 60%; height: auto;"/> 

save and Finished 버튼을 클릭한다.

<img src="./assets/jenkins_admin_user_created.png" style="width: 60%; height: auto;"/> 

설정 완료가 되면 아래 화면이 나오고 Jenkins를 시작 할 수 있다.  

<img src="./assets/jenkins_is_ready.png" style="width: 60%; height: auto;"/> 

- 해당 문서는 영문을 기준으로 하며 Jenkins는 브라우저의 언어를 따른다. 

<br/><br/>

### <a name='-1'></a>추가 플러그인 설치

Manage Jenkins 메뉴 선택  

<img src="./assets/manage_jenkins1.png" style="width: 80%; height: auto;"/> 

Manage Plugin 선택

<img src="./assets/jenkins_first_manage_plugins.png" style="width: 80%; height: auto;"/> 

Available Tab 이동하여 git으로 검색 한다.   
Git Parameter , GitHub Integration 선택  
docker 로 검색 후 	Docker Pipeline , docker-build-step 선택  
Download now and install after restart 클릭

<img src="./assets/plugin_git.png" style="width: 80%; height: auto;"/>  

아래와 같이 설치가 진행이 되고 Restart Check 를 하여 Jenkins를 재기동 한다

<img src="./assets/jenkins_restart_check.png" style="width: 60%; height: auto;"/>    

Jenkins restarting이 되고 다시 로그인을 한다.  

<img src="./assets/jenkins_restarting.png" style="width: 60%; height: auto;"/>

<br/><br/>
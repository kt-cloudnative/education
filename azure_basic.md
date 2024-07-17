# Chapter 19 

 Azure 기본 과정으로 CI/CD 및 AKS 실습을 진행을 합니다.  

 아래 환경은 Public Azure 환경이고 초기 가입시 200 달러의 Credit을 제공하기 때문에 충분히 테스트가 가능 합니다.   

 테스트를 진행하기 위해서는 외부 접속이 가능한 개인 PC 와  GitHub 계정 사전 생성이 필요합니다.

<br/>

1. CI 구성하기 (  Github Action )

2. GitOps 구성 (  Azure Repo )

3. AKS 구성 ( Azure Kubernetes Service Cluster )

4. CD 구성하기 (  ArgoCD )

5. Eventhub 구성  및 테스트 하기 

6. Azure cache for Redis 구성

7. Azure DB for MySQL 구성 및 접속하기 

8. Full Stack Application 배포 해 보기 


<br/>

## 전체 구성

<br/>

<img src="./assets/cicd-gitops-architecture.png" style="width: 70%; height: auto;"/>  

<br>

구성요소

- Source Repo : GitHub
- Deployment Manifests : kustomize
- GitOps Repo : Azure Repos / GitHub
- CI : GitHub Action
- CD : ArgoCD
- Container Registry : Azure Container Registry ( ACR ) 
- 배포 Target : Azure Kubernetes Service ( AKS )

<br/>

> 사내 Private Cloud vs Azure Public Cloud  

<br/>

| 구분 | Source Repo | GitOps Repo | CI | CD | Container Registry | 컨테이너플랫폼 | MessageBroker | Cache
|:--------| :-----|:----|  :----|  :----| :----| :----| :----| :----|  
| 사내 Private Cloud | GitLab | GitLab | Jenkins | Jenkins/ArgoCD | Nexus | FlyingCube 2.0(OKD 11) | Kafka | Redis
| Azure Public Cloud | Github | Github/Azure Devops | Github Action | ArgoCD | Azure Container Registry | AKS (K8S 1.28.9) | EventHub | Azure Cache for Redis

<br/>

## 1. CI 구성하기 (  Github , Azure Container Registry ) 

<br>

CI 구성은 Jenkins 대신하여 빠르게 진행하기 위하여 GitHub Action 을 사용 합니다.  

Github Action 을 사용하기 전에 도커 이미지를 저장하기 위한 ACR ( Azure Container Registry ) 에 private registry 를 생성합니다. 

<br/>

###  ACR ( Azure Container Registry ) 생성하기

<br/>

https://portal.azure.com/ 에 접속하여 container registry 를 검색을 하여 선택한 후
Create 버튼을 클릭합니다.  


<img src="./assets/acr_1.png" style="width: 60%; height: auto;"/>  

- resource group : 없으면 아래  new 버튼 클릭하여 생성 ( 리소스 그룹은 모든 리소스의 최상단에 위치 할 수 있습니다.)  
- registry name : < 원하는 이름으로 기입 > ( 도커 이미지 앞에 <registry name>.azurecr.io 가 붙음 )

<br/>

Networking 설정에서는 무료 버전 임으로 아래와 같이 public 으로 설정됨    
-  public 으로 설정하더라도 권한으로 제어가 되기 때문에 anonymous pull 은 불가능   

<img src="./assets/acr_2.png" style="width: 60%; height: auto;"/>  

<br/>

Encryption 은  disable 로 설정    

<img src="./assets/acr_3.png" style="width: 60%; height: auto;"/>  

<br/>

Tag는 생략하고 Review + create 를 클릭하여 생성합니다.
`icishub` 라는 이름으로 레지스트리가 생성이 되었습니다.  

<img src="./assets/acr_4.png" style="width: 60%; height: auto;"/>  

<br/>

`icishub` 를 클릭하고 Setting -> Access keys 로 이동합니다.  

admin user를 체크하면 password 가 생성이 되고 github action에 사용하기 위해 비밀번호를 복사하여 저장합니다.     

<img src="./assets/acr_5.png" style="width: 60%; height: auto;"/>  

<br/>

Services -> Repositorys 에 보면 아직 도커 이미지가 아무것도 없는 것을 확인 할 수 있습니다. 

<img src="./assets/acr_6.png" style="width: 60%; height: auto;"/>  

<br/>

###  GitHub Action 구성

<br/>

이제 CI를 하기 위해서 github의 아래 url를 웹 브라우저에 붙여넣기 합니다.  

`https://github.com/shclub/edu1` 를 Fork 버튼을 클릭하여 본인의 github 계정에 Fork 하고 포크된 본인의 github 계정의 `edu1` repository에 가서   `.github/workflows` 폴더로 클릭하여 이동합니다.    

<br/>
 
해당 폴더 밑에 아래 와 같이 docker_azure.yaml 화일을 생성합니다.     
- 아래는 github action 의  yaml 화일이고 소스를 가져와서 도커 이미지를 만들고 ACR에 push 하는 예제입니다.         
- 도커 이미지를 생성하기 위해서는 최상위 폴더에 Dockerfile 이 있어야 합니다.  
- Github 는 기본적으로 ubuntu 를 CI 할때 사용 합니다.

<br/>

```yaml
name: Publish Docker Azure image

on:      
  workflow_dispatch:
    inputs:
      name:
        description: "Docker TAG"
        required: true
        default: "master"
    
jobs:
  push_to_registry:
    name: Push Docker image to Docker Hub
    runs-on: ubuntu-latest
    steps:
      - name: Check out the repo
        uses: actions/checkout@v4
      
      - name: Log in to Docker Hub
        uses: azure/docker-login@v1
        with:
          login-server: ${{ secrets.AZURE_URL }}
          username: ${{ secrets.ACR_USERNAME }}
          password: ${{ secrets.ACR_PASSWORD }}
      
      - name: Extract metadata (tags, labels) for Docker
        id: meta
        uses: docker/metadata-action@98669ae865ea3cffbcbaa878cf57c20bbf1c6c38
        with:
          images: ${{ github.repository }}
          tags: ${{ github.event.inputs.name }}
      
      - name: Build and Push to ACR
        uses: docker/build-push-action@v2
        with:
          context: .
          push: true
          tags: ${{ secrets.AZURE_URL }}/${{ steps.meta.outputs.tags }}
```  

<br/>

화일을 생성 후에 3가지 변수를 repository 의 secret 에 저장합니다. 아래의 단계로 진행 합니다.    

Settings -> Secrets and variables -> Action -> New repository secret 클릭

<img src="./assets/acr_7.png" style="width: 60%; height: auto;"/>    

- AZURE_URL : <본인의 acr registry name>.azurecr.io
- ACR_USERNAME : 계정 이름
- ACR_PASSWORD : 계정의 비밀번호  

<br/>

Action Tab 으로 이동하여 Publish Docker Azure image 를 선택합니다.      

<img src="./assets/acr_8.png" style="width: 60%; height: auto;"/>    

<br/>

Run workflow 를 선택을 하여 tag 에 `v1` 를 입력합니다 ( 원하시는 Tag 명을 넣으시면 됩니다. )    

<img src="./assets/acr_9.png" style="width: 60%; height: auto;"/>    

<br/>

Workflow 를 Run 하면 노란색 아이콘이 보이고 클릭하면 자세한 빌드 내용을 볼수 있습니다.  
시간이 경과하여 파란색 아이콘이 생성이 되면 빌드가 완료가 되면 push 까지 진행이 되었습니다.  

<img src="./assets/acr_10.png" style="width: 60%; height: auto;"/>    

<br/>

Azure Portal 로 이동을 하여 Azure Container Registry 에 가면  도커 이미지가 Push 된 것을 확인 할수 있습니다.     

<img src="./assets/acr_11.png" style="width: 60%; height: auto;"/>    

<br/>

도커 이미지가 생성이 되었고 Push 가 되었기 때문에 CI 는 완료가 되었습니다.

<br/>

## 2. GitOps 구성 ( Azure DevOps Azure Repo )

<br/>

Microsoft는 Azure Repo 와 GitHub 통해 GitOps를 구현 할수 있지만 여기에서는 Azure Repo SaaS 서비스를 활용 해 본다. GitHub 도 사용 방법은 유사하다.  

<br/>

`https://dev.azure.com/` 에 로그인을 한다.    

New Project 버튼을 클릭하고 edu 라는 이름으로  Visibility는 `private` 로 설정하고 repository 를 생성한다.   

<img src="./assets/azure_repo_1.png" style="width: 60%; height: auto;"/>    

<br/>

생성된 Project 에서 Repos를 클릭하고 Files를 선택하면 Generate Git Credentials 버튼이 보이고 클릭을 하면 https/ssh 로 해당 Repository에 접속할 비밀번호를 생성 할 수 있다.

<img src="./assets/azure_repo_2.png" style="width: 60%; height: auto;"/>    

<br/>

Https URL , 계정 , 비밀번호를 로컬 pc에 저장해 놓습니다.  

<img src="./assets/azure_repo_3.png" style="width: 60%; height: auto;"/>    

<br/>

GitOps 폴더는 직접 구성해도 되지만 여기서는 기존에 개발되어 있던 gitops repository 를 clone 하도록 합니다.    

import Repository 를 클릭하고 `https://github.com/shclub/edu1_gitops.git` 를 clone 합니다.


<img src="./assets/azure_repo_4.png" style="width: 60%; height: auto;"/>    

<br/>

시간이 약간 경과 된 후 아래와 같이 yaml 화일이 생성된 것을 확인 할 수 있습니다.  

<img src="./assets/azure_repo_5.png" style="width: 60%; height: auto;"/>    

<br/>

deployment.yaml 와 kustomization.yaml 에서 본인의 도커 이미지로 변경합니다.      

<br/>

deployment.yaml
```yaml
...
    spec:
      containers:
      - name: edu1
        image: icishub.azurecr.io/shclub/edu1  # 본인의 이미지 이름으로 변경. tag는 설정하지 않음
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
```  

<br/>


kustomization.yaml  
```yaml
...
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
- deployment.yaml
- service.yaml
images:
- name: icishub.azurecr.io/shclub/edu1 # 본인 이미지로 교체
  newTag: "v1"  # Tag도 설정 . 향후 CI 진행시 Kustomize 방식 사용하면 자동으로 변경
```  

<br/>

여기 까지 진행이 되면 CD 준비가 완료가 되었습니다.  

<br/>

## 3. AKS ( Azure Kubernetes Service ) 구성

<br/>

AKS는 Azure Kubernetes Service 의 약자로 Azure에서 Managed 하는 Kubernetes Service 입니다.     

컨테이너 이미지를 배포 하기 위해 AKS 클러스터를 하나 생성 해야 합니다.  

<br/>

### AKS Cluster 생성하기   

<br/>

https://portal.azure.com 에서 `aks` 로 검색을 한 후 `kubernetes service` 를 선택한다.

<img src="./assets/aks_search.png" style="width: 60%; height: auto;"/>    

<br/>

AKS Cluster를 신규로 생성하기 위해서 Create 버튼을 클릭하고 Kubernetes cluster를 선택합니다.

<img src="./assets/aks_2.png" style="width: 60%; height: auto;"/>    

<br/>

Resource Group 을 설정하고 원하시는 kubernetes 이름을 입력합니다.  

<img src="./assets/aks_3.png" style="width: 60%; height: auto;"/>

<br/>

kubernetes 버전은 1.28.9(default)를 선택하고 인증 방식은 local account RBAC 을 선택합니다. 
- 현재 사용하는 OKD 4.11 은 1.25 버전을 사용함  

<img src="./assets/aks_4.png" style="width: 60%; height: auto;"/>

<br/>

Node Pool은 Woker node를 나타내며 Node Size를 클릭하여 Node에 대한 세부 설정을 할수 있습니다.

<img src="./assets/aks_5.png" style="width: 60%; height: auto;"/>

<br/>

- OS : Ubuntu
- Node Size : ( Choose a size 클릭해서 변경 )
- Node Count : worker node 의 count
   - 최소값 : 1
   - 최대값 : 2 ( 1 로 설정시 cpu 부족으로 argocd pod cpu 부족 현상 발생 )

<img src="./assets/aks_6.png" style="width: 60%; height: auto;"/>

<br/>

worker node vm 사이즈는 가격이 저렴한 것중에서 memory가 높은 것을 선택  

<img src="./assets/aks_6-1.png" style="width: 60%; height: auto;"/>

<br/>

변경이 완료 된 것을 확인 할 수 있다.  

<img src="./assets/aks_6-2.png" style="width: 60%; height: auto;"/>

<br/>

worker node 당 pod의 갯수를 설정 한다.  

<img src="./assets/aks_7.png" style="width: 60%; height: auto;"/>

<br/>

테스트 환경이기 때문에 AKS는 Public 으로 오픈하여 외부에서 k8S API를 호출 할 수 있도록 한다.  

- cni : Azure CNI
- Network Polocy : Calico
- Load Balancer : Standard ( Free Tier 에서 LoadBalancer IP는 최대 3개 할당 됨 . 워커 노드에는 최대 2개만 가능 )  

<img src="./assets/aks_8.png" style="width: 60%; height: auto;"/>

<br/>

container registry는 기존에 생성된 registry를 입력하고 istio는 설정하지 않는다.

- registry를 설정하면 해당 registry 에서 worker node의 role이 assign 되어 registry의 image를 권한 없이 pull 할 수 있다. 

<img src="./assets/aks_9.png" style="width: 60%; height: auto;"/>

<br/>

Container Registry 권한 확인  

<img src="./assets/azr_argocd_7.png" style="width: 60%; height: auto;"/>

<br/>

container log 활성화를 하고 cost preset은 cost-optimized를 선택한다.  


<img src="./assets/aks_10.png" style="width: 60%; height: auto;"/>

<br/>

secret store CSI Driver를 enable 시키면 kubernetes secret을 azure key vault 와 연동 할수 있다.    

infrastructure resource group은 MC 라는 prefix로 생성이 된다.  

<img src="./assets/aks_11.png" style="width: 60%; height: auto;"/>

<br/>

review + create 버튼을 클릭하여 AKS Cluster 를 생성한다.  

<img src="./assets/aks_complete.png" style="width: 60%; height: auto;"/>

<br/>


### Cli 로 접속하기 ( 웹으로 접속 )

<br/>

웹 브라우저를 통하여 터미널 환경을 사용 할 수 있습니다. ( 랜딩존 환경에서는 오픈 불가 )  

생성된 AKS 상단에 Cloud Shell 버튼을 클릭합니다.   

<img src="./assets/azure_cloudshell_1.png" style="width: 60%; height: auto;"/>

<br/>

브라우저 하단에 검은색 화면이 나타나면 shell prompt 가 뜰때 까지 기다립니다.

현재 aks에서  shell 을 실행하면 별도 azure 로그인 과정은 필요 없습니다.  

<br/>


위에서 생성한 AKS Cluster에 접속합니다.  
- az aks get-credentials --resource-group `<resource group>` --name `<aks cluster name>`  


<br/>

```bash
lee [ ~ ]$ az aks get-credentials --resource-group icis-poc-0 --name icisaks1
Merged "icisaks1" as current context in /home/lee/.kube/config
```  

<br/>

node를 조회해 보면 worker node 2개가 생성 되어 있는 것을 확인 할수 있습니다.

```bash
lee [ ~ ]$ kubectl get nodes -o wide
NAME                                STATUS   ROLES   AGE   VERSION   INTERNAL-IP    EXTERNAL-IP   OS-IMAGE             KERNEL-VERSION      CONTAINER-RUNTIME
aks-agentpool-36747243-vmss000000   Ready    agent   11d   v1.28.9   10.224.0.4     <none>        Ubuntu 22.04.4 LTS   5.15.0-1066-azure   containerd://1.7.15-1
aks-agentpool-36747243-vmss000002   Ready    agent   8d    v1.28.9   10.224.0.113   <none>        Ubuntu 22.04.4 LTS   5.15.0-1066-azure   containerd://1.7.15-1
lee [ ~ ]$ 
```  
<br/>

namespace list 를 확인합니다.  

```bash
lee [ ~ ]$ kubectl get namespaces
NAME                            STATUS   AGE
aks-command                     Active   4d3h
argocd                          Active   8d
azure-extensions-usage-system   Active   8d
calico-system                   Active   11d
default                         Active   11d
gatekeeper-system               Active   11d
kube-node-lease                 Active   11d
kube-public                     Active   11d
kube-system                     Active   11d
mvp                             Active   8d
tigera-operator                 Active   11d
```   

<br/>

> TIP : Cloud Shell 이 안된다면 ?  

<br/>

kubernetes resources -> Run command 를 클릭하고 아래에 명령어를 넣고 RUN 버튼을 누르면 kubectl 명령어를 사용 할수 있다.    

<br/>

<img src="./assets/azure_cloudshell_6.png" style="width: 60%; height: auto;"/>

<br/>

shell 로 접속하지 않고도 브라우저에서도 간단한 리소스들은 확인 가능 합니다.  

<img src="./assets/azure_cloudshell_7.png" style="width: 60%; height: auto;"/>


<br/>


### Cli 로 접속하기 ( For Mac )

<br/>

참고 
- https://november11tech.tistory.com/153  
- https://blog.hojaelee.com/213?category=913645  

<br/>

Mac 에서 brew 를 사용하여 azure cli 설치합니다.  

<br/>

```bash
jakelee@jake-MacBookAir ~ % brew update && brew install azure-cli
```    

<br/>

아래 명령어로 로그인을 합니다.

```bash
jakelee@jake-MacBookAir ~ % az login
```      

명령어 실행 시 Azure에 로그인할 수 있는 웹 브라우저가 열리고 이미 로그인이 되어 있으면 아래 처럼 보입니다.

<img src="./assets/azr_login_1.png" style="width: 60%; height: auto;"/>

<br/>

tenent 번호를 입력합니다.  

<img src="./assets/azr_login_2.png" style="width: 60%; height: auto;"/>

<br/>

로컬 환경에 kubectl이 설치되어 있지 않다면 azure cli 로 설치한다.  

```bash
jakelee@jake-MacBookAir ~ % az aks install-cli
```    
<br/>

위에서 생성한 AKS Cluster에 접속합니다.  
- az aks get-credentials --resource-group <resource group> --name <aks cluster name>  

<br/>

```bash
jakelee@jake-MacBookAir ~ % az aks get-credentials --resource-group icis-poc-0 --name icisaks1

Merged "icisaks1" as current context in /Users/jakelee/.kube/config
```  

<br/>

worker node 와  도커엔진을  확인합니다.  

```bash
jakelee@jake-MacBookAir ~ % kubectl get nodes -o wide
NAME                                STATUS   ROLES   AGE     VERSION   INTERNAL-IP    EXTERNAL-IP   OS-IMAGE             KERNEL-VERSION      CONTAINER-RUNTIME
aks-agentpool-36747243-vmss000000   Ready    agent   3d18h   v1.28.9   10.224.0.4     <none>        Ubuntu 22.04.4 LTS   5.15.0-1066-azure   containerd://1.7.15-1
aks-agentpool-36747243-vmss000001   Ready    agent   4m7s    v1.28.9   10.224.0.113   <none>        Ubuntu 22.04.4 LTS   5.15.0-1066-azure   containerd://1.7.15-1
```  

<br/>

namespace list 를 확인 합니다.  

```bash
jakelee@jake-MacBookAir ~ % kubectl get namespaces
NAME                STATUS   AGE
calico-system       Active   3d14h
default             Active   3d14h
gatekeeper-system   Active   3d14h
kube-node-lease     Active   3d14h
kube-public         Active   3d14h
kube-system         Active   3d14h
tigera-operator     Active   3d14h
```

<br/>

### 배포 준비

<br/>

Azure Container Registry 를 확인합니다.  

```bash
jakelee@jake-MacBookAir ~ % az acr list --resource-group icis-poc-0 --query "[].{acrLoginServer:loginServer}" --output table

AcrLoginServer
------------------
icishub.azurecr.io
```  

<br/>

2개의 namespace 를 생성합니다.  

<br/>

application을 배포 하기 위한 `mvp` 라는 이름으로 namespace를 생성합니다. 

```bash
jakelee@jake-MacBookAir ~ % kubectl create namespace mvp
namespace/mvp created
```  

<br/>

또한 Argocd 설치를 위한 argocd namespace를 생성합니다.    

```bash
jakelee@jake-MacBookAir ~ % kubectl create namespace argocd
namespace/argocd created
```  

<br/>

## 4. CD 구성하기 ( ArgoCD )

<br/>

GitOps를 통하여 배포하기 위해 ArgoCD를 설치를 합니다.  

Azure 에는 ArgoCD Managed 서비스는 없고 Market Place 를 통해서 설치하거나 직접 Helm 으로 설치 해야 합니다.  

여기에서는 Market Place 에서 제공하는 ArgoCD를 설치 하도록 하겠습니다.  

<br/>

### ArgoCD 설치

<br/>

ArgoCD는 GitOps를 구현 하는 솔루션으로 Flux 와 같이 가장 많이 사용하는 오픈 소스입니다.

<br/>

ArgoCD는 Kubernetes 에서 동작하기 때문에 AKS 에서 Settings -> `Extensions + applications` 로 이동한 후  Add 버튼을 클릭합니다.  

<br/>

<img src="./assets/argocd_aks_1.png" style="width: 60%; height: auto;"/>

<br/>

argocd 를 검색한 후 Create 버튼을 클릭합니다. 

<img src="./assets/argocd_aks_2.png" style="width: 60%; height: auto;"/>  

aks에서 검색하지 않고 portal 에서도 검색 할수도 있습니다. 

<br/>

Bitnami 에서 제공하는 Argocd를 확인하고 Create 버튼을 클릭합니다.  

<img src="./assets/argocd_aks_3.png" style="width: 60%; height: auto;"/>  

<br/>

Resource Group을 선택하고 AKS는 이미 존재 함으로 AKS는 생성하지 않습니다.

<img src="./assets/argocd_aks_4.png" style="width: 60%; height: auto;"/>  

<br/>

Resource Group을 선택하고 AKS는 이미 존재 함으로 AKS는 생성하지 않습니다.

<img src="./assets/argocd_aks_4.png" style="width: 60%; height: auto;"/>  

<br/>

설치 될 AKS 이름을 설정합니다. 

<img src="./assets/argocd_aks_5.png" style="width: 60%; height: auto;"/>  

<br/>

내부적으로 helm chart로 설치가 진행 되는것을 알 수 있습니다.  

- Cluster extension Resource Name은 Helm의 release name 으로 설정이 되면 모든 k8s resource에 prefix로 설정 된다.
- installation namespace : argocd ( 위에서 생성함 )
- application parameters : 클릭하면 argocd helm github로 이동하여 variable list를 보여준다.  
- parameter key : helm values 에 set 하는 key를 선언
  - argocd admin password 는 아래와 같아서 Bcrypt으로 암호화 되기 때문에 기존 처럼 admin 비밀 번호를 알수가 없어 비밀번호를 초기에 생성해야함.  

    values.yaml
    ```yaml
    556     # -- Bcrypt hashed admin password
    557     ## Argo expects the password in the secret to be bcrypt hashed. You can create this hash with
    558     ## `htpasswd -nbBC 10 "" $ARGO_PWD | tr -d ':\n' | sed 's/$2y/$2a/'`
    559     argocdServerAdminPassword: ""
    560     # -- Admin password modification time. Eg. `"2006-01-02T15:04:05Z"`
    561     # @default -- `""` (defaults to current time)
    562     argocdServerAdminPasswordMtime: ""
    ```

<br/>

아래와 같이 설정 한다.

<img src="./assets/argocd_aks_6.png" style="width: 60%; height: auto;"/>  

<br/>

설치 후에 Cli로 아래와 같이 helm release 상태를 확인 해 볼수 있다.  

```bash
jakelee@jake-MacBookAir ~ % helm list -n argocd
NAME   	NAMESPACE	REVISION	UPDATED                                	STATUS  	CHART        	APP VERSION
argocd1	argocd   	20      	2024-07-08 14:45:55.130680288 +0000 UTC	deployed	argo-cd-6.3.3	2.11.2
```  

<br/>

application parameters 클릭시 이동해서 key 값 찾기.

<img src="./assets/argocd_aks_7.png" style="width: 60%; height: auto;"/>  

<br/>

설치 완료가 되면 아래와 같이 complete 가 된다.

<img src="./assets/argocd_aks_8.png" style="width: 60%; height: auto;"/>  

<br/>

pod를 조회 해 본다.

```bash
jakelee@jake-MacBookAir ~ % kubectl get po -n argocd
NAME                                              READY   STATUS    RESTARTS   AGE
argocd1-argo-cd-app-controller-84c89b9959-mjxn4   1/1     Running   0          110s
argocd1-argo-cd-repo-server-5f944c77ff-hcdbk      1/1     Running   0          110s
argocd1-argo-cd-server-6f6b59b479-8x6c4           1/1     Running   0          110s
argocd1-redis-master-0                            1/1     Running   0          110s
```  

<br/>

오픈소스에서는 별도의 PV/PVC가 생성 되지 않지만 Azure Market Place의 Argocd는 dynamic provisioning 을 통하여 Redis 용 백업 PV/PVC가 자동으로 생성 된 것을 확인 할 수 있다.  ( 오픈소스로 설치시 별도 설정이 필요하다.  )

```bash
jakelee@jake-MacBookAir ~ % kubectl get pvc -n argocd
NAME                                STATUS   VOLUME                                     CAPACITY   ACCESS MODES   STORAGECLASS   AGE
redis-data-argocd1-redis-master-0   Bound    pvc-7e254ade-d68a-45b9-a83d-18bf1dfe9d6c   8Gi        RWO            default        5m24s
jakelee@jake-MacBookAir ~ % kubectl get pv
NAME                                       CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM                                      STORAGECLASS   REASON   AGE
pvc-7e254ade-d68a-45b9-a83d-18bf1dfe9d6c   8Gi        RWO            Delete           Bound    argocd/redis-data-argocd1-redis-master-0   default                 5m26s
```  

<br/>

MarketPlace 용 ArgoCD는 아래 처럼 과금을 위한 설정이 된다.

<img src="./assets/argocd_aks_9.png" style="width: 60%; height: auto;"/>  

<br/>

서비스를 조회해 보면 이름 앞에 `argocd1` prefix 가 붙은 것을 확인 할 수 있고 `argo-cd-server` 서버가 `ClusterIP` Type으로 설정되어 외부에서 접속이 불가능하다.    

```bash
jakelee@jake-MacBookAir ~ % kubectl get svc -n argocd
NAME                             TYPE           CLUSTER-IP     EXTERNAL-IP     PORT(S)                      AGE
argocd1-argo-cd-app-controller   ClusterIP      10.0.86.106    <none>          8082/TCP                     9h
argocd1-argo-cd-repo-server      ClusterIP      10.0.249.187   <none>          8081/TCP                     9h
argocd1-argo-cd-server           ClusterIP      10.0.151.42    <none>          80,443                       9h
argocd1-redis-headless           ClusterIP      None           <none>          6379/TCP                     9h
argocd1-redis-master             ClusterIP      10.0.237.20    <none>          6379/TCP                     9h
```  

<br/>

서비스의 Type을 `LoadBalancer` Type으로 변경하면 `EXTERNAL-IP` 가 할당 되지만 Azure ArgoCD 에서는 주기적으로 초기 설정값으로 원복이된다.     

AKS의 Argocd 설정에서 Configuration Setting 에서 추가로 parameter를 아래와 같이 추가 하고 Update 하면 `LoadBalancer` IP를 할당 받고 계속 유지가 된다.  

<img src="./assets/argocd_aks_10.png" style="width: 60%; height: auto;"/>    

<br/>

```bash
jakelee@jake-MacBookAir ~ % kubectl get svc -n argocd
NAME                             TYPE           CLUSTER-IP     EXTERNAL-IP     PORT(S)                      AGE
argocd1-argo-cd-app-controller   ClusterIP      10.0.86.106    <none>          8082/TCP                     9h
argocd1-argo-cd-repo-server      ClusterIP      10.0.249.187   <none>          8081/TCP                     9h
argocd1-argo-cd-server           LoadBalancer   10.0.151.42    52.231.186.96   80:30583/TCP,443:32247/TCP   9h
argocd1-redis-headless           ClusterIP      None           <none>          6379/TCP                     9h
argocd1-redis-master             ClusterIP      10.0.237.20    <none>          6379/TCP                     9h
```  

<br>
 
웹 브라우저에서 `http://52.231.186.96` 로 접속하여 로그인 하여 봅니다.
- admin/New1234!  

<br/>

### ArgoCD 로 배포하기

<br/>

ArgoCD 로 로그인후 Settings-> CONNECT REPO를  클릭한다.  

<img src="./assets/argocd_aks_11.png" style="width: 60%; height: auto;"/>    

<br/>

아래와 같이 설정한다.  

- Connetion Method 는 HTTS 를 선택  
- project : default  
- Repository URL : azure repo 에 설정한 url  
- Username : azure repo repository 에서 get credentials을 통해서 생성    
- Password : azure repo repository 에서 get credentials을 통해서 생성    

<img src="./assets/argocd_aks_12.png" style="width: 60%; height: auto;"/>    

<br/>

Connect 를 누르면 git url이 등록되고 Connection Stauts 가 successful 인것 을 확인 할 수 있다. 

<img src="./assets/argocd_aks_13.png" style="width: 60%; height: auto;"/>    

<br/>

Applications -> NEW APP를 클릭한다.   

<img src="./assets/argocd_aks_14.png" style="width: 60%; height: auto;"/>    

<br/>

Application Name은 원하는 이름으로 설정하고 Project Name은  defaulf 로 선택한다.  

<img src="./assets/argocd_aks_15.png" style="width: 60%; height: auto;"/>    

<br/>

아래와 같이 설정한다.  

- Repository URL : Azure Repo Repository  
- Path : . ( 현재 폴더 의미 )  
- Cluster URL : 현재 kubernetes  
- Namespace : mvp ( 위에서 생성)  

<img src="./assets/argocd_aks_16.png" style="width: 60%; height: auto;"/>    

<br/>

kustomization.yaml 내용을 보여준다.  

<img src="./assets/argocd_aks_17.png" style="width: 60%; height: auto;"/>    

<br/>

생성된 App를 볼수 있고 Sync 버튼을 클릭한다.  

<img src="./assets/argocd_aks_18.png" style="width: 60%; height: auto;"/>    

<br/>

SYNCHRONIZE RESOURCES 에 sync 할 리소스가 체크되어 있는지 확인하고 상단에 SYNCHRONIZE 버튼을 클릭한다.  

<img src="./assets/argocd_aks_19.png" style="width: 60%; height: auto;"/>    

<br/>

edu1 카드를 클릭하면 배포가 진행 되는 것을 확인 할 수 있다.    

<br/>

<img src="./assets/argocd_aks_20.png" style="width: 60%; height: auto;"/>    


```bash
jakelee@jake-MacBookAir ~ % kubectl get po -n mvp
NAME                       READY   STATUS             RESTARTS      AGE
edu1-5f4fcb5d88-n5x8v      1/1     Running            0             3m7s
jakelee@jake-MacBookAir ~ % kubectl get svc -n mvp
NAME      TYPE           CLUSTER-IP     EXTERNAL-IP      PORT(S)        AGE
edu1      ClusterIP      10.0.169.123   <none>           80/TCP         6m17s
```  

<br/>

Pod 와 Service 만 배포가 되었고 향후에 Ingress Controller를 설치하고 URL 로 접속 할 예정입니다.

<br/>

## trouble shooting

<br/>

### Argocd 설치시 Insufficient cpu 에러  

<br/>

```bash
jakelee@jake-MacBookAir ~ % kubectl get events -n argocd
LAST SEEN   TYPE      REASON                 OBJECT                                                     MESSAGE
...
iciscer1-argo-cd-repo-server-578cc566cc-kwvl2          0/1 nodes are available: 1 Insufficient cpu. preemption: 0/1 nodes are available: 1 No preemption victims found for incoming pod..
```  
<br/>

해결 방법 : AKS node pool max 값을 2 이상으로 조정하고 생성

<br/>

### Worker Node 에서 ACR Image Pull 권한 오류   

<br/>

argocd 에서 배포시 아래와 같은 에러 발생  

<br/>

```bash
Normal   Pulling    6m5s (x4 over 7m38s)    kubelet            Pulling image "icishub.azurecr.io/cloudnative-mvp1-azure/mvp_apigw"
Warning  Failed     6m5s (x4 over 7m38s)    kubelet            Failed to pull image "icishub.azurecr.io/cloudnative-mvp1-azure/mvp_apigw": [rpc error: code = NotFound desc = failed to pull and unpack image "icishub.azurecr.io/cloudnative-mvp1-azure/mvp_apigw:latest": failed to resolve reference "icishub.azurecr.io/cloudnative-mvp1-azure/mvp_apigw:latest": icishub.azurecr.io/cloudnative-mvp1-azure/mvp_apigw:latest: not found, failed to pull and unpack image "icishub.azurecr.io/cloudnative-mvp1-azure/mvp_apigw:latest": failed to resolve reference "icishub.azurecr.io/cloudnative-mvp1-azure/mvp_apigw:latest": failed to authorize: failed to fetch anonymous token: unexpected status from GET request to https://icishub.azurecr.io/oauth2/token?scope=repository%3Acloudnative-mvp1-azure%2Fmvp_apigw%3Apull&service=icishub.azurecr.io: 401 Unauthorized]
```  

<br/>

해결 방법 : worker node에서 ACR 에 접속할 권한이 없기 때문에 권한을 할당 해야함.  

```bash
jakelee@jake-MacBookAir aks_argocd % az aks update --name icisaks1 --resource-group icis-poc-0 --attach-acr icishub
```
<br/>

Container Registry 권한 확인  

<img src="./assets/azr_argocd_7.png" style="width: 60%; height: auto;"/>

<br/>

 [ Azure advanced Hands-On 문서로 넘어가기 ](./azure_advanced.md)     

<br/>

# k8s Overview

<br/>

Redhat Openshift 의  `오픈소스 버전`인 OKD Cluster 를  생성하고 사용해본다.   ( OKD 4.12 버전 기준 : k8s 1.25 )      

또한 Observability 에 대한 개념을 이해한다.  

<br/>

OKD 설명 참고 :  https://velog.io/@_gyullbb/OKD-%EA%B0%9C%EC%9A%94  

   
1. k8s & OKD 구조 

2. k8s 주요 기능



<br/>

## 1. k8s & OKD 구조  

<br/>

### kubernetes Cluster 구성

<br/>

- 출처 : https://www.redhat.com/ko/topics/containers/kubernetes-architecture


<br/>

kubernetes의 가장 큰 단위는 cluster 이고 cluster는 서버들의 모임이다.  

<br/>

<img src="./assets/k8s_cluster.png" style="width: 80%; height: auto;"/> 
<br/>

#### 컨트롤 플레인 ( Control Plane )

<br/>

Master Node 들의 모임이며 최근에는 Master 라는 말 보다는 Control Plane 이라는 용어를 사용한다.    

<br/>

클러스터를 제어하는 쿠버네티스 구성 요소와 클러스터의 상태 및 구성에 관한 데이터가 함께 있습니다. 이 핵심 쿠버네티스 구성 요소는 컨테이너가 필요한 리소스를 갖고 충분한 횟수로 실행되도록 하는 중요한 작업을 맡습니다.   

<br/>

컨트롤 플레인은 컴퓨팅 노드와 상시 연결되어 있습니다. 클러스터가 일정한 방식으로 실행되도록 구성했다면 컨트롤 플레인은 해당 방식에 따라 실행.  

<br>

구성요소 
- kube-apiserver 
  - 쿠버네티스 클러스터와 상호 작용해야 하나요? API에 요청하세요. 쿠버네티스 API는 쿠버네티스 컨트롤 플레인의 프론트엔드로, 내부 및 외부 요청을 처리합니다.   
  - API 서버는 요청이 유효한지 판별하고 유효한 요청을 처리합니다. REST 호출이나 kubectl 커맨드라인 인터페이스 또는 kubeadm과 같은 기타 CLI(command-line interface)를 통해 API에 액세스할 수 있습니다.    

<br/>

- kube-scheduler
  - 클러스터가 양호한 상태인가? 새 컨테이너가 필요하다면 어디에 적합한가? 쿠버네티스 스케줄러는 이러한 것들을 주로 다룹니다.  

  - 스케줄러는 CPU 또는 메모리와 같은 포드의 리소스 요구 사항과 함께 클러스터의 상태를 고려합니다. 그런 다음 포드를 적절한 컴퓨팅 노드에 예약합니다.   

<br/>

- kube-controller-manager
  - 컨트롤러는 실제로 클러스터를 실행하고 쿠버네티스 controller-manager에는 여러 컨트롤러 기능이 하나로 통합되어 있습니다.   
  - 하나의 컨트롤러는 스케줄러를 참고하여 정확한 수의 포드가 실행되게 합니다.     
  - 포드에 문제가 생기면 또 다른 컨트롤러가 이를 감지하고 대응합니다.   
  - 컨트롤러는 서비스를 포드에 연결하므로 요청이 적절한 엔드포인트로 이동합니다.  
  - 또한 계정 및 API 액세스 토큰 생성을 위한 컨트롤러가 있습니다.  

<br/>

- etcd
  - 설정 데이터와 클러스터의 상태에 관한 정보는 키-값 저장소 데이터베이스인 etcd에 상주합니다.   

  - 내결함성을 갖춘 분산형 etcd는 클러스터에 관한 궁극적 정보 소스(Source Of Truth, SOT)가 되도록 설계되었습니다.  

  - k3s 같은 경량 k8s 배포판은 etcd 대신 sqlite 를 사용하기도 합니다.     


<br/>

#### 컴퓨트 플레인 ( Compute Plane )

<br/>

Worker Node 들의 모임이며 최근에는 Worker 라는 말 보다는 Compute Plane 또는 Data Plane 이라는 용어를 사용한다.    

<br/>

구성요소  
- 노드 ( Node )
 - 쿠버네티스 클러스터에는 최소 1개 이상의 컴퓨팅 노드가 필요하지만 일반적으로 여러 개가 있습니다. Pod는 노드에서 실행하도록 예약되고 오케스트레이션됩니다.     
 - 클러스터의 용량을 확장해야 한다면 노드를 더 추가하면 됩니다.  
 - 일반적으로 노드 하나는 VM ( 또는 Dedi 서버 ) 1개입 니다.  
 - 각 노드에서 OS 가 설치 됩니다. ( CentOS , Ubuntu , CoreOS 등 )

<br/>

- Pod
 - 파드는 쿠버네티스 오브젝트 모델에서 가장 작고 단순한 유닛으로, 애플리케이션의 단일 인스턴스를 나타냅니다. 

 - 각 파드는 컨테이너 실행 방식을 제어하는 옵션과 함께 컨테이너 하나 또는 긴밀히 결합된 일련의 컨테이너로 구성되어 있습니다.   

 - 파드를 퍼시스턴트 스토리지에 연결하여 스테이트풀(stateful) 애플리케이션을 실행할 수 있습니다.

<br/>

- 컨테이너 런타임 엔진
 - 컨테이너 실행을 위해 각 컴퓨팅 노드에는 컨테이너 런타임 엔진이 있습니다. 그중 한 가지 예가 Docker입니다.  
 - 하지만 쿠버네티스는 rkt, CRI-O와 같은 다른 Open Container Initiative 호환 런타임도 지원합니다. OKD는 CRI-O를 사용 한다.  

<br/>

- kubelet
 - 각 컴퓨팅 노드에는 컨트롤 플레인과 통신하는 매우 작은 애플리케이션인 kubelet이 있습니다. kublet은 컨테이너가 포드에서 실행되게 합니다.   
 - 컨트롤 플레인에서 노드에 작업을 요청하는 경우 kubelet이 이 작업을 실행합니다.

<br/>

- kube-proxy
 - 각 컴퓨팅 노드에는 쿠버네티스 네트워킹 서비스를 용이하게 하기 위한 네트워크 프록시인 kube-proxy도 있습니다.  
- kube-proxy는 운영 체제의 패킷 필터링 계층에 의존하거나 트래픽 자체를 전달하여 클러스터 내부 또는 외부의 네트워크 통신 (iptables) 을 처리합니다.  
  

<br/>

#### Addon

<br/>

애드온은 쿠버네티스 리소스(데몬셋, 디플로이먼트 등)를 이용하여 클러스터 기능을 구현한다. 이들은 클러스터 단위의 기능을 제공하기 때문에 애드온에 대한 네임스페이스 리소스는 kube-system 네임스페이스에 속한다.  

<br/>

선택된 일부 애드온은 아래에 설명하였고, 사용 가능한 전체 확장 애드온 리스트는 애드온을 참조한다.

<br/>

- DNS (coreDNS )
  - 여타 애드온들이 절대적으로 요구되지 않지만, 많은 예시에서 필요로 하기 때문에 모든 쿠버네티스 클러스터는 클러스터 DNS를 갖추어야만 한다.  
  
  - 클러스터 DNS는 구성환경 내 다른 DNS 서버와 더불어, 쿠버네티스 서비스를 위해 DNS 레코드를 제공해주는 DNS 서버다.  

  - 쿠버네티스에 의해 구동되는 컨테이너는 DNS 검색에서 이 DNS 서버를 자동으로 포함한다.  

- 컨테이너 리소스 모니터링  
  - 컨테이너 리소스 모니터링은 중앙 데이터베이스 내의 컨테이너들에 대한 포괄적인 시계열 매트릭스를 기록하고 그 데이터를 열람하기 위한 UI를 제공해 준다.

<br/>

#### 쿠버네티스 클러스터에 필요한 요소

<br/>


퍼시스턴트 스토리지
- 쿠버네티스는 애플리케이션을 실행하는 컨테이너를 관리할 뿐만 아니라 클러스터에 연결된 애플리케이션 데이터도 관리할 수 있습니다.   

  쿠버네티스를 사용하면 사용자가 기본 스토리지 인프라에 관한 상세 정보를 알지 못해도 스토리지 리소스를 요청할 수 있습니다.  
  퍼시스턴트 볼륨은 포드가 아닌 클러스터에 따라 다르므로 포드보다 수명이 오래 지속될 수 있습니다.

<br/>

컨테이너 레지스트리
- 쿠버네티스가 의존하는 컨테이너 이미지는 컨테이너 레지스트리에 저장됩니다.   
  이러한 레지스트리를 직접 구성하거나 제 3사가 구성할 수 있습니다.

<br/>

<img src="./assets/k8s_cluster2.png" style="width: 80%; height: auto;"/> 

<br/>

참고:
- 출처:  https://kubernetes.io/ko/docs/concepts/overview/components/

 
<br/>

## 2. k8s 주요 기능

<br/>

- Deployment vs Statefulset vs Daemonset 
  - https://youtu.be/30KAInyvY_o?si=GW9tbgiZeuG48bVv

<br/>

- Node Selector vs Node Affinity
  - https://youtu.be/rX4v_L0k4Hc?si=iRGq9gjitRoFmy04  


<br/>

- Ingress   
  -  https://youtu.be/1BksUVJ1f5M?si=01rbXov20XJdEPJh

<br/>

- Headless 서비스  
  - https://youtu.be/If03sN4isO4?si=sn43EXafiVFJ7JLH


<br/>
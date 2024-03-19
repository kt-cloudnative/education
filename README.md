# Cloud Native 역량 강화 교육


<br/>

본 교육 과정은 Cloud Native Evangelist 양성 과정으로 기본 개념과 실습 그리고 OJT 진행을 하며 직접 설치부터 설정 및 활용까지 수행한다.     

문의 :  이석환 ( seokhwan.lee@kt.com / shclub@gmail.com )

<br/>


1. Chapter 1 : 1주차    ( [가이드 문서보기](./chapter1.md) ) 

     - SpringBoot 개념 설명 
     - IDE 개발 환경 구성  ( Hello World)
     - SpringBoot 전체 hands-on [ Hands-On 문서보기 ](./springboot_hands_on.md)   

          - 뷰 템플릿 과 MVC 패턴
          - JDBC vs JPA vs Mybatis vs Spring Data JDBC 비교
          - Spring Data JPA Hands-on 
          - Rest API 와 JSON
          - HTTP 와 Rest Controller
          - 서비스와 트랜잭션, 그리고 롤백
          - Spring MyBatis Hands-on
          - Spring Data JDBC Hands-on
          - 테스트 작성하기
          - 댓글 서비스 만들기

     - SpringBoot Data JPA hands-on [ Hands-On 문서보기 ](./springboot_hands_on_jpa.md)  

          - 데이터 생성 with JPA
          - 롬복과 리팩토링
          - 데이터 조회 , 수정 및 삭제 with JPA
          - CRUD 와 SQL Query
          - QueryDSL 사용
     
     <br/>

     - 샘플 소스 ( Web ): [ 소스 보기 ](https://github.com/kt-cloudnative/springboot_jpa_1)  
     - 샘플 소스( Rest ) : [ 소스 보기 ](https://github.com/kt-cloudnative/springboot_jpa_2)

     <br/>

     - Spring Mybatis Hands-on [ Hands-On 문서보기 ](./springboot_hands_on_mybatis.md)  

          -  프로젝트 생성 및 환경 설정
          -  프로젝트 구성하기 
          -  실행해 보기
          -  SQL문 로그 보기 ( log4jdbc 설정 )
          -  Swagger 설정 ( 3.0 )  

     <br/>

     - 샘플 소스 ( mybatis ): [ 소스 보기 ](https://github.com/kt-cloudnative/springboot_mybatis_1)  

     - 샘플 소스 ( mybatis / log4j2 / OpenApi ): [ 소스 보기 ](https://github.com/kt-cloudnative/springboot_mybatis_2)


     <br/>

     - Spring Data JDBC Hands-on [ Hands-On 문서보기 ](./springboot_hands_on_spring_data_jdbc.md) 
  
          -  Spring Data JPA vs Spring Data JDBC 
          -  프로젝트 구성하기 ( CRUD , Paging & Sorting )
          -  실행해 보기
          -  로컬 캐쉬 ( Caffeine Cache ) 사용하기 

     - 샘플 소스 ( Spring Data Jdbc :  ): [ 실습 소스 보기 ](https://github.com/kt-cloudnative/springboot_data_jdbc_1)
     - 샘플 소스 ( Spring Data Jdbc ): [ 전체 소스 보기 ( CUD 포함) ](https://github.com/kt-cloudnative/springboot_data_jdbc_2)

     
<br/>

2. Chapter 2 : 2주차  

     - SpringBoot 계속

          -  Spring Security 5.x [ Hands-On 문서보기 ](./spring_security_1.md)    
               - JWT  연동
               - Frontend ( React )  example 

          -  Spring Security 6.x [ Hands-On 문서보기 ](./spring_security_2.md)    
               - JWT
               - Frontend ( Vue )  example 
               - 과제 : JWT -> OAuth 연동 전환

          -  IoC 와 DI
          -  AOP
          -  Object Mapper   
          -  PSA ( Portal Service Abstraction ) 
 
     <br/>

     - CRUD Without Security

          - 샘플 소스 ( React Front without security ): [ 소스 보기 ](https://github.com/kt-cloudnative/react_crud_simple)  

          - 샘플 소스 ( SpringBoot without security ): [ 소스 보기 ](https://github.com/kt-cloudnative/springboot_crud_simple)

     <br/>

     - CRUD With Security

          - 샘플 소스 ( React Front with security ): [ 소스 보기 ](https://github.com/kt-cloudnative/react_crud_security)

          - 샘플 소스 ( Vue Front with security ): [ 소스 보기 ](https://github.com/kt-cloudnative/vue_crud_security)

          - 샘플 소스 ( SpringBoot with security ): [ 소스 보기  ](https://github.com/kt-cloudnative/springboot_crud_security)

     <br/>


3. Chapter 3 : 3주차  ( [가이드 문서보기](./chapter3.md) ) 

     - 실습 환경 구성 ( [환경](./install.md) )
     - Git 
     - Docker  
     - Swagger 실습 
     - Docker Compose 설치 및 활용 ( DB 연동 )  
     - 샘플 소스 : [ 소스 보기 ](https://github.com/kt-cloudnative/edu2)  

     <br/>

4. Chapter 4 : 3주차  ( [가이드 문서보기](./chapter4.md) )  

     - VM 기반으로 Jenkins 설치 및 설정 , GitHub , Docker 계정 생성 , Jenkins Pipeline 생성하여 CI 실습  
     - 샘플 소스 : [ 소스 보기 ](https://github.com/kt-cloudnative/edu1)    
     - Jenkins 설치 : ( [가이드 문서보기](./jenkins_install.md) )  

    <br/>

5. Chapter 5 : 3주차  ( [가이드 문서보기](./private_docker_registry.md) )  

     - Private Docker Registry 구성 
     - Private Docker Registry 설명 및 구축 하기 ( /w Nexus )
     - Remote 로 연결하기 ( Insecure Registry 설정 )
     
     <br/>
     
     참고 : kt cloud 디스크 증설 및 DataDog 연동  ( [가이드 문서보기](./kt_cloud_datadog.md) )  

<br/>

6. Chapter 6 : 3주차   ( [가이드 문서보기](./chapter5.md) )  

     - kubernetes 설치 (k3s) 및 설정 , k8s 이해 및 활용
     - kubernetes IDE 인 Lens 설치 및 사용법 실습   
     - Helm 설치 및 helm으로 prometheus 설치 활용
     - k8s hands-on Basic [ Hands-On 문서보기 ](./k8s_basic_hands_on.md)  

          - 실습 전체 개요
          - kubeconfig 설정 : kubectl 설치
          - kubectl 활용
          - kubernetes 리소스 ( Pod , Service , Deployment 생성 및 삭제)
          - 배포 ( Rolling Update / Rollback )
          - Serivce Expose ( Ingress )  
     
     <br/>

     - k8s hands-on Middle [ Hands-On 문서보기 ](./k8s_middle_hands_on_2023.md)  
          - Docker Hub Rate Limit
          - Helm
          - Kubernetes의 Node Scheduling
          - Storage Volume ( PV/PVC , DB 설치 + NFS )
          - MariaDB NFS 에 설치 ( /w Helm Chart )
          - Service - Headless, Endpoint, ExternalName
          - Daemonset , Job , CronJob
          - configMap , Secret
          - NFS 라이브러리 설치 ( Native Kubernetes ) 

     <br/>


7. Chapter 7 : 3주차   ( [가이드 문서보기](./chapter6.md) ) 

     - GitOps 설명 
     - ArgoCD 설치 및 설정 
     - kustomize 설명 및 실습
     - k8s에 배포 실습 ( Blue/Green , Canary )  
     - ArgoCD Hands-on [ Hands-On 문서보기 ](./argocd_hands_on.md) 

          - kubectl plugin 설치
          - Blue/Green 배포
          - Canary 배포
          - ArgoCD 계정 추가 및 권한 할당
          - kustomize 사용법
          - ArgoCD remote Cluster 에서 배포 하기 

     <br/>

8. Chapter 8 : 3주차  ( [가이드 문서보기](./chapter8.md) )
     
     - Podman 소개
     - Jib ( Skaffold ) 소개
     - React/SpringBoot CI/CD 설명 및 실습
          - 언어별(React/SpringBoot) Build Tool 생성 
          - Gitops Repository 구성 
          - 언어별 (React/SpringBoot) CI Pipeline 구성 및 빌드 (/W CORS) 
          - ArgoCD 배포
     - React/SpringBoot/MariaDB 3-tier 구조 한번에 배포 하기
          - ArgoCD Apps-of-Apps 패턴 

9. Chapter 9 : 4주차   ( [가이드 문서보기](./airflow.md) ) 
     - SpringBatch 소개 및 실습
     - Apache Airflow 소개 및 실습

10. Chapter 10 : 4주차   ( [가이드 문서보기](./kafka.md) ) 
     - Kafka 소개 및 실습

11. Chapter 11 : 4주차   ( [가이드 문서보기](./redis.md) ) 
     - Redis 소개 및 실습
     
12. Chapter 12 : 4주차   ( [가이드 문서보기](./keycloak.md) ) 

     - KeyCloak 설치 및 실습 
     - keyCloak 를 이용한 오픈 소스 시스템 연동 ( Jenkins / ArgoCD , Airflow , Kibana 등 )  
     - SpringBoot Backend 연동  

13. Chapter 13 : 5주차   ( [가이드 문서보기](./k8s_security.md) ) 

    - 보안 component
    - K8S Security Overview  
    - Network Policy  
    - Route 에 SSL 인증서 설정     
    - User Account vs Service Account  
    - Krew 설명 및 설치  
    - Kubernetes 보안 Components  
    - Security Context
    - Pod Security Policy ( PSP )  


14. Chapter 14 : 5주차   ( [가이드 문서보기](./k8s_observability.md) )
    - Observability 소개

15. Charpter 15 : 6주차 ( [가이드 문서보기](./k8s_prometheus.md) )
    - Prometheus 설명 및 실습
    - prometheus & Thanos 설치 ( [가이드 문서보기](./prometheus.md) )

16. Charpter 16 : 6주차 ( MSA패턴 1 )
    - krirobo77 사이트: [참고](https://github.com/kirobo77) 
    - D.D.D ( [가이드 문서보기](./ddd.md) )
    - CQRS 패턴 ( [가이드 문서보기](./cqrs.md) )
      - 소스 샘플 : https://github.com/kt-cloudnative/cqrs
    - Cleancode ( [가이드 문서보기](./cleancode.md) )
    - Repository Pattern ( [가이드 문서보기](./repository_pattern.md) )
    - Spring Cloud Gateway ( [가이드 문서보기](./APIGateway.md) )
      - 소스 샘플 : https://github.com/kt-cloudnative/scg
    - Spring MVC vs WebFlux
    

17. Charpter 17 : 7주차 (  MSA 패턴 2 )

    - Cache 패턴 ( [가이드 문서보기](./cache-patterns.md) ) 
      - 소스 샘플 : https://github.com/kt-cloudnative/cache
    - CircuitBreaker 패턴 ( Resillience4J )
    - Async 패턴
    - Openfeign 
    - Service Mesh 패턴 ( [가이드 문서보기](./service_mesh_patterns.md) ) 


18. Chapter 18: 8주차 ( Observability )

     - Opensearch ( [가이드 문서보기](./opensearch.md) ) 
     - Elastic stack ( [가이드 문서보기](./elastic_stack.md) ) 


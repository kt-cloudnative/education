# Cloud Native 역량 강화 교육

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

          -  Spring Security [ Hands-On 문서보기 ](./spring_security.md)    
               - JWT / OAuth 연동
               - Frontend ( Vue / React ) simple example 


          -  IoC 와 DI
          -  AOP
          -  Object Mapper   
          -  PSA ( Portal Service Abstraction ) 
 
     <br/>

     - CRUD Without Security

          - 샘플 소스 ( React Front without security ): [ 소스 보기 ](https://github.com/kt-cloudnative/react_crud_simple)  

          - 샘플 소스 ( SpringBoot  without security ): [ 소스 보기 ](https://github.com/kt-cloudnative/springboot_crud_simple)

     <br/>

     - CRUD With Security

          - 샘플 소스 ( React Front with security ): [ 소스 보기 ](https://github.com/kt-cloudnative/react_crud_token)

          - 샘플 소스 ( Vue Front with security ): [ 소스 보기 ](https://github.com/kt-cloudnative/vue_crud_token)

          - 샘플 소스 ( SpringBoot with security ): [ 소스 보기  ](https://github.com/kt-cloudnative/springboot_crud_token)

     <br/>



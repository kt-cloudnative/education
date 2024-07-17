# Chapter 19 

 Azure 기본 과정의 다음 단계로 다양한 Azure 상품을 사용해 보도록 합니다.  

<br/>

5. Eventhub 구성  및 테스트 하기 

6. Azure cache for Redis 구성

7. Azure DB for MySQL 구성 및 접속하기 

8. Full Stack Application 배포 해 보기 

<br/>

## 전체 구성

<br/>

우리가 구현하고자 하는 구성은 아래와 같다.  

<img src="./assets/azure_full_app_architecture.png" style="width: 80%; height: auto;"/>  

<br>

## 5. Eventhub 구성  및 테스트 하기 


<br/>

Eventhub는 Azure의 MessageBroker Managed 서비스 입니다.
kafka 는 아니지만 Kafka 라이브러리를 호환을 하기 때문에 기존 소스 변경 없이 config만 변경하면 그대로 사용이 가능 합니다.

<br/>

### EventHub 설정

<br/>

portal 에서 Eventhub로 검색을 합니다.  

<br/>

<img src="./assets/azr_eventhub_1.png" style="width: 60%; height: auto;"/>

<br/>

리소스를 설정을 하고  namespace 를 설정합니다.  
- namespace는 kafka의 경우 cluster를 말하며 private Cloud 에서는 주로 3개 Node로 구성합니다.    
- price plan은 Basic 이 아닌 Standard를 선택합니다. Standard 이상에서만 kafka 라이브러리 호환이 지원 됩니다.  

<img src="./assets/azr_eventhub_2.png" style="width: 60%; height: auto;"/>

<br/>

local Authentification은 `Enabled` 로 설정해야 합니다.  

<img src="./assets/azr_eventhub_3.png" style="width: 60%; height: auto;"/>

<br/>

네트웍 설정은 Public 으로 설정합니다.  

<img src="./assets/azr_eventhub_4.png" style="width: 60%; height: auto;"/>

<br/>

설정을 확인합니다.  

<img src="./assets/azr_eventhub_5.png" style="width: 60%; height: auto;"/>

<br/>

Deployment 가 완료 되면 Go To Resource 를 클릭합니다.  

<img src="./assets/azr_eventhub_7.png" style="width: 60%; height: auto;"/>

<br/>

테스트 데이터를 생성 할수 있다는 메시지가 보이고 `Basic Plan` 을 선택했다면 
`KAFKA SURFACE` 는 `NOT SUPPORTED` 로  설정이되고  그 이상이면  `SUPPORTED` 로 설정됩니다.  

<img src="./assets/azr_eventhub_8.png" style="width: 60%; height: auto;"/>

<br/>

`Entities` -> `Event Hub` 로 이동하여 `+ Event Hub` 를 클릭하고 TOPIC 을 생성합니다.      

<img src="./assets/azr_eventhub_9.png" style="width: 60%; height: auto;"/>

<br/>

TOPIC 은 `ppon` 이름으로  partition 은 1로 설정하고 생성합니다.      

<img src="./assets/azr_eventhub_10.png" style="width: 60%; height: auto;"/>

<br/>

사양을 확인합니다. 현재 요즘제에서는 데이터 캡쳐는 불가능 합니다.  

<img src="./assets/azr_eventhub_11.png" style="width: 60%; height: auto;"/>

<br/>

topic을 생성하고 Generate Data 를 선택을 하면 아래 화면이 나오고 생성한  topic을 설정합니다.    

아래의 send 버튼을  클릭하면 JSON 데이터가 topic에 publish 됩니다.  

<img src="./assets/azr_eventhub_12.png" style="width: 60%; height: auto;"/>

<br/>

event body에 JSON 형태의 데이터를 볼 수 있다.  

<img src="./assets/azr_eventhub_12_1.png" style="width: 60%; height: auto;"/>

<br/>

overview 에서 metric 정보를 보면 데이터가 하나 들어 간 것을 볼 수 있습니다.  


<img src="./assets/azr_eventhub_12_2.png" style="width: 60%; height: auto;"/>

<br/>

namespace 나 topic에 권한을 할당 하기 위해서는  
Settings -> `Shared access policies` 로 이동하여 `+ Add` 버튼을 클릭하고 SAS Policy 를 추가합니다.  

<img src="./assets/azr_eventhub_13.png" style="width: 60%; height: auto;"/>

<br/>

`topicAccesskey` 라는 이름으로 생성이 되었고 Send, Listen 기능이 있을 것을 확인 할 수 있습니다. 

<img src="./assets/azr_eventhub_14.png" style="width: 40%; height: auto;"/>

<br/>


`topicAccesskey` 라는 이름으로 생성이 되었고 Send, Listen 기능이 있을 것을 확인 할 수 있습니다. 

<img src="./assets/azr_eventhub_14.png" style="width: 60%; height: auto;"/>

<br/>


## 6. Azure cache for Redis 구성

<br/>

Azure cache for Redis 는 오픈소스인 Redis 이고 사용 방법은 기존 Redis 와 동일 합니다.  

<br/>

### Azure Redis Cluster 구성하기 

<br/>

Create Redis Cache를 클릭한다.  

<img src="./assets/azure_redis1.png" style="width: 60%; height: auto;"/>

<br/>

DNS 이름을 설정하고 Cache Size 를 가장 작은 값을 설정한다. 
- DNS 이름은 Redis Cluster의 이름이다.   

<img src="./assets/azure_redis2.png" style="width: 60%; height: auto;"/>

<br/>

Public 으로 Network을 오픈한다.  

<img src="./assets/azure_redis3.png" style="width: 60%; height: auto;"/>

<br/>

TLS 설정은 하지 않고 Entra 인증도 하지 않는다.    
Redis 버전은 최신인 6.x를 선택한다.  

<img src="./assets/azure_redis4.png" style="width: 60%; height: auto;"/>

<br/>

사양을 확인한다.

<img src="./assets/azure_redis5.png" style="width: 60%; height: auto;"/>

<br/>

생성을 하는데 상당한 시간이 소요됩니다.  

<img src="./assets/azure_redis6.png" style="width: 60%; height: auto;"/>

<br/>

생성이 완료 되면  hostname 과 keys를 확인합니다.  

<img src="./assets/azure_redis8.png" style="width: 60%; height: auto;"/>

<br/>

Cached Key 에서 Primary 값을 복사합니다.  

<img src="./assets/azure_redis11.png" style="width: 60%; height: auto;"/>


<br/>


Advanced 설정을 확인 합니다.  

<img src="./assets/azure_redis9.png" style="width: 60%; height: auto;"/>

<br/>

Medis 어플로 연결 설정을 한다.    
현재 price plan 으로 redis를 설정하면 standalone 이고 premium tier 이상을 해야 cluster type 설정이 가능하다.  

<img src="./assets/azure_redis12.png" style="width: 60%; height: auto;"/>

<br/>

connect 하면 현재 redis cache에 저장된 데이터를 확인 할 수 있다.  

<img src="./assets/azure_redis13.png" style="width: 60%; height: auto;"/>

<br/>

## 7. Azure DB for MySQL 구성 및 접속하기 

<br/>

Azure DB는 다양한 Database를 지원하며 우리가 사용할 수 있는 DB는 Postgres , MySQL , MS SQL 이다. ( Mariadb는 지원 안함 )   
mysql은 8.x 이상을 지원 하고 있고 기본적으로 테이블 이름은 소문자로 생성이 되고 Azure 에서는  별도 설정이 불가능 하다. 

<br/>

### MySQL 구성하기 

<br/>

portal 에서 mysql 를 검색하고 선택을 합니다.  

<img src="./assets/azure_mysql_1.png" style="width: 60%; height: auto;"/>

<br/>

Flexible Server 를 선택하고 Create 버튼을 클릭합니다.  

<img src="./assets/azure_mysql_2.png" style="width: 60%; height: auto;"/>

<br/>

리소스 그룹과 서버이름을 입력하고 region을 선택합니다. 사이즈는 기본 값을 설정합니다. ( korea south는 사용 불가)  

<img src="./assets/azure_mysql_3.png" style="width: 60%; height: auto;"/>

<br/>

아래와 같이 설정하고 admin 계정과 비밀번호를 설정합니다.  

<img src="./assets/azure_mysql_4.png" style="width: 60%; height: auto;"/>

<br/>

Public access를 모두 허용합니다.  

<img src="./assets/azure_mysql_5.png" style="width: 60%; height: auto;"/>

<br/>

security key는 그대로 두고 넘어갑니다.  

<img src="./assets/azure_mysql_6.png" style="width: 60%; height: auto;"/>

<br/>

사양을 확인하고 생성합니다.    

<img src="./assets/azure_mysql_7.png" style="width: 60%; height: auto;"/>

<br/>

설치가 진행중이고 모든 것을 설치를 해도 credit 50%는 남아 있는 것을 확인 할 수 있습니다.  

<img src="./assets/azure_mysql_8.png" style="width: 60%; height: auto;"/>

<br/>

정상적으로 생성이 되면 host 이름과 사용자 이름을 확인 할 수 있고 외부에서 접속을 위해 host 이름을 복사하여 저장합니다.  

<img src="./assets/azure_mysql_9.png" style="width: 60%; height: auto;"/>

<br/>

MySQL 이 생성이 된 이후 DBeaver 같은 Tool을 통해 접속을 하기 위해서는 SSL을 off 해야 합니다.   

Server parameters -> All을 선택한 후 ssl로 검색을 하면 require_secure_transport 이 보이고 OFF로 변경하고 저장합니다.  

시간이 경과되고 서버 파라미터가 설정이 됩니다.   

<img src="./assets/azure_mysql_10.png" style="width: 60%; height: auto;"/>

<br/>

Settings -> Connect 로 가면 접속 하는 방법이 변경된 것을 확인 할 수 있습니다.  

<img src="./assets/azure_mysql_11.png" style="width: 60%; height: auto;"/>

<br/>

Cloud Shell 로 아래 처럼 접속을 해봅니다.    

```bash
mysql -h icistr1.mysql.database.azure.com -u kt_admin -p
```  
<br/>

<img src="./assets/azure_mysql_12.png" style="width: 60%; height: auto;"/>

<br/>


<br/>

### DB 구성하기 

<br/>

이제 실습을 하기 위한 DB를 생성해 봅니다.    
Settings -> Databases 로 이동하여 Add 버튼을 클릭합니다.  

<img src="./assets/azure_mysql_13.png" style="width: 60%; height: auto;"/>

<br/>

baseinfo 라는 이름으로 설정하고 characterset 은 한글이 깨지지 않기 위하여 아래와 같이 설정합니다.  

<img src="./assets/azure_mysql_14.png" style="width: 60%; height: auto;"/>

<br/>

DB 가 생성이 된 것을 확인 할수 있습니다. 

<img src="./assets/azure_mysql_15.png" style="width: 60%; height: auto;"/>

<br/>

이제 DB Tool 인 DBeaver 로 connection을 생성하고 Test Connection 버튼을 클릭합니다.

<img src="./assets/azure_mysql_16.png" style="width: 40%; height: auto;"/>

<br/>

정상적으로 접속 된 것을 확인합니다.

<img src="./assets/azure_mysql_17.png" style="width: 30%; height: auto;"/>

<br/>

추가로 ppon 이름으로 DB를 하나 더 생성합니다.  

<br/>

`baseinfo` DB에 table 을 생성 하도록 데이터를 입력한다.    
- 아래는 mariadb 용이고 Mysql 로 변경 필요    

<br/>

```bash
CREATE TABLE BI_CS_CUST_BAS (
  CUST_ID varchar(11) NOT NULL,
  SYS_TRTR_ID varchar(15) NOT NULL,
  SYS_TRT_ORG_ID varchar(15) DEFAULT NULL,
  SYS_SVC_ID varchar(50) DEFAULT NULL,
  SYS_COMP_ID varchar(50) DEFAULT NULL,
  SYS_RECD_CRET_DT TIMESTAMP NOT NULL,
  SYS_RECD_CHG_DT TIMESTAMP DEFAULT NULL,
  CUST_NM varchar(60) NOT NULL,
  CUST_IDFY_NO varchar(20) NOT NULL,
  CUST_IDFY_NO_TYPE_CD varchar(30) NOT NULL,
  CUST_TYPE_CD varchar(3) NOT NULL,
  CUST_PTCL_TYPE_CD varchar(2) DEFAULT NULL,
  SALE_CMPN_ID varchar(11) NOT NULL,
  DGT3_CNTRY_CD varchar(3) DEFAULT NULL,
  DUPL_CUST_YN varchar(1) NOT NULL,
  DUPL_CUST_WHY_CD varchar(1) DEFAULT NULL,
  PERSON_SEX_DIV_CD varchar(3) DEFAULT NULL,
  REG_ORG_ID varchar(11) DEFAULT NULL,
  CUST_STTUS_TYPE_CD varchar(1) DEFAULT NULL,
  CUST_STTUS_EFCT_ST_DATE TIMESTAMP DEFAULT NULL,
  IPIN_CI varchar(88) DEFAULT NULL,
  IPIN_DI varchar(64) DEFAULT NULL,
  ICIS_CUST_ID varchar(11) DEFAULT NULL,
  NSTEP_CUST_ID decimal(9,0) DEFAULT NULL,
  RNM_ATHN_YN varchar(1) DEFAULT NULL,
  EXP_DATE TIMESTAMP DEFAULT NULL,
  IF_EXPT_YN varchar(1) DEFAULT NULL,
  BOCU_ID varchar(11) DEFAULT NULL,
  SYNC_HST_NO decimal(38,0) DEFAULT NULL,
  SYNC_TRIGR_CRET_DT TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
  SYNC_TRT_DT_VAL varchar(17) DEFAULT NULL,
  CONSTRAINT BI_CS_CUST_BAS_PK PRIMARY KEY (CUST_ID)
);
CREATE INDEX BI_CS_CUST_BAS_IX01 ON BI_CS_CUST_BAS (CUST_NM,CUST_IDFY_NO_TYPE_CD,CUST_IDFY_NO);
```  

<br/>

```bash
INSERT INTO BI_CS_CUST_BAS (CUST_ID,SYS_TRTR_ID,SYS_TRT_ORG_ID,SYS_SVC_ID,SYS_COMP_ID,SYS_RECD_CRET_DT,SYS_RECD_CHG_DT,CUST_NM,CUST_IDFY_NO,CUST_IDFY_NO_TYPE_CD,CUST_TYPE_CD,CUST_PTCL_TYPE_CD,SALE_CMPN_ID,DGT3_CNTRY_CD,DUPL_CUST_YN,DUPL_CUST_WHY_CD,PERSON_SEX_DIV_CD,REG_ORG_ID,CUST_STTUS_TYPE_CD,CUST_STTUS_EFCT_ST_DATE,IPIN_CI,IPIN_DI,ICIS_CUST_ID,NSTEP_CUST_ID,RNM_ATHN_YN,EXP_DATE,IF_EXPT_YN,BOCU_ID,SYNC_HST_NO,SYNC_TRIGR_CRET_DT,SYNC_TRT_DT_VAL) VALUES
	 ('0AAAD87HNY8','91162614','AT12000','StrgCustIntgInfoSOstrgCustIntgInfo','service','2017-05-23 17:58:07.0','2017-05-23 17:59:06.0','김형목','0011153V01135','1','1','N1','KT',NULL,'N',NULL,NULL,NULL,'3','2017-05-23 00:00:00.0','C+EvhJxOQnBVn1IJoKSKCNz2j3HGLrwtaAVRLEDmNcGBrMSLqsFvEjzQ==','MC0GCCqGSIb3DQIJAyEAgPxkcUlFyc1zP1tFFXdDA9W9rTYsoY3GlA8m1oxKYow=','3003973716G',636283878,'Y','9999-12-31 00:00:00.0',NULL,NULL,NULL,NULL,NULL),
	 ('0DDUG8TIDKI','91141722','YC20405','cdmDom03Svr63','RnmAthnIfResltStoreSO','2021-02-22 14:26:05.0','2021-02-22 14:27:29.0','김로운','1406133V00245','1','1','N1','KT',NULL,'N',NULL,NULL,NULL,'3','2021-02-22 14:26:05.0','u/vPIPWsINVUA1mFgEWyr1s3cOLV4ZXNx6MVH55Ux+YVgKtnj4JQGJVNG5AyO4+cmxvA==','MC0GCCqGSIb3DQIJAyEAENjsJgUD4NWHDQmzyR9ODRrWtkPZUX229EI9MlSmOJI=','3013843607G',645376666,'Y','9999-12-31 00:00:00.0',NULL,NULL,NULL,NULL,NULL),
	 ('11111111111','12345','SPT8050','cdmcorpDom01Svr21','RnmAthnIfResltStoreSO','2021-08-30 19:29:00.0','2021-08-30 19:29:06.0','MVP교육생','6201251V01758','1','1','N1','KT','KOR','N',NULL,NULL,NULL,'3','2021-08-30 19:29:00.0','cQ/Z4Eao91nuTz1oNVftXT75T1iUh6DE3tM9DU15dBcCzh/3KhUTtzoDbSnKbdr3t26ZsCEVO3QTfsLAKfNIKQ==','MC0GCCqGSIb3DQIJAyEA+kQTyIg/K60ogzFyfGrUHip5WawqpIHHp7umPfBGbQ4=','3015037604G',646453989,'Y','9999-12-31 00:00:00.0',NULL,NULL,NULL,NULL,NULL),
	 ('8REKZGJGPCX','MIG',NULL,NULL,NULL,'2007-08-11 05:04:44.0','2007-08-11 05:04:44.0','류인구','6510101V00971','1','1','N1','KT','KOR','N',NULL,NULL,NULL,'1','1988-06-16 00:00:00.0','QkbvKbmWfg/nwESR6yetERKGJirPcTDrGLu4ZbJUfiGUsPyBshHh4IIUX/C9GC2FppHDdCk+T3tLqvfl3q8eDA==','MC0GCCqGSIb3DQIJAyEA7s5FXB3WiAuk7WJX8G9UIp2/eZK6BtgppzZU8vJ7y5g=','00102350354',NULL,'Y',NULL,NULL,NULL,NULL,NULL,NULL),
	 ('8T1KZAXGNOQ','10063789','412866',NULL,NULL,'2006-03-13 10:07:23.0','2009-02-23 01:51:29.0','최동진','5909281V00703','1','1','N1','KT','KOR','N',NULL,'1','412866','3','2009-02-23 00:00:00.0','fD7KAZJDIjf1EYKfabT+AHAhVKt6U6qOx3/w8301dZU/IgaMGfzoTuuyR8kTi04v91b+7QLsj44o4374TMZbBQ==','MC0GCCqGSIb3DQIJAyEATb25QSwmCo8R1TQAsLjgzadTne0jkc+AXaLuQdFbNFo=','0002586214C',NULL,'Y',NULL,NULL,NULL,NULL,NULL,NULL),
	 ('8XHL719K2Q9','200363693','C10004','WISE.CUSTOMER','CRIS_MIG','2002-10-01 11:34:26.0','2010-11-11 17:41:08.0','김정자','4107092V00129','1','1','N1','KT','KOR','N',NULL,'2','C10004','1','1978-12-17 00:00:00.0','1vQ/nXeh/gLP6+U7V5LGiBavHs0utclHmzv/gnUBuNpyfgOP4O2RdyMCTJ8RHM0+NMJpp6ElombtNPZNCbn0xQ==','MC0GCCqGSIb3DQIJAyEAafe4POwsfKeOLY4fl8WrD+IjwKnSe8kJYQs5iovfUEU=','00111406985',330814912,'Y','9999-12-31 00:00:00.0',NULL,NULL,NULL,NULL,NULL),
	 ('9ITL76GGM3O','91094288','AA95033','StrgCustIntgInfoSOstrgCustIntgInfo','service','2004-02-14 15:58:20.0','2016-03-14 13:59:56.0','이한열','6007101V00563','1','1','N1','KT',NULL,'N',NULL,NULL,NULL,'1','2012-09-08 00:00:00.0','ZKnfj8tHPMP7lM1YTONXbDqynwQm0F7LLA0BBhZ3dAHmWafYMZqX9TD1RJyAOdsdzOpQSQ2Yk5Rowed9iPHB6w==','MC0GCCqGSIb3DQIJAyEAroazScpf8pb7WyxxsN1TqC3YntZYi9Zv96xnpkf+PG0=','0002632777C',624446011,'Y','9999-12-31 00:00:00.0',NULL,NULL,NULL,NULL,NULL),
	 ('BHTL73TGOHU','MIG',NULL,NULL,NULL,'2007-08-11 05:04:44.0','2007-08-11 05:04:44.0','김여방','3205261V00117','1','1','N1','KT','KOR','N',NULL,NULL,NULL,'1','1976-07-07 00:00:00.0','/X3RzaGaJkQzlNGEoS7MhhyyHNx2aD3YUlueQ6vCUX8MuOHOOtkDmljEQq2gWKwT8/Q+YhrXQpx+uK/t1QbibQ==','MC0GCCqGSIb3DQIJAyEA3SXw4AN+0w/bOnetSMlWqc/IDqbJ9G2ewx4r2kMlmdY=','00115376495',NULL,'Y',NULL,NULL,NULL,NULL,NULL,NULL),
	 ('BP4L7BULBHH','91234832','IA00028','RnmAthnIfResltStoreSO','service','1998-12-12 17:40:18.0','2019-07-31 11:22:27.0','김규리','6101202V02118','1','1','N1','KT',NULL,'N',NULL,'2','H00046','2','2012-04-27 00:00:00.0','veYIwB5/psTETiRHo72lZ5ItnjxBJe1UMjLNe+Fk5bgogCGhUH+plbX5KapkHBgJiJmXpyRO8PqBmImA7V410g==','MC0GCCqGSIb3DQIJAyEAuTbquYwBPH9oiTA47UNMI1Lju2IbEMhHm+T/B6q2lZ0=','0007866738C',227304858,'Y','9999-12-31 00:00:00.0',NULL,NULL,NULL,NULL,NULL),
	 ('BVKKZUEGH0V','MIG',NULL,NULL,'DR-2017-05850:청구계정BIZR고객 복구(I)','2007-08-11 04:57:20.0','2017-02-12 22:33:52.0','새동락식당','4102518092','7','2','N1','KT','KOR','Y',NULL,NULL,NULL,'3','2007-08-11 00:00:00.0',NULL,NULL,'0004754751C',NULL,NULL,NULL,'N',NULL,NULL,NULL,NULL);
```    

<br/>

```bash
CREATE TABLE BI_CSYSCD (
	GRP_ID VARCHAR(4) NOT NULL,
	CD VARCHAR(7) NOT NULL,
	NAME VARCHAR(50) NOT NULL,
	OUTPUT_SEQ NUMBER,
	REF1 VARCHAR(10),
	REF2 VARCHAR(20),
	REF3 NUMBER,
	REMARK VARCHAR(120),
	REG_DATE DATE,
	REG_OFC_CD VARCHAR(6),
	REGER_EMP_NO VARCHAR(9),
	REGER_EMP_NAME VARCHAR(10),
	DEFINE_NAME VARCHAR(80),
	END_EMP_NO VARCHAR(9),
	END_EMP_NAME VARCHAR(10),
	END_DATE VARCHAR(8),
	START_DATE VARCHAR(8),
	REF4 VARCHAR(20),
	REF5 VARCHAR(20),
	CONSTRAINT BI_CSYSCD_PK PRIMARY KEY (GRP_ID,CD)
);
CREATE INDEX BI_CSYSCD_IX01 ON BI_CSYSCD (GRP_ID,CD,NAME);
```  

<br/>

`ppon` DB에 table 을 생성 하도록 데이터를 입력한다.    
- 아래는 mariadb 용이고 Mysql 로 변경 필요    

```bash
CREATE TABLE PP_CORDORDINFO (
	ORD_NO VARCHAR(15) NOT NULL,
	SA_ID VARCHAR(11) DEFAULT NULL,
	SA_CD VARCHAR(4) DEFAULT NULL,
	SVC_TYPE_CD VARCHAR(2) DEFAULT NULL,
	TERM_CNTRCT_TYPE VARCHAR(1),
	KT_USE_FLAG VARCHAR(1),
	SVC_NO VARCHAR(20),
	ORD_MNG_NO VARCHAR(10),
	SVC_OFC_CD VARCHAR(9),
	EXCH_OFC_CD VARCHAR(6),
	INSTL_MNG_OFC_CD VARCHAR(6),
	CUST_ID VARCHAR(11),
	CUST_NAME VARCHAR(50),
	CUST_NO_TYPE VARCHAR(1),
	CUST_NO VARCHAR(20),
	USER_ORD_TYPE_CD VARCHAR(2) ,
	ORD_TYPE_CD VARCHAR(2) ,
	ORD_TYPE_CD_1 VARCHAR(2),
	ORD_TYPE_CD_2 VARCHAR(2),
	ORG_ORD_TYPE_CD VARCHAR(2),
	ORD_RSN_CD VARCHAR(2),
	ORD_STATUS_CD VARCHAR(2) ,
	TRAN_TYPE_CD VARCHAR(2),
	REQER_NAME VARCHAR(50),
	CUST_REL_CD VARCHAR(2),
	COMPLETED_RESV_DATE_HH VARCHAR(10),
	CONST_DATE VARCHAR(8),
	COMPLETED_DATE_HH VARCHAR(10),
	WORK_DELAY_RSN_CD VARCHAR(4),
	L_WORK_DELAY_RSN_CD VARCHAR(4),
	TEST_DD_CNT NUMBER(3,0),
	CONST_RESV_DATE VARCHAR(8),
	P_INSTL_SVC_NO VARCHAR(20),
	REUSE_SVC_NO_FLAG VARCHAR(1),
	REUSE_SVC_NO VARCHAR(16),
	IA_ADDR_FLAG VARCHAR(1),
	CNTC_MEDIA_CD VARCHAR(2),
	CNTC_TEL_NO VARCHAR(20),
	CNTC_TEL_NO_1 VARCHAR(12),
	CNTC_EMAIL_ADDR VARCHAR(50),
	CNTC_ADDR VARCHAR(100),
	"REF" VARCHAR(100),
	ADDR_TEXT VARCHAR(100),
	SVC_CNT NUMBER,
	CNTC_CUST_NO VARCHAR(13),
	PAUSE_SA_ID VARCHAR(11),
	RCV_OFC_CD VARCHAR(9),
	RCV_DATE VARCHAR(8) ,
	RCVER_EMP_NO VARCHAR(9),
	RCVER_EMP_NAME VARCHAR(50),
	RCVER_EMP_TEL_NO VARCHAR(12),
	REG_DATE DATE DEFAULT sysdate ,
	LEGACY_ORD_NO VARCHAR(20),
	REUSE_SVC_CD VARCHAR(1),
	COMPLETE_FLAG VARCHAR(1) DEFAULT '0' NOT NULL,
	CANCEL_RSN_CD VARCHAR(2),
	CANCEL_REF VARCHAR(100),
	CANCEL_REGER_NAME VARCHAR(50),
	CSR_NOTE VARCHAR(100),
	MIG_ID VARCHAR(5),
	MIG_DATE DATE,
	CHNG_SVC_NO VARCHAR(20),
	PAUSE_RS_CD VARCHAR(2),
	RS_ORD_NO VARCHAR(15),
	RS_SA_ID VARCHAR(11),
	NP_KIND VARCHAR(1) DEFAULT null,
	DOR_SVC_NO VARCHAR(20),
	OTHER_BUSI_CD VARCHAR(1),
	INIT_RESV_DATE_HH VARCHAR(10),
	REPR_ORD_NO VARCHAR(15),
	INSTALLER_TYPE VARCHAR(1),
	REAL_COMPLETED_DATE DATE,
	COMPETE_COMPANY_CD VARCHAR(2),
	WORK_TEAM_CD VARCHAR(2),
	SMT_CONTL_ORD_NO VARCHAR(15),
	SGL_SA_CD VARCHAR(4),
	SGL_SA_DTL_CD VARCHAR(4),
	CANCEL_TIME VARCHAR(2),
	CHG_DATE DATE,
	IDENTITY_FLAG VARCHAR(1),
	STAMP_TAXT_FLAG VARCHAR(1),
	PAPER_CD VARCHAR(1),
	CUST_ENV VARCHAR(20),
	DOC_SEC_REQ_FLAG VARCHAR(1),
	EMG_REQ_FLAG VARCHAR(1),
	WM_EMP_NO VARCHAR(20),
	DAY_DIV VARCHAR(2),
	CANCEL_REFUSE_FLAG VARCHAR(1),
	RADDR_TEXT VARCHAR(200),
	BNA_FLAG VARCHAR(1),
	APP_REQ_ID VARCHAR(15),
	AGENCY_CD VARCHAR(9),
	CUST_CNTPLC_SEQ NUMBER(10,0),
	IST_DIV_CD VARCHAR(2),
	KNOTE_ITEM_ID VARCHAR(100),
	INSTL_TEL_NO VARCHAR(20),
	CONSTRAINT PP_CORDORDINFO_PK PRIMARY KEY (ORD_NO)
);
CREATE INDEX PP_CORDORDINFO_IX01 ON PP_CORDORDINFO (SA_ID,ORD_NO);
```  

<br/>


## 8. Full Stack Application 배포 해 보기 

<br/>

총 6개의 POD가 배포가 되어야 한다. 

```bash
jakelee@jake-MacBookAir mvp_custinfo_backend % kubectl get pod -n mvp
NAME                        READY   STATUS    RESTARTS   AGE
apigw-6fc67fb655-58sz5      1/1     Running   0          15h
baseinfo-75979dd6bc-ss2ql   1/1     Running   0          18h
custinfo-c66644d9f-bh5pl    1/1     Running   0          26h
frontend-546467c64c-pvh7m   1/1     Running   0          14h
ppon-75d9cd8db-922gn        1/1     Running   0          14h
pponsub-dd5857b56-zkdrf     1/1     Running   0          18h
```   

<br/>

frontend 서비스 만 LoadBalancer 로 설정되어 외부에서 접속 가능한 IP 를 가져 온다.  

```bash
jakelee@jake-MacBookAir mvp_custinfo_backend % kubectl get svc -n mvp
NAME       TYPE           CLUSTER-IP     EXTERNAL-IP      PORT(S)        AGE
apigw      ClusterIP      10.0.196.242   <none>           80/TCP         33h
baseinfo   ClusterIP      10.0.4.237     <none>           80/TCP         40h
custinfo   ClusterIP      10.0.68.30     <none>           80/TCP         40h
frontend   LoadBalancer   10.0.161.33    52.231.185.134   80:31213/TCP   32h
ppon       ClusterIP      10.0.243.80    <none>           80/TCP         15h
pponsub    ClusterIP      10.0.174.11    <none>           80/TCP         17h
```  

<br/>

`https://github.com/azure-edu` 에 소스가 저장되어 있고 `private`으로 구성되어 있다.      

repository 6개를 다 fork 하고 secret 설정 후에 GitHub Action으로 6개 도커 이미지를 생성 해야 한다.

<br/>

소스 폴더 구조   

```bash
jakelee@jake-MacBookAir azure_edu % tree -d -L 2
.
├── mvp_apigw
│   └── src
├── mvp_baseinfo_backend
│   └── src
├── mvp_custinfo_backend
│   └── src
├── mvp_frontend
│   ├── public
│   └── src
├── mvp_ppon_pub_backend
│   └── src
└── mvp_ppon_sub_backend
    └── src

14 directories
```  

<br/>


### API GW 서비스 

<br/>

mvp_apigw : Spring Cloud Gateway 기반의 API GW
- MicroService 분기 , 로깅  

<br/>

### 마이크로 서비스 ( 4개 )

<br/>

mvp_baseinfo_backend   
- 기본 정보 가져오기 . 없으면 가져오고 Redis에 Caching
- application-prd.yml
  ```yaml
  data:
    redis:
      host: icistr1.redis.cache.windows.net
      port: 6379
      password: "xxxxxxYH9xxxxxxxxgOAzCaPCPiF8="
      ssl:
        enabled: false
      #azure:
      #  passwordless-enabled: true
    commandtime_duration: 10
  ```
  
<br/>

mvp_custinfo_backend   
- 고객 정보 전체 가져오기
- application-prd.yml
  ```yaml
  data:
    redis:
      host: icistr1.redis.cache.windows.net
      port: 6379
      password: "xxxxxxxxxxxCaPCPiF8="
      ssl:
        enabled: false
      #azure:
      #  passwordless-enabled: true
    commandtime_duration: 10
  ```
  
<br/>

mvp_ppon_pub_backend   
- 개통 서비스 Publish
- application-prd.yml
  - eventhub 가 443 포트 이지만 kafka 설정 시에는 9093 으로 설정  

    <img src="./assets/azure_kafka_config_1.png" style="width: 80%; height: auto;"/>  

- pom.xml : eventhub 연동을 위해서는 spring-cloud-azure-starter 추가 필요
  ```xml
		<!-- Azure -->
		<dependency>
			<groupId>com.azure.spring</groupId>
			<artifactId>spring-cloud-azure-starter</artifactId>
		</dependency>
    	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>com.azure.spring</groupId>
				<artifactId>spring-cloud-azure-dependencies</artifactId>
				<version>5.13.0</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
  ```  

 <br/>

mvp_ppon_sub_backend   
- 개통 서비스 subscribe
- application-prd.yml
  - eventhub 가 443 포트 이지만 kafka 설정 시에는 9093 으로 설정  
  - consumer group id는 eventhub ppon topic 에서 미리 생성  

    <img src="./assets/azure_kafka_config_2.png" style="width: 80%; height: auto;"/>

- pom.xml : eventhub 연동을 위해서는 spring-cloud-azure-starter 추가 필요
  ```xml
		<!-- Azure -->
		<dependency>
			<groupId>com.azure.spring</groupId>
			<artifactId>spring-cloud-azure-starter</artifactId>
		</dependency>
    	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>com.azure.spring</groupId>
				<artifactId>spring-cloud-azure-dependencies</artifactId>
				<version>5.13.0</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
  ```

<br/>

Azure DB For MySQL 연동시에는 application-prd.yaml에 아래와 같이 설정한다.

```yaml
  ## azure MySQL
    #driverClassName: org.mysql.jdbc.Driver #org.mariadb.jdbc.Driver
    #url: jdbc:mysql://icistr1.mysql.database.azure.com:3306/baseinfo 
    #username: kt_admin
    ##password: New11111111!
    #password: ENC(cX5eDeWCal1Zen+LxWhQFXkqh44xdeDG)
```  

<br/>

pom.xml 에는 아래 와 같이 library 추가   
```yaml
          <!-- MySQL -->
	        <dependency>
	            <groupId>mysql</groupId>
	            <artifactId>mysql-connector-java</artifactId>
	            <version>8.0.28</version>
	        </dependency>
```

<br/>

### frontend

<br/>

mvp_frontend   
- Vue.js 로 개발 된 화면

<br/>


### ArgoCD 로 배포하기

<br/>


`https://github.com/azure-edu-gitops` 에 gitops manifest 가 저장되어 있고 `public`으로 구성되어 있다.    

해당 repository 를 fork 하여 본인의 docker image 이름으로 다 변경한다.

<br/>

gitops 폴더 구조   

```bash
jakelee@jake-MacBookAir mvp_gitops % tree -d -L 4
.
├── charts
│   ├── baseinfo_backend
│   ├── custinfo_backend
│   ├── gateway
│   ├── ppon_pub_backend
│   └── ppon_sub_backend
└── frontend

8 directories
```


<br/>

우리는 app-of-apps 패턴을 사용하여 한번에 배포를 한다.     


`https://github.com/azure-edu-gitops/app_of_apps.git` 에 app-of-apps  패턴 manifest 가 저장되어 있고 `public`으로 구성되어 있다.  
  
<br>

폴더 구조  

```bash
jakelee@jake-MacBookAir app_of_apps % tree  -L 4
.
├── README.md
├── application.yaml
└── apps
    ├── baseinfo.yaml
    ├── custinfo.yaml
    ├── frontend.yaml
    ├── pponpub.yaml
    └── pponsub.yaml

2 directories, 7 files
```  

<br/>

웹 브라우저에서  ArgoCD 로 접속한다.   

`https://github.com/azure-edu-gitops/app_of_apps.git` 을 repository로 설정하고 배포를 진행한다.  

<br/>

완료가 되면 서비스별로 Card가 만들어지고 서비스별로 하나씩 manifest Sync 를 해준다.   


<img src="./assets/azure_argocd_app_of_apps_1.png" style="width: 80%; height: auto;"/>


<br/>

정상적으로 배포가 되고 서비스를 조회해 보면 아래와 같이 EXTERNAL-IP 가 생성 된것을 볼수 있다.   

```bash
jakelee@jake-MacBookAir azure_edu % kubectl get svc -n mvp
NAME       TYPE           CLUSTER-IP     EXTERNAL-IP      PORT(S)        AGE
frontend   LoadBalancer   10.0.161.33    52.231.185.134   80:31213/TCP   40h
```  

<br/>

### Application 동작 확인

<br/>

웹 브라우저에서 frontend 서비스의 IP로 웹브라우저로 접속해 본다.    

좀 있다가 아래와 같은 고객정보가 나타나면 접속이 성공된 것을 알 수 있다.  

<img src="./assets/azure_mvp_frontend_1.png" style="width: 60%; height: auto;"/>  


<br/>

왼쪽 frame 에서 기본 정보를 클릭하여 화면을 이동한다.

<img src="./assets/azure_mvp_frontend_2.png" style="width: 30%; height: auto;"/>  

<br/>

그룹명  `M00S` , 코드 `10` 으로 설정 하고 조회를 한다.   
아래 데이터가 나오면 DB 조회가 성공하고 Redis 에 저장이 된다.  

<img src="./assets/azure_mvp_frontend_3.png" style="width: 30%; height: auto;"/>    

<br/>

redis를 확인해본다.  

<img src="./assets/azure_mvp_frontend_4_redis.png" style="width: 40%; height: auto;"/>  

왼쪽 frame 에서 유선 전화를 클릭하여 화면을 이동한다.

<img src="./assets/azure_mvp_frontend_5.png" style="width: 30%; height: auto;"/>  

<br/>

고객번호  `11111111111` 을 입력하고 고객 검증 버튼을 클릭한다.     
성공 메시지가 나오는 것을 확인한다.      

- ppon 서비스에서 custinfo 서비스를 조회를 하여 검증을하며 내부에는 openfeign 으로 구성  

<img src="./assets/azure_mvp_frontend_6.png" style="width: 60%; height: auto;"/>  

<br/>

하단부에 저장 버튼을 클릭하면 고객정보가 저장이 된다.  
- eventhub의 `ppon` topic에  publish 가 되고 subscribe가 되어 DB에 저장이 된다.   

<img src="./assets/azure_mvp_frontend_7.png" style="width: 60%; height: auto;"/>  

<br/>

eventhub 에서 확인을 해본다. in/out이 `1`개씩 되어 있다.  

<img src="./assets/azure_mvp_frontend_8_eventhub.png" style="width: 60%; height: auto;"/>  

<br/>

ArgoCD에서 pponsub Application 을 클릭하고 POD 를 클릭하면 로그를 볼수 있다.    


<img src="./assets/azure_mvp_frontend_9.png" style="width: 60%; height: auto;"/>  

<br/>

아래 비지니스 로그에서 `ordNo` 값이 `d34ba273-a0f0-4` 인 것을  확인 할 수 있다.    

```bash
{"SERVICE":"","TYPE":"ONLINE","CATEGORY":"DEBUG","WORKFLOW":"","GLOBAL_NO":"","TRANSACTION_ID":"","TRACE_ID":"","ERROR_ID":"","DATE":"20240717091057110","SOURCE":"EvenHubPponListener.receiveMessage(32)","LOG_LEVEL":"INFO","MESSAGE":"[Kafka] Received message: {\"inDs\":{\"ordNo\":\"d34ba273-a0f0-4\",\"custName\":null,\"custId\":\"11111111111\",\"custNoType\":\"2\",\"addrText\":\"\",\"completedResvDateHh\":\"\",\"cntcTelNo\":\"\"}}"}
```

<br/>

DB에서 확인을 합니다.  

<img src="./assets/azure_mvp_frontend_10.png" style="width: 60%; height: auto;"/>  

<br/>





# 1. CQRS(Command Query Responsibility Segregation)

------

## 1.1. 개요

- **CQRS는 크리스 리처드슨의 '마이크로서비스 패턴'에 나오는 44가지 패턴 중 핵심 패턴 중 하나로 어플리케이션 패턴 중 데이터 패턴 중 조회에 대한 부분이다.**

![img](./assets/msapattern.png)



## 1.2. Query 와 Command 란?

- CQRS 이전의 하위 개념인 CQS 에 대한 이해가 필요하다.

  ![img](./assets/cq.png)

- CQS 는 Design By Contract 라는 용어를 만든 버트란드 메이어가 소개한 개념이다.

- 함수는 특정 동작을 수행하는 코드 블록을 의미하는데, 함수의 목적에 따라서 두가지로 분류할 수 있다.

- **Command 와 Query 이다.**

###  1.2.1. Command

- Command 는 시스템에 어떠한 side effect, 즉 **변경을 가하는 행위**를 하는 것을 말한다.

- 그래서 Command 성 함수 라고 한다면 변경을 가하는 함수를 말할 수 있다.

- **Command 성 함수는 시스템의 상태를 변경시키는 대신 값을 반환하지 않아야 한다.** 

```
// O, 상태만 변경시킴
void updateUser(User user) {
  user.updateAge(12);
}

// X, 값을 반환
User updateUser(User user) {
  return user.updateAge(12);
}
```

### 1.2.2. Query

- 이에 반해서 Query 는 시스템의 **상태를 관찰할 수 있는 행위**를 하는 것을 말한다.

- 마찬가지로 Query 성 함수라고 한다면 **단지 시스템의 상태만 확인하는 함수**라고 할 수 있다.

- **Query 성 함수는 시스템의 상태를 단지 반환하기만 하고 상태를 변경시키지 않아야 한다.**

```
// O, 값만 반환
User getUser(Long userId) {
  return users.get(userId);
}

// X, 상태를 변경
void getUser(Long userId) {
  User user = users.get(userId);
  user.updateLastQueriedAt();
  return user;
}
```

### 1.2.3. CQS (Command Query Separation ) 란?

 ![img](./assets/cqs.png)

- 버트란드 메이어는 위의 Command 와 Query 를 분리해야 하며 하나의 함수는 이 성격을 띄어야 한다고 했다.

- **즉, 어떠한 함수가 있다면 그 함수는 Command 또는 Query 중 하나의 역할만 수행해야 한다.**

- 만약 하나의 함수에서 Command 와 Query 가 모두 동시에 일어나게 된다면, 이는 소프트웨어의 3가지 원칙 중 복잡하지 않아야 한다는 KISS 가 지켜지지 않을 것이다.

  - > *KISS 원칙은 "Keep It Simple Stupid!", "Keep It Short and Simple", "Keep It Small and Simple"의 첫 글자를 따서 만든 약어다. 소프트웨어를 설계하는 작업이나 코딩을 하는 행위에서 되도록이면 간단하고 단순하게 만드는 것이 좋다는 원리로 소스 코드나 설계 내용이 '**불필요하게**' 장황하거나 복잡해지는 것을 경계하라는 원칙이다.*

  단순할수록 이해하기 쉽고, 이해하기 쉬울수록 버그가 발생할 가능성이 줄어든다. 이는 곧 생산성 향상으로 연결된다. 작업이 불필요하게 복잡해지는 것을 항상 경계해야 한다.

- 이런 관점에서 연장선상에 있는 것이 바로 Command Query Responsibility Segregation 이다.



## 1.3. CQRS (Command Query Responsibility Separation ) 란?

![img](./assets/cqrs.png)

- CQRS 는**그렉 영**이 소개한 말이고, CQS에 비해 조금 더 큰 레벨에서의 Command 모듈과 Query 모듈의 책임을 분리하자는 말이다.

- CQS 는 코드 레벨에서의 분리를 말한다면 CQRS 는 조금 더 거시적인 관점에서의 분리를 의미한다.

- 앞서 버트란드 메이어가 말한것 처럼 command 형이거나 query 형의 함수를 분리시키면 소프트웨어가 더욱 단순해지고 이해하기 쉬워진다고 했다.

- **CQRS 는 이 원칙을 차용한다.**

- 일반적으로 CQRS 패턴을 적용한 애플리케이션은 다음과 같은 형태를 띄게 된다. 



![img](./assets/figure1.png)

- 위 그림을 보면 하나의 service interface 를 두고 두개의 서로 다른 애플리케이션이 존재한다.
  - **read side**
  - *write side**

- 아래의 그림은 **게임 보드** 라는 가상의 도메인을 모델링한 그림이다.![img](./assets/figure2.png)
  - 사용자는 정답을 입력한다.
  - 정답이라면 점수를 올리고 오답이면 점수를 내린다.
  - 사용자의 랭킹 확인할 수 있다.

- 위의 구조는 동일한 도메인 모델을 사용한다. 즉, 조회의 책임과 명령의 책임이 하나의 도메인에 포함되어있다는 이야기다.

- 그럼 무슨 문제가 생길까?

- 위 아키텍처에서는 3가지의 잠재적인 문제가 존재할 것이다.
  - 복잡성
  - 성능
  - 확장성

- **문제1. 복잡성, 도메인이 비대해진다.**

  - 우리의 시스템에서 도메인이 갖는 의미에 대해서 생각해보자.
  - **도메인이란 곧 비즈니스이다.**
  - 비즈니스는 보통 특정한 데이터의 상태를 변경 (create, update, delete) 을 하는 것이다. 이러한 비즈니스는 시간이 증가하면서 점점 복잡도가 올라가게 되고, 많은 요구사항들을 포함할 수 있어야 했다.
  - 하지만 query 는 어떠할까?
  - query 는 단순 데이터 조회이기 때문에 비즈니스와 무관하지만 가끔 query 를 위한 처리가 도메인에 침투하는 경우가 생긴다.

  - 우리가 만나는 UI 의 데이터 대부분은 비정형 데이터들일 것이다. 즉, 쇼핑몰에서 user 정보에 따른 관심 물품, 최근 구매 내역, AI 추천 상품 목록 등 이러한 데이터는 하나 이상의 바운디드 컨텍스트와 관련이 있게 된다.
  - 그럼 이런 비즈니스 요구사항이 생길때마다 도메인을 수정해야 할까? 쿼리를 더 잘 할 수 있도록 도메인에 관련 행위를 추가해야 하는가?
  - 안 그래도 도메인 자체는 비대해져 가는데, 비즈니스 자체를 표현해야 하는 도메인에 query 가 침투한다? 즉 복잡성이 올라간다는 것을 의미한다.

- **문제2. 성능**
  
  - 대부분의 write 연산에서 우리는 일관성 (consistency) 에 대해서 많은 신경을 써야한다.
  - 대부분의 잘 알려진 consistency 를 지키기 위한 해법 으로 **DB Locking 기법**을 사용할 것이다.
- write 연산에서 한번 lock 을 잡게 되면 그 뒤의 read 연산이 모두 대기를 하게 되며 전반적인 성능이 낮아지는 결과를 초래할 수 있다. (물론 lock 의 기법에 따라서 결과는 다를 수 있지만)
  
- **문제 3. 확장성**
  - 많은 시스템에서 읽기와 쓰기에 대한 불균형이 존재한다는 사실은 꽤나 자주 들리는 이야기다.
  - 쓰기 작업과 읽기 작업의 비율이 **1(write):1000(read)** 라고 한다.
  - 그렇다는 이야기는 **read side 와 write side 의 서버는 서로 다른 기준으로 설계**가 되어야 한다는 것이다.
  - 즉, 독립적으로 확장이 가능해야 하고 각각 목적에 맞는 다른 솔루션이 필요하다는 이야기다.
  - 만약 이 둘이 분리되어 있지 않고 하나의 컴퓨팅 엔진만을 사용한다면 혹은 하나의 데이터소스만을 사용한다면 독립적인 확장이 힘들 것이다.
  - 하지만 여기서 CQRS 를 적용해서 **책임에 따른 Command 와 Query 의 연산을 각각 독립적으로 분리**시키면 다음과 같은 형태를 띄게 된다.![img](./assets/figure3.png)

- 앞서 보았던 일반적인 CQRS 의 형태와 비슷하게 되었다.

- 그렇다면 정답과 관련된 비즈니스를 책임지는 윗쪽 도메인에게는 **상태를 변경시키는 Command 의 책임**만 존재하기에 비즈니스를 그대로 표현할 수 있다.

- 역시 아래의 도메인에게는 **상태를 확인하는 Query 의 책임** 만 존재하게 된다.

- 이렇게 되면 어떤 장점이 있을 수 있을까?

- 단순히 가장 먼저 드는 생각은 Command 와 Query 에 각기 다른 Persistence Module 을 사용할 수 있을것이다.
  - Command Side 에는 객체 중심적인 개발이 가능한 **Spring Data JDBC/JPA** 를 사용할 수 있다.
  - Query Side 에는 최적화된 쿼리를 위해서 **JdbcTemplage/MyBatis** 를 사용할 수 있을 것이다.

- CQRS 더 고도화 시켜볼 수 있다
  - Command 와 Query 의 책임이 분리되었기 때문에 Command 와 Query 는 서로 다른 인프라가 구성될 수 있다.![img](./assets/figure5.png)
  - 그럼 위와 같이 Polyglot 한 Persistence Infra 가 구성될 수 있다.
  - Command infra 에는 write 에 최적화된 DB를 사용할 수 있을 것이다.
  - Query Side 에는 더욱 빠른 쿼리을 위해서 elasticsearch나 와 같은 검색 엔진을 도입할 수 있을 것이다.

  - Query Side 에 **Materialized View** 를 이용하여 복잡한 쿼리를 방지하고 **관점에 따른 정보 뷰**를 생성하여 사용하곤 한다
  - Write Side 에서 발생하는 변경 사항들에 대해서는 중간에 메시징 인프라를 이용해서 계속해서 동기화를 시켜주는 형태로 사용하기도 한다.

  

## 1.4. CQRS 의 장점



### 1.4.1. Scalability, 확장성

- 많은 enterprise 시스템에서, read 연산이 write 연산보다 훨씬 많이 일어나기 때문에 당신의 scalability, 즉 서버의 확장성은 read side 와 write side 각각에 다른 기준이 적용되어야 한다.

- 하나의 Bounded Context 내에서 **read side 와 write side 를 분리함으로써 각각을 서로 다르게 확장**할 수 있음을 의미한다.

- 예를 들어, Windows Azure에서 애플리케이션을 호스팅하는 경우, 각 side 를 분리한다면 **서로 다른 수의 인스턴스를 추가하여 독립적으로 확장**할 수 있다.

  

### 1.4.2 Reduced Complexity, 복잡성 줄이기

- 당신의 복잡한 도메인 속에서, **하나의 객체 안에 읽기 연산과 쓰기 연산을 모두 설계하고 구현하는 것은 복잡성을 더욱 악화**시킬 수 있다.

- 여러 케이스에서, 비즈니스 로직의 복잡성은 **update 와 트랜잭셔널한 연산을 수행할 떄에만 발생하지만, 반대로 읽기 연산은 그보다 훨씬 단순**하다.

- 비즈니스 로직과 읽기 연산이 하나의 모델에 뒤섞여 있다면, 더욱 어려운 비즈니스적 문제를 해결하거나, 대용량 처리, 분산 처리, 성능, 트랜잭션 및 consistency 를 처리하는 데에 큰 산이 될 것이다.

- 읽기 연산과 비즈니스 로직을 분리하면 이러한 문제를 해결하는데에 도움이 되지만 많은 대다수는 기존 모델을 분리하고 이해하는데에 여러 노력이 필요할 수도 있다.

- 많은 다른 Pattern 들 처럼, **CQRS 패턴을 도메인에 내제된 복잡성 중 일부를 더 쉽게 이해되고 특정 영역에서 문제 해결에 집중할 수 있도록 하는 접근 방식 또는 메커니즘**으로 바라볼 수 있다.

- 읽기 연산과 비즈니스 로직을 분리하여 경계 컨텍스트를 단순화하는 또 다른 잠재적인 이점은 테스트를 더 쉽게 만들 수 있다는 것이다.

  

### 1.4.3. Flexibility, 유연성

- CQRS 패턴을 사용할 때 얻어지는 유연성은 read-side 와 write-side 를 분리할 때 주로 발생된다.

- CQRS 패턴을 사용한다면, UI 에서 보여질 특정 쿼리를 추가한다거나 하는 read-side 에서 변경 및 추가가 쉬워진다.

- 비즈니스 로직에 어떠한 영향을 끼치지 않는다고 확신할 수 있을 때, **UI 에서 보여질 특정 view 를 위해서 쿼리를 추가하는 것과 같이 read-side 에서 변경하는 것이 훨씬 쉬워진다.**

- write-side 에서, 도메인의 핵심 비즈니스 로직만 표현하는 모델을 갖는다는 것은 read 연산과 write 연산이 혼재되어있을 때 보다 훨씬 간단하다는 것을 의미한다.

- 장기적으로 봤을 때, 당신의 핵심 도메인 비즈니스 로직을 명확하게 설명하는 코어 도메인 모델이 가치 있는 자산이 될 것이다.

- 당신이 직면해있는 계속해서 변경되는 비즈니스 환경과 경쟁의 압박 속에서 훨씬 더 agile 스럽게 해준다.

이러한 유연성과 agility 은 DDD 의 Continousous Integration 과 관련이 있다.

- 어떤 경우에는 write side 와 read side 에 서로 다른 개발 팀을 구성하는 것이 가능할 수도 있지만, 현실에서 이것은 아마도 얼마나 특정 바운디드 컨텍스트가 얼마나 큰지에 달려있을 것이다.



### 1.4.4. 비즈니스에 집중하기

- 만약 당신이 CRUD 를 사용한다면, 기술은 해결책을 형성하는 경향이 있다.
- CQRS 패턴을 채택하는 것은 당신이 **비즈니스에 집중하고 task-oridened (작업 지향) UI 를 만드는 데에 집중**할 수 있도록 도와준다.
- **read-side 와 write-side 의 관심사를 분리하는 것의 결과는 변화하는 비즈니스 요구사항에 더 잘 적응할 수 있는 솔루션**이다. 이로 인해서 장기적으로 개발 및 유지보수 비용이 절감된다.



### 1.4.5. 작업 기반 UI 만들기를 촉진 (Facilitates building task-based UIs)

- CQRS 패턴을 구현할 때, 도메인에게 작업을 시작하도록 할때 command 를 사용한다.
- 이러한 command 들은 일반적으로 도메인의 연산과 [*유비쿼터스 언어](https://github.com/dhslrl321/cqrs-journey-guide-korean/blob/master/terms/Ubiquitous Language.md)에 밀접하게 연관이 있다.
- 예를 들어서 "컨퍼런스 X 의 두 자리 좌석을 예매한다" 라는 command 가 있다고 해보자. 전통적인 CRUD 스타일의 작업을 하는 대신 이러한 명령을 도메인으로 보내기 위한 UI 를 설계할 수 있다.
- 이렇게 된다면 더욱 직관적이게 되고 작업 기반 UI 를 더 쉽게 설계할 수 있게 된다.



## 1.5. CQRS 단점

- **개발 리소스 증가**
  - CQRS 패턴을 적용하는 추가적인 개발 리소스가 투입될 수 있으므로 관련 사항을 이해관계자에게 이해 시키는 것이 중요하다.

  - write-side 와 read-side 를 명시적으로 분리하기 때문에 중복된 코드가 생길 수 있다.

- **즉시적인 일관성이 보장되지 않는다(최종 일관성은 보장)**
  - command 에 따른 data 의 무결성이 잠시동안 깨질 수 있다.
  - 이 말은 데이터의 consistency 가 항상 동일하지 않다
  - 하지만 최종적으로는 데이터가 맞춰질 것이니 **Eventual Consistency**라고 할 수 있다.



## 1.6. CQRS 를 사용해야 하는 경우

- **많은 사용자가 동일한 데이터에 병렬로 액세스하는 협업 도메인**
  -  CQRS를 사용하면 도메인 수준에서 **병합 충돌 및 충돌이 발생할 때 명령으로 병합할 수 있는 충돌을 최소화할 수 있을 정도로 자세하게 명령을 정의**할 수 있다.
- **여러 단계를 거치거나 복잡한 도메인 모델을 사용하는 복잡한 프로세스를 통해 사용자를 안내하는 작업 기반 사용자 인터페이스.**** 
  - 쓰기 모델에는 비즈니스 논리, 입력 유효성 검사 및 비즈니스 유효성 검사가 포함된 전체 명령 처리 스택이 있다. 
  - 쓰기 모델은 데이터 변경(DDD 용어의 집계)에 대한 단일 단위로 연결된 개체 세트를 처리하고 이러한 개체가 항상 일관된 상태인지 확인할 수 있다. 
  - 읽기 모델은 비즈니스 논리 또는 유효성 검사 스택을 보유하지 않으며 보기 모델에 사용할 DTO를 반환한다. 
  - **결과적으로 읽기 모델과 쓰기 모델의 일관성이 유지**됩니다.
- **읽기 수가 쓰기 수보다 훨씬 큰 경우 데이터 읽기의 성능을 데이터 쓰기 성능과 별도로 미세 조정해야 하는 시나리오.** 
  - 읽기 모델을 스케일 아웃할 수 있지만 몇 가지 인스턴스에서만 쓰기 모델을 실행할 수 있습니다. 
  - 소수의 쓰기 모델 인스턴스는 병합 충돌 발생을 최소화하는 데도 기여한다.
- **협업**
  - Udi Dahan 과 Greg Young 모두 바운디드 컨텍스트에 CQRS 패턴을 적용할 때 발생되는 최고 이점으로 협업을 꼽았다.
  - 개발자 중 한 팀은 쓰기 모델에 포함되는 복잡한 도메인 모델에 집중하고 또 한 팀은 읽기 모델과 사용자 인터페이스에 집중할 수 환경 제공.
- **비지니스 변화가 잦은 경우(배포)**
  -  이벤트 소싱과 조합해 다른 시스템과 통합하는 경우. 이때 하위 시스템 하나의 일시적인 장애가 다른 시스템의 가용성에 영향을 주지 않아야 합니다.

- **클라우드 환경**
  - PaaS(Platform as a Service) 클라우드 컴퓨팅 플랫폼에서 제공되는 많은 여러 서비스는 확상성이 뛰어난 데이터 저장소, 메시징 서비스 및 캐싱 서비스가 CQRS 구현을 위한 인프라로 적합.



## 1.7. CQRS 를 사용하지 말아야 하는 경우

- 도메인 또는 비즈니스 규칙이 간단한 경우 복잡도만 증가 한다.
- 간단한 CRUD 스타일 사용자 인터페이스 및 데이터 액세스 작업이 충분할 경우 굳이 분리할 필요가 없다.



## 1.8 CQRS의 구현형태

- 명령과 쿼리 모델이 한 프로세스에 있는지 다른 프로세스에 있는지
- 같은 DB를 사용하는지 다른 DB를 사용하는지

![img](./assets/figure6.png)



### 1.8.1. 구현: 같은 프로세스, 같은 DB

- 가장 단순하고 명령과 쿼리가 코드 수준에서 분리된다.
- 명령/쿼리 동일 데이터 보장



![img](./assets/figure7.png)



### 1.8.2. 구현: 같은 프로세스, 같은 DB, 다른 테이블

- 명령과 쿼리가 코드 수준에서 분리 되고 데이터 수준에서도 분리된다.
  - 단 데이터가 같은 DB에 있는 형태

- 쿼리 전용 테이블 사용
  - 예: 최근 조회수 많은 글 목록을 별도 테이블로 따로 저장
  - 쿼리 모델은 이 테이블을 이용해서 구현
- 명령이 상태를 변경할때 쿼리 전용 테이블을 함께 변경된다.

![img](./assets/figure8.png)



### 1.8.3. 구현: 같은 프로세스, 다른 DB

- 예: 상품목록을 레디스와 같은 저장소에 캐싱하고 쿼리 모델은 레디스를 사용하는 방식
  - 명령이 데이터를 변경하면 변경내역을 쿼리쪽 DB에 전달



![img](./assets/figure9.png)

### 1.8.3.1. 구현: 다른 프로세스, 다른 DB

- 명령이 데이터를 변경하면 변경내역을 쿼리쪽 DB에 전달
  - 마이크로서비스 분리 추세에 따라 많이 만날 수 있는 형태



![img](./assets/figure10.png)

### 1.8.3.2. 다른 DB로 변경 전파

- 명령이 직접 쿼리 DB를 수정하는 방식
  - 카프카와 같은 메세징 수단을 이용해서 전달하는 변형도 있음
  - 장점: 구현이 단순함
  - 단점: 데이터 유실 가능성이 있음
    - 쿼리 DB나 메시징이 일시적으로 장애가 발생하게 되면 쿼리 DB에 반영해야 할 데이터가 유실될 수 있음
    - 쿼리 DB나 메시징의 문제 때문에 명령을 수행하는 기능 자체가 에러가 발생할 수 있음
- 변경내역을 기록하고 별도 전파기를 이용해서 변경내역을 전달하는 방식
  - 중간에 메시징을 두는 변형이 있음
  - 명령은 상태를 변경한 다음 변경내역을 별도 테이블에 기록 함
  - 장점: 한 트랜잭션으로 처리 되기 때문에 변경내역이 유실되지 않음
  - 단점: 전파기를 별도로 구현해야하는 부담이 있음
- DB가 제공하는 CDC(Change Data Capture)를 사용하는 방식
  - 중간에 메시징을 두는 변형이 있음
  - DB의 바이너리 로그를 읽어서 변경 데이터를 확인하고 변경된 데이터를 쿼리쪽에 전달하는 방식
  - 장점: 명령 쪽 코드에서 변경내역을 따로 저장하지 않아도 되므로 명령코드가 단순해짐



![img](./assets/figure11.png)



### 1.8.3.3. 다른 DB 사용시 주의 사항

- **데이터 유실**
  - 유실 허용 여부에 따라 DB 트랜잭션 범위 중요
  - 예)
    - 주문목록 쿼리 기능의 경우 데이터가 유실되면 곤란함
    - 최근 읽기글 쿼리 기능은 일시적으로 데이터 전파가 안되도 치명적이지 않음
- **허용 가능 지연 시간**
  - 명령의 변경내역을 얼마나 빨리 쿼리쪽에 반영하는지에 따라서 구현의 선택이 달라질 수 있음
- **중복 전달**
  - 쿼리쪽 DB에 변경된 데이터를 전달하는 과정에서 문제가 발생하게 되면 다시 전달할 수 있는 수단이 필요
  - 다시 전달할 수 있는 수단을 만들다 보면 쿼리쪽에 이미 반영된 데이터를 중복으로 전달하는 경우도 발생하게 됨
  - 중복으로 데이터를 전달 하더라도 쿼리쪽 데이터가 망가지지 않도록 별도의 처리를 해야 함



## 1.9. 요약

- ***CQRS 패턴을  적용하기에 앞써 초기 비용과 오버헤드, 미래의 비즈니스 이점 사이의 장단점을 명확히 분석해야 한다.***

- ***CQRS 패턴을 적용할 수 있는 곳을 식별하는 데 유용한 지표는 복잡하며 유동적인 비즈니스 규칙을 포함하여 collaborative 한 구성 요소를 찾는 것이다.***

- ***CQRS 패턴은  Command 와 Query 를 분리시켜 복잡성을 줄이는 것이 핵심이며 서비스 및 인프라 구성은 도메인 비지니스의 성격에 따라 고려되어야 한다.***





# 2. CQRS 구성하기 위한 기술



## 2.1. Spring with kafka



### 2.1.1. 개요

- [Apache Kafka](https://kafka.apache.org/) 는 분산 및 내결함성 스트림 처리 시스템이다.

- *Spring Kafka는 @KafkaListener* 어노테이션 을 통해 *KafkaTemplate* 및 **Message-driven POJO 가 있는 단순하고 일반적인 Spring 템플릿 프로그래밍 모델을 제공**한다.



### 2.1.2. 의존성 추가

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```



### 2.1.2. 설정

```yaml
  kafka:
    consumer:
      bootstrap-servers: localhost:9092
      group-id: consumerGroupId
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      bootstrap-servers: localhost:9092
```



### 2.1.3. 이벤트 전송

- *KafkaTemplate* 클래스 를 사용하여 메시지를 보낼 수 있다.

```java
@Autowired
private KafkaTemplate<String, String> kafkaTemplate;

public void sendMessage(String msg) {
    kafkaTemplate.send(topicName, msg);
}
```

- send API는 ListenableFuture 객체 를 반환한다. 보내는 스레드를 차단하고 보낸 메시지에 대한 결과를 얻으려면 *ListenableFuture* 객체 의 *get* API를 호출할 수 있다. 스레드는 결과를 기다리지만 Producer 속도가 느려진다.
  - Kafka는 빠른 스트림 처리 플랫폼이다. 따라서 후속 메시지가 이전 메시지의 결과를 기다리지 않도록 결과를 비동기적으로 처리하는 것이 좋다.

- 이 같은 경우 콜백을 통해 작업을 수행할 수 있다.

```java
public void sendMessage(String message) {
            
    ListenableFuture<SendResult<String, String>> future = 
      kafkaTemplate.send(topicName, message);
	
    future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

        @Override
        public void onSuccess(SendResult<String, String> result) {
            System.out.println("Sent message=[" + message + 
              "] with offset=[" + result.getRecordMetadata().offset() + "]");
        }
        @Override
        public void onFailure(Throwable ex) {
            System.out.println("Unable to send message=[" 
              + message + "] due to : " + ex.getMessage());
        }
    });
}
```



### 2.1.4. 이벤트 수신

- @KafkaListener 어노테이션을 통해 원하는 topic에 대한 메시지를 수신 받을 수 있다.

```java
@KafkaListener(topics = "topicName", groupId = "foo")
public void listenGroupFoo(String message) {
    System.out.println("Received Message in group foo: " + message);
}
```

- Topic 에 대해 각각 다른 Group ID를 가진 여러 리스너를 구현할 수 있다. 또한 한 Consumer는 다양한 Topic 메시지를 수신 할 수 있다.

```java
@KafkaListener(topics = "topic1, topic2", groupId = "foo")
```

- Spring은 또한 리스너에서 @Header 주석을 사용하여 하나 이상의 메시지 헤더 검색을 지원한다.

```java
@KafkaListener(topics = "topicName")
public void listenWithHeaders(
  @Payload String message, 
  @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
      System.out.println(
        "Received Message: " + message"
        + "from partition: " + partition);
}
```



## 2.2. Caching Data with Spring



### 2.2.1. 개요

- **Spring에서 Caching 추상화를 사용하는 방법 과 일반적으로 시스템의 성능을 향상시키는 기능을 제공**한다.



### 2.2.2. 의존성 추가

- Spring Boot를 사용하는 경우 *[spring-boot-starter-cache](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-cache)* 스타터 패키지를 활용하여 캐싱 종속성을 쉽게 추가할 수 있다.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
    <version>2.4.0</version>
</dependency>
```



### 2.2.3. 캐싱 활성화

- 캐싱을 활성화하기 위해 Spring은 프레임워크에서 다른 구성 수준 기능을 활성화하는 것과 마찬가지로 어노테이션을 사용한다.

- *구성 클래스에 @EnableCaching* 주석을 추가하여 캐싱 기능을 활성화할 수 있다.

- 캐싱을 사용하기 위해 어노테이션을 사용하여 캐싱 동작을 메서드에 바인딩할 수 있다.

```java
package com.example.cloudnative.catalogws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableCaching //Cache 기능 활성화  
public class CatalogWsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogWsApplication.class, args);
    }
}
```



### 2.2.4. @Cacheable

- 메소드에 대한 캐싱 동작을 활성화하는 가장 간단한 방법은 *@Cacheable* 로 구분 하고 결과가 저장될 캐시 이름으로 매개변수화한다.

```java
@Cacheable("addresses")
public String getAddress(Customer customer) {...}
```

- *getAddress()* 호출 은 실제로 메서드를 호출한 다음 결과를 캐싱하기 전에 먼저 캐시 *주소 를 확인한다.*



### 2.2.5 @CacheEvict

- 자주 필요하지 않은 값으로 캐시를 채울 경우 캐시는 상당히 크고 빠르게 증가할 수 있으며 오래되거나 사용되지 않는 데이터를 많이 보유할 수 있다. 
- 이 경우 새로운 값을 캐시에 다시 로드할 수 있도록 @CacheEvict 주석을 사용하여 하나 이상의 모든 값을 제거 할수 가 있다.

```java
@CacheEvict(value="addresses", allEntries=true)
public String getAddress(Customer customer) {...}
```

- 여기서 비울 캐시와 함께 *allEntries 추가 매개변수를 사용한다.* 이렇게 하면 캐시 *주소* 의 모든 항목이 지워지고 새 데이터를 위해 준비됩니다.



## 2.3.  Spring Data JDBC



### 2.3.1. 개요

- **MSA 환경의 DDD(도메인주도설계) 관점의 Aggregate의 일관성을 제공하는 필수 기능을 제공하는 프레임워크**이다.
- Spring Data JDBC는 Spring Data JPA만큼 복잡하지 않으며  JPA의 캐시, 지연 로딩, 세션 등 여러 기능을 제공하지 않는다. 
- 자체 ORM이 있으며 매핑된 엔터티, 저장소, 쿼리 주석 및 JdbcTemplate 과 같이 Spring Data JPA와 함께 사용되는 대부분의 기능을  제공한다.
- Spring Data JDBC는 스키마 생성을 제공하지 않므로 스키마를 사전에 생성하여야 한다.
- Spring Data JDBC는 Spring JDBC를 사용하는 것처럼 간단한 솔루션을 제공하며  Spring Data JPA의 대부분의 기능을 제공한다.
- Spring Data JDBC의 가장 큰 장점 중 하나는 Spring Data JPA에 비해 데이터베이스에 접근할 때 향상된 성능을 제공한다.
- Spring Data JDBC를 사용할 때 가장 큰 단점 중 하나는 데이터베이스 벤더에 의존하므로 데이터베이스를 MySQL에서 Oracle로 변경하기로 결정한 경우 다른 Dialect를 사용하여 데이터베이스에서 발생하는 문제를 처리해야 한다.



### 2.3.2. Spring Data JDBC 의존성 추가

- Spring Data JDBC는 JDBC 종속성 스타터가 있는 Spring Boot 애플리케이션에서 사용할 수 있다.
  - 이 의존성은 데이터베이스 드라이버를 가져오지 않는다.

```xml
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-data-jdbc</artifactId>
</dependency> 
```

- 이 예에서는 H2 데이터베이스를 사용하고 있다. 
  -  이러한 경우 스키마 개체를 만들기 위한 SQL DDL 명령이 있는 사용자 지정 *schema.sql 파일을 만들 수 있다.
- 자동으로 Spring Boot는 이 파일을 선택하여 데이터베이스 객체를 생성하는 데 사용한다.



### 2.3.3. 엔티티 추가

- 다른 Spring Data 프로젝트와 마찬가지로 주석을 사용하여 POJO를 데이터베이스 테이블과 매핑된다. 
- Spring Data JDBC 에서 엔티티는 **@Id** 가 있어야 합니다 . Spring Data JDBC는 *@Id* 주석을 사용하여 엔티티를 식별한다.

- Spring Data JPA와 유사하게 Spring Data JDBC는 기본적으로 Java 엔티티를 관계형 데이터베이스 테이블에 매핑하고 속성을 열 이름에 매핑하는 명명 전략을 사용한다. 
- 기본적으로 엔터티 및 속성의 Camel Case 이름은 테이블 및 열의 스네이크 케이스 이름에 각각 매핑된다.

- *@Table* 및 *@Column* 주석을 사용하여 엔티티 및 속성을 테이블 및 열과 명시적으로 매핑할 수 있다.

```java
public class Person {
    @Id
    private long id;
    private String firstName;
    private String lastName;
    // constructors, getters, setters
}
```

- *Person* 클래스 에서 *@Table* 또는 *@Column* 주석을 사용할 필요가 없습니다 . Spring Data JDBC의 기본 명명 전략은 엔터티와 테이블 간의 모든 매핑을 암시적으로 수행한다.



### 2.3.4. JDBC 저장소 선언

- Spring Data JDBC는 Spring Data JPA와 유사한 구문을 사용한다.
- *Repository* , *CrudRepository 또는 PagingAndSortingRepository* 인터페이스 를 확장하여 Spring Data JDBC 저장소를 생성할 수 있다. 
- *CrudRepository* 를 구현함으로써 우리는 특히 *save* , *delete* , *findById* 와 같은 가장 일반적으로 사용되는 메소드의 구현을 상속한다.

- JDBC 저장소를 예제는 다음과 같다.

```java
@Repository 
public interface PersonRepository extends CrudRepository<Person, Long> {
}
```

- JPA와 마찬가지로 페이징 및 정렬 기능이 필요한 경우 *PagingAndSortingRepository* 인터페이스를 사용할 수 있다.



### 2.3.5. JDBC 저장소 사용자 정의

- *CrudRepository* 의 내장 메소드 에도 불구하고 특정 경우에 대한 메소드를 생성해야 한다.

- 이제 수정하지 않는 쿼리와 수정하는 쿼리를 사용하여 *PersonRepository 에 대한 예제이다.*

```java
@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

    List<Person> findByFirstName(String firstName);

    @Modifying
    @Query("UPDATE person SET first_name = :name WHERE id = :id")
    boolean updateByFirstName(@Param("id") Long id, @Param("name") String name);
}
```

- 버전 2.0부터 Spring Data JDBC는 [쿼리 메소드](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.query-methods) 를 지원한다. 즉, 예를 들어 *findByFirstName과* 같은 키워드를 포함하는 쿼리 메서드의 이름을 지정하면 Spring Data JDBC는 쿼리 객체를 자동으로 생성한다.

- 수정 쿼리의 경우 *@Modifying* 주석을 사용하여 엔터티를 수정하는 쿼리 메서드에 주석을 추가한다. *또한 @Query* 주석 으로 장식합니다 .

- *@Query* 주석 내부에 SQL 명령을 추가한다. **Spring Data JDBC에서는 일반 SQL로 쿼리를 작성한다.** 
- JPQL과 같은 고급 쿼리 언어를 사용하지 않는다. 

- **Spring Data JDBC가 인덱스 번호가 있는 매개변수 참조를 지원하지 않고**매개변수는 이름으로만 참조할 수** 있다.





# 3. 실습



## 3.1. 개요

- CQRS를 구현하기 위한 여러 케이스별 실습을 진행한다.

- 해당 실습은 고전적인 스타일의 일반적인 CRUD Backend API 제공을 위한 서비스를 기반으로 CQRS의 여러가지 관점으로 어플리케이션을 개발해 본다.

  


## 3.2.  사전준비

### 3.2.1. JDK 설치

- 다운로드 : [Eclipse Temulin Java 17](https://projects.eclipse.org/projects/adoptium.temurin/downloads)

```
지난 2021년 9월  14일 JAVA LTS(Long Term Support)인 JDK 17 GA 가 릴리즈되었다.
JDK17은 향후 최대 2029년 9월까지 업데이트가 제공될 예정이다.
참고로 다음 LTS는 JDK21 (2023년 9월)이 될 것으로 예상된다.

2018년 오라클의 정책 변경에 따라 Oracle JDK 바이너리에 적용되던 BCL 라이선스가 바뀌어 이를 사용하려면 라이선스 구독이 필요하다. 따라서 대안으로는 OpenJDK 레퍼런스 소스 코드를 기반으로 제작된 여러 밴더사에서 제공중인 바이너리를 사용할 수 있으며, Azul Platform, Amazon Corretto, ReadHat OpenJDK, AdoptOpenJDK 가 그 대표적인 예이다.

이 중에서 커뮤니티 기반 빌드인 AdoptOpenJDK 가 많이 쓰이는데, AdoptOpenJDK 의 최근 변화에 대해 알아보고 JDK 17 사용 방법을 살펴보고자 한다.
 
AdoptOpenJDK 에서 Eclipse Adoptium 으로 이전

https://blog.adoptopenjdk.net/2021/08/goodbye-adoptopenjdk-hello-adoptium/
2021년 8월 2일 AdoptOpenJDK 가 Eclipse Adoptium 으로 이전되었다.
Eclipse Adoptium 는 최상위 프로젝트(TLP)를 의미하며, Eclipse Temurin 에서 Java SE 런타임을 진행한다.
Eclipse Temurin 은 오라클 SE TCK(Technology Compatibility Kit)와 Eclipse AQAvit 테스트를 통과했다.
Azul Platform Core OpenJDK 지원 구독을 통해 Temurin 에 대한 상용 지원이 가능하다고 한다.
기존의 AdoptOpenJDK 웹사이트와 AdoptOpenJDK API는 당분간 유지할 예정이나, 빠른 시일내에 Eclipse Adoptium 으로 이전할 것을 권장하고 있다.
```



### 3.2.2. STS 설치

- 다운로드 :  https://spring.io/tools

- ![image-20221023203106620](./assets/figure12.png)

- 실행

  ```
  java -jar spring-tool-suite-4-4.16.0.RELEASE-e4.25.0-win32.win32.x86_64.self-extracting.jar
  ```



### 3.2.3. Lombok 설치

- 다운로드 : https://projectlombok.org/download

![image-20221023202639158](./assets/figure13.png)

- 실행

  ```shell
  java -jar lombok.jar
  ```

- 설치

  ![image-20221023202859386](./assets/figure15.png)



### 3.2.4. Docker Desktop 설치

- Kafka를 활용한 데이타 동기화 실습 시 Kafka + Kafka Connect + MySql 구동을 위한 도커환경 구성이 필요하다.

- 다운로드 : https://www.docker.com/products/docker-desktop/

  ![image-20221109101222977](./assets/figure16.png)

- 설치
  
  - 다운로드한 파일을 실행해서 설치한다.
- 실행
  
  - 프로그램 목록에서 Docker Desktop을 실행한다.



### 3.2.5 Git Bash 설치

- Curl 등 테스트를 위해 Linux Shell 환경을 제공해주는클라이언트를 설치한다.

- 다운로드 : https://git-scm.com/downloads

![image-20221109101643467](./assets/figure17.png)

- 설치

  - 다운로드한 파일을 실행해서 설치한다.

- 실행

  - 프로그램 목록에서 Git Bash를 실행한다.

  ![image-20221109101746785](./assets/figure19.png)





## 3.3. 실습 애플리케이션 소개

실습 애플리케이션은 신용카드를 제공하는 간단한 도메인 서비스이다 해당 서비스는 아래의 기능을 가지고 있다.

- 카드에서 돈을 인출할 수 있다.(명령)
- 카드서 돈을 인출한 목록을 읽을 수 있습니다( 조회)

해당서비스에서 중요한 요구사항은 **카드에서 인출한 돈이 인출 목록에 정확하게 조회되어야 한다**.

따라서 명령과 조회의 상태를 일관성 있게 만드는 **동기화** 작업이 필요하다.



## 3.4.  주요 라이브러리

| 기술 스택         | 참고                                         |
| ----------------- | -------------------------------------------- |
| Spring Web MVC    | Rest API를 제공하는 라이브러리               |
| Spring Data JDBC  | Database와 연계하는 Spring Data의 라이브러리 |
| Spring with Kafka | Kafka와 연계하는 라이브러리                  |
| Spring Data Cache | Spring에서 Cache를 사용할 수 있는 라이브러리 |
| JAVA 17           | Eclipse Temurin JAVA 17                      |



## 3.5. 단일 프로세스로 처리되는 CRUD



### 3.5.1. 개요

- 전통적인 단일 프로세스 환경에서 도메인의 CRUD를 처리하는 방식이다.




### 3.5.2. 아키텍처 구성

<img src="./assets/figure20.png" alt="image-20221110163349664" style="zoom:150%;" />



### 3.5.3. 실습

- 프로젝트 구조

  ```
  ├─java
  │  └─com
  │      └─kt
  │          └─cqrs
  │              ├─controller
  │              ├─repository
  │              │  └─entity
  │              └─service
  └─resources
  ```

- Database

  - 파일명 : schema.sql

    ```sql
    CREATE TABLE IF NOT EXISTS CREDIT_CARD (
      ID            VARCHAR(255) PRIMARY KEY,
      INITIAL_LIMIT DECIMAL(18,2) NOT NULL,
      USED_LIMIT    DECIMAL(18,2) NOT NULL
    );
    
    CREATE TABLE IF NOT EXISTS WITHDRAWAL (
      ID     VARCHAR(255) PRIMARY KEY,
      CARD_ID   VARCHAR(255)    NOT NULL,
      AMOUNT DECIMAL(18,2) NOT NULL,
      foreign key (CARD_ID) references CREDIT_CARD(ID)
    );
    ```

  - 파일명 : data.sql

    ```sql
    INSERT INTO credit_card (ID, INITIAL_LIMIT, USED_LIMIT) VALUES
      ('3a3e99f0-5ad9-47fa-961d-d75fab32ef0e', 10000, 0);
    
    COMMIT;
    ```

-  Dependency

  -  파일명 : pom.xml
  
  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0"
  	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  	<modelVersion>4.0.0</modelVersion>
  
  	<groupId>icis.com</groupId>
  	<artifactId>monolithic</artifactId>
  	<version>0.0.1-SNAPSHOT</version>
  	<packaging>jar</packaging>
  
  
  	<parent>
  		<groupId>org.springframework.boot</groupId>
  		<artifactId>spring-boot-starter-parent</artifactId>
  		<version>2.7.2</version>
  		<relativePath /> <!-- lookup parent from repository -->
  	</parent>
  
  	<properties>
  		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
  		<java.version>17</java.version>
  		<spring-cloud.version>2021.0.3</spring-cloud.version>
  	</properties>
  
  	<dependencyManagement>
  		<dependencies>
  			<dependency>
  				<groupId>org.springframework.cloud</groupId>
  				<artifactId>spring-cloud-dependencies</artifactId>
  				<version>${spring-cloud.version}</version>
  				<type>pom</type>
  				<scope>import</scope>
  			</dependency>
  		</dependencies>
  	</dependencyManagement>
  
  	<build>
  		<plugins>
  			<plugin>
  				<groupId>org.springframework.boot</groupId>
  				<artifactId>spring-boot-maven-plugin</artifactId>
  			</plugin>
  		</plugins>
  	</build>
  
  	<dependencies>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-data-jdbc</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-web</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-actuator</artifactId>
  		</dependency>
  
  		<dependency>
  			<groupId>com.h2database</groupId>
  			<artifactId>h2</artifactId>
  		</dependency>
  
  		<dependency>
  			<groupId>org.projectlombok</groupId>
  			<artifactId>lombok</artifactId>
  			<scope>provided</scope>
  		</dependency>
  
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-test</artifactId>
  			<scope>test</scope>
  		</dependency>
  
  		<dependency>
  			<groupId>junit</groupId>
  			<artifactId>junit</artifactId>
  			<scope>test</scope>
  		</dependency>
  
          <dependency>
  			<groupId>org.springdoc</groupId>
  			<artifactId>springdoc-openapi-ui</artifactId>
  			<version>1.6.6</version>
  		</dependency>
  					
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-devtools</artifactId>
  			<scope>runtime</scope>
  			<optional>true</optional>
  		</dependency>	
  	</dependencies>
  
  </project>
  
  ```
  
-  Properties

  -  파일명 : resources/application.yml
  
  ```yaml
  server:
    port: 8080
    
  spring:     
    devtools:
      restart:
        enabled: true
    h2:
      console:
        enabled: true
        settings:
          web-allow-others: true
        path: /h2-console        
    datasource:
      url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
  ```
  
-  BootStrap

  -  파일명 : CqrsApplication.java
  
  ```java
  package com.kt.cqrs;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.web.servlet.config.annotation.EnableWebMvc;
  
  @EnableWebMvc
  @SpringBootApplication
  public class CqrsApplication {
  
  	public static void main(String[] args) {
  		SpringApplication.run(CqrsApplication.class, args);
  	}
  }
  
  ```


- Exception
  -  파일명 : NotEnoughMoneyException.java
  ```java
  package com.kt.cqrs.service;
  
  import java.util.UUID;
  
  public class NotEnoughMoneyException extends RuntimeException {
  
  	private static final long serialVersionUID = 1L;
  
  	public NotEnoughMoneyException(UUID cardNo, long wanted, long availableBalance) {
          super(String.format("Card %s not able to withdraw %s. Balance is %s", cardNo, wanted, availableBalance));
      }
  }
  ```


- Entity
  -  파일명 : CreditCard.java
  ```java
  package com.kt.cqrs.repository.entity;
  
  import java.util.UUID;
  
  import org.springframework.data.annotation.Id;
  import org.springframework.data.relational.core.mapping.Table;
  
  import lombok.AllArgsConstructor;
  import lombok.Builder;
  import lombok.Getter;
  import lombok.NoArgsConstructor;
  import lombok.Setter;
  import lombok.ToString;
  
  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  @Table("CREDIT_CARD")
  public class CreditCard {
  
      @Id
      private UUID id;
      private long initialLimit;
      private long usedLimit;
  
  }
  ```
  -  파일명 : Withdrawal.java
  ```java
  package com.kt.cqrs.repository.entity;
  
  import java.util.UUID;
  
  import org.springframework.data.annotation.Id;
  import org.springframework.data.annotation.Transient;
  import org.springframework.data.domain.Persistable;
  import org.springframework.data.relational.core.mapping.Table;
  
  import lombok.AllArgsConstructor;
  import lombok.Builder;
  import lombok.Getter;
  import lombok.NoArgsConstructor;
  import lombok.Setter;
  import lombok.ToString;
  
  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  @Table("WITHDRAWAL")
  public class Withdrawal implements Persistable<UUID>{
  
      @Transient
      private boolean isNew = false;
      
      @Id
      private UUID id;
      private long amount;
      private UUID cardId;
      
      public static Withdrawal newWithdrawal(UUID id, long amount, UUID cardId) {
      	Withdrawal withdrawal = new Withdrawal(true, id, amount, cardId);
          return withdrawal;
      }
  
      @Override
      public boolean isNew() {
          return isNew;
      }
  }
  
  ```
  
- Rpository
  -  파일명 : CreditCardRepository.java
  ```java
  package com.kt.cqrs.repository;
  import java.util.UUID;
    import org.springframework.data.repository.CrudRepository;
    import com.kt.cqrs.repository.entity.CreditCard;
  
    public interface CreditCardRepository extends CrudRepository<CreditCard, UUID> {
    }
  ```
  -  파일명 : WithdrawalRepository.java
  ```java
  package com.kt.cqrs.repository;
  
  import java.util.List;
  import java.util.UUID;
  import org.springframework.data.repository.CrudRepository;
  import com.kt.cqrs.repository.entity.Withdrawal;
  
  public interface WithdrawalRepository extends CrudRepository<Withdrawal, UUID> {
      List<Withdrawal> findByCardId(UUID cardId);
  }
  
  ```

- Service
  -  파일명 : WithdrawalService.java
  ```java
  package com.kt.cqrs.service;
  
  import java.util.List;
  import java.util.UUID;
  
  import org.springframework.stereotype.Service;
  import org.springframework.transaction.annotation.Transactional;
  
  import com.kt.cqrs.repository.CreditCardRepository;
  import com.kt.cqrs.repository.WithdrawalRepository;
  import com.kt.cqrs.repository.entity.CreditCard;
  import com.kt.cqrs.repository.entity.Withdrawal;
  
  import lombok.RequiredArgsConstructor;
  import lombok.extern.slf4j.Slf4j;
  
  @Slf4j
  @Service
  @RequiredArgsConstructor
  public class WithdrawalService {
  
  	private final CreditCardRepository creditCardRepository;
  	private final WithdrawalRepository withdrawalRepository;
  
  	@Transactional
  	public void withdraw(UUID cardId, long amount) {
  		CreditCard creditCard = creditCardRepository.findById(cardId)
  				.orElseThrow(() -> new IllegalStateException("Cannot find card with id " + cardId));
  		withdraw(creditCard, amount);
  	}
  
  	public void withdraw(CreditCard creditCard, long amount) {
  		if (thereIsMoneyToWithdraw(creditCard, amount)) {
  			creditCard.setUsedLimit(creditCard.getUsedLimit() + amount);
  			creditCardRepository.save(creditCard);
  			withdrawalRepository.save(Withdrawal.newWithdrawal(UUID.randomUUID(), amount, creditCard.getId()));
  		} else {
  			throw new NotEnoughMoneyException(creditCard.getId(), amount, availableBalance(creditCard));
  		}
  	}
  
  	public long availableBalance(CreditCard creditCard) {
  		return creditCard.getInitialLimit() - creditCard.getUsedLimit();
  	}
  
  	private boolean thereIsMoneyToWithdraw(CreditCard creditCard, long amount) {
  		return availableBalance(creditCard) >= amount;
  	}
  
  	public List<Withdrawal> withdraw(UUID cardId) {
  		return withdrawalRepository.findByCardId(cardId);
  	}
  }
  
  ```
  
- Controller
  -  파일명 : WithdrawalController.java
  ```java
  package com.kt.cqrs.controller;
  
  import java.util.List;
  import java.util.UUID;
  import javax.websocket.server.PathParam;
  import org.springframework.http.ResponseEntity;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.PostMapping;
  import org.springframework.web.bind.annotation.RequestBody;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RestController;
  import com.kt.cqrs.repository.entity.Withdrawal;
  import com.kt.cqrs.service.WithdrawalService;
  import lombok.RequiredArgsConstructor;
  
  @RestController
  @RequestMapping("/withdrawal")
  @RequiredArgsConstructor
  class WithdrawalController {
  
      private final WithdrawalService withdrawalService;
  
      @PostMapping
      ResponseEntity<?> withdraw(@RequestBody Withdrawal withdrawal) {
          withdrawalService.withdraw(withdrawal.getCardId(), withdrawal.getAmount());
          return ResponseEntity.ok().build();
      }
  
  	@GetMapping
      ResponseEntity<List<Withdrawal>> withdrawals(@PathParam("cardId") String cardId) {
          return ResponseEntity.ok().body(withdrawalService.withdraw(UUID.fromString(cardId)));
      }
  }
  ```

  

### 3.3.3. 테스트

- 카드인출(명령)

```shell
curl localhost:8080/withdrawal -X POST --header 'Content-Type: application/json' -d '{"cardId":"3a3e99f0-5ad9-47fa-961d-d75fab32ef0e", "amount": 10.00}' --verbose
```

- 인출목록 조회(쿼리)

```shell
curl http://localhost:8080/withdrawal?cardId=3a3e99f0-5ad9-47fa-961d-d75fab32ef0e --verbose
```

- 예상 결과

```shell
[{"amount":10.00}]
```

- 데이터 확인

```http
http://localhost:8080/h2-console/
```





## 3.6. 단일 프로세스로 처리되는 동기식 CQRS - Entity

### 3.6.1. 개요

- 코드레벨에서 명령과 조회를 분리

- 명령과 조회 동기로 처리

- 명령과 조회 동일하게  Domain 기반의 Entity 형태로 조회

- 구현은 간단하나 복잡한 조회 모델일 경우 구현의 어렵다.

  

### 3.6.2. 아키텍처 구성

<img src="./assets/figure21.png" alt="image-20221110163529502" style="zoom:150%;" />

### 3.6.3 실습

- 프로젝트 구성

```
├─java
│  └─com
│      └─kt
│          └─cqrs
│              ├─command
│              │  ├─controller
│              │  ├─payload
│              │  ├─repository
│              │  │  └─entity
│              │  └─service
│              └─query
│                  ├─controller
│                  ├─repository
│                  │  └─entity
│                  └─service
└─resources
```



- Database

  - 파일명 : schema.sql

    ```sql
    CREATE TABLE IF NOT EXISTS CREDIT_CARD (
      ID            VARCHAR(255) PRIMARY KEY,
      INITIAL_LIMIT DECIMAL(18,2) NOT NULL,
      USED_LIMIT    DECIMAL(18,2) NOT NULL
    );
    
    CREATE TABLE IF NOT EXISTS WITHDRAWAL (
      ID     VARCHAR(255) PRIMARY KEY,
      CARD_ID   VARCHAR(255)    NOT NULL,
      AMOUNT DECIMAL(18,2) NOT NULL,
      foreign key (CARD_ID) references CREDIT_CARD(ID)
    );
    ```

  - 파일명 : data.sql

    ```sql
    INSERT INTO credit_card (ID, INITIAL_LIMIT, USED_LIMIT) VALUES
      ('3a3e99f0-5ad9-47fa-961d-d75fab32ef0e', 10000, 0);
    
    COMMIT;
    ```

    

- Dependency

  - 파일명 :  pom.xml

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
    
        <groupId>icis.com</groupId>
        <artifactId>class-sync-with-entity</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <packaging>jar</packaging>
    
         <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>2.7.2</version>
            <relativePath/> <!-- lookup parent from repository -->
        </parent>
    
        <properties>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
            <java.version>17</java.version>
            <spring-cloud.version>2021.0.3</spring-cloud.version>
        </properties>
    
        <dependencyManagement>
            <dependencies>
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-dependencies</artifactId>
                    <version>${spring-cloud.version}</version>
                    <type>pom</type>
                    <scope>import</scope>
                </dependency>
            </dependencies>
        </dependencyManagement>
    
        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                </plugin>
            </plugins>
        </build>
    
        <dependencies>
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-starter-data-jdbc</artifactId>
    		</dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-actuator</artifactId>
            </dependency>
    
            <dependency>
                <groupId>com.h2database</groupId>
                <artifactId>h2</artifactId>
            </dependency>
    
    		<dependency>
    			<groupId>org.projectlombok</groupId>
    			<artifactId>lombok</artifactId>
    			<scope>provided</scope>
    		</dependency>
    
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-starter-test</artifactId>
    			<scope>test</scope>
    		</dependency>
    		
    		<dependency>
    			<groupId>junit</groupId>
    			<artifactId>junit</artifactId>
    			<scope>test</scope>
    		</dependency>
    		
    		<dependency>
    			<groupId>org.springdoc</groupId>
    			<artifactId>springdoc-openapi-ui</artifactId>
    			<version>1.6.6</version>
    		</dependency>
    		
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-devtools</artifactId>
    			<scope>runtime</scope>
    			<optional>true</optional>
    		</dependency>		
    
        </dependencies>
    
    
    </project>
    
    ```

- Properties

  - 파일명 : application.yml

    ```yaml
    server:
      port: 8080
      
    spring:     
      devtools:
        restart:
          enabled: true
      h2:
        console:
          enabled: true
          settings:
            web-allow-others: true
          path: /h2-console        
      datasource:
        url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
    ```

- Bootstrap

  - 파일명 : CqrsApplication.java

    ```java
    package com.kt.cqrs;
    
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.web.servlet.config.annotation.EnableWebMvc;
    
    @EnableWebMvc
    @SpringBootApplication
    public class CqrsApplication {
    
    	public static void main(String[] args) {
    		SpringApplication.run(CqrsApplication.class, args);
    	}
    }
    
    ```

- Exception

  -  파일명 : NotEnoughMoneyException.java

```java
package com.kt.cqrs.command.service;

import java.util.UUID;

public class NotEnoughMoneyException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotEnoughMoneyException(UUID cardNo, long wanted, long availableBalance) {
        super(String.format("Card %s not able to withdraw %s. Balance is %s", cardNo, wanted, availableBalance));
    }

}

```

- Command

  - Entity

    - 파일명 : CreditCard.java

      ```java
      package com.kt.cqrs.command.repository.entity;
      
      import java.util.UUID;
      
      import org.springframework.data.annotation.Id;
      import org.springframework.data.relational.core.mapping.Table;
      
      import lombok.AllArgsConstructor;
      import lombok.Builder;
      import lombok.Getter;
      import lombok.NoArgsConstructor;
      import lombok.Setter;
      import lombok.ToString;
      
      @Getter
      @Setter
      @Builder
      @AllArgsConstructor
      @NoArgsConstructor
      @ToString
      @Table("CREDIT_CARD")
      public class CreditCard {
      
          @Id
          private UUID id;
          private long initialLimit;
          private long usedLimit;
      
      }
      
      ```

    - 파일명 : Withdrawal.java

      ```java
      package com.kt.cqrs.command.repository.entity;
      
      import java.util.UUID;
      
      import org.springframework.data.annotation.Id;
      import org.springframework.data.annotation.Transient;
      import org.springframework.data.domain.Persistable;
      import org.springframework.data.relational.core.mapping.Table;
      
      import lombok.AllArgsConstructor;
      import lombok.Builder;
      import lombok.Getter;
      import lombok.NoArgsConstructor;
      import lombok.Setter;
      import lombok.ToString;
      
      @Getter
      @Setter
      @Builder
      @AllArgsConstructor
      @NoArgsConstructor
      @ToString
      @Table("WITHDRAWAL")
      public class Withdrawal implements Persistable<UUID>{
      
          @Transient
          private boolean isNew = false;
          
          @Id
          private UUID id;
          private long amount;
          private UUID cardId;
          
          public static Withdrawal newWithdrawal(UUID id, long amount, UUID cardId) {
          	Withdrawal withdrawal = new Withdrawal(true, id, amount, cardId);
              return withdrawal;
          }
      
          @Override
          public boolean isNew() {
              return isNew;
          }
      
      }
      
      ```

      

  - Rpository

    - 파일명 : CreditCardRepository.java

      ```java
      package com.kt.cqrs.command.repository;
      
      import java.util.UUID;
      import org.springframework.data.repository.CrudRepository;
      import com.kt.cqrs.command.repository.entity.CreditCard;
      
      public interface CreditCardRepository extends CrudRepository<CreditCard, UUID> {
      }
      
      ```

    - 파일명 : WithdrawalCommandRepository.java

      ```java
      package com.kt.cqrs.command.repository;
      
      import java.util.UUID;
      import org.springframework.data.repository.CrudRepository;
      import com.kt.cqrs.command.repository.entity.Withdrawal;
      
      public interface WithdrawalCommandRepository extends CrudRepository<Withdrawal, UUID> {
      }
      
      ```

      

  - Service

    - 파일명 : WithdrawalCommandService.java

      ```java
      package com.kt.cqrs.command.service;
      
      import java.util.UUID;
      
      import org.springframework.stereotype.Service;
      import org.springframework.transaction.annotation.Transactional;
      
      import com.kt.cqrs.command.repository.CreditCardRepository;
      import com.kt.cqrs.command.repository.WithdrawalRepository;
      import com.kt.cqrs.command.repository.entity.CreditCard;
      import com.kt.cqrs.command.repository.entity.Withdrawal;
      
      import lombok.RequiredArgsConstructor;
      import lombok.extern.slf4j.Slf4j;
      
      @Slf4j
      @Service
      @RequiredArgsConstructor
      public class WithdrawalCommandService {
      
          private final CreditCardRepository creditCardRepository;
          private final WithdrawalCommandRepository withdrawalCommandRepository;
      
          @Transactional
          public void withdraw(UUID cardId, long amount) {
              CreditCard creditCard = creditCardRepository.findById(cardId)
                      .orElseThrow(() -> new IllegalStateException("Cannot find card with id " + cardId));
              withdraw(creditCard, amount);
      		withdrawalCommandRepository.save(Withdrawal.newWithdrawal(UUID.randomUUID(), amount, creditCard.getId()));
          }
      
      	public void withdraw(CreditCard creditCard, long amount) {
      		if (thereIsMoneyToWithdraw(creditCard, amount)) {
      			creditCard.setUsedLimit(creditCard.getUsedLimit() + amount);
      			log.info("creditCard = {}", creditCard);
      			creditCardRepository.save(creditCard);
      		} else {
      			throw new NotEnoughMoneyException(creditCard.getId(), amount, availableBalance(creditCard));
      		}
      	}
      
      	public long availableBalance(CreditCard creditCard) {
      		return creditCard.getInitialLimit() - creditCard.getUsedLimit();
      	}
      
      	private boolean thereIsMoneyToWithdraw(CreditCard creditCard, long amount) {
      		return availableBalance(creditCard) >= amount;
      	}
      	
      }
      ```

  - Payload

    - 파일명 : WithdrawalCommand.java

      ```java
      package com.kt.cqrs.command.payload;
      
      import java.util.UUID;
      
      import lombok.AllArgsConstructor;
      import lombok.Data;
      import lombok.NoArgsConstructor;
      
      @Data
      @NoArgsConstructor
      @AllArgsConstructor
      public class WithdrawalCommand {
          private UUID card;
          private long amount;
      
      }
      
      ```

  - Controller

    - 파일명 : WithdrawalCommandController.java

      ```java
      package com.kt.cqrs.command.controller;
      
      import org.springframework.http.ResponseEntity;
      import org.springframework.web.bind.annotation.PostMapping;
      import org.springframework.web.bind.annotation.RequestBody;
      import org.springframework.web.bind.annotation.RequestMapping;
      import org.springframework.web.bind.annotation.RestController;
      
      import com.kt.cqrs.command.payload.WithdrawalCommand;
      import com.kt.cqrs.command.service.WithdrawalCommandService;
      
      import lombok.RequiredArgsConstructor;
      
      @RestController
      @RequestMapping("/withdrawal")
      @RequiredArgsConstructor
      class WithdrawalCommandController {
      	
          private final WithdrawalCommandService withdrawalCommandService;
      
          @PostMapping
          ResponseEntity<?> withdraw(@RequestBody WithdrawalCommand withdrawalCommand) {
          	withdrawalCommandService.withdraw(withdrawalCommand.getCard(), withdrawalCommand.getAmount());
              return ResponseEntity.ok().build();
          }
      
      }
      
      ```

  

- Query

  - Entity

    - 파일명 : Withdrawal.java

    ```java
    package com.kt.cqrs.query.repository.entity;
    
    import java.util.UUID;
    
    import org.springframework.data.annotation.Id;
    import org.springframework.data.relational.core.mapping.Table;
    
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import lombok.ToString;
    
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Table("WITHDRAWAL")
    public class Withdrawal{
    
        @Id
        private UUID id;
        private long amount;
        private UUID cardId;
       
    
    }
    
    ```

    

  - Repository

    - 파일명 : WithdrawalRepository.java

    ```java
    package com.kt.cqrs.query.repository;
    
    import java.util.List;
    import java.util.UUID;
    import org.springframework.data.repository.CrudRepository;
    import com.kt.cqrs.query.repository.entity.Withdrawal;
    
    public interface WithdrawalRepository extends CrudRepository<Withdrawal, UUID> {
        List<Withdrawal> findByCardId(UUID cardId);
    }
    
    ```

    

  - Service

    -  파일명 : WithdrawalQueryService.java

    ```java
    package com.kt.cqrs.query.service;
    
    import java.util.List;
    import java.util.UUID;
    
    import org.springframework.stereotype.Service;
    
    import com.kt.cqrs.query.repository.WithdrawalRepository;
    import com.kt.cqrs.query.repository.entity.Withdrawal;
    
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    
    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class WithdrawalQueryService {
    
        private final WithdrawalRepository withdrawalRepository;
    
    	public List<Withdrawal> withdraw(UUID cardId) {
    		return withdrawalRepository.findByCardId(cardId);
    	}
    }
    
    ```

  - Controller

    - 파일명 : WithdrawalQueryController.java

    ```java
    package com.kt.cqrs.query.controller;
    
    import java.util.List;
    import java.util.UUID;
    
    import javax.websocket.server.PathParam;
    
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    
    import com.kt.cqrs.query.repository.entity.Withdrawal;
    import com.kt.cqrs.query.service.WithdrawalQueryService;
    
    import lombok.RequiredArgsConstructor;
    
    @RestController
    @RequestMapping("/withdrawal")
    @RequiredArgsConstructor
    class WithdrawalQueryController {
    	
        private final WithdrawalQueryService withdrawalQueryService;
    
        @GetMapping
        ResponseEntity<List<Withdrawal>> withdrawals(@PathParam("cardId") String cardId) {
            return ResponseEntity.ok().body(withdrawalQueryService.withdraw(UUID.fromString(cardId)));
        }
    }
    
    ```


### 3.6.4 테스트

- 키드인출(명령)

```shell
curl localhost:8080/withdrawal -X POST --header 'Content-Type: application/json' -d '{"cardId":"3a3e99f0-5ad9-47fa-961d-d75fab32ef0e", "amount": 10.00}' --verbose
```

- 쿼리로 확인:

```shell
curl http://localhost:8080/withdrawal?cardId=3a3e99f0-5ad9-47fa-961d-d75fab32ef0e --verbose
```

- 예상 결과:

```shell
[{"amount":10.00}]
```

- 데이터 확인

```http
http://localhost:8080/h2-console/
```



## 3.7. 단일 프로세스로 처리되는 동기식 CQRS - Dto

### 3.7.1. 개요

- 코드레벨에서 명령과 조회를 분리

- 명령과 조회 동기로 처리

- 명령의 경우   Domain 기반의 Entity 형태로 조회

- 조희의 경우 Temaplate 또는 Native Query 기반의 Dto 형태로 조회

- 도메인의 Aggregate Root 일관성 보장 및 다양한 조회모델에 대응이 가능

  

### 3.7.2. 아키텍처 구성

<img src="./assets/figure22.png" alt="image-20221110163507335" style="zoom:150%;" />

### 3.7.3. 실습

- **프로젝트 구조**

  ```
  ├─java
  │  └─com
  │      └─kt
  │          └─cqrs
  │              ├─command
  │              │  ├─controller
  │              │  ├─payload
  │              │  ├─repository
  │              │  │  └─entity
  │              │  └─servcie
  │              └─query
  │                  ├─controller
  │                  ├─payload
  │                  └─service
  └─resources
  ```

- Database

  - 파일명 : .java schema.sql

    ```sql
    CREATE TABLE IF NOT EXISTS CREDIT_CARD (
      ID            VARCHAR(255) PRIMARY KEY,
      INITIAL_LIMIT DECIMAL(18,2) NOT NULL,
      USED_LIMIT    DECIMAL(18,2) NOT NULL
    );
    
    CREATE TABLE IF NOT EXISTS WITHDRAWAL (
      ID     VARCHAR(255) PRIMARY KEY,
      CARD_ID   VARCHAR(255)    NOT NULL,
      AMOUNT DECIMAL(18,2) NOT NULL,
      foreign key (CARD_ID) references CREDIT_CARD(ID)
    );
    ```

  - 파일명: data.sql

    ```sql
    INSERT INTO credit_card (ID, INITIAL_LIMIT, USED_LIMIT) VALUES
      ('3a3e99f0-5ad9-47fa-961d-d75fab32ef0e', 10000, 0);
    
    COMMIT;
    ```

    

- Dependency
  -  파일명 : pom.xml
  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
  
      <groupId>icis.com</groupId>
      <artifactId>class-sync-with-dto</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <packaging>jar</packaging>
      <parent>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-parent</artifactId>
          <version>2.7.2</version>
          <relativePath/> <!-- lookup parent from repository -->
      </parent>
  
      <properties>
          <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
          <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
          <java.version>17</java.version>
          <spring-cloud.version>2021.0.3</spring-cloud.version>
      </properties>
  
      <dependencyManagement>
          <dependencies>
              <dependency>
                  <groupId>org.springframework.cloud</groupId>
                  <artifactId>spring-cloud-dependencies</artifactId>
                  <version>${spring-cloud.version}</version>
                  <type>pom</type>
                  <scope>import</scope>
              </dependency>
          </dependencies>
      </dependencyManagement>
  
      <build>
          <plugins>
              <plugin>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-maven-plugin</artifactId>
              </plugin>
          </plugins>
      </build>
  
      <dependencies>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-data-jdbc</artifactId>
          </dependency>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-web</artifactId>
          </dependency>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-actuator</artifactId>
          </dependency>
  
          <dependency>
              <groupId>com.h2database</groupId>
              <artifactId>h2</artifactId>
          </dependency>
  
  		<dependency>
  			<groupId>org.projectlombok</groupId>
  			<artifactId>lombok</artifactId>
  			<scope>provided</scope>
  		</dependency>
  
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-test</artifactId>
  			<scope>test</scope>
  		</dependency>
  		
  		<dependency>
  			<groupId>junit</groupId>
  			<artifactId>junit</artifactId>
  			<scope>test</scope>
  		</dependency>
  		
  		<dependency>
  			<groupId>org.springdoc</groupId>
  			<artifactId>springdoc-openapi-ui</artifactId>
  			<version>1.6.6</version>
  		</dependency>
  		
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-devtools</artifactId>
  			<scope>runtime</scope>
  			<optional>true</optional>
  		</dependency>
      </dependencies>
  
  </project>
  
  ```
  
- Properties
  -  파일명 : application.yml
  ```yaml
  server:
    port: 8080
    
  spring:     
    devtools:
      restart:
        enabled: true
    h2:
      console:
        enabled: true
        settings:
          web-allow-others: true
        path: /h2-console        
    datasource:
      url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
  ```
  
- Bootstrap
  -  파일명 : CqrsApplication.java
  ```java
  package com.kt.cqrs;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.web.servlet.config.annotation.EnableWebMvc;
  
  @EnableWebMvc
  @SpringBootApplication
  public class CqrsApplication {
  
  	public static void main(String[] args) {
  		SpringApplication.run(CqrsApplication.class, args);
  	}
  }
  ```
  
- Exception

  -  파일명 : NotEnoughMoneyException.java
  ```java
  package com.kt.cqrs.command.servcie;

  import java.util.UUID;
  
  public class NotEnoughMoneyException extends RuntimeException {
  
  	private static final long serialVersionUID = 1L;
  
  	public NotEnoughMoneyException(UUID cardNo, long wanted, long availableBalance) {
          super(String.format("Card %s not able to withdraw %s. Balance is %s", cardNo, wanted, availableBalance));
      }
  
  }
  
  ```

- Command

  - Entity

    - 파일명 : CreditCard.java

    ```java
    package com.kt.cqrs.command.repository.entity;
    
    import java.util.UUID;
    
    import org.springframework.data.annotation.Id;
    import org.springframework.data.relational.core.mapping.Table;
    
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import lombok.ToString;
    
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Table("CREDIT_CARD")
    public class CreditCard {
    
        @Id
        private UUID id;
        private long initialLimit;
        private long usedLimit;
    
    }
    
    ```
    
  - Rpository
  
  - 파일명 : CreditCardRepository.java
  
  ```java
    package com.kt.cqrs.command.repository;
  
    import java.util.UUID;
    import org.springframework.data.repository.CrudRepository;
    import com.kt.cqrs.command.repository.entity.CreditCard;
    
    public interface CreditCardRepository extends CrudRepository<CreditCard, UUID> {
    }
  ```
  
  - Service
  
  -  파일명 : WithdrawalCommandService.java
  
  ```java
    package com.kt.cqrs.command.servcie;
  
    import java.util.UUID;
    
    import org.springframework.jdbc.core.JdbcTemplate;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import com.kt.cqrs.command.repository.CreditCardRepository;
    import com.kt.cqrs.command.repository.entity.CreditCard;
    
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    
    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class WithdrawalCommandService {
    
        private final CreditCardRepository creditCardRepository;
        private final JdbcTemplate jdbcTemplate;
    
        @Transactional
        public void withdraw(UUID cardId, long amount) {
            CreditCard creditCard = creditCardRepository.findById(cardId)
                    .orElseThrow(() -> new IllegalStateException("Cannot find card with id " + cardId));
            withdraw(creditCard, amount);
            jdbcTemplate.update("INSERT INTO WITHDRAWAL(ID, CARD_ID, AMOUNT) VALUES (?,?,?)", UUID.randomUUID(), cardId, amount);
        }
        
    	public void withdraw(CreditCard creditCard, long amount) {
    		if (thereIsMoneyToWithdraw(creditCard, amount)) {
    			creditCard.setUsedLimit(creditCard.getUsedLimit() + amount);
    			log.info("creditCard = {}", creditCard);
    			creditCardRepository.save(creditCard);
    		} else {
    			throw new NotEnoughMoneyException(creditCard.getId(), amount, availableBalance(creditCard));
    		}
    	}
    
    	public long availableBalance(CreditCard creditCard) {
    		return creditCard.getInitialLimit() - creditCard.getUsedLimit();
    	}
    
    	private boolean thereIsMoneyToWithdraw(CreditCard creditCard, long amount) {
    		return availableBalance(creditCard) >= amount;
    	}
    
    }
    
  ```
  
  - Payload
  
  - 파일명 : WithdrawalCommand.java
  
  ```java
    package com.kt.cqrs.command.payload;
  
    import java.util.UUID;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class WithdrawalCommand {
        private UUID card;
        private long amount;
    }
  ```
  
  - Controller
  
  - 파일명 : WithdrawalCommandService.java
  
  ```java
    package com.kt.cqrs.command.controller;
  
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    import com.kt.cqrs.command.payload.WithdrawalCommand;
    import com.kt.cqrs.command.servcie.WithdrawalCommandService;
    import lombok.RequiredArgsConstructor;
    
    @RestController
    @RequestMapping("/withdrawal")
    @RequiredArgsConstructor
    class WithdrawalCommandController {
    
        private final WithdrawalCommandService withdrawalService;
    
        @PostMapping
        ResponseEntity<?> withdraw(@RequestBody WithdrawalCommand withdrawalCommand) {
            withdrawalService.withdraw(withdrawalCommand.getCard(), withdrawalCommand.getAmount());
            return ResponseEntity.ok().build();
        }
    
    }
  ```
  
  
  
- Query

  - Service

    -  파일명 : WithdrawalQueryService.java

    ```java
    package com.kt.cqrs.query.service;
    
    import java.util.List;
    import java.util.UUID;
    
    import org.springframework.jdbc.core.BeanPropertyRowMapper;
    import org.springframework.jdbc.core.JdbcTemplate;
    import org.springframework.stereotype.Service;
    import com.kt.cqrs.query.payload.WithdrawalDto;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    
    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class WithdrawalQueryService {
    
        private final JdbcTemplate jdbcTemplate;
    
    	@SuppressWarnings("deprecation")
    	public List<WithdrawalDto> withdraw(UUID cardId) {
    		return jdbcTemplate.query("SELECT * FROM WITHDRAWAL WHERE CARD_ID = ?", new Object[]{cardId}, new BeanPropertyRowMapper<>(WithdrawalDto.class));
        }
    
    }
    
    ```
    
  - Payload

    - 파일명: WithdrawalDto.java

    ```java
  package com.kt.cqrs.query.payload;
    
    import java.util.UUID;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class WithdrawalDto {
        private UUID cardId;
        private long amount;
    
    }
    
    ```
    
  - Controller
  
  - 파일명 : WithdrawalQueryController.java
  
  ```java
    	package com.kt.cqrs.query.controller;
  
    import java.util.List;
    import java.util.UUID;
    import javax.websocket.server.PathParam;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    import com.kt.cqrs.query.payload.WithdrawalDto;
    import com.kt.cqrs.query.service.WithdrawalQueryService;
    import lombok.RequiredArgsConstructor;
    
    @RestController
    @RequestMapping("/withdrawal")
    @RequiredArgsConstructor
    class WithdrawalQueryController {
    
        private final WithdrawalQueryService withdrawalQueryService;
    
        @GetMapping
        ResponseEntity<List<WithdrawalDto>> withdrawals(@PathParam("cardId") String cardId) {
            return ResponseEntity.ok().body(withdrawalQueryService.withdraw(UUID.fromString(cardId)));
        }
    
    }		
  ```
  
  

### 3.7.4. 테스트

- 키드인출(명령)

```
curl localhost:8080/withdrawal -X POST --header 'Content-Type: application/json' -d '{"card":"3a3e99f0-5ad9-47fa-961d-d75fab32ef0e", "amount": 10.00}' --verbose
```

- 쿼리로 확인:

```
curl http://localhost:8080/withdrawal?cardId=3a3e99f0-5ad9-47fa-961d-d75fab32ef0e --verbose
```

- 예상 결과:

```
[{"amount":10.00}]
```

- 데이터 확인

```http
http://localhost:8080/h2-console/
```



## 3.8. 단일 서비스로 처리되는 이벤트 기반 CQRS - ApplicationEvent

### 3.8.1. 개요

- 코드레벨에서 명령과 조회를 분리

- 명령모델에 대해 이벤트 기반으로 처리를하여 도메인의 역할과 책임을 구분하여 응집도를 낮출수 있음

  

### 3.8.2. 아키텍처 구성

<img src="./assets/figure23.png" alt="image-20221110174211176" style="zoom:150%;" />

### 3.8.3 실습

- 패키지 구조

  ```
  ├─java
  │  └─com
  │      └─kt
  │          └─cqrs
  │              ├─command
  │              │  ├─controller
  │              │  ├─event
  │              │  ├─payload
  │              │  ├─repository
  │              │  │  └─entity
  │              │  └─service
  │              └─query
  │                  ├─controller
  │                  ├─payload
  │                  └─service
  ```
  
- Database

  - 파일명 : schema.sql

    ```sql
    CREATE TABLE IF NOT EXISTS CREDIT_CARD (
      ID            VARCHAR(255) PRIMARY KEY,
      INITIAL_LIMIT DECIMAL(18,2) NOT NULL,
      USED_LIMIT    DECIMAL(18,2) NOT NULL
    );
    
    CREATE TABLE IF NOT EXISTS WITHDRAWAL (
      ID     VARCHAR(255) PRIMARY KEY,
      CARD_ID   VARCHAR(255)    NOT NULL,
      AMOUNT DECIMAL(18,2) NOT NULL
    );
    ```
  
- 파일명 : data.sql
  
  ```sql
    INSERT INTO credit_card (ID, INITIAL_LIMIT, USED_LIMIT) VALUES
      ('3a3e99f0-5ad9-47fa-961d-d75fab32ef0e', 10000, 0);
    
    COMMIT;
  ```
- Dependency
  - 파일명 : pom.xml
  
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
    
        <groupId>icis.com</groupId>
        <artifactId>class-async</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <packaging>jar</packaging>
        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>2.7.2</version>
            <relativePath/> <!-- lookup parent from repository -->
        </parent>
    
        <properties>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
            <java.version>17</java.version>
            <spring-cloud.version>2021.0.3</spring-cloud.version>
        </properties>
    
        <dependencyManagement>
            <dependencies>
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-dependencies</artifactId>
                    <version>${spring-cloud.version}</version>
                    <type>pom</type>
                    <scope>import</scope>
                </dependency>
            </dependencies>
        </dependencyManagement>
    
        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                </plugin>
            </plugins>
        </build>
    
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-data-jdbc</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-actuator</artifactId>
            </dependency>
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-configuration-processor</artifactId>
    			<optional>true</optional>
    		</dependency>
            <dependency>
                <groupId>com.h2database</groupId>
                <artifactId>h2</artifactId>
            </dependency>
    
    		<dependency>
    			<groupId>org.projectlombok</groupId>
    			<artifactId>lombok</artifactId>
    			<scope>provided</scope>
    		</dependency>
    
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-starter-test</artifactId>
    			<scope>test</scope>
    		</dependency>
    
    		<dependency>
    			<groupId>junit</groupId>
    			<artifactId>junit</artifactId>
    			<scope>test</scope>
    		</dependency>
    		
    		<dependency>
    			<groupId>org.springdoc</groupId>
    			<artifactId>springdoc-openapi-ui</artifactId>
    			<version>1.6.6</version>
    		</dependency>
    
        </dependencies>
    
    
    </project>
    
    ```
- Properties
  - 파일명 : resources/application.yaml	
  
    ```yaml
    server:
      port: 8080
      
    spring:     
      devtools:
        restart:
          enabled: true
      h2:
        console:
          enabled: true
          settings:
            web-allow-others: true
          path: /h2-console        
      datasource:
        url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
    ```
- Bootstrap
  - 파일명 :  
  
    ```java
    package com.kt.cqrs;
    
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    
    @SpringBootApplication
    public class CqrsApplication {
    
    	public static void main(String[] args) {
    		SpringApplication.run(CqrsApplication.class, args);
    	}
    }
    
    ```
- Event

  - 파일명 : CardWithdraw.java

    ```java
    package com.kt.cqrs.command.event;
    
    import java.util.UUID;
    
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import lombok.ToString;
    
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public class CardWithdraw {
    
        private UUID cardNo;
        private long amount;
    
    }
    
    ```

  - 파일명 : EventHandler.java

    ```java
    package com.kt.cqrs.command.event;
    
    import java.util.UUID;
    
    import org.springframework.context.event.EventListener;
    import org.springframework.jdbc.core.JdbcTemplate;
    import org.springframework.scheduling.annotation.Async;
    import org.springframework.stereotype.Service;
    
    import lombok.RequiredArgsConstructor;
    
    @Service
    @RequiredArgsConstructor
    class EventHandler {
    
        private final JdbcTemplate jdbcTemplate;
    
        @Async
        @EventListener
        public void addWithdrawalOnCardWithdrawn(CardWithdraw event) {
            jdbcTemplate.update("INSERT INTO WITHDRAWAL(ID, CARD_ID, AMOUNT) VALUES (?,?,?)", UUID.randomUUID(), event.getCardNo(), event.getAmount());
        }
    }

    ```
    
    

- Command

  - Entity

    - 파일명 : 

    ```java
    package com.kt.cqrs.command.repository.entity;
    
    import java.util.UUID;
    
    import org.springframework.data.annotation.Id;
    import org.springframework.data.relational.core.mapping.Table;
    
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import lombok.ToString;
    
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Table("CREDIT_CARD")
    public class CreditCard {
        @Id
        private UUID id;
        private long initialLimit;
        private long usedLimit;
    }
    
    ```
    
  - Rpository

    - 파일명 : 

    ```java
    package com.kt.cqrs.command.repository;
    
    import java.util.UUID;
    import org.springframework.data.repository.CrudRepository;
    import com.kt.cqrs.command.repository.entity.CreditCard;
    
    public interface CreditCardRepository extends CrudRepository<CreditCard, UUID> {
    }
    
    ```
    
  - Service

    -  파일명 : 

    ```java
    package com.kt.cqrs.command.service;
    
    import java.util.UUID;
    
    import org.springframework.context.ApplicationEventPublisher;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    
    import com.kt.cqrs.command.repository.CreditCardRepository;
    import com.kt.cqrs.command.repository.entity.CreditCard;
    import com.kt.cqrs.event.CardWithdraw;
    
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    
    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class WithdrawalCommandService {
    
        private final CreditCardRepository creditCardRepository;
        private final ApplicationEventPublisher applicationEventPublisher;
    
        @Transactional
        public void withdraw(UUID cardId, long amount) {
            CreditCard creditCard = creditCardRepository.findById(cardId)
                    .orElseThrow(() -> new IllegalStateException("Cannot find card with id " + cardId));
            CardWithdraw event = withdraw(creditCard, amount);
            applicationEventPublisher.publishEvent(event);
        }
        
    	public CardWithdraw withdraw(CreditCard creditCard, long amount) {
    		if (thereIsMoneyToWithdraw(creditCard, amount)) {
    			creditCard.setUsedLimit(creditCard.getUsedLimit() + amount);
    			log.info("creditCard = {}", creditCard);
    			creditCardRepository.save(creditCard);
    		} else {
    			throw new NotEnoughMoneyException(creditCard.getId(), amount, availableBalance(creditCard));
    		}
    		
    		return new CardWithdraw(creditCard.getId(), amount);
    	}
    
    	public long availableBalance(CreditCard creditCard) {
    		return creditCard.getInitialLimit() - creditCard.getUsedLimit();
    	}
    
    	private boolean thereIsMoneyToWithdraw(CreditCard creditCard, long amount) {
    		return availableBalance(creditCard) >= amount;
    	}
    }
    
    ```
    
    - 파일명 : NotEnoughMoneyException.java
    
    ```
    package com.kt.cqrs.command.service;
    
    import java.util.UUID;
    
    public class NotEnoughMoneyException extends RuntimeException {
    
    	private static final long serialVersionUID = 1L;
    
    	public NotEnoughMoneyException(UUID cardNo, long wanted, long availableBalance) {
            super(String.format("Card %s not able to withdraw %s. Balance is %s", cardNo, wanted, availableBalance));
        }
    }
    ```
    
  - Payload

    - 파일명 : WithdrawalCommand.java

    ```java
    package com.kt.cqrs.command.payload;
    
    import java.util.UUID;
    
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class WithdrawalCommand {
        private UUID card;
        private long amount;
    
    }
    
    ```
    
  - Controller

    - 파일명 : WithdrawalCommandController.java

    ```java
    package com.kt.cqrs.command.controller;
    
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    
    import com.kt.cqrs.command.payload.WithdrawalCommand;
    import com.kt.cqrs.command.service.WithdrawalCommandService;
    
    import lombok.RequiredArgsConstructor;
    
    @RestController
    @RequestMapping("/withdrawal")
    @RequiredArgsConstructor
    class WithdrawalCommandController {
    
        private final WithdrawalCommandService withdrawalCommandService;
    
        @PostMapping
        ResponseEntity<?> withdraw(@RequestBody WithdrawalCommand withdrawalCommand) {
        	withdrawalCommandService.withdraw(withdrawalCommand.getCard(), withdrawalCommand.getAmount());
            return ResponseEntity.ok().build();
        }
    
    }
    
    ```

  

- Query

  - Service

    -  파일명 : WithdrawalQueryService.java

    ```java
    package com.kt.cqrs.query.service;
    
    import java.util.List;
    import java.util.UUID;
    
    import org.springframework.jdbc.core.BeanPropertyRowMapper;
    import org.springframework.jdbc.core.JdbcTemplate;
    import org.springframework.stereotype.Service;
    
    import com.kt.cqrs.query.payload.WithdrawalDto;
    
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    
    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class WithdrawalQueryService {
    
        private final JdbcTemplate jdbcTemplate;
    
    
    	@SuppressWarnings("deprecation")
    	public List<WithdrawalDto> withdraw(UUID cardId) {
    		return jdbcTemplate.query("SELECT * FROM WITHDRAWAL WHERE CARD_ID = ?", new Object[]{cardId}, new BeanPropertyRowMapper<>(WithdrawalDto.class));
        }
    }
    
    ```
    
  - Payload
  
    - 파일명 : WithdrawalDto.java
  
    ```java
    package com.kt.cqrs.query.payload;
    
    import java.util.UUID;
    
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class WithdrawalDto {
        private UUID cardId;
        private long amount;
    
    }
    
    ```
    
  - Controller
  
    - 파일명 : WithdrawalQueryController.java
  
    ```java
    package com.kt.cqrs.query.controller;
    
    import java.util.List;
    import java.util.UUID;
    
    import javax.websocket.server.PathParam;
    
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    
    import com.kt.cqrs.query.payload.WithdrawalDto;
    import com.kt.cqrs.query.service.WithdrawalQueryService;
    
    import lombok.RequiredArgsConstructor;
    
    @RestController
    @RequestMapping("/withdrawal")
    @RequiredArgsConstructor
    class WithdrawalQueryController {
    
        private final WithdrawalQueryService withdrawalQueryService;
    
        @GetMapping
        ResponseEntity<List<WithdrawalDto>> withdrawals(@PathParam("cardId") String cardId) {
            return ResponseEntity.ok().body(withdrawalQueryService.withdraw(UUID.fromString(cardId)));
        }
    
    }
    
    ```

### 3.8.3 테스트

- 키드인출(명령)

```shell
curl localhost:8080/withdrawal -X POST --header 'Content-Type: application/json' -d '{"card":"3a3e99f0-5ad9-47fa-961d-d75fab32ef0e", "amount": 10.00}' --verbose
```

- 쿼리로 확인:

```shell
curl http://localhost:8080/withdrawal?cardId=3a3e99f0-5ad9-47fa-961d-d75fab32ef0e --verbose
```

- 예상 결과:

```shell
[{"amount":10.00}]
```

- 데이터 확인

```http
http://localhost:8080/h2-console/
```



## 3.9. 단일 서비스로 처리되는 이벤트 기반 CQRS - Port and Adapter

### 3.9.1. 개요

- Hexagonal 아키텍처 기반 Port and Adapter 패턴을 적용
- 도메인 내부에서 명령과 조회를 분리
- 명령모델에 대해 이벤트 기반으로 처리를하여 도메인의 역할과 책임을 구분하여 응집도를 낮출수 있음
- 도메인 계층을 보호할 수 있음
  - 인프라가 변경되더라도 도메인이 코드가 달라지지 않음
  - 어플리케이션 복잡도가 높아짐

### 3.9.2. 아키텍처 구성

<img src="./assets/figure24.png" alt="image-20221110174238341" style="zoom:150%;" />

### 3.9.3 실습

- 패키지 구조

  ```
   main
      ├─java
      │  └─com
      │      └─kt
      │          └─cqrs
      │              ├─adapter
      │              │  ├─in
      │              │  │  ├─api
      │              │  │  └─event
      │              │  └─out
      │              │      ├─event
      │              │      └─persistence
      │              └─domain
      │                  └─port
      └─resources
  ```

- Database

  - 파일명: schema.sql

    ```sql
    CREATE TABLE IF NOT EXISTS CREDIT_CARD (
      ID            VARCHAR(255) PRIMARY KEY,
      INITIAL_LIMIT DECIMAL(18,2) NOT NULL,
      USED_LIMIT    DECIMAL(18,2) NOT NULL
    );
    
    CREATE TABLE IF NOT EXISTS WITHDRAWAL (
      ID     VARCHAR(255) PRIMARY KEY,
      CARD_ID   VARCHAR(255)    NOT NULL,
      AMOUNT DECIMAL(18,2) NOT NULL,
      foreign key (CARD_ID) references CREDIT_CARD(ID)
    );
    ```

  - 파일명: data.sql

    ```sql
    INSERT INTO credit_card (ID, INITIAL_LIMIT, USED_LIMIT) VALUES
      ('3a3e99f0-5ad9-47fa-961d-d75fab32ef0e', 10000, 0);
    
    COMMIT;
    ```
  
- Dependency
  - 파일명 : pom.xml
  
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
    
        <groupId>icis.com</groupId>
        <artifactId>class-aync-repository</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <packaging>jar</packaging>
        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>2.7.2</version>
            <relativePath/> <!-- lookup parent from repository -->
        </parent>
    
        <properties>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
            <java.version>17</java.version>
            <spring-cloud.version>2021.0.3</spring-cloud.version>
        </properties>
    
        <dependencyManagement>
            <dependencies>
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-dependencies</artifactId>
                    <version>${spring-cloud.version}</version>
                    <type>pom</type>
                    <scope>import</scope>
                </dependency>
            </dependencies>
        </dependencyManagement>
    
        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                </plugin>
            </plugins>
        </build>
    
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-data-jdbc</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-actuator</artifactId>
            </dependency>
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-configuration-processor</artifactId>
    			<optional>true</optional>
    		</dependency>
            <dependency>
                <groupId>com.h2database</groupId>
                <artifactId>h2</artifactId>
            </dependency>
            <dependency>
                <groupId>org.flywaydb</groupId>
                <artifactId>flyway-core</artifactId>
            </dependency>
    
    		<dependency>
    			<groupId>org.projectlombok</groupId>
    			<artifactId>lombok</artifactId>
    			<scope>provided</scope>
    		</dependency>
    
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-starter-test</artifactId>
    			<scope>test</scope>
    		</dependency>
    		<dependency>
    			<groupId>org.modelmapper</groupId>
    			<artifactId>modelmapper</artifactId>
    			<version>2.3.2</version>
    		</dependency>
    		<dependency>
    			<groupId>junit</groupId>
    			<artifactId>junit</artifactId>
    			<scope>test</scope>
    		</dependency>
    		
    		<dependency>
    			<groupId>org.springdoc</groupId>
    			<artifactId>springdoc-openapi-ui</artifactId>
    			<version>1.6.6</version>
    		</dependency>
    
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-devtools</artifactId>
    			<scope>runtime</scope>
    			<optional>true</optional>
    		</dependency>	
        </dependencies>
    
    
    </project>
    
    ```
  
- Properties
  - 파일명 : application.yml
  
    ```yaml
    server:
      port: 8080
      
    spring:     
      devtools:
        restart:
          enabled: true
      h2:
        console:
          enabled: true
          settings:
            web-allow-others: true
          path: /h2-console        
      datasource:
        url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
    ```
  
- Bootstrap
  - 파일명 : CqrsApplication.java
  
    ```java
    package com.kt.cqrs;
    
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.web.servlet.config.annotation.EnableWebMvc;
    
    @EnableWebMvc
    @SpringBootApplication
    public class CqrsApplication {
    
    	public static void main(String[] args) {
    		SpringApplication.run(CqrsApplication.class, args);
    	}
    }
    
    ```
  
- In Api

  - 파일명 : WithdrawalCommand.java

  ```java
  package com.kt.cqrs.adapter.in.api;
  
  import java.util.UUID;
  
  import lombok.AllArgsConstructor;
  import lombok.Data;
  import lombok.NoArgsConstructor;
  
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public class WithdrawalCommand {
      private UUID card;
      private long amount;
  }
  
  ```
  
  - 파일명 : WithdrawalController.java
  
  ```java
  package com.kt.cqrs.adapter.in.api;
  
  import java.util.ArrayList;
  import java.util.List;
  import java.util.UUID;
  
  import javax.websocket.server.PathParam;
  
  import org.modelmapper.ModelMapper;
  import org.springframework.http.ResponseEntity;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.PostMapping;
  import org.springframework.web.bind.annotation.RequestBody;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RestController;
  
  import com.kt.cqrs.domain.WithdrawalCommandService;
  import com.kt.cqrs.domain.WithdrawalQueryService;
  import com.kt.cqrs.domain.ports.Withdrawal;
  
  import lombok.AllArgsConstructor;
  
  
  @AllArgsConstructor
  @RestController
  @RequestMapping("/withdrawal")
  class WithdrawalController {
  
      private final WithdrawalCommandService withdrawalCommandService;
      private final WithdrawalQueryService withdrawalQueryService;
  
      @PostMapping
      ResponseEntity<?> withdraw(@RequestBody WithdrawalCommand withdrawalCommand) {
      	withdrawalCommandService.withdraw(withdrawalCommand.getCard(), withdrawalCommand.getAmount());
          return ResponseEntity.ok().build();
      }
  
      @GetMapping
      ResponseEntity<List<WithdrawalQuery>> withdrawals(@PathParam("cardId") String cardId) {
      	List<Withdrawal> withdrawalRecords = withdrawalQueryService.withdrawal(UUID.fromString(cardId));
          List<WithdrawalQuery> result = new ArrayList<>();
          withdrawalRecords.forEach(v -> result.add(new ModelMapper().map(v, WithdrawalQuery.class)));
          return ResponseEntity.ok().body(result);
      }
  
  }
  
  ```
  
  - 파일명 : WithdrawalQuery.java
  
  ```java
  package com.kt.cqrs.adapter.in.api;
  
  import java.util.UUID;
  
  import lombok.AllArgsConstructor;
  import lombok.Data;
  import lombok.NoArgsConstructor;
  
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public class WithdrawalQuery {
      private UUID cardId;
      private long amount;
  }
  
  ```
  
  
  
- In event

  - 파일명 : EventSubscriber.java

  ```java
  package com.kt.cqrs.adapter.in.event;
  
  import java.util.UUID;
  
  import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
  import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
  
  import com.kt.cqrs.domain.port.CardWithdrawn;
  
  import lombok.extern.slf4j.Slf4j;
  
  @Slf4j
  @Component
  class EventSubscriber {
  
      private final JdbcTemplate jdbcTemplate;
  
      EventSubscriber(JdbcTemplate jdbcTemplate) {
          this.jdbcTemplate = jdbcTemplate;
      }
      
      @Async
      @EventListener
      public void addWithdrawalOnCardWithdrawn(CardWithdrawn event) {
      	log.info("subscribeEvent = {}", event);
          jdbcTemplate.update("INSERT INTO WITHDRAWAL(ID, CARD_ID, AMOUNT) VALUES (?,?,?)", UUID.randomUUID(), event.getCardNo(), event.getAmount());
      }
  }
  
  ```
  
  
  
- Out event

  -  파일명 : EventPublisherAdapter.java

  ```java
  package com.kt.cqrs.adapter.out.event;
  
  import org.springframework.context.ApplicationEventPublisher;
  import org.springframework.stereotype.Component;
  
  import com.kt.cqrs.adapter.in.event.DomainEvent;
  import com.kt.cqrs.domain.ports.EventPublisher;
  
  import lombok.AllArgsConstructor;
  
  @Component
  @AllArgsConstructor
  public class EventPublisherAdapter implements EventPublisher {
  
  	private final ApplicationEventPublisher applicationEventPublisher;
  	
  	@Override
  	public void publishEvent(DomainEvent event) {
  		applicationEventPublisher.publishEvent(event);
  		
  	}
  
  }
  ```

- Persistence

  - 파일명 :  CreditCardJdbcRepository.java

  ```java
  package com.kt.cqrs.adapter.out.persistence;
  
  import java.util.UUID;
  import org.springframework.data.repository.CrudRepository;
  import com.kt.cqrs.domain.ports.CreditCard;
  
  public interface CreditCardJdbcRepository extends CrudRepository<CreditCard, UUID> {
  }
  
  ```

  - 파일명 : CreditCardRepositoryAdapter.java

  ```java
  package com.kt.cqrs.adapter.out.persistence;
  
  import lombok.AllArgsConstructor;
  import org.springframework.stereotype.Repository;
  
  import com.kt.cqrs.domain.port.CreditCard;
  import com.kt.cqrs.domain.port.CreditCardRepository;
  
  import java.util.Optional;
  import java.util.UUID;
  
  @Repository
  @AllArgsConstructor
  public class CreditCardRepositoryAdapter implements CreditCardRepository {
  
      private final CreditCardJdbcRepository creditCardJdbcRepository;
  
      @Override
      public Optional<CreditCard> load(UUID cardId) {
          return creditCardJdbcRepository.findById(cardId);
      }
  
      @Override
      public void save(CreditCard record) {
      	creditCardJdbcRepository.save(record);
      }
  }
  
  ```

  - 파일명 : WithdrawalJdbcRepository.java

  ```java
  package com.kt.cqrs.adapter.out.persistence;
  
  import java.util.List;
  import java.util.UUID;
  
  import org.springframework.data.repository.CrudRepository;
  
  import com.kt.cqrs.domain.port.Withdrawal;
  
  public interface WithdrawalJdbcRepository extends CrudRepository<Withdrawal, UUID> {
  	List<Withdrawal> findByCardId(UUID cardId);
  }
  
  ```

  - 파일명 : WithdrawalRepositoryAdapter.java

  ```java
  package com.kt.cqrs.adapter.out.persistence;
  
  import java.util.List;
  import java.util.UUID;
  
  import org.springframework.stereotype.Repository;
  
  import com.kt.cqrs.domain.port.Withdrawal;
  import com.kt.cqrs.domain.port.WithdrawalRepository;
  
  import lombok.AllArgsConstructor;
  
  @Repository
  @AllArgsConstructor
  public class WithdrawalRepositoryAdapter implements WithdrawalRepository {
  
      private final WithdrawalJdbcRepository withdrawalJdbcRepository;
  
      @Override
      public List<Withdrawal> list(UUID cardId) {
          return withdrawalJdbcRepository.findByCardId(cardId);
      }
  }
  
  ```

- Domian

  - port

    -  파일명 : CardWithdrawn.java

    ```java
    package com.kt.cqrs.domain.port;
    
    import java.util.UUID;
    
    import com.kt.cqrs.adapter.in.event.DomainEvent;
    
    import lombok.Value;
    
    @Value
    public class CardWithdrawn implements DomainEvent {
    
        private final UUID cardNo;
        private final long amount;
    
    }
    
    ```

    - 파일명 : CreditCard.java

    ```java
    package com.kt.cqrs.domain.port;
    
    import java.util.UUID;
    
    import org.springframework.data.annotation.Id;
    import org.springframework.data.relational.core.mapping.Table;
    
    import com.kt.cqrs.adapter.in.event.DomainEvent;
    
    import lombok.Data;
    import lombok.NoArgsConstructor;
    
    @Data
    @NoArgsConstructor
    @Table(name = "CREDIT_CARD")
    public class CreditCard {
    
        @Id
        private UUID id;
        private long initialLimit;
        private long usedLimit;
    
    
        public void apply(DomainEvent event) {
            if (event instanceof CardWithdrawn) {
                this.usedLimit = usedLimit + ((CardWithdrawn) event).getAmount();
            } else {
                throw new IllegalStateException("Event: " + event.getClass().getSimpleName() + "not handled");
            }
        }
    }
    
    ```
    - 파일명 : CreditCardDao.java

    ```java
    package com.kt.cqrs.domain.port;
    
    import java.util.Optional;
    import java.util.UUID;
    
    public interface CreditCardRepository {
    
        Optional<CreditCard> load(UUID cardId);
    
        void save(CreditCard record);
    }
    
    ```

    - 파일명 : EventPublisher.java

    ```java
    package com.kt.cqrs.domain.port;
    
    import com.kt.cqrs.adapter.in.event.DomainEvent;
    
    public interface EventPublisher {
    	void publishEvent(DomainEvent event);
    }
    
    ```

    - 파일명 : DomainEvent.java

    ```java
    package com.kt.cqrs.domain.port;
    
    public interface DomainEvent {
    }
    
    ```
  
    - 파일명 : Withdrawal.java
  
    ```java
    package com.kt.cqrs.domain.port;
    
    import java.util.UUID;
    
    import org.springframework.data.annotation.Id;
    import org.springframework.data.relational.core.mapping.Table;
    
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import lombok.ToString;
    
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Table("WITHDRAWAL")
  public class Withdrawal {
        
  	@Id
        private UUID id;
        private long amount;
        private UUID cardId;
    
    }
    
    ```
  
    - 파일명 : WithdrawalRepository.java
  
    ```java
    package com.kt.cqrs.domain.port;
    
    import java.util.List;
    import java.util.UUID;
    
    public interface WithdrawalRepository {
    
        List<Withdrawal> list(UUID cardId);
    
    }
    
    ```
  
  - Service
  
    - 파일명 :  NotEnoughMoneyException.java
  
    ```java
  package com.kt.cqrs.domain.service;
    
    import java.util.UUID;
    
    public class NotEnoughMoneyException extends RuntimeException {
  
    	private static final long serialVersionUID = 1L;
    
    	public NotEnoughMoneyException(UUID cardNo, long wanted, long availableBalance) {
            super(String.format("Card %s not able to withdraw %s. Balance is %s", cardNo, wanted, availableBalance));
        }
    }
    ```
  
    - 파일명 : WithdrawalCommandService.java
  
    ```java
  package com.kt.cqrs.domain.service;
    
    import java.util.UUID;
    
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    
    import com.kt.cqrs.domain.port.CardWithdrawn;
    import com.kt.cqrs.domain.port.CreditCard;
    import com.kt.cqrs.domain.port.CreditCardRepository;
    import com.kt.cqrs.domain.port.EventPublisher;
    
    import lombok.AllArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    
    @Slf4j
    @Service
    @AllArgsConstructor
    public class WithdrawalCommandService {
    
        private final CreditCardRepository creditCardRepository;
        private final EventPublisher eventPublisher;
    
        @Transactional
        public void withdraw(UUID cardId, long amount) {
        	CreditCard creditCard = creditCardRepository.load(cardId)
                    .orElseThrow(() -> new IllegalStateException("Cannot find card with id " + cardId));
        	CardWithdrawn event = withdraw(creditCard, amount);
        	eventPublisher.publishEvent(event);
        }
        
    	public CardWithdrawn withdraw(CreditCard creditCard, long amount) {
    		if (thereIsMoneyToWithdraw(creditCard, amount)) {
    			creditCard.setUsedLimit(creditCard.getUsedLimit() + amount);
    			log.info("creditCard = {}", creditCard);
    			creditCardRepository.save(creditCard);
    		} else {
    			throw new NotEnoughMoneyException(creditCard.getId(), amount, availableBalance(creditCard));
    		}
    		
    		return new CardWithdrawn(creditCard.getId(), amount);
    	}
    
    	public long availableBalance(CreditCard creditCard) {
    		return creditCard.getInitialLimit() - creditCard.getUsedLimit();
  	}
    
    	private boolean thereIsMoneyToWithdraw(CreditCard creditCard, long amount) {
    		return availableBalance(creditCard) >= amount;
    	}
    }
    
    ```
  
    - 파일명 : WithdrawalQueryService.java
  
    ```java
  package com.kt.cqrs.domain.service;
    
    import java.util.List;
    import java.util.UUID;
    
    import org.springframework.stereotype.Service;
    
    import com.kt.cqrs.domain.port.Withdrawal;
    import com.kt.cqrs.domain.port.WithdrawalRepository;
    
    import lombok.AllArgsConstructor;
    
    @Service
    @AllArgsConstructor
    public class WithdrawalQueryService {
    
    	 private final WithdrawalRepository withdrawalRepository;
    	 
    	public List<Withdrawal> withdrawal(UUID cardId) {
    		return withdrawalRepository.list(cardId);
    	}
    }
    
    ```
  

> ** 명령과 조회서비스로 분리해 본다.**



### 3.9.4. **테스트**

- 키드인출(명령)

```shell
curl localhost:8080/withdrawal -X POST --header 'Content-Type: application/json' -d '{"card":"3a3e99f0-5ad9-47fa-961d-d75fab32ef0e", "amount": 10.00}' --verbose
```

- 쿼리로 확인:

```shell
curl http://localhost:8080/withdrawal?cardId=3a3e99f0-5ad9-47fa-961d-d75fab32ef0e --verbose
```

- 예상 결과:

```shell
[{"amount":10.00}]
```

- 데이터 확인

```http
http://localhost:8080/h2-console/
```





## 3.10. 단일 프로세스로 처리되는 Trigger 기반 CQRS  - H2 Trigger 



### 3.10.1. 개요

- 코드레벨에서 명령과 조회를 분리
- 명령과 조회 모델의 데이타 일관성을 위해 Database 기능을 통해 처리
  - Database의 추가 오버헤드 발생
- 코드의 복잡도 감소
- 향후 프로세스 분리 시 비용 감소
- 솔루션 유지보수 비용 증가 및 솔루션 선택의 제약사항 발생
- 데이타 일관성을 보장하지 않으므로 보정을 할 수 있는 별도 방안 고려 필요.

### 3.10.2. 아키텍처 구성

<img src="./assets/figure26.png" alt="image-20221110163657186" style="zoom:150%;" />

### 3.10.3 실습

- 패키지 구조

  ```
  ├─java
  │  └─com
  │      └─kt
  │          └─cqrs
  │              ├─command
  │              │  ├─controller
  │              │  ├─payload
  │              │  ├─repository
  │              │  │  └─entity
  │              |  ├─event
  │              │  └─service
  │              └─query
  │                  ├─controller
  │                  ├─payload
  │                  └─service
  └─resources
  ```

- Database

  - 파일명: schema.sql

    ```sql
    CREATE TABLE IF NOT EXISTS CREDIT_CARD (
      ID            UUID PRIMARY KEY,
      INITIAL_LIMIT DECIMAL(18,2) NOT NULL,
      USED_LIMIT    DECIMAL(18,2) NOT NULL
    );
    
    CREATE TABLE IF NOT EXISTS WITHDRAWAL (
      ID     UUID PRIMARY KEY,
      CARD_ID   UUID    NOT NULL,
      AMOUNT DECIMAL(18,2) NOT NULL
    );
    
    CREATE TRIGGER ON_CARD_WITHDRAWN
    AFTER UPDATE
      ON CREDIT_CARD
    FOR EACH ROW
    CALL "com.kt.cqrs.command.event.EventHandler";
    ```
  
  - 파일명: data.sql
  
    ```sql
    INSERT INTO credit_card (ID, INITIAL_LIMIT, USED_LIMIT) VALUES
      ('3a3e99f0-5ad9-47fa-961d-d75fab32ef0e', 10000, 0);
    
    COMMIT;
    ```
  
- Dependency
  -  파일명 : 
  ```java
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
  
      <groupId>icis.com</groupId>
      <artifactId>service-trigger</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <packaging>jar</packaging>
  
      <parent>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-parent</artifactId>
          <version>2.7.2</version>
          <relativePath/> <!-- lookup parent from repository -->
      </parent>
  
      <properties>
          <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
          <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
          <java.version>17</java.version>
          <spring-cloud.version>2021.0.3</spring-cloud.version>
      </properties>
  
      <dependencyManagement>
          <dependencies>
              <dependency>
                  <groupId>org.springframework.cloud</groupId>
                  <artifactId>spring-cloud-dependencies</artifactId>
                  <version>${spring-cloud.version}</version>
                  <type>pom</type>
                  <scope>import</scope>
              </dependency>
          </dependencies>
      </dependencyManagement>
  
      <build>
          <plugins>
              <plugin>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-maven-plugin</artifactId>
              </plugin>
          </plugins>
      </build>
  
      <dependencies>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-data-jdbc</artifactId>
          </dependency>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-web</artifactId>
          </dependency>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-actuator</artifactId>
          </dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-configuration-processor</artifactId>
  			<optional>true</optional>
  		</dependency>
          <dependency>
              <groupId>com.h2database</groupId>
              <artifactId>h2</artifactId>
          </dependency>
  
  		<dependency>
  			<groupId>org.projectlombok</groupId>
  			<artifactId>lombok</artifactId>
  			<scope>provided</scope>
  		</dependency>
  
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-test</artifactId>
  			<scope>test</scope>
  		</dependency>
  
  		<dependency>
  			<groupId>junit</groupId>
  			<artifactId>junit</artifactId>
  			<scope>test</scope>
  		</dependency>
  		
  		<dependency>
  			<groupId>org.springdoc</groupId>
  			<artifactId>springdoc-openapi-ui</artifactId>
  			<version>1.6.6</version>
  		</dependency>
  		
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-devtools</artifactId>
  			<scope>runtime</scope>
  			<optional>true</optional>
  		</dependency>
  
      </dependencies>
  
  </project>
  
  ```
  
- Properties
  -  파일명 : application.yml
  ```yaml
  server:
    port: 8080
    
  spring:     
    devtools:
      restart:
        enabled: true
    h2:
      console:
        enabled: true
        settings:
          web-allow-others: true
        path: /h2-console        
    datasource:
      url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
  ```
  
- Bootstrap
  -  파일명 : CqrsApplication.java
  ```java
  package com.kt.cqrs;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.web.servlet.config.annotation.EnableWebMvc;
  
  @EnableWebMvc
  @SpringBootApplication
  public class CqrsApplication {
  
  	public static void main(String[] args) {
  		SpringApplication.run(CqrsApplication.class, args);
  	}
  }
  
  ```
  
- Exception

  -  파일명 : NotEnoughMoneyException.java
  
  ```java
  package com.kt.cqrs.command.service;
  
  import java.util.UUID;
  
  public class NotEnoughMoneyException extends RuntimeException {
  
  	private static final long serialVersionUID = 1L;
  
  	public NotEnoughMoneyException(UUID cardNo, long wanted, long availableBalance) {
          super(String.format("Card %s not able to withdraw %s. Balance is %s", cardNo, wanted, availableBalance));
      }
  }
  
  ```
- Command

  - Entity

    - 파일명 : CreditCard.java

    ```java
    package com.kt.cqrs.command.repository.entity;
    
    import java.util.UUID;
    
    import org.springframework.data.annotation.Id;
    import org.springframework.data.relational.core.mapping.Table;
    
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import lombok.ToString;
    
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Table("CREDIT_CARD")
    public class CreditCard {
    
        @Id
        private UUID id;
        private long initialLimit;
        private long usedLimit;
    
    }
    
    ```
    
  - Rpository

    - 파일명 : CreditCardRepository.java

    ```java
    package com.kt.cqrs.command.repository;
    
    import java.util.UUID;
    
    import org.springframework.data.repository.CrudRepository;
    
    import com.kt.cqrs.command.repository.entity.CreditCard;
    
    public interface CreditCardRepository extends CrudRepository<CreditCard, UUID> {
    
    }
    
    ```
    
  - Service

    -  파일명 : WithdrawalCommandService.java

    ```java
    package com.kt.cqrs.command.service;
    
    import java.util.UUID;
    
    import org.springframework.jdbc.core.JdbcTemplate;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    
    import com.kt.cqrs.command.repository.CreditCardRepository;
    import com.kt.cqrs.command.repository.entity.CreditCard;
    
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    
    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class WithdrawalCommandService {
    
        private final CreditCardRepository creditCardRepository;
        private final JdbcTemplate jdbcTemplate;
    
    
        @Transactional
        public void withdraw(UUID cardId, long amount) {
            CreditCard creditCard = creditCardRepository.findById(cardId)
                    .orElseThrow(() -> new IllegalStateException("Cannot find card with id " + cardId));
            withdraw(creditCard, amount);
        }
        
        public void withdraw(CreditCard creditCard, long amount) {
    		if (thereIsMoneyToWithdraw(creditCard, amount)) {
    			creditCard.setUsedLimit(creditCard.getUsedLimit() + amount);
    			log.info("creditCard = {}", creditCard);
    			creditCardRepository.save(creditCard);
    		} else {
    			throw new NotEnoughMoneyException(creditCard.getId(), amount, availableBalance(creditCard));
    		}
    	}
    
    	public long availableBalance(CreditCard creditCard) {
    		return creditCard.getInitialLimit() - creditCard.getUsedLimit();
    	}
    
    	private boolean thereIsMoneyToWithdraw(CreditCard creditCard, long amount) {
    		return availableBalance(creditCard) >= amount;
    	}
    
    }
    ```
    
  - Payload

    - 파일명 : WithdrawalCommand.java

    ```java
    package com.kt.cqrs.command.payload;
    
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    
    
    import java.util.UUID;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class WithdrawalCommand {
        private UUID card;
        private long amount;
    
    }
    
    ```
    
  - Controller

    - 파일명 : WithdrawalCommandController.java

    ```java
    package com.kt.cqrs.command.controller;
    
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    
    import com.kt.cqrs.command.payload.WithdrawalCommand;
    import com.kt.cqrs.command.service.WithdrawalCommandService;
    
    import lombok.RequiredArgsConstructor;
    
    @RestController
    @RequestMapping("/withdrawal")
    @RequiredArgsConstructor
    class WithdrawalCommandController {
    
        private final WithdrawalCommandService withdrawalCommandService;
    
        @PostMapping
        ResponseEntity<?> withdraw(@RequestBody WithdrawalCommand withdrawalCommand) {
        	withdrawalCommandService.withdraw(withdrawalCommand.getCard(), withdrawalCommand.getAmount());
            return ResponseEntity.ok().build();
        }
    
    }
    
    ```

- Query

  - Event
    - 파일명 : CreditCardUsedTrigger.java
  
    ```java
      package com.kt.cqrs.query.event;
      
      import java.math.BigDecimal;
      import java.sql.Connection;
      import java.sql.PreparedStatement;
      import java.sql.SQLException;
      import java.util.UUID;
      
      import org.h2.api.Trigger;
      
      public class CreditCardUsedTrigger implements Trigger {
      
          @Override
          public void init(Connection connection, String s, String s1, String s2, boolean b, int i) throws SQLException {
      
          }
      
          @Override
          public void fire(Connection connection, Object[] before, Object[] after) throws SQLException {
              try (PreparedStatement stmt = connection.prepareStatement(
                      "INSERT INTO WITHDRAWAL (ID, CARD_ID, AMOUNT) " + "VALUES (?, ?, ?)")) {
                  stmt.setObject(1, UUID.randomUUID()); //generate withdrawal id
                  stmt.setObject(2, cardId(after));
                  stmt.setObject(3, getUsedLimitChange(before, after));
      
                  stmt.executeUpdate();
              }
          }
      
          private Object cardId(Object[] cardRow) {
              return cardRow[0];
          }
      
          private BigDecimal getUsedLimitChange(Object[] oldCardRow, Object[] newCardRow) {
              return ((BigDecimal) newCardRow[2]).subtract((BigDecimal) oldCardRow[2]);
          }
      
          @Override
          public void close() throws SQLException {
      
          }
      
          @Override
          public void remove() throws SQLException {
      
          }
      }
      
    ```
  
  - Service
  
    -  파일명 : WithdrawalQueryService.java
  
    ```java
    package com.kt.cqrs.query.service;
    
    import java.util.List;
    import java.util.UUID;
    
    import org.springframework.jdbc.core.BeanPropertyRowMapper;
    import org.springframework.jdbc.core.JdbcTemplate;
    import org.springframework.stereotype.Service;
    import org.springframework.web.bind.annotation.PathVariable;
    
    import com.kt.cqrs.query.controller.WithdrawalDto;
    
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    
    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class WithdrawalQueryService {
    
        private final JdbcTemplate jdbcTemplate;
    
    	
        @SuppressWarnings("deprecation")
    	public List<WithdrawalDto> withdrawal(@PathVariable UUID cardId) {
            return jdbcTemplate.query("SELECT * FROM WITHDRAWAL WHERE CARD_ID = ?", new Object[]{cardId},
                    new BeanPropertyRowMapper<>(WithdrawalDto.class));
        } 
    }
    
    ```
  
  - Payload
  
    - 파일명 : WithdrawalDto.java
  
    ```java
    package com.kt.cqrs.query.payload;
    
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    
    
    import java.util.UUID;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class WithdrawalDto {
        private UUID cardId;
        private long amount;
    
    }
    
    ```
  
  - Controller
  
    - 파일명 : WithdrawalQueryController.java
  
    ```java
    package com.kt.cqrs.query.controller;
    
    import java.util.List;
    import java.util.UUID;
    
    import javax.websocket.server.PathParam;
    
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    
    import com.kt.cqrs.query.payload.WithdrawalDto;
    import com.kt.cqrs.query.service.WithdrawalQueryService;
    
    import lombok.RequiredArgsConstructor;
    
    @RestController
    @RequestMapping("/withdrawal")
    @RequiredArgsConstructor
    class WithdrawalQueryController {
    
        private final WithdrawalQueryService withdrawalQueryService;
    
        @GetMapping
        ResponseEntity<List<WithdrawalDto>> withdrawals(@PathParam("cardId") String cardId) {
            return ResponseEntity.ok().body(withdrawalQueryService.withdrawal(UUID.fromString(cardId)));
        }
    }
    ```
### 3.10.4. 테스트

- 키드인출(명령)

```shell
curl localhost:8080/withdrawal -X POST --header 'Content-Type: application/json' -d '{"card":"3a3e99f0-5ad9-47fa-961d-d75fab32ef0e", "amount": 10.00}' --verbose
```

- 쿼리로 확인:

```shell
curl http://localhost:8080/withdrawal?cardId=3a3e99f0-5ad9-47fa-961d-d75fab32ef0e --verbose
```

- 예상 결과:

```shell
[{"amount":10.00}]
```

- 데이터 확인

```http
http://localhost:8080/h2-console/
```



## 3.11. 분리된 프로세스로 처리되는 이벤트 기반 CQRS  - Kafka

### 3.11.1. 개요

- 다른 프로세스 명령과 조회를 분리
- 확장성과 가용성 향상
- 별도 이벤트스토리지를 통한 이벤트 유실 고려
  - 향후 이벤트소싱 패턴으로 발전 가능
- 메세징 솔루션 기반으로 코드 복잡도 증가
- 솔루션 유지보수 비용 증가 및 솔루션 선택의 제약사항 발생
- 데이타 일관성을 보장하지 않으므로 보정을 할 수 있는 별도 방안 고려 필요.

### 3.11.2. 아키텍처 구성

<img src="./assets/figure27.png" alt="image-20221111142121633" style="zoom:150%;" />

### 3.11.3. 실습 - Docker 환경 구성

- 해당 실습은 Docker 기반에서 진행하므로 사전에 Docker 파일과 Docker Compose 파일을 준비한다.
  - 파일명 : docker-compose

    ```yaml
    version: "3.1"
    
    services:
      command:
        build: ./service-db-event-command
        ports:
          - 8080:8080
        links:
          - kafka
    
      query:
        build: ./service-db-event-query
        ports:
          - 8888:8888
        links:
          - kafka
    
      zookeeper:
        image: debezium/zookeeper:0.8
        ports:
         - 2181:2181
         - 2888:2888
         - 3888:3888
      kafka:
        image: debezium/kafka:0.8
        ports:
         - 9092:9092
        links:
         - zookeeper
        environment:
         - ZOOKEEPER_CONNECT=zookeeper:2181
         - ADVERTISED_LISTENERS=PLAINTEXT://host.docker.internal:9092
         - KAFKA_CREATE_TOPICS=domain-event 
    
    ```
    

### 3.11.4. 실습 - 명령 서비스

- 패키지 구조

  ```
  ├─java
  │  └─com
  │      └─kt
  │          └─cqrs
  │              └─command
  │                  ├─controller
  │                  ├─event
  │                  ├─payload
  │                  ├─repository
  │                  │  └─entity
  │                  └─service
  └─resources
  ```

- Docker

  - 파일명 : Dockerfile

    ```shell
    # Start with a base image containing Java runtime
    FROM eclipse-temurin:17-jdk
    
    # Add a volume pointing to /tmp
    VOLUME /tmp
    
    # Make port 8080 available to the world outside this container
    EXPOSE 8080
    
    # The application's jar file
    ARG JAR_FILE=target/service-db-event-command-0.0.1-SNAPSHOT.jar
    
    # Add the application's jar to the container
    ADD ${JAR_FILE} service-db-event-command.jar
  
    # Run the jar file 
  ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/service-db-event-command.jar"]
    ```
  
- Database

  - 파일명: schema.sql

    ```sql
    CREATE TABLE IF NOT EXISTS CREDIT_CARD (
      id            VARCHAR(255) PRIMARY KEY,
      initial_limit DECIMAL(18,2) NOT NULL,
      used_limit    DECIMAL(18,2) NOT NULL
    );
    
    CREATE TABLE IF NOT EXISTS STORED_DOMAIN_EVENT (
      id     VARCHAR(255) PRIMARY KEY,
      content   VARCHAR2(4096)    NOT NULL,
      sent   BOOLEAN    NOT NULL,
      event_timestamp   DATETIME   NOT NULL,
      event_type VARCHAR2(128) NOT NULL
    );
    ```

  - 파일명: data.sql

    ```sql
    INSERT INTO credit_card (ID, INITIAL_LIMIT, USED_LIMIT) VALUES
      ('3a3e99f0-5ad9-47fa-961d-d75fab32ef0e', 10000, 0);
    COMMIT;
    ```

- Dependency

  -  파일명 :  pom.xml

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
  
      <groupId>com.kt.cqrs</groupId>
      <artifactId>service-db-event-command</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <packaging>jar</packaging>
  
      <parent>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-parent</artifactId>
          <version>2.7.2</version>
          <relativePath/> <!-- lookup parent from repository -->
      </parent>
  
      <properties>
          <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
          <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
          <java.version>17</java.version>
          <spring-cloud.version>2021.0.3</spring-cloud.version>
      </properties>
  
      <dependencyManagement>
          <dependencies>
              <dependency>
                  <groupId>org.springframework.cloud</groupId>
                  <artifactId>spring-cloud-dependencies</artifactId>
                  <version>${spring-cloud.version}</version>
                  <type>pom</type>
                  <scope>import</scope>
              </dependency>
          </dependencies>
      </dependencyManagement>
  
      <build>
          <plugins>
              <plugin>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-maven-plugin</artifactId>
              </plugin>
          </plugins>
      </build>
  
  
      <dependencies>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-data-jdbc</artifactId>
          </dependency>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-web</artifactId>
          </dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-configuration-processor</artifactId>
  			<optional>true</optional>
  		</dependency>        
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-actuator</artifactId>
          </dependency>
  
          <dependency>
              <groupId>com.h2database</groupId>
              <artifactId>h2</artifactId>
          </dependency>
  
  		<dependency>
  			<groupId>org.projectlombok</groupId>
  			<artifactId>lombok</artifactId>
  			<scope>provided</scope>
  		</dependency>
  		
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-devtools</artifactId>
  			<scope>runtime</scope>
  			<optional>true</optional>
  		</dependency>
  		
  		<dependency>
  			<groupId>org.springframework.kafka</groupId>
  			<artifactId>spring-kafka</artifactId>
  		</dependency>		
  
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-test</artifactId>
  			<scope>test</scope>
  		</dependency>
  
  		<dependency>
  			<groupId>junit</groupId>
  			<artifactId>junit</artifactId>
  			<scope>test</scope>
  		</dependency>
  		
  		<dependency>
  			<groupId>org.springdoc</groupId>
  			<artifactId>springdoc-openapi-ui</artifactId>
  			<version>1.6.6</version>
  		</dependency>
  
      </dependencies>
  
  </project>
  
  ```

- Properties

  -  파일명 :  application.yml

  ```java
  server:
    port: 8080
    
  spring:     
    devtools:
      restart:
        enabled: true
    h2:
      console:
        enabled: true
        settings:
          web-allow-others: true
        path: /h2-console        
    datasource:
      url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
      
    kafka:
      bootstrap-servers:
      - kafka:9092
      consumer:
        group-id: consumerGroup
        auto-offset-reset: earliest
        key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
        value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      listener:      
         ack-mode: MANUAL_IMMEDIATE 
   
  ```

- Exception

  -  파일명 : NotEnoughMoneyException.java	
  
  ```java
  package com.kt.cqrs.command.service;
  
  import java.util.UUID;
  
  public class NotEnoughMoneyException extends RuntimeException {
  
  	private static final long serialVersionUID = 1L;
  
  	public NotEnoughMoneyException(UUID cardNo, long wanted, long availableBalance) {
          super(String.format("Card %s not able to withdraw %s. Balance is %s", cardNo, wanted, availableBalance));
      }
  }
  ```

- Bootstrap

  -  파일명 : CqrsApplication.java

  ```java
  package com.kt.cqrs.command;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.kafka.annotation.EnableKafka;
  import org.springframework.scheduling.annotation.EnableScheduling;
  import org.springframework.web.servlet.config.annotation.EnableWebMvc;
  
  @EnableWebMvc
  @EnableKafka
  @SpringBootApplication
  @EnableScheduling
  public class CqrsApplication {
  
  	public static void main(String[] args) {
  		SpringApplication.run(CqrsApplication.class, args);
  	}
  }
  
  ```

- Command

  - Entity

    - 파일명 : CardWithdrawn.java

    ```java
    package com.kt.cqrs.command.repository.entity;
    
    
    import java.util.Date;
    import java.util.UUID;
    
    import com.kt.cqrs.command.event.DomainEvent;
    
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import lombok.ToString;
    
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public class CardWithdrawn implements DomainEvent {
    
        private UUID cardNo;
        private long amount;
        private Date timestamp = new Date();
    
        public CardWithdrawn(UUID cardNo, long amount) {
            this.cardNo = cardNo;
            this.amount = amount;
        }
    
        @Override
        public String getType() {
            return "card-withdrawn";
        }
    }
    
    ```

    - 파일명 : CreditCard.java

    ```
    package com.kt.cqrs.command.repository.entity;
    
    import java.util.UUID;
    
    import org.springframework.data.annotation.Id;
    import org.springframework.data.relational.core.mapping.Table;
    
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import lombok.ToString;
    
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Table("CREDIT_CARD")
    public class CreditCard {
    
        @Id 
        private UUID id;
        private long initialLimit;
        private long usedLimit;
    
        public CreditCard(long limit) {
            this.initialLimit = limit;
        }
    
    }
    
    ```

  - Repository

    - 파일명 : CreditCardRepository.java

    ```java
  package com.kt.cqrs.command.repository;
    
    import java.util.UUID;
    
    import org.springframework.data.repository.CrudRepository;
    
    import com.kt.cqrs.command.repository.entity.CreditCard;
    
    public interface CreditCardRepository extends CrudRepository<CreditCard, UUID> {
    }
    
    ```
  
  - Service

    -  파일명 : WithdrawalService.java

    ```java
  package com.kt.cqrs.command.service;
    
    import java.util.UUID;
    
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    
    import com.kt.cqrs.command.event.DomainEventPublisher;
    import com.kt.cqrs.command.repository.CreditCardRepository;
    import com.kt.cqrs.command.repository.entity.CardWithdrawn;
    import com.kt.cqrs.command.repository.entity.CreditCard;
    
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    
    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class WithdrawalService {
    
        private final CreditCardRepository creditCardRepository;
        private final DomainEventPublisher domainEventPublisher;
    
        @Transactional
        public void withdraw(UUID cardId, long amount) {
            CreditCard creditCard = creditCardRepository.findById(cardId)
                    .orElseThrow(() -> new IllegalStateException("Cannot find card with id " + cardId));
            withdraw(creditCard, amount);
            domainEventPublisher.publish(new CardWithdrawn(cardId, amount));
        }
        
    
        public void withdraw(CreditCard creditCard, long amount) {
            if (thereIsMoneyToWithdraw(creditCard, amount)) {
            	creditCard.setUsedLimit(creditCard.getUsedLimit() + amount);
    			log.info("creditCard = {}", creditCard);
    			creditCardRepository.save(creditCard);
            } else {
                throw new NotEnoughMoneyException(creditCard.getId(), amount, availableBalance(creditCard));
            }
        }
    
        public long chargeBack(CreditCard creditCard, long amount) {
            return creditCard.getUsedLimit() - amount;
        }
    
        public long availableBalance(CreditCard creditCard) {
            return creditCard.getInitialLimit() - creditCard.getUsedLimit();
        }
    
        private boolean thereIsMoneyToWithdraw(CreditCard creditCard, long amount) {
            return availableBalance(creditCard) >= amount;
        }
    
    }
    
    ```
  
  - Payload

    - 파일명 : WithdrawalCommand.java

    ```java
  package com.kt.cqrs.command.payload;
    
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    
    
    import java.util.UUID;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class WithdrawalCommand {
        private UUID card;
        private long amount;
    
    }
    
    ```
  
  - Controller

    - 파일명 : WithdrawalController.java

    ```java
  package com.kt.cqrs.command.controller;
    
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    
    import com.kt.cqrs.command.payload.WithdrawalCommand;
    import com.kt.cqrs.command.service.WithdrawalService;
    
    import lombok.RequiredArgsConstructor;
    
    @RestController
    @RequestMapping("/withdrawal")
    @RequiredArgsConstructor
    class WithdrawalController {
    
        private final WithdrawalService withdrawalService;
    
        @PostMapping
        ResponseEntity<?> withdraw(@RequestBody WithdrawalCommand withdrawalCommand) {
            withdrawalService.withdraw(withdrawalCommand.getCard(), withdrawalCommand.getAmount());
            return ResponseEntity.ok().build();
        }
    
    }
    
    
    ```
  
  - Event

    - 파일명 : DomainEvent.java

    ```java
  package com.kt.cqrs.command.event;
    
    public interface DomainEvent {
    
        String getType();
    }
    
    ```
  
    - 파일명 :  DomainEventPublisher.java

    ```java
  package com.kt.cqrs.command.event;
    
    public interface DomainEventPublisher {
    
        void publish(DomainEvent event);
    
    }
    ```
  
    - 파일명 : DomainEventsStorage.java

    ```java
  package com.kt.cqrs.command.event;
    
    import org.springframework.data.repository.CrudRepository;
    
    import java.util.List;
    
    interface DomainEventsStorage extends CrudRepository<StoredDomainEvent, Long> {
        List<StoredDomainEvent> findAllBySentOrderByEventTimestampDesc(boolean sent);
    }
    ```
  
    - 파일명 : KafkaDomainEventPublisher.java

    ```java
  package com.kt.cqrs.command.event;
    
    import java.util.List;
    
    import org.springframework.kafka.core.KafkaTemplate;
    import org.springframework.scheduling.annotation.Scheduled;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    
    import com.fasterxml.jackson.core.JsonProcessingException;
    import com.fasterxml.jackson.databind.ObjectMapper;
    
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    
    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class KafkaDomainEventPublisher implements DomainEventPublisher {
    
        private final DomainEventsStorage domainEventStorage;
        private final ObjectMapper objectMapper;
        private final KafkaTemplate<String, String> kafkaTemplate;
    
        @Override
        public void publish(DomainEvent domainEvent) {
            try {
                domainEventStorage.save(StoredDomainEvent.newStoredDomainEvent(objectMapper.writeValueAsString(domainEvent), domainEvent.getType()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    
    
        @Scheduled(fixedRate = 2000)
        @Transactional
        public void publishExternally() {
        	log.info("publishExternally");
        	List<StoredDomainEvent> storedDomainEvents = domainEventStorage.findAllBySentOrderByEventTimestampDesc(false);
        	for(StoredDomainEvent storedDomainEvent: storedDomainEvents) {
        		log.info("storedDomainEvent = {}",storedDomainEvent);
        		kafkaTemplate.send("domain-event", storedDomainEvent.getContent());
        		storedDomainEvent.setSent(true);
        		domainEventStorage.save(storedDomainEvent);
        	}
                   
        }
     
    }
    
    ```
  
    - 파일명 : StoredDomainEvent.java

    ```java
  package com.kt.cqrs.command.event;
    
    import java.util.Date;
    import java.util.UUID;
    
    import org.springframework.data.annotation.Id;
    import org.springframework.data.annotation.Transient;
    import org.springframework.data.domain.Persistable;
    import org.springframework.data.relational.core.mapping.Table;
    
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import lombok.ToString;
    
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Table("STORED_DOMAIN_EVENT")
    class StoredDomainEvent implements Persistable<String> {
    
    	@Transient
    	private boolean isNew = false;
    
    	@Id
    	private String id;
    	private String content;
    	private boolean sent;
    	private Date eventTimestamp;
    	private String eventType;
    
    	StoredDomainEvent(String content, String eventType) {
    		this.isNew = true;
    		this.content = content;
    		this.id = UUID.randomUUID().toString();
    		this.eventType = eventType;
    		this.eventTimestamp = new Date();
    	}
    
    	public static StoredDomainEvent newStoredDomainEvent(String content, String eventType) {
    		StoredDomainEvent storedDomainEvent = new StoredDomainEvent(content, eventType);
    		return storedDomainEvent;
    	}
    
    	@Override
    	public boolean isNew() {
    		return isNew;
    	}
    
    	void sent() {
    		this.sent = true;
    	}
    
    }
    ```
  
    


### 3.11.5. 실습 - 조회 서비스

- 패키지 구성
  
  ```
  ├─java
  │  └─com
  │      └─kt
  │          └─cqrs
  │              └─query
  │                  ├─controller
  │                  ├─event
  │                  ├─repository
  │                  │  └─entity
  │                  └─service
  └─resources
  ```
  
- Docker
  
  - 파일명 : /Dockerfile
  
    ```shell
    # Start with a base image containing Java runtime
    FROM eclipse-temurin:17-jdk
    
    # Make port 8888 available to the world outside this container
    EXPOSE 8888
    
    # The application's jar file
    ARG JAR_FILE=target/service-db-event-query-0.0.1-SNAPSHOT.jar
    
    # Add the application's jar to the container
    ADD ${JAR_FILE} service-db-event-query.jar
    
    # Run the jar file 
    ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/service-db-event-query.jar"]
    ```
  
- Database

  - 파일명: schema.sql

  ```sql
  CREATE TABLE IF NOT EXISTS WITHDRAWAL (
    ID     VARCHAR(255) PRIMARY KEY,
    CARD_ID   VARCHAR(255)    NOT NULL,
    AMOUNT DECIMAL(18,2) NOT NULL
  );
  ```

- Dependency
  
  -  파일명 : pom.xml
  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0"
  	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  	<modelVersion>4.0.0</modelVersion>
  
  	<groupId>com.kt.cqrs</groupId>
  	<artifactId>service-db-event-query</artifactId>
  	<version>0.0.1-SNAPSHOT</version>
  	<packaging>jar</packaging>
  
  	<parent>
  		<groupId>org.springframework.boot</groupId>
  		<artifactId>spring-boot-starter-parent</artifactId>
  		<version>2.7.2</version>
  		<relativePath /> <!-- lookup parent from repository -->
  	</parent>
  
  	<properties>
  		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
  		<java.version>17</java.version>
  		<spring-cloud.version>2021.0.3</spring-cloud.version>
  	</properties>
  
  	<dependencyManagement>
  		<dependencies>
  			<dependency>
  				<groupId>org.springframework.cloud</groupId>
  				<artifactId>spring-cloud-dependencies</artifactId>
  				<version>${spring-cloud.version}</version>
  				<type>pom</type>
  				<scope>import</scope>
  			</dependency>
  		</dependencies>
  	</dependencyManagement>
  
  	<build>
  		<plugins>
  			<plugin>
  				<groupId>org.springframework.boot</groupId>
  				<artifactId>spring-boot-maven-plugin</artifactId>
  			</plugin>
  		</plugins>
  	</build>
  
  	<dependencies>
  
  		<dependency>
  			<groupId>com.h2database</groupId>
  			<artifactId>h2</artifactId>
  		</dependency>
  		
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-data-jdbc</artifactId>
  		</dependency>
  
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-web</artifactId>
  		</dependency>
  		
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-actuator</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-configuration-processor</artifactId>
  			<optional>true</optional>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-devtools</artifactId>
  			<scope>runtime</scope>
  			<optional>true</optional>
  		</dependency>
  		
  		<dependency>
  			<groupId>org.springframework.kafka</groupId>
  			<artifactId>spring-kafka</artifactId>
  		</dependency>
  
  		<dependency>
  			<groupId>org.projectlombok</groupId>
  			<artifactId>lombok</artifactId>
  			<scope>provided</scope>
  		</dependency>
  
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-test</artifactId>
  			<scope>test</scope>
  		</dependency>
  
  		<dependency>
  			<groupId>junit</groupId>
  			<artifactId>junit</artifactId>
  			<scope>test</scope>
  		</dependency>
  
  		<dependency>
  			<groupId>org.springdoc</groupId>
  			<artifactId>springdoc-openapi-ui</artifactId>
  			<version>1.6.6</version>
  		</dependency>
  
  
  	</dependencies>
  
  </project>
  
  ```
  
- Properties
  -  파일명 : application.yml
  ```yaml
  server:
    port: 8888
    
  spring:     
    devtools:
      restart:
        enabled: true
    h2:
      console:
        enabled: true
        settings:
          web-allow-others: true
        path: /h2-console        
    datasource:
      url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
      
    kafka:
      bootstrap-servers:
      - kafka:9092
      consumer:
        group-id: consumerGroupId
        auto-offset-reset: earliest
        
        key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
        value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      listener:      
         ack-mode: MANUAL_IMMEDIATE
  ```
  
  
  
- Bootstrap
  - 파일명 : CqrsApplication.java

  ```java
  package com.kt.cqrs;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.kafka.annotation.EnableKafka;
  import org.springframework.web.servlet.config.annotation.EnableWebMvc;
  
  @EnableWebMvc
  @EnableKafka
  @SpringBootApplication
  public class CqrsApplication {
  
  	public static void main(String[] args) {
  		SpringApplication.run(CqrsApplication.class, args);
  	}
  }
  
  ```
  
- Query

  - Entity

    - 파일명 : Withdrawal.java

    ```java
    package com.kt.cqrs.query.repository.entity;
    
    import java.util.UUID;
    
    import org.springframework.data.annotation.Id;
    import org.springframework.data.annotation.Transient;
    import org.springframework.data.domain.Persistable;
    import org.springframework.data.relational.core.mapping.Table;
    
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import lombok.ToString;
    
    
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Table("WITHDRAWAL")
    public class Withdrawal implements Persistable<UUID>{
    
        @Transient
        private boolean isNew = false;
        
        @Id
        private UUID id;
        private long amount;
        private UUID cardId;
        
        public static Withdrawal newWithdrawal(UUID id, long amount, UUID cardId) {
        	Withdrawal withdrawal = new Withdrawal(true, id, amount, cardId);
            return withdrawal;
        }
    
        @Override
        public boolean isNew() {
            return isNew;
        }
    
    }
    
    ```
    
  - Rpository

    - 파일명 : WithdrawalRepository.java

    ```java
    package com.kt.cqrs.query.repository;
    
    import java.util.List;
    import java.util.UUID;
    
    import org.springframework.data.repository.CrudRepository;
    
    import com.kt.cqrs.query.repository.entity.Withdrawal;
    
    public interface WithdrawalRepository extends CrudRepository<Withdrawal, UUID> {
    
    	 List<Withdrawal> findByCardId(UUID cardId);
    }
    
    ```
    
  - Service

    -  파일명 : WithdrawalService.java

    ```java
    package com.kt.cqrs.query.service;
    
    import java.util.List;
    import java.util.UUID;
    
    import org.springframework.stereotype.Service;
    
    import com.kt.cqrs.query.repository.WithdrawalRepository;
    import com.kt.cqrs.query.repository.entity.Withdrawal;
    
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    
    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class WithdrawalService {
    
    	private final WithdrawalRepository withdrawalRepository;
    
    	public List<Withdrawal> withdraw(UUID cardId) {
    		return withdrawalRepository.findByCardId(cardId);
    	}
    
    }
    
    ```
    
  - Event

    - 파일명 : CardWithdrawn.java

    ```java
    package com.kt.cqrs.query.event;
    
    
    import java.util.Date;
    import java.util.UUID;
    
    import lombok.Data;
    
    @Data
    public class CardWithdrawn {
    
        private UUID cardNo;
        private long amount;
        private Date timestamp;
        private String type;
    
    }
    ```
    
    - 파일명 : QueryUpdater.java
    
    ```java
    package com.kt.cqrs.query.event;
    
    import java.util.UUID;
    
    import org.springframework.kafka.annotation.KafkaListener;
    import org.springframework.kafka.support.Acknowledgment;
    import org.springframework.stereotype.Service;
    
    import com.fasterxml.jackson.core.JsonProcessingException;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.kt.cqrs.query.repository.WithdrawalRepository;
    import com.kt.cqrs.query.repository.entity.Withdrawal;
    
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    
    @Slf4j
    @Service
    @RequiredArgsConstructor
    class QueryUpdater {
    
    	private final WithdrawalRepository withdrawalRepository;
    
    	@KafkaListener(topics="domain-event")
    	public void handle(String kafkaMessage, Acknowledgment acknowledgment) {
    		
    		ObjectMapper mapper = new ObjectMapper();
    		CardWithdrawn cardWithdrawn = null;
    		try {
    			cardWithdrawn  = mapper.readValue(kafkaMessage, CardWithdrawn.class);
    		} catch (JsonProcessingException e) {
    			e.printStackTrace();
    		}
    		
            withdrawalRepository.save(Withdrawal.newWithdrawal(UUID.randomUUID(), cardWithdrawn.getAmount(), cardWithdrawn.getCardNo()));
    	}
    }
    ```
    
  - Controller

    - 파일명 :  WithdrawalController.java

    ```java
    package com.kt.cqrs.query.controller;
    
    import java.util.List;
    import java.util.UUID;
    
    import javax.websocket.server.PathParam;
    
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    
    import com.kt.cqrs.query.repository.entity.Withdrawal;
    import com.kt.cqrs.query.service.WithdrawalService;
    
    import lombok.RequiredArgsConstructor;
    
    @RestController
    @RequestMapping("/withdrawal")
    @RequiredArgsConstructor
    class WithdrawalController {
    
    	private final WithdrawalService withdrawalService;
    
        @GetMapping
        ResponseEntity<List<Withdrawal>> withdrawals(@PathParam("cardId") String cardId) {
        	 return ResponseEntity.ok().body(withdrawalService.withdraw(UUID.fromString(cardId)));
        }
    
    }
    ```

  

### 3.11.6. 테스트

- Docker Compose 실행

```shell
docker-compose up --build
```

- 카드인출(명령)

```shell
curl localhost:8080/withdrawal -X POST --header 'Content-Type: application/json' -d '{"card":"3a3e99f0-5ad9-47fa-961d-d75fab32ef0e", "amount": 10.00}' --verbose
```

- 쿼리로 확인:

```shell
curl http://localhost:8888/withdrawal?cardId=3a3e99f0-5ad9-47fa-961d-d75fab32ef0e --verbose
```

- 예상 결과:

```shell
[{"amount":10.00}]
```

- 데이타 확인

```http
http://localhost:8080/h2-console/
```



## 3.12. 분리된 프로세스로 처리되는 CDC 기반 CQRS - Kafka Connect



### 3.12.1. 개요

- 명령과 조회가 프로세스로 분리
- 명령과 조회 모델의 데이타 일관성 솔루션을 통해 처리(CDC)
- 가용성 및 성능 장점 
  - 부하가 발생하는 서비스의 스케일 아웃이 가능함
  - 조회 서비스에 Cache 등 을 활용한 성능향상 가능
- 코드의 복잡도 감소
  - 샘플의 경우 Event 처리를 위한 코드를 별도로 구상하였으나 일반적으로 DB to DB 직접 연동하는 형태로 활용
- 솔루션 유지보수 비용 증가 및 솔루션 선택의 제약사항 발생
- 데이타 일관성을 보장하지 않으므로 보정을 할 수 있는 별도 방안 고려 필요.



### 3.12.2. 아키텍처 구성

<img src="./assets/figur36.png" alt="image-20221111141952064" style="zoom:150%;" />



### 3.12.3. 실습 - Docker 환경 구성

- 해당 실습을 위해 MySql, Kafka, Kafka Connect가 필요하므로 Docker-Compose 를 통해 구성한다.
  - 파일명 : docker-compose.yaml

  ```
  version: '2'
  services:
    command:
      build: ./service-db-cdc-command
      ports:
        - 8080:8080
      links:
        - mysql    
    query:
      build: ./service-db-cdc-query
      ports:
        - 8888:8888
      links:
        - kafka    
    zookeeper:
      image: debezium/zookeeper:0.8
      ports:
       - 2181:2181
       - 2888:2888
       - 3888:3888
    kafka:
      image: debezium/kafka:0.8
      ports:
       - 9092:9092
      links:
       - zookeeper
      environment:
       - ZOOKEEPER_CONNECT=zookeeper:2181
       - ADVERTISED_LISTENERS=PLAINTEXT://host.docker.internal:9092
    mysql:
      image: debezium/example-mysql:0.8
      ports:
       - 3306:3306
      environment:
       - MYSQL_ROOT_PASSWORD=debezium
       - MYSQL_USER=mysqluser
       - MYSQL_PASSWORD=mysqlpw
      volumes:
        - ./db/initdb.d:/docker-entrypoint-initdb.d     
    connect:
      image: debezium/connect-jdbc-es:0.8
      build:
        context: debezium-jdbc
      ports:
       - 8083:8083
       - 5005:5005
      links:
       - kafka
       - mysql
      environment:
       - BOOTSTRAP_SERVERS=kafka:9092
       - GROUP_ID=1
       - CONFIG_STORAGE_TOPIC=my_connect_configs
       - OFFSET_STORAGE_TOPIC=my_connect_offsets
  
  ```

  - 파일명 : initdb.d
  
  ```sql
  CREATE DATABASE inventory;
  
  USE inventory;
  
  drop user 'mysqluser'@'%';
  
  flush privileges;
  
  create user mysqluser@'%' identified by 'mysqlpw'; 
  
  grant all privileges on *.* to 'mysqluser'@'%' identified by 'mysqlpw' with grant option;
  
  flush privileges;  
   
  CREATE TABLE IF NOT EXISTS credit_card (
    id            CHAR(36),
    initial_limit INT NOT NULL,
    used_limit    INT NOT NULL,
    PRIMARY KEY (ID)
  );
  
  CREATE TABLE IF NOT EXISTS withdrawal (
    id    CHAR(36) PRIMARY KEY,
    card_id   CHAR(36)    NOT NULL,
    amount INT NOT NULL
  );
  
  INSERT IGNORE INTO inventory.credit_card (ID, INITIAL_LIMIT, USED_LIMIT) VALUES  ('3a3e99f0-5ad9-47fa-961d-d75fab32ef0e', 10000, 0);
  
  COMMIT;
  ```
  
  - MySql 테이블 생성 및 Kafka Connect 생성정보는 아래 위치에서 다운로드 한다.
    - 다운로드 위치 : https://github.com/kirobo77/cqrs/sample/service-db-cdc.zip

### 3.10.3. 실습 - 명령서비스

- 패키지 구조

  ```
  ├─java
  │  └─com
  │      └─kt
  │          └─cqrs
  │              └─command
  │                  ├─controller
  │                  ├─payload
  │                  ├─repository
  │                  │  └─entity
  │                  └─service
  └─resources
  ```

- Docker

  - 파일명 : Dockerfile

  ```
  # Start with a base image containing Java runtime
  FROM eclipse-temurin:17-jdk
  
  # Make port 8080 available to the world outside this container
  EXPOSE 8080
  
  # The application's jar file
  ARG JAR_FILE=target/service-db-cdc-command-0.0.1-SNAPSHOT.jar
  
  # Add the application's jar to the container
  ADD ${JAR_FILE} service-db-cdc-command.jar
  
  # Run the jar file 
  ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/service-db-cdc-command.jar"]
  ```

- Dependency

  -  파일명 : pom.xml

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
  
      <groupId>com.kt.cqrs</groupId>
      <artifactId>service-db-cdc-command</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <packaging>jar</packaging>
  
      <parent>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-parent</artifactId>
          <version>2.7.2</version>
          <relativePath/> <!-- lookup parent from repository -->
      </parent>
  
      <properties>
          <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
          <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
          <java.version>17</java.version>
          <spring-cloud.version>2021.0.3</spring-cloud.version>
      </properties>
  
      <dependencyManagement>
          <dependencies>
              <dependency>
                  <groupId>org.springframework.cloud</groupId>
                  <artifactId>spring-cloud-dependencies</artifactId>
                  <version>${spring-cloud.version}</version>
                  <type>pom</type>
                  <scope>import</scope>
              </dependency>
          </dependencies>
      </dependencyManagement>
  
      <build>
          <plugins>
              <plugin>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-maven-plugin</artifactId>
              </plugin>
          </plugins>
      </build>
  
  
      <dependencies>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-data-jdbc</artifactId>
          </dependency>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-web</artifactId>
          </dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-configuration-processor</artifactId>
  			<optional>true</optional>
  		</dependency>        
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-actuator</artifactId>
          </dependency>
  
  		<dependency>
  			<groupId>mysql</groupId>
  			<artifactId>mysql-connector-java</artifactId>
  		</dependency>
  
  		<dependency>
  			<groupId>org.projectlombok</groupId>
  			<artifactId>lombok</artifactId>
  			<scope>provided</scope>
  		</dependency>
  		
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-devtools</artifactId>
  			<scope>runtime</scope>
  			<optional>true</optional>
  		</dependency>
  		
  		<dependency>
  			<groupId>org.springframework.kafka</groupId>
  			<artifactId>spring-kafka</artifactId>
  		</dependency>		
  
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-test</artifactId>
  			<scope>test</scope>
  		</dependency>
  
  		<dependency>
  			<groupId>junit</groupId>
  			<artifactId>junit</artifactId>
  			<scope>test</scope>
  		</dependency>
  		
  		<dependency>
  			<groupId>org.springdoc</groupId>
  			<artifactId>springdoc-openapi-ui</artifactId>
  			<version>1.6.6</version>
  		</dependency>
  
      </dependencies>
  
  </project>
  
  ```

- Properties

  -  파일명 : resources/application.yml

  ```yaml
  server:
    port: 8080
    
  spring:     
    devtools:
      restart:
        enabled: true
    datasource:
      url: jdbc:mysql://mysql:3306/inventory?autoReconnect=true&useSSL=false
      username: mysqluser
      password: mysqlpw
        
    kafka:
      bootstrap-servers:
      - kafka:9092  
      consumer:
        group-id: inventory-event
        auto-offset-reset: earliest
        key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
        value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      listener:      
         ack-mode: MANUAL_IMMEDIATE
  
   
  ```

- Bootstrap

  -  파일명 : CqrsApplication.java

  ```java
  package com.kt.cqrs.command;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.kafka.annotation.EnableKafka;
  import org.springframework.scheduling.annotation.EnableScheduling;
  import org.springframework.web.servlet.config.annotation.EnableWebMvc;
  
  @EnableWebMvc
  @EnableKafka
  @SpringBootApplication
  @EnableScheduling
  public class CqrsApplication {
  
  	public static void main(String[] args) {
  		SpringApplication.run(CqrsApplication.class, args);
  	}
  }
  
  ```

- Entity

  - 파일명 : CreditCard.java

  ```java
  package com.kt.cqrs.command.repository.entity;
  
  import org.springframework.data.annotation.Id;
  import org.springframework.data.relational.core.mapping.Table;
  import lombok.AllArgsConstructor;
  import lombok.Builder;
  import lombok.Getter;
  import lombok.NoArgsConstructor;
  import lombok.Setter;
  import lombok.ToString;
  
  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  @Table("credit_card")
  public class CreditCard {
      @Id
      private String id;
      private long initialLimit;
      private long usedLimit;
  }
  
  ```

- Rpository

  - 파일명 : CreditCardRepository.java

  ```java
  package com.kt.cqrs.command.repository;
  
  import org.springframework.data.repository.CrudRepository;
  import com.kt.cqrs.command.repository.entity.CreditCard;
  
  public interface CreditCardRepository extends CrudRepository<CreditCard, String> {
  }
  
  ```

- Service

  -  파일명 : WithdrawalService.java

  ```java
  package com.kt.cqrs.command.service;
  
  import org.springframework.stereotype.Service;
  import org.springframework.transaction.annotation.Transactional;
  
  import com.kt.cqrs.command.repository.CreditCardRepository;
  import com.kt.cqrs.command.repository.entity.CreditCard;
  
  import lombok.RequiredArgsConstructor;
  import lombok.extern.slf4j.Slf4j;
  
  @Slf4j
  @Service
  @RequiredArgsConstructor
  public class WithdrawalService {
  
      private final CreditCardRepository creditCardRepository;
  
      @Transactional
      public void withdraw(String cardId, long amount) {
          CreditCard creditCard = creditCardRepository.findById(cardId)
                  .orElseThrow(() -> new IllegalStateException("Cannot find card with id " + cardId));
          withdraw(creditCard, amount);
      }
      
  
      public void withdraw(CreditCard creditCard, long amount) {
          if (thereIsMoneyToWithdraw(creditCard, amount)) {
          	creditCard.setUsedLimit(creditCard.getUsedLimit() + amount);
  			log.info("creditCard = {}", creditCard);
  			creditCardRepository.save(creditCard);
          } else {
              throw new NotEnoughMoneyException(creditCard.getId(), amount, availableBalance(creditCard));
          }
      }
  
      public long chargeBack(CreditCard creditCard, long amount) {
          return creditCard.getUsedLimit() - amount;
      }
  
      public long availableBalance(CreditCard creditCard) {
          return creditCard.getInitialLimit() - creditCard.getUsedLimit();
      }
  
      private boolean thereIsMoneyToWithdraw(CreditCard creditCard, long amount) {
          return availableBalance(creditCard) >= amount;
      }
  
  }
  
  ```

- Exception

  -  파일명 : NotEnoughMoneyException.java

  ```java
  package com.kt.cqrs.command.service;
  
  public class NotEnoughMoneyException extends RuntimeException {
  
  	private static final long serialVersionUID = 1L;
  
  	public NotEnoughMoneyException(String cardNo, long wanted, long availableBalance) {
          super(String.format("Card %s not able to withdraw %s. Balance is %s", cardNo, wanted, availableBalance));
      }
  
  
  }
  
  ```

- Payload

  - 파일명 : WithdrawalCommand.java

  ```java
  package com.kt.cqrs.command.payload;
  
  import lombok.AllArgsConstructor;
  import lombok.Data;
  import lombok.NoArgsConstructor;
  
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public class WithdrawalCommand {
      private String card;
      private long amount;
  }
  ```

- Controller

  - 파일명 : WithdrawalCommandController.java

  ```java
  package com.kt.cqrs.command.controller;
  
  import org.springframework.http.ResponseEntity;
  import org.springframework.web.bind.annotation.PostMapping;
  import org.springframework.web.bind.annotation.RequestBody;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RestController;
  import com.kt.cqrs.command.payload.WithdrawalCommand;
  import com.kt.cqrs.command.service.WithdrawalCommandService;
  
  import lombok.RequiredArgsConstructor;
  
  @RestController
  @RequestMapping("/withdrawal")
  @RequiredArgsConstructor
  class WithdrawalCommandController {
  
  	private final WithdrawalCommandService withdrawalCommandService;
  
  	@PostMapping
  	ResponseEntity<?> withdraw(@RequestBody WithdrawalCommand withdrawalCommand) {
  		withdrawalCommandService.withdraw(withdrawalCommand.getCard(), withdrawalCommand.getAmount());
  		return ResponseEntity.ok().build();
  	}
  
  }
  
  ```



### 3.10.3. 실습 - 조회서비스

- 패키지 구조

  ```
  ├─java
  │  └─com
  │      └─kt
  │          └─cqrs
  │              └─query
  │                  ├─controller
  │                  ├─event
  │                  │  └─message
  │                  ├─repository
  │                  │  └─entity
  │                  └─service
  └─resources
  ```

- Docker

  - 파일명 : Dockerfile

  ```
  # Start with a base image containing Java runtime
  FROM eclipse-temurin:17-jdk
  
  # Make port 8888 available to the world outside this container
  EXPOSE 8888
  
  # The application's jar file
  ARG JAR_FILE=target/service-db-cdc-query-0.0.1-SNAPSHOT.jar
  
  # Add the application's jar to the container
  ADD ${JAR_FILE} service-db-cdc-query.jar
  
  # Run the jar file 
  ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/service-db-cdc-query.jar"]
  ```

- Dependency

  -  파일명 : pom.xml

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0"
  	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  	<modelVersion>4.0.0</modelVersion>
  
  	<groupId>com.kt.cqrs</groupId>
  	<artifactId>service-db-cdc-query</artifactId>
  	<version>0.0.1-SNAPSHOT</version>
  	<packaging>jar</packaging>
  
  	<parent>
  		<groupId>org.springframework.boot</groupId>
  		<artifactId>spring-boot-starter-parent</artifactId>
  		<version>2.7.2</version>
  		<relativePath /> <!-- lookup parent from repository -->
  	</parent>
  
  	<properties>
  		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
  		<java.version>17</java.version>
  		<spring-cloud.version>2021.0.3</spring-cloud.version>
  	</properties>
  
  	<dependencyManagement>
  		<dependencies>
  			<dependency>
  				<groupId>org.springframework.cloud</groupId>
  				<artifactId>spring-cloud-dependencies</artifactId>
  				<version>${spring-cloud.version}</version>
  				<type>pom</type>
  				<scope>import</scope>
  			</dependency>
  		</dependencies>
  	</dependencyManagement>
  
  	<build>
  		<plugins>
  			<plugin>
  				<groupId>org.springframework.boot</groupId>
  				<artifactId>spring-boot-maven-plugin</artifactId>
  			</plugin>
  		</plugins>
  	</build>
  
  	<dependencies>
  
  		<dependency>
  			<groupId>com.h2database</groupId>
  			<artifactId>h2</artifactId>
  		</dependency>
  		
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-data-jdbc</artifactId>
  		</dependency>
  
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-web</artifactId>
  		</dependency>
  		
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-actuator</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-configuration-processor</artifactId>
  			<optional>true</optional>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-devtools</artifactId>
  			<scope>runtime</scope>
  			<optional>true</optional>
  		</dependency>
  		
  		<dependency>
  			<groupId>org.springframework.kafka</groupId>
  			<artifactId>spring-kafka</artifactId>
  		</dependency>
  
  		<dependency>
  			<groupId>org.projectlombok</groupId>
  			<artifactId>lombok</artifactId>
  			<scope>provided</scope>
  		</dependency>
  
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-test</artifactId>
  			<scope>test</scope>
  		</dependency>
  
  		<dependency>
  			<groupId>junit</groupId>
  			<artifactId>junit</artifactId>
  			<scope>test</scope>
  		</dependency>
  
  		<dependency>
  			<groupId>org.springdoc</groupId>
  			<artifactId>springdoc-openapi-ui</artifactId>
  			<version>1.6.6</version>
  		</dependency>
  
  
  	</dependencies>
  
  </project>
  
  ```

- Properties

  -  파일명 : resources/application.yml

  ```yaml
  server:
    port: 8888
    
  spring:     
    devtools:
      restart:
        enabled: true
    h2:
      console:
        enabled: true
        settings:
          web-allow-others: true
        path: /h2-console        
    datasource:
      url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
      
    kafka:
      bootstrap-servers:
      - kafka:9092
      consumer:
        group-id: inventory-event
        auto-offset-reset: earliest
        key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
        value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      listener:      
         ack-mode: MANUAL_IMMEDIATE
     
  ```

- Bootstrap

  -  파일명 : CqrsApplication.java

  ```java
  package com.kt.cqrs;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.kafka.annotation.EnableKafka;
  import org.springframework.web.servlet.config.annotation.EnableWebMvc;
  
  @EnableWebMvc
  @EnableKafka
  @SpringBootApplication
  public class CqrsApplication {
  
  	public static void main(String[] args) {
  		SpringApplication.run(CqrsApplication.class, args);
  	}
  }
  
  ```

- Event

  - 파일명 : EventHandler.java

  ```java
  package com.kt.cqrs.query.event;
  
  import java.util.UUID;
  
  import org.springframework.kafka.annotation.KafkaListener;
  import org.springframework.kafka.support.Acknowledgment;
  import org.springframework.stereotype.Service;
  
  import com.fasterxml.jackson.core.JsonProcessingException;
  import com.fasterxml.jackson.databind.ObjectMapper;
  import com.kt.cqrs.query.event.message.Envelope;
  import com.kt.cqrs.query.repository.WithdrawalRepository;
  import com.kt.cqrs.query.repository.entity.Withdrawal;
  
  import lombok.RequiredArgsConstructor;
  import lombok.extern.slf4j.Slf4j;
  
  @Service
  @Slf4j
  @RequiredArgsConstructor
  class EventHandler {
  
  	private final WithdrawalRepository withdrawalRepository;
  	
  	@KafkaListener(topics="credit_card")
  	public void handle(String kafkaMessage, Acknowledgment acknowledgment) {
  		
  		ObjectMapper mapper = new ObjectMapper();
  		Envelope message = null;
  		try {
  			message  = mapper.readValue(kafkaMessage, Envelope.class);
  			log.info("message = {}", message);
  		} catch (JsonProcessingException e) {
  			e.printStackTrace();
  		}
  		
  		String op = message.getPayload().getOp();
  		if("u".equals(op)) {
  			saveWithdrawalFrom(message);
  		}
  	}
  	
  
  	private void saveWithdrawalFrom(Envelope message) {
  		String cardId = message.getPayload().getBefore().getId();
  		long withdrawalAmount
  				= balanceAfter(message) - balanceBefore(message);
  		 withdrawalRepository.save(Withdrawal.newWithdrawal(UUID.randomUUID(), withdrawalAmount, UUID.fromString(cardId)));
  	}
  
  	private long balanceAfter(Envelope message) {
  		return message.getPayload().getAfter().getUsedLimit();
  	}
  
  	private long balanceBefore(Envelope message) {
  		return message.getPayload().getBefore().getUsedLimit();
  	}
  
  }
  ```

  - **Kafka Connect에서 보내는 Topic 메세지를 받기위한 Java 클래스를 com.kt.cqrs.event.message 위치에 복사한다**.
  
- 다운로드 위치 : https://github.com/kirobo77/cqrs/sample/topicJava.zip
  
- Entity
  
  - 파일명 : Withdrawal.java
  
    ```java
    package com.kt.cqrs.query.repository.entity;
    
    import java.util.UUID;
    
    import org.springframework.data.annotation.Id;
    import org.springframework.data.annotation.Transient;
    import org.springframework.data.domain.Persistable;
    import org.springframework.data.relational.core.mapping.Table;
    
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import lombok.ToString;
    
    
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Table("WITHDRAWAL")
    public class Withdrawal implements Persistable<UUID>{
    
        @Transient
        private boolean isNew = false;
        
        @Id
        private UUID id;
        private long amount;
        private UUID cardId;
        
        public static Withdrawal newWithdrawal(UUID id, long amount, UUID cardId) {
        	Withdrawal withdrawal = new Withdrawal(true, id, amount, cardId);
            return withdrawal;
        }
    
        @Override
        public boolean isNew() {
            return isNew;
        }
    
    }
    
    ```
  ```
  
  ```
  
- Repository
  
  -  파일명 : WithdrawalRepository.java
  
    ```java
    package com.kt.cqrs.query.repository;
    
    import java.util.List;
    import java.util.UUID;
    
    import org.springframework.data.repository.CrudRepository;
    
    import com.kt.cqrs.query.repository.entity.Withdrawal;
    
    public interface WithdrawalRepository extends CrudRepository<Withdrawal, UUID> {
    
    	 List<Withdrawal> findByCardId(UUID cardId);
    }
    
    ```
  ```
  
  ```
  
- Service
  
  - 파일명 : WithdrawalQueryService.java
  
    ```java
    package com.kt.cqrs.query.service;
    
    import java.util.List;
    import java.util.UUID;
    
    import org.springframework.stereotype.Service;
    
    import com.kt.cqrs.query.repository.WithdrawalRepository;
    import com.kt.cqrs.query.repository.entity.Withdrawal;
    
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    
    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class WithdrawalService {
    
    	private final WithdrawalRepository withdrawalRepository;
    
    	public List<Withdrawal> withdraw(UUID cardId) {
    		return withdrawalRepository.findByCardId(cardId);
    	}
    
    }
    
    ```
  ```
  
  ```
  
- Controller
  
  - 파일명 : WithdrawalQueryController.java
  
    ```java
    package com.kt.cqrs.query.controller;
    
    import java.util.List;
    import java.util.UUID;
    
    import javax.websocket.server.PathParam;
    
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    
    import com.kt.cqrs.query.repository.entity.Withdrawal;
    import com.kt.cqrs.query.service.WithdrawalService;
    
    import lombok.RequiredArgsConstructor;
    
    @RestController
    @RequestMapping("/withdrawal")
    @RequiredArgsConstructor
    class WithdrawalController {
    
    	private final WithdrawalService withdrawalService;
    
        @GetMapping
        ResponseEntity<List<Withdrawal>> withdrawals(@PathParam("cardId") String cardId) {
        	 return ResponseEntity.ok().body(withdrawalService.withdraw(UUID.fromString(cardId)));
        }
    
    
    }
    
    
    ```

### 3.10.4. 테스트

- 빌드
  - 이클립스 프로젝트 익스플로러 컨텍스트 메뉴의 Run As> Maven Build 를 클릭 한 후 Goals 항목에 아래 명령어를 넣는다.

```
clean install
```

- Docker Compose 실행

```shell
docker-compose up --build
```

- Kafka Connect 생성

```shell
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @source.json --verbose
```

- 카드인출(명령)

```shell
curl localhost:8080/withdrawal -X POST --header 'Content-Type: application/json' -d '{"card":"3a3e99f0-5ad9-47fa-961d-d75fab32ef0e", "amount": 10.00}' --verbose
```

- 쿼리로 확인:

```shell
curl http://localhost:8888/withdrawal?cardId=3a3e99f0-5ad9-47fa-961d-d75fab32ef0e --verbose
```

- 예상 결과:

```shell
[{"amount":10.00}]
```

- H2 데이터확인

```http
http://localhost:8888/h2-console/
```

- MySql 데이터확인

```
docker exec -ti service-db-cdc-mysql-1 bash
....
cd bin
./mysql -u root -p   //패수워드 : debezium
use inventory;
show tables;
select * from credit_card;
```





# [별첨] 

## 1.1. DDD(domain-driven-design) Pattern

### 1.1.1.  DDD(domain-driven-design) 이란 무엇인가

- DDD (domain-driven-design) 는 **특히 복잡하고, 끊임없이 변화하는 비즈니스 규칙이 존재하며, 기업 내에서 해당 비즈니스가 계속해서 유지되고 발전될 것으로 예상되는 시스템을 개발하는 방법론**이다.

- DDD 는 핵심 접근법 (The core of the DDD approach) 은 도메인을 분석하고 해당 분석을 토대로 개념적 도메인 모델을 만드는 몇가지 일련의 기술을 사용한다.

- 그리고 만들어진 모델은 개발하려는 소프트웨어의 기반이 된다.

- DDD 방식의 접근법 (분석과 모델링)은 특히 크고 복잡한 도메인을 설계하는데에 적합하다.

- DDD 는 또한 복잡한 시스템을 관리하는데에도 도움이 되며 소프트웨어 개발 프로세스의 다른 측면으로도 해결 방법이 될 수 있다.
  - 도메인에 집중하기도 하면서, DDD 는 비즈니스 팀과 개발 팀이 의사소통 사이에서 오해를 할 수 있는 부분에 집중한다. DDD 가 사용하는 도메인 모델은 상세하고 풍부한 비즈니스 지식을 표현하기도 함과 동시에 실제 작성된 코드와 모델이 비슷해야 하기 때문이다.
  - 도메인 모델은 오랜 기간동안 계속 최신화가 될 수록 유리하다. 가치있는 도메인 지식을 포착함으로써, 그들은 추후에 마주할 시스템 유지보수 업무에도 도움이 될 것이다.
  - DDD 는 거대한 문제를 해결하는 도메인을 효과적으로 나누고 병렬적으로 일을 수행할 수 있게 하며, 비즈니스 가치를 부분적으로 전달할 수도 있다.

- 이러한 DDD 는 오랫동안 유지되고 복잡한 시스템에 적합한데, 만약 당신이 개발하려는 시스템이 작고, 단순하며, 짧은 기간동안에만 유지될 프로젝트라면 DDD 의 장점을 살리지 못할 것이다



## 1.1.2. DDD(domain-driven-design) 의 컨셉과 용어

- CQRS 패턴을 구현하기 위해서 필요한 몇가지 DDD 개념들을 알아두면 도움이 된다.
  - Domain Model, 도메인 모델
  - Ubiquitous Language, 유비쿼터스 언어
  - Entities, Value Object and services
  - Aggregate and Aggregate Roots



## 1.1.3. 도메인 모델

- DDD 의 중심에는 domain model 이라는 것이 존재한다.

- 도메인 모엘은 도메인 전문가, 비즈니스 전문가, 소프트웨어 개발자들의 토론과 여러가지의 질문들로 만들어지며 이러한 도메인 모델은 다음과 같은 역할울 수행하게 된다.
  - 도메인 전문가로부터 여러 도메인 지식을을 포착할 수 있다.
  - 팀이 도메인 지식에 대해서 align 할 수 있다.
  - 개발자는 해당 도메인 모델을 토대로 코드를 작성한다.
  - 도메인에 대한 엄청난 변화에도 즉각적으로 반영될 수 있다.

- DDD 는 도메인이 곧 비즈니스 가치이기 떄문에 도메인에 집중한다.

- 이러한 도메인 모델을 통해서 기업은 가치를 구현하고 숨어있는 비즈니스 가치를 찾아낸다.

- DDD 접근 방식의 대부분은 이러한 도메인 모델을 생성, 유지 및 사용하는 방법에 초점을 맞추고 있다.

- 도메인 모델은 일반적으로 Entities, Value Object and Aggregates 와 같은 요소로 구성되며 유비쿼터스 언어의 용어를 사용하여 설명한다.



## 1.4. Ubiquitous Language

- Ubiquitous Language 의 개념은 도메인 모델에 매우 밀접하게 닮아있다.

- 도메인 모델은 도메인 전문가와 개발자 사이에 대한 오해를 줄이고 같은 곳을 바라볼 수 있도록 간극을 좁히는 역할을 수행하기도 한다.

- 만약 도메인 전문가와 개발자가 도메인에 대해서 (앞서 Journey 파트에서 나온 콘토소 컨퍼런스 관리 시스템의 컨퍼런스, 참석자, 의자, 대기자 명단 과 같은) 동일한 용어를 사용한다면 오해에 대한 여지를 줄일 수 있게 된다.

- 좀 더 구체적으로는 모든 사람들이 동일한 개념의 언어를 사용한다면, 언어 간 번역으로 인한 오해가 있을 가능성이 적어진다. 예를 들어서 도메인 전문가가 참석자를 deletegtor 라고 표현하 하는데, 실제로 소프트웨어 개발자는 이를 티켓 판매원이라고 이해할 수 있고 결국 도메인에 대한 공통 개념이 없다 보니 시간이 지날수록 문제가 더 커질 수 있게 되는 것이다.

- 더 구체적으로, 모든 사람이 같은 언어를 사용한다면, 언어 간 번역으로 인한 오해가 있을 가능성이 적다. 예를 들어, 개발자가 "도그니 전문가가 델레 게이트에 대해 이야기한다면, 그는 실제로 소프트웨어의 참석자에 대해 이야기하고 있다"고 생각해야 한다면, 결국 이러한 명확성 부족으로 인해 무언가가 잘못될 것이다.

  

## 1.5. Entities, value objects, and services

- DDD 는 internal artifacts (or building blocks) 를 식별하기 위해서 다음과 같은 용어를 사용한다.

### 1.5.1. Entities

- 엔티티는 식별자를 가지는 객체를 의미한다. 예를 들어서 컨퍼런스 관리 시스템에서 conference 는 엔티티가 될 수 있다.

- conference 의 여러 속성들은 시간이 지남에 따라서 변할 수 있지만 해당 conference 자체는 시스템 안에서 고유할 것이다.

- 이러한 엔티티들은 항상 시스템의 메모리상에만 존재하지 않을 수 있다. 다른 시스템에 잠시 저장할 수도 있으며 DB 를 통해서 영속화 하고 필요할 때 시스템으로 다시 불러올 수도 있다.

### 1.5.2. Value Objects

- 꼭 모든 객체가 유일성을 보장해야 하지 않을 수도 있다.

- 예를 들어서 어떤 객체들은 단지 속성의 값으로만 존재할 수도 있다.

- 예를 들어서 우리의 컨퍼런스 관리 시스템에서 컨퍼런스 참석자의 주소에 대해서는 꼭 식별자를 갖지 않아도 된다.

- 대신 집중하는 것은 value object 는 불변성을 보장해야 한다는 것이다.

### 1.5.3. Services

- 항상 모든것 객체 형태로 관리하지 않아도 된다.

- 예를 들어, 회의 관리 시스템에서는 외부 결제 처리 시스템을 서비스로 모델링하는 것이 합리적일 수 있다.

- 서비스가 필요로 하는 파라미터만 넘기고 결과를 반환받아 특정 기능을 수행할 수도 있다.

- 이러한 서비스의 특징이라고 한다면 엔티티나 값 객체와 달리 stateless 하다는 것이다.

  

## 1.6. Aggregate 와 Aggregate Root

- Entitiy, Value Object 및 Service 가 DDD 가 도메인 모델에서 존재하는 구성요소를 설명하는 데에 사용되는 용어인 반면에 Aggregate 나 Aggregate Root 라는 용어는 특히 그러한 용어들의 그룹화와 life-cycle 에 관련이 있다.

- 만약 공유된 데이터에 대해서 다수의 사용자를 허용하는 시스템을 설계한다면 당신은 일관성(consistency) 와 사용성(usabililty) 사이의 트레이드 오프를 잘 파악해야 한다.

- 극단적인 예를 보면, 유저가 어떠한 데이터에 대한 수정을 하고 있을 때, 시스템은 해당 데이터를 다른 사용자가 사용하지 못하도록 system 단에서 lock 을 걸 수 있다.

- 하지만 해당 lock 이 풀릴 떄 까지 시스템의 가용성은 낮아지게 된다.

- 또 다른 극단적인 예를 보자, 만약 다른 사용자가 사용하는 자원에 대해서 lock 을 걸지 않는다면, 다른 사용자들은 어떠한 제약 없이 동시에 해당 데이터를 수정할 수도 있고 시스템의 일관성이 깨지게 될 것이다.

- 이런 상황에서 locking 을 할 것이냐 말 것이냐를 결정하기 위해서는 해당 도메인에 대한 지식이 필요하다.
  - 해당 트랜잭션을 통해서 어떤 값 객체나 엔티티가 영향을 받는지 알아야 한다.
  - 한 오브젝트로 부터 다른 엔티티나 값 객체에 얼마나 영향을 미치며 어디까지 consistency 를 보장하는 경계에 대해서도 인지해야 한다.

### 1.6.1. Aggregate

- DDD 는 일관성을 보장해야 하는 관련된 엔티티와 값 객체들을 하나로 묶는 용어로 aggregate 라는 용어를 사용한다.

- 해당 consistency 경계는 일반적으로 transactional consistency 를 기본으로 한다.

### 1.6.2. Aggregate Root

root entity 로 알려진 Aggregate Root 는 애그리거트에 접근할 수 있는 진입접 (gatekeeper) 을 의미한다.

한 애그리거트에 속해있는 값 객체나 엔티티에 접근하기 위해서는 무조건 Aggregate Root 를 통해서만 수행되어야 한다.

외부 엔티티는 Aggregate Root 대한 레퍼런스만 가질 수 있다.



> #### 요약하자면 aggregate 와 aggregate root 는 DDD 가 일반적으로 도메인 모델에 존재하는 수많은 엔티티와 값 객체 사이에 존재하는 복잡한 관계를 관리하는데 사용되는 메커니즘이다.



## 1.7. Bounded Contexts

- 지금까지 간략히 알아본 DDD 컨셉과 용어는 도메인 모델을 생성, 유지 및 사용하는 것과 관련이 있다.

- 대형 시스템에서는 도메인 모델을 단일로 관리하고 만들어 나가는 것은 실용적이지 않을 수 있다.

- 크기와 복잡성으로 인해서 consistency 역시 보장하고 유지하기가 어렵다.

- 이런 상황에서 DDD는 Bounded Context 에 대한 개념을 사용한다.

- 시스템 내에서 단일 대형 모델이 아닌, 여러 개의 작은 모델을 사용하여 해당 모델들이 적절한 협력을 거쳐서 기능을 수행하게 된다.

- 각각은 전체 시스템 내의 일부의 기능이나 비즈니스 가치에 집중할 수 있게 된다.

- Boundex Context, 바운디드 컨텍스트는 특정 도메인 모델의 문맥 (컨텍스트)이다.

- 각 바운디드 컨텍스트는 자체적인 유비쿼터스 언어를 가지게 될 수 있고 자체적인 개념이 존재할 수도 있다.

[![image](./assets/figure28.png)](https://user-images.githubusercontent.com/48385288/187064826-4bff4c3f-e8d5-4340-9b69-dea986ab38e1.png)

- 위 그림은 우리가 journey 에서 구현한 컨퍼런스 관리 시스템이 여러 바운디드 컨텍스트로 분리된 것을 보여준다.

- 실제로는 위 그림에 나온 3개의 바운디드 컨테스트보다 훨씬 많을 것이다.

- 바운디드 컨테스트가 얼마나 커야하고 얼마나 작아야 하는지에 대한 규칙은 없다.

- 궁극적으로 비즈니스에 대한 가치와 요구사항 및 프로젝트 제약에 의해서 결정되는 것이다.



#### 1.7.1. Eric Evans 는 더 거대한 바운디드 컨텍스트에 대한 여러 사례들을 만들었다.

- 거대한 바운디드 컨텍스트
  - 통합 모델로 더 많은 것을 처리할 때 사용자 작업 간의 흐름이 더 부드럽다.
  - 두 개의 별개의 모델과 매핑보다 하나의 일관된 모델을 이해하는 것이 더 쉽다.
  - 두 모델을 해석하는 것은 어렵다. (어쩔 때는 불가능에 가깝다)
  - 두 모델을 사용하는 것은 팀의 커뮤니케이션 비용을 증가시킨다.

- 작은 바운디드 컨텍스트
  - 개발자 간의 커뮤니케이션 오버헤드가 줄어든다.
  - CI, 지속적 통합이 더욱 쉬워진다.
  - 큰 바운디드 컨텍스트는 더욱 추상적이게 될 수 있으며 더 다양한 기술을 요구할 수 있다.



## 1.8. 손상 방지 레이어(Anti-Corruption Layers)

- 서로 다른 바운디드 컨텍스트는 서로 다른 도메인 모델을 가지고 있는다.

- 만약 한 바운디드 컨텍스트에서 다른 바운디드 컨텍스트와 통신할 때, 한 도메인 모델의 특정 개념이 다른 도메인 모델의 개념으로 잘못 침투하는 것을 주의해야 한다.

- 이 때, Anti-Corruption Layers 는 두 도메인 모델 사이를 깨끗하게 만드는 게이트웨이 역할을 수행한다.



## 1.9. Context Maps

- 크고 복잡한 시스템은 다양한 방식으로 서로 상호작용하는 여러 경계 컨텍스트를 가질 수 있다.

- context map 은 각각의 바운디드 컨텍스트 사이의 관계를 설명하는 문서이다.

- 이는 diagram 이 될 수도 있고, 표가 될 수도 있으며 문자가 될 수 있다.

- 컨텍스트맵은 높은 수준에서 시스템을 시각화 하는데에 도움을 주며 바운디드 컨텍스트 사이를 명확히 하는 데 도움이 된다.

- 바운디드 컨텍스트가 데이터를 교환하고 공유하는 위치, 방법 그리고 한 도메인 모델에서 다른 도메인 모델로 이동할 때 데이터를 어디서 변환하는지를 보여준다.

- customer 과 같은 비즈니스 엔티티는 여러 바운디드 컨텍스트에 존재할 수 있다. 하지만 특정 바운디드 컨텍스트와 관련된 다른 속성값을 포함하고 표현할 수 있다.

- customer 엔티티가 한 바운디드 컨텍스트에서 다른 바운디드 컨텍스트로 이동할 때 현재 컨텍스트에 대한 속성을 노출하거나 숨기는 등 변환될 수도 있다.



## 1.10. Bounded Context 와 Multiple Architecture

- 바운디드 컨텍스트는 일반적으로 시스템 내의 다른 바운디드 컨텍스트와 명확하게 경계를 나타낸다. 
- 바운디드 컨텍스트가 DDD 접근법에 따라서 구현된다면, 바운디드 컨텍스트는 자제 도메인 모델과 자체 유비쿼터스 언어를 갖게 된다.

- 바운디드 컨텍스트의 구현은 일반적으로 데이터 저장소에서 UI 에 이르기 까지 모든 것이 포함된다.

- 또한 동일한 도메인 개념이 여러 바운디드 컨텍스트에 존재할 수 있다. 예를 들어서 컨퍼런스 관리 시스템에서 참석자의 개념은 예약을 다루는 다른 바운디드 컨텍스트에서 다른 의미로 사용될 수 있다.

- 각 바운디드 컨텍스트의 도메인 전문가의 관점에서 이러한 다양한 버전의 참석자는 다른 행동과 속성들이 필요할 수 있다.

- 예를 들어서 예약 바운디드 컨텍스트에서 참석자는 예약 및 결제를 하는 사용자를 의미한다. 결국 결제와 관련된 정보를 요구할 수도 있고 호텔 바운디드 컨텍스트에서 참석자는 흠연 선호도와 같은 정보가 중요할 수도 있다.

- 이러한 바운디드 컨텍스트를 보고 알 수 있는 중요한 것은 각각의 서로 다른 바운디드 컨텍스트는 다른 시스템 아키텍처가 적용될 수 있다는 것이다.

- 예를 들어서 하나의 바운디드 컨텍스트는 DDD 의 Layered Architecture 를 이용해서 구현될 수 있고, 다른 바운디드 컨텍스트는 단순 CRUD 아키텍처, 또 다른 바운디드 컨텍스트는 CQRS 패턴을 적용한 아키텍처를 사용할 수 있다는 것이다.

- 아래의 그림은 영속성 자장 장치부터 UI 까지, 포함된 모든 컴포넌트를 보여준다.

[![image](./assets/figure29.png)](https://user-images.githubusercontent.com/48385288/187066142-712a959c-6052-406f-9060-b8ee16a860e9.png)

- 복잡성을 관리하는 것 이외에도, 시스템을 바운디드 컨텍스트로 나누는 또 다른 이점이 있다.

- 다른 요구사항에 따라서 적절한 아키텍처를 선택할 수도 있고, 특정 부분만 다른 기술을 사용할 수도 있다.

- 복잡성을 관리하는 것 외에도, 시스템을 경계된 맥락으로 나누는 또 다른 이점이 있다. 
  - 시스템의 다른 부분에 적절한 기술 아키텍처를 사용하여 각 부분의 특성을 광고할 수 있다. 
  - 예를 들어, 시스템의 복잡한 부분인지, 핵심 도메인 기능이 포함되어 있는지, 예상 수명과 같은 질문을 해결할 수 있습니다.



## 1.11. Bounded Context 와 multiple 개발팀

- 다른 바운디드 컨텍스트를 명확하게 분리하고 별도의 도메인 모델과 유비쿼터스 언어로 작업하면 각 경계 컨텍스트에 대해 별도의 팀을 사용하여 개발 작업을 병렬화할 수 있다.

  

## 1.12. 여러 바운디드 컨텍스트를 maintaining 하기

- 바운디드 컨텍스트는 더 관리하기 쉬운 부분으로 나뉘기 때문에 대규모 시스템의 복잡성을 관리하는데에 도움되지만, 각 바운디드 컨텍스트가 혼자 독립적으로 존재할 가능성은 거의 없다.

- 바운디드 컨텍스트는 서로 데이터를 교환해야 하며, 다른 도메인 모델에서 동일한 도메인 객체를 변환을 해야 하는 경우 이러한 데이터 변경은 꽤나 복잡할 것이다.

- 컨퍼런스 관리 시스템에서는 컨퍼런스 예약, 배지 인쇄 및 호텔 예약 문제를 다루는 경계된 맥락 간에 attendee 객체에 대한 속성을 변환해야 할 수도 있다.

- DDD 접근 방식은 Anti-Corruption Layers 를 사용하거나 [Shared Kernel](https://github.com/dhslrl321/cqrs-journey-guide-korean/blob/master/terms/Shared Kernel.md) 을 사용하는 것과 같은 여러 바운디드 컨텍스트 사이에서 여러 모델 간의 상호 작용을 처리하기 위한 다양한 접근 방식을 제공한다

> Note: 기술적인 관점에서 서로 다른 바운디드 컨텍스트 사이의 통신은 messaging infrastructure 를 이용한 비동기 통신을 주로 사용한다.





## 2. 클린코드 

## 2.1. Class

### 2.1.1 클래스 체계

- JAVA Convention에 따르면 가장 먼저 변수 목록이 나온다.
  **static public --> static private --> private 인스턴스 --> (public은 필요한 경우가 거의 없다)**  
- 변수목록 다음에는 공개 함수가 나온다. 비공개 함수는 자신을 호출 하는 공개 함수 직후에 나온다.  
  즉, 추상화 단계가 순차적으로 내려간다.

### 2.1.2. 캡슐화

- 변수와 유틸리티 함수는 가능한 공개하지 않는 편이 낫지만 반드시 숨겨야 하는 것은 아니다.  
- 우리에게 테스트는 중요하므로 테스트를 위해 protected로 선언해서 접근을 허용하기도 한다.  
  **하지만 비공개 상태를 유지할 온갖 방법을 강구하고, 캡슐화를 풀어주는 결정은 언제나 최후의 수단이다.**

- 클래스는 작아야 한다!

- 클래스는 첫째! 작아야한다. 둘째! 작아야한다. 더 작아야 한다. 단 함수와는 다르게(함수는 물리적인 행 수로 측정)  **클래스는 맡은 책임을 측정한다.**

### 2.1.3. 개념은 빈 행으로 분리하라

- 코드의 각 줄은 수식이나 절을 나타내고, 여러 줄의 묶음은 완결된 생각 하나를 표현한다.  
- 생각 사이에는 빈 행을 넣어 분리해야한다. 그렇지 않다면 단지 줄바꿈만 다를 뿐인데도 코드 가독성이 현저히 떨어진다.

```java
// 어마어마하게 큰 슈퍼 만능 클래스
public class SuperDashboard extends JFrame implements MetaDataUser {
	public String getCustomizerLanguagePath()
	public void setSystemConfigPath(String systemConfigPath) 
	public String getSystemConfigDocument()
	public void setSystemConfigDocument(String systemConfigDocument) 
	public boolean getGuruState()
	public boolean getNoviceState()
	public boolean getOpenSourceState()
	public void showObject(MetaObject object) 
	public void showProgress(String s)
	public boolean isMetadataDirty()
	public void setIsMetadataDirty(boolean isMetadataDirty)
	public Component getLastFocusedComponent()
	public void setLastFocused(Component lastFocused)
	public void setMouseSelectState(boolean isMouseSelected) 
	public boolean isMouseSelected()
	public LanguageManager getLanguageManager()
	public Project getProject()
	public Project getFirstProject()
	public Project getLastProject()
	public String getNewProjectName()
	public void setComponentSizes(Dimension dim)
	public String getCurrentDir()
	public void setCurrentDir(String newDir)
	public void updateStatus(int dotPos, int markPos)
	public Class[] getDataBaseClasses()
	public MetadataFeeder getMetadataFeeder()
	public void addProject(Project project)
	public boolean setCurrentProject(Project project)
	public boolean removeProject(Project project)
	public MetaProjectHeader getProgramMetadata()
	public void resetDashboard()
	public Project loadProject(String fileName, String projectName)
	public void setCanSaveMetadata(boolean canSave)
	public MetaObject getSelectedObject()
	public void deselectObjects()
	public void setProject(Project project)
	public void editorAction(String actionName, ActionEvent event) 
	public void setMode(int mode)
	public FileManager getFileManager()
	public void setFileManager(FileManager fileManager)
	public ConfigManager getConfigManager()
	public void setConfigManager(ConfigManager configManager) 
	public ClassLoader getClassLoader()
	public void setClassLoader(ClassLoader classLoader)
	public Properties getProps()
	public String getUserHome()
	public String getBaseDir()
	public int getMajorVersionNumber()
	public int getMinorVersionNumber()
	public int getBuildNumber()
	public MetaObject pasting(MetaObject target, MetaObject pasted, MetaProject project)
	public void processMenuItems(MetaObject metaObject)
	public void processMenuSeparators(MetaObject metaObject) 
	public void processTabPages(MetaObject metaObject)
	public void processPlacement(MetaObject object)
	public void processCreateLayout(MetaObject object)
	public void updateDisplayLayer(MetaObject object, int layerIndex) 
	public void propertyEditedRepaint(MetaObject object)
	public void processDeleteObject(MetaObject object)
	public boolean getAttachedToDesigner()
	public void processProjectChangedState(boolean hasProjectChanged) 
	public void processObjectNameChanged(MetaObject object)
	public void runProject()
	public void setAçowDragging(boolean allowDragging) 
	public boolean allowDragging()
	public boolean isCustomizing()
	public void setTitle(String title)
	public IdeMenuBar getIdeMenuBar()
	public void showHelper(MetaObject metaObject, String propertyName) 
	
	// ... many non-public methods follow ...
}
```

```java
// 메소드를 5개로 줄인다고 하더라도 여전히 책임이 많다..

public class SuperDashboard extends JFrame implements MetaDataUser {
	public Component getLastFocusedComponent()
	public void setLastFocused(Component lastFocused)
	public int getMajorVersionNumber()
	public int getMinorVersionNumber()
	public int getBuildNumber() 
}
```

- 클래스 이름은 해당 클래스 책임을 기술해야된다. 작명은 클래스 크기를 줄이는 첫번째 관문이다.
  간결한 이름이 떠오르지 않는다면 클래스 책임이 너무 많아서이다.  
  (e.g. Chapter 2장에 언급한 것 처럼 Manager, Processor, Super 등)

- 또한 클래스 설명은 "if", "and", "or", "but"을 사용하지 않고 25 단어 내외로 가능해야된다.
  한글의 경우 만약, 그리고, ~하며, 하지만 이 들어가면 안된다.

### 2.1.4. 단일 책임의 원칙 - Single Responsibility Principle

- 단일 책임의 원칙 (이하 SRP)은 클래스나 모듈을 변경할 이유가 단 하나뿐이어야 한다는 원칙이다.
  책임, 즉 변경할 이유를 파악하려고 애쓰다 보면 코드를 추상화 하기도 쉬워진다.  

```java
// 이 코드는 작아보이지만, 변경할 이유가 2가지이다.

public class SuperDashboard extends JFrame implements MetaDataUser {
	public Component getLastFocusedComponent()
	public void setLastFocused(Component lastFocused)
	public int getMajorVersionNumber()
	public int getMinorVersionNumber()
	public int getBuildNumber() 
}
```

```java
// 위 코드에서 버전 정보를 다루는 메서드 3개를 따로 빼서
// Version이라는 독자적인 클래스를 만들어 다른 곳에서 재사용하기 쉬워졌다.

public class Version {
	public int getMajorVersionNumber() 
	public int getMinorVersionNumber() 
	public int getBuildNumber()
}
```

- SRP는 객체지향설계에서 더욱 중요한 개념이고, 지키기 수월한 개념인데, 개발자가 가장 무시하는 규칙 중 하나이다.  
- 대부분의 프로그래머들이 **돌아가는 소프트웨어**에 초점을 맞춘다. 전적으로 올바른 태도이기는 하지만,  
  돌아가는 소프트웨어가 작성되면 **깨끗하고 체계적인 소프트웨어**라는 다음 관심사로 전환을 해야한다.

- 작은 클래스가 많은 시스템이든, 큰 클래스가 몇 개뿐인 시스템이든 돌아가는 부품은 그 수가 비슷하다.

> "도구 상자를 어떻게 관리하고 싶은가?  
> 작은 서랍을 많이 두고 기능과 이름이 명확한 컴포넌트를 나눠 넣고 싶은가?  
> 아니면 큰 서랍 몇개를 두고 모두 던져 넣고 싶은가?"  

- **큰 클래스 몇개가 아니라 작은 클래스 여럿으로 이뤄진 시스템이 더 바람직하다.  
  작은 클래스는 각자 맡은 책임이 하나며, 변경할 이유가 하나며, 다른 작은 클래스와 협력해  
  시스템에 필요한 동작을 수행한다.** 

### 2.1.5. 응집도

- 클래스는 인스턴스 변수 수가 작아야 한다.  
- 각 클래스 메서드는 클래스 인스턴스 변수를 하나 이상 사용해야 한다. 
-  일반적으로 메서드가 변수를 더 많이 사용할 수록 메서드와 클래스는 응집도가 더 높다.  
- 모든 인스턴스 변수를 메서드마다 사용하는 클래스는 응집도가 가장 높지만, 이런 클래스는 가능하지도,  
  바람직하지도 않다. 하지만 가능한한 응집도가 높은 클래스를 지향해야 한다.  
- **응집도가 높다는 말은 클래스에 속한 메서드와 변수가 서로 의존하며 논리적인 단위로 묶인다는 의미기 때문이다**

```java
// Stack을 구현한 코드, 응집도가 높은 편이다.

public class Stack {
	private int topOfStack = 0;
	List<Integer> elements = new LinkedList<Integer>();

	public int size() { 
		return topOfStack;
	}

	public void push(int element) { 
		topOfStack++; 
		elements.add(element);
	}
	
	public int pop() throws PoppedWhenEmpty { 
		if (topOfStack == 0)
			throw new PoppedWhenEmpty();
		int element = elements.get(--topOfStack); 
		elements.remove(topOfStack);
		return element;
	}
}
```

- **함수를 작게, 매개변수 목록을 짧게**라는 전략을 따르다 보면  때때로 몇몇 메서드만이 사용하는 인스턴스 변수가 아주 많아진다.  
- 이는 십중 팔구 새로운 클래스를 쪼개야 한다는 신호다.  
  응집도가 높아지도록 변수와 메서드를 적절히 분리해 새로운 클래스 두세 개로 쪼개준다.

### 2.1.6. 응집도를 유지하면 작은 클래스 여럿이 나온다.

- 큰 함수를 작은 함수 여럿으로 나누기만 해도 클래스 수가 많아진다.
- 예를 들어,   
  - 변수가 아주 많은 큰 함수가 하나 있다  
    --> 큰 함수 일부를 작은 함수로 빼내고 싶다   
    --> 빼내려는 코드가 큰 함수에 정의 된 변수를 많이 사용한다  
    --> 변수들을 새 함수에 인수로 넘겨야 하나? NO!  
    --> 변수들을 클래스 인스턴스 변수로 승격 시키면 인수가 필요없다. But! 응집력이 낮아짐  
    --> **몇몇 함수가 몇몇 인스턴스 변수만 사용한다면 독자적인 클래스로 분리해도 된다!**

- 큰 함수를 작은 함수 여럿으로 쪼개다 보면 종종 작은 클래스 여럿으로 쪼갤 기회가 생긴다.

```java
// 이 하나의 크고 더러운 함수를 여러 함수와 클래스로 잘게 나누면서 적절한 이름을 부여해보자!

package literatePrimes;

public class PrintPrimes {
	public static void main(String[] args) {
		final int M = 1000; 
		final int RR = 50;
		final int CC = 4;
		final int WW = 10;
		final int ORDMAX = 30; 
		int P[] = new int[M + 1]; 
		int PAGENUMBER;
		int PAGEOFFSET; 
		int ROWOFFSET; 
		int C;
		int J;
		int K;
		boolean JPRIME;
		int ORD;
		int SQUARE;
		int N;
		int MULT[] = new int[ORDMAX + 1];
		
		J = 1;
		K = 1; 
		P[1] = 2; 
		ORD = 2; 
		SQUARE = 9;
	
		while (K < M) { 
			do {
				J = J + 2;
				if (J == SQUARE) {
					ORD = ORD + 1;
					SQUARE = P[ORD] * P[ORD]; 
					MULT[ORD - 1] = J;
				}
				N = 2;
				JPRIME = true;
				while (N < ORD && JPRIME) {
					while (MULT[N] < J)
						MULT[N] = MULT[N] + P[N] + P[N];
					if (MULT[N] == J) 
						JPRIME = false;
					N = N + 1; 
				}
			} while (!JPRIME); 
			K = K + 1;
			P[K] = J;
		} 
		{
			PAGENUMBER = 1; 
			PAGEOFFSET = 1;
			while (PAGEOFFSET <= M) {
				System.out.println("The First " + M + " Prime Numbers --- Page " + PAGENUMBER);
				System.out.println("");
				for (ROWOFFSET = PAGEOFFSET; ROWOFFSET < PAGEOFFSET + RR; ROWOFFSET++) {
					for (C = 0; C < CC;C++)
						if (ROWOFFSET + C * RR <= M)
							System.out.format("%10d", P[ROWOFFSET + C * RR]); 
					System.out.println("");
				}
				System.out.println("\f"); PAGENUMBER = PAGENUMBER + 1; PAGEOFFSET = PAGEOFFSET + RR * CC;
			}
		}
	}
}
```

위 코드를... 바꿔보자면

```java
package literatePrimes;

public class PrimePrinter {
	public static void main(String[] args) {
		final int NUMBER_OF_PRIMES = 1000;
		int[] primes = PrimeGenerator.generate(NUMBER_OF_PRIMES);
		
		final int ROWS_PER_PAGE = 50; 
		final int COLUMNS_PER_PAGE = 4; 
		RowColumnPagePrinter tablePrinter = 
			new RowColumnPagePrinter(ROWS_PER_PAGE, 
						COLUMNS_PER_PAGE, 
						"The First " + NUMBER_OF_PRIMES + " Prime Numbers");
		tablePrinter.print(primes); 
	}
}
```

```java
package literatePrimes;

import java.io.PrintStream;

public class RowColumnPagePrinter { 
	private int rowsPerPage;
	private int columnsPerPage; 
	private int numbersPerPage; 
	private String pageHeader; 
	private PrintStream printStream;
	
	public RowColumnPagePrinter(int rowsPerPage, int columnsPerPage, String pageHeader) { 
		this.rowsPerPage = rowsPerPage;
		this.columnsPerPage = columnsPerPage; 
		this.pageHeader = pageHeader;
		numbersPerPage = rowsPerPage * columnsPerPage; 
		printStream = System.out;
	}
	
	public void print(int data[]) { 
		int pageNumber = 1;
		for (int firstIndexOnPage = 0 ; 
			firstIndexOnPage < data.length ; 
			firstIndexOnPage += numbersPerPage) { 
			int lastIndexOnPage =  Math.min(firstIndexOnPage + numbersPerPage - 1, data.length - 1);
			printPageHeader(pageHeader, pageNumber); 
			printPage(firstIndexOnPage, lastIndexOnPage, data); 
			printStream.println("\f");
			pageNumber++;
		} 
	}
	
	private void printPage(int firstIndexOnPage, int lastIndexOnPage, int[] data) { 
		int firstIndexOfLastRowOnPage =
		firstIndexOnPage + rowsPerPage - 1;
		for (int firstIndexInRow = firstIndexOnPage ; 
			firstIndexInRow <= firstIndexOfLastRowOnPage ;
			firstIndexInRow++) { 
			printRow(firstIndexInRow, lastIndexOnPage, data); 
			printStream.println("");
		} 
	}
	
	private void printRow(int firstIndexInRow, int lastIndexOnPage, int[] data) {
		for (int column = 0; column < columnsPerPage; column++) {
			int index = firstIndexInRow + column * rowsPerPage; 
			if (index <= lastIndexOnPage)
				printStream.format("%10d", data[index]); 
		}
	}

	private void printPageHeader(String pageHeader, int pageNumber) {
		printStream.println(pageHeader + " --- Page " + pageNumber);
		printStream.println(""); 
	}
		
	public void setOutput(PrintStream printStream) { 
		this.printStream = printStream;
	} 
}
```

```java
package literatePrimes;

import java.util.ArrayList;

public class PrimeGenerator {
	private static int[] primes;
	private static ArrayList<Integer> multiplesOfPrimeFactors;

	protected static int[] generate(int n) {
		primes = new int[n];
		multiplesOfPrimeFactors = new ArrayList<Integer>(); 
		set2AsFirstPrime(); 
		checkOddNumbersForSubsequentPrimes();
		return primes; 
	}

	private static void set2AsFirstPrime() { 
		primes[0] = 2; 
		multiplesOfPrimeFactors.add(2);
	}
	
	private static void checkOddNumbersForSubsequentPrimes() { 
		int primeIndex = 1;
		for (int candidate = 3 ; primeIndex < primes.length ; candidate += 2) { 
			if (isPrime(candidate))
				primes[primeIndex++] = candidate; 
		}
	}

	private static boolean isPrime(int candidate) {
		if (isLeastRelevantMultipleOfNextLargerPrimeFactor(candidate)) {
			multiplesOfPrimeFactors.add(candidate);
			return false; 
		}
		return isNotMultipleOfAnyPreviousPrimeFactor(candidate); 
	}

	private static boolean isLeastRelevantMultipleOfNextLargerPrimeFactor(int candidate) {
		int nextLargerPrimeFactor = primes[multiplesOfPrimeFactors.size()];
		int leastRelevantMultiple = nextLargerPrimeFactor * nextLargerPrimeFactor; 
		return candidate == leastRelevantMultiple;
	}
	
	private static boolean isNotMultipleOfAnyPreviousPrimeFactor(int candidate) {
		for (int n = 1; n < multiplesOfPrimeFactors.size(); n++) {
			if (isMultipleOfNthPrimeFactor(candidate, n)) 
				return false;
		}
		return true; 
	}
	
	private static boolean isMultipleOfNthPrimeFactor(int candidate, int n) {
		return candidate == smallestOddNthMultipleNotLessThanCandidate(candidate, n);
	}
	
	private static int smallestOddNthMultipleNotLessThanCandidate(int candidate, int n) {
		int multiple = multiplesOfPrimeFactors.get(n); 
		while (multiple < candidate)
			multiple += 2 * primes[n]; 
		multiplesOfPrimeFactors.set(n, multiple); 
		return multiple;
	} 
}
```

- **가장 먼저 원래 프로그램의 정확한 동작을 검증하는 테스트 슈트를 작성하라.**  
- **그 다음 한번에 하나씩 여러번에 걸쳐 코드를 변경하고,**  
- **코드를 변경 할 때 마다 테스트를 수행해 원래 프로그램과 동일하게 동작하는지 확인하라.**

### 2.1.7. 변경하기 쉬운 클래스 

- 시스템은 변경이 불가피하다. 그리고 변경이 있을 때 마다 의도대로 동작하지 않을 위험이 따른다.  
- 깨끗한 시스템은 클래스를 체계적으로 관리해 변경에 따르는 위험을 최대한 낮춘다.  

```java
// 해당 코드는 새로운 SQL문을 지원할 때 손대야 하고, 기존 SQL문을 수정할 때도 손대야 하므로 SRP위반

public class Sql {
	public Sql(String table, Column[] columns)
	public String create()
	public String insert(Object[] fields)
	public String selectAll()
	public String findByKey(String keyColumn, String keyValue)
	public String select(Column column, String pattern)
	public String select(Criteria criteria)
	public String preparedInsert()
	private String columnList(Column[] columns)
	private String valuesList(Object[] fields, final Column[] columns) private String selectWithCriteria(String criteria)
	private String placeholderList(Column[] columns)
}
```

- 클래스 일부에서만 사용되는 비공개 메서드는 코드 개선의 잠재적인 여지를 시사한다.

```java
// 공개 인터페이스를 전부 SQL 클래스에서 파생하는 클래스로 만들고, 비공개 메서드는 해당 클래스로 옮기고,
// 공통된 인터페이스는 따로 클래스로 뺐다.
// 이렇게 하면 update문 추가 시에 기존의 클래스를 건드릴 이유가 없어진다.

	abstract public class Sql {
		public Sql(String table, Column[] columns) 
		abstract public String generate();
	}
	public class CreateSql extends Sql {
		public CreateSql(String table, Column[] columns) 
		@Override public String generate()
	}
	
	public class SelectSql extends Sql {
		public SelectSql(String table, Column[] columns) 
		@Override public String generate()
	}
	
	public class InsertSql extends Sql {
		public InsertSql(String table, Column[] columns, Object[] fields) 
		@Override public String generate()
		private String valuesList(Object[] fields, final Column[] columns)
	}
	
	public class SelectWithCriteriaSql extends Sql { 
		public SelectWithCriteriaSql(
		String table, Column[] columns, Criteria criteria) 
		@Override public String generate()
	}
	
	public class SelectWithMatchSql extends Sql { 
		public SelectWithMatchSql(String table, Column[] columns, Column column, String pattern) 
		@Override public String generate()
	}
	
	public class FindByKeySql extends Sql public FindByKeySql(
		String table, Column[] columns, String keyColumn, String keyValue) 
		@Override public String generate()
	}
	
	public class PreparedInsertSql extends Sql {
		public PreparedInsertSql(String table, Column[] columns) 
		@Override public String generate() {
		private String placeholderList(Column[] columns)
	}
	
	public class Where {
		public Where(String criteria) public String generate()
	}
	
	public class ColumnList {
		public ColumnList(Column[] columns) public String generate()
	}
```

- **잘 짜여진 시스템은 추가와 수정에 있어서 건드릴 코드가 최소이다.**
- **변경으로부터 격리**

- OOP입문에서 concrete 클래스와 abstract 클래스가 있는데, 
  - concrete 클래스에 의존(상세한 구현에 의존)하는 클라이언트 클래스는 구현이 바뀌면 위험에 빠진다.  
  - 그래서 인터페이스와 abstract 클래스를 사용해 구현이 미치는 영향을 격리시켜야 한다.  

- **상세한 구현에 의존하는 코드는 테스트가 어려움.**  
  - 그래서 추상화를 통해 테스트가 가능할 정도로 시스템의 결합도를 낮춤으로써  유연성과 재사용성도 더욱 높아진다.

- **결함도가 낮다는 말은 각 시스템 요소가 다른 요소로부터 그리고 변경으로부터 잘 격리되어있다는 뜻이다.**

```java
// Portfolio 클래스를 구현하자, 그런데 이 클래스는 외부 TokyoStockExchange API를 사용해 포트폴리오 값을 계산한다.
// 따라서 API 특성 상 시세 변화에 영향을 많이 받아 5분마다 값이 달라지는데, 이때문에 테스트 코드를 짜기 쉽지 않다.
// 그러므로 Portfolio에서 외부 API를 직접 호출하는 대신 StockExchange라는 인터페이스를 생성한 후 메서드를 선언하다.

public interface StockExchange { 
	Money currentPrice(String symbol);
}
```

```java
// 이후 StockExchange 인터페이스를 구현하는 TokyoStockExchange 클래스를 구현한다.
// 그리고 Portfolio 생성자를 수정해 StockExchange 참조자를 인수로 받는다.

public Portfolio {
	private StockExchange exchange;
	public Portfolio(StockExchange exchange) {
		this.exchange = exchange; 
	}
	// ... 
}
```

```java
// 이제 TokyoStockExchange 클래스를 흉내내는 테스트용 클래스를 만들 수 있다.(FixedStockExchangeStub)
// 테스트용 클래스는 StockExchange 인터페이스를 구현하며 고정된 주가를 반환한다.
// 그럼으로써 무난히 테스트 코드를 작성 할 수 있다.

public class PortfolioTest {
	private FixedStockExchangeStub exchange;
	private Portfolio portfolio;
	
	@Before
	protected void setUp() throws Exception {
		exchange = new FixedStockExchangeStub(); 
		exchange.fix("MSFT", 100);
		portfolio = new Portfolio(exchange);
	}

	@Test
	public void GivenFiveMSFTTotalShouldBe500() throws Exception {
		portfolio.add(5, "MSFT");
		Assert.assertEquals(500, portfolio.value()); 
	}
}

```

- 위에서 개선한 Portfolio 클래스는 상세 구현 클래스가 아닌 StockExchange라는 인터페이스에 의존하므로,  실제로 주가를 얻어오는 출처나 얻어오는 방식 등과 같은 구체적인 사실을 모두 숨길 수 있다.



## 2.2. Method

### 2.2.2. 명령과 조회를 분리하라

- 함수는 뭔가 객체 상태를 변경하거나, 객체 정보를 반환하거나 둘 중 하나다. 둘 다 수행해서는 안 된다.
  - `public boolean set(String attribute, String value);`같은 경우에는 속성 값 설정 성공 시 true를 반환하므로 괴상한 코드가 작성된다.  
  - `if(set(“username”, “unclebob”))...` 그러므로 명령과 조회를 분리해 혼란을 주지 않도록 한다.  

### 2.2.3. 오류코드보다 예외를 사용하라!

- try/catch를 사용하면 오류 처리 코드가 원래 코드에서 분리되므로 코드가 깔끔해 진다.
- Try/Catch 블록 뽑아내기  

```java
if (deletePage(page) == E_OK) {
	if (registry.deleteReference(page.name) == E_OK) {
		if (configKeys.deleteKey(page.name.makeKey()) == E_OK) {
			logger.log("page deleted");
		} else {
			logger.log("configKey not deleted");
		}
	} else {
		logger.log("deleteReference from registry failed"); 
	} 
} else {
	logger.log("delete failed"); return E_ERROR;
}
```

- 정상 작동과 오류 처리 동작을 뒤섞는 추한 구조이므로 if/else와 마찬가지로 블록을 별도 함수로 뽑아내는 편이 좋다.

```java
public void delete(Page page) {
	try {
		deletePageAndAllReferences(page);
  	} catch (Exception e) {
  		logError(e);
  	}
}

private void deletePageAndAllReferences(Page page) throws Exception { 
	deletePage(page);
	registry.deleteReference(page.name); 
	configKeys.deleteKey(page.name.makeKey());
}

private void logError(Exception e) { 
	logger.log(e.getMessage());
}
```

- 오류 처리도 한가지 작업이다.
  - Error.java 의존성 발생

```java
public enum Error { 
	OK,
	INVALID,
	NO_SUCH,
	LOCKED,
	OUT_OF_RESOURCES, 	
	WAITING_FOR_EVENT;
}
```

- 오류를 처리하는 곳곳에서 오류코드를 사용한다면 enum class를 쓰게 되는데 이런 클래스는 의존성이 발생하므로, 새 오류코드를 추가하거나 변경할 때 코스트가 많이 필요하다.
  그러므로 예외를 사용하는 것이 더 안전하다.

 



# 3. Repository Pattern

## 3.1. 개요

- Repository Pattern 은 2004 년 에릭 에반스의 Domain-Driven-Design 에서 처음 소개된 개념으로, 공통적인 데이터 Access & Manipluate 에 집중하여 **도메인 모델 계층과 구현 기술을 분리**시키는 것을 의미한다.

 ![img](./assets/figure30.png)

- 이렇게 함으로써 RDB 나 Query 와 같이 어떠한 **구현 기술에 종속적이지 않고 도메인에 더욱 집중**할 수 있게 되는 패턴을 의미한다.

- Repository 에 대해서 **Martin Fowler** 는  domain 과 data source layer 간에 **중재자 역할을 수행**하는 것이라고 한다.

- repository 는 영속성 장치에서 쿼리의 결과로 받아온 데이터를 repository 에서는 domain 에서 사용하기 적합하도록 Domain 객체로 mapping 하는 역할을 수행한다.

![img](./assets/figure31.png)

- 위 그림은 Jpa 를 사용할 떄 기본으로 사용되는 Repository 의 구현체인 (정확히는 `JpaRepository` 의 구현체) `SimpleJpaRepository` 클래스이다.

- 위와 같이 entity 에 대한 정보를 받기도 하며 실제 connection 을 처리할 entity manager 또한 보유하고 있는 것을 알 수 있다.

- **꼭 영속성 장치일 필요는 없다.**
  - domain 관점에서 보면 repository 뒤에 어떤 장치가 숨어있던 상관 없이 **데이터를 조작하는 데에 필요한 인터페이스만을 바라보고 협력**하기 때문에 RDBMS 이던, WebServer 이던, FileSystem 이던 **상관 없다**.

 

## 3.2. DIP 와 Repository

### 3.2.1. Repository

- **DDD** 에서 말하는 Layered Architecture 를 적용한다면 아마 다음과 같은 구조가 일반적으로 사용될 것이다.

 



![img](./assets/figure32.png)

- 가운데 있는 Infrastructure Persistence Layer 가 바로 Repository 가 존재하는 레이어이다.

>  **Repository 는 도메인을 영속화하는데 필요한 일종의 명세이다.**
>
> 도메인 관점에서 **"나는 이런 것들을 이렇게 저장할 것이고 이렇게 불러올거야!"** 라는 명세를 만들어놓고 실제 구현 기술에 대한 부분을 분리시킨다는 의미이다.

- 위의 **Domain Model Layer** 와 **Infrastructure Layer** 를 나누는 것도 같은 맥락이다.
  -  **Domain Model Layer** 에서는 저장하는 방법에 대해서 관심을 갖고,
  - **Infrastructure Layer** 에서는 실제로 어떻게 저장하는지에 대해서 관심을 갖는다.

 

### 3.2.2 DIP(Dependency Inversion Principle)

- 이 두개의 계층사이를 분리하기 위해서 DIP 를 이용해서 도메인 모델에 존재하는 Repository 추상화로 만들고 실제 구현을 infrastructure 에서 하게 한다.

- DIP 를 사용한다는 것은 **의존의 방향을 역전시**키겠다는 이야기다.

- 즉, **고수준 모듈**(의미 있는 단일 기능)이 **저수준 모듈**(고수준 모듈을 구현하기 위한 기능)에 의존하지 않도록 하기 위함인데, 단지 **선언과 구현을 분리** 쯤으로 생각한다면 잘못된 DIP 의 결과가 나올 수 있다.

- 예를 들면 아래와 같은 형태로 말이다.![img](./assets/figure33.png)

- 이렇게 된다면 Repository 를 다양한 형태의 구현으로 다형적이게 만든다는 조건은 만족시켰다.

- 하지만 여전히 고수준 모듈이 저수준 모듈에 의존하고 있다. 즉, 의존의 관점에서 본다면 `OrderDomainService` 가 infrastructure 를 알게 되는 형태이다.

> 이렇게 의존의 방향이 잘못된다면 많은 고통이 발생할 수 있다. 현재는 이상없는것 처럼 보이겠지만 한 해가 지나고, 다음 해가 지나서 다른 개발자가 도메인 로직에서 Repository 를 추상적인 것에 의존하는 게 아니라 구체적인 ElasticsearchRepository 를 의존했다고 해보자. 그리고 그 다음해에 비즈니스가 변경되어 저장할 필요 없이 단지 API 로 다른 곳에 relay 만 한다고 했을때, 이들을 분리하는 것은 또 다른 pain point 가 될 것이다.

- 그래서 이를 해결하기 위해서 `OrderRepository` 를 고수준 모듈로 만드는 것이다.

 ![img](./assets/figure34.png)

이렇게 된다면 하나의 추상적인 Repository 에 대해서 다양한 구현이 가능하게 된다.

 ![img](./assets/figure35.png)

- 결국 **Repository 는 Jpa 진영에서 DB 와 연결하기 위한 layer 로 부르는 것이 아니라는 것**을 알 수 있다.

- **도메인의 관점**에서 Repository 는 데이터를 저장하는 backing 을 추상화한 것으로 도메인은 어떻게 Repository 에 저장되는지 관심을 갖지 않는다.

- 오로지 도메인 로직 자체에만 관심을 갖는다.

- 그래서 도메인 관점으로 보자면 Repository 를 두고 infrastructure 에서 이를 JPA 를 사용하던 MyBatis 를 사용해서 DAO 계층을 만들건 **중요하지 않게 되는 것**이다.

###  3.3. DDD 관점의 Repository 는?

- 이제 Domain Driven Design, 설계의 관점에서 Repository 를 생각해보자

- DDD 에서는 애그리거트라는 용어가 존재한다.

- 애그리거트는 간략하게 말하자면 **하나의 unit, 비즈니스 단위로 취급할 수 있는 오브젝트의 집합**이다.

- 예를 들어서 **Review** 라는 애그리거트가 존재한다고 해보자.
  -  그럼 해당 Review 라는 애그리거트에는 다음과 같은 오브젝트가 존재할 것이다.
  - Review 에는 글을 쓴 사람인 `Reviewer`
  - 글의 본문인 `Contents`
  - 리뷰의 제목인 `Title`
  - 해당 리뷰의 `Tag`

- 이외에도 리뷰를 표현하는 다양한 오브젝트가 존재할 것인데, 해당 오브젝트는 **리뷰**라는 비즈니스 **개념 하나**를 구성하는 요소들이다.

- 결국 어떤 애그리거트가 저장된다는 소리는 해당 애그리거트에 포함되는 모든 entity 와 value 들에 대해서 **transaction consistency** 를 보장해야 한다.

- 그래서 일반적으로 DDD 에서는 하나의 Aggregate 를 Repository 의 대상 엔티티로 삼는다.

- 즉 Review 라는 애그리거트가 존재할 때, 해당 애그리거트를 저장하고 로드하는 Repository 는 ReviewRepository 만 존재해야 한다는 소리다.

- Review 가 Tag 들을 포함하고 있다고 해서 TagRepository 가 존재해서는 안된다는 것이다.

 

## 3.4. 해결해야 할 문제

- 너무 많은 컨버팅 코드

- **휴먼 에러**
- **JPA 사용시 Lazy Loading 불가**





[출처]

https://learn.microsoft.com/ko-kr/azure/architecture/patterns/cqrs

https://wonit.tistory.com/

https://github.com/microsoftarchive/cqrs-journey

https://github.com/dhslrl321/cqrs-journey-guide-korean

https://www.youtube.com/watch?v=H1IF3BUeFb8&list=PLwouWTPuIjUgr29uSrSkVo8PRmem6HRDE&index=4

 


#  Spring Security 2 ( SpringBoot 3.x, SpringBoot Security 6.x)
 
Vue 와 SpringBoot를 연동하면서 security 활용 방법에 대해서 실습한다.  


1. 프로젝트 생성 및 환경 설정

3. 실행해보기

4. 소스위치   
  - 보안 없음
    - React ( 사원 정보 ) : https://github.com/kt-cloudnative/react_crud_simple
    - SpringBoot 2.x ( 사원 정보 ) : https://github.com/kt-cloudnative/springboot_crud_simple  

  - 보안 추가
    - React ( 사원 정보 ) : https://github.com/kt-cloudnative/react_crud_security
    - vue 3.x ( 사원 정보 ) : https://github.com/kt-cloudnative/vue_crud_security
    - SpringBoot 2.x ( 사원 정보 ) : https://github.com/kt-cloudnative/springboot_crud_security_old
    - SpringBoot 3.x ( 사원 정보 ) : https://github.com/kt-cloudnative/springboot_crud_security
    
5. 참고
  - Sprint Security 6.x
    - https://ksh-coding.tistory.com/m/70
    - https://adjh54.tistory.com/m/221  
    - Filter와 FilterChain : https://velog.io/@zini9188/Spring-Security-Filter%EC%99%80-FilterChain
  

<br/>

## 프로젝트 생성 및 환경 설정 

<br/>

###  Vue Frontend 구성    

<br/>

Vue & Spring Boot 연동.  
- Vue는 5173 port, Spring boot는 8080 port 사용

Vue를 빌드 하기 위해 node.js 를 설치합니다.
node 공식 페이지 ( https://nodejs.org/en/download ) 접속 후 LTS 버전을 클릭하여 다운로드 하고 설치 합니다.  

아래와 같이 node version을 확인합니다.   

```bash
jakelee@jake-MacBookAir Downloads % node -v
v14.19.3
```

<br/>

github 에서 Vue 3.x 소스를 다운 받습니다.  

```bash
jakelee@jake-MacBookAir Downloads % git clone https://github.com/kt-cloudnative/vue_crud_security.git
```  

vue_crud_security 폴더로 이동하여 빌드 ( install ) 하고 start 합니다.  
   
```bash
jakelee@jake-MacBookAir vue_crud_security % npm install
jakelee@jake-MacBookAir vue_crud_security % npm start
```  

브라우저에서 http://localhost:5173 을 입력하면 아래와 같은 로그인 화면이 보입니다.  

<img src="./assets/spring_security3-1.png" style="width: 80%; height: auto;"/> 

<br/>

Vue에서도 backend API 호출은 axios 라이브러리를 사용한다.  

package.json에 추가된 것을 확인 가능    

```bash
{
  "name": "vue3-caravan",
  "version": "0.0.0",
  "private": true,
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext .vue,.js,.jsx,.cjs,.mjs --fix --ignore-path .gitignore",
    "format": "prettier --write src/"
  },
  "dependencies": {
    "@quasar/extras": "^1.16.9",
    "axios": "^1.6.5",
    "pinia": "^2.1.7",
    "quasar": "^2.14.2",
    "vue": "^3.3.11",
    "vue-axios": "^3.5.2",
    "vue-router": "^4.2.5"
  },
  "devDependencies": {
    "@quasar/vite-plugin": "^1.6.0",
    "@rushstack/eslint-patch": "^1.3.3",
    "@vitejs/plugin-vue": "^4.5.2",
    "@vue/eslint-config-prettier": "^8.0.0",
    "eslint": "^8.49.0",
    "eslint-plugin-vue": "^9.17.0",
    "prettier": "^3.0.3",
    "sass": "^1.69.7",
    "vite": "^5.0.10"
  }
}
```  

<br/>

Axios  

- Axios란  
  - 비동기 방식으로 HTTP 데이터 요청을 실행
  - 직접적으로 XMLHttpRequest를 다루지 않고 Ajax를 호출해서 사용
  - Axios의 리턴 값은 Promise 객체 형식
- Ajax란 Asynchronous Javascript And Xml(비동기식 자바스크립트와 xml)의 약자로  XMLHttpRequest객체를 이용해서 데이터를 로드하는 기법
- XMLHttpRequest란 웹 브라우저와 서버 사이의 메소드를 통해 데이터를 전송하는 객체 형식의 API
- Promise란 자바스크립트 비동기 로직 처리에 사용되는 객체이다.  

<br/>

Vue 폴더 구조는 아래와 같습니다.  

<img src="./assets/spring_security4-1.png" style="width: 80%; height: auto;"/>   

- helpers
  - fetch-wrapper.js : axios 핸들러
- plugins
  - axios.js : API 호출 공통 모듈 (interceptor 사용 )
- router
  - index.js : 웹페이지별 라우팅
- stores
  - useAuth.js : 로그인 / 로그아웃
  - useEmployee.js :  Token을 사용하여 API 호출 ( 임직원정보 CRUD ) 
- views
  - EmployeeList.vue : Popup 화면 구성 
  - Employees.vue : 화면 구성
  - Login.vue : 로그인 화면 
  - NotFoundPage.vue  : 에러 처리 화면

App.vue : 시작 페이지
main.js : 라이브러리 로드 


<br/> 

반복되는 작업은 interceptor 를 사용하여 구성.  
token을 가져온 후에 모든 API에는 Header에 Bearer 토큰 설정해야 함.  

```javascript
  //Axios는 자바스크립트에서 HTTP 통신을 위해 쓰이는 Promise 기반 HTTP Client이다.
  //Axios Interceptors는 모든 Request/Response가 목적지에 도달하기 전에 Request에 원하는 내용을 담아 보내거나 원하는 코드를 실행시킬 수 있다.
    
   
// 요청 인터셉터 추가하기
axios.interceptors.request.use(
  function (config) {
    // 요청이 전달되기 전에 작업 수행
    return config;
  },
  function (error) {
    // 요청 오류가 있는 작업 수행
    return Promise.reject(error);
  }
);

// 응답 인터셉터 추가하기
axios.interceptors.response.use(
  function (response) {
    // 2xx 범위에 있는 상태 코드는 이 함수를 트리거 합니다.
    // 응답 데이터가 있는 작업 수행
    return response;
  },
  function (error) {
    // 2xx 외의 범위에 있는 상태 코드는 이 함수를 트리거 합니다.
    // 응답 오류가 있는 작업 수행
    switch (error.response.status) {
      case 401:
        localStorage.removeItem('token');
        window.location.reload();
        break;
      case 403:
      case 404:
        router.push({
          name: 'notfound',
          props: {
            error: {
              message: error.response.data.message,
              status: error.status
            }
          }
        });
        break;
      case 422:
        break;
      default:
        console.log(error.response.data);
    }

    return Promise.reject(error);
  }
);
```  

<br/>


Route/index.js 에서는 API 별로 화면을 분리  

```javascript  
...
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  linkActiveClass: 'active',
  routes: [
    { path: '/', component: () => import('@/views/Login.vue') },
    { path: '/login', component: () => import('@/views/Login.vue') },
    { path: '/employees', component: () => import('@/views/Employees.vue') },
    {
      path: '/:catchAll(.*)*',
      name: 'notfound',
      component: () => import('@/views/NotFoundPage.vue')
    }
  ]
});
...
```  


<br/>

###  SpringBoot Backend 구성  ( SpringBoot 3.x  , Spring Security 6.x)

<br/>

github 에서 SpringBoot 소스를 다운 받습니다.  

```bash
jakelee@jake-MacBookAir Downloads % git clone hhttps://github.com/kt-cloudnative/springboot_crud_security.git
```  

IntelliJ 에서 해당 소스를 오픈 합니다.  

폴더 구조는 아래와 같습니다.  

<img src="./assets/spring_security5.png" style="width: 80%; height: auto;"/>     

- common : 보안 및 config 관련된 패키지
- employee : 임직원 정보 CRUD 가 있는 패키지  

<br/>

security를 위해 dependency가 추가 된것 을 확인합니다.   

```xml
		<!-- Spring Security -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>

		<!-- Jwt Token -->
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt</artifactId>
			<version>0.9.1</version>
		</dependency>

		<!-- springboot 3.x용 jaxb -->

		<dependency>
			<groupId>javax.xml.bind</groupId>
			<artifactId>jaxb-api</artifactId>
			<version>2.3.1</version>
		</dependency>

		<!-- Naver lucy xss filter -->
		<dependency>
			<groupId>com.navercorp.lucy</groupId>
			<artifactId>lucy-xss</artifactId>
			<version>1.6.3</version>
		</dependency>
		<dependency>
			<groupId>com.navercorp.lucy</groupId>
			<artifactId>lucy-xss-servlet</artifactId>
			<version>2.0.0</version>
		</dependency>

		<!-- Jasypt -->
		<dependency>
			<groupId>com.github.ulisesbocchio</groupId>
			<artifactId>jasypt-spring-boot</artifactId>
			<version>2.1.0</version>
			<scope>compile</scope>
		</dependency>
		<dependency>
			<groupId>com.sun.jini</groupId>
			<artifactId>sun-util</artifactId>
			<version>2.1</version>
		</dependency>

		<!-- Oauth 2 -->
		<dependency>
			<groupId>org.springframework.security.oauth.boot</groupId>
			<artifactId>spring-security-oauth2-autoconfigure</artifactId>
			<version>2.1.8.RELEASE</version>
		</dependency>
```  

<br/>

common 패키지에서 붉은 색으로  표기된 부분이 security 관련 Class 입니다.   

<img src="./assets/spring_security6-1.png" style="width: 80%; height: auto;"/>     

<br/>

jasypt 란   

개발자가 암호화 작동 방식에 대한 깊은 지식 없이도 최소한의 노력으로 프로젝트에 기본 암호화 기능을 추가할 수 있도록 하는 Java 라이브러리 입니다.  
주로 프로젝트가 공개되어도 프로퍼티 내에 작성된 설정 정보를 암호화하여 노출시키지 않는 목적으로 사용한다.  

jasyptConfig.java
```java  
public class JasyptConfig{

    private static final String JASYPT_KEY        = "education";
    private static final String ALGORITHM       = "PBEWithMD5AndDES";

    @Bean("jasyptStringEncryptor")
    public StandardPBEStringEncryptor StringEnc() {
        StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig conf = new EnvironmentPBEConfig();
        conf.setPassword(JASYPT_KEY); //암호화할 때 사용하는 키
        conf.setAlgorithm(ALGORITHM); // 암호화 알고리즘
        enc.setConfig(conf);
        return enc;
    }
}
```  

<br/>

application.yml 화일에 bean 이름을 등록한다.  
```yml
#jasypt encryptor
jasypt:
  encryptor:
    bean: jasyptStringEncryptor
```  

<br/>

jasypt 온라인 암복호화 사이트에서 https://www.devglan.com/online-tools/jasypt-online-encryption-decryption 사용할 값을 암호화 한후 key를 복사한다.

- Plain-Text: caravan
- Type: Two Way Encryption  ( With Secret Text )
- Secret-Key: education

<br/>

<img src="./assets/spring_security7.png" style="width: 80%; height: auto;"/>  

<br/>

resources 폴더에 application-local.yml 화일의 db 비밀번호에 
ENC(설정 값) 으로 작성한다.

<img src="./assets/spring_security8.png" style="width: 80%; height: auto;"/>  

<br/> 

SpringBoot에서 암호화된 값을 복호화 해서 가져오는 방법은 아래와 같다.  

EmployeeService.java
```java
// jasypt로 저장된 비밀번호가  복호화 된다.
@Value("${spring.datasource.password}")  // application-local.yml
private String h2Password;
```  

<br/>

이번 소스는 id / password가 소스에 하드 코딩 되어 있다. (edu/edu1234)

<br/>

[ JWT 생성 ]

- POST API with mapping /api/login
- id: user_id, pw: user_pw 입력시 사용자 확인 후 JWT 생성  

<img src="./assets/spring_security9.png" style="width: 100%; height: auto;"/>  

<br/>

JwtRequestFilter  

- OncePerRequestFilter : Spring Security Configuration Class
  - 모든 Request를 가로챈다
  - Request의 헤더에 토큰이 있는지 확인한다
  - 없다면 JwtAuthenticationController를 호출해 토큰을 생성한다

<br/>

JwtAuthenticationController  

- Request Body에서 id, pw를 추출해 AuthenticationManager에게 전달
- 올바른 id, pw가 인증이 되면
  - JwtTokenUtil의 generateToken(UserDetails)을 통해 토큰 생성

<br/>

AuthenticationManager (Spring Security)  

<img src="./assets/spring_security10.png" style="width: 100%; height: auto;"/>  


Spring security Configuration Class  

- User Request Body에서 얻은 id, pw와
- JwtUserDetailsService를 호출해서 얻은 id, pw
  - 현재는 user_id로 하드코딩되어있다
- 두 id,pw를 비교해 사용자 일치하는지 검증
  - if 일치
    - return true
  - else if 불일치
    - throw invalid user exception  

JwtUserDetailsService  

- user의 id, pw return

JwtTokenUtil  

- 토큰을 생성해 return   

<br/>

[ JWT 검증 ]  

<br/>  

- GET API with mapping /api/v1/employee
- user가 유효한 JWT를 가지고 있을 때 접근이 허용됨  

<img src="./assets/spring_security11.png" style="width: 100%; height: auto;"/>  

<br/>

JwtRequestFilter  

- User Request's Header에서 Token이 있는지 확인
있다면 Token 추출
- JwtUserDetailsService에서 userName으로 user의 detail 얻어옴
- JwtTokenUtil에서 Token 유효성 검사
  - validateToken(jwtToken, userDetails)
- 유효한 Token이면 Spring Security에 유효한 User라 알려주고 hello() method에게 접근 가능하게 설정

<br/>

JwtUserDetailsService  

- user의 detail을 return

JwtTokenUtil 

- Token 유효성 검사(Validate Token) 후 return True/False


<br/>

[ Secret Key 설정 ]    

<br/>  

- Hashing algorithm과 함께 사용할 Secret Key를 설정
- Secret Key는 Header, Payload와 결합되어 Hash 생성  

<br/>

application.yml 화일에 key를 입력한다.  

```yml
jwt:
  secret: edujwt
```

<br/>

[ JWtTokenutil ]    

<br/>  

- JWT를 생성하고 검증하는 역할 수행
- io.jsonwebtoken.Jwts 라이브러리 사용  


```java
@Component
public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = -2550185165626007488L;
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //generate token for user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    //while creating the token -
//1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
//2. Sign the JWT using the HS512 algorithm and secret key.
//3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
//   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                //.setExpiration(new Date(System.currentTimeMillis() + 5 * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    //validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
```  

doGenerateToken  

- Token 생성
  - claim : Token에 담을 정보
  - issuer : Token 발급자
  - subject : Token 제목
  - issuedate : Token 발급 시간
  - expiration : Token 만료 시간
    - milliseconds 기준!
    - JWT_TOKEN_VALIDITY = 5 * 60 * 60 => 5시간
- signWith (알고리즘, 비밀키)   

<br/>

[ JwtUserDetailsService ]    

<br/>  

- DB에서 UserDetail를 얻어와 AuthenticationManager에게 제공하는 역할
- 이번에는 DB 없이 하드코딩된 User List에서 get userDetail

계정은 edu/edu1234  이고 password 는 SHA256으로 암호화 하여 생성.

```java
public class JwtUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("edu".equals(username)) {
        //    return new User("user_id", "$2a$10$m/enYHaLsCwH2dKMUAtQp.ksGOA6lq7Fd2pnMb4L.yT4GyeAPRPyS",
            return new User("edu", "db03de15b8000fc35ad975c1322f98124a22521e0616a55c926807eb7225fa38",
                    new ArrayList<>()); //edu1234

        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
```  

- Spring Security 에서는  Password를 BcryptEncoder를 통해 Bcrypt화 하지만 우리는 SHA256으로 변경한다.  
  - https://www.javainuse.com/onlineBcrypt 에서 user_pw를 Bcrypt화
- id : user_id, pw: user_pw로 고정해 사용자 확인
- 사용자 확인 실패시 throw Exception  

<br/>

[ JwtAuthenticationController ]    

<br/>  

- 사용자가 입력한 id, pw를 body에 넣어서 POST API mapping /api/login
- 사용자의 id, pw를 검증
- jwtTokenUtil을 호출해 Token을 생성하고 JwtResponse에 Token을 담아 return ResponseEntity  

<br/>

```java
@RestController
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @PostMapping("/api/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
```  

<br/>

[ JwtRequest ]    

<br/>  

- 사용자에게서 받은 id, pw를 저장  

<br/>

```java
@Data
@NoArgsConstructor
public class JwtRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;

    private String username;
    private String password;

    public JwtRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

}
```  

<br/>

[ JwtResponse ]    

<br/>  

- 사용자에게 반환될 JWT를 담은 Response  

<br/>

```java
public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;

    public JwtResponse(String jwttoken) {
        this.jwttoken = jwttoken;
    }

    public String getToken() {
        return this.jwttoken;
  }
}
```  

<br/>

[ JwtRequestFilter ]    

<br/>  

- Client의 Request를 Intercept해서 Header의 Token가 유효한지 검증
- if 유효한 Token
  - Spring Security의 Authentication을 Setting, to specify that the current user is authenticated 

<br/>

```java
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;
        // JWT Token is in the form "Bearer token". Remove Bearer word and get
        // only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        // Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

            // if token is valid configure Spring Security to manually set
            // authentication
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the
                // Spring Security Configurations successfully.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
```  

<br/>

- JWT Token은 Bearer (스페이스) 로 시작
- jwtTokenUtil.validateToken을 통해 Token 유효성 검사
- if true) UsernamePasswordAuthenticationToken을 설정해 유효한 사용자임을 Spring Security에게 알려준다  

<br/>

[ SecurityConfig ]    

<br/>  

- HttpSecurity를 커스터마이징
  - ex) CORS 등 해결

<br/>

```java
...
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    //@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //return new BCryptPasswordEncoder();
        return new Sha256PasswordEncoder(); // default 대신 SHA256 Encoder 사용함
    }

    // ️ CORS 설정
    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            //config.setAllowedMethods(Collections.singletonList("GET"));
            //config.setAllowedOriginPatterns(Collections.singletonList("http://localhost:3000")); //  허용할 origin
            config.setAllowedOriginPatterns(Collections.singletonList("*")); // ⭐ 허용할 origin
            config.setAllowCredentials(true);
            return config;
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http  //cors
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)  //서버에 인증정보를 저장하지 않기에 csrf를 사용하지 않는다.
                .formLogin(Customizer.withDefaults());
        http
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers(
                                        "/",
                                        "/actuator/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/api/login",
                                        "/login",
                                        "/api/v1/**",
                                        "/h2-console/*"
                                        ).permitAll()
                                .anyRequest().authenticated()
                )
                .headers(
                headersConfigurer ->
                        headersConfigurer
                                .frameOptions(// multi login 시 IFRAME 사용 가능하도록
                                        HeadersConfigurer.FrameOptionsConfig::sameOrigin
                                )
        );
        
        // Add a filter to validate the tokens with every request
        //Session 기반의 인증기반을 사용하지 않고 추후 JWT를 이용하여서 인증 예정
        http.sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
       

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 정적 리소스 spring security 대상에서 제외
        return (web) ->
                web
                        .ignoring()
                        .requestMatchers(
                                PathRequest.toStaticResources().atCommonLocations()
                        );
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(jwtUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
```   


<br/>

[ Employee 패키지 ]    

<br/>  

- employee 패키지에는 임직원 정보 CRUD 가 구현되어 있다.  
- GET 과 POST 로만 구현이 되어 있다.

<img src="./assets/spring_security12.png" style="width: 100%; height: auto;"/>  

<br/>

EmployeeController.java  

```java
@Tag(name = "posts", description = "Employee API")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins ="*")
@RequestMapping("/api/v1/")
public class EmployeeController {

    private final EmployeeService employeeService;


    //@Cacheable(cacheNames = "employee")
    @GetMapping("/employees")
    @Operation(summary ="임직원 전체 조회",description="임직원 전체를 조회 합니다.")
    public List<EmployeeEntity> getEmployeeList() {
        return this.employeeService.getEmployeeList();
    }

    @Operation(summary ="임직원 단건 조회",description="특정 임직원 단건에 대한 정보 조회 합니다.")
    @GetMapping("/employees/{id}")
    public EmployeeEntity getEmployee(@PathVariable Long id) {
        return this.employeeService.getEmployee(id);
    }

    @Operation(summary ="임직원 등록",description="임직원을 등록합니다.")
    @PostMapping("/employees")
    public ResponseEntity<EmployeeEntity> create(@RequestBody EmployeeEntity employeeEntity) {
        EmployeeEntity createdEntity = employeeService.create(employeeEntity);
        return ResponseEntity.ok(createdEntity);
    }

    @PostMapping("/employees/{id}")
    @Operation(summary ="임직원 수정",description="임직원 정보를  수정합니다.")
    public ResponseEntity<EmployeeEntity> update(@PathVariable Long id, @RequestBody EmployeeEntity employeeEntity) {
        EmployeeEntity updatedEntity = employeeService.update(id,employeeEntity);
        return ResponseEntity.ok(updatedEntity);
    }

    @PostMapping("/employee/{id}")
    @Operation(summary ="임직원 정보 삭제",description="임직원 정보를 삭제합니다.")
    public ResponseEntity<EmployeeEntity> delete(@PathVariable Long id) {
        EmployeeEntity deletedEntity = employeeService.delete(id);
        return ResponseEntity.ok(deletedEntity);
    }
}
```  


<br/>

## 실행하기  

<br/>

###  실행해보기

- react는 edu12-3 폴더에서 npm start 명령어를 입력하여 기동을 한다.
- springboot는 intellij 에서 기동한다.  

<br/>

웹브라우저에서 http://localhost:3000 를 입력하고 로그인 을 한다.  
- 계정 : edu/edu1234  

chrome 브라우저에서 개발자 도구 메뉴를 선택하고 Application Tab 을 클릭하고 Local Storage 항목을 보면 token이  생성되어 저장 된 것을 확인 할 수 있다.  


<img src="./assets/spring_security13.png" style="width: 100%; height: auto;"/>  

<br/> 

Add 버튼을 클릭하여 아래와 같이 데이터를 입력하고 SAVE 버튼을 클릭한다.   

<img src="./assets/spring_security14.png" style="width: 100%; height: auto;"/>  

<br/>

데이터가 정상으로 입력되면 아래와 같이 데이터가 보인다.  

<img src="./assets/spring_security15.png" style="width: 100%; height: auto;"/>  

<br/>

IntelliJ 콘솔에서도 로그를 확인 할 수 있다.  

```bash  
edu12-4 19:27:42.416 INFO  jdbc.sqltiming : - INSERT INTO "EMPLOYEE" ("EMPDEPTNAME", "EMPMAIL", "EMPNAME", "EMPTELNO", "ID") VALUES ('SW아키텍처팀', 'seokhwan.lee@kt.com', '이석환', '01030181888', 1)\n {executed in 1 msec} 
edu12-2 19:27:42.458 INFO  com.kt.edu.thirdproject.employee.service.EmployeeService : - Request to get all Employees 
edu12-2 19:27:42.458 INFO  com.kt.edu.thirdproject.employee.service.EmployeeService : - h2 password : caravan 
edu12-2 19:27:42.458 INFO  jdbc.sqltiming : - SELECT "EMPLOYEE"."ID" AS "ID", "EMPLOYEE"."EMPNAME" AS "EMPNAME", "EMPLOYEE"."EMPMAIL" AS "EMPMAIL", "EMPLOYEE"."EMPTELNO" AS "EMPTELNO", "EMPLOYEE"."EMPDEPTNAME" AS "EMPDEPTNAME" FROM "EMPLOYEE"\n {executed in 0 msec} 
edu12-2 19:27:42.466 INFO  jdbc.resultsettable : - \n|---|--------|--------------------|------------|------------|\n|id |empname |empmail             |emptelno    |empdeptname |\n|---|--------|--------------------|------------|------------|\n|1  |이석환     
```  


<br/>

삭제 테스트는 Talend  API를 사용해 보도록 합니다.  

먼저 웹 브라우저에서 개발자 도구로 이동하여 token 값을 복사합니다.  

아래와 같이 설정을 하고 send 버튼을 눌러 실행합니다.  

<img src="./assets/spring_security16.png" style="width: 100%; height: auto;"/>  

- method :  post
- url : http://localhost:8080/api/v1/employee/1
- header
  - key : Authentication
  - value : Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlZHUiLCJleHAiOjE2NTU5MjE5OTYsImlhdCI6MTY1NTkwMzk5Nn0.rMP_jEFKL37Lg5La7jPHBEt9BiVTx0uTAyexd7BV67KOE375XCsR6v7dGKHAOz-tFQmZ7Mz_8LvB-C4EEIQggg 

<br/>

200 메시지를 확인하면 데이터가 정상적으로 삭제가 된 것을 알 수 있습니다.  

<br/>

###  Vue JS Dockerfile 구성

<br/>

Vue js의 도커 파일 구성은 아래와 같다.   

Dockerfile

```bash
#
# Build stage
#
FROM node:lts-alpine as build
WORKDIR /app
ENV PATH /app/node_modules/.bin:$PATH
COPY package*.json ./
RUN npm install
COPY . ./
RUN npm run build

#
# Package stage
#
# production environment
FROM nginx:stable-alpine

ENV TZ Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```  

<br/>

- Build stage : 빌드하여 도커 파일을 만든다.
- package stage : 도커 이미지를 실행하며 nginx 를 앞에 붙인다.

<br/>

###  SpringBoot Dockerfile 구성

<br/>ew

SpringBoot 의 도커 파일 구성은 아래와 같다.   

Dockerfile

```bash
#
# Build stage
#
FROM  maven:3.8.4-openjdk-17 AS MAVEN_BUILD

RUN mkdir -p build
WORKDIR /build

COPY pom.xml ./
COPY src ./src

COPY . ./
RUN mvn package

#
# Package stage
#
# production environment

FROM eclipse-temurin:17.0.2_8-jre-alpine

COPY --from=MAVEN_BUILD /build/target/*.jar app.jar

ENV TZ Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENV SPRING_PROFILES_ACTIVE dev

ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1 -XshowSettings:vm"
ENV JAVA_OPTS="${JAVA_OPTS} -XX:+UseG1GC -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35 -XX:G1ConcRefinementThreads=20"

#ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar  app.jar "]
ENTRYPOINT ["sh", "-c", "java -jar  app.jar "]
```  

<br/>

- Build stage : Maven 이미지로 도커 파일을 만든다.
- package stage : 도커 이미지를 실행하여 컨테이너화 한다.  

<br/>
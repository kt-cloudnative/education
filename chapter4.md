# Chapter 4 
 
CI 구성을 위해 Jenkins와 GitHub 그리고 Docker Hub를 연계하는 방법에 대해 설명한다.   

![](./assets/jenkins_ci_start.png)  


1. GitHub Repository fork  

2. Jenkins 설정  

   - Jenkins는 해당 Repository를 Checkout하여 Docker Image로 빌드 

   - 빌드 완료 후 Docker Hub로 Image를 Push  

 
<br/>

## GitHub Repository fork

<br/>

https://github.com/kt-cloudnative/edu1 repository를 본인의 github 계정에 fork 한다.  

<br/>

## Jenkins 설정

<br/>

### <a name='-1'></a>일반 사용자 계정을 생성한다  

admin 계정으로 테스트 할 예정 으로  skip.  

Manage Jenkins -> Manage Users  로 이동한다. 사용자 생성 버튼 클릭 후 사용자 생성.  
<img src="./assets/jenkins_user.png" style="width: 80%; height: auto;"/>


<br/>

### <a name='-1'></a>계정 별 권한 부여방법 
Configure Global Security로 이동  

admin 계정으로 테스트 할 예정 으로  skip.
    
<img src="./assets/configure_global_security.png" style="width: 80%; height: auto;"/>


생성한 계정을 입력하고 Add 클릭 추가후 권한 설정은 일단 Ovrall 체크 후 저장  
Project-based Matrix Authorization Strategy 체크 후 권한 설정  

<img src="./assets/configure_global_security2.png" style="width: 80%; height: auto;"/>

<br/>

### <a name='Githubtoken'></a>Github token 생성하기

웹브라우저에서 Github 로그인하고 
Jenkins 에서 github repository 인증을 위해 사용할 token 을 생성한다.  

- Settings - Developer settings - Personal access tokens - Generate token  
선택해서 토큰 생성  

Github 사이트의 오른쪽 상단 본인 계정의 Setting으로 이동한다. ( 프로젝트의 setting이 아님 )  

<img src="./assets/github_token_setting1.png" style="width: 60%; height: auto;"/>

Expiration 은 No Expiration으로 선택하고 repo, admin:repo_hook 만 체크하고  Generation Token 버튼 클릭해서 토큰 생성  
 
<img src="./assets/github_token_setting2.png" style="width: 80%; height: auto;"/>


복사 아이콘을 클릭하여 토큰 값을 복사한다. 
- 다시 페이지에 들어가면 보이지 않아서 복사 후 저장 필요  

<img src="./assets/github_token_setting3.png" style="width: 80%; height: auto;"/>

<br/>

### <a name='GitHubCredential.'></a>GitHub Credential을 생성한다.  

<br/>

Jenkins가 GitHub에서 Code를 가져올 수 있도록 Credential을 추가하자

- Manage Jenkins -> Manage Credential -> System 으로 이동한다.

<img src="./assets/jenkins_github_credential1.png" style="width: 80%; height: auto;"/>

Global Credential 클릭  

<img src="./assets/jenkins_github_credential2.png" style="width: 80%; height: auto;"/>

Add Credential를 클릭하면 계정 설정하는 화면이 나온다.  

<img src="./assets/jenkins_github_credential3.png" style="width: 80%; height: auto;"/>

Kind는  Username with password 를 선택해주시면 됩니다.  

Username 은 본인의 Github ID 를 선택해주시면 됩니다. ( 이메일 아님 )  

ID는 본인이 원하는 식별자를 넣어준다.  

password는 이전에 발급받은 Github Token 값을 입력한다.  
       
<img src="./assets/jenkins_github_credential4.png" style="width: 80%; height: auto;"/>

<br/>

### <a name='DockerHubCredential.'></a>Docker Hub Credential을 생성한다.  

Jenkins가 Docker Hub에 Image를 push 할 수 있도록 Credential을 추가하자

- Manage Jenkins -> Manage Credential -> System  -> Global Credential 로 이동한다.

Add Credential를 클릭하면 계정 설정하는 화면이 나온다.  

Kind는  Username with password 를 선택해주시면 됩니다.  

Username 은 본인의 Docker Hub ID 를 선택해주시면 됩니다. ( 이메일 아님 )  

ID는 본인이 원하는 식별자를 넣어준다.  

password는 이전에 Docker Hub 본인 계정의 비밀번호 값을 입력한다.  

<img src="./assets/jenkins_dockerhub_credential.png" style="width: 80%; height: auto;"/>


GitHub와 Docker Hub Credential 이 생선된 것을 확인한다.  

<img src="./assets/jenkins_github_dockerhub_credential.png" style="width: 60%; height: auto;"/>

<br/>

### <a name='.-1'></a>파이프 라인을 구성한다.
        
메인 화면 좌측 메뉴에서 새로운 Item 선택  

<img src="./assets/pipeline_newitem.png" style="width: 60%; height: auto;"/>

item 이름을 입력하고 Pipeline 을 선택 후에 OK  

<img src="./assets/pipeline_main.png" style="width: 60%; height: auto;"/>

로그 Rotation을 5로 설정한다. 5개의 History 를 저장한다. 

<img src="./assets/pipeline_log_rotation.png" style="width: 80%; height: auto;"/>

Jenkins 홈 폴더로 이동하고 아래 명령어를 실행한다.

- 홈 폴더 확인은  대쉬보드에서 Manage Jenkins -> Configure System으로 이동하여 보면 상단에 표시  

<img src="./assets/jenkins_home_folder.png" style="width: 60%; height: auto;"/>  


```bash
cd /var/lib/jenkins
ls ./jobs/edu1/builds
```    

7개의 History중  3,4,5,6,7  5개만 저장된것을 확인 할 수있다.    

<img src="./assets/jenkins_build_folder.png" style="width: 40%; height: auto;"/>  


대쉬보드에서도 History를 확인 할 수 있다.  

<img src="./assets/jenkins_build_history.png" style="width: 40%; height: auto;"/>  


fork한 Github Project URL을 설정하고 Git Parameter 를 체크하고 Parameter Type은 교육을 위한 용도 임으로 Branch를 선택한다. ( Tag는 Release를 위한 빌드 방식으로 그 당시 snapshot 이다. )     

Branch의 default는 origin/master로 설정한다.    

<img src="./assets/pipeline_git.png" style="width: 80%; height: auto;"/>

<br/> 

* Tag 를 사용한 빌드는 맨 아래 부분을 참고한다.

<br/>

Build Triggers - GitHub hook trigger for GITScm polling 선택  
Repository 에 Git Url을 입력한다.  
Credential에 Jenkins에서 생성한 github_ci를 선택하여 추가한다.
 
<img src="./assets/pipeline_git2.png" style="width: 80%; height: auto;"/>


Script Path는 Jenkinsfile 로 설정한다.  
github에 대소문자 구문하여 Jenkinsfile 이 있어야함.  
Save 버튼을 클릭하여 저장한다.  

<img src="./assets/pipeline_scriptpath.png" style="width: 60%; height: auto;"/>

<br/>

### <a name='-1'></a>빌드 실행

빌드 하기 전에 Jenkins 화일로 이동하여 docker hub 의 repository와 docker credential은 본인의 계정으로 설정한다.  

대쉬보드에서 Build With Parameter를 선택하고 Branch 선택 후 빌드 한다.  

<img src="./assets/jenkins_first_build.png" style="width: 60%; height: auto;"/>

빌드가 진행 되는 것을 단계별로 확인 할 수 있다.  

<img src="./assets/build_stage2.png" style="width: 60%; height: auto;"/>

에러가 발생하면 해당 단계에서 마우스 오른쪽 버튼을 클릭하여 로그 확인 할수 있다.  
또한 왼쪽 하단의 Build History 에서 해당 빌드 번호를 클릭하여 자세한 에러를 볼수 있다.  

<img src="./assets/build_error.png" style="width: 60%; height: auto;"/>

Console Output을 선택하고 에러를 확인 할 수 있다.  

<img src="./assets/build_console_output.png" style="width: 60%; height: auto;"/>

아래 에러는 docker hub에서 repository가 private 으로 설정이 되어 발생한 에러이고 위에 설명한 대로 public 으로 변경하면 에러가 발생하지 않는다.  

<img src="./assets/build_docker_access_denied.png" style="width: 60%; height: auto;"/>

대쉬보드에서 해당 파이프라인인 edu1을 선택하고 다시 빌드 한다.  

<img src="./assets/build_again.png" style="width: 60%; height: auto;"/>

성공적으로 완료된 화면을 볼 수 있다.   

<img src="./assets/build_finish.png" style="width: 60%; height: auto;"/>

Docker Hub에서 정상적으로 생성된 이미지를 확인 할수 있다.  

<img src="./assets/build_dockerhub_check.png" style="width: 60%; height: auto;"/>

* Docker build에 에러가 발생하는 경우가 있는데 Jenkins plugins가 정상적을
  설치가 안되어 있을수 있다.   
  docker 검색하여 제대로 설치가 되어 있는지 확인 하고 없으면 재 설치 한다.

<br/>

### <a name='Dockerpull'></a>Docker pull 및 실행 테스트  

터미널로 VM 서버에 접속하여 생성된 도커이미지를 다운로드(pull)하고 실행 (run)  

```bash
docker pull shclub/edu1
```  

<img src="./assets/docker_pull_edu1.png" style="width: 60%; height: auto;"/>  

```bash
docker run -p 40003:8080 shclub/edu1
```
Python Flask 가 정상적으로 로드가 된걸 확인 할 수 있다.

<img src="./assets/docker_run_edu1.png" style="width: 60%; height: auto;"/>  

브라우저에서 http://(본인 VM 공인 ip):40003 호출하여 Hello World 확인
 
<img src="./assets/flask_web_edu1.png" style="width: 60%; height: auto;"/>  

<br/>  

- 과제 : github webhook를 통한 빌드 자동화   ( git push  하면 자동 빌드 )        
    
<br/>

### <a name='Jenkinsfile'></a>Jenkinsfile 설명  

Jenkins 화일에서 github와 docker credential 은  Jenkins 설정에서 Credential을 생성한
id를 입력하면 된다.   

반드시 본인이 만든 값으로 Jenkins파일의 수정해야 함.  

<img src="./assets/pipeline_credential.png" style="width: 80%; height: auto;"/>  

Jenkins에 설정된 credential  

<img src="./assets/jenkins_github_dockerhub_credential.png" style="width: 80%; height: auto;"/>  


Jenkins Stage View 를 통해 Step별 진행 사항을 볼수 있다.  


<img src="./assets/jenkins_stage_view.png" style="width: 60%; height: auto;"/>  

<br/>

### <a name='Jenkins-1'></a>Jenkins 환경변수

<br/>

env 환경변수는 다음과 같은 형식 env.VARNAME으로 참조될 수 있다. 대표적인 env의 property는 아래와 같다. 

<img src="./assets/jenkins_env_variable.png" style="width: 80%; height: auto;"/>  

<br/>

currentBuild 환경변수는 현재 빌드되고 있는 정보를 담고있다. 보통 readonly 옵션인데 일부 writable한 옵션이 존재한다. 대표적인 currentBuild의 property는 아래와 같다.

<img src="./assets/jenkins_current_variable.png" style="width: 80%; height: auto;"/>    


환경 변수 사용 예제.

```bash
pipeline {
    agent any
    stages {
        stage('Example') {
            steps {
                echo "Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
            }
        }
    }
}
```



<br/>

### <a name='TagJenkins'></a>Tag를 사용한 Jenkins 빌드

<br/>

Tag를 사용한 빌드는 운영(Release)를 위해 주로 사용하며 Tagging하는 순간의 snapshot 이다.  

Git Parameter에서 Tag를 선택하고의 default는 RB.0.1 을 임으로 설정한다.  

<img src="./assets/jenkins_tag.png" style="width: 60%; height: auto;"/>  

<br/>

GitHub 로 이동 한후 Repository를 선택 한 후 code Tab 으로 들어간다.  
Tags 아이콘을 클릭한다.

<img src="./assets/github_tag1.png" style="width: 60%; height: auto;"/>  

Tags를 선택하고 Create new release 버튼을 클릭한다.  

<img src="./assets/github_tag2.png" style="width: 60%; height: auto;"/>  

Choose a tag를 클릭하면 tag이름을 입력하는 text 박스가 나온다.

<img src="./assets/github_tag4.png" style="width: 60%; height: auto;"/>  

생성하고 싶은 Tag 이름을 입력한다.  
jenkins pipeline에서 RB.0.1을 기본값으로 설정을 해서 같은 이름으로 입력한다.  
입력창 아래 생성된 + Create new tag : RB.0.1 클릭

<img src="./assets/github_tag5.png" style="width: 60%; height: auto;"/>  

Title 값을 입력 ( 원하는 이름 아무거나 입력 ) 하고 Publish release 버튼을 클릭한다

<img src="./assets/github_tag6.png" style="width: 60%; height: auto;"/>

<br/>

Jenkins 대쉬보드에서 Build with Parameters 선택하면 Tag이름이 RB.0.1로 기본값이 설정된다.  

Tag를 선택하고 Build 버튼을 클릭하면 해당 Tag의 소스로 빌드가 된다.

<img src="./assets/jenkins_tag_build.png" style="width: 60%; height: auto;"/>  

<br/>


### 과제 1

<br/>

Vue/SpringBoot 로 개발한 소스를 docker로 빌드하는 pipeline을 작성합니다.  
 
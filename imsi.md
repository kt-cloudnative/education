
root@newedu-k3s:/data/rancher/k3s/server/manifests# (
>   set -x; cd "$(mktemp -d)" &&
>   OS="$(uname | tr '[:upper:]' '[:lower:]')" &&
>   ARCH="$(uname -m | sed -e 's/x86_64/amd64/' -e 's/\(arm\)\(64\)\?.*/\1\2/' -e 's/aarch64$/arm64/')" &&
>   KREW="krew-${OS}_${ARCH}" &&
>   curl -fsSLO "https://github.com/kubernetes-sigs/krew/releases/latest/download/${KREW}.tar.gz" &&
>   tar zxvf "${KREW}.tar.gz" &&
>   ./"${KREW}" install krew
> )
++ mktemp -d
+ cd /tmp/tmp.Mpu8R2HKkl
++ uname
++ tr '[:upper:]' '[:lower:]'
+ OS=linux
++ uname -m
++ sed -e s/x86_64/amd64/ -e 's/\(arm\)\(64\)\?.*/\1\2/' -e 's/aarch64$/arm64/'
+ ARCH=amd64
+ KREW=krew-linux_amd64
+ curl -fsSLO https://github.com/kubernetes-sigs/krew/releases/latest/download/krew-linux_amd64.tar.gz
+ tar zxvf krew-linux_amd64.tar.gz
./LICENSE
./krew-linux_amd64
+ ./krew-linux_amd64 install krew
Adding "default" plugin index from https://github.com/kubernetes-sigs/krew-index.git.
Updated the local copy of plugin index.
Installing plugin: krew
Installed plugin: krew
\
 | Use this plugin:
 | 	kubectl krew
 | Documentation:
 | 	https://krew.sigs.k8s.io/
 | Caveats:
 | \
 |  | krew is now installed! To start using kubectl plugins, you need to add
 |  | krew's installation directory to your PATH:
 |  |
 |  |   * macOS/Linux:
 |  |     - Add the following to your ~/.bashrc or ~/.zshrc:
 |  |         export PATH="${KREW_ROOT:-$HOME/.krew}/bin:$PATH"
 |  |     - Restart your shell.
 |  |
 |  |   * Windows: Add %USERPROFILE%\.krew\bin to your PATH environment variable
 |  |
 |  | To list krew commands and to get help, run:
 |  |   $ kubectl krew
 |  | For a full list of available plugins, run:
 |  |   $ kubectl krew search
 |  |
 |  | You can find documentation at
 |  |   https://krew.sigs.k8s.io/docs/user-guide/quickstart/.
 | /
/
root@newedu-k3s:/data/rancher/k3s/server/manifests# export PATH="${KREW_ROOT:-$HOME/.krew}/bin:$PATH"
root@newedu-k3s:/data/rancher/k3s/server/manifests#
root@newedu-k3s:/data/rancher/k3s/server/manifests# kubectl krew
krew is the kubectl plugin manager.
You can invoke krew through kubectl: "kubectl krew [command]..."

Usage:
  kubectl krew [command]

Available Commands:
  help        Help about any command
  index       Manage custom plugin indexes
  info        Show information about an available plugin
  install     Install kubectl plugins
  list        List installed kubectl plugins
  search      Discover kubectl plugins
  uninstall   Uninstall plugins
  update      Update the local copy of the plugin index
  upgrade     Upgrade installed plugins to newer versions
  version     Show krew version and diagnostics

Flags:
  -h, --help      help for krew
  -v, --v Level   number for the log level verbosity

Use "kubectl krew [command] --help" for more information about a command.
root@newedu-k3s:/data/rancher/k3s/server/manifests# kubectl krew install oidc-login
Updated the local copy of plugin index.
Installing plugin: oidc-login
Installed plugin: oidc-login
\
 | Use this plugin:
 | 	kubectl oidc-login
 | Documentation:
 | 	https://github.com/int128/kubelogin
 | Caveats:
 | \
 |  | You need to setup the OIDC provider, Kubernetes API server, role binding and kubeconfig.
 | /
/
WARNING: You installed plugin "oidc-login" from the krew-index plugin repository.
   These plugins are not audited for security by the Krew maintainers.
   Run them at your own risk.
root@newedu-k3s:/data/rancher/k3s


kubectl oidc-login setup \
  --oidc-issuer-url=https://localhost:40007/realms/k8s-realm \
  --oidc-client-id=k8s-client \
  --oidc-client-secret=tJyAK3Jo4Xnx8lfYZwZSB7bctFUroQft

  root@newedu-k3s:~/keycloak# curl https://localhost:6443/api/v1/namespaces/default --header "Authorizaton: Bearer ${TOKEN}" --insecure
{
  "kind": "Status",
  "apiVersion": "v1",
  "metadata": {},
  "status": "Failure",
  "message": "Unauthorized",
  "reason": "Unauthorized",
  "code": 401


  //////



2/15 과제 : frontend/backend with keycloak 연동 하기 

참고 : https://calgary.tistory.com/57

예제 소스 : https://github.com/ivangfr/springboot-react-keycloak

위의 예제 소스를 사용하여 keycloak 연동해 보기

1) 로컬에 Docker compose로 구성
2) postgres는 k3s 설치한 버전 사용 할 수도 있음. 본인의 선택.
   - k3s 사용시 추가로 DB 생성 : keycloak2
3) keycloak는 local 에 설치 하고 사용해야 함. External 사용시 secret 키 필요
4) springboot는 intellij 로 실행  9080 포트 , react는 vs code로 실행
5) admin 계정으로 연동시에는 모든 권한 필요 , user 계정으로 연결시에는 조회 와 comment 만 가능

* 주의 사항 :
   1) KeycloakInitializerRunner class 는  CommandLineRunner
 사용하여 기동시 마다 keycloak에 realm 생성 후 삭제 함.
 2)한번 realm 생성 한후에 CommandLineRunner 주석처리 하거나  처음부터 주석처리하고 직접 keycloak에 realm 과 계정 생성 해도 됨


2/19 예상 과제 : 기존 Employee 예제를 keycloak 연동 소스로 변경하기. 

- Vue.js : https://github.com/kt-cloudnative/vue_crud_security
- Springboot : https://github.com/kt-cloudnative/springboot_crud_security

- 조건
1) admin은 모든권한 , edu 계정은 frontend  에서 view 아이콘만 보이고 조회만 가능



eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJVOFVDY2MtWnppNW9xYVhPZVZnWmdsLUxURmpfYXJ3dlJ2dl91Mjc4ZWNrIn0.eyJleHAiOjE2NjU1ODQ5NTIsImlhdCI6MTY2NTU4NDY1MiwianRpIjoiNmJhNDY1ZDktNmVmYi00Mzk5LTgyMTUtZjcxNjk0MzdhYzZhIiwiaXNzIjoiaHR0cDovLzE5Mi4xNjguMTkzLjQ6ODA4MC9yZWFsbXMvbmdpbngiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiYTk1MTE3YmYtMWEyZS00ZDQ2LTljNDQtNWZkZWU4ZGRkZDExIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoibmdpbngtcGx1cyIsInNlc3Npb25fc3RhdGUiOiI5ODM2ZjVmZC05ODdmLTQ4NzUtYWM3NS1mN2RkNTMyNTA0N2MiLCJhY3IiOiIxIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtbmdpbngiLCJvZmZsaW5lX2FjY2VzcyIsIm5naW54LWtleWNsb2FrLXJvbGUiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwiLCJzaWQiOiI5ODM2ZjVmZC05ODdmLTQ4NzUtYWM3NS1mN2RkNTMyNTA0N2MiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6Im5naW54LXVzZXIiLCJnaXZlbl9uYW1lIjoiIiwiZmFtaWx5X25hbWUiOiIifQ.MIqg3Q-pXvVG04leKBiVaDGCjv4gfsp3JywCumQ3CIk8cck9Q6tptM2CWIznmQLi4K6RUu7i7TodTnZAMDids0c-igX8oEe6ZLuR_Ub9SQSdVLymforfGYcSNJfnVVGLF8KHqPeLOp0TVPXxf56Qv6BO7B6fDGBxUvDsWEsw_5ko5v1pRiSHK-VS3zjw5weoJBD4rnYo9ZdhqYwyzL_nrUEWd05uWs4H-zCLKjTHw0AVPFO9MJ6OawJ7sc8AKeLq4FOKg2A_mIDF7SDds43UUvfU
AK5a2zoy5PYhhESx0C5V7YTaaJDtiGFH1iY27_Yj3DcEQDZBBhDTRKrs3K7wxA


http://localhost:8080/realms/company-services/protocol/openid-connect/login-status-iframe.html/init?client_id=movies-app&origin=http%3A%2F%2Flocalhost%3A3000



import {useUserStore} from "@/keycloak/user";
import {onMounted} from "vue";
import {serviceFactory} from "@/keycloak/factory";
import Products from "@/components/Products.vue";
import TopBar from "@/components/TopBar.vue";
import {getEnableKeycloak} from "@/keycloak/config";

const userStore = useUserStore();
const keycloakService = serviceFactory(getEnableKeycloak(), userStore)
const logout = () => keycloakService.logout()

onMounted(() => {
  keycloakService.login()
})
  


curl -X POST http://211.252.87.34:40007/realms/k8s-realm/protocol/openid-connect/token \
-d grant-type=password -d client_id=k8s-secret -d username=k8s-admin -d password=“New1234!” -d  \
scope=openid -d client_secret=“tJyAK3Jo4Xnx8lfYZwZSB7bctFUroQft”  | jq -r ‘.id_token’


curl -X 'POST' \
  'http://211.252.87.34:40007/realms/k8s-realm/protocol/openid-connect/token' \
  -d grant-type=password -d client_id=k8s-secret -d username=k8s-admin -d password='New1234!' -d  scope=openid -d  client_secret='tJyAK3Jo4Xnx8lfYZwZSB7bctFUroQft' -K   | jq -r '.id_token'

curl -X 'POST' \
  'http://localhost:40007/realms/k8s-realm/protocol/openid-connect/token' \
  -d grant-type=password -d client_id=k8s-secret -d username=k8s-admin -d password='New1234!' -d  scope=openid -d  client_secret='tJyAK3Jo4Xnx8lfYZwZSB7bctFUroQft' -K

  curl -X POST 'http://localhost:40007/realms/k8s-realm/protocol/openid-connect/token' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'grant_type=password' \
  --data-urlencode 'client_id=k8s-client \
  --data-urlencode 'client_secret=tJyAK3Jo4Xnx8lfYZwZSB7bctFUroQft' \
  --data-urlencode 'username=k8s-admin' \
  --data-urlencode 'password=New1234!' \
  --data-urlencode 'redirect_uri=<Redirect URI>'


curl \
  -d "client_id=k8s-admin” \
  -d "client_secret=tJyAK3Jo4Xnx8lfYZwZSB7bctFUroQft" \
  -d "grant_type=client_credentials” \
  "http://localhost:40007/realms/k8s-realm/protocol/openid-connect/token"


root@newedu-k3s:~# curl -X POST http://localhost:40007/realms/k8s-realm/protocol/sername=k8s-admin -d password="New1234!" -d scope=openid -d client_secret=tJyAK3Jo4Xnx8lfYZwZSB7bctFUroQft
{"access_token":"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJCM01pU1FJNmlpR0ZQbDFVbVpkWlkxbUlzU2tvU2VmR09LcUtaSllLWjk0In0.eyJleHAiOjE3MDgyMzAzMzQsImlhdCI6MTcwODIzMDAzNCwianRpIjoiZmJjM2M1ZTMtZWI1ZS00YTk2LWFkYTQtYWNkYWJmMzA1NjU5IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo0MDAwNy9yZWFsbXMvazhzLXJlYWxtIiwic3ViIjoiOTZlNDdlM2UtOGMxOS00MjFmLWFkNDQtZGQ3MzY2YWExZjFjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiazhzLWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiI4NGE2YzA1ZS1lOGRmLTQ4MjAtYmU4ZS04Y2JlNGJjMzlkMTMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIi8qIl0sInJlc291cmNlX2FjY2VzcyI6eyJrOHMtY2xpZW50Ijp7InJvbGVzIjpbIms4cy1yb2xlIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsInNpZCI6Ijg0YTZjMDVlLWU4ZGYtNDgyMC1iZThlLThjYmU0YmMzOWQxMyIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6Impha2UgbGVlIiwiZ3JvdXBzIjpbIms4cy1yb2xlIl0sInByZWZlcnJlZF91c2VybmFtZSI6Ims4cy1hZG1pbiIsImdpdmVuX25hbWUiOiJqYWtlIiwiZmFtaWx5X25hbWUiOiJsZWUifQ.0m64kvvwrGl2PlPdpaAQeHbVNuGKknFfPbxOjn3GQl09_sbDg6fwW2oejXgMC8gA0nzeLPos4SwncMBszLN7jK4ix4rbN8QKi9jTpZNLM30PeODRT5EhJNNWynIbfs6DDQyxolhIn3kMVz8Rt6oW-Ifdc0nxGh3h5hgqRNGJ-x5r9mNn5e5QQGv4-PexllpOBL7D7f5qor87cWsG4-cdHvFFHRmlQg4HVIcah5qV__6ixCwxAKDxE5l5pQMf4DqQmFH8JNhdQNAXT9XyCvYJVUxwc_LI44yqIKyrXVerXA61XicC6xcKZkEPErb8dCWZt43kPN5_P6wkXhK25cp6qQ","expires_in":300,"refresh_expires_in":1800,"refresh_token":"eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI5MTA1OGRmMi0zZjZjLTQwZWYtYTcxYi00YjdkMGYxM2I4NDUifQ.eyJleHAiOjE3MDgyMzE4MzQsImlhdCI6MTcwODIzMDAzNCwianRpIjoiZjdlZWQwNjctYjRhOC00NDQxLWE5NjAtMzRhNDU3NDFhZWEwIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo0MDAwNy9yZWFsbXMvazhzLXJlYWxtIiwiYXVkIjoiaHR0cDovL2xvY2FsaG9zdDo0MDAwNy9yZWFsbXMvazhzLXJlYWxtIiwic3ViIjoiOTZlNDdlM2UtOGMxOS00MjFmLWFkNDQtZGQ3MzY2YWExZjFjIiwidHlwIjoiUmVmcmVzaCIsImF6cCI6Ims4cy1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiODRhNmMwNWUtZThkZi00ODIwLWJlOGUtOGNiZTRiYzM5ZDEzIiwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsInNpZCI6Ijg0YTZjMDVlLWU4ZGYtNDgyMC1iZThlLThjYmU0YmMzOWQxMyJ9.qnWesq3QV1flgA_m6TuSZR-UrxM4f-o94zMW0UJefAs","token_type":"Bearer","id_token":"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJCM01pU1FJNmlpR0ZQbDFVbVpkWlkxbUlzU2tvU2VmR09LcUtaSllLWjk0In0.eyJleHAiOjE3MDgyMzAzMzQsImlhdCI6MTcwODIzMDAzNCwiYXV0aF90aW1lIjowLCJqdGkiOiIyZTc4MThlNi1hOGE5LTQ2ZDktYTZhZi1lMTQ1YjZjYzQxZWUiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjQwMDA3L3JlYWxtcy9rOHMtcmVhbG0iLCJhdWQiOiJrOHMtY2xpZW50Iiwic3ViIjoiOTZlNDdlM2UtOGMxOS00MjFmLWFkNDQtZGQ3MzY2YWExZjFjIiwidHlwIjoiSUQiLCJhenAiOiJrOHMtY2xpZW50Iiwic2Vzc2lvbl9zdGF0ZSI6Ijg0YTZjMDVlLWU4ZGYtNDgyMC1iZThlLThjYmU0YmMzOWQxMyIsImF0X2hhc2giOiJBVzhGaUdpRkoyUnVTc0NESFFlZU53IiwiYWNyIjoiMSIsInNpZCI6Ijg0YTZjMDVlLWU4ZGYtNDgyMC1iZThlLThjYmU0YmMzOWQxMyIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6Impha2UgbGVlIiwiZ3JvdXBzIjpbIms4cy1yb2xlIl0sInByZWZlcnJlZF91c2VybmFtZSI6Ims4cy1hZG1pbiIsImdpdmVuX25hbWUiOiJqYWtlIiwiZmFtaWx5X25hbWUiOiJsZWUifQ.bHUanXTNc_uDWpCkuZjC9_8JnPc1QkV_29RchyzJ4UTaIz2zhtYjA0UkUfU1hsaGqomS1yAlSfnlaBoiUa_Mhj3dNtBjzt-yYv6T53hcM_3jWZjpsq1tJ2CaDFTmY055guP3Sv36cggZFJw3UI5ghMpO6C4ihP7olSG0saFvppcO3wr0RJ0TZbjpGJFFK1AikPq8pRnunll4u-0iYis2phnc5aJ_HOnvzDneOBpBntyTHxTCdzRRLOIcIzMvRNjut3xFOHY2i67S9wTigpmr2yxMjLpOPOQZdGKA7LshAUNoX89fFFHjkwd8xYtnOvAYgXC44BpK1q6xjMuUfUq_tA","not-before-policy":0,"session_state":"84a6c05e-e8df-4820-be8e-8cbe4bc39d13","scope":"openid profile email"}


curl -X POST http://211.252.87.34:40007/realms/k8s-realm/protocol/openid-connect/token -d grant_type=password -d client_id=k8s-client -d username=k8s-admin -d password="New1234!" -d scope=openid -d client_secret=tJyAK3Jo4Xnx8lfYZwZSB7bctFUroQft

root@newedu-k3s:/etc/rancher/k3s# vi /etc/rancher/k3s/config.yaml


root@newedu-k3s:/etc/rancher/k3s# cat config.yaml
kube-apiserver-arg:
- "oidc-issuer-url=https://localhost:40007/realms/k8s-realm/"
- "oidc-client-id=k8s-client"
- "oidc-username-claim=preferred_username"
- "oidc-groups-claim=groups"

root@newedu-k3s:/etc/rancher/k3s# systemctl restart k3s

root@newedu-k3s:~/keycloak# curl https://localhost:6443/api/v1/namespaces/default --header "Authorizaton: Bearer ${TOKEN}" --insecure
{
  "kind": "Status",
  "apiVersion": "v1",
  "metadata": {},
  "status": "Failure",
  "message": "Unauthorized",
  "reason": "Unauthorized",
  "code": 401
}


    '--kube-apiserver-arg' 'oidc-username-claim=preferred_username' \
    '--kube-apiserver-arg' 'oidc-groups-claim=groups' \
    '--kube-apiserver-arg' 'oidc-client-id=k8s-client' \
    '--kube-apiserver-arg' 'oidc-issuer-url=https://localhost:40007/realms/k8s-realm/'





curl -X POST http://localhost:40007/realms/k8s-realm/protocol/username=k8s-admin -d password="New1234!" -d scope=openid -d client_secret=tJyAK3Jo4Xnx8lfYZwZSB7bctFUroQft

root@newedu-k3s:~/keycloak# helm repo add jetstack https://charts.jetstack.io
"jetstack" has been added to your repositories

root@newedu-k3s:~/keycloak# helm repo update
Hang tight while we grab the latest from your chart repositories...
...Successfully got an update from the "nfs-subdir-external-provisioner" chart repository
...Successfully got an update from the "apache-airflow" chart repository
...Successfully got an update from the "elastic" chart repository
...Successfully got an update from the "jenkins" chart repository
...Successfully got an update from the "jetstack" chart repository
...Successfully got an update from the "prometheus-community" chart repository
...Successfully got an update from the "bitnami" chart repository
Update Complete. ⎈Happy Helming!⎈


root@newedu-k3s:~/keycloak# kubectl create namespace cert-manager

root@newedu-k3s:~/keycloak# helm install cert-manager jetstack/cert-manager --namespace cert-manager --version v1.8.0


root@newedu-k3s:~/keycloak# kubectl get po -n cert-manager
NAME                                       READY   STATUS    RESTARTS   AGE
cert-manager-cainjector-5c55bb7cb4-gjkf4   1/1     Running   0          42s
cert-manager-76578c9687-kn6cd              1/1     Running   0          42s
cert-manager-startupapicheck-fngwv         1/1     Running   0          42s
cert-manager-webhook-556f979d7f-nzkmw      1/1     Running   0          42s



 root@newedu-k3s:~/security# helm install    cert-manager jetstack/cert-manager    --namespace cert-manager  --version v1.1.1 --set installCRDs=true --set global.leaderElection.namespace=cert-manager
NAME: cert-manager
LAST DEPLOYED: Mon Feb 19 13:04:51 2024
NAMESPACE: cert-manager
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
cert-manager has been deployed successfully!

In order to begin issuing certificates, you will need to set up a ClusterIssuer
or Issuer resource (for example, by creating a 'letsencrypt-staging' issuer).

More information on the different types of issuers and how to configure them
can be found in our documentation:

https://cert-manager.io/docs/configuration/

For information on how to configure cert-manager to automatically provision
Certificates for Ingress resources, take a look at the `ingress-shim`
documentation:

https://cert-manager.io/docs/usage/ingress/
root@newedu-k3s:~/security# helm list -n cert-manager
NAME        	NAMESPACE   	REVISION	UPDATED                                	STATUS  	CHART              	APP VERSION
cert-manager	cert-manager	1       	2024-02-19 13:04:51.712393225 +0000 UTC	deployed	cert-manager-v1.1.1	v1.1.1
root@newedu-k3s:~/security# kubectl get po -n cert-manager
NAME                                       READY   STATUS    RESTARTS   AGE
cert-manager-cainjector-57b69db8bb-pxgzd   1/1     Running   0          15s
cert-manager-6649cbf787-kzw94              1/1     Running   0          15s
cert-manager-webhook-67b45687b7-knxff      1/1     Running   0          15s



-------------

finition.apiextensions.k8s.io/orders.acme.cert-manager.io created
root@newedu-k3s:~/security# helm repo add jetstack https://charts.jetstack.io
"jetstack" already exists with the same configuration, skipping
root@newedu-k3s:~/security# helm install cert-manager --namespace cert-manager --version v1.14.2 jetstack/cert-manager
NAME: cert-manager
LAST DEPLOYED: Mon Feb 19 13:43:10 2024
NAMESPACE: cert-manager
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
cert-manager v1.14.2 has been deployed successfully!

In order to begin issuing certificates, you will need to set up a ClusterIssuer
or Issuer resource (for example, by creating a 'letsencrypt-staging' issuer).

More information on the different types of issuers and how to configure them
can be found in our documentation:

https://cert-manager.io/docs/configuration/

For information on how to configure cert-manager to automatically provision
Certificates for Ingress resources, take a look at the `ingress-shim`
documentation:

https://cert-manager.io/docs/usage/ingress/

root@newedu-k3s:~/security# helm list -n cert-manager
NAME        	NAMESPACE   	REVISION	UPDATED                                	STATUS  	CHART               	APP VERSION
cert-manager	cert-manager	1       	2024-02-19 13:43:10.710935779 +0000 UTC	deployed	cert-manager-v1.14.2	v1.14.2
root@newedu-k3s:~/security# kubectl get po -n cert-manager
NAME                                            READY   STATUS      RESTARTS   AGE
my-release-cert-manager-startupapicheck-jdkg7   0/1     Completed   2          4m39s
cert-manager-678788484c-2wn9m                   1/1     Running     0          60s
cert-manager-cainjector-6d77d76d64-j6rk5        1/1     Running     0          60s
cert-manager-webhook-8697986756-66772           1/1     Running     0          60s

-----
root@newedu-k3s:~/security# kubectl apply -f issuer.yaml -n cert-manager
clusterissuer.cert-manager.io/letsencrypt-staging created
clusterissuer.cert-manager.io/letsencrypt-prod created

----




root@newedu-k3s:~/keycloak# kubectl create secret tls test-tls --cert /certs/wildcard-cert.pem --key /certs/wildcard-key.pem
secret/test-tls created
root@newedu-k3s:~/keycloak# kubectl get ing -n keycloak
NAME                        CLASS    HOSTS                                    ADDRESS       PORTS   AGE
keycloak-ingress            <none>   keycloak.211.252.87.34.nip.io            172.27.0.41   80      12d
frontend-keycloak-ingress   <none>   frontend-keycloak.211.252.87.34.nip.io   172.27.0.41   80      148m
root@newedu-k3s:~/keycloak# kubectl create secret tls test-tls --cert /certs/wildcard-cert.pem --key /certs/wildcard-key.pem -n keycloak
secret/test-tls created
root@newedu-k3s:~/keycloak# ls
front_ing.yaml  front_ing_ssl.yaml  iss.yaml  keycloak-ingress.yaml  keycloak_emp.sh  keycloak_token.sh  rb.yaml  rbac.yaml  test.yaml  values.yaml
root@newedu-k3s:~/keycloak# vi test.yaml
root@newedu-k3s:~/keycloak# cp test.yaml front_duckdns_ing.yaml
root@newedu-k3s:~/keycloak# vi front_duckdns*
root@newedu-k3s:~/keycloak# vi front_duckdns*
root@newedu-k3s:~/keycloak# kubectl apply -f front_duckdns_ing.yaml -n keycloak
ingress.networking.k8s.io/duckdns-ingress created
root@newedu-k3s:~/keycloak# kubectl get ing -n keycloak
NAME                        CLASS    HOSTS                                           ADDRESS       PORTS     AGE
keycloak-ingress            <none>   keycloak.211.252.87.34.nip.io                   172.27.0.41   80        12d
frontend-keycloak-ingress   <none>   frontend-keycloak.211.252.87.34.nip.io          172.27.0.41   80        151m
duckdns-ingress             <none>   frontend-keycloak-ssl.kteducation.duckdns.org                 80, 443   5s



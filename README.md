# spring-boot-oauth2-jwt-authorization-server
Access token

Para invocar la autentificacion con Authentication code: GET -> https://localhost:8446/oauth/authorize?response_type=code&client_id=client_id&redirect_uri=ruta_de_redireccion

Para la peticion de token: POST -> https://localhost:8446/oauth/tokens y en body "grant_type=authorization_code&redirect_uri=ruta_de _redireccion&code=code"

Refresh token

Para solicitar refresh token : POST -> https://localhost:8446/oauth/tokens y en body "grant_type=refresh_token&client_id=client_id&client_secret=secret&refresh_token=refresh_token"

Revoke

Logout Para hacer logout: POST-> https://localhost:8446/oauth/tokens/revoke y en el body "token=token&token_type_hint=access_token"

Para las peticiones POST usar 'application/x-www-form-urlencoded'.

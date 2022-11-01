# Configure Keycloak Authorization Server

### 1. Configure Keycloak

1. Start Keycloak server
    - From folder `./qraphql-oauth2` run
    - `docker-compose --env-file .env --profile=import-realm up -d`
    - To stop keycloak run
    - `docker-compose --env-file .env --profile=import-realm down`
2. Log in
    - [http://localhost:8181/admin](http://localhost:8181/admin)
    - Username: `admin`
    - Password: `Pa55w0rd`
3. Create realm `graphql-movie-app`
4. Add Roles
    - Realm Roles &rarr;
    - `app_user_role`
    - `app_admin_role`
    - `app_super_user_role`
5. Add Groups
    - Groups:
    - `app_user_group`
        - Role Mapping &rarr; Assign Role &rarr; `app_user_role`
    - `app_admin_group`
        - Role Mapping &rarr; Assign Role &rarr; `app_admin_role`
    - `app_super_user_group`
        - Role Mapping &rarr; Assign Role &rarr; `app_super_user_role`
6. Create Users
    - User 1:
        - Username: `app.user`
        - Email: `app.user@gmail.com`
        - First Name: `App`
        - Last Name: `User`
        - Join Group: `app_user_group`
        - Create
        - Credentials:
            - Password: `123`
            - Temporary:  OFF
            - Set password
    - User 2:
        - Username: `app.admin`
        - Email: `app.admin@gmail.com`
        - First Name: `AppKate`
        - Last Name: `Admin`
        - Join Group: `app_admin_group`
        - Create
        - Credentials:
            - Password: `234`
            - Temporary:  OFF
            - Set password
    - User 3:
        - Username: `app.superuser`
        - Email: `app.superuser@gmail.com`
        - First Name: `AppArt`
        - Last Name: `SuperUser`
        - Join Group: `app_super_user_group`
        - Create
        - Credentials:
            - Password: `345`
            - Temporary:  OFF
            - Set password
7. Create **first** client
    - Client ID: `graphql-movie-app-client`
    - Client protocol: `openid-connect`
    - Save
8. Settings
    - Client authentication: `On`
    - Authorization: `Off`
    - Authentication flow:
        - Standard flow - `ON`
        - Direct Access Grants Enabled: `ON` (For simplicity of testing)
        - Service accounts roles: `ON`
    - Valid Redirect URIs (for simplicity - less secured):
        - `http://localhost:*` - for localhost testing
        - `http://host.testcontainers.internal:*` - for docker testing
        - `http://host.docker.internal:*` - for docker testing
9. Get Credentials
    - Credentials &rarr;
    - Client Authenticator: Client Id and Secret
    - Secret: `NRNT4zeO5yTGBhnbb8eFqpRmAd2VbNAT`

### 2. Export Realm

- Start exporting with profile `export-realm`
    - From folder ./docker-compose run
    - `docker-compose --env-file .env --profile=export-realm up -d`

### 3. Import realm

- Start import with profile `import-realm`
    - From folder ./docker-compose run
    - `docker-compose --env-file .env --profile=import-realm up -d`

### 4. Request Access Token - Password grant_type

1. Through IntelliJ IDEA HttpClient
    - use [1 GET Access Token - Password grant_type](/qraphql-oauth2/oauth-requests.http)
2. Through Postman or curl

```shell script
curl --location --request POST 'http://localhost:8181/realms/graphql-movie-app/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'username=app.user' \
--data-urlencode 'password=123' \
--data-urlencode 'client_id=graphql-movie-app-client' \
--data-urlencode 'client_secret=NRNT4zeO5yTGBhnbb8eFqpRmAd2VbNAT' \
--data-urlencode 'scope=openid profile'
```

- Response

```json
{
  "access_token": "eyJh...viFF8VGGQ",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbG...YSYyqqI",
  "token_type": "Bearer",
  "id_token": "eyJhb...ZAMasfZeTFA",
  "not-before-policy": 0,
  "session_state": "d89742d1-7b3a-4429-8255-dc6cfcf99511",
  "scope": "openid profile app_user_role email"
}
```

- View Access Token - [https://jwt.io](https://jwt.io)
    - "scope": "openid profile email",
    - "email_verified": false,
    - "name": "App User",
    - "preferred_username": "app.user",
    - "given_name": "App",
    - "family_name": "User",
    - "email": "app.user@gmail.com"

```json
{
  "exp": 1667291678,
  "iat": 1667291378,
  "jti": "a441558a-b8e5-417e-a0f4-6558745708e9",
  "iss": "http://localhost:8181/realms/graphql-movie-app",
  "aud": "account",
  "sub": "f2220189-366f-4eb9-bf9c-5771c7f015a8",
  "typ": "Bearer",
  "azp": "graphql-movie-app-client",
  "session_state": "c4cbb912-721a-4dff-9d05-b25f2bce66fa",
  "acr": "1",
  "realm_access": {
    "roles": [
      "default-roles-graphql-movie-app",
      "app_user_role",
      "offline_access",
      "uma_authorization"
    ]
  },
  "resource_access": {
    "account": {
      "roles": [
        "manage-account",
        "manage-account-links",
        "view-profile"
      ]
    }
  },
  "scope": "openid email profile",
  "sid": "c4cbb912-721a-4dff-9d05-b25f2bce66fa",
  "email_verified": false,
  "name": "App User",
  "preferred_username": "app.user",
  "given_name": "App",
  "family_name": "User",
  "email": "app.user@gmail.com"
}
```

### 5. Request Access Token - Authorization Code grant_type

- Use Browser to Get URI
    - `http://localhost:8081/realms/graphql-movie-app/protocol/openid-connect/auth?response_type=code&client_id=graphql-movie-app&scope=openid profile&state=jskd879sdkj&redirect_uri=http://localhost:8083/no_matter_callback`
- Will redirect to Keycloak login page
    - enter username and password
- Will redirect to `http://localhost:8083/no_matter_callback`
    - `http://localhost:8083/no_matter_callback?state=jskd879sdkj&session_state=44b74481-cf7d-443a-a138-9efdcd9c4d95&code=8f6f6c61-db23-4418-9db0-7069ff07e8ff.44b74481-cf7d-443a-a138-9efdcd9c4d95.c10b3c5f-fcc4-40fd-890e-dcbd7b18e6b2`
- Copy code and make POST request
    - using IntelliJ IDEA HttpClient
        - [2 Get Access token (Authorization code Grant Type)](/docker-compose/oauth-requests.http)
    - using Postman or curl

```shell script
 curl --location --request POST 'http://localhost:8181/realms/graphql-movie-app/protocol/openid-connect/token' \
 --header 'Content-Type: application/x-www-form-urlencoded' \
 --data-urlencode 'grant_type=authorization_code' \
 --data-urlencode 'client_id=graphql-movie-app' \
 --data-urlencode 'client_secret=NRNT4zeO5yTGBhnbb8eFqpRmAd2VbNAT' \
 --data-urlencode 'code=8f6f6c61-db23-4418-9db0-7069ff07e8ff.44b74481-cf7d-443a-a138-9efdcd9c4d95.c10b3c5f-fcc4-40fd-890e-dcbd7b18e6b2' \
 --data-urlencode 'redirect_uri=http://localhost:8083/no_matter_callback'
```

- Will receive response

```json
{
  "access_token": "eyJhbGciO...2XVw",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhb...HyZlatxI",
  "token_type": "Bearer",
  "id_token": "eyJhbGc...APJUiZg",
  "not-before-policy": 0,
  "session_state": "b2988770-69cf-4714-bfa4-b8aef1c56bb7",
  "scope": "openid profile app_admin_role email"
}

```



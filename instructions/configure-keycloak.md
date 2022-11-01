# Configure Keycloak Authorization Server

### 1. Configure Keycloak

1. Start Keycloak server
    - From folder `./qraphql-oauth2` run
    - `docker-compose --env-file .env --profile=import-realm up -d`
    - To stop keycloak run
    - `docker-compose --env-file .env --profile=import-realm down`
2. Log in
    - [http://localhost:8081/admin](http://localhost:8081/admin)
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

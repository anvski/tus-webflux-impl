# Tus Api Implementation
Basic Tus API  implemented using Spring Webflux (Kotlin), following the standards of the protocol.
A sample client side in Angular powered by `Uppy` along with it's `tus-js-client` integration.
The project uses PostgreSQL and provides an init migration using `Flyway` to set up the necessary tables in the `main` schema.

## Database Tables

**`main.file`**

| Column Name   | Data Type | Constraints                  |
|---------------|-----------|------------------------------|
| id            | bigserial | primary key                  |                        |
| file_size     | bigint    | default: 0                   |                        |
| mime_type     | text      | not null                     |                        |
| content_offset| bigint    | default: 0                   |                        |
| name          | text      | not null                     |                        |
| uuid          | uuid      | default: gen_random_uuid()   |                        |
| meta_data     | text      |                              |                        

**`main.file_requests`**
| Column Name  | Data Type | Constraints                 |
|--------------|-----------|-----------------------------|
| id           | bigserial | primary key                 |
| file_id      | bigint    | references file             |
| request_data | jsonb     |                             |
| user_id      | bigint    | references users            |

**`main.users`**

| Column Name | Data Type | Constraints |
|-------------|-----------|-------------|
| id          | bigserial | primary key |
| username    | text      | not null    |
| email       | text      | not null    |
| password    | text      | not null    |

## Security
The project uses JWT as an authorization mechanism and provides a spring security configuration along with a JWT provider.
The configuration provides a simple secret for the JWT encoding, in reality a high entropy secret would be used.

## Storage
The project uses the local storage for saving the content transferred under the resumable upload protocol. The incoming file chunk is saved under the following base directory: `${user.dir}/upload_directory/staging_upload` and additionally mapped depending on the mime type from the request.
Supported mime types and their directories:
 - video/mp4 -> `video`
 -  image/png -> `images`
 - image/jpeg -> `images`

## Run
Since `${user.dir}` is used, the location from which the JAR is run is important.

 1. Navigate to the `webflux` directory and execute: `gradlew clean
    build` 
2. Navigate to the parent directory or `cd ../` and execute
    `java -jar ./webflux/build/libs/ReactiveTusApi-0.0.1-SNAPSHOT.jar`

## Libraries Used
- https://uppy.io/ (Upload component along with it's tus-js-client
   integration)
- https://docs.gradle.org/8.6/userguide/userguide.html
- https://github.com/tus/tus-js-client
- https://www.npmjs.com/package/jwt-decode (Decoding JWT's on the
client side) 
- https://www.npmjs.com/package/ngx-cookie-service (Cookie
service for Angular) 
- https://v15.material.angular.io/ (UI components)
- https://documentation.red-gate.com/flyway (For database migrations)
- https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-reactor/
(Kotlin coroutines utilities for the Reactor library)
- https://spring.io/projects/spring-data-r2dbc (Reactive database
connectivity for relational DB's)
- https://github.com/pgjdbc/r2dbc-postgresql (PostgreSQL R2DBC driver)
- https://github.com/jwtk/jjwt (Java JWT support)
- https://github.com/reactor/reactor-core (Reactive Streams)
- https://github.com/FasterXML/jackson-module-kotlin

These and the rest of the dependencies can be found under `package.json` or `build.gradle.kts`

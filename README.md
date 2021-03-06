# kisalt

kisalt (means `make it short` in Turkish - `kısalt` actually) is a basic URL shortener.

### Running
kisalt using [Redis](https://redis.io/) as backend data store. So you need to start `Redis` first:

````bash
$ redis-server
````

Then either run directly with maven:
````bash
$ mvn spring-boot:run
````

Or build and run:
````bash
$ mvn clean package
$ java -jar target/kisalt-0.0.1-SNAPSHOT.jar
````

#### Note
kisalt uses [Testcontainers](https://www.testcontainers.org/) for integration tests. So Docker is needed for tests.

### Docker

If you want persistency, first create a volume and start redis with mounting it:
```bash
$ docker volume create redis
$ docker run --name redis-app -v redis:/data redis redis-server --appendonly yes
```

Then build docker image for kisalt and run it:
```bash
# Build yoursels
$ docker build -t kisalt . 
# or pull from docker hub
$ docker pull alpert/kisalt
$ docker run --name kisalt --link redis-app:redis -p 8080:8080 kisalt --spring.redis.host=redis
# You can run another as:
$ docker run --name kisalt-1 --link redis-app:redis -p 8081:8080 kisalt --spring.redis.host=redis
```

### API

kisalt uses Basic Auth and default credentials are alpert:alpert

Shorten url
```
[POST] http://localhost:8080/v1/shorten
http://url-to-be-shortened-in-body
```

Redirect to url
```
[GET] http://localhost:8080/v1/:id
Redirects to corresponding original url
```

Info about url
```
[GET] http://localhost:8080/v1/info/:id
```

After running you can view [Swagger](https://swagger.io/) api documentation by browsing to:
```
http://localhost:8080/swagger-ui.html
```

## TODO

 - Implement metric APIs
 - Add a home page
 - Custom error page
 - Better exception handling

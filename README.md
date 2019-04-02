# Scala based REST API #

## Goal :
- create a scala based REST API 
- has a nice UI (swagger)

## Example
In this example, a **scalatra** based server that creates vertexes and edges, and display those edges.

The swagger usage is a code-to-documentation implementation: the documentation is coded then automatically generated at server startup


## Build & Run ##

Launch swagger-UI
```
$ docker pull swaggerapi/swagger-ui
docker run -p 80:8080 -e SWAGGER_JSON=http://0.0.0.0:8080/api-docs/swagger.json -v /bar:/foo swaggerapi/swagger-ui 
```

```sh
$ cd flowershop
$ sbt
> jetty:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.

The swagger-UI is available at ```http://0.0.0.0:80``` or whichever port was specified when the docker image was launched

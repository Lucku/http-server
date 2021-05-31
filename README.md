# HTTP Server

A simple, dockerized HTTP server, offering GET and HEAD methods for serving static files.

## Build and run it

1. **Build the Docker image**

   Run `$ docker build -t http-server .` or `$ maven clean package -P docker`

2. **Run the server**

   In order to run the web server inside the Docker container, we need to consider 3 aspects when
   performing `docker run`:

    - We need to map a local directory representing the web server's file root to a target directory in the container.
      This target directory is by default `/usr/app/config.yml` and can be modified inside the web server
      configuration (see below)

    - We need to map the web server's port on the container to a port on the host. The server port defaults to port 8080
      and can be modified inside the web server's configuration (see below).

    - We can optionally mount a configuration YAML file into the container. This file needs to resolve to the container
      path `/usr/app/config.yml`. A valid example configuration file can be found in the root of this repository. For
      configuration parameters refer to [Configuration](#configuration).

   As a summary, our run command looks as follows:

    ``` bash
    $ docker run \
        -v /path/to/www:/usr/app/www \
        -v /path/to/config.yml:/usr/app/config.yml \
        -p 8080:8080 \ 
        http-server
    ```

3. **Make requests**

   Request resources using a browser at `localhost:8080` or by running `$ curl http://localhost:8080`
   (or using other configured port).

## Configuration

The server can be parameterized in the following way:

| Parameter name | Type   | Description                                                     | Default value |
|----------------|--------|-----------------------------------------------------------------|---------------|
| `port`         | int    | Port at which the server starts.                                | 8080          |
| `sourcePath`   | string | Local file path from where static files are served.             | "./www"       |
| `rootResource` | string | Resource that the server returns in case that "/" is requested. | "index.html"  |

In order to modify the parameters, a file called `config.yml` has to be present in `/usr/app` inside the Docker
container. An example `config.yml` can be found [here](config.yml).

## Explanation of the implementation approach

This application consists of a web server implemented using the Java NIO (non-blocking IO) API. The server is able to
handle multiple client connections and keep them persistent if not requested otherwise.

It has the capability to handle GET and HEAD requests to static resources which are served from a local, configurable
directory. Furthermore, it is able to respect HTTP conditional requests using the headers `If-Match`, `If-None-Match`
and `If-Modified-Since` while delivering resources together with calculated ETag headers.

The fundament of the application is the `HttpServer` class, which contains the main loop which is constantly accepting
new incoming connections, reads and writes to clients. The server class stores a state that is updated based on the
server being started or stopped. This state can be queried from the outside and protects against misuse leading to
unforeseen behaviour of the implementation, e.g. when accidentally starting the server's main loop twice. The states and
their transitions are:

`Idle -> Running -> Stopped`

- Idle: The server was created and is ready to be started.
- Running: The server's main loop is up and running. At this point, starting the server again will lead to an exception.
- Stopped: The server had been running and was stopped. It cannot be started again from this same object. An attempt to
  start it will also lead to an exception.

Internally, the server class makes use of a `ClientHandler`, which is a supportive class that hooks into certain moments
of the server-client lifecycle in the server's main loop:

- acceptClient: handle new client connection by creating a client stack
- handleRead: read request from client
- handleWrite: write response to client
- cleanupConnections: cleanup client stacks which are outdated (e.g. through closed client connections)

The handler can also be seen as the provisioner of the HTTP layer on top of the TCP socket connection, and therefore the
core of the application.

Since the events of a client connecting to the server and the server reading and writing to the former are decoupled in
the Java NIO approach, the `ClientHandler` needs to maintain a state for each client. In this implementation, this state
is represented by a `HttpContext`, containing a `HttpRequest` and `HttpResponse`. That way we are able to parse the HTTP
request at the moment after the client socket is ready to be read and retrieve that parsed request again at a moment
when the socket is writable in order to create an HTTP response. In case that there is an issue with the request, e.g.
when its malformed, the `400 Bad Request` response can already be created and set in the HTTP context after reading it,
and there is no additional processing of the request that needs to be done when writing the response.

When reading a client request, an `HttpRequestParser` takes care of parsing it into an `HttpRequest` object. As
explained before, this object is put into the clients own `HttpContext` together with a potential response indicating a
bad request (or any other potential response that might occur at this point, like `InternalServerError`).

At the writing-end of the `ClientHandler`, it is checked if there is a response already available for the client, and if
that's the case, we directly return that to the client. If not, we have to put in the work to find the requested
resource.

The `HttpRequestHandlerFactory` is providing us the right `HttpRequestHandler` for the relevant HTTP method, if it is
supported by the server (GET or HEAD).

![Handler classes reference diagram](docs/handler-refence-diagram.png)

The request handler is then responsible for resolving the requested file path and building an appropriate response. It
is doing so by the use of a `FileInfoRetriever`. The latter acts mainly as a "validation chain" which is constructed by
looking at the request headers and identifying `If-Match`, `If-None-Match` and `If-Modified-Since` conditions. The
`HttpRequestHandler` asks the `FileInfoRetriever` for its validation result and other necessary file metadata in order
to determine the response to be returned to the client.

Throughout the code, HTTP responses are created using the builder pattern. The `HttpResponseBuilder` allows to fluently
define response models and provides convenient methods for calculating and adding common contents like the `ETag` and
`Content-Length` headers.

All error conditions that potentially appear during the processing of HTTP requests/responses are handled by throwing
`HttpException`s. This is a family of handy exception classes which can convert themselves into `HttpResponse`s to be
returned to the clients. There is one exception for every error class, and it knows which information to be included in
the specific error response. A `BadRequestException`, e.g., will include error details in the response body while an
`InternalServerError` won't do that for security reasons.

For more information on specific classes, please consult the JavaDoc comments at the corresponding code.

*All the code is authored by me*.

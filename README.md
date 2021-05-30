# HTTP Server

A simple, dockerized HTTP server, offering GET and HEAD methods for serving static files.

# Build and run it

1. Build the Docker image

`$ docker build -t http-server .`

or

`$ maven clean package -P docker`

2. Run

// TODO: Explain

``` bash
$ docker run 
    -v /path/to/www:/usr/app/www \
    -v /path/to/config.yml:/usr/app/config.yml \
    -p 8080:8080 \ 
    http-server`
```

# Configuration

# Web Front-End

## Running Web Front-End on localhost

* Change the backend url back to the dokku
* In a terminal, run sh local-deploy.sh

## Running locally or using unit tests

* Change the backend url to "http://localhost:4567" and redirct url to the same url
* In the backend directory, run mvn clean, mvn package, and STATIC_LOCATION=`pwd`/src/main/resources/web/ mvn exec:java
* Run deploy.sh in the web directory
* To view unit test results, go to {localhost url}/spec_runner.html

## JSDocs

* [JSDocs](docs/index.html)

## Unit Test Descriptions
* A test to check if photos can be posted with a message.
* A test to check if a link can be posted with a message.
* A test to check if a link can be posted with a comment.

## Backlog for Phase 3
* Cache and interact with backend.
* base64 / file serialization (kind of have it going but doesn't work)
* Couldn't fix "unspecified error" 

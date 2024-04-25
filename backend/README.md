# Back-End Server README

This README would normally document whatever steps are necessary to get your application up and running.
Documentation/JavaDocs can be found [here](target/site/apidocs/index.html)

## What is this repository for?

* Quick summary:
  * This branch serves as the location for the backend portion of the application. It allows the application to connect with the online ElephantSQL database to store needed data and recieve it. It is also used as the base for what is pushed to the dokku-backend branch which allows the dokku app to be deployed properly.

### Generating JavaDocs

In backend folder, run:
```mvn javadoc:javadoc```

Find docs at this folder:
```backend/target/site/apidocs```

## How do I get set up?

* Summary of set up
* Configuration
* Dependencies
* Database configuration
  * To properly set up the connection to the database you will want to set up its url which contains the username and password as an ENV Varialbe named DATABASE_URL
* How to run tests
  * Running mvn package will be enough to run tests and will show up in the console log.
* Deployment instructions:
  * You can get the DATABASE_URL from Important Info
    * DATABASE_URL=postgres://< username >:< password >@isilo.db.elephantsql.com/< username >
  * Optionally, change the port from 4567 to whatever number you want by providing the proper environment configuration. For example, PORT=8998.
  * Package the Backend (Will run tests as well)
    * DATABASE_URL=??? mvn package
  * Run the Backend
    * DATABASE_URL=??? mvn exec:java
  * Just test the Backend
    * DATABASE_URL=??? mvn test

## Resources

[How to authenticate on backend](https://developers.google.com/identity/sign-in/web/backend-auth)

## Contribution guidelines

* Writing tests
  * Tests for the backend functionality are written in DatabaseTest.java.
* Code review
* Other guidelines

## Who do I talk to?

* Repo owner or admin
* Other community or team contact
  * Contact for Phase 1 Backend: Carlos cag224@lehigh.edu
  * Contact for Phase 2 Backend: Ethan esl225@lehigh.edu

## Code Notes for New Contributors

* When running curl commands use the variable names used in Requests.java. 
  * See important info for a more descript breakdown of each route.
* SQL prepared statements use the column names that are in the data base table.( Found in the Elephant SQL website )

## Testing

* **test/java/edu/lehigh/cse216/cag224/backend**
  *AppTest.java*: Testing from the database connection
  *DataRowTest.java*: Testing for the DataRow class (representing a message)
  *DatabaseTest.java*: Testing for the database sql statemenets
  *RouteTest.java*: A specialized testing class for running each of the routes and checking their output. Will be run on a temporary table with garbage data.
  *Google drive: test to add and get files from Google Drive storage
  *Memcachier: test to ensure memcachier cache for files and session info

The routes being tested are:

* Deliverable: Authentication
POST to authenticate and get the session id

* Deliverable: Messages
GET to retrieve all messages
  * Verify that file placeholder is returned correctly in posts and comments
GET to retrieve a single message  
  * Verify that complete file string is returned in both post and comments
POST for adding a message  
PUT for updating a message  
DELETE for removing a message  

* Deliverable: Liking
PUT for liking a message  
PUT for disliking a message  
  
* Deliverable: Comments
POST for adding a comment
PUT for editing a comment
DELETE for removing a comment

* Deliverable: Profiles
GET for getting another user's profile  
PUT for updating your own profile
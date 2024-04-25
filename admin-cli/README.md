# Admin CLI App

## How to Run the App
1. Navigate into the admin-cli directory
2. Run `mvn package` (some tests may fail because the app is not yet connected to the database)
3. Run `POSTGRES_IP=<> POSTGRES_PORT=5432 POSTGRES_USER=<> POSTGRES_PASS=<> mvn exec:java` with the appropriate database information.

## Current Functionality
The following commands currently work for the admin app:

- **T** create tables
- **R** remove/drop tables
- **F** fill the tables with test data
- **\*** get all messages
- **-** invalidate/delete a message
- **+** insert a new message
- **U** upvote a message
- **D** downvote a message
- **B** invalidate/ban a user
- **?** view help menu
- **q** quit app

## How to Run Tests
- Run command `POSTGRES_IP=<> POSTGRES_PORT=5432 POSTGRES_USER=<> POSTGRES_PASS=<> mvn -Dtest=AppTest test` to run all tests. Fill in the database information for the testing database.

## Current Javadocs
Click [here](src/main/java/index-all.html) for the current javadocs.

## How to Generate Javadocs: 
- Run command `javadoc edu.lehigh.cse216.mfs409.admin` in src/main/java directory
- All necessary HTML files should be in the java directory

## Phase 3 Tests to be Implemented

- ClearStorage: should remove the least recently used content from the google drive/cloud storage
- InvalidContent - selecteed content should be marked as invalid so it can no longer be shown

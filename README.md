 The Buzz README #


### Project Information ###

* CSE216-010
* Team 05 - Softserve Engineering


### Functionality ###

- The admin application can currently create the messages table, delete the messages table, insert a new message, fill the tables with test data, get all messages, upvote/downvote a message, and invalidate a user/message.
- The backend routes can currently support a POST command for new messages and comments, new profiles, and authentication, PUT commands to upvote/downvote posts by ID, update a user's profile, or edit comments, DELETE commands to delete a message by ID, GET commands to get all messages or a message by ID, or get a user's profile. It is also currently running on the dokku URL.
- The mobile app allows users to log in, and if oauth was working, post messages and upvote/downvote messages.
- The web front-end allows users to log in, create a profile, post messages or comments, edit comments, upvote/downvote messages, and view other users' profiles.


### Running the App ###

- To run backend locally you need to be in the backend directory run `mvn package`, and then `PORT=8998 mvn exec:java` (and DATABASE_URL set as a local environment variable). To access, go to localhost:8998 on the browser.
- To run on Dokku ssh into the dokku environment and run `dokku ps:start 2023sp-softserve`. When finished, run `dokku ps:stop 2023sp-softserve`. To access, go to 2023sp-softserve.dokku.cse.lehigh.edu on the browser. To be able to get the messages from the database, CORS must be enabled by running `dokku config:set 2023sp-softserve CORS_ENABLED=true`. When done, disable CORS by running  `dokku config:set 2023sp-softserve CORS_ENABLED=false`

### Developer Documentation ###

- Information on running [backend](backend/README.md)
- Information on running [admin](admin-cli/README.md)
- Information on running [web](web/README.md)
- Information on running [mobile](mobile/README.md)

- How to generate backend javadocs: [backend readme](backend/README.md)
- How to generate admin javadocs: [admin readme](admin-cli/README.md)
- [Web](web/README.md)
- [Mobile](mobile/README.md)

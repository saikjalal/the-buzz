# Mobile Branch

## Building & Testing

Launching app on emulator: ```flutter run```

If you have issues running on the emulator, try ```flutter clean```

Another source of issue could be if no backend is currently running. Switch over to local backend by setting **static bool useLocalBackend = true;** in *real_backend.dart*. Then run, from the backend folder, ```PORT=8998 DATABSE_URL=... mvn exec:java```

Running Tests (All): ```flutter test -r expanded```

Running Tests (Single): ```flutter test path/to/test.dart```

Running Tests Visually: ```flutter run -t test/path/to/ui_test.dart```
> Note: Can use hot restart to re-run test

## Building and testing on web

Launching the application on chrome: flutter run -d chrome --web-browser-flag "--disable-web-security" (this will sometimes throw an error which I currently can't figure out the fix to)

## File Structure (w/ Explanations)

> File named are bolded. Folders are italicized. Class names are in parentheses.

- *lib*
  **login.dart**: Renders login button
  **splash.dart**: Checks the status of the login while authenticating.
  - *backend*
    - **backend_singleton**: The front-facing class (BackendModel) for interacting with the backend. If you are writing code in any other folder, use this class!
    - **backend_super**: A class (Backend) that defines the structure of the backend API. If updated, it will force TrueBackend and FalseBackend to be updated accordingly. If you do so, remeber to test!
    - **mock_backend**: A backend class (FalseBackend) that will interact with our fake database
    - **real_backend**: A backend class (TrueBackend) that will interact with our real database.
  - *components*
    - **appbar**: A general AppBar to be used on each page. Pre-configured with styling
    - **future_handler**: A function to encapsulate the boilerplate code of a future builder. Use in all instances of bulk loading data
    - **icon_button**: A styled icon that will function as a button
    - **styling**: A file for all the color-themes, text-themes etc...
    - **custom_textfield**: A file to create comments that can be both editable and handle links using the Linkify widget
    - **image_picker**: A file for getting images from either the camera or gallery and encoding them to base64
  - *model*
    - **message_class**: A class to represent a Message posted by a user
    - **auth**: A class to handle user authentication
    - **comment_class**: A class to represent a Comment posted by a user
    - **user_class**: A class to represent a user
  - *views*
    - **add_message_card**: A widget (AddMessageCard) that is intended for posting a message to the database
    - **add_comment_card**: A widget (AddCommentCard) that is intended for posting a comment to the database
    - **message_board**: A page-style widget (MessageBoardPage) that will display all the messages and give interface for adding a message
    - **edit_profile**: A page-style widget (EditProfileScreen) that allows the user to edit their profile and update the db
    - **profile_screen**: A page-style widget (ProfileScreen) that allows the user to view their profile
    - **message_card**: A widget (MessageCard) for displaying a singular message, giving the option to upvote and downvote
    - **comment_card**: A widget (CommentCard) for displaying a list of comments, giving the option to edit them if posted by the current user
    - **main**: Entry point for the app. Will configure global settings.

- *test*
  - *backend*
    - **backend_test**: A set of backend tests that will ensure proper functionality with the fake/live backend.
  - *model*
    - **message_class_test**: A set of tests of the (Message) class
  - *views*
    - **add_message_card_test**: Test for adding a message via the AddMessageCard widget
    - **message_board_test**: A test for the home page, ensuring we can see all messages and scroll up to refresh
    - **message_card_test**: A test for an individual message card, ensuring we can upvote and downvote properly
    - **caching_test**: A test to ensure that after a message is fetched all the way from the db, it is then stored in the cache
    - **file_test**: A test for adding a file with a comment or message

## Documentation (run from mobile folder)

Running Documentation ```dhttpd --path doc/api```
[Click to go to Documentation](http://localhost:8080)

Alternatively, dart documentation can be found in the folder mobile/doc/api.

Generating Documentation ```dart doc .```

## New unit test descriptions for Phase 3

- A test that checks if an image can be posted with a message
- A test that checks if an image can be posted with a comment
- A test that checks if the UI updates once an image is posted to show that image
- A test that checks if content is being drawn from a local storage cache

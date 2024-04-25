package edu.lehigh.cse216.cag224.backend;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

// imports for caching
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

import java.lang.InterruptedException;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class App {
    // Creating a template for the catcher function argument
    @FunctionalInterface
    public interface BackendModel<Req, Res, Input, Output> {
        Output apply(Req request, Res res, Input auth) throws BackendException, SQLException;
    }

    private static final Gson gson = new Gson(); // json object

    private static final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
            new GsonFactory())
            .setAudience(Arrays.asList("1064276834137-ifm6s78ffncma2vkp2qfru3rvvlo71ln.apps.googleusercontent.com"))
            .build();

    private static final String DEFAULT_PORT_DB = "5432";
    private static final int DEFAULT_PORT_SPARK = 4567;

    /**
     * Get a fully-configured connection to the database, or exit immediately
     * Uses the Postgres configuration from environment variables
     * 
     * @return null on failure, otherwise configured database object
     */
    private static Database getDatabaseConnection() {
        Database.useTestTables = false;
        if (System.getenv("DATABASE_URL") != null) {
            return Database.getDatabase(System.getenv("DATABASE_URL"), DEFAULT_PORT_DB);
        }

        Map<String, String> env = System.getenv();
        String ip = env.get("POSTGRES_IP");
        String port = env.get("POSTGRES_PORT");
        String user = env.get("POSTGRES_USER");
        String pass = env.get("POSTGRES_PASS");
        return Database.getDatabase(ip, port, "", user, pass);
    }

    /**
     * Get an integer environment variable if it exists, and otherwise return the
     * default value.
     * 
     * @envar The name of the environment variable to get.
     * @defaultVal The integer value to use as the default if envar isn't found
     * 
     * @returns The best answer we could come up with for a value for envar
     */
    static int getIntFromEnv(String envar, int defaultVal) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get(envar) != null) {
            return Integer.parseInt(processBuilder.environment().get(envar));
        }
        return defaultVal;
    }

    /**
     * Enable CORS on the backend
     * 
     * @param origin  the origin to greenlight
     * @param methods the methods to greenlight
     * @param headers the headers to greenlight
     */
    private static void enableCORS(String origin, String methods, String headers) {
        // Create an OPTIONS route that reports the allowed CORS headers and methods
        Spark.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return true;
        });

        // 'before' is a decorator, which will run before any
        // get/post/put/delete. In our case, it will put three extra CORS
        // headers into the response
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }

    /**
     * Function for generating the error or ok response
     * 
     * @param result     is the resulting object to be return
     * @param comparison is the object to compare it to. If same, it will return
     *                   error
     * @param message    is the error message
     * @return a string to return from Spark.METHOD
     */
    private static String genRoute(Object result, Object comparison, String message) {
        if (result == comparison) {
            return gson.toJson(new Responses.StructuredResponse(false, message, null));
        } else {
            return gson.toJson(new Responses.StructuredResponse(true, null, result));
        }
    }

    /**
     * Function to generate an endpoint, configured with an error message (instead
     * of 500 Server Error)
     * 
     * @param action is the code to run
     * @param mc memcache (hashmap) that stores auth token
     * @return a route to pass into a Spark endpoint
     */
    private static Route catcher(BackendModel<Request, Response, Auth, String> action,  MemcachedClient mc) {
        return (Request req, Response res) -> {
            // Set status
            res.status(200);
            res.type("application/json");
            try {
                // Get the auth token
                String authToken = req.headers("Authorization");
                if (authToken == null) {
                    // If unknwon, let the user know what went wrong
                    return gson.toJson(new Responses.StructuredResponse(false,
                            "Missing authorization token. Please set the Authorization header of your web request!",
                            null));
                }

                mc.set("testToken", 0, new Auth("test@email.com", 1, "testToken")); // TODO remove test token
                if (mc.get(authToken)==null) {
                    // If the authContext doesn't contain the token, we need to login first.
                    return gson.toJson(
                            new Responses.StructuredResponse(false, "Authentication Failed. Login required.", null));
                }
                // Give the auth to the catcher input function
                Auth auth = mc.get(authToken);
                return action.apply(req, res, auth);
            } catch (BackendException e) {
                // Backend Exception are "known" errors
                return gson.toJson(new Responses.StructuredResponse(false, e.getLocalizedMessage(), null));
            } catch (SQLException e) {
                e.printStackTrace();
                return gson
                        .toJson(new Responses.StructuredResponse(false, "SQL Error:" + e.getLocalizedMessage(), null));
            } catch (Exception e) {
                // If we have an unknown error, we will return "Server Error"
                return gson.toJson(
                        new Responses.StructuredResponse(false, "Server Error:" + e.getLocalizedMessage(), null));
            }
        };
    }


    public static MemcachedClient memcacheBuilder() {
        // Memcache setup stuff
        List<InetSocketAddress> servers = AddrUtil.getAddresses(System.getenv("MEMCACHIER_SERVERS").replace(",", " "));
        AuthInfo authInfo = AuthInfo.plain(System.getenv("MEMCACHIER_USERNAME"), System.getenv("MEMCACHIER_PASSWORD"));

        MemcachedClientBuilder builder = new XMemcachedClientBuilder(servers);

        // Configure SASL auth for each server
        for(InetSocketAddress server : servers) {
            builder.addAuthInfo(server, authInfo);
        }

        // Use binary protocol
        builder.setCommandFactory(new BinaryCommandFactory());
        // Connection timeout in milliseconds (default: )
        builder.setConnectTimeout(1000);
        // Reconnect to servers (default: true)
        builder.setEnableHealSession(true);
        // Delay until reconnect attempt in milliseconds (default: 2000)
        builder.setHealSessionInterval(2000);
        MemcachedClient mc;
        try {
            mc = builder.build();
            /* just for testing - commented out
            //TODO: should i put these error checks in every time i access mc?
            try {
                mc.set("foo", 0, "bar");
                String val = mc.get("foo");
                System.out.println(val);
            } catch (TimeoutException te) {
                System.err.println("Timeout during set or get: " + te.getMessage());
            } catch (InterruptedException ie) {
                System.err.println("Interrupt during set or get: " + ie.getMessage());
            } catch (MemcachedException me) {
                System.err.println("Memcached error during get or set: " + me.getMessage());
            }
            */
            return mc;
        } catch (IOException ioe) {
            System.err.println("Couldn't create a connection to MemCachier: " + ioe.getMessage());
            System.exit(0);
        }
        // must return something to not have error, but program should exit if the memcachedClient build is unsucessful
        return null;
        // memcache setup complete
    }


    public static void main(String[] args) {
        // Connect to memcache
        MemcachedClient mc = memcacheBuilder();

        // Get the port on which to listen for requests
        Spark.port(getIntFromEnv("PORT", DEFAULT_PORT_SPARK));

        // Set up the location for serving static files. If the STATIC_LOCATION
        // environment variable is set, we will serve from it. Otherwise, serve
        // from "/web"
        String static_location_override = System.getenv("STATIC_LOCATION");
        if (static_location_override == null) {
            Spark.staticFileLocation("/web");
        } else {
            Spark.staticFiles.externalLocation(static_location_override);
        }

        if ("True".equalsIgnoreCase(System.getenv("CORS_ENABLED"))) {
            // Setting the CORS
            final String acceptCrossOriginRequestsFrom = "*";
            final String acceptedCrossOriginRoutes = "GET,PUT,POST,DELETE,OPTIONS";
            final String supportedRequestHeaders = "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin";
            enableCORS(acceptCrossOriginRequestsFrom, acceptedCrossOriginRoutes, supportedRequestHeaders);
        }

        // Set up a route for serving the main page (going to default to login now!)
        Spark.get("/", (req, res) -> {
            res.redirect("/login.html");
            return "";
        });

        // Get database connection
        Database db = getDatabaseConnection();

        // Adding the authentication route
        Spark.post("/session_authenticate", (request, response) -> {
            response.status(200);
            response.type("application/json");
            Integer userID = null;
            String sessionToken = null;
            try {
                // Get the authentication and verify it
                Requests.OAuthToken req = gson.fromJson(request.body(), Requests.OAuthToken.class);
                req.verify();

                // Try to get the id token using the oauth token
                GoogleIdToken idToken = verifier.verify(req.oauth_token);
                if (idToken != null) {
                    // Get the user information
                    Payload payload = idToken.getPayload();
                    String email = payload.getEmail();
                    String name = (String) payload.get("name");

                    // Verify the email
                    if (!email.contains("@lehigh.edu"))
                        throw new BackendException("Cannot allow emails to authenticate other than from lehigh.edu");

                    // Generate a session token and check if the user is banned
                    sessionToken = Auth.genSessionToken(email);
                    User u = db.usersTable().tryCreateUser(email, name);
                    if (u.isBanned)
                        throw new BackendException("User is banned from The Buzz");
                    userID = u.uid;
                    if (userID == null)
                        throw new BackendException("Cannot find/create user with given email");

                    
                    // Associate the sessionToken with the Auth, expiration after 30 days
                    mc.set(sessionToken, 30, new Auth(payload.getEmail(), userID, sessionToken));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return genRoute(0, 0, e.getLocalizedMessage());
            }
            // Return the session
            if (userID == null || sessionToken == null)
                return genRoute(null, null, "Cannot get session token");
            return genRoute(new Responses.Session(userID, sessionToken), null, "Cannot get session token");
        });

        // Get all messages
        Spark.get("/messages", catcher((request, response, auth) -> {
            return gson.toJson(new Responses.StructuredResponse(true, null, db.messagesTable().getAllMessages(auth.userID)));
        }, mc));

        // Get a single message at id
        Spark.get("/messages/:id", catcher((request, response, auth) -> {
            // Get the id
            int idx = Integer.parseInt(request.params("id"));
            // Get the message and return
            Message data = db.messagesTable().getOneMessage(idx, auth.userID, mc);
            return genRoute(data, null, idx + " not found");
        }, mc));

        // Add a new message
        Spark.post("/messages", catcher((request, response, auth) -> {
            // Process the expected input
            Requests.NewMessage req = gson.fromJson(request.body(), Requests.NewMessage.class);
            // Verify the input
            req.verify();
            // Insert the message and check the result
            boolean success = db.messagesTable().insertMessage(auth.userID, req.mMessage, req.mFile);
            return genRoute(success, false, "error performing insertion");
        }, mc));

        // Update a message contents
        Spark.put("/messages/:id", catcher((request, response, auth) -> {
            // Process input
            int idx = Integer.parseInt(request.params("id"));
            Requests.NewMessage req = gson.fromJson(request.body(), Requests.NewMessage.class);
            // Verify input
            req.verify();
            // Update the message and return
            boolean result = db.messagesTable().updateMessage(idx, req.mMessage);
            return genRoute(result, false, "Cannot update row");
        }, mc));

        // Delete a message
        Spark.delete("/messages/:id", catcher((request, response, auth) -> {
            // Process expected input
            int idx = Integer.parseInt(request.params("id"));
            // Delete message and return result
            boolean result = db.messagesTable().deleteMessage(idx);
            return genRoute(result, false, "Error deleting");
        }, mc));

        // Upvote a message
        Spark.put("/messages/l/:id", catcher((request, response, auth) -> {
            // Process input
            int idx = Integer.parseInt(request.params("id"));
            // Do upvote and return
            Responses.LikeStates result = db.likesTable().voteOnMessage(idx, auth.userID, 1);
            return genRoute(result, null, "Cannot update row");
        }, mc));

        // Downvote a message
        Spark.put("/messages/d/:id", catcher((request, response, auth) -> {
            // Process input
            int idx = Integer.parseInt(request.params("id"));
            // Do downvote and return
            Responses.LikeStates result = db.likesTable().voteOnMessage(idx, auth.userID, -1);
            return genRoute(result, null, "Cannot update row");
        }, mc));

        // Add a comment
        Spark.post("/comment", catcher((request, response, auth) -> {
            // Process input
            Requests.NewComment req = gson.fromJson(request.body(), Requests.NewComment.class);
            // Vefify
            req.verify();
            // Insert comment and check result
            boolean result = db.commentsTable().insertComment(auth.userID, req.messageId, req.comment, req.mFile);
            return genRoute(result, false, "Error adding a comment");
        }, mc));

        // Update a comment
        Spark.put("/comment/:id", catcher((request, response, auth) -> {
            // Process input
            int idx = Integer.parseInt(request.params("id"));
            Requests.NewComment req = gson.fromJson(request.body(), Requests.NewComment.class);
            // Verify input
            req.verify();
            // Update the comment and return
            boolean result = db.commentsTable().updateComment(auth.userID, idx, req.comment, req.mFile);
            return genRoute(result, false, "Error editing a comment");
        }, mc));

        // Delete a comment
        Spark.delete("/comment/:id", catcher((request, response, auth) -> {
            // Process input
            int idx = Integer.parseInt(request.params("id"));
            // Delete comment and return
            boolean result = db.commentsTable().deleteComment(auth.userID, idx);
            return genRoute(result, false, "Error deleting a comment");
        }, mc));

        // Getting a profile
        Spark.get("/profile/:id", catcher((request, response, auth) -> {
            // Process input
            int idx = Integer.parseInt(request.params("id"));
            // Get the user and return
            User user = db.usersTable().getUser(auth.userID, idx);
            return genRoute(user, null, "Error getting a user");
        }, mc));

        // Updating a profile
        Spark.put("/profile", catcher((request, response, auth) -> {
            // Process input
            Requests.FullUserProfile req = gson.fromJson(request.body(), Requests.FullUserProfile.class);
            // Verify input
            req.verify();
            // Create the user to update
            User user = new User(auth.userID, req.name, auth.email, req.gender_identity, req.sexualOrientation, req.bio,
                    false);
            // Update the user and return
            boolean result = db.usersTable().updateUser(auth.userID, user);
            return genRoute(result, false, "Error updating a user");
        }, mc));
    }
}

import '../model/message_class.dart';
import '../model/user_class.dart';
import '../model/comment_class.dart';

/// Mini class for binding the userID to sessionToken together
class Session {
  int userID;
  String sessionToken;
  Session(this.userID, this.sessionToken);
}

/// A super class to define the functionality for the fake (testing) backend and the live backend
/// Will enforce a singleton pattern for ease of use throughout the app
abstract class Backend {
  /// User ID, null when unknown
  int? userID;

  /// Session token, null when unknown
  String? sessionToken;

  /// Session authenticate to get session token and userID
  Future<Session?> sessionAuthenticate(String authToken);

  /// Get all messages from the database
  Future<List<Message>> getAllMessages();

  /// Get single message from the database based on message `id`
  /// - might return null if we have a non-existent id
  Future<Message?> getMessageById(int id);

  /// Post a message with content `m` and base64 file `f`
  /// - Will return Message or null if failure
  Future<bool?> postMessage(String m, String f);

  /// Update message at the `id` with data from message `m`
  /// - Will return success status
  Future<bool> updateMessage(Message m, int id);

  /// Delete message at `id`
  /// - Will return success status
  Future<bool> deleteMessage(int id);

  /// Upvote message at `id`
  /// - Will return success status
  Future<bool> upvoteMessage(int id);

  /// Downvote message at `id`
  /// - Will return success status
  Future<bool> downvoteMessage(int id);

  /// Post a comment with content 'c' onto message at 'id' with file `f`
  /// - Will return success status
  Future<bool> postComment(String c, int id, String f);

  /// Update a comment on message at 'commentID' to content 'c'
  /// - Will return success status
  Future<bool> updateComment(int commentID, String c);

  /// Remove a comment at 'cID' on message at 'mID'
  /// - Will return success status
  Future<bool> removeComment(int cID);

  /// Get another user's profile info at user 'id'
  /// - Will return User or null if failure
  Future<User?> getProfile(int id);

  /// Update the current user's profile info to 'user'
  /// - Will return success status
  Future<bool> updateProfile(User user);
}

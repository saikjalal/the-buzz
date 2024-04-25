import 'real_backend.dart';
import 'backend_super.dart';
import '../model/comment_class.dart';
import '../model/message_class.dart';
import '../model/user_class.dart';

String backendUrl = "";

/// A fake backend for testing the app
///
/// **Note: Do not use explicitly. Use BackendModel for access!**
class FalseBackend extends Backend {
  /// Instance of the fake backend
  static FalseBackend? _instance;

  /// Constructor for the singleton (private)
  FalseBackend._();

  /// Will get the current instance of the fake-backend singleton
  static FalseBackend get instance {
    // Sets instance only if null. If _instance is not null, will do nothing
    _instance ??= FalseBackend._();
    // Return the instance
    return _instance!;
  }

  /// List of messages to interact with
  final List<Message> _messages = [];

  //* !!!!! False backend should be used for testing !!!!! *//

// Get all messages from the database
  @override
  Future<List<Message>> getAllMessages() async {
    // Sort messages before returning
    _messages.sort();
    return _messages;
  }

  // Get single message from the database
  @override
  Future<Message?> getMessageById(int id) async {
    // Look for messages where ID matches element
    Iterable<Message> results = _messages.where((element) => element.id == id);
    // If results is empty, return null to indicate a non-existent ID
    if (results.isEmpty) return null;
    // Return the 'first' matching id. If we did our IDs correctly, results will always have only one.
    return results.first;
  }

  // Post a message with content m and file f
  @override
  Future<bool?> postMessage(String m, String f) async {
    // Otherwise add the message and return true
    // Will generate a 'random' id based on the message and the date of post
    Message message =
        Message(m, 0, m.hashCode + DateTime.now().hashCode, f, DateTime.now(), [], 0, 0);
    _messages.add(message);
    return true;
  }

  // Update message
  @override
  Future<bool> updateMessage(Message m, int id) async {
    // If message id is not equal to the id to change, we return false
    if (m.id != id) return false;
    // If the message doesn't exist, return false
    int index = _messages.indexWhere((element) => element.id == id);
    if (index == -1) return false;
    // Otherwise set the message at the index we provide earlier to be the new message and return true
    _messages[index] = m;
    return true;
  }

  // Delete message
  @override
  Future<bool> deleteMessage(int id) async {
    // Get initial length of the message list
    int initialLength = _messages.length;
    // Remove all messages where id is a match
    _messages.removeWhere((element) => element.id == id);
    // Compare initial length with new length to determine if it was removed
    return initialLength == _messages.length + 1;
  }

  // Upvote message
  @override
  Future<bool> upvoteMessage(int id) async {
    // Get index of message with id
    int index = _messages.indexWhere((element) => element.id == id);
    // If it doesn't exist, return false
    if (index == -1) return false;
    // If it does exist, increment the likes and return true
    _messages[index].likes += 1;
    return true;
  }

  // Downvote message
  @override
  Future<bool> downvoteMessage(int id) async {
    // Get index of the message with id
    int index = _messages.indexWhere((element) => element.id == id);
    // If it doesn't exist, return false
    if (index == -1) return false;
    // If the likes are already 0, return false
    if (_messages[index].likes == 0) return false;
    // If it does exist, decrement the likes and return true
    _messages[index].likes -= 1;
    return true;
  }

  @override
  Future<User?> getProfile(int id) {
    // TODO: implement getProfile
    throw UnimplementedError();
  }

  @override
  Future<bool> postComment(String c, int id, String f) {
    // TODO: implement postComment
    throw UnimplementedError();
  }

  @override
  Future<bool> removeComment(int cID) {
    // TODO: implement removeComment
    throw UnimplementedError();
  }

  @override
  Future<Session?> sessionAuthenticate(String authToken) {
    // TODO: implement sessionAuthenticate
    throw UnimplementedError();
  }

  @override
  Future<bool> updateComment(int id, String c) {
    // TODO: implement updateComment
    throw UnimplementedError();
  }

  @override
  Future<bool> updateProfile(User user) {
    // TODO: implement updateProfile
    throw UnimplementedError();
  }
}

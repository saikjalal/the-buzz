// ignore_for_file: overridden_fields

import 'dart:convert';
import 'package:clean_start_buzz/components/shared_prefs.dart';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart'; // https://docs.flutter.dev/cookbook/networking/fetch-data
import 'backend_singleton.dart';
import '../model/message_class.dart';
import '../model/user_class.dart';
import 'backend_super.dart';

enum _Method { get, post, put, delete }

/// Singleton Class to interact with the live backend.
/// It extends Backend as a super-class for all backend-like APIs
///
/// **Note: Do not use explicitly. Use BackendModel for access!**
class TrueBackend extends Backend {
  /// Static instance of the backend
  static TrueBackend? _instance;

  static late String backendUrl;

  static bool useLocalBackend = true;

  /// User ID, null when unknown
  @override
  int? userID = 1;

  /// Session token, null when unknown
  @override
  String? sessionToken;

  /// Constructor for the singleton (private)
  TrueBackend._();

  // Will get the current instance of the singleton
  static TrueBackend get instance {
    if (kDebugMode && useLocalBackend) {
      // can add && false to override this condition
      // If the app is in debug mode, we use the localhost
      backendUrl = "http://10.0.2.2:4567";
    } else {
      // If the app is in production, we use the dokku backend
      backendUrl = "http://2023sp-softserve.dokku.cse.lehigh.edu";
    }

    // Will assign the instance if not null, otherwise this statement does nothing (and will use the pre-existing one).
    _instance ??= TrueBackend._();
    // Return instance of singleton
    return _instance!;
  }

  Future<Map<String, dynamic>?> _queryRoute(String route, _Method method,
      {Map<String, dynamic>? body}) async {
    // Query the backend
    Response res;
    switch (method) {
      // Under each case, must pass an authorization token.
      case _Method.get:
        res = await get(Uri.parse("$backendUrl$route"), headers: {
          'Authorization': BackendModel.instance.sessionToken ?? ""
        });
        break;
      case _Method.post:
        res = await post(Uri.parse("$backendUrl$route"),
            headers: {
              'Authorization': BackendModel.instance.sessionToken ?? ""
            },
            body: body?.toString());
        break;
      case _Method.delete:
        res = await delete(Uri.parse("$backendUrl$route"),
            headers: {
              'Authorization': BackendModel.instance.sessionToken ?? ""
            },
            body: body?.toString());
        break;
      case _Method.put:
        res = await put(Uri.parse("$backendUrl$route"),
            headers: {
              'Authorization': BackendModel.instance.sessionToken ?? ""
            },
            body: body?.toString());
        break;
      default:
        return null;
    }

    // Try to decode the json
    Map<String, dynamic> resDecoded;
    try {
      resDecoded = jsonDecode(res.body);
    } on FormatException {
      return null;
    }
    if (resDecoded['mStatus'] != 'ok') return null;
    return resDecoded;
  }

  /// Session authenticate to get session token and userID
  @override
  Future<Session?> sessionAuthenticate(String authToken) async {
    Map<String, dynamic>? resDecoded = await _queryRoute(
        "/session_authenticate", _Method.get,
        body: {'oauth_token': authToken});
    if (resDecoded == null) return null;
    // Get the data
    Map<String, dynamic>? m = resDecoded['mData'];
    if (m == null) return null;
    if (!m.containsKey("sessionToken") || !m.containsKey("userID")) return null;
    return Session(m['userID'], m['sessionToken']);
  }

  // Get all messages from the database
  @override
  Future<List<Message>> getAllMessages() async {
    // Query to get the json
    Map<String, dynamic>? resDecoded =
        await _queryRoute("/messages", _Method.get);
    if (resDecoded == null) return [];

    // Get the list of data
    List? l = resDecoded['mData'];
    // if the data structure didn't have the mData field, return an empty string
    if (l == null) return [];

    // Add it to the list
    List<Message> messages = [];
    for (var element in l) {
      messages.add(Message.fromJson(element));
    }
    // Sort the list & return
    messages.sort();
    return messages;
  }

  // Get single message from the database
  @override
  Future<Message?> getMessageById(int id) async {
    dynamic fileFound = await checkForMessageinSF(id);
    if (fileFound == false) {
      // Query to get the json
      Map<String, dynamic>? resDecoded =
          await _queryRoute("/messages/$id", _Method.get);
      if (resDecoded == null) return null;
      Message m = Message.fromJson(resDecoded['mData']);

      await addMessageToSF(m);

      return m;
    } else {
      return fileFound as Message;
      // String foundJSON = fileFound;
      // Map<String, dynamic> j = jsonDecode(foundJSON) as Map<String, dynamic>;
      // print(j);
      // return Message(j['message'], j['likes'], j['id'], j['file'],
      //     DateTime.now(), j['like'], j['like'], j['like']);
    }
  }

  // Post a message with content m and file f
  @override
  Future<bool?> postMessage(String m, String f) async {
    // Query to get the json
    Map<String, dynamic>? resDecoded = await _queryRoute(
        "/messages", _Method.post,
        body: {'mMessage': "'$m'", 'mFile': "'$f'"});
    if (resDecoded == null) return null;
    print(resDecoded);
    return resDecoded['mData'];
  }

  // Update message
  @override
  Future<bool> updateMessage(Message m, int id) async {
    throw UnimplementedError("Unused in the mobile implementation");
  }

  // Delete message
  @override
  Future<bool> deleteMessage(int id) async {
    // Query to get the json
    Map<String, dynamic>? resDecoded =
        await _queryRoute("/messages/$id", _Method.delete);
    if (resDecoded == null) return false;
    return true;
  }

  // Upvote message
  @override
  Future<bool> upvoteMessage(int id) async {
    // Query to get the json
    Map<String, dynamic>? resDecoded =
        await _queryRoute("/messages/l/$id", _Method.put);
    if (resDecoded == null) return false;
    return true;
  }

  // Downvote message
  @override
  Future<bool> downvoteMessage(int id) async {
    // Query to get the json
    Map<String, dynamic>? resDecoded =
        await _queryRoute("/messages/d/$id", _Method.put);
    if (resDecoded == null) return false;
    return true;
  }

  // Get user profile info
  @override
  Future<User?> getProfile(int id) async {
    // Query to get the json
    Map<String, dynamic>? resDecoded =
        await _queryRoute("/profile/$id", _Method.get);
    if (resDecoded == null) return null;
    return User.fromJson(resDecoded['mData']);
  }

  @override
  Future<bool> postComment(String c, int id, String f) async {
    // Query to get the json
    Map<String, dynamic>? resDecoded = await _queryRoute(
        "/comment", _Method.post,
        body: {'comment': "'$c'", 'messageId': "$id", 'mFile': "'$f'"});
    if (resDecoded == null) return false;
    return true;
  }

  @override
  Future<bool> removeComment(int cID) async {
    // Query to get the json
    Map<String, dynamic>? resDecoded = await _queryRoute(
      "/comment/$cID",
      _Method.delete,
    );
    if (resDecoded == null) return false;
    return true;
  }

  @override
  Future<bool> updateComment(int commentID, String c) async {
    // Query to get the json
    Map<String, dynamic>? resDecoded = await _queryRoute(
        "/comment/$commentID", _Method.put,
        body: {'comment': "'$c'", 'messageId': -1});
    if (resDecoded == null) return false;
    return true;
  }

  @override
  Future<bool> updateProfile(User user) async {
    // Query to get the json
    Map<String, dynamic>? resDecoded =
        await _queryRoute("/profile", _Method.put, body: user.toJson());
    if (resDecoded == null) return false;
    return true;
  }
}

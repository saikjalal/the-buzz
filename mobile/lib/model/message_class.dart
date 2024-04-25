import 'dart:convert';

import 'comment_class.dart';

/// Message class represents a user's post
/// As we build up our database, we should update this class defintion
class Message implements Comparable {
  /// The message the user posted
  late String message;

  /// The number of likes the message has
  late int likes;

  /// Unique id to identify the message
  /// Final to avoid modification
  late final int id;

  /// Creation date for sorting
  late final DateTime creationDate;

  /// Optional file attached to the message
  /// Should be in base64 format
  String file = "";

  /// User ID of the user who posted the message
  late int userID;

  /// Like status of the current user on this message
  /// -1 if downvoted, 0 if neutral, 1 if upvoted
  late int likeStatus;

  /// List of comments attached to the message
  late List<Comment> comments;

  /// Basic constructor for the message
  /// - `message` | the user's message [String]
  /// - `likes` | the number of likes [int]
  /// - `id` | the id of the message [int]
  /// - `creationDate` | the date of the message's creation [DateTime]
  Message(this.message, this.likes, this.id, this.file, this.creationDate,
      this.comments, this.likeStatus, this.userID);

  /// Constructor of message from a json
  /// - `json` is the data receieved from the database [Map]
  Message.fromJson(Map<String, dynamic> json) {
    // Set data fields
    message = json['mContent'] ?? "";
    likes = json['mLikes'] ?? -1;
    id = json['mId'] ?? -1;
    // TODO: REMOVE THE DEFAULT CREATION DATE
    creationDate =
        DateTime.parse(json['creationDate'] ?? DateTime.now().toString());
    likeStatus = json['myLikeStatus'] ?? 0;
    userID = json['userID'] ?? 0;
    List<dynamic> commentJson = json['mComments'] ?? [];
    comments = commentJson
        .map((dynamic e) => Comment.fromJson(e as Map<String, dynamic>))
        .toList();
    file = json['mFile'] ?? "";
  }

  /// Function that returns String representation of message
  ///
  /// *Just returns id right now. Can change as desired*
  @override
  String toString() {
    return id.toString();
  }

  /// A function needed by Comparable interface.
  ///
  /// Will compare messages by creation date. Sorts by oldest -> newest...
  @override
  int compareTo(dynamic other) {
    if (other is Message) {
      return id.compareTo(other.id);
    } else {
      return 0;
    }
  }

  /// Function for validating messages based on length (1-1023 characters only)
  static bool validateMessage(String message) {
    // Conditional for valid message
    if (message.isNotEmpty && message.length <= 1024) return true;
    // Otherwise false
    return false;
  }
}

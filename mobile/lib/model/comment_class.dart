/// Comment class represents a comment on a messagewith an optional file
class Comment {
  /// The id of the comment. Null if the comment was added
  late final int? commentID;

  /// The contents of the comment
  late String comment;

  /// The optional file attached to a comment
  /// Should be the file stored in base64
  late String file;

  /// The message id that the comment is attached to
  late int messageID;

  /// The user id of whoever commented
  late int userID;

  Comment(this.comment, this.messageID, this.userID, this.file, this.commentID);

  Comment.fromJson(Map<String, dynamic> json) {
    // Process the json into an instance
    commentID = json['commentID'];
    comment = json['comment'] ?? "";
    file = json['mFile'] ?? "";
    messageID = json['messageID'] ?? -1;
    userID = json['userID'] ?? -1;
  }
}

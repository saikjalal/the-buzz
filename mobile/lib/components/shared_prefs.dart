import 'package:shared_preferences/shared_preferences.dart';

import '../model/comment_class.dart';
import '../model/message_class.dart';

/// Add a file into the local cache
/// isMessage = true if file is attached to a message, false if file is attached to a comment
/// id = message id or comment id
addFileToSF(bool isMessage, int id, String content) async {
  SharedPreferences prefs = await SharedPreferences.getInstance();
  // name of variable is m for message or c for comment + id
  if (isMessage) {
    prefs.setString('file_m$id', content);
  } else {
    prefs.setString('file_c$id', content);
  }
}

/// Check if the file for a message or comment is in the local cache
/// isMessage = true if file is attached to a message, false if file is attached to a comment
/// id = message id or comment id
/// returns false if not found, returns file string if found
Future<dynamic> checkForFileinSF(bool isMessage, int id) async {
  SharedPreferences prefs = await SharedPreferences.getInstance();
  String checkFor = "";
  if (isMessage) {
    checkFor = 'file_m$id';
  } else {
    checkFor = 'file_c$id';
  }

  bool found = prefs.containsKey(checkFor);
  if (!found) return false;

  return prefs.getString(checkFor);
}

clearPrefs() async {
  SharedPreferences prefs = await SharedPreferences.getInstance();
  prefs.clear();
}

addMessageToSF(Message m) async {
  SharedPreferences prefs = await SharedPreferences.getInstance();
  // name of variable is m for message or c for comment + id
  prefs.setInt("${m.id}mid", m.id);
  prefs.setString("${m.id}message", m.message);
  prefs.setInt("${m.id}muid", m.userID);
  prefs.setString("${m.id}file", m.file.toString());
  prefs.setInt("${m.id}likeStatus", m.likeStatus);
  prefs.setInt("${m.id}likes", m.likes);
  prefs.setStringList("${m.id}commentIDs",
      m.comments.map((e) => e.commentID.toString()).toList());
  for (Comment c in m.comments) {
    prefs.setInt("${m.id}${c.commentID}cid", c.commentID ?? -1);
    prefs.setString("${m.id}${c.commentID}ccomment", c.comment);
    prefs.setInt("${m.id}${c.commentID}cuserID", c.userID);
    prefs.setString("${m.id}${c.commentID}cfile", c.file);
  }
}

Future<dynamic> checkForMessageinSF(int id) async {
  SharedPreferences prefs = await SharedPreferences.getInstance();

  bool found = prefs.containsKey("${id}mid");
  if (!found) return false;
  List<Comment> comments = [];
  List<String> tokens = prefs.getStringList("${id}commentIDs")!;
  for (String t in tokens) {
    comments.add(Comment(
      prefs.getString("$id${t}ccomment")!,
      id,
      prefs.getInt(
        "$id${t}cuserID ",
      )!,
      prefs.getString(
        "$id${t}cfile",
      )!,
      prefs.getInt(
        "$id${t}cid",
      ),
    ));
  }

  Message m = Message(
      prefs.getString("${id}message")!,
      prefs.getInt("${id}likes")!,
      id,
      prefs.getString("${id}file")!,
      DateTime.now(),
      comments,
      prefs.getInt("${id}likeStatus")!,
      prefs.getInt("${id}muid")!);

  return m;
}

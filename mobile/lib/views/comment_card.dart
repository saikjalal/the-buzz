import 'dart:convert';

import 'package:clean_start_buzz/model/message_class.dart';
import 'package:flutter/material.dart';
import 'package:flutter_linkify/flutter_linkify.dart';
import 'package:url_launcher/url_launcher_string.dart';
import '../backend/backend_singleton.dart';
import '../components/custom_textfield.dart';
import '../components/styling.dart';
import '../model/comment_class.dart';
import 'add_comment_card.dart';
import '../components/alerts.dart';

Future<List<Widget>> commentList(int messageID, BuildContext context) async {
  Message? m = await BackendModel.instance.getMessageById(messageID);

  if (m == null) {
    return [const SizedBox(width: 0)];
  }
  List<Comment> comments = m.comments;

  return comments.map((Comment e) {
    return Padding(
        padding: const EdgeInsets.only(bottom: 4, top: 4, left: 16),
        child: Row(mainAxisAlignment: MainAxisAlignment.end, children: [
          e.userID == BackendModel.instance.userID
              ? const Icon(Icons.edit)
              : const SizedBox(),
          Flexible(
              child: CustomTextField(
                  onEdit: (String newText) {
                    if (e.commentID == null) {
                      showBottomAlert(
                          "Cannot edit a comment until it is fetched (on refresh).",
                          3,
                          22,
                          context);
                    } else {
                      BackendModel.instance
                          .updateComment(e.commentID!, newText);
                    }
                  },
                  decoration: const InputDecoration(
                      disabledBorder: InputBorder.none,
                      border: InputBorder.none),
                  enabled: e.userID == BackendModel.instance.userID,
                  style: const TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.w400,
                  ),
                  text: e.comment,
                  textBuilder: (String value) {
                    return Linkify(
                      onOpen: (link) async {
                        if (await canLaunchUrlString(link.url)) {
                          await launchUrlString(link.url);
                        } else {
                          throw 'Could not launch ${link.url}';
                        }
                      },
                      text: value,
                      style: TextThemes.normalText(),
                    );
                  })),
          //if there is a file, display it
          (e.file != "")
              ? Flexible(
                  child:
                      Image.memory(base64Decode(e.file), fit: BoxFit.scaleDown))
              : const SizedBox(width: 8)
        ]));
  }).toList();
}

List<Widget> commentPostBtn(int messageID, void Function(String) callback) {
  return [AddCommentCard(callback, messageID)];
}

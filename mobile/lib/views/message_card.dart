import 'package:clean_start_buzz/components/shared_prefs.dart';
import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher_string.dart';
import '../backend/backend_singleton.dart';
import '../components/future_handler.dart';
import '../model/comment_class.dart';
import '../model/message_class.dart';
import '../components/icon_button.dart';
import '../components/styling.dart';
import 'profile_screen.dart';
import 'comment_card.dart';
import 'package:flutter_linkify/flutter_linkify.dart';
import 'dart:convert';

/// A Widget to display a singular message
/// - `message` is the Message instance to display
class MessageCard extends StatefulWidget {
  /// The message that was passed
  final Message message;
  final void Function(int) callback;

  /// A Widget to display a singular message
  /// - `message` is the Message instance to display
  const MessageCard(this.message, this.callback, {super.key});

  @override
  State<MessageCard> createState() => _MessageCardState();
}

class _MessageCardState extends State<MessageCard> {
  // Will keep track if the comment section is visible users. Users should have the option to toggle the comments.
  bool showComments = false;
  // Indicates if the edit box for posting a comments is visible to users
  bool showcommentPostBtn = false;

  @override
  Widget build(BuildContext context) {
    return Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
              Padding(
                  padding:
                      const EdgeInsets.symmetric(vertical: 12, horizontal: 12),
                  child:
                      Row(mainAxisAlignment: MainAxisAlignment.end, children: [
                    SplashIconButton(
                      onPressed: () {
                        Navigator.of(context).push(MaterialPageRoute(
                            builder: (context) =>
                                ProfileScreen(widget.message.userID)));
                      },
                      icon: Icons.account_circle,
                    ),
                    Text(widget.message.userID.toString()),
                    const SizedBox(width: 8),
                    Expanded(
                        child: Linkify(
                      onOpen: (link) async {
                        if (await canLaunchUrlString(link.url)) {
                          await launchUrlString(link.url);
                        } else {
                          throw 'Could not launch ${link.url}';
                        }
                      },
                      text: widget.message.message,
                      style: TextThemes.normalText(),
                    )), // Message text
                    //if message has image, display it. if not, show nothing
                    //if not found in local cache, make db call. else, get from local cache
                    waitAndQuery<dynamic>(
                        future: checkForFileinSF(true, widget.message.id),
                        child: (dynamic fileFound) {
                          if (fileFound == false || fileFound == null) {
                            return waitAndQuery<Message?>(
                                future: BackendModel.instance
                                    .getMessageById(widget.message.id),
                                child: (Message? m) {
                                  if (m == null) {
                                    return const SizedBox(width: 0);
                                  }

                                  String file = m.file;

                                  if (file == "") {
                                    return const SizedBox(width: 0);
                                  }
                                  //after fetching, add img to local cache to not fetch again
                                  addFileToSF(true, widget.message.id, file);
                                  Image img = Image.memory(base64Decode(file),
                                      fit: BoxFit.scaleDown);
                                  return Flexible(child: img);
                                });
                          } else {
                            Image img = Image.memory(base64Decode(fileFound),
                                fit: BoxFit.scaleDown);
                            return Flexible(child: img);
                          }
                        }),
                    Text(
                      widget.message.likes.toString(),
                      style: TextThemes.emphasisText(),
                    ), // show number of likes
                    const SizedBox(width: 8),
                    SplashIconButton(
                        onPressed: () async {
                          // Upvote and update UI
                          await BackendModel.instance
                              .upvoteMessage(widget.message.id);
                          if (widget.message.likeStatus == 1) {
                            widget.message.likes -= 1;
                            widget.message.likeStatus = 0;
                          } else if (widget.message.likeStatus == 0) {
                            widget.message.likes += 1;
                            widget.message.likeStatus = 1;
                          } else {
                            widget.message.likes += 2;
                            widget.message.likeStatus = 1;
                          }
                          // change it programmatically to actually display. Otherwise you must refresh to display data base changes
                          setState(() {});
                        },
                        icon: widget.message.likeStatus == 1
                            ? Icons.thumb_up
                            : Icons.thumb_up_outlined), // upvote button
                    const SizedBox(width: 4),
                    SplashIconButton(
                      onPressed: () async {
                        // Downvote and update UI
                        await BackendModel.instance
                            .downvoteMessage(widget.message.id);
                        if (widget.message.likeStatus == -1) {
                          widget.message.likes += 1;
                          widget.message.likeStatus = 0;
                        } else if (widget.message.likeStatus == 0) {
                          widget.message.likes -= 1;
                          widget.message.likeStatus = -1;
                        } else {
                          widget.message.likes -= 2;
                          widget.message.likeStatus = -1;
                        }
                        // change it programmatically to actually display. Otherwise you must refresh to display data base changes
                        setState(() {});
                      },
                      icon: widget.message.likeStatus == -1
                          ? Icons.thumb_down
                          : Icons.thumb_down_outlined,
                    ), // downvote button
                    const SizedBox(width: 4),
                    SplashIconButton(
                      onPressed: () =>
                          setState(() => showComments = !showComments),
                      icon: Icons.comment,
                    ),
                    const SizedBox(width: 4),
                    SplashIconButton(
                      onPressed: () => setState(
                          () => showcommentPostBtn = !showcommentPostBtn),
                      icon: Icons.reply,
                    )
                  ])),
            ] +
            (showComments
                ? []
                : [
                    waitAndQuery<List<Widget>>(
                        future: commentList(widget.message.id, context),
                        child: (List<Widget> ws) {
                          return Column(children: ws);
                        })
                  ]) +
            (showcommentPostBtn
                ? []
                : commentPostBtn(
                    widget.message.id,
                    (String input) => setState(() {
                          widget.message.comments.add(Comment(
                              input,
                              widget.message.id,
                              BackendModel.instance.userID ?? -1,
                              "",
                              -1));
                        }))));
  }
}

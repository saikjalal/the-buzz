import 'package:clean_start_buzz/backend/backend_singleton.dart';
import 'package:clean_start_buzz/model/comment_class.dart';
import 'package:clean_start_buzz/model/message_class.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  test('Test to post files with messages and comments', () async {
    // Set us to test data
    BackendModel.useRealBackend = false;
    //post message with encoded file data
    await BackendModel.instance
        .postMessage("test message", "file data with message ENCODED");

    //check for message 1 (assuming db is clean at start of test)
    Message? m = await BackendModel.instance.getMessageById(1);

    String? file = m?.file;
    expect(file == "file data with message ENCODED", true);

    //repeat process with comment on message 1
    await BackendModel.instance
        .postComment("test comment", 1, "file data with comment ENCODED");

    //fetch message to access comment 1 (should be comment 1 since there are no other comments)
    Message? mWithComments = await BackendModel.instance.getMessageById(1);
    List<Comment>? comments = mWithComments?.comments;

    if (comments == null) return;

    //first comment should have file data
    expect(comments[0].file == "file data with comment ENCODED", true);
  });
}

import 'package:mobile/backend/backend_singleton.dart';
import 'package:mobile/backend/backend_super.dart';
import 'package:mobile/model/message_class.dart';

import 'package:test/test.dart';

void main() {
  test('Test backend routes', () async {
    // Init the false backend
    BackendModel.useRealBackend = false;
    Backend fb = BackendModel.instance;
    // Check if the singleton returned the same class
    expect(fb, BackendModel.instance);

    // Create a message
    String message = "Message 1";
    Message? m = await fb.postMessage(message);
    expect(m != null, true);

    // Check it created one message
    List<Message> mess = await fb.getAllMessages();
    expect(mess.length, 1);

    // Add a second message and check the lenghh
    expect(await fb.postMessage("Message 2") != null, true);
    mess = await fb.getAllMessages();
    expect(mess.length, 2);

    // Check we can query the correct message
    Message? initMessage = await fb.getMessageById(m!.id);
    expect(initMessage, m);

    // Check we can update the message
    String updateString = "Message Modified 2";
    expect(await fb.updateMessage(Message(updateString, m.likes, m.id, DateTime.now()), m.id), true);

    // Expect the message to be changed
    Message? updatedMessage = await fb.getMessageById(m.id);
    expect(updatedMessage!.message, updateString);

    // Expect us to unable be able to update a message with the wrong id
    expect(await fb.updateMessage(Message(updateString, 0, 1, DateTime.now()), -1), false);

    // Expect us to be able to delete a message
    expect(await fb.deleteMessage(updatedMessage.id), true);
    expect(await fb.getMessageById(updatedMessage.id), null);

    // Expect us to be unable to delete a non-existent message
    expect(await fb.deleteMessage(-100), false);

    // ### Expect us to be able to upvote and downvote a message ###
    // Checking initial condition
    Message? m2 = await fb.postMessage("Testing Likes");
    expect(m2 != null, true);

    // Check likes are 0
    Message? messageLikes = await fb.getMessageById(m2!.id);
    expect(messageLikes!.likes, 0);

    // Upvote works
    await fb.upvoteMessage(m2.id);
    await fb.upvoteMessage(m2.id);
    messageLikes = await fb.getMessageById(m2.id);
    expect(messageLikes!.likes, 2);

    // Downvote works
    await fb.downvoteMessage(m2.id);
    messageLikes = await fb.getMessageById(m2.id);
    expect(messageLikes!.likes, 1);

    // Downvotes can't go past 0
    await fb.downvoteMessage(m2.id);
    await fb.downvoteMessage(m2.id);
    await fb.downvoteMessage(m2.id);
    await fb.downvoteMessage(m2.id);
    messageLikes = await fb.getMessageById(m2.id);
    expect(messageLikes!.likes, 0);

    // Expect us to be unable to upvote or downvote a nonexistent message
    expect(await fb.downvoteMessage(-100), false);
    expect(await fb.upvoteMessage(-100), false);
  });
}

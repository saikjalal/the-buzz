import 'dart:io';

import 'package:mobile/backend/backend_singleton.dart';
import 'package:mobile/model/message_class.dart';

import 'package:test/test.dart';

void main() {
  test('Test message class', () async {
    BackendModel.useRealBackend = false;

    // Test message constructors
    Message message = Message("Message", 2, 1, DateTime.now());
    expect(message.message, "Message");
    expect(message.likes, 2);
    expect(message.id, 1);

    // Test message constructors
    message = Message.fromJson({'message': "Message", 'likes': 2, 'id': 1, 'creationDate': DateTime.now().toIso8601String()});
    expect(message.message, "Message");
    expect(message.likes, 2);
    expect(message.id, 1);

    message = await BackendModel.instance.postMessage("Message Test") ?? Message("", 0, 0, DateTime.now());
    expect(message.id != 0, true); // expect us not to end with null
    expect(message.message, "Message Test");
    expect(message.likes, 0);

    // Testing if to string produces id
    expect(message.toString(), message.id.toString());

    // Check compare to
    sleep(const Duration(seconds: 2));
    Message mLater = await BackendModel.instance.postMessage("Message Test") ?? Message("", 0, 0, DateTime.now());
    expect(message.id != 0, true); // expect us not to end with null
    expect(message.compareTo(mLater) < 0, true);
    expect(message.compareTo(message) == 0, true);

    // Check message validation
    expect(Message.validateMessage(""), false);
    expect(Message.validateMessage("hello"), true);
    expect(Message.validateMessage("YOOOOOOOOO"), true);

    // Test edge case for message validation
    StringBuffer builder = StringBuffer("");
    for (int i = 0; i < 1024; i++) {
      builder.write("a");
    }
    expect(builder.toString().length, 1024);
    expect(Message.validateMessage(builder.toString()), true);
    builder.write("a");
    expect(builder.toString().length, 1025);
    expect(Message.validateMessage(builder.toString()), false);
  });
}

// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility in the flutter_test package. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mobile/backend/backend_singleton.dart';
import 'package:mobile/model/message_class.dart';
import 'package:mobile/views/message_board.dart';

void main() {
  testWidgets('Test Message Card (Singular)', (WidgetTester tester) async {
    // Set us to test data
    BackendModel.useRealBackend = false;
    BackendModel.instance.postMessage("Message");

    await tester.pumpWidget(
      const MaterialApp(
          home: Scaffold(
        body: MessageBoardPage(),
      )),
    );

    // Wait until data is loaded
    await tester.pumpAndSettle();

    // Check for 0 likes
    void findNum(int n) {
      expect(find.text("-1"), n == -1 ? findsOneWidget : findsNothing);
      expect(find.text("0"), n == 0 ? findsOneWidget : findsNothing);
      expect(find.text("1"), n == 1 ? findsOneWidget : findsNothing);
      expect(find.text("2"), n == 2 ? findsOneWidget : findsNothing);
      expect(find.text("3"), n == 3 ? findsOneWidget : findsNothing);
    }

    // Expect inital state to be 0, one thumbs up, one thumbs down
    Finder up = find.byIcon(Icons.thumb_up);
    Finder down = find.byIcon(Icons.thumb_down);
    Finder commentsButton = find.byIcon(Icons.comment); 
    findNum(0);
    expect(up, findsOneWidget);
    expect(down, findsOneWidget);
    expect(commentsButton, findsOneWidget);
    expect(comments, findsOneWidget);

    // added some general tests to check if comments can be toggled!

    await tester.tap(commentsButton); // Show comments
    await tester.pumpAndSettle();
    expect(comments, findsOneWidget);

    await tester.tap(commentsButton); // Hide comments
    await tester.pumpAndSettle();
    expect(comments, findsNothing);

    await tester.tap(up); // likes @ 1
    await tester.pumpAndSettle();
    findNum(1);

    await tester.tap(up); // likes @ 2
    await tester.pumpAndSettle();
    findNum(2);

    await tester.tap(down); // likes @ 1
    await tester.pumpAndSettle();
    findNum(1);

    await tester.tap(up); // likes @ 2
    await tester.pumpAndSettle();
    findNum(2);

    await tester.tap(up); // likes @ 3
    await tester.pumpAndSettle();
    findNum(3);

    await tester.tap(down); // likes @ 2
    await tester.pumpAndSettle();
    findNum(2);

    await tester.tap(down); // likes @ 1
    await tester.pumpAndSettle();
    findNum(1);

    await tester.tap(down); // likes @ 0
    await tester.pumpAndSettle();
    findNum(0);

    await tester.tap(down); // likes @ 0
    await tester.pumpAndSettle();
    findNum(0);

    await tester.tap(down); // likes @ 0
    await tester.pumpAndSettle();
    findNum(0);
  });
}

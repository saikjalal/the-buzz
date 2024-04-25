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
  testWidgets('Test ListView Message Board', (WidgetTester tester) async {
    // Set us to test data
    BackendModel.useRealBackend = false;
    BackendModel.instance.postMessage("1");
    BackendModel.instance.postMessage("2");
    BackendModel.instance.postMessage("3");
    BackendModel.instance.postMessage("4");

    await tester.pumpWidget(
      const MaterialApp(
          home: Scaffold(
        body: MessageBoardPage(),
      )),
    );

    // Wait until data is loaded
    await tester.pumpAndSettle();

    // Add new data after data is loaded
    BackendModel.instance.postMessage("5");

    // Check it can display multiple widgets
    expect(find.text("1"), findsOneWidget);
    expect(find.text("2"), findsOneWidget);
    expect(find.text("3"), findsOneWidget);
    expect(find.text("4"), findsOneWidget);
    expect(find.text("5"), findsNothing);

    await tester.timedDrag(find.byType(ListView), const Offset(0, 1000), const Duration(seconds: 1));
    await tester.pumpAndSettle();

    // Can refresh to show Message: 5
    expect(find.text("5", skipOffstage: false), findsOneWidget);
  });
}

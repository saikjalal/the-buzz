import 'package:flutter/material.dart';

/// Show a snack bar (bottom floating alert)
/// - [text] is the text to show
/// - [duration] is how long to show the text in seconds
/// - [context] is the BuildContext
/// - [fromBottom] is how far from the bottom
void showBottomAlert(
  String text,
  int duration,
  double fromBottom,
  BuildContext context,
) {
  SnackBar snackBar = SnackBar(
    behavior: SnackBarBehavior.floating,
    margin: EdgeInsets.only(bottom: fromBottom, left: 8, right: 8),
    duration: Duration(seconds: duration),
    content: Text(
      text,
      style: const TextStyle(fontSize: 18),
    ),
  );

  ScaffoldMessenger.of(context).showSnackBar(snackBar);
}

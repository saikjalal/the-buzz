import 'package:flutter/material.dart';
import 'styling.dart';

/// Create a plain app bar
/// - `title` is the title at the top of the app bar
AppBar rootAppBar(String title) {
  // Return an app bar with the title with our blue color
  return AppBar(
    title: Text(title),
    backgroundColor: ColorThemes.secondayColor,
  );
}

// This file was created to synchronize the app bar style across pages (once we are multipage)
// Also we can add more sophisticated app bars (like with a built-in `<` for going back a page)

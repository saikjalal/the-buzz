import 'package:flutter/material.dart';

/// A class to access different text themes
class TextThemes {
  TextThemes._();

  /// A variable used to adjust the themes in all the text. Can be modified programmatically to change the size of the app text
  static double sizefactor = 0;

  /// Will generate basic text
  static TextStyle normalText() => TextStyle(
        fontWeight: FontWeight.w300,
        color: ColorThemes.darkPrimaryColor,
        fontSize: 16 + sizefactor,
      );

  /// Will generate emphasis, large, bold text
  static TextStyle emphasisText() => TextStyle(
        fontWeight: FontWeight.w600,
        color: ColorThemes.darkPrimaryColor,
        fontSize: 20 + sizefactor,
      );
}

/// A class to access different colors
class ColorThemes {
  ColorThemes._();
  /// White
  static Color lightPrimaryColor = Colors.white;
  /// Black
  static Color darkPrimaryColor = Colors.black;
  /// Teal blue
  static Color secondayColor = const Color(0xff66d7d1);
  /// Teal blue with slight variation
  static Color splashColor = secondayColor.withRed(150);
}

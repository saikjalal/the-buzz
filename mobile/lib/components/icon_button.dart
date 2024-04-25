import 'package:flutter/material.dart';
import 'styling.dart';

  /// An icon button with all the styling built-in
  /// - `onPressed` is the function that is called when the icon is tapped
  /// - `icon` is the Icon type that is displayed
class SplashIconButton extends StatelessWidget {
  /// A function that is called on pressed
  final void Function() onPressed;
  /// An icon to display
  final IconData icon;

  /// An icon button with all the styling built-in
  /// - `onPressed` is the function that is called when the icon is tapped
  /// - `icon` is the Icon type that is displayed
  const SplashIconButton({required this.onPressed, required this.icon, super.key});

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.transparent,
      child: IconButton(
        padding: const EdgeInsets.all(4),
        constraints: const BoxConstraints(),
        splashRadius: 20,
        onPressed: onPressed,
        highlightColor: ColorThemes.splashColor, // setting a splash on the button
        icon: Icon(icon, color: ColorThemes.secondayColor, size: 24),
      ),
    );
  }
}

import 'package:flutter/material.dart';

class CustomTextField extends StatefulWidget {
  final String text;
  final Widget Function(String) textBuilder;
  final Function(String) onEdit;
  final bool enabled;
  final InputDecoration? decoration;
  final TextStyle? style;

  const CustomTextField(
      {required this.text,
      required this.textBuilder,
      required this.onEdit,
      this.enabled = true,
      this.decoration,
      this.style,
      super.key});

  @override
  State<CustomTextField> createState() => _CustomTextFieldState();
}

class _CustomTextFieldState extends State<CustomTextField> {
  bool isEditing = false;
  late TextEditingController controller;
  late String textDisplay;
  late FocusNode node;

  @override
  void initState() {
    controller = TextEditingController(text: widget.text);
    textDisplay = widget.text;
    node = FocusNode();
    node.addListener(() {
      if (!node.hasFocus) {
        if (mounted) {
          setState(() {
            isEditing = false;
          });
        }
      }
    });
    super.initState();
  }

  @override
  void dispose() {
    controller.dispose();
    node.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        if (!widget.enabled) return;

        setState(() {
          isEditing = true;
        });
        node.requestFocus();
      },
      child: isEditing
          ? TextField(
              focusNode: node,
              controller: controller,
              onSubmitted: (String v) {
                FocusScope.of(context).unfocus();
                widget.onEdit(v);
                controller.text = v;
                textDisplay = v;
                setState(() {
                  isEditing = false;
                });
              },
              decoration: widget.decoration,
              style: widget.style)
          : widget.textBuilder(textDisplay),
    );
  }
}

import 'package:clean_start_buzz/model/auth.dart';
import 'package:flutter/material.dart';
import '../backend/backend_singleton.dart';
import '../backend/backend_super.dart';
import '../components/alerts.dart';
import '../components/image_picker.dart';
import '../components/shared_prefs.dart';
import '../model/message_class.dart';
import '../components/styling.dart';

/// A Widget for adding a message
/// - `updateCallback` is the code that should be run in the parent after + is clicked
class AddMessageCard extends StatefulWidget {
  /// A callback function for updating parent UI
  final void Function() updateCallback;

  /// A Widget for adding a message
  /// - `updateCallback` is the code that should be run in the parent after + is clicked
  AddMessageCard(this.updateCallback, {super.key});

  @override
  State<AddMessageCard> createState() => _AddMessageCardState();
}

class _AddMessageCardState extends State<AddMessageCard> {
  /// Controller for the text that was typed
  final TextEditingController _controller = TextEditingController();

  /// A focus node for defocusing the keyboard on +
  final FocusNode _focus = FocusNode();

  /// The file to be added with the comment
  String? _fileToAdd;

  /// Boolean for keeping track if image has already been uploaded so we can limit to 1 per message
  bool _fileAdded = false;

  @override
  Widget build(BuildContext context) {
    return Padding(
      // Add padding under the widget
      padding: const EdgeInsets.only(bottom: 16),
      child: Container(
        // Add border styling
        decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(16),
            color: Colors.transparent,
            border: Border.all(
              color: ColorThemes.secondayColor,
              width: 2,
            )),
        padding: const EdgeInsets.all(16), // Padding from the border
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Flexible(
              // Text field
              child: TextField(
                controller: _controller,
                focusNode: _focus,
                maxLength: 1024,
                maxLines: null,
                buildCounter: ((context,
                        {required currentLength,
                        required isFocused,
                        maxLength}) =>
                    Container(
                      alignment: Alignment.centerLeft,
                      child: Text(
                          "$currentLength/$maxLength ${_fileAdded ? "\t image added!" : ""}",
                          style: const TextStyle(
                              fontSize:
                                  12)), // keep track of the number of characters
                    )),
                keyboardType:
                    TextInputType.multiline, // make it a multiline text field
                decoration: const InputDecoration(
                  isCollapsed: true,
                  border: InputBorder.none,
                  hintText: 'Post Message',
                ),
              ),
            ),
            GestureDetector(
              // GestureDetector wraps the image from gallery button
              onTap: () async {
                // When we tap the button, we open the image upload
                if (!_fileAdded) {
                  _fileToAdd = await getFromGallery();
                  if (_fileToAdd != null) {
                    _fileAdded = true;
                    setState(() {});
                  }
                } else {
                  showBottomAlert(
                      "Already added an image, can only add 1", 3, 22, context);
                }
              },
              child: Padding(
                // gallery button
                padding: const EdgeInsets.only(right: 8, left: 16),
                child: Icon(
                  Icons.image,
                  color: ColorThemes.secondayColor,
                  size: 25,
                ),
              ),
            ),
            GestureDetector(
              // GestureDetector wraps the image from camera button
              onTap: () async {
                // When we tap the button, we open the image upload
                if (!_fileAdded) {
                  _fileToAdd = await getFromCamera();
                  if (_fileToAdd != null) {
                    _fileAdded = true;
                    setState(() {});
                  }
                } else {
                  showBottomAlert(
                      "Already added an image, can only add 1", 3, 22, context);
                }
              },
              child: Padding(
                // camera button
                padding: const EdgeInsets.only(right: 8, left: 16),
                child: Icon(
                  Icons.photo_camera,
                  color: ColorThemes.secondayColor,
                  size: 25,
                ),
              ),
            ),
            GestureDetector(
              // GestureDetector wraps the '+' button
              onTap: () async {
                // When we tap the +, we get the text
                String input = _controller.text;
                // Validate it
                if (!Message.validateMessage(input)) return;
                // Post the message
                Backend backend = BackendModel.instance;
                // add the message, and if there is a file put that if not put empty string
                // Message? messagePosted = await backend.postMessage(input, _fileToAdd ?? "");
                // if (_fileAdded && messagePosted != null) {
                //   addFileToSF(true, messagePosted.id, _fileToAdd!);
                // }
                backend.postMessage(input, _fileToAdd ?? "");
                // reset adding file logic
                _fileAdded = false;
                _fileToAdd = null;
                // And then set text to empty and unfocus
                _controller.text = "";
                _focus.unfocus();
                // And update callback
                widget.updateCallback();
              },
              child: Padding(
                // '+' button
                padding: const EdgeInsets.only(right: 8, left: 16),
                child: Icon(
                  Icons.add_circle_outline_rounded,
                  color: ColorThemes.secondayColor,
                  size: 25,
                ),
              ),
            )
          ],
        ),
      ),
    );
  }
}

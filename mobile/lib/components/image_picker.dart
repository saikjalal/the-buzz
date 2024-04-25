import 'dart:io';
import 'dart:convert';
import 'package:image_picker/image_picker.dart';

/// Get image from gallery
Future<String?> getFromGallery() async {
  XFile? pickedFile = await ImagePicker().pickImage(
    source: ImageSource.gallery,
    maxWidth: 1800,
    maxHeight: 1800,
  );
  if (pickedFile != null) {
    File imageFile = File(pickedFile.path);
    //convert file to base64
    String imgB64 = base64Encode(imageFile.readAsBytesSync());
    return imgB64;
  } else {
    return null;
  }
}

/// Get image from camera
Future<String?> getFromCamera() async {
  XFile? pickedFile = await ImagePicker().pickImage(
    source: ImageSource.camera,
    maxWidth: 1800,
    maxHeight: 1800,
  );
  if (pickedFile != null) {
    File imageFile = File(pickedFile.path);
    //convert file to base64
    String imgB64 = base64Encode(imageFile.readAsBytesSync());
    return imgB64;
  } else {
    return null;
  }
}
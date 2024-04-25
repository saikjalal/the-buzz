import 'package:flutter/material.dart';
import '../backend/backend_singleton.dart';
import '../model/user_class.dart';
// Can ignore most blue squiggly lines.
// this is a basic widget to edit profile

class EditProfileScreen extends StatefulWidget {
  const EditProfileScreen({Key? key}) : super(key: key);

  @override
  _EditProfileScreenState createState() => _EditProfileScreenState();
}

class _EditProfileScreenState extends State<EditProfileScreen> {
  final _formKey = GlobalKey<FormState>();

  // Strings for entering basic information
  // email cannot be changed, it's not included here
  String? _name;
  String? _genderIdentity;
  String? _bio;
  String? _sexualOrientation;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Edit Profile'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              TextFormField(
                decoration: InputDecoration(
                  labelText: 'Name',
                  hintText: 'Update your name',
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter a valid name';
                  }
                  return null;
                },
                onSaved: (value) {
                  _name = value;
                },
              ),TextFormField(
                decoration: InputDecoration(
                  labelText: 'Bio',
                  hintText: 'Update your Bio',
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter a valid Bio';
                  }
                  return null;
                },
                onSaved: (value) {
                  _bio = value;
                },
              ),
              TextFormField(
                decoration: InputDecoration(
                  labelText: 'Gender Identity',
                  hintText: 'Update your Gender Identity',
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter a Gender Identity';
                  }
                  return null;
                },
                onSaved: (value) {
                  _genderIdentity = value;
                },
              ),
              TextFormField(
                decoration: InputDecoration(
                  labelText: 'Sexual Orientation',
                  hintText: 'Update your sexual orientation',
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter a sexual orientation';
                  }
                  return null;
                },
                onSaved: (value) {
                  _sexualOrientation = value;
                },
              ),
              SizedBox(height: 16),
              ElevatedButton(
                onPressed: () {
                  if (_formKey.currentState!.validate()) {
                    _formKey.currentState!.save();

                    //create user object to pass into updateProfile
                    //backend should not edit name or email, so those are empty strings
                    //if no info was put for any other parameters, make them empty strings
                    User newInfo = User(_name ?? "-", "-", _genderIdentity ?? "-",
                        _sexualOrientation ?? "-", _bio ?? "-");
                    BackendModel.instance.updateProfile(newInfo);

                    Navigator.pop(context);
                  }
                },
                child: Text('Save'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

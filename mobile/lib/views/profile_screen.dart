import 'package:flutter/material.dart';
import '../components/future_handler.dart';
import '../../backend/backend_singleton.dart';
import '../model/user_class.dart';

class ProfileScreen extends StatelessWidget {
  final int userIdToDisplay;
  const ProfileScreen(this.userIdToDisplay, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Profile'),
      ),
      body: Center(
          child: waitAndQuery<User?>(
              future: BackendModel.instance.getProfile(userIdToDisplay),
              child: (User? t) {
                if (t == null) {
                  return const Text(
                    "Error: No User Found",
                    style: TextStyle(color: Colors.red),
                  );
                }

                return Column(
                  children: <Widget>[
                    const Text('Name:', style: TextStyle(fontSize: 20)),
                    Text(t.name, style: TextStyle(fontSize: 16)),
                    const Text('\nEmail:', style: TextStyle(fontSize: 20)),
                    Text(t.email, style: TextStyle(fontSize: 16)),
                    const Text('\nGender Identity:',
                        style: TextStyle(fontSize: 20)),
                    Text(t.genderIdentity, style: TextStyle(fontSize: 16)),
                    const Text('\nSexual Identity:',
                        style: TextStyle(fontSize: 20)),
                    Text(t.sexualIdentity, style: TextStyle(fontSize: 16)),
                    const Text('\nBio:', style: TextStyle(fontSize: 20)),
                    Text(t.bio, style: TextStyle(fontSize: 16)),
                  ],
                );
              })),
    );
  }
}

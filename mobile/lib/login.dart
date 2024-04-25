import 'package:flutter/material.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'backend/backend_singleton.dart';
import 'backend/backend_super.dart';
import 'components/alerts.dart';
import 'model/auth.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({Key? key}) : super(key: key);

  @override
  LoginScreenState createState() => LoginScreenState();
}

class LoginScreenState extends State<LoginScreen> {
  @override
  Widget build(BuildContext context) {
    return Material(
      child: Column(
        children: [
          const SizedBox(height: 36), // add some padding
          const Text(
            "Sign into the Buzz",
            style: TextStyle(fontSize: 30),
          ),
          const SizedBox(height: 18), // add some padding
          ElevatedButton(
            onPressed: () async {
              //** TODO: WHEN HUA FIXES SIGN IN, WE USE THIS GOOD CODE INSTEAD */
              /*GoogleSignIn signIn = Auth.instance.googleSignIn;
              GoogleSignInAccount? accountInstance = await signIn.signIn();
              if (accountInstance == null) {
                // ignore: use_build_context_synchronously
                showBottomAlert("Unable to sign-in", 6, 22, context);
                return;
              }
              GoogleSignInAuthentication auth =
                  await accountInstance.authentication;
              String? idToken = auth.accessToken;
              String? idTokenAlt = auth.idToken;
              print(idToken);
              print(idTokenAlt);
              if (idToken == null) {
                return;
              }
              Backend model = BackendModel.instance;
              Session? sess = await model.sessionAuthenticate(idToken);
              if (sess == null) {
                // ignore: use_build_context_synchronously
                showBottomAlert("Unable to sign-in", 6, 22, context);
                return;
              }
              model.userID = sess.userID;
              model.sessionToken = sess.sessionToken;
              */
              Backend model = BackendModel.instance;
              model.sessionToken = "testToken";
              Navigator.of(context).pushReplacementNamed('/home');
            },
            child: const Text("Sign In"),
          ),
        ],
      ),
    );
  }
}

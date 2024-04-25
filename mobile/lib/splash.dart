// ignore_for_file: use_build_context_synchronously

import 'package:flutter/material.dart';
import 'components/appbar.dart';
import 'model/auth.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({Key? key}) : super(key: key);

  @override
  SplashScreenState createState() => SplashScreenState();
}

class SplashScreenState extends State<SplashScreen> {
  @override
  void initState() {
    checkSignInState();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: rootAppBar("Splash Screen"),
      body: const Center(
        child: CircularProgressIndicator(),
      ),
    );
  }

  // login logic
  void checkSignInState() async {
    bool signedIn = await Auth.instance.googleSignIn.isSignedIn();
    if (signedIn) {
      // If already signed in, go to home
      Navigator.pushReplacementNamed(context, '/home');
    } else {
      // Otherwise go to login
      Navigator.pushReplacementNamed(context, '/login');
    }
  }
}

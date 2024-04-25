import 'dart:io';

import 'package:clean_start_buzz/components/shared_prefs.dart';
import 'package:flutter/material.dart';
import 'components/styling.dart';
import 'login.dart';
import 'splash.dart';
import 'views/message_board.dart';

//Imported class to bypass https security
class MyHttpOverrides extends HttpOverrides {
  @override
  HttpClient createHttpClient(SecurityContext? context) {
    return super.createHttpClient(context)
      ..badCertificateCallback =
          (X509Certificate cert, String host, int port) => true;
  }
}

void main() {
  HttpOverrides.global = MyHttpOverrides();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    clearPrefs();
    return GestureDetector(
      onTap: () {
        FocusManager.instance.primaryFocus?.unfocus();
      },
      child: MaterialApp(
        debugShowCheckedModeBanner: false,
        title: 'The Buzz',
        theme: ThemeData(
          scaffoldBackgroundColor: ColorThemes.lightPrimaryColor,
          colorScheme:
              ColorScheme.fromSeed(seedColor: ColorThemes.secondayColor),
          fontFamily: 'Lato',
        ),
        initialRoute: '/',
        routes: {
          // routes
          '/': (_) => const SplashScreen(),
          '/login': (_) => const LoginScreen(),
          '/home': (context) => const MessageBoardPage(),
        },
      ),
    );
  }
}

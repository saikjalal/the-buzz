import 'package:google_sign_in/google_sign_in.dart';

String id =
    "1064276834137-9nsvad36bb95klb7imoud3rr856nejjn.apps.googleusercontent.com";

class Auth {
  static Auth? _instance;
  // Avoid self isntance
  Auth._();
  static Auth get instance {
    _instance ??= Auth._();
    return _instance!;
  }

  GoogleSignIn googleSignIn =
      GoogleSignIn(clientId: id, serverClientId: id, scopes: ["email"]);
}

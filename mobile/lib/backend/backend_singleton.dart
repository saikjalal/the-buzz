import 'package:flutter/foundation.dart';
import 'mock_backend.dart';
import 'real_backend.dart';
import 'dart:io' show Platform;

import 'backend_super.dart';

/// A class for accessing the instances of the backend
class BackendModel {
  // by default, it is set to use the real backend
  static bool useRealBackend = true;
  /// Private variables to represent a false backend and true backend instances
  static final TrueBackend _tInstance = TrueBackend.instance;
  static final FalseBackend _fInstance = FalseBackend.instance;

  /// Constructor for the singleton (private)
  BackendModel._();

  // Will get the current instance of the singleton
  static Backend get instance {
    if (!kDebugMode && !useRealBackend) {
      // If in production, make sure that true backend is being used.
      throw Exception("Cannot use false backend in a non-development version of the app. Remove instances of BackendModel.useRealBackend = false\n");
    }

    //if (Platform.environment.containsKey('FLUTTER_TEST') && useRealBackend) {
      // If in testing, make sure that false backend is being used.
      //throw Exception("Cannot use true backend in a test version of the app. Set useReadBackend variable to false:\nBackendModel.useRealBackend = false\n");
   // }

    // Return tInstance if useReadBackend is true. Return _fInstance otherwise
    return useRealBackend ? _tInstance : _fInstance;
  }
}

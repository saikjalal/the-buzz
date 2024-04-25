import 'package:clean_start_buzz/backend/backend_singleton.dart';
import 'package:clean_start_buzz/components/shared_prefs.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  test('Test message caching', () async {
    // Set us to test data
    BackendModel.useRealBackend = false;
    //post message (it should be cached)
    await BackendModel.instance.postMessage("test message", "filedata");

    //try to access message once so it will cache upon fetch
    //check for message 1 (assuming db is clean at start of test)
    await BackendModel.instance.getMessageById(1);

    //after you fetched it, it should be in the cache now
    dynamic found = await checkForMessageinSF(1);

    expect(found != false, true);
  });
}

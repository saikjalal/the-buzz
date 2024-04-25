import 'package:flutter/material.dart';

/// A class for generating the future builer.
/// Will show a circular progress indicator (i.e. spinny wheel of death) while waiting for data to be fetched. Then will show child.
/// - `T` is the type of the data to fetch
/// - `future` is the future we want to wait for
/// - `child` is a lambda function that takes one variable (the data returned from the future) and is tasked with displaying the data after it has loaded
FutureBuilder waitAndQuery<T>(
    {required Future<T> future, required Widget Function(T data) child}) {
  return FutureBuilder(
    future: future, // load the future into the future builder
    builder: (context, snapshot) {
      if (snapshot.hasError) {
        // If snapshot has an error, show error
        print(snapshot.error);
        print(snapshot.stackTrace);
        return Text("Error: ${snapshot.error}",
            style: const TextStyle(color: Colors.red));
      } else if (!snapshot.hasData) {
        // If snapshot is still loading, show progress indicator
        return const Center(child: CircularProgressIndicator());
      }

      // If snapshot is finished loading, show child
      return child(snapshot.data as T);
    },
  );
}

/// User class represents a user of the appand their profile info
class User {
  /// The name of the user
  late String name;

  /// The email of the user
  late String email;

  /// The gender identity of the user
  late String genderIdentity;

  /// The sexual identity of the user
  late String sexualIdentity;

  /// The bio of the user
  late String bio;

  /// Basic constructor for the user
  /// - `name` | the user's name [String]
  /// - `email` | the user's email [String]
  /// - `genderIdentity` | the user's gender identity [String]
  /// - `sexualIdentity` | the user's sexual identity [String]
  /// - `bio` | the user's bio [String]
  User(this.name, this.email, this.genderIdentity, this.sexualIdentity, this.bio);

  /// Constructor of message from a json
  /// - `json` is the data receieved from the database [Map]
  User.fromJson(Map<String, dynamic> json) {
    // Set data fields
    name = json['name'] ?? "";
    email = json['email'] ?? "";
    genderIdentity = json['gender_identity'] ?? "";
    sexualIdentity = json['sexual_identity'] ?? "";
    bio = json['bio'] ?? "";
  }

  /// Construct a json object of the data
  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'email': email,
      'gender_identity': genderIdentity,
      'sexual_identity': sexualIdentity,
      'bio': bio,
    };
  }
}

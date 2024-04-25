package edu.lehigh.cse216.mfs409.admin;

public class User {
	Integer uid;
	String name;
	String email;
	String gender_identity;
	String sexual_identity;
	String bio;

	User(Integer uid, String name, String email, String gender_identity, String sexual_identity, String bio) {
		this.uid = uid;
		this.name = name;
		this.email = email;
		this.gender_identity = gender_identity;
		this.sexual_identity = sexual_identity;
		this.bio = bio;
	}
}
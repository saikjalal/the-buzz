package edu.lehigh.cse216.cag224.backend;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    Integer uid;
    String name;
    String email;
    String gender_identity;
    String sexualOrientation;
    String bio;
    Boolean isBanned;

    /**
     * A user constructor
     * 
     * @param uid               the user's uid
     * @param name              the user's name
     * @param email             the user's email
     * @param gender_identity   the user's gender identity (protected field)
     * @param sexualOrientation the user's sexual identity (protected field)
     * @param bio               the user's bio
     * @param isBanned          the user's ban status (protected field)
     */
    User(Integer uid, String name, String email, String gender_identity, String sexualOrientation, String bio,
            Boolean isBanned) {
        if (name == null)
            name = "";
        if (email == null)
            email = "";
        if (gender_identity == null)
            gender_identity = "";
        if (sexualOrientation == null)
            sexualOrientation = "";
        if (bio == null)
            bio = "";
        if (isBanned == null)
            isBanned = false;
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.gender_identity = gender_identity;
        this.sexualOrientation = sexualOrientation;
        this.bio = bio;
        this.isBanned = isBanned;
    }

    /**
     * A user factory to process a result set.
     * 
     * @param rs   the result set with the following parameters expected
     *                 <hr>
     *                 user ID = result.getInt("userID") <br>
     *                 <br>
     *                 user name = result.getInt("name") <br>
     *                 <br>
     *                 user email = result.getInt("email") <br>
     *                 <br>
     *                 user gender identity = result.getInt("genderIdentity") <br>
     *                 <br>
     *                 user sexual orientation = result.getInt("sexualOrientation")
     *                 <br>
     *                 <br>
     *                 user bio = result.getInt("bio") <br>
     *                 <br>
     *                 user's ban status = result.getInt("isBanned") <br>
     *                 <br>
     * @return a User instance
     * @throws SQLException if there is an issue processing the result set
     */
    public static User factory(ResultSet rs) throws SQLException {
        return new User(rs.getInt("userID"), rs.getString("name"), rs.getString("email"),
                rs.getString("genderIdentity"), rs.getString("sexualOrientation"), rs.getString("bio"),
                rs.getBoolean("isBanned"));
    }
}

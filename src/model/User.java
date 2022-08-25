package model;

/**
 * Creates User object for login
 */
public class User {
    public int userID;
    public String userName;
    public String userPassword;

    /**
     * Constructor for each User
     */
    public User(int userID, String userName, String userPassword) {
        this.userID = userID;
        this.userName = userName;
        this.userPassword = userPassword;
    }

    /**
     * User ID get function
     * @return userID
     */
    public int getUserID() {return userID;}

    /**
     * User Name get function
     * @return userName
     */
    public String getUserName() {return userName;}

    /**
     * User password get function
     * @return userPassword
     */
    public String getUserPassword() {return userPassword;}
}

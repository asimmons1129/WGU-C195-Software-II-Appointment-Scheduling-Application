package model;

/**
 * Creates a user
 * @author Andre Simmons
 */
public class User {
    private int id;
    private String username;
    private String password;

    /**
     * Constructor for each user
     * @param i
     * @param u
     * @param p
     */
    public User(int i, String u, String p){
        id = i;
        username = u;
        password = p;
    }

    /*
     * getter and setter functions
     */
    public void setId(int i) {id = i;}

    public int getId() {return id;}

    public void setUsername(String u) {username = u;}

    public String getUsername() {return username;}

    public void setPassword(String p) {password = p;}

    public String getPassword() {return password;}
}

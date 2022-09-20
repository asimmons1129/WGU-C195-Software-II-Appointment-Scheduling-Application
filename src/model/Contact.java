package model;

/**
 * Creates a contact
 * @author Andre Simmons
 */
public class Contact {
    private int id;
    private String name;
    private String email;

    /**
     * Constructor variable for each contact
     * @param i
     * @param n
     * @param e
     */
    public Contact(int i, String n, String e){
        id = i;
        name = n;
        email = e;
    }
    /*
    Getter and setter functions
     */
    public void setId(int i){id = i;}

    public int getId(){return id;}

    public void setName(String n){name = n;}

    public String getName(){return name;}

    public void setEmail(String e){email = e;}

    public String getEmail(){return email;}
}

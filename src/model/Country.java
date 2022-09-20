package model;

/**
 * Creates a country
 * @author Andre Simmons
 */
public class Country {
    private int id;
    private String name;

    /**
     * Constructor for each country
     * @param i
     * @param n
     */
    public Country(int i, String n){
        id = i;
        name = n;
    }

    /*
    getter and setter functions
     */
    public void setId(int i){id = i;}

    public int getId(){return id;}

    public void setName(String n){name = n;}

    public String getName(){return name;}

}

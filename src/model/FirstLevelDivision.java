package model;

/**
 * Creates first level division
 * @author Andre Simmons
 */
public class FirstLevelDivision {
    private int id;
    private String name;
    private int countryId;

    /**
     * Constructor for each first level division
     * @param i
     * @param n
     * @param c
     */
    public FirstLevelDivision(int i, String n, int c){
        id = i;
        name = n;
        countryId = c;
    }
    /*
    getter and setter functions
     */
    public void setId(int i) {id = i;}

    public int getId() {return id;}

    public void setName(String n) {name = n;}

    public String getName() {return name;}

    public void setCountryId(int c) {countryId = c;}

    public int getCountryId() {return countryId;}
}

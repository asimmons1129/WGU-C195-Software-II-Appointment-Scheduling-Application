package model;

/**
 * Creates country object
 * @author Andre Simmons
 */
public class Country {
    private int countryID;
    private String countryName;

    /**
     *Constructor for country
     * @param countryID
     * @param countryName
     */
    public Country(int countryID, String countryName) {
        this.countryID = countryID;
        this.countryName = countryName;
    }

    /**
     *Country ID get function
     * @return countryID
     */
    public int getCountryID(){return countryID;}

    /**
     *Country Name get function
     * @return countryName
     */
    public String getCountryName() {return countryName;}
}

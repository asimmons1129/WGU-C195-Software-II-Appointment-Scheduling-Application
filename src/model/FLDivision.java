package model;

/**
 * Creates First Level Division object
 * @author Andre Simmons
 */
public class FLDivision {
    private int divisionID;
    private String divisionName;
    public int country_ID;

    /**
     *Constructor variable for first level division
     * @param divisionID
     * @param country_ID
     * @param divisionName
     */
    public FLDivision(int divisionID, String divisionName, int country_ID) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.country_ID = country_ID;
    }

    /**
     *First level division ID get function
     * @return divisionID
     */
    public int getDivisionID() {return divisionID;}

    /**
     *First level division name get function
     * @return divisionName
     */
    public String getDivisionName() {return divisionName;}

    /**
     *Country ID get function
     * @return country_ID
     */
    public int getCountry_ID() {return country_ID;}

}

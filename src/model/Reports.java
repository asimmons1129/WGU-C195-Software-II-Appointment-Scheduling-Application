package model;

/**
 * Creates general Report object
 * @author Andre Simmons
 */
public class Reports {
    private int divisionCount;
    private String divisionName;
    public String appointmentMonth;
    public int appointmentTotal;

    /**
     * Countructor for Reports
     * @param divisionCount
     * @param divisionName
     */
    public Reports(String divisionName, int divisionCount) {
        this.divisionCount = divisionCount;
        this.divisionName = divisionName;

    }


    /**
     * Division name get function
     * @return countryName
     */
    public String getDivisionName() {return divisionName;}

    /**
     *Get function for each division's total
     * @return countryCount
     */
    public int getDivisionCount() {return divisionCount;}
}

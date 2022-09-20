package model;

/**
 * Creates report that filters customers by division
 * @author Andre Simmons
 */
public class Report {
    private int divisionTotal;
    private String divisionName;

    /**
     * Constructor for each customer by division report
     * @param dt
     * @param dn
     */
    public Report(int dt, String dn){
        divisionTotal = dt;
        divisionName = dn;
    }
    /*
    getter and setter functions
     */
    public void setDivisionTotal(int dt) {divisionTotal = dt;}

    public int getDivisionTotal() {return divisionTotal;}

    public void setDivisionName(String dn) {divisionName = dn;}

    public String getDivisionName() {return divisionName;}


}

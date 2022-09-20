package model;

/**
 * Creates a report that filters the total appointments for each type and shows the month
 * @author Andre Simmons
 */
public class ReportByMonth {
    private String month;
    private String type;
    private int typeTotal;

    /**
     * Constructor for each report by type for each month
     * @param m
     * @param t
     * @param tt
     */
    public ReportByMonth(String m, String t, int tt){
        month = m;
        type = t;
        typeTotal = tt;
    }
    /*
    getter and setter functions
     */
    public void setMonth(String m) {month = m;}

    public String getMonth() {return month;}

    public void setType(String t) {type = t;}

    public String getType() {return type;}

    public void setTypeTotal(int tt) {typeTotal = tt;}

    public int getTypeTotal() {return typeTotal;}
}

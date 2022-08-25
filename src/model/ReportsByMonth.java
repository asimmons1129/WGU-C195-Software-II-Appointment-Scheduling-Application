package model;

/**
 * Creates Report object that's filtered by Month
 * @author Andre Simmons
 */
public class ReportsByMonth {
    public String appointmentMonth;
    public int appointmentTotal;

    /**
     * Constructor for report object filtered by month
     * @param appointmentMonth
     * @param appointmentTotal
     */
    public ReportsByMonth(String appointmentMonth, int appointmentTotal) {
        this.appointmentMonth = appointmentMonth;
        this.appointmentTotal = appointmentTotal;
    }

    /**Appointment Month get function
     * @return appointmentMonth
     */
    public String getAppointmentMonth() {return appointmentMonth;}

    /**
     * Appointment total per month get function
     * @return appointmentTotal
     */
    public int getAppointmentTotal() {return appointmentTotal;}
}

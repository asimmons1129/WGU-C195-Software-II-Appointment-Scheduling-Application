package model;

/**
 * Creates Report object filtered by type
 * @author Andre Simmons
 */
public class ReportsByType {
    public String appointmentType;
    public int appointmentTotal;

    /**
     *Constructor for Reports filtered by Type
     * @param appointmentTotal
     * @param appointmentType
     */
    public ReportsByType(String appointmentType, int appointmentTotal) {
        this.appointmentType = appointmentType;
        this.appointmentTotal = appointmentTotal;
    }

    /**
     * Appointment Type get function
     * @return appointmentType
     */
    public String getAppointmentType() {return appointmentType;}

    /**
     * Get function for Appointment Total for each type
     * @return
     */
    public int getAppointmentTotal() {return appointmentTotal;}
}

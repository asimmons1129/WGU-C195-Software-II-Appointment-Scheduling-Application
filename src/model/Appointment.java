package model;
import java.time.LocalDateTime;

/**
 * Creates an appointment
 * @author Andre Simmons
 */
public class Appointment {
    /**
     * appointment ID
     */
    private int appointment_id;
    /**
     * appointment title
     */
    private String appointment_title;
    /**
     * description of appointment
     */
    private String appointment_description;
    /**
     * location of appointment
     */
    private String appointment_location;
    /**
     * type of appointment
     */
    private String appointment_type;
    /**
     * start time of appointment
     */
    private LocalDateTime start;
    /**
     * end time of appointment
     */
    private LocalDateTime end;
    /**
     * customer ID
     */
    public int customerID;
    /**
     * Database user ID
     */
    public int userID;
    /**
     * contact ID
     */
    public int contactID;
    /**
     * Contact Name
     */
    public String contactName;

    /**
     * Constructor variable for each appointment
     * @param appointmentID
     * @param appointmentTitle
     * @param appointmentDescription
     * @param appointmentLocation
     * @param appointmentType
     * @param start
     * @param end
     * @param customerID
     * @param userID
     */
    public Appointment(int appointmentID, String appointmentTitle, String appointmentDescription,
                        String appointmentLocation, String appointmentType, LocalDateTime start, LocalDateTime end, int customerID,
                        int userID, String contactName) {
        this.appointment_id = appointmentID;
        this.appointment_title = appointmentTitle;
        this.appointment_description = appointmentDescription;
        this.appointment_location = appointmentLocation;
        this.appointment_type = appointmentType;
        this.start = start;
        this.end = end;
        this.customerID = customerID;
        this.userID = userID;
        this.contactName = contactName;
    }

    /**
     * Appointment ID get function
     * @return appointmentID
     */
    public int getAppointmentID() {return appointment_id;}

    /**
     *Appointment title get function
     * @return appointmentTitle
     */
    public String getAppointmentTitle() {return appointment_title;}

    /**
     * Appointment description get function
     * @return appointmentDescription
     */
    public String getAppointmentDescription() {return appointment_description;}

    /**
     * Appointment location get function
     * @return appointmentLocation
     */
    public String getAppointmentLocation() {return appointment_location;}

    /**
     * Appointment type get function
     * @return appointmentType
     */
    public String getAppointmentType() {return appointment_type;}

    /**
     * Appointment start time get function
     * @return start
     */
    public LocalDateTime getStart() {
        System.out.println("Start Date/Time: " + start);
        return start;
    }



    /**
     * Appointment end time get function
     * @return end
     */
    public LocalDateTime getEnd() {
        System.out.println("End Date/Time: " + end);
        return end;
    }

    /**
     * Customer ID get function
     * @return customerID
     */
    public int getCustomerID () {return customerID;}

    /**
     * User ID get function
     * @return userID
     */
    public int getUserID() {return userID;}

    public String getContactName() {return contactName;}

}

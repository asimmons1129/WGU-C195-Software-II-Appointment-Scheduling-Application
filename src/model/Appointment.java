package model;
import java.time.LocalDateTime;

/**
 * Creates an appointment
 * @author Andre Simmons
 */
public class Appointment {
    private int appointmentId;
    private String title;
    private String description;
    private String location;
    private String type;
    private int customerId;
    private int userId;
    private String contactName;
    private int contactId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    /**
     * Constructor variable for each appointment
     * @param ai
     * @param ti
     * @param d
     * @param l
     * @param ty
     * @param ci
     * @param ui
     * @param cn
     * @param coi
     * @param st
     * @param et
     */
    public Appointment(int ai, String ti, String d, String l, String ty, int ci, int ui, String cn, int coi, LocalDateTime st, LocalDateTime et){
        appointmentId = ai;
        title = ti;
        description = d;
        location = l;
        type = ty;
        customerId = ci;
        userId = ui;
        contactName = cn;
        startTime = st;
        endTime = et;
        contactId = coi;
    }

    /**
     * appointment id set function
     * @param ai
     */
    public void setAppointmentId(int ai){appointmentId = ai;}

    /**
     * appointment id get function
     * @return appointmentId
     */
    public int getAppointmentId(){return appointmentId;}

    /**
     * description set function
     * @param d
     */
    public void setDescription(String d){description = d;}

    /**
     * description get function
     * @return description
     */
    public String getDescription(){return description;}
    /**
     * location set function
     * @param l
     */
    public void setLocation(String l){location = l;}
    /**
     * location get function
     * @return location
     */
    public String getLocation(){return location;}
    /**
     * type set function
     * @param ty
     */
    public void setType(String ty){type = ty;}
    /**
     * type get function
     * @return type
     */
    public String getType(){return type;}
    /**
     * title set function
     * @param ti
     */
    public void setTitle(String ti){title = ti;}
    /**
     * title get function
     * @return title
     */
    public String getTitle(){return title;}
    /**
     * customer id set function
     * @param ci
     */
    public void setCustomerId(int ci){customerId = ci;}
    /**
     * customer id get function
     * @return customerId
     */
    public int getCustomerId(){return customerId;}
    /**
     * user id set function
     * @param ui
     */
    public void setUserId(int ui){userId = ui;}
    /**
     * user id get function
     * @return userId
     */
    public int getUserId(){return userId;}
    /**
     * contact name set function
     * @param cn
     */
    public void setContactName(String cn){contactName = cn;}
    /**
     * contact name get function
     * @return contactName
     */
    public String getContactName(){return contactName;}
    /**
     * contact id set function
     * @param coi
     */
    public void setContactId(int coi){contactId = coi;}
    /**
     * contact id get function
     * @return contactId
     */
    public int getContactId(){return contactId;}
    /**
     * start time set function
     * @param st
     */
    public void setStartTime(LocalDateTime st){startTime = st;}
    /**
     * start time get function
     * @return startTime
     */
    public LocalDateTime getStartTime(){return startTime;}
    /**
     * end time set function
     * @param et
     */
    public void setEndTime(LocalDateTime et){endTime = et;}
    /**
     * end time get function
     * @return endTime
     */
    public LocalDateTime getEndTime(){return endTime;}
}

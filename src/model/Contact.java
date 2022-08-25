package model;

/**
 * Creates Contact objects
 * @author Andre Simmons
 */
public class Contact {
    public int contactID;
    public String contactName;
    public String contactEmail;

    /**
     * Constructor for contact
     * @param contactID
     * @param contactName
     * @param contactEmail
     */
    public Contact(int contactID, String contactName, String contactEmail) {
        this.contactID = contactID;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
    }

    /**
     *contact ID get function
     * @return contactID
     */
    public int getContactId() {return contactID;}

    /**
     *Contact Name get function
     * @return contactName
     */
    public String getContactName() {return contactName;}

    /**
     *Contact Email get function
     * @return contactEmail
     */
    public String getContactEmail() {return contactEmail;}
}

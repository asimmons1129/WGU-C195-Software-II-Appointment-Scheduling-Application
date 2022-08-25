package model;

/**
 * Creates Customer Object
 * @author Andre Simmons
 */
public class Customer {
    private String divisionName;
    private int customerID;
    private String customerName;
    private String customerAddress;
    private String customerPostalCode;
    private String customerPhoneNumber;
    private int divisionID;

    /**
     * Constructor for each Customer
     *
     * @param customerID
     * @param customerName
     * @param customerAddress
     * @param customerPostalCode
     * @param customerPhoneNumber
     * @param divisionID
     * @param divisionName
     */
    public Customer(int customerID, String customerName, String customerAddress, String customerPostalCode,
                    String customerPhoneNumber, int divisionID, String divisionName) {

        this.customerID = customerID;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPostalCode = customerPostalCode;
        this.customerPhoneNumber = customerPhoneNumber;
        this.divisionID = divisionID;
        this.divisionName = divisionName;
    }


    /**
     * Customer ID get function
     *
     * @return customerID
     */
    public Integer getCustomerID() {
        return customerID;
    }

    /**
     * Customer ID set function
     *
     * @param customerID
     */
    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    /**
     * Customer Name get function
     *
     * @return customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Customer Name set function
     *
     * @param customerName
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Customer Address get function
     *
     * @return customerAddress
     */
    public String getCustomerAddress() {
        return customerAddress;
    }

    /**
     * Customer Address set function
     *
     * @param address
     */
    public void setCustomerAddress(String address) {
        this.customerAddress = address;
    }

    /**
     * Customer Postal Code get function
     *
     * @return customerPostalCode
     */
    public String getCustomerPostalCode() {
        return customerPostalCode;
    }

    /**
     * Customer Postal Code set function
     *
     * @param postalCode
     */
    public void setCustomerPostalCode(String postalCode) {
        this.customerPostalCode = postalCode;
    }

    /**
     * Customer Phone Number get function
     *
     * @return customerPhoneNumber
     */
    public String getCustomerPhone() {
        return customerPhoneNumber;
    }

    /**
     * Customer Phone Number set function
     *
     * @param phone
     */
    public void setCustomerPhone(String phone) {
        this.customerPhoneNumber = phone;
    }

    /**
     * Customer First Level Division ID get function
     *
     * @return divisionID
     */
    public Integer getCustomerDivisionID() {
        return divisionID;
    }

    /**
     * Customer First Level Division ID set function
     *
     * @param divisionID
     */
    public void setCustomerDivisionID(Integer divisionID) {
        this.divisionID = divisionID;
    }

    /**
     * Customer First Level Division Name get function
     *
     * @return divisionID
     */
    public String getDivisionName() {
        return divisionName;
    }

}



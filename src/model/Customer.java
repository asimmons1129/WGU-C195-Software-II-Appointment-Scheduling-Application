package model;

/**
 * Creates a customer
 * @author Andre Simmons
 */
public class Customer {
    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private int divisionId;
    private String divisionName;

    /**
     * Constructor for each customer
     * @param i
     * @param n
     * @param a
     * @param pc
     * @param p
     * @param di
     * @param dn
     */
    public Customer(int i, String n, String a, String pc, String p, int di, String dn){
        id = i;
        name = n;
        address = a;
        postalCode = pc;
        phone = p;
        divisionId = di;
        divisionName = dn;
    }
    /*
    getter and setter functions
     */
    public void setId(int i){id = i;}

    public int getId(){return id;}

    public void setName(String n){name = n;}

    public String getName(){return name;}

    public void setAddress(String a){address = a;}

    public String getAddress(){return address;}

    public void setPostalCode(String pc){postalCode = pc;}

    public String getPostalCode(){return postalCode;}

    public void setPhone(String p){phone = p;}

    public String getPhone(){return phone;}

    public void setDivisionId(int di){divisionId = di;}

    public int getDivisionId(){return divisionId;}

    public void setDivisionName(String dn){divisionName = dn;}

    public String getDivisionName(){return divisionName;}
}

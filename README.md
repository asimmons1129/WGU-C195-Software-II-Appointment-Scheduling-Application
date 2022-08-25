# WGU-C195-Software-II-Appointment-Scheduling-Application


Project Title

C195_Appointment_Project

Purpose of Application:

The purpose of this application is to build a fully functioning GUI based appointment scheduling application containing multiple functionalities

Author Information:

Author: Andre Simmons
Contact Info: asim435@wgu.edu
Application Version: 1.0
Date: 8/24/22

IDE Information:

IntelliJ IDEA 2021.3 (Community Edition)
Build #IC-213.5744.223, built on November 27, 2021
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o
JDK-11.0.8.jdk
JavaFX-SDK-17.0.1

MySQL Connector Driver:

mysql-connector-java-8.0.28

How To Run:

First, install all proper IDE and javafx software.  Once installed, open the ‘C195_Appointment_Project.iml’ file in IntelliJ.  Click the run button to start the application.  Once the login page appears, enter these credentials: 

username: test
password: test

The login page can be translated to French based on the local machine’s language settings.  However, there’s also an optional dropdown to select the language in order to avoid doing a system restart when changing language settings.  Once logged in, the main menu lets the user choose between ‘Appointments’, ‘Customers’, ‘Reports’, and ‘Exit’.  Appointments takes the user to the appointment page where the user is able to add, edit, or delete appointments.  Appointments can also be filtered by current month or current week.  Customers takes the user to the customer records page where they’re able to add, edit or delete customers.  Reports takes the user to the report page where the user can view the appointments for each contact, total appointments by type/month, and a custom report.  Users can exit the app by clicking the exit button.

Additional Report:

For the custom report, I chose to filter customers by first level divisions.  The table displays each first level division assigned to a customer on the left and the total customers with that first level division on the right.  This was accomplished through an SQL query that performed an inner join where the ‘Customers’ table and ‘FLDivisions’ table had the same division ID and tallied how many customers had each similar division ID assigned.  This report is a good way to look at which areas have the most customers.

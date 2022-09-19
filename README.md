# WGU-C195-Software-II-Appointment-Scheduling-Application

Purpose of Application:

The purpose of this application is to build a fully functioning GUI based appointment scheduling application containing multiple functionalities

Author Information:

Author: Andre Simmons

Application Version: 1.0

Date: 8/24/22


IDE Information:

IntelliJ IDEA 2021.3 (Community Edition)
Build #IC-213.5744.223, built on November 27, 2021
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o
JDK-17.0.1
JavaFX-SDK-17.0.1

MySQL Connector Driver:

mysql-connector-java-8.0.28

How To Run:

First, install all proper IDE and javafx software.  Once installed, open the project in IntelliJ.  Click the run button to start the application.  Once the login page appears, enter credentials based on the information in the users table from the database.

The login page can be translated to French based on the local machine’s language settings. Once logged in, the main console loads where the user can view both the appointments and the customers table.  Each button takes the user to the appropriate page.  Customers and appointments can be added, deleted or updated.  There are error checks throughout the application.  There are also radio buttons to filter the appointments and the reports button takes the user to the reports page.  The logout button takes the user back to the login screen.

Additional Report:

For the custom report, I chose to filter customers by first level divisions.  The table displays each first level division assigned to a customer on the left and the total customers with that first level division on the right.  This was accomplished through an SQL query that performed an inner join where the ‘Customers’ table and ‘FLDivisions’ table had the same division ID and tallied how many customers had each similar division ID assigned.  This report is a good way to look at which areas have the most customers.


Video Demonstration:

[![Appointment-Scheduler](https://img.youtube.com/watch?v=5qBSd9T3lUM.jpg)](https:/https://www.youtube.com/watch?v=5qBSd9T3lUM)

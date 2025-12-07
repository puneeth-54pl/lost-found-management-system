# Lost & Found Management System

A complete web application for managing lost and found items, built with Java JSP, Servlets, JDBC, and MySQL.

## Features
- **User Authentication**: Register, Login, Logout.
- **Post Items**: Users can post lost or found items.
- **Search**: Search items by keyword, category, and location.
- **Admin Dashboard**: Approve or reject posted items.
- **User Dashboard**: Manage your own posts.
- **Responsive Design**: Built with Bootstrap 5.

## Prerequisites
- Java Development Kit (JDK) 8 or higher.
- Apache Tomcat 8.5 or higher.
- MySQL Server.

## Database Setup
1. Open your MySQL Client (Workbench or Command Line).
2. Run the script located at `db/schema.sql`.
   - This will create the database `lostfound_db`, tables, and seed data.
   - **Default Admin Credentials**:
     - Username: `admin`
     - Password: `admin123`

## Configuration
- **Database Connection**: Check `src/main/java/com/lostfound/util/DBConnection.java`.
  - Default URL: `jdbc:mysql://localhost:3306/lostfound_db`
  - Default User: `root`
  - Default Password: `password` (Change this to match your local MySQL password).

## How to Run
1. **Compile**: Open the project in your IDE (Eclipse/IntelliJ) or compile using command line.
2. **Deploy**:
   - if using an IDE, add the project to the Tomcat Server and start it.
   - If using manual deployment, compile the java classes to `WEB-INF/classes` and copy the `src/main/webapp` content to `Tomcat/webapps/lostfound`.
3. **Access**: Open your browser and go to `http://localhost:8080/lostfound` (or your configured context path).

## Project Structure
- `src/main/java`: Java source files (Models, DAOs, Controllers).
- `src/main/webapp`: JSP views, CSS, and WEB-INF configuration.
- `db`: SQL scripts.

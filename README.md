# Lost & Found Management System

A comprehensive web application for managing lost and found items with advanced security features and user-friendly functionality. Built with Java JSP, Servlets, JDBC, and MySQL.

## ✨ Features

### 🔐 Security Features
- **Secure Authentication**: SHA-256 password hashing with salt for maximum security
- **Input Validation**: XSS protection and comprehensive input sanitization
- **Access Control**: Proper authorization checks for all user actions
- **Secure File Uploads**: Validated image uploads with type and size restrictions

### 📝 Core Functionality
- **User Authentication**: Secure register, login, logout with hashed passwords
- **Post Items**: Users can post lost or found items with optional images
- **Advanced Search**: Search items by keyword, category, location, and date
- **Image Upload**: Support for JPEG, PNG, GIF, and WebP images (up to 10MB)
- **Mark as Found**: One-click resolution for found lost items
- **Claim System**: Users can claim found items with admin approval workflow

### 👨‍💼 Admin Features
- **Admin Dashboard**: Approve or reject posted items
- **User Management**: Monitor user activities and claims
- **Content Moderation**: Review and manage all posted content
- **Claim Management**: Process and approve/reject item claims

### 👤 User Features
- **User Dashboard**: Manage personal posts and track claims
- **Smart Matching**: Automatic potential match suggestions
- **Image Display**: Visual item identification with uploaded photos
- **Status Tracking**: Clear status indicators for all items
- **Responsive Design**: Mobile-friendly interface built with Bootstrap 5

## Prerequisites
- Java Development Kit (JDK) 8 or higher.
- Apache Tomcat 8.5 or higher.
- MySQL Server.

## Database Setup
1. Open your MySQL Client (Workbench or Command Line).
2. Run the script located at `db/schema_v2.sql` (recommended) or `db/schema.sql`.
   - This will create the database `lostfound_db`, tables, and seed data.
   - **Default Admin Credentials**:
     - Username: `admin`
     - Password: `admin123`
3. **Important**: The system now uses SHA-256 password hashing. Existing users may need to reset passwords.

## Configuration
- **Database Connection**: Check `src/main/java/com/lostfound/util/DBConnection.java`.
  - Default URL: `jdbc:mysql://localhost:3306/lostfound_db`
  - Default User: `root`
  - Default Password: `mysql` (Change this to match your local MySQL password).
- **File Uploads**: Images are stored in `src/main/webapp/uploads/items/`
- **Security**: Passwords are automatically hashed using SHA-256 with salt

## How to Run
1. **Compile**: Open the project in your IDE (Eclipse/IntelliJ) or compile using command line.
2. **Deploy**:
   - if using an IDE, add the project to the Tomcat Server and start it.
   - If using manual deployment, compile the java classes to `WEB-INF/classes` and copy the `src/main/webapp` content to `Tomcat/webapps/lostfound`.
3. **Access**: Open your browser and go to `http://localhost:8080/lostfound` (or your configured context path).

## 🚀 How to Use

### For Users:
1. **Register/Login**: Create account or login with existing credentials
2. **Post Items**: Click "Post an Item" to report lost or found items with optional images
3. **Search Items**: Use the search bar and filters to find items
4. **Manage Posts**: Use your dashboard to mark found items or delete posts
5. **Claim Items**: Click "Claim This Item" on found items that belong to you

### For Admins:
1. **Login**: Use admin credentials (admin/admin123)
2. **Review Posts**: Check pending items in the admin dashboard
3. **Approve/Reject**: Approve legitimate posts, reject spam/inappropriate content
4. **Manage Claims**: Review and process item claims from users

### New Features:
- **Image Upload**: Attach photos to your posts for better identification
- **Mark as Found**: Found your lost item? Click "Found" button instead of deleting
- **Secure Login**: All passwords are encrypted and secure
- **Smart Matching**: System suggests potential matches for your posts

## 🏗️ Project Structure
```
src/main/java/com/lostfound/
├── controller/          # Servlet controllers (AuthServlet, ItemServlet, etc.)
├── dao/                # Data Access Objects (UserDAO, ItemDAO, etc.)
├── model/              # JavaBeans (User, Item, Category, etc.)
└── util/               # Utility classes (DBConnection, PasswordUtil)

src/main/webapp/
├── uploads/items/      # Uploaded item images
├── css/                # Stylesheets
├── *.jsp               # JSP view files
└── WEB-INF/            # Configuration files

db/                     # Database schema files
```

## 🛡️ Security Features
- **Password Hashing**: SHA-256 with random salt
- **Input Validation**: XSS protection and SQL injection prevention
- **File Upload Security**: Type and size validation for images
- **Access Control**: Proper authorization for all operations
- **Session Management**: Secure session handling

## 📊 Database Schema
The application uses MySQL with the following main tables:
- `users`: User accounts with secure password storage
- `items`: Lost/found item posts with image support
- `categories`: Item categories (Electronics, Documents, etc.)
- `locations`: Campus locations
- `claims`: Item claim requests
- `matches`: Potential item matches

## 🔄 Recent Updates
- ✅ **Security Enhancement**: Implemented SHA-256 password hashing
- ✅ **Image Upload**: Added support for item photos with validation
- ✅ **Mark as Found**: One-click resolution for found items
- ✅ **Bug Fixes**: Fixed authorization vulnerabilities
- ✅ **UI Improvements**: Enhanced dashboard with better UX
- ✅ **Input Validation**: Added comprehensive security checks

## 🤝 Contributing
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License
This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments
- Built with Java EE technologies
- UI powered by Bootstrap 5
- Icons from Font Awesome
- Secure hashing implementation using Java Security API

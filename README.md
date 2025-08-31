# 📚 Bookstore Management System

A comprehensive enterprise-level bookstore management system built with Java EE, featuring multi-role authentication, inventory management, customer loyalty programs, and automated billing with PDF generation and email integration.

## 🌟 Features

### Authentication & Security
- **Multi-role authentication system** (Admin, Manager, Staff)
- **Session management** with secure token generation
- **Brute force protection** with account lockout
- **Remember Me functionality** for user convenience
- **URL-level access control** with role-based permissions
- **Complete activity logging** for audit trails

### Core Business Operations
- **Inventory Management**: Complete book CRUD operations with ISBN validation
- **Customer Relationship Management**: Client profiles with loyalty tracking
- **Advanced Billing System**: Invoice creation with real-time calculations
- **Loyalty Program**: Automatic tier management (Silver, Gold, Platinum)
- **Automated Email Integration**: Professional invoice emails
- **PDF Generation**: Branded invoice PDFs with logo support

### Advanced Search & Analytics
- **Multi-criteria search** across books, clients, and invoices
- **Real-time statistics** and dashboard analytics
- **Profit margin calculations** for inventory management
- **Customer tier analytics** with loyalty insights

## 🏗️ Architecture

### Design Patterns
- **MVC Architecture**: Clean separation of concerns
- **Command Pattern**: CRUD operations with transaction safety
- **DAO Pattern**: Database abstraction layer
- **Service Layer**: Business logic encapsulation
- **Factory Pattern**: Object creation management

### Technology Stack
- **Backend**: Java EE 8, Servlets, JSP
- **Database**: MySQL with JDBC connectivity
- **Security**: BCrypt password hashing, Session management
- **PDF Generation**: iText library for professional invoices
- **Email**: JavaMail API for automated notifications
- **Build Tool**: Maven for dependency management

## 🚀 Getting Started

### Prerequisites
- Java 8 or higher
- Apache Tomcat 10.1+
- MySQL 5.7+


### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/sajeewa1100/Bookstore-Billing-System---Pahana-Edu
   cd Bookstore-Billing-System---Pahana-Edu
   ```

2. **Database Setup**
   ```sql
   CREATE DATABASE pahana_bookstore;
   -- Import the provided SQL schema file
   mysql -u root -p pahana_bookstore < database/pahana_bookstore.sql
   ```

3. **Configure Database Connection**
   ```properties
   # src/main/resources/database.properties
   db.url=jdbc:mysql://localhost:3306/pahana_bookstore
   db.username=your_username
   db.password=your_password
   ```

4. **Email Configuration** (Optional)
   ```java
   // In EmailService.java
   private static final String SMTP_HOST = "smtp.gmail.com";
   private static final String EMAIL_USERNAME = "your-email@gmail.com";
   private static final String EMAIL_PASSWORD = "your-app-password";
   ```

5. **Build and Deploy**
   ```bash
   mvn clean compile
   # Deploy the WAR file to Tomcat webapps directory
   ```

6. **Access the Application**
   ```
   URL: http://localhost:8080/Bookstore-Billing-System-PahanaEdu/
   ```

## 👥 Default User Accounts

| Role | Username | Password | Access Level |
|------|----------|----------|--------------|
| Admin | admin | admin123 | Full system access |
| Manager | manager | manager123 | Staff + invoice management |
| Staff | staff001 | staff123 | Billing and customer operations |

## 📖 Usage Guide

### For Staff Users
1. **Login** with staff credentials
2. **Create invoices** for walk-in or registered customers
3. **Search books** by ISBN, title, or author
4. **Manage client** information and loyalty status
5. **Print invoices** and send automated emails

### For Managers
1. **Monitor sales** through dashboard analytics
2. **Manage staff** accounts and permissions
3. **View all invoices** across the system
4. **Generate reports** on customer loyalty and sales

### For Administrators
1. **Manage user accounts** and system settings
2. **Configure loyalty** program parameters
3. **Monitor system** activity and security logs
4. **Backup and maintain** system data

## 🔧 Key Components

### Security Features
```java
// Session Management
SessionService.createSession(user, clientIP, userAgent);

// Password Protection
PasswordUtils.hashPassword(password);
PasswordUtils.verifyPassword(plainPassword, hashedPassword);

// Access Control
AuthorizationUtil.hasManagerAccess(request);
```

### Business Logic
```java
// Automatic Loyalty Calculation
loyaltySettingsService.calculateClientTier(loyaltyPoints);
billingService.calculateInvoice(items, client);

// Email Integration
emailService.sendInvoiceEmail(invoice, client);
```

### Data Management
```java
// Command Pattern Implementation
BillingCommand createCommand = commandFactory.createCommand("CREATE_INVOICE", invoice);
CommandResult result = createCommand.execute();
```

## 📊 System Demonstration

### Complete Feature Workflow
1. **User Authentication**: Secure login with role-based redirection
2. **Inventory Management**: Add books with automatic validation
3. **Customer Registration**: Create client profiles with loyalty tracking
4. **Invoice Processing**: Real-time calculations with discount application
5. **Automated Services**: PDF generation and email dispatch
6. **Loyalty Management**: Automatic tier updates based on purchases

## 🛠️ Development

### Project Structure
```
src/
├── main/
│   ├── java/
│   │   ├── controller/     # Servlets and request handling
│   │   ├── service/        # Business logic layer
│   │   ├── dao/           # Database access objects
│   │   ├── model/         # Data transfer objects
│   │   ├── command/       # Command pattern implementation
│   │   └── util/          # Utility classes
│   ├── resources/         # Configuration files
│   └── webapp/
│       ├── views/         # JSP pages
│       ├── assets/        # CSS, JS, images
│       └── WEB-INF/       # Web configuration
```

### Testing
- **Unit tests** for service layer components
- **Integration tests** for database operations
- **Security tests** for authentication flows
- **Business logic validation** for loyalty calculations

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🎓 Academic Information

**Course**: Advanced Software Engineering  
**Institution**: [Your University Name]  
**Semester**: [Current Semester]  
**Student**: [Your Name]  
**Student ID**: [Your ID]

## 🔮 Future Enhancements

- [ ] RESTful API development for mobile integration
- [ ] Advanced reporting with charts and graphs
- [ ] Barcode scanning for inventory management
- [ ] Multi-store support with centralized management
- [ ] Integration with accounting software
- [ ] Real-time inventory alerts and notifications

## 📞 Support

For any questions or issues, please contact:
- **Email**: [pathumpc1100@gmail.com]
- **GitHub Issues**: [Repository Issues Page]

---

**⭐ If you find this project helpful, please consider giving it a star!**
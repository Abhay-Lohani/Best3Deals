# Table of Contents
1. [Introduction](#introduction)
2. [Project Structure](#project-structure)
3. [Dependencies](#dependencies)
4. [Build/Deployment Instructions](#builddeployment-instructions)
5. [Usage Scenarios](#usage-scenarios)
6. [Development Approach](#development-approach)
7. [Design Principles](#design-principles)
8. [API Documentation](#api-documentation)
9. [Code Coverage & Smells Analysis](#code-coverage--smells-analysis)
10. [Test-Driven Development (TDD)](#test-driven-development-tdd)
11. [Code Coverage](#code-coverage)


# 1. Introduction 

Best3 Deals is a comprehensive online shopping aggregator platform that helps users find the best three deals in their region. The platform consists of three main components: a backend service, a customer-facing Flutter application, and a seller management Flutter application.

## Features
- **Regional Deal Discovery**: Shows the best 3 deals based on user's location
- **Multi-Platform Support**: Available on both iOS and Android
- **Seller Management**: Dedicated platform for sellers to manage their products and stores
- **Real-time Updates**: Dynamic deal updates and price tracking
- **User Authentication**: Secure login and registration system
- **Location-based Services**: Personalized deals based on user's region
- **Social Features**: User reviews, posts, and comments on stores and products
- **Push Notifications**: Real-time updates for deals and interactions

# 2. Project Structure 

The project consists of three main components:

## 1. Backend Service (`/backend`)
- RESTful API implementation with Spring Boot 3.4.2
- MySQL 8.0 database management with JPA
- JWT-based authentication and authorization
- Deal aggregation logic with location-based filtering
- AWS S3 integration for file storage
- Email notification service
- Social features management
- Swagger UI for API documentation

## 2. Customer Application (`/best-3-deals-frontend`)
- Flutter-based mobile application
- User interface for browsing deals
- Location-based deal filtering
- User authentication
- Deal comparison features
- Social features (posts, reviews, comments)
- Push notification system
- Store and product reviews

## 3. Seller Application (`/SellerApp`)
- Flutter-based seller management platform
- Product management interface
- Store management
- Flyer creation and management
- Deal creation and management

# 3. Dependencies

## Backend Requirements
- **Java 17** or higher  
  Download Link: [Oracle JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) | [OpenJDK](https://jdk.java.net/17/)
- **Spring Boot** (v3.4.2)  
  Download Link: [Spring Boot](https://spring.io/projects/spring-boot)
- **MySQL** (v8.0 or higher)  
  Download Link: [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)
- **Maven** for dependency management  
  Download Link: [Apache Maven](https://maven.apache.org/download.cgi)

## Core Dependencies
| Dependency | Version | Purpose |
|------------|---------|---------|
| `spring-boot-starter-web` | 3.4.2 | REST API development |
| `spring-boot-starter-data-jpa` | 3.4.2 | Database ORM |
| `spring-boot-starter-security` | 3.4.2 | Authentication & Authorization |
| `spring-boot-starter-mail` | 3.4.2 | Email notifications |
| `spring-boot-starter-validation` | 3.4.2 | Request validation |

## Database
| Dependency | Version | Purpose |
|------------|---------|---------|
| `mysql-connector-j` | 8.0+ | MySQL JDBC driver |

## Security
| Dependency | Version | Purpose |
|------------|---------|---------|
| `jjwt-api` | 0.11.5 | JWT token creation |
| `jjwt-impl` | 0.11.5 | JWT implementation |
| `jjwt-jackson` | 0.11.5 | JWT JSON support |
| `spring-security-oauth2-jose` | 6.1+ | JWT encoding/decoding |

## Cloud Services
| Dependency | Version | Purpose |
|------------|---------|---------|
| `aws-java-sdk-s3` | 1.12.777 | AWS S3 file storage |

## Development Tools
| Dependency | Version | Purpose |
|------------|---------|---------|
| `spring-boot-devtools` | 3.4.2 | Developer tools |
| `lombok` | 1.18.30 | Code reduction |
| `springdoc-openapi-starter-webmvc-ui` | 2.4.0 | API documentation |

## Testing
| Dependency | Version | Purpose |
|------------|---------|---------|
| `spring-boot-starter-test` | 3.4.2 | Core testing |
| `spring-security-test` | 6.1+ | Security testing |
| `junit` | 4.13.2 | Unit testing |

---

## Flutter Applications Requirements
- **Flutter SDK** (v3.5.0 or higher)  
  Download Link: [Flutter SDK](https://docs.flutter.dev/get-started/install)
- **Dart** (v3.5.0 or higher)  
  Download Link: [Dart SDK](https://dart.dev/get-dart)
- **Android Studio** / **VS Code** with Flutter extensions  
  Download Links: [Android Studio](https://developer.android.com/studio) | [VS Code](https://code.visualstudio.com/)

### Customer App Dependencies
```yaml
dependencies:
  flutter:
    sdk: flutter
  cupertino_icons: ^1.0.8
  flutter_native_splash: ^2.4.4
  http: ^1.3.0
  sqflite: 2.3.3+2
  path: ^1.9.0
  geolocator: 12.0.0
  geocoding: ^3.0.0
  shared_preferences: 2.5.2
  url_launcher: ^6.3.1
  google_maps_flutter: ^2.10.0
  image_picker: ^1.1.2
  firebase_core: ^2.0.0
  firebase_messaging: ^14.0.0
  flutter_local_notifications: ^17.2.1
  provider: ^6.1.2
  carousel_slider: 5.0.0

```

### Seller App Dependencies
```yaml
dependencies:
  flutter:
    sdk: flutter
  flutter_native_splash: ^2.4.4
  sqflite: 2.3.3+2
  http: ^1.3.0
  path: ^1.9.0
  geolocator: 12.0.0
  geocoding: ^3.0.0
  shared_preferences: 2.5.2
  url_launcher: ^6.3.1
  image_picker: ^1.1.2
  cupertino_icons: ^1.0.8

```

### Key Features Enabled by Dependencies
- **Location Services**: `geolocator` and `geocoding` for location-based features
- **Local Storage**: `sqflite` and `shared_preferences` for data persistence
- **Network Communication**: `http` for API calls
- **Authentication**: Firebase services for user authentication
- **Push Notifications**: `firebase_messaging` and `flutter_local_notifications`
- **UI Components**: `carousel_slider` for deal carousels
- **State Management**: `provider` for app state management
- **Image Handling**: `image_picker` for profile and product images
- **Maps Integration**: `google_maps_flutter` for location visualization
- **Deep Linking**: `url_launcher` for external links

# 4. Build/Deployment Instructions

## Initial Setup
1. Clone the repository:
```bash
git clone https://git.cs.dal.ca/courses/2025-winter/csci-5308/group05.git
```
```bash
cd group05
```


## Backend Setup
1. Navigate to the backend directory:
```bash
cd backend
```

2. Configure the application:
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://your-database-host:3306/your-database-name
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=your-database-username
spring.datasource.password=your-database-password
spring.jpa.hibernate.ddl-auto=update  

# JWT Configuration
jwt.secret=your-256-bit-secret
jwt.expiration=86400000 # 24 hours  

# AWS S3 Configuration
aws.s3.bucket-name=your-bucket-name
aws.access-key=your-access-key
aws.secret-key=your-secret-key
aws.region=your-region  

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

```

3. Install dependencies:
```bash
mvn clean install
```

4. Start the server:
```bash
mvn spring-boot:run
```

## API Documentation:
```
http://localhost:8080/swagger-ui.html
```


## Customer App Setup
1. Navigate to the frontend directory:
```bash
cd best-3-deals-frontend
```

2. Install Flutter dependencies:
```bash
flutter pub get
```

3. Run the application:
```bash
flutter run
```

## Seller App Setup
1. Navigate to the seller app directory:
```bash
cd SellerApp
```

2. Install Flutter dependencies:
```bash
flutter pub get
```

3. Run the application:
```bash
flutter run
```

# 5. Usage Scenarios 

## Customer Application

1. **User Registration/Login**
   - Create a new account or login with existing credentials  
     
     <img src="https://drive.google.com/uc?export=view&id=1lnf9aZkPUhh46M955PbN92EMZVgjzT5x" alt="User Registration/Login Screenshot" width="300"/>
     
   - Enable location services for personalized deals
          
   - Set up notification preferences  
     
     <img src="https://drive.google.com/uc?export=view&id=1O94xRNEwWSaMYqsBimupIGZHqlgHUrkF" alt="Notification Preferences Screenshot" width="300"/>

2. **Browse Deals**
   - View best 3 deals in your region  
     
     <img src="https://drive.google.com/uc?export=view&id=1O9RSDxoI2foyqjuKKzOwn1ohRDkySXk_" alt="View Best 3 Deals Screenshot" width="300"/>
     
   - Filter deals by location distance  
     
     <img src="https://drive.google.com/uc?export=view&id=1Xyfi_EzN7PqtNFi0UqFnlyVVX09QqLZx" alt="Filter Deals Screenshot" width="300"/>
     
   - Compare deals from different sellers  
     
     <img src="https://drive.google.com/uc?export=view&id=1O9RSDxoI2foyqjuKKzOwn1ohRDkySXk_" alt="Compare Deals Screenshot" width="300"/>
     
   - View store flyers  
     
     <img src="https://drive.google.com/uc?export=view&id=1J5TPwB8LtiR_oE4sCDJ0yPiFQ0kF90tb" alt="Store Flyers Screenshot" width="300"/>

3. **Deal Interaction**
   - Save favorite deals  
     
     <img src="https://drive.google.com/uc?export=view&id=1oymBQBTnInsy8nQ0jJfx3diYCrg6EAJ5" alt="Save Favorite Deals Screenshot" width="300"/>
     
   - Track price changes  
     
4. **Social Features**
   - Create posts about stores or products  
     
     <img src="https://drive.google.com/uc?export=view&id=1n1iVgoS_2mT4PnPP5OBSvV0ld7J8zPhw" alt="Create Posts Screenshot" width="300"/>
     
   - Share reviews and ratings  
     
     <img src="https://drive.google.com/uc?export=view&id=1IR5jDGZYJYlt4quh5ZzaZwgd6y8pXVRO" alt="Share Reviews Screenshot" width="300"/>
     
   - Comment on other users' posts  
     
     <img src="https://drive.google.com/uc?export=view&id=1Sl-DOfxWsJ7VhT1l1p-CrhIVtH2D8vwU" alt="Comment on Posts Screenshot" width="300"/>

5. **Notifications**
   - Receive deal alerts  
     
     <img src="https://drive.google.com/uc?export=view&id=1OETbjhiOUOPRAEujus3GLwZlymXq-38K" alt="Deal Alerts Screenshot" width="300"/>
     
   - Stay updated with store announcements  
        
   - Customize notification preferences  
     
     <img src="https://drive.google.com/uc?export=view&id=1O94xRNEwWSaMYqsBimupIGZHqlgHUrkF" alt="Customize Notification Preferences Screenshot" width="300"/>

## Seller Application

1. **Store Management**
   - Create and manage store profiles  
     
     <img src="https://drive.google.com/uc?export=view&id=1S5kSiBjTPblDzFE4jJEqn7Xtv6GVZ10V" alt="Manage Store Profiles Screenshot" width="300"/>
     
   - Set store location and details  
     
     <img src="https://drive.google.com/uc?export=view&id=1w2wo_Vx9hpZ88n15FKTQMPuK1-h35V8i" alt="Set Store Location Screenshot" width="300"/>
     
   - Manage store information  
     
     <img src="https://drive.google.com/uc?export=view&id=1ImOEksX0LrI7XRcZAyfMC8QXMrxG7BsA" alt="Manage Store Information Screenshot" width="300"/>
    

2. **Product Management**
   - Add new products  
     
     <img src="https://drive.google.com/uc?export=view&id=1Lv-fEKP1oxoXZhvNm1h0ib5slVtbzSN1" alt="Add New Products Screenshot" width="300"/>
     
   - Update product details  
     
     <img src="https://drive.google.com/uc?export=view&id=1tC6XTQg6aO8raR-QRxAn_rfGzF9E6egQ" alt="Update Product Details Screenshot" width="300"/>
     
   - Set pricing and discounts  
          
   - Manage product inventory  
     
     <img src="https://drive.google.com/uc?export=view&id=1417jjAKmUmX5g-7OszrqK2cBfkffGi7G" alt="Manage Product Inventory Screenshot" width="300"/>

3. **Flyer Management**
   - Create and edit digital flyers  
     
     <img src="https://drive.google.com/uc?export=view&id=1ZGpHhGaU8dCFkpV9E4BhW3DrJBRuVFoT" alt="Create Digital Flyers Screenshot" width="300"/>
     
   - Manage multiple flyers  
     
     <img src="https://drive.google.com/uc?export=view&id=1Tff_DbmMWn1kNxXQuvdEmanhOoh-ZD5h" alt="Manage Multiple Flyers Screenshot" width="300"/>

4. **Deal Management**
   - Create new deals  
     
     <img src="https://drive.google.com/uc?export=view&id=1CsS4p9y25Lh_f4qYTGK7qaELUZIOsEZG" alt="Create New Deals Screenshot" width="300"/>
     
   - Manage active deals  
     
     <img src="https://drive.google.com/uc?export=view&id=1loC5j29wJKvQ0VOGHhhMK8gjuLda0O1s" alt="Manage Active Deals Screenshot" width="300"/>

   **[Screenshots of the Seller app and customer app](https://drive.google.com/drive/folders/1Wim0O33YpcjMW_mL7N_-DA9OitUmJELE?usp=sharing)**

## Admin Portal

1. **User Management**
   - Add new users
   - Delete existing users
   - Review user lists for account verification and compliance

2. **Flyer Management**
   - Review all uploaded digital flyers
   - Delete flyers that do not meet guidelines or have expired

3. **Product Management**
   - Review product listings
   - Delete products as needed to ensure catalog accuracy

4. **Store Management**
   - Review store profiles and information
   - Delete stores that violate policies or are inactive

5. **Post Management**
   - Review user-generated posts
   - Delete posts that contain inappropriate content or spam

   **[Screenshots of the Admin portal](https://drive.google.com/drive/folders/1FLM23EjI0z3_eFyumGHzx7UXk1800FhM?usp=sharing)**

# 6. Development Approach

## Architecture
- **Backend**: 
  - Monolithic Architecture 
  - Spring Boot 3.4.2 with JPA
  - JWT-based authentication
  - AWS S3 for file storage
  - MySQL 8.0 database
- **Frontend**: Clean Architecture using efficient state management.
- **Database**: MySQL with JPA/Hibernate
- **Authentication**: JWT-based authentication with Spring Security

## Testing Strategy
- Unit tests for business logic
- Integration tests for API endpoints
- Widget tests for Flutter applications
- End-to-end testing for critical user flows

## Performance Optimization
### Backend
- Database query optimization
- Connection pooling
- Selective field retrieval
- Response compression

### Frontend
- State management optimization
- Image caching
- Lazy loading
- API response caching

## Security Implementation
### Backend
- JWT-based authentication
- Role-based access control
- Password encryption
- AWS S3 secure file storage
- Input validation and sanitization

### Frontend
- Secure token storage
- Input validation
- Secure file uploads

# 7. Design Principles

## SOLID Principles
### 1. Single Responsibility Principle
Each component has a specific purpose:
- `DatabaseHelper` class in Flutter app handles only database operations
- `DistanceCalculator` utility class focuses solely on calculating distances
- `ForgotPasswordService` manages only password reset functionality

Example from `DatabaseHelper.dart`:
```dart
class DatabaseHelper {
   static final DatabaseHelper _instance = DatabaseHelper._internal();
   static Database? _database;

   factory DatabaseHelper() {
      return _instance;
   }

   DatabaseHelper._internal();

   Future<Database> get database async {
      if (_database != null) return _database!;
      _database = await _initDatabase();
      return _database!;
   }
}
```

### 2. Open/Closed Principle
The application is designed to be open for extension but closed for modification:
- Repository interfaces extend `JpaRepository` for database operations
- `ApiService` class can be extended for new API endpoints without modifying existing code
- Exception handling system allows adding new exception types without changing existing handlers

Example from `GlobalExceptionHandler.java`:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
   @ExceptionHandler(NoSuchElementException.class)
   public ResponseEntity<ApiResponse<String>> handleNoSuchElementException(NoSuchElementException e) {
      ApiResponse<String> response = new ApiResponse<>(false, e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
   }
   // New exception handlers can be added without modifying existing ones
}
```

### 3. Liskov Substitution Principle
Repository interfaces can be used interchangeably:
```java
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {}
@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {}
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {}
```

### 4. Interface Segregation
- `ApiService` in Flutter app provides focused methods for different types of API calls
- Repository interfaces define only the methods they need
- Authentication service separates concerns between login, registration, and password reset

Example from `ApiService.dart`:
```dart
class ApiService {
   Future<Map<String, dynamic>> callApi({
      required String url,
      required Map<String, String> headers,
      required String body,
   }) async {
      // Implementation
   }
}
```

### 5. Dependency Inversion
- Services depend on repository interfaces rather than concrete implementations
- Authentication service uses dependency injection for required services
- Flutter app's database helper uses abstraction for database operations

Example from `ForgotPasswordService.java`:
```java
@Service
public class ForgotPasswordService {
   private final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder;

   public ForgotPasswordService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
      this.userRepository = userRepository;
      this.passwordEncoder = passwordEncoder;
   }
}
```

## Clean Architecture
### 1. Separation of Concerns
- Clear separation between data, domain, and presentation layers
- Repository pattern for data access
- Service layer for business logic
- DTOs for data transfer

Example from `ApiResponse.java`:
```java
@Data
public class ApiResponse<T> {
   private boolean success;
   private String message;
   private T data;

   public ApiResponse(boolean success, String message, T data) {
      this.success = success;
      this.message = message;
      this.data = data;
   }
}
```

### 2. Dependency Rule
- Dependencies point inward from outer layers to inner layers
- Domain entities don't depend on outer layers
- Services depend on repositories, not vice versa

## Security Principles
### 1. Authentication and Authorization
- JWT-based authentication with token validation
- Role-based access control
- Secure password handling with encryption

Example from `JwtAuthenticationFilter.java`:
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
   private final JwtService jwtService;
   private final UserDetailsService userDetailsService;

   @Override
   protected void doFilterInternal(
           @NonNull HttpServletRequest request,
           @NonNull HttpServletResponse response,
           @NonNull FilterChain filterChain
   ) throws ServletException, IOException {
      // Implementation
   }
}
```

## Performance Optimization
### 1. Caching and Database Optimization
- Singleton pattern for database connections
- Efficient query patterns in repositories
- Optimized API response structure

Example from `DatabaseHelper.dart`:
```dart
class DatabaseHelper {
   static final DatabaseHelper _instance = DatabaseHelper._internal();
   static Database? _database;

   factory DatabaseHelper() {
      return _instance;
   }
}
```

### 2. API Optimization
- Structured API responses with proper status codes
- Efficient error handling
- Optimized data transfer objects

Example from `ApiResponse.java`:
```java
public class ApiResponse<T> {
   private boolean success;
   private String message;
   private T data;

   public ApiResponse(boolean success, String message, T data) {
      this.success = success;
      this.message = message;
      this.data = data;
   }
}
```

# 8. API Documentation

Access Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

### Sample API Requests

#### **User Signup**
```http
POST /api/auth/signup
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "johndoe@example.com",
  "password": "securePassword123"
}
```

#### **User Login**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "johndoe@example.com",
  "password": "securePassword123"
}
```

#### **Fetch User Profile**
```http
GET /api/users/profile
Authorization: Bearer YOUR_JWT_TOKEN
```

# 9. Code Coverage & Smells Analysis

### **Checking Code Coverage**



### **Code Smells Before & After Refactoring**  

- [Before Refactor Code Smells Report](https://docs.google.com/spreadsheets/d/1qVdfPTqts2SOvIt0Ak8Qd8vj2_RhNKqrB1uKExjpHOo/edit?usp=sharing)  
- [After Refactor Code Smells Report](https://docs.google.com/spreadsheets/d/1VAvU48nThW_M9aY7JSvkdN9LRi2n15I5DGqG8fUBf-A/edit?usp=sharing)  


# 10. Test-Driven Development (TDD) 
This project follows Test-Driven Development with the following implementation order:

## 1. Feature: Add Profile Page Update API's
- **Test Commit:** [1c04e58accc3a99e8c3c82381dc92f81ed15ea32](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group05/-/merge_requests/37/diffs?commit_id=1c04e58accc3a99e8c3c82381dc92f81ed15ea32)  
  *Added tests to verify the profile page update API endpoints, ensuring that user profile updates are validated and persisted correctly.*

- **Implementation Commit:** [156c9bb3148025ccf952b2ee00f736a90aea7d35](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group05/-/merge_requests/37/diffs?commit_id=156c9bb3148025ccf952b2ee00f736a90aea7d35)  
  *Implemented the profile page update functionality by integrating the API endpoints with the UI and updating the local state accordingly.*

## 2. Feature: Admin Console
- **Test Commit:** [c44fbbd8c90c5dd9f483e9a39ef0bf97dbd80045](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group05/-/merge_requests/43/diffs?commit_id=c44fbbd8c90c5dd9f483e9a39ef0bf97dbd80045)  
  *Created tests to cover the admin console features, including role-based access and functionality verification.*

- **Implementation Commit:** [156c9bb3148025ccf952b2ee00f736a90aea7d35](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group05/-/merge_requests/37/diffs?commit_id=156c9bb3148025ccf952b2ee00f736a90aea7d35)  
  *Implemented the admin console features by integrating the necessary API calls and building the required UI components for admin users.*

## 3. Feature: Store Service
- **StoreServiceTest Test Commit:** [1dd42da8](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group05/-/commit/1dd42da8bae5ac8740a33573a14d9c3bc7058246)  
  *Added tests for the StoreService to validate store-related functionalities.*

- **StoreService Implementation Commit:** [4026e89e](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group05/-/commit/4026e89edb36e81b9b9ddc5402cb6dbe452783f9)  
  *Implemented the StoreService methods to support store management features.*

## 4. Feature: Store Product Price Comparison
- **Test Commit:** [72f7c489](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group05/-/commit/72f7c4893a0d7839f10c9b4bbd0984623f1e4ec5)  
  *Added tests for comparing product prices across stores.*

- **Implementation Commit:** [795a95c9](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group05/-/commit/795a95c9c96523b3aafba06f94fa30b2293854f3)  
  *Implemented the logic for store product price comparison.*

## 5. Feature: StoreProduct Service
- **Test Commit:** [ed1bd68b](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group05/-/commit/ed1bd68bde049ed911c80f0a20e525f313bee3c3)  
  *Added tests for the StoreProduct service to ensure correct processing of product details.*

- **Implementation Commit:** [7bfe8555](https://git.cs.dal.ca/courses/2025-winter/csci-5308/group05/-/commit/7bfe85558a0ab44b8a139ae80e890c050d164bbd)  
  *Implemented the StoreProduct service functionalities, including data manipulation and API integration.*


# 11. Code Coverage
We used [JaCoCo](https://www.eclemma.org/jacoco/) to generate a code coverage report for the project. The report shows a total coverage of **76%** across the codebase. For detailed information, you can view the report at the following path in the project folder:

path : group05/Code_coverage_report.html



<a id="readme-top"></a>

<!-- HEADER -->
# Price Comparator API

<!-- TABLE OF CONTENT -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li>
      <a href="#features">Features</a>
    </li>
    <li>
      <a href="#features">Project Structure</a>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li>
      <a href="#technologies-used">Technologies Used</a>
    </li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## About The Project

The **Price Comparator API** is a backend application that powers a service for comparing grocery prices across multiple supermarket chains such as Lidl, Kaufland, and Profi.

The goal of this project is to help users:
- Compare prices of everyday grocery items
- Track price changes over time
- Find the best deals
- Organize and manage shopping lists efficiently

The backend handles all core functionality including product data management, store and price tracking.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- FEATURES -->
## Features

- 🛒 Track products and their price history
- 🏷️ Compare prices across multiple supermarket chains
- 📈 Monitor price trends and fluctuations
- 📝 Create and manage shopping lists
- 📍 Support for locating the best prices nearby (future feature)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- PROJECT STRUCTURE -->
## Project Structure

### Main Package

`com.example.price_comparator`

### 1. config
Contains configuration for setting up the OpenAPI (Swagger) documentation

### 2. controller
Contains REST controllers handling HTTP requests.

### 3. dto
Handles Data Transfer Objects (DTO)

### 4. exception
Contains custom exceptions.

### 5. model
Contains the database models.

### 6.repository
Contains Data Access Object (DAO) interfaces for database interaction.

### 7. service
Contains business logic.

### 8. utils
Contains helper functions.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->
## Getting Started

Follow these steps to set up the project locally.

### Prerequisites

Make sure you have the following installed on your system:

- Java 17 or later
- Maven
- PostgreSQL (configured with a user and database)

### Installation

#### 1. Clone the Repository

```bash
git clone https://github.com/AlexBadita/PriceComparatorAPI.git
cd price-comparator
```

#### 2. Intall Dependencies

Use Maven to download and install all necessary dependencies:
```bash
mvn dependency:resolve
```

#### 3. Create and Configure PostgreSQL

Update the `application.properties` file (in src/main/resources/) with your database credentials:
```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update
```

#### 4. Build the Project

```bash
mvn clean install
```

#### 5. Run the Application

On the first run, use the `load-data` profile to create the necessary tables and populate them from CSV files:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=load-data"
```
This should only be done once, unless you need to reinitialize the database.

For subsequent runs, simply use:
```bash
mvn spring-boot:run
```
Alternatively, run PriceComparatorApplication.java directly from your IDE.

#### 6. [Optional] Access the Swagger UI
Once the application is running, open `http://localhost:8080/swagger-ui.html` in your browser to explore the API using Swagger.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

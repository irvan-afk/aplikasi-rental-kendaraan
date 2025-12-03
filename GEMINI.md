# Gemini Code Companion Context

This document provides a comprehensive overview of the Rental Kendaraan project, its structure, and its conventions to be used as a context for Gemini Code Companion.

## Project Overview

This project is a Java-based desktop application for managing a vehicle rental system. It provides a graphical user interface (GUI) for viewing, adding, updating, and deleting vehicle information.

**Key Technologies:**

*   **Language:** Java 17
*   **Build Tool:** Apache Maven
*   **User Interface:** Java Swing
*   **Database:** PostgreSQL
*   **Database Connection Pool:** HikariCP
*   **Testing:** JUnit 5

**Architecture:**

The application follows a classic layered architecture:

*   **Presentation Layer (GUI):** The `gui` package contains the main `RentalAppGui` class, which is a `JFrame` that provides the user interface.
*   **Service Layer:** The `service` package contains the `AdminService` class, which encapsulates the business logic of the application.
*   **Data Access Layer (DAO):** The `dao` package contains the `VehicleDAO` class, which is responsible for all database interactions related to vehicles. It uses a `ConnectionManager` to obtain database connections from a HikariCP connection pool.
*   **Domain Model:** The `vehicle` package contains the `Vehicle` base class and its subclasses (`Car`, `Motorcycle`, `Truck`). It also includes a `factory` subpackage for creating different types of vehicles.
*   **Database:** The `resources/script.sql` file defines the database schema for the application. It includes tables for `vehicles`, `customers`, and `rentals`.

## Building and Running

### Prerequisites

*   Java Development Kit (JDK) 17 or later
*   Apache Maven
*   A running PostgreSQL database server

### Database Setup

1.  Create a PostgreSQL database.
2.  Execute the `src/main/resources/script.sql` file to create the necessary tables.
3.  Configure the database connection in `src/main/java/db/ConnectionManager.java`. You will need to update the `URL`, `USER`, and `PASSWORD` constants with your database credentials.

### Build

To build the project and create an executable JAR file, run the following command from the project root directory:

```bash
mvn clean package
```

This will generate a `rental-kendaraan-1.0-jar-with-dependencies.jar` file in the `target` directory.

### Run

To run the application, execute the following command from the project root directory:

```bash
java -jar target/rental-kendaraan-1.0-jar-with-dependencies.jar
```

## Development Conventions

*   **Code Style:** The code generally follows standard Java conventions.
*   **Testing:** JUnit 5 is used for unit testing. Test classes are located in the `test` directory.
*   **Database Migrations:** Database schema changes should be managed through SQL scripts in the `src/main/resources` directory.
*   **Dependency Management:** Project dependencies are managed using Maven. Add new dependencies to the `pom.xml` file.
*   **GUI:** The user interface is built using Java Swing.

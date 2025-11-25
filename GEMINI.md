# Gemini Code Assistant Context: Vehicle Rental System

## Project Overview

This is a Java-based desktop application for managing a vehicle rental system. It features a graphical user interface (GUI) built with Swing.

The project is built using Apache Maven and connects to a PostgreSQL database for data persistence.

### Core Features:
*   **Vehicle Management:** Add, view, and delete vehicles (Cars, Motorcycles, Trucks).
*   **Database Interaction:** Uses a PostgreSQL database to store information about vehicles, customers, and rentals.
*   **GUI:** Provides a simple Swing-based interface for administrators to manage the vehicle inventory.

### Architecture:
*   **Language:** Java 17
*   **Build Tool:** Apache Maven
*   **Database:** PostgreSQL (with HikariCP for connection pooling)
*   **GUI:** Java Swing
*   **Design Patterns:** The codebase makes use of several design patterns, including:
    *   **Factory:** For creating different types of vehicles (`CarFactory`, `MotorcycleFactory`).
    *   **Data Access Object (DAO):** The `VehicleDAO` class encapsulates database operations.
    *   **Facade:** The `RentalServiceFacade` likely simplifies interactions with various rental subsystems.
    *   **Strategy:** The `pricing` package suggests different pricing strategies can be applied (e.g., `HourlyPricing`, `DailyPricing`).

## Building and Running

### 1. Database Setup

Before running the application, you need to set up a PostgreSQL database.

1.  Create a database (e.g., `rental_db`).
2.  Execute the SQL script to create the necessary tables. The script can be found at: `src/main/resources/script.sql`.
3.  Configure the database connection details (URL, username, password) in the `src/main/resources/config.properties` file.

### 2. Build the Project

This project uses Maven. You can build it using the standard Maven commands.

*   **Compile:**
    ```sh
    mvn compile
    ```
*   **Package into a JAR:**
    ```sh
    mvn package
    ```
    This will create an executable JAR file in the `target/` directory (e.g., `rental-kendaraan-1.0.jar`).

### 3. Run the Application

Once the project is packaged, you can run the GUI application from the command line:

```sh
java -jar target/rental-kendaraan-1.0.jar
```

## Development Conventions

*   **Testing:** The project is set up with JUnit 5 for unit testing. Test files are located in the `test/` directory. New features should be accompanied by corresponding tests.
*   **Code Style:** Follow the existing code style and formatting present in the project files.
*   **Database Schema:** Changes to the database schema should be reflected in the `src/main/resources/script.sql` file.

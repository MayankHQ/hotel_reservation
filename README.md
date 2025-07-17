# Hotel Reservation System 🏨

Welcome to the Hotel Reservation System, a Java-based application for managing hotel reservations efficiently. Whether you're running a small inn or a boutique hotel, this system simplifies the reservation process, enhances guest management, and keeps your business organized.

## Features 🌟

-   **Reserve a Room:** Easily make new reservations by providing guest details, room numbers, and contact information.
-   **View Reservations:** Get an overview of all current reservations, including guest names, room numbers, contact details, and reservation dates.
-   **Edit Reservation Details:** Update guest names, room numbers, and contact information for existing reservations.
-   **Delete Reservations:** Remove reservations that are no longer needed.

## Database Schema 🗄️

To set up the database, create a database named `hotel_db` and then create the `reservations` table using the following SQL script:

```sql
CREATE TABLE reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    guest_name VARCHAR(100) NOT NULL,
    room_number INT NOT NULL,
    contact_number VARCHAR(20),
    reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

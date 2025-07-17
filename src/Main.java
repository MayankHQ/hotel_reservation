import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";

    private static final String user = "root";

    private static final String password = "SmartManu17@";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(url, user, password);
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();
                switch(choice){
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, scanner);
                        break;
                    case 4:
                        updateReservation(connection, scanner);
                        break;
                    case 5:
                        deleteReservation(connection, scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice");
                }
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private static void reserveRoom(Connection connection, Scanner scanner){
        try{
            System.out.print("Enter guest name: ");
            String name = scanner.next();
            scanner.nextLine();
            System.out.print("Enter room number: ");
            int roomNumber = scanner.nextInt();
            System.out.print("Enter contact number: ");
            String contactNumber = scanner.next();

            String checkQuery = "SELECT * FROM reservations WHERE room_number = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, roomNumber);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Room already booked. Please select a different room.");
                    return;
                }
            }

            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number) VALUES (?, ?, ?)";

            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, name);
                statement.setInt(2, roomNumber);
                statement.setString(3, contactNumber);

                int affectedRow = statement.executeUpdate();
                if(affectedRow > 0){
                    System.out.println("Reservation successful.");
                }
                else{
                    System.out.println("Reservation failed.");
                }

            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void viewReservations(Connection connection) throws SQLException {
        String sql = "SELECT * FROM reservations";

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){

            System.out.println("Current Reservations: ");

            while(resultSet.next()){
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                System.out.println("Reservation ID: " + reservationId);
                System.out.println("Guest Name: " + guestName);
                System.out.println("Room Number: " + roomNumber);
                System.out.println("Contact Number: " + contactNumber);
                System.out.println("Reservation Date: " + reservationDate);
                System.out.println();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void getRoomNumber(Connection connection, Scanner scanner){
        try{
            System.out.print("Enter reservation ID: ");
            int reservationId = scanner.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();

            String sql = "SELECT room_number FROM reservations WHERE reservation_id = ? AND guest_name = ?";

            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, reservationId);
                statement.setString(2, guestName);

                try(ResultSet rs = statement.executeQuery()){
                    if(rs.next()){
                        int roomNumber = rs.getInt("room_number");
                        System.out.println("Room Number: " + roomNumber);
                    }
                    else{
                        System.out.println("Reservation not found for the givenID and guest name.");
                    }
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection connection, Scanner scanner){
        try{
            System.out.print("Enter reservation ID to update: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            if (!reservationExists(connection, id)) {
                System.out.println("Reservation ID not found.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String name = scanner.nextLine();
            System.out.print("Enter new room number: ");
            int room = scanner.nextInt();
            System.out.print("Enter new contact number: ");
            String contact = scanner.next();

            String sql = "UPDATE reservations SET guest_name = ?, room_number = ?, contact_number = ? WHERE reservation_id = ?";

            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, name);
                statement.setInt(2, room);
                statement.setString(3, contact);
                statement.setInt(4, id);

                int affectedRows = statement.executeUpdate();
                if(affectedRows > 0){
                    System.out.println("Reservation updated successful.");
                }else{
                    System.out.println("Reservation update failed.");
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection, Scanner scanner){
        try{
            System.out.print("Enter reservation ID to delete: ");
            int id = scanner.nextInt();

            if (!reservationExists(connection, id)) {
                System.out.println("Reservation ID not found.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = ?";

            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, id);
                int affectedRows = statement.executeUpdate();
                if(affectedRows > 0){
                    System.out.println("Reservation deleted successful.");
                }else{
                    System.out.println("Reservation delete failed.");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId){

        String sql = "SELECT * FROM reservations WHERE reservation_id = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, reservationId);
            try(ResultSet rs = statement.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException{
        System.out.print("Exiting System");
        int i = 3;
        while(i != 0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("Thank you for using the system.");
    }
}
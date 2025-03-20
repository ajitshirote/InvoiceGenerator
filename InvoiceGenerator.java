import java.sql.*;
import java.util.Scanner;

public class InvoiceGenerator {
    public static void main(String[] args) {
        // Setup JDBC connection
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/invoiceDB", "root", "password")) {
            Scanner scanner = new Scanner(System.in);

            // Collect invoice details
            System.out.print("Enter Invoice Number: ");
            String invoiceNumber = scanner.nextLine();

            System.out.print("Enter Customer Name: ");
            String customerName = scanner.nextLine();

            System.out.print("Enter Customer Address: ");
            String customerAddress = scanner.nextLine();

            // Create an invoice
            Invoice invoice = new Invoice(invoiceNumber, customerName, customerAddress, connection);

            // Add items to the invoice
            while (true) {
                System.out.print("\nEnter Item Name (or 'done' to finish): ");
                String itemName = scanner.nextLine();
                if (itemName.equalsIgnoreCase("done")) {
                    break;
                }

                System.out.print("Enter Quantity: ");
                int quantity = Integer.parseInt(scanner.nextLine());

                System.out.print("Enter Unit Price: ");
                double unitPrice = Double.parseDouble(scanner.nextLine());

                // Add item to invoice
                InvoiceItem item = new InvoiceItem(itemName, quantity, unitPrice);
                invoice.addItem(item);
            }

            // Save invoice to database
            invoice.saveToDatabase();

            // Print the invoice to console
            invoice.printInvoice();

            // Generate the invoice PDF
            invoice.generatePDF();

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

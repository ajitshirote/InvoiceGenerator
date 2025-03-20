import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.io.FileOutputStream;
import java.io.File;
public class Invoice {
    private String invoiceNumber;
    private String customerName;
    private String customerAddress;
    private List<InvoiceItem> items = new ArrayList<>();
    private Connection connection;

    public Invoice(String invoiceNumber, String customerName, String customerAddress, Connection connection) {
        this.invoiceNumber = invoiceNumber;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.connection = connection;
    }

    public void addItem(InvoiceItem item) {
        items.add(item);
    }

    public double getTotalAmount() {
        double total = 0;
        for (InvoiceItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public void saveToDatabase() throws SQLException {
        String insertInvoiceQuery = "INSERT INTO invoices (invoice_number, customer_name, customer_address, total_amount) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertInvoiceQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, invoiceNumber);
            pstmt.setString(2, customerName);
            pstmt.setString(3, customerAddress);
            pstmt.setDouble(4, getTotalAmount());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int invoiceId = generatedKeys.getInt(1);

                String insertItemQuery = "INSERT INTO invoice_items (invoice_id, item_name, quantity, unit_price) VALUES (?, ?, ?, ?)";
                try (PreparedStatement itemPstmt = connection.prepareStatement(insertItemQuery)) {
                    for (InvoiceItem item : items) {
                        itemPstmt.setInt(1, invoiceId);
                        itemPstmt.setString(2, item.getItemName());
                        itemPstmt.setInt(3, item.getQuantity());
                        itemPstmt.setDouble(4, item.getUnitPrice());
                        itemPstmt.addBatch();
                    }
                    itemPstmt.executeBatch();
                }
            }
        }
    }

    public void printInvoice() {
        System.out.println("Invoice Number: " + invoiceNumber);
        System.out.println("Customer: " + customerName);
        System.out.println("Address: " + customerAddress);
        System.out.println("\nItems:\n-----------------------------");

        for (InvoiceItem item : items) {
            System.out.println(item);
        }

        System.out.println("-----------------------------");
        System.out.println("Total Amount: $" + getTotalAmount());
    }

    public void generatePDF() throws Exception {
        // PDF Generation code using iText library
        // Print the current working directory
        System.out.println("Current Working Directory: " + System.getProperty("user.dir"));

        // Create the directory (optional)
        File dir = new File("pdf_output");
        if (!dir.exists()) {
            dir.mkdirs();  // Create the directory if it doesn't exist
        }

        // Specify the PDF file path (relative to the current working directory)
        String pdfPath = "pdf_output/"+invoiceNumber+".pdf";
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
        document.open();
        document.add(new com.itextpdf.text.Paragraph("Invoice Number: " + invoiceNumber));
        document.add(new com.itextpdf.text.Paragraph("Customer: " + customerName));
        document.add(new com.itextpdf.text.Paragraph("Address: " + customerAddress));
        document.add(new com.itextpdf.text.Paragraph("Items:"));

        for (InvoiceItem item : items) {
            document.add(new com.itextpdf.text.Paragraph(item.toString()));
        }

        document.add(new com.itextpdf.text.Paragraph("Total Amount: $" + getTotalAmount()));
        document.close();
    }
}

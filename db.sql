CREATE TABLE invoices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_address VARCHAR(255),
    total_amount DOUBLE NOT NULL,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE invoice_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id INT,
    item_name VARCHAR(100),
    quantity INT,
    unit_price DOUBLE,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);
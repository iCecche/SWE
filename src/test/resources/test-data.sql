INSERT INTO USERS (USERNAME, PASSWORD, ROLE) VALUES
    ('alessiaceccherini03@gmail.com', '$2a$12$jzfpOqxEFWLahwMGFqlua.anSojWt7x/IMB1mQ/FRch9ReVZdoN8m', 'USER'),
    ('user1@gmail.com', '$2b$12$0mRvM2iS9dqfr8A6YstWdOhR16gxJV97F73dL2RlK4.f8.akXiC7W', 'USER'),
    ('user2@gmail.com', '$2b$12$0mRvM2iS9dqfr8A6YstWdOhR16gxJV97F73dL2RlK4.f8.akXiC7W', 'USER'),
    ('user3@gmail.com', '$2b$12$0mRvM2iS9dqfr8A6YstWdOhR16gxJV97F73dL2RlK4.f8.akXiC7W', 'USER'),
    ('user4@gmail.com', '$2b$12$0mRvM2iS9dqfr8A6YstWdOhR16gxJV97F73dL2RlK4.f8.akXiC7W', 'USER'),
    ('user5@gmail.com', '$2b$12$0mRvM2iS9dqfr8A6YstWdOhR16gxJV97F73dL2RlK4.f8.akXiC7W', 'USER'),
    ('user6@gmail.com', '$2b$12$0mRvM2iS9dqfr8A6YstWdOhR16gxJV97F73dL2RlK4.f8.akXiC7W', 'USER'),
    ('user7@gmail.com', '$2b$12$0mRvM2iS9dqfr8A6YstWdOhR16gxJV97F73dL2RlK4.f8.akXiC7W', 'USER'),
    ('user8@gmail.com', '$2b$12$0mRvM2iS9dqfr8A6YstWdOhR16gxJV97F73dL2RlK4.f8.akXiC7W', 'USER'),
    ('admin', 'admin', 'ADMIN');

INSERT INTO USER_INFO (id, nome, cognome, indirizzo, cap, provincia, stato) VALUES
    (1, 'Marco', 'Rossi', 'Via Roma 10', '00100', 'RM', 'Italia'),
    (2, 'Laura', 'Bianchi', 'Corso Milano 25', '20100', 'MI', 'Italia'),
    (3, 'Luca', 'Verdi', 'Piazza Garibaldi 5', '80100', 'NA', 'Italia'),
    (4, 'Giulia', 'Neri', 'Viale Europa 50', '50100', 'FI', 'Italia'),
    (5, 'Matteo', 'Conti', 'Via Torino 15', '10100', 'TO', 'Italia'),
    (6, 'Sara', 'Gallo', 'Via Napoli 8', '80100', 'NA', 'Italia'),
    (7, 'Andrea', 'Ferri', 'Lungarno Mediceo 3', '56100', 'PI', 'Italia'),
    (8, 'Elisa', 'Romano', 'Via Venezia 12', '30100', 'VE', 'Italia'),
    (9, 'Davide', 'Greco', 'Corso Trieste 20', '00198', 'RM', 'Italia'),
    (10, 'Francesca', 'Martini', 'Via Bologna 14', '40100', 'BO', 'Italia');

INSERT INTO PRODUCT (NAME, DESCRIPTION, PRICE, STOCK_QUANTITY) VALUES
    ('Laptop', 'Laptop da gaming', 1200, 25),
    ('Smartphone', 'Smartphone Android', 800, 40),
    ('Cuffie Bluetooth', 'Audio alta qualità', 150, 100),
    ('Monitor 27"', 'Monitor IPS 2K', 300, 30),
    ('Mouse Wireless', 'Mouse ergonomico', 50, 80),
    ('Tastiera Meccanica', 'Tastiera RGB', 90, 60),
    ('SSD 1TB', 'Disco rigido veloce', 110, 70),
    ('Stampante Laser', 'Stampante professionale', 200, 20),
    ('Router WiFi 6', 'Router ad alte prestazioni', 130, 50),
    ('Webcam HD', 'Webcam Full HD', 70, 55);

INSERT INTO ORDERS (USER_ID, DATE) VALUES
    (1, '2025-04-01'), -- Mario compra 1 Laptop
    (2, '2025-04-02'), -- Luigi compra 2 Smartphone
    (3, '2025-04-03'), -- Anna compra 1 Cuffie Bluetooth
    (4, '2025-04-04'); -- Carla compra 3 Smartwatch


INSERT INTO ORDERS_DETAILS (ORDER_ID, PRODUCT_ID, QUANTITY) VALUES
    (1,1 ,2),
    (1, 5, 4),
    (2, 10, 1),
    (3, 2,10),
    (3, 3, 3),
    (3, 9, 25),
    (4, 4, 40);

INSERT INTO ORDERS_STATUS (ORDER_ID, DELIVERY_STATUS, PAYMENT_STATUS) VALUES
    (1, 'PENDING', 'PENDING'),
    (2, 'PENDING', 'PAID'),
    (3, 'DELIVERED', 'PAID'),
    (4, 'SHIPPED', 'PENDING');

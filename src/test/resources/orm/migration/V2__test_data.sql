-- V2__test_data.sql

INSERT INTO USERS (USERNAME, PASSWORD, ROLE)
VALUES ('user1', 'user1', 'USER'),
       ('user2', 'user2', 'USER'),
       ('user3', 'user3', 'USER'),
       ('user4', 'user4', 'USER'),
       ('user5', 'user5', 'USER'),
       ('user6', 'user6', 'USER'),
       ('user7', 'user7', 'USER'),
       ('user8', 'user8', 'USER'),
       ('user9', 'user9', 'USER'),
       ('admin', 'admin', 'ADMIN');

INSERT INTO USER_INFO (id, nome, cognome, indirizzo, cap, provincia, stato)
VALUES (1, 'Marco', 'Rossi', 'Via Roma 10', '00100', 'RM', 'Italia'),
       (2, 'Laura', 'Bianchi', 'Corso Milano 25', '20100', 'MI', 'Italia'),
       (3, 'Luca', 'Verdi', 'Piazza Garibaldi 5', '80100', 'NA', 'Italia'),
       (4, 'Giulia', 'Neri', 'Viale Europa 50', '50100', 'FI', 'Italia'),
       (5, 'Matteo', 'Conti', 'Via Torino 15', '10100', 'TO', 'Italia'),
       (6, 'Sara', 'Gallo', 'Via Napoli 8', '80100', 'NA', 'Italia'),
       (7, 'Andrea', 'Ferri', 'Lungarno Mediceo 3', '56100', 'PI', 'Italia'),
       (8, 'Elisa', 'Romano', 'Via Venezia 12', '30100', 'VE', 'Italia'),
       (9, 'Davide', 'Greco', 'Corso Trieste 20', '00198', 'RM', 'Italia'),
       (10, 'Francesca', 'Martini', 'Via Bologna 14', '40100', 'BO', 'Italia');

INSERT INTO PRODUCT (NAME, DESCRIPTION, PRICE, STOCK_QUANTITY)
VALUES ('Laptop', 'Laptop da gaming', 1200, 25),
       ('Smartphone', 'Smartphone Android', 800, 40),
       ('Cuffie Bluetooth', 'Audio alta qualità', 150, 100),
       ('Monitor 27"', 'Monitor IPS 2K', 300, 30),
       ('Mouse Wireless', 'Mouse ergonomico', 50, 80),
       ('Tastiera Meccanica', 'Tastiera RGB', 90, 60),
       ('SSD 1TB', 'Disco rigido veloce', 110, 70),
       ('Stampante Laser', 'Stampante professionale', 200, 20),
       ('Router WiFi 6', 'Router ad alte prestazioni', 130, 50),
       ('Webcam HD', 'Webcam Full HD', 70, 55);

INSERT INTO ORDERS (USER_ID, DATE, DELIVERY_STATUS, PAYMENT_STATUS)
VALUES (1, '2025-04-01', 'PENDING', 'PENDING'), -- Mario compra 1 Laptop
       (2, '2025-04-02', 'PENDING', 'PAID'),    -- Luigi compra 2 Smartphone
       (3, '2025-04-03', 'DELIVERED', 'PAID'),  -- Anna compra 1 Cuffie Bluetooth
       (4, '2025-04-04', 'SHIPPED', 'PENDING'); -- Carla compra 3 Smartwatch


INSERT INTO ORDERS_DETAILS (ORDER_ID, PRODUCT_ID, QUANTITY)
VALUES (1, 1, 2),
       (1, 5, 4),
       (2, 10, 1),
       (3, 2, 10),
       (3, 3, 3),
       (3, 9, 25),
       (4, 4, 40);

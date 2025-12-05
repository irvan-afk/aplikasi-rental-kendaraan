-- 1. Hapus tabel lama jika ada (agar bersih)
DROP TABLE IF EXISTS rentals;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS vehicles;
DROP TABLE IF EXISTS users;

-- 2. Buat tabel users terlebih dahulu
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- 3. Buat tabel vehicles
CREATE TABLE vehicles (
    id SERIAL PRIMARY KEY,
    plate VARCHAR(20) UNIQUE NOT NULL,
    type VARCHAR(20) NOT NULL,
    brand VARCHAR(50),
    model VARCHAR(50),
    base_price NUMERIC(12,2) NOT NULL,
    available BOOLEAN DEFAULT TRUE
);

-- 4. Buat tabel customers
CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE REFERENCES users(id),
    name VARCHAR(100) NOT NULL
);

-- 5. Buat tabel rentals
CREATE TABLE rentals (
    id SERIAL PRIMARY KEY,
    vehicle_id INTEGER NOT NULL REFERENCES vehicles(id),
    customer_id INTEGER NOT NULL REFERENCES customers(id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    duration_days INTEGER NOT NULL,
    total_cost NUMERIC(12,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. Masukkan Data Dummy
INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'ADMIN');
INSERT INTO users (username, password, role) VALUES ('user', 'user123', 'CUSTOMER');

-- 7. Masukkan Data Customer Dummy (Link ke User 'user')
INSERT INTO customers (user_id, name) VALUES (2, 'user');

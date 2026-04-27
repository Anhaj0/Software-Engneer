CREATE TABLE IF NOT EXISTS master_country (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS master_city (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    country_id BIGINT,
    CONSTRAINT fk_city_country FOREIGN KEY (country_id) REFERENCES master_country (id)
);

CREATE TABLE IF NOT EXISTS customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    dob DATE,
    nic_number VARCHAR(50) UNIQUE
);

CREATE TABLE IF NOT EXISTS customer_mobile (
    customer_id BIGINT NOT NULL,
    mobile_number VARCHAR(50) NOT NULL,
    CONSTRAINT fk_cust_mobile FOREIGN KEY (customer_id) REFERENCES customer (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS customer_address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    line_1 VARCHAR(255),
    line_2 VARCHAR(255),
    city_id BIGINT,
    country_id BIGINT,
    CONSTRAINT fk_addr_customer FOREIGN KEY (customer_id) REFERENCES customer (id) ON DELETE CASCADE,
    CONSTRAINT fk_addr_city FOREIGN KEY (city_id) REFERENCES master_city (id),
    CONSTRAINT fk_addr_country FOREIGN KEY (country_id) REFERENCES master_country (id)
);

CREATE TABLE IF NOT EXISTS customer_family (
    parent_customer_id BIGINT NOT NULL,
    child_customer_id BIGINT NOT NULL,
    PRIMARY KEY (parent_customer_id, child_customer_id),
    CONSTRAINT fk_family_parent FOREIGN KEY (parent_customer_id) REFERENCES customer (id) ON DELETE CASCADE,
    CONSTRAINT fk_family_child FOREIGN KEY (child_customer_id) REFERENCES customer (id) ON DELETE CASCADE
);

-- Insert dummy data for Country
INSERT INTO master_country (name) VALUES ('United States') ON DUPLICATE KEY UPDATE name=name;
INSERT INTO master_country (name) VALUES ('United Kingdom') ON DUPLICATE KEY UPDATE name=name;
INSERT INTO master_country (name) VALUES ('Canada') ON DUPLICATE KEY UPDATE name=name;
INSERT INTO master_country (name) VALUES ('Australia') ON DUPLICATE KEY UPDATE name=name;
INSERT INTO master_country (name) VALUES ('Germany') ON DUPLICATE KEY UPDATE name=name;

-- Insert dummy data for City (assuming IDs 1 to 5 correspond to the above countries)
INSERT INTO master_city (name, country_id) VALUES ('New York', 1) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO master_city (name, country_id) VALUES ('Los Angeles', 1) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO master_city (name, country_id) VALUES ('London', 2) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO master_city (name, country_id) VALUES ('Manchester', 2) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO master_city (name, country_id) VALUES ('Toronto', 3) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO master_city (name, country_id) VALUES ('Vancouver', 3) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO master_city (name, country_id) VALUES ('Sydney', 4) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO master_city (name, country_id) VALUES ('Melbourne', 4) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO master_city (name, country_id) VALUES ('Berlin', 5) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO master_city (name, country_id) VALUES ('Munich', 5) ON DUPLICATE KEY UPDATE name=name;

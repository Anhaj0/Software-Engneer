-- Fresh schema initialization script for MariaDB
-- Safe to run on a new/empty schema.

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS customer_family;
DROP TABLE IF EXISTS customer_address;
DROP TABLE IF EXISTS customer_mobile;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS master_city;
DROP TABLE IF EXISTS master_country;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE master_country (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE master_city (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    country_id INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_master_city_country
        FOREIGN KEY (country_id) REFERENCES master_country(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE customer (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    dob DATE NOT NULL,
    nic_number VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_customer_nic_number UNIQUE (nic_number)
) ENGINE=InnoDB;

CREATE TABLE customer_mobile (
    customer_id INT NOT NULL,
    mobile_number VARCHAR(20) NOT NULL,
    PRIMARY KEY (customer_id, mobile_number),
    CONSTRAINT fk_customer_mobile_customer
        FOREIGN KEY (customer_id) REFERENCES customer(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE customer_address (
    id INT NOT NULL AUTO_INCREMENT,
    customer_id INT NOT NULL,
    line_1 VARCHAR(255) NOT NULL,
    line_2 VARCHAR(255) NULL,
    city_id INT NOT NULL,
    country_id INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_customer_address_customer
        FOREIGN KEY (customer_id) REFERENCES customer(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_customer_address_city
        FOREIGN KEY (city_id) REFERENCES master_city(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_customer_address_country
        FOREIGN KEY (country_id) REFERENCES master_country(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE customer_family (
    parent_customer_id INT NOT NULL,
    child_customer_id INT NOT NULL,
    PRIMARY KEY (parent_customer_id, child_customer_id),
    CONSTRAINT fk_customer_family_parent
        FOREIGN KEY (parent_customer_id) REFERENCES customer(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_customer_family_child
        FOREIGN KEY (child_customer_id) REFERENCES customer(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT chk_customer_family_not_self CHECK (parent_customer_id <> child_customer_id)
) ENGINE=InnoDB;

-- Indexes for all foreign key columns and customer.nic_number
CREATE INDEX idx_master_city_country_id ON master_city(country_id);
CREATE INDEX idx_customer_nic_number ON customer(nic_number);
CREATE INDEX idx_customer_mobile_customer_id ON customer_mobile(customer_id);
CREATE INDEX idx_customer_address_customer_id ON customer_address(customer_id);
CREATE INDEX idx_customer_address_city_id ON customer_address(city_id);
CREATE INDEX idx_customer_address_country_id ON customer_address(country_id);
CREATE INDEX idx_customer_family_parent_customer_id ON customer_family(parent_customer_id);
CREATE INDEX idx_customer_family_child_customer_id ON customer_family(child_customer_id);

INSERT INTO master_country (id, name) VALUES
    (1, 'United States'),
    (2, 'Canada'),
    (3, 'United Kingdom'),
    (4, 'Australia'),
    (5, 'India');

INSERT INTO master_city (id, name, country_id) VALUES
    (1, 'New York', 1),
    (2, 'Los Angeles', 1),
    (3, 'Toronto', 2),
    (4, 'Vancouver', 2),
    (5, 'London', 3),
    (6, 'Manchester', 3),
    (7, 'Sydney', 4),
    (8, 'Melbourne', 4),
    (9, 'Mumbai', 5),
    (10, 'Bengaluru', 5);

DROP TABLE IF EXISTS customer;
CREATE TABLE customer (
	id INT PRIMARY KEY,
	firstName VARCHAR(255) NULL,
	lastName VARCHAR(255) NULL,
	birthdate VARCHAR(255) NULL
);
DROP TABLE IF EXISTS new_customer;
CREATE TABLE new_customer (
	id INT ,
	firstName VARCHAR(255) NULL,
	lastName VARCHAR(255) NULL,
	birthdate VARCHAR(255) NULL
);
DROP TABLE IF EXISTS customer;

CREATE TABLE customer (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50),
  age INT,
  city VARCHAR(50)
);

INSERT INTO customer (name, age, city)
VALUES
        ( 'Art', 39, 'Volodymyr'),
        ( 'Kate', 39, 'Kramatorsk'),
        ( 'Arina', 12, 'Kyiv'),
        ( 'Nazar', 6, 'Krakiv');
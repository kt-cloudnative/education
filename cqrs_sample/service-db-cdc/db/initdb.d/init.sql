CREATE DATABASE inventory;

USE inventory;

drop user 'mysqluser'@'%';

flush privileges;

create user mysqluser@'%' identified by 'mysqlpw'; 

grant all privileges on *.* to 'mysqluser'@'%' identified by 'mysqlpw' with grant option;

flush privileges;  
 
CREATE TABLE IF NOT EXISTS credit_card (
  id            CHAR(36),
  initial_limit INT NOT NULL,
  used_limit    INT NOT NULL,
  PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS withdrawal (
  id    CHAR(36) PRIMARY KEY,
  card_id   CHAR(36)    NOT NULL,
  amount INT NOT NULL
);

INSERT IGNORE INTO inventory.credit_card (ID, INITIAL_LIMIT, USED_LIMIT) VALUES  ('3a3e99f0-5ad9-47fa-961d-d75fab32ef0e', 10000, 0);

COMMIT;
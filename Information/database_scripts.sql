-- =====================================================================
-- Monitoring Project - MySQL scripts
-- Database : monitoring_db
-- Every script executed in MySQL is recorded here (newest at the bottom).
-- =====================================================================


-- ---------------------------------------------------------------------
-- 2026-10-13 | Initial setup (run in MySQL Workbench as root)
-- ---------------------------------------------------------------------
CREATE DATABASE monitoring_db;

CREATE USER 'monitor_user'@'localhost' IDENTIFIED BY 'monitoring_db';

GRANT ALL PRIVILEGES ON monitoring_db.* TO 'monitor_user'@'localhost';

FLUSH PRIVILEGES;

SHOW DATABASES;


-- ---------------------------------------------------------------------
-- 2026-10-13 | Category Manager table
-- Created automatically by Hibernate on application start
-- (spring.jpa.hibernate.ddl-auto=update). Reference copy only -
-- run it manually only if you do not want Hibernate to create it.
-- ---------------------------------------------------------------------
USE monitoring_db;

CREATE TABLE IF NOT EXISTS category_manager (
    S_NO     BIGINT       NOT NULL AUTO_INCREMENT,
    USER_ID  BIGINT       NOT NULL,
    CATEGORY VARCHAR(100) NOT NULL,
    REASON   VARCHAR(255) NOT NULL,
    PRIMARY KEY (S_NO),
    CONSTRAINT UK_USER_ID_REASON UNIQUE (USER_ID, REASON)
);


-- ---------------------------------------------------------------------
-- 2026-10-13 | Verification queries
-- ---------------------------------------------------------------------
USE monitoring_db;

SHOW TABLES;

SELECT * FROM category_manager;

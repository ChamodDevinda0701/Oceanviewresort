-- ============================================================
-- Ocean View Resort - SQL Server Setup Script
-- Run this in SQL Server Management Studio (SSMS)
-- Server: localhost | Auth: Windows Authentication
-- ============================================================

-- Step 1: Create the database
IF NOT EXISTS (
    SELECT name FROM sys.databases 
    WHERE name = 'OceanViewResortDB'
)
BEGIN
    CREATE DATABASE OceanViewResortDB;
    PRINT 'Database OceanViewResortDB created.';
END
ELSE
BEGIN
    PRINT 'Database OceanViewResortDB already exists.';
END
GO

-- Step 2: Use the database
USE OceanViewResortDB;
GO

-- Step 3: Create USERS table
IF NOT EXISTS (
    SELECT * FROM sysobjects 
    WHERE name='USERS' AND xtype='U'
)
BEGIN
    CREATE TABLE USERS (
        ID        INT IDENTITY(1,1) PRIMARY KEY,
        USERNAME  VARCHAR(50)   NOT NULL UNIQUE,
        PASSWORD  VARCHAR(50)   NOT NULL,
        FULL_NAME VARCHAR(100)  NOT NULL
    );
    PRINT 'USERS table created.';
END
GO

-- Step 4: Create ROOMS table
IF NOT EXISTS (
    SELECT * FROM sysobjects 
    WHERE name='ROOMS' AND xtype='U'
)
BEGIN
    CREATE TABLE ROOMS (
        ROOM_NUMBER     VARCHAR(10)  PRIMARY KEY,
        ROOM_TYPE       VARCHAR(30)  NOT NULL,
        PRICE_PER_NIGHT FLOAT        NOT NULL,
        IS_AVAILABLE    BIT          NOT NULL DEFAULT 1
    );
    PRINT 'ROOMS table created.';
END
GO

-- Step 5: Create RESERVATIONS table
IF NOT EXISTS (
    SELECT * FROM sysobjects 
    WHERE name='RESERVATIONS' AND xtype='U'
)
BEGIN
    CREATE TABLE RESERVATIONS (
        RESERVATION_NUMBER  VARCHAR(20)  PRIMARY KEY,
        GUEST_NAME          VARCHAR(100) NOT NULL,
        ADDRESS             VARCHAR(200) NOT NULL,
        CONTACT_NUMBER      VARCHAR(15)  NOT NULL,
        ROOM_NUMBER         VARCHAR(10)  NOT NULL,
        ROOM_TYPE           VARCHAR(30)  NOT NULL,
        CHECK_IN_DATE       DATE         NOT NULL,
        CHECK_OUT_DATE      DATE         NOT NULL,
        TOTAL_AMOUNT        FLOAT        NOT NULL,
        BOOKING_DATE        DATETIME     DEFAULT GETDATE()
    );
    PRINT 'RESERVATIONS table created.';
END
GO

-- Step 6: Insert default admin user
IF NOT EXISTS (
    SELECT 1 FROM USERS WHERE USERNAME = 'admin'
)
BEGIN
    INSERT INTO USERS (USERNAME, PASSWORD, FULL_NAME)
    VALUES ('admin', 'admin123', 'Administrator');
    PRINT 'Default admin user inserted.';
END
GO

-- Step 7: Insert default rooms
IF NOT EXISTS (SELECT 1 FROM ROOMS WHERE ROOM_NUMBER = '101')
    INSERT INTO ROOMS VALUES ('101', 'Standard', 5000.00, 1);

IF NOT EXISTS (SELECT 1 FROM ROOMS WHERE ROOM_NUMBER = '102')
    INSERT INTO ROOMS VALUES ('102', 'Standard', 5000.00, 1);

IF NOT EXISTS (SELECT 1 FROM ROOMS WHERE ROOM_NUMBER = '201')
    INSERT INTO ROOMS VALUES ('201', 'Deluxe', 9500.00, 1);

IF NOT EXISTS (SELECT 1 FROM ROOMS WHERE ROOM_NUMBER = '202')
    INSERT INTO ROOMS VALUES ('202', 'Deluxe', 9500.00, 1);

IF NOT EXISTS (SELECT 1 FROM ROOMS WHERE ROOM_NUMBER = '301')
    INSERT INTO ROOMS VALUES ('301', 'Suite', 18000.00, 1);

IF NOT EXISTS (SELECT 1 FROM ROOMS WHERE ROOM_NUMBER = '302')
    INSERT INTO ROOMS VALUES ('302', 'Suite', 18000.00, 1);

PRINT 'Default rooms inserted.';
GO

-- ============================================================
-- Verify setup
-- ============================================================
SELECT 'USERS'        AS TableName, COUNT(*) AS Rows FROM USERS
UNION ALL
SELECT 'ROOMS',        COUNT(*) FROM ROOMS
UNION ALL
SELECT 'RESERVATIONS', COUNT(*) FROM RESERVATIONS;

PRINT 'Setup complete! You can now run the Java application.';
GO

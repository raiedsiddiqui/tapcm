CREATE DATABASE IF NOT EXISTS survey_app;
USE survey_app;

CREATE TABLE IF NOT EXISTS users (
	user_ID INT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED TINYINT allows for 255 users*/
	name VARCHAR(255) NOT NULL,
	username VARCHAR(255) NOT NULL,
	password VARCHAR(255) NOT NULL,
	enabled BOOLEAN NOT NULL,
	email VARCHAR(50) NOT NULL,
	role VARCHAR(45),
	PRIMARY KEY (user_ID)
);

CREATE TABLE IF NOT EXISTS patients (
	patient_ID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED SMALLINT allows for 65,535 patients*/
	firstname VARCHAR(255) NOT NULL,
	lastname VARCHAR(255) NOT NULL,
	preferredname VARCHAR(255),
	gender VARCHAR(3), /*Using VARCHAR(3) allows for 3 characters, expecting values like 'M', 'F', 'MTF', etc...*/
	email VARCHAR(50),
	volunteer TINYINT UNSIGNED NOT NULL, /* Same as user_ID */
	notes TEXT,
	PRIMARY KEY (patient_ID)
);

CREATE TABLE IF NOT EXISTS surveys (
	survey_ID TINYINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED TINYINT allows 255 surveys*/
	title VARCHAR(50),
	type VARCHAR(255),
	contents MEDIUMBLOB,
	last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, /*Automatically updates when record changed*/
    priority TINYINT(1), /*Between 0-9*/
    description TEXT,
	PRIMARY KEY (survey_ID)
);

CREATE TABLE IF NOT EXISTS survey_results (
	result_ID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED SMALLINT allows for 65,535 results*/
	survey_ID SMALLINT UNSIGNED NOT NULL, /*Same as survey_ID field in surveys*/
	patient_ID SMALLINT UNSIGNED NOT NULL, /*Same as patient_ID in patients*/
	completed BOOLEAN NOT NULL DEFAULT 0, /*The completion status of the survey (0=incomplete)*/
	startDate DATETIME, /*Automatically assigns the startDate when the survey is assigned*/
	editDate TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP, /*editDate represents the last edit date*/
	data MEDIUMBLOB, /*The survey data*/
	PRIMARY KEY (result_ID)
);

CREATE TABLE IF NOT EXISTS activities (
	event_ID INT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED INT allows for 4,294,967,295 events, which should be enough*/
	event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	start_time TIMESTAMP NULL;
	end_time TIMESTAMP NULL;
	volunteer SMALLINT UNSIGNED NOT NULL,
	patient SMALLINT UNSIGNED,
	appointment SMALLINT UNSIGNED,
	description TEXT,
	PRIMARY KEY (event_ID)
);

CREATE TABLE IF NOT EXISTS appointments (
	appointment_ID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED SMALLINT allows for 65,535 appointments*/
	volunteer TINYINT UNSIGNED NOT NULL, /* Same as user_ID */
	patient SMALLINT UNSIGNED NOT NULL, /* Same as patient_ID */
	date_time DATETIME NOT NULL, /*Contains both date and time, separated using functions in query*/
	comments TEXT, /*Volunteer visit comments*/
	status TEXT NOT NULL, /*Approval status of appointment*/
	completed BOOLEAN NOT NULL DEFAULT 0, /*The completion status of the appointment (0=incomplete)*/
	contactedAdmin BOOLEAN NOT NULL DEFAULT 0, /*The status of the volunteer contacted the admin*/
	PRIMARY KEY(appointment_ID)
);

CREATE TABLE IF NOT EXISTS messages (
	message_ID BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	recipient TINYINT UNSIGNED NOT NULL, /* Same as user_ID */
	sender TINYINT UNSIGNED NOT NULL,
	msg TEXT,
    sent TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	subject VARCHAR(255) NOT NULL,
	msgRead BOOLEAN NOT NULL DEFAULT 0,
	PRIMARY KEY (message_ID)
);

CREATE TABLE IF NOT EXISTS pictures (
    picture_ID MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
    pic VARCHAR(255), /* path to picture file */
    owner SMALLINT, /* the ID of the user or patient the picture belongs to */
    owner_is_user BOOLEAN, /* 1 if the value of owner refers to a user, 0 if a patient */
    PRIMARY KEY (picture_ID)
);

CREATE TABLE IF NOT EXISTS narratives (
    narrative_ID MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
    title VARCHAR(50) NOT NULL,
    contents TEXT,
    edit_Date TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP, /*edit_Date represents the last edit date*/
    user_ID INT NOT NULL,
    patient_ID SMALLINT UNSIGNED NOT NULL;
    appointment SMALLINT UNSIGNED NOT NULL,
    PRIMARY KEY (narrative_ID)
);

CREATE TABLE IF NOT EXISTS volunteers (
	volunteer_ID MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
	firstname VARCHAR(255) NOT NULL,
	lastname VARCHAR(255) NOT NULL,
	preferredname VARCHAR(255),
	age_type VARCHAR(1);/* 1 character, expecting value like 'Y' for younger, 'O' for older */
	gender VARCHAR(3), /*Using VARCHAR(3) allows for 3 characters, expecting values like 'M', 'F', 'MTF', etc...*/
	email VARCHAR(50),
	experience_level VARCHAR(1), /* Using VARCHAR(1) for 1 character, expecting value like "E", "B", "I"...*/
	city VARCHAR(50),
	province VARCHAR(3),
	phone_number VARCHAR(20),
	PRIMARY KEY (volunteer_ID)	
);
COMMIT;

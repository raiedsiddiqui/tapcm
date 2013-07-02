CREATE DATABASE IF NOT EXISTS survey_app;
USE survey_app;

CREATE TABLE IF NOT EXISTS users (
	user_ID TINYINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED TINYINT allows for 255 users*/
	name VARCHAR(255) NOT NULL,
	username VARCHAR(30) NOT NULL,
	password VARCHAR(20) NOT NULL,
	is_admin BOOLEAN NOT NULL DEFAULT FALSE,
	email VARCHAR(50) NOT NULL,
	PRIMARY KEY (user_ID)
);

CREATE TABLE IF NOT EXISTS patients (
	patient_ID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED SMALLINT allows for 65,535 patients*/
	firstname VARCHAR(255) NOT NULL,
	lastname VARCHAR(255) NOT NULL,
	gender VARCHAR(3), /*Using VARCHAR(3) allows for 3 characters, expecting values like 'M', 'F', 'MTF', etc...*/
	age TINYINT UNSIGNED, /*Using UNSIGNED TINYINT allows ages between 0-255, which is overkill but I can't get any smaller than that*/
	email VARCHAR(50),
	caretaker TINYINT,
	PRIMARY KEY (patient_ID)
);

CREATE TABLE IF NOT EXISTS surveys (
	survey_ID TINYINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED TINYINT allows 255 surveys*/
	title VARCHAR(50) NOT NULL,
	author VARCHAR(255) NOT NULL,
	last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, /*Automatically updates when record changed*/
	PRIMARY KEY (survey_ID)
);

CREATE TABLE IF NOT EXISTS survey_results (
	result_ID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED SMALLINT allows for 65,535 results*/
	survey SMALLINT UNSIGNED NOT NULL, /*Same as survey_ID field in surveys*/
	patient SMALLINT UNSIGNED NOT NULL, /*Same as patient_ID in patients*/
	caretaker SMALLINT UNSIGNED NOT NULL, /*Same as user_ID in users*/
	started DATE NOT NULL,
	completed DATE,
	results TEXT,
	PRIMARY KEY (result_ID)
);

CREATE TABLE IF NOT EXISTS activites (
	event_ID INT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED INT allows for 4,294,967,295 events, which should be enough*/
	event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	caretaker SMALLINT UNSIGNED NOT NULL,
	patient SMALLINT UNSIGNED NOT NULL,
	description TEXT,
	PRIMARY KEY (event_ID)
);

CREATE TABLE IF NOT EXISTS appointments (
	appointment_ID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED SMALLINT allows for 65,535 appointments*/
	caretaker TINYINT UNSIGNED NOT NULL, /*Same as user_ID from users table*/
	patient SMALLINT UNSIGNED NOT NULL, /*Same as patient_ID from patients table*/
	date_time DATETIME NOT NULL,
	details TEXT,
	PRIMARY KEY(appointment_ID)
);

CREATE TABLE IF NOT EXISTS required_surveys (
	req_survey_ID MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED MEDIUMINT allows for 16,777,215 surveys (255 surveys * 65,535 patients = 16,711,425)*/
	patient SMALLINT UNSIGNED NOT NULL, /*Same as patient_ID from patients table*/
	survey SMALLINT UNSIGNED NOT NULL,
	PRIMARY KEY (req_survey_ID)
);

COMMIT;

CREATE DATABASE IF NOT EXISTS survey_app;
USE survey_app;

CREATE TABLE IF NOT EXISTS users (
	user_ID TINYINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED TINYINT allows for 255 users*/
	name VARCHAR(255) NOT NULL,
	username VARCHAR(255) NOT NULL,
	password VARCHAR(255) NOT NULL,
	enabled BOOLEAN NOT NULL,
	email VARCHAR(50) NOT NULL,
	role VARCHAR(45),
	PRIMARY KEY (user_ID)
);

CREATE TABLE IF NOT EXISTS patients (
	patient_ID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, /*zzUsing UNSIGNED SMALLINT allows for 65,535 patients*/
	firstname VARCHAR(255) NOT NULL,
	lastname VARCHAR(255) NOT NULL,
	gender VARCHAR(3), /*Using VARCHAR(3) allows for 3 characters, expecting values like 'M', 'F', 'MTF', etc...*/
	age TINYINT UNSIGNED, /*Using UNSIGNED TINYINT allows ages between 0-255, which is overkill but I can't get any smaller than that*/
	email VARCHAR(50),
	volunteer TINYINT UNSIGNED NOT NULL, /* Same as user_ID */
    color VARCHAR(7), /*Hexcode color string (#ffffff)*/
	PRIMARY KEY (patient_ID)
);

CREATE TABLE IF NOT EXISTS surveys (
	survey_ID TINYINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED TINYINT allows 255 surveys*/
	title VARCHAR(50),
	type VARCHAR(255),
	contents MEDIUMBLOB,
	last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, /*Automatically updates when record changed*/
	PRIMARY KEY (survey_ID)
);

CREATE TABLE IF NOT EXISTS survey_results (
	result_ID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED SMALLINT allows for 65,535 results*/
	survey_ID SMALLINT UNSIGNED NOT NULL, /*Same as survey_ID field in surveys*/
	patient_ID SMALLINT UNSIGNED NOT NULL, /*Same as patient_ID in patients*/
	completed BOOLEAN NOT NULL DEFAULT 0, /*The completion status of the survey (0=incomplete)*/
	startDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, /*Automatically assigns the startDate when the survey is assigned*/
	editDate TIMESTAMP, /*editDate represents the last edit date*/
	data MEDIUMBLOB, /*The survey data*/
	PRIMARY KEY (result_ID)
);

CREATE TABLE IF NOT EXISTS activites (
	event_ID INT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED INT allows for 4,294,967,295 events, which should be enough*/
	event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	volunteer SMALLINT UNSIGNED NOT NULL,
	patient SMALLINT UNSIGNED NOT NULL,
	description TEXT,
	PRIMARY KEY (event_ID)
);

CREATE TABLE IF NOT EXISTS appointments (
	appointment_ID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, /*Using UNSIGNED SMALLINT allows for 65,535 appointments*/
	volunteer TINYINT UNSIGNED NOT NULL, /* Same as user_ID */
	patient SMALLINT UNSIGNED NOT NULL, /* Same as patient_ID */
	date_time DATETIME NOT NULL, /*Contains both date and time, separated using functions in query*/
	details TEXT,
	PRIMARY KEY(appointment_ID)
);

CREATE TABLE IF NOT EXISTS messages (
	message_ID BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	recipient TINYINT UNSIGNED NOT NULL, /* Same as user_ID */
	sender VARCHAR(255) NOT NULL,
	msg TEXT,
    sent TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	subject VARCHAR(255) NOT NULL,
	msgRead BOOLEAN NOT NULL DEFAULT 0,
	PRIMARY KEY (message_ID)
);
COMMIT;

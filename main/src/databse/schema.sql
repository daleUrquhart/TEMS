DROP DATABASE TEMS;
CREATE DATABASE tems;
USE tems;

-- Gender stuff
CREATE TABLE Genders (
    gender_id INT AUTO_INCREMENT PRIMARY KEY,
    gender_name VARCHAR(255) UNIQUE NOT NULL
); 


-- Stores an auditionee's preferrd gender roles they want to see
CREATE TABLE AuditioneeGenderRoles (
    auditionee_id INT NOT NULL,
    gender_id INT NOT NULL,
    PRIMARY KEY (auditionee_id, gender_role_id),
    FOREIGN KEY (auditionee_id) REFERENCES Auditionees(auditionee_id) ON DELETE CASCADE,
    FOREIGN KEY (gender_id) REFERENCES Genders(gender_id) ON DELETE CASCADE
);

-- User
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('auditionee', 'recruiter') NOT NULL
);

CREATE TABLE Auditionees (
    auditionee_id INT PRIMARY KEY,
    gender_id INT NOT NULL,
    years_of_experience INT NOT NULL CHECK (years_of_experience >= 0),
    FOREIGN KEY (auditionee_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (gender_id) REFERENCES Genders(gender_id) ON DELETE CASCADE
);

CREATE TABLE TalentRecruiters (
    recruiter_id INT PRIMARY KEY,
    company VARCHAR(255), 
    FOREIGN KEY (recruiter_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

CREATE TABLE Listings (
    listing_id INT AUTO_INCREMENT PRIMARY KEY,
    recruiter_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    genre ENUM('Action', 'Drama', 'Comedy', 'Sci-Fi', 'Horror', 'Romance', 'Other') NOT NULL,
    gender_id INT NOT NULL, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recruiter_id) REFERENCES TalentRecruiters(recruiter_id) ON DELETE CASCADE,
    FOREIGN KEY (gender_id) REFERENCES Genders(gender_id) ON DELETE CASCADE
);

CREATE TABLE Criteria (
    criteria_id INT AUTO_INCREMENT PRIMARY KEY,
    listing_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    weight INT NOT NULL CHECK (weight >= 0),
    FOREIGN KEY (listing_id) REFERENCES Listings(listing_id) ON DELETE CASCADE
);

CREATE TABLE Applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    auditionee_id INT NOT NULL,
    listing_id INT NOT NULL,
    submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'accepted', 'rejected') DEFAULT 'pending',
    resume TEXT,
    cover_letter TEXT,
    FOREIGN KEY (auditionee_id) REFERENCES Auditionees(auditionee_id) ON DELETE CASCADE,
    FOREIGN KEY (listing_id) REFERENCES Listings(listing_id) ON DELETE CASCADE
);

CREATE TABLE Scores (
    score_id INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT NOT NULL,
    criteria_id INT NOT NULL,
    score DECIMAL(5,2) CHECK (score >= 0 AND score <= 100),
    FOREIGN KEY (application_id) REFERENCES Applications(application_id) ON DELETE CASCADE,
    FOREIGN KEY (criteria_id) REFERENCES Criteria(criteria_id) ON DELETE CASCADE
);

CREATE TABLE Offers (
    offer_id INT AUTO_INCREMENT PRIMARY KEY,
    recruiter_id INT NOT NULL,
    auditionee_id INT NOT NULL,
    listing_id INT NOT NULL,
    gender_id INT NOT NULL, 
    status ENUM('pending', 'accepted', 'declined') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    FOREIGN KEY (recruiter_id) REFERENCES TalentRecruiters(recruiter_id) ON DELETE CASCADE,
    FOREIGN KEY (auditionee_id) REFERENCES Auditionees(auditionee_id) ON DELETE CASCADE,
    FOREIGN KEY (listing_id) REFERENCES Listings(listing_id) ON DELETE CASCADE,
    FOREIGN KEY (gender_id) REFERENCES Genders(gender_id) ON DELETE CASCADE
);

CREATE TABLE Notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);


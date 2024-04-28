DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Friendship;
DROP TABLE IF EXISTS Buildings;
DROP TABLE IF EXISTS Locations;
DROP TABLE IF EXISTS Campus_Events;
DROP TABLE IF EXISTS Event_Participants;

CREATE TABLE IF NOT EXISTS Users (
	user_ID			INT				PRIMARY KEY,
    first_name		VARCHAR(50)		NOT NULL,
    last_name		VARCHAR(50)		NOT NULL,
    email			VARCHAR(50)		NOT NULL,
    user_pass		VARCHAR(30)		NOT NULL,
    is_Organizer	BOOL			NOT NULL
);

CREATE TABLE IF NOT EXISTS Friendship (
	requestor	INT				NOT NULL	REFERENCES Users(user_ID),
    receiver	INT				NOT NULL	REFERENCES Users(user_ID),
    f_status	VARCHAR(10)		NOT NULL,
    CONSTRAINT checkUnique CHECK (requestor <> receiver),
    CONSTRAINT friend_pk PRIMARY KEY (requestor, receiver)
);

INSERT OR REPLACE INTO Friendship VALUES (1293960, 1277182, 'areFriends'),
                              (1280394, 1293960, 'areFriends'),
                              (1282758, 1293960, 'pending'),
                              (1293960, 1291907, 'areFriends'),
                              (1281866, 1293960, 'areFriends');

CREATE TABLE IF NOT EXISTS Buildings (
	building_name	VARCHAR(60)		PRIMARY KEY,
    num_of_floors	TINYINT			NOT NULL,
    campus			VARCHAR(5)		NOT NULL,
    address			VARCHAR(100)	NOT NULL
);

CREATE TABLE IF NOT EXISTS Locations (
	building_name	VARCHAR(60)		NOT NULL	REFERENCES Buildings(building_name),
    room_no			VARCHAR(15)		NOT NULL,
    on_floor		VARCHAR(10)		NOT NULL,
    room_type		VARCHAR(20),
    loc_desc		VARCHAR(200),
    CONSTRAINT loc_pk PRIMARY KEY (building_name, room_no)
);

DROP TABLE Campus_Events;

CREATE TABLE IF NOT EXISTS Campus_Events (
	event_name		VARCHAR(50)		NOT NULL	PRIMARY KEY,
    start_time		DATETIME		NOT NULL,
    end_time        DATETIME        NOT NULL,
    building_name	VARCHAR(40)		NOT NULL,
    room_no			VARCHAR(5)		NOT NULL,
    event_desc		VARCHAR(500)	NOT NULL,
    organizer		INT				NOT NULL	REFERENCES Users(user_ID),
    CONSTRAINT check_time CHECK (end_time > start_time),
    CONSTRAINT event_fk FOREIGN KEY (building_name, room_no)
    REFERENCES Locations(building_name, room_no)
);

INSERT OR REPLACE INTO Campus_Events VALUES ("Financial Aid Workshop 2024", "2024-04-25 12:30", "2024-04-25 14:00",
                                            "Edward Guiliano Global Center 1855 Broadway", "401", 
                                            "Learn more about FAFSA, HESC, and scholarships.", 1293960);

INSERT OR REPLACE INTO Campus_Events VALUES ("Event 2", "2024-04-11 12:30", "2024-04-11 14:08",
                                            "Edward Guiliano Global Center 1855 Broadway", "401", 
                                            "Learn more about FAFSA, HESC, and scholarships.", 1281866);

INSERT OR REPLACE INTO Campus_Events VALUES ("Event 3", "2024-04-10 12:30", "2024-04-10 14:08",
                                            "Edward Guiliano Global Center 1855 Broadway", "401", 
                                            "Learn more about FAFSA, HESC, and scholarships.", 1291907);

DROP TABLE IF EXISTS Event_Participants;

CREATE TABLE IF NOT EXISTS Event_Participants (
	event_name		VARCHAR(50)		NOT NULL	REFERENCES Campus_Events(event_name) ON DELETE CASCADE,
    participant		INT				NOT NULL	REFERENCES Users(user_ID) ON DELETE CASCADE,
    CONSTRAINT ep_pk PRIMARY KEY (event_name, participant)
);

INSERT OR REPLACE INTO Event_Participants VALUES ("Event 2", 1280394),
                                                 ("Event 2", 1277182),
                                                 ("Event 3", 1281866),
                                                 ("Test", 1282758);

SELECT DISTINCT e.*, u.first_name, u.last_name FROM Campus_Events e
JOIN Users u
ON e.organizer = u.user_ID
JOIN Event_Participants p
ON e.event_name = p.event_name AND e.organizer <> 1293960
JOIN Friendship f
ON (p.participant = f.requestor AND f.receiver = 1293960) OR (p.participant = f.receiver AND f.requestor = 1293960);

INSERT INTO Friendship VALUES (1281866, 1277182, "areFriends");

INSERT INTO Users VALUES (1293960, "Henry", "Yeung", "cyeung03@nyit.edu", "passwordHenry", true),
						 (1281866, "Guang", "Too", "gtoo@nyit.edu", "passwordGuang", true),
						 (1291907, "Autumn", "Penge", "apenge@nyit.edu", "passwordAutumn", true),
                         (1282758, "Ahmadullah", "Sharifi", "asharifi@nyit.edu", "passwordAhmadullah", true),
                         (1277182, "Christian", "Pascal", "csinoagp@nyit.edu", "passwordChris", false),
                         (1280394, "Brahian", "Almonte", "balmon02@nyit.edu", "passwordBrahian", false);

INSERT INTO Buildings VALUES ("Edward Guiliano Global Center 1855 Broadway", 12, "NYC", "1855 Broadway, New York, NY 10023"),
							 ("16W. 61st Street", 11, "NYC", "16W. 61st Street, New York, NY 10023"),
                             ("26W. 61st Street", 5, "NYC", "26W. 61st Street, New York, NY 10023"),
                             ("Student Activities Building/1849 Broadway", 2, "NYC", "1849 Broadway, New York, NY 10023");

INSERT INTO Locations VALUES ("Edward Guiliano Global Center 1855 Broadway", "Lounge", "1", "Other", "Student Lounge"),
                             ("Edward Guiliano Global Center 1855 Broadway", "Library", "2", "Other", "Library"),
							 ("Edward Guiliano Global Center 1855 Broadway", "202", "2", "Office", "Sebastien Marion (Library)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "303", "3", "Group Study Room", ""),
                             ("Edward Guiliano Global Center 1855 Broadway", "304", "3", "Group Study Room", ""),
                             ("Edward Guiliano Global Center 1855 Broadway", "305", "3", "Group Study Room", ""),
                             ("Edward Guiliano Global Center 1855 Broadway", "307", "3", "Lab", "Computer Lab"),
                             ("Edward Guiliano Global Center 1855 Broadway", "319", "3", "Lab", "Computer Lab"),
                             ("Edward Guiliano Global Center 1855 Broadway", "401", "4", "Classroom", ""),
                             ("Edward Guiliano Global Center 1855 Broadway", "401A", "4", "Office", "Chris Wernicki (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "401B", "4", "Office", "Ellen Katz (Humanities)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "402A", "4", "Office", "Bernard Fryshman (Physics), Michael Izady (Humanities)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "405", "4", "Classroom", ""),
                             ("Edward Guiliano Global Center 1855 Broadway", "405A", "4", "Office", "Ana Petrovic (Bio/Chem)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "406", "4", "Classroom", ""),
                             ("Edward Guiliano Global Center 1855 Broadway", "407", "4", "Lab", "Computer Science Lab"),
                             ("Edward Guiliano Global Center 1855 Broadway", "408", "4", "Lab", "Physics Lab"),
                             ("Edward Guiliano Global Center 1855 Broadway", "408A", "4", "Classroom", ""),
                             ("Edward Guiliano Global Center 1855 Broadway", "409", "4", "Classroom", ""),
                             ("Edward Guiliano Global Center 1855 Broadway", "Arch Studio", "5", "Studio", "Architecture Studio"),
                             ("Edward Guiliano Global Center 1855 Broadway", "501", "5", "Lab", "Computer Lab"),
                             ("Edward Guiliano Global Center 1855 Broadway", "601", "6", "Lab", "Computer Lab"),
                             ("Edward Guiliano Global Center 1855 Broadway", "601A", "6", "Office", "Felix Fischman (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "602", "6", "Classroom", ""),
                             ("Edward Guiliano Global Center 1855 Broadway", "603", "6", "Lab", "Computer Lab"),
                             ("Edward Guiliano Global Center 1855 Broadway", "604", "6", "Classroom", ""),
                             ("Edward Guiliano Global Center 1855 Broadway", "605A", "6", "Office", "Ludmilla Amani (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "606", "6", "Lab", "Computer Lab"),
                             ("Edward Guiliano Global Center 1855 Broadway", "606A", "6", "Other", "American Institute of Architecture Students"),
                             ("Edward Guiliano Global Center 1855 Broadway", "607", "6", "Other", "Entrepreneurship and Technology Innovation Center"),
                             ("Edward Guiliano Global Center 1855 Broadway", "608", "6", "Studio", "Senior Design Studio"),
                             ("Edward Guiliano Global Center 1855 Broadway", "701A", "7", "Office", "Anthony Normil (IT Services), Jonathan Worrell (IT Services)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "701B", "7", "Office", "William Stennett (IT Services)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "701C", "7", "Classroom", ""),
                             ("Edward Guiliano Global Center 1855 Broadway", "702", "7", "Classroom", ""),
                             ("Edward Guiliano Global Center 1855 Broadway", "702A", "7", "Office", "Francine Glazer (Center for Teaching & Learning)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "703", "7", "Lab", "Computer Lab"),
                             ("Edward Guiliano Global Center 1855 Broadway", "704", "7", "Classroom", ""),
                             ("Edward Guiliano Global Center 1855 Broadway", "705", "7", "Classroom", ""),
                             ("Edward Guiliano Global Center 1855 Broadway", "706", "7", "Classroom", ""),
                             ("Edward Guiliano Global Center 1855 Broadway", "707", "7", "Office", "Dennis McAleer (IT Services), David Webster (IT Services)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "708", "7", "Classroom", ""),
                             ("Edward Guiliano Global Center 1855 Broadway", "801", "8", "Lab", "Computer Lab"),
                             ("Edward Guiliano Global Center 1855 Broadway", "801A", "8", "Office", "Houwei Cao (CoECS), Reza Khalaj Amineh (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "802B", "8", "Office", "Cecilia Dong (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "803A", "8", "Office", "N. Sertac Artan (CoECS), Anand Santhanakrishnan (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "804", "8", "Office", "Jackline Okot (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "805", "8", "Office", "Paula Juric Kreuz (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "805A", "8", "Office", "Edward Vicari (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "806", "8", "Office", "Yoshi Saito (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "807", "8", "Office", "Jerry Cheng (CoECS), Wenjia Li (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "808", "8", "Office", "Paolo Gasti (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "809", "8", "Office", "Kazi Ahmed (CoECS), Richard Meyers (CoECS), Lazaros Pavlidis (CoECS), George Salayka (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "810", "8", "Office", "Susan Gass (CoECS), Maryam Ravan (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "811", "8", "Office", "Huanying Gu (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "812", "8", "Office", "Lak Amara (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "814", "8", "Office", "Steven Billis (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "815", "8", "Conference Room", ""),
                             ("Edward Guiliano Global Center 1855 Broadway", "901", "9", "Lab", "Biology Lab"),
                             ("Edward Guiliano Global Center 1855 Broadway", "901A", "9", "Office", "Niharika Nath (Bio/Chem)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "902", "9", "Lab", "Chemistry Lab"),
                             ("Edward Guiliano Global Center 1855 Broadway", "903", "9", "Lab", "Computer Lab"),
                             ("Edward Guiliano Global Center 1855 Broadway", "904", "9", "Office", "Kristina Murtha (CoECS)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "1007", "10", "Other", "CNC Room"),
                             ("Edward Guiliano Global Center 1855 Broadway", "1009", "10", "Lab", "Fabrication Lab"),
                             ("Edward Guiliano Global Center 1855 Broadway", "1010", "10", "Lab", "Laser Lab"),
                             ("Edward Guiliano Global Center 1855 Broadway", "1011", "10", "Lab", "Computer Lab"),
                             ("Edward Guiliano Global Center 1855 Broadway", "1012", "10", "Other", "Plot Shop"),
                             ("Edward Guiliano Global Center 1855 Broadway", "1013", "10", "Studio", "Thesis Studio"),
                             ("Edward Guiliano Global Center 1855 Broadway", "Gallery Space", "11", "Other", "Gallery Space"),
                             ("Edward Guiliano Global Center 1855 Broadway", "1109", "11", "Office", "Anthony Caradonna (Architecture)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "1110", "11", "Office", "Jason Van Nest (Architecture), Nader Vossoughian (Architecture)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "1111", "11", "Office", "Marcella Del Signore (Architecture), Farzana Gandhi (Architecture)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "1112", "11", "Office", "Matthias Altwicker (Architecture), Matthew Ford (Architecture), Pablo Lorenzo-Eiroa (Architecture), Jeffrey Raven (Architecture)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "1113", "11", "Office", "Desiree Gomez (Architecture), John Vincennes (Architecture)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "1116", "11", "Office", "Gertrudis Brens (Architecture), Giovanni Santamaria (Architecture)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "1118", "11", "Office", "David Diamond (Architecture), Michael Nolan (Architecture)"),
                             ("Edward Guiliano Global Center 1855 Broadway", "1119", "11", "Studio", "Master Studio"),
                             ("Edward Guiliano Global Center 1855 Broadway", "1120", "11", "Studio", "Architecture Studio");

INSERT INTO Locations VALUES ("16W. 61st Street", "Lounge", "Lobby", "Other", "Student Lounge"),
                             ("16W. 61st Street", "001", "Lobby", "Office", "Admissions"),
							 ("16W. 61st Street", "002", "Lobby", "Office", "Bursar"),
                             ("16W. 61st Street", "003", "Lobby", "Office", "Registrar"),
                             ("16W. 61st Street", "004", "Lobby", "Office", "Michael Murphy (Registrar)"),
                             ("16W. 61st Street", "005", "Lobby", "Office", "Financial Aid"),
							 ("16W. 61st Street", "006", "Lobby", "Office", "Admissions"),
                             ("16W. 61st Street", "007", "Lobby", "Office", "Debra Bush-Ford (Financial Aid)"),
                             ("16W. 61st Street", "008", "Lobby", "Office", "Admissions"),
                             ("16W. 61st Street", "010", "Lobby", "Office", "Michaelson Eustache (Admissions)"),
                             ("16W. 61st Street", "601", "6", "Office", "Tracy McGoldrick (Humanities)"),
                             ("16W. 61st Street", "602", "6", "Office", "English Adjunct Faculty Office"),
                             ("16W. 61st Street", "603", "6", "Lab", "English Language Lab"),
                             ("16W. 61st Street", "605", "6", "Office", "English Adjunct Faculty Office"),
                             ("16W. 61st Street", "606", "6", "Office", "Lori Jirousek-Falls (Humanities)"),
                             ("16W. 61st Street", "607", "6", "Office", "Jonathan Goldman (Humanities), Michael Gamble (Humanities)"),
                             ("16W. 61st Street", "608", "6", "Office", "Lissi Athanasiou-Krikelis (Humanities)"),
                             ("16W. 61st Street", "609", "6", "Other", "Writing Center"),
                             ("16W. 61st Street", "610", "6", "Office", "English Adjunct Faculty Office"),
                             ("16W. 61st Street", "611", "6", "Office", "Aiping Gao (Humanities)"),
                             ("16W. 61st Street", "612", "6", "Office", "Katherine Williams (Humanities)"),
                             ("16W. 61st Street", "613", "6", "Office", "Jennifer Griffiths (Humanities)"),
                             ("16W. 61st Street", "614", "6", "Office", "Michael Schiavi (Humanities)"),
                             ("16W. 61st Street", "615", "6", "Other", "English Department Testing Room"),
                             ("16W. 61st Street", "623", "6", "Classroom", ""),
                             ("16W. 61st Street", "624", "6", "Classroom", ""),
                             ("16W. 61st Street", "625", "6", "Office", "English Adjunct Faculty Office"),
                             ("16W. 61st Street", "710C", "7", "Office", "Spencer Turkel (Bio/Chem)"),
                             ("16W. 61st Street", "711", "7", "Office", "Subhabrata Chaudhury (Bio/Chem)"),
                             ("16W. 61st Street", "712", "7", "Office", "Eleni Nikitopoulos (Bio/Chem), Shenglong Zhang (Bio/Chem)"),
                             ("16W. 61st Street", "715", "7", "Office", "Andrew Hofstrand (Math), Eve Armstrong (Physics)"),
                             ("16W. 61st Street", "716", "7", "Office", "Sophia Domokos (Physics), Vitaly Katsnelson (Math)"),
                             ("16W. 61st Street", "721", "7", "Classroom", ""),
                             ("16W. 61st Street", "722", "7", "Classroom", ""),
                             ("16W. 61st Street", "723", "7", "Classroom", ""),
                             ("16W. 61st Street", "724", "7", "Office", "Ranja Roy (Math)"),
                             ("16W. 61st Street", "725", "7", "Other", "Tutoring Room (HEOP)"),
                             ("16W. 61st Street", "810C", "8", "Office", "Grady Carney (Bio/Chem), Laihan Luo (Math)"),
                             ("16W. 61st Street", "811", "8", "Office", "Jim Martinez (Humanities)"),
                             ("16W. 61st Street", "812", "8", "Lab", "Physics Lab"),
                             ("16W. 61st Street", "817", "8", "Office", "Daniel Quigley (Dean)"),
                             ("16W. 61st Street", "820", "8", "Classroom", ""),
                             ("16W. 61st Street", "821", "8", "Classroom", ""),
                             ("16W. 61st Street", "822", "8", "Classroom", ""),
                             ("16W. 61st Street", "823", "8", "Office", "Kate O'Hara (Humanities)"),
                             ("16W. 61st Street", "824", "8", "Classroom", ""),
                             ("16W. 61st Street", "910", "9", "Lab", "HIVE: XR + Usability Research Lab"),
                             ("16W. 61st Street", "912", "9", "Office", "Anila Jaho (Digital Art)"),
                             ("16W. 61st Street", "913", "9", "Office", "Patty Wongpakdee (Digital Art)"),
                             ("16W. 61st Street", "914", "9", "Office", "Sung (Kevin) Park (Digital Art)"),
                             ("16W. 61st Street", "915", "9", "Office", "Rozina Vavetsi (Digital Art)"),
                             ("16W. 61st Street", "916", "9", "Office", "Christine Kerigan (Digital Art), Michael Hosenfeld (Digital Art)"),
                             ("16W. 61st Street", "917", "9", "Lab", "Tracking System Research Lab"),
                             ("16W. 61st Street", "919", "9", "Lab", "Computer Lab"),
                             ("16W. 61st Street", "921", "9", "Other", "Plotter Room"),
                             ("16W. 61st Street", "922", "9", "Lab", "Computer Lab"),
                             ("16W. 61st Street", "923", "9", "Studio", "Art Studio"),
                             ("16W. 61st Street", "924", "9", "Lab", "Computer Lab"),
                             ("16W. 61st Street", "925", "9", "Studio", "Art Studio"),
                             ("16W. 61st Street", "927", "9", "Lab", "Computer Lab"),
                             ("16W. 61st Street", "928", "9", "Lab", "XR+Mid Air Gesture Research Lab"),
                             ("16W. 61st Street", "1011", "10", "Office", "Roger Yu (Physics)"),
                             ("16W. 61st Street", "1012", "10", "Office", "Miles Benepe (Physics), Lyubov Nakonechna (CoAS)"),
                             ("16W. 61st Street", "1013", "10", "Other", "Society of Physics Students"),
                             ("16W. 61st Street", "1016", "10", "Office", "Robert Alexander (Psychology), Melissa Huey (Psychology)"),
                             ("16W. 61st Street", "1017", "10", "Office", "Daniel Cinotti (Psychology), Lynn Rogoff (Humanities)"),
                             ("16W. 61st Street", "1018", "10", "Office", "Erin Fabian (CoAS), Nayoung Kim (Psychology)"),
                             ("16W. 61st Street", "1021", "10", "Studio", "TV Studio"),
                             ("16W. 61st Street", "1023", "10", "Lab", "Radio Lab"),
                             ("16W. 61st Street", "1026", "10", "Lab", "Game Design Lab"),
                             ("16W. 61st Street", "1027", "10", "Lab", "Human Factors and Neuroscience Lab"),
                             ("16W. 61st Street", "1028", "10", "Lab", "Cognitive Psychophysics Lab"),
                             ("16W. 61st Street", "1029", "10", "Classroom", ""),
                             ("16W. 61st Street", "1030", "10", "Office", "Uma Lyer (Math)"),
                             ("16W. 61st Street", "1032", "10", "Other", "Editing Room"),
                             ("16W. 61st Street", "1034", "10", "Other", "Video Equipment Rental Room"),
                             ("16W. 61st Street", "Auditorium", "11", "Other", "Auditorium"),
                             ("16W. 61st Street", "1104", "11", "Office", "Joyce Chiu (Management)"),
                             ("16W. 61st Street", "1106", "11", "Other", "Audiovisual Center"),
                             ("16W. 61st Street", "1118", "11", "Conference Room", ""),
                             ("16W. 61st Street", "1119", "11", "Conference Room", "");
                             
INSERT INTO Locations VALUES ("26W. 61st Street", "004", "0", "Other", "Spirituality Room"),
							 ("26W. 61st Street", "005", "0", "Lab", "Computer Lab"),
                             ("26W. 61st Street", "006", "0", "Other", "Grizzly Cupboard"),
                             ("26W. 61st Street", "007", "0", "Other", "Student Government Association"),
                             ("26W. 61st Street", "011", "0", "Classroom", ""),
                             ("26W. 61st Street", "012", "0", "Other", "Game Room"),
                             ("26W. 61st Street", "013", "0", "Other", "Esports Room"),
                             ("26W. 61st Street", "020", "0", "Other", "Security"),
                             ("26W. 61st Street", "021", "0", "Other", "Tech Threads"),
                             ("26W. 61st Street", "Lounge", "1", "Other", "Student Lounge"),
                             ("26W. 61st Street", "102", "1", "Office", "Office of Student Life"),
							 ("26W. 61st Street", "103", "1", "Office", "International Education"),
                             ("26W. 61st Street", "104", "1", "Office", "Office of Student Life"),
                             ("26W. 61st Street", "105", "1", "Office", "Office of Student Life"),
                             ("26W. 61st Street", "106", "1", "Office", "Office of Student Life"),
                             ("26W. 61st Street", "107", "1", "Office", "Office of Student Life"),
                             ("26W. 61st Street", "108", "1", "Office", "Office of Student Life"),
                             ("26W. 61st Street", "109", "1", "Office", "Office of Student Life"),
                             ("26W. 61st Street", "M02", "Mezzanine", "Office", "Susan Heim (Student Life)"),
                             ("26W. 61st Street", "M03", "Mezzanine", "Office", "Hannah Berling (Student life)"),
                             ("26W. 61st Street", "M04", "Mezzanine", "Office", "Robert DiGangi (student life)"),
                             ("26W. 61st Street", "205", "2", "Other", "Learning Center"),
							 ("26W. 61st Street", "206", "2", "Other", "Learning Center"),
                             ("26W. 61st Street", "207", "2", "Classroom", ""),
                             ("26W. 61st Street", "208", "2", "Office", "Askia VanOmmeren (CSEE)"),
                             ("26W. 61st Street", "209", "2", "Other", "CSEE Suite"),
                             ("26W. 61st Street", "210", "2", "Office", "Amy Bravo (CSEE)"),
                             ("26W. 61st Street", "211", "2", "Office", "Mick Bradley (CSEE)"),
                             ("26W. 61st Street", "212", "2", "Conference Room", ""),
                             ("26W. 61st Street", "216", "2", "Office", "Dennisia Cameron (CSEE)"),
                             ("26W. 61st Street", "217", "2", "Office", "Daniel Reddington (CSEE)"),
                             ("26W. 61st Street", "218", "2", "Office", "Francesca Bradley-Hightower (UAA)"),
                             ("26W. 61st Street", "219", "2", "Office", "Daniel Johnston (UAA)"),
                             ("26W. 61st Street", "220", "2", "Office", "Dylan Judge (UAA)"),
                             ("26W. 61st Street", "221", "2", "Office", "Sydney McMahon (UAA)"),
                             ("26W. 61st Street", "223", "2", "Office", "Tadiyos Gebre (ASE)"),
                             ("26W. 61st Street", "227", "2", "Office", "Peer Advising"),
                             ("26W. 61st Street", "228", "2", "Office", "Peer Advising"),
                             ("26W. 61st Street", "304", "3", "Office", "Raja Nag (LEAF)"),
                             ("26W. 61st Street", "305", "3", "Office", "Colleen Kirk (Management), Veneta Sotiropoulos (Management)"),
                             ("26W. 61st Street", "306", "3", "Office", "Shaya Sheikh (Management), Radoslaw Nowak (Management)"),
                             ("26W. 61st Street", "307", "3", "Office", "Bisrat Kinfemichael (LEAF)"),
                             ("26W. 61st Street", "308", "3", "Office", "Rajendra Tibrewala (Management)"),
                             ("26W. 61st Street", "310A", "3", "Office", "Tunc Ozelli (LEAF), Peter Harris (LEAF)"),
                             ("26W. 61st Street", "311", "3", "Office", "Adjunct Faculty Office"),
                             ("26W. 61st Street", "312", "3", "Classroom", ""),
                             ("26W. 61st Street", "313", "3", "Classroom", ""),
                             ("26W. 61st Street", "314", "3", "Classroom", ""),
                             ("26W. 61st Street", "318", "3", "Office", "Visiting Professor Office"),
                             ("26W. 61st Street", "401", "4", "Classroom", ""),
                             ("26W. 61st Street", "408", "4", "Other", "Board Meeting Room"),
                             ("26W. 61st Street", "409", "4", "Classroom", ""),
                             ("26W. 61st Street", "410", "4", "Classroom", ""),
                             ("26W. 61st Street", "411", "4", "Office", "William Ninehan (Management)"),
                             ("26W. 61st Street", "423", "4", "Office", "Scott Liu (Management)"),
                             ("26W. 61st Street", "501", "5", "Office", "Ellie Schwartz (Management), Patthara Chandaragga (Management)"),
                             ("26W. 61st Street", "502", "5", "Classroom", ""),
                             ("26W. 61st Street", "503", "5", "Other", "Technology Center Seminar Room"),
                             ("26W. 61st Street", "504", "5", "Other", "Simulation Trading Room"),
                             ("26W. 61st Street", "505", "5", "Office", "Rakesh Mittal (Management)"),
                             ("26W. 61st Street", "506", "5", "Office", "Joo-Kwang Yun (LEAF)"),
                             ("26W. 61st Street", "507", "5", "Office", "Maya Kroumova (Management)"),
                             ("26W. 61st Street", "509", "5", "Office", "Deborah Y. Cohn (Management)"),
                             ("26W. 61st Street", "510", "5", "Office", "Kevin O'Sullivan (Management)"),
                             ("26W. 61st Street", "511", "5", "Office", "Robert Koenig (Management)"),
                             ("26W. 61st Street", "512", "5", "Office", "Diamando Afxentiou (LEAF)");

INSERT INTO Locations VALUES ("Student Activities Building 1849 Broadway", "Lounge", "1", "Other", "Student Lounge");
UPDATE Locations SET loc_desc = "" WHERE loc_desc IS NULL;
UPDATE Buildings SET building_name = "Edward Guiliano Global Center 1855 Broadway" WHERE building_name = "Edward Guiliano Global Center/1855 Broadway";
UPDATE Buildings SET building_name = "Student Activities Building 1849 Broadway" WHERE building_name = "Student Activities Building/1849 Broadway";
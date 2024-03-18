const express = require("express");
const PORT = process.env.PORT || 3000;  // PORT depends on environment, or 3001 if there is none
const socket = require("socket.io");
const sqlite = require("sqlite3")
const app = express();
const server = app.listen(PORT);

app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(express.static('public')); //show static files in 'public' directory
console.log('Server is running');
const io = socket(server);

// Connects to local database
let db = new sqlite.Database('./naviar.db', function(err) {
  if (err) {
    console.error(err.message);
  }
  console.log("Connected to the database");
});
// Enables foreign key support for this database
db.get("PRAGMA foreign_keys = ON");

// Reads from local database and returns array of objects
async function db_all(query, params) {
	return new Promise(function(resolve, reject) {
		db.all(query, params, function(error, rows) {
			if (error) return reject(error);
			resolve(rows);
		});
	});
}

// Runs queries on local database that modifies its contents
async function db_run(query, params) {
	return new Promise(function(resolve, reject) {
		db.run(query, params, function(error) {
			if (error) return reject(error);
			resolve(this);
		});
	});
}

// Socket.io Connection
io.on('connection', function(socket) {
  console.log("New socket connection: " + socket.id)
  
  // Validates login and return user info
  socket.on('login', async function(userEmail, password) {
    console.log(userEmail);
    console.log(password);
    var userQuery = 'SELECT first_name, last_name, user_ID, is_Organizer FROM Users WHERE email = ? AND user_pass = ?';
    var userInfo = await db_all(userQuery, [userEmail, password]);
    if (userInfo[0] == undefined) {
      io.emit('login', false, "", 0, false);
    } else {
      var username = userInfo[0].first_name + " " + userInfo[0].last_name;
      var userID = userInfo[0].user_ID;
      var is_organizer = userInfo[0].is_Organizer
      console.log(is_organizer);
      console.log(username);
      io.emit('login', true, username, userID, is_organizer);
    }
  });

  // Sends a friend request and handles errors
  socket.on('addFriend', async function(userID, inputID) {
    console.log(userID);
    console.log(inputID);
    var msg;
    if (userID == inputID) {
      msg = "You can't add yourself as a friend.";
    } else {
      var findfriendship1 = await db_all('SELECT f_status FROM Friendship WHERE requestor = ? AND receiver = ?', [userID, inputID]);
      var findfriendship2 = await db_all('SELECT f_status FROM Friendship WHERE requestor = ? AND receiver = ?', [inputID, userID]);
      if (findfriendship1.length > 0) {
        if (findfriendship1[0].f_status == "pending") {
          msg = "You have already sent a friend request to this person."
        } else if (findfriendship1[0].f_status == "areFriends") {
          msg = "You are already friends with that person."
        }
      } else if (findfriendship2.length > 0) {
        if (findfriendship2[0].f_status == "pending") {
          msg = "That person has already sent a friend request to you."
        } else if (findfriendship2[0].f_status == "areFriends") {
          msg = "You are already friends with that person."
        }
      } else {
        var findValidUser = await db_all('SELECT 1 FROM Users WHERE user_ID = ?', [inputID]);
        if (findValidUser.length == 0) {
          msg = "Invalid student ID."
        } else {
          await db_run('INSERT INTO Friendship VALUES(?, ?, "pending")', [userID, inputID]);
          msg = "A friend request has been sent to " + inputID + ".";
        }
      }
    }
    
    var test = await db_all('SELECT * FROM Friendship', []);
    console.log(test);
    io.emit('addFriend', msg);
  });

  socket.on('getRequested', async function(userID) {
    var getFriendsQuery = 'SELECT u.user_ID, u.first_name, u.last_name FROM Users u, Friendship f WHERE f.requestor = ? AND u.user_ID = f.receiver;'
    var friendslist = await db_all(getFriendsQuery, [userID]);
    console.log(friendslist);
    io.emit('getRequested', friendslist);
  });
});
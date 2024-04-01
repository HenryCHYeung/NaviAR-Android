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
      socket.emit('login', false, "", 0, false);
    } else {
      var username = userInfo[0].first_name + " " + userInfo[0].last_name;
      var userID = userInfo[0].user_ID;
      var is_organizer = userInfo[0].is_Organizer
      console.log(is_organizer);
      console.log(username);
      socket.emit('login', true, username, userID, is_organizer);
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
    socket.emit('addFriend', msg);
  });

  socket.on('getRequests', async function(userID) {
    var getRequestedQuery = 'SELECT u.user_ID, u.first_name, u.last_name, u.email FROM Users u, Friendship f WHERE f.requestor = ? ' + 
    'AND u.user_ID = f.receiver AND f.f_status = "pending"';
    var requestsPending = await db_all(getRequestedQuery, [userID]);
    var getReceivedQuery = 'SELECT u.user_ID, u.first_name, u.last_name, u.email FROM Users u, Friendship f WHERE f.receiver = ? ' + 
    'AND u.user_ID = f.requestor AND f.f_status = "pending"';
    var receivedFriends = await db_all(getReceivedQuery, [userID]);
    var getFriendsQuery = 'SELECT u.user_ID, u.first_name, u.last_name, u.email FROM Users u, Friendship f WHERE ((f.requestor = ? ' + 
    'AND u.user_ID = f.receiver) OR (f.receiver = ? AND u.user_ID = f.requestor)) AND f.f_status = "areFriends"';
    var currentFriends = await db_all(getFriendsQuery, [userID, userID]);
    console.log(requestsPending);
    console.log(receivedFriends);
    console.log(currentFriends);
    socket.emit('getRequests', requestsPending, receivedFriends, currentFriends);
  });

  socket.on('rejectRequest', async function(userID, otherID) {
    await db_run('DELETE FROM Friendship WHERE requestor = ? AND receiver = ?', [otherID, userID]);
    socket.emit('rejectRequest', "Deleted");
  });

  socket.on('acceptRequest', async function(userID, otherID) {
    await db_run('UPDATE Friendship SET f_status = "areFriends" WHERE requestor = ? AND receiver = ?', [otherID, userID]);
    socket.emit('acceptRequest', "Accepted");
  });

  socket.on('cancelRequest', async function(userID, otherID) {
    await db_run('DELETE FROM Friendship WHERE requestor = ? AND receiver = ?', [userID, otherID]);
    socket.emit('cancelRequest', "Cancelled");
  });

  socket.on('unfriend', async function(userID, otherID) {
    var unfriendQuery = 'DELETE FROM Friendship WHERE (requestor = ? AND receiver = ?) OR (requestor = ? AND receiver = ?)';
    await db_run(unfriendQuery, [userID, otherID, otherID, userID]);
    socket.emit('unfriend', "Unfriended");
  });

  socket.on('getBuildings', async function() {
    var buildingsList = await db_all('SELECT building_name FROM Buildings WHERE campus = "NYC"', []);
    console.log(buildingsList);
    socket.emit('getBuildings', buildingsList);
  });

  socket.on('getRooms', async function(building) {
    var roomsList = await db_all('SELECT room_no FROM Locations WHERE building_name = ?', [building]);
    console.log(roomsList);
    socket.emit('getRooms', roomsList);
  });

  socket.on('createEvent', async function(eventName, building, room, dateTime, desc, userID) {
    var msg = "";

    try {
      await db_run('INSERT INTO Campus_Events VALUES(?, ?, ?, ?, ?, ?)', [eventName, dateTime, building, room, desc, userID]);
      msg = "Event created successfully."
    } catch(e) {
      if (e.message.includes("UNIQUE constraint failed")) {
        msg = "An event with this name already exists. Please use a different name."
      }
    }
    console.log(msg);
    socket.emit('createEvent', msg);
  });
});
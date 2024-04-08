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
      socket.emit('login', false, "", 0, 0);
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

  socket.on('createEvent', async function(eventName, building, room, startTime, endTime, desc, userID, attempt) {
    var msg = "";
    console.log(attempt);
    var existsQ = 'SELECT 1 FROM Campus_Events WHERE building_name = ? AND room_no = ? AND (NOT (end_time <= ? AND start_time < ?) '+ 
    'AND NOT (? <= end_time AND ? < start_time))';
    var existingTime = await db_all(existsQ, [building, room, startTime, endTime, startTime, endTime]);
    if (attempt == "first" && existingTime.length > 0) {
      msg = "overlaps";
    } else {
      try {
        await db_run('INSERT INTO Campus_Events VALUES(?, ?, ?, ?, ?, ?, ?)', [eventName, startTime, endTime, building, room, desc, userID]);
        msg = "Event created successfully."
      } catch(e) {
        if (e.message.includes("UNIQUE constraint failed")) {
          msg = "An event with this name already exists. Please use a different name."
        } else {
          console.log(e);
        }
      }
    }
    console.log(msg);
    socket.emit('createEvent', msg);
  });

  socket.on('getEvents', async function(userid, is_organizer, currentTime) {
    var createdList = [];
    var listPeoplePerEvent = [];
    if (is_organizer == 1) {
      var createdQuery = 'SELECT e.*, u.first_name, u.last_name FROM Campus_Events e, Users u WHERE organizer = ? AND u.user_ID = ?'
      createdList = await db_all(createdQuery, [userid, userid]);
    }
    var joinedQuery = 'SELECT e.*, u.first_name, u.last_name FROM Campus_Events e, Users u, Event_Participants p WHERE e.organizer' + 
    ' = u.user_ID AND p.participant = ? AND e.event_name = p.event_name';
    var userJoined = await db_all(joinedQuery, [userid]);

    var friendsEventsQ = 'SELECT DISTINCT e.*, u.first_name, u.last_name FROM Campus_Events e JOIN Users u ON e.organizer = ' + 
    'u.user_ID AND e.start_time > ? JOIN Event_Participants p ON e.event_name = p.event_name AND e.organizer <> ? JOIN Friendship f' +
    ' ON (p.participant = f.requestor AND f.receiver = ?) OR (p.participant = f.receiver AND f.requestor = ?)';
    var eventsWithFriendsList = await db_all(friendsEventsQ, [currentTime, userid, userid, userid]);

    var futureQuery = 'SELECT DISTINCT e.*, u.first_name, u.last_name FROM Campus_Events e, Users u WHERE e.organizer' + 
    ' = u.user_ID AND e.organizer <> ? AND e.start_time > ?';
    var otherFuture = await db_all(futureQuery, [userid, currentTime]);

    var listPeopleQ = 'SELECT DISTINCT u.first_name, u.last_name FROM Users u JOIN Event_Participants p ON p.event_name = ? AND ' +
    'p.participant = u.user_ID';
    var allEventList = createdList.concat(userJoined, eventsWithFriendsList, otherFuture);
    for (var i = 0; i < allEventList.length; i++) {
      listPeoplePerEvent.push(await db_all(listPeopleQ, [allEventList[i].event_name]));
    }
    console.log(allEventList.length);
    console.log(listPeoplePerEvent.length);
    console.log(allEventList);
    console.log(listPeoplePerEvent);
    socket.emit('getEvents', createdList, userJoined, listPeoplePerEvent, eventsWithFriendsList, otherFuture);
  });

  socket.on('deleteEvent', async function(userid, eventName) {
    await db_run('DELETE FROM Campus_Events WHERE organizer = ? AND event_name = ?', [userid, eventName]);
    console.log("Deleted");
    socket.emit('deleteEvent', "Event deleted");
  });

  socket.on('withdrawEvent', async function(userid, eventName) {
    await db_run('DELETE FROM Event_Participants WHERE participant = ? AND event_name = ?', [userid, eventName]);
    console.log("Withdrawn");
    socket.emit('withdrawEvent', "Withdrawn from event");
  });

  socket.on('joinEvent', async function(userid, eventName) {
    var msg = "";
    try {
      await db_run('INSERT INTO Event_Participants VALUES(?, ?)', [eventName, userid]);
      msg = "Joined";
    } catch(e) {
      if (e.message.includes("UNIQUE constraint failed")) {
        msg = "You have already joined this event."
      }
    }
    console.log(msg);
    socket.emit('joinEvent', msg);
  });
});
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

// Reads from local database and returns array of objects
async function db_all(query, params) {
	return new Promise(function(resolve, reject) {
		db.all(query, params, function(error, rows) {
			if (error) return reject(error);
			resolve(rows);
		});
	});
}

//Socket.io Connection
io.on('connection', function(socket) {
  console.log("New socket connection: " + socket.id)
  
  socket.on('login', async function(userEmail, password) {
    console.log(userEmail);
    console.log(password);
    var userInfo = await db_all('SELECT first_name, last_name, user_ID FROM Users WHERE email = ? AND user_pass = ?', [userEmail, password]);
    if (userInfo[0] == undefined) {
      io.emit('login', false, "", 0);
    } else {
      var username = userInfo[0].first_name + " " + userInfo[0].last_name;
      var userID = userInfo[0].user_ID;
      console.log(username);
      io.emit('login', true, username, userID);
    }
  })
})
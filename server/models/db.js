const { Pool,Client } = require('pg');
const config = require("../config/config.js");
const util = require('util');
const User = require("../models/user.model.js");

// Create a connection to the database
var connectionString = process.env.connectionString;

getClient = function () {
    return new Client({
        connectionString:connectionString,
        ssl: { rejectUnauthorized: false },
    });
};


module.exports = getClient;
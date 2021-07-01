const sql = require("./db.js");
const { NotFoundError } = require("../helpers/utility");

// constructor
const User = function(user) {
    if( typeof user.id !== 'undefined' ) {
        this.id = user.id;
    }

    this.device_id = user.device_id;
    this.ble_id = user.ble_id;
    this.fcm_id = user.fcm_id;
    this.name = user.name;

    if( typeof user.created_at !== 'undefined' ) {
        this.created_at = user.created_at;
    }
    if( typeof user.updated_at !== 'undefined' ) {
        this.updated_at = user.updated_at;
    }
};

User.create = async (newUser) => {
    let client = sql();
    try {

        client.connect();
        const query = {
            text: "INSERT INTO users\n" +
            "\t(device_id, ble_id, fcm_id, \"name\", created_at, updated_at)\n" +
            "\tVALUES($1,$2,$3,$4, now(), now())\n" +
            "on conflict (device_id) do update set ble_id=$2,fcm_id=$3,\"name\"=$4,updated_at = now()\n" +
            "RETURNING id",
            values: [newUser.device_id, newUser.ble_id, newUser.fcm_id, newUser.name],
        };

        let result = await client.query(query);

        if (result.rowCount)
            return result.rows[0].id;

    } catch (e) {
        console.log(e);
    } finally {
        try {
            client.end();
        } catch (e) {

        }
    }

    throw new NotFoundError("User does not exist");
};

User.markPositive = async (device_id,mark_time) => {
    let client = sql();

    try {
        client.connect();

        const date = new Date(mark_time);
        const query = {
            text: "INSERT INTO positives\n" +
            "(user_id, insertion_time, mark_time)\n" +
            "VALUES($1, now(), $2)\n" +
            "RETURNING insertion_time",
            values: [device_id, date],
        };

        let result = await client.query(query);

        if (result.rowCount)
            return true;
    } catch (e) {
        console.log(e);
    } finally {
        try {
            client.end();
        } catch (e) {

        }
    }

    throw new Error("Failed to mark user as positive");
};

User.login = async (value) => {
    let result;
    let client = sql();
    try {
        client.connect();
        const query = {
            text: 'SELECT * FROM users WHERE mobile = $1 OR email = $2',
            values: [value,value],
        };
        result = await client.query(query);
    }catch (e) {
        console.log(e);
    }finally {
        try {
            client.end();
        }catch (e) {
            
        }
    }
    if (result && result.rowCount) {
        return result.rows[0];
    }
    else {
        throw new NotFoundError("User does not exist");
    }
};

User.getPositives = async (last_seen) => {
    let client = sql();
    try {

        client.connect();
        const query = {
            text: "SELECT user_id, mark_time,insertion_time as created_at FROM positives\n" +
            "where insertion_time >= $1\n" +
            "order by insertion_time  asc",
            values: [last_seen],
        };
        let result = await client.query(query);
        return result.rows;
    } catch (e) {
        console.log(e);
    } finally {
        try {
            client.end();
        } catch (e) {
        }
    }

    throw new Error("Can't get positives");
};

User.getType = async (id) => {
    let row = await sql.query(`SELECT ut.name FROM users u LEFT JOIN user_types ut ON u.user_type_id = ut.id WHERE u.id = ?`, id);
    if( row.length ) {
        return row[0];
    }
    else {
        throw new NotFoundError("User does not exist");
    }
};

User.getActiveFcmIDs = async (user_id) => {
    let client = sql();
    try {
        client.connect();
        const query = {
            text: "SELECT fcm_id FROM users\n" +
            "where updated_at > now() - interval('14 days')\n" +
            "and device_id != $1\n",
            values: [user_id]
        };

        let result = await client.query(query);

        return result.rows;
    } catch (e) {
        console.log(e)
    } finally {
        try {
            client.end();
        } catch (e) {
        }
    }

    throw new Error("Can't get positives");
};

User.getUserFCMToken = async(userId)=> {
    let client = sql();
    try {
        client.connect();
        const query = {
            text: "SELECT fcm_id FROM users\n" +
            "where device_id = $1\n",
            values: [userId]
        };

        let result = await client.query(query);
        return result.rows;
    }catch (e) {
        console.log(e);
    }finally {
        try{
            client.end();
        }catch (e) {
        }
    }

    throw new Error("Can't get positives");
};

module.exports = User;
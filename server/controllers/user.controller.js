const User = require("../models/user.model.js");
const config = require("../config/config");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const { sendNewPositive, sendPing } = require("../helpers/firebaseHelper");
const {NotFoundError} = require("../helpers/utility");

// Register a new User
exports.register = async (req, res) => {

    // Create an user object
    const user = new User({
        name: req.body.name,
        ble_id: req.body.ble_id,
        device_id: req.body.device_id,
        fcm_id: req.body.fcm_id
    });
    try {
        const id = await User.create(user);
        user.id = id;
        console.log("user_created: " + id);
        res.send(user);
    }
    catch (err){
        console.log("failed to create user , " + user.device_id);
        console.log(err)
        res.status(500).send(err);
    }
};

exports.set_positive = async (req, res) => {
    // Create an user object
    const device_id = req.body.device_id;
    const mark_time = req.body.mark_time;

    try {
        const success = await User.markPositive(device_id,mark_time);
        if(success) {
            sendNewPositive(device_id,mark_time,new Date());
            res.status(200).send({success:true});
        }
    }
    catch (err){
        console.log("failed to markPositive , " + device_id);
        console.log(err)
        res.status(500).send(err);
    }
};

exports.getPositives = async (req, res) => {
    // Create an user object
    const last_seen = req.body.last_seen;

    try {
        const rows = await User.getPositives(last_seen);
        res.send(rows);
    }
    catch (err){
        console.log("failed to getPositives " );
        console.log(err)
        res.status(500).send(err);
    }
};


// Login
exports.login = async (req, res) => {
    try {
        // Check user exist
        const user = await User.login(req.body.mobile_or_email);
        if (user) {
            const validPass = await bcrypt.compare(req.body.password, user.password);
            if (!validPass) return res.status(400).send("Mobile/Email or Password is wrong");

            // Create and assign token
            const token = jwt.sign({id: user.id, user_type_id: user.user_type_id}, config.TOKEN_SECRET);
            res.header("auth-token", token).send({"token": token});
            // res.send("Logged IN");
        }
    }
    catch (err) {
        console.log(err);
        if( err instanceof NotFoundError ) {
            res.status(401).send(`Mobile/Email or Password is wrong`);
        }
        else {
            let error_data = {
                entity: 'User',
                model_obj: {param: req.params, body: req.body},
                error_obj: err,
                error_msg: err.message
            };
            res.status(500).send("Error retrieving User");
        }
    }   
    
};

exports.sendSoundPing = async (req,res)=> {
    try {
        const user_id = req.body.user_id;
        const target_id = req.body.target_id;

        try {
            console.log("trying to get user fcm",target_id);
            const rows = await User.getUserFCMToken(target_id);

            if(rows && rows.length) {
                console.log("fcm token found",rows[0]["fcm_id"]);

                const success = await sendPing("start_listening",user_id,target_id,rows[0]["fcm_id"]);
                console.log("ping sent");
                if(success){
                    res.status(200).send("success");
                }else{
                    res.status(500).send("Something went Wrong");
                }
            }else{
                res.status(500).send("Something went wrong");
            }
        }
        catch (err){
            console.log("failed to markPositive , " + user_id);
            console.log(err);
            res.status(500).send(err);
        }
    } catch (err) {
        console.log(err);
        res.status(500).send("Error retrieving User");
    }
};

exports.soundReceived = async (req,res)=> {
    try {
        const user_id = req.body.user_id;
        const target_id = req.body.target_id;

        try {
            console.log("trying to get user fcm",target_id);
            const rows = await User.getUserFCMToken(target_id);

            if(rows && rows.length) {
                console.log("fcm token found",rows[0]["fcm_id"]);

                const success = await sendPing("sound_received",user_id,target_id,rows[0]["fcm_id"]);
                console.log("ping sent");
                if(success){
                    res.status(200).send("success");
                }else{
                    res.status(500).send("Something went Wrong");
                }
            }else{
                res.status(500).send("Something went wrong");
            }
        }
        catch (err){
            console.log("failed to markPositive , " + user_id);
            console.log(err);
            res.status(500).send(err);
        }
    } catch (err) {
        console.log(err);
        res.status(500).send("Error retrieving User");
    }
};


// Access auth users only
exports.authuseronly = (req, res) => {
    res.send("Hey,You are authenticated user. So you are authorized to access here.");
};

// Admin users only
exports.adminonly = (req, res) => {
    res.send("Success. Hellow Admin, this route is only for you");
};


// Access auth users only
exports.hello = (req, res) => {
    res.send("Hey,You are authenticated user. So you are authorized to access here.");
};

exports.sendFcmTest =(req,res)=>{
    sendNewPositive("1233",new Date(),new Date());
}


var admin = require("firebase-admin");
const User = require("../models/user.model");
var serviceAccount = JSON.parse(process.env.firebase_secret_json);

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

module.exports.firebase = admin;

exports.sendNewPositive = async(user_id, mark_time, insertion_time) => {
    var topic = 'positives';

    var message = {
        data:{
            "type": "positive_detected",
            "user_id": user_id,
            "mark_time": mark_time.toString(),
            "insertion_time": insertion_time.getTime().toString()
        },
        topic: topic
    };

    try {
        admin.messaging().send(message)
            .then((response) => {
                // Response is a message ID string.
                console.log('Successfully sent message:', response);
            })
            .catch((error) => {
                console.log('Error sending message:', error);
            });
    }catch (e){
        console.log(e);
        console.error("Failed to get active ids");
    }
};


exports.sendPing = async(type,user_id,target_id,target_fcm_id) => {

    var message = {
        data:{
            "type": type,
            "user_id": user_id,
            "target_id": target_id,
        },
    };

    try {
        const success = await admin.messaging().sendToDevice(target_fcm_id, message);
        console.log("sent message");
        return success.failureCount === 0;
    }catch (e){
        console.log(e);
        console.error("Failed to get active ids");
    }
    return false;
};


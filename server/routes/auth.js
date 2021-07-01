const router = require('express').Router();
const {loggedIn, adminOnly} = require("../helpers/auth.middleware");
const userController = require('../controllers/user.controller');


// router.post('/login', userController.login);

router.post('/register',userController.register);

router.post('/mark_positive',userController.set_positive);

router.post('/get_positives',userController.getPositives);

router.get('/send_fcm_test',userController.sendFcmTest);

router.post('/sound_ping',userController.sendSoundPing);

router.post('/sound_received',userController.soundReceived);

router.get('/',userController.hello)

module.exports = router;
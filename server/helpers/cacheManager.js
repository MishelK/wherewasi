const NodeCache = require( "node-cache" );
const myCache = new NodeCache({ stdTTL: 100, checkperiod: 120 });

exports.setKeyInStore = async (user_id,targets) => {
    let objects = targets.map((t) => {
        return ({key: t, val: user_id})
    });
    objects.push({key: user_id, val: user_id});
    myCache.mset(objects);
};

exports.checkIfAvailable = async(user_id,targets)=> {
    let keys = targets;
    keys.push(user_id);
    let result = myCache.mget(keys);
    //returns empty object ({}) if no results found or expired.
    // If the value was found it returns an object with the key value pair.
    return !!(result && Object.keys(result).length === 0);
};

exports.removeKeys = async(user_id,target_id) =>{
    myCache.del([user_id,target_id]);
};
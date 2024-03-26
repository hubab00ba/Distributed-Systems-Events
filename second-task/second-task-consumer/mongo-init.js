db.getSiblingDB('second-task');
db.createUser({user: 'mongo_user', pwd: 'password',roles: [{role: 'readWrite',db: 'second-task'} ]});
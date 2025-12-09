const { utils } = require('../utils');

class UserMiddleware {

	constructor(context, repositories) {
		this.context = context;
        this.repositories = context.repositories;
	}

	createUser() { 
		return async (req, res, next) => {
            if(!req.body) {
                return next(utils.buildError(400, 'Body is empty'))
            }
            const login = req.body.login;
            const password = req.body.password;

            if (!login || login == 'undefined') {
                return next(utils.buildError(400, '"login" is empty'))
            }
            if (!utils.isEmailValid(login)) {
                return next(utils.buildError(400, '"login" should be a valid email format'))
            }
            if (!password || password == 'undefined' || password.length < 5 || password.includes(' ')) {
                return next(utils.buildError(400, '"password" is empty or incorrect (should not include spaces, should contains at least 5 or more symbols)'))
            }

            try {
            	const user = await this.repositories.users.createUser(login, password);
            	res.send(utils.wrapResult(user));
        	} catch (err) {
        		next(err);
        	}
		}
	}


    markNotificationsAsSeen() {
        return async (req, res, next) => {
            try {
                const currentUserId = res.locals.session.user._id.toString();
                const result = await this.repositories.users.updateUserSettings(
                    currentUserId,
                    { notificationsSeen: new Date() }
                );
                await this.repositories.notifications.markNotificationsOfUserAsSeen(currentUserId);
                res.send(utils.wrapResult('ok'));
            } catch (err) {
                next(err);
            }            
        }
    }
}

exports.create = (context) => new UserMiddleware(context);
const { utils } = require('../utils');


class NotificationMiddleware {

	constructor(context) {
		this.context = context;
        this.repositories = context.repositories;
	}

	fetchNotifications() { 
		return async (req, res, next) => {
            try {
            	const notifications = await this.repositories.notifications.getNotifications();
            	res.send(utils.wrapResult({
                    items: notifications
                }));
        	} catch (err) {
        		next(err);
        	}
		}
	}

	fetchNotificationById() {
		return async (req, res, next) => {
			try {
				const id = req.params.id;
				const notification = await this.repositories.notifications.getNotificationById(id);
				res.send(utils.wrapResult({notification}));
			} catch (err) {
				next(err);
			}
		}
	}

}


exports.create = (context) => new NotificationMiddleware(context);
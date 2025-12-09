const ObjectId = require("mongodb").ObjectId;
const { utils } = require('../utils');
var iconv = require('iconv-lite');

class NotificationWorker {

	constructor(context) {
		this.context = context;
	}

    async doWork() {
	    console.log("Работает notificationWorker");

	    const userIds = this.context.repositories.subscriptions.getSubscriptions();
	    const lastJoke = await this.context.repositories.jokes.getLastJoke();

	    // Создаём нотификации
	    const notificationPromises = userIds.map((userId) => {
	        return this.context.repositories.notifications.createNotification(
	            {
	                title: "Новый анекдот",
	                text: lastJoke.text,
	                image: "",
	                silent: false
	            },
	            lastJoke._id,
	            userId,
	        )
	    });
	    await Promise.all(notificationPromises);
    }

}

exports.create = (context) => new NotificationWorker(context); 
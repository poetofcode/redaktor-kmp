const ObjectId = require("mongodb").ObjectId;
const { utils } = require('../utils');
var iconv = require('iconv-lite');

class PushWorker {

	constructor(ctx) {
		this.context = ctx;
    	if (this.context.config.fcmEnabled) {
			const createPushSender = require('../utils/push_sender.js').create;
			this.pushSender = createPushSender();
    	}

    	// this.sendTestPush();
	}


	// async sendTestPush() {
	// 	const token = 'ekFu_m9-QZmDaLgMTp9WWc:APA91bEDWZxJezv0OUBKXVETahSs1xnzabVkao-tzMFG9ymeg0IZ1cBw8JjFe3PwMy7XKMNmRug0TpxnNs_cYz4Ha-LJV-lwtanWeOVJe_A8iKrgshVzWw-uprPRjC_kuXBS32pEKnrA';
	// 	this.pushSender.sendPush({
	// 		title: "Пуш", text: "Хард пуш месседж"
	// 	}, [token])
	// 	    .then((response) => {
	// 	        // Просмотр успешных отправок
	// 	        console.log(`${response.successCount} сообщений успешно отправлено`);
	// 	        console.log(`${response.failureCount} сообщений не удалось отправить`);

	// 	        // Если есть ошибки, пройтись по каждому результату
	// 	        response.responses.forEach((resp, idx) => {
	// 	            if (!resp.success) {
	// 	                console.error(`Ошибка при отправке на устройство с токеном ${token}:`, resp.error);
	// 	            }
	// 	        });
	// 	    })
	// 	    .catch((error) => {
	// 	        console.error('Ошибка при отправке сообщения:', error);
	// 	    });
	// }


    async doWork() {
    	if (!this.context.config.fcmEnabled) {
    		return;
    	}
	    console.log(`Работает PushWorker`);

	    // Достаём непрочитанные нотификации
	    const unreadNotifications = await this.context.repositories.notifications.getUnreadNotifications();
	    const sessionsPromises = unreadNotifications.map((item) => {
	        return this.context.repositories.sessions.fetchActiveSessionsByUserId(item.userId);
	    });
	    const sessionsArr = await Promise.all(sessionsPromises);
	    const mergedArr = unreadNotifications.map((item, index) => {
	        item.sessions = sessionsArr[index];
	        return item;
	    });

	    const withPushTokens = mergedArr.map((item) => {
	        const pushTokens = item.sessions.filter((s) => {
	            return s.params && s.params.pushToken
	        })
	        .map((s) => s.params.pushToken );
	        item.pushTokens = pushTokens;
	        return item;
	    }).filter((item) => item.pushTokens.length > 0);

	    const notSentPromises = withPushTokens.map((item) => {
	        return this.context.repositories.notificationStatuses.filterPushTokensNotSent(item._id, item.pushTokens);
	    });
	    const pushTokensNotSent = await Promise.all(notSentPromises);


	    const withTokensNotSentPromises = withPushTokens.map((item, index) => {
	        item.pushTokens = pushTokensNotSent[index];
	        return item;
	    })
	    .filter((item) => item.pushTokens.length > 0)
	    .map((item) => {
	    	const res = this.pushSender.sendPush(item, item.pushTokens);
	       return res;
	    });

	    try {
		    const sendResponses = await Promise.all(withTokensNotSentPromises);

		    // console.log("Send responses:");
		    // console.log(sendResponses);

		    if (sendResponses.length == 0) {
		        return;
		    }

		    // Сохраняем статусы отправки
		    const createStatusPromises = withPushTokens.map((item, index) => {
		        if (sendResponses[index] && sendResponses[index].responses) {
		            item.sendResponse = sendResponses[index].responses;
		        }

		        return item;
		    })
		    .filter((item) => !item.sendResponses)
		    .map((item) => {
		        return Promise.all(item.pushTokens.map((t, idx) => {
		        	const error = item.sendResponse[idx].success ? null : item.sendResponse[idx].error;
		            return this.context.repositories.notificationStatuses.createStatus(item._id, t, item.sendResponse[idx].success, error)
		        }));
		    });
		    Promise.all(createStatusPromises);
		} catch (err) {
			console.log("Sending pushes error:", err);
		}
    }

}

exports.create = (context) => new PushWorker(context); 
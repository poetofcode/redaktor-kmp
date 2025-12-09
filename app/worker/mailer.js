const ObjectId = require("mongodb").ObjectId;
const { utils } = require('../utils');
const createMailer = require('../utils/mailer.js').createMailer;

class MailerWorker {

    constructor(context) {
        this.context = context;
    }

    async doWork() {
        console.log('Работает MailerWorker');

        if (!this.mailer) {
            this.mailer = createMailer(this.context);
            await this.mailer.init();
        }

        const userIds = this.context.repositories.subscriptions.getSubscriptions();
        const lastJoke = await this.context.repositories.jokes.getLastJoke();
        
        const sendPromises = userIds.map((userId) => this.sendOneMail(userId, lastJoke));
        await Promise.all(sendPromises);
    }

    async sendOneMail(userId, lastJoke) {
        const foundUser = await this.context.repositories.users.findUserById(userId);
        const mailerStatus = await this.mailer.send(foundUser.login, lastJoke.text);
    }
}

exports.create = (context) => new MailerWorker(context);
const nodemailer = require('nodemailer');

class Mailer {
    constructor(context) {
        this.context = context;
    }

    async init() {
		this.testEmailAccount = await nodemailer.createTestAccount();
		this.transporter = nodemailer.createTransport({
		    host: 'smtp.ethereal.email',
		    port: 587,
		    secure: false,
		    auth: {
		        user: this.testEmailAccount.user,
		        pass: this.testEmailAccount.pass,
		    },
		});

		// GMAIL
		// this.transporter = nodemailer.createTransport({
		//     service: 'gmail',
		//     auth: {
		//         user: 'login',
		//         pass: 'pass',
		//     },
		// });

		// YANDEX
		// this.transporter = await nodemailer.createTransport({
	 	// 	   service: 'Yandex',
		//     auth: {
		//         user: "login", // generated ethereal user
		//         pass: "pass" // generated ethereal password
		//     }
		// });
    }

    async send(email, jokeText) {
        console.log(`[Mailer] email: ${email}, joke: ${jokeText}`);

		let result = await this.transporter.sendMail({
		    from: `"Nodemailer" <${this.testEmailAccount.user}>`,
		    to: email,
		    subject: 'New joke',
		    text: jokeText,
		    html:
		        'This <i>message</i> was sent from <strong>Node js</strong> server.',
		});

		console.log("[Mailer] result:");
		console.log(result);
    }
}


function createMailer(context) {
	return new Mailer(context);
}

exports.createMailer = createMailer;
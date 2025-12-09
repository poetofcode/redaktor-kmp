const app = require('./app').app;
var fs = require('fs');

(async () => {
	try {
		console.log("Starting...");
		const env = process.env.redaktor_ENV || "dev";
		console.log(`env = ${env}`);
		const configurations = JSON.parse(fs.readFileSync('config.json', 'utf8'));
		const config = configurations[env];
		await app.start(config);
	    console.log(`Server running: http://127.0.0.1:${config.port}/front`);
	} catch(err) {
	    return console.log(err);
	} 
})();

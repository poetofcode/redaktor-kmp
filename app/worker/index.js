const { utils } = require('../utils');

async function launch(context) {
    console.log("Workers started");

    const workers = {};
    (await utils.requireAll('app/worker/')).forEach((name, value) => {
        workers[name] = value.create(context);
    });

    const withIntervals = [
        // [ workers.jokes, seconds(120) ],
        // [ workers.mailer, seconds(5) ]
        // [ new SerialWorker([workers.notifications, workers.pushes]), seconds(10) ]
    ];

    withIntervals.forEach((w) => {
        const worker = w[0];
        const interval = w[1];
        utils.setIntervalImmediately(async () => {
            try {
                worker.doWork()
            } catch(err) {
                console.error(err);
            }
        }, interval);
    });
}

function seconds(sec) {
    return sec * 1000;
}

class SerialWorker {

    constructor(children) {
        this.children = children;
    }

    async doWork() {
        for (const w of this.children) {
            await w.doWork();
        }
    }
}

exports.launch = launch;
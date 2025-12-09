const { utils } = require('../utils');

class SessionMiddleware {

    constructor(context) {
        this.context = context;
        this.repositories = context.repositories;
    }

    createSession() {
        return async(req, res, next) => {
            if(!req.body) {
                return next(utils.buildError(400, 'Body is empty'))
            }
            const login = req.body.login;
            const password = req.body.password;

            if (!login || login == 'undefined') {
                return next(utils.buildError(400, '"login" is empty'))
            }
            if (!password || password == 'undefined') {
                return next(utils.buildError(400, '"password" is empty'))
            }

            const foundUser = await this.repositories.users.findUserByLogin(login);
            if (!foundUser) {
                return next(utils.buildError(400, `User is not found, wrong login or password`));
            }

            // TODO uncomment
            /*
            if (!foundUser.activated) {
                return next(utils.buildError(400, `User is not activated yet`));   
            }
            */

            if (utils.sha1(password) !== foundUser.password) {
                return next(utils.buildError(400, `User is not found, wrong login or password`));
            }

            try {
                const clientIP = parseIp(req);
                const clientType = req.header('x-client-type');
                const clientVersion = req.header('x-client-version');
                const session = await this.repositories.sessions.createSession(
                    foundUser._id, 
                    clientIP,
                    clientType,
                    clientVersion
                );
                res.send(utils.wrapResult(session));
            }
            catch(err) {
                next(err);
            }              
        }
    }

    fetchSessions() {
        return async(req, res, next) => {
            try {
                const sessions = await this.repositories.sessions.fetchSessionsAll();
                res.send(utils.wrapResult(sessions));
            }
            catch(err) {
                next(err);
            }  
        }
    }

    fetchSessionByToken() {
        return async(req, res, next) => {
            try {
                const token = req.params.token;
                const session = await this.repositories.sessions.fetchSessionByToken(token);
                res.send(utils.wrapResult(session));
            }
            catch(err) {
                err.status = 400;
                return next(err)
            }  
        }
    }

    deleteSessionByToken() {
        return async(req, res, next) => {
            try {
                const token = req.params.token;
                await this.repositories.sessions.deleteSessionByToken(token);
                res.send(utils.wrapResult('ok'));
            }
            catch(err) {
                next(err);
            }  
        }
    }

    saveFirebasePushToken() {
        return async(req, res, next) => {
            try {    
                if(!req.body) {
                    return next(utils.buildError(400, 'Body is empty'))
                }
                const currentSession = res.locals.session;
                const pushToken = req.body.pushToken;

                if (!pushToken || pushToken == 'undefined') {
                    return next(utils.buildError(400, '"pushToken" is empty'))
                }

                await this.repositories.sessions.saveSessionParams(
                    currentSession._id,
                    { pushToken: pushToken }
                )
                res.send(utils.wrapResult('ok'));
            }
            catch (err) {
                next(err);
            }
        }
    }

}

const parseIp = (req) =>
    req.headers['x-forwarded-for']?.split(',').shift()
    || req.socket?.remoteAddress


exports.create = (context) => new SessionMiddleware(context);
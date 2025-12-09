const ObjectId = require("mongodb").ObjectId;
const crypto = require('crypto');

class SessionRepository {

    constructor(context) {
        this.context = context;
        this.db = context.getDb();
        this.sessionCollection = this.db.collection('sessions');
    }

    async createSession(userId, clientIP, clientType, clientVersion) {
        const newSession = {
            token: crypto.randomUUID(),
            createdAt: new Date(),
            userId: new ObjectId(userId),
            ip: clientIP,
            clientType: clientType,
            clientVersion: clientVersion
        }
        const sessions = await this.sessionCollection.insertOne(newSession);
        const user = await this.context.repositories.users.findUserById(newSession.userId.toString());
        delete newSession.userId;
        newSession.user = user;
        return newSession;
    }

    async fetchSessionsAll() {
        const sessions = await this.sessionCollection.find({}).toArray();
        return sessions;
    }

    async fetchSessionByToken(token) {
        const session = await this.sessionCollection.findOne({ token : token });
        if (!session) {
            throw new Error('Not found session');
        }
        const user = await this.context.repositories.users.findUserById(session.userId.toString());
        delete session.userId;
        session.user = user;
        return session;
    }

    async fetchActiveSessionsByUserId(userId) {
        const sessions = await this.sessionCollection.find({ 
            userId: userId 
            // TODO Тут нужно запрашивать только актуальные сессии
            //      например те, по которым была активность 
            //      в какой то ограниченный период времени
        }).toArray();
        return sessions;
    }


    async deleteSessionByToken(token) {
        const result = await this.sessionCollection.deleteOne({ token : token });
        return result;
    }

    async saveSessionParams(sessionId, newParams) {
        const res = await this.sessionCollection.updateOne(
            { _id: sessionId },
            { 
                $set: { params: newParams }
            }
        );
        return res;
    }

}

exports.create = (context) => new SessionRepository(context);
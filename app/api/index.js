const { utils } = require('../utils');

async function initRoutes(router, context) {
	const repositories = {};
	context.repositories = repositories;

	(await utils.requireAll('app/repository/')).forEach((name, value) => {
		repositories[name] = value.create(context);
	});

	const middlewares = {};
	(await utils.requireAll('app/api/')).forEach((name, value) => {
		middlewares[name] = value.create(context);
	});

	const sessionMiddleware = middlewares.sessions;
	const sessionRepository = repositories.sessions;

	router.use(handleTokenValidation(sessionMiddleware, sessionRepository));

	router.post('/sessions', sessionMiddleware.createSession());
	router.get('/sessions', sessionMiddleware.fetchSessions());
	router.get('/sessions/:token', sessionMiddleware.fetchSessionByToken());
	router.delete('/sessions/:token', sessionMiddleware.deleteSessionByToken());
	router.post('/sessions/push_token', sessionMiddleware.saveFirebasePushToken());

	router.post('/users', middlewares.users.createUser());
	router.post('/users/me/notifications/seen/', middlewares.users.markNotificationsAsSeen());

	// router.post('/subscriptions', middlewares.subscriptions.subscribe());
	// router.delete('/subscriptions', middlewares.subscriptions.unsubscribe());
	// router.get('/subscriptions/me', middlewares.subscriptions.getSubscription());

	// router.get('/jokes', middlewares.jokes.fetchJokes());

	router.get('/notifications', middlewares.notifications.fetchNotifications());
	router.get('/notifications/:id', middlewares.notifications.fetchNotificationById());

	router.use(handleErrors);
}


function handleTokenValidation(sessionMiddleware, sessionRepository) {
	return async (req, res, next) => {
		const authHeader = req.header('Authorization');

		// White-list эндпоинтов без авторизации:
		const isSessionsPost = req.path === '/sessions' && req.method === 'POST';
		const isSessionByTokenGet = req.path.startsWith("/sessions/") && req.path !== "/sessions/" && req.method === 'GET';
		const isSessionByTokenDelete = req.path.startsWith("/sessions/") && req.path !== "/sessions/" && req.method === 'DELETE';
		const isUserCreate = req.path.startsWith('/users') && req.method === 'POST' && !req.path.startsWith('/users/');
		const isJokesGet = req.path.startsWith('/jokes');

		if (isSessionsPost || isSessionByTokenGet || isSessionByTokenDelete || isUserCreate || isJokesGet) {
			return next();
		}

		if (authHeader) {
			const token = authHeader.replace('Bearer ', '');
			try {
		        const session = await sessionRepository.fetchSessionByToken(token);
		        if (session) {
		        	res.locals.session = session;
		        	return next();
		        }
	    	} catch (err) {
	    		console.log(err);
	    	}
		}

		res.status(401).send(utils.wrapError(new Error('Not authorized')));
	}
}


function handleErrors(err, req, res, next) {
  if (res.headersSent) {
	return next(err)
  }
  console.log(err);
  res.status(err.status || 500).send(utils.wrapError(err));
}


exports.initRoutes = initRoutes;
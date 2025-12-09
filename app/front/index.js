const AuthMiddleware = require('./auth.js').AuthMiddleware;
const cookieParser = require('cookie-parser');

function initRoutes(router, context) {
	router.use(async function (req, res, next) {
		const token = req.cookies.token;

		console.log("Headers:");
		console.log(req.headers);

		if (!token && req.path === '/login') {
			return next();
		}
		if (!token && req.path !== '/login') {
			return res.redirect('/front/login');
		}

		try {
			const result = (await context.apiGet(`/sessions/${token}`)).data.result;
			if (result) {
				if (req.path === '/login') {
					return res.redirect('/front');
				} else {
					return next();
				}
			}

		} catch (err) {
			if (err.response.status == 400) {
				res.clearCookie('token');
				return res.redirect('/front/login');
			} else {
				return next(err);
			}
		}

		next();
	});

	const authMiddleware = new AuthMiddleware(context);

	router.use(function (req, res, next) {
	    switch (req.path) {
	        case '/login':
	            res.locals.isLogin = true;
	            break;
	        default:
	            res.locals.isLogin = false;
	    }
	    next();
	});

	router.get('/login', authMiddleware.loginPage());
	router.post('/login', authMiddleware.loginAction());

	router.get('/', indexPage());

	router.use((err, req, res, next) => {
	  if (res.headersSent) {
    	return next(err)
      }
	  console.error(err.stack);
	  res.render('error', { error: err });
	});

}

function indexPage() {
	return async(req, res, next) => {
		try {
			res.render("index.hbs");
		} catch(err) {
			next(err);
		}
	}
}


exports.initRoutes = initRoutes;
const cookieParser = require('cookie-parser');

class AuthMiddleware {

	constructor(context) {
		this.context = context;
	}

	loginPage() {
		return async(req, res, next) => {
			try {
				res.render("signin.hbs");
			} catch(err) {
				next(err);
			}
		}
	}

	loginAction(req, res) {
		return async(req, res, next) => {
			try {
	            var response = await this.context.apiPost(`/sessions`, {
	            	login: req.body.username,
	            	password: req.body.password
	            });
	            const result = response.data.result;
				res.cookie('token', result.token, {
					sameSite: 'none', 
					secure: false,
					httpOnly: true
				});

				// res.redirect('/front');
				return res.render("post_auth.hbs", { 
					token: result.token, 
					email: result.user.login 
				});

			} catch(err) {
	            if (err.response.status == 400) {
	            	return res.render("signin.hbs", { authError: true });
	            }
				next(err);
			}
		}
	}

}

exports.AuthMiddleware = AuthMiddleware;
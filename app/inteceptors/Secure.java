package inteceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import constants.Constants;
import play.mvc.Before;
import play.mvc.Controller;

public class Secure extends Controller {

	@Before
	static void checkAuthenticated() {
		if (session.contains(Constants.CURRENT_USERNAME)) {
			// The user is authenticated,
			// add User object to the renderArgs scope
			// User authenticated = User.findByUsername(session.get("user"));
			// renderArgs.put("user", authenticated);
		} else {
			// The user is not authenticated,
			// redirect to the login form
			// Authentication.login();
		}
	}

	@Before
	static void checkAuthorization() {
		Admin adminAnnotation = getActionAnnotation(Admin.class);
		if (adminAnnotation != null) {
			// The action method is annotated with @Admin,
			// check the permission
			// if (!renderArgs.get("user", User.class).isAdmin()) {
			// The connected user is not admin;
			forbidden("You must be admin to see this page");
			// }
		}
	}

	@Target({ ElementType.METHOD, ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Admin {
	}

}
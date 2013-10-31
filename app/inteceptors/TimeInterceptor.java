package inteceptors;

import java.util.Date;

import play.Logger;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;

public class TimeInterceptor extends Controller {

	@Before(unless = { "Home.logout" })
	static void start() {
		session.put("_execute_time_", (new Date()).getTime());
	}

	@After(unless = { "Home.logout" })
	static void end() {
		long time = (new Date()).getTime() - Long.parseLong(session.get("_execute_time_"));
		session.put("_execute_time_", time);
		Logger.info("Action %s execution time is %d ms", request.action, time);
	}

}
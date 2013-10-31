package inteceptors;

import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;

public class ConstantsInterceptor extends Controller {

	@Before()
	static void init() {
		// renderArgs.put(Constants.GLOBLE_FACILITIES,
		// Cache.get(Constants.GLOBLE_FACILITIES));
		// renderArgs.put(Constants.GLOBLE_EVENT_SESSIONS,
		// Cache.get(Constants.GLOBLE_EVENT_SESSIONS));
	}

}
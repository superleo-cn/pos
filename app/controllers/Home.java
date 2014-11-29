package controllers;

import constants.Constants;
import constants.Pages;

public class Home extends Basic {

	public static void index() {
		if (session.get(Constants.CURRENT_USERID) == null) {
			redirect("/login");
		}
		render(Pages.HOME);
	}

}
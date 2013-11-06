package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.User;
import play.Logger;
import constants.Constants;
import constants.Pages;

public class Auth extends Basic {

	public static void index() {
		render(Pages.LOGIN);
	}

	public static void loginJson(User user) {
		Map result = new HashMap();
		try{
			List datas = new ArrayList();
			User dbUser = User.login(user);
			if (dbUser != null) {
				Logger.info("Login successfully");
				datas.add(dbUser);
				result.put(Constants.STATUS, Constants.SUCCESS);
				result.put(Constants.DATAS, datas);
			} else {
				Logger.info("Login unsuccesfully");
				result.put(Constants.STATUS, Constants.FAILURE);
				result.put(Constants.DATAS, datas);
			}
		} catch (Exception e) {
			Logger.error(e, "Login error happend");
			result.put(Constants.ERROR, Constants.FAILURE);
			result.put(Constants.MESSAGE, "Error Happend. " + e.getMessage());
		}
		renderJSON(result);
	}

}
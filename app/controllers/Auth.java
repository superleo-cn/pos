package controllers;

import java.util.HashMap;
import java.util.Map;

import models.User;
import play.Logger;
import play.cache.Cache;
import play.i18n.Lang;
import constants.Constants;
import constants.Pages;

public class Auth extends Basic {

	public static void index() {
		render(Pages.LOGIN);
	}

	public static void login(User user) {
		User result = User.login(user);
		Map data = new HashMap();
		if (result != null) {
			Logger.info("Login successfully");
			data.put(Constants.HTTP_CODE, Constants.CODE_SUCCESS);
			data.put(Constants.DATAS, Constants.SUCCESS);
		}else{
			Logger.info("Login unsuccesfully");
			data.put(Constants.HTTP_CODE, Constants.CODE_SUCCESS);
			data.put(Constants.DATAS, Constants.FAILURE);
		}
		renderJSON(data);
		
	}

}
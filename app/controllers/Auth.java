package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import constants.Constants;
import constants.Messages;
import constants.Pages;

public class Auth extends Basic {

	final static Logger logger = LoggerFactory.getLogger(Auth.class);

	public static void index() {
		render(Pages.LOGIN);
	}

	public static void loginJson(User user) {
		Map result = new HashMap();
		try {
			List datas = new ArrayList();
			User dbUser = User.login(user);
			if (dbUser != null) {
				datas.add(dbUser);
				result.put(Constants.STATUS, Constants.SUCCESS);
				result.put(Constants.DATAS, datas);
			} else {
				result.put(Constants.STATUS, Constants.FAILURE);
				result.put(Constants.DATAS, datas);
			}
		} catch (Exception e) {
			result.put(Constants.ERROR, Constants.FAILURE);
			result.put(Constants.MESSAGE, Messages.LOGIN_ERROR);
			logger.error(Messages.LOGIN_MESSAGE, new Object[] { user.username, e });

		}
		renderJSON(result);
	}

}
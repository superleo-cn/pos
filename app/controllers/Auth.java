package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.User;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.MyPropertiesUtils;
import constants.Constants;
import constants.Messages;
import constants.Pages;

public class Auth extends Basic {

	final static Logger logger = LoggerFactory.getLogger(Auth.class);

	public static void index() {
		
		// Http.Request req = newRequest();
				User user = new User();
				user.username = "123";
				user.id = 1L;
				user.realname = "111";
				user.createDate = new Date();
				user.password = "password";
				User user2 = new User();
				user2.createDate = null;
				user2.password = "qqq";

				User.store2(user);
		
		render(Pages.LOGIN);
	}

	public static void loginJson(User user) {
		Map result = new HashMap();
		try {
			List datas = new ArrayList();
			User dbUser = User.login(user);
			if (dbUser != null) {
				User paramUser = new User();
				paramUser.id = dbUser.id;
				paramUser.userIp = user.userIp;
				paramUser.userMac = user.userMac;
				paramUser.lastLoginDate = new Date();
				logger.info("[System]-[Info]-[Param User({}) IP is {}, Mac is {}]", new Object[] { paramUser.id,
						paramUser.userIp, paramUser.userMac });
				User.store(paramUser);
				datas.add(dbUser);
				result.put(Constants.CODE, Constants.SUCCESS);
				result.put(Constants.MESSAGE, Messages.LOGIN_SUCCESS);
				result.put(Constants.DATAS, datas);
			} else {
				result.put(Constants.CODE, Constants.FAILURE);
				result.put(Constants.MESSAGE, Messages.LOGIN_FAILURE);
				result.put(Constants.DATAS, datas);
			}
		} catch (Exception e) {
			result.put(Constants.CODE, Constants.ERROR);
			result.put(Constants.MESSAGE, Messages.LOGIN_ERROR);
			logger.error(Messages.LOGIN_ERROR_MESSAGE, new Object[] { user.username, e });

		}
		renderJSON(result);
	}

}
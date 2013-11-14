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

import play.cache.Cache;
import utils.MyPropertiesUtils;
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
			User dbUser = User.loginJson(user);
			if (dbUser != null) {
				user.id = dbUser.id;
				user.lastLoginDate = new Date();
				User.store(user);
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

    public static void logout() {
        Cache.delete(session.getId());
        session.remove(Constants.CURRENT_USER_REALNAME);
        session.remove(Constants.CURRENT_USERNAME);
        session.remove(Constants.CURRENT_USERID);
        index();
    }

}
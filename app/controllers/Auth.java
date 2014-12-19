package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Audit;
import models.Shop;
import models.User;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.cache.Cache;

import com.avaje.ebean.annotation.Transactional;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import constants.Constants;
import constants.Messages;
import constants.Pages;

public class Auth extends Basic {

	final static Logger logger = LoggerFactory.getLogger(Auth.class);

	public static void index() {
		render(Pages.LOGIN);
	}

	@Transactional
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
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		renderJSON(gson.toJson(result));
	}

	/**
	 * check whether the user type is correct or not
	 * 
	 * @param dbUser
	 * @return
	 */
	private static boolean isLoginUser(User dbUser) {
		for (String type : Constants.LOGIN_USERTYPS) {
			if (StringUtils.equalsIgnoreCase(dbUser.usertype, type)) {
				return true;
			}
		}
		return false;
	}

	@Transactional
	public static void login() {
		Map result = new HashMap();
		User user = new User();
		try {
			List datas = new ArrayList();
			user.username = request.params.get("username");
			user.password = request.params.get("password");
			User dbUser = User.login(user);
			boolean isCorrectRole = isLoginUser(dbUser);

			logger.info("[The user({}-{}) is trying to login to the system.]", new Object[] { dbUser.username, dbUser.usertype });
			if (dbUser != null && isCorrectRole && dbUser.password.equalsIgnoreCase(user.password)) {
				user.id = dbUser.id;
				user.lastLoginDate = new Date();
				User.store(user);
				datas.add(dbUser);
				result.put(Constants.CODE, Constants.SUCCESS);
				result.put(Constants.MESSAGE, Messages.LOGIN_SUCCESS);
				result.put(Constants.DATAS, datas);

				Audit audit = new Audit();
				audit.action = "Login";
				audit.user = dbUser;
				if (dbUser.shops != null) {
					audit.shop = dbUser.getMyShop();
				}
				Audit.store(audit);
				if (dbUser.shops != null) {
					session.put(Constants.CURRENT_SHOPNAME, dbUser.getMyShop().name);
					session.put(Constants.CURRENT_SHOPID, dbUser.getMyShop().id);
				}

				session.put(Constants.CURRENT_USERID, dbUser.id);
				session.put(Constants.CURRENT_USERNAME, dbUser.username);
				session.put(Constants.CURRENT_USER_REALNAME, dbUser.realname);
				session.put(Constants.CURRENT_USERTYPE, dbUser.usertype);
				logger.info("[The user({}-{}) Login successfully.]", new Object[] { dbUser.username, dbUser.usertype });
			} else {
				error();
			}
		} catch (Exception e) {
			result.put(Constants.CODE, Constants.ERROR);
			result.put(Constants.MESSAGE, Messages.LOGIN_ERROR);
			logger.error(Messages.LOGIN_ERROR_MESSAGE, new Object[] { user.username, e });

		}
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		renderJSON(gson.toJson(result));
	}

	@Transactional
	public static void loginAdminJson(User user) {
		Map result = new HashMap();
		try {
			List datas = new ArrayList();
			User dbUser = User.loginAdminJson(user);
			if (dbUser != null) {
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
			logger.error(Messages.LOGIN_ERROR_MESSAGE, new Object[] { user.username, e.getMessage() });

		}
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		renderJSON(gson.toJson(result));
	}

	@Transactional
	public static void logout() {
		String userid = session.get(Constants.CURRENT_USERID);
		try {
			Cache.delete(session.getId());
			String shopId = session.get(Constants.CURRENT_SHOPID);
			if (shopId != null) {
				Audit audit = new Audit();
				audit.action = "Logout";
				User user = new User();
				if (session.get(Constants.CURRENT_USERID) != null) {
					user.id = Long.valueOf(session.get(Constants.CURRENT_USERID));
					audit.user = user;
					Shop shop = new Shop();
					shop.id = Long.valueOf(session.get(Constants.CURRENT_SHOPID));
					audit.shop = shop;
					Audit.store(audit);
				}
				session.remove(Constants.CURRENT_SHOPNAME);
				session.remove(Constants.CURRENT_SHOPID);
			}
			session.remove(Constants.CURRENT_USER_REALNAME);
			session.remove(Constants.CURRENT_USERNAME);
			session.remove(Constants.CURRENT_USERTYPE);
			session.remove(Constants.CURRENT_USERID);
			session.clear();
		} catch (Exception e) {
			logger.error(Messages.LOGOUT_ERROR_MESSAGE, new Object[] { userid, e.getMessage() });
		}
		index();
	}

}

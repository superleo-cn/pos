package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Audit;
import models.Shop;
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

import com.avaje.ebean.annotation.Transactional;

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
				/* login */
				Audit audit = new Audit();
                audit.action = "Login";
                audit.user= dbUser;
                audit.shop = dbUser.shop;
                Audit.store(audit);
				
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

	@Transactional
	public static void login() {
		Map result = new HashMap();
        User user = new User();
		try {
			List datas = new ArrayList();
			user.username = request.params.get("username");
            user.password = request.params.get("password");
            User dbUser = User.login(user);
			if (dbUser != null) {
				user.id = dbUser.id;
				user.lastLoginDate = new Date();
				User.store(user);
				datas.add(dbUser);
				result.put(Constants.CODE, Constants.SUCCESS);
				result.put(Constants.MESSAGE, Messages.LOGIN_SUCCESS);
				result.put(Constants.DATAS, datas);

                Audit audit = new Audit();
                audit.action = "Login";
                audit.user= dbUser;
                if(dbUser.shop != null){
                	audit.shop = dbUser.shop;
                }
                Audit.store(audit);
                if(dbUser.shop!=null)
                session.put(Constants.CURRENT_SHOPID, dbUser.shop.id);

                session.put(Constants.CURRENT_USERID, dbUser.id);
                session.put(Constants.CURRENT_USERNAME, dbUser.username);
                session.put(Constants.CURRENT_USER_REALNAME, dbUser.realname);
                session.put(Constants.CURRENT_USERTYPE, dbUser.usertype);

                play.Logger.info("Login successfully");

			} else {
				error();
			}
		} catch (Exception e) {
			result.put(Constants.CODE, Constants.ERROR);
			result.put(Constants.MESSAGE, Messages.LOGIN_ERROR);
			logger.error(Messages.LOGIN_ERROR_MESSAGE, new Object[] { user.username, e });

		}
		renderJSON(result);
	}
	
	@Transactional
	public static void loginAdminJson(User user) {
		Map result = new HashMap();
		try {
			List datas = new ArrayList();
			User dbUser = User.loginAdminJson(user);
			if (dbUser != null) {
				Audit audit = new Audit();
                audit.action = "Login";
                audit.user= dbUser;
                Audit.store(audit);
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

	@Transactional
    public static void logout() {
        Cache.delete(session.getId());


        String shopId = session.get(Constants.CURRENT_SHOPID);
        if(shopId!=null) {

            Audit audit = new Audit();
            audit.action = "Logout";
            User user = new User();
            if(session.get(Constants.CURRENT_USERID)!=null) {
                user.id= Long.valueOf(session.get(Constants.CURRENT_USERID));
                audit.user= user;
                Shop shop = new Shop();
                shop.id = Long.valueOf(session.get(Constants.CURRENT_SHOPID));
                audit.shop = shop;
                Audit.store(audit);
            }

        }

        session.remove(Constants.CURRENT_USER_REALNAME);
        session.remove(Constants.CURRENT_USERNAME);
        session.remove(Constants.CURRENT_USERTYPE);
        session.remove(Constants.CURRENT_USERID);

        index();
    }

}

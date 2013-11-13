package controllers;

import models.User;
import play.Logger;
import play.cache.Cache;
import play.mvc.Controller;
import sun.net.www.protocol.http.AuthCache;
import constants.Constants;
import constants.Pages;

public class Home extends Basic {

    public static void index() {

        render(Pages.HOME);
    }

}
package controllers;

import constants.Constants;
import constants.Pages;
import models.User;

public class ChangePassword extends Basic {

    public static void index() {
        Long userId = Long.valueOf(session.get(Constants.CURRENT_USERID));
        User user = User.view(userId);
        String newPassword = request.params.get("password");
        user.password=newPassword;
        User.store(user);
        renderJSON("ok");
    }

}
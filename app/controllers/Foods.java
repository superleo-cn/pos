package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;

import com.avaje.ebean.annotation.Transactional;

import models.Food;
import models.User;
import utils.Pagination;
import constants.Constants;
import constants.Pages;

public class Foods extends Basic {

	public static void listJson(Long id) {
		Map result = new HashMap();
		try{
			result.put(Constants.STATUS, Constants.SUCCESS);
			result.put(Constants.DATAS, Food.listByShop(id));
		} catch (Exception e) {
			result.put(Constants.ERROR, Constants.FAILURE);
			result.put(Constants.MESSAGE, "Error Happend. " + e.getMessage());
		}
		renderJSON(result);
		
	}

}
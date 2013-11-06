package controllers;

import java.util.HashMap;
import java.util.Map;

import models.Cash;
import constants.Constants;

public class Cashs extends Basic {

	public static void listJson(Long id) {
		Map result = new HashMap();
		try{
			result.put(Constants.STATUS, Constants.SUCCESS);
			result.put(Constants.DATAS, Cash.listByShop(id));
		} catch (Exception e) {
			result.put(Constants.ERROR, Constants.FAILURE);
			result.put(Constants.MESSAGE, "Error Happend. " + e.getMessage());
		}
		renderJSON(result);
		
	}

}
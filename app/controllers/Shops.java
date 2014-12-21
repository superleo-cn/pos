package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Shop;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import constants.Constants;
import constants.Messages;

public class Shops extends Basic {

	final static Logger logger = LoggerFactory.getLogger(Shops.class);

	public static void listJson(Long id) {
		Map result = new HashMap();
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		try {
			List<Shop> shops = Shop.listJson(id);
			if (CollectionUtils.size(shops) > 0) {
				result.put(Constants.CODE, Constants.SUCCESS);
				result.put(Constants.DATAS, shops.get(0));
			} else {
				result.put(Constants.CODE, Constants.FAILURE);
			}
		} catch (Exception e) {
			result.put(Constants.CODE, Constants.ERROR);
			result.put(Constants.MESSAGE, Messages.SHOP_LIST_ERROR);
			logger.error(Messages.SHOP_LIST_ERROR_MESSAGE);
		}
		renderJSON(gson.toJson(result));
	}

}
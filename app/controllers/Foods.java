package controllers;

import java.util.HashMap;
import java.util.Map;

import models.Attribute;
import models.Category;
import models.Food;
import models.Shop;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import constants.Constants;
import constants.Messages;

public class Foods extends Basic {

	final static Logger logger = LoggerFactory.getLogger(Foods.class);

	public static void listJson(Long id) {
		Map result = new HashMap();
		try {
			result.put(Constants.CODE, Constants.SUCCESS);
			result.put(Constants.DATAS, Food.listByShop(id));
		} catch (Exception e) {
			Shop shop = Shop.view(id);
			String shopName = StringUtils.defaultIfEmpty(shop.name, Constants.NA);
			result.put(Constants.CODE, Constants.ERROR);
			result.put(Constants.MESSAGE, Messages.FOOD_LIST_ERROR);
			logger.error(Messages.FOOD_LIST_ERROR_MESSAGE, new Object[] { shopName, e });
		}
		renderJSON(result);

	}
	
	public static void listExtJson(Long id) {
		Map result = new HashMap();
		try {
			Map datas = new HashMap();
			datas.put("Foods", Food.listByShop(id));
			datas.put("Categories", Category.listByShop(id));
			datas.put("Attributes", Attribute.listByShop(id));
			result.put(Constants.CODE, Constants.SUCCESS);
			result.put(Constants.DATAS, datas);
		} catch (Exception e) {
			Shop shop = Shop.view(id);
			String shopName = StringUtils.defaultIfEmpty(shop.name, Constants.NA);
			result.put(Constants.CODE, Constants.ERROR);
			result.put(Constants.MESSAGE, Messages.FOOD_LIST_ERROR);
			logger.error(Messages.FOOD_LIST_ERROR_MESSAGE, new Object[] { shopName, e });
		}
		renderJSON(result);

	}

}
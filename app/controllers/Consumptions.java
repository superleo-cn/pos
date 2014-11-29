package controllers;

import java.util.HashMap;
import java.util.Map;

import models.Consumption;
import models.Shop;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import constants.Constants;
import constants.Messages;

/**
 * Notice:
 * 
 * This is the cash function is not available for the tablet.
 * 
 * @author superleo
 * 
 */
public class Consumptions extends Basic {

	final static Logger logger = LoggerFactory.getLogger(Consumptions.class);

	public static void listJson(Long id) {
		Map result = new HashMap();
		try {
			result.put(Constants.CODE, Constants.SUCCESS);
			result.put(Constants.DATAS, Consumption.listByShop(id));
		} catch (Exception e) {
			Shop shop = Shop.view(id);
			String shopName = StringUtils.defaultIfEmpty(shop.name, Constants.NA);
			result.put(Constants.CODE, Constants.ERROR);
			result.put(Constants.MESSAGE, Messages.CONSUMPTION_LIST_ERROR);
			logger.error(Messages.CONSUMPTION_LIST_ERROR_MESSAGE, new Object[] { shopName, e });
		}
		renderJSON(result);

	}

}
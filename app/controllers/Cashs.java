package controllers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import models.Cash;
import models.Shop;
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
public class Cashs extends Basic {

	final static Logger logger = LoggerFactory.getLogger(Cashs.class);

	public static void listJson(Long id) {
		Map result = new HashMap();
		try {
			result.put(Constants.CODE, Constants.SUCCESS);
			result.put(Constants.DATAS, Cash.listByShop(id));
		} catch (Exception e) {
			Shop shop = Shop.view(id);
			String shopName = StringUtils.defaultIfEmpty(shop.name, Constants.NA);
			result.put(Constants.CODE, Constants.ERROR);
			result.put(Constants.MESSAGE, Messages.CASH_LIST_ERROR);
			logger.error(Messages.CASH_LIST_ERROR_MESSAGE, new Object[] { shopName, e });
		}
		renderJSON(result);

	}

}
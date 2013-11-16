package controllers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avaje.ebean.annotation.Transactional;

import models.Food;
import models.Transaction;
import models.User;
import constants.Constants;
import constants.Messages;

public class Transactions extends Basic {
	
	final static Logger logger = LoggerFactory.getLogger(Transactions.class);

	public static void listJson(Long id) {

	}

	@Transactional
	public static void store() {
		Map result = new HashMap();
		String str = "";
		try {
			String[]  ids = params.getAll("id");
			str = ids[0];
			logger.info("[System]-[Info]-[{}]", ids[0]);
		} catch (Exception e) {
			result.put(Constants.ERROR, Constants.FAILURE);
			str = "Transaction unsuccessfull. Error message is: " + e.getMessage();
		}
		renderText(str);
	}
	
	@Transactional
	public static void store3(Transaction[] transactions) {
		Map result = new HashMap();
		String str = "";
		try {
			if (CollectionUtils.size(transactions) > 0) {

				for (Transaction transaction : transactions) {
					str += "[shopId = " + transaction.shop.id + "], [userId = " + transaction.user.id
							+ "], [quantity = " + transaction.quantity + "], [foodId = " + transaction.food.id
							+ "], [totalDiscount = " + transaction.totalDiscount + "], [totalRetailPrice = "
							+ transaction.totalRetailPrice + "], [totalPackage = " + transaction.totalPackage
							+ "], [totalPackage = " + transaction.freeOfCharge + "]\n";
				}

			}
			logger.info("[System]-[Info]-[{}]", str);
		} catch (Exception e) {
			result.put(Constants.ERROR, Constants.FAILURE);
			str = "Transaction unsuccessfull. Error message is: " + e.getMessage();
		}
		renderText(str);
	}

	@Transactional
	public static void store2(Transaction transaction) {
		Map result = new HashMap();
		try {
			Boolean flag = Transaction.store(transaction);
			if (flag) {
				result.put(Constants.STATUS, Constants.SUCCESS);
				result.put(Constants.MESSAGE, "Transaction successfull.");
			} else {
				result.put(Constants.STATUS, Constants.FAILURE);
				result.put(Constants.MESSAGE, "Transaction unsuccessfull.");
			}
		} catch (Exception e) {
			result.put(Constants.ERROR, Constants.FAILURE);
			result.put(Constants.MESSAGE, "Transaction unsuccessfull. Error message is: " + e.getMessage());
		}
		renderJSON(result);
	}

}
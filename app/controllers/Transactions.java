package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Transaction;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avaje.ebean.annotation.Transactional;

import constants.Constants;

public class Transactions extends Basic {

	final static Logger logger = LoggerFactory.getLogger(Transactions.class);

	@Transactional
	public static void store(Transaction[] transactions) {
		Map result = new HashMap();
		String str = "";
		try {
			if (CollectionUtils.size(transactions) > 0) {
				List datas = new ArrayList();
				for (Transaction transaction : transactions) {
					str += "[shopId = " + transaction.shop.id + "], [userId = " + transaction.user.id
							+ "], [quantity = " + transaction.quantity + "], [foodId = " + transaction.food.id
							+ "], [totalDiscount = " + transaction.totalDiscount + "], [totalRetailPrice = "
							+ transaction.totalRetailPrice + "], [totalPackage = " + transaction.totalPackage
							+ "], [totalPackage = " + transaction.freeOfCharge + "]\n";
					logger.info("[System]-[Info]-[The transaction data is : {}]", str);
					boolean flag = Transaction.store(transaction);
					if (!flag) {
						datas.add(transaction.androidId);
					}
				}
				
				result.put(Constants.DATAS, datas);
				if (CollectionUtils.size(datas) == 0) {
					result.put(Constants.STATUS, Constants.SUCCESS);
					result.put(Constants.MESSAGE, "Transaction successfully.");
				} else {
					result.put(Constants.STATUS, Constants.FAILURE);
					result.put(Constants.MESSAGE, "Transaction failed.");
				}
			}

		} catch (Exception e) {
			result.put(Constants.ERROR, Constants.ERROR);
			str = "All the Transaction submitted unsuccessfully. Error message is: " + e.getMessage();
		}
		renderText(str);
	}

}
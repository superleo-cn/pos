package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import models.CashTransaction;
import models.ConsumeTransaction;
import models.Transaction;

import com.avaje.ebean.annotation.Transactional;

import constants.Constants;

public class CashTransactions extends Basic {

	final static Logger logger = LoggerFactory.getLogger(CashTransactions.class);

	@Transactional
	public static void store(CashTransaction[] cashTransactions) {
		Map result = new HashMap();

		try {
			if (CollectionUtils.size(cashTransactions) > 0) {
				List datas = new ArrayList();
				String str = "";
				for (CashTransaction transaction : cashTransactions) {
					str += "[androidId = " + transaction.androidId + "], [shopId = " + transaction.shop.id
							+ "], [userId = " + transaction.user.id + "], [price = " + transaction.cash.price
							+ "], [cashId = " + transaction.cash.id + "],[quantity = " + transaction.quantity + "]\n";
					logger.info("[System]-[Info]-[The transaction data is : {}]", str);
					boolean flag = CashTransaction.store(transaction);
					if (flag) {
						datas.add(transaction.androidId);
					}
				}
				result.put(Constants.DATAS, datas);
				if (CollectionUtils.size(datas) == CollectionUtils.size(cashTransactions)) {
					result.put(Constants.CODE, Constants.SUCCESS);
					result.put(Constants.MESSAGE, "Transaction successfully.");
				} else {
					result.put(Constants.CODE, Constants.FAILURE);
					result.put(Constants.MESSAGE, "Transaction failed.");
				}
			}

		} catch (Exception e) {
			String errMsg = "All the Transaction submitted unsuccessfully. Error message is: " + e.getMessage();
			result.put(Constants.CODE, Constants.ERROR);
			result.put(Constants.MESSAGE, errMsg);
			logger.error("[System]-[Info]-{}]", new Object[] { errMsg, e });
		}
		renderJSON(result);
	}

}
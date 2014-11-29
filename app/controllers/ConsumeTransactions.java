package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.ConsumeTransaction;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avaje.ebean.annotation.Transactional;

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
public class ConsumeTransactions extends Basic {

	final static Logger logger = LoggerFactory.getLogger(ConsumeTransactions.class);

	@Transactional
	public static void store(ConsumeTransaction[] consumeTransactions) {
		Map result = new HashMap();
		try {
			if (consumeTransactions != null && CollectionUtils.size(consumeTransactions) > 0) {
				List datas = new ArrayList();
				for (ConsumeTransaction transaction : consumeTransactions) {
					String str = "[androidId = " + transaction.androidId + "], [shopId = " + transaction.shop.id + "], [userId = " + transaction.user.id + "], [price = "
							+ transaction.price + "]";
					logger.info("[System]-[Info]-[The consume transaction data is : {}]", str);
					boolean flag = ConsumeTransaction.store(transaction);
					if (flag) {
						datas.add(transaction.androidId);
					}
				}
				result.put(Constants.DATAS, datas);
				logger.info(Messages.TRANSACTION_MESSAGE, new Object[] { datas.size(), ConsumeTransaction.class.getSimpleName(), consumeTransactions.length });
				if (CollectionUtils.size(datas) == CollectionUtils.size(consumeTransactions)) {
					result.put(Constants.CODE, Constants.SUCCESS);
					result.put(Constants.MESSAGE, "Consume Transaction successfully.");
				} else {
					result.put(Constants.CODE, Constants.FAILURE);
					result.put(Constants.MESSAGE, "Consume Transaction failed.");
				}
			}

		} catch (Exception e) {
			String errMsg = "All the Consume Transaction submitted unsuccessfully. Error message is: " + e.getMessage();
			result.put(Constants.CODE, Constants.ERROR);
			result.put(Constants.MESSAGE, errMsg);
			logger.error("[System]-[Info]-{}]", new Object[] { errMsg });
		}
		renderJSON(result);
	}

}
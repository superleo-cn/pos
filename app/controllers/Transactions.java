package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Attribute;
import models.Transaction;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avaje.ebean.annotation.Transactional;

import constants.Constants;
import constants.Messages;

public class Transactions extends Basic {

	final static Logger logger = LoggerFactory.getLogger(Transactions.class);

	@Transactional
	public static void store(Transaction[] transactions) {
		Map result = new HashMap();
		String str = "";
		int size = 0; // successful transaction records
		try {
			if (CollectionUtils.size(transactions) > 0) {
				List datas = new ArrayList();
				for (Transaction transaction : transactions) {
					str = "[date = " + new Date() + "], [transactionId = " + transaction.transactionId
							+ "], [androidId = " + transaction.androidId + "], [shopId = " + transaction.shop.id
							+ "], [userId = " + transaction.user.id + "], [quantity = " + transaction.quantity
							+ "], [foodId = " + transaction.food.id + "], [totalDiscount = "
							+ transaction.totalDiscount + "], [totalRetailPrice = " + transaction.totalRetailPrice
							+ "], [totalPackage = " + transaction.totalPackage + "], [freeOfCharge = "
							+ transaction.freeOfCharge + "], [orderDate = " + transaction.orderDate + "]\n";
					logger.info("[System]-[Info]-[The transaction data is : {}]", str);
					List<Transaction> list = Transaction.getByTransactionId(transaction.transactionId);
					if (CollectionUtils.size(list) == 0) {
						size++;
						boolean flag = Transaction.store(transaction);
						if (flag) {
							datas.add(transaction.androidId);
						}
					}
				}

				result.put(Constants.DATAS, datas);
				logger.info(Messages.TRANSACTION_MESSAGE,
						new Object[] { datas.size(), Transaction.class.getSimpleName(), transactions.length });
				if (CollectionUtils.size(datas) == size) {
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

	@Transactional
	public static void storeWithAttrs(Transaction[] transactions) {
		Map result = new HashMap();
		String str = "";
		int size = 0; // successful transaction records
		try {
			if (CollectionUtils.size(transactions) > 0) {
				List datas = new ArrayList();
				for (Transaction transaction : transactions) {
					str += "[date = " + new Date() + "], [transactionId = " + transaction.transactionId
							+ "], [androidId = " + transaction.androidId + "], [shopId = " + transaction.shop.id
							+ "], [userId = " + transaction.user.id + "], [quantity = " + transaction.quantity
							+ "], [foodId = " + transaction.food.id + "], [totalDiscount = "
							+ transaction.totalDiscount + "], [totalRetailPrice = " + transaction.totalRetailPrice
							+ "], [totalPackage = " + transaction.totalPackage + "], [freeOfCharge = "
							+ transaction.freeOfCharge + "], [orderDate = " + transaction.orderDate + "]\n";
					logger.info("[System]-[Info]-[The transaction data is : {}]", str);
					List<Transaction> list = Transaction.getByTransactionId(transaction.transactionId);
					if (CollectionUtils.size(list) == 0) {
						size++;
						String attributeIds = transaction.attributeIds;
						List<Attribute> attributes = new ArrayList<Attribute>();
						if (StringUtils.isNotEmpty(attributeIds)) {
							String[] arrs = StringUtils.split(attributeIds, ",");
							for (String attrId : arrs) {
								Attribute arr = Attribute.getById(Long.parseLong(attrId));
								if (arr != null) {
									attributes.add(arr);
								}
							}
							transaction.attributes = attributes;
						}
						boolean flag = Transaction.store(transaction);
						if (flag) {
							datas.add(transaction.androidId);
						}
					}
				}

				result.put(Constants.DATAS, datas);
				logger.info(Messages.TRANSACTION_MESSAGE,
						new Object[] { datas.size(), Transaction.class.getSimpleName(), transactions.length });
				if (CollectionUtils.size(datas) == size) {
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
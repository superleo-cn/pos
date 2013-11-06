package controllers;

import java.util.HashMap;
import java.util.Map;

import models.ConsumeTransaction;
import models.Transaction;

import com.avaje.ebean.annotation.Transactional;

import constants.Constants;

public class ConsumeTransactions extends Basic {

	@Transactional
	public static void store(ConsumeTransaction consumeTransaction) {
		Map result = new HashMap();
		try {
			Boolean flag = ConsumeTransaction.store(consumeTransaction);
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
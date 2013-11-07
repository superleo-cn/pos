package controllers;

import java.util.HashMap;
import java.util.Map;

import models.CashTransaction;
import models.Transaction;

import com.avaje.ebean.annotation.Transactional;

import constants.Constants;

public class CashTransactions extends Basic {

	@Transactional
	public static void store(CashTransaction cashTransaction) {
		Map result = new HashMap();
		try {
			Boolean flag = CashTransaction.store(cashTransaction);
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
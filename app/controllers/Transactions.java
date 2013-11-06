package controllers;

import java.util.HashMap;
import java.util.Map;

import com.avaje.ebean.annotation.Transactional;

import models.Food;
import models.Transaction;
import models.User;
import constants.Constants;

public class Transactions extends Basic {

	public static void listJson(Long id) {

	}

	@Transactional
	public static void store(Transaction transaction) {
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
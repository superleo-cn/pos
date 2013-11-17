package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.CashTransaction;
import models.DailySummary;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avaje.ebean.annotation.Transactional;

import constants.Constants;

public class DailySummarys extends Basic {

	final static Logger logger = LoggerFactory.getLogger(DailySummarys.class);

	@Transactional
	public static void store(DailySummary dailySummary) {
		Map result = new HashMap();

		try {
			if (dailySummary != null) {
				List datas = new ArrayList();
				String str = "[androidId = " + dailySummary.androidId + "], [shopId = " + dailySummary.shop.id + "],"
						+ "[userId = " + dailySummary.user.id + "], [A.OpenBalance = " + dailySummary.aOpenBalance
						+ "]," + "[B.Expenses = " + dailySummary.bExpenses + "], [C.CashCollected = "
						+ dailySummary.cCashCollected + "], " + "[D.DailyTurnover = " + dailySummary.dDailyTurnover
						+ "], [" + "[E.NextOpenBalance = " + dailySummary.eNextOpenBalance + "], "
						+ "[F.BringBackCash = " + dailySummary.fBringBackCash + "], " + "[G.TotalBalance = "
						+ dailySummary.gTotalBalance + "]";
				logger.info("[System]-[Info]-[The DailySummary data is : {}]", str);
				boolean flag = DailySummary.store(dailySummary);
				result.put(Constants.DATAS, datas);
				if (flag) {
					result.put(Constants.CODE, Constants.SUCCESS);
					result.put(Constants.MESSAGE, "Transaction successfully.");
				} else {
					result.put(Constants.CODE, Constants.FAILURE);
					result.put(Constants.MESSAGE, "Transaction failed.");
				}

			}

		} catch (Exception e) {
			String errMsg = "The Transaction submitted unsuccessfully. Error message is: " + e.getMessage();
			result.put(Constants.CODE, Constants.ERROR);
			result.put(Constants.MESSAGE, errMsg);
			logger.error("[System]-[Info]-{}]", new Object[] { errMsg, e });
		}
		renderJSON(result);
	}

}
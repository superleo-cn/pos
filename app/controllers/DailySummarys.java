package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.ConsumeTransaction;
import models.DailySummary;

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
public class DailySummarys extends Basic {

	final static Logger logger = LoggerFactory.getLogger(DailySummarys.class);

	@Transactional
	public static void store(DailySummary[] dailySummaries) {
		Map result = new HashMap();

		try {
			if (dailySummaries != null && CollectionUtils.size(dailySummaries) > 0) {
				List datas = new ArrayList();
				for (DailySummary dailySummary : dailySummaries) {
					String str = "[androidId = " + dailySummary.androidId + "], [shopId = " + dailySummary.shop.id + "]," + "[userId = " + dailySummary.user.id
							+ "], [A.OpenBalance = " + dailySummary.aOpenBalance + "]," + "[B.Expenses = " + dailySummary.bExpenses + "], [C.CashCollected = "
							+ dailySummary.cCashCollected + "], " + "[D.DailyTurnover = " + dailySummary.dDailyTurnover + "], [E.NextOpenBalance = "
							+ dailySummary.eNextOpenBalance + "], " + "[F.BringBackCash = " + dailySummary.fBringBackCash + "], " + "[G.TotalBalance = "
							+ dailySummary.gTotalBalance + "]";
					logger.info("[System]-[Info]-[The daily transaction data is : {}]", str);
					boolean flag = DailySummary.store(dailySummary);
					if (flag) {
						datas.add(dailySummary.androidId);
					}
				}
				result.put(Constants.DATAS, datas);
				logger.info(Messages.TRANSACTION_MESSAGE, new Object[] { datas.size(), ConsumeTransaction.class.getSimpleName(), dailySummaries.length });
				if (CollectionUtils.size(datas) == CollectionUtils.size(dailySummaries)) {
					result.put(Constants.CODE, Constants.SUCCESS);
					result.put(Constants.MESSAGE, "Daily Transaction successfully.");
				} else {
					result.put(Constants.CODE, Constants.FAILURE);
					result.put(Constants.MESSAGE, "Daily Transaction failed.");
				}
			}

		} catch (Exception e) {
			String errMsg = "All the Daily Transaction submitted unsuccessfully. Error message is: " + e.getMessage();
			result.put(Constants.CODE, Constants.ERROR);
			result.put(Constants.MESSAGE, errMsg);
			logger.error("[System]-[Info]-{}]", new Object[] { errMsg });
		}
		renderJSON(result);
	}

}
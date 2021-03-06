package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Audit;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avaje.ebean.annotation.Transactional;

import constants.Constants;

public class Audits extends Basic {

	final static Logger logger = LoggerFactory.getLogger(Audits.class);

	public static void index() {
		render("/pages/audits.html");
	}
	
	@Transactional
	public static void store(Audit[] audits) {
		Map result = new HashMap();
		try {
			if (audits != null && CollectionUtils.size(audits) > 0) {
				List datas = new ArrayList();
				String str = "";
				for (Audit audit : audits) {
					str += "[androidId = " + audit.androidId + "], [shopId = " + audit.shop.id + "], [userId = "
							+ audit.user.id + "], [action = " + audit.action + "], [actionDate = " + audit.actionDate + "]\n";
					logger.info("[System]-[Info]-[The transaction data is : {}]", str);
					boolean flag = Audit.store(audit);
					if (flag) {
						datas.add(audit.androidId);
					}
				}
				result.put(Constants.DATAS, datas);
				if (CollectionUtils.size(datas) == CollectionUtils.size(audits)) {
					result.put(Constants.CODE, Constants.SUCCESS);
					result.put(Constants.MESSAGE, "Audit successfully.");
				} else {
					result.put(Constants.CODE, Constants.FAILURE);
					result.put(Constants.MESSAGE, "Audit failed.");
				}
			}

		} catch (Exception e) {
			String errMsg = "All the Audits submitted unsuccessfully. Error message is: " + e.getMessage();
			result.put(Constants.CODE, Constants.ERROR);
			result.put(Constants.MESSAGE, errMsg);
			logger.error("[System]-[Info]-{}]", new Object[] { errMsg, e });
		}
		renderJSON(result);
	}
}
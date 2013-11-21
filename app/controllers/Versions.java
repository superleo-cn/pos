package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Audit;
import models.Food;
import models.Shop;
import models.Version;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avaje.ebean.annotation.Transactional;

import constants.Constants;
import constants.Messages;

public class Versions extends Basic {

	final static Logger logger = LoggerFactory.getLogger(Versions.class);

	public static void listJson() {
		Map result = new HashMap();
		try {
			result.put(Constants.CODE, Constants.SUCCESS);
			result.put(Constants.DATAS, Version.getLastVersion());
		} catch (Exception e) {
			result.put(Constants.CODE, Constants.ERROR);
			result.put(Constants.MESSAGE, Messages.VERSION_LIST_ERROR_MESSAGE);
			logger.error(Messages.VERSION_LIST_ERROR, new Object[] { e });
		}
		renderJSON(result);

	}
}
package controllers;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	public static void index() {
		List<Version> versions = Version.getLastVersion();
		Version version = null;
		if(versions != null){
			version = versions.get(0);
		}
		render("/pages/upload.html", version);
	}

	public static void store(Version version, File file) {
		String message = null;
		try {
			version.name = uploadFile(file);
			Version.store(version);
			message = "上传成功";
		} catch (Exception e) {
			logger.error(Messages.VERSION_LIST_ERROR, new Object[] { e });
			message = "上传失败";
		}
		renderText(message);
	}
}
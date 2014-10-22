package controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import models.Category;
import models.Food;
import models.Shop;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.data.FileUpload;
import play.mvc.Http;
import utils.Pagination;
import au.com.bytecode.opencsv.CSVReader;

import com.avaje.ebean.Ebean;
import com.google.gson.Gson;

import constants.Constants;

/**
 * Created with IntelliJ IDEA. User: lala Date: 3/15/14 Time: 11:01 AM To change
 * this template use File | Settings | File Templates.
 */
public class MasterItem extends Basic {

	final static Logger logger = LoggerFactory.getLogger(MasterItem.class);

	public static void show(Long id) {
		renderJSON(Food.view(id));
	}

	public static void update() {

		Food food = new Gson().fromJson(new InputStreamReader(request.body), Food.class);
		if (food.id != null && food.id > 0) {
			System.out.println(new Gson().toJson(food));
			food.modifiedBy = session.get(Constants.CURRENT_USERNAME);
			food.modifiedDate = new Date();

			Ebean.update(food);
		} else {
			Ebean.save(food);
		}
		Map data = new HashMap();
		data.put("messages", "Store Item Successfully.");
		renderJSON(data);
	}

	public static void uploadImg(File img) {

		DiskFileItemFactory factory = new DiskFileItemFactory();
		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			FileUpload fileUpload = (FileUpload) ((List) Http.Request.current().args.get("__UPLOADS")).get(0);

			File destPath = new File(Play.applicationPath.getPath() + "/public/upload/" + fileUpload.getFileName());
			logger.info("Upload file " + destPath.getAbsolutePath());

			BufferedImage bimg = ImageIO.read(fileUpload.asFile());

			if (bimg.getWidth() != bimg.getHeight() && Math.abs(bimg.getWidth() - bimg.getHeight()) > 50)
				throw new IllegalArgumentException("File width and height are not equal : " + bimg.getWidth() + "/"
						+ bimg.getHeight());
			FileUtils.copyFile(fileUpload.asFile(), destPath);
			Map data = new HashMap();
			data.put("success", "Upload file Successfully.");
			data.put("path", fileUpload.getFileName());
			renderJSON(data);
		} catch (Exception e) {
			logger.error(e.getMessage());
			Map data = new HashMap();
			data.put("error", e.getMessage());
			renderJSON(data);
		}
	}

	public static void upload(String type) {

		Map<String, String> response = new HashMap<>();
		int size = 0;
		try {
			String type1 = request.params.get("type");
			String attachment = request.params.get("attachment");
			attachment = attachment.substring(attachment.indexOf(",") + 1);
			byte[] bytes = Base64.decodeBase64(attachment);
			new ByteArrayInputStream(bytes);
			InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(bytes));
			logger.info(attachment + "");
			CSVReader csv = new CSVReader(reader);

			List<String[]> list = csv.readAll();
			if (list.size() > 500)
				throw new RuntimeException("Records size <= 500");
			if (list.size() <= 1)
				throw new RuntimeException("No Record");
			if (list.size() != 0) {
				if(StringUtils.equals(type, "item")){
					size = uploadItem(list);
				}else if(StringUtils.equals(type, "category")){
					size = uploadCategroy(list);
				}
			}
		} catch (Exception e) {
			response.put("error", e.getMessage());
			renderJSON(response);
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
		response.put("success", "true");
		response.put("size", size + "");
		renderJSON(response);
	}

	public static int uploadItem(List<String[]> list) {
		if (list.size() != 0) {
			List foodList = new ArrayList();
			Map<Long, Shop> shopMap = new HashMap<>();
			for (int i = 1; i < list.size(); i++) {
				String columns[] = list.get(i);
				if (columns.length != 12)
					throw new RuntimeException("Invalid CSV Columns");

				Food food = new Food();
				food.sn = columns[0];
				food.barCode = columns[1];
				food.name = columns[2];
				food.nameZh = columns[3];
				if (NumberUtils.isNumber(columns[4]))
					food.costPrice = Float.valueOf(columns[4]);
				if (NumberUtils.isNumber(columns[5]))
					food.retailPrice = Float.valueOf(columns[5]);

				food.picture = columns[6];
				food.status = true;
				food.createBy = session.get(Constants.CURRENT_USERNAME);
				food.createDate = new Date();
				// food.flag=true;

				if (NumberUtils.isNumber(columns[8])) {
					food.position = Integer.valueOf(columns[8]);
				}
				if (NumberUtils.isNumber(columns[9])) {
					if (Integer.valueOf(columns[9]) == 1)
						food.flag = true;
					else	
						food.flag = false;
				}
				food.type = columns[10];
				if (NumberUtils.isNumber(columns[7])) {
					Shop shop = null;
					if (!shopMap.containsKey(Long.valueOf(columns[7]))) {
						shop = Shop.view(Long.valueOf(columns[7]));
						shopMap.put(Long.valueOf(columns[7]), shop);
					}
					shop = shopMap.get(Long.valueOf(columns[7]));
					if (shop != null) {
						Category cat = Ebean.find(Category.class).where().eq("code", columns[11]).eq("shop.id", shop.id).findUnique();
						if (cat != null) {
							food.shop = new Shop();
							food.category = new Category();
							food.shop.id = shop.id;
							food.category.id = cat.id;
							foodList.add(food);
						}
					}
				}

			}
			Food.bulkStore(foodList);
			return foodList.size();
		}
		return 0;
	}

	private static int uploadCategroy(List<String[]> list) {
		if (list.size() != 0) {
			List categoryList = new ArrayList();
			Map<Long, Shop> shopMap = new HashMap<>();
			for (int i = 1; i < list.size(); i++) {
				String columns[] = list.get(i);
				if (columns.length != 5)
					throw new RuntimeException("Invalid CSV Columns");

				Category category = new Category();
				category.name = columns[0];
				category.nameZh = columns[1];
				category.code = columns[2];
				category.status = true;
				if (NumberUtils.isNumber(columns[3])) {
					category.position = Integer.valueOf(columns[3]);
				}
				category.status = true;
				category.createBy = session.get(Constants.CURRENT_USERNAME);
				category.createDate = new Date();

				if (NumberUtils.isNumber(columns[4])) {
					Shop shop = null;
					if (!shopMap.containsKey(Long.valueOf(columns[4]))) {
						shop = Shop.view(Long.valueOf(columns[4]));
						shopMap.put(Long.valueOf(columns[4]), shop);
					}
					shop = shopMap.get(Long.valueOf(columns[4]));
					if (shop != null) {
						category.shop = new Shop();
						category.shop.id = Long.valueOf(columns[4]);
						categoryList.add(category);
					}
				}

			}

			Category.bulkStore(categoryList);
			return categoryList.size();
		}
		return 0;
	}

	public static void search() {
		int currentPage = 1;
		if (request.params.get("iDisplayStart") != "0") {
			currentPage = (Integer.parseInt(request.params.get("iDisplayStart")) / Integer.parseInt(request.params
					.get("iDisplayLength"))) + 1;
		}
		Pagination pagination = new Pagination();
		pagination.currentPage = currentPage;

		pagination.pageSize = Integer.parseInt(request.params.get("iDisplayLength"));
		Map<String, String> searchs = new HashMap<String, String>();
		String outlet = request.params.get("sSearch_0");
		if (StringUtils.isEmpty(outlet) || "undefined".equalsIgnoreCase(outlet) || "ALL".equalsIgnoreCase(outlet))
			outlet = "%";
		if (session.get(Constants.CURRENT_USERTYPE).equals("OPERATOR"))
			outlet = session.get("shopName");
		searchs.put("shopName", outlet);
		renderJSON(Food.search(searchs, pagination));
	}
}

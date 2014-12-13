package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Audit;
import models.Dashboard;
import models.Food;
import models.ReportCashierClosing;
import models.ReportCollectionDetails;
import models.ReportExpensesDetails;
import models.ReportMoney;
import models.ReportPL;
import models.ReportQuantity;
import models.ReportTransactionDetail;
import models.ReportTransactionSummary;
import models.Shop;
import models.User;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.Pagination;
import constants.Constants;

public class Reports extends Basic {

	final static Logger logger = LoggerFactory.getLogger(Reports.class);

	public static void shops() {
		Pagination pagination = new Pagination();
		pagination.currentPage = 0;
		pagination.pageSize = 100;
		String shopName = null;
		String type = session.get(Constants.CURRENT_USERTYPE);
		if(!StringUtils.equals(type, Constants.USERTYPE_SUPER_ADMIN)){
			shopName = session.get(Constants.CURRENT_SHOPNAME);	
		}
		renderJSON(Shop.search(shopName, pagination));
	}

	public static void items() {
		Pagination pagination = new Pagination();
		pagination.currentPage = 0;
		pagination.pageSize = 100;
		String shopId = null;
		String type = session.get(Constants.CURRENT_USERTYPE);
		if(!StringUtils.equals(type, Constants.USERTYPE_SUPER_ADMIN)){
			shopId = session.get(Constants.CURRENT_SHOPID);	
		}
		renderJSON(Food.searchDistinct2(shopId, pagination));
	}

	public static void cashiers() {
		Pagination pagination = new Pagination();
		pagination.currentPage = 0;
		pagination.pageSize = 1000;
		Map<String, String> search = new HashMap<String, String>();
		search.put("usertype", "CASHIER");
		renderJSON(User.search(search, pagination));
	}

	public static void users() {
		Pagination pagination = new Pagination();
		pagination.currentPage = 0;
		pagination.pageSize = 1000;
		Map<String, String> search = new HashMap<String, String>();
		renderJSON(User.search(search, pagination));
	}

	public static void pieChartQuantity() throws IOException {
		Map searchs = getChartsParams();
		String type = (String) searchs.get("type");
		List<ReportQuantity> list = null;
		if (StringUtils.equals(type, "Daily")) {
			list = Dashboard.dailyPieChartQuantity(searchs);
		} else if (StringUtils.equals(type, "Monthly")) {
			list = Dashboard.monthlyPieChartQuantity(searchs);
		}
		renderJSON(list);
	}

	public static void pieChartMoney() throws IOException {
		Map searchs = getChartsParams();
		String type = (String) searchs.get("type");
		List<ReportMoney> list = null;
		if (StringUtils.equals(type, "Daily")) {
			list = Dashboard.dailyPieChartMoney(searchs);
		} else if (StringUtils.equals(type, "Monthly")) {
			list = Dashboard.monthlyPieChartMoney(searchs);
		}
		renderJSON(list);
	}

	public static void lineChartQuantity() throws IOException {
		DecimalFormat df = new DecimalFormat("0");
		Map searchs = getChartsParams();
		Map result = new HashMap();
		String type = (String) searchs.get("type");
		List<ReportMoney> list = null;
		if (StringUtils.equals(type, "Daily")) {
			list = Dashboard.dailyLineChartQuantity(searchs);
			Map<String, String> report = new HashMap<String, String>();
			if (list != null && list.size() > 0) {
				StringBuilder cats = new StringBuilder();
				StringBuilder datas = new StringBuilder();

				for (ReportMoney money : list) {
					report.put(money.orderHour, df.format(money.value));
				}

				for (int i = 1; i <= 24; i++) {
					String key = String.format("%02d", i);
					String val = report.get(key);
					if (StringUtils.isEmpty(val)) {
						datas.append("0|");
					} else {
						datas.append(val + "|");
					}
					cats.append(key + "|");
				}
				cats.deleteCharAt(cats.length() - 1);
				datas.deleteCharAt(datas.length() - 1);
				result.put("categories", cats.toString());
				result.put("dataset", datas.toString());
			}

		} else if (StringUtils.equals(type, "Monthly")) {
			list = Dashboard.monthlyLineChartQuantity(searchs);
			Map<String, String> report = new HashMap<String, String>();
			if (list != null && list.size() > 0) {
				String month = searchs.get("date") + "-01";
				int daysInMonth = 0;
				Date date = null;
				try {
					Calendar c = Calendar.getInstance();
					date = new SimpleDateFormat("yyyy-MM-dd").parse(month);
					c.setTime(date);
					daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				StringBuilder cats = new StringBuilder();
				StringBuilder datas = new StringBuilder();

				for (ReportMoney money : list) {
					report.put(money.orderDateStr, df.format(money.value));
				}

				for (int i = 1; i <= daysInMonth; i++) {
					String key = String.format("%02d", i);
					String val = report.get(key);
					if (StringUtils.isEmpty(val)) {
						datas.append("0|");
					} else {
						datas.append(val + "|");
					}
					cats.append(key + "|");
				}
				cats.deleteCharAt(cats.length() - 1);
				datas.deleteCharAt(datas.length() - 1);
				result.put("categories", cats.toString());
				result.put("dataset", datas.toString());
			}

		}

		renderJSON(result);
	}

	public static void lineChartMoney() throws IOException {
		DecimalFormat df = new DecimalFormat("0.00");
		Map searchs = getChartsParams();
		Map result = new HashMap();
		String type = (String) searchs.get("type");
		List<ReportMoney> list = null;
		if (StringUtils.equals(type, "Daily")) {
			list = Dashboard.dailyLineChartMoney(searchs);
			Map<String, String> report = new HashMap<String, String>();
			if (list != null && list.size() > 0) {
				StringBuilder cats = new StringBuilder();
				StringBuilder datas = new StringBuilder();

				for (ReportMoney money : list) {
					report.put(money.orderHour, df.format(money.value));
				}

				for (int i = 1; i <= 24; i++) {
					String key = String.format("%02d", i);
					String val = report.get(key);
					if (StringUtils.isEmpty(val)) {
						datas.append("0|");
					} else {
						datas.append(val + "|");
					}
					cats.append(key + "|");
				}
				cats.deleteCharAt(cats.length() - 1);
				datas.deleteCharAt(datas.length() - 1);
				result.put("categories", cats.toString());
				result.put("dataset", datas.toString());
			}

		} else if (StringUtils.equals(type, "Monthly")) {
			list = Dashboard.monthlyLineChartMoney(searchs);
			Map<String, String> report = new HashMap<String, String>();
			if (list != null && list.size() > 0) {
				String month = searchs.get("date") + "-01";
				int daysInMonth = 0;
				Date date = null;
				try {
					Calendar c = Calendar.getInstance();
					date = new SimpleDateFormat("yyyy-MM-dd").parse(month);
					c.setTime(date);
					daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				StringBuilder cats = new StringBuilder();
				StringBuilder datas = new StringBuilder();

				for (ReportMoney money : list) {
					report.put(money.orderDateStr, df.format(money.value));
				}

				for (int i = 1; i <= daysInMonth; i++) {
					String key = String.format("%02d", i);
					String val = report.get(key);
					if (StringUtils.isEmpty(val)) {
						datas.append("0|");
					} else {
						datas.append(val + "|");
					}
					cats.append(key + "|");
				}
				cats.deleteCharAt(cats.length() - 1);
				datas.deleteCharAt(datas.length() - 1);
				result.put("categories", cats.toString());
				result.put("dataset", datas.toString());
			}
		}
		renderJSON(result);
	}

	public static Map getChartsParams() throws IOException {
		Map searchs = new HashMap();
		String outlet = request.params.get("shopName");
		if (StringUtils.isEmpty(outlet) || "undefined".equalsIgnoreCase(outlet) || "ALL".equalsIgnoreCase(outlet)) {
			outlet = "%";
		}

		if (session.get(Constants.CURRENT_USERTYPE).equals(Constants.USERTYPE_OPERATOR)) {
			outlet = session.get("shopName");
		}

		searchs.put("shopName", outlet);
		String type = request.params.get("type");
		searchs.put("type", StringUtils.defaultIfEmpty(type, null));
		String date = request.params.get("date");
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (StringUtils.isEmpty(date)) {
			date = sdf.format(today);
		}
		searchs.put("date", date);
		session.put("reportTransactionSearchs", new ObjectMapper().writeValueAsString(searchs));
		return searchs;

	}

	public static void transaction() throws IOException {

		int currentPage = 1;
		if (request.params.get("iDisplayStart") != "0") {
			currentPage = (Integer.parseInt(request.params.get("iDisplayStart")) / Integer.parseInt(request.params.get("iDisplayLength"))) + 1;
		}
		Pagination pagination = new Pagination();
		pagination.currentPage = currentPage;
		pagination.pageSize = Integer.parseInt(request.params.get("iDisplayLength"));
		Map searchs = new HashMap();
		String food = request.params.get("sSearch_0");
		if (StringUtils.isEmpty(food) || "undefined".equalsIgnoreCase(food) || "ALL".equalsIgnoreCase(food)) {
			food = "%";
		}
		searchs.put("food", food);

		String outlet = request.params.get("sSearch_2");
		if (StringUtils.isEmpty(outlet) || "undefined".equalsIgnoreCase(outlet) || "ALL".equalsIgnoreCase(outlet)) {
			outlet = "%";
		}

		if (session.get(Constants.CURRENT_USERTYPE).equals(Constants.USERTYPE_OPERATOR)) {
			outlet = session.get("shopName");
		}

		searchs.put("shopName", outlet);
		String dateFrom = request.params.get("sSearch_3");
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (StringUtils.isEmpty(dateFrom) || "undefined".equalsIgnoreCase(dateFrom)) {
			dateFrom = sdf.format(today) + " 00:00:00";
		}
		searchs.put("dateFrom", dateFrom);
		String dateTo = request.params.get("sSearch_4");
		if (StringUtils.isEmpty(dateTo) || "undefined".equalsIgnoreCase(dateTo)) {
			dateTo = sdf.format(today) + " 23:59:59";
		}
		searchs.put("dateTo", dateTo);

		session.put("reportTransactionSearchs", new ObjectMapper().writeValueAsString(searchs));
		renderJSON(ReportTransactionSummary.search(searchs, pagination));
	}

	public static void transactionSummary() throws IOException {

		Map searchs = new HashMap();
		String outlet = request.params.get("sSearch_1");
		if (StringUtils.isEmpty(outlet) || "undefined".equalsIgnoreCase(outlet) || "ALL".equalsIgnoreCase(outlet)) {
			outlet = "%";
		}

		if (session.get(Constants.CURRENT_USERTYPE).equals(Constants.USERTYPE_OPERATOR)) {
			outlet = session.get("shopName");
		}

		searchs.put("shopName", outlet);
		String dateFrom = request.params.get("sSearch_2");
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (StringUtils.isEmpty(dateFrom) || "undefined".equalsIgnoreCase(dateFrom)) {
			dateFrom = sdf.format(today) + " 00:00:00";
		}
		searchs.put("dateFrom", dateFrom);
		String dateTo = request.params.get("sSearch_3");
		if (StringUtils.isEmpty(dateTo) || "undefined".equalsIgnoreCase(dateTo)) {
			dateTo = sdf.format(today) + " 23:59:59";
		}
		searchs.put("dateTo", dateTo);

		session.put("reportTransactionSearchs", new ObjectMapper().writeValueAsString(searchs));

		Shop shop = Shop.findByName(outlet);
		Pagination pagination = new Pagination();
		if(shop != null){
			pagination = ReportTransactionSummary.search(searchs, shop);
		}
		renderJSON(pagination);
	}

	public static void pl() throws IOException {
		int currentPage = 1;
		if (request.params.get("iDisplayStart") != "0") {
			currentPage = (Integer.parseInt(request.params.get("iDisplayStart")) / Integer.parseInt(request.params.get("iDisplayLength"))) + 1;
		}
		Pagination pagination = new Pagination();
		pagination.currentPage = currentPage;

		pagination.pageSize = Integer.parseInt(request.params.get("iDisplayLength"));
		Map searchs = new HashMap();
		String outlet = request.params.get("sSearch_1");
		if (StringUtils.isEmpty(outlet) || "undefined".equalsIgnoreCase(outlet) || "ALL".equalsIgnoreCase(outlet)) {
			outlet = "%";
		}

		if (session.get(Constants.CURRENT_USERTYPE).equals(Constants.USERTYPE_OPERATOR)) {
			outlet = session.get("shopName");
		}
		searchs.put("shopName", outlet);

		String dateFrom = request.params.get("sSearch_2");
		if (StringUtils.isEmpty(dateFrom) || "undefined".equalsIgnoreCase(dateFrom)) {
			dateFrom = "2000-01-01";
		}
		searchs.put("dateFrom", dateFrom);

		String dateTo = request.params.get("sSearch_3");
		if (StringUtils.isEmpty(dateTo) || "undefined".equalsIgnoreCase(dateTo)) {
			dateTo = "2222-01-01";
		}
		searchs.put("dateTo", dateTo);

		session.put("reportPlSearch", new ObjectMapper().writeValueAsString(searchs));
		renderJSON(ReportPL.search(searchs, pagination));

	}

	public static void loginAudit() throws IOException {

		int currentPage = 1;
		if (request.params.get("iDisplayStart") != "0") {
			currentPage = (Integer.parseInt(request.params.get("iDisplayStart")) / Integer.parseInt(request.params.get("iDisplayLength"))) + 1;
		}
		Pagination pagination = new Pagination();
		pagination.currentPage = currentPage;
		pagination.pageSize = Integer.parseInt(request.params.get("iDisplayLength"));
		Map searchs = new HashMap();
		String cashier = request.params.get("sSearch_0");
		if (StringUtils.isEmpty(cashier) || "undefined".equalsIgnoreCase(cashier) || "ALL".equalsIgnoreCase(cashier)) {
			cashier = "%";
		}

		searchs.put("user.realname", cashier);
		String outlet = request.params.get("sSearch_1");
		if (StringUtils.isEmpty(outlet) || "undefined".equalsIgnoreCase(outlet) || "ALL".equalsIgnoreCase(outlet)) {
			outlet = "%";
		}

		if (session.get(Constants.CURRENT_USERTYPE).equals(Constants.USERTYPE_OPERATOR)) {
			outlet = session.get("shopName");
		}
		searchs.put("shop.name", outlet);

		String dateFrom = request.params.get("sSearch_2");
		if (StringUtils.isEmpty(dateFrom) || "undefined".equalsIgnoreCase(dateFrom)) {
			dateFrom = "2000-01-01";
		}
		searchs.put("dateFrom", dateFrom);

		String dateTo = request.params.get("sSearch_3");
		if (StringUtils.isEmpty(dateTo) || "undefined".equalsIgnoreCase(dateTo)) {
			dateTo = "2222-01-01";
		}
		searchs.put("dateTo", dateTo);

		session.put("loginAuditSearch", new ObjectMapper().writeValueAsString(searchs));
		renderJSON(Audit.search(searchs, pagination));

	}

	public static void cashierClosing() throws IOException {
		int currentPage = 1;
		if (request.params.get("iDisplayStart") != "0") {
			currentPage = (Integer.parseInt(request.params.get("iDisplayStart")) / Integer.parseInt(request.params.get("iDisplayLength"))) + 1;
		}
		Pagination pagination = new Pagination();
		pagination.currentPage = currentPage;
		pagination.pageSize = Integer.parseInt(request.params.get("iDisplayLength"));
		Map searchs = new HashMap();
		String cashier = request.params.get("sSearch_0");
		if (StringUtils.isEmpty(cashier) || "undefined".equalsIgnoreCase(cashier) || "ALL".equalsIgnoreCase(cashier)) {
			cashier = "%";
		}
		searchs.put("realName", cashier);

		String outlet = request.params.get("sSearch_1");
		if (StringUtils.isEmpty(outlet) || "undefined".equalsIgnoreCase(outlet) || "ALL".equalsIgnoreCase(outlet)) {
			outlet = "%";
		}

		if (session.get(Constants.CURRENT_USERTYPE).equals(Constants.USERTYPE_OPERATOR)) {
			outlet = session.get("shopName");
		}
		searchs.put("shopName", outlet);

		String dateFrom = request.params.get("sSearch_2");
		if (StringUtils.isEmpty(dateFrom) || "undefined".equalsIgnoreCase(dateFrom)) {
			dateFrom = "2000-01-01";
		}
		searchs.put("dateFrom", dateFrom);

		String dateTo = request.params.get("sSearch_3");
		if (StringUtils.isEmpty(dateTo) || "undefined".equalsIgnoreCase(dateTo)) {
			dateTo = "2222-01-01";
		}

		searchs.put("dateTo", dateTo);
		session.put("cashierClosingSearch", new ObjectMapper().writeValueAsString(searchs));
		renderJSON(ReportCashierClosing.search(searchs, pagination));
	}

	public static void exportCashierClosing() throws IOException {

		InputStream is = Reports.getControllerClass().getClassLoader().getResourceAsStream("reports/CashierClosing.jasper");

		Pagination pagination = new Pagination();
		pagination.all = true;
		pagination.currentPage = 1;

		logger.info("Cashier Closing " + session);
		Map searchs = new HashMap();
		if (session.get("cashierClosingSearch") != null)
			searchs = new ObjectMapper().readValue(session.get("cashierClosingSearch"), Map.class);
		else {
			String cashier = "%";
			searchs.put("realName", cashier);
			String outlet = "%";
			searchs.put("shopName", outlet);
			String dateFrom = "2000-01-01";
			searchs.put("dateFrom", dateFrom);
			String dateTo = "2222-01-01";
			searchs.put("dateTo", dateTo);
		}

		if (session.get(Constants.CURRENT_USERTYPE).equals(Constants.USERTYPE_OPERATOR)) {
			searchs.put("shopName", session.get("shopName"));
		}

		pagination = ReportCashierClosing.search((Map) searchs, pagination);
		JRDataSource dataSource = new JRBeanCollectionDataSource(pagination.recordList);
		JasperPrint print = null;
		try {
			Map parameters = new HashMap();
			parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
			print = JasperFillManager.fillReport(is, parameters, dataSource);
			exportXls(print, "CashierClosing.xls");
		} catch (JRException e) {
			logger.error("Export Error", e);
		}
	}

	public static void exportExpensesDetails() throws IOException {
		InputStream is = Reports.getControllerClass().getClassLoader().getResourceAsStream("reports/ExpensesDetails.jasper");
		Pagination pagination = new Pagination();
		pagination.all = true;
		pagination.currentPage = 1;

		logger.info("ExpensesDetails " + session);
		Map searchs = new HashMap();
		if (session.get("cashierClosingSearch") != null) {
			searchs = new ObjectMapper().readValue(session.get("cashierClosingSearch"), Map.class);
		} else {
			String cashier = "%";
			searchs.put("realName", cashier);
			String outlet = "%";
			searchs.put("shopName", outlet);
			String dateFrom = "2000-01-01";
			searchs.put("dateFrom", dateFrom);
			String dateTo = "2222-01-01";
			searchs.put("dateTo", dateTo);
		}

		if (session.get(Constants.CURRENT_USERTYPE).equals(Constants.USERTYPE_OPERATOR)) {
			searchs.put("shopName", session.get("shopName"));
		}

		pagination = ReportExpensesDetails.search((Map) searchs, pagination);
		JRDataSource dataSource = new JRBeanCollectionDataSource(pagination.recordList);
		JasperPrint print = null;
		try {
			Map parameters = new HashMap();
			parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
			print = JasperFillManager.fillReport(is, parameters, dataSource);
			exportXls(print, "ExpensesDetails.xls");
		} catch (JRException e) {
			logger.error("Export Error", e);
		}
	}

	public static void exportCollectionDetails() throws IOException {
		InputStream is = Reports.getControllerClass().getClassLoader().getResourceAsStream("reports/CollectionDetails.jasper");
		Pagination pagination = new Pagination();
		pagination.all = true;
		pagination.currentPage = 1;

		logger.info("CollectionDetails " + session);
		Map searchs = new HashMap();
		if (session.get("cashierClosingSearch") != null)
			searchs = new ObjectMapper().readValue(session.get("cashierClosingSearch"), Map.class);
		else {
			String cashier = "%";
			searchs.put("realName", cashier);
			String outlet = "%";
			searchs.put("shopName", outlet);
			String dateFrom = "2000-01-01";
			searchs.put("dateFrom", dateFrom);
			String dateTo = "2222-01-01";
			searchs.put("dateTo", dateTo);
		}

		if (session.get(Constants.CURRENT_USERTYPE).equals(Constants.USERTYPE_OPERATOR)) {
			searchs.put("shopName", session.get("shopName"));
		}

		pagination = ReportCollectionDetails.search((Map) searchs, pagination);
		JRDataSource dataSource = new JRBeanCollectionDataSource(pagination.recordList);
		JasperPrint print = null;
		try {
			Map parameters = new HashMap();
			parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
			print = JasperFillManager.fillReport(is, parameters, dataSource);
			exportXls(print, "CollectionDetails.xls");
		} catch (JRException e) {
			logger.error("Export Error", e);
		}
	}

	public static void exportLoginAudit() throws IOException {
		InputStream is = Reports.getControllerClass().getClassLoader().getResourceAsStream("reports/LoginAudit.jasper");
		Pagination pagination = new Pagination();
		pagination.all = true;
		pagination.currentPage = 1;

		Map searchs = new HashMap();
		if (session.get("loginAuditSearch") != null)
			searchs = new ObjectMapper().readValue(session.get("loginAuditSearch"), Map.class);
		else {
			String cashier = "%";
			searchs.put("user.realname", cashier);
			String outlet = "%";
			searchs.put("shop.name", outlet);
			String dateFrom = "2000-01-01";
			searchs.put("dateFrom", dateFrom);
			String dateTo = "2222-01-01";
			searchs.put("dateTo", dateTo);
		}
		pagination = Audit.search((Map) searchs, pagination);
		JRDataSource dataSource = new JRBeanCollectionDataSource(pagination.recordList);
		JasperPrint print = null;
		try {
			Map parameters = new HashMap();
			parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
			print = JasperFillManager.fillReport(is, parameters, dataSource);
			exportXls(print, "LoginAudit.xls");
		} catch (JRException e) {
			logger.error("Export Error", e);
		}
	}

	public static void exportPL() throws IOException {
		InputStream is = Reports.getControllerClass().getClassLoader().getResourceAsStream("reports/PL.jasper");

		Pagination pagination = new Pagination();
		pagination.all = true;
		pagination.currentPage = 1;

		Map searchs = new HashMap();
		if (session.get("reportPlSearch") != null)
			searchs = new ObjectMapper().readValue(session.get("reportPlSearch"), Map.class);
		else {
			String outlet = "%";
			searchs.put("shopName", outlet);
			String dateFrom = "2000-01-01";
			searchs.put("dateFrom", dateFrom);
			String dateTo = "2222-01-01";
			searchs.put("dateTo", dateTo);
		}

		if (session.get(Constants.CURRENT_USERTYPE).equals(Constants.USERTYPE_OPERATOR)) {
			searchs.put("shopName", session.get("shopName"));
		}

		pagination = ReportPL.search((Map) searchs, pagination);
		JRDataSource dataSource = new JRBeanCollectionDataSource(pagination.recordList);
		JasperPrint print = null;
		try {
			Map parameters = new HashMap();
			parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
			print = JasperFillManager.fillReport(is, parameters, dataSource);
			exportXls(print, "PL.xls");
		} catch (JRException e) {
			logger.error("Error", e);
		}
	}

	public static void exportTransactionDetail() throws IOException {

		InputStream is = Reports.getControllerClass().getClassLoader().getResourceAsStream("reports/TransactionDetail.jasper");
		Pagination pagination = new Pagination();
		pagination.all = true;
		pagination.currentPage = 1;

		Map searchs = new HashMap();
		if (session.get("reportTransactionSearchs") != null)
			searchs = new ObjectMapper().readValue(session.get("reportTransactionSearchs"), Map.class);
		else {
			String food = "%";
			searchs.put("food", food);
			String outlet = "%";
			searchs.put("shopName", outlet);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateFrom = sdf.format(new Date());
			searchs.put("dateFrom", dateFrom + " 00:00:00");
			String dateTo = sdf.format(new Date());
			searchs.put("dateTo", dateTo + " 23:59:59");
		}

		if (session.get(Constants.CURRENT_USERTYPE).equals(Constants.USERTYPE_OPERATOR)) {
			searchs.put("shopName", session.get("shopName"));
		}

		pagination = ReportTransactionDetail.search((Map) searchs, pagination);
		JRDataSource dataSource = new JRBeanCollectionDataSource(pagination.recordList);
		JasperPrint print = null;
		try {
			Map parameters = new HashMap();
			parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
			print = JasperFillManager.fillReport(is, parameters, dataSource);
			exportXls(print, "TransactionDetail.xls");
		} catch (JRException e) {
			logger.error("Error", e);
		}
	}

	public static void exportTransactionSummary() throws IOException {

		InputStream is = Reports.getControllerClass().getClassLoader().getResourceAsStream("reports/TransactionSummary.jasper");

		Pagination pagination = new Pagination();
		pagination.all = true;
		pagination.currentPage = 1;

		Map searchs = new HashMap();
		if (session.get("reportTransactionSearchs") != null)
			searchs = new ObjectMapper().readValue(session.get("reportTransactionSearchs"), Map.class);
		else {
			String food = "%";
			searchs.put("food", food);
			String outlet = "%";
			searchs.put("shopName", outlet);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateFrom = sdf.format(new Date());
			searchs.put("dateFrom", dateFrom + " 00:00:00");
			String dateTo = sdf.format(new Date());
			searchs.put("dateTo", dateTo + " 23:59:59");
		}

		if (session.get(Constants.CURRENT_USERTYPE).equals(Constants.USERTYPE_OPERATOR)) {
			searchs.put("shopName", session.get("shopName"));
		}

		pagination = ReportTransactionSummary.search(searchs, pagination);
		JRDataSource dataSource = new JRBeanCollectionDataSource(pagination.recordList);
		JasperPrint print = null;
		try {
			Map parameters = new HashMap();
			parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
			print = JasperFillManager.fillReport(is, parameters, dataSource);
			exportXls(print, "TransactionSummary.xls");
		} catch (JRException e) {
			logger.error("Error", e);
		}
	}
	
	public static void exportSummary() throws IOException {

		InputStream is = Reports.getControllerClass().getClassLoader().getResourceAsStream("reports/Summary.jasper");
		
		Pagination pagination = new Pagination();
		pagination.all = true;
		pagination.currentPage = 1;
		Shop shop = null;
		Map searchs = new HashMap();
		if (session.get("reportTransactionSearchs") != null)
			searchs = new ObjectMapper().readValue(session.get("reportTransactionSearchs"), Map.class);
		else {
			
			String outlet = request.params.get("sSearch_1");
			if (StringUtils.isEmpty(outlet) || "undefined".equalsIgnoreCase(outlet) || "ALL".equalsIgnoreCase(outlet)) {
				outlet = "%";
			}

			if (session.get(Constants.CURRENT_USERTYPE).equals(Constants.USERTYPE_OPERATOR)) {
				outlet = session.get("shopName");
			}

			searchs.put("shopName", outlet);
			String dateFrom = request.params.get("sSearch_2");
			Date today = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (StringUtils.isEmpty(dateFrom) || "undefined".equalsIgnoreCase(dateFrom)) {
				dateFrom = sdf.format(today) + " 00:00:00";
			}
			searchs.put("dateFrom", dateFrom);
			String dateTo = request.params.get("sSearch_3");
			if (StringUtils.isEmpty(dateTo) || "undefined".equalsIgnoreCase(dateTo)) {
				dateTo = sdf.format(today) + " 23:59:59";
			}
			searchs.put("dateTo", dateTo);
			session.put("reportTransactionSearchs", new ObjectMapper().writeValueAsString(searchs));

		}

		shop = Shop.findByName((String)searchs.get("shopName"));
		if(shop != null){
			pagination = ReportTransactionSummary.search(searchs, shop);
		}

		JRDataSource dataSource = new JRBeanCollectionDataSource(pagination.recordList);
		JasperPrint print = null;
		try {
			Map parameters = new HashMap();
			parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
			print = JasperFillManager.fillReport(is, parameters, dataSource);
			exportXls(print, "TransactionSummary.xls");
		} catch (JRException e) {
			logger.error("Error", e);
		}
	}

	private static void exportXls(JasperPrint print, String fileName) throws JRException {

		JRXlsExporter exporterXLS = new JRXlsExporter();
		exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, print);
		exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, response.out);
		exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
		exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
		exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
		exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);

		exporterXLS.exportReport();
		response.setHeader("Content-Type", "application/vnd.ms-excel");
		response.setHeader("Content-Disposition", " attachment; filename=" + fileName);
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");
	}

}

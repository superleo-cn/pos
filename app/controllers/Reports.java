package controllers;

import constants.Constants;
import models.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import play.Logger;
import utils.Pagination;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lala
 * Date: 11/13/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class Reports extends Basic {

    public static void shops() {
        Pagination pagination = new Pagination();
        pagination.currentPage = 0;
        pagination.pageSize = 100;
        renderJSON(Shop.search(null, pagination));
    }


    public static void items() {
        Pagination pagination = new Pagination();
        pagination.currentPage = 0;
        pagination.pageSize = 100;
        renderJSON(Food.searchDistinct2(null, pagination));
    }


    public static void cashiers() {
        Pagination pagination = new Pagination();
        pagination.currentPage = 0;
        pagination.pageSize = 1000;
        Map<String,String> search = new HashMap<String, String>();
        search.put("usertype","CASHIER");
        renderJSON(User.search(search, pagination));
    }

    public static void users() {
        Pagination pagination = new Pagination();
        pagination.currentPage = 0;
        pagination.pageSize = 1000;
        Map<String,String> search = new HashMap<String, String>();
        renderJSON(User.search(search, pagination));
    }
    
    
    public static void charts() throws IOException {

        Map searchs = new HashMap();

        String outlet =  request.params.get("shopName");
        if(StringUtils.isEmpty(outlet) || "undefined".equalsIgnoreCase(outlet) || "ALL".equalsIgnoreCase(outlet))
            outlet="%";
        if(session.get(Constants.CURRENT_USERTYPE).equals("OPERATOR"))
            outlet=session.get("shopName");

        searchs.put("shopName",outlet);
        String dateFrom =  request.params.get("dateFrom");
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(StringUtils.isEmpty(dateFrom) || "undefined".equalsIgnoreCase(dateFrom))
            dateFrom=sdf.format(today)+" 00:00:00";
        searchs.put("dateFrom",dateFrom);
        String dateTo =  request.params.get("dateTo");
        if(StringUtils.isEmpty(dateTo) || "undefined".equalsIgnoreCase(dateTo ))
            dateTo=sdf.format(today)+" 23:59:59";
        searchs.put("dateTo",dateTo);

        session.put("reportTransactionSearchs",new ObjectMapper().writeValueAsString(searchs));

        renderJSON(ReportTransactionSummary.charts(searchs));

    }
    
    
    public static void transaction() throws IOException {

        int currentPage = 1;
        if(request.params.get("iDisplayStart")!="0") {
            currentPage = (Integer.parseInt(request.params.get("iDisplayStart"))/Integer.parseInt(request.params.get("iDisplayLength")))+1;
        }
        Pagination pagination = new Pagination();
        pagination.currentPage = currentPage;
        pagination.pageSize = Integer.parseInt(request.params.get("iDisplayLength"));
        Map searchs = new HashMap();
        String food =  request.params.get("sSearch_0");
        if(StringUtils.isEmpty(food) || "undefined".equalsIgnoreCase(food) || "ALL".equalsIgnoreCase(food))
            food="%";
        searchs.put("food",food);

        String outlet =  request.params.get("sSearch_1");
        if(StringUtils.isEmpty(outlet) || "undefined".equalsIgnoreCase(outlet) || "ALL".equalsIgnoreCase(outlet))
            outlet="%";
        if(session.get(Constants.CURRENT_USERTYPE).equals("OPERATOR"))
            outlet=session.get("shopName");

        searchs.put("shopName",outlet);
        String dateFrom =  request.params.get("sSearch_2");
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(StringUtils.isEmpty(dateFrom) || "undefined".equalsIgnoreCase(dateFrom))
            dateFrom=sdf.format(today)+" 00:00:00";
        searchs.put("dateFrom",dateFrom);
        String dateTo =  request.params.get("sSearch_3");
        if(StringUtils.isEmpty(dateTo) || "undefined".equalsIgnoreCase(dateTo ))
            dateTo=sdf.format(today)+" 23:59:59";
        searchs.put("dateTo",dateTo);

        session.put("reportTransactionSearchs",new ObjectMapper().writeValueAsString(searchs));

        renderJSON(ReportTransactionSummary.search(searchs, pagination));

    }


    public static void pl() throws IOException {

        int currentPage = 1;
        if(request.params.get("iDisplayStart")!="0") {
            currentPage = (Integer.parseInt(request.params.get("iDisplayStart"))/Integer.parseInt(request.params.get("iDisplayLength")))+1;
        }
        Pagination pagination = new Pagination();
        pagination.currentPage = currentPage;

        pagination.pageSize = Integer.parseInt(request.params.get("iDisplayLength"));
        Map searchs = new HashMap();
        String outlet =  request.params.get("sSearch_1");
        if(StringUtils.isEmpty(outlet) || "undefined".equalsIgnoreCase(outlet) || "ALL".equalsIgnoreCase(outlet))
            outlet="%";
        if(session.get(Constants.CURRENT_USERTYPE).equals("OPERATOR"))
            outlet=session.get("shopName");
        searchs.put("shopName",outlet);

        String dateFrom =  request.params.get("sSearch_2");
        if(StringUtils.isEmpty(dateFrom) || "undefined".equalsIgnoreCase(dateFrom))
            dateFrom="2000-01-01";
        searchs.put("dateFrom",dateFrom);

        String dateTo =  request.params.get("sSearch_3");
        if(StringUtils.isEmpty(dateTo) || "undefined".equalsIgnoreCase(dateTo ))
            dateTo="2222-01-01";

        searchs.put("dateTo",dateTo);

        session.put("reportPlSearch",new ObjectMapper().writeValueAsString(searchs));

        renderJSON(ReportPL.search(searchs, pagination));

    }


    public static void loginAudit() throws IOException {

        int currentPage = 1;
        if(request.params.get("iDisplayStart")!="0") {
            currentPage = (Integer.parseInt(request.params.get("iDisplayStart"))/Integer.parseInt(request.params.get("iDisplayLength")))+1;
        }
        Pagination pagination = new Pagination();
        pagination.currentPage = currentPage;
        pagination.pageSize = Integer.parseInt(request.params.get("iDisplayLength"));
        Map searchs = new HashMap();
        String cashier =  request.params.get("sSearch_0");
        if(StringUtils.isEmpty(cashier) || "undefined".equalsIgnoreCase(cashier)  || "ALL".equalsIgnoreCase(cashier))
            cashier="%";
        searchs.put("user.realname",cashier);
        String outlet =  request.params.get("sSearch_1");
        if(StringUtils.isEmpty(outlet) || "undefined".equalsIgnoreCase(outlet)  || "ALL".equalsIgnoreCase(outlet))
            outlet="%";
        if(session.get(Constants.CURRENT_USERTYPE).equals("OPERATOR"))
            outlet=session.get("shopName");

        searchs.put("shop.name",outlet);

        String dateFrom =  request.params.get("sSearch_2");
        if(StringUtils.isEmpty(dateFrom) || "undefined".equalsIgnoreCase(dateFrom))
            dateFrom="2000-01-01";
        searchs.put("dateFrom",dateFrom);

        String dateTo =  request.params.get("sSearch_3");
        if(StringUtils.isEmpty(dateTo ) || "undefined".equalsIgnoreCase(dateTo ))
            dateTo="2222-01-01";

        searchs.put("dateTo",dateTo);

        session.put("loginAuditSearch",new ObjectMapper().writeValueAsString(searchs));

        renderJSON(Audit.search(searchs, pagination));

    }

    public static void cashierClosing() throws IOException {

        int currentPage = 1;
        if(request.params.get("iDisplayStart")!="0") {
            currentPage = (Integer.parseInt(request.params.get("iDisplayStart"))/Integer.parseInt(request.params.get("iDisplayLength")))+1;
        }
        Pagination pagination = new Pagination();
        pagination.currentPage = currentPage;
        pagination.pageSize = Integer.parseInt(request.params.get("iDisplayLength"));
        Map searchs = new HashMap();
        String cashier =  request.params.get("sSearch_0");
        if(StringUtils.isEmpty(cashier) || "undefined".equalsIgnoreCase(cashier)  || "ALL".equalsIgnoreCase(cashier))
            cashier="%";
        searchs.put("realName",cashier);
        String outlet =  request.params.get("sSearch_1");
        if(StringUtils.isEmpty(outlet) || "undefined".equalsIgnoreCase(outlet)  || "ALL".equalsIgnoreCase(outlet))
            outlet="%";
        if(session.get(Constants.CURRENT_USERTYPE).equals("OPERATOR"))
            outlet=session.get("shopName");
        searchs.put("shopName",outlet);

        String dateFrom =  request.params.get("sSearch_2");
        if(StringUtils.isEmpty(dateFrom) || "undefined".equalsIgnoreCase(dateFrom))
            dateFrom="2000-01-01";
        searchs.put("dateFrom",dateFrom);

        String dateTo =  request.params.get("sSearch_3");
        if(StringUtils.isEmpty(dateTo ) || "undefined".equalsIgnoreCase(dateTo ))
            dateTo="2222-01-01";

        searchs.put("dateTo",dateTo);


        session.put("cashierClosingSearch",new ObjectMapper().writeValueAsString(searchs));

        renderJSON(ReportCashierClosing.search(searchs, pagination));

    }


    public static void exportCashierClosing() throws IOException {


        InputStream is = Reports.getControllerClass().getClassLoader().getResourceAsStream("reports/CashierClosing.jasper");

        Pagination pagination = new Pagination();
        pagination.all = true;
        pagination.currentPage=1;

        play.Logger.info("Cashier Closing " + session);
        Map searchs = new HashMap();
        if(session.get("cashierClosingSearch")!=null)
            searchs = new ObjectMapper().readValue(session.get("cashierClosingSearch"), Map.class);
        else {
            String cashier="%";
            searchs.put("realName",cashier);
            String outlet ="%";
            searchs.put("shopName",outlet);
            String dateFrom="2000-01-01";
            searchs.put("dateFrom",dateFrom);
            String dateTo = "2222-01-01";
            searchs.put("dateTo",dateTo);
        }

        if(session.get(Constants.CURRENT_USERTYPE).equals("OPERATOR"))
            searchs.put("shopName",session.get("shopName"));

        pagination = ReportCashierClosing.search((Map) searchs, pagination);
        JRDataSource dataSource = new JRBeanCollectionDataSource(pagination.recordList);
        JasperPrint print = null;
        try {

            Map parameters = new HashMap();
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
            print = JasperFillManager.fillReport(is, parameters, dataSource);

            exportXls(print,"CashierClosing.xls");

        } catch (JRException e) {
            e.printStackTrace();
            Logger.error("Error",e);
        }
    }



    public static void exportExpensesDetails() throws IOException {

        InputStream is = Reports.getControllerClass().getClassLoader().getResourceAsStream("reports/ExpensesDetails.jasper");

        Pagination pagination = new Pagination();
        pagination.all = true;
        pagination.currentPage=1;

        play.Logger.info("ExpensesDetails " + session);
        Map searchs = new HashMap();
        if(session.get("cashierClosingSearch")!=null)
            searchs = new ObjectMapper().readValue(session.get("cashierClosingSearch"), Map.class);
        else {
            String cashier="%";
            searchs.put("realName",cashier);
            String outlet ="%";
            searchs.put("shopName",outlet);
            String dateFrom="2000-01-01";
            searchs.put("dateFrom",dateFrom);
            String dateTo = "2222-01-01";
            searchs.put("dateTo",dateTo);
        }

        if(session.get(Constants.CURRENT_USERTYPE).equals("OPERATOR"))
            searchs.put("shopName",session.get("shopName"));

        pagination = ReportExpensesDetails.search((Map) searchs, pagination);
        JRDataSource dataSource = new JRBeanCollectionDataSource(pagination.recordList);
        JasperPrint print = null;
        try {

            Map parameters = new HashMap();
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
            print = JasperFillManager.fillReport(is, parameters, dataSource);

            exportXls(print,"ExpensesDetails.xls");

        } catch (JRException e) {
            e.printStackTrace();
            Logger.error("Error",e);
        }
    }


    public static void exportCollectionDetails() throws IOException {

        InputStream is = Reports.getControllerClass().getClassLoader().getResourceAsStream("reports/CollectionDetails.jasper");

        Pagination pagination = new Pagination();
        pagination.all = true;
        pagination.currentPage=1;

        play.Logger.info("CollectionDetails " + session);
        Map searchs = new HashMap();
        if(session.get("cashierClosingSearch")!=null)
            searchs = new ObjectMapper().readValue(session.get("cashierClosingSearch"), Map.class);
        else {
            String cashier="%";
            searchs.put("realName",cashier);
            String outlet ="%";
            searchs.put("shopName",outlet);
            String dateFrom="2000-01-01";
            searchs.put("dateFrom",dateFrom);
            String dateTo = "2222-01-01";
            searchs.put("dateTo",dateTo);
        }

        if(session.get(Constants.CURRENT_USERTYPE).equals("OPERATOR"))
            searchs.put("shopName",session.get("shopName"));

        pagination = ReportCollectionDetails.search((Map) searchs, pagination);
        JRDataSource dataSource = new JRBeanCollectionDataSource(pagination.recordList);
        JasperPrint print = null;
        try {

            Map parameters = new HashMap();
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
            print = JasperFillManager.fillReport(is, parameters, dataSource);

            exportXls(print,"CollectionDetails.xls");

        } catch (JRException e) {
            e.printStackTrace();
            Logger.error("Error",e);
        }
    }


    public static void exportLoginAudit() throws IOException {


        InputStream is = Reports.getControllerClass().getClassLoader().getResourceAsStream("reports/LoginAudit.jasper");

        Pagination pagination = new Pagination();
        pagination.all = true;
        pagination.currentPage=1;

        Map searchs = new HashMap();
        if(session.get("loginAuditSearch")!=null)
            searchs = new ObjectMapper().readValue(session.get("loginAuditSearch"), Map.class);
        else {
            String cashier="%";
            searchs.put("user.realname",cashier);
            String outlet ="%";
            searchs.put("shop.name",outlet);
            String dateFrom="2000-01-01";
            searchs.put("dateFrom",dateFrom);
            String dateTo = "2222-01-01";
            searchs.put("dateTo",dateTo);
        }
        pagination = Audit.search((Map) searchs, pagination);
        JRDataSource dataSource = new JRBeanCollectionDataSource(pagination.recordList);
        JasperPrint print = null;
        try {
            Map parameters = new HashMap();
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
            print = JasperFillManager.fillReport(is, parameters, dataSource);

            exportXls(print,"LoginAudit.xls");

        } catch (JRException e) {
            e.printStackTrace();
            Logger.error("Error",e);
        }
    }


    public static void exportPL() throws IOException {


        InputStream is = Reports.getControllerClass().getClassLoader().getResourceAsStream("reports/PL.jasper");

        Pagination pagination = new Pagination();
        pagination.all = true;
        pagination.currentPage=1;

        Map searchs = new HashMap();
        if(session.get("reportPlSearch")!=null)
            searchs = new ObjectMapper().readValue(session.get("reportPlSearch"), Map.class);
        else {
            String outlet ="%";
            searchs.put("shopName",outlet);
            String dateFrom="2000-01-01";
            searchs.put("dateFrom",dateFrom);
            String dateTo = "2222-01-01";
            searchs.put("dateTo",dateTo);
        }

        if(session.get(Constants.CURRENT_USERTYPE).equals("OPERATOR"))
            searchs.put("shopName",session.get("shopName"));

        pagination = ReportPL.search((Map) searchs, pagination);
        JRDataSource dataSource = new JRBeanCollectionDataSource(pagination.recordList);
        JasperPrint print = null;
        try {
            Map parameters = new HashMap();
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
            print = JasperFillManager.fillReport(is, parameters, dataSource);

            exportXls(print,"PL.xls");

        } catch (JRException e) {
            e.printStackTrace();
            Logger.error("Error",e);
        }
    }

    public static void exportTransactionDetail() throws IOException {


        InputStream is = Reports.getControllerClass().getClassLoader().getResourceAsStream("reports/TransactionDetail.jasper");

        Pagination pagination = new Pagination();
        pagination.all = true;
        pagination.currentPage=1;

        Map searchs = new HashMap();
        if(session.get("reportTransactionSearchs")!=null)
            searchs = new ObjectMapper().readValue(session.get("reportTransactionSearchs"), Map.class);
        else {
            String food="%";
            searchs.put("food",food);
            String outlet ="%";
            searchs.put("shopName",outlet);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateFrom=sdf.format(new Date());
            searchs.put("dateFrom",dateFrom+" 00:00:00");
            String dateTo = sdf.format(new Date());
            searchs.put("dateTo",dateTo+" 23:59:59");
        }

        if(session.get(Constants.CURRENT_USERTYPE).equals("OPERATOR"))
            searchs.put("shopName",session.get("shopName"));

        pagination = ReportTransactionDetail.search((Map) searchs, pagination);
        JRDataSource dataSource = new JRBeanCollectionDataSource(pagination.recordList);
        JasperPrint print = null;
        try {

            Map parameters = new HashMap();
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
            print = JasperFillManager.fillReport(is, parameters, dataSource);

            exportXls(print,"TransactionDetail.xls");

        } catch (JRException e) {
            e.printStackTrace();
            Logger.error("Error",e);
        }
    }

    public static void exportTransactionSummary() throws IOException {

        InputStream is = Reports.getControllerClass().getClassLoader().getResourceAsStream("reports/TransactionSummary.jasper");

        Pagination pagination = new Pagination();
        pagination.all = true;
        pagination.currentPage=1;

        Map searchs = new HashMap();
        if(session.get("reportTransactionSearchs")!=null)
            searchs = new ObjectMapper().readValue(session.get("reportTransactionSearchs"), Map.class);
        else {
            String food="%";
            searchs.put("food",food);
            String outlet ="%";
            searchs.put("shopName",outlet);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateFrom=sdf.format(new Date());
            searchs.put("dateFrom",dateFrom+" 00:00:00");
            String dateTo = sdf.format(new Date());
            searchs.put("dateTo",dateTo+" 23:59:59");
        }

        if(session.get(Constants.CURRENT_USERTYPE).equals("OPERATOR"))
            searchs.put("shopName",session.get("shopName"));

        pagination = ReportTransactionSummary.search(searchs, pagination);
        JRDataSource dataSource = new JRBeanCollectionDataSource(pagination.recordList);
        JasperPrint print = null;
        try {
            Map parameters = new HashMap();
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
            print = JasperFillManager.fillReport(is, parameters, dataSource);

            exportXls(print,"TransactionSummary.xls");

        } catch (JRException e) {
            e.printStackTrace();
            Logger.error("Error",e);
        }
    }

    private static void exportXls(JasperPrint print,String fileName) throws JRException {
        ByteArrayOutputStream outputByteArray = new ByteArrayOutputStream();

        JRXlsExporter exporterXLS = new JRXlsExporter();
        exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, print);
        exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, response.out);
        exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
        exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
        exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);

        exporterXLS.exportReport();
        response.setHeader("Content-Type","application/vnd.ms-excel");
        response.setHeader("Content-Disposition"," attachment; filename="+fileName);
        response.setHeader("Pragma" ,"no-cache");
        response.setHeader("Expires","0");
    }
}

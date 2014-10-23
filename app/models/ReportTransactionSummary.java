package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import utils.Pagination;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.PagingList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.annotation.Sql;

/**
 * Created with IntelliJ IDEA.
 * User: lala
 * Date: 11/14/13
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Sql
public class ReportTransactionSummary implements Comparable<ReportTransactionSummary> {

    @Transient
    public Long no;

    @Transient
    public String item;
    public String foodName,foodNameZh;
    public String shopName;
    public Long totalQuantity;
    public Double totalPrice;

    /* the following are service methods */
    public static Pagination search(Map search, Pagination pagination) {
        pagination = pagination == null ? new Pagination() : pagination;

        Query query  = Ebean.find(ReportTransactionDetail.class).orderBy("shopName");
        ExpressionList expList = query.where();

        if (search.keySet()!=null) {
            Iterator searchKeys = search.keySet().iterator();
            while(searchKeys.hasNext()){
                String key = (String) searchKeys.next();
                String value = (String) search.get(key);
                play.Logger.info("Value " + value);
                if(StringUtils.isEmpty(value)) continue;

                if(key.equalsIgnoreCase("food")) {

                    expList.where().or(
                            Expr.like("foodName", "%" + value + "%"),
                            Expr.like("foodNameZh", "%" + value + "%")
                    );
                }
                else if(key.equalsIgnoreCase("shopName")){
                    expList.where().ilike(key, "%" + value+ "%");
                }
                else if(key.equalsIgnoreCase("dateFrom")){
                    expList.where().ge("orderDate",  value);
                }
                else if(key.equalsIgnoreCase("dateTo")){
                    expList.where().le("orderDate", value);
                }
            }
        }

        PagingList<ReportTransactionDetail> pagingList = expList.findPagingList(pagination.pageSize);
        pagingList.setFetchAhead(false);
        List<ReportTransactionDetail> tmpList = expList.findList();
        ArrayList<ReportTransactionSummary> list = new ArrayList<ReportTransactionSummary>();

        if(tmpList!=null) {
            for(ReportTransactionDetail report:tmpList) {
                if(pagination.zh)
                    report.item = report.foodNameZh;
                else
                    report.item = report.foodName;
            }

            Map<String,ReportTransactionSummary> summaryMap = new LinkedHashMap<String, ReportTransactionSummary>(  );
            for(ReportTransactionDetail report:tmpList) {
                String shop = report.shopName;
                String item = report.item;
                ReportTransactionSummary reportTransactionSummary = summaryMap.get(shop+item);
                if(summaryMap.get(shop+item)==null){
                    reportTransactionSummary = new ReportTransactionSummary();
                    reportTransactionSummary.shopName=shop;
                    reportTransactionSummary.item=item;
                    if(report.quantity==null) report.quantity=0l;
                    if(report.totalRetailPrice==null) report.totalRetailPrice=0.0;
                    reportTransactionSummary.totalQuantity=report.quantity;
                    reportTransactionSummary.totalPrice=report.totalRetailPrice;
                    summaryMap.put(shop+item,reportTransactionSummary);
                }
                else {

                    if(report.quantity==null) report.quantity=0l;
                    if(report.totalRetailPrice==null) report.totalRetailPrice=0.0;

                    reportTransactionSummary.totalQuantity+=report.quantity;
                    reportTransactionSummary.totalPrice+=report.totalRetailPrice;
                }
            }

            Collection<ReportTransactionSummary> tmp2List = (Collection) summaryMap.values();


            if(tmp2List!=null) {
                Long no = 1l;
                int startIndex =  ((pagination.currentPage-1)*pagination.pageSize);
                for(ReportTransactionSummary report:tmp2List) {
                    report.no = no;
                    no++;
                }

                list.addAll(tmp2List);

                int endIndex = (startIndex+pagination.pageSize);
                if(pagination.all) {
                    endIndex = tmp2List.size();
                }
                if(endIndex>=tmp2List.size())
                    endIndex=tmp2List.size();

                play.Logger.info("start "+startIndex+" end "+endIndex);
                list = new ArrayList<ReportTransactionSummary>(list.subList(startIndex,endIndex));
                pagination.iTotalDisplayRecords = tmp2List.size();
                pagination.iTotalRecords = tmp2List.size();
            }
            else {

                pagination.iTotalDisplayRecords = 0;
                pagination.iTotalRecords = 0;
            }




            pagination.recordList = list;

        }


        return pagination;
    }
    
    
    public static List<ReportQuantity> pieChartQuantity(Map search) {
    	String sql ="SELECT id, shop_name, order_date, food_name, food_name_zh, quantity FROM (" +  
    				"SELECT " + 
	    			 	"id, shop_name," + 
					    "DATE_FORMAT(order_date, '%Y-%m-%d') as order_date," + 
					    "food_name," + 
					    "food_name_zh," + 
					    "COUNT(quantity) as quantity " + 
					"FROM" + 
					   " report_transaction_detail " + 
					"GROUP BY" + 
					   " id, shop_name, food_name, food_name_zh, " + 
					   " DATE_FORMAT(order_date, '%Y-%m-%d')" +
					") a";
    	
    	RawSql rawSql =   
    		    RawSqlBuilder  
    		        .parse(sql)  
    		        // map resultSet columns to bean properties  
    		        .columnMapping("id",  "id")  
    		        .columnMapping("shop_name",  "shopName")  
    		        .columnMapping("food_name",      "label")  
    		        .columnMapping("food_name_zh",    "foodNameZh")  
    		        .columnMapping("order_date",    "orderDate")  
    		        .columnMapping("quantity",    "value") 
    		        .create();  

    	Query<ReportQuantity> query = Ebean.find(ReportQuantity.class);  
        query.setRawSql(rawSql);          

        if (search.keySet()!=null) {
            Iterator searchKeys = search.keySet().iterator();
            while(searchKeys.hasNext()){
                String key = (String) searchKeys.next();
                String value = (String) search.get(key);
                play.Logger.info("Value " + value);
                if(StringUtils.isEmpty(value)) continue;
               
                else if(key.equalsIgnoreCase("shopName")){
                	query.where().eq("shopName", value);  
                }
                else if(key.equalsIgnoreCase("dateFrom")){
                	query.where().ge("orderDate",  value);
                }
                else if(key.equalsIgnoreCase("dateTo")){
                	query.where().le("orderDate", value);
                }
            }
        }

        return query.findList();
    }
    
    public static List<ReportMoney> pieChartMoney(Map search) {
    	String sql ="SELECT id, shop_name, order_date, food_name, food_name_zh, value FROM (" +  
    				"SELECT " + 
	    			 	"id, shop_name," + 
					    "DATE_FORMAT(order_date, '%Y-%m-%d') as order_date," + 
					    "food_name," + 
					    "food_name_zh," + 
					    "sum(total_retail_price) as value " + 
					"FROM" + 
					   " report_transaction_detail " + 
					"GROUP BY" + 
					   " id, shop_name, food_name, food_name_zh, " + 
					   " DATE_FORMAT(order_date, '%Y-%m-%d')" +
					") a";
    	
    	RawSql rawSql =   
    		    RawSqlBuilder  
    		        .parse(sql)  
    		        // map resultSet columns to bean properties  
    		        .columnMapping("id",  "id")  
    		        .columnMapping("shop_name",  "shopName")  
    		        .columnMapping("food_name",      "label")  
    		        .columnMapping("food_name_zh",    "foodNameZh")  
    		        .columnMapping("order_date",    "orderDate")  
    		        .columnMapping("value",    "value") 
    		        .create();  

    	Query<ReportMoney> query = Ebean.find(ReportMoney.class);  
        query.setRawSql(rawSql);          

        if (search.keySet()!=null) {
            Iterator searchKeys = search.keySet().iterator();
            while(searchKeys.hasNext()){
                String key = (String) searchKeys.next();
                String value = (String) search.get(key);
                play.Logger.info("Value " + value);
                if(StringUtils.isEmpty(value)) continue;
               
                else if(key.equalsIgnoreCase("shopName")){
                	query.where().eq("shopName", value);  
                }
                else if(key.equalsIgnoreCase("dateFrom")){
                	query.where().ge("orderDate",  value);
                }
                else if(key.equalsIgnoreCase("dateTo")){
                	query.where().le("orderDate", value);
                }
            }
        }

        return query.findList();
    }
    
    public static List<ReportMoney> lineChartMoney(Map search) {
    	String sql ="SELECT id, shop_name, order_date, order_hour, value FROM (" +  
    				"SELECT " + 
	    			 	"id, shop_name," + 
					    "DATE_FORMAT(order_date, '%Y-%m-%d') as order_date," + 
	    			 	"DATE_FORMAT(order_date, '%H') AS order_hour," +
					    "sum(total_retail_price) as value " + 
					"FROM" + 
					   " report_transaction_detail " + 
					"GROUP BY" + 
					   " id, shop_name, " + 
					   " DATE_FORMAT(order_date, '%Y-%m-%d %H')" +
					") a order by order_hour asc";
    	
    	RawSql rawSql =   
    		    RawSqlBuilder  
    		        .parse(sql)  
    		        // map resultSet columns to bean properties  
    		        .columnMapping("id",  "id")  
    		        .columnMapping("shop_name",  "shopName")
    		        .columnMapping("order_date",    "orderDate")  
    		        .columnMapping("order_hour",    "orderHour")  
    		        .columnMapping("value",    "value") 
    		        .create();  

    	Query<ReportMoney> query = Ebean.find(ReportMoney.class);  
        query.setRawSql(rawSql);          

        if (search.keySet()!=null) {
            Iterator searchKeys = search.keySet().iterator();
            while(searchKeys.hasNext()){
                String key = (String) searchKeys.next();
                String value = (String) search.get(key);
                play.Logger.info("Value " + value);
                if(StringUtils.isEmpty(value)) continue;
               
                else if(key.equalsIgnoreCase("shopName")){
                	query.where().eq("shopName", value);  
                }
                else if(key.equalsIgnoreCase("dateFrom")){
                	query.where().ge("orderDate",  value);
                }
                else if(key.equalsIgnoreCase("dateTo")){
                	query.where().le("orderDate", value);
                }
            }
        }

        return query.findList();
    }
    

    public Long getNo() {
        return no;
    }

    public String getItem() {
        return item;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodNameZh() {
        return foodNameZh;
    }

    public String getShopName() {
        return shopName;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    @Override
    public int compareTo(ReportTransactionSummary o) {
        if(o==null) return 0;
        if(foodName==null || shopName==null) return 0;
        ReportTransactionSummary reportTransactionSummary = (ReportTransactionSummary)o;

        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

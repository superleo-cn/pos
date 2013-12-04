package models;

import com.avaje.ebean.*;
import com.avaje.ebean.annotation.Sql;
import org.apache.commons.lang.StringUtils;
import utils.Pagination;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: lala
 * Date: 11/14/13
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Sql
public class ReportTransactionSummary {

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

        Query query  = Ebean.find(ReportTransactionDetail.class);
        ExpressionList expList = query.where();
        query.findList();
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

            Map<String,ReportTransactionSummary> summaryMap = new HashMap();
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

                int endIndex = (startIndex+10);
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
}

package models;

import com.avaje.ebean.*;
import com.avaje.ebean.annotation.Sql;
import org.apache.commons.lang.StringUtils;
import utils.Pagination;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: lala
 * Date: 11/15/13
 * Time: 1:55 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class ReportCashierClosing {

    @Transient
    public Long no;

    public Date createDate;

    public String shopName,realName;

    public Double openBalance,expenses,cashInDrawer,nextOpenBalance;

    public Double dailyTurnover,totalCollection,total;


    /* the following are service methods */
    public static Pagination search(Map search, Pagination pagination) {
        pagination = pagination == null ? new Pagination() : pagination;

        Query query  = Ebean.find(ReportCashierClosing.class);
        ExpressionList expList = query.where();
        if (search.keySet()!=null) {
            Iterator searchKeys = search.keySet().iterator();
            while(searchKeys.hasNext()){
                String key = (String) searchKeys.next();
               String value = (String) search.get(key);
                play.Logger.info("Key " + key+" Value " + value);
                if(StringUtils.isEmpty(value)) continue;

                if(key.equalsIgnoreCase("shopName") || key.equalsIgnoreCase("realName")){
                    expList.where().ilike(key, "%" + value + "%");
                }
                else if(key.equalsIgnoreCase("dateFrom")){
                    expList.where().ge("createDate", value+" 00:00:00");
                }
                else if(key.equalsIgnoreCase("dateTo")){
                    expList.where().le("createDate", value+" 23:59:59");
                }
            }
        }
        List<ReportCashierClosing> list = new ArrayList<ReportCashierClosing>();
        if(!pagination.all)
        {
        PagingList<ReportCashierClosing> pagingList = expList.findPagingList(pagination.pageSize);
        pagingList.setFetchAhead(false);
        Page page = pagingList.getPage(pagination.currentPage-1);

        list = page.getList();


        pagination.iTotalDisplayRecords = expList.findRowCount();
        pagination.iTotalRecords = expList.findRowCount();

        }
        else {
            play.Logger.info("ALLLL ");
            pagination.currentPage = 1;
            list = expList.findList();
        }

        if(list!=null) {
            Long no = ((pagination.currentPage-1)*pagination.pageSize)+1l;
            for(ReportCashierClosing report:list) {
                report.no = no;
                if(report.dailyTurnover==null) report.dailyTurnover=0.0;

                no++;
            }
        }

        pagination.recordList = list;
        return pagination;
    }

    public Long getNo() {
        return no;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getShopName() {
        return shopName;
    }

    public String getRealName() {
        return realName;
    }

    public Double getOpenBalance() {
        return openBalance;
    }

    public Double getExpenses() {
        return expenses;
    }

    public Double getDailyTurnover() {
        if(dailyTurnover==null) return 0.0;
        return dailyTurnover;
    }

    public Double getCashInDrawer() {
        return cashInDrawer;
    }

    public Double getTotalCollection() {
        return totalCollection;
    }

    public Double getTotal() {
        return total;
    }
}

package models;

import com.avaje.ebean.*;
import com.avaje.ebean.annotation.Sql;
import org.apache.commons.lang.StringUtils;
import utils.Pagination;

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
@Sql
public class ReportCashierClosing {

    @Transient
    public Long no;
    public Date createDate;
    public String foodName,foodNameZh,shopName;

    @Transient
    public String item;

    public Double openBalance,expenses,cashCollected,dailyturnover,nextOpenBalance,bringCashBack;


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
                    expList.where().ge("createDate",  value);
                }
                else if(key.equalsIgnoreCase("dateTo")){
                    expList.where().le("createDate",  value);
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
            pagination.currentPage = 1;
            list = expList.findList();
        }

        if(list!=null) {
            Long no = ((pagination.currentPage-1)*10)+1l;
            for(ReportCashierClosing report:list) {
                report.no = no;
                if(pagination.zh)
                    report.item = report.foodNameZh;
                else
                    report.item = report.foodName;

                no++;
            }
        }

        pagination.recordList = list;
        return pagination;
    }
}

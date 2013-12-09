package models;

import com.avaje.ebean.*;
import org.apache.commons.lang.StringUtils;
import utils.Pagination;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: lala
 * Date: 12/7/13
 * Time: 12:10 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class ReportExpensesDetails {

    @Transient
    public Long no;

    public Date createDate;

    public String shopName,realName,foodName;

    public Double price;

    public static Pagination search(Map search, Pagination pagination) {
        pagination = pagination == null ? new Pagination() : pagination;

        Query query  = Ebean.find(ReportExpensesDetails.class);
        ExpressionList expList = query.where();
        if (search.keySet()!=null) {
            Iterator searchKeys = search.keySet().iterator();
            while(searchKeys.hasNext()){
                String key = (String) searchKeys.next();
                String value = (String) search.get(key);
                play.Logger.info("Value " + value);
                if(StringUtils.isEmpty(value)) continue;

                if(key.equalsIgnoreCase("shopName") || key.equalsIgnoreCase("realName")){
                    expList.where().ilike(key, "%" + value + "%");
                }
                else if(key.equalsIgnoreCase("dateFrom")){
                    expList.where().ge("createDate", value);
                }
                else if(key.equalsIgnoreCase("dateTo")){
                    expList.where().le("createDate", value);
                }
            }
        }
        List<ReportExpensesDetails> list = new ArrayList<ReportExpensesDetails>();
        if(!pagination.all)
        {
            PagingList<ReportExpensesDetails> pagingList = expList.findPagingList(pagination.pageSize);
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
            Long no = ((pagination.currentPage-1)*pagination.pageSize)+1l;
            for(ReportExpensesDetails report:list) {
                report.no = no;
                //if(report.dailyTurnover==null) report.dailyTurnover=0.0;

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

    public String getFoodName() {
        return foodName;
    }

    public Double getPrice() {
        return price;
    }
}

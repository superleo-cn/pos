package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import org.apache.commons.lang.StringUtils;
import utils.Pagination;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: lala
 * Date: 11/14/13
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class ReportTransaction {

    public Integer serialNo;
    public String item;
    public String shop;
    public Integer quantity;
    public Double totalPrice;

    public Date transactionDate;
    public Double retailPrice;
    public Double totalDiscount;
    public Double totalPack;
    public String foc;



    /* the following are service methods */
    public static Pagination search(String queryName, Pagination pagination) {
        pagination = pagination == null ? new Pagination() : pagination;
      /*  ExpressionList expList = Ebean.find(Food.class).where();
        if (StringUtils.isNotEmpty(queryName)) {
            queryName = StringUtils.trimToNull(queryName);
            expList.where().ilike("name", "%" + queryName + "%");
        }
        PagingList<Food> pagingList = expList.findPagingList(pagination.pageSize);
        pagingList.setFetchAhead(false);
        Page page = pagingList.getPage(pagination.currentPage);*/

        ReportTransaction reportTransaction = new ReportTransaction();

        pagination.recordList = new ArrayList();
        pagination.pageCount = 5;
        pagination.recordCount = 50;
        return pagination;
    }
}

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
 * Date: 11/15/13
 * Time: 1:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReportPL {


    public Long no;

    public String item;

    public String shopName;

    public Double amount;


    /* the following are service methods */
    public static Pagination search(Map search, Pagination pagination) {
        pagination = pagination == null ? new Pagination() : pagination;

        pagination = pagination == null ? new Pagination() : pagination;

        String sql =
                "SELECT " +
                        "    ts.name shopName, " +
                        "     SUM(c_cash_collected) AS cash_collected ,  " +
                        "    SUM(b_expenses)       AS expenses   " +
                        "    " +
                        "FROM " +
                        "    tb_daily_summary td,tb_shop ts " +
                        "WHERE " +
                        "    td.shop_id=ts.id and" +
                        "    td.create_date BETWEEN :dateFrom AND :dateTo  and ts.name like :shopName  " +
                        "GROUP BY" +
                        "    ts.name";
        SqlQuery query  = Ebean.createSqlQuery(sql);

        if (search.keySet()!=null) {
            Iterator searchKeys = search.keySet().iterator();
            while(searchKeys.hasNext()){
                String key = (String) searchKeys.next();
                String value = (String) search.get(key);
                play.Logger.info("value " + value);
                if(StringUtils.isEmpty(value)) continue;

                if(key.equalsIgnoreCase("dateFrom") || key.equalsIgnoreCase("dateTo") ) {
                    query.setParameter(key,value);
                }
                else if(key.equalsIgnoreCase("shopName")) {
                    query.setParameter(key,"%"+value+"%");
                }
            }
        }



        List<SqlRow> tmpList = query.findList();
        ArrayList<ReportPL> list = new ArrayList<ReportPL>();

        play.Logger.info("current page"+pagination.currentPage);
        int startIndex =  ((pagination.currentPage-1)*10);

        if(tmpList!=null) {
            Long no = 1l;

            for(SqlRow report:tmpList) {
                ReportPL reportPL = new ReportPL();

                reportPL.no = no;
                reportPL.shopName= (String) report.get("shopName");
                reportPL.item = "A. Sales";
                reportPL.amount        = (Double) report.get("cash_collected");
                no++;
                list.add(reportPL);

                reportPL = new ReportPL();
                reportPL.no = no;
                reportPL.shopName= (String) report.get("shopName");
                reportPL.item = "B. Cost of Sales";
                reportPL.amount        = 0.0;
                no++;
                list.add(reportPL);

                reportPL = new ReportPL();
                reportPL.no = no;
                reportPL.shopName= (String) report.get("shopName");
                reportPL.item = "C. Expenses";
                reportPL.amount        = (Double) report.get("expenses");
                no++;
                list.add(reportPL);
            }
        }


        sql =
                "SELECT " +
                        "    ts.name shopName, " +
                        "     SUM(total_cost_price) AS total_cost_price " +
                        "FROM " +
                        "    tb_transaction tt,tb_shop ts " +
                        "WHERE " +
                        "    tt.shop_id=ts.id and" +
                        "    tt.create_date BETWEEN :dateFrom AND :dateTo and ts.name like :shopName   " +
                        "GROUP BY" +
                        "    ts.name";
        query  = Ebean.createSqlQuery(sql);

        if (search.keySet()!=null) {
            Iterator searchKeys = search.keySet().iterator();
            while(searchKeys.hasNext()){
                String key = (String) searchKeys.next();
                String value = (String) search.get(key);
                play.Logger.info("Valuex " + value);
                if(StringUtils.isEmpty(value)) continue;

                if(key.equalsIgnoreCase("dateFrom") || key.equalsIgnoreCase("dateTo") ) {
                    query.setParameter(key,value);
                } else if(key.equalsIgnoreCase("shopName")) {
                    query.setParameter(key,"%"+value+"%");
                }

            }
        }
        tmpList = query.findList();

        play.Logger.info(" size "+tmpList.size());
        if(tmpList!=null) {
            for(SqlRow report:tmpList) {
                for(ReportPL exising:list ){
                    if(exising.shopName.equalsIgnoreCase((String)report.get("shopName")) && exising.item.equalsIgnoreCase("B. Cost of Sales")) {
                        exising.amount = (Double) report.get("total_cost_price");
                        break;
                    }
                }
            }
        }


        pagination.iTotalDisplayRecords = list.size();
        pagination.iTotalRecords = list.size();

        int endIndex = (startIndex+10);
        if(endIndex>=list.size())
            endIndex=list.size();

        play.Logger.info("start "+startIndex+" end "+endIndex);
        list = new ArrayList<ReportPL>(list.subList(startIndex,endIndex));

        pagination.recordList = list;
        return pagination;
    }
}

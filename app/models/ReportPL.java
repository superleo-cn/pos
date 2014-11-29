package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.Pagination;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;

public class ReportPL {

	final static Logger logger = LoggerFactory.getLogger(ReportPL.class);

	public Long no;

	public String item;

	public String shopName;

	public Double sales, costOfSales, expenses, netProfit;

	/* the following are service methods */
	public static Pagination search(Map search, Pagination pagination) {
		pagination = pagination == null ? new Pagination() : pagination;
		String sql = "SELECT ts.name shopName, SUM(c_cash_collected-e_next_open_balance) AS sales, SUM(b_expenses) AS expenses FROM tb_daily_summary td,tb_shop ts WHERE  td.create_date BETWEEN :dateFrom AND :dateTo  and ts.name like :shopName  "
				+ "GROUP BY ts.name";
		SqlQuery query = Ebean.createSqlQuery(sql);

		if (search.keySet() != null) {
			Iterator searchKeys = search.keySet().iterator();
			while (searchKeys.hasNext()) {
				String key = (String) searchKeys.next();
				String value = (String) search.get(key);
				logger.info("value " + value);
				if (StringUtils.isEmpty(value)) {
					continue;
				}

				if (key.equalsIgnoreCase("dateFrom")) {
					query.setParameter(key, value + " 00:00:00");
				} else if (key.equalsIgnoreCase("dateTo")) {
					query.setParameter(key, value + " 23:59:59");
				} else if (key.equalsIgnoreCase("shopName")) {
					query.setParameter(key, "%" + value + "%");
				}
			}
		}

		List<SqlRow> tmpList = query.findList();
		ArrayList<ReportPL> list = new ArrayList<ReportPL>();

		logger.info("current page" + pagination.currentPage);
		int startIndex = ((pagination.currentPage - 1) * pagination.pageSize);

		if (tmpList != null) {
			Long no = 1l;
			for (SqlRow report : tmpList) {
				ReportPL reportPL = new ReportPL();
				reportPL.no = no;
				reportPL.shopName = (String) report.get("shopName");
				reportPL.sales = (Double) report.get("sales");
				reportPL.costOfSales = 0.0;
				reportPL.expenses = (Double) report.get("expenses");
				reportPL.netProfit = reportPL.sales - reportPL.expenses;
				list.add(reportPL);
				no++;
			}
		}

		sql = "SELECT ts.name shopName, SUM(total_cost_price) AS total_cost_price FROM tb_transaction tt,tb_shop ts WHERE tt.shop_id=ts.id and tt.create_date BETWEEN :dateFrom AND :dateTo and ts.name like :shopName   "
				+ "GROUP BY ts.name";
		query = Ebean.createSqlQuery(sql);

		if (search.keySet() != null) {
			Iterator searchKeys = search.keySet().iterator();
			while (searchKeys.hasNext()) {
				String key = (String) searchKeys.next();
				String value = (String) search.get(key);

				logger.info("Key " + key + " Value " + value);
				if (StringUtils.isEmpty(value)) {
					continue;
				}

				if (key.equalsIgnoreCase("dateFrom")) {
					query.setParameter(key, value + " 00:00:00");
				} else if (key.equalsIgnoreCase("dateTo")) {
					query.setParameter(key, value + " 23:59:59");
				} else if (key.equalsIgnoreCase("shopName")) {
					query.setParameter(key, "%" + value + "%");
				}

			}
		}
		tmpList = query.findList();

		logger.info(" size " + tmpList.size());
		if (tmpList != null) {
			for (SqlRow report : tmpList) {
				for (ReportPL exising : list) {
					if (exising.shopName.equalsIgnoreCase((String) report.get("shopName"))) {
						exising.costOfSales = (Double) report.get("total_cost_price");
						exising.netProfit = exising.netProfit - exising.costOfSales;
					}
				}
			}
		}

		pagination.iTotalDisplayRecords = list.size();
		pagination.iTotalRecords = list.size();

		int endIndex = (startIndex + pagination.pageSize);
		if (endIndex >= list.size()) {
			endIndex = list.size();
		}

		logger.info("start " + startIndex + " end " + endIndex);
		list = new ArrayList<ReportPL>(list.subList(startIndex, endIndex));

		pagination.recordList = list;
		return pagination;
	}

	public Long getNo() {
		return no;
	}

	public String getItem() {
		return item;
	}

	public String getShopName() {
		return shopName;
	}

	public Double getSales() {
		return sales;
	}

	public void setSales(Double sales) {
		this.sales = sales;
	}

	public Double getCostOfSales() {
		return costOfSales;
	}

	public void setCostOfSales(Double costOfSales) {
		this.costOfSales = costOfSales;
	}

	public Double getExpenses() {
		return expenses;
	}

	public void setExpenses(Double expenses) {
		this.expenses = expenses;
	}

	public Double getNetProfit() {
		return netProfit;
	}

	public void setNetProfit(Double netProfit) {
		this.netProfit = netProfit;
	}
}

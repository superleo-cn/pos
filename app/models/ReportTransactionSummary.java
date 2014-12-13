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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.Pagination;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.PagingList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.annotation.Sql;

@Entity
@Sql
public class ReportTransactionSummary implements Comparable<ReportTransactionSummary> {

	final static Logger logger = LoggerFactory.getLogger(ReportTransactionSummary.class);

	@Transient
	public Long no;

	@Transient
	public String id, item, itemCategory;
	public String foodName, foodNameZh;
	public String shopName;
	public Long totalQuantity;
	public Double gst, sc, retailPrice, totalPrice;

	/* the following are service methods */
	public static Pagination search(Map search, Pagination pagination) {
		pagination = pagination == null ? new Pagination() : pagination;

		Query query = Ebean.find(ReportTransactionDetail.class).orderBy("shopName");
		ExpressionList expList = query.where();

		if (search.keySet() != null) {
			Iterator searchKeys = search.keySet().iterator();
			while (searchKeys.hasNext()) {
				String key = (String) searchKeys.next();
				String value = (String) search.get(key);
				logger.info("Value " + value);
				if (StringUtils.isEmpty(value)) {
					continue;
				}
				if (key.equalsIgnoreCase("food")) {

					expList.where().or(Expr.like("foodName", "%" + value + "%"), Expr.like("foodNameZh", "%" + value + "%"));
				} else if (key.equalsIgnoreCase("shopName")) {
					expList.where().ilike(key, "%" + value + "%");
				} else if (key.equalsIgnoreCase("dateFrom")) {
					expList.where().ge("orderDate", value);
				} else if (key.equalsIgnoreCase("dateTo")) {
					expList.where().le("orderDate", value);
				}
			}
		}
		expList.orderBy("position asc");

		PagingList<ReportTransactionDetail> pagingList = expList.findPagingList(pagination.pageSize);
		pagingList.setFetchAhead(false);
		List<ReportTransactionDetail> tmpList = expList.findList();
		ArrayList<ReportTransactionSummary> list = new ArrayList<ReportTransactionSummary>();

		if (tmpList != null) {
			for (ReportTransactionDetail report : tmpList) {
				if (pagination.zh) {
					report.item = report.foodNameZh;
					report.itemCategory = report.categoryNameZh.replaceAll("\\<.*?>","");
				} else {
					report.item = report.foodName;
					report.itemCategory = report.categoryName.replaceAll("\\<.*?>","");
				}
			}

			Map<String, ReportTransactionSummary> summaryMap = new LinkedHashMap<String, ReportTransactionSummary>();
			for (ReportTransactionDetail report : tmpList) {
				String shop = report.shopName;
				String item = report.item;
				String itemCategory = report.itemCategory;
				ReportTransactionSummary reportTransactionSummary = summaryMap.get(shop + item);
				if (summaryMap.get(shop + item) == null) {
					reportTransactionSummary = new ReportTransactionSummary();
					reportTransactionSummary.shopName = shop;
					reportTransactionSummary.item = item;
					reportTransactionSummary.itemCategory = itemCategory;
					if (report.quantity == null) {
						report.quantity = 0l;
					}
					if (report.totalRetailPrice == null) {
						report.totalRetailPrice = 0.0;
					}
					reportTransactionSummary.totalQuantity = report.quantity;
					reportTransactionSummary.totalPrice = report.totalRetailPrice;
					summaryMap.put(shop + item, reportTransactionSummary);
				} else {
					if (report.quantity == null) {
						report.quantity = 0l;
					}
					if (report.totalRetailPrice == null) {
						report.totalRetailPrice = 0.0;
					}
					reportTransactionSummary.totalQuantity += report.quantity;
					reportTransactionSummary.totalPrice += report.totalRetailPrice;
				}
			}

			Collection<ReportTransactionSummary> tmp2List = (Collection) summaryMap.values();
			if (tmp2List != null) {
				Long no = 1l;
				int startIndex = ((pagination.currentPage - 1) * pagination.pageSize);
				for (ReportTransactionSummary report : tmp2List) {
					report.no = no;
					no++;
				}

				list.addAll(tmp2List);

				int endIndex = (startIndex + pagination.pageSize);
				if (pagination.all) {
					endIndex = tmp2List.size();
				}
				if (endIndex >= tmp2List.size()) {
					endIndex = tmp2List.size();
				}

				logger.info("start " + startIndex + " end " + endIndex);
				list = new ArrayList<ReportTransactionSummary>(list.subList(startIndex, endIndex));
				pagination.iTotalDisplayRecords = tmp2List.size();
				pagination.iTotalRecords = tmp2List.size();
			} else {
				pagination.iTotalDisplayRecords = 0;
				pagination.iTotalRecords = 0;
			}
			pagination.recordList = list;
		}
		return pagination;
	}

	public static Pagination search(Map search, Shop shop) {
		String sql = 
					"select id, shop_name, quantity, retail_price, gst, sc, total_price from (" +
					"SELECT id, " +
					    "shop_name, " +
					   " quantity,  " +
					   " retail_price, " +
					   " gst, " +
					   " sc, " +
					   " retail_price+gst+sc AS total_price " +
					"FROM " +
					   " ( " +
					   "     SELECT id, " +
					    "        shop_name, " +
					    "        SUM(quantity)                  AS quantity, " +
					    "        SUM(total_retail_price)        AS retail_price, " +
					    "        SUM(total_retail_price) * " + (Integer.parseInt(shop.gstRate) / 100.0) + " AS gst, " +
					    "        SUM(total_retail_price) * " + (Integer.parseInt(shop.serviceRate) / 100.0) + " AS sc " +
					    "    FROM " +
					    "        report_transaction_detail " +
					    "    WHERE " +
					    "        order_date >= :dateFrom and order_date <= :dateTo " +
					"        GROUP BY " +
					"            shop_name ) b" + 
					")a";

		RawSql rawSql = RawSqlBuilder.parse(sql)
				// map resultSet columns to bean properties
				.columnMapping("id", "id").columnMapping("shop_name", "shopName").columnMapping("quantity", "totalQuantity").columnMapping("gst", "gst")
				.columnMapping("sc", "sc").columnMapping("retail_price", "retailPrice").columnMapping("total_price", "totalPrice").create();

		Query<ReportTransactionSummary> query = Ebean.find(ReportTransactionSummary.class);
		query.setRawSql(rawSql);
		


		if (search.keySet() != null) {
			Iterator searchKeys = search.keySet().iterator();
			while (searchKeys.hasNext()) {
				String key = (String) searchKeys.next();
				String value = (String) search.get(key);
				logger.info("Value " + value);
				if (StringUtils.isEmpty(value)) {
					continue;
				}
				if (key.equalsIgnoreCase("shopName")) {
					query.where().ilike(key, "%" + value + "%");
				} else if (key.equalsIgnoreCase("dateFrom")) {
					query.setParameter("dateFrom", value);
				} else if (key.equalsIgnoreCase("dateTo")) {
					query.setParameter("dateTo", value);
				}
			}
		}

		List <ReportTransactionSummary> list = query.findList();
		Pagination pagination = new Pagination();
		if(list != null && list.size() > 0){
			ReportTransactionSummary obj = list.get(0);
			obj.no = 1L;
			pagination.recordList = list;
			pagination.iTotalDisplayRecords = pagination.recordList.size();
			pagination.iTotalRecords = pagination.recordList.size();
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

	@Override
	public int compareTo(ReportTransactionSummary o) {
		if (o == null) {
			return 0;
		}
		if (foodName == null || shopName == null) {
			return 0;
		}
		return 0;
	}
}

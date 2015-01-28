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
	public String shopName, type;
	public Long totalQuantity;
	public Double gst, sc, retailPrice, totalPrice, card, cash;

	private static void setQuery(ExpressionList query, Map search) {
		if (search.keySet() != null) {
			Iterator searchKeys = search.keySet().iterator();
			while (searchKeys.hasNext()) {
				String key = (String) searchKeys.next();
				Object obj = search.get(key);
				String value = null;
				List<String> values = null;
				if (obj instanceof String) {
					value = (String) obj;
				} else {
					values = (List<String>) obj;
				}
				play.Logger.info("Value " + value);
				if (StringUtils.isEmpty(value) && values == null) {
					continue;
				} else if (key.equalsIgnoreCase("food")) {
					query.where().or(Expr.like("foodName", "%" + value + "%"), Expr.like("foodNameZh", "%" + value + "%"));
				} else if (key.equalsIgnoreCase("shopName")) {
					query.where().in("shopName", values);
				} else if (key.equalsIgnoreCase("dateFrom")) {
					query.where().ge("orderDate", value);
				} else if (key.equalsIgnoreCase("dateTo")) {
					query.where().le("orderDate", value);
				}

			}
		}
	}

	private static void setQuery2(Query query, Map search) {
		if (search.keySet() != null) {
			Iterator searchKeys = search.keySet().iterator();
			while (searchKeys.hasNext()) {
				String key = (String) searchKeys.next();
				if (key.equalsIgnoreCase("shopName")) {
					continue;
				}
				Object obj = search.get(key);
				String value = (String) obj;
				play.Logger.info("Value " + value);
				if (StringUtils.isEmpty(value)) {
					continue;
				} else if (key.equalsIgnoreCase("shopNameSummary")) {
					query.where().eq("shopName", value);
				} else if (key.equalsIgnoreCase("dateFrom")) {
					query.setParameter("dateFrom", value);
				} else if (key.equalsIgnoreCase("dateTo")) {
					query.setParameter("dateTo", value);
				}

			}
		}
	}

	/* the following are service methods */
	public static Pagination search(Map search, Pagination pagination) {
		pagination = pagination == null ? new Pagination() : pagination;

		Query query = Ebean.find(ReportTransactionDetail.class).orderBy("shopName");
		ExpressionList expList = query.where();

		setQuery(expList, search);

		PagingList<ReportTransactionDetail> pagingList = expList.findPagingList(pagination.pageSize);
		pagingList.setFetchAhead(false);
		List<ReportTransactionDetail> tmpList = expList.findList();
		ArrayList<ReportTransactionSummary> list = new ArrayList<ReportTransactionSummary>();

		if (tmpList != null) {
			for (ReportTransactionDetail report : tmpList) {
				if (pagination.zh) {
					report.item = report.foodNameZh;
					report.itemCategory = report.categoryNameZh.replaceAll("\\<.*?>", "");
				} else {
					report.item = report.foodName;
					report.itemCategory = report.categoryName.replaceAll("\\<.*?>", "");
				}
			}

			Map<String, ReportTransactionSummary> summaryMap = new LinkedHashMap<String, ReportTransactionSummary>();
			for (ReportTransactionDetail report : tmpList) {
				String shop = report.shopName;
				String item = report.item;
				String type = report.type;
				String itemCategory = report.itemCategory;
				ReportTransactionSummary reportTransactionSummary = summaryMap.get(shop + item + type);
				if (summaryMap.get(shop + item + type) == null) {
					reportTransactionSummary = new ReportTransactionSummary();
					reportTransactionSummary.shopName = shop;
					reportTransactionSummary.item = item;
					reportTransactionSummary.itemCategory = itemCategory;
					reportTransactionSummary.type = type;
					if (report.quantity == null) {
						report.quantity = 0l;
					}
					if (report.totalRetailPrice == null) {
						report.totalRetailPrice = 0.0;
					}
					reportTransactionSummary.totalQuantity = report.quantity;
					reportTransactionSummary.totalPrice = report.totalRetailPrice;
					summaryMap.put(shop + item + type, reportTransactionSummary);
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
		String sql = "select id, shop_name, quantity, retail_price, gst, sc, total_price from (" + "SELECT id, " + "shop_name, " + " quantity,  " + " retail_price, " + " gst, "
				+ " sc, " + " retail_price+gst+sc AS total_price " + "FROM " + " ( " + "     SELECT id, " + "        shop_name, "
				+ "        SUM(quantity)                  AS quantity, " + "        SUM(total_retail_price)        AS retail_price, " + "        SUM(total_retail_price) * "
				+ (Integer.parseInt(shop.gstRate) / 100.0) + " AS gst, " + "        SUM(total_retail_price) * " + (Integer.parseInt(shop.serviceRate) / 100.0) + " AS sc "
				+ "    FROM " + "        report_transaction_detail " + "    WHERE " + "        order_date >= :dateFrom and order_date <= :dateTo " + "        GROUP BY "
				+ "            shop_name ) b" + ")a";

		String sql2 = "select id, shop_name, quantity, retail_price, gst, sc, total_price, type from (" + "SELECT id, " + "shop_name, " + " quantity,  " + " retail_price, "
				+ " gst, " + " sc, " + " retail_price+gst+sc AS total_price, type " + "FROM " + " ( " + "     SELECT id, " + "        shop_name, "
				+ "        SUM(quantity)                  AS quantity, " + "        SUM(total_retail_price)        AS retail_price, " + "        SUM(total_retail_price) * "
				+ (Integer.parseInt(shop.gstRate) / 100.0) + " AS gst, " + "        SUM(total_retail_price) * " + (Integer.parseInt(shop.serviceRate) / 100.0) + " AS sc, "
				+ " type  FROM " + "        report_transaction_detail " + "    WHERE " + "        order_date >= :dateFrom and order_date <= :dateTo " + "        GROUP BY "
				+ "            shop_name, type ) b" + ")a";

		RawSql rawSql = RawSqlBuilder.parse(sql)
				// map resultSet columns to bean properties
				.columnMapping("id", "id").columnMapping("shop_name", "shopName").columnMapping("quantity", "totalQuantity").columnMapping("gst", "gst").columnMapping("sc", "sc")
				.columnMapping("retail_price", "retailPrice").columnMapping("total_price", "totalPrice").create();

		Query<ReportTransactionSummary> query = Ebean.find(ReportTransactionSummary.class);
		query.setRawSql(rawSql);

		RawSql rawSql2 = RawSqlBuilder.parse(sql2)
				// map resultSet columns to bean properties
				.columnMapping("id", "id").columnMapping("shop_name", "shopName").columnMapping("quantity", "totalQuantity").columnMapping("gst", "gst").columnMapping("sc", "sc")
				.columnMapping("retail_price", "retailPrice").columnMapping("total_price", "totalPrice").columnMapping("type", "type").create();

		Query<ReportTransactionSummary> query2 = Ebean.find(ReportTransactionSummary.class);
		query2.setRawSql(rawSql2);

		setQuery2(query, search);
		setQuery2(query2, search);

		List<ReportTransactionSummary> list = query.findList();
		List<ReportTransactionSummary> list2 = query2.findList();
		Pagination pagination = new Pagination();
		if (list != null && list.size() > 0) {
			ReportTransactionSummary obj = list.get(0);
			obj.card = 0.0;
			obj.cash = 0.0;
			if (list2 != null && list2.size() > 0) {
				for (ReportTransactionSummary inner : list2) {
					if (StringUtils.equalsIgnoreCase(inner.type, "CARD")) {
						obj.card = inner.totalPrice;
					} else if (StringUtils.equalsIgnoreCase(inner.type, "CASH")) {
						obj.cash = inner.totalPrice;
					}
				}
			}
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

	public Double getGst() {
		return gst;
	}

	public void setGst(Double gst) {
		this.gst = gst;
	}

	public Double getSc() {
		return sc;
	}

	public void setSc(Double sc) {
		this.sc = sc;
	}

	public Double getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(Double retailPrice) {
		this.retailPrice = retailPrice;
	}

	public void setTotalQuantity(Long totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
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

	public Double getCard() {
		return card;
	}

	public Double getCash() {
		return cash;
	}

}

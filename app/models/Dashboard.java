package models;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

import org.apache.commons.lang.StringUtils;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.annotation.Sql;

@Entity
@Sql
public class Dashboard {

	/** daily report **/
	public static List<ReportQuantity> dailyPieChartQuantity(Map search) {
		String sql = "SELECT id, shop_name, order_date, food_name, food_name_zh, quantity FROM (" + "SELECT " + "id, shop_name,"
				+ "DATE_FORMAT(order_date, '%Y-%m-%d') as order_date, food_name, food_name_zh," + "sum(quantity) as quantity FROM report_transaction_detail GROUP BY"
				+ " id, shop_name, food_name, food_name_zh, " + " DATE_FORMAT(order_date, '%Y-%m-%d')" + ") a";

		RawSql rawSql = RawSqlBuilder.parse(sql)
				// map resultSet columns to bean properties
				.columnMapping("id", "id").columnMapping("shop_name", "shopName").columnMapping("food_name", "label").columnMapping("food_name_zh", "foodNameZh")
				.columnMapping("order_date", "orderDate").columnMapping("quantity", "value").create();

		Query<ReportQuantity> query = Ebean.find(ReportQuantity.class);
		query.setRawSql(rawSql);

		if (search.keySet() != null) {
			Iterator searchKeys = search.keySet().iterator();
			while (searchKeys.hasNext()) {
				String key = (String) searchKeys.next();
				String value = (String) search.get(key);
				play.Logger.info("Value " + value);
				if (StringUtils.isEmpty(value)) {
					continue;
				} else if (key.equalsIgnoreCase("shopName")) {
					query.where().eq("shopName", value);
				} else if (key.equalsIgnoreCase("date")) {
					query.where().eq("orderDate", value);
				}
			}
		}

		return query.findList();
	}

	public static List<ReportMoney> dailyPieChartMoney(Map search) {
		String sql = "SELECT id, shop_name, order_date, food_name, food_name_zh, value FROM (" + "SELECT " + "id, shop_name,"
				+ "DATE_FORMAT(order_date, '%Y-%m-%d') as order_date, food_name, food_name_zh, sum(total_retail_price) as value FROM report_transaction_detail GROUP BY"
				+ " id, shop_name, food_name, food_name_zh, " + " DATE_FORMAT(order_date, '%Y-%m-%d')" + ") a";

		RawSql rawSql = RawSqlBuilder.parse(sql)
				// map resultSet columns to bean properties
				.columnMapping("id", "id").columnMapping("shop_name", "shopName").columnMapping("food_name", "label").columnMapping("food_name_zh", "foodNameZh")
				.columnMapping("order_date", "orderDate").columnMapping("value", "value").create();

		Query<ReportMoney> query = Ebean.find(ReportMoney.class);
		query.setRawSql(rawSql);

		if (search.keySet() != null) {
			Iterator searchKeys = search.keySet().iterator();
			while (searchKeys.hasNext()) {
				String key = (String) searchKeys.next();
				String value = (String) search.get(key);
				play.Logger.info("Value " + value);
				if (StringUtils.isEmpty(value)) {
					continue;
				} else if (key.equalsIgnoreCase("shopName")) {
					query.where().eq("shopName", value);
				} else if (key.equalsIgnoreCase("date")) {
					query.where().eq("orderDate", value);
				}
			}
		}

		return query.findList();
	}

	public static List<ReportMoney> dailyLineChartQuantity(Map search) {
		String sql = "SELECT id, shop_name, order_date, order_hour, value FROM (" + "SELECT " + "id, shop_name," + "DATE_FORMAT(order_date, '%Y-%m-%d') as order_date,"
				+ "DATE_FORMAT(order_date, '%H') AS order_hour," + "sum(quantity) as value " + "FROM" + " report_transaction_detail " + "GROUP BY" + " id, shop_name, "
				+ " DATE_FORMAT(order_date, '%Y-%m-%d %H')" + ") a order by order_hour asc";

		RawSql rawSql = RawSqlBuilder.parse(sql)
				// map resultSet columns to bean properties
				.columnMapping("id", "id").columnMapping("shop_name", "shopName").columnMapping("order_date", "orderDate").columnMapping("order_hour", "orderHour")
				.columnMapping("value", "value").create();

		Query<ReportMoney> query = Ebean.find(ReportMoney.class);
		query.setRawSql(rawSql);

		if (search.keySet() != null) {
			Iterator searchKeys = search.keySet().iterator();
			while (searchKeys.hasNext()) {
				String key = (String) searchKeys.next();
				String value = (String) search.get(key);
				play.Logger.info("Value " + value);
				if (StringUtils.isEmpty(value)) {
					continue;
				} else if (key.equalsIgnoreCase("shopName")) {
					query.where().eq("shopName", value);
				} else if (key.equalsIgnoreCase("date")) {
					query.where().eq("orderDate", value);
				}
			}
		}

		return query.findList();
	}

	public static List<ReportMoney> dailyLineChartMoney(Map search) {
		String sql = "SELECT id, shop_name, order_date, order_hour, value FROM (" + "SELECT " + "id, shop_name," + "DATE_FORMAT(order_date, '%Y-%m-%d') as order_date,"
				+ "DATE_FORMAT(order_date, '%H') AS order_hour," + "sum(total_retail_price) as value " + "FROM" + " report_transaction_detail " + "GROUP BY" + " id, shop_name, "
				+ " DATE_FORMAT(order_date, '%Y-%m-%d %H')" + ") a order by order_hour asc";

		RawSql rawSql = RawSqlBuilder.parse(sql)
				// map resultSet columns to bean properties
				.columnMapping("id", "id").columnMapping("shop_name", "shopName").columnMapping("order_date", "orderDate").columnMapping("order_hour", "orderHour")
				.columnMapping("value", "value").create();

		Query<ReportMoney> query = Ebean.find(ReportMoney.class);
		query.setRawSql(rawSql);

		if (search.keySet() != null) {
			Iterator searchKeys = search.keySet().iterator();
			while (searchKeys.hasNext()) {
				String key = (String) searchKeys.next();
				String value = (String) search.get(key);
				play.Logger.info("Value " + value);
				if (StringUtils.isEmpty(value)) {
					continue;
				} else if (key.equalsIgnoreCase("shopName")) {
					query.where().eq("shopName", value);
				} else if (key.equalsIgnoreCase("date")) {
					query.where().eq("orderDate", value);
				}
			}
		}

		return query.findList();
	}

	/** monthly report **/
	public static List<ReportQuantity> monthlyPieChartQuantity(Map search) {
		String sql = "SELECT id, shop_name, order_date, food_name, food_name_zh, quantity FROM (" + "SELECT " + "id, shop_name,"
				+ "DATE_FORMAT(order_date, '%Y-%m') as order_date," + "food_name," + "food_name_zh," + "COUNT(quantity) as quantity " + "FROM" + " report_transaction_detail "
				+ "GROUP BY" + " id, shop_name, food_name, food_name_zh, " + " DATE_FORMAT(order_date, '%Y-%m')" + ") a";

		RawSql rawSql = RawSqlBuilder.parse(sql)
				// map resultSet columns to bean properties
				.columnMapping("id", "id").columnMapping("shop_name", "shopName").columnMapping("food_name", "label").columnMapping("food_name_zh", "foodNameZh")
				.columnMapping("order_date", "orderDateStr").columnMapping("quantity", "value").create();

		Query<ReportQuantity> query = Ebean.find(ReportQuantity.class);
		query.setRawSql(rawSql);

		if (search.keySet() != null) {
			Iterator searchKeys = search.keySet().iterator();
			while (searchKeys.hasNext()) {
				String key = (String) searchKeys.next();
				String value = (String) search.get(key);
				play.Logger.info("Value " + value);
				if (StringUtils.isEmpty(value)) {
					continue;
				} else if (key.equalsIgnoreCase("shopName")) {
					query.where().eq("shopName", value);
				} else if (key.equalsIgnoreCase("date")) {
					query.where().eq("orderDateStr", value);
				}
			}
		}

		return query.findList();
	}

	public static List<ReportMoney> monthlyPieChartMoney(Map search) {
		String sql = "SELECT id, shop_name, order_date, food_name, food_name_zh, value FROM (" + "SELECT " + "id, shop_name," + "DATE_FORMAT(order_date, '%Y-%m') as order_date,"
				+ "food_name," + "food_name_zh," + "sum(total_retail_price) as value " + "FROM" + " report_transaction_detail " + "GROUP BY"
				+ " id, shop_name, food_name, food_name_zh, " + " DATE_FORMAT(order_date, '%Y-%m')" + ") a";

		RawSql rawSql = RawSqlBuilder.parse(sql)
				// map resultSet columns to bean properties
				.columnMapping("id", "id").columnMapping("shop_name", "shopName").columnMapping("food_name", "label").columnMapping("food_name_zh", "foodNameZh")
				.columnMapping("order_date", "orderDateStr").columnMapping("value", "value").create();

		Query<ReportMoney> query = Ebean.find(ReportMoney.class);
		query.setRawSql(rawSql);

		if (search.keySet() != null) {
			Iterator searchKeys = search.keySet().iterator();
			while (searchKeys.hasNext()) {
				String key = (String) searchKeys.next();
				String value = (String) search.get(key);
				play.Logger.info("Value " + value);
				if (StringUtils.isEmpty(value)) {
					continue;
				} else if (key.equalsIgnoreCase("shopName")) {
					query.where().eq("shopName", value);
				} else if (key.equalsIgnoreCase("date")) {
					query.where().eq("orderDateStr", value);
				}
			}
		}

		return query.findList();
	}

	public static List<ReportMoney> monthlyLineChartQuantity(Map search) {
		String sql = "SELECT id, shop_name, order_month, order_date, value FROM (" + "SELECT " + "id, shop_name," + "DATE_FORMAT(order_date, '%Y-%m') as order_month,"
				+ "DATE_FORMAT(order_date, '%d') AS order_date," + "sum(quantity) as value " + "FROM" + " report_transaction_detail " + "GROUP BY" + " id, shop_name, "
				+ " DATE_FORMAT(order_date, '%Y-%m-%d')" + ") a order by order_month, order_date asc";

		RawSql rawSql = RawSqlBuilder.parse(sql)
				// map resultSet columns to bean properties
				.columnMapping("id", "id").columnMapping("shop_name", "shopName").columnMapping("order_date", "orderDateStr").columnMapping("order_month", "orderMonth")
				.columnMapping("value", "value").create();

		Query<ReportMoney> query = Ebean.find(ReportMoney.class);
		query.setRawSql(rawSql);

		if (search.keySet() != null) {
			Iterator searchKeys = search.keySet().iterator();
			while (searchKeys.hasNext()) {
				String key = (String) searchKeys.next();
				String value = (String) search.get(key);
				play.Logger.info("Value " + value);
				if (StringUtils.isEmpty(value))
					continue;

				else if (key.equalsIgnoreCase("shopName")) {
					query.where().eq("shopName", value);
				} else if (key.equalsIgnoreCase("date")) {
					query.where().eq("orderMonth", value);
				}
			}
		}

		return query.findList();
	}

	public static List<ReportMoney> monthlyLineChartMoney(Map search) {
		String sql = "SELECT id, shop_name, order_month, order_date, value FROM (" + "SELECT " + "id, shop_name," + "DATE_FORMAT(order_date, '%Y-%m') as order_month,"
				+ "DATE_FORMAT(order_date, '%d') AS order_date," + "sum(total_retail_price) as value " + "FROM" + " report_transaction_detail " + "GROUP BY" + " id, shop_name, "
				+ " DATE_FORMAT(order_date, '%Y-%m-%d')" + ") a order by order_month, order_date asc";

		RawSql rawSql = RawSqlBuilder.parse(sql)
				// map resultSet columns to bean properties
				.columnMapping("id", "id").columnMapping("shop_name", "shopName").columnMapping("order_date", "orderDateStr").columnMapping("order_month", "orderMonth")
				.columnMapping("value", "value").create();

		Query<ReportMoney> query = Ebean.find(ReportMoney.class);
		query.setRawSql(rawSql);

		if (search.keySet() != null) {
			Iterator searchKeys = search.keySet().iterator();
			while (searchKeys.hasNext()) {
				String key = (String) searchKeys.next();
				String value = (String) search.get(key);
				play.Logger.info("Value " + value);
				if (StringUtils.isEmpty(value)) {
					continue;
				} else if (key.equalsIgnoreCase("shopName")) {
					query.where().eq("shopName", value);
				} else if (key.equalsIgnoreCase("date")) {
					query.where().eq("orderMonth", value);
				}
			}
		}

		return query.findList();
	}

	/** get daily result */
	public static List<ReportMoney> dailyClosingSummary(Map search) {
		String sql = "SELECT id, shop_name, order_date, value FROM (SELECT id, shop_name,"
				+ " DATE_FORMAT(order_date, '%Y-%m-%d') as order_date, sum(total_retail_price) as value FROM report_transaction_detail GROUP BY"
				+ " id, shop_name, DATE_FORMAT(order_date, '%Y-%m-%d') ) a order by order_date asc";

		RawSql rawSql = RawSqlBuilder.parse(sql)
		// map resultSet columns to bean properties
				.columnMapping("id", "id").columnMapping("shop_name", "shopName").columnMapping("order_date", "orderDate").columnMapping("value", "value").create();

		Query<ReportMoney> query = Ebean.find(ReportMoney.class);
		query.setRawSql(rawSql);

		if (search.keySet() != null) {
			Iterator searchKeys = search.keySet().iterator();
			while (searchKeys.hasNext()) {
				String key = (String) searchKeys.next();
				String value = (String) search.get(key);
				play.Logger.info("Value " + value);
				if (StringUtils.isEmpty(value)) {
					continue;
				} else if (key.equalsIgnoreCase("shopName")) {
					query.where().eq("shopName", value);
				} else if (key.equalsIgnoreCase("date")) {
					query.where().eq("orderDate", value);
				}
			}
		}

		return query.findList();
	}
}

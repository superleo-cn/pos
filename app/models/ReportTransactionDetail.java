package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import com.avaje.ebean.Query;

@Entity
public class ReportTransactionDetail {

	final static Logger logger = LoggerFactory.getLogger(ReportTransactionDetail.class);

	@Transient
	public Long no;
	public Date createDate, orderDate;
	public String foodName, foodNameZh, shopName;

	@Transient
	public String item;

	public Double retailPrice, costPrice, totalCostPrice, totalDiscount, totalRetailPrice, totalPackage;
	public Long quantity;
	public String freeOfCharge;

	/* the following are service methods */
	public static Pagination search(Map search, Pagination pagination) {
		pagination = pagination == null ? new Pagination() : pagination;

		Query query = Ebean.find(ReportTransactionDetail.class).orderBy("orderDate desc");
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
		List<ReportTransactionDetail> list = new ArrayList<ReportTransactionDetail>();
		if (!pagination.all) {
			PagingList<ReportTransactionDetail> pagingList = expList.findPagingList(pagination.pageSize);
			pagingList.setFetchAhead(false);
			Page page = pagingList.getPage(pagination.currentPage - 1);

			list = page.getList();

			pagination.iTotalDisplayRecords = expList.findRowCount();
			pagination.iTotalRecords = expList.findRowCount();

		} else {
			pagination.currentPage = 1;
			list = expList.findList();
		}

		if (list != null) {
			Long no = ((pagination.currentPage - 1) * pagination.pageSize) + 1l;
			for (ReportTransactionDetail report : list) {
				report.no = no;
				if (pagination.zh) {
					report.item = report.foodNameZh;
				} else {
					report.item = report.foodName;
				}
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

	public String getFoodName() {
		return foodName;
	}

	public String getFoodNameZh() {
		return foodNameZh;
	}

	public String getShopName() {
		return shopName;
	}

	public String getItem() {
		return item;
	}

	public Double getRetailPrice() {
		return retailPrice;
	}

	public Double getCostPrice() {
		return costPrice;
	}

	public Double getTotalCostPrice() {
		return totalCostPrice;
	}

	public Double getTotalDiscount() {
		return totalDiscount;
	}

	public Double getTotalRetailPrice() {
		return totalRetailPrice;
	}

	public Long getQuantity() {
		return quantity;
	}

	public Double getTotalPackage() {
		return totalPackage;
	}

	public String getFreeOfCharge() {
		return freeOfCharge;
	}

	public Date getOrderDate() {
		return orderDate;
	}
}

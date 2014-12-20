package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_audit")
public class Audit {

	final static Logger logger = LoggerFactory.getLogger(Audit.class);

	@Expose
	@Id
	public Long id;

	@Expose
	@Transient
	public Long no;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	public User user;

	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "shop_id", referencedColumnName = "id")
	public Shop shop;

	@Expose
	public String createBy, modifiedBy;

	@Expose
	public String action;

	@Expose
	public Date createDate, modifiedDate;

	@Expose
	public Date actionDate;

	@Transient
	public Long androidId;

	@Expose
	@Transient
	public String shopName, realName;

	public String getAction() {
		return action;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public String getRealName() {
		return realName;
	}

	public String getShopName() {
		return shopName;
	}

	/* the following are service methods */
	public static Pagination search(String queryName, Pagination pagination) {
		pagination = pagination == null ? new Pagination() : pagination;
		ExpressionList expList = Ebean.find(Audit.class).where();
		if (StringUtils.isNotEmpty(queryName)) {
			queryName = StringUtils.trimToNull(queryName);
			expList.where().ilike("action", "%" + queryName + "%");
		}
		PagingList<Audit> pagingList = expList.findPagingList(pagination.pageSize);
		pagingList.setFetchAhead(false);
		Page page = pagingList.getPage(pagination.currentPage);
		pagination.recordList = page.getList();
		pagination.pageCount = page.getTotalPageCount();
		pagination.recordCount = page.getTotalRowCount();
		return pagination;
	}

	public static Audit view(Long id) {
		if (id != null) {
			return Ebean.find(Audit.class, id);
		}
		return null;
	}

	public static boolean store(Audit audit) {
		try {
			audit.createDate = new Date();
			Ebean.save(audit);
			return true;
		} catch (Exception e) {
			logger.error("Store Aduit Error", e);
		}
		return false;
	}

	public static boolean delete(Long id) {
		Integer flag = Ebean.delete(Audit.class, id);
		return (flag > 0) ? true : false;
	}

	public static List<Audit> listByShop(Long id) {
		if (id != null) {
			return null;
		}
		return null;
	}

	/* the following are service methods */
	public static Pagination search(Map search, Pagination pagination) {
		pagination = pagination == null ? new Pagination() : pagination;
		Query query = Ebean.find(Audit.class).fetch("user", "realname").fetch("shop", "name").order("createDate desc");
		ExpressionList expList = query.where();
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

				logger.info("Key " + key + " Value " + value);
				if (StringUtils.isEmpty(value) && values == null) {
					continue;
				}

				if (key.equalsIgnoreCase("dateFrom")) {
					expList.where().ge("createDate", value + " 00:00:00");
				} else if (key.equalsIgnoreCase("dateTo")) {
					expList.where().le("createDate", value + " 23:59:59");
				} else if (key.equalsIgnoreCase("shopName")) {
					query.where().in("shop.name", values);
				} else {
					expList.where().ilike(key, "%" + value + "%");
				}
			}
		}
		
		List<Audit> list = new ArrayList<Audit>();
		if (!pagination.all) {
			PagingList<Audit> pagingList = expList.findPagingList(pagination.pageSize);
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
			for (Audit report : list) {
				report.no = no;
				report.realName = report.user.realname;
				if (report.shop != null) {
					report.shopName = report.shop.name;
				} else {
					report.shopName = "";
				}
				no++;
			}
		}

		pagination.recordList = list;
		return pagination;
	}

}

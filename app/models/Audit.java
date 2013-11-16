package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import play.data.validation.Required;
import utils.Pagination;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;

import constants.Constants;

@Entity
@Table(name = "tb_audit")
public class Audit {
	@Id
	public Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	public User user;

	@ManyToOne
	@JoinColumn(name = "shop_id", referencedColumnName = "id")
	public Shop shop;

	public String action;

	public String createBy, modifiedBy;

	public Date createDate, modifiedDate;

	@Transient
	public Long androidId;

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
			e.printStackTrace();
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
}

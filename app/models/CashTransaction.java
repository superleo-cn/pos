package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
@Table(name = "tb_cash_transaction")
public class CashTransaction {
	@Id
	public Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	public User user;

	@ManyToOne
	@JoinColumn(name = "shop_id", referencedColumnName = "id")
	public Shop shop;

	@ManyToOne
	@JoinColumn(name = "cash_id", referencedColumnName = "id")
	public Cash cash;

	public Float price;

	public Integer quantity;

	public Float totalPrice;

	public String createBy, modifiedBy;

	public Date createDate, modifiedDate;

	/* the following are service methods */
	public static Pagination search(String queryName, Pagination pagination) {
		pagination = pagination == null ? new Pagination() : pagination;
		ExpressionList expList = Ebean.find(CashTransaction.class).where();
		if (StringUtils.isNotEmpty(queryName)) {
			queryName = StringUtils.trimToNull(queryName);
			expList.where().ilike("name", "%" + queryName + "%");
		}
		PagingList<CashTransaction> pagingList = expList.findPagingList(pagination.pageSize);
		pagingList.setFetchAhead(false);
		Page page = pagingList.getPage(pagination.currentPage);
		pagination.recordList = page.getList();
		pagination.pageCount = page.getTotalPageCount();
		pagination.recordCount = page.getTotalRowCount();
		return pagination;
	}

	public static CashTransaction view(Long id) {
		if (id != null) {
			return Ebean.find(CashTransaction.class, id);
		}
		return null;
	}

	public static boolean store(CashTransaction cashTransaction) {
		if (cashTransaction.id == null || cashTransaction.id == 0) {
			if (cashTransaction.cash != null && cashTransaction.cash.id != null && cashTransaction.user != null
					&& cashTransaction.user.id != null) {
				Cash cash = Cash.view(cashTransaction.cash.id);
				User user = User.view(cashTransaction.user.id);
				if (cash != null && user != null) {
					cashTransaction.price = cash.price;
					cashTransaction.totalPrice = cashTransaction.price * cashTransaction.quantity;
					cashTransaction.createBy = user.username;
					cashTransaction.createDate = new Date();
					Ebean.save(cashTransaction);
					return true;
				}
			}
		}
		return false;
	}

	public static boolean delete(Long id) {
		Integer flag = Ebean.delete(CashTransaction.class, id);
		return (flag > 0) ? true : false;
	}

	public static List<CashTransaction> listByShop(Long id) {
		if (id != null) {
			return Ebean.find(CashTransaction.class).where().eq("status", true).findList();
		}
		return null;
	}
}

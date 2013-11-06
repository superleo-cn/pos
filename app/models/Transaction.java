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
@Table(name = "tb_transaction")
public class Transaction {
	@Id
	public Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	public User user;

	@ManyToOne
	@JoinColumn(name = "food_id", referencedColumnName = "id")
	public Food food;

	public Float costPrice;

	public Float retailPrice;

	public Integer quantity;

	public Float totalCostPrice;

	public Float totalRetailPrice;

	public String createBy, modifiedBy;

	public Date createDate, modifiedDate;

	/* the following are service methods */
	public static Pagination search(String queryName, Pagination pagination) {
		pagination = pagination == null ? new Pagination() : pagination;
		ExpressionList expList = Ebean.find(Transaction.class).where();
		if (StringUtils.isNotEmpty(queryName)) {
			queryName = StringUtils.trimToNull(queryName);
			expList.where().ilike("name", "%" + queryName + "%");
		}
		PagingList<Transaction> pagingList = expList.findPagingList(pagination.pageSize);
		pagingList.setFetchAhead(false);
		Page page = pagingList.getPage(pagination.currentPage);
		pagination.recordList = page.getList();
		pagination.pageCount = page.getTotalPageCount();
		pagination.recordCount = page.getTotalRowCount();
		return pagination;
	}

	public static Transaction view(Integer id) {
		if (id != null) {
			return Ebean.find(Transaction.class, id);
		}
		return null;
	}

	public static boolean store(Transaction transaction) {
		if (transaction.id == null || transaction.id == 0) {
			if (transaction.food != null && transaction.user != null && transaction.food.id != null
					&& transaction.user.id != null) {
				Food food = Food.view(transaction.food.id);
				User user = User.view(transaction.user.id);
				if (food != null && transaction.quantity != null) {
					Float totalCostPrice = food.costPrice * transaction.quantity;
					Float totalRetailPrice = food.retailPrice * transaction.quantity;
					transaction.costPrice = food.costPrice;
					transaction.retailPrice = food.retailPrice;
					transaction.totalCostPrice = totalCostPrice;
					transaction.totalRetailPrice = totalRetailPrice;
					transaction.createBy = user.username;
					transaction.createDate = new Date();
					Ebean.save(transaction);
					return true;
				}
			}
		}
		return false;
	}

	public static boolean delete(Integer id) {
		Integer flag = Ebean.delete(Transaction.class, id);
		return (flag > 0) ? true : false;
	}

	public static List<Transaction> listByShop(Long id) {
		if (id != null) {
			return Ebean.find(Transaction.class).where().eq("status", true).findList();
		}
		return null;
	}
}

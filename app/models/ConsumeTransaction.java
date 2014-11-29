package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
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
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;

@Entity
@Table(name = "tb_consume_transaction")
public class ConsumeTransaction {

	final static Logger logger = LoggerFactory.getLogger(ConsumeTransaction.class);

	@Id
	public Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	public User user;

	@ManyToOne
	@JoinColumn(name = "shop_id", referencedColumnName = "id")
	public Shop shop;

	@ManyToOne
	@JoinColumn(name = "consumption_id", referencedColumnName = "id")
	public Consumption consumption;

	public Float price;

	public String createBy, modifiedBy;

	public Date createDate, modifiedDate;

	@Transient
	public Long androidId;

	/* the following are service methods */
	public static Pagination search(String queryName, Pagination pagination) {
		pagination = pagination == null ? new Pagination() : pagination;
		ExpressionList expList = Ebean.find(ConsumeTransaction.class).where();
		if (StringUtils.isNotEmpty(queryName)) {
			queryName = StringUtils.trimToNull(queryName);
			expList.where().ilike("name", "%" + queryName + "%");
		}
		PagingList<ConsumeTransaction> pagingList = expList.findPagingList(pagination.pageSize);
		pagingList.setFetchAhead(false);
		Page page = pagingList.getPage(pagination.currentPage);
		pagination.recordList = page.getList();
		pagination.pageCount = page.getTotalPageCount();
		pagination.recordCount = page.getTotalRowCount();
		return pagination;
	}

	public static ConsumeTransaction view(Long id) {
		if (id != null) {
			return Ebean.find(ConsumeTransaction.class, id);
		}
		return null;
	}

	public static boolean store(ConsumeTransaction consumeTransaction) {
		try {
			if (consumeTransaction.id == null || consumeTransaction.id == 0) {
				if (consumeTransaction.consumption != null && consumeTransaction.consumption.id != null && consumeTransaction.user != null && consumeTransaction.user.id != null) {
					Consumption consumption = Consumption.view(consumeTransaction.consumption.id);
					User user = User.view(consumeTransaction.user.id);
					if (consumption != null && user != null) {
						consumeTransaction.createBy = user.username;
						consumeTransaction.createDate = new Date();
						Ebean.save(consumeTransaction);
						return true;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Store Consume Transaction Error", e);
		}
		return false;
	}

	public static boolean delete(Long id) {
		Integer flag = Ebean.delete(ConsumeTransaction.class, id);
		return (flag > 0) ? true : false;
	}

	public static List<ConsumeTransaction> listByShop(Long id) {
		if (id != null) {
			return Ebean.find(ConsumeTransaction.class).where().eq("status", true).findList();
		}
		return null;
	}
}

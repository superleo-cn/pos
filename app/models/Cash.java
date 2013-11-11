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
@Table(name = "tb_cash")
public class Cash {
	@Id
	public Long id;
	
	@Required(message = "Cash price cannot be empty")
	public Float price;

	@Required(message = "Status cannot be empty")
	public Boolean status;
	
	@ManyToOne
	@JoinColumn(name = "shop_id", referencedColumnName = "id")
	public Shop shop;
	
	public Integer position;

	public String createBy, modifiedBy;
	
	public Date createDate, modifiedDate;

	/* the following are service methods */
	public static Pagination search(String queryName, Pagination pagination) {
		pagination = pagination == null ? new Pagination() : pagination;
		ExpressionList expList = Ebean.find(Cash.class).where();
		if (StringUtils.isNotEmpty(queryName)) {
			queryName = StringUtils.trimToNull(queryName);
			expList.where().ilike("name", "%" + queryName + "%");
		}
		PagingList<Cash> pagingList = expList.findPagingList(pagination.pageSize);
		pagingList.setFetchAhead(false);
		Page page = pagingList.getPage(pagination.currentPage);
		pagination.recordList = page.getList();
		pagination.pageCount = page.getTotalPageCount();
		pagination.recordCount = page.getTotalRowCount();
		return pagination;
	}

	public static Cash view(Long id) {
		if (id != null) {
			return Ebean.find(Cash.class, id);
		}
		return null;
	}

	public static void store(Cash cash) {
		if (cash.id != null && cash.id > 0) {
			Cash newCash = Ebean.find(Cash.class, cash.id);
			try {
				PropertyUtils.copyProperties(newCash, cash);
				newCash.modifiedDate = new Date();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Ebean.update(newCash);
		} else {
			Ebean.save(cash);
		}
	}

	public static boolean delete(Long id) {
		Integer flag = Ebean.delete(Cash.class, id);
		return (flag > 0) ? true : false;
	}
	
	public static List<Cash> listByShop(Long id) {
		if (id != null) {
			return Ebean.find(Cash.class).select("id, name, price, picture").where().eq("status", true)
					.findList();
		}
		return null;
	}
}

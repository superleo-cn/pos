package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import play.data.validation.Required;
import utils.Pagination;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;

@Entity
@Table(name = "tb_shop")
public class Shop {
	@Id
	public Long id;
	
	@Required(message = "Shop name cannot be empty")
	public String name;

	@Required(message = "Status cannot be empty")
	public Boolean status;

	@Required(message = "Expiry date cannot be empty")
	public Date expiryDate;

	public String createBy, modifiedBy;
	
	public Date createDate, modifiedDate;

	/* the following are service methods */
	public static Pagination search(String queryName, Pagination pagination) {
		pagination = pagination == null ? new Pagination() : pagination;
		ExpressionList expList = Ebean.find(Shop.class).where();
		if (StringUtils.isNotEmpty(queryName)) {
			queryName = StringUtils.trimToNull(queryName);
			expList.where().ilike("name", "%" + queryName + "%");
		}
		PagingList<Shop> pagingList = expList.findPagingList(pagination.pageSize);
		pagingList.setFetchAhead(false);
		Page page = pagingList.getPage(pagination.currentPage);
		pagination.recordList = page.getList();
		pagination.pageCount = page.getTotalPageCount();
		pagination.recordCount = page.getTotalRowCount();
		return pagination;
	}

	public static Shop view(Integer id) {
		if (id != null) {
			return Ebean.find(Shop.class, id);
		}
		return null;
	}

	public static void store(Shop shop) {
		if (shop.id != null && shop.id > 0) {
			Shop newUser = Ebean.find(Shop.class, shop.id);
			try {
				PropertyUtils.copyProperties(newUser, shop);
				newUser.modifiedDate = new Date();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Ebean.update(newUser);
		} else {
			Ebean.save(shop);
		}
	}

	public static boolean delete(Long id) {
		Integer flag = Ebean.delete(Shop.class, id);
		return (flag > 0) ? true : false;
	}
}

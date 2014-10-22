package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;

import play.data.validation.Required;
import utils.Pagination;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;

@Entity
@Table(name = "tb_category")
public class Category {
	@Id
	public Long id;

	@Required(message = "Category name cannot be empty")
	public String name;

	@Required(message = "Category chinese name cannot be empty")
	public String nameZh;

	@Required(message = "Category code cannot be empty")
	public String code;

	@Required(message = "Status cannot be empty")
	public Boolean status;

	public Integer position;

	@ManyToOne
	@JoinColumn(name = "shop_id", referencedColumnName = "id")
	public Shop shop;

	public String createBy, modifiedBy;

	public Date createDate, modifiedDate;

	/* the following are service methods */
	public static Pagination search(String queryName, Pagination pagination) {
		pagination = pagination == null ? new Pagination() : pagination;
		ExpressionList expList = Ebean.find(Category.class).where();
		if (StringUtils.isNotEmpty(queryName)) {
			queryName = StringUtils.trimToNull(queryName);
			expList.where().ilike("name", "%" + queryName + "%");
		}
		PagingList<Category> pagingList = expList.findPagingList(pagination.pageSize);
		pagingList.setFetchAhead(false);
		Page page = pagingList.getPage(pagination.currentPage);
		pagination.recordList = page.getList();
		pagination.pageCount = page.getTotalPageCount();
		pagination.recordCount = page.getTotalRowCount();
		return pagination;
	}

	public static List<Category> listByShop(Long id) {
		return Ebean.find(Category.class).select("id, name, nameZh, code, status, position").where().eq("shop.id", id)
				.eq("status", true).order("position").findList();
	}

	public static void bulkStore(List<Category> list) {
		Ebean.save(list);
	}

}

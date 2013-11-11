package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@Table(name = "tb_food")
public class Food {
	@Id
	public Long id;

	@Required(message = "Food name cannot be empty")
	public String name;

	@Required(message = "Food cost price cannot be empty")
	public Float costPrice;
	
	@Required(message = "Food retail price cannot be empty")
	public Float retailPrice;

	public String picture;

	@Transient
	public String getPictureUrl() {
		return Constants.WEBSITE_URL + picture;
	}

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
		ExpressionList expList = Ebean.find(Food.class).where();
		if (StringUtils.isNotEmpty(queryName)) {
			queryName = StringUtils.trimToNull(queryName);
			expList.where().ilike("name", "%" + queryName + "%");
		}
		PagingList<Food> pagingList = expList.findPagingList(pagination.pageSize);
		pagingList.setFetchAhead(false);
		Page page = pagingList.getPage(pagination.currentPage);
		pagination.recordList = page.getList();
		pagination.pageCount = page.getTotalPageCount();
		pagination.recordCount = page.getTotalRowCount();
		return pagination;
	}

	public static Food view(Long id) {
		if (id != null) {
			return Ebean.find(Food.class, id);
		}
		return null;
	}

	public static List<Food> listByShop(Long id) {
		if (id != null) {
			List<Food> foods = Ebean.find(Food.class).select("id, name, price, picture").where().eq("status", true)
					.findList();
			CollectionUtils.forAllDo(foods, new Closure() {
				public void execute(Object o) {
					if (o != null) {
						Food food = (Food) o;
						food.picture = Constants.PICTURE_URL + food.picture;
					}
				}
			});
			return foods;
		}
		return null;
	}

}

package models;

import java.util.*;

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
public class Food implements Comparable {
	@Id
	public Long id;

	@Required(message = "SN cannot be empty")
	public String sn;

	@Required(message = "Food name cannot be empty")
	public String name;

	@Required(message = "Food chinese name cannot be empty")
	public String nameZh;

	@Required(message = "Food type cannot be empty")
	public String type;

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
	
	@ManyToOne
	@JoinColumn(name = "category_id", referencedColumnName = "id")
	public Category category;

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

    public static Pagination searchDistinct2(String queryName, Pagination pagination) {
        pagination = pagination == null ? new Pagination() : pagination;
        ExpressionList expList = Ebean.find(Food.class).where();
        if (StringUtils.isNotEmpty(queryName)) {
            queryName = StringUtils.trimToNull(queryName);
            expList.where().ilike("name", "%" + queryName + "%");
        }
        List<Food> list =  expList.order("name").findList();
        Set<String> set = new TreeSet<String>();
        Set<Food> set2 = new TreeSet<Food>();
        for(Food food:list){
            set.add(food.name);
        }

        long id =0l;
        for(String name:set){
            Food food = new Food();
            food.name=name;
            food.id=id;
            id++;
            set2.add(food);
        }
        pagination.recordList= Arrays.asList(set2.toArray());
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
			List<Food> foods = Ebean.find(Food.class).select("id, sn, name, nameZh, type, retailPrice, picture, position").fetch("category", "category").where()
					.eq("shop.id", id).eq("status", true).order("position").findList();
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

    @Override
    public int compareTo(Object o) {
        if(o==null || name==null) return 0;
        if(!(o instanceof Food)) return 0;
        Food food = (Food)o;
        return name.compareTo(food.name);  //To change body of implemented methods use File | Settings | File Templates.
    }
}

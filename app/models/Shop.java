package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Required;
import utils.Pagination;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_shop")
public class Shop {

	final static Logger logger = LoggerFactory.getLogger(Shop.class);

	@Expose
	@Id
	public Long id;

	@Expose
	@Required(message = "Shop name cannot be empty")
	public String name;

	@Expose
	@Required(message = "Shop code cannot be empty")
	public String code;

	@Expose
	@Required(message = "Status cannot be empty")
	public Boolean status;

	@Expose
	@Required(message = "Expiry date cannot be empty")
	public Date expiryDate;

	@Expose
	public String address;

	@Expose
	public String contact;

	@Expose
	public String website;

	@Expose
	public String email;

	@Expose
	public String weChat;

	@Expose
	public String openTime;

	@Expose
	public String gstRegNo;

	@Expose
	public String gstRate;

	@Expose
	public String serviceRate;

	@Expose
	public Boolean kichenPrinter, sendSms;

	@Expose
	public String createBy, modifiedBy;

	@Expose
	public Date createDate, modifiedDate;

	// @ManyToOne
	// @JoinColumn(name = "shop_id", referencedColumnName = "id")
	@ManyToMany(mappedBy = "shops")
	public List<User> users;

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

	public static Shop view(Long id) {
		if (id != null) {
			return Ebean.find(Shop.class, id);
		}
		return null;
	}

	public static List<Shop> findByNames(List<String> shopName) {
		if (shopName != null && shopName.size() > 0) {
			return Ebean.find(Shop.class).select("id, name, gstRegNo, gstRate, serviceRate").where().in("name", shopName).findList();
		}
		return null;
	}
	
	public static Shop findByName(String shopName) {
		if (StringUtils.isNotEmpty(shopName)) {
			List<Shop> list = Ebean.find(Shop.class).select("id, name, gstRegNo, gstRate, serviceRate").where().eq("name", shopName).findList();
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		}
		return null;
	}

	public static List<Shop> listJson(Long id) {
		return Ebean.find(Shop.class)
				.select("id, name, code, status, expiryDate, address, contact, website, email, weChat, openTime, gstRegNo, gstRate, serviceRate, kichenPrinter").where()
				.eq("id", id).findList();
	}

	public static List<Shop> list() {
		return Ebean.find(Shop.class)
				.select("id, name, code, status, expiryDate, address, contact, website, email, weChat, openTime, gstRegNo, gstRate, serviceRate, kichenPrinter").findList();
	}

	public static List<Shop> listByUserId(String userId) {
		return Ebean.find(Shop.class)
				.select("id, name, code, status, expiryDate, address, contact, website, email, weChat, openTime, gstRegNo, gstRate, serviceRate, kichenPrinter")
				.fetch("users", new FetchConfig().query()).where().eq("users.id", userId).findList();
	}

	public static void store(Shop shop) {
		if (shop.id != null && shop.id > 0) {
			Shop newUser = Ebean.find(Shop.class, shop.id);
			try {
				PropertyUtils.copyProperties(newUser, shop);
				newUser.modifiedDate = new Date();
			} catch (Exception e) {
				logger.error("Store Shop Error", e);
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

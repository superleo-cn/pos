package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Required;
import utils.Pagination;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;

@Entity
@Table(name = "tb_shop")
public class Shop {

	final static Logger logger = LoggerFactory.getLogger(Shop.class);

	@Id
	public Long id;

	@Required(message = "Shop name cannot be empty")
	public String name;

	@Required(message = "Shop code cannot be empty")
	public String code;

	@Required(message = "Status cannot be empty")
	public Boolean status;

	@Required(message = "Expiry date cannot be empty")
	public Date expiryDate;

	public String address;

	public String contact;

	public String website;

	public String email;

	public String weChat;

	public String openTime;

	public String gstRegNo;

	public String gstRate;

	public String serviceRate;

	public Boolean kichenPrinter, sendSms;

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

	public static Shop view(Long id) {
		if (id != null) {
			return Ebean.find(Shop.class, id);
		}
		return null;
	}
	
	public static Shop findByName(String shopName) {
		if (StringUtils.isNotEmpty(shopName)) {
			 List<Shop> list = Ebean.find(Shop.class)
				.select("id, name, gstRegNo, gstRate, serviceRate").where()
				.eq("name", shopName).findList();
			 if(list != null && list.size() > 0){
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

package models;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Required;
import utils.Pagination;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import com.avaje.ebean.annotation.Transactional;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tb_user")
public class User {

	final static Logger logger = LoggerFactory.getLogger(User.class);

	@Expose
	@Id
	public Long id;

	@Expose
	@Required(message = "Username cannot be empty")
	public String username;

	@Expose
	@Required(message = "Password cannot be empty")
	public String password;

	@Expose
	public String realname;

	@Expose
	@Required(message = "User type cannot be empty")
	public String usertype;

	@Expose
	@Required(message = "Status cannot be empty")
	public Boolean status;

	@Expose
	public String userIp;

	@Expose
	public String userMac;

	@Expose
	public String createBy, modifiedBy;

	@Expose
	public Date createDate, modifiedDate, lastLoginDate;

	// @ManyToOne
	// @JoinColumn(name = "shop_id", referencedColumnName = "id")
	@ManyToMany
	@JoinTable(name = "shop_user", joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "shop_id", referencedColumnName = "id") })
	public List<Shop> shops;

	@Transient
	public Long shopId;

	@Transient
	public Shop getMyShop() {
		if (shops != null && shops.size() > 0) {
			return shops.get(0);
		}
		return null;
	}

	/* the following are service methods */
	public static User loginPage(User user) {
		List<User> users = Ebean.find(User.class).select("id, username, realname, usertype, status").fetch("shops", "id").where().eq("username", user.username)
				.eq("password", user.password).eq("status", true).findList();
		if (CollectionUtils.size(users) > 0) {
			return users.get(0);
		}
		return null;
	}

	// STATUS: LOCKED (Please don't change)
	public static User loginJson(User user) {
		List<User> users = Ebean.find(User.class).select("id, username, realname, usertype, status").fetch("shops", "id, code, name").where().eq("username", user.username)
				.in("shop.id", user.shopId).eq("status", true).findList();
		if (CollectionUtils.size(users) > 0) {
			return users.get(0);
		}
		return null;
	}

	public static User login(User user) {
		List<User> users = Ebean.find(User.class).select("id, username, realname, usertype, status").fetch("shops", "id").where().eq("username", user.username).eq("status", true)
				.findList();
		if (CollectionUtils.size(users) > 0) {
			return users.get(0);
		}
		return null;
	}

	// STATUS: LOCKED (Please don't change)
	public static User loginAdminJson(User user) {
		List<User> users = Ebean.find(User.class).select("id, username, realname, usertype, status").fetch("shops", "id").where().eq("username", user.username)
				.eq("password", user.password).eq("status", true).findList();
		if (CollectionUtils.size(users) > 0) {
			return users.get(0);
		}
		return null;
	}

	public static Pagination search(String queryName, Pagination pagination) {
		pagination = pagination == null ? new Pagination() : pagination;
		ExpressionList expList = Ebean.find(User.class).where();
		if (StringUtils.isNotEmpty(queryName)) {
			queryName = StringUtils.trimToNull(queryName);
			expList.where().ilike("realname", "%" + queryName + "%");
		}
		PagingList<User> pagingList = expList.findPagingList(pagination.pageSize);
		pagingList.setFetchAhead(false);
		Page page = pagingList.getPage(pagination.currentPage);
		pagination.recordList = page.getList();
		pagination.pageCount = page.getTotalPageCount();
		pagination.recordCount = page.getTotalRowCount();
		return pagination;
	}

	public static Pagination search(Map search, Pagination pagination) {
		pagination = pagination == null ? new Pagination() : pagination;
		ExpressionList expList = Ebean.find(User.class).where();
		if (search.keySet() != null) {
			Iterator searchKeys = search.keySet().iterator();
			while (searchKeys.hasNext()) {
				String key = (String) searchKeys.next();
				String value = (String) search.get(key);
				play.Logger.info("Value " + value);
				if (StringUtils.isEmpty(value)) {
					continue;
				}
				expList.where().eq(key, value);
			}
		}
		expList.select("realname");
		PagingList<User> pagingList = expList.findPagingList(pagination.pageSize);
		pagingList.setFetchAhead(false);
		Page page = pagingList.getPage(pagination.currentPage);
		pagination.recordList = page.getList();
		pagination.pageCount = page.getTotalPageCount();
		pagination.recordCount = page.getTotalRowCount();
		return pagination;
	}

	public static User view(Long id) {
		if (id != null) {
			return Ebean.find(User.class, id);
		}
		return null;
	}

	@Transactional
	public static void store(User user) {
		if (user.id != null && user.id > 0) {
			User updateUser = Ebean.find(User.class, user.id);
			try {
				updateUser.userIp = user.userIp;
				updateUser.userMac = user.userMac;
				updateUser.password = user.password;
				updateUser.lastLoginDate = new Date();
				logger.info("[System]-[Info]-[User({}) Login, IP is {}, Mac is {}]", new Object[] { updateUser.username, updateUser.userIp, updateUser.userMac });
				updateUser.modifiedDate = new Date();
			} catch (Exception e) {
				logger.error("Store User Error", e);
			}
			Ebean.update(updateUser);
		} else {
			Ebean.save(user);
		}
	}

	@Transactional
	public static boolean delete(Long id) {
		User user = Ebean.find(User.class, id);
		user.status = false;
		user.modifiedDate = new Date();
		Ebean.update(user);
		return true;
	}

}

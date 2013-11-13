package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Required;
import utils.MyPropertiesUtils;
import utils.Pagination;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import com.avaje.ebean.annotation.Transactional;

@Entity
@Table(name = "tb_user")
public class User {

	final static Logger logger = LoggerFactory.getLogger(User.class);

	@Id
	public Long id;

	@Required(message = "Username cannot be empty")
	public String username;

	@Required(message = "Password cannot be empty")
	public String password;

	public String realname;

	@Required(message = "User type cannot be empty")
	public String usertype;

	@Required(message = "Status cannot be empty")
	public Boolean status;

	public String userIp;

	public String userMac;

	public String createBy, modifiedBy;

	public Date createDate, modifiedDate, lastLoginDate;

	@ManyToOne
	@JoinColumn(name = "shop_id", referencedColumnName = "id")
	public Shop shop;

	/* the following are service methods */
	public static User login(User user) {
		List<User> users = Ebean.find(User.class).select("id, username, realname, usertype, status")
				.fetch("shop", "id").where().eq("username", user.username).eq("password", user.password)
				.eq("status", true).findList();
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
				updateUser.lastLoginDate = new Date();
				logger.info("[System]-[Info]-[User({}) Login, IP is {}, Mac is {}]", new Object[] {
						updateUser.username, updateUser.userIp, updateUser.userMac });
				updateUser.modifiedDate = new Date();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Ebean.update(updateUser);
		} else {
			Ebean.save(user);
		}
	}

	public static void store2(User user) {
		if (user.id != null && user.id > 0) {
			try {
				User user2 = Ebean.find(User.class, 1L);
				MyPropertiesUtils.copyProperties(user2, user);
				System.out.println("store2 ->" + user2.username);
				System.out.println("store2 ->" + user2.id);
				System.out.println("store2 ->" + user2.realname);
				System.out.println("store2 ->" + user2.createDate);
				System.out.println("store2 ->" + user2.password);
				System.out.println("store2 ->" + user2.userIp);
				System.out.println("store2 ->" + user2.userMac);
				user2.lastLoginDate = new Date();
				logger.info("[System]-[Info]-[Update User({}) IP is {}, Mac is {}]", new Object[] { user2.username,
						user2.userIp, user2.userMac });
				user2.modifiedDate = new Date();
				Ebean.update(user2);
			} catch (Exception e) {
				e.printStackTrace();
			}
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

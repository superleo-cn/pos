package models;

import java.util.*;

import javax.persistence.*;

import com.avaje.ebean.*;
import com.avaje.ebean.Query;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import play.data.validation.Required;
import utils.Pagination;

import constants.Constants;

@Entity
@Table(name = "tb_audit")
public class Audit {
	@Id
	public Long id;

    @Transient
    public Long no;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	public User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "shop_id", referencedColumnName = "id")
	public Shop shop;


	public String createBy, modifiedBy;

    public String action;

	public Date createDate, modifiedDate;

	@Transient
	public Long androidId;

    @Transient
    public String shopName,realName;



    /* the following are service methods */
	public static Pagination search(String queryName, Pagination pagination) {
		pagination = pagination == null ? new Pagination() : pagination;
		ExpressionList expList = Ebean.find(Audit.class).where();
		if (StringUtils.isNotEmpty(queryName)) {
			queryName = StringUtils.trimToNull(queryName);
			expList.where().ilike("action", "%" + queryName + "%");
		}
		PagingList<Audit> pagingList = expList.findPagingList(pagination.pageSize);
		pagingList.setFetchAhead(false);
		Page page = pagingList.getPage(pagination.currentPage);
		pagination.recordList = page.getList();
		pagination.pageCount = page.getTotalPageCount();
		pagination.recordCount = page.getTotalRowCount();
		return pagination;
	}

	public static Audit view(Long id) {
		if (id != null) {
			return Ebean.find(Audit.class, id);
		}
		return null;
	}

	public static boolean store(Audit audit) {
		try {
			audit.createDate = new Date();
			Ebean.save(audit);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean delete(Long id) {
		Integer flag = Ebean.delete(Audit.class, id);
		return (flag > 0) ? true : false;
	}

	public static List<Audit> listByShop(Long id) {
		if (id != null) {
			return null;
		}
		return null;
	}


    /* the following are service methods */
    public static Pagination search(Map search, Pagination pagination) {
        pagination = pagination == null ? new Pagination() : pagination;

        Query query  = Ebean.find(Audit.class).fetch("user","realname").fetch("shop","name");

        ExpressionList expList = query.where();
        if (search.keySet()!=null) {
            Iterator searchKeys = search.keySet().iterator();
            while(searchKeys.hasNext()){
                String key = (String) searchKeys.next();
                String value = (String) search.get(key);
                play.Logger.info("Value " + value);
                if(StringUtils.isEmpty(value)) continue;

                if(key.equalsIgnoreCase("dateFrom")) {
                    expList.where().gt("createDate", value);
                }
                else if(key.equalsIgnoreCase("dateTo")) {
                    expList.where().lt("createDate", value);
                }
                else {
                    expList.where().ilike(key, "%" + value+ "%");
                }
            }
        }
        List<Audit> list = new ArrayList<Audit>();
        if(!pagination.all)
        {
            PagingList<Audit> pagingList = expList.findPagingList(pagination.pageSize);
            pagingList.setFetchAhead(false);
            Page page = pagingList.getPage(pagination.currentPage-1);

            list = page.getList();

            pagination.iTotalDisplayRecords = expList.findRowCount();
            pagination.iTotalRecords = expList.findRowCount();

        }
        else {
            pagination.currentPage = 1;
            list = expList.findList();
        }

        if(list!=null) {
            Long no = ((pagination.currentPage-1)*10)+1l;
            for(Audit report:list) {
                report.no = no;
                report.realName=report.user.realname;
                report.shopName =report.shop.name;
                no++;
            }
        }

        pagination.recordList = list;
        return pagination;
    }

}

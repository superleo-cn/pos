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
@Table(name = "tb_version")
public class Version {
	@Id
	public Long id;
	
	public String name;
	
	public String versionSn;

	public Long versionNo;

	public String createBy, modifiedBy;

	public String description;

	public Date createDate, modifiedDate;

	public static Version view(Long id) {
		if (id != null) {
			return Ebean.find(Version.class, id);
		}
		return null;
	}

	public static List<Version> getLastVersion() {
		return Ebean.find(Version.class).order("id desc").setMaxRows(1).findList();
	}

	public static boolean store(Version version) {
		try {
			version.createDate = new Date();
			Ebean.save(version);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean delete(Long id) {
		Integer flag = Ebean.delete(Version.class, id);
		return (flag > 0) ? true : false;
	}

}

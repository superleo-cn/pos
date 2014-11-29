package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avaje.ebean.Ebean;

@Entity
@Table(name = "tb_version")
public class Version {

	final static Logger logger = LoggerFactory.getLogger(Version.class);

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
			logger.error("Store Version Error", e);
		}
		return false;
	}

	public static boolean delete(Long id) {
		Integer flag = Ebean.delete(Version.class, id);
		return (flag > 0) ? true : false;
	}

}

package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.Required;

import com.avaje.ebean.Ebean;

@Entity
@Table(name = "tb_attribute")
public class Attribute {
	@Id
	public Long id;

	@Required(message = "Attribute name cannot be empty")
	public String name;

	@Required(message = "Attribute chinese name cannot be empty")
	public String nameZh;

	@Required(message = "Attribute code cannot be empty")
	public String code;

	@Required(message = "Status cannot be empty")
	public Boolean status;

	public Integer position;

	@ManyToOne
	@JoinColumn(name = "food_id", referencedColumnName = "id")
	public Food food;

	public String createBy, modifiedBy;

	public Date createDate, modifiedDate;

	public static List<Attribute> listByShop(Long id) {
		return Ebean.find(Attribute.class).select("id, name, nameZh, code, status, position").fetch("food", "id")
				.where().eq("food.shop.id", id).eq("status", true).order("position").findList();
	}

	public static Attribute getById(Long id) {
		return Ebean.find(Attribute.class).select("id").where().eq("id", id).eq("status", true).findUnique();
	}

}

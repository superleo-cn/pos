package models;

import java.util.Date;

import javax.persistence.Entity;

@Entity
public class ReportQuantity {

	public Long id;
	public String shopName;
	public String label, foodNameZh;
	public Long value;
	public Date orderDate;
	public String orderDateStr;

}

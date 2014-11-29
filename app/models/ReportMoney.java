package models;

import java.util.Date;

import javax.persistence.Entity;


@Entity
public class ReportMoney {

	public Long id;
	public String shopName;
	public String label, foodNameZh, orderHour;
	public Double value;
	public Date orderDate;
	public String orderDateStr;
	public String orderMonth;
}

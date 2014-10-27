package models;

import java.util.Date;

import javax.persistence.Entity;

/**
 * Created with IntelliJ IDEA. User: lala Date: 11/15/13 Time: 1:55 AM To change
 * this template use File | Settings | File Templates.
 */
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

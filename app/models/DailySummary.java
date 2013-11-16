package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.Pagination;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import com.avaje.ebean.annotation.Transactional;

import constants.Constants;

@Entity
@Table(name = "tb_daily_summary")
public class DailySummary {

	final static Logger logger = LoggerFactory.getLogger(DailySummary.class);
	@Id
	public Long id;

	@ManyToOne
	@JoinColumn(name = "shop_id", referencedColumnName = "id")
	public Shop shop;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	public User user;

	public Float aOpenBalance;

	public Float bExpenses;

	public Float cCashCollected;

	public Float dDailyTurnover;

	public Float eNextOpenBalance;

	public Float fBringBackCash;

	public Float gTotalBalance;

	public String middleCalculateTime;

	public String middleCalculateBalance;

	public String calculateTime;

	public String courier;

	public String others;

	public String createBy, modifiedBy;

	public Date createDate, modifiedDate;

}

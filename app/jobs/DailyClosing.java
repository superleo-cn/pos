package jobs;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mail.DailyClosingMailer;
import models.Dashboard;
import models.ReportMoney;
import models.Shop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.jobs.Job;
import play.jobs.On;

/** Fire at 00:10 (noon) every day **/
// @OnApplicationStart
@On("0 22 1 * * ?")
public class DailyClosing extends Job {

	final static Logger logger = LoggerFactory.getLogger(DailyClosing.class);

	public void doJob() {
		getDailyEmail();
		logger.info("complete daily report sending history.");
	}

	public static void getDailyEmail() {
		Map searchs = new HashMap();
		DecimalFormat df = new DecimalFormat("0.00");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		Date yesterday = c.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(yesterday);
		// searchs.put("date", "2014-10-18");
		searchs.put("date", date);
		List<Shop> shops = Shop.list();
		if (shops != null) {
			for (Shop s : shops) {
				searchs.put("shopName", s.name);
				List<ReportMoney> list = Dashboard.dailyClosingSummary(searchs);
				if (list != null && list.size() > 0) {
					ReportMoney money = list.get(0);
					DailyClosingMailer.send(date, s.name, s.email, df.format(money.value));
					if(s.sendSms != null && s.sendSms){
						DailyClosingMailer.sendSMS(date, s.name, s.contact, df.format(money.value));
					}
				}
			}
		}
	}
}
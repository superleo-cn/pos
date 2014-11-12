package mail;

import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.libs.Mail;
import utils.SmsUtil;

public class DailyClosingMailer {

	public static final String DAILY_SUM_TITLE = "[%s] Daily Transaction Summary";
	public static final String DAILY_SUM_INFO = "On [%s], you totally collect is [$%s].";
	public static final String RECEIVE_DAILY_SUM_EMAIL = "lihui@weebo.com.sg";

	final static Logger logger = LoggerFactory.getLogger(DailyClosingMailer.class);

	public static void send(String date, String shopName, String sendTo, String result) {
		try {
			HtmlEmail email = new HtmlEmail();
			String sendFrom = Play.configuration.getProperty("mail.smtp.user");
			email.addTo(sendTo);
			email.addBcc(RECEIVE_DAILY_SUM_EMAIL);
			email.setFrom(sendFrom, "Weebo");
			email.setSubject(String.format(DAILY_SUM_TITLE, shopName));
			email.setTextMsg(String.format(DAILY_SUM_INFO, date, result));
			Mail.send(email);
		} catch (Exception me) {
			logger.error("Error", me);
		}
	}
	
	public static void sendSMS(String date, String shopName, String mobileNo, String result) {
		try {
			String subject = String.format(DAILY_SUM_TITLE, shopName);
			String info = String.format(DAILY_SUM_INFO, date, result);
			String sMsg = subject + "\n" + info;
			SmsUtil.sendMsg(mobileNo, sMsg);
		} catch (Exception me) {
			logger.error("Error", me);
		}
	}
}

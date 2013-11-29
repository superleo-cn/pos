import java.util.Date;

import models.User;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import utils.MyPropertiesUtils;

public class UtilTest {

	@Test
	public void testPropertyBeanUtils() throws Exception {
		String userId = "admin";
		String shopId = "123";
		String userIp = "192.168.1.100";
		String userMac = "";// "b2:00:1f:2a:e3:c0";
		// Http.Request req = newRequest();
		User user = new User();
		user.realname = "123";
		User user2 = new User();
		PropertyUtils.copyProperties(user2, user);
		System.out.println(user2.realname);
	}

	@Test
	public void testPropertyBeanUtils2() throws Exception {
		// Http.Request req = newRequest();
		User user = new User();
		user.username = "123";
		user.id = 1L;
		user.realname = "111";
		user.createDate = new Date();
		user.password = "password";
		User user2 = new User();
		user2.createDate = null;
		user2.password = "qqq";
		java.util.Date defaultValue = null;
		DateConverter converter = new DateConverter(defaultValue);
		ConvertUtils.register(converter, java.util.Date.class);

		MyPropertiesUtils.copyProperties(user, user2);
		System.out.println(user.username);
		System.out.println(user.id);
		System.out.println(user.realname);
		System.out.println(user.createDate);
		System.out.println(user.password);
	}
	
	@Test
	public void testStrings() throws Exception {
		String placeholder = "%-20s%-20s%-10s?";
		String formatted = String.format(placeholder, "Ivan", "Scala", "10000000");
		System.out.println(formatted);
		formatted = String.format(placeholder, "I m superman ", "What you want from", "2000");
		System.out.println(formatted);
		
	}

	
	@Test
	public void testDouble() throws Exception {
		String placeholder = "2001.0912";
		placeholder = StringUtils.substring(placeholder, 0, placeholder.indexOf(".") + 3);
		System.out.println(placeholder);
		
	}
}
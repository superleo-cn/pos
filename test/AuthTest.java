import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import models.User;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class AuthTest extends FunctionalTest {

	@Test(timeout = 400000)
	public void testLogin() {
		String userId = "admin";
		String shopId = "123";
		String userIp = "192.168.1.100";
		String userMac = "";// "b2:00:1f:2a:e3:c0";
		// Http.Request req = newRequest();
		Map<String, String> params = new HashMap<>();
		params.put("user.username", userId);
		params.put("user.password", shopId);
		params.put("user.userIp", userIp);
		params.put("user.userMac", userMac);
		Response response = POST("http://ec2-54-254-145-129.ap-southeast-1.compute.amazonaws.com:8080/loginJson",
				params);
		assertIsOk(response);
		try {
			response.out.writeTo(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
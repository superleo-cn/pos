import java.util.HashMap;
import java.util.Map;

import org.junit.*;

import play.Logger;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;

public class AuthTest extends FunctionalTest {

	@Test
	public void testLogin() {
		String userId = "admin";
		String shopId = "123";
		String userIp = "192.168.1.100";
		String userMac = "b2:00:1f:2a:e3:c0";
		// Http.Request req = newRequest();
		Map<String, String> params = new HashMap<>();
		params.put("user.username", userId);
		params.put("user.password", shopId);
		params.put("user.userIp", userIp);
		params.put("user.userMac", userMac);
		Response response = POST("/loginJson", params);
		assertIsOk(response);
		// assertContentType("text/html", response);
		// assertCharset(play.Play.defaultWebEncoding, response);

	}

}
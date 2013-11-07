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
		String userId = "2";
		String shopId = "1";
		// Http.Request req = newRequest();
		Map<String, String> params = new HashMap<>();
		params.put("user.username", userId);
		params.put("user.password", shopId);
		Response response = POST("/loginJson", params);
		assertIsOk(response);
		// assertContentType("text/html", response);
		// assertCharset(play.Play.defaultWebEncoding, response);

	}

}
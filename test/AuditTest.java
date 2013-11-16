import java.util.HashMap;
import java.util.Map;

import models.User;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class AuditTest extends FunctionalTest {

	@Test(timeout = 400000)
	public void testSubmitAudit() {

		try {
			Map<String, String> params = new HashMap<>();
			params.put("audits[0].androidId", "0");
			params.put("audits[0].user.id", "1");
			params.put("audits[0].shop.id", "1");
			params.put("audits[0].action", "Login");

			params.put("audits[1].androidId", "2");
			params.put("audits[1].user.id", "abc");
			params.put("audits[1].shop.id", "1");
			params.put("audits[1].action", "Logout");

			Response response = POST("/audits/submit", params);
			assertIsOk(response);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
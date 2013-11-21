import java.util.HashMap;
import java.util.Map;

import models.User;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class VersionTest extends FunctionalTest {

	@Test(timeout = 400000)
	public void testSubmitAudit() {

		try {
			Response response = POST("/versions/update");
			assertIsOk(response);
			response.out.writeTo(System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
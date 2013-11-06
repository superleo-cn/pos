import java.util.HashMap;
import java.util.Map;

import org.junit.*;

import play.Logger;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;

public class ConsumeTransactionTest extends FunctionalTest {

	@Test
	public void testConsumeTransactionStore() {
		String userId = "2";
		String shopId = "1";
		String consumptionId = "1";
		String pirce = "100.1";
		// Http.Request req = newRequest();
		Map<String, String> params = new HashMap<>();
		params.put("consumeTransaction.user.id", userId);
		params.put("consumeTransaction.shop.id", shopId);
		params.put("consumeTransaction.consumption.id", consumptionId);
		params.put("consumeTransaction.price", pirce);

		Response response = POST("/consumeTransactions/store", params);
		assertIsOk(response);
		// assertContentType("text/html", response);
		// assertCharset(play.Play.defaultWebEncoding, response);

	}

}
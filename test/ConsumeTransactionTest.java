import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Cash;
import models.Consumption;

import org.junit.Test;

import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class ConsumeTransactionTest extends FunctionalTest {

	@Test
	public void testConsumeTransactionStore() {
		String userId = "2";
		String shopId = "1";
		// Http.Request req = newRequest();
		List<Consumption> list = Consumption.listByShop(Long.parseLong(shopId));
		Map<String, String> params = new HashMap<>();
		for (int i = 0; i < list.size(); i++) {
			Consumption consumption = list.get(i);
			params.put("consumeTransactions[" + i + "].user.id", userId);
			params.put("consumeTransactions[" + i + "].shop.id", shopId);
			params.put("consumeTransactions[" + i + "].consumption.id", String.valueOf(consumption.id));
			params.put("consumeTransactions[" + i + "].price", String.valueOf(i + 1));
		}

		Response response = POST("/consumeTransactions/submit", params);
		assertIsOk(response);
		// assertContentType("text/html", response);
		// assertCharset(play.Play.defaultWebEncoding, response);

	}

}
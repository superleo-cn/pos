import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Cash;

import org.junit.Test;

import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class CashTransactionTest extends FunctionalTest {

	@Test
	public void testCashTransactionStore() {
		String userId = "2";
		String shopId = "1";
		// Http.Request req = newRequest();
		List<Cash> list = Cash.listByShop(Long.parseLong(shopId));
		Map<String, String> params = new HashMap<>();
		for (int i = 0; i < list.size(); i++) {
			Cash cash = list.get(i);
			params.put("cashTransactions[" + i + "].user.id", userId);
			params.put("cashTransactions[" + i + "].shop.id", shopId);
			params.put("cashTransactions[" + i + "].cash.id", String.valueOf(cash.id));
			params.put("cashTransactions[" + i + "].quantity", String.valueOf(i + 1));
		}

		Response response = POST("/cashTransactions/submit", params);
		assertIsOk(response);
		// assertContentType("text/html", response);
		// assertCharset(play.Play.defaultWebEncoding, response);

	}

}
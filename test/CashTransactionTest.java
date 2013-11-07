import java.util.HashMap;
import java.util.Map;

import org.junit.*;

import play.Logger;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;

public class CashTransactionTest extends FunctionalTest {

	@Test
	public void testCashTransactionStore() {
		String userId = "2";
		String shopId = "1";
		String cashId = "2";
		String qunatity = "13";
		// Http.Request req = newRequest();
		Map<String, String> params = new HashMap<>();
		params.put("cashTransaction.user.id", userId);
		params.put("cashTransaction.shop.id", shopId);
		params.put("cashTransaction.cash.id", cashId);
		params.put("cashTransaction.quantity", qunatity);

		Response response = POST("/cashTransactions/store", params);
		assertIsOk(response);
		// assertContentType("text/html", response);
		// assertCharset(play.Play.defaultWebEncoding, response);

	}

}
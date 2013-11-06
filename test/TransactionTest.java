import java.util.HashMap;
import java.util.Map;

import org.junit.*;

import play.Logger;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;

public class TransactionTest extends FunctionalTest {

	@Test
	public void testTransactionStore() {
		String userId = "2";
		String shopId = "1";
		String foodId = "1";
		String quantity = "10";
		// Http.Request req = newRequest();
		Map<String, String> params = new HashMap<>();
		params.put("transaction.user.id", userId);
		params.put("transaction.shop.id", shopId);
		params.put("transaction.quantity", quantity);
		params.put("transaction.food.id", foodId);

		Response response = POST("/transactions/store", params);
		assertIsOk(response);
		// assertContentType("text/html", response);
		// assertCharset(play.Play.defaultWebEncoding, response);

	}

}
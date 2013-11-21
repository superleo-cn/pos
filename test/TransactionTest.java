import java.io.IOException;
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
		try {
			String userId = "2";
			String shopId = "1";
			String foodId = "1";
			String quantity = "10";
			String discount = "2";
			String totalPackage = "2";
			String totalRetailPrice = "5";
			String foc = "1";
			// Http.Request req = newRequest();
			Map<String, String> params = new HashMap<>();
			params.put("transaction.user.id", userId);
			params.put("transaction.shop.id", shopId);
			params.put("transaction.quantity", quantity);
			params.put("transaction.food.id", foodId);
			params.put("transaction.totalDiscount", discount);
			params.put("transaction.totalRetailPrice", totalRetailPrice);
			params.put("transaction.totalPackage", totalPackage);
			params.put("transaction.freeOfCharge", foc);

			Response response = POST("/transactions/store2", params);
			assertIsOk(response);
			response.out.writeTo(System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testTransactionSubmit() {
		try {
			String userId = "2";
			String shopId = "1";
			String foodId = "1";
			String quantity = "10";
			String discount = "2";
			String totalPackage = "2.2";
			String totalRetailPrice = "5.8";
			String foc = "1";
			// Http.Request req = newRequest();
			Map<String, String> params = new HashMap<>();
			for (int i = 0; i < 2; i++) {
				params.put("transactions[" + i + "].androidId", "123");
				params.put("transactions[" + i + "].user.id", userId);
				params.put("transactions[" + i + "].shop.id", shopId);
				params.put("transactions[" + i + "].quantity", quantity);
				params.put("transactions[" + i + "].food.id", foodId);
				params.put("transactions[" + i + "].totalDiscount", discount);
				params.put("transactions[" + i + "].totalRetailPrice", totalRetailPrice);
				params.put("transactions[" + i + "].totalPackage", totalPackage);
				params.put("transactions[" + i + "].freeOfCharge", foc);
			}

			Response response = POST("/transactions/submit", params);
			assertIsOk(response);
			response.out.writeTo(System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
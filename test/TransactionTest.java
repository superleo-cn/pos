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
			params.put("transactions[0].user.id", userId);
			params.put("transactions[0].shop.id", shopId);
			params.put("transactions[0].quantity", quantity);
			params.put("transactions[0].food.id", foodId);
			params.put("transactions[0].totalDiscount", discount);
			params.put("transactions[0].totalRetailPrice", totalRetailPrice);
			params.put("transactions[0].totalPackage", totalPackage);
			params.put("transactions[0].freeOfCharge", foc);
			params.put("transactions[0].orderDate", "2013-12-12 20:01:30");

			Response response = POST("/transactions/store", params);
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
			for (int i = 0; i < 1; i++) {
				params.put("transactions[" + i + "].androidId", "123");
				params.put("transactions[" + i + "].user.id", userId);
				params.put("transactions[" + i + "].shop.id", shopId);
				params.put("transactions[" + i + "].quantity", quantity);
				params.put("transactions[" + i + "].food.id", foodId);
				params.put("transactions[" + i + "].totalDiscount", discount);
				params.put("transactions[" + i + "].totalRetailPrice", totalRetailPrice);
				params.put("transactions[" + i + "].totalPackage", totalPackage);
				params.put("transactions[" + i + "].freeOfCharge", foc);
				params.put("transactions[" + i + "].orderDate", "2013-12-12 20:01:30");
			}

			Response response = POST("/transactions/submit", params);
			assertIsOk(response);
			response.out.writeTo(System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testTransactionWithAttrSubmit() {
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
			for (int i = 0; i < 1; i++) {
				params.put("transactions[" + i + "].androidId", "123");
				params.put("transactions[" + i + "].transactionId", "123");
				params.put("transactions[" + i + "].user.id", userId);
				params.put("transactions[" + i + "].shop.id", shopId);
				params.put("transactions[" + i + "].quantity", quantity);
				params.put("transactions[" + i + "].food.id", foodId);
				params.put("transactions[" + i + "].totalDiscount", discount);
				params.put("transactions[" + i + "].totalRetailPrice", totalRetailPrice);
				params.put("transactions[" + i + "].totalPackage", totalPackage);
				params.put("transactions[" + i + "].freeOfCharge", foc);
				params.put("transactions[" + i + "].attributeIds", "1,2");
				params.put("transactions[" + i + "].orderDate", "2013-12-12 20:01:30");
				
			}

			Response response = POST("/transactions/submitWithAttrs", params);
			assertIsOk(response);
			response.out.writeTo(System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import models.DailySummary;

import org.junit.Test;

import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class DailySummarysTest extends FunctionalTest {

	@Test
	public void testDailySummarysStore() {
		String userId = "2";
		String shopId = "1";
		Map<String, String> params = new HashMap<>();
		params.put("dailySummary.androidId", "0");
		params.put("dailySummary.user.id", userId);
		params.put("dailySummary.shop.id", shopId);
		params.put("dailySummary.aOpenBalance", "1");
		params.put("dailySummary.bExpenses", "2");
		params.put("dailySummary.cCashCollected", "3");
		params.put("dailySummary.dDailyTurnover", "4");
		params.put("dailySummary.eNextOpenBalance", "5");
		params.put("dailySummary.fBringBackCash", "6");
		params.put("dailySummary.gTotalBalance", "7");
		params.put("dailySummary.middleCalculateTime", "8");
		params.put("dailySummary.middleCalculateBalance", "9");
		params.put("dailySummary.calculateTime", "10");
		params.put("dailySummary.courier", "11");
		params.put("dailySummary.others", "12");

		Response response = POST("/dailySummarys/submit", params);
		assertIsOk(response);
		try {
			response.out.writeTo(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
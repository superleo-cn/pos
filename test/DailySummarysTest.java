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
		params.put("dailySummary.aOpenBalance", "0");
		params.put("dailySummary.bExpenses", "0");
		params.put("dailySummary.cCashCollected", "0");
		params.put("dailySummary.dDailyTurnover", "0");
		params.put("dailySummary.eNextOpenBalance", "0");
		params.put("dailySummary.fBringBackCash", "0");
		params.put("dailySummary.gTotalBalance", "0");
		params.put("dailySummary.middleCalculateTime", "0");
		params.put("dailySummary.middleCalculateBalance", "0");
		params.put("dailySummary.calculateTime", "0");
		params.put("dailySummary.courier", "0");
		params.put("dailySummary.others", "0");

		Response response = POST("/dailySummarys/submit", params);
		assertIsOk(response);
		// assertContentType("text/html", response);
		// assertCharset(play.Play.defaultWebEncoding, response);

	}

}
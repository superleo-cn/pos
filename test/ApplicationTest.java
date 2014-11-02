import jobs.DailyClosing;
import jobs.LogCleaner;

import org.junit.Test;

import play.test.FunctionalTest;

public class ApplicationTest extends FunctionalTest {
	@Test(timeout = 400000)
	public void testLogCleanerJob() {
		LogCleaner boot = new LogCleaner();
		boot.doJob();
	}

	@Test(timeout = 200000)
	public void testDailyClosingJob() {
		DailyClosing boot = new DailyClosing();
		boot.doJob();
	}
}
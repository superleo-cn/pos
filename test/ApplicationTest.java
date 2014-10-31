import jobs.Bootstrap;

import org.junit.Test;

import play.test.FunctionalTest;

public class ApplicationTest extends FunctionalTest {
	@Test(timeout = 400000)
	public void testSubmitAudit() {
		Bootstrap boot = new Bootstrap();
		boot.doJob();
	}
}
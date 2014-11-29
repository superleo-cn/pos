package jobs;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.jobs.Job;
import play.jobs.On;

/** Fire at 12pm (noon) every day **/
@On("0 0 2 * * ?")
public class LogCleaner extends Job {

	final static Logger logger = LoggerFactory.getLogger(LogCleaner.class);

	public void doJob() {
		logger.info("Start to clean log history.");
		String projectRoot = Play.applicationPath.getAbsolutePath();
		String logs = projectRoot + File.separator + "logs";
		logger.info("Log path = " + logs);
		File directory = new File(logs);
		Date deleteDate = null;
		// get delete date 14 days ago
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -14);
		deleteDate = c.getTime();
		// delete logs which over 14 days
		if (directory != null && directory.exists()) {
			File[] files = directory.listFiles();
			for (File file : files) {
				Date fileDate = fileDate(file);
				if (deleteDate.after(fileDate)) {
					if (file.delete()) {
						logger.info("file [" + file.getName() + "] deleted");
					} else {
						logger.warn("file [" + file.getName() + "] deleted failed");
					}
				}
			}
		}
		logger.info("complete log history.");
	}

	static Date fileDate(String pathStr) {
		return new Date(new File(pathStr).lastModified());
	}

	static Date fileDate(File file) {
		return new Date(file.lastModified());
	}

}
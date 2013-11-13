package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public class MyBeanUtils {
	
	final static Logger logger = LoggerFactory.getLogger(MyBeanUtils.class);

	public static void copyProperties(Object destBean, Object srcBean) {
		BeanUtils.copyProperties(srcBean, destBean);
	}

}

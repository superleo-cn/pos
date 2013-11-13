package utils;

import java.beans.PropertyDescriptor;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.Auth;

public class MyPropertiesUtils {
	
	final static Logger logger = LoggerFactory.getLogger(MyPropertiesUtils.class);

	public static void copyProperties(Object destBean, Object srcBean) {
		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(srcBean);
		if (descriptors != null) {
			logger.info("[System]-[Info]-[User fields size is {}]", new Object[] { descriptors.length });
			for (PropertyDescriptor descriptor : descriptors) {
				try {
					String propertyName = descriptor.getName();
					Object val = PropertyUtils.getProperty(srcBean, propertyName);
					logger.info("[System]-[Info]-[User field({}) value({})]", new Object[] { propertyName, val });
					if (val != null) {
						PropertyUtils.setProperty(destBean, propertyName, val);
					}
				} catch (Exception ignore) {
					// not interested in what we can't read or write
				}
			}
		}
	}

}

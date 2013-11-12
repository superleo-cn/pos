package utils;

import java.beans.PropertyDescriptor;

import org.apache.commons.beanutils.PropertyUtils;

public class MyPropertiesUtils {

	public static void copyProperties(Object destBean, Object srcBean) {
		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(srcBean);
		if (descriptors != null) {
			for (PropertyDescriptor descriptor : descriptors) {
				try {
					String propertyName = descriptor.getName();
					Object val = PropertyUtils.getProperty(srcBean, propertyName);
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

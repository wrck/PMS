package com.dp.plat.pms.filter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ReflectionUtils;

@PropertySource("classpath:system.properties")
public class ExcludeAdminControllerTypeFilter implements TypeFilter {

	@Value("${sys.admin.exclude}")
    private boolean exclude;
	
	@Value("${sys.admin.exclude.filter.regex}")
    private String regex;
	
	private final Pattern pattern;
	
    public ExcludeAdminControllerTypeFilter() {
		super();
		
		try {
			Properties loadAllProperties = PropertiesLoaderUtils.loadAllProperties("system.properties");
            exclude = Boolean.parseBoolean(loadAllProperties.getProperty("sys.admin.exclude"));
            regex = loadAllProperties.getProperty("sys.admin.exclude.filter.regex");
        } catch (IOException e) {
            e.printStackTrace();
        }
		this.pattern = Pattern.compile(regex);
	}

	@Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
        // 可以通过MetadataReader获得各种信息，然后根据自己的需求返回boolean
		return this.exclude && this.pattern.matcher(metadataReader.getClassMetadata().getClassName()).matches();
    }

}
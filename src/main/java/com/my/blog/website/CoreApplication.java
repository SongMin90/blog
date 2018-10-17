package com.my.blog.website;

import com.alibaba.druid.pool.DruidDataSource;
import com.my.blog.website.constant.WebConst;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;

@MapperScan("com.my.blog.website.dao")
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@Configuration
public class CoreApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder builder) {
		return builder.sources(this.getClass());
	}

	@Bean(initMethod = "init", destroyMethod = "close")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return new DruidDataSource();
	}

	@Bean
	public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());
		sqlSessionFactoryBean.setMapperLocations(resolver
				.getResources("classpath*:/mapper/*Mapper.xml"));
		return sqlSessionFactoryBean.getObject();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	/**
	 * 文件上传配置
	 * @return
	 */
	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		// 文件最大 KB,MB
		factory.setMaxFileSize(WebConst.MAX_FILE_SIZE + "MB");
		// 设置总上传数据总大小
		factory.setMaxRequestSize(WebConst.MAX_FILE_SIZE + "MB");
		return factory.createMultipartConfig();
	}

	/**
	 * 配置一个TomcatEmbeddedServletContainerFactory bean
	 *
	 * @return
	 */
	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
			@Override
			protected void postProcessContext(Context context) {
				SecurityConstraint constraint = new SecurityConstraint();
				constraint.setUserConstraint("CONFIDENTIAL");
				SecurityCollection collection = new SecurityCollection();
				collection.addPattern("/*");
				constraint.addCollection(collection);
				context.addConstraint(constraint);
			}
		};
		tomcat.addAdditionalTomcatConnectors(httpConnector());
		return tomcat;
	}

	/**
	 * 让我们的应用支持HTTP是个好想法，但是需要重定向到HTTPS，
	 * 但是不能同时在application.properties中同时配置两个connector， 所以要以编程的方式配置HTTP
	 * connector，然后重定向到HTTPS connector
	 *
	 * @return Connector
	 */
	@Bean
	public Connector httpConnector() {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setScheme("http");
		//Connector监听的http的端口号
		connector.setPort(80);
		connector.setSecure(false);
		//监听到http的端口号后转向到的https的端口号
		connector.setRedirectPort(443);
		return connector;
	}

	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}

}

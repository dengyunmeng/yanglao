package com.elderlycare.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DruidConfig {

    @Bean
    @ConditionalOnBean(DruidDataSource.class)
    public ServletRegistrationBean<StatViewServlet> statViewServlet(DataSource dataSource) {
        if (!(dataSource instanceof DruidDataSource)) {
            return null;
        }
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        bean.addInitParameter("loginUsername", "admin");
        bean.addInitParameter("loginPassword", "admin123");
        bean.addInitParameter("allow", "");
        return bean;
    }

    @Bean
    @ConditionalOnBean(DruidDataSource.class)
    public FilterRegistrationBean<WebStatFilter> webStatFilter(DataSource dataSource) {
        if (!(dataSource instanceof DruidDataSource)) {
            return null;
        }
        FilterRegistrationBean<WebStatFilter> bean = new FilterRegistrationBean<>(new WebStatFilter());
        bean.addUrlPatterns("/*");
        bean.addInitParameter("exclusions", "*.js,*.css,*.gif,*.jpg,*.png,*.ico,/druid/*");
        return bean;
    }
}

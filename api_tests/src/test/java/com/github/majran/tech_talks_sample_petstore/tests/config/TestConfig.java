package com.github.majran.tech_talks_sample_petstore.tests.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@Configuration
@ComponentScan
@PropertySource({"classpath:test-${env:dev}.properties", "classpath:test.properties", "classpath:allure.properties",})
public class TestConfig {

}

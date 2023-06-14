package com.github.majran.tech_talks_sample_petstore.tests.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class ReqSpec {
    @Value("${baseUrl}")
    private String baseUrl;

    public RequestSpecBuilder getRequestSpecBuilder() {
        return new RequestSpecBuilder().setRelaxedHTTPSValidation()
                .setBaseUri(baseUrl)
                .log(LogDetail.ALL)
                .addFilter(new AllureRestAssured())
                //.addFilter(new SwaggerCoverageRestAssured())
                .setConfig(RestAssuredConfig.config().objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory(
                        (type, s) -> new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).registerModule(new JavaTimeModule()).setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
                        )));
    }

    public Response defaultResponseHandler(Response r) {
        return r.prettyPeek().andReturn();
    }
}

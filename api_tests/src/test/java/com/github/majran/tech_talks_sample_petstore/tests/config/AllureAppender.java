package com.github.majran.tech_talks_sample_petstore.tests.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AllureAppender extends AppenderBase<ILoggingEvent> {
    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        Allure.getLifecycle()
                .getCurrentTestCaseOrStep()
                .ifPresent(step ->
                        Allure.step(iLoggingEvent.getFormattedMessage()));
    }
}

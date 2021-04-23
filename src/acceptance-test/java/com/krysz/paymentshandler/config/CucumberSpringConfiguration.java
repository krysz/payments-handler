package com.krysz.paymentshandler.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.cucumber.spring.CucumberContextConfiguration;

@ActiveProfiles("IT")
@CucumberContextConfiguration
@SpringBootTest(classes = {
    BeansConfiguration.class,
    IntegrationTestConfiguration.class }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CucumberSpringConfiguration {
}

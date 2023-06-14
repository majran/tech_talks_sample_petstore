package com.github.majran.tech_talks_sample_petstore.tests;

import com.github.majran.tech_talks_sample_petstore.api_client.api.PetPetApi;
import com.github.majran.tech_talks_sample_petstore.api_client.model.PetDTO;
import com.github.majran.tech_talks_sample_petstore.api_client.model.PetTypeDTO;
import com.github.majran.tech_talks_sample_petstore.tests.config.ReqSpec;
import com.github.majran.tech_talks_sample_petstore.tests.config.TestConfig;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringJUnitConfig
@ContextConfiguration(classes = TestConfig.class)
@Epic("Pets")
@Feature("Pets CRUD")
public class PetTests {

    @Autowired
    private ReqSpec reqSpec;

    @Test
    public void shouldGetAllPets() {
        //given
        PetDTO expectedHamster = new PetDTO()
                .name("Basil")
                .birthDate(LocalDate.from(DateTimeFormatter.ofPattern("yyyy-MM-dd").parse("2002-08-06")))
                .type(new PetTypeDTO().name("hamster"));

        //when
        PetDTO[] actualPets = PetPetApi.pet(() -> reqSpec.getRequestSpecBuilder())
                .listPets()
                .execute(response -> reqSpec.defaultResponseHandler(response)
                        .then()
                        .statusCode(SC_OK))
                .extract().as(PetDTO[].class);

        //then
        assertThat(Arrays.asList(actualPets))
                .usingRecursiveFieldByFieldElementComparator(
                        RecursiveComparisonConfiguration.builder()
                                .withIgnoreAllExpectedNullFields(true).build())
                .contains(expectedHamster);
    }
}

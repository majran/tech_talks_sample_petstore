package com.github.majran.tech_talks_sample_petstore.tests;


import com.github.majran.tech_talks_sample_petstore.api_client.api.PettypesPetApi;
import com.github.majran.tech_talks_sample_petstore.api_client.model.PetTypeDTO;
import com.github.majran.tech_talks_sample_petstore.tests.config.ReqSpec;
import com.github.majran.tech_talks_sample_petstore.tests.config.TestConfig;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.apache.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringJUnitConfig
@ContextConfiguration(classes = TestConfig.class)
@Epic("Pet Types")
@Feature("PetTypes CRUD")
public class PetTypeTests {

    @Autowired
    private ReqSpec reqSpec;

    @Test
    @Issue("PETBUG-1234")
    @TmsLink("PET-1234")
    @Story("User should get pet types")
    @Description("API should return array with pet types")
    @DisplayName("GET PetTypes")
    @Tag("Regression")
    public void shouldReturnAllTypesOfPets() {
        //given
        //when
        PetTypeDTO[] petTypeDTOS = PettypesPetApi.pettypes(() -> reqSpec.getRequestSpecBuilder())
                .listPetTypes()
                .execute(r -> reqSpec.defaultResponseHandler(r))
                .then()
                .statusCode(SC_OK)
                .extract()
                .body()
                .as(PetTypeDTO[].class);

        //then
        log.info("Fetched pet types: {}", Arrays.stream(petTypeDTOS).map(PetTypeDTO::getName).collect(Collectors.joining(", ")));
        assertThat(petTypeDTOS).isNotEmpty();
    }

    @Test
    public void shouldBeAbleToAddNewPetType() {
        //given
        PetTypeDTO newPetType = new PetTypeDTO().name(RandomStringUtils.randomAlphanumeric(8));
        log.info("Pet to be add: {}", newPetType.getName());

        //when
        PetTypeDTO addedPet = PettypesPetApi.pettypes(() -> reqSpec.getRequestSpecBuilder())
                .addPetType()
                .body(newPetType)
                .executeAs(r -> reqSpec.defaultResponseHandler(r).then().statusCode(SC_CREATED).extract().response());
        log.info("Id of newly added pet: {}", addedPet.getId());

        //then
        assertThat(addedPet.getName()).isEqualTo(newPetType.getName());
        assertThat(addedPet.getId()).isNotNull();

        log.info("Getting newly added pet type by ID");
        PetTypeDTO petTypeGetAfterAdd = PettypesPetApi.pettypes(() -> reqSpec.getRequestSpecBuilder()).getPetType()
                .petTypeIdPath(addedPet.getId())
                .executeAs(r -> reqSpec.defaultResponseHandler(r).then().statusCode(SC_OK).extract().response());

        log.info("New pet type fetched is {}", petTypeGetAfterAdd);
        assertThat(petTypeGetAfterAdd).isEqualTo(addedPet);
    }

    @Test
    public void shouldDeletePetType() {
        //given
        PetTypeDTO newPetType = new PetTypeDTO().name(RandomStringUtils.randomAlphanumeric(8));
        log.info("Pet to be add: {}", newPetType.getName());

        PetTypeDTO addedPet = PettypesPetApi.pettypes(() -> reqSpec.getRequestSpecBuilder())
                .addPetType()
                .body(newPetType)
                .executeAs(r -> reqSpec.defaultResponseHandler(r).then().statusCode(SC_CREATED).extract().response());
        log.info("Id of newly added pet: {}", addedPet.getId());

        //when
        PettypesPetApi.pettypes(() -> reqSpec.getRequestSpecBuilder())
                .deletePetType()
                .petTypeIdPath(addedPet.getId())
                .execute(r -> reqSpec.defaultResponseHandler(r).then().statusCode(SC_NO_CONTENT).extract().response());

        //then
        PettypesPetApi.pettypes(() -> reqSpec.getRequestSpecBuilder()).getPetType()
                .petTypeIdPath(addedPet.getId())
                .execute(r -> reqSpec.defaultResponseHandler(r).then().statusCode(SC_NOT_FOUND).extract().response());
    }

    @Test
    public void shouldUpdatePetType() {
        //given
        PetTypeDTO newPetType = new PetTypeDTO().name(RandomStringUtils.randomAlphanumeric(8));
        log.info("Pet to be add: {}", newPetType.getName());

        PetTypeDTO addedPet = PettypesPetApi.pettypes(() -> reqSpec.getRequestSpecBuilder())
                .addPetType()
                .body(newPetType)
                .executeAs(r -> reqSpec.defaultResponseHandler(r).then().statusCode(SC_CREATED).extract().response());
        log.info("Id of newly added pet: {}", addedPet.getId());

        //when
        PetTypeDTO petTypeUpdates = addedPet.name(addedPet.getName() + "-updated");
        PettypesPetApi.pettypes(() -> reqSpec.getRequestSpecBuilder())
                .updatePetType()
                .petTypeIdPath(addedPet.getId())
                .body(petTypeUpdates)
                .execute(r -> reqSpec.defaultResponseHandler(r).then().statusCode(SC_NO_CONTENT).extract().response());

        //then
        log.info("Getting newly updated pet type by ID");
        PetTypeDTO petTypeGetAfterUpdate = PettypesPetApi.pettypes(() -> reqSpec.getRequestSpecBuilder()).getPetType()
                .petTypeIdPath(addedPet.getId())
                .executeAs(r -> reqSpec.defaultResponseHandler(r).then().statusCode(SC_OK).extract().response());
        assertThat(petTypeGetAfterUpdate.getName()).isEqualTo(petTypeUpdates.getName());
    }

    @Test
    public void shouldGet400OnPetTypeIdValidation() {
        PettypesPetApi.pettypes(() -> reqSpec.getRequestSpecBuilder())
                .getPetType()
                .petTypeIdPath(-1)
                .execute(r -> reqSpec.defaultResponseHandler(r).then().statusCode(SC_BAD_REQUEST).extract().response());
    }

    @Test
    public void shouldGet404OnPetTypeIdValidation() {
        PettypesPetApi.pettypes(() -> reqSpec.getRequestSpecBuilder())
                .getPetType()
                .petTypeIdPath(999999) // :P
                .execute(r -> reqSpec.defaultResponseHandler(r).then().statusCode(SC_NOT_FOUND).extract().response());
    }

}

package com.backend.tasks.controller;

import com.backend.tasks.Application;
import com.backend.tasks.ApplicationTest;
import com.backend.tasks.dto.OrganizationCreateOrUpdateDto;
import com.backend.tasks.exceptions.ExceptionTranslator;
import com.backend.tasks.exceptions.ObjectNotFoundException;
import com.backend.tasks.model.Organization;
import com.backend.tasks.service.org.OrganizationService;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Random;

import static com.backend.tasks.utils.TestUtils.createOrganization;
import static com.backend.tasks.utils.TestUtils.createOrganizationCreateOrUpdateDto;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Implement tests for OrganizationController
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = ApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrganizationControllerTest extends BaseControllerTest {
    @MockBean
    private OrganizationService organizationService;


    @Test
    public void findAll() throws Exception {
        Random random = new Random();
        Organization organizationA = createOrganization(random.nextLong(), "Organization A");
        Organization organizationB = createOrganization(random.nextLong(), "Organization B");

        when(organizationService.findAll()).thenReturn(Lists.newArrayList(organizationA, organizationB));

        mockMvc.perform(get("/orgs").contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(organizationA.getId())))
                .andExpect(jsonPath("$.[0].name", is(organizationA.getName())))
                .andExpect(jsonPath("$.[1].id", is(organizationB.getId())))
                .andExpect(jsonPath("$.[1].name", is(organizationB.getName())));

        verify(organizationService, only()).findAll();
    }

    @Test
    public void create() throws Exception {
        OrganizationCreateOrUpdateDto organizationADto = createOrganizationCreateOrUpdateDto("Organization A");

        Random random = new Random();
        Organization organizationA = createOrganization(random.nextLong(), "Organization A");

        when(organizationService.create((Organization) notNull())).thenReturn(organizationA);

        mockMvc.perform(post("/orgs").contentType(MediaType.APPLICATION_JSON).content(json(organizationADto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(organizationA.getId())))
                .andExpect(jsonPath("$.name", is(organizationA.getName())));

    }

    @Test
    public void createWithValidationErrors() throws Exception {
        OrganizationCreateOrUpdateDto organizationADto = createOrganizationCreateOrUpdateDto("O");

        mockMvc.perform(post("/orgs").contentType(MediaType.APPLICATION_JSON).content(json(organizationADto)))
                .andDo(print()).andExpect(status().isBadRequest());

        organizationADto.setName("");
        mockMvc.perform(post("/orgs").contentType(MediaType.APPLICATION_JSON).content(json(organizationADto)))
                .andDo(print()).andExpect(status().isBadRequest());

        organizationADto.setName(null);
        mockMvc.perform(post("/orgs").contentType(MediaType.APPLICATION_JSON).content(json(organizationADto)))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    public void findById() throws Exception {
        Random random = new Random();
        Long id = random.nextLong();
        Organization organizationA = createOrganization(id, "Organization A");

        when(organizationService.findById(id)).thenReturn(organizationA);

        mockMvc.perform(get("/orgs/" + id).contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(organizationA.getId())))
                .andExpect(jsonPath("$.name", is(organizationA.getName())));
        verify(organizationService, only()).findById(id);
    }

    @Test
    public void findByIdNotFound() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(organizationService).setControllerAdvice(new ExceptionTranslator(null)).build();

        when(organizationService.findById(1L)).thenThrow(new ObjectNotFoundException("Organization", 1L));

        mockMvc.perform(get("/orgs/1").contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void update() throws Exception {
        OrganizationCreateOrUpdateDto organizationADto = createOrganizationCreateOrUpdateDto("Organization A");

        Random random = new Random();
        Long id = random.nextLong();
        Organization organizationA = createOrganization(id, "Organization ABC");
        Organization organizationAUpdated = createOrganization(id, "Organization A");

        when(organizationService.findById(id)).thenReturn(organizationA);
        when(organizationService.update(organizationAUpdated)).thenReturn(organizationAUpdated);

        mockMvc.perform(put("/orgs/" + id).contentType(MediaType.APPLICATION_JSON).content(json(organizationADto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(organizationAUpdated.getId())))
                .andExpect(jsonPath("$.name", is(organizationAUpdated.getName())));
    }

    @Test
    public void updateWithValidationErrors() throws Exception {
        OrganizationCreateOrUpdateDto organizationADto = createOrganizationCreateOrUpdateDto("O");

        Random random = new Random();
        Long id = random.nextLong();
        Organization organizationA = createOrganization(id, "Organization ABC");
        Organization organizationAUpdated = createOrganization(id, "Organization A");

        when(organizationService.findById(id)).thenReturn(organizationA);
        when(organizationService.update(organizationAUpdated)).thenReturn(organizationAUpdated);

        String urlTemplate = "/orgs/" + id;
        mockMvc.perform(put(urlTemplate).contentType(MediaType.APPLICATION_JSON).content(json(organizationADto)))
                .andDo(print()).andExpect(status().isBadRequest());

        organizationADto.setName("");
        mockMvc.perform(put(urlTemplate).contentType(MediaType.APPLICATION_JSON).content(json(organizationADto)))
                .andDo(print()).andExpect(status().isBadRequest());

        organizationADto.setName(null);
        mockMvc.perform(put(urlTemplate).contentType(MediaType.APPLICATION_JSON).content(json(organizationADto)))
                .andDo(print()).andExpect(status().isBadRequest());

    }

    @Test
    public void deleteOrganization() throws Exception {
        Random random = new Random();
        Long id = random.nextLong();

        Organization organizationA = createOrganization(id, "Organization ABC");

        OrganizationService serviceMock = mock(OrganizationService.class);
        when(organizationService.findById(id)).thenReturn(organizationA);
        doNothing().when(serviceMock).delete(id);

        mockMvc.perform(delete("/orgs/" + id).contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNoContent());

    }

    @Test
    public void deleteOrganizationNotFound() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(organizationService).setControllerAdvice(new ExceptionTranslator(null)).build();
        Random random = new Random();
        Long id = random.nextLong();

        OrganizationService serviceMock = mock(OrganizationService.class);
        doThrow(new ObjectNotFoundException("Organization", id)).when(serviceMock).delete(id);

        mockMvc.perform(delete("/orgs/" + id).contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound());
    }

}

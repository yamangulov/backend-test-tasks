package com.backend.tasks.service.org.impl;

import com.backend.tasks.ApplicationTest;
import com.backend.tasks.exceptions.CustomValidationException;
import com.backend.tasks.exceptions.ObjectAlreadyExistException;
import com.backend.tasks.exceptions.ObjectNotFoundException;
import com.backend.tasks.model.Organization;
import com.backend.tasks.repository.OrganizationRepository;
import com.backend.tasks.service.org.OrganizationService;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static com.backend.tasks.utils.TestUtils.createOrganization;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Implement tests for OrganizationServiceImpl
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTest.class)
public class OrganizationServiceImplTest {
    @Autowired
    private OrganizationService organizationService;

    @MockBean
    private OrganizationRepository organizationRepository;


    @Test
    public void findAll() throws Exception {
        Organization organizationA = createOrganization(1L, "Organization A");
        Organization organizationB = createOrganization(2L, "Organization B");
        when(organizationRepository.findAll()).thenReturn(Lists.newArrayList(organizationA, organizationB));
        List<Organization> allOrganizations = organizationService.findAll();

        assertThat(allOrganizations.size()).isEqualTo(2);
        assertThat(allOrganizations.get(0)).isEqualTo(organizationA);
        assertThat(allOrganizations.get(1)).isEqualTo(organizationB);
    }

    @Test
    public void create() throws Exception {
        Organization organizationA = createOrganization(null, "Organization A");
        Organization createdOrganizationA = createOrganization(1L, "Organization A");

        when(organizationRepository.save(organizationA)).thenReturn(createdOrganizationA);

        Organization createdOrganization = organizationService.create(organizationA);

        assertThat(createdOrganization).isEqualTo(createdOrganizationA);
    }

    @Test(expected = ObjectAlreadyExistException.class)
    public void createWithSameName() throws Exception {
        Organization organizationA = createOrganization(null, "Organization A");
        Organization createdOrganizationA = createOrganization(1L, "Organization A");

        when(organizationRepository.findByName(organizationA.getName())).thenReturn(createdOrganizationA);

        Organization createdOrganization = organizationService.create(organizationA);

    }

    @Test
    public void findById() throws Exception {
        Organization organizationA = createOrganization(1L, "Organization A");

        when(organizationRepository.findById(organizationA.getId())).thenReturn(Optional.ofNullable(organizationA));

        Organization organizationById = organizationService.findById(organizationA.getId());

        assertThat(organizationById).isEqualTo(organizationA);
    }

    @Test
    public void update() throws Exception {
        Organization organizationA = createOrganization(1L, "Organization A");
        Organization updatedOrganizationA = createOrganization(1L, "Organization updated name");

        when(organizationRepository.findById(organizationA.getId())).thenReturn(Optional.ofNullable(organizationA));
        when(organizationRepository.findByName(updatedOrganizationA.getName())).thenReturn(null);
        when(organizationRepository.save(updatedOrganizationA)).thenReturn(updatedOrganizationA);

        Organization updatedOrganization = organizationService.update(updatedOrganizationA);

        assertThat(updatedOrganization.getId()).isEqualTo(updatedOrganizationA.getId());
        assertThat(updatedOrganization.getName()).isEqualTo(updatedOrganizationA.getName());
    }

    @Test(expected = CustomValidationException.class)
    public void updateWithExistedName() throws Exception {
        Organization organizationA = createOrganization(1L, "Organization A");
        Organization updatedOrganizationA = createOrganization(1L, "Organization updated name");
        Organization organizationB = createOrganization(2L, "Organization updated name");

        when(organizationRepository.findById(organizationA.getId())).thenReturn(Optional.ofNullable(organizationA));
        when(organizationRepository.findByName(updatedOrganizationA.getName())).thenReturn(organizationB);
        when(organizationRepository.save(updatedOrganizationA)).thenReturn(updatedOrganizationA);

        Organization updatedOrganization = organizationService.update(updatedOrganizationA);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void updateWithNotExistedOrganization() throws Exception {
        Organization organizationA = createOrganization(1L, "Organization A");
        when(organizationRepository.findById(organizationA.getId())).thenReturn(Optional.ofNullable(null));

        Organization updatedOrganization = organizationService.update(organizationA);

    }

    @Test(expected = ObjectNotFoundException.class)
    public void delete() throws Exception {
        Organization organizationA = createOrganization(1L, "Organization A");

        doNothing().when(organizationRepository).deleteById(organizationA.getId());
        when(organizationRepository.findById(organizationA.getId())).thenReturn(Optional.ofNullable(null));

        organizationService.delete(organizationA.getId());

        Organization organizationById = organizationService.findById(organizationA.getId());
    }

}

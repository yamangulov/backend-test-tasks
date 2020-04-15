package com.backend.tasks.service.user.impl;

import com.backend.tasks.ApplicationTest;
import com.backend.tasks.exceptions.CustomValidationException;
import com.backend.tasks.exceptions.ExceptionTranslator;
import com.backend.tasks.exceptions.ObjectAlreadyExistException;
import com.backend.tasks.exceptions.ObjectNotFoundException;
import com.backend.tasks.model.Organization;
import com.backend.tasks.model.User;
import com.backend.tasks.repository.UserRepository;
import com.backend.tasks.service.org.OrganizationService;
import com.backend.tasks.service.user.UserService;
import com.google.common.collect.Lists;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static com.backend.tasks.utils.TestUtils.createOrganization;
import static com.backend.tasks.utils.TestUtils.createUser;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Implement tests for UserServiceImpl
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTest.class)
public class UserServiceImplTest {
    @Autowired
    private UserService userService;


    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OrganizationService organizationService;

    @Test
    public void create() throws Exception {
        Organization organizationA = createOrganization(1L, "Organization A");
        User userA = createUser(null, "userA", "userpasswordA");
        User createdUserA = createUser(1L, "userA", "userpasswordA");

        when(organizationService.findById(organizationA.getId())).thenReturn(organizationA);
        when(userRepository.findByUsernameAndOrganizationId(userA.getUsername(), organizationA.getId())).thenReturn(null);
        when(userRepository.save(userA)).thenReturn(createdUserA);

        User createdUser = userService.create(userA, organizationA.getId());

        assertThat(createdUser).isEqualTo(createdUserA);
    }

    @Test(expected = ObjectAlreadyExistException.class)
    public void createWithExistingUsername() throws Exception {
        Organization organizationA = createOrganization(1L, "Organization A");
        User userA = createUser(null, "userA", "userpasswordA");
        User existedUser = createUser(2L, "userA", "userpassword");
        User createdUserA = createUser(3L, "userA", "userpasswordA");

        when(organizationService.findById(organizationA.getId())).thenReturn(organizationA);
        when(userRepository.findByUsernameAndOrganizationId(userA.getUsername(), organizationA.getId())).thenReturn(existedUser);
        when(userRepository.save(userA)).thenReturn(createdUserA);

        User createdUser = userService.create(userA, organizationA.getId());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void createWithNotExistedOrganization() throws Exception {
        Organization organizationA = createOrganization(1L, "Organization A");
        User userA = createUser(null, "userA", "userpasswordA");

        when(organizationService.findById(organizationA.getId()))
                .thenThrow(new ObjectNotFoundException("Organization", organizationA.getId()));

        User createdUser = userService.create(userA, organizationA.getId());
    }

    @Test
    public void find() throws Exception {
        Organization organizationA = createOrganization(1L, "Organization A");
        User userA = createUser(1L, "userA", "userpasswordA");

        when(userRepository.findByIdAndOrganizationId(userA.getId(), organizationA.getId())).thenReturn(userA);

        User foundUser = userService.find(userA.getId(), organizationA.getId());
        assertThat(foundUser).isEqualTo(userA);
    }

    @Test
    public void update() throws Exception {
        Organization organizationA = createOrganization(1L, "Organization A");
        User userA = createUser(2L, "userA", "userpasswordA");
        User updatedUserA = createUser(2L, "userABC", "userpasswordA");

        when(userRepository.findById(userA.getId())).thenReturn(Optional.ofNullable(userA));
        when(userRepository.findByUsernameAndOrganizationId(userA.getUsername(), organizationA.getId())).thenReturn(null);
        when(userRepository.save(userA)).thenReturn(updatedUserA);

        User updateUser = userService.update(userA, organizationA.getId());

        assertThat(updateUser).isEqualTo(updatedUserA);
    }

    @Test(expected = CustomValidationException.class)
    public void updateWithExistingUsername() throws Exception {
        Organization organizationA = createOrganization(1L, "Organization A");
        User userA = createUser(2L, "userA", "userpasswordA");
        User updatedUserA = createUser(2L, "userABC", "userpasswordA");
        User existedUserWithSuchUsername = createUser(3L, "userABC", "userpasswordA");

        when(userRepository.findById(userA.getId())).thenReturn(Optional.ofNullable(userA));
        when(userRepository.findByUsernameAndOrganizationId(updatedUserA.getUsername(), organizationA.getId()))
                .thenReturn(existedUserWithSuchUsername);
        when(userRepository.save(userA)).thenReturn(updatedUserA);

        User updateUser = userService.update(updatedUserA, organizationA.getId());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void updateObjectNotFoundExceptionWithNotExistedUser() throws Exception {
        Organization organizationA = createOrganization(1L, "Organization A");
        User userA = createUser(2L, "userA", "userpasswordA");

        when(userRepository.findById(userA.getId())).thenReturn(Optional.ofNullable(null));

        User updateUser = userService.update(userA, organizationA.getId());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void delete() throws Exception {
        Organization organizationA = createOrganization(1L, "Organization A");
        User userA = createUser(2L, "userA", "userpasswordA");

        doNothing().when(userRepository).deleteById(userA.getId());
        when(userRepository.findByIdAndOrganizationId(userA.getId(), organizationA.getId()))
                .thenThrow(new ObjectNotFoundException("Organization", organizationA.getId()));

        userService.delete(organizationA.getId(), organizationA.getId());

        User userById = userService.find(userA.getId(), organizationA.getId());

        Assertions.assertThat(userById).isNull();
    }


    @Test
    public void findAllByOrgId() throws Exception {
        Organization organizationA = createOrganization(1L, "Organization A");
        User userA = createUser(1L, "userA", "userpasswordA");
        userA.setOrganization(organizationA);
        User userB = createUser(2L, "userB", "userpasswordB");
        userB.setOrganization(organizationA);

        when(userRepository.findByOrganizationId(organizationA.getId())).thenReturn(Lists.newArrayList(userA, userB));

        List<User> usersByOrgId = userService.findAllByOrgId(organizationA.getId());

        Assertions.assertThat(usersByOrgId.size()).isEqualTo(2);
        Assertions.assertThat(usersByOrgId.get(0)).isEqualTo(userA);
        Assertions.assertThat(usersByOrgId.get(1)).isEqualTo(userB);

    }
}

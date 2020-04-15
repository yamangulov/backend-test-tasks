package com.backend.tasks.controller;

import com.backend.tasks.ApplicationTest;
import com.backend.tasks.dto.UserCreateDto;
import com.backend.tasks.dto.UserUpdateDto;
import com.backend.tasks.exceptions.ExceptionTranslator;
import com.backend.tasks.exceptions.ObjectNotFoundException;
import com.backend.tasks.model.Organization;
import com.backend.tasks.model.User;
import com.backend.tasks.service.user.UserService;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Random;

import static com.backend.tasks.utils.TestUtils.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Implement tests for UserController
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = ApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest extends BaseControllerTest {
    @MockBean
    private UserService userService;

    @Test
    public void findAllByOrgId() throws Exception {
        Random random = new Random();

        Organization organizationA = createOrganization(random.nextLong(), "Organization A");
        User userA = createUser(random.nextLong(), "usrA", "passwdA");
        User userB = createUser(random.nextLong(), "usrB", "passwdB");

        when(userService.findAllByOrgId(organizationA.getId())).thenReturn(Lists.newArrayList(userA, userB));

        mockMvc.perform(get("/orgs/" + organizationA.getId() + "/users").contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(userA.getId())))
                .andExpect(jsonPath("$.[0].username", is(userA.getUsername())))
                .andExpect(jsonPath("$.[0].password", is(userA.getPassword())))
                .andExpect(jsonPath("$.[1].id", is(userB.getId())))
                .andExpect(jsonPath("$.[1].username", is(userB.getUsername())))
                .andExpect(jsonPath("$.[1].password", is(userB.getPassword())));
    }

    @Test
    public void findAllByOrgIdNotFound() throws Exception {
        when(userService.findAllByOrgId(1L)).thenReturn(Lists.newArrayList());

        mockMvc.perform(get(uriPrefix(1l)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void create() throws Exception {
        UserCreateDto userCreateDto = createUserCreateDto("usrA", "passwdB");

        Random random = new Random();
        User userA = createUser(random.nextLong(), "usrA", "passwdA");

        Organization organizationA = createOrganization(random.nextLong(), "Organization A");

        when(userService.create((User) notNull(), eq(organizationA.getId()))).thenReturn(userA);

        mockMvc.perform(post(uriPrefix(organizationA.getId())).contentType(MediaType.APPLICATION_JSON).content(json(userCreateDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userA.getId())))
                .andExpect(jsonPath("$.username", is(userA.getUsername())))
                .andExpect(jsonPath("$.password", is(userA.getPassword())));
    }

    @Test
    public void createWithValidationErrors() throws Exception {
        UserCreateDto userCreateDto = createUserCreateDto("u", "passwdA");

        mockMvc.perform(post(uriPrefix(1L)).contentType(MediaType.APPLICATION_JSON).content(json(userCreateDto)))
                .andDo(print()).andExpect(status().isBadRequest());

        userCreateDto.setUsername(" ");
        mockMvc.perform(post(uriPrefix(1L)).contentType(MediaType.APPLICATION_JSON).content(json(userCreateDto)))
                .andDo(print()).andExpect(status().isBadRequest());

        userCreateDto.setUsername(" dfd");
        mockMvc.perform(post(uriPrefix(1L)).contentType(MediaType.APPLICATION_JSON).content(json(userCreateDto)))
                .andDo(print()).andExpect(status().isBadRequest());

        userCreateDto.setUsername(" dfd dddd- ");
        mockMvc.perform(post(uriPrefix(1L)).contentType(MediaType.APPLICATION_JSON).content(json(userCreateDto)))
                .andDo(print()).andExpect(status().isBadRequest());

        userCreateDto.setPassword(" ");
        mockMvc.perform(post(uriPrefix(1L)).contentType(MediaType.APPLICATION_JSON).content(json(userCreateDto)))
                .andDo(print()).andExpect(status().isBadRequest());

        userCreateDto.setPassword("1");
        mockMvc.perform(post(uriPrefix(1L)).contentType(MediaType.APPLICATION_JSON).content(json(userCreateDto)))
                .andDo(print()).andExpect(status().isBadRequest());
    }


    @Test
    public void findById() throws Exception {
        Random random = new Random();
        Long id = random.nextLong();
        Long organizationId = random.nextLong();
        User userA = createUser(random.nextLong(), "usrA", "passwdA");

        when(userService.find(id, organizationId)).thenReturn(userA);

        mockMvc.perform(get(uriPrefix(organizationId) + "/" + id).contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userA.getId())))
                .andExpect(jsonPath("$.username", is(userA.getUsername())))
                .andExpect(jsonPath("$.password", is(userA.getPassword())));
        verify(userService, only()).find(id, organizationId);
    }


    @Test
    public void findByIdNotFound() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userService).setControllerAdvice(new ExceptionTranslator(null)).build();

        Random random = new Random();
        Long id = random.nextLong();
        Long organizationId = random.nextLong();

        when(userService.find(id, organizationId)).thenThrow(new ObjectNotFoundException("User", id));

        mockMvc.perform(get(uriPrefix(organizationId) + "/" + id).contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound());
    }


    @Test
    public void update() throws Exception {
        UserUpdateDto userUpdateDto = createUserUpdateDto("usrAUpdated", "passwdAUpdated");

        Random random = new Random();
        User userA = createUser(random.nextLong(), "usrA", "passwdA");

        Organization organizationA = createOrganization(random.nextLong(), "Organization A");

        User userAUpdated = createUser(userA.getId(), userUpdateDto.getUsername(), userUpdateDto.getPassword());


        when(userService.find(eq(userA.getId()), eq(organizationA.getId()))).thenReturn(userA);
        when(userService.update((User) notNull(), eq(organizationA.getId()))).thenReturn(userAUpdated);


        mockMvc.perform(put(uriPrefix(organizationA.getId()) + "/" + userA.getId()).contentType(MediaType.APPLICATION_JSON).content(json(userUpdateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userAUpdated.getId())))
                .andExpect(jsonPath("$.username", is(userAUpdated.getUsername())))
                .andExpect(jsonPath("$.password", is(userAUpdated.getPassword())));
    }


    @Test
    public void updateWithValidationErrors() throws Exception {
        UserUpdateDto userUpdateDto = createUserUpdateDto("u", "passwdAUpdated");

        Random random = new Random();
        User userA = createUser(random.nextLong(), "usrA", "passwdB");

        Organization organizationA = createOrganization(random.nextLong(), "Organization A");

        User userAUpdated = createUser(userA.getId(), userUpdateDto.getUsername(), userUpdateDto.getPassword());

        when(userService.find(eq(userA.getId()), eq(organizationA.getId()))).thenReturn(userA);
        when(userService.update((User) notNull(), eq(organizationA.getId()))).thenReturn(userAUpdated);

        String urlTemplate = uriPrefix(organizationA.getId()) + "/" + userA.getId();

        mockMvc.perform(put(urlTemplate).contentType(MediaType.APPLICATION_JSON).content(json(userUpdateDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        userUpdateDto.setUsername(" ");
        mockMvc.perform(put(urlTemplate).contentType(MediaType.APPLICATION_JSON).content(json(userUpdateDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        userUpdateDto.setUsername(" dfd");
        mockMvc.perform(put(urlTemplate).contentType(MediaType.APPLICATION_JSON).content(json(userUpdateDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        userUpdateDto.setUsername(" dfd dddd- ");
        mockMvc.perform(put(urlTemplate).contentType(MediaType.APPLICATION_JSON).content(json(userUpdateDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        userUpdateDto.setPassword(" ");
        mockMvc.perform(put(urlTemplate).contentType(MediaType.APPLICATION_JSON).content(json(userUpdateDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());


    }

    @Test
    public void deleteUser() throws Exception {
        Random random = new Random();
        User userA = createUser(random.nextLong(), "usrA", "passwdB");
        Organization organizationA = createOrganization(random.nextLong(), "Organization A");

        UserService serviceMock = mock(UserService.class);
        when(userService.find(eq(userA.getId()), eq(organizationA.getId()))).thenReturn(userA);
        doNothing().when(serviceMock).delete(userA.getId(), organizationA.getId());

        mockMvc.perform(delete(uriPrefix(organizationA.getId()) + "/" + userA.getId()).contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNoContent());

    }

    @Test
    public void deleteUserNotFound() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userService).setControllerAdvice(new ExceptionTranslator(null)).build();

        Random random = new Random();
        User userA = createUser(random.nextLong(), "usrA", "passwdB");
        Organization organizationA = createOrganization(random.nextLong(), "Organization A");

        UserService serviceMock = mock(UserService.class);
        doThrow(new ObjectNotFoundException("Organization", userA.getId())).when(serviceMock).delete(userA.getId(), organizationA.getId());

        mockMvc.perform(delete(uriPrefix(organizationA.getId()) + "/" + userA.getId()).contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound());

    }

    private String uriPrefix(Long id) {
        return "/orgs/" + id + "/users";
    }
}

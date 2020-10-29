package com.sp.sec.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.sec.user.domain.Authority;
import com.sp.sec.user.domain.User;
import com.sp.sec.user.service.UserService;
import com.sp.sec.web.controller.RestResponsePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    User user2() {
        return User.builder()
                .userId("a1")
                .email("user2@test.com")
                .name("user2")
                .authorities(Set.of(Authority.USER))
                .enabled(true)
                .build();
    }

    User admin() {
        return User.builder()
                .userId("a2")
                .email("admin@test.com")
                .name("admin")
                .authorities(Set.of(Authority.ADMIN))
                .enabled(true)
                .build();
    }

    @DisplayName("1. 리스트 접근은 권한이 있어야 한다.")
    @Test
    void test_1() throws Exception {
        mockMvc.perform(get("/user/list"))
                .andExpect(status().is3xxRedirection());
    }

    @DisplayName("2. admin은 user list에 접근할 수 있다.")
    @Test
    void test_2() throws Exception {
        when(userService.listUsers(1, 10)).thenReturn(new RestResponsePage(user2(), admin()));
        mockMvc.perform(get("/user/list").with(user(admin())))
                .andExpect(status().isOk());
    }

    @DisplayName("3. user 리스트는 페이징 되어 내려온다.")
    @Test
    void test_3() throws Exception {
        when(userService.listUsers(1, 10)).thenReturn(new RestResponsePage(user2(), admin()));
        String resp = mockMvc.perform(get("/user/list").with(user(admin())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        RestResponsePage<User> page = mapper.readValue(resp, new TypeReference<RestResponsePage<User>>() {
        });

        assertEquals(2, page.getTotalElements());
        assertEquals(1, page.getTotalPages());
        assertEquals(0, page.getNumber());
    }

    @DisplayName("4. admin 은 사용자에게 authority를 줄수 있다.")
    @Test
    void test_4() throws Exception {
        when(userService.addAuthority("a1", Authority.ROLE_ADMIN)).thenReturn(true);
        when(userService.findUser("a1")).thenReturn(Optional.of(user2()));
        mockMvc.perform(put(format("/user/authority/add?userId=%s&authority=%s", "a1", Authority.ROLE_ADMIN))
                .with(user(admin())))
                .andExpect(status().isOk());
    }

    @DisplayName("5. admin 은 사용자에게 authority를 뺄 수 있다.")
    @Test
    void test_5() throws Exception {
        when(userService.addAuthority("a1", Authority.ROLE_ADMIN)).thenReturn(true);
        when(userService.findUser("a1")).thenReturn(Optional.of(user2()));
        String resp = mockMvc.perform(put(format("/user/authority/remove?userId=%s&authority=%s", "a1", Authority.ROLE_ADMIN))
                .with(user(admin())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        User user = mapper.readValue(resp, User.class);
    }

}

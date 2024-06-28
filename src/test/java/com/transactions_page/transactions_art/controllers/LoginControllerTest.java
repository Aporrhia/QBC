package com.transactions_page.transactions_art.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginControllerTest {
    
        @Autowired
        private MockMvc mockMvc;
    
        // Correct credentials
        @Test
        void testLoginWithValidCredentials() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("phone-code", "34")
                    .param("phone", "455234195")
                    .param("password", "dneqwu*Yh3n")
                    .param("action", "login"))
                    .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                    .andExpect(MockMvcResultMatchers.redirectedUrl("/account"));
        }
    
        // Incorrect credentials
        @Test
        void testLoginWithInvalidCredentials() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("phone-code", "134")
                    .param("phone", "4535234195")
                    .param("password", "dneqw2u*Yh3n")
                    .param("action", "login"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
                    .andExpect(MockMvcResultMatchers.model().attribute("error", "Invalid phone or password"))
                    .andExpect(MockMvcResultMatchers.view().name("login"));
        }

        // Null credentials
        @Test
        void testLoginWithNullCredentials() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("phone-code", "")
                    .param("phone", "")
                    .param("password", "")
                    .param("action", "login"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
                    .andExpect(MockMvcResultMatchers.model().attribute("error", "Invalid phone or password"))
                    .andExpect(MockMvcResultMatchers.view().name("login"));
        }
    
        @Test
        void contextLoads() {
        }
    
    }
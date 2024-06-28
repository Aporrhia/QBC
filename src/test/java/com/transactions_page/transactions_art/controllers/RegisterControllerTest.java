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
class RegisterControllerTest {
    
        @Autowired
        private MockMvc mockMvc;
    
        // Correct credentials
        @Test
        void testRegistration() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/register")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("accFname", "Pops")
                    .param("accLname", "Saint")
                    .param("accEmail", "pop@gmail.com")
                    .param("accDOB", "1989-10-12")
                    .param("country", "10")
                    .param("phone", "21432435435")
                    .param("bank", "5")
                    .param("accPassword", "dfghwfewg4zghre")
                    .param("confirmPassword", "dfghwfewg4zghre"))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        // Incorrect credentials (Existing email)
        @Test
        void testRegistrationExistingEmail() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/register")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("accFname", "Pops")
                    .param("accLname", "Saint")
                    .param("accEmail", "st.pop@gmail.com")
                    .param("accDOB", "1989-10-12")
                    .param("country", "10")
                    .param("phone", "21432435435")
                    .param("bank", "5")
                    .param("accPassword", "dfghwfewg4zghre")
                    .param("confirmPassword", "dfghwfewg4zghre"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
                    .andExpect(MockMvcResultMatchers.model().attribute(
                        "error", 
                        "An account with the same email or phone number already exists"
                        ))
                    .andExpect(MockMvcResultMatchers.view().name("registration"));
        }

        // Incorrect credentials (Underaged user)
        @Test
        void testRegistrationUnderaged() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/register")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("accFname", "Pops")
                    .param("accLname", "Saint")
                    .param("accEmail", "pop@gmail.com")
                    .param("accDOB", "2020-10-12")
                    .param("country", "10")
                    .param("phone", "21432435433")
                    .param("bank", "5")
                    .param("accPassword", "dfghwfewg4zghre")
                    .param("confirmPassword", "dfghwfewg4zghre"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
                    .andExpect(MockMvcResultMatchers.model().attribute(
                        "error", 
                        "Registering client must be older than 18 y.o."
                        ))
                    .andExpect(MockMvcResultMatchers.view().name("registration"));
        }


        @Test
        void contextLoads() {
        }
    
    }
import application.service.AvailableCarService;
import org.example.SpringBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SpringBootApplication.class)
@AutoConfigureMockMvc
class RestApiIT {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AvailableCarService availableCarService;

    @Test
    void availableCarsWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/cars"))
                .andExpect(status().isUnauthorized());

        System.out.println("SECURITY: /api/v1/cars without token -> 401 UNAUTHORIZED");
    }

    @Test
    void availableCarsWithUserRoleReturns200() throws Exception {
        when(availableCarService.getAvailableCars()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/cars")
                        .with(jwt().jwt(jwt -> jwt
                                        .subject(USER_ID.toString())
                                        .claim("user_id", USER_ID.toString()))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk());

        System.out.println("SECURITY: USER can call /api/v1/cars");
    }
}

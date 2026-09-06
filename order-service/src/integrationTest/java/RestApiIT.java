import org.example.SpringBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SpringBootApplication.class)
@AutoConfigureMockMvc
class RestApiIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void carsWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/cars"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void carsWithJwtReturns200() throws Exception {
        mockMvc.perform(get("/cars")
                        .with(jwt().jwt(jwt -> jwt
                                .subject(USER_ID.toString())
                                .claim("user_id", USER_ID.toString()))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk());
    }

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
}

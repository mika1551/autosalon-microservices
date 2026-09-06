import org.example.SpringBootApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SpringBootApplication.class)
@AutoConfigureMockMvc
class OrderAccessIT {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID OTHER_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID MANAGER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID OWN_ORDER_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");
    private static final UUID OTHER_ORDER_ID = UUID.fromString("77777777-7777-7777-7777-777777777777");
    private static final UUID OWN_CONFIG_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID OTHER_CONFIG_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("delete from custom_orders");
        jdbcTemplate.update("delete from car_configuration_options");
        jdbcTemplate.update("delete from car_configurations");

        jdbcTemplate.update(
                "insert into car_configurations (id, car_model, base_price, removed) values (?, ?, ?, false)",
                OWN_CONFIG_ID, "BMW 320i", new BigDecimal("3000000")
        );
        jdbcTemplate.update(
                "insert into car_configurations (id, car_model, base_price, removed) values (?, ?, ?, false)",
                OTHER_CONFIG_ID, "Audi A4", new BigDecimal("2800000")
        );

        jdbcTemplate.update(
                "insert into custom_orders (id, client_id, manager_id, car_model, configuration_id, status, removed) values (?, ?, ?, ?, ?, ?, false)",
                OWN_ORDER_ID, USER_ID, MANAGER_ID, "BMW 320i", OWN_CONFIG_ID, "CREATED"
        );
        jdbcTemplate.update(
                "insert into custom_orders (id, client_id, manager_id, car_model, configuration_id, status, removed) values (?, ?, ?, ?, ?, ?, false)",
                OTHER_ORDER_ID, OTHER_USER_ID, MANAGER_ID, "Audi A4", OTHER_CONFIG_ID, "CREATED"
        );
    }

    @Test
    void userSeesOnlyOwnCustomOrder() throws Exception {
        MvcResult result = mockMvc.perform(get("/orders/custom")
                        .with(jwtFor(USER_ID, "USER")))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(1)))
                        .andExpect(jsonPath("$[0].clientId").value(USER_ID.toString()))
                        .andReturn();
        System.out.println("SECURITY: USER sees only own custom order");
        System.out.println("SECURITY: USER /orders/custom response = " + result.getResponse().getContentAsString());
    }

    @Test
    void managerSeesAllCustomOrders() throws Exception {
        MvcResult result = mockMvc.perform(get("/orders/custom")
                        .with(jwtFor(MANAGER_ID, "MANAGER")))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(2)))
                        .andReturn();
        System.out.println("SECURITY: MANAGER sees all custom orders");
        System.out.println("SECURITY: MANAGER /orders/custom response = " + result.getResponse().getContentAsString());
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtFor(UUID userId, String role) {
        return jwt().jwt(jwt -> jwt
                        .subject(userId.toString())
                        .claim("user_id", userId.toString()))
                .authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }
}

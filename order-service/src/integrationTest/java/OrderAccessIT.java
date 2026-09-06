import domain.enums.CustomOrderStatus;
import domain.model.CarConfiguration;
import domain.model.CustomOrder;
import domain.repository.CarConfigurationRepository;
import domain.repository.CustomOrderRepository;
import org.example.SpringBootApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(classes = SpringBootApplication.class)
@AutoConfigureMockMvc
class OrderAccessIT {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID OTHER_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID MANAGER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CustomOrderRepository customOrderRepository;

    @Autowired
    CarConfigurationRepository carConfigurationRepository;

    @BeforeEach
    void setUp() {
        customOrderRepository.findAll().forEach(order -> customOrderRepository.deleteById(order.getId()));
        carConfigurationRepository.findAll().forEach(configuration -> carConfigurationRepository.deleteById(configuration.getId()));

        CarConfiguration userConfiguration = new CarConfiguration();
        userConfiguration.setId(UUID.fromString("44444444-4444-4444-4444-444444444444"));
        userConfiguration.setCarModel("BMW 320i");
        userConfiguration.setBasePrice(new BigDecimal("3000000"));
        carConfigurationRepository.save(userConfiguration);

        CarConfiguration otherConfiguration = new CarConfiguration();
        otherConfiguration.setId(UUID.fromString("55555555-5555-5555-5555-555555555555"));
        otherConfiguration.setCarModel("Audi A4");
        otherConfiguration.setBasePrice(new BigDecimal("2800000"));
        carConfigurationRepository.save(otherConfiguration);

        customOrderRepository.save(new CustomOrder(
                UUID.fromString("66666666-6666-6666-6666-666666666666"),
                USER_ID,
                MANAGER_ID,
                "BMW 320i",
                userConfiguration,
                CustomOrderStatus.CREATED
        ));

        customOrderRepository.save(new CustomOrder(
                UUID.fromString("77777777-7777-7777-7777-777777777777"),
                OTHER_USER_ID,
                MANAGER_ID,
                "Audi A4",
                otherConfiguration,
                CustomOrderStatus.CREATED
        ));
    }

    @Test
    void userSeesOnlyOwnCustomOrder() throws Exception {
        mockMvc.perform(get("/orders/custom")
                        .with(jwtFor(USER_ID, "USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].clientId").value(USER_ID.toString()));
    }

    @Test
    void managerSeesAllCustomOrders() throws Exception {
        mockMvc.perform(get("/orders/custom")
                        .with(jwtFor(MANAGER_ID, "MANAGER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void userCannotReadSomeoneElsesCustomOrder() throws Exception {
        mockMvc.perform(get("/orders/custom/{id}", UUID.fromString("77777777-7777-7777-7777-777777777777"))
                        .with(jwtFor(USER_ID, "USER")))
                .andExpect(status().isForbidden());
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtFor(UUID userId, String role) {
        return jwt().jwt(jwt -> jwt
                        .subject(userId.toString())
                        .claim("user_id", userId.toString()))
                .authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }
}

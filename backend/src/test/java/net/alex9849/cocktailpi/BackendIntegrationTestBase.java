package net.alex9849.cocktailpi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BackendIntegrationTestBase {

    private static final Path DB_PATH = initDbPath();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    private String adminToken;

    @DynamicPropertySource
    static void overrideDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
                "jdbc:sqlite:file:" + DB_PATH.toAbsolutePath() + "?mode=rwc&foreign_keys=on&busy_timeout=10000");
    }

    @BeforeAll
    void ensureAdminToken() throws Exception {
        this.adminToken = loginAndGetToken();
    }

    protected String getAdminToken() {
        return adminToken;
    }

    private String loginAndGetToken() throws Exception {
        String payload = """
                {"username":"Admin","password":"123456","remember":false}
                """;
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();
        return parseToken(result.getResponse().getContentAsString());
    }

    private String parseToken(String json) throws IOException {
        JsonNode node = objectMapper.readTree(json);
        return node.get("accessToken").asText();
    }

    private static Path initDbPath() {
        try {
            Path targetDir = Paths.get("target");
            Files.createDirectories(targetDir);
            return Files.createTempFile(targetDir, "cocktailpi-test-", ".db");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create sqlite test database", e);
        }
    }
}

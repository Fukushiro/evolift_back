package com.treino.api.Evolift.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:h2:mem:evolift_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
})
class AuthControllerTests {

	@LocalServerPort
	private int port;

	@Test
	void registerAndLoginUser() throws Exception {
		String username = "user-" + UUID.randomUUID();
		String password = "secret123";
		String body = """
				{
					"username": "%s",
					"password": "%s"
				}
				""".formatted(username, password);
		HttpClient client = HttpClient.newHttpClient();

		HttpResponse<String> registerResponse = client.send(post("/auth/register", body), HttpResponse.BodyHandlers.ofString());

		assertThat(registerResponse.statusCode()).isEqualTo(201);
		assertThat(registerResponse.body()).contains(username);
		assertThat(registerResponse.body()).contains("User registered successfully");

		HttpResponse<String> loginResponse = client.send(post("/auth/login", body), HttpResponse.BodyHandlers.ofString());

		assertThat(loginResponse.statusCode()).isEqualTo(200);
		assertThat(loginResponse.body()).contains(username);
		assertThat(loginResponse.body()).contains("Login successful");
	}

	private HttpRequest post(String path, String body) {
		return HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(body))
				.build();
	}
}

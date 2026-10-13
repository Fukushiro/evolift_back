package com.treino.api.Evolift.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:h2:mem:evolift_exercicio_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
class ExercicioControllerTests {

	@LocalServerPort
	private int port;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void onlyAdminsCanCreateUpdateAndDeleteExercises() throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		String adminUsername = "admin-" + UUID.randomUUID();
		String password = "secret123";
		registerUser(client, adminUsername, password);
		jdbcTemplate.update("update app_users set role = 'ADMIN' where username = ?", adminUsername);

		String userUsername = "user-" + UUID.randomUUID();
		registerUser(client, userUsername, password);

		HttpResponse<String> deniedCreation = client.send(request(
				"POST", "/exercicios", """
						{"nome":"Agachamento"}
						""", userUsername, password), HttpResponse.BodyHandlers.ofString());
		assertThat(deniedCreation.statusCode()).isEqualTo(403);

		HttpResponse<String> createResponse = client.send(request(
				"POST", "/exercicios", """
						{"nome":"Agachamento"}
						""", adminUsername, password), HttpResponse.BodyHandlers.ofString());
		assertThat(createResponse.statusCode()).isEqualTo(201);
		assertThat(createResponse.body()).contains("\"posicao\":1");
		Matcher idMatcher = Pattern.compile("\"id\":(\\d+)").matcher(createResponse.body());
		assertThat(idMatcher.find()).isTrue();
		long exerciseId = Long.parseLong(idMatcher.group(1));

		HttpResponse<String> invalidPositionResponse = client.send(request(
				"POST", "/exercicios", """
						{"nome":"Posição inválida","posicao":0}
						""", adminUsername, password), HttpResponse.BodyHandlers.ofString());
		assertThat(invalidPositionResponse.statusCode()).isEqualTo(400);

		HttpResponse<String> getResponse = client.send(
				request("GET", "/exercicios/" + exerciseId, null), HttpResponse.BodyHandlers.ofString());
		assertThat(getResponse.statusCode()).isEqualTo(200);
		assertThat(getResponse.body()).contains("\"nome\":\"Agachamento\"");

		HttpResponse<String> deniedUpdate = client.send(request(
				"PUT", "/exercicios/" + exerciseId, """
						{"nome":"Agachamento livre"}
						""", userUsername, password), HttpResponse.BodyHandlers.ofString());
		assertThat(deniedUpdate.statusCode()).isEqualTo(403);

		HttpResponse<String> updateResponse = client.send(request(
				"PUT", "/exercicios/" + exerciseId, """
						{"nome":"Agachamento livre","posicao":3}
						""", adminUsername, password), HttpResponse.BodyHandlers.ofString());
		assertThat(updateResponse.statusCode()).isEqualTo(200);
		assertThat(updateResponse.body()).contains("\"nome\":\"Agachamento livre\"");
		assertThat(updateResponse.body()).contains("\"posicao\":3");

		HttpResponse<String> deniedDelete = client.send(request(
				"DELETE", "/exercicios/" + exerciseId, null, userUsername, password),
				HttpResponse.BodyHandlers.ofString());
		assertThat(deniedDelete.statusCode()).isEqualTo(403);

		HttpResponse<String> deleteResponse = client.send(request(
				"DELETE", "/exercicios/" + exerciseId, null, adminUsername, password),
				HttpResponse.BodyHandlers.ofString());
		assertThat(deleteResponse.statusCode()).isEqualTo(204);

		HttpResponse<String> missingResponse = client.send(
				request("GET", "/exercicios/" + exerciseId, null), HttpResponse.BodyHandlers.ofString());
		assertThat(missingResponse.statusCode()).isEqualTo(404);
	}

	private void registerUser(HttpClient client, String username, String password) throws Exception {
		HttpResponse<String> response = client.send(request("POST", "/auth/register", """
				{
					"username": "%s",
					"password": "%s"
				}
				""".formatted(username, password)), HttpResponse.BodyHandlers.ofString());
		assertThat(response.statusCode()).isEqualTo(201);
	}

	private HttpRequest request(String method, String path, String body) {
		return request(method, path, body, null, null);
	}

	private HttpRequest request(String method, String path, String body, String username, String password) {
		HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
				.header("Content-Type", "application/json");
		if (username != null && password != null) {
			String credentials = username + ":" + password;
			String authorization = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
			builder.header("Authorization", "Basic " + authorization);
		}
		if (body == null) {
			builder.method(method, HttpRequest.BodyPublishers.noBody());
		}
		else {
			builder.method(method, HttpRequest.BodyPublishers.ofString(body));
		}
		return builder.build();
	}
}

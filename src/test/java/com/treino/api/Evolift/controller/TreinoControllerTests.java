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
		"spring.datasource.url=jdbc:h2:mem:evolift_treino_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
class TreinoControllerTests {

	@LocalServerPort
	private int port;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void createReadUpdateAndDeleteTreino() throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		String adminUsername = "admin-" + UUID.randomUUID();
		String adminPassword = "secret123";
		registerAndPromoteToAdmin(client, adminUsername, adminPassword);
		HttpResponse<String> exerciseResponse = client.send(
				request("POST", "/exercicios", """
						{
							"nome": "Supino"
						}
						""", adminUsername, adminPassword),
				HttpResponse.BodyHandlers.ofString());
		assertThat(exerciseResponse.statusCode()).isEqualTo(201);
		Matcher exerciseIdMatcher = Pattern.compile("\"id\":(\\d+)").matcher(exerciseResponse.body());
		assertThat(exerciseIdMatcher.find()).isTrue();
		long exerciseId = Long.parseLong(exerciseIdMatcher.group(1));

		String createBody = """
				{
					"nome": "Treino A",
					"exercicios": [
						{
							"exercicioId": %d,
							"quantidadeSeries": 2,
							"quantidadeExecucoes": 10
						}
					]
				}
				""".formatted(exerciseId);

		HttpResponse<String> createResponse = client.send(
				request("POST", "/treinos", createBody, adminUsername, adminPassword),
				HttpResponse.BodyHandlers.ofString());

		assertThat(createResponse.statusCode()).isEqualTo(201);
		Matcher idMatcher = Pattern.compile("\"id\":(\\d+)").matcher(createResponse.body());
		assertThat(idMatcher.find()).isTrue();
		long treinoId = Long.parseLong(idMatcher.group(1));
		assertThat(createResponse.body()).contains("\"nome\":\"Treino A\"");
		assertThat(createResponse.body()).contains("\"nome\":\"Supino\"");
		assertThat(createResponse.body()).contains("\"quantidadeSeries\":2");
		assertThat(createResponse.body()).contains("\"quantidadeExecucoes\":10");

		HttpResponse<String> secondWorkoutResponse = client.send(
				request("POST", "/treinos", """
						{
							"nome": "Treino B",
							"exercicios": [
								{
									"exercicioId": %d,
									"quantidadeSeries": 4,
									"quantidadeExecucoes": 5
								}
							]
						}
						""".formatted(exerciseId), adminUsername, adminPassword),
				HttpResponse.BodyHandlers.ofString());
		assertThat(secondWorkoutResponse.statusCode()).isEqualTo(201);
		assertThat(secondWorkoutResponse.body()).contains("\"quantidadeSeries\":4");
		assertThat(secondWorkoutResponse.body()).contains("\"quantidadeExecucoes\":5");

		HttpResponse<String> getResponse = client.send(
				request("GET", "/treinos/" + treinoId, null),
				HttpResponse.BodyHandlers.ofString());
		assertThat(getResponse.statusCode()).isEqualTo(200);
		assertThat(getResponse.body()).contains("Treino A");

		HttpResponse<String> listResponse = client.send(
				request("GET", "/treinos", null),
				HttpResponse.BodyHandlers.ofString());
		assertThat(listResponse.statusCode()).isEqualTo(200);
		assertThat(listResponse.body()).contains("Treino A");

		String updateBody = """
				{
					"nome": "Treino B",
					"exercicios": [
						{
							"exercicioId": %d,
							"quantidadeSeries": 3,
							"quantidadeExecucoes": 10
						}
					]
				}
				""".formatted(exerciseId);
		HttpResponse<String> updateResponse = client.send(
				request("PUT", "/treinos/" + treinoId, updateBody),
				HttpResponse.BodyHandlers.ofString());
		assertThat(updateResponse.statusCode()).isEqualTo(200);
		assertThat(updateResponse.body()).contains("Treino B");

		HttpResponse<String> deleteResponse = client.send(
				request("DELETE", "/treinos/" + treinoId, null, adminUsername, adminPassword),
				HttpResponse.BodyHandlers.ofString());
		assertThat(deleteResponse.statusCode()).isEqualTo(204);

		HttpResponse<String> missingResponse = client.send(
				request("GET", "/treinos/" + treinoId, null),
				HttpResponse.BodyHandlers.ofString());
		assertThat(missingResponse.statusCode()).isEqualTo(404);
	}

	@Test
	void rejectsTreinoWithInvalidQuantities() throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		String adminUsername = "admin-" + UUID.randomUUID();
		String adminPassword = "secret123";
		registerAndPromoteToAdmin(client, adminUsername, adminPassword);
		String body = """
				{
					"nome": "Treino inválido",
					"exercicios": [
						{
							"exercicioId": 1,
							"quantidadeSeries": 0,
							"quantidadeExecucoes": 12
						}
					]
				}
				""";
		HttpResponse<String> response = HttpClient.newHttpClient().send(
				request("POST", "/treinos", body, adminUsername, adminPassword),
				HttpResponse.BodyHandlers.ofString());

		assertThat(response.statusCode()).isEqualTo(400);
	}

	@Test
	void onlyAdminsCanCreateOrDeleteTreinos() throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		String username = "usuario-" + UUID.randomUUID();
		String password = "secret123";
		HttpResponse<String> registration = client.send(
				request("POST", "/auth/register", """
						{
							"username": "%s",
							"password": "%s"
						}
						""".formatted(username, password)),
				HttpResponse.BodyHandlers.ofString());
		assertThat(registration.statusCode()).isEqualTo(201);
		assertThat(registration.body()).contains("\"role\":\"USUARIO\"");

		String body = """
				{
					"nome": "Treino protegido",
					"exercicios": [
						{
							"exercicioId": 1,
							"quantidadeSeries": 3,
							"quantidadeExecucoes": 10
						}
					]
				}
				""";
		HttpResponse<String> unauthenticatedResponse = client.send(
				request("POST", "/treinos", body),
				HttpResponse.BodyHandlers.ofString());
		assertThat(unauthenticatedResponse.statusCode()).isEqualTo(401);

		HttpResponse<String> userCreateResponse = client.send(
				request("POST", "/treinos", body, username, password),
				HttpResponse.BodyHandlers.ofString());
		assertThat(userCreateResponse.statusCode()).isEqualTo(403);

		HttpResponse<String> userDeleteResponse = client.send(
				request("DELETE", "/treinos/1", null, username, password),
				HttpResponse.BodyHandlers.ofString());
		assertThat(userDeleteResponse.statusCode()).isEqualTo(403);
	}

	private void registerAndPromoteToAdmin(HttpClient client, String username, String password) throws Exception {
		HttpResponse<String> registration = client.send(
				request("POST", "/auth/register", """
						{
							"username": "%s",
							"password": "%s"
						}
						""".formatted(username, password)),
				HttpResponse.BodyHandlers.ofString());
		assertThat(registration.statusCode()).isEqualTo(201);
		assertThat(registration.body()).contains("\"role\":\"USUARIO\"");

		jdbcTemplate.update("update app_users set role = 'ADMIN' where username = ?", username);
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

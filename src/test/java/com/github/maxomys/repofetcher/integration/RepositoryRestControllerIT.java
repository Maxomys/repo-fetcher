package com.github.maxomys.repofetcher.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.github.maxomys.repofetcher.api.RepositoryResponse;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest()
@AutoConfigureWebTestClient(timeout = "15000")
public class RepositoryRestControllerIT {

  @Autowired
  WebTestClient webTestClient;

  static String baseUrl;

  @BeforeAll
  static void setUp(WireMockRuntimeInfo wmRuntimeInfo) {
    baseUrl = wmRuntimeInfo.getHttpBaseUrl();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("GH_BASE_URL", () -> baseUrl);
  }

  @Test
  void getNonForkRepositoriesForUsernameTest() {
    // repos
    stubFor(get(urlPathTemplate("/users/{username}/repos"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBodyFile("ghUserReposResponse.json")));

    // branches
    stubFor(get(urlPathTemplate("/repos/{username}/{repo}/branches"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBodyFile("ghBranchesResponse.json")));

    webTestClient
        .get()
        .uri("/api/repos/foo")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(RepositoryResponse.class).value(repositories -> {
          assertEquals(3, repositories.size());
          assertFalse(repositories.get(0).repositoryName().isEmpty());
          assertEquals(3, repositories.get(1).branches().size());
        });
  }

  @Test
  void getNonForkRepositoriesForUsernameAllForksTest() {
    stubFor(get(urlPathTemplate("/users/{username}/repos"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBodyFile("ghUserReposResponseAllForks.json")));

    webTestClient
        .get()
        .uri("/api/repos/foo")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(RepositoryResponse.class).value(repositories -> {
          assertTrue(repositories.isEmpty());
        });

    verify(exactly(0), getRequestedFor(urlPathTemplate("/repos/{username}/{repo}/branches")));
  }

  @Test
  void getNonForkRepositoriesForUsernameNoBranchesTest() {
    // repos
    stubFor(get(urlPathTemplate("/users/{username}/repos"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBodyFile("ghUserReposResponse.json")));

    // empty list of branches
    stubFor(get(urlPathTemplate("/repos/{username}/{repo}/branches"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("[]")));

    webTestClient
        .get()
        .uri("/api/repos/foo")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(RepositoryResponse.class).value(repositories -> {
          assertEquals(3, repositories.size());
          assertTrue(repositories.get(0).branches().isEmpty());
        });
  }

  @Test
  void getNonForkRepositoriesForUsernameNotFoundTest() {
    stubFor(get(urlPathTemplate("/users/{username}/repos"))
        .withQueryParam("page", matching("^[1-9]\\d*$"))
        .willReturn(aResponse()
            .withStatus(404)
            .withHeader("Content-Type", "application/json")
            .withBody("{\r\n" + //
                "  \"message\": \"Not Found\",\r\n" + //
                "  \"documentation_url\": \"https://docs.github.com/rest/repos/repos#list-repositories-for-a-user\",\r\n"
                + //
                "  \"status\": \"404\"\r\n" + //
                "}")));

    webTestClient
        .get()
        .uri("/api/repos/foo")
        .exchange()
        .expectStatus()
        .is4xxClientError()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(404)
        .jsonPath("$.message")
        .isEqualTo("The user you are looking for does not exist.");
  }

}

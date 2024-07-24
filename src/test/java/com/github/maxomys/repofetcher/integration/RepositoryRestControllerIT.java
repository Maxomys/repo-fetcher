package com.github.maxomys.repofetcher.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest()
@AutoConfigureWebTestClient(timeout = "9999999")
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
        .withQueryParam("page", matching("^[1-2]$"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBodyFile("ghUserReposResponse.json")));

    // empty page
    stubFor(get(urlPathTemplate("/users/{username}/repos"))
        .withQueryParam("page", equalTo("3"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("[]")));

    // branches
    stubFor(get(urlPathTemplate("/repos/{username}/{repo}/branches"))
        .withQueryParam("page", matching("^[1-2]$"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBodyFile("ghBranchesResponse.json")));

    // empty page
    stubFor(get(urlPathTemplate("/repos/{username}/{repo}/branches"))
        .withQueryParam("page", equalTo("3"))
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
        .expectBody()
        .jsonPath("$[0].branches[0]")
        .isNotEmpty();
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
        .isEqualTo(404);
  }

}

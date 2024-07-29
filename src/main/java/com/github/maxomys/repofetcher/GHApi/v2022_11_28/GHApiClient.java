package com.github.maxomys.repofetcher.GHApi.v2022_11_28;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.maxomys.repofetcher.exceptions.UserNotFoundException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class GHApiClient {

  private final String API_REPOSITORIES_URL = "/users/{username}/repos";
  private final String API_BRANCHES_URL = "/repos/{username}/{repo}/branches";

  private final WebClient ghWebClient;

  public Flux<GHRepository> getRepositoriesForUsername(String username) {
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(API_REPOSITORIES_URL);

    Map<String, String> urlParams = new HashMap<>();
    urlParams.put("username", username);

    return ghWebClient
        .get()
        .uri(uriBuilder.buildAndExpand(urlParams).toString())
        .retrieve()
        .onStatus(status -> status.value() == 404, res -> {
          throw new UserNotFoundException(username);
        })
        .bodyToFlux(GHRepository.class);
  }

  public Flux<GHBranch> getBranchesForUsernameAndRepository(String username, String repositoryName) {
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(API_BRANCHES_URL);

    Map<String, String> urlParams = new HashMap<>();
    urlParams.put("username", username);
    urlParams.put("repo", repositoryName);

    return ghWebClient
        .get()
        .uri(uriBuilder.buildAndExpand(urlParams).toString())
        .retrieve()
        .bodyToFlux(GHBranch.class);
  }

}

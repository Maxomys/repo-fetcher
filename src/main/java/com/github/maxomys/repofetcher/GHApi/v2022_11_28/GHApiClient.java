package com.github.maxomys.repofetcher.GHApi.v2022_11_28;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.maxomys.repofetcher.exceptions.UserNotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GHApiClient {

  private final String API_REPOSITORIES_URL = "/users/{username}/repos";
  private final String API_BRANCHES_URL = "/repos/{username}/{repo}/branches";

  private final RestClient ghRestClient;

  public List<GHRepository> getRepositoriesForUsernameAllPages(String username) {
    List<GHRepository> allRepositories = new ArrayList<>();

    int page = 1;

    while (true) {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(API_REPOSITORIES_URL)
          .queryParam("page", page);

      Map<String, String> urlParams = new HashMap<>();
      urlParams.put("username", username);

      ResponseEntity<List<GHRepository>> response = ghRestClient
          .get()
          .uri(uriBuilder.buildAndExpand(urlParams).toString())
          .retrieve()
          .onStatus(status -> status.value() == 404, (req, res) -> {
            throw new UserNotFoundException(username);
          })
          .toEntity(new ParameterizedTypeReference<List<GHRepository>>() {
          });

      List<GHRepository> repositories = response.getBody();

      if (repositories == null || repositories.isEmpty()) {
        break;
      }

      allRepositories.addAll(repositories);
      page++;
    }

    return allRepositories;
  }

  public List<GHBranch> getBranchesForUsernameAndRepositoryAllPages(String username, String repositoryName) {
    List<GHBranch> allBranches = new ArrayList<>();

    int page = 1;

    while (true) {
      UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(API_BRANCHES_URL)
          .queryParam("page", page);

      Map<String, String> urlParams = new HashMap<>();
      urlParams.put("username", username);
      urlParams.put("repo", repositoryName);

      ResponseEntity<List<GHBranch>> response = ghRestClient
          .get()
          .uri(uriBuilder.buildAndExpand(urlParams).toString())
          .retrieve()
          .toEntity(new ParameterizedTypeReference<List<GHBranch>>() {
          });

      List<GHBranch> branches = response.getBody();

      if (branches == null || branches.isEmpty()) {
        break;
      }

      allBranches.addAll(branches);
      page++;
    }

    return allBranches;
  }

}

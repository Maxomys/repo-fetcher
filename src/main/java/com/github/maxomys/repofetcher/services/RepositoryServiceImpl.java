package com.github.maxomys.repofetcher.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.maxomys.repofetcher.GHApi.v2022_11_28.GHApiClient;
import com.github.maxomys.repofetcher.GHApi.v2022_11_28.GHRepository;
import com.github.maxomys.repofetcher.api.BranchResponse;
import com.github.maxomys.repofetcher.api.RepositoryResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RepositoryServiceImpl implements RepositoryService {

  private final GHApiClient ghApiClient;

  @Override
  public List<RepositoryResponse> getNonForkRepositoriesForUsername(String username) {
    List<GHRepository> ghRepositoriesWithoutForks = ghApiClient.getRepositoriesForUsernameAllPages(username)
        .stream()
        .filter(repo -> !repo.isFork())
        .collect(Collectors.toList());

    List<RepositoryResponse> repositoryResponses = new ArrayList<>();

    ghRepositoriesWithoutForks.forEach(repo -> {
      List<BranchResponse> branchResponses = ghApiClient
          .getBranchesForUsernameAndRepositoryAllPages(repo.owner().login(), repo.name())
          .stream()
          .map(branch -> new BranchResponse(branch.name(), branch.commit().sha())).collect(Collectors.toList());

      repositoryResponses.add(new RepositoryResponse(repo.name(), repo.owner().login(), branchResponses));
    });

    return repositoryResponses;
  }

}

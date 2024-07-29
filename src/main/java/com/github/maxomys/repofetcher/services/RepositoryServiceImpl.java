package com.github.maxomys.repofetcher.services;

import org.springframework.stereotype.Service;

import com.github.maxomys.repofetcher.GHApi.v2022_11_28.GHApiClient;
import com.github.maxomys.repofetcher.api.BranchResponse;
import com.github.maxomys.repofetcher.api.RepositoryResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class RepositoryServiceImpl implements RepositoryService {

  private final GHApiClient ghApiClientReactive;

  @Override
  public Flux<RepositoryResponse> getNonForkRepositoriesForUsername(String username) {
    return ghApiClientReactive.getRepositoriesForUsername(username)
        .filter(repository -> !repository.fork())
        .flatMap(repository -> ghApiClientReactive.getBranchesForUsernameAndRepository(username, repository.name())
            .map(branch -> new BranchResponse(branch.name(), branch.commit().sha()))
            .collectList()
            .map(branches -> new RepositoryResponse(repository.name(), repository.owner().login(), branches)));
  }

}

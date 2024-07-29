package com.github.maxomys.repofetcher.services;

import com.github.maxomys.repofetcher.api.RepositoryResponse;

import reactor.core.publisher.Flux;

public interface RepositoryService {

  Flux<RepositoryResponse> getNonForkRepositoriesForUsername(String username);

}

package com.github.maxomys.repofetcher.services;

import java.util.List;

import com.github.maxomys.repofetcher.api.RepositoryResponse;

public interface RepositoryService {

  List<RepositoryResponse> getNonForkRepositoriesForUsername(String username);

}

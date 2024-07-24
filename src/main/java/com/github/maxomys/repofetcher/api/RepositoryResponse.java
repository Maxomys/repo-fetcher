package com.github.maxomys.repofetcher.api;

import java.util.List;

public record RepositoryResponse(String repositoryName, String ownerLogin, List<BranchResponse> branches) {

}

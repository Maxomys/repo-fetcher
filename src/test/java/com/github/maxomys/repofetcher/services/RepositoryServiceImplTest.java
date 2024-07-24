package com.github.maxomys.repofetcher.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.maxomys.repofetcher.GHApi.v2022_11_28.GHApiClient;
import com.github.maxomys.repofetcher.GHApi.v2022_11_28.GHBranch;
import com.github.maxomys.repofetcher.GHApi.v2022_11_28.GHCommit;
import com.github.maxomys.repofetcher.GHApi.v2022_11_28.GHRepository;
import com.github.maxomys.repofetcher.GHApi.v2022_11_28.GHUser;
import com.github.maxomys.repofetcher.api.RepositoryResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RepositoryServiceImplTest {

    @Mock
    private GHApiClient ghApiClient;

    @InjectMocks
    private RepositoryServiceImpl repositoryService;

    @Test
    public void testGetNonForkRepositoriesForUsername() {
        GHUser user = new GHUser("1", "testuser");
        GHRepository repo1 = new GHRepository(1L, "repo1", false, user);
        GHRepository repo2 = new GHRepository(2L, "repo2", true, user); // Forked repo
        GHRepository repo3 = new GHRepository(3L, "repo3", false, user);
        List<GHRepository> repositories = List.of(repo1, repo2, repo3);

        GHCommit commit = new GHCommit("commit1", "sha1");
        GHBranch branch = new GHBranch("branch1", commit);
        List<GHBranch> branches = List.of(branch);

        when(ghApiClient.getRepositoriesForUsernameAllPages(anyString())).thenReturn(repositories);
        when(ghApiClient.getBranchesForUsernameAndRepositoryAllPages(anyString(), anyString())).thenReturn(branches);

        List<RepositoryResponse> response = repositoryService.getNonForkRepositoriesForUsername("testuser");

        assertEquals(2, response.size());

        assertEquals("repo1", response.get(0).repositoryName());
        assertEquals("testuser", response.get(0).ownerLogin());
        assertEquals(1, response.get(0).branches().size());
        assertEquals("branch1", response.get(0).branches().get(0).name());
        assertEquals("sha1", response.get(0).branches().get(0).lastCommitSha());

        assertEquals("repo3", response.get(1).repositoryName());
        assertEquals("testuser", response.get(1).ownerLogin());
        assertEquals(1, response.get(1).branches().size());
        assertEquals("branch1", response.get(1).branches().get(0).name());
        assertEquals("sha1", response.get(1).branches().get(0).lastCommitSha());
    }
}

package com.github.maxomys.repofetcher.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.maxomys.repofetcher.api.NotFoundResponse;
import com.github.maxomys.repofetcher.api.RepositoryResponse;
import com.github.maxomys.repofetcher.exceptions.UserNotFoundException;
import com.github.maxomys.repofetcher.services.RepositoryService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/repos")
public class RepositoryRestController {

  private final RepositoryService repositoryService;

  @GetMapping("/{username}")
  public Flux<RepositoryResponse> getNonForkRepositoriesForUsernameReactive(@PathVariable String username) {
    return repositoryService.getNonForkRepositoriesForUsername(username);
  }

  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public NotFoundResponse handleException(Exception e) {
    return new NotFoundResponse(HttpStatus.NOT_FOUND.value(), "The user you are looking for does not exist.");
  }
}

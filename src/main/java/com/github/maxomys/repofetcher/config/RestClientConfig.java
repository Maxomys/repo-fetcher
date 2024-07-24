package com.github.maxomys.repofetcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

  @Value("${GH_PRIVATE_KEY}")
  private String GH_PRIVATE_KEY;

  @Value("${GH_BASE_URL}")
  private String ghBaseUrl;

  @Bean
  public RestClient ghRestClient() {
    RestClient.Builder builder = RestClient.builder()
        .defaultHeader("User-Agent", "RepoFetcher/1.0 (Java 21; Windows 11)")
        .baseUrl(ghBaseUrl);

    if (GH_PRIVATE_KEY != null && !GH_PRIVATE_KEY.isEmpty()) {
      builder.defaultHeader("Authorization", "Bearer " + GH_PRIVATE_KEY);
    }

    return builder.build();
  }

}

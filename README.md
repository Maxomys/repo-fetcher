# Repo Fetcher Task

## Overview

Repo Fetcher is a Spring Boot application designed to interact with the GitHub API to retrieve repositories for a specified user. It filters out forked repositories and provides information on the branches within each repository.

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven

### Installation

1. **Clone the repository:**

    ```bash
    git clone https://github.com/your-username/repo-fetcher.git
    cd repo-fetcher
    ```

2. **Build the project:**

    ```bash
    mvn clean install
    ```

3. **Run the application:**

    ```bash
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8080`.

## Usage

### Endpoints

- **Get Non-Fork Repositories for a User**
  - **URL:** `/api/repo/{username}`
  - **Method:** `GET`
  - **Response:**
    - **200 OK:** List of non-fork repositories with their branches and commit SHAs.
    - **404 NOT FOUND:** When the user is not found.

### Example Request

```bash
curl http://localhost:8080/api/repo/octocat
```

### Example Response

```json
[
  {
    "repositoryName": "Hello-World",
    "ownerLogin": "octocat",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "1a2b3c4d5e"
      },
      {
        "name": "develop",
        "lastCommitSha": "6f7g8h9i0j"
      }
    ]
  }
]
```

### Testing

This project uses WireMock for testing interactions with the GitHub API. To run the tests:

```bash
mvn test
```

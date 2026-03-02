# Screenmatch

A command-line Java application built with Spring Boot that integrates with the [OMDb API](https://www.omdbapi.com/) to fetch, process, and analyze TV series data. The application retrieves detailed information about series, seasons, and episodes, then performs statistical analysis on episode ratings using the Java Streams API.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [How It Works](#how-it-works)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Configuration](#configuration)

---

## Overview

Screenmatch allows users to search for any TV series by name. Once a series is found, the application automatically fetches data for all available seasons and their respective episodes. It then computes and displays:

- A full list of episodes per season
- Average rating per season
- Overall rating statistics (mean, best, worst, and total episode count)

---

## Tech Stack

| Technology | Version | Role |
|---|---|---|
| Java | 17 | Core language |
| Spring Boot | 3.5.11 | Application framework |
| Jackson Databind | 2.21.1 | JSON deserialization |
| Jackson Annotations | 2.21 | OMDb field mapping |
| Maven | (wrapper) | Build and dependency management |

---

## Project Structure

```
src/
└── main/
    └── java/br/com/screenmatch/
        ├── ScreenmatchApplication.java   # Spring Boot entry point; triggers Principal.exibeMenu()
        ├── model/
        │   ├── DadosSerie.java           # Record mapping series-level data from OMDb
        │   ├── DadosTemporada.java       # Record mapping season-level data from OMDb
        │   ├── DadosEpisodio.java        # Record mapping episode-level data from OMDb
        │   └── Episodio.java             # Domain class with parsed rating and release date
        ├── service/
        │   ├── IConverteDados.java       # Generic deserialization interface
        │   ├── ConverteDados.java        # Jackson-based implementation of IConverteDados
        │   └── ConsumoApi.java           # HTTP client using java.net.http (Java 11+)
        └── principal/
            └── Principal.java            # Application logic, Stream pipelines, statistics
```

---

## How It Works

1. **HTTP Request** – `ConsumoApi` uses the native `HttpClient` (introduced in Java 11) to make GET requests to the OMDb API and return the raw JSON string.

2. **Deserialization** – `ConverteDados` uses Jackson's `ObjectMapper` to deserialize JSON into typed Java objects. The model classes use `@JsonAlias` to map OMDb field names (e.g. `"Title"`, `"imdbRating"`) to domain-specific field names, and `@JsonIgnoreProperties(ignoreUnknown = true)` to safely ignore extra fields.

3. **Domain Modeling** – Raw OMDb data (`DadosEpisodio`) is converted into a richer `Episodio` domain object that parses the rating as a `Double` and the release date as a `LocalDate`, handling malformed values gracefully with try/catch blocks.

4. **Stream Pipelines** – `Principal` uses Java Streams to:
   - Flatten all episodes across seasons with `flatMap`
   - Filter out episodes with missing ratings
   - Group episodes by season and compute averages using `Collectors.groupingBy` + `Collectors.averagingDouble`
   - Compute summary statistics using `Collectors.summarizingDouble` (`DoubleSummaryStatistics`)

---

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+** (or use the included `mvnw` wrapper)
- An **OMDb API key** (free tier available at [omdbapi.com](https://www.omdbapi.com/apikey.aspx))

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/juliocesarcs2004/Streaming-Platform-Api-With-Java-And-Spring-Boot.git
cd Streaming-Platform-Api-With-Java-And-Spring-Boot
```

### 2. Set your API key

Open `Principal.java` and replace the existing key value:

```java
private final String API_KEY = "&apikey=YOUR_API_KEY_HERE";
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

### 4. Search for a series

When prompted, type the name of any TV series (e.g. `Breaking Bad`) and press Enter. The application will print full season/episode data and rating statistics to the console.

---

## Configuration

| Property | Location | Description |
|---|---|---|
| `ENDERECO` | `Principal.java` | Base URL for the OMDb API |
| `API_KEY` | `Principal.java` | Your personal OMDb API key |
| `java.version` | `pom.xml` | Java version used at compile time (default: 17) |

---

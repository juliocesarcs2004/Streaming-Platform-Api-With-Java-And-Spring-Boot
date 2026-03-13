# Screenmatch

A Java application with Spring Boot that runs in the terminal (`CommandLineRunner`) to search for series on OMDb, persist data in PostgreSQL, and query series/episodes with Spring Data JPA.

## Table of Contents

- [Current Project Status](#current-project-status)
- [How It Works](#how-it-works)
- [Available Menu Features](#available-menu-features)
- [Technology Stack](#technology-stack)
- [Main Structure](#main-structure)
- [Domain Model](#domain-model)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Database Setup Example](#database-setup-example)
- [Running the Application](#running-the-application)
- [Typical Usage Flow](#typical-usage-flow)
- [Important Notes](#important-notes)
- [Troubleshooting](#troubleshooting)

## Current Project Status

- Application type: interactive console app (does not expose a REST API at the moment)
- Persistence: PostgreSQL with JPA/Hibernate (`ddl-auto=update`)
- External data source: OMDb API
- Plot translation: OpenAI (`gpt-4o-mini`) during series registration

## How It Works

1. The app starts through `ScreenmatchApplication`, which implements `CommandLineRunner`.
2. It creates `Principal` and opens an interactive terminal menu.
3. When searching for a series, the app calls OMDb (`https://www.omdbapi.com/?t=...`).
4. JSON responses are mapped with Jackson into records (`DadosSerie`, `DadosTemporada`, `DadosEpisodio`).
5. Data is converted to JPA entities (`Serie`, `Episodio`) and persisted in PostgreSQL.
6. The plot is translated to Portuguese via OpenAI before persisting the series.
7. Repository methods and JPQL queries are used for filtering and ranking features.

## Available Menu Features

1. `Search series`  
   Input: series title  
   Action: fetch from OMDb, translate plot, persist in `series` table
2. `Search episodes by series`  
   Input: previously saved series title  
   Action: fetch all seasons/episodes from OMDb and persist in `episodios`
3. `List searched series`  
   Action: list persisted series sorted by genre
4. `Search series by title`  
   Input: partial title  
   Action: lookup with case-insensitive matching
5. `Search series by actor`  
   Input: actor name + minimum rating  
   Action: filter by actor substring and rating threshold
6. `Top 5 series`  
   Action: return top-rated series
7. `Search series by category`  
   Input: category in Portuguese (`Ação`, `Romance`, `Comédia`, `Drama`, `Crime`)  
   Action: filter by enum category
8. `Filter series`  
   Input: max number of seasons + minimum rating  
   Action: custom JPQL filter
9. `Search episodes by title snippet`  
   Input: part of episode title  
   Action: return matching episodes across all stored series
10. `Top 5 episodes by series`  
   Input: series title  
   Action: rank episodes by rating for that series
11. `Search episodes after year`  
   Input: series title + year  
   Action: return episodes released from that year onward

## Technology Stack

- Java 17
- Spring Boot 3.5.11
- Spring Data JPA
- PostgreSQL (driver JDBC)
- Jackson (`jackson-databind`, `jackson-annotations`)
- OpenAI Java SDK (`openai-java`)
- Google GenAI SDK (dependency present, not used in the main flow)
- Maven Wrapper

## Main Structure

```text
src/main/java/br/com/alura/screenmatch
├── ScreenmatchApplication.java   # Entry point + CommandLineRunner
├── principal/Principal.java      # Menu and business flow
├── model/                        # JPA entities and OMDb integration records
├── repository/SerieRepository.java
└── service/                      # API consumption, JSON conversion, and translation
```

## Domain Model

### Entity: `Serie`
- `id` (Long, PK)
- `titulo` (String, unique)
- `totalTemporadas` (Integer)
- `avaliacao` (Double)
- `genero` (`Categoria` enum)
- `atores` (String)
- `poster` (String)
- `sinopse` (String, translated)
- `episodios` (`@OneToMany`, eager, cascade all)

### Entity: `Episodio`
- `id` (Long, PK)
- `temporada` (Integer)
- `titulo` (String)
- `numeroEpisodio` (Integer)
- `avaliacao` (Double; defaults to `0.0` for invalid values)
- `dataLancamento` (LocalDate; nullable when parsing fails)
- `serie` (`@ManyToOne`)

### Enum: `Categoria`
- Supported values: `ACAO`, `ROMANCE`, `COMEDIA`, `DRAMA`, `CRIME`
- Includes mapping from OMDb English genres and Portuguese menu input.

## Prerequisites

- Java 17+
- PostgreSQL running
- Valid OpenAI API key
- (Optional) your own OMDb API key

## Configuration

Set the environment variables before running:

```bash
export CONNECTION_URL="jdbc:postgresql://localhost:5432/screenmatch"
export USERNAME="your_username"
export PASSWORD="your_password"
export OPENAI_API_KEY="your_openai_key"
```

On Windows (PowerShell):

```powershell
$env:CONNECTION_URL="jdbc:postgresql://localhost:5432/screenmatch"
$env:USERNAME="your_username"
$env:PASSWORD="your_password"
$env:OPENAI_API_KEY="your_openai_key"
```

Notes:
- `application.properties` expects `CONNECTION_URL`, `USERNAME`, and `PASSWORD`.
- The OpenAI client reads `OPENAI_API_KEY` directly from environment variables.
- OMDb API key is currently hardcoded in `Principal.java`.

## Database Setup Example

If needed, create the database manually:

```sql
CREATE DATABASE screenmatch;
```

Then use a JDBC URL like:

```text
jdbc:postgresql://localhost:5432/screenmatch
```

## Running the Application

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

You can also run tests:

```bash
./mvnw test
```

## Typical Usage Flow

1. Run option `1` and save one or more series.
2. Run option `2` for each series to import episodes.
3. Use options `3` to `11` to query data from the local database.

This order is important because some options depend on already persisted series/episodes.

## Important Notes

- The OMDb key is currently fixed in `Principal.java` (`API_KEY` hardcoded). To use your own key, change this field in the code.
- Plot translation uses OpenAI every time a series is registered; without a valid `OPENAI_API_KEY`, the registration flow may fail.
- There is a `ConsultaGemini` class, but it is not connected to the current main flow.
- The project currently has no REST endpoints; all interactions happen through terminal input/output.

## Troubleshooting

- `Could not resolve placeholder 'CONNECTION_URL'`: set missing environment variables before startup.
- Database connection errors: verify PostgreSQL is running and credentials are correct.
- OpenAI authentication errors: confirm `OPENAI_API_KEY` is valid and exported in the same terminal session.
- Empty results in query options: import series first (option `1`) and episodes (option `2`).
- Genre errors in option `7`: use one of the supported Portuguese values exactly (`Ação`, `Romance`, `Comédia`, `Drama`, `Crime`).

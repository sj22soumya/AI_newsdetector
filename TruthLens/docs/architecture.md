# TruthLens — System Architecture

## Architectural Overview

TruthLens follows a **layered architecture** that cleanly separates concerns across distinct
application layers. Each layer has a single responsibility and communicates only with adjacent layers.

```
┌─────────────────────────────────────────────────────┐
│                   USER (Terminal)                   │
└────────────────────────┬────────────────────────────┘
                         │ input / display
┌────────────────────────▼────────────────────────────┐
│             PRESENTATION LAYER — Main.java          │
│         Menu, Input Reading, Result Display         │
└────────────────────────┬────────────────────────────┘
                         │ delegates to services
┌────────────────────────▼────────────────────────────┐
│              SERVICE LAYER                          │
│  NewsAnalysisService  │  ExplanationService         │
│  HistoryService                                     │
└────┬───────────────────┬────────────────────────────┘
     │ analysis          │ history
┌────▼───────────────┐  ┌▼────────────────────────────┐
│  ANALYSIS LAYER    │  │    REPOSITORY LAYER          │
│  TextAnalyzer      │  │  IRepository<T>              │
│  KeywordAnalyzer   │  │  FileHistoryRepository       │
│  FeatureExtractor  │  └──────────────┬───────────────┘
│  RuleBasedClass.   │                 │ file I/O
└────────────────────┘  ┌─────────────▼───────────────┐
                        │      DATA LAYER              │
                        │   data/history.txt           │
                        └─────────────────────────────┘
```

---

## Layer Responsibilities

### Presentation Layer (`Main.java`)
- Renders menus and prompts
- Reads user input from `Scanner`
- Calls service methods
- Displays results and error messages
- Catches and handles all exceptions — never crashes

### Service Layer (`service/`)
- **`NewsAnalysisService`**: Orchestrates the full analysis pipeline
- **`ExplanationService`**: Converts features + signals into human-readable explanation
- **`HistoryService`**: Provides CRUD operations over analysis records

### Analysis Layer (`analyzer/` + `classifier/`)
- **`TextAnalyzer`**: Statistical text metrics
- **`KeywordAnalyzer`**: Keyword category matching
- **`FeatureExtractor`**: Combines both analyzers into `NewsFeatures`
- **`NewsClassifier`** (abstract): Defines classification contract
- **`RuleBasedClassifier`**: Implements weighted scoring

### Repository Layer (`repository/`)
- **`IRepository<T>`**: Generic CRUD interface
- **`FileHistoryRepository`**: File-backed implementation

### Data Layer (`data/`)
- `data/history.txt`: Pipe-delimited flat file storage

### Model Layer (`model/`)
- **`NewsArticle`**: Input data object
- **`NewsFeatures`**: Extracted feature vector
- **`PredictionResult`**: Analysis output
- **`AnalysisRecord`**: Persistent history record

### Utility Layer (`util/`)
- **`TextUtils`**: Text-processing helpers
- **`InputValidator`**: Input validation + exception throwing
- **`FileManager`**: File I/O operations

### Exception Layer (`exception/`)
- **`InvalidNewsException`**: Invalid article input
- **`InvalidInputException`**: Invalid menu or UI input
- **`FileStorageException`**: File read/write failures

---

## Analysis Pipeline

```
NewsArticle (headline + content)
       │
       ▼
FeatureExtractor
  ├── TextAnalyzer
  │     ├── word count
  │     ├── sentence count
  │     ├── uppercase ratio
  │     ├── exclamation ratio
  │     ├── question mark ratio
  │     └── URL count
  └── KeywordAnalyzer
        ├── sensational keywords
        ├── suspicious keywords
        ├── emotional keywords
        └── strong claim keywords
       │
       ▼
NewsFeatures (numerical feature object)
       │
       ▼
RuleBasedClassifier
  ├── sensational score   (0–20)
  ├── suspicious score    (0–20)
  ├── capitalization score(0–15)
  ├── punctuation score   (0–10)
  ├── URL score           (0–15)
  └── emotional score     (0–20)
       │
       ▼
PredictionResult (risk score + classification + signals)
       │
       ▼
ExplanationService (explanation + recommendation)
       │
       ▼
HistoryService → FileHistoryRepository → data/history.txt
```

---

## Design Decisions

| Decision | Rationale |
|----------|-----------|
| Java | Academic requirement; standard library is sufficient |
| Layered architecture | Separation of concerns; each layer testable independently |
| Abstract `NewsClassifier` | Allows future `MLClassifier` without changing the pipeline |
| `IRepository<T>` interface | Decouples storage from business logic; swappable to database later |
| File persistence (no DB) | Simplicity; no external dependencies required |
| Rule-based scoring | Deterministic; transparent; explainable without ML infrastructure |
| Risk score instead of true/false | Honest; avoids false certainty; more useful for education |
| Explanation from detected signals only | Prevents fabrication; builds user trust |

---

## Non-Functional Requirements

### Performance
- Analysis completes in < 1 second for typical article lengths
- History file I/O is synchronous; acceptable for single-user CLI use

### Usability
- Polished terminal UI with clear headings and consistent formatting
- All errors produce user-friendly messages (no stack traces displayed)
- Input validated before processing; clear error messages provided

### Reliability
- Corrupted history records are skipped with a warning; application continues
- Missing `data/` directory created automatically
- Application never crashes on normal user errors

### Maintainability
- Each class has a single responsibility
- Keyword lists are centralised in `KeywordAnalyzer.java`
- New classifiers can be added by extending `NewsClassifier` without modifying the service layer

### Error Handling
- Custom exceptions used throughout
- Every public entry point catches appropriate exceptions
- File errors degrade gracefully (application continues; warns user)

### Resource Efficiency
- No threads, no network, no external processes
- Memory footprint is minimal (< 5 MB typical)
- No persistent database connections

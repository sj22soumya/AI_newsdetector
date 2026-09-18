# TruthLens — Intelligent News Credibility Analysis System

> **Two ways to run:** Web UI (browser on localhost:8080) or Terminal CLI

## Overview

TruthLens is a Java-based command-line application that analyzes news articles and estimates their credibility risk level using rule-based textual signal analysis. It is built as a college academic project demonstrating core Java OOP concepts.

> **Important**: TruthLens is an educational tool. It does **not** determine whether a news claim is factually true or false. It performs an automated text-based risk assessment and explains the detected signals.

---

## Problem

The proliferation of misleading and sensationalized online news is a growing concern. TruthLens helps users become more critical consumers of news by highlighting common textual patterns associated with low-credibility content — such as sensational language, excessive capitalization, clickbait phrases, and emotional manipulation.

---

## Objectives

- Analyze news article text for credibility risk signals
- Generate a deterministic risk score (0–100)
- Classify articles into three risk levels
- Explain the reasons behind the risk assessment
- Maintain a searchable analysis history
- Demonstrate core Java OOP concepts in a real application

---

## Features

| Feature | Description |
|---------|-------------|
| Text Analysis | Word count, sentence count, uppercase ratio, punctuation ratios, URL detection |
| Keyword Analysis | Detects sensational, suspicious, emotional, and strong-claim indicators |
| Feature Extraction | Converts raw text into numerical feature vectors |
| Rule-Based Classification | Weighted scoring engine producing 0–100 risk score |
| Explanation Engine | Explains only the signals that were actually triggered |
| Recommendation | Classification-appropriate action recommendation |
| History Management | Save, load, search, delete, and clear analysis records |
| File Analysis | Analyze `.txt` files using the same pipeline as direct input |
| Persistence | Records stored in `data/history.txt` using pipe-delimited format |

---

## Functional Modules

1. **News Input** — Enter text or load from a `.txt` file
2. **Text Analysis** — Statistical analysis of the article text
3. **Keyword Analysis** — Centralized keyword category matching
4. **Feature Extraction** — Numerical feature generation
5. **Classification Engine** — Rule-based risk score calculation
6. **Explanation Engine** — Signal-based explanation generation
7. **Recommendations** — Classification-appropriate guidance
8. **Analysis History** — Full CRUD history management
9. **Repository Layer** — Generic file-backed repository
10. **Custom Exceptions** — Graceful error handling
11. **CLI Interface** — Polished terminal-based UI
12. **File Analysis** — Local file reading via same pipeline
13. **History Search** — Keyword search across stored records
14. **About System** — System information and concept overview

---

## Java Concepts Demonstrated

| Concept | Where Used |
|---------|-----------|
| Encapsulation | All model classes (private fields, getters/setters) |
| Inheritance | `RuleBasedClassifier` extends `NewsClassifier` |
| Polymorphism | `NewsClassifier` reference holds `RuleBasedClassifier` object |
| Abstraction | Abstract class `NewsClassifier`, interface `IRepository<T>` |
| Interfaces | `IRepository<T>` implemented by `FileHistoryRepository` |
| Generics | `IRepository<T>`, `List<AnalysisRecord>`, `Map<String, Number>` |
| Collections | `List`, `Map`, `Set` used throughout |
| Exception Handling | try/catch in all user-facing operations |
| Custom Exceptions | `InvalidNewsException`, `InvalidInputException`, `FileStorageException` |
| File Handling | `FileManager` reads/writes `data/history.txt` |
| Packages | `model`, `analyzer`, `classifier`, `service`, `repository`, `exception`, `util` |

---

## Technologies

- **Language**: Java
- **JDK**: 17 or newer
- **Build**: Standard `javac` — no Maven, Gradle, or external libraries required
- **Storage**: Plain text file (`data/history.txt`)
- **Interface**: Terminal / Command Line

---

## Project Structure

```
TruthLens/
├── src/
│   ├── Main.java
│   ├── model/
│   │   ├── NewsArticle.java
│   │   ├── NewsFeatures.java
│   │   ├── PredictionResult.java
│   │   └── AnalysisRecord.java
│   ├── analyzer/
│   │   ├── TextAnalyzer.java
│   │   ├── KeywordAnalyzer.java
│   │   └── FeatureExtractor.java
│   ├── classifier/
│   │   ├── NewsClassifier.java      (abstract)
│   │   └── RuleBasedClassifier.java
│   ├── service/
│   │   ├── NewsAnalysisService.java
│   │   ├── ExplanationService.java
│   │   └── HistoryService.java
│   ├── repository/
│   │   ├── IRepository.java         (interface)
│   │   └── FileHistoryRepository.java
│   ├── exception/
│   │   ├── InvalidNewsException.java
│   │   ├── InvalidInputException.java
│   │   └── FileStorageException.java
│   └── util/
│       ├── TextUtils.java
│       ├── InputValidator.java
│       └── FileManager.java
├── data/
│   └── history.txt
├── tests/
│   ├── TruthLensTest.java
│   ├── sensational_article.txt
│   └── normal_article.txt
├── docs/
│   ├── architecture.md
│   ├── workflow.md
│   ├── use-case.md
│   ├── class-diagram.md
│   ├── sequence-diagram.md
│   ├── storage-design.md
│   └── testing.md
├── out/                (compiled .class files)
├── README.md
├── statement.md
└── .gitignore
```

---

## Requirements

- Java JDK 17 or newer
- Windows, macOS, or Linux terminal

Verify Java installation:
```
java -version
javac -version
```

---

## Installation

```bash
# Clone or download the project
cd TruthLens
```

---

## Compilation

From the `TruthLens/` directory:

```bash
javac -d out -sourcepath src src/model/NewsArticle.java src/model/NewsFeatures.java src/model/PredictionResult.java src/model/AnalysisRecord.java src/exception/InvalidNewsException.java src/exception/InvalidInputException.java src/exception/FileStorageException.java src/util/TextUtils.java src/util/InputValidator.java src/util/FileManager.java src/analyzer/TextAnalyzer.java src/analyzer/KeywordAnalyzer.java src/analyzer/FeatureExtractor.java src/classifier/NewsClassifier.java src/classifier/RuleBasedClassifier.java src/service/ExplanationService.java src/repository/IRepository.java src/repository/FileHistoryRepository.java src/service/HistoryService.java src/service/NewsAnalysisService.java src/Main.java
```

**Windows one-liner (PowerShell):**
```powershell
javac -d out -sourcepath src (Get-ChildItem -Recurse src\*.java | Select-Object -ExpandProperty FullName)
```

---

## Running the Application

```bash
java -cp out Main
```

---

## Running Tests

Compile tests:
```bash
javac -d out -sourcepath src -cp out tests/TruthLensTest.java
```

Run tests:
```bash
java -cp out TruthLensTest
```

Expected output: `21 passed | 0 failed | 21 total — ALL TESTS PASSED`

---

## Testing

The automated test suite (`tests/TruthLensTest.java`) covers:

1. Valid article analysis
2. Empty headline rejection
3. Empty content rejection
4. Short content rejection
5. Sensational article → high risk score
6. Normal article → low risk score
7. URL detection
8. Excessive capitalization detection
9. Excessive punctuation detection
10. Suspicious keyword detection
11. History save
12. History persistence (load on restart simulation)
13. History search by keyword
14. History delete
15. History clear
16. Invalid menu input (3 sub-tests)
17. Invalid file path (2 sub-tests)

---

## Data Storage

Analysis history is stored in `data/history.txt`.

**Format (pipe-delimited):**
```
TL-XXXXXXXX|2026-09-16 14:30:22|Headline text here|48|NEEDS VERIFICATION
```

- Pipes within headlines are escaped as `{{PIPE}}`
- The `data/` directory is created automatically if it does not exist
- Missing or corrupted records are skipped with a warning
- The file is never required to be present before first run

---

## Limitations

- **Not a fact-checker**: TruthLens cannot verify whether claims are factually accurate
- **Textual signals only**: Rule-based scoring can produce false positives and false negatives
- **No source verification**: Does not assess the credibility of cited sources
- **English only**: Keyword lists are English-language only
- **Writing style ≠ truth**: A well-written article may still contain false claims

---

## Future Enhancements

- Machine learning classifier (replace `RuleBasedClassifier` with `MLClassifier`)
- Natural language processing (NLP) integration
- Source credibility analysis
- External fact-checking API integration
- Browser extension
- Web interface (Spring Boot / React)
- Database storage (SQLite / PostgreSQL)
- Multi-language keyword support

---

## Disclaimer

> This system provides an automated risk assessment based on textual signals. It does not establish whether a news claim is factually true or false. Results should be used as a starting point for further verification, not as a definitive determination of accuracy.

---

## Author

TruthLens — College Academic Project  
Java Programming, OOP Principles  
JDK 17+

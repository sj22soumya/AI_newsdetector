# TruthLens — Storage Design

## Overview

TruthLens uses a simple, flat-file persistence strategy.
All analysis history records are stored in a single text file: `data/history.txt`.
No external database is required.

---

## File Location

```
TruthLens/
└── data/
    └── history.txt
```

- The `data/` directory is created automatically if it does not exist.
- The `history.txt` file is created on first save if it does not exist.
- Relative paths are used throughout — no absolute paths hardcoded.

---

## Record Format

Each record occupies a single line in the file.
Fields are separated by a pipe character (`|`).

**Format:**
```
id|timestamp|headline|riskScore|classification
```

**Example:**
```
TL-A3F2B91C|2026-09-16 14:30:22|Scientists Find New Mars Evidence|12|POTENTIALLY CREDIBLE
TL-8D1E4F2A|2026-09-16 15:12:45|SHOCKING SECRET EXPOSED!!!|76|POTENTIALLY MISLEADING
```

---

## Field Definitions

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `id` | String | Unique analysis ID (prefix "TL-" + 8 hex chars) | `TL-A3F2B91C` |
| `timestamp` | String | ISO-style date-time: `yyyy-MM-dd HH:mm:ss` | `2026-09-16 14:30:22` |
| `headline` | String | The article headline (pipes escaped as `{{PIPE}}`) | `Coffee Study Published` |
| `riskScore` | int | Risk score 0–100 | `48` |
| `classification` | String | One of the three classification labels | `NEEDS VERIFICATION` |

---

## Pipe Escaping

If a headline contains a `|` character, it is replaced with `{{PIPE}}` before storage
and restored when reading.

**Example:**
- Stored: `Breaking: CEO|CFO resign`
- In file: `TL-XXXXXXXX|...|Breaking: CEO{{PIPE}}CFO resign|...|...`
- Restored: `Breaking: CEO|CFO resign`

---

## Write Process

1. A new `AnalysisRecord` is created after each analysis.
2. `HistoryService.saveRecord()` is called.
3. `FileHistoryRepository.save()` appends the record to the in-memory cache.
4. `persistAll()` rewrites the entire file from the cache.
5. `FileManager.writeLines()` opens the file in overwrite mode and writes all lines.

---

## Read Process

1. On first `getAll()` call, `FileHistoryRepository` lazy-loads the file.
2. `FileManager.readLines()` reads all non-empty lines.
3. Each line is parsed using `AnalysisRecord.fromStorageString()`.
4. Lines with fewer than 5 pipe-separated fields are skipped with a warning.
5. Lines with unparseable integer fields are skipped with a warning.
6. Valid records are added to the in-memory cache.

---

## Error Handling

| Scenario | Behaviour |
|----------|-----------|
| `data/` directory missing | Created automatically by `FileManager.ensureParentDirectory()` |
| `history.txt` not found (first run) | Returns empty list; no error |
| File is empty | Returns empty list; no error |
| Corrupted line (wrong fields) | Skipped; warning printed to stderr; other records loaded normally |
| File read permission denied | `FileStorageException` logged to stderr; application continues with empty history |
| File write permission denied | `FileStorageException` logged to stderr; in-memory session continues normally |

---

## Limitations

- **Plain text**: Not suitable for large datasets (thousands of records).
- **No concurrent access**: Designed for single-user CLI use only.
- **No encryption**: Headlines and classifications are stored in plaintext.

---

## Future Enhancement

The `IRepository<T>` interface allows the file-based repository to be replaced by
a database-backed implementation (e.g., SQLite, H2, or PostgreSQL) without modifying
`HistoryService` or any other layer.

```java
// Example future extension:
class SQLiteHistoryRepository implements IRepository<AnalysisRecord> { ... }
// Inject into HistoryService — zero changes to service or presentation layer.
```

# TruthLens — Testing Documentation

## Test Strategy

TruthLens uses an automated unit-level test suite (`tests/TruthLensTest.java`) that exercises
all major functional modules without requiring user interaction.

Tests are written in plain Java with no external testing framework (JUnit is not required).
Custom `pass()` / `fail()` helpers count results and print a summary.

---

## Compiling Tests

From the `TruthLens/` directory:

```bash
javac -d out -sourcepath src -cp out tests/TruthLensTest.java
```

## Running Tests

```bash
java -cp out TruthLensTest
```

---

## Test Cases

| # | Test | Description | Expected Outcome |
|---|------|-------------|-----------------|
| 1 | Valid Article Analysis | Analyze a well-formed article with headline and content | Score 0–100, classification set, explanation set |
| 2 | Empty Headline | Pass empty string as headline | `InvalidNewsException` thrown |
| 3 | Empty Content | Pass empty string as content | `InvalidNewsException` thrown |
| 4 | Very Short Content | Pass 5-char string as content | `InvalidNewsException` with minimum length message |
| 5 | Sensational Article | Article with SHOCKING, EXPOSED, DEEP STATE etc. | Risk score ≥ 50, classified as POTENTIALLY MISLEADING |
| 6 | Normal/Credible Article | Balanced reporting without sensational language | Risk score ≤ 30, classified as POTENTIALLY CREDIBLE |
| 7 | URL Detection | Article containing 2 URLs | Risk score > 0; URL signal detected |
| 8 | Excessive Capitalisation | Article with most words in ALL CAPS | Risk score > 10; caps signal detected |
| 9 | Excessive Punctuation | Article with many `!` and `?` marks | Risk score > 5; punctuation signal detected |
| 10 | Suspicious Keywords | Article with "they don't want you to know", "guaranteed" etc. | Risk score > 20; suspicious phrases detected |
| 11 | History Save | Save record after analysis | Record count increases by 1 |
| 12 | History Load | Simulate restart with new service instance | Loaded count equals or exceeds saved count |
| 13 | History Search | Search for a specific unique word in headline | At least 1 result; empty result for non-existent word |
| 14 | History Delete | Delete the last saved record by ID | Delete returns true; false for non-existent ID |
| 15 | History Clear | Clear all records | Record count = 0 after clear |
| 16a | Invalid Menu Input (alpha) | Pass "abc" to menu validator | `InvalidInputException` with "not a valid number" |
| 16b | Invalid Menu Input (range) | Pass "99" to menu with max=8 | `InvalidInputException` with "between 1 and 8" |
| 16c | Invalid Menu Input (empty) | Pass "" to menu validator | `InvalidInputException` with "cannot be empty" |
| 17a | Invalid File Path (missing) | Pass non-existent path to validator | `InvalidInputException` with "File not found" |
| 17b | Invalid File Path (empty) | Pass "" to file path validator | `InvalidInputException` with "cannot be empty" |

**Total: 21 individual assertions**

---

## Verified Test Results

```
============================================================
  TRUTHLENS — AUTOMATED TEST SUITE
============================================================

  TEST 1 — Valid Article Analysis
  [PASS] Valid article analysis completed — Score: 0, Classification: POTENTIALLY CREDIBLE

  TEST 2 — Empty Headline
  [PASS] Correctly threw InvalidNewsException: Headline cannot be empty.

  TEST 3 — Empty Content
  [PASS] Correctly threw InvalidNewsException: Article content cannot be empty.

  TEST 4 — Very Short Content
  [PASS] Correctly rejected short content: Article content is too short...

  TEST 5 — Sensational Article (Expects HIGH score)
  [PASS] Sensational article scored: 76 (POTENTIALLY MISLEADING)

  TEST 6 — Normal/Credible Article (Expects LOW score)
  [PASS] Normal article scored: 7 (POTENTIALLY CREDIBLE)

  TEST 7 — Article with URLs
  [PASS] URL article scored: 19

  TEST 8 — Excessive Capitalisation
  [PASS] Caps article scored: 32

  TEST 9 — Excessive Punctuation
  [PASS] Punctuation article scored: 18

  TEST 10 — Suspicious Keywords
  [PASS] Suspicious keywords article scored: 28

  TEST 11 — History Save
  [PASS] History save works correctly.

  TEST 12 — History Load (Persistence)
  [PASS] History persistence works.

  TEST 13 — History Search
  [PASS] History search found 1 record(s) for 'Zebra'
  [PASS] History search correctly returned empty for missing keyword

  TEST 14 — History Delete
  [PASS] History delete works correctly

  TEST 15 — History Clear
  [PASS] History clear works correctly

  TEST 16 — Invalid Menu Input
  [PASS] Correctly rejected non-numeric input
  [PASS] Correctly rejected out-of-range input
  [PASS] Correctly rejected empty input

  TEST 17 — Invalid File Path
  [PASS] Correctly rejected non-existent file
  [PASS] Correctly rejected empty file path

============================================================
  RESULTS: 21 passed | 0 failed | 21 total
  ALL TESTS PASSED ✓
============================================================
```

---

## Manual Test Scenarios

The following should be verified by running the application interactively:

| Scenario | Steps | Expected |
|----------|-------|----------|
| Full analysis flow | Run app → Option 1 → enter headline + article | Result displayed with all sections |
| File analysis | Run app → Option 2 → enter headline + `tests/sensational_article.txt` | High risk score |
| History persistence | Analyze articles → Exit → Re-run → Option 3 | Same records appear |
| Clear confirmation | Option 6 → type "no" | History not cleared |
| About section | Option 7 | All OOP concepts listed |
| Exit | Option 8 | Goodbye message |

---

## Known Limitations

- History file tests share a common `data/history.txt`; test count depends on prior runs
- No isolation between test runs (not a limitation for demo; noted for awareness)
- Corrupted history file test: manually corrupt `history.txt` and restart — app warns and skips

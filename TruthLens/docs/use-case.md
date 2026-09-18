# TruthLens — Use Case Diagram

## Actors

- **User**: The person interacting with TruthLens via the command-line interface

## Use Case Diagram (Mermaid)

```mermaid
graph TD
    User((User))

    User --> UC1[Analyze News Article\nEnter text directly]
    User --> UC2[Analyze News File\nLoad from .txt file]
    User --> UC3[View Analysis History]
    User --> UC4[Search Analysis History\nBy headline keyword]
    User --> UC5[Delete History Record\nBy record ID]
    User --> UC6[Clear All History]
    User --> UC7[View System Information\nAbout section]

    UC1 --> UC1a[Validate Input]
    UC1 --> UC1b[Run Analysis Pipeline]
    UC1b --> UC1c[Display Result]
    UC1c --> UC1d[Save to History]

    UC2 --> UC2a[Validate File Path]
    UC2 --> UC2b[Read File Content]
    UC2b --> UC1b

    UC4 --> UC4a[Case-Insensitive Keyword Match]
    UC4a --> UC4b[Display Matching Records]

    UC5 --> UC5a[Find Record by ID]
    UC5a --> UC5b[Remove from File]

    UC6 --> UC6a[Confirm Action]
    UC6a --> UC6b[Clear history.txt]
```

---

## Use Case Descriptions

### UC1 — Analyze News Article
- **Actor**: User
- **Trigger**: User selects option 1 from main menu
- **Precondition**: None
- **Steps**:
  1. User enters a headline
  2. User enters article text (two blank lines = done)
  3. System validates inputs
  4. System runs the analysis pipeline
  5. System displays risk score, classification, signals, explanation, recommendation
  6. System saves record to history
- **Postcondition**: Record stored in `data/history.txt`
- **Exceptions**: `InvalidNewsException` for empty/short input

### UC2 — Analyze News File
- **Actor**: User
- **Trigger**: User selects option 2 from main menu
- **Precondition**: A `.txt` file exists on the local filesystem
- **Steps**:
  1. User enters a headline
  2. User enters the file path
  3. System validates the file path
  4. System reads file content
  5. System runs same pipeline as UC1
- **Exceptions**: `InvalidInputException` for missing/unreadable file; `FileStorageException` on read error

### UC3 — View Analysis History
- **Actor**: User
- **Trigger**: User selects option 3
- **Steps**: System loads all records from file and displays them in a table
- **Postcondition**: None (read-only)

### UC4 — Search Analysis History
- **Actor**: User
- **Trigger**: User selects option 4
- **Steps**: User enters keyword; system returns all records with matching headlines
- **Exceptions**: `InvalidInputException` for empty keyword

### UC5 — Delete History Record
- **Actor**: User
- **Trigger**: User selects option 5
- **Steps**: User enters record ID; system deletes matching record and rewrites file
- **Exceptions**: Record not found message if ID invalid

### UC6 — Clear All History
- **Actor**: User
- **Trigger**: User selects option 6
- **Steps**: User confirms; system clears all records from memory and file
- **Precondition**: History has at least one record

### UC7 — View System Information
- **Actor**: User
- **Trigger**: User selects option 7
- **Steps**: System displays project name, purpose, technology, and OOP concepts demonstrated

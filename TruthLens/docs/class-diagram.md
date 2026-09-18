# TruthLens — Class Diagram

## Class Diagram (Mermaid)

```mermaid
classDiagram
    %% ── MODEL ─────────────────────────────────────────
    class NewsArticle {
        -String headline
        -String content
        +NewsArticle()
        +NewsArticle(headline, content)
        +getHeadline() String
        +getContent() String
        +getFullText() String
        +setHeadline(String)
        +setContent(String)
        +toString() String
    }

    class NewsFeatures {
        -int wordCount
        -int sentenceCount
        -int charCount
        -double avgSentenceLength
        -int uppercaseWordCount
        -double uppercaseRatio
        -int exclamationCount
        -double exclamationRatio
        -int questionMarkCount
        -double questionMarkRatio
        -int urlCount
        -int suspiciousKeywordCount
        -int sensationalKeywordCount
        -int emotionalKeywordCount
        -int strongClaimCount
        -List~String~ matchedSensational
        -List~String~ matchedSuspicious
        -List~String~ matchedEmotional
        -List~String~ matchedStrongClaims
        +getters/setters
    }

    class PredictionResult {
        +CREDIBLE String
        +VERIFY String
        +MISLEADING String
        -int riskScore
        -String classification
        -double confidence
        -List~String~ detectedSignals
        -String explanation
        -String recommendation
        -NewsFeatures features
        +getters/setters
        +getConfidencePercent() String
    }

    class AnalysisRecord {
        -String id
        -LocalDateTime timestamp
        -String headline
        -int riskScore
        -String classification
        +AnalysisRecord(id, ts, hl, score, cls)
        +toStorageString() String
        +fromStorageString(line)$ AnalysisRecord
        +getFormattedTimestamp() String
        +toString() String
    }

    PredictionResult --> NewsFeatures : contains

    %% ── ANALYZERS ─────────────────────────────────────
    class TextAnalyzer {
        +analyze(NewsArticle) Map~String,Number~
        +formatStats(Map) String
    }

    class KeywordAnalyzer {
        -Set~String~ SENSATIONAL_KEYWORDS$
        -Set~String~ SUSPICIOUS_KEYWORDS$
        -Set~String~ EMOTIONAL_KEYWORDS$
        -Set~String~ STRONG_CLAIM_KEYWORDS$
        +analyze(String) Map~String,List~String~~
        +getSensationalKeywords()$ Set~String~
        +getSuspiciousKeywords()$ Set~String~
        +getEmotionalKeywords()$ Set~String~
        +getStrongClaimKeywords()$ Set~String~
    }

    class FeatureExtractor {
        -TextAnalyzer textAnalyzer
        -KeywordAnalyzer keywordAnalyzer
        +extract(NewsArticle) NewsFeatures
        +getTextAnalyzer() TextAnalyzer
    }

    FeatureExtractor --> TextAnalyzer : uses
    FeatureExtractor --> KeywordAnalyzer : uses
    FeatureExtractor --> NewsFeatures : creates
    TextAnalyzer --> NewsArticle : reads
    KeywordAnalyzer --> NewsArticle : reads

    %% ── CLASSIFIER ────────────────────────────────────
    class NewsClassifier {
        <<abstract>>
        +CREDIBLE_MAX int
        +VERIFY_MAX int
        +classify(NewsFeatures) PredictionResult*
        #scoreToLabel(int) String
        #calculateConfidence(int) double
        #clamp(int, int) int
    }

    class RuleBasedClassifier {
        -MAX_SENSATIONAL int
        -MAX_SUSPICIOUS int
        -MAX_CAPS int
        -MAX_PUNCTUATION int
        -MAX_URL int
        -MAX_EMOTIONAL int
        +classify(NewsFeatures) PredictionResult
        -scoreSensational(NewsFeatures) int
        -scoreSuspicious(NewsFeatures) int
        -scoreCaps(NewsFeatures) int
        -scorePunctuation(NewsFeatures) int
        -scoreUrls(NewsFeatures) int
        -scoreEmotional(NewsFeatures) int
        -buildSignals(NewsFeatures,...) List~String~
    }

    NewsClassifier <|-- RuleBasedClassifier : extends
    RuleBasedClassifier --> NewsFeatures : reads
    RuleBasedClassifier --> PredictionResult : creates

    %% ── SERVICES ──────────────────────────────────────
    class NewsAnalysisService {
        -FeatureExtractor featureExtractor
        -NewsClassifier classifier
        -ExplanationService explanationService
        -HistoryService historyService
        +NewsAnalysisService(HistoryService)
        +analyze(NewsArticle) PredictionResult
        +analyzeFile(filePath, headline) PredictionResult
        +getLastStats(NewsArticle) Map~String,Number~
        +getTextAnalyzer() TextAnalyzer
    }

    class ExplanationService {
        -DISCLAIMER String$
        +generateExplanation(PredictionResult)
        +getDisclaimer() String
        -buildExplanation(PredictionResult) String
        -buildRecommendation(String) String
    }

    class HistoryService {
        -IRepository~AnalysisRecord~ repository
        +saveRecord(AnalysisRecord)
        +getAllRecords() List~AnalysisRecord~
        +findById(String) AnalysisRecord
        +searchByHeadline(String) List~AnalysisRecord~
        +deleteRecord(String) boolean
        +clearHistory()
        +getRecordCount() int
    }

    NewsAnalysisService --> FeatureExtractor : uses
    NewsAnalysisService --> NewsClassifier : uses
    NewsAnalysisService --> ExplanationService : uses
    NewsAnalysisService --> HistoryService : uses
    NewsAnalysisService --> NewsArticle : reads
    NewsAnalysisService --> PredictionResult : produces

    %% ── REPOSITORY ────────────────────────────────────
    class IRepository~T~ {
        <<interface>>
        +save(T)
        +getAll() List~T~
        +findById(String) T
        +delete(String) boolean
        +clear()
    }

    class FileHistoryRepository {
        -HISTORY_FILE String$
        -List~AnalysisRecord~ cache
        +save(AnalysisRecord)
        +getAll() List~AnalysisRecord~
        +findById(String) AnalysisRecord
        +delete(String) boolean
        +clear()
        -ensureLoaded()
        -persistAll()
    }

    IRepository <|.. FileHistoryRepository : implements
    HistoryService --> IRepository : uses
    FileHistoryRepository --> AnalysisRecord : manages

    %% ── UTILITIES ─────────────────────────────────────
    class TextUtils {
        <<utility>>
        +splitWords(String)$ List~String~
        +splitSentences(String)$ List~String~
        +countUppercaseWords(List)$ int
        +countChar(String, char)$ int
        +countUrls(String)$ int
        +extractUrls(String)$ List~String~
        +countKeywordOccurrences(String,String)$ int
        +safeRatio(int,int)$ double
        +truncate(String,int)$ String
    }

    class InputValidator {
        <<utility>>
        +validateHeadline(String)$
        +validateContent(String)$
        +validateFilePath(String)$
        +validateMenuChoice(String,int,int)$ int
        +validateSearchKeyword(String)$
        +validateRecordId(String)$
    }

    class FileManager {
        <<utility>>
        +readLines(String)$ List~String~
        +readFileContent(String)$ String
        +writeLines(String,List)$
        +appendLine(String,String)$
        +deleteFile(String)$
        +ensureParentDirectory(String)$
    }

    %% ── EXCEPTIONS ────────────────────────────────────
    class InvalidNewsException {
        -String field
        +InvalidNewsException(String)
        +InvalidNewsException(String,String)
        +getField() String
    }

    class InvalidInputException {
        -String input
        +InvalidInputException(String)
        +InvalidInputException(String,String)
        +getInput() String
    }

    class FileStorageException {
        -String filePath
        +FileStorageException(String)
        +FileStorageException(String,String)
        +FileStorageException(String,String,Throwable)
        +getFilePath() String
    }

    %% ── MAIN ──────────────────────────────────────────
    class Main {
        -HistoryService historyService
        -NewsAnalysisService analysisService
        -Scanner scanner
        +main(String[])$
        +run()
        -analyzeNewsText()
        -analyzeNewsFile()
        -runAnalysis(headline,content,filePath)
        -viewHistory()
        -searchHistory()
        -deleteHistoryRecord()
        -clearHistory()
        -showAbout()
        -displayAnalysisResult(PredictionResult,Map)
    }

    Main --> NewsAnalysisService : uses
    Main --> HistoryService : uses
    Main --> NewsArticle : creates
    Main --> PredictionResult : displays
    Main --> AnalysisRecord : displays
```

---

## Inheritance Hierarchy

```
RuntimeException
  ├── InvalidNewsException
  ├── InvalidInputException
  └── FileStorageException

NewsClassifier (abstract)
  └── RuleBasedClassifier
      └── (future: MLClassifier)

IRepository<T> (interface)
  └── FileHistoryRepository
```

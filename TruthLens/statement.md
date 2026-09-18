# Problem Statement

## Project: TruthLens — Intelligent News Credibility Analysis System

---

## Problem

In the digital information age, news consumers are frequently exposed to sensationalized, misleading, 
or fabricated content that mimics legitimate journalism. These articles often use specific textual 
patterns — excessive capitalization, emotional language, clickbait phrases, and urgent calls to action 
— to manipulate readers into accepting and sharing unverified information.

Traditional fact-checking relies on human judgment and domain expertise, making it slow and 
unscalable. There is a need for an accessible, automated tool that helps users identify common 
textual risk signals in news content before sharing it.

---

## Scope

TruthLens is a Java-based command-line application that performs **text-based credibility risk 
assessment** on news articles. It does **not** claim to determine factual truth. Instead, it 
analyzes observable textual signals and produces a transparent, explainable risk score.

**In Scope:**
- Text statistical analysis (word count, capitalization, punctuation)
- Keyword-based signal detection (sensational, suspicious, emotional language)
- Rule-based risk scoring (deterministic, 0–100)
- Three-level classification (Potentially Credible / Needs Verification / Potentially Misleading)
- Explanation generation from detected signals only
- Analysis history with persistence, search, and management
- Local file analysis capability
- Full command-line interface
- Modern Web UI dashboard (HTML/CSS/JS) served by embedded Java HTTP Server

**Out of Scope:**
- Live internet scraping or API calls
- Machine learning or neural network models
- External fact-checking database integration
- User authentication or multi-user support
- Mobile app interface

---

## Target Users

- **Students** learning to critically evaluate digital news
- **Educators** teaching media literacy and information verification
- **General public** who want a simple tool to flag potentially misleading content
- **Developers** studying Java OOP concepts through a practical project

---

## High-Level Features

| # | Feature | Description |
|---|---------|-------------|
| 1 | News Text Analysis | Analyze headline and article text entered directly |
| 2 | File Analysis | Analyze content from a local `.txt` file |
| 3 | Risk Scoring | Deterministic 0–100 risk score using weighted textual signals |
| 4 | Classification | Three-level risk classification with clear labels |
| 5 | Explanation | Human-readable explanation of detected signals |
| 6 | Recommendations | Contextual guidance based on classification result |
| 7 | History Tracking | Persistent record of all analyses |
| 8 | History Search | Search past records by headline keyword |
| 9 | History Management | View, delete individual records, or clear all history |
| 10 | Input Validation | Robust error handling for all user inputs |
| 11 | Disclaimer | Clear disclosure that results are not absolute truth determinations |
| 12 | System Information | About section explaining OOP concepts demonstrated |

---

## Disclaimer

TruthLens is an educational tool. Its assessments are based entirely on textual signals 
and writing patterns. It cannot verify factual claims, access external databases, or 
establish the truth or falsity of any statement. Users should always verify important 
information using multiple reliable sources.

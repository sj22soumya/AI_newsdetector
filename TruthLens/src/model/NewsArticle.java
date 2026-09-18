package model;

/**
 * Represents a news article with a headline and body content.
 * Demonstrates encapsulation with private fields and public accessors.
 */
public class NewsArticle {

    private String headline;
    private String content;

    // Default constructor
    public NewsArticle() {
        this.headline = "";
        this.content  = "";
    }

    // Parameterised constructor
    public NewsArticle(String headline, String content) {
        this.headline = (headline != null) ? headline.trim() : "";
        this.content  = (content  != null) ? content.trim()  : "";
    }

    // --- Getters ---
    public String getHeadline() { return headline; }
    public String getContent()  { return content;  }

    // --- Setters ---
    public void setHeadline(String headline) {
        this.headline = (headline != null) ? headline.trim() : "";
    }

    public void setContent(String content) {
        this.content = (content != null) ? content.trim() : "";
    }

    // Convenience: total text for analysis (headline + content)
    public String getFullText() {
        return headline + " " + content;
    }

    @Override
    public String toString() {
        int preview = Math.min(content.length(), 80);
        return "NewsArticle{headline='" + headline + "', content='" + content.substring(0, preview) + "...'}";
    }
}

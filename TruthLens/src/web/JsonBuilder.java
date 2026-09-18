package web;

/**
 * Lightweight JSON builder — no external libraries needed.
 * Builds JSON strings manually for API responses.
 */
public class JsonBuilder {

    private final StringBuilder sb;
    private boolean firstField = true;

    public JsonBuilder() {
        this.sb = new StringBuilder("{");
    }

    /** Add a string field */
    public JsonBuilder add(String key, String value) {
        comma();
        sb.append("\"").append(escapeJson(key)).append("\":\"")
          .append(escapeJson(value == null ? "" : value)).append("\"");
        return this;
    }

    /** Add a numeric field */
    public JsonBuilder add(String key, int value) {
        comma();
        sb.append("\"").append(escapeJson(key)).append("\":").append(value);
        return this;
    }

    /** Add a double field (2 decimal places) */
    public JsonBuilder add(String key, double value) {
        comma();
        sb.append("\"").append(escapeJson(key)).append("\":")
          .append(String.format("%.2f", value));
        return this;
    }

    /** Add a boolean field */
    public JsonBuilder add(String key, boolean value) {
        comma();
        sb.append("\"").append(escapeJson(key)).append("\":").append(value);
        return this;
    }

    /** Add a raw JSON value (array or nested object) */
    public JsonBuilder addRaw(String key, String rawJson) {
        comma();
        sb.append("\"").append(escapeJson(key)).append("\":").append(rawJson);
        return this;
    }

    public String build() {
        return sb.toString() + "}";
    }

    private void comma() {
        if (!firstField) sb.append(",");
        firstField = false;
    }

    /** Escape special JSON characters in a string */
    public static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /** Convert a Java List<String> to a JSON array string */
    public static String listToJsonArray(java.util.List<String> list) {
        if (list == null || list.isEmpty()) return "[]";
        StringBuilder ab = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) ab.append(",");
            ab.append("\"").append(escapeJson(list.get(i))).append("\"");
        }
        ab.append("]");
        return ab.toString();
    }

    /** Build a simple error response */
    public static String error(String message) {
        return new JsonBuilder()
            .add("success", false)
            .add("error", message)
            .build();
    }

    /** Build a simple success response */
    public static String success(String message) {
        return new JsonBuilder()
            .add("success", true)
            .add("message", message)
            .build();
    }
}

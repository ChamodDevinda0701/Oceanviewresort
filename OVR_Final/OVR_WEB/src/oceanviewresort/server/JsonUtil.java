package oceanviewresort.server;

import java.util.List;
import java.util.Map;

/**
 * JsonUtil - hand-built JSON builder (no external libraries).
 * Converts Java objects to JSON strings for REST API responses.
 */
public class JsonUtil {

    // ── Primitives ────────────────────────────────────────────
    public static String string(String value) {
        if (value == null) return "null";
        return "\"" + value.replace("\\", "\\\\")
                           .replace("\"", "\\\"")
                           .replace("\n", "\\n") + "\"";
    }

    // ── Success / Error wrappers ──────────────────────────────
    public static String success(String message) {
        return "{\"status\":\"success\",\"message\":" +
               string(message) + "}";
    }

    public static String success(String message, String dataKey,
                                 String dataValue) {
        return "{\"status\":\"success\",\"message\":" +
               string(message) + ",\"" + dataKey + "\":" +
               string(dataValue) + "}";
    }

    public static String error(String message) {
        return "{\"status\":\"error\",\"message\":" +
               string(message) + "}";
    }

    // ── Object builder from Map ───────────────────────────────
    public static String object(Map<String, String> fields) {
        StringBuilder sb = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (i++ > 0) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            String v = entry.getValue();
            // If value looks like a number or boolean, don't quote it
            if (v != null && (v.matches("-?\\d+(\\.\\d+)?") ||
                v.equals("true") || v.equals("false"))) {
                sb.append(v);
            } else {
                sb.append(string(v));
            }
        }
        sb.append("}");
        return sb.toString();
    }

    // ── Array builder ─────────────────────────────────────────
    public static String array(List<String> items) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(items.get(i));
        }
        sb.append("]");
        return sb.toString();
    }

    // ── Parse a single field from a JSON string ───────────────
    public static String parseField(String json, String field) {
        String key = "\"" + field + "\"";
        int idx = json.indexOf(key);
        if (idx < 0) return null;
        int colon = json.indexOf(":", idx);
        if (colon < 0) return null;
        int start = colon + 1;
        while (start < json.length() &&
               json.charAt(start) == ' ') start++;
        if (json.charAt(start) == '"') {
            int end = json.indexOf("\"", start + 1);
            // handle escaped quotes
            while (end > 0 && json.charAt(end - 1) == '\\')
                end = json.indexOf("\"", end + 1);
            return json.substring(start + 1, end);
        } else {
            int end = start;
            while (end < json.length() &&
                   json.charAt(end) != ',' &&
                   json.charAt(end) != '}') end++;
            return json.substring(start, end).trim();
        }
    }
}

package com.travel.util;

public final class HtmlEscaper {
    private HtmlEscaper() {
    }

    public static String escapeHtml(Object value) {
        if (value == null) {
            return "";
        }

        String text = String.valueOf(value);
        StringBuilder escaped = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            switch (text.charAt(i)) {
                case '&' -> escaped.append("&amp;");
                case '<' -> escaped.append("&lt;");
                case '>' -> escaped.append("&gt;");
                case '"' -> escaped.append("&quot;");
                case '\'' -> escaped.append("&#39;");
                default -> escaped.append(text.charAt(i));
            }
        }
        return escaped.toString();
    }
}

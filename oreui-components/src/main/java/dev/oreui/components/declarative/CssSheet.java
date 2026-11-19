package dev.oreui.components.declarative;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class CssSheet {
    private final List<Rule> rules;

    private CssSheet(List<Rule> rules) {
        this.rules = List.copyOf(rules);
    }

    public static CssSheet parse(String css) {
        List<Rule> rules = new ArrayList<>();
        int index = 0;
        while (index < css.length()) {
            int open = css.indexOf('{', index);
            if (open < 0) {
                break;
            }
            int close = css.indexOf('}', open + 1);
            if (close < 0) {
                break;
            }
            String selector = css.substring(index, open).trim();
            String body = css.substring(open + 1, close);
            if (!selector.isEmpty()) {
                rules.add(new Rule(selector, declarations(body)));
            }
            index = close + 1;
        }
        return new CssSheet(rules);
    }

    public Map<String, Float> styleFor(HtmlNode node) {
        Map<String, Float> style = new LinkedHashMap<>();
        String className = node.attribute("class");
        for (Rule rule : rules) {
            if (rule.matches(node.tag(), className)) {
                style.putAll(rule.declarations);
            }
        }
        return style;
    }

    private static Map<String, Float> declarations(String body) {
        Map<String, Float> values = new LinkedHashMap<>();
        for (String declaration : body.split(";")) {
            int colon = declaration.indexOf(':');
            if (colon <= 0) {
                continue;
            }
            String name = declaration.substring(0, colon).trim().toLowerCase(Locale.ROOT);
            String value = declaration.substring(colon + 1).trim().toLowerCase(Locale.ROOT);
            if (!name.equals("width") && !name.equals("height")
                    && !name.equals("gap") && !name.equals("padding")) {
                continue;
            }
            try {
                if (value.endsWith("px")) {
                    value = value.substring(0, value.length() - 2);
                } else if (value.endsWith("f")) {
                    value = value.substring(0, value.length() - 1);
                }
                values.put(name, Float.parseFloat(value));
            } catch (NumberFormatException ignored) {
                continue;
            }
        }
        return values;
    }

    private record Rule(String selector, Map<String, Float> declarations) {
        private boolean matches(String tag, String className) {
            String normalized = selector.toLowerCase(Locale.ROOT).trim();
            if (normalized.startsWith(".")) {
                return className != null && List.of(className.split("\\s+")).contains(normalized.substring(1));
            }
            int dot = normalized.indexOf('.');
            if (dot > 0) {
                String selectorTag = normalized.substring(0, dot);
                String selectorClass = normalized.substring(dot + 1);
                return selectorTag.equals(tag) && className != null
                        && List.of(className.split("\\s+")).contains(selectorClass);
            }
            return normalized.equals(tag);
        }
    }
}
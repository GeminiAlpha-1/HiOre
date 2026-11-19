package dev.oreui.core.text;

public final class TextGeometry {
    public static final String ELLIPSIS = "…";
    private static final String START_FORBIDDEN = "，。、；：？！）】》」』%,.;:?!)]}";
    private static final String END_FORBIDDEN = "（【《「『([{";
    private static final int CJK_PUNCT_START = 12288;
    private static final int CJK_PUNCT_END = 12351;
    private static final int FULLWIDTH_START = 65280;
    private static final int FULLWIDTH_END = 65535;

    private TextGeometry() {
    }

    public static void wrap(String text, float maxWidth, TextMeasure measure, int maxLines, TextLayout out) {
        if (measure == null) {
            throw new NullPointerException("measure");
        }
        if (out == null) {
            throw new NullPointerException("out");
        }
        out.clear();
        if (text == null || text.isEmpty() || !(maxWidth > 0f) || maxLines <= 0) {
            return;
        }
        StringBuilder current = out.buffer();
        int index = 0;
        while (index < text.length()) {
            int codePoint = text.codePointAt(index);
            int charCount = Character.charCount(codePoint);
            current.appendCodePoint(codePoint);
            if (measure.width(current) <= maxWidth) {
                index += charCount;
                continue;
            }
            if (current.length() == charCount) {
                out.add(current.toString());
                current.setLength(0);
                index += charCount;
                if (out.size() >= maxLines) {
                    return;
                }
                continue;
            }
            current.setLength(current.length() - charCount);
            if (out.size() >= maxLines - 1) {
                out.add(truncate(current.toString(), maxWidth, measure));
                return;
            }
            String head = current.toString();
            int lastLength = Character.charCount(head.codePointBefore(head.length()));
            String tail = head.substring(head.length() - lastLength);
            if (START_FORBIDDEN.indexOf(codePoint) >= 0) {
                String trimmed = head.substring(0, head.length() - lastLength);
                if (!trimmed.isEmpty()) {
                    out.add(trimmed);
                }
                current.setLength(0);
                current.append(tail);
            } else if (END_FORBIDDEN.indexOf(head.codePointBefore(head.length())) >= 0) {
                String trimmed = head.substring(0, head.length() - lastLength);
                if (!trimmed.isEmpty()) {
                    out.add(trimmed);
                }
                current.setLength(0);
                current.append(tail);
            } else {
                boolean latin = !isCjk(codePoint) && !isCjk(head.codePointBefore(head.length()));
                int space = latin ? head.lastIndexOf(' ') : -1;
                if (space > 0) {
                    out.add(head.substring(0, space));
                    current.setLength(0);
                    current.append(head.substring(space + 1));
                } else {
                    out.add(head);
                    current.setLength(0);
                }
            }
            current.appendCodePoint(codePoint);
            index += charCount;
        }
        if (current.length() > 0) {
            out.add(current.toString());
        }
    }

    public static String ellipsize(String text, float maxWidth, TextMeasure measure) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (measure == null) {
            throw new NullPointerException("measure");
        }
        if (measure.width(text) <= maxWidth) {
            return text;
        }
        return truncate(text, maxWidth, measure);
    }

    public static String clipToWidth(String text, float maxWidth, TextMeasure measure) {
        if (text == null || text.isEmpty() || !(maxWidth > 0f)) {
            return "";
        }
        if (measure == null) {
            throw new NullPointerException("measure");
        }
        if (measure.width(text) <= maxWidth) {
            return text;
        }
        StringBuilder out = new StringBuilder();
        for (int index = 0; index < text.length(); ) {
            int codePoint = text.codePointAt(index);
            out.appendCodePoint(codePoint);
            if (measure.width(out) > maxWidth) {
                out.setLength(out.length() - Character.charCount(codePoint));
                break;
            }
            index += Character.charCount(codePoint);
        }
        return out.toString();
    }

    private static String truncate(String text, float maxWidth, TextMeasure measure) {
        StringBuilder candidate = new StringBuilder(text);
        while (true) {
            String line = candidate + ELLIPSIS;
            if (measure.width(line) <= maxWidth) {
                return line;
            }
            if (candidate.length() == 0) {
                return ELLIPSIS;
            }
            candidate.setLength(candidate.length() - Character.charCount(candidate.codePointBefore(candidate.length())));
        }
    }

    private static boolean isCjk(int codePoint) {
        return Character.UnicodeScript.of(codePoint) == Character.UnicodeScript.HAN
                || (codePoint >= CJK_PUNCT_START && codePoint <= CJK_PUNCT_END)
                || (codePoint >= FULLWIDTH_START && codePoint <= FULLWIDTH_END);
    }
}
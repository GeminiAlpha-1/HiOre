package dev.oreui.core.text;

@FunctionalInterface
public interface TextMeasure {
    int width(CharSequence text);
}
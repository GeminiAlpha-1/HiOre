package dev.oreui.components;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class ComponentRegistry {
    private final Map<String, Entry> entries = new LinkedHashMap<>();
    private IFeatureFlag featureFlags = feature -> true;

    public ComponentRegistry setFeatureFlags(IFeatureFlag featureFlags) {
        if (featureFlags == null) {
            throw new NullPointerException("featureFlags");
        }
        this.featureFlags = featureFlags;
        return this;
    }

    public ComponentRegistry register(String id, IWidgetFactory factory) {
        return register(id, null, factory);
    }

    public ComponentRegistry register(String id, String feature, IWidgetFactory factory) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        entries.put(id, new Entry(factory, feature));
        return this;
    }

    public OreWidget create(String id, ComponentContext context) {
        if (context == null) {
            throw new NullPointerException("context");
        }
        Entry entry = entries.get(id);
        if (entry == null) {
            throw new IllegalArgumentException("unknown widget id: " + id);
        }
        if (entry.feature != null && !featureFlags.isEnabled(entry.feature)) {
            return null;
        }
        return entry.factory.create(context);
    }

    public boolean contains(String id) {
        return entries.containsKey(id);
    }

    public Set<String> ids() {
        return Set.copyOf(entries.keySet());
    }

    private record Entry(IWidgetFactory factory, String feature) {
    }
}
package dev.oreui.theme;

import java.util.EnumMap;
import java.util.Map;

public final class OreTheme {
    private final EnumMap<ColorRole, Integer> colors;
    private final EnumMap<Space, Integer> spaces;
    private final EnumMap<SizeToken, Integer> sizes;
    private final EnumMap<Radius, Integer> radii;
    private final EnumMap<Border, Integer> borders;
    private final EnumMap<TypeRole, TypeStyle> types;
    private final EnumMap<Motion, Integer> motions;
    private final EnumMap<AssetKey, String> assets;
    private final EnumMap<SoundRole, String> sounds;
    private final EnumMap<ButtonVariant, EnumMap<ControlState, StateStyle>> states;

    private OreTheme(Builder builder) {
        colors = new EnumMap<>(builder.colors);
        spaces = new EnumMap<>(builder.spaces);
        sizes = new EnumMap<>(builder.sizes);
        radii = new EnumMap<>(builder.radii);
        borders = new EnumMap<>(builder.borders);
        types = new EnumMap<>(builder.types);
        motions = new EnumMap<>(builder.motions);
        assets = new EnumMap<>(builder.assets);
        sounds = new EnumMap<>(builder.sounds);
        states = new EnumMap<>(ButtonVariant.class);
        for (ButtonVariant variant : ButtonVariant.values()) {
            states.put(variant, new EnumMap<>(builder.states.get(variant)));
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public int color(ColorRole role) {
        return require(colors, role);
    }

    public int space(Space space) {
        return require(spaces, space);
    }

    public int size(SizeToken size) {
        return require(sizes, size);
    }

    public int radius(Radius radius) {
        return require(radii, radius);
    }

    public int border(Border border) {
        return require(borders, border);
    }

    public TypeStyle type(TypeRole role) {
        return require(types, role);
    }

    public int motion(Motion motion) {
        return require(motions, motion);
    }

    public String asset(AssetKey key) {
        return require(assets, key);
    }

    public String sound(SoundRole role) {
        return require(sounds, role);
    }

    public StateStyle state(ButtonVariant variant, ControlState state) {
        EnumMap<ControlState, StateStyle> byState = states.get(variant);
        if (byState == null) {
            throw new IllegalStateException("missing theme variant: " + variant);
        }
        return require(byState, state);
    }

    public Builder derive() {
        return new Builder(this);
    }

    private static <K, V> V require(Map<K, V> map, K key) {
        V value = map.get(key);
        if (value == null) {
            throw new IllegalStateException("missing theme token: " + key);
        }
        return value;
    }

    public static final class Builder {
        private final EnumMap<ColorRole, Integer> colors = new EnumMap<>(ColorRole.class);
        private final EnumMap<Space, Integer> spaces = new EnumMap<>(Space.class);
        private final EnumMap<SizeToken, Integer> sizes = new EnumMap<>(SizeToken.class);
        private final EnumMap<Radius, Integer> radii = new EnumMap<>(Radius.class);
        private final EnumMap<Border, Integer> borders = new EnumMap<>(Border.class);
        private final EnumMap<TypeRole, TypeStyle> types = new EnumMap<>(TypeRole.class);
        private final EnumMap<Motion, Integer> motions = new EnumMap<>(Motion.class);
        private final EnumMap<AssetKey, String> assets = new EnumMap<>(AssetKey.class);
        private final EnumMap<SoundRole, String> sounds = new EnumMap<>(SoundRole.class);
        private final EnumMap<ButtonVariant, EnumMap<ControlState, StateStyle>> states =
                new EnumMap<>(ButtonVariant.class);

        private Builder() {
            initStates();
        }

        private Builder(OreTheme source) {
            colors.putAll(source.colors);
            spaces.putAll(source.spaces);
            sizes.putAll(source.sizes);
            radii.putAll(source.radii);
            borders.putAll(source.borders);
            types.putAll(source.types);
            motions.putAll(source.motions);
            assets.putAll(source.assets);
            sounds.putAll(source.sounds);
            for (ButtonVariant variant : ButtonVariant.values()) {
                states.put(variant, new EnumMap<>(source.states.get(variant)));
            }
        }

        public Builder override(ColorRole role, int argb) {
            if (role == null) {
                throw new NullPointerException("role");
            }
            colors.put(role, argb);
            return this;
        }

        public Builder override(Space space, int value) {
            if (space == null) {
                throw new NullPointerException("space");
            }
            spaces.put(space, value);
            return this;
        }

        public Builder override(SizeToken size, int value) {
            if (size == null) {
                throw new NullPointerException("size");
            }
            sizes.put(size, value);
            return this;
        }

        public Builder override(Radius radius, int value) {
            if (radius == null) {
                throw new NullPointerException("radius");
            }
            radii.put(radius, value);
            return this;
        }

        public Builder override(Border border, int value) {
            if (border == null) {
                throw new NullPointerException("border");
            }
            borders.put(border, value);
            return this;
        }

        public Builder override(TypeRole role, TypeStyle style) {
            if (role == null || style == null) {
                throw new NullPointerException("type role/style");
            }
            types.put(role, style);
            return this;
        }

        public Builder override(Motion motion, int millis) {
            if (motion == null) {
                throw new NullPointerException("motion");
            }
            motions.put(motion, millis);
            return this;
        }

        public Builder override(AssetKey key, String value) {
            if (key == null || value == null) {
                throw new NullPointerException("asset key/value");
            }
            assets.put(key, value);
            return this;
        }

        public Builder override(SoundRole role, String value) {
            if (role == null || value == null) {
                throw new NullPointerException("sound role/value");
            }
            sounds.put(role, value);
            return this;
        }

        public Builder overrideState(ButtonVariant variant, ControlState state, StateStyle style) {
            if (variant == null || state == null || style == null) {
                throw new NullPointerException("variant/state/style");
            }
            EnumMap<ControlState, StateStyle> byState = states.get(variant);
            if (byState == null) {
                byState = new EnumMap<>(ControlState.class);
                states.put(variant, byState);
            }
            byState.put(state, style);
            return this;
        }

        public OreTheme build() {
            return new OreTheme(this);
        }

        private void initStates() {
            for (ButtonVariant variant : ButtonVariant.values()) {
                EnumMap<ControlState, StateStyle> byState = new EnumMap<>(ControlState.class);
                for (ControlState state : ControlState.values()) {
                    byState.put(state, StateStyle.ForVariant.of(variant, state));
                }
                states.put(variant, byState);
            }
        }
    }
}
package com.adminmenu.client;

import com.adminmenu.ClientConfig;
import net.minecraft.util.Mth;

/** Accent colour of the menu. Everything tinted (tabs, buttons, lines) is derived from one RGB value. */
public final class Theme {
    public record Preset(String name, int rgb) {}

    public static final Preset[] PRESETS = {
            new Preset("Cyan", 0x4FD8E8),
            new Preset("Blue", 0x60A5FA),
            new Preset("Violet", 0xA78BFA),
            new Preset("Pink", 0xF472B6),
            new Preset("Crimson", 0xF05A6E),
            new Preset("Orange", 0xFB923C),
            new Preset("Gold", 0xF5C451),
            new Preset("Emerald", 0x34D399),
    };

    private static int accent = -1;

    private Theme() {}

    public static int accent() {
        if (accent < 0) {
            try {
                accent = ClientConfig.ACCENT.get();
            } catch (Exception e) {
                accent = 0x4FD8E8;
            }
        }
        return accent;
    }

    /** Applies the colour immediately; call {@link #save()} when the user is done choosing. */
    public static void set(int rgb) {
        accent = rgb & 0xFFFFFF;
    }

    public static void save() {
        try {
            ClientConfig.ACCENT.set(accent());
            ClientConfig.ACCENT.save();
        } catch (Exception ignored) {
            // config not ready; the colour still applies for this session
        }
    }

    /** Second colour of the gradients: same colour, hue shifted a little and slightly darker. */
    public static int accent2() {
        float[] hsv = hsv(accent());
        return Mth.hsvToRgb((hsv[0] + 0.14f) % 1f, Math.min(1f, hsv[1] * 0.9f), hsv[2] * 0.85f);
    }

    public static float[] hsv(int rgb) {
        float r = ((rgb >> 16) & 255) / 255f, g = ((rgb >> 8) & 255) / 255f, b = (rgb & 255) / 255f;
        float max = Math.max(r, Math.max(g, b)), min = Math.min(r, Math.min(g, b));
        float d = max - min, h;
        if (d == 0) h = 0;
        else if (max == r) h = ((g - b) / d) % 6f;
        else if (max == g) h = (b - r) / d + 2f;
        else h = (r - g) / d + 4f;
        h /= 6f;
        if (h < 0) h += 1f;
        return new float[]{h, max == 0 ? 0 : d / max, max};
    }
}

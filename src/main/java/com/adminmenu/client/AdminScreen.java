package com.adminmenu.client;

import com.adminmenu.net.Net;
import com.adminmenu.net.Packets;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.IntConsumer;

/**
 * The admin menu. Immediate-mode: every frame the layout is computed, drawn and the clickable
 * areas are collected into {@link #hits}, so there are no widget objects to keep in sync.
 */
public class AdminScreen extends Screen {
    private static final int W = 240;

    private static final int BG = 0xFF0D1322, BORDER = 0xFF26324D, CARD = 0xFF141C2F, CARD_HI = 0xFF1D2843;
    private static final int TEXT = 0xFFE6ECF8, DIM = 0xFF8A97B5, GREEN = 0xFF4ADE80;
    private static final String[] MODES = {"Survival", "Creative", "Adventure", "Spectator"};
    private static final int[] MODE_COL = {0xFF4ADE80, 0xFF38BDF8, 0xFFFBBF24, 0xFFA78BFA};

    private enum Tab {
        GAMEMODE("Gamemode", Items.GRASS_BLOCK),
        TP("TP", Items.ENDER_PEARL),
        KILL("Kill", Items.WITHER_SKELETON_SKULL),
        ITEMS("Items", Items.CHEST),
        GIVE("Give", Items.DIAMOND),
        SPECTATE("Spectate", Items.SPYGLASS),
        SKILLS("Skills", Items.NETHER_STAR);

        final String label;
        final Item icon;

        Tab(String label, Item icon) {
            this.label = label;
            this.icon = icon;
        }
    }

    private record Hit(int x, int y, int w, int h, IntConsumer onClick) {}

    private static String lastItem = "minecraft:diamond";
    private static String lastCount = "1";

    private final List<Packets.Entry> players = new ArrayList<>();
    private final List<Hit> hits = new ArrayList<>();
    private final List<SkillConfig.Skill> skills = SkillConfig.load();

    private UUID selected;
    /** The Items tab is an action (opens the inventory), so it is never the "current" tab. */
    private Tab tab = Tab.TP;
    private int page, pages = 1, skillScroll, px, py, ph, ticks, refreshIn;
    private long killArmedUntil;
    private boolean themeOpen, draggingHue;
    private int[] hueBar, listRect, skillRect;
    private String tip;
    private EditBox itemBox, countBox;

    public AdminScreen() {
        super(Component.literal("Admin Menu"));
    }

    // ------------------------------------------------------------------ setup & data

    @Override
    protected void init() {
        ph = Math.min(height - 8, 350);
        px = (width - W) / 2;
        py = (height - ph) / 2;

        itemBox = new EditBox(font, 0, 0, 100, 16, Component.literal("item"));
        itemBox.setMaxLength(100);
        itemBox.setHint(Component.literal("minecraft:diamond"));
        itemBox.setValue(lastItem);
        countBox = new EditBox(font, 0, 0, 36, 16, Component.literal("count"));
        countBox.setMaxLength(4);
        countBox.setFilter(s -> s.matches("\\d{0,4}"));
        countBox.setValue(lastCount);
        addWidget(itemBox);
        addWidget(countBox);

        requestList();
    }

    @Override
    public void tick() {
        ticks++;
        if (ticks % 100 == 0) requestList();
        if (refreshIn > 0 && --refreshIn == 0) requestList();
        if (itemBox != null) {
            lastItem = itemBox.getValue();
            lastCount = countBox.getValue();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void requestList() {
        Net.CH.sendToServer(new Packets.ListReq());
    }

    public void onList(List<Packets.Entry> list) {
        players.clear();
        players.addAll(list);
        if (selected == null || find(selected) == null) {
            UUID self = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.getUUID() : null;
            if (self != null && find(self) != null) selected = self;
            else selected = players.isEmpty() ? null : players.get(0).id();
        }
    }

    private Packets.Entry find(UUID id) {
        if (id == null) return null;
        for (Packets.Entry e : players) if (e.id().equals(id)) return e;
        return null;
    }

    private void act(Packets.Type type, int n, String s, String s2) {
        if (selected == null) return;
        Net.CH.sendToServer(new Packets.Action(type, selected, s, s2, n));
        refreshIn = 6;
    }

    private void select(UUID id) {
        selected = id;
        killArmedUntil = 0;
    }

    private void clickTab(Tab t, int button) {
        if (t == Tab.ITEMS) {
            // Inv View mod: left click = inventory, right click = ender chest
            act(Packets.Type.VIEW, 0, button == 1 ? "echest" : "inv", "");
        } else {
            tab = t;
            killArmedUntil = 0;
        }
    }

    // ------------------------------------------------------------------ rendering

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        hits.clear();
        tip = null;
        hueBar = null;
        listRect = null;
        skillRect = null;

        g.fill(0, 0, width, height, 0x66000000);
        int ac = 0xFF000000 | Theme.accent(), ac2 = 0xFF000000 | Theme.accent2();
        box(g, px, py, W, ph, BG, BORDER);

        int x = px + 12, w = W - 24, y = py + 10;

        // header
        g.fill(x, y + 1, x + 3, y + 8, ac);
        g.drawString(font, Component.literal("Admin Menu").withStyle(ChatFormatting.BOLD), x + 8, y, TEXT, false);
        String on = players.size() + " online";
        int ow = font.width(on) + 18;
        box(g, x + w - ow, y - 3, ow, 14, CARD, BORDER);
        dot(g, x + w - ow + 6, y + 2, GREEN);
        g.drawString(font, on, x + w - ow + 14, y, TEXT, false);
        y += 17;
        hgrad(g, x, y, w, 2, ac, ac2);
        y += 8;

        if (themeOpen) y = themeStrip(g, x, y, w);
        y = tabs(g, x, y, w, mx, my);

        Packets.Entry sel = find(selected);
        y = card(g, x, y, w, sel) + 6;
        y = actions(g, x, y, w, mx, my, pt, sel);

        // player list
        int footY = py + ph - 24;
        g.drawString(font, "Players (" + players.size() + ")", x, y, DIM, false);
        y += 12;
        int rows = Math.max(1, (footY - 6 - y) / 15);
        pages = Math.max(1, (players.size() + rows - 1) / rows);
        page = Mth.clamp(page, 0, pages - 1);
        listRect = new int[]{x, y, w, rows * 15};
        playerRows(g, x, y, w, mx, my, rows, ac);

        // footer
        String pg = (page + 1) + " / " + pages;
        button(g, x, footY, 16, 14, "<", mx, my, 0, false, () -> page = Math.max(0, page - 1));
        g.drawString(font, pg, x + 21, footY + 3, DIM, false);
        button(g, x + 25 + font.width(pg), footY, 16, 14, ">", mx, my, 0, false, () -> page = Math.min(pages - 1, page + 1));
        button(g, x + w - 52, footY, 52, 14, "Refresh", mx, my, 0, false, this::requestList);
        button(g, x + w - 52 - 4 - 46, footY, 46, 14, "Theme", mx, my, 0, themeOpen, () -> themeOpen = !themeOpen);

        if (tip != null) g.renderTooltip(font, Component.literal(tip), mx, my);
    }

    private int themeStrip(GuiGraphics g, int x, int y, int w) {
        int ac = Theme.accent();
        int step = (w - 14) / (Theme.PRESETS.length - 1);
        for (int i = 0; i < Theme.PRESETS.length; i++) {
            Theme.Preset p = Theme.PRESETS[i];
            int sx = x + i * step;
            box(g, sx, y, 14, 14, 0xFF000000 | p.rgb(), p.rgb() == ac ? TEXT : BORDER);
            hits.add(new Hit(sx, y, 14, 14, b -> {
                if (b == 0) {
                    Theme.set(p.rgb());
                    Theme.save();
                }
            }));
        }
        int hy = y + 18;
        for (int i = 0; i < w; i++) g.fill(x + i, hy, x + i + 1, hy + 8, 0xFF000000 | Mth.hsvToRgb(i / (float) w, 0.65f, 1f));
        int mark = x + (int) (Theme.hsv(ac)[0] * w);
        g.fill(mark - 1, hy - 2, mark + 1, hy + 10, 0xFFFFFFFF);
        hueBar = new int[]{x, hy, w, 8};
        return y + 34;
    }

    private int tabs(GuiGraphics g, int x, int y, int w, int mx, int my) {
        int gap = 4, tw = (w - gap * 2) / 3, th = 22;
        int ac = 0xFF000000 | Theme.accent();
        Tab[] all = Tab.values();
        for (int i = 0; i < all.length; i++) {
            Tab t = all[i];
            int cx = x + (i % 3) * (tw + gap), cy = y + (i / 3) * (th + gap);
            boolean on = t == tab, hov = inside(mx, my, cx, cy, tw, th);
            box(g, cx, cy, tw, th, on ? lerp(CARD, ac, 0.22f) : hov ? CARD_HI : CARD, on ? ac : BORDER);
            int total = 16 + 3 + font.width(t.label);
            int ix = cx + (tw - total) / 2;
            g.renderItem(new ItemStack(t.icon), ix, cy + 3);
            g.drawString(font, t.label, ix + 19, cy + 7, on ? TEXT : 0xFFC3CCE3, false);
            if (on) g.fill(cx + 4, cy + th - 2, cx + tw - 4, cy + th - 1, ac);
            if (hov && t == Tab.ITEMS) tip = "Open this player's inventory (right-click: ender chest)";
            hits.add(new Hit(cx, cy, tw, th, b -> {
                if (b == 0 || (b == 1 && t == Tab.ITEMS)) clickTab(t, b);
            }));
        }
        return y + 3 * (th + gap) + 2;
    }

    private int card(GuiGraphics g, int x, int y, int w, Packets.Entry e) {
        box(g, x, y, w, 38, CARD, BORDER);
        if (e == null) {
            g.drawString(font, "No player selected", x + 8, y + 15, DIM, false);
            return y + 38;
        }
        face(g, e.id(), x + 5, y + 5, 28);
        g.drawString(font, Component.literal(e.name()).withStyle(ChatFormatting.BOLD), x + 40, y + 6, TEXT, false);
        String coords = e.x() + " " + e.y() + " " + e.z();
        g.drawString(font, coords, x + w - 6 - font.width(coords), y + 6, 0xFF000000 | Theme.accent(), false);
        int m = Mth.clamp(e.mode(), 0, 3);
        dot(g, x + 40, y + 23, MODE_COL[m]);
        g.drawString(font, MODES[m] + " Mode", x + 48, y + 21, DIM, false);
        String d = dim(e.dim());
        g.drawString(font, d, x + w - 6 - font.width(d), y + 21, DIM, false);
        return y + 38;
    }

    private int actions(GuiGraphics g, int x, int y, int w, int mx, int my, float pt, Packets.Entry e) {
        boolean give = tab == Tab.GIVE;
        itemBox.visible = give;
        countBox.visible = give;
        if (!give) {
            itemBox.setFocused(false);
            countBox.setFocused(false);
        }

        switch (tab) {
            case GAMEMODE -> {
                int bw = (w - 4) / 2;
                for (int i = 0; i < 4; i++) {
                    int mode = i;
                    button(g, x + (i % 2) * (bw + 4), y + (i / 2) * 20, bw, 16, MODES[i], mx, my, 0,
                            e != null && e.mode() == i, () -> act(Packets.Type.GAMEMODE, mode, "", ""));
                }
                return y + 44;
            }
            case TP -> {
                button(g, x, y, w, 18, "Teleport to this player", mx, my, 1, false, () -> act(Packets.Type.TP_TO, 0, "", ""));
                button(g, x, y + 22, w, 18, "Bring them to me", mx, my, 0, false, () -> act(Packets.Type.BRING, 0, "", ""));
                return y + 44;
            }
            case KILL -> {
                boolean armed = System.currentTimeMillis() < killArmedUntil;
                button(g, x, y, w, 18, armed ? "Click again to confirm" : "Kill this player", mx, my, 2, false, () -> {
                    if (System.currentTimeMillis() < killArmedUntil) {
                        killArmedUntil = 0;
                        act(Packets.Type.KILL, 0, "", "");
                    } else {
                        killArmedUntil = System.currentTimeMillis() + 2500;
                    }
                });
                return y + 22;
            }
            case GIVE -> {
                itemBox.setX(x);
                itemBox.setY(y);
                itemBox.setWidth(w);
                itemBox.render(g, mx, my, pt);
                countBox.setX(x);
                countBox.setY(y + 20);
                countBox.render(g, mx, my, pt);
                int rem = w - 40, bw = (rem - 4) / 2;
                button(g, x + 40, y + 20, bw, 16, "Give", mx, my, 1, false, () -> {
                    int n = 1;
                    try {
                        n = Integer.parseInt(countBox.getValue());
                    } catch (NumberFormatException ignored) {
                        // keep 1
                    }
                    act(Packets.Type.GIVE, n, itemBox.getValue().trim(), "");
                });
                button(g, x + 40 + bw + 4, y + 20, rem - bw - 4, 16, "Held item", mx, my, 0, false, () -> {
                    ItemStack st = Minecraft.getInstance().player.getMainHandItem();
                    ResourceLocation key = st.isEmpty() ? null : ForgeRegistries.ITEMS.getKey(st.getItem());
                    if (key != null) itemBox.setValue(key.toString());
                });
                return y + 40;
            }
            case SPECTATE -> {
                button(g, x, y, w, 18, "Spectate this player", mx, my, 1, false, () -> act(Packets.Type.SPECTATE, 0, "", ""));
                return y + 22;
            }
            case SKILLS -> {
                int visible = 4, rowH = 17;
                skillRect = new int[]{x, y, w, visible * rowH};
                if (skills.isEmpty()) {
                    g.drawString(font, "Add skills in config/adminmenu_skills.json", x, y + 4, DIM, false);
                }
                skillScroll = Mth.clamp(skillScroll, 0, Math.max(0, skills.size() - visible));
                for (int i = 0; i < visible && skillScroll + i < skills.size(); i++) {
                    SkillConfig.Skill s = skills.get(skillScroll + i);
                    int ry = y + i * rowH;
                    boolean hov = inside(mx, my, x, ry, w, 16);
                    box(g, x, ry, w, 16, hov ? CARD_HI : CARD, BORDER);
                    g.drawString(font, s.name(), x + 6, ry + 4, TEXT, false);
                    String id = font.plainSubstrByWidth(s.id(), w - 20 - font.width(s.name()));
                    g.drawString(font, id, x + w - 6 - font.width(id), ry + 4, DIM, false);
                    hits.add(new Hit(x, ry, w, 16, b -> {
                        if (b == 0) act(Packets.Type.SKILL, 0, s.id(), s.slot());
                    }));
                }
                return y + visible * rowH + 4;
            }
            default -> {
                return y;
            }
        }
    }

    private void playerRows(GuiGraphics g, int x, int y, int w, int mx, int my, int rows, int ac) {
        UUID self = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.getUUID() : null;
        for (int i = 0; i < rows; i++) {
            int idx = page * rows + i;
            if (idx >= players.size()) break;
            Packets.Entry e = players.get(idx);
            int ry = y + i * 15;
            boolean sel = e.id().equals(selected), hov = inside(mx, my, x, ry, w, 14);
            box(g, x, ry, w, 14, sel ? lerp(CARD, ac, 0.18f) : hov ? CARD_HI : CARD, sel ? ac : BORDER);
            face(g, e.id(), x + 3, ry + 2, 10);
            String name = e.name() + (e.id().equals(self) ? " · you" : "");
            g.drawString(font, name, x + 17, ry + 3, TEXT, false);
            int m = Mth.clamp(e.mode(), 0, 3);
            dot(g, x + w - 9, ry + 5, MODE_COL[m]);
            g.drawString(font, MODES[m], x + w - 14 - font.width(MODES[m]), ry + 3, DIM, false);
            hits.add(new Hit(x, ry, w, 14, b -> {
                if (b == 0) select(e.id());
            }));
        }
    }

    // ------------------------------------------------------------------ drawing helpers

    /** kind: 0 normal, 1 primary (accent gradient), 2 danger. */
    private void button(GuiGraphics g, int x, int y, int w, int h, String label, int mx, int my,
                        int kind, boolean on, Runnable r) {
        boolean hov = inside(mx, my, x, y, w, h);
        int ac = 0xFF000000 | Theme.accent(), ac2 = 0xFF000000 | Theme.accent2();
        if (kind == 1) {
            hgrad(g, x, y, w, h, lerp(0xFF101827, ac, hov ? 0.75f : 0.55f), lerp(0xFF101827, ac2, hov ? 0.75f : 0.55f));
        } else if (kind == 2) {
            box(g, x, y, w, h, hov ? 0xFF4A1F2B : 0xFF33202A, 0xFF7A2E3E);
        } else {
            box(g, x, y, w, h, on ? lerp(CARD, ac, 0.25f) : hov ? CARD_HI : CARD, on ? ac : BORDER);
        }
        g.drawString(font, label, x + (w - font.width(label)) / 2, y + (h - 8) / 2, kind == 0 && !on ? 0xFFC3CCE3 : TEXT, false);
        hits.add(new Hit(x, y, w, h, b -> {
            if (b == 0) r.run();
        }));
    }

    private ResourceLocation skin(UUID id) {
        Minecraft mc = Minecraft.getInstance();
        PlayerInfo info = mc.getConnection() != null ? mc.getConnection().getPlayerInfo(id) : null;
        return info != null ? info.getSkinLocation() : DefaultPlayerSkin.getDefaultSkin(id);
    }

    private void face(GuiGraphics g, UUID id, int x, int y, int size) {
        PlayerFaceRenderer.draw(g, skin(id), x, y, size);
    }

    private static void rr(GuiGraphics g, int x, int y, int w, int h, int c) {
        g.fill(x + 1, y, x + w - 1, y + h, c);
        g.fill(x, y + 1, x + w, y + h - 1, c);
    }

    private static void box(GuiGraphics g, int x, int y, int w, int h, int fill, int border) {
        rr(g, x, y, w, h, border);
        rr(g, x + 1, y + 1, w - 2, h - 2, fill);
    }

    private static void hgrad(GuiGraphics g, int x, int y, int w, int h, int c1, int c2) {
        for (int i = 0; i < w; i++) g.fill(x + i, y, x + i + 1, y + h, lerp(c1, c2, i / (float) Math.max(1, w - 1)));
    }

    private static void dot(GuiGraphics g, int x, int y, int c) {
        g.fill(x + 1, y, x + 3, y + 4, c);
        g.fill(x, y + 1, x + 4, y + 3, c);
    }

    private static int lerp(int a, int b, float t) {
        int r = 0;
        for (int s = 24; s >= 0; s -= 8) {
            int ca = (a >> s) & 255, cb = (b >> s) & 255;
            r |= ((int) (ca + (cb - ca) * t) & 255) << s;
        }
        return r;
    }

    private static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private static String dim(String d) {
        return switch (d) {
            case "overworld" -> "Overworld";
            case "the_nether" -> "Nether";
            case "the_end" -> "The End";
            default -> d;
        };
    }

    // ------------------------------------------------------------------ input

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (super.mouseClicked(mx, my, button)) return true;
        if (hueBar != null && button == 0 && inside(mx, my, hueBar[0], hueBar[1] - 2, hueBar[2], hueBar[3] + 4)) {
            draggingHue = true;
            setHue(mx);
            return true;
        }
        for (int i = hits.size() - 1; i >= 0; i--) {
            Hit h = hits.get(i);
            if (inside(mx, my, h.x(), h.y(), h.w(), h.h())) {
                h.onClick().accept(button);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (draggingHue && hueBar != null) {
            setHue(mx);
            return true;
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (draggingHue) {
            draggingHue = false;
            Theme.save();
        }
        return super.mouseReleased(mx, my, button);
    }

    private void setHue(double mx) {
        float h = Mth.clamp((float) ((mx - hueBar[0]) / hueBar[2]), 0f, 0.999f);
        Theme.set(Mth.hsvToRgb(h, 0.65f, 1f));
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        int dir = delta > 0 ? -1 : 1;
        if (listRect != null && inside(mx, my, listRect[0], listRect[1], listRect[2], listRect[3])) {
            page = Mth.clamp(page + dir, 0, pages - 1);
            return true;
        }
        if (skillRect != null && inside(mx, my, skillRect[0], skillRect[1], skillRect[2], skillRect[3])) {
            skillScroll = Math.max(0, skillScroll + dir);
            return true;
        }
        return super.mouseScrolled(mx, my, delta);
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        boolean typing = itemBox.isFocused() || countBox.isFocused();
        if (!typing && ClientSetup.OPEN.matches(key, scan)) {
            onClose();
            return true;
        }
        return super.keyPressed(key, scan, mods);
    }
}

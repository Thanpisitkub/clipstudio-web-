package com.adminmenu.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Skills shown in the Skills tab. Edit config/adminmenu_skills.json to list every skill you want;
 * each entry runs "/skill add <player> <slot> <id>" on the server.
 */
public final class SkillConfig {
    public record Skill(String name, String id, String slot) {}

    private static final String DEFAULT = """
            [
              {"name": "Guard",           "id": "epicfight:guard",           "slot": "guard"},
              {"name": "Active Guard",    "id": "epicfight:active_guard",    "slot": "guard"},
              {"name": "Parrying",        "id": "epicfight:parrying",        "slot": "guard"},
              {"name": "Roll",            "id": "epicfight:roll",            "slot": "dodge"},
              {"name": "Step",            "id": "epicfight:step",            "slot": "dodge"}
            ]
            """;

    private SkillConfig() {}

    public static List<Skill> load() {
        Path file = FMLPaths.CONFIGDIR.get().resolve("adminmenu_skills.json");
        try {
            if (!Files.exists(file)) Files.writeString(file, DEFAULT);
            try (Reader r = Files.newBufferedReader(file)) {
                JsonArray arr = new Gson().fromJson(r, JsonArray.class);
                List<Skill> out = new ArrayList<>();
                for (JsonElement e : arr) {
                    JsonObject o = e.getAsJsonObject();
                    out.add(new Skill(o.get("name").getAsString(), o.get("id").getAsString(), o.get("slot").getAsString()));
                }
                return out;
            }
        } catch (IOException | RuntimeException e) {
            return new ArrayList<>();
        }
    }
}

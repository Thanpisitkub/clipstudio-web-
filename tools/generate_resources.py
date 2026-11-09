#!/usr/bin/env python3
"""Generates the resource files of Fight Style (item textures, mod icon, item models, language file, Epic Fight capability files).

Run from the project root:  python3 tools/generate_resources.py
Needs Pillow (pip install pillow) for the two PNG files.
"""
import json
import os

from PIL import Image

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "src", "main", "resources")
MODID = "fiststyle"

# number, preset id, internal name, impact, armor_negation (%), max_strikes   (Epic Fight weapon attributes, see capability files)
STYLES = [
    (1, "light_fist", "Light Fist", 1.0, 0.0, 2),
    (2, "heavy_fist", "Heavy Fist", 3.5, 20.0, 3),
    (3, "drunken_fist", "Drunken Fist", 2.5, 10.0, 3),
    (4, "dragon_fist", "Dragon Fist", 2.0, 15.0, 3),
    (5, "shadow_fist", "Shadow Fist", 1.2, 25.0, 2),
    (6, "tiger_fist", "Tiger Fist", 2.0, 10.0, 2),
    (7, "thunder_fist", "Thunder Fist", 1.5, 5.0, 3),
    (8, "wind_fist", "Wind Fist", 1.0, 0.0, 4),
    (9, "iron_fist", "Iron Fist", 3.0, 35.0, 2),
    (10, "flame_fist", "Flame Fist", 2.0, 15.0, 4),
    (11, "phantom_fist", "Phantom Fist", 1.3, 30.0, 2),
    (12, "god_fist", "God Fist", 3.5, 40.0, 4),
    (13, "sanji_fist", "Sanji Fist", 1.0, 0.0, 4),
]

YELLOW = (255, 221, 0, 255)
BLACK = (0, 0, 0, 255)


def write(path, text):
    full = os.path.normpath(os.path.join(ROOT, path))
    os.makedirs(os.path.dirname(full), exist_ok=True)
    with open(full, "w", encoding="utf-8", newline="\n") as f:
        f.write(text)


def icon(size):
    """A solid black square with a yellow letter F, drawn on a 16x16 grid and scaled up with hard edges."""
    img = Image.new("RGBA", (16, 16), BLACK)
    px = img.load()
    for y in range(3, 13):          # stem
        for x in range(4, 7):
            px[x, y] = YELLOW
    for y in range(3, 6):           # top bar
        for x in range(7, 12):
            px[x, y] = YELLOW
    for y in range(7, 10):          # middle bar
        for x in range(7, 11):
            px[x, y] = YELLOW
    return img.resize((size, size), Image.NEAREST)


def main():
    for path, size in (("assets/%s/textures/item/fight_style.png" % MODID, 16), ("logo.png", 128)):
        full = os.path.normpath(os.path.join(ROOT, path))
        os.makedirs(os.path.dirname(full), exist_ok=True)
        icon(size).save(full)

    # Hidden-in-hand model: no elements, so nothing is drawn in first or third person.
    write("assets/%s/models/item/invisible.json" % MODID, json.dumps({
        "textures": {"particle": "%s:item/fight_style" % MODID},
        "elements": [],
    }, indent=2) + "\n")

    lang = {"itemGroup.%s" % MODID: "Fight Style"}

    for number, preset, name, impact, armor, strikes in STYLES:
        item = "fight_style_%d" % number
        write("assets/%s/models/item/%s.json" % (MODID, item), json.dumps({
            "loader": "forge:separate_transforms",
            "base": {"parent": "minecraft:item/generated", "textures": {"layer0": "%s:item/fight_style" % MODID}},
            "perspectives": {
                "firstperson_righthand": {"parent": "%s:item/invisible" % MODID},
                "firstperson_lefthand": {"parent": "%s:item/invisible" % MODID},
                "thirdperson_righthand": {"parent": "%s:item/invisible" % MODID},
                "thirdperson_lefthand": {"parent": "%s:item/invisible" % MODID},
            },
        }, indent=2) + "\n")
        write("data/%s/capabilities/weapons/%s.json" % (MODID, item), json.dumps({
            "type": "%s:%s" % (MODID, preset),
            "attributes": {"common": {"armor_negation": armor, "impact": impact, "max_strikes": strikes}},
        }, indent=2) + "\n")
        lang["item.%s.%s" % (MODID, item)] = "Fight Style %d" % number

    write("assets/%s/lang/en_us.json" % MODID, json.dumps(lang, indent=2) + "\n")
    write("pack.mcmeta", json.dumps({"pack": {"description": "Fight Style resources", "pack_format": 15}}, indent=2) + "\n")


if __name__ == "__main__":
    main()

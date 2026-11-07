#!/usr/bin/env python3
"""Copies the Epic Sanji keyframe files from YOUR Epic Sanji jar into the built Fight Style jar.

Epic Sanji is "All rights reserved", so these files are not stored in this (public) repository.
Without them Wind Fist and Flame Fist use their fallback moves (Blackstar / Solar).

Usage:
    python3 tools/add_sanji.py EpicSanji-20.1.6.1.jar fiststyle-1.20.1.jar fiststyle-1.20.1-sanji.jar

The result is a new jar; the input jars are not changed.
"""
import sys
import zipfile

SRC_PREFIX = "assets/epicsanji/animmodels/animations/"
DST_PREFIX = "assets/fiststyle/animmodels/animations/"
NEEDED = [
    "biped/combat/sanji_auto_1.json", "biped/combat/sanji_auto_2.json",
    "biped/combat/sanji_auto_3.json", "biped/combat/sanji_auto_4.json",
    "biped/combat/sanji_concasser.json",
    "biped/skill/sanji_diable_auto_1.json", "biped/skill/sanji_diable_auto_2.json",
    "biped/skill/sanji_diable_auto_3.json", "biped/skill/sanji_diable_auto_4.json",
    "biped/skill/sanji_diable_antimaner.json",
]


def main():
    if len(sys.argv) != 4:
        sys.exit(__doc__)
    sanji_jar, mod_jar, out_jar = sys.argv[1:]

    with zipfile.ZipFile(sanji_jar) as sanji:
        names = set(sanji.namelist())
        missing = [n for n in NEEDED if SRC_PREFIX + n not in names]
        if missing:
            sys.exit("Not found in the Epic Sanji jar: " + ", ".join(missing))

        with zipfile.ZipFile(mod_jar) as src, zipfile.ZipFile(out_jar, "w", zipfile.ZIP_DEFLATED) as out:
            for item in src.infolist():
                out.writestr(item, src.read(item.filename))
            for n in NEEDED:
                out.writestr(DST_PREFIX + n, sanji.read(SRC_PREFIX + n))

    print("Wrote", out_jar, "with", len(NEEDED), "Epic Sanji animation files")


if __name__ == "__main__":
    main()

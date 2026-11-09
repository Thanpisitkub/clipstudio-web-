# Fight Style

ม็อดสไตล์การต่อสู้สำหรับ Epic Fight (Minecraft 1.20.1, Forge, Java 17) มีไอเท็ม 12 ชิ้น
ชื่อ **Fight Style 1** ถึง **Fight Style 12** อยู่ในแท็บ Creative ชื่อ "Fight Style"

ต้องติดตั้งคู่กัน: Epic Fight 20.14.17 และ Weapons of Miracles 2.0.171
ไอเท็มทุกชิ้นใช้ไอคอนเดียวกัน (ตัว F สีเหลืองบนพื้นดำ) และมองไม่เห็นในมือทั้งมุมมองบุคคลที่ 1 และ 3

## ไอเท็มและสไตล์

| ไอเท็ม | id | สไตล์ | ท่าที่ใช้ (ทั้งหมดมาจาก Weapons of Miracles) | ดาเมจ (ค่าเริ่มต้น) |
|---|---|---|---|---|
| Fight Style 1 | `fiststyle:fight_style_1` | Light Fist | Ender Blaster มือเดียว + Agony (วิ่ง/กระโดด) | 3.90 – 5.00 |
| Fight Style 2 | `fiststyle:fight_style_2` | Heavy Fist | Torment | 5.00 – 8.00 |
| Fight Style 3 | `fiststyle:fight_style_3` | Drunken Fist | ท่า Heavy Fist สลับลำดับ | 4.50 – 7.00 |
| Fight Style 4 | `fiststyle:fight_style_4` | Dragon Fist | Orbit | 4.20 – 6.00 |
| Fight Style 5 | `fiststyle:fight_style_5` | Shadow Fist | Moonless | 3.60 – 5.20 |
| Fight Style 6 | `fiststyle:fight_style_6` | Tiger Fist | Agony | 4.50 – 6.50 |
| Fight Style 7 | `fiststyle:fight_style_7` | Thunder Fist | Nova | 4.00 – 5.80 |
| Fight Style 8 | `fiststyle:fight_style_8` | Wind Fist | Epic Sanji (สำรอง: Blackstar) | 3.50 – 5.00 |
| Fight Style 9 | `fiststyle:fight_style_9` | Iron Fist | Antitheus | 5.00 – 7.50 |
| Fight Style 10 | `fiststyle:fight_style_10` | Flame Fist | Epic Sanji Diable Jambe (สำรอง: Solar Obscuridad) | 4.50 – 6.50 |
| Fight Style 11 | `fiststyle:fight_style_11` | Phantom Fist | Ender Blaster สองมือ | 3.80 – 5.50 |
| Fight Style 12 | `fiststyle:fight_style_12` | God Fist | Herrscher | 5.50 – 8.00 |

ทุกสไตล์ attack speed 2.10 ช่วงดาเมจของ Light Fist และ Heavy Fist ตามที่กำหนด ส่วนสไตล์อื่นเป็นค่าที่ผมตั้งให้เอง (แก้ได้ใน config)
คอมโบมี 6 ช่อง: หมัด 1, 2, 3, 4, ท่าวิ่ง, ท่ากระโดด ดาเมจหมัด 1–4 ไล่จากค่าต่ำสุดไปสูงสุด ท่าวิ่งและท่ากระโดดใช้ค่ากลาง

## ตั้งค่า (ไม่ต้อง build ใหม่)

- `<โลก>/serverconfig/fiststyle-server.toml` : ดาเมจต่ำสุด/สูงสุด และ attack speed ของแต่ละสไตล์ (คำนวณฝั่งเซิร์ฟเวอร์เท่านั้น)
  `animationSpeed` ปรับความเร็วเล่นท่าเฉพาะตอนถือไอเท็มสไตล์นั้น (Light Fist 1.20 เร็วขึ้น, Heavy Fist 0.90 ช้าลงนิดหน่อย, สไตล์อื่น 1.00) โดย attack speed ยังเป็น 2.10
- `config/fiststyle-combos.json` : ชื่อ animation ของแต่ละสไตล์ (ท่ายืน/เดิน/วิ่ง/ป้องกัน และคอมโบ 6 ช่อง) สร้างให้ตอนรันเกมครั้งแรก
  ชื่อใช้รูปแบบ `Class.FIELD` เช่น `Agony.AGONY_AUTO_1` (คลาส: WOM, Agony, Enderblaster, Nova, Orbit, Moonless, Blackstar, Solar, Ruine, Herrscher, Sanji)
  เขียน `A|B` ได้ ถ้าไม่มี A จะใช้ B

## หมัด 3 ของ Light Fist (อัปเปอร์คัต)

ช่องหมัด 3 ตอนนี้ใช้ `Enderblaster.ENDERBLASTER_ONEHAND_AUTO_3` **ยังไม่ได้ยืนยันว่าเป็นท่าต่อยขึ้น**
ลองในเกมแล้วถ้าไม่ใช่ ให้แก้ช่องที่ 3 ของ `light_fist` ใน `config/fiststyle-combos.json`

## Epic Sanji (Wind Fist / Flame Fist)

Epic Sanji เป็น "All rights reserved" และ repo นี้เป็น public จึงไม่ได้เก็บไฟล์ท่าของ Sanji ไว้ใน repo
ถ้าต้องการท่า Sanji ให้รันบนเครื่องคุณ:

```
python3 tools/add_sanji.py EpicSanji-20.1.6.1.jar fiststyle-1.20.1.jar fiststyle-1.20.1-sanji.jar
```

ยังไม่ได้ทดสอบว่าไฟล์ท่าของ Sanji (เขียนไว้สำหรับ Epic Fight รุ่นเก่า) เล่นถูกต้องบน Epic Fight 20.14.17
ถ้าไม่เพิ่มไฟล์ สองสไตล์นี้จะใช้ท่าสำรอง

## ดาวน์โหลด jar

ได้จากหน้า Releases (รุ่น `latest-jar`) หรือแท็บ Actions > Artifacts ชื่อไฟล์ `fiststyle-1.20.1.jar`
เมื่อ push ขึ้น main workflow จะ build ให้อัตโนมัติ หรือกด "Run workflow" เอง
workflow ดาวน์โหลด Epic Fight และ Weapons of Miracles จาก Modrinth ตอน build (ไม่เก็บไว้ใน repo)

Build เองในเครื่อง: ใส่ `epicfight-20.14.17.jar` และ `wom-2.0.171.jar` ในโฟลเดอร์ `libs/` แล้วรัน `gradle build`
(ไม่มี Gradle wrapper ใน repo ให้ใช้ Gradle 8.8 และ Java 17)

## สถานะการทดสอบ

ดูหัวข้อ "ทดสอบแล้ว / ยังไม่ได้ทดสอบ" ใน release notes หรือที่ผมสรุปไว้ในแชท
ทุกอย่างที่เกี่ยวกับการเล่นจริง (animation, คอมโบ, ดาเมจ, multiplayer) ต้องทดสอบในเกม

## หมายเหตุ: skill ของ Weapons of Miracles

ท่าของ WoM (Moonless, Torment, Herrscher, Solar, Antitheus) อ่านข้อมูลจาก skill ของอาวุธนั้น ๆ ถ้าไม่มี skill เกมจะ crash ("SkillDataKey is unregistered")
แต่ละสไตล์จึงติด skill passive/innate ชุดเดียวกับอาวุธ WoM ที่ท่ามาจาก (เช่น Shadow Fist = Lunar Echo, God Fist = Herrscher)
ถ้าเคยรันเกมมาแล้ว ให้ลบ `config/fiststyle-combos.json` เพื่อให้สร้างใหม่ (ไฟล์เก่ายังมีท่ายืนของ Drunken Fist และท่ากระโดดของ Flame Fist แบบเดิม)

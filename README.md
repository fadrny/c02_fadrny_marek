# (FIM UHK) KIKM-KPGR1 - Úloha 2: Vyplnění a ořezání n-úhelníkové oblasti

Tento projekt navazuje na úlohu 1 a rozšiřuje aplikaci o algoritmy pro vyplňování oblastí (Seed Fill, Scan Line) a ořezávání polygonů. Aplikace umožňuje interaktivní zadávání a editaci vektorových tvarů a jejich následnou rasterizaci.

## Funkcionalita a ovládání

Aplikace se ovládá pomocí myši a klávesových zkratek.

### Základní režim (Kreslení a Vyplňování)
* **LMB (Levé tl. myši):** Kreslení úsečky / Aplikace vyplnění (dle zvoleného módu).
* **RMB (Pravé tl. myši):** Přidání bodu polygonu.
    * *Shift + RMB:* Přidání bodu se zarovnáním úhlu (45°).
* **MMB (Střední tl. myši):** Editace (přesun) nejbližšího bodu.
* **Klávesa R:** Zapnutí/vypnutí kreslení obdélníka (zadání diagonálou nebo základnou a výškou).
* **Klávesa P:** Dokončení aktuálního polygonu a začátek nového.
* **Klávesa F:** Přepínání módu levého tlačítka myši (kreslení úsečky / vyplnění oblasti).:
    1.  Lines (pouze obrysy)
    2.  Seed Fill (dle pozadí)
    3.  Seed Fill (dle hranice)
    4.  Scan Line
* **Klávesa C:** Vymazání plátna (Clear).
* **Mezerník:** Změna barvy kreslení.
    * *Shift + Mezerník:* Změna barvy reverzně.


### Režim Ořezávání (Clipping Mode)
* **Klávesa K:** Zapnutí/vypnutí režimu ořezávání.
* **Klávesa 1:** Editace ořezávaného polygonu (Subject).
* **Klávesa 2:** Editace ořezávacího polygonu (Clipper).
* **Klávesa C:** Vymazání vybraného polygonu.
* **MMB / RMB:** Editace a přidávání vrcholů vybraného polygonu.

---

## Zadání úlohy

**2. Průběžná úloha: Vyplnění a ořezání n-úhelníkové oblasti**

```
Navažte na předchozí úlohu implementující zadávání a vykreslování vyplněného polygonu (n-úhelníku).
Použijte strukturu aplikace navrženou v modulu task2 (viz Oliva-obsah-ukázky a návody). Případně ji upravte.
Pro implementaci použijte připravená rozhraní a třídy v package fill.
Implementujte algoritmus semínkového vyplnění rastrově zadané oblasti.
Myší zadanou hranici oblasti vykreslete na rastrovou plochu plátna barvou odlišnou od barvy vyplnění.
Kliknutím vyberte počáteční pixel záplavového algoritmu a plochu vybarvěte.
Uvažujte dvě možnosti hraniční podmínky vyplňování. Jednak omezení barvou pozadí a jednak barvou hranice.
Implementujte funkci pro kreslení obdelníka zadaného základnou a třetím bodem jehož vzdálenost od základny určuje jeho výšku. Zadání základny obdelníka: stisk plus tažení myši, výška obdelníka: výběr bodu kliknutím v prostoru. Pro uložení vytvořte speciální třídu dědící z třídy Polygon.
Implementujte algoritmus ořezání libovolného uzavřeného n-úhelníku konvexním polygonem. Ořezávací polygon může být fixně zadán a musí mít alespoň pět vrcholů. Oba útvary, ořezávaný i ořezávací, jsou zadány polygonem tvořících jejich obvod (geometricky zadaná hranice). Ořezávací polygon uvažujte pouze jako konvexní, případně s kladnou i zápornou orientací vrcholů.
Implementujte Scan-line algoritmus vyplnění geometricky zadané plochy n-úhelníku, který je výsledkem ořezání v předchozím kroku.
Implementujte funkci na klávesu C pro mazání plátna a všech datových struktur.
Bonus: Doplňte možnost editace již zadaného n-úhelníku, změna pozice vrcholu, případně smazání stávajícího či přidání nového vrcholu.
Bonus2: Při vyplňovaní rastrově i vektorově zadané hranice implementujte také variantu vyplnění útvaru pravidelně se opakujícím vzorem zadaným předpisem v rozhraní PatternFill.
Při hodnocení je důraz kladen na správné vyplnění oblasti a znázornění vykreslení oříznuté oblasti, na kvalitu návrhu a na efektivitu a čitelnost implementace. Vytvořte vhodné třídy implementující dané algoritmy.
Vytvořte si GITový repozitář a pravidelně commitujte postup.
Odevzdávejte prostřednictvím BB (Olivy), před odevzdáním si znovu přečtěte pravidla odevzdávání semestrálních projektů a průběžných úloh.
```

---

## Instalace a spuštění

Projekt vyžaduje **Java 8** nebo novější.

### 1. Spuštění sestaveného souboru (.jar)
Hotový spustitelný soubor naleznete na GitHubu v sekci **[Releases](https://github.com/fadrny/c02_fadrny_marek/releases)**.

1. Stáhněte nejnovější `.jar` soubor.
2. Otevřete terminál (příkazovou řádku) ve složce se staženým souborem.
3. Spusťte aplikaci příkazem:

    ```bash
    java -jar c02_fadrny_marek.jar
    ```

### 2. Sestavení a spuštění z zdrojového kódu
Pokud chcete projekt sestavit sami, postupujte podle následujících kroků:
1. Naklonujte repozitář z GitHubu:

    ```bash
    git clone https://github.com/fadrny/c02_fadrny_marek.git
    cd c02_fadrny_marek
    ```
2. Vytvořte složku pro výstup a zkompilujte zdrojové kódy:

    ```bash
    mkdir -p out
    javac -d out -sourcepath src src/Main.java
    ```
3. Spusťte aplikaci:

    ```bash
    java -cp out Main
    ```

---

## Hodnocení (Grading Table) viz [PozadavkyPGRF1_Task2_2025.docx](/PozadavkyPGRF1_Task2_2025.docx)

| Požadavky | Splněno (%) | Řešení ovládání | Komentáře k řešení |
| :--- | :---: | :--- | :--- |
| **Zobrazení úsečky** | 100 | Levé tlačítko myši | |
| **Polygon** | | | |
| Interaktivní zadání | 100 | Pravé tlačítko myši | |
| Uzavření polygonu | 100 | | |
| Pružné vykreslení hranice | 100 | | |
| Zapouzdření do třídy | 100 | | |
| **Seed Fill – rastrově zadaná hranice** | | | |
| Zadání počátku | 100 | Levé tlačítko myši | F pro změnu módu |
| Zapouzdření do třídy | 100 | | |
| Omezení barvou hranice | 100 | | |
| Omezení barvou pozadí | 100 | | |
| **Scan Line – vektorově zadaná hranice** | | | |
| Správnost vyplnění | 100 | Automaticky (mód 3) | F pro změnu módu |
| Dotahy a přetahy | 100 | | |
| Zapouzdření do třídy | 100 | | |
| Obtažení barvou hranice | 100 | | |
| Implementace algoritmu pro řazení průsečíků | 100 | | |
| **Kreslení obdélníka** | | | |
| Vykreslení obdélníka (základna + výška) | 100 | R | |
| Dědění datových struktur (`Rectangle extends Polygon`) | 100 | | |
| **Ořezání** | | | |
| Správné ořezání polygonem | 100 | K (mód ořezání) | Sutherland-Hodgman |
| Editace ořezávacího polygonu | 100 | MMB, RMB | Přepínání klávesami 1 a 2 |
| Volba orientace polygonu | 100 | | |
| **Ostatní** | | | |
| Mazání struktur a plátna klávesou C | 100 | C | |
| GITový repozitář s výsledným kódem | 100 | | [https://github.com/fadrny/c02_fadrny_marek](https://github.com/fadrny/c02_fadrny_marek) |
| **Bonus** | | | |
| Vyplnění vzorem Scan Line | - | | |
| Vyplnění vzorem Seed Fill | - | | |
| Editace a mazání polygonů a jejich vrcholů | 100 | Stisk kolečka myši a táhnutí | |
| Implementace Seed Fill algoritmu pomocí fronty či zásobníku | 100 | | Stack |
| Pravidelné commity do GIT | 100 | | |
| **Vlastní rozšíření** | | | |
| Přepínání barev | 100 | Stisk mezerníku | |
| Nápověda (Status bar) | 100 | | Zobrazuje aktuální mód a ovládání |
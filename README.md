# Rush Hour — Java parancssoros játék

Kétszemélyes Rush Hour játék, amelyben egy emberi játékos és egy véletlenszerű
lépéseket választó gépi ellenfél játszik. A cél a saját céljármű kijuttatása a
tábláról a hozzá rendelt kijáraton keresztül. A program felülete angol nyelvű.

## Követelmények

- JDK 21
- Apache Maven vagy az IntelliJ IDEA beépített Maven-támogatása
- Internetkapcsolat a függőségek első letöltéséhez

## Fordítás és indítás

A projekt gyökérkönyvtárában, a `pom.xml` mellett:

```sh
mvn clean install
java -jar target/Rushhour-1.0-SNAPSHOT-jar-with-dependencies.jar
```

IntelliJ IDEA használata esetén a projekt Maven-projektként importálható.
A projekt SDK-ját Java 21-re kell állítani, majd az
`src/main/java/org/example/Main.java` osztály `main()` metódusa indítható.
A parancsokat a Run ablakban lehet megadni.

## Játékszabályok

- Az emberi játékos céljárműve **A**, a gépi játékosé **B**.
- Az emberi játékos kezd. Minden érvényes lépése után a gép egy lépést hajt végre.
- A soron lévő játékos bármelyik járművet mozgathatja, az ellenfél céljárművét is.
- A járművek kizárólag a rögzített irányuk mentén csúsztathatók; nem forgathatók.
- Egy lépésben több mező is megtehető, ha a teljes út szabad.
- A járművek nem fedhetik át és nem ugorhatják át egymást.
- A semleges járművek nem hagyhatják el a táblát.
- A céljármű csak a saját kijáratán keresztül, a megfelelő sorban és irányban
  hagyhatja el a táblát.
- A jelenlegi megvalósításban a kijutást egyetlen lépésnek kell végrehajtania:
  a teljes céljárműnek el kell hagynia a táblát. A részleges kilógás nem megengedett.
- A kijutó céljármű tulajdonosa nyer, függetlenül attól, ki mozgatta a járművet.
  A játék a győztes lépés után azonnal véget ér.
- A hibás parancs, az érvénytelen lépés, valamint a `show` és a `help` parancs
  nem adja át a lépés jogát a gépnek.
- Ha egyik járműnek sincs érvényes lépése, a játék döntetlennel zárul.

## Alapértelmezett pálya

```text
. . . . C .
. . . . C .
. A A . C .
. D . . . .
. D B B . .
. D . . . .
```

A `.` üres mezőt jelöl. A modellben a sorok és az oszlopok indexelése **0-tól** indul.

| Jármű | Szerep | Irány | Hossz | Kezdőpozíció (sor, oszlop) |
| --- | --- | --- | --- | --- |
| A | Emberi céljármű | Vízszintes | 2 | (2, 1) |
| B | Gépi céljármű | Vízszintes | 2 | (4, 2) |
| C | Semleges akadály | Függőleges | 3 | (0, 4) |
| D | Semleges akadály | Függőleges | 3 | (3, 1) |

A kijárata a 2. indexű sor jobb szélén, B kijárata a 4. indexű sor bal szélén van.
Kezdetben C akadályozza A, D pedig B kijutását.
A pálya beállításait a `Main.createBoard()` metódus tartalmazza.
A `Board` tetszőleges pozitív sorszámú és oszlopszámú táblát kezel;
az alapértelmezett pálya mérete 6 × 6.

## Parancsok

| Parancs | Művelet |
| --- | --- |
| `show` | Az aktuális tábla és a kijáratok megjelenítése |
| `help` | A parancsok ismertetése |
| `move A 1` | A mozgatása egy mezővel jobbra |
| `move B -1` | B mozgatása egy mezővel balra |
| `move C 3` | C mozgatása három mezővel lefelé |
| `move D -1` | D mozgatása egy mezővel felfelé, ha az út szabad |
| `quit` | Kilépés a programból |

Általános alak: `move <járműazonosító> <lépésszám>`.
A pozitív lépésszám jobbra vagy lefelé, a negatív balra vagy felfelé mozgat.
A nulla lépésszám érvénytelen. A parancsnevek nem különböztetik meg a kis- és
nagybetűket, a járműazonosítók viszont igen (`A` és `a` különböző).
A példák a parancsok alakját szemléltetik; végrehajthatóságuk a tábla állapotától függ.

## Gépi ellenfél

A `ComputerPlayer` véletlenszerűen választ lépést:

1. Összegyűjti azokat a járműveket, amelyeknek van legalább egy érvényes lépése.
2. Véletlenszerűen kiválaszt egy járművet ebből a listából.
3. Véletlenszerűen kiválasztja a jármű egyik érvényes lépését.

A választás a `java.util.Random` segítségével történik. Az algoritmus nem végez
stratégiai keresést és nem részesíti előnyben a győztes lépéseket, ezért akár az
emberi céljármű kijutását is elősegítheti. A lehetséges lépések vizsgálata a
`Board.canMoveVehicle()` metódussal történik, a tábla állapotának módosítása nélkül.

## A program felépítése

| Osztály | Feladat |
| --- | --- |
| `Main` | Az alapértelmezett pálya létrehozása és a `Game.start()` meghívása |
| `Game` | Parancsfeldolgozás, a körök vezérlése és az eredmény közlése |
| `Board` | Járművek kezelése, lépésellenőrzés, mozgatás és győzelem felismerése |
| `Vehicle` | Azonosító, hossz, irány, pozíció és céljárműjelző tárolása |
| `Position` | Megváltoztathatatlan koordinátaérték `equals`, `hashCode` és `toString` metódussal |
| `Orientation` | A vízszintes és függőleges irány felsorolása |
| `Exit` | A kijárathoz rendelt céljármű, sor és oldal tárolása |
| `Move` | Egy jármű és a hozzá tartozó lépésszám tárolása |
| `ComputerPlayer` | Véletlenszerű érvényes gépi lépés kiválasztása |
| `BoardPrinter` | A tábla és a kijáratok konzolos megjelenítése |

A `Game` feldolgozza a parancsot, a `Board` ellenőrzi és végrehajtja a lépést,
a `BoardPrinter` pedig megjeleníti az új állapotot. Érvényes emberi lépés után,
ha még nincs győztes, a `ComputerPlayer` választ lépést a gép számára.
A játékmenet vezérlése nem a `main()` metódusban történik.

## Tesztelés és kódellenőrzés

```sh
mvn test
mvn clean verify
mvn checkstyle:check
```

- `test`: a JUnit 5 tesztek futtatása.
- `verify`: tesztelés, JAR-csomagolás és a JaCoCo-jelentés előállítása.
- `checkstyle:check`: a kódformázási és stílusszabályok külön ellenőrzése.

A JaCoCo lefedettségi jelentése a `target/site/jacoco/index.html` fájlban érhető el,
amely böngészővel nyitható meg.

A tesztek ellenőrzik a modell érvényességi feltételeit, a mozgatást, az ütközéseket,
a kijáratokat, a győzelmet, a parancsfeldolgozást, a gépi lépésválasztást és a
körök váltását. A parancsfeldolgozás tesztjei előre megadott szöveges bemenetet
adnak át egy `Scanner` objektumnak, majd ellenőrzik a tábla állapotát.
A konzolra írt üzenetek szövegét nem hasonlítják össze automatikusan.
A gépi játékos tesztjei rögzített véletlenmagot vagy előre meghatározott lépést
választó tesztobjektumot használnak a megismételhetőség érdekében.

A Maven-konfiguráció JaCoCo-jelentést készít, de jelenleg nem kényszeríti ki
a 80%-os lefedettségi küszöböt. A Checkstyle-ellenőrzés külön paranccsal futtatható,
nincs automatikusan a build életciklusához kötve.

## Jelenlegi funkciók és korlátok

A megvalósítás tartalmazza a játékmodell osztályait, a parancssoros interakciót,
a váltott emberi és gépi köröket, a véletlenszerű gépi ellenfelet, a bal és jobb
oldali kijáratokat, valamint a győzelem és a lépés nélküli döntetlen felismerését.

A kezdőállapot jelenleg a kódban van megadva. A fájlból történő pályabetöltés,
a játékállás mentése és visszatöltése, az adatbázisos eredménytábla, valamint
a felső és alsó kijáratok kezelése még nem része a megvalósításnak.

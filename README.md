# Auction_onlab

1.hét
Erre a hétre a részletes specifikáció megírását, illetve a projekt során használt technológiák összegyűjtését beszéltük meg feladatként. A részletes specifikáció után egy külön fejezetben felvázoltam, hogy milyen ötleteim vannak a mesterséges intelligencia alkalmazására, illetve ezek melyik felületen és hogyan lennének elérhetők.
Ugyanebbe a dokumentumba készítettem egy ábrát és rövid leírást a program által használt technológiákról.
Beszéltük, hogy voltak hiányosságok az adatbázisban. Voltak hiányzó attribútumok (például tárgy állapota) és egy Watch táblára is szükség lenne. Ezeket javítottam.
Elkezdtem elkészíteni a képernyőképeket Just in Mind-ban. Eleinte Figma-ban próbáltam meg, de abban nagyon lassan és nehézkesen sikerült csak, így váltottam a Just in Mind-ra, amiben sokkal gyorsabban tudtam haladni. Elsősorban a főképernyőt, illetve a licitálós oldalt készítettem el, de  remélhetőleg holnap délutánig be tudom fejezni a maradékot is.

2.hét
Erre a hétre az adatbázis és a backend elkészítését kaptam feladatul.
Elkészítettem az adatbázist MS SQL-ben. SQLExpress2022-t használtam, mivel az előző félévben is azt használtuk a JPA-s laboron, és ki volt kötve, hogy legalább Express-re van szükség. Az adatbázisban kialakítottam a táblákat, kapcsolatokat, elsődleges kulcsokat.
Ezt követően inicializáltam a Spring projektet. Gradlet használok (nem pedig Maven-t), ez csak személyes preferenciám, mert gradle-lel már dolgoztam korábban.
Létrehoztam az adatbázis kapcsolatot. Egy ideig nem értettem, miért nem akar létrejönni a kapcsolat, pedig megnéztem és a Servicek között ott futott az adatbázisom is. Végül az volt a gond, hogy alapvetően a MSSQL letiltja a TCP/IP kapcsolatokat. Ezeket engedélyeztem, és az SQL Server Browser-t is, illetve az 1433-mas portot is engedélyeztem a tűzfalban. Ezután már létrejött a kapcsolat.
Ezt követően kialakítottam az Entitás osztályokat, majd a Repository osztályoknak csak pár alapvető függvényét írtam meg. A Servicek-nél és a Controllerek-nél is csak tesztelés céljából írtam meg 1-1 függvényt, viszont végül nem sikerült tesztelni Postman-ból, mert még vannak javításra váró hibák az Entity osztályokban. Jelen állapotban a kód leáll exception miatt.

3.hét
Erre a hétre az API Swagger Editorban történő legenerálását beszéltük meg.
Hétfőn először még az adatbáziskapcsolat kapcsán felmerülő hibát kerestem meg és javítottam. Kiderült, hogy az összetett kulcsot rosszul definiáltam a backendben.
Viszont miután javíttottam utána egy rossz application property miatt (update) felülíródott az adatbázistáblák összetétele, és ezt visszajavítottam.
Ezután Swagger Editorban megírtam a kéréseket, viszont legeneráláskor több opciót is próbáltam, de egyik sem olyan módon generálta le a backend kéréseket, mint ahogy azok nekem kellettek volna.
Így is hasznát tudtam venni mint terv. Először megírtam a DTO-kat, majd az extension function-öket az átalakításhoz az Entityk és a DTo-k között.
Ezután pedig a UserControllert alakítgattam, ez elég nehézkesen ment eleinte, mert kevés official kidolgozott megoldást találtam, ami komplexebben belemenne a dologba.
Végül egészen sikerült megértenem és elkészítettem a UserControllert.
# Auction_szakdolgozat

1.hét
Ezen a héten újra futtattam a projektet. Kijavítottam frontend hibákat, amik a rossz dátumkezelést eredményezték.
Volt egy időzónás probléma is, ami miatt egyből ongoing/expired lett a felrakott aukciós tárgy, ezt fixáltam. 
A státuszokat javítottam, most megfelelő sorrendben jelennek meg és automatikusan frissülnek mind frontenden, mind az adatbázisban.
Ezen kívül videókat néztem a dockerizációról és megírtam az ütemtervet.

2.hét
Kijavítottam az authentikációt, most minden megfelelő backend hívás esetén egyből redirect-el majd átdob a google oauth2 authentikációra.
A dockerizáció alakult, lett is végső docker fájl, ami végül (gyanúsan tárhelyhiány miatt) órákig indult.

3.hét
Utánanéztem a szemantikus keresésnek. Végül arra jutottam, hogy 2 külön microservice-t fogok készíteni, az egyik
embeddinget készít a másik pedig keres és ment egy quadrant vektor alapú adatbázisba.
Ezek közül az előbbi microservicet megvalósítottam.

4.hét 
Sikerült a másik microservice-t is megvalósítani, működik a vektor alapú keresés. Ezen kívül
javítottam kisebb frontend hibákat (túl hosszú skála, összefolyt elemek, rövid keresősáv, stb.). Utánanéztem hogyan lehetne elkészíteni
az agent-et.
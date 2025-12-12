# BidVerse Telepítési Útmutató

Ez a dokumentum leírja a rendszer nulláról való felépítését és indítását.

## Követelmények

*   **Java 21** (Backend)
*   **Node.js 18+** (Frontend)
*   **Python 3.10+** (AI szolgáltatások)
*   **Docker** (Qdrant adatbázishoz)
*   **MS SQL Server** (Helyi telepítés vagy Docker)

---

## 1. Adatbázisok előkészítése

### SQL Server
A rendszer alapértelmezetten a `localhost:1433` címen keresi az adatbázist, amennyiben telepítéskor a NAMED INSTANCE mezőnek adtál.
Amennyiben adtál, akkor a 'spring.datasource.url=jdbc:sqlserver://localhost\\{named_instance}crypt=true;trustServerCertificate=true'-ra cseréld ki ezt a sort,
 ahol a 'named_instance' helyére az általad megadott érték kerül.
Felhasználó: `SA`, Jelszó: `n5m_35z3m_A_73117` (ezeket állítsd át az `application.properties`-ben a saját felhasználó-jelszó kombinációdra).

1.  Indítsd el az SQL Servert.
2.  Hozdd létre BidVerse névvel adatbázist.
3.  Futtasd le a létrehozó szkripteket az `installers` mappából:
    *   `installers/structure.sql` (Táblák létrehozása)
    *   `installers/mintaadat.sql` (Tesztadatok betöltése)

### Qdrant (Vektor adatbázis)
A legegyszerűbb Dockerrel futtatni:
A Docker Desktop telepítése után egyszerűen a terminálban futtasd a lenti parancsot.
```bash
1. docker run -d -p 6333:6333 qdrant/qdrant
Fontos kiemelni, hogy a szemantikus keresés csak általad újonnan létrehozott rekordokon fog működni.
```

---

## 2. Telepítés és Konfiguráció

### Backend (Spring Boot)
```bash
1. Navigálj a GitHub repository gyökérmappájába és ott:
2. cd Backend/BidVerse_backend
3. Végül pedig: gradlew build (java 21-re figyelj)
```

### Frontend (React)
```bash
1. Navigálj a GitHub repository gyökérmappájába és ott:
2. cd Frontend/my-vite-app
3. npm install
4. npm run dev
```

### AI Szolgáltatások (Python)
Mind a 4 AI szolgáltatásnak szüksége van egy `.env` fájlra a mappáján belül, ami tartalmazza a Google API kulcsot:
`GOOGLE_API_KEY=ide_jon_a_kulcs` (AI_AGENT esetén `GEMINI_API_KEY` néven is működik).

**AI_Flask telepítése:**
```bash
1. cd AI_Flask
2. python -m venv venv
3. venv\Scripts\activate
4. pip install -r requirements.txt
5. Futtasd: python app.py
```

**AI_AGENT telepítése:**
```bash
1. cd AI_AGENT
2. python -m venv venv
3. venv\Scripts\activate
4. pip install -r requirements.txt
5. python app.py
```

**AI_SEARCH_FLASK telepítése:**
```bash
1. cd AI_SEARCH_Flask
2. python -m venv venv
3. venv\Scripts\activate
4. pip install -r requirements.txt
5. python app.py


**AI_SEARCH_ALGO telepítése:**
```bash
1. cd AI_SEARCH_ALGO
2. python -m venv venv
3. venv\Scripts\activate
4. pip install -r requirements.txt
5. python search_service_flask.py
```

---

## 3. Indítási sorrend

Javasolt külön terminál ablakokban futtatni őket:

1.  **Qdrant**: `docker start ...` (ha nem futna)
2.  **SQL Server**: (ha nem futna)
3.  **AI_Flask**: `python app.py` (Port: 5000)
3.  **AI_SEARCH_Flask**: `python app.py` (Port: 5001)
4.  **AI_SEARCH_ALGO**: `python search_service_flask.py` (Port: 8001)
5.  **AI_AGENT**: `python app.py` (Port: 5002)
6.  **Backend**: `gradlew bootRun` (Port: 8081)
7.  **Frontend**: `npm run dev` (Port: 5173)

Az alkalmazás ezután elérhető a http://localhost:5173 címen.

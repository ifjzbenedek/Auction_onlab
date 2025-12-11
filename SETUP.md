# BidVerse Telepítési Útmutató

Ez a dokumentum leírja a rendszer nulláról való felépítését és indítását.

## Követelmények

*   **Java 17+** (Backend)
*   **Node.js 18+** (Frontend)
*   **Python 3.10+** (AI szolgáltatások)
*   **Docker** (Qdrant adatbázishoz)
*   **SQL Server** (Helyi telepítés vagy Docker)

---

## 1. Adatbázisok előkészítése

### SQL Server
A rendszer alapértelmezetten a `localhost:1433` címen keresi az adatbázist.
Felhasználó: `SA`, Jelszó: `n5m_35z3m_A_73117` (vagy állítsd át az `application.properties`-ben).

1.  Indítsd el az SQL Servert.
2.  Futtasd le a létrehozó szkripteket az `installers` mappából:
    *   `installers/structure.sql` (Táblák létrehozása)
    *   `installers/mintaadat.sql` (Tesztadatok betöltése)

### Qdrant (Vektor adatbázis)
A legegyszerűbb Dockerrel futtatni:
```bash
docker run -d -p 6333:6333 qdrant/qdrant
```

---

## 2. Telepítés és Konfiguráció

### Backend (Spring Boot)
```bash
cd Backend/BidVerse_backend
./gradlew build
```
*(Windows-on: `gradlew build`)*

### Frontend (React)
```bash
cd Frontend/my-vite-app
npm install
```

### AI Szolgáltatások (Python)
Mindhárom AI szolgáltatásnak szüksége van egy `.env` fájlra a mappáján belül, ami tartalmazza a Google API kulcsot:
`GOOGLE_API_KEY=ide_jon_a_kulcs` (AI_AGENT esetén `GEMINI_API_KEY` néven is működik).

**AI_Flask telepítése:**
```bash
cd AI_Flask
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate
pip install -r requirements.txt
```

**AI_AGENT telepítése:**
```bash
cd AI_AGENT
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

**AI_SEARCH_ALGO telepítése:**
```bash
cd AI_SEARCH_ALGO
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

---

## 3. Indítási sorrend

Javasolt külön terminál ablakokban futtatni őket:

1.  **Qdrant**: `docker start ...` (ha nem futna)
2.  **SQL Server**: (ha nem futna)
3.  **AI_Flask**: `python app.py` (Port: 5000)
4.  **AI_SEARCH_ALGO**: `python search_service_flask.py` (Port: 8001)
5.  **AI_AGENT**: `python app.py` (Port: 5002)
6.  **Backend**: `./gradlew bootRun` (Port: 8081)
7.  **Frontend**: `npm run dev` (Port: 5173)

Az alkalmazás ezután elérhető a http://localhost:5173 címen.

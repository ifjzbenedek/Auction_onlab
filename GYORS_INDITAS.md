# BidVerse - Gyors Indítási Útmutató

## 1️⃣ Egyszerű Docker Compose Indítás (AJÁNLOTT)

### Előfeltételek
- Docker Desktop telepítve
- SQL Server már fut lokálisan (vagy használd a docker-compose.yml-ben levő SQL Server konténert)

### Egy paranccsal indítás:
```powershell
docker-compose up -d
```

Ez elindítja:
- ✅ Qdrant (port 6333)
- ✅ AI_SEARCH_Flask (port 5001)
- ✅ AI_SEARCH_ALGO (port 8001)
- ✅ AI_Flask (port 5000)
- ✅ AI_AGENT (port 5002)
- ✅ Backend (port 8081)
- ✅ Frontend (port 80)

### Adatbázis inicializálás:
```powershell
# SQL Server Management Studio-ban futtasd:
sqlcmd -S localhost -U SA -P n5m_35z3m_A_73117 -i structure.sql
sqlcmd -S localhost -U SA -P n5m_35z3m_A_73117 -i mintaadat.sql
```

### Ellenőrzés:
```powershell
docker-compose ps
```

### Leállítás:
```powershell
docker-compose down
```

---

## 2️⃣ Bash Script Indítás (Windows PowerShell)

### Automatikus indítás scripttel:

Készíts egy `start-all.ps1` fájlt:

```powershell
# BidVerse - Összes szolgáltatás indítása

Write-Host "🚀 BidVerse indítása..." -ForegroundColor Green

# Qdrant
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd AI_SEARCH_ALGO; docker run -p 6333:6333 qdrant/qdrant"

# AI Services
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd AI_SEARCH_Flask; .\venv\Scripts\activate; python app.py"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd AI_SEARCH_ALGO; .\venv\Scripts\activate; python search_service_flask.py"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd AI_Flask; .\venv\Scripts\activate; python app.py"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd AI_AGENT; .\venv\Scripts\activate; python app.py"

# Wait for AI services
Start-Sleep -Seconds 10

# Backend
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd Backend\BidVerse_backend; .\gradlew.bat bootRun"

# Wait for backend
Start-Sleep -Seconds 20

# Frontend
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd Frontend\my-vite-app; npm run dev"

Write-Host "✅ Minden szolgáltatás elindult!" -ForegroundColor Green
Write-Host "🌐 Frontend: http://localhost:5173" -ForegroundColor Cyan
```

**Futtatás:**
```powershell
.\start-all.ps1
```

---

## 3️⃣ Minimal Setup (Csak Backend + Frontend)

Ha nincs szükség AI funkciókra fejlesztés közben:

### Terminal 1 - Backend:
```powershell
cd Backend\BidVerse_backend
.\gradlew.bat bootRun
```

### Terminal 2 - Frontend:
```powershell
cd Frontend\my-vite-app
npm run dev
```

**Figyelem:** AI funkciók nem fognak működni (képgenerálás, keresés, autobid).

---

## 4️⃣ Dockerfile-ok létrehozása (ha még nincsenek)

### Backend Dockerfile
`Backend/BidVerse_backend/Dockerfile`:
```dockerfile
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### AI_SEARCH_ALGO Dockerfile
`AI_SEARCH_ALGO/Dockerfile`:
```dockerfile
FROM python:3.11-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY . .
EXPOSE 8001
CMD ["python", "search_service_flask.py"]
```

### AI_SEARCH_Flask Dockerfile
`AI_SEARCH_Flask/Dockerfile`:
```dockerfile
FROM python:3.11-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir sentence-transformers flask
COPY . .
EXPOSE 5001
CMD ["python", "app.py"]
```

---

## 5️⃣ VSCode Tasks (Még gyorsabb)

`.vscode/tasks.json`:
```json
{
  "version": "2.0.0",
  "tasks": [
    {
      "label": "Start All Services",
      "type": "shell",
      "command": "docker-compose up -d",
      "problemMatcher": []
    },
    {
      "label": "Stop All Services",
      "type": "shell",
      "command": "docker-compose down",
      "problemMatcher": []
    },
    {
      "label": "Backend Only",
      "type": "shell",
      "command": "cd Backend/BidVerse_backend && .\\gradlew.bat bootRun",
      "problemMatcher": []
    },
    {
      "label": "Frontend Only",
      "type": "shell",
      "command": "cd Frontend/my-vite-app && npm run dev",
      "problemMatcher": []
    }
  ]
}
```

**Használat:**
- `Ctrl+Shift+P` → `Tasks: Run Task` → `Start All Services`

---

## 🎯 Ajánlott Workflow

### Első telepítés:
```powershell
# 1. Adatbázis
sqlcmd -S localhost -U SA -P n5m_35z3m_A_73117 -i structure.sql
sqlcmd -S localhost -U SA -P n5m_35z3m_A_73117 -i mintaadat.sql

# 2. Docker Compose
docker-compose up -d
```

### Napi fejlesztés:
```powershell
# Indítás reggel
docker-compose up -d

# Munka...

# Leállítás este
docker-compose down
```

### Csak frontend/backend módosítás:
```powershell
# AI szolgáltatások futnak Docker-ben
docker-compose up -d qdrant ai-embedding ai-search ai-flask ai-agent

# Backend manuálisan (hot reload)
cd Backend\BidVerse_backend
.\gradlew.bat bootRun

# Frontend manuálisan (hot reload)
cd Frontend\my-vite-app
npm run dev
```

---

## ⚡ Leggyorsabb módszer (0 parancs)

### VS Code Extension: Docker + Tasks

1. Telepítsd: `Docker` extension (ms-azuretools.vscode-docker)
2. Jobb klikk `docker-compose.yml` → `Compose Up`
3. Kész!

---

## 📊 Teljesítmény összehasonlítás

| Módszer | Indítási idő | Terminálok száma | Bonyolultság |
|---------|--------------|-------------------|--------------|
| **Manuális (eredeti)** | ~5 perc | 7 db | ⭐⭐⭐⭐⭐ |
| **PowerShell Script** | ~2 perc | 1 db | ⭐⭐⭐ |
| **Docker Compose** | ~1 perc | 1 parancs | ⭐ |
| **VS Code Task** | ~1 perc | 1 klikk | ⭐ |

---

## 🐛 Hibaelhárítás

### Docker Compose fail:
```powershell
docker-compose logs [service-name]
```

### Port foglalt:
```powershell
# Ellenőrzés
netstat -ano | findstr :8081

# Összes konténer leállítása
docker-compose down
```

### Rebuild szükséges:
```powershell
docker-compose up -d --build
```

---

**Ajánlás:** Használd a **Docker Compose** megoldást! Egy parancs, minden fut, könnyű debug.

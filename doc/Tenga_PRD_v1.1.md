
# 🧠 Product Requirements Document (PRD v1.1)
**Projektname:** Tenga – Personal Knowledge Base Agent  
**Version:** 0.1.1 (Anforderungsspezifikation)  
**Status:** Draft  
**Autor:** Christoph Dick  
**Datum:** 14. Oktober 2025  

---

## 1. Ziel des Projekts
Ziel ist die Entwicklung einer **persönlichen KI-basierten Knowledge Base**, die über einen **Chat-Agenten (z. B. ChatGPT)** und einen **MCP-Backend-Service** Gedanken, Ideen oder Informationen als strukturierte Markdown-Dokumente erfasst, verwaltet und wieder abrufbar macht.

Die Anwendung soll es ermöglichen, Wissen einfach durch natürliche Sprache zu speichern, automatisch mit Tags zu versehen, bei Bedarf zu aktualisieren und über ein Web-Interface zu durchsuchen.

---

## 2. Produktidee & Grundprinzip
> „Ich kann einfach mit meinem Agenten reden – und der merkt sich meine Gedanken als strukturierte, tagbare Wissensdokumente.“

Kernkonzept:
- **Gesprächsbasiertes Erfassen von Wissen**
- **Automatisches Tagging mit Hierarchien**
- **Versionierung pro Dokument**
- **Abruf und semantische Suche**
- **Web-Interface**
- **Schnittstellen (REST + MCP)**
- **Einfacher Zugriff per API-Key**

---

## 3. Hauptziele & Nutzen
| Ziel | Beschreibung | Nutzen |
|------|---------------|--------|
| 🧩 Wissensspeicherung | Gedanken, Ideen oder Links werden als Markdown-Dokumente erfasst | Minimiert Reibung, kein manuelles Notieren nötig |
| 🏷️ Intelligentes Tagging | Hierarchische Tags (Parent-Child-Beziehungen) ermöglichen Gruppierung | Schnelle, semantische Filterung |
| 🧾 Versionierung | Historie der letzten 10 Änderungen pro Dokument | Sicherheit & Transparenz bei Änderungen |
| 🔍 Kontextsuche | KI-gestützte Suche und Zusammenfassung | Erkennt ähnliche Ideen oder Themen |
| 🌐 Webinterface | Übersicht, Filterung, Detailansicht | Komfortable Verwaltung über den Browser |
| 🔐 API-Key Zugriff | Schlankes Auth-Modell für Agenten- und User-Integration | Einfach, sicher, ohne komplexe Auth-Flows |

---

## 4. Epics & User Stories
### **Epic 1: Wissen erfassen**
| ID | User Story | Akzeptanzkriterien |
|----|-------------|-------------------|
| 1.1 | Als Nutzer möchte ich Gedanken oder Ideen über den Agenten eingeben können, die automatisch als Markdown-Dokument gespeichert werden. | Eingaben werden im Backend persistiert; jedes Dokument enthält Titel, Inhalt, Tags und Metadaten. |
| 1.2 | Als Nutzer möchte ich, dass Tags automatisch erkannt und gespeichert werden. | Tags werden aus dem Textinhalt generiert; Hierarchien werden berücksichtigt. |
| 1.3 | Als Nutzer möchte ich bestehende Dokumente erweitern oder aktualisieren können. | System erkennt ähnliche bzw. bereits existierende Dokumente und legt eine neue Version an. |
| 1.4 | Als Nutzer möchte ich meine letzten Änderungen nachvollziehen können. | Zu jedem Dokument sind bis zu 10 vorherige Versionen abrufbar. |

---

### **Epic 2: Wissen durchsuchen & abrufen**
| ID | User Story | Akzeptanzkriterien |
|----|-------------|-------------------|
| 2.1 | Als Nutzer möchte ich Anfragen wie „Zeig mir meine Ideen zu Projekt X“ stellen können. | System identifiziert relevante Dokumente per Tag- oder Ähnlichkeitssuche. |
| 2.2 | Als Nutzer möchte ich, dass der Agent die gefundenen Dokumente zusammenfasst. | Agent fasst mehrere relevante Snippets inhaltlich zusammen. |
| 2.3 | Als Nutzer möchte ich über das Web-Interface Dokumente nach Tags filtern. | Auswahl eines Parent-Tags zeigt automatisch auch alle Dokumente mit Child-Tags. |

---

### **Epic 3: Verwaltung & Webinterface**
| ID | User Story | Akzeptanzkriterien |
|----|-------------|-------------------|
| 3.1 | Als Nutzer möchte ich eine Übersicht aller gespeicherten Dokumente sehen. | Listen- oder Kartenansicht mit Titel, Tags, Datum, Version. |
| 3.2 | Als Nutzer möchte ich nach bestimmten Tags oder Text filtern können. | Suchfeld + Tag-Filterung (inkl. Parent-Child-Relation). |
| 3.3 | Als Nutzer möchte ich Dokumente im Detail betrachten und ältere Versionen einsehen können. | Detailansicht zeigt Markdown-Inhalt + Version-History (max. 10). |
| 3.4 | Als Nutzer möchte ich eine frühere Version wiederherstellen können. | Eine alte Version kann als neue aktuelle Version gespeichert werden. |

---

### **Epic 4: Schnittstellen & Integration**
| ID | User Story | Akzeptanzkriterien |
|----|-------------|-------------------|
| 4.1 | Als Entwickler möchte ich über eine REST-API auf Dokumente, Tags und Versionen zugreifen können. | CRUD-Endpoints für Dokumente, Tags und Versions-History vorhanden. |
| 4.2 | Als Entwickler möchte ich über eine MCP-Schnittstelle interagieren können. | MCP-Manifest verfügbar; Methoden: `createNote`, `updateNote`, `getNotes`, `searchNotes`. |
| 4.3 | Als Nutzer möchte ich mich über einen API-Key authentifizieren können. | API-Key identifiziert Nutzer eindeutig; Zugriff nur auf eigene Daten. |
| 4.4 | Als Nutzer möchte ich meinen API-Key einmal im Web-Interface eingeben und dauerhaft speichern. | Session persistiert lokal (z. B. LocalStorage). |

---

## 5. Funktionale Anforderungen
- Speicherung von Textinhalten als Markdown-Strukturen mit Metadaten.
- Hierarchische Tags (Parent ↔ Child).
- Maximal 10 Versionen pro Dokument; ältere Versionen nicht durchsuchbar.
- Volltext- und semantische Suche.
- REST + MCP-API mit API-Key-Authentifizierung.
- Web-UI: Anzeige, Filterung, Tag-Navigation, Version-History.
- Keine Datei-Uploads.

---

## 6. Nicht-funktionale Anforderungen
- Antwortzeit unter 500 ms bei < 10.000 Dokumenten.
- Zugriff per API-Key; keine Benutzerkonten.
- Lauffähig lokal (Docker) und Cloud.
- UTF-8 Markdown mit YAML Front-Matter.
- Erweiterbar für Embeddings und Multi-User.
- Fehler dürfen keine Versionen überschreiben.

---

## 7. Offene Punkte
- Datenbanktechnologie: später definieren.
- Technische Umsetzung von Tagging & Versionierung.
- UI/Design-System: noch offen.
- Key-Verwaltung & -Erstellung: folgt später.

---

## 8. Nächste Schritte
1. Architekturentwurf (High-Level)
2. Datenmodell (konzeptionell)
3. API-Definition (REST + MCP)
4. UI-Wireframes
5. Proof of Concept (Speichern, Abruf, Filterung)

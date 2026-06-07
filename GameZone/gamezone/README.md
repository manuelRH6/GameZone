# 🎮 GameZone - Sistema de Gestión de Videojuegos

**Examen Final — Programación de Computadores II**
Universidad Popular del César

## Integrantes

- Julián [Apellido]

## Descripción

Sistema de gestión de catálogo de videojuegos digitales y físicos con interfaz gráfica JavaFX, persistencia JSON y arquitectura en capas.

## Arquitectura

```
src/main/java/gamezone/
├── interfaces/        → Sellable, Displayable
├── model/             → VideoGame (abstract), DigitalVideoGame, PhysicalVideoGame, Sale
├── repository/        → VideoGameRepository, SaleRepository (CRUD + JSON)
├── service/           → VideoGameService (lógica de negocio)
├── ui/                → MainMenuController (JavaFX UI)
└── Main.java          → Punto de entrada JavaFX
```

## Funcionalidades

- ✅ Agregar videojuegos (digital o físico) con validaciones
- ✅ Listar todos los videojuegos con tabla interactiva
- ✅ Buscar por título (case-insensitive)
- ✅ Buscar por plataforma
- ✅ Editar y eliminar videojuegos
- ✅ Realizar ventas con control de stock
- ✅ Historial de ventas con totales
- ✅ Persistencia en archivos JSON (`data/`)
- ✅ Alertas UI para errores y confirmaciones

## Reglas de negocio

- **Juego Digital:** Si `sizeGB > 50`, se agregan $5.000 al precio base
- **Juego Físico:** Si el estado es `"usado"`, se aplica 25% de descuento

## Requisitos

- Java 17+
- Maven 3.8+
- JavaFX 21 (incluido vía Maven)

## Cómo ejecutar

```bash
mvn clean javafx:run
```

O desde IntelliJ IDEA: ejecutar `Main.java` con la configuración de JavaFX.

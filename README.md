# 🛒 TPI - Sistema de Gestión de Stock (Mayorista de Computación)

**Universidad Nacional de San Antonio de Areco (UNSAdA)**  
**Asignatura:** Programación Orientada a Objetos (2026)  
**Proyecto:** Trabajo Práctico Integrador (Actividad N°9)  

---

## 👥 Integrantes del Equipo
* **Sol Dias**
* **Lázaro**
* **Bautista**

---

## 📌 Descripción del Proyecto
Este sistema de software está desarrollado bajo el paradigma de **Programación Orientada a Objetos (POO)** en **Java** para gestionar el inventario, ventas y proveedores de un distribuidor mayorista de artículos de computación. 

Cuenta con una **Interfaz Gráfica de Usuario (GUI) interactiva** construida en **Java Swing**, con visualización de métricas KPI en pesos argentinos (`$ ARS`), etiquetas dinámicas de estado (`NORMAL` vs `CRÍTICO`), buscador en tiempo real, registro de ventas con facturación y módulo de alta de nuevos productos.

---

## 🚀 Tecnologías y Frameworks Utilizados
* **Lenguaje:** Java (compatible con Java 17 y Java 23).
* **Persistencia de Datos (ORM):** JPA / Hibernate (persistencia relacional automática).
* **Base de Datos:** H2 Database Engine (modo de almacenamiento en archivo local `stockdb.mv.db`).
* **Gestor de Proyecto y Dependencias:** Apache Maven (`pom.xml`).
* **Interfaz Gráfica:** Java Swing / AWT (Layouts `GridBagLayout`, `BorderLayout`, `TableRowSorter`, `DefaultTableCellRenderer`).
* **Control de Versiones:** Git & GitHub.

---

## 🏗️ Patrones de Diseño Implementados
1. **Patrón Singleton:**
   * **Implementación:** Clase `JPAUtil`.
   * **Propósito:** Garantiza la existencia de una única instancia global de `EntityManagerFactory` para optimizar los recursos de conexión a la base de datos H2.
2. **Patrón Observer (Observador):**
   * **Implementación:** Interfaz `StockObserver` y sus implementaciones concretas `AlertaStockListener` y `MainFrame`.
   * **Propósito:** Notifica en tiempo real a la interfaz gráfica y al sistema cuando el stock actual de un artículo alcanza o cae por debajo de su stock mínimo configurado.
3. **Patrón DAO (Data Access Object):**
   * **Implementación:** Clase `ArticuloDAO`.
   * **Propósito:** Encapsula y aísla toda la lógica de persistencia SQL/JPA respecto de la capa de presentación e interfaz gráfica.

---

## 📋 Entidades Principales del Dominio
* **Artículos:** Código único, descripción, rubro, proveedor, stock actual, stock mínimo y precio unitario (\$ ARS).
* **Rubros:** Categorización de artículos de computación (Procesadores, Placas de Video, Almacenamiento, Memorias RAM, Monitores, etc.).
* **Proveedores:** CUIT/CUIL, razón social, dirección, teléfono y correo electrónico.
* **Clientes:** Código, apellido, nombre, DNI, dirección, teléfono y email.
* **Facturas / DetalleFactura:** Registro de ventas con asociación de artículos, cantidad, cálculo automático de subtotal y total facturado.

---

## 🗂️ Estructura del Proyecto

```text
TPI_GestionStock/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── mayorista/
│       │           ├── config/      # Configuración de JPA/Hibernate (JPAUtil)
│       │           ├── dao/         # Objeto de acceso a datos (ArticuloDAO)
│       │           ├── gui/         # Interfaz Gráfica Swing (MainFrame)
│       │           ├── model/       # Entidades JPA (Articulo, Rubro, Proveedor, Cliente, Factura)
│       │           ├── observer/    # Patrón Observer (StockObserver, AlertaStockListener)
│       │           └── Main.java    # Punto de entrada de la aplicación
│       └── resources/
│           └── META-INF/
│               └── persistence.xml # Configuración de conexión H2 / Hibernate
├── data/                            # Archivo de base de datos H2 (stockdb.mv.db)
├── pom.xml                          # Dependencias de Maven
└── README.md                        # Documentación del proyecto
```

---

## ⚙️ Instrucciones de Instalación y Ejecución

### Requisitos Previos:
* JDK 17 o superior instalado.
* VS Code, IntelliJ IDEA o Eclipse con soporte para Java y Maven.

### Pasos para Ejecutar:
1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/ddias-sol/TPI_GestionStock.git
   cd TPI_GestionStock
   ```
2. **Abrir el proyecto** en tu IDE preferido.
3. **Ejecutar la aplicación:**
   * Ejecutar la clase principal `com.mayorista.Main` o la interfaz `com.mayorista.gui.MainFrame`.
4. La base de datos H2 se creará e inicializará automáticamente con el catálogo inicial de prueba en el primer arranque.

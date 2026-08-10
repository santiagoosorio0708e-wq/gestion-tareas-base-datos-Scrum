# 📋 TaskFlow - Sistema Profesional de Gestión de Tareas

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/Java_Swing-GUI-blue?style=for-the-badge)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)

## 📖 Resumen del Proyecto
**TaskFlow** es una solución de software orientada a la gestión eficiente de proyectos y tareas. Diseñado para equipos que buscan un control de flujo de trabajo ágil, el sistema permite la creación, asignación y seguimiento de actividades mediante un modelo de datos robusto y una moderna interfaz gráfica.

## 💡 Motivación
La gestión de tareas en entornos educativos y de desarrollo inicial suele carecer de trazabilidad y de una interfaz amigable. Este proyecto nace con el objetivo de centralizar la información, eliminar la ambigüedad en las responsabilidades del equipo y proporcionar una herramienta visual que permite seguir el progreso mediante un **Tablero Kanban interactivo** (Por Realizar, En Proceso, Finalizado).

## 🚀 Características Principales
- **Interfaz Gráfica (GUI) Moderna**: Migración completa a Java Swing (`JFrame`, `JTable`, `JTabbedPane`), abandonando los antiguos diálogos emergentes para ofrecer una experiencia fluida de ventana única.
- **Tablero Kanban Visual**: Vista dinámica de 3 columnas para visualizar y clasificar el avance de las tareas según su estado.
- **Validación de Datos en Tiempo Real**: Bloqueo inteligente de caracteres numéricos en campos de texto (como nombres de usuarios) usando eventos de teclado (`KeyAdapter`).
- **Gestión Avanzada**: Asignación directa de tareas a usuarios, filtrado por niveles de prioridad (Alta, Media, Baja) y listado personalizado.
- **Diseño Responsivo y Nativo**: Integración del *Look & Feel* nativo del sistema operativo (Windows/Mac/Linux) para una apariencia profesional y organizada.

## 🏗️ Arquitectura del Sistema
El sistema ha sido estructurado bajo el principio de **Separación de Responsabilidades (SoC)**, dividiendo la lógica de la interfaz y la persistencia de datos temporal:

- `model/`: Contiene las entidades principales (`Task`, `User`) y los enumerados (`Priority`, `Status`) que actúan como "fuente de verdad" para clasificaciones y estados.
- `service/`: Implementa la lógica de negocio en `TaskManager`, garantizando que la manipulación, guardado y filtrado de datos sea centralizada.
- `App.java`: Actúa como el controlador principal de la Interfaz Gráfica (Vista), agrupando la lógica de componentes (tablas, botones) y los eventos de los usuarios.

## 🛠️ Tecnologías y Herramientas
- **Lenguaje**: Java SE
- **Librerías Visuales**: Java Swing & AWT
- **Entorno (IDE)**: Visual Studio Code / IntelliJ IDEA
- **Control de Versiones**: Git & GitHub
- **Estándares de Código**: Conventional Commits (`feat`, `fix`, `docs`, `refactor`) y JavaDoc.

## 💻 Instrucciones de Instalación y Uso

### Prerrequisitos
- Tener instalado el [JDK de Java](https://www.oracle.com/java/technologies/downloads/) (Versión 8 o superior).
- Contar con Git instalado en tu equipo.

### Pasos para ejecutar de forma local

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/santiagoosorio0708e-wq/proyecto_java.git
   cd "proyecto java"
   ```

2. **Compilar el código fuente**
   Asegúrate de estar en la raíz del proyecto. Ejecuta el siguiente comando para compilar las clases en una carpeta `bin`:
   ```bash
   javac -d bin -sourcepath src src/App.java
   ```

3. **Ejecutar la aplicación**
   ```bash
   java -cp bin App
   ```
   *(Alternativamente, puedes abrir el proyecto en VS Code y usar la extensión "Extension Pack for Java" para ejecutar el archivo `App.java` con el botón "Run").*

## 👥 Contribuciones
Este proyecto es el resultado de un esfuerzo colaborativo, desarrollado bajo estrictas normas de versionado para asegurar la calidad del código entregado.

- **Santiago Osorio**
- **Daniel Jaimes Gamboa**

## 📜 Licencia
Este proyecto es de carácter académico.
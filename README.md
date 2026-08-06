# Gestor de Tareas (Jira/Trello Style)

Este es un proyecto de consola interactiva desarrollado en **Java**, utilizando Programación Orientada a Objetos (POO) y la biblioteca estándar `JOptionPane` para la interfaz de usuario. Simula el funcionamiento básico de sistemas de gestión de tareas como Jira o Trello.

## Características Principales

- **Gestión de Usuarios**: Permite registrar y listar los miembros del equipo que participarán en los proyectos.
- **Gestión de Tareas**: Crea tareas con título, descripción y prioridad asignada, y asígnalas a los usuarios registrados.
- **Prioridad de Tareas**: Las tareas se clasifican en tres niveles de prioridad: `ALTA`, `MEDIA`, y `BAJA`.
- **Estados de Tareas**: Las tareas pueden transitar por los estados: `POR_REALIZAR` (To Do), `EN_PROCESO` (In Progress) y `FINALIZADO` (Done).
- **Tablero Kanban**: Visualización de las tareas agrupadas por su estado actual.
- **Vista de Usuario**: Visualiza las tareas asignadas a un usuario específico, ordenadas automáticamente por prioridad (de mayor a menor).
- **Datos de Prueba**: Opción integrada para cargar datos de prueba instantáneamente y probar todas las funcionalidades.

## Estructura del Proyecto

El proyecto está estructurado utilizando los principios de la Programación Orientada a Objetos:

- `src/model/`: Contiene las clases principales y modelos de datos.
  - `User.java`: Representa a los usuarios del sistema.
  - `Task.java`: Representa las tareas con sus propiedades y estado.
  - `Status.java`: Enumerador para los estados de una tarea.
  - `Priority.java`: Enumerador para los niveles de prioridad.
- `src/service/`: Contiene la lógica de negocio.
  - `TaskManager.java`: Clase encargada de administrar las colecciones de usuarios y tareas (usando `ArrayList`), así como filtrar y ordenar los datos.
- `src/App.java`: Punto de entrada de la aplicación que contiene el menú interactivo con `JOptionPane`.

## Requisitos

- Java Development Kit (JDK) 11 o superior. (Probado en JDK 21)

## Cómo Ejecutar el Proyecto

Puedes ejecutar el proyecto desde cualquier entorno de desarrollo integrado (IDE) compatible con Java como IntelliJ IDEA, Eclipse, o VS Code.

Si prefieres ejecutarlo desde la **línea de comandos / terminal**, sigue estos pasos:

1. Clona el repositorio:
   ```bash
   git clone https://github.com/santiagoosorio0708e-wq/proyecto_java.git
   ```

2. Navega a la carpeta del proyecto:
   ```bash
   cd proyecto_java
   ```

3. Crea una carpeta `bin` para los archivos compilados:
   ```bash
   mkdir bin
   ```

4. Compila el código fuente:
   ```bash
   javac -d bin src/App.java src/model/*.java src/service/*.java
   ```

5. Ejecuta la aplicación:
   ```bash
   java -cp bin App
   ```

## Control de Versiones

Este proyecto utiliza [Conventional Commits](https://www.conventionalcommits.org/) para los mensajes de commit.

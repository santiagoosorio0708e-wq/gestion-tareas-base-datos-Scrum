# TaskFlow - Sistema Profesional de Gestion de Tareas

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/Java_Swing-GUI-blue?style=for-the-badge)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)

## Descripcion
TaskFlow es una solucion de software orientada a la gestion eficiente de proyectos y tareas. Diseñado para equipos que buscan un control de flujo de trabajo agil, el sistema permite la creacion, asignacion y seguimiento de actividades mediante un modelo de datos robusto y una moderna interfaz grafica basada en Java Swing.

## Objetivos
- Centralizar la informacion de las tareas para eliminar la ambigüedad en las responsabilidades del equipo.
- Proporcionar una herramienta visual, interactiva y facil de usar mediante un Tablero Kanban (Por Realizar, En Proceso, Finalizado).
- Garantizar la integridad de los datos mediante validaciones en tiempo real y tipado estricto (uso de enumeraciones para prioridades y estados).
- Mantener una arquitectura escalable que permita futuras expansiones del proyecto.

## Caracteristicas Principales
- Interfaz Grafica (GUI) Moderna: Uso de Java Swing (JFrame, JTable, JTabbedPane) para ofrecer una experiencia fluida de ventana unica.
- Tablero Kanban Visual: Vista dinamica de 3 columnas para clasificar el avance de las tareas segun su estado.
- Validacion de Datos en Tiempo Real: Bloqueo inteligente de caracteres numericos en campos de texto de nombres usando eventos de teclado.
- Gestion Avanzada: Asignacion directa de tareas a usuarios y listados personalizados ordenados por prioridad (Alta, Media, Baja).
- Diseño Nativo: Integracion del estilo visual del sistema operativo host para una apariencia profesional.

## Arquitectura del Sistema
El sistema ha sido estructurado bajo el principio de Separacion de Responsabilidades (SoC), dividiendo la logica de negocio y la interfaz de usuario:

- model/: Contiene las entidades principales (Task, User) y los enumerados (Priority, Status) que actuan como fuente unica de verdad para clasificaciones.
- service/: Implementa la logica de negocio en la clase TaskManager, centralizando la manipulacion, guardado y filtrado de datos.
- App.java: Actua como el controlador principal de la Interfaz Grafica (Vista), agrupando la logica de componentes graficos y enlazando los eventos de los usuarios con el servicio.

## Tecnologias
- Lenguaje Base: Java SE
- Librerias UI: Java Swing & AWT
- Entorno de Desarrollo (IDE): Visual Studio Code / IntelliJ IDEA
- Control de Versiones: Git & GitHub
- Estandares de Codigo: JavaDoc y Conventional Commits.

## Instrucciones de Instalacion y Uso

### Prerrequisitos
- Tener instalado el JDK de Java (Version 8 o superior).
- Contar con Git configurado en tu sistema.

### Pasos para ejecucion local

1. Clonar el repositorio
   ```bash
   git clone https://github.com/santiagoosorio0708e-wq/proyecto_java.git
   cd "proyecto java"
   ```

2. Compilar el codigo fuente
   Posicionado en la raiz del proyecto, ejecuta el siguiente comando para compilar las clases (se guardaran en la carpeta bin):
   ```bash
   javac -d bin -sourcepath src src/App.java
   ```

3. Iniciar la aplicacion
   ```bash
   java -cp bin App
   ```

## Contribuciones
Proyecto colaborativo desarrollado con enfoque en buenas practicas de versionado y codigo limpio.

- Santiago Osorio
- Daniel Jaimes Gamboa

## Licencia
Proyecto de caracter academico.
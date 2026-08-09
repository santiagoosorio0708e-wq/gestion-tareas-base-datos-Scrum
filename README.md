📋 TaskFlow - Sistema Profesional de Gestión de Tareas
📖 Resumen del Proyecto
TaskFlow es una solución de software orientada a la gestión eficiente de proyectos y tareas. Diseñado para equipos que buscan un control de flujo de trabajo ágil, el sistema permite la creación, asignación y seguimiento de actividades mediante un modelo de datos robusto y escalable.

💡 Motivación
La gestión de tareas en entornos educativos y de desarrollo inicial suele carecer de trazabilidad. Este proyecto nace con el objetivo de centralizar la información, eliminar la ambigüedad en las responsabilidades del equipo y proporcionar una herramienta que permite visualizar el progreso mediante estados definidos (To-Do, In Progress, Done).

🏗️ Arquitectura del Sistema
El sistema ha sido estructurado bajo un enfoque de Separación de Responsabilidades (SoC), dividiendo la lógica de la interfaz y la persistencia temporal:

model/: Contiene las entidades principales (Task, User) y los enumerados (Priority, Status) que actúan como "fuente de verdad" para los estados y clasificaciones.

service/: Implementa la lógica de negocio en TaskManager, garantizando que la manipulación de datos sea centralizada y segura.

App.java: Actúa como el controlador de entrada, gestionando la interacción con el usuario mediante JOptionPane para una experiencia visual o consola para debugging.

🚀 Características Técnicas
Gestión de Prioridades: Clasificación inteligente de tareas (Alta, Media, Baja).

Asignación de Responsables: Vinculación directa entre entidades User y Task.

Control de Estados: Ciclo de vida completo para cada tarea.

Historial de Cambios: Implementación de Conventional Commits para una trazabilidad precisa de las contribuciones del equipo.

⚙️ Flujo de Operación
Entrada: El usuario interactúa a través de una interfaz polimórfica (consola/visual).

Procesamiento: TaskManager recibe las peticiones y actualiza el estado de las colecciones internas.

Persistencia: Visualización en tiempo real del estado de los proyectos.

🛠️ Tecnologías y Herramientas
Lenguaje: Java SE

IDE: Visual Studio Code

Control de Versiones: Git (GitHub)

Estándares: Conventional Commits (feat, fix, docs, refactor)

💻 Instrucciones de Instalación
Bash
# 1. Clonar el repositorio
git clone https://github.com/santiagoosorio0708e-wq/proyecto_java.git

# 2. Abrir en VS Code
code proyecto_java

# 3. Compilar y ejecutar
# Localizar App.java en la carpeta src y ejecutar con el botón "Run"
👥 Contribuciones
Este proyecto es el resultado de un esfuerzo colaborativo.

Santiago Osorio

Daniel Jaimes Gamboa

El desarrollo se ha regido bajo estrictas normas de versionado para asegurar la calidad del código entregado.

📜 Licencia
Este proyecto es de carácter académico.
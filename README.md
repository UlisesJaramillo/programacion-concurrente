# Simulación del Parque Ecológico “ECO-PCS”

Este proyecto simula el funcionamiento del parque ecológico “ECO-PCS”, un acuario natural, desde que los visitantes llegan al parque hasta que se van. La simulación incluye la gestión de diversas actividades del parque, tales como la entrada al parque, el uso de molinetes, el alquiler de bicicletas, la participación en actividades, y la gestión de recursos como gomones y equipos de snorkel.

## Descripción del Proyecto

El sistema utiliza mecanismos de sincronización en Java para coordinar el acceso y la interacción entre los visitantes y las actividades del parque. Se emplean semáforos, monitores, locks y barreras cíclicas para gestionar recursos limitados y sincronizar la ejecución de tareas concurrentes.

### Componentes Clave

- **`EcoPcs`**: Clase principal que gestiona la simulación del parque, incluyendo los recursos y actividades.
- **`Personas`**: Clase que representa a los visitantes del parque.
- **`ControlTiempo`**: Clase que gestiona el tiempo y el horario del parque.
- **`Repositor`**: Clase que maneja la disponibilidad de equipos y recursos.
- **`Agente`**: Clase que gestiona la asignación de bolsos y recursos a los visitantes.
- **`TestParque`**: Clase principal que inicia la simulación.

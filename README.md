# Autores y Link-Repositorios

Jaime López Díaz

Nicolás Jiménez

Backend Repo-Link: https://github.com/Parcial-1-Programacion-Concurrente/Backend-Springboot-OperacionDeGalton.git

Frontend Repo-Link: https://github.com/Parcial-1-Programacion-Concurrente/Frontend-React-Node.git

# Galton Board Simulation System

## Descripción General

El **Sistema de Simulación Galton** permite la creación, simulación y gestión de Galton Boards y máquinas de distribución. Estas máquinas generan distribuciones basadas en distintos modelos estadísticos (binomial, normal, exponencial, etc.), simulando la caída de bolas a través de los Galton Boards y generando datos de distribución en tiempo real.

El sistema está compuesto por una **interfaz de usuario** para la gestión y monitorización de simulaciones, y un conjunto de **operaciones del sistema** que realizan el procesamiento de los datos, creación de entidades, y persistencia de los estados.

## Funcionalidades Principales

1. **Creación de Galton Boards**: El sistema permite la creación de tableros de Galton (Galton Boards), los cuales son usados por las máquinas de distribución para generar simulaciones de caída de bolas.
2. **Simulación de Caída de Bolas**: Las máquinas pueden simular la caída de bolas a través de los tableros de Galton. La distribución de las bolas en los contenedores es calculada de acuerdo con el tipo de distribución configurada en cada máquina.
3. **Monitoreo en Tiempo Real**: Los resultados de la simulación son actualizados en tiempo real, mostrando el estado actual de la distribución de bolas.
4. **Detención de Producción**: Permite al usuario detener la simulación y la producción de las máquinas.
5. **Persistencia del Estado de Simulación**: El sistema guarda el estado actual de la simulación, asegurando que los datos se puedan consultar posteriormente.

## Estructura de Clases y Servicios

### Clases Principales

1. **GaltonBoard**
   Representa un tablero de Galton. Contiene propiedades como el número de bolas y el número de contenedores en el tablero, así como la distribución actual de las bolas.
2. **Distribucion**
   Representa la distribución de las bolas en los contenedores del Galton Board. Esta clase almacena los datos de la simulación de distribución.
3. **Maquina**
   Clase abstracta que representa una máquina de distribución. Existen varias subclases específicas para cada tipo de distribución (Binomial, Geométrica, Exponencial, Normal, Uniforme, Poisson, etc.).
4. **MaquinaWorker**
   Maneja el procesamiento concurrente de las máquinas. Se encarga de iniciar el trabajo de las máquinas, monitorear el estado de los componentes y ensamblar las máquinas.
5. **FabricaGauss**
   Representa una fábrica que gestiona la creación de múltiples máquinas y Galton Boards. Permite iniciar la simulación de varias máquinas a la vez.

### Servicios

Los servicios proporcionan la lógica de negocio del sistema y permiten interactuar con los datos de las máquinas, distribuciones y Galton Boards.

1. **MaquinaService**

   - Gestiona la creación y actualización de máquinas.
   - Calcula la distribución de bolas para cada tipo de máquina.
   - Asigna Galton Boards a las máquinas y gestiona los componentes requeridos por estas.
2. **GaltonBoardService**

   - Maneja la creación y simulación de los Galton Boards.
   - Simula la caída de bolas y actualiza la distribución en tiempo real.
   - Persistencia del estado de la simulación.
3. **DistribucionService**

   - Gestiona las distribuciones asociadas a cada Galton Board.
   - Actualiza la distribución en función de las bolas procesadas durante la simulación.
4. **MaquinaWorkerService**

   - Inicia el trabajo de las máquinas en hilos concurrentes.
   - Verifica que todas las máquinas hayan terminado su trabajo antes de ensamblarlas.
   - Calcula la distribución final una vez que todos los componentes han sido procesados.
5. **FabricaGaussService**

   - Crea y gestiona una fábrica de máquinas.
   - Inicia la producción de máquinas de distribución y Galton Boards en paralelo.
   - Controla la detención de simulaciones usando semáforos para la sincronización.
6. **ComponenteWorkerService**

   - Gestiona los workers que procesan los componentes de las máquinas.
   - Valida los componentes antes de ensamblar las máquinas.

### Operaciones del Sistema

1. **Crear Galton Boards**
   El sistema permite crear nuevos Galton Boards que serán utilizados para simular la caída de bolas.
2. **Crear Máquinas de Distribución**
   Se pueden crear máquinas que simulen diferentes tipos de distribuciones (binomial, normal, exponencial, etc.).
3. **Simular Caída de Bolas**
   Inicia el proceso de simulación de caída de bolas en los Galton Boards y distribuye las bolas en los contenedores.
4. **Actualizar Distribución en Tiempo Real**
   Durante la simulación, los resultados se actualizan en tiempo real y se pueden visualizar a través de la interfaz.
5. **Persistir Estado de Simulación**
   El estado de la simulación se guarda en la base de datos, permitiendo que se consulte en el futuro.
6. **Detener Producción**
   El sistema permite detener la simulación en curso, deteniendo el procesamiento de las máquinas.


## Concurrencia


El sistema de simulación utiliza **concurrencia** para manejar múltiples tareas en paralelo. A continuación se describen los componentes clave de concurrencia utilizados:

1. **MaquinaWorker y ComponenteWorker**:

   - Cada máquina tiene un `MaquinaWorker` que gestiona varios `ComponenteWorkers`. Estos workers se ejecutan en hilos separados utilizando un **ExecutorService**, lo que permite procesar varias máquinas y componentes en paralelo.
2. **Semáforos**:

* Se utiliza un **Semaphore** en `FabricaGaussService` para controlar la producción de máquinas y asegurar que no se puedan realizar múltiples operaciones de simulación simultáneamente.

3. **Futures y Callbacks**:

* Las tareas relacionadas con la simulación de la producción de máquinas se manejan de forma asíncrona mediante **Future** y **Callable**, permitiendo el procesamiento paralelo de múltiples simulaciones.

4. **Bloqueos y Monitores**:

* Se utilizan secciones sincronizadas en el código para garantizar que no haya condiciones de carrera en el acceso a los datos críticos, como las actualizaciones de la distribución y el estado de la simulación.

## Patrones de Diseño


1. **Patrón Singleton**:
   * El patrón Singleton es utilizado por los servicios del sistema para garantizar una única instancia. Spring Boot gestiona esto automáticamente mediante anotaciones `@Service`, asegurando que las instancias de los servicios sean únicas y compartidas.
2. **Patrón Factory**:
   * El sistema utiliza un **Factory** para la creación de diferentes tipos de máquinas de distribución. Dependiendo del tipo de distribución seleccionado (binomial, normal, exponencial, etc.), se crea la instancia específica de la máquina.
3. **Patrón Strategy**:

* Se utiliza el patrón Strategy para manejar diferentes algoritmos de cálculo de distribución. Cada tipo de máquina (binomial, normal, etc.) tiene su propio servicio que implementa una estrategia de cálculo de distribución específica.

4. **Patrón Observer**:

* El sistema sigue el patrón **Observer** para la actualización en tiempo real de las distribuciones. Las clases de simulación notifican a los observadores interesados cuando cambia la distribución, permitiendo a la interfaz de usuario mostrar actualizaciones en vivo.

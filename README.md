# UnionShop Market Agency · Proyecto Final GP2 2025-I

Aplicación de consola para una agencia de vuelos. Permite registrar clientes y vuelos, vender varios boletos en una operación, consultar disponibilidad, anular ventas y exportar boletas. Está pensada para una persona operando una carpeta de datos local.

## Ejecutar

Requiere **JDK 21** y **Apache Ant 1.10.15** o una versión posterior compatible con Java 21. También puedes abrir el proyecto Ant existente en NetBeans y seleccionar el JDK 21.

En Bash, desde la raíz del repositorio:

```bash
ant clean verificar
export AGENCIA_USUARIO=operador
read -rsp 'Contraseña local: ' AGENCIA_CLAVE
printf '\n'
export AGENCIA_CLAVE
java -jar dist/ProyectoFinalGP2-2025I.jar
unset AGENCIA_CLAVE
```

En Windows, establece `AGENCIA_USUARIO` y `AGENCIA_CLAVE` como variables de entorno de la sesión que inicia la aplicación. Si ejecutas desde NetBeans, inicia el IDE desde una sesión con esas variables. La contraseña se oculta en una terminal interactiva. Las consolas sin soporte de entrada oculta, como algunas consolas de IDE, pueden mostrar los caracteres introducidos.

Puedes indicar una carpeta alternativa como argumento del JAR. El archivo histórico `venta.txt` se busca en el directorio desde el que se inicia el proceso.

## Flujo de operación

1. En **Vuelos**, registra un código, dos ciudades distintas, el precio unitario y la capacidad total.
2. En **Clientes**, registra un DNI de ocho dígitos, nombre, apellidos y dirección. Los ceros iniciales del DNI se conservan.
3. En **Ventas**, selecciona un cliente y un vuelo, indica la cantidad de boletos y asigna un número de venta único. La boleta conserva el nombre, el trayecto, el precio unitario y el importe de esa operación, incluso si después editas el catálogo.
4. En **Historial** consulta las ventas activas y anuladas. Usa **Anular** para devolver los asientos al inventario y **Resumen** para consultar boletos e importes activos acumulados. La anulación registra el cambio en la agencia y libera asientos. La aplicación no procesa cargos ni devoluciones de dinero.
5. Usa **Exportar boletas** para regenerar `datos/boletas.txt`. El historial original de `venta.txt` se incorpora como referencia de solo lectura. Sus ventas no alteran la disponibilidad ni los totales actuales, porque el proyecto original no conservaba el estado de vuelos y clientes.

Los cambios confirmados se guardan en `datos/agencia.tsv`. Para respaldar la operación, cierra la aplicación y copia ese archivo junto con el histórico si quieres conservarlo. Los archivos operativos contienen datos personales y se excluyen de Git. La contraseña que figuraba en el commit inicial permanece en el historial de Git. Si se usó fuera de este ejercicio, cámbiala en ese sistema.

## Análisis del código original

Revisión del commit `76eda56`, incluidos los siete archivos Java, la configuración de compilación, el manifiesto y el historial de boletas.

| Área | Inconsistencia encontrada | Corrección |
| --- | --- | --- |
| Venta | La búsqueda ignoraba el DNI introducido y comparaba con una variable inicializada en cero. | Búsqueda exacta por DNI de texto y verificación de que cliente y vuelo existan. |
| Entrada y navegación | Dos lectores compartían `System.in`. Las entradas inválidas podían lanzar excepciones y una opción incorrecta cerraba el lector principal. | Un lector para toda la sesión, validación de números y manejo del fin de entrada. |
| Catálogos | Los arreglos fijos admitían duplicados y podían desbordarse. El DNI numérico perdía ceros iniciales. | Colecciones por clave única, DNI de ocho dígitos, códigos normalizados y validación de texto, rutas, precios y capacidad. |
| Inventario | Actualizar un vuelo reemplazaba sus asientos libres. No había forma de liberar los de una venta. | Capacidad total y ocupación calculada sobre las ventas activas. Los registros vinculados a ellas no se pueden eliminar y las anulaciones liberan inventario. |
| Importes y boletas | El campo de cantidad no se usaba en el total y el precio era `double`. La boleta dependía de las entidades originales. | `BigDecimal` de dos decimales, ventas de varios boletos y copia de los datos de la operación en cada boleta. |
| Persistencia | Solo se escribía la boleta. Un fallo de archivo podía dejar una venta anunciada y asientos descontados. Reiniciar borraba el catálogo y el inventario. | Una versión de estado en UTF-8 se reemplaza de forma atómica antes de confirmar cambios. El bloqueo de archivo impide dos operadores simultáneos en la misma carpeta. Los datos con un formato inválido detienen la carga. |
| Historial | El texto de las boletas era el único registro duradero. No había estado de anulación ni un resumen confiable. | Historial estructurado para nuevas operaciones, consulta del archivo previo sin modificarlo y exportación regenerable. |
| Acceso | El usuario y la contraseña estaban escritos en Java. | Configuración mediante variables de entorno y conservación del límite de tres intentos. |
| Compilación | NetBeans y Ant generaban el JAR. Faltaban instrucciones y una verificación reproducible. | Se mantienen los descriptores `nbproject`, el manifiesto y el punto de entrada `VistaControl.INDEX`. El objetivo `ant verificar` compila el JAR y ejecuta la prueba de integración. |

## Arquitectura y verificación

- `MODEL` define clientes, vuelos y la información histórica de las ventas.
- `UTIL.Lectura` centraliza la entrada. `VistaControl.Menu` y `INDEX` conservan el flujo de consola.
- `Operaciones` aplica las reglas de negocio y `ArchivoAgencia` conserva el estado en el sistema de archivos. Se necesita un volumen local que soporte reemplazo atómico.
- `test/VistaControl/FlujoAgenciaTest.java` verifica DNI, duplicados, totales exactos, sobreventa, capacidad, restricciones de eliminación, reinicio, anulación, boletas y fallos de guardado y carga.

El alcance es el registro local de ventas. El proyecto conserva sus herramientas Java, NetBeans y Ant, sin dependencias externas de ejecución.

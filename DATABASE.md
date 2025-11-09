# Base de Datos - Red Sísmica

## Descripción

Este proyecto ahora incluye una base de datos H2 en memoria que se inicializa automáticamente al ejecutar la aplicación. La base de datos contiene datos precargados para facilitar las pruebas.

## Tecnología

- **Base de datos**: H2 Database Engine (en memoria)
- **Versión**: 2.2.220
- **Modo**: In-Memory (los datos se pierden al cerrar la aplicación)
- **URL de conexión**: `jdbc:h2:mem:redseismica`
- **Usuario**: `sa`
- **Contraseña**: (vacía)

## Estructura de la Base de Datos

### Tablas

1. **roles** - Roles de los empleados
   - id (PK)
   - nombre

2. **empleados** - Información de los empleados
   - id (PK)
   - nombre
   - apellido
   - mail
   - telefono
   - rol_id (FK)

3. **usuarios** - Credenciales de acceso
   - id (PK)
   - username
   - password
   - empleado_id (FK)

4. **sismografos** - Dispositivos de medición sísmica
   - id (PK)
   - numero_serie
   - fecha_instalacion
   - modelo
   - estado_actual
   - fecha_hora_estado

5. **estaciones** - Estaciones sismológicas
   - id (PK)
   - codigo
   - nombre
   - latitud
   - longitud
   - sismografo_id (FK)

6. **ordenes_inspeccion** - Órdenes de inspección
   - id (PK)
   - numero_orden
   - fecha_hora_emision
   - fecha_hora_finalizacion
   - fecha_hora_cierre
   - estado
   - observacion_cierre
   - estacion_id (FK)
   - responsable_id (FK)

7. **motivos_tipo** - Tipos de motivos para poner fuera de servicio
   - id (PK)
   - descripcion

8. **motivos_fuera_servicio** - Motivos asociados a órdenes cerradas
   - id (PK)
   - motivo_tipo_id (FK)
   - comentario
   - orden_id (FK)

9. **cambios_estado_sismografo** - Historial de cambios de estado
   - id (PK)
   - sismografo_id (FK)
   - fecha_hora
   - estado
   - observacion
   - empleado_id (FK)

## Datos Precargados

### Roles
- ResponsableInspeccion
- ResponsableReparacion

### Empleados
1. **Juan Pérez** (Responsable de Inspección)
   - Email: juan.perez@empresa.com
   - Usuario: jperez / password123

2. **Ana García** (Responsable de Reparación)
   - Email: ana.garcia@empresa.com
   - Usuario: agarcia / password123

3. **Luis Rodríguez** (Responsable de Reparación)
   - Email: luis.rodriguez@empresa.com
   - Usuario: lrodriguez / password123

4. **María López** (Responsable de Inspección)
   - Email: maria.lopez@empresa.com
   - Usuario: mlopez / password123

### Sismógrafos
- **1001** - Inhabilitado por inspección (Estación Central)
- **1002** - Online (Estación Norte)
- **1003** - Fuera de servicio (Estación Sur)

### Estaciones
- **101** - Estación Central (-31.4167, -64.1833)
- **102** - Estación Norte (-31.3500, -64.2000)
- **103** - Estación Sur (-31.5000, -64.1500)

### Órdenes de Inspección
- **Orden 1** - Completamente realizada (puede cerrarse)
- **Orden 2** - En curso (no puede cerrarse)
- **Orden 3** - Completamente realizada (puede cerrarse)
- **Orden 4** - Cerrada (ya procesada)

### Motivos Tipo
- Sensor dañado
- Cable cortado
- Pérdida de calibración
- Batería agotada
- Interferencia electromagnética
- Problema de conectividad

## Clases DAO (Data Access Object)

El proyecto incluye las siguientes clases DAO para acceder a los datos:

- `DatabaseConfig.java` - Configuración y conexión a la base de datos
- `DataLoader.java` - Carga de datos iniciales
- `OrdenInspeccionDAO.java` - Acceso a órdenes de inspección
- `EmpleadoDAO.java` - Acceso a empleados
- `MotivoTipoDAO.java` - Acceso a motivos tipo

## Inicialización

La base de datos se inicializa automáticamente al ejecutar la aplicación:

```bash
java -jar target/red-seismica-1.0-SNAPSHOT.jar
```

Durante la inicialización verás:
```
=== Inicializando Base de Datos ===
✓ Tablas creadas exitosamente
  - Roles insertados
  - Empleados insertados
  - Usuarios insertados
  - Sismógrafos insertados
  - Estaciones insertadas
  - Órdenes de inspección insertadas
  - Motivos tipo insertados
✓ Datos iniciales cargados exitosamente
=== Base de Datos Lista ===
```

## Pruebas

Para probar la conexión y carga de datos sin ejecutar la interfaz:

```bash
mvn compile
java -cp "target/classes;%USERPROFILE%\.m2\repository\com\h2database\h2\2.2.220\h2-2.2.220.jar" com.redseismica.test.TestDatabase
```

## Consola H2 (Opcional)

Si deseas acceder a la consola web de H2 para inspeccionar los datos, puedes modificar `DatabaseConfig.java` para usar una base de datos persistente:

```java
private static final String DB_URL = "jdbc:h2:~/redseismica";
```

Y luego ejecutar:
```bash
java -jar %USERPROFILE%\.m2\repository\com\h2database\h2\2.2.220\h2-2.2.220.jar
```

## Notas

- Los datos se cargan cada vez que se ejecuta la aplicación
- La base de datos es en memoria, por lo que los cambios no persisten
- Para hacer la base de datos persistente, cambiar la URL de conexión en `DatabaseConfig.java`

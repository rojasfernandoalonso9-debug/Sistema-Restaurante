# Guía técnica

## Capas

- `controller`: endpoints REST, DTO de entrada y validación HTTP.
- service: casos transaccionales de pedidos, recetas, cobros, inventario, catálogo, mesas, empleados y reportes.
- `repository`: persistencia JPA.
- entity: entidades relacionales para órdenes y líneas, productos/categorías, mesas, movimientos, ventas, empleados y usuarios.
- `security`: emisión y validación de JWT; contraseñas BCrypt.
- `config`: autorización stateless y CORS configurable.

## Esquema

`database/schema.sql` crea tablas normalizadas para cuentas, categorías, productos, recetas, mesas, pedidos, líneas de pedido, movimientos de inventario, ventas y personal. Las claves foráneas, restricciones CHECK e índices preservan integridad y búsquedas habituales. El esquema se aplica al iniciar MySQL y Hibernate usa `ddl-auto: validate` para detectar discrepancias al arrancar.

Las operaciones comerciales usan las tablas relacionales. business_records queda para notas sencillas. Los pedidos calculan importes desde el catálogo, guardan el precio vendido en cada línea y bloquean mesas ocupadas. Los cobros exigen pedidos entregados y usan una clave única por pedido. Las anulaciones guardan usuario, fecha y motivo. Los movimientos actualizan existencias dentro de una transacción y bloquean salidas mayores que el stock.



Cada receta de `product_ingredients` indica la cantidad de un insumo por unidad de producto. `PUT /api/products/{id}/recipe` reemplaza la receta y `GET` la consulta. Al avanzar un pedido de `READY` a `DELIVERED`, el servicio calcula los consumos de todas sus líneas, bloquea los insumos por ID, valida la disponibilidad total y crea movimientos `OUT` en una transacción. Si falta stock, la transición falla con HTTP 409 y el pedido permanece `READY`. Los productos sin receta no descuentan existencias. Las entradas y salidas manuales siguen disponibles.

## Seguridad

- `POST /api/auth/setup` solo registra al primer usuario y exige contraseña de 12 caracteres.
- `POST /api/auth/login` entrega un JWT de duración configurable.
- `JWT_SECRET` debe ser privada, aleatoria y de al menos 32 caracteres.
- Las rutas de módulo necesitan el encabezado `Authorization: Bearer <token>`.
- La lista y gestión de usuarios se limita a `ROLE_ADMIN`; la contraseña nunca se incluye en la respuesta.
- En un despliegue público, añade HTTPS, límites de intentos de acceso, rotación de secretos y política de respaldo.

## Variables

| Variable | Uso |
|---|---|
| `DB_URL` | URL JDBC MySQL |
| `DB_USERNAME`, `DB_PASSWORD` | Cuenta de base local/servidor |
| `JWT_SECRET` | Firma JWT privada (32+ caracteres) |
| `JWT_EXPIRATION_MS` | Duración del token; predeterminado 24 h |
| `SERVER_PORT` | Puerto backend; predeterminado 8080 |
| `CORS_ORIGIN` | Origen de frontend permitido |
| `VITE_API_URL` | URL base API en el frontend |

## Validación local

La suite backend usa H2 en modo MySQL para pruebas de API, transiciones de pedido, consumo de receta, restricciones de stock, checkout, permisos y reportes. Ejecuta `mvn test` en `backend/`. El frontend ejecuta `npm test` y `npm run build` en `frontend/`, incluyendo la validación del paquete XLSX.

La base MySQL de destino debe estar disponible para validar esquema y permisos antes de desplegar. H2 no sustituye esa prueba operativa.

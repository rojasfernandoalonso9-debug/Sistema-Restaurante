# Verificación

## Comandos

Desde `frontend/`: `npm install`, `npm run build`, `npm test`.

Desde `backend/`: `mvn test` (requiere JDK 21 y Maven 3.9+).

## Cobertura automatizada

- La prueba integrada crea cuenta, categoría, producto, mesa, insumo y movimiento.
- Crea un pedido y valida su total calculado, secuencia de estados y cobro único.
- Comprueba historial de stock, rechazo de salida excesiva y reporte de venta.
- Anula la venta y confirma que ingresos/reporte la excluyen.
- Prueba las validaciones del frontend para nombres, precios y cantidades.
- El contexto de test usa H2 en memoria; no modifica ni requiere MySQL local.

Antes de desplegar, valida el esquema en MySQL 8 y revisa permisos y respaldos.

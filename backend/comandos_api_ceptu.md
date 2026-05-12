# CURLs para pruebas en los endpoints

## 0. Autenticación y Seguridad
### Registrar un nuevo usuario (Ej: Rol ADMIN)
```
curl -X POST http://localhost:8080/api/v1/auth/register \
-H "Content-Type: application/json" \
-d '{"email":"admin@unimagdalena.edu.co", "password":"securepassword123", "role":"ROLE_ADMIN"}'
```

### Iniciar sesión (Obtener JWT Token)
```
curl -X POST http://localhost:8080/api/v1/auth/login \
-H "Content-Type: application/json" \
-d '{"email":"admin@unimagdalena.edu.co", "password":"securepassword123"}'
```
*(Copia el token recibido en la respuesta y reemplaza `[TU_TOKEN_JWT]` en los siguientes comandos)*

---

## 1. Módulo de clientes
### Obtener todos los clientes
```
curl -X GET http://localhost:8080/api/v1/customers \
-H "Authorization: Bearer [TU_TOKEN_JWT]"
```

### Obtener cliente por ID
```
curl -X GET http://localhost:8080/api/v1/customers/[UUID_CLIENTE] \
-H "Authorization: Bearer [TU_TOKEN_JWT]"
```

## 2. Módulo de direcciones
### Crear dirección para un cliente
```
curl -X POST http://localhost:8080/api/v1/customers/[UUID_CLIENTE]/addresses \
-H "Content-Type: application/json" \
-H "Authorization: Bearer [TU_TOKEN_JWT]" \
-d '{"street":"Carrera 15 # 22-10", "city":"Santa Marta", "state":"Magdalena", "zipCode":"470001", "country":"Colombia"}'
```

### Listar direcciones de un cliente
```
curl -X GET http://localhost:8080/api/v1/customers/[UUID_CLIENTE]/addresses \
-H "Authorization: Bearer [TU_TOKEN_JWT]"
```

## 3. Módulo de categorías
### Crear categoría
```
curl -X POST http://localhost:8080/api/v1/categories \
-H "Content-Type: application/json" \
-H "Authorization: Bearer [TU_TOKEN_JWT]" \
-d '{"name":"Ropa Universitaria", "description":"Prendas institucionales oficiales"}'
```

### Listar categorías
```
curl -X GET http://localhost:8080/api/v1/categories \
-H "Authorization: Bearer [TU_TOKEN_JWT]"
```

## 4. Módulo de productos e inventario
### Crear producto (con inventario inicial)
```
curl -X POST http://localhost:8080/api/v1/products \
-H "Content-Type: application/json" \
-H "Authorization: Bearer [TU_TOKEN_JWT]" \
-d '{"categoryId":"[UUID_CATEGORIA]", "name":"Sudadera Oficial Unimag", "sku":"SUD-001", "price":85000.00, "initialStock":50, "minimumStock":10}'
```

### Actualizar inventario
```
curl -X PUT http://localhost:8080/api/v1/products/[UUID_PRODUCTO]/inventory \
-H "Content-Type: application/json" \
-H "Authorization: Bearer [TU_TOKEN_JWT]" \
-d '{"availableStock":100, "minimumStock":20}'
```

## 5. Módulo de pedidos
### Crear pedido (Estado inicial: CREATED)
```
curl -X POST http://localhost:8080/api/v1/orders \
-H "Content-Type: application/json" \
-H "Authorization: Bearer [TU_TOKEN_JWT]" \
-d '{"customerId":"[UUID_CLIENTE]", "addressId":"[UUID_DIRECCION]", "items":[{"productId":"[UUID_PRODUCTO]", "quantity":2}]}'
```

### Confirmar pago (Descuenta stock y pasa a PAID)
```
curl -X PUT http://localhost:8080/api/v1/orders/[UUID_PEDIDO]/pay \
-H "Authorization: Bearer [TU_TOKEN_JWT]"
```

### Despachar pedido (Pasa a SHIPPED)
```
curl -X PUT http://localhost:8080/api/v1/orders/[UUID_PEDIDO]/ship \
-H "Authorization: Bearer [TU_TOKEN_JWT]"
```

### Entregar pedido (Pasa a DELIVERED)
```
curl -X PUT http://localhost:8080/api/v1/orders/[UUID_PEDIDO]/deliver \
-H "Authorization: Bearer [TU_TOKEN_JWT]"
```

### Cancelar pedido
```
curl -X PUT http://localhost:8080/api/v1/orders/[UUID_PEDIDO]/cancel \
-H "Content-Type: application/json" \
-H "Authorization: Bearer [TU_TOKEN_JWT]" \
-d '{"notes":"Solicitud del cliente"}'
```

## 6. Reportes
### Productos con bajo stock
```
curl -X GET http://localhost:8080/api/v1/reports/low-stock-products \
-H "Authorization: Bearer [TU_TOKEN_JWT]"
```

### Ingresos mensuales
```
curl -X GET http://localhost:8080/api/v1/reports/monthly-income \
-H "Authorization: Bearer [TU_TOKEN_JWT]"
```

### Top clientes
```
curl -X GET "http://localhost:8080/api/v1/reports/top-customers?limit=5" \
-H "Authorization: Bearer [TU_TOKEN_JWT]"
```

### Productos más vendidos (Rango de fechas)
```
curl -X GET "http://localhost:8080/api/v1/reports/best-selling-products?startDate=2026-01-01T00:00:00Z&endDate=2026-12-31T23:59:59Z&limit=10" \
-H "Authorization: Bearer [TU_TOKEN_JWT]"
```

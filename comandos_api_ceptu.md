# CURLs para pruebas en los endpoints

## 1. Módulo de clientes
### Obtener todos los clientes
```
curl -X GET http://localhost:8080/api/v1/customers
```

### Obtener cliente por ID
```
curl -X GET http://localhost:8080/api/v1/customers/[UUID_CLIENTE]
```

## 2. Módulo de direcciones
### Crear dirección para un cliente
```
curl -X POST http://localhost:8080/api/v1/customers/[UUID_CLIENTE]/addresses \\
-H "Content-Type: application/json" \\
-d '{"street":"Carrera 15 # 22-10", "city":"Santa Marta", "state":"Magdalena", "zipCode":"470001", "country":"Colombia"}'
```

### Listar direcciones de un cliente
```
curl -X GET http://localhost:8080/api/v1/customers/[UUID_CLIENTE]/addresses
```

## 3. Módulo de categorías
### Crear categoría
```
curl -X POST http://localhost:8080/api/v1/categories \\
-H "Content-Type: application/json" \\
-d '{"name":"Ropa Universitaria", "description":"Prendas institucionales oficiales"}'
```

### Listar categorías
```
curl -X GET http://localhost:8080/api/v1/categories
```

## 4. Módulo de productos e inventario
### Crear producto (con inventario inicial)
```
curl -X POST http://localhost:8080/api/v1/products \\
-H "Content-Type: application/json" \\
-d '{"categoryId":"[UUID_CATEGORIA]", "name":"Sudadera Oficial Unimag", "sku":"SUD-001", "price":85000.00, "availableStock":50, "minimumStock":10}'
```

### Actualizar inventario
```
curl -X PUT http://localhost:8080/api/v1/products/[UUID_PRODUCTO]/inventory \\
-H "Content-Type: application/json" \\
-d '{"availableStock":100, "minimumStock":20}'
```

## 5. Módulo de pedidos
### Crear pedido (Estado inicial: CREATED)
```
curl -X POST http://localhost:8080/api/v1/orders \\
-H "Content-Type: application/json" \\
-d '{"customerId":"[UUID_CLIENTE]", "addressId":"[UUID_DIRECCION]", "items":[{"productId":"[UUID_PRODUCTO]", "quantity":2}]}'
```

### Confirmar pago (Descuenta stock y pasa a PAID)
```
curl -X PUT http://localhost:8080/api/v1/orders/[UUID_PEDIDO]/pay
```

### Despachar pedido (Pasa a SHIPPED)
```
curl -X PUT http://localhost:8080/api/v1/orders/[UUID_PEDIDO]/ship
```

### Entregar pedido (Pasa a DELIVERED)
```
curl -X PUT http://localhost:8080/api/v1/orders/[UUID_PEDIDO]/deliver
```

### Cancelar pedido
```
curl -X PUT http://localhost:8080/api/v1/orders/[UUID_PEDIDO]/cancel \\
-H "Content-Type: application/json" \\
-d '{"notes":"Solicitud del cliente"}'
```

## 6. Reportes
### Productos con bajo stock
```
curl -X GET http://localhost:8080/api/v1/reports/low-stock-products
```

### Ingresos mensuales
```
curl -X GET http://localhost:8080/api/v1/reports/monthly-income
```

### Top clientes
```
curl -X GET "http://localhost:8080/api/v1/reports/top-customers?limit=5"
```

### Productos más vendidos (Rango de fechas)
```
curl -X GET "http://localhost:8080/api/v1/reports/best-selling-products?startDate=2026-01-01T00:00:00Z&endDate=2026-12-31T23:59:59Z&limit=10"
```


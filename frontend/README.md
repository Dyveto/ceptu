# Ceptu - Frontend

Este directorio contiene el cliente web del proyecto Ceptu, desarrollado con Vite 8.0.10. La interfaz está diseñada para integrarse con el backend de Spring Boot y manejar la capa de seguridad mediante JWT.

## Requisitos Previos

* **Node.js:** v22.22.2 (LTS) o superior.
* **Gestor de paquetes:** npm (v10.9.7 o superior).

## Configuración del Entorno

1. Entrar al directorio del frontend:
```bash
cd frontend

```


2. Instalar las dependencias (asegurando la versión específica de Vite):
```bash
npm install

```



## Scripts Disponibles

En el directorio del proyecto, puedes ejecutar los siguientes comandos:

### `npm run dev`

Inicia el servidor de desarrollo en modo local.
Vite levantará la aplicación en: [http://localhost:5173]

### `npm run build`

Empaqueta la aplicación para producción en la carpeta `dist/`.
Utiliza Rolldown para un bundling optimizado y rápido.

### `npm run preview`

Permite previsualizar localmente la versión de producción generada por el comando build.

## Integración con el Backend

Por defecto, el frontend espera que el servidor API de Ceptu esté corriendo en:

* **URL:** `http://localhost:8080`

## Notas de Versión

Este proyecto utiliza **Vite 8.0.10** para garantizar la estabilidad del entorno de desarrollo y evitar regresiones conocidas en versiones menores posteriores (.11/.12). No actualizar la dependencia de Vite sin previa validación técnica.
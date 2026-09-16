# Escuela Colombiana de Ingeniería Julio Garavito
## Arquitectura de Software – ARSW
### Laboratorio – Parte 2: BluePrints API con Seguridad JWT (OAuth 2.0)
### Carlos Duban Rojas y Juan Daniel Bogotá Fuentes

Este laboratorio extiende la **Parte 1** ([Lab_P1_BluePrints_Java21_API](https://github.com/DECSIS-ECI/Lab_P1_BluePrints_Java21_API)) agregando **seguridad a la API** usando **Spring Boot 3, Java 21 y JWT (OAuth 2.0)**.  
El API se convierte en un **Resource Server** protegido por tokens Bearer firmados con **RS256**.  
Incluye un endpoint didáctico `/auth/login` que emite el token para facilitar las pruebas.

---

## Objetivos
- Implementar seguridad en servicios REST usando **OAuth2 Resource Server**.
- Configurar emisión y validación de **JWT**.
- Proteger endpoints con **roles y scopes** (`blueprints.read`, `blueprints.write`).
- Integrar la documentación de seguridad en **Swagger/OpenAPI**.

---

## Requisitos
- JDK 21
- Maven 3.9+
- Git

---

## Ejecución del proyecto
1. Clonar o descomprimir el proyecto:
   ```bash
   git clone https://github.com/DECSIS-ECI/Lab_P2_BluePrints_Java21_API_Security_JWT.git
   cd Lab_P2_BluePrints_Java21_API_Security_JWT
   ```
   ó si el profesor entrega el `.zip`, descomprimirlo y entrar en la carpeta.

2. Ejecutar con Maven:
   ```bash
   mvn -q -DskipTests spring-boot:run
   ```

3. Verificar que la aplicación levante en `http://localhost:8080`.

---

## Endpoints principales

### 1. Login (emite token)
```
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "student",
  "password": "student123"
}
```
Respuesta:
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

### 2. Consultar blueprints (requiere scope `blueprints.read`)
```
GET http://localhost:8080/api/blueprints
Authorization: Bearer <ACCESS_TOKEN>
```

### 3. Crear blueprint (requiere scope `blueprints.write`)
```
POST http://localhost:8080/api/blueprints
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json

{
  "name": "Nuevo Plano"
}
```

---

## Swagger UI
- URL: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- Pulsa **Authorize**, ingresa el token en el formato:
  ```
  Bearer eyJhbGciOi...
  ```

---

## Estructura del proyecto
```
src/main/java/co/edu/eci/blueprints/
  ├── api/BlueprintController.java       # Endpoints protegidos
  ├── auth/AuthController.java           # Login didáctico para emitir tokens
  ├── config/OpenApiConfig.java          # Configuración Swagger + JWT
  └── security/
       ├── SecurityConfig.java
       ├── MethodSecurityConfig.java
       ├── JwtKeyProvider.java
       ├── InMemoryUserService.java
       └── RsaKeyProperties.java
src/main/resources/
  └── application.yml
```

---

## Actividades propuestas
1. Revisar el código de configuración de seguridad (`SecurityConfig`) e identificar cómo se definen los endpoints públicos y protegidos.

Se revisó la clase SecurityConfig para entender qué rutas quedan abiertas y cuáles piden token. /auth/login y la documentación de Swagger son públicas, mientras que todo lo que empieza por /api/ exige un token válido con el scope correspondiente, cualquier otra ruta que no esté listada queda bloqueada por defecto. Esto se comprobó corriendo la aplicación y probando primero sin token, luego pidiendo el token en /auth/login, y por último usando ese token para consultar /api/blueprints.

Este comportamiento tiene sentido con lo visto en clase sobre OAuth2 Resource Server, la API no maneja sesiones de usuario, solo valida el token que llega en cada petición, así que es coherente con la idea de que un servicio REST no debería depender de un estado guardado en el servidor.

**Evidencia**

```
PS> curl.exe -i http://localhost:8080/api/blueprints
HTTP/1.1 401
WWW-Authenticate: Bearer
Content-Length: 0
```

```
PS> curl.exe -i -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{ \"username\":\"student\",\"password\":\"student123\" }'
HTTP/1.1 200
Content-Type: application/json

{"access_token":"eyJhbGciOiJSUzI1NiJ9...","token_type":"Bearer","expires_in":3600}
```

```
PS> curl.exe -i http://localhost:8080/api/blueprints -H "Authorization: Bearer eyJhbGciOiJSUzI1NiJ9..."
HTTP/1.1 200
Content-Type: application/json

[{"name":"Casa de campo","id":"b1"},{"name":"Edificio urbano","id":"b2"}]
```


2. Explorar el flujo de login y analizar las claims del JWT emitido.

Se hizo login contra /auth/login con el usuario student y se tomó el access_token que devolvió la respuesta. Ese token se pegó en [jwt.io](https://jwt.io) para ver su contenido decodificado sin necesidad de escribir código adicional.

![alt text](docs/img/AccessToken.png)

En el token se puede ver el algoritmo de firma (RS256) en el header, y en el payload las claims que trae: quién lo emitió (iss), para qué usuario es (sub), cuándo se emitió y cuándo expira (iat/exp), y los permisos que tiene (scope). La resta entre exp e iat da 3600 segundos, que coincide con el expires_in que devolvió el login.

![alt text](docs/img/actividad2-jwt-claims.png)


3. Extender los scopes (`blueprints.read`, `blueprints.write`) para controlar otros endpoints de la API, del laboratorio P1 trabajado.
4. Modificar el tiempo de expiración del token y observar el efecto.
5. Documentar en Swagger los endpoints de autenticación y de negocio.

---

## Lecturas recomendadas
- [Spring Security Reference – OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
- [Spring Boot – Securing Web Applications](https://spring.io/guides/gs/securing-web/)
- [JSON Web Tokens – jwt.io](https://jwt.io/introduction)

---

## Licencia
Proyecto educativo con fines académicos – Escuela Colombiana de Ingeniería Julio Garavito.

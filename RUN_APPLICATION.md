# Running the Product Catalog Application

## Quick Start Commands

### Option 1: Run with Maven (Recommended)

```bash
cd /home/ubuntu/Desktop/demo/BFS-Batch10-ProductCatalog
mvn spring-boot:run
```

**Output:**
```
2026-08-25 14:30:45.123  INFO 12345 --- [main] c.w.p.ProductCatalogApplication : Starting ProductCatalogApplication v1.0.0
2026-08-25 14:30:45.234  INFO 12345 --- [main] c.w.p.ProductCatalogApplication : The following profiles are active: 
2026-08-25 14:30:47.123  INFO 12345 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8080 (http)
2026-08-25 14:30:47.234  INFO 12345 --- [main] c.w.p.ProductCatalogApplication : Started ProductCatalogApplication in 2.111 seconds
```

### Option 2: Run Packaged JAR

```bash
# First build
mvn clean package -DskipTests

# Then run
java -jar target/product-catalog-1.0.0.jar
```

### Option 3: Run from IDE

1. Open IDE (IntelliJ, Eclipse, VS Code)
2. Open project: `/home/ubuntu/Desktop/demo/BFS-Batch10-ProductCatalog`
3. Right-click `ProductCatalogApplication.java`
4. Select "Run"

---

## Access Points

### After Application Starts

| Resource | URL | Port |
|----------|-----|------|
| **Swagger UI** | http://localhost:8080/productcatalog/swagger-ui.html | 8080 |
| **OpenAPI JSON** | http://localhost:8080/productcatalog/v3/api-docs | 8080 |
| **OpenAPI YAML** | http://localhost:8080/productcatalog/v3/api-docs.yaml | 8080 |
| **H2 Console** | http://localhost:8080/productcatalog/h2-console | 8080 |
| **API Base** | http://localhost:8080/productcatalog/products | 8080 |

---

## Application Startup Sequence

```
1. Spring Boot initializes
   └─ Loads application.properties
   └─ Configures DataSource (H2)
   └─ Sets up JPA/Hibernate

2. Component scanning
   └─ Finds @Configuration classes
   └─ Registers OpenApiConfig
   └─ Finds @RestController classes
   └─ Registers ProductController

3. Dependency injection
   └─ Creates ProductService bean
   └─ Creates ProductRepository bean
   └─ Injects into controller

4. Springdoc initialization
   └─ Scans all @RestController classes
   └─ Reads @Tag, @Operation, @ApiResponse
   └─ Extracts @Schema from DTOs
   └─ Generates OpenAPI specification

5. Embedded Tomcat starts
   └─ Listens on port 8080
   └─ All endpoints ready

6. Application ready
   └─ Swagger UI accessible
   └─ REST API functional
```

---

## Configuration on Startup

### Database
```
Driver: H2
URL: jdbc:h2:mem:productcatalogdb
Username: sa
Password: (empty)
DDL: update (creates tables on startup)
```

### Server
```
Port: 8080
Context Path: /productcatalog
Compression: enabled
Error Handling: enabled
```

### Swagger
```
Enabled: true
Path: /swagger-ui.html
API Docs: /v3/api-docs
Sort: alphabetical
```

---

## First Steps After Running

### 1. Verify Application is Running
```bash
curl http://localhost:8080/productcatalog/swagger-ui.html
```

Expected: HTML content of Swagger UI

### 2. Check OpenAPI Definition
```bash
curl http://localhost:8080/productcatalog/v3/api-docs | jq
```

Expected: JSON OpenAPI specification

### 3. Create First Product (via Swagger)
1. Open Swagger UI
2. Click "POST /products"
3. Click "Try it out"
4. Paste:
```json
{
  "productCode": "PROD-001",
  "name": "Sample Laptop",
  "description": "Test laptop",
  "category": "Electronics",
  "price": 999.99,
  "stockQuantity": 10
}
```
5. Click "Execute"
6. Expected: 201 Created response

### 4. Retrieve All Products
1. Click "GET /products"
2. Click "Try it out"
3. Click "Execute"
4. Expected: 200 OK with product list

---

## Troubleshooting

### Port 8080 Already in Use

```bash
# Find process using port 8080
lsof -i :8080

# Kill process (example)
kill -9 <PID>

# Or use different port
java -jar target/product-catalog-1.0.0.jar --server.port=9090
```

### Application Won't Start

Check logs:
```bash
# If using Maven
mvn spring-boot:run -X

# If using JAR
java -jar target/product-catalog-1.0.0.jar --debug
```

### Swagger UI Blank

1. Clear browser cache
2. Try incognito mode
3. Check browser console for errors
4. Verify OpenAPI JSON is accessible

### Database Connection Error

Ensure H2 is in classpath:
```bash
mvn dependency:tree | grep h2
```

---

## System Requirements

- **Java**: 17 or higher
- **Maven**: 3.6.0 or higher
- **Memory**: 512 MB minimum
- **Disk Space**: 200 MB for build
- **Port**: 8080 must be available

---

## Useful Maven Commands

```bash
# Clean build
mvn clean

# Build without tests
mvn clean install -DskipTests

# Run tests
mvn test

# Run application
mvn spring-boot:run

# Package as JAR
mvn clean package

# Run specific class
mvn exec:java -Dexec.mainClass="com.wipro.productcatalog.ProductCatalogApplication"
```

---

## Environment Variables (Optional)

```bash
# Set custom port
export SERVER_PORT=9090

# Set database URL
export DATASOURCE_URL=jdbc:h2:~/productcatalog

# Set JPA DDL mode
export JPA_DDL_AUTO=validate

# Enable debug logging
export LOG_LEVEL=DEBUG
```

---

## Health Check After Startup

```bash
# Check if application is running
curl -s http://localhost:8080/productcatalog/actuator/health 2>/dev/null

# Check if Swagger UI is accessible
curl -s http://localhost:8080/productcatalog/swagger-ui.html | grep -q "<!DOCTYPE" && echo "Swagger UI OK" || echo "Swagger UI Not Found"

# Verify endpoints
curl -s http://localhost:8080/productcatalog/products | jq length
```

---

## Stopping the Application

### If Running in Terminal
```bash
Press: Ctrl + C
```

### If Running in Background
```bash
# Find process
ps aux | grep java

# Kill process
kill <PID>
```

### If Running as JAR
```bash
# Find Java process
jps

# Kill by name
pkill -f product-catalog
```

---

**Status**: Ready to Run  
**Last Updated**: 2026-08-25  

🚀 **Application is ready to be started!**

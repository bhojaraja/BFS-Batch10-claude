# Swagger Integration - Quick Start Guide

## 🚀 Getting Started (5 Minutes)

### Step 1: Verify Dependency is Added

Check `pom.xml` has Springdoc dependency:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.1.0</version>
</dependency>
```

### Step 2: Run Maven Install (Optional)

```bash
mvn clean install
```

### Step 3: Start Spring Boot Application

```bash
# Option 1: IDE - Run ProductCatalogApplication.java
# Option 2: Maven
mvn spring-boot:run

# Option 3: Java
java -jar target/product-catalog-1.0.0.jar
```

### Step 4: Open Swagger UI

Open browser and navigate to:

```
http://localhost:8080/productcatalog/swagger-ui.html
```

---

## 📚 What You'll See

```
┌─────────────────────────────────────────────────────────┐
│  Product Catalog Management System API                  │
│  v1.0.0                                                 │
│  REST API for managing product catalog...              │
├─────────────────────────────────────────────────────────┤
│  PRODUCTS (6 endpoints)                                 │
├─────────────────────────────────────────────────────────┤
│ ▾ POST /products                [201 Created]           │
│   Create a new product                                  │
│                                                          │
│ ▾ GET /products                 [200 OK]                │
│   Retrieve all products from the catalog                │
│                                                          │
│ ▾ GET /products/{id}            [200 OK]                │
│   Retrieve a specific product by its ID                 │
│                                                          │
│ ▾ PUT /products/{id}            [200 OK]                │
│   Update an existing product                            │
│                                                          │
│ ▾ DELETE /products/{id}         [204 No Content]        │
│   Delete a product from the catalog                     │
│                                                          │
│ ▾ GET /products/category/{cat}  [200 OK]                │
│   Retrieve all products in a specific category          │
└─────────────────────────────────────────────────────────┘
```

---

## 🔍 Testing an Endpoint

### Example: Create a Product

1. **Click** `POST /products` to expand it
2. **Click** "Try it out" button
3. **Paste** this request body:

```json
{
  "productCode": "LAPTOP-001",
  "name": "Dell XPS 13",
  "description": "13-inch FHD laptop with Intel i7",
  "category": "Electronics",
  "price": 1299.99,
  "stockQuantity": 25
}
```

4. **Click** "Execute"
5. **View** response (should be 201 Created with product details)

---

## 📋 All Endpoints Quick Reference

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| POST | /products | Create product | 201 |
| GET | /products | List all products | 200 |
| GET | /products/{id} | Get product by ID | 200 |
| PUT | /products/{id} | Update product | 200 |
| DELETE | /products/{id} | Delete product | 204 |
| GET | /products/category/{cat} | Filter by category | 200 |

---

## 🧪 Test Scenarios

### Scenario 1: Get All Products
```
GET /products
Response: 200 OK [Array of products]
```

### Scenario 2: Get Non-Existent Product
```
GET /products/999
Response: 404 Not Found
{
  "timestamp": "2026-08-25T10:30:45",
  "status": 404,
  "message": "Product not found with ID: 999"
}
```

### Scenario 3: Create Duplicate Product
```
POST /products
Body: {same productCode as existing}
Response: 409 Conflict
{
  "timestamp": "2026-08-25T10:30:45",
  "status": 409,
  "message": "Product code already exists: LAPTOP-001"
}
```

### Scenario 4: Invalid Data (Validation)
```
POST /products
Body: {"name": "", "price": -50, "stockQuantity": -10}
Response: 400 Bad Request
{
  "timestamp": "2026-08-25T10:30:45",
  "status": 400,
  "message": "name: Product name is required, price: Price must be greater than 0, ..."
}
```

---

## 📖 Additional Documentation

### View OpenAPI Specification

**JSON Format:**
```
http://localhost:8080/productcatalog/v3/api-docs
```

**YAML Format:**
```
http://localhost:8080/productcatalog/v3/api-docs.yaml
```

### Access H2 Database Console

```
http://localhost:8080/productcatalog/h2-console
```

**Credentials:**
- Driver: `org.h2.Driver`
- JDBC URL: `jdbc:h2:mem:productcatalogdb`
- Username: `sa`
- Password: (leave blank)

---

## 💡 Tips & Tricks

### Tip 1: Export API Definition
1. Copy OpenAPI JSON URL
2. Open Postman
3. Click "Import"
4. Paste URL
5. Creates Postman collection automatically

### Tip 2: Filter Endpoints
Use the search box in Swagger UI to find specific endpoints

### Tip 3: View Response Schema
Click on response schema names to expand and see field descriptions

### Tip 4: Check Request Duration
Enable in `application.properties`:
```properties
springdoc.swagger-ui.display-request-duration=true
```

---

## ⚙️ Configuration

### Default Configuration

```properties
# Swagger UI is enabled by default
springdoc.swagger-ui.enabled=true

# Access paths
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs

# UI options
springdoc.swagger-ui.operations-sorter=method
springdoc.swagger-ui.tags-sorter=alpha
springdoc.swagger-ui.display-request-duration=true
```

### Disable Swagger UI (Production)

Edit `application.properties`:
```properties
springdoc.swagger-ui.enabled=false
```

---

## 🐛 Troubleshooting

| Problem | Solution |
|---------|----------|
| Swagger UI not loading | Ensure app is running on port 8080 |
| "Loading..." stuck | Clear browser cache or try incognito |
| No endpoints shown | Verify controller has `@RestController` |
| Schema shows as string | Add `@Schema` annotation to DTO fields |
| 500 error | Check application logs for stack trace |

---

## 📚 Files Modified/Created

### Modified:
- `pom.xml` - Added Springdoc dependency
- `application.properties` - Added Swagger configuration
- `ProductRequestDTO.java` - Added `@Schema` annotations
- `ProductResponseDTO.java` - Added `@Schema` annotations
- `ErrorResponse.java` - Added `@Schema` annotations
- `ProductController.java` - Added Swagger annotations (partial)

### Created:
- `OpenApiConfig.java` - OpenAPI configuration
- `SwaggerApiResponses.java` - Reusable response annotations
- `SWAGGER_INTEGRATION.md` - Complete integration guide
- `SWAGGER_QUICK_START.md` - This file

---

## 🔗 Useful Links

- **Swagger UI**: http://localhost:8080/productcatalog/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/productcatalog/v3/api-docs
- **H2 Console**: http://localhost:8080/productcatalog/h2-console
- **Springdoc Docs**: https://springdoc.org
- **OpenAPI Spec**: https://spec.openapis.org

---

## ✅ Verification Checklist

- [ ] Maven dependency added to pom.xml
- [ ] OpenApiConfig.java created
- [ ] application.properties updated with Swagger config
- [ ] DTOs have @Schema annotations
- [ ] Application starts without errors
- [ ] Swagger UI accessible at correct URL
- [ ] Can expand endpoints and see documentation
- [ ] "Try it out" feature works
- [ ] Test request/response cycle completes
- [ ] Can see error responses for invalid data

---

**Quick Start Version**: 1.0.0  
**Last Updated**: 2026-08-25  

Ready to explore your API documentation! 🎉

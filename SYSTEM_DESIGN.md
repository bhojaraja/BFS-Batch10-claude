# Product Catalog Management System - System Design Document

---

## 1. HIGH LEVEL DESIGN

### 1.1 System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                             │
│  (Web Browser / Mobile App / REST Client)                        │
└────────────────────┬────────────────────────────────────────────┘
                     │ HTTP/HTTPS
┌────────────────────▼────────────────────────────────────────────┐
│                    API GATEWAY LAYER                             │
│  - Request Routing                                               │
│  - Rate Limiting                                                 │
│  - Authentication/Authorization                                  │
└────────────────────┬────────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────────┐
│                  PRESENTATION LAYER                              │
│  ┌──────────────────────────────────────────────────────┐       │
│  │  REST Controllers                                     │       │
│  │  - ProductController                                 │       │
│  │  - CategoryController                                │       │
│  └──────────────────────────────────────────────────────┘       │
└────────────────────┬────────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────────┐
│                  BUSINESS LOGIC LAYER                            │
│  ┌──────────────────────────────────────────────────────┐       │
│  │  Services                                             │       │
│  │  - ProductService                                     │       │
│  │  - CategoryService                                    │       │
│  │  - ValidationService                                  │       │
│  │  - ExceptionHandler                                   │       │
│  └──────────────────────────────────────────────────────┘       │
└────────────────────┬────────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────────┐
│                 PERSISTENCE LAYER                                │
│  ┌──────────────────────────────────────────────────────┐       │
│  │  Repositories (Data Access Objects)                   │       │
│  │  - ProductRepository                                  │       │
│  │  - CategoryRepository                                 │       │
│  │  - JPA/ORM Mapping                                    │       │
│  └──────────────────────────────────────────────────────┘       │
└────────────────────┬────────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────────┐
│                   DATABASE LAYER                                 │
│  (MySQL / PostgreSQL / Oracle)                                   │
│  - Products Table                                                │
│  - Categories Table                                              │
│  - Audit Logs Table                                              │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 Component Interaction Flow

```
Client Request
     │
     ▼
┌─────────────────────────┐
│  Controller             │ (Request validation, mapping)
│  - Receives HTTP request│
│  - Routes to Service    │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│  Service                │ (Business logic)
│  - Validation           │
│  - Business rules       │
│  - Exception handling   │
│  - Calls Repository     │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│  Repository             │ (Data access)
│  - Database queries     │
│  - Entity mapping       │
│  - Transaction mgmt     │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│  Database               │ (Persistence)
│  - Execute SQL          │
│  - Return data          │
└────────┬────────────────┘
         │
         ▼
Response (JSON) back through layers
```

### 1.3 Key Design Principles

- **Separation of Concerns:** Each layer has specific responsibilities
- **Single Responsibility:** Each class handles one specific task
- **Dependency Injection:** Loose coupling between components
- **RESTful Design:** Stateless, standard HTTP methods
- **Transaction Management:** Data consistency and ACID properties
- **Exception Handling:** Centralized error management
- **Validation:** Input validation at multiple layers

---

## 2. DATABASE DESIGN

### 2.1 Entity Relationship Diagram (ERD)

```
┌──────────────────────┐
│      Category        │
├──────────────────────┤
│ PK: category_id      │
│ category_name        │
│ description          │
│ created_at           │
│ updated_at           │
└──────┬───────────────┘
       │ (1:N)
       │ has many
       │
       │
┌──────▼───────────────┐
│      Product         │
├──────────────────────┤
│ PK: product_id       │
│ FK: category_id      │
│ name                 │
│ description          │
│ price                │
│ stock_quantity       │
│ created_at           │
│ updated_at           │
│ created_by           │
│ updated_by           │
│ version (optimistic  │
│    locking)          │
└──────────────────────┘

┌──────────────────────┐
│    Audit_Log         │
├──────────────────────┤
│ PK: log_id           │
│ product_id           │
│ operation (CRUD)     │
│ old_value            │
│ new_value            │
│ changed_by           │
│ changed_at           │
└──────────────────────┘
```

### 2.2 Database Schema

#### Table: categories
```sql
CREATE TABLE categories (
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category_name (category_name)
);
```

#### Table: products
```sql
CREATE TABLE products (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_code VARCHAR(50) NOT NULL UNIQUE,
    category_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    price DECIMAL(10, 2) NOT NULL CHECK (price > 0),
    stock_quantity INT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INT DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    INDEX idx_product_code (product_code),
    INDEX idx_category_id (category_id),
    INDEX idx_name (name),
    INDEX idx_created_at (created_at)
);
```

#### Table: audit_logs
```sql
CREATE TABLE audit_logs (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT,
    operation VARCHAR(20) NOT NULL,
    old_value JSON,
    new_value JSON,
    changed_by VARCHAR(100) NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE SET NULL,
    INDEX idx_product_id (product_id),
    INDEX idx_changed_at (changed_at),
    INDEX idx_operation (operation)
);
```

### 2.3 Database Indexes

| Table | Column | Type | Reason |
|-------|--------|------|--------|
| categories | category_name | UNIQUE | Prevent duplicate categories |
| products | product_code | UNIQUE | Unique product identifier |
| products | category_id | NORMAL | Foreign key joins |
| products | name | NORMAL | Search operations |
| products | created_at | NORMAL | Date range queries |
| audit_logs | product_id | NORMAL | Track changes per product |
| audit_logs | changed_at | NORMAL | Historical queries |

---

## 3. REST API DESIGN

### 3.1 API Endpoints

#### Product Endpoints

| HTTP | Endpoint | Description | Request Body | Response |
|------|----------|-------------|--------------|----------|
| POST | `/api/v1/products` | Create product | ProductDTO | ProductResponseDTO (201) |
| GET | `/api/v1/products` | List all products | - | Page<ProductResponseDTO> (200) |
| GET | `/api/v1/products/{id}` | Get product by ID | - | ProductResponseDTO (200) |
| PUT | `/api/v1/products/{id}` | Update product | ProductDTO | ProductResponseDTO (200) |
| DELETE | `/api/v1/products/{id}` | Delete product | - | Empty (204) |
| GET | `/api/v1/products/category/{categoryId}` | List by category | - | Page<ProductResponseDTO> (200) |
| GET | `/api/v1/products/search?name=value` | Search by name | - | Page<ProductResponseDTO> (200) |

#### Category Endpoints

| HTTP | Endpoint | Description | Request Body | Response |
|------|----------|-------------|--------------|----------|
| GET | `/api/v1/categories` | List all categories | - | List<CategoryDTO> (200) |
| POST | `/api/v1/categories` | Create category | CategoryDTO | CategoryDTO (201) |
| GET | `/api/v1/categories/{id}` | Get category by ID | - | CategoryDTO (200) |
| PUT | `/api/v1/categories/{id}` | Update category | CategoryDTO | CategoryDTO (200) |
| DELETE | `/api/v1/categories/{id}` | Delete category | - | Empty (204) |

### 3.2 Request/Response Examples

#### Create Product Request
```json
POST /api/v1/products
Content-Type: application/json

{
  "productCode": "PROD-001",
  "name": "Laptop",
  "description": "High performance laptop for professionals",
  "categoryId": 1,
  "price": 999.99,
  "stockQuantity": 50
}
```

#### Create Product Response (201)
```json
{
  "id": 1,
  "productCode": "PROD-001",
  "name": "Laptop",
  "description": "High performance laptop for professionals",
  "categoryId": 1,
  "categoryName": "Electronics",
  "price": 999.99,
  "stockQuantity": 50,
  "createdAt": "2026-08-24T10:30:00Z",
  "updatedAt": "2026-08-24T10:30:00Z",
  "createdBy": "admin",
  "updatedBy": "admin"
}
```

#### List Products Request
```
GET /api/v1/products?page=0&size=10&sort=name,asc
```

#### List Products Response (200)
```json
{
  "content": [
    {
      "id": 1,
      "productCode": "PROD-001",
      "name": "Laptop",
      "price": 999.99,
      "stockQuantity": 50,
      "categoryName": "Electronics"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "currentPage": 0,
  "pageSize": 10,
  "hasNext": true,
  "hasPrevious": false
}
```

#### Search by Category Request
```
GET /api/v1/products/category/1?page=0&size=10
```

#### Search by ID Request
```
GET /api/v1/products/1
```

#### Search by ID Response (200)
```json
{
  "id": 1,
  "productCode": "PROD-001",
  "name": "Laptop",
  "description": "High performance laptop for professionals",
  "categoryId": 1,
  "categoryName": "Electronics",
  "price": 999.99,
  "stockQuantity": 50,
  "createdAt": "2026-08-24T10:30:00Z",
  "updatedAt": "2026-08-24T10:30:00Z"
}
```

#### Update Product Request
```json
PUT /api/v1/products/1
Content-Type: application/json

{
  "name": "Laptop Pro",
  "price": 1299.99,
  "stockQuantity": 45
}
```

#### Delete Product Request
```
DELETE /api/v1/products/1
```

#### Delete Product Response (204)
```
No Content
```

### 3.3 Error Responses

#### 400 Bad Request
```json
{
  "timestamp": "2026-08-24T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fieldErrors": [
    {
      "field": "price",
      "message": "Price must be greater than 0"
    },
    {
      "field": "name",
      "message": "Product name is required"
    }
  ]
}
```

#### 404 Not Found
```json
{
  "timestamp": "2026-08-24T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product with ID 999 not found"
}
```

#### 409 Conflict (Concurrent Update)
```json
{
  "timestamp": "2026-08-24T10:30:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Product was modified; please refresh and try again"
}
```

#### 500 Internal Server Error
```json
{
  "timestamp": "2026-08-24T10:30:00Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred"
}
```

### 3.4 HTTP Status Codes

| Code | Meaning | Scenario |
|------|---------|----------|
| 200 | OK | Successful GET, PUT, PATCH |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Validation failure, invalid input |
| 401 | Unauthorized | Missing/invalid authentication |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Concurrent update conflict |
| 422 | Unprocessable Entity | Semantic validation error |
| 500 | Internal Server Error | Server exception |
| 503 | Service Unavailable | Database down, etc. |

---

## 4. PACKAGE STRUCTURE

### 4.1 Project Directory Layout

```
ProductCatalog/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── productcatalog/
│   │   │           ├── ProductCatalogApplication.java
│   │   │           │
│   │   │           ├── config/                    (Configuration)
│   │   │           │   ├── AppConfig.java
│   │   │           │   ├── SecurityConfig.java
│   │   │           │   └── DatabaseConfig.java
│   │   │           │
│   │   │           ├── controller/                (Presentation Layer)
│   │   │           │   ├── ProductController.java
│   │   │           │   ├── CategoryController.java
│   │   │           │   └── GlobalExceptionHandler.java
│   │   │           │
│   │   │           ├── dto/                       (Data Transfer Objects)
│   │   │           │   ├── ProductDTO.java
│   │   │           │   ├── ProductRequestDTO.java
│   │   │           │   ├── ProductResponseDTO.java
│   │   │           │   ├── CategoryDTO.java
│   │   │           │   └── PaginatedResponse.java
│   │   │           │
│   │   │           ├── entity/                    (Database Entities)
│   │   │           │   ├── Product.java
│   │   │           │   ├── Category.java
│   │   │           │   └── AuditLog.java
│   │   │           │
│   │   │           ├── exception/                 (Custom Exceptions)
│   │   │           │   ├── ProductNotFoundException.java
│   │   │           │   ├── DuplicateProductException.java
│   │   │           │   ├── InvalidProductException.java
│   │   │           │   ├── ConcurrentUpdateException.java
│   │   │           │   └── CategoryNotFoundException.java
│   │   │           │
│   │   │           ├── repository/                (Data Access Layer)
│   │   │           │   ├── ProductRepository.java
│   │   │           │   ├── CategoryRepository.java
│   │   │           │   └── AuditLogRepository.java
│   │   │           │
│   │   │           ├── service/                   (Business Logic Layer)
│   │   │           │   ├── ProductService.java
│   │   │           │   ├── ProductServiceImpl.java
│   │   │           │   ├── CategoryService.java
│   │   │           │   ├── CategoryServiceImpl.java
│   │   │           │   ├── ValidationService.java
│   │   │           │   └── AuditLogService.java
│   │   │           │
│   │   │           ├── validator/                 (Validation Logic)
│   │   │           │   ├── ProductValidator.java
│   │   │           │   ├── PriceValidator.java
│   │   │           │   └── StockValidator.java
│   │   │           │
│   │   │           ├── mapper/                    (DTO Mappers)
│   │   │           │   ├── ProductMapper.java
│   │   │           │   └── CategoryMapper.java
│   │   │           │
│   │   │           ├── util/                      (Utility Classes)
│   │   │           │   ├── DateUtil.java
│   │   │           │   └── ValidationUtil.java
│   │   │           │
│   │   │           └── security/                  (Security)
│   │   │               ├── JwtTokenProvider.java
│   │   │               ├── UserPrincipal.java
│   │   │               └── AuthenticationFilter.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       ├── db/
│   │       │   └── migration/
│   │       │       ├── V1__Initial_Schema.sql
│   │       │       └── V2__Add_Audit_Table.sql
│   │       └── logback-spring.xml
│   │
│   └── test/
│       ├── java/
│       │   └── com/productcatalog/
│       │       ├── controller/
│       │       │   └── ProductControllerTest.java
│       │       ├── service/
│       │       │   └── ProductServiceTest.java
│       │       ├── repository/
│       │       │   └── ProductRepositoryTest.java
│       │       └── validator/
│       │           └── ProductValidatorTest.java
│       └── resources/
│           ├── application-test.properties
│           └── test-data.sql
│
├── pom.xml                                        (Maven Configuration)
├── docker-compose.yml                            (Docker Setup)
├── README.md
└── .gitignore
```

### 4.2 Module Organization

```
Controller Layer (Presentation)
    ↓ (Depends on)
Service Layer (Business Logic)
    ↓ (Depends on)
Repository Layer (Data Access)
    ↓ (Depends on)
Database Layer (Persistence)

Cross-Cutting Concerns:
├── DTO (Transfer Objects)
├── Entity (Domain Objects)
├── Exception (Error Handling)
├── Validator (Input Validation)
├── Mapper (Object Mapping)
└── Config (Configuration)
```

---

## 5. LAYERED ARCHITECTURE

### 5.1 Presentation Layer (Controller)

**Responsibility:** Handle HTTP requests/responses

```java
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    private final ProductMapper productMapper;
    
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO request) {
        Product product = productService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productMapper.toResponseDTO(product));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(productMapper.toResponseDTO(product));
    }
    
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        Page<Product> products = productService.getAllProducts(page, size, sortBy);
        return ResponseEntity.ok(products.map(productMapper::toResponseDTO));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO request) {
        Product product = productService.updateProduct(id, request);
        return ResponseEntity.ok(productMapper.toResponseDTO(product));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponseDTO>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Product> products = productService.getProductsByCategory(categoryId, page, size);
        return ResponseEntity.ok(products.map(productMapper::toResponseDTO));
    }
}
```

**Key Responsibilities:**
- HTTP request mapping
- Parameter validation (using @Valid)
- Delegate to Service layer
- Response formatting
- HTTP status code assignment

---

### 5.2 Service Layer (Business Logic)

**Responsibility:** Implement business logic and rules

```java
public interface ProductService {
    Product createProduct(ProductRequestDTO request);
    Product getProductById(Long id);
    Page<Product> getAllProducts(int page, int size, String sortBy);
    Product updateProduct(Long id, ProductRequestDTO request);
    void deleteProduct(Long id);
    Page<Product> getProductsByCategory(Long categoryId, int page, int size);
}

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductValidator productValidator;
    private final AuditLogService auditLogService;
    
    @Transactional
    @Override
    public Product createProduct(ProductRequestDTO request) {
        // Validate input
        productValidator.validateProductRequest(request);
        
        // Check for duplicates
        if (productRepository.existsByProductCode(request.getProductCode())) {
            throw new DuplicateProductException(
                "Product code already exists: " + request.getProductCode());
        }
        
        // Check category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(
                    "Category not found: " + request.getCategoryId()));
        
        // Create product
        Product product = new Product();
        product.setProductCode(request.getProductCode());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCreatedBy(getCurrentUser());
        
        Product savedProduct = productRepository.save(product);
        
        // Audit log
        auditLogService.logOperation(savedProduct.getId(), "CREATE", null, savedProduct);
        
        log.info("Product created with ID: {}", savedProduct.getId());
        return savedProduct;
    }
    
    @Transactional
    @Override
    public Product updateProduct(Long id, ProductRequestDTO request) {
        // Validate input
        productValidator.validateProductRequest(request);
        
        // Get existing product
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                    "Product not found with ID: " + id));
        
        // Check version for optimistic locking
        if (!request.getVersion().equals(product.getVersion())) {
            throw new ConcurrentUpdateException(
                "Product was modified; please refresh and try again");
        }
        
        // Store old values for audit
        Product oldProduct = cloneProduct(product);
        
        // Update fields
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setUpdatedBy(getCurrentUser());
        product.setVersion(product.getVersion() + 1);
        
        Product updatedProduct = productRepository.save(product);
        
        // Audit log
        auditLogService.logOperation(id, "UPDATE", oldProduct, updatedProduct);
        
        log.info("Product updated with ID: {}", id);
        return updatedProduct;
    }
    
    @Transactional(readOnly = true)
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                    "Product not found with ID: " + id));
    }
    
    @Transactional(readOnly = true)
    @Override
    public Page<Product> getAllProducts(int page, int size, String sortBy) {
        Sort sort = Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    @Override
    public Page<Product> getProductsByCategory(Long categoryId, int page, int size) {
        // Verify category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(
                "Category not found with ID: " + categoryId);
        }
        
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategoryId(categoryId, pageable);
    }
    
    @Transactional
    @Override
    public void deleteProduct(Long id) {
        // Check if product exists
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                    "Product not found with ID: " + id));
        
        // Soft delete or check dependencies
        if (hasActiveOrders(id)) {
            log.warn("Product {} has active orders", id);
            // Option: throw exception or soft delete
        }
        
        // Delete product
        productRepository.deleteById(id);
        
        // Audit log
        auditLogService.logOperation(id, "DELETE", product, null);
        
        log.info("Product deleted with ID: {}", id);
    }
    
    private boolean hasActiveOrders(Long productId) {
        // Check if product is referenced in active orders
        return false; // Placeholder
    }
    
    private String getCurrentUser() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
    
    private Product cloneProduct(Product product) {
        // Create a copy of product for comparison
        return new Product(
            product.getId(),
            product.getProductCode(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStockQuantity()
        );
    }
}
```

**Key Responsibilities:**
- Business logic implementation
- Data validation and processing
- Transaction management
- Call Repository layer
- Handle exceptions
- Audit logging

---

### 5.3 Repository Layer (Data Access)

**Responsibility:** Database operations

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByProductCode(String productCode);
    
    boolean existsByProductCode(String productCode);
    
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    List<Product> findByStockQuantityLessThan(int quantity);
    
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId " +
           "AND p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByCategoryAndPriceRange(
        @Param("categoryId") Long categoryId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice);
}

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findByCategoryName(String categoryName);
    
    boolean existsByCategoryName(String categoryName);
}

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByProductIdOrderByChangedAtDesc(Long productId);
    
    Page<AuditLog> findByOperationOrderByChangedAtDesc(
        String operation, Pageable pageable);
}
```

**Key Responsibilities:**
- CRUD operations
- Database queries
- Entity mapping (JPA/ORM)
- Transaction coordination
- Query optimization

---

### 5.4 Exception Handling Layer

**Responsibility:** Centralized error management

```java
// Custom Exceptions
public abstract class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }
    
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class ProductNotFoundException extends ApplicationException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}

public class DuplicateProductException extends ApplicationException {
    public DuplicateProductException(String message) {
        super(message);
    }
}

public class InvalidProductException extends ApplicationException {
    public InvalidProductException(String message) {
        super(message);
    }
}

public class ConcurrentUpdateException extends ApplicationException {
    public ConcurrentUpdateException(String message) {
        super(message);
    }
}

public class CategoryNotFoundException extends ApplicationException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}

// Global Exception Handler
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(
            ProductNotFoundException ex, HttpServletRequest request) {
        log.warn("Product not found: {}", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error("Not Found")
                    .message(ex.getMessage())
                    .path(request.getRequestURI())
                    .build());
    }
    
    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProductException(
            DuplicateProductException ex, HttpServletRequest request) {
        log.warn("Duplicate product: {}", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.CONFLICT.value())
                    .error("Conflict")
                    .message(ex.getMessage())
                    .path(request.getRequestURI())
                    .build());
    }
    
    @ExceptionHandler(ConcurrentUpdateException.class)
    public ResponseEntity<ErrorResponse> handleConcurrentUpdateException(
            ConcurrentUpdateException ex, HttpServletRequest request) {
        log.warn("Concurrent update conflict: {}", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.CONFLICT.value())
                    .error("Conflict")
                    .message(ex.getMessage())
                    .path(request.getRequestURI())
                    .build());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation error: {}", ex.getMessage());
        
        List<FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> FieldError.builder()
                    .field(error.getField())
                    .message(error.getDefaultMessage())
                    .rejectedValue(error.getRejectedValue())
                    .build())
                .collect(Collectors.toList());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ValidationErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error("Bad Request")
                    .message("Validation failed")
                    .fieldErrors(fieldErrors)
                    .path(request.getRequestURI())
                    .build());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred", ex);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error("Internal Server Error")
                    .message("An unexpected error occurred")
                    .path(request.getRequestURI())
                    .build());
    }
}
```

---

### 5.5 Validation Layer

**Responsibility:** Input validation

```java
@Component
@Slf4j
public class ProductValidator {
    
    private static final int MAX_NAME_LENGTH = 255;
    private static final int MAX_DESCRIPTION_LENGTH = 1000;
    private static final BigDecimal MIN_PRICE = BigDecimal.ZERO;
    private static final BigDecimal MAX_PRICE = new BigDecimal("999999.99");
    private static final int MIN_STOCK = 0;
    private static final int MAX_STOCK = Integer.MAX_VALUE;
    
    public void validateProductRequest(ProductRequestDTO request) {
        List<String> errors = new ArrayList<>();
        
        // Validate product code
        if (request.getProductCode() == null || request.getProductCode().trim().isEmpty()) {
            errors.add("Product code is required");
        }
        
        // Validate name
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            errors.add("Product name is required");
        } else if (request.getName().length() > MAX_NAME_LENGTH) {
            errors.add("Product name must not exceed " + MAX_NAME_LENGTH + " characters");
        }
        
        // Validate description
        if (request.getDescription() != null && 
            request.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            errors.add("Description must not exceed " + MAX_DESCRIPTION_LENGTH + " characters");
        }
        
        // Validate price
        if (request.getPrice() == null) {
            errors.add("Price is required");
        } else if (request.getPrice().compareTo(MIN_PRICE) <= 0) {
            errors.add("Price must be greater than 0");
        } else if (request.getPrice().compareTo(MAX_PRICE) > 0) {
            errors.add("Price exceeds maximum allowed value");
        }
        
        // Validate stock quantity
        if (request.getStockQuantity() < MIN_STOCK) {
            errors.add("Stock quantity cannot be negative");
        } else if (request.getStockQuantity() > MAX_STOCK) {
            errors.add("Stock quantity exceeds maximum allowed value");
        }
        
        // Validate category
        if (request.getCategoryId() == null || request.getCategoryId() <= 0) {
            errors.add("Category ID must be valid");
        }
        
        if (!errors.isEmpty()) {
            log.warn("Validation errors: {}", errors);
            throw new InvalidProductException(String.join(", ", errors));
        }
    }
    
    public void validatePriceUpdate(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidProductException("Price must be greater than 0");
        }
    }
    
    public void validateStockUpdate(int quantity) {
        if (quantity < 0) {
            throw new InvalidProductException("Stock quantity cannot be negative");
        }
    }
}
```

---

### 5.6 DTO Layer

**Responsibility:** Data transfer between layers

```java
// Request DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    
    @NotBlank(message = "Product code is required")
    private String productCode;
    
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    private Long categoryId;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Price exceeds maximum allowed value")
    private BigDecimal price;
    
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Max(value = 2147483647, message = "Stock quantity exceeds maximum")
    private Integer stockQuantity;
    
    private Integer version; // For optimistic locking
}

// Response DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    
    private Long id;
    private String productCode;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private Integer stockQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Integer version;
}

// Mapper
@Component
public class ProductMapper {
    
    public ProductResponseDTO toResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getCategoryName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .createdBy(product.getCreatedBy())
                .updatedBy(product.getUpdatedBy())
                .version(product.getVersion())
                .build();
    }
    
    public Product toEntity(ProductRequestDTO dto) {
        return Product.builder()
                .productCode(dto.getProductCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .build();
    }
}
```

---

### 5.7 Entity Layer

**Responsibility:** Database entities (domain objects)

```java
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_code", columnList = "product_code", unique = true),
    @Index(name = "idx_category_id", columnList = "category_id"),
    @Index(name = "idx_name", columnList = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_code", nullable = false, unique = true, length = 50)
    private String productCode;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "stock_quantity", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer stockQuantity;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
    
    @Column(name = "version", nullable = false, columnDefinition = "INT DEFAULT 0")
    @Version
    private Integer version;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        stockQuantity = stockQuantity != null ? stockQuantity : 0;
        version = 0;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

@Entity
@Table(name = "categories", indexes = {
    @Index(name = "idx_category_name", columnList = "category_name", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "category_name", nullable = false, unique = true, length = 100)
    private String categoryName;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    private List<Product> products = new ArrayList<>();
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

---

### 5.8 Architecture Layers Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│  HTTP Request (from Client)                                     │
└──────────────────────┬──────────────────────────────────────────┘
                       │
        ┌──────────────▼──────────────┐
        │  CONTROLLER LAYER           │
        │  ProductController.java     │
        │  ✓ Receives request         │
        │  ✓ @Valid annotations       │
        │  ✓ Maps to Service          │
        │  ✓ Returns Response         │
        └──────────────┬──────────────┘
                       │
        ┌──────────────▼──────────────────────────────┐
        │  SERVICE LAYER                              │
        │  ProductService/ProductServiceImpl.java      │
        │  ✓ Business logic                           │
        │  ✓ Validation (ProductValidator)            │
        │  ✓ Transaction management                   │
        │  ✓ Exception handling                       │
        │  ✓ Audit logging (AuditLogService)          │
        │  ✓ Calls Repository                         │
        └──────────────┬──────────────────────────────┘
                       │
        ┌──────────────▼──────────────────────────────┐
        │  REPOSITORY LAYER                           │
        │  ProductRepository.java                     │
        │  ✓ Database queries                         │
        │  ✓ JPA/ORM operations                       │
        │  ✓ Entity mapping                           │
        │  ✓ Transaction coordination                 │
        └──────────────┬──────────────────────────────┘
                       │
        ┌──────────────▼──────────────────────────────┐
        │  DATABASE LAYER                             │
        │  MySQL / PostgreSQL                         │
        │  ✓ Execute SQL                              │
        │  ✓ Persist data                             │
        │  ✓ Return results                           │
        └──────────────┬──────────────────────────────┘
                       │
        ┌──────────────▼──────────────────────────────┐
        │  Response Flow (Reverse)                    │
        │  ✓ Entity → DTO (Mapper)                    │
        │  ✓ Service → Controller                     │
        │  ✓ Controller → HTTP Response               │
        └──────────────┬──────────────────────────────┘
                       │
        ┌──────────────▼──────────────────────────────┐
        │  HTTP Response (to Client)                  │
        │  ✓ Status Code                              │
        │  ✓ JSON Body                                │
        │  ✓ Headers                                  │
        └──────────────────────────────────────────────┘

Cross-Cutting Layers:
├── Exception Handler (GlobalExceptionHandler)
├── DTO/Mapper (ProductRequestDTO, ProductResponseDTO)
├── Validator (ProductValidator, PriceValidator, StockValidator)
├── Entity (Product, Category, AuditLog)
├── Configuration (@Configuration classes)
└── Security (@EnableWebSecurity, JwtTokenProvider)
```

---

## 6. DEPENDENCY INJECTION FLOW

```
Application Starts
    ↓
Spring Container Scans @Component, @Service, @Repository
    ↓
Creates Beans:
    ├── ProductRepository (from Spring Data JPA)
    ├── ProductService (depends on ProductRepository)
    ├── ProductValidator (standalone)
    ├── AuditLogService (depends on AuditLogRepository)
    └── ProductController (depends on ProductService)
    
ProductController
    ↓ (constructor injection)
    ├── ProductService
    │   ├── ProductRepository
    │   ├── CategoryRepository
    │   ├── ProductValidator
    │   └── AuditLogService
    │
    └── ProductMapper
```

---

## 7. KEY ARCHITECTURAL PATTERNS

### 7.1 Design Patterns Used

| Pattern | Location | Purpose |
|---------|----------|---------|
| **MVC** | Controller/Service/Repository | Separation of concerns |
| **Repository** | Repository layer | Data access abstraction |
| **DTO** | DTO layer | Transfer data between layers |
| **Service Locator** | Service layer | Locate and use dependencies |
| **Singleton** | Spring Beans | Single instance per context |
| **Factory** | Mapper | Create DTO instances |
| **Strategy** | Validator | Different validation strategies |
| **Observer** | @Transactional | Transaction management |
| **Decorator** | Exception handlers | Decorate responses with error info |

### 7.2 Transaction Management

```java
@Transactional(propagation = Propagation.REQUIRED)
public Product createProduct(ProductRequestDTO request) {
    // All DB operations here are in one transaction
    // Rolls back if exception occurs
}

@Transactional(readOnly = true)
public Product getProductById(Long id) {
    // Optimized for read-only operations
    // No transaction overhead for writes
}
```

---

## Summary

**Document Version:** 1.0  
**Last Updated:** 2026-08-24  
**Status:** Complete

This design document provides:
- ✅ High-level system architecture
- ✅ Database schema with ER diagram
- ✅ RESTful API specifications
- ✅ Project package structure
- ✅ Layered architecture with code examples
- ✅ Exception handling strategy
- ✅ Validation framework
- ✅ DTO and Entity definitions
- ✅ Design patterns and best practices

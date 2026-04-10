# Backend Blueprint — Decorator Pattern API
> **Version 2.0 — Simplified for 1-Hour Delivery | Group Work Ready**

---

## ⚠️ READ FIRST — Critical Constraints
| Constraint | Decision |
|---|---|
| Delivery time | ~1 hour |
| Database | ❌ None — pure in-memory `HashMap` |
| External services | ❌ None |
| Security | ✅ Simple: API Key header (one hardcoded key) |
| Java version | Java 17 or 21 |
| Framework | Spring Boot 3.3.x |
| Build tool | Maven |
| Extra libs | Lombok + Jackson (already included with Spring Boot) |
| Swagger | ✅ SpringDoc OpenAPI (one dependency) |
| Testing | ❌ Not required for delivery |

---

## Non-Negotiable English Rules (MANDATORY — READ BEFORE CODING)

1. **Every class name, interface name, enum name → English, PascalCase.**
2. **Every method name, variable name, field name → English, camelCase.**
3. **Every package name → English, lowercase, no underscores.**
4. **Every API path, endpoint, query param → English, kebab-case.**
5. **Every error message returned to the client → English.**
6. **Every log message → English.**
7. **Comments in methods → English only, only when logic is non-trivial.**
8. **No `@Autowired` on fields — use constructor injection only.**
9. **No business logic inside controllers.**
10. **No business logic inside data classes (records/DTOs).**
11. **Money values → `BigDecimal`, never `double` or `float`.**
12. **IDs → `String` (UUID generated with `UUID.randomUUID().toString()`).**
13. **Timestamps → `LocalDateTime.now()` stored as `String` ISO-8601 via Jackson.**

---

## 1. Tech Stack — Exact Dependencies for `pom.xml`

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.4</version>
</parent>

<dependencies>
    <!-- Core web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Reduce boilerplate -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Swagger UI auto-generated -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.6.0</version>
    </dependency>
</dependencies>
```

**No `spring-boot-starter-data-jpa`. No `postgresql`. No `flyway`. No `spring-security`.**

---

## 2. Project Structure — Complete Folder and File Map

```
decorator-api/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/com/example/decoratorapi/
        │   │
        │   ├── DecoratorApiApplication.java           ← main entry point
        │   │
        │   ├── config/
        │   │   ├── ApiKeyFilter.java                  ← security: validates X-API-KEY header
        │   │   ├── FilterConfig.java                  ← registers ApiKeyFilter as a bean
        │   │   └── OpenApiConfig.java                 ← Swagger title, version, description
        │   │
        │   ├── shared/
        │   │   ├── ApiResponse.java                   ← generic response wrapper: {success, data, message}
        │   │   ├── ErrorResponse.java                 ← error body: {status, error, message, path, timestamp}
        │   │   ├── GlobalExceptionHandler.java        ← maps exceptions → HTTP responses
        │   │   └── exception/
        │   │       ├── ResourceNotFoundException.java  ← thrown when entity not found → 404
        │   │       ├── InvalidDecoratorException.java  ← thrown when decorator rule is violated → 400
        │   │       └── DuplicateResourceException.java ← thrown when duplicate key → 409
        │   │
        │   ├── store/
        │   │   ├── InMemoryComponentStore.java        ← HashMap<String, ComponentRecord> — THE "database"
        │   │   ├── InMemoryDecoratorStore.java        ← HashMap<String, DecoratorDefinition> — catalog
        │   │   └── DataInitializer.java               ← @PostConstruct: seeds decorator catalog on startup
        │   │
        │   ├── component/
        │   │   ├── controller/
        │   │   │   └── ComponentController.java       ← REST endpoints for components
        │   │   ├── service/
        │   │   │   └── ComponentService.java          ← orchestrates component operations
        │   │   ├── model/
        │   │   │   ├── Component.java                 ← interface: defines component contract
        │   │   │   ├── BaseProductComponent.java      ← implements Component — the "raw" product
        │   │   │   ├── AbstractDecorator.java         ← abstract class wrapping a Component
        │   │   │   ├── InsuranceDecorator.java        ← concrete decorator
        │   │   │   ├── GiftWrapDecorator.java         ← concrete decorator
        │   │   │   ├── PrioritySupportDecorator.java  ← concrete decorator
        │   │   │   └── ExpressDeliveryDecorator.java  ← concrete decorator
        │   │   ├── factory/
        │   │   │   └── DecoratorFactory.java          ← creates decorators by type name
        │   │   ├── record/
        │   │   │   └── ComponentRecord.java           ← stored in HashMap: id, name, price, applied decorators
        │   │   ├── dto/
        │   │   │   ├── request/
        │   │   │   │   ├── CreateComponentRequest.java
        │   │   │   │   └── ApplyDecoratorRequest.java
        │   │   │   └── response/
        │   │   │       ├── ComponentResponse.java
        │   │   │       └── PriceBreakdownResponse.java
        │   │   └── mapper/
        │   │       └── ComponentMapper.java           ← converts between domain model ↔ DTO
        │   │
        │   └── decorator/
        │       ├── controller/
        │       │   └── DecoratorCatalogController.java ← GET /api/v1/decorators — list available decorators
        │       ├── model/
        │       │   ├── DecoratorDefinition.java       ← stores name, description, extra cost (catalog entry)
        │       │   └── DecoratorType.java             ← enum: INSURANCE, GIFT_WRAP, PRIORITY_SUPPORT, EXPRESS_DELIVERY
        │       └── service/
        │           └── DecoratorCatalogService.java   ← reads from InMemoryDecoratorStore
        │
        └── resources/
            └── application.yml                        ← server port, API key, app name, Swagger config
```

---

## 3. `application.yml` — Complete Configuration File

```yaml
server:
  port: 8080

spring:
  application:
    name: decorator-api
  jackson:
    date-format: yyyy-MM-dd'T'HH:mm:ss
    time-zone: UTC
    default-property-inclusion: non_null

app:
  api-key: "decorator-secret-2026"    # change this for production

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
```

---

## 4. Entry Point

### `DecoratorApiApplication.java`
**Package:** `com.example.decoratorapi`

```java
@SpringBootApplication
public class DecoratorApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(DecoratorApiApplication.class, args);
    }
}
```

---

## 5. `config/` — Configuration Classes

### 5.1 `ApiKeyFilter.java`
**Package:** `com.example.decoratorapi.config`
**Responsibility:** Intercepts every request and checks that the header `X-API-KEY` matches the configured key. Rejects with `403` if it does not match.

**Fields:**
```java
private final String apiKey;  // injected from application.yml: ${app.api-key}
```

**Methods:**
| Method | Signature | Description |
|---|---|---|
| `doFilter` | `void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)` | Extract `X-API-KEY` from request headers. If missing or wrong → write `403` JSON error and return. If correct → call `chain.doFilter()`. |

**Implementation detail (comment for AI):**
```java
// Cast to HttpServletRequest to read headers
// Use response.setContentType("application/json") before writing error
// Write: {"status":403,"error":"Forbidden","message":"Invalid or missing API key"}
```

---

### 5.2 `FilterConfig.java`
**Package:** `com.example.decoratorapi.config`
**Responsibility:** Register `ApiKeyFilter` as a Spring `FilterRegistrationBean`.

**Methods:**
| Method | Signature | Description |
|---|---|---|
| `apiKeyFilter` | `@Bean FilterRegistrationBean<ApiKeyFilter> apiKeyFilter()` | Create bean, set filter, add URL pattern `/*`, set order 1. |

---

### 5.3 `OpenApiConfig.java`
**Package:** `com.example.decoratorapi.config`
**Responsibility:** Configure Swagger title, version, and description.

**Methods:**
| Method | Signature | Description |
|---|---|---|
| `openAPI` | `@Bean OpenAPI openAPI()` | Return `new OpenAPI()` with `Info` (title: `Decorator Pattern API`, version: `1.0.0`). Add a security scheme for `X-API-KEY` header. |

---

## 6. `shared/` — Global Utilities

### 6.1 `ApiResponse.java`
**Package:** `com.example.decoratorapi.shared`
**Responsibility:** Standard wrapper for every successful response.

```java
@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) { ... }
    public static <T> ApiResponse<T> ok(String message, T data) { ... }
}
```

**Rules:**
- All controllers return `ResponseEntity<ApiResponse<T>>`.
- `success` is always `true` for 2xx responses.

---

### 6.2 `ErrorResponse.java`
**Package:** `com.example.decoratorapi.shared`
**Responsibility:** Standard body for every error response.

```java
@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
    private String timestamp;  // ISO-8601 string
}
```

---

### 6.3 `GlobalExceptionHandler.java`
**Package:** `com.example.decoratorapi.shared`
**Responsibility:** Catch all exceptions and convert them to clean JSON error responses. Controllers must never have `try/catch`.

**Annotation:** `@RestControllerAdvice`

**Methods:**
| Method | Exception handled | HTTP Status | Description |
|---|---|---|---|
| `handleNotFound` | `ResourceNotFoundException` | 404 | Return error with exception message |
| `handleInvalidDecorator` | `InvalidDecoratorException` | 400 | Return error with exception message |
| `handleDuplicate` | `DuplicateResourceException` | 409 | Return error with exception message |
| `handleValidation` | `MethodArgumentNotValidException` | 400 | Extract first field error message |
| `handleGeneral` | `Exception` | 500 | Return generic internal error message |

**Each method must:**
1. Extract the `HttpServletRequest` to read the path.
2. Build an `ErrorResponse` with current timestamp.
3. Return `ResponseEntity<ErrorResponse>`.

---

### 6.4 `exception/ResourceNotFoundException.java`
```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
```

### 6.5 `exception/InvalidDecoratorException.java`
```java
public class InvalidDecoratorException extends RuntimeException {
    public InvalidDecoratorException(String message) { super(message); }
}
```

### 6.6 `exception/DuplicateResourceException.java`
```java
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) { super(message); }
}
```

---

## 7. `store/` — In-Memory Storage (Replaces the Database)

> **IMPORTANT:** No Spring Data JPA. No SQL. No `@Entity`. The "database" is a `HashMap` held in a Spring `@Component` bean. Since Spring beans are singletons by default, data persists for the entire runtime of the application.

---

### 7.1 `InMemoryComponentStore.java`
**Package:** `com.example.decoratorapi.store`
**Annotation:** `@Component`
**Responsibility:** Store and retrieve `ComponentRecord` objects using their `id` as key.

**Fields:**
```java
private final Map<String, ComponentRecord> storage = new ConcurrentHashMap<>();
```

> Use `ConcurrentHashMap` — it is thread-safe for concurrent HTTP requests.

**Methods:**
| Method | Signature | Description |
|---|---|---|
| `save` | `ComponentRecord save(ComponentRecord record)` | Put record into map by `record.getId()`. Return the saved record. |
| `findById` | `Optional<ComponentRecord> findById(String id)` | Return `Optional.ofNullable(storage.get(id))`. |
| `findAll` | `List<ComponentRecord> findAll()` | Return `new ArrayList<>(storage.values())`. |
| `deleteById` | `void deleteById(String id)` | Remove from map. Throw `ResourceNotFoundException` if not present. |
| `existsById` | `boolean existsById(String id)` | Return `storage.containsKey(id)`. |
| `count` | `int count()` | Return `storage.size()`. |

---

### 7.2 `InMemoryDecoratorStore.java`
**Package:** `com.example.decoratorapi.store`
**Annotation:** `@Component`
**Responsibility:** Store the decorator catalog (the available decorator types that can be applied).

**Fields:**
```java
private final Map<String, DecoratorDefinition> catalog = new LinkedHashMap<>();
```

> Use `LinkedHashMap` to preserve insertion order for consistent listing.

**Methods:**
| Method | Signature | Description |
|---|---|---|
| `add` | `void add(DecoratorDefinition definition)` | Put into map using `definition.getType()` as key. |
| `findByType` | `Optional<DecoratorDefinition> findByType(String type)` | Return `Optional.ofNullable(catalog.get(type.toUpperCase()))`. |
| `findAll` | `List<DecoratorDefinition> findAll()` | Return list of all values. |
| `exists` | `boolean exists(String type)` | Return `catalog.containsKey(type.toUpperCase())`. |

---

### 7.3 `DataInitializer.java`
**Package:** `com.example.decoratorapi.store`
**Annotation:** `@Component`
**Responsibility:** Populate the decorator catalog with default entries when the application starts. This runs once on startup.

**Annotation on method:** `@PostConstruct`

**Method:**
| Method | Signature | Description |
|---|---|---|
| `init` | `void init()` | Call `decoratorStore.add(...)` four times, once for each `DecoratorType`. |

**Data to seed (four entries):**
```
Type: INSURANCE
  displayName: "Insurance Coverage"
  description: "Adds insurance protection to your product"
  additionalCost: 15.00

Type: GIFT_WRAP
  displayName: "Gift Wrapping"
  description: "Wraps the product in premium gift packaging"
  additionalCost: 8.50

Type: PRIORITY_SUPPORT
  displayName: "Priority Support"
  description: "Adds 24/7 priority customer support"
  additionalCost: 25.00

Type: EXPRESS_DELIVERY
  displayName: "Express Delivery"
  description: "Delivers the product within 24 hours"
  additionalCost: 12.00
```

---

## 8. `component/model/` — Decorator Pattern Core Domain

> This is the heart of the project. Implement it carefully. Every other class depends on this.

---

### 8.1 `Component.java` — Interface
**Package:** `com.example.decoratorapi.component.model`
**Type:** `interface`
**Responsibility:** Define the contract that every component (base and decorator) must fulfill.

```java
public interface Component {
    String getId();
    String getName();
    String getDescription();
    BigDecimal getBasePrice();
    BigDecimal getFinalPrice();
    List<String> getAppliedDecoratorTypes();  // e.g. ["INSURANCE", "GIFT_WRAP"]
    // No @Override needed in interface — these are the method signatures
}
```

---

### 8.2 `BaseProductComponent.java`
**Package:** `com.example.decoratorapi.component.model`
**Annotation:** `@Getter` (Lombok)
**Responsibility:** The concrete base component. Represents a product before any decorator is applied.

**Fields:**
```java
private final String id;          // UUID string
private final String name;
private final String description;
private final BigDecimal basePrice;
```

**Constructor:**
```java
public BaseProductComponent(String id, String name, String description, BigDecimal basePrice)
```

**Implemented methods:**
| Method | Return |
|---|---|
| `getId()` | `this.id` |
| `getName()` | `this.name` |
| `getDescription()` | `"Base: " + this.description` |
| `getBasePrice()` | `this.basePrice` |
| `getFinalPrice()` | `this.basePrice` |
| `getAppliedDecoratorTypes()` | `new ArrayList<>()` (empty — no decorators yet) |

---

### 8.3 `AbstractDecorator.java`
**Package:** `com.example.decoratorapi.component.model`
**Type:** `abstract class implements Component`
**Responsibility:** Wrap an existing `Component` and delegate default behavior to it. Subclasses only override the extra cost and extra description.

**Fields:**
```java
protected final Component wrappedComponent;   // the component being decorated
protected final String decoratorType;         // matches DecoratorType enum name
```

**Constructor:**
```java
protected AbstractDecorator(Component wrappedComponent, String decoratorType)
```

**Implemented methods (final — subclasses MUST NOT override these):**
| Method | Return |
|---|---|
| `getId()` | `wrappedComponent.getId()` |
| `getName()` | `wrappedComponent.getName()` |
| `getBasePrice()` | `wrappedComponent.getBasePrice()` |
| `getFinalPrice()` | `wrappedComponent.getFinalPrice().add(getAdditionalCost())` |
| `getAppliedDecoratorTypes()` | Build new list: copy inner list + add `this.decoratorType` |

**Abstract methods (subclasses MUST implement these):**
| Method | Signature | Description |
|---|---|---|
| `getAdditionalCost` | `BigDecimal getAdditionalCost()` | Returns the exact cost this decorator adds |
| `getAdditionalDescription` | `String getAdditionalDescription()` | Returns the text fragment this decorator adds |

**Implemented `getDescription()`:**
```java
public String getDescription() {
    return wrappedComponent.getDescription() + " + " + getAdditionalDescription();
}
```

---

### 8.4 `InsuranceDecorator.java`
**Package:** `com.example.decoratorapi.component.model`
**Extends:** `AbstractDecorator`

**Constructor:**
```java
public InsuranceDecorator(Component component) {
    super(component, DecoratorType.INSURANCE.name());
}
```

**Implemented methods:**
| Method | Return value |
|---|---|
| `getAdditionalCost()` | `new BigDecimal("15.00")` |
| `getAdditionalDescription()` | `"Insurance Coverage"` |

---

### 8.5 `GiftWrapDecorator.java`
**Package:** `com.example.decoratorapi.component.model`
**Extends:** `AbstractDecorator`

**Constructor:**
```java
public GiftWrapDecorator(Component component) {
    super(component, DecoratorType.GIFT_WRAP.name());
}
```

**Implemented methods:**
| Method | Return value |
|---|---|
| `getAdditionalCost()` | `new BigDecimal("8.50")` |
| `getAdditionalDescription()` | `"Gift Wrapping"` |

---

### 8.6 `PrioritySupportDecorator.java`
**Package:** `com.example.decoratorapi.component.model`
**Extends:** `AbstractDecorator`

**Constructor:**
```java
public PrioritySupportDecorator(Component component) {
    super(component, DecoratorType.PRIORITY_SUPPORT.name());
}
```

**Implemented methods:**
| Method | Return value |
|---|---|
| `getAdditionalCost()` | `new BigDecimal("25.00")` |
| `getAdditionalDescription()` | `"Priority Support"` |

---

### 8.7 `ExpressDeliveryDecorator.java`
**Package:** `com.example.decoratorapi.component.model`
**Extends:** `AbstractDecorator`

**Constructor:**
```java
public ExpressDeliveryDecorator(Component component) {
    super(component, DecoratorType.EXPRESS_DELIVERY.name());
}
```

**Implemented methods:**
| Method | Return value |
|---|---|
| `getAdditionalCost()` | `new BigDecimal("12.00")` |
| `getAdditionalDescription()` | `"Express Delivery"` |

---

## 9. `component/factory/DecoratorFactory.java`

**Package:** `com.example.decoratorapi.component.factory`
**Annotation:** `@Component`
**Responsibility:** Instantiate the correct decorator class based on a `String` type name. The service never knows which decorator class to use — it only calls the factory.

**Methods:**
| Method | Signature | Description |
|---|---|---|
| `apply` | `com.example.decoratorapi.component.model.Component apply(com.example.decoratorapi.component.model.Component base, String type)` | Match `type.toUpperCase()` against `DecoratorType` values. Return the matching decorator wrapping `base`. Throw `InvalidDecoratorException("Unknown decorator type: " + type)` if no match. |
| `applyAll` | `com.example.decoratorapi.component.model.Component applyAll(com.example.decoratorapi.component.model.Component base, List<String> types)` | Iterate `types` list. For each, call `apply(current, type)` and reassign `current`. Return final wrapped component. |

**Switch inside `apply`:**
```java
return switch (type.toUpperCase()) {
    case "INSURANCE"         -> new InsuranceDecorator(base);
    case "GIFT_WRAP"         -> new GiftWrapDecorator(base);
    case "PRIORITY_SUPPORT"  -> new PrioritySupportDecorator(base);
    case "EXPRESS_DELIVERY"  -> new ExpressDeliveryDecorator(base);
    default -> throw new InvalidDecoratorException("Unknown decorator type: " + type);
};
```

---

## 10. `component/record/ComponentRecord.java`

**Package:** `com.example.decoratorapi.component.record`
**Annotation:** `@Data @AllArgsConstructor @Builder`
**Responsibility:** The persistent object stored in `InMemoryComponentStore`. This is what goes into the `HashMap`. It stores the current state of a component after all decorators are applied.

> **This is NOT the domain `Component` interface.** This is the storage data class.

**Fields:**
```java
private String id;
private String name;
private String description;
private BigDecimal basePrice;
private BigDecimal finalPrice;
private List<String> appliedDecoratorTypes;  // ordered list of applied decorators
private String createdAt;   // ISO-8601 string: "2026-04-10T17:00:00"
private String updatedAt;   // ISO-8601 string
```

**Static factory method:**
```java
public static ComponentRecord fromDomain(com.example.decoratorapi.component.model.Component component) {
    // Build a ComponentRecord from the domain Component interface
    String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    return ComponentRecord.builder()
        .id(component.getId())
        .name(component.getName())
        .description(component.getDescription())
        .basePrice(component.getBasePrice())
        .finalPrice(component.getFinalPrice())
        .appliedDecoratorTypes(new ArrayList<>(component.getAppliedDecoratorTypes()))
        .createdAt(now)
        .updatedAt(now)
        .build();
}
```

---

## 11. `component/dto/` — Data Transfer Objects

### 11.1 `CreateComponentRequest.java`
**Package:** `com.example.decoratorapi.component.dto.request`
**Annotation:** `@Data`
**Fields with validation:**
```java
@NotBlank(message = "name is required")
@Size(max = 100, message = "name must be at most 100 characters")
private String name;

@NotBlank(message = "description is required")
private String description;

@NotNull(message = "basePrice is required")
@DecimalMin(value = "0.01", message = "basePrice must be greater than 0")
private BigDecimal basePrice;
```

---

### 11.2 `ApplyDecoratorRequest.java`
**Package:** `com.example.decoratorapi.component.dto.request`
**Annotation:** `@Data`
**Fields with validation:**
```java
@NotBlank(message = "decoratorType is required")
private String decoratorType;   // must be one of: INSURANCE, GIFT_WRAP, PRIORITY_SUPPORT, EXPRESS_DELIVERY
```

---

### 11.3 `ComponentResponse.java`
**Package:** `com.example.decoratorapi.component.dto.response`
**Annotation:** `@Data @AllArgsConstructor @Builder`
**Fields:**
```java
private String id;
private String name;
private String description;
private BigDecimal basePrice;
private BigDecimal finalPrice;
private List<String> appliedDecoratorTypes;
private PriceBreakdownResponse priceBreakdown;
private String createdAt;
private String updatedAt;
```

---

### 11.4 `PriceBreakdownResponse.java`
**Package:** `com.example.decoratorapi.component.dto.response`
**Annotation:** `@Data @AllArgsConstructor @Builder`
**Fields:**
```java
private BigDecimal basePrice;
private List<DecoratorCostItem> decoratorCosts;   // each item: type + cost
private BigDecimal totalAdded;
private BigDecimal finalPrice;

@Data @AllArgsConstructor
public static class DecoratorCostItem {
    private String decoratorType;
    private BigDecimal cost;
}
```

---

## 12. `component/mapper/ComponentMapper.java`

**Package:** `com.example.decoratorapi.component.mapper`
**Annotation:** `@Component`
**Responsibility:** Convert between `ComponentRecord` and `ComponentResponse`. Controllers use this, not manual mapping.

**Methods:**
| Method | Signature | Description |
|---|---|---|
| `toResponse` | `ComponentResponse toResponse(ComponentRecord record)` | Map all fields from record to response. Build `PriceBreakdownResponse` by iterating `appliedDecoratorTypes` and looking up cost from `InMemoryDecoratorStore`. |
| `toResponseList` | `List<ComponentResponse> toResponseList(List<ComponentRecord> records)` | `records.stream().map(this::toResponse).toList()` |

---

## 13. `component/service/ComponentService.java`

**Package:** `com.example.decoratorapi.component.service`
**Annotation:** `@Service`
**Responsibility:** Contains all business logic. Controllers call this service. The service uses the store and factory. This is the only class with business rules.

**Constructor injection:**
```java
private final InMemoryComponentStore componentStore;
private final InMemoryDecoratorStore decoratorStore;
private final DecoratorFactory decoratorFactory;
private final ComponentMapper componentMapper;
```

**Methods (implement all of these):**
| Method | Signature | Description |
|---|---|---|
| `createComponent` | `ComponentResponse createComponent(CreateComponentRequest request)` | 1. Create `BaseProductComponent` with `UUID.randomUUID().toString()`. 2. Convert to `ComponentRecord` via `fromDomain`. 3. Save to store. 4. Return mapped response. |
| `getComponent` | `ComponentResponse getComponent(String id)` | Find by id from store. Throw `ResourceNotFoundException` if not found. Return mapped response. |
| `getAllComponents` | `List<ComponentResponse> getAllComponents()` | Find all from store. Return mapped response list. |
| `applyDecorator` | `ComponentResponse applyDecorator(String componentId, ApplyDecoratorRequest request)` | 1. Retrieve existing `ComponentRecord` by id. 2. Validate decorator type exists in catalog. 3. Rebuild domain Component chain from the record (base + stored decorators). 4. Apply new decorator using factory. 5. Save updated record with new decorator added to list and updated `finalPrice` and `description`. 6. Return mapped response. |
| `removeDecorator` | `ComponentResponse removeDecorator(String componentId, String decoratorType)` | 1. Retrieve existing `ComponentRecord`. 2. Check decorator is in `appliedDecoratorTypes`. 3. Remove it from the list. 4. Rebuild the chain from scratch using remaining decorators. 5. Recalculate price and description. 6. Update the record in store. 7. Return mapped response. |
| `deleteComponent` | `void deleteComponent(String id)` | Delegate to `componentStore.deleteById(id)`. |
| `simulateComposition` | `ComponentResponse simulateComposition(CreateComponentRequest request, List<String> decoratorTypes)` | Build and decorate in memory WITHOUT saving. Return transient response. |

**Private helper (must implement):**
```java
// Rebuilds the full decorator chain from a stored record
// Used by applyDecorator and removeDecorator to reconstruct the domain model
private com.example.decoratorapi.component.model.Component rebuildChain(ComponentRecord record) {
    com.example.decoratorapi.component.model.Component component =
        new BaseProductComponent(record.getId(), record.getName(),
                                 record.getName(), record.getBasePrice());
    return decoratorFactory.applyAll(component, record.getAppliedDecoratorTypes());
}
```

---

## 14. `component/controller/ComponentController.java`

**Package:** `com.example.decoratorapi.component.controller`
**Annotation:** `@RestController @RequestMapping("/api/v1/components")`
**Responsibility:** Expose HTTP endpoints. Validate input. Call service. Return responses. No business logic here.

**Constructor injection:**
```java
private final ComponentService componentService;
```

**Endpoints — complete table:**
| HTTP Method | Path | Method name | Request body | Returns | Status |
|---|---|---|---|---|---|
| `POST` | `/api/v1/components` | `createComponent` | `@Valid @RequestBody CreateComponentRequest` | `ApiResponse<ComponentResponse>` | `201` |
| `GET` | `/api/v1/components` | `getAllComponents` | none | `ApiResponse<List<ComponentResponse>>` | `200` |
| `GET` | `/api/v1/components/{id}` | `getComponent` | `@PathVariable String id` | `ApiResponse<ComponentResponse>` | `200` |
| `POST` | `/api/v1/components/{id}/decorators` | `applyDecorator` | `@PathVariable id`, `@Valid @RequestBody ApplyDecoratorRequest` | `ApiResponse<ComponentResponse>` | `200` |
| `DELETE` | `/api/v1/components/{id}/decorators/{decoratorType}` | `removeDecorator` | `@PathVariable String id`, `@PathVariable String decoratorType` | `ApiResponse<ComponentResponse>` | `200` |
| `DELETE` | `/api/v1/components/{id}` | `deleteComponent` | `@PathVariable String id` | `ApiResponse<Void>` | `200` |
| `POST` | `/api/v1/components/simulate` | `simulateComposition` | `@RequestBody SimulateRequest` (name, description, basePrice, List<String> decoratorTypes) | `ApiResponse<ComponentResponse>` | `200` |

**Swagger annotations to add on each method:**
```java
@Operation(summary = "Create a new base component", description = "Creates an undecorated product component")
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "Component created"),
    @ApiResponse(responseCode = "400", description = "Validation error")
})
```

---

## 15. `decorator/` Module — Decorator Catalog

### 15.1 `DecoratorType.java`
**Package:** `com.example.decoratorapi.decorator.model`
**Type:** `enum`

```java
public enum DecoratorType {
    INSURANCE,
    GIFT_WRAP,
    PRIORITY_SUPPORT,
    EXPRESS_DELIVERY
}
```

---

### 15.2 `DecoratorDefinition.java`
**Package:** `com.example.decoratorapi.decorator.model`
**Annotation:** `@Data @AllArgsConstructor @Builder`
**Responsibility:** Catalog entry for one decorator type.

**Fields:**
```java
private String type;            // e.g. "INSURANCE"
private String displayName;     // e.g. "Insurance Coverage"
private String description;     // human-readable explanation
private BigDecimal additionalCost;
private boolean canBeAppliedMultipleTimes;  // false for all by default
```

---

### 15.3 `DecoratorCatalogService.java`
**Package:** `com.example.decoratorapi.decorator.service`
**Annotation:** `@Service`

**Methods:**
| Method | Signature | Description |
|---|---|---|
| `getAll` | `List<DecoratorDefinition> getAll()` | Delegate to `decoratorStore.findAll()` |
| `getByType` | `DecoratorDefinition getByType(String type)` | Delegate to `decoratorStore.findByType(type)`. Throw `ResourceNotFoundException` if absent. |

---

### 15.4 `DecoratorCatalogController.java`
**Package:** `com.example.decoratorapi.decorator.controller`
**Annotation:** `@RestController @RequestMapping("/api/v1/decorators")`

**Endpoints:**
| HTTP Method | Path | Method name | Returns | Status |
|---|---|---|---|---|
| `GET` | `/api/v1/decorators` | `listDecorators` | `ApiResponse<List<DecoratorDefinition>>` | `200` |
| `GET` | `/api/v1/decorators/{type}` | `getDecorator` | `ApiResponse<DecoratorDefinition>` | `200` |

---

## 16. Full Endpoint Reference

| # | Method | Endpoint | Auth | Body | Response |
|---|---|---|---|---|---|
| 1 | GET | `/api/v1/decorators` | X-API-KEY | — | List of available decorators |
| 2 | GET | `/api/v1/decorators/{type}` | X-API-KEY | — | Single decorator definition |
| 3 | POST | `/api/v1/components` | X-API-KEY | `{name, description, basePrice}` | Created component |
| 4 | GET | `/api/v1/components` | X-API-KEY | — | All stored components |
| 5 | GET | `/api/v1/components/{id}` | X-API-KEY | — | Single component with breakdown |
| 6 | POST | `/api/v1/components/{id}/decorators` | X-API-KEY | `{decoratorType}` | Updated component |
| 7 | DELETE | `/api/v1/components/{id}/decorators/{type}` | X-API-KEY | — | Component without that decorator |
| 8 | DELETE | `/api/v1/components/{id}` | X-API-KEY | — | Confirmation message |
| 9 | POST | `/api/v1/components/simulate` | X-API-KEY | `{name, description, basePrice, decoratorTypes[]}` | Simulated result (NOT saved) |

**Auth header example:**
```
X-API-KEY: decorator-secret-2026
```

---

## 17. Request/Response Examples

### Create component
**POST /api/v1/components**
```json
{
  "name": "Laptop Pro X",
  "description": "High performance laptop",
  "basePrice": 1200.00
}
```

**Response 201:**
```json
{
  "success": true,
  "message": "Component created",
  "data": {
    "id": "a1b2c3d4-...",
    "name": "Laptop Pro X",
    "description": "Base: High performance laptop",
    "basePrice": 1200.00,
    "finalPrice": 1200.00,
    "appliedDecoratorTypes": [],
    "priceBreakdown": {
      "basePrice": 1200.00,
      "decoratorCosts": [],
      "totalAdded": 0.00,
      "finalPrice": 1200.00
    },
    "createdAt": "2026-04-10T17:00:00",
    "updatedAt": "2026-04-10T17:00:00"
  }
}
```

### Apply decorator
**POST /api/v1/components/a1b2c3d4.../decorators**
```json
{
  "decoratorType": "INSURANCE"
}
```

**Response 200:**
```json
{
  "success": true,
  "message": "Decorator applied",
  "data": {
    "id": "a1b2c3d4-...",
    "finalPrice": 1215.00,
    "appliedDecoratorTypes": ["INSURANCE"],
    "priceBreakdown": {
      "basePrice": 1200.00,
      "decoratorCosts": [
        { "decoratorType": "INSURANCE", "cost": 15.00 }
      ],
      "totalAdded": 15.00,
      "finalPrice": 1215.00
    }
  }
}
```

### Error example
**GET /api/v1/components/nonexistent-id**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Component not found with id: nonexistent-id",
  "path": "/api/v1/components/nonexistent-id",
  "timestamp": "2026-04-10T17:05:00"
}
```

---

## 18. Group Work Division

Split implementation into these independent tasks that can be worked in parallel:

| Team Member | Responsibility | Files to create |
|---|---|---|
| **Member A** | Domain model + Factory | `Component.java`, `BaseProductComponent.java`, `AbstractDecorator.java`, all 4 concrete decorators, `DecoratorFactory.java` |
| **Member B** | Storage + Catalog + Initializer | `InMemoryComponentStore.java`, `InMemoryDecoratorStore.java`, `DataInitializer.java`, `DecoratorType.java`, `DecoratorDefinition.java` |
| **Member C** | Service + Mapper | `ComponentService.java`, `ComponentMapper.java`, `DecoratorCatalogService.java` |
| **Member D** | Controllers + DTOs + Config | All `*Controller.java`, all request/response DTOs, `ApiKeyFilter.java`, `FilterConfig.java`, `OpenApiConfig.java` |
| **Any member** | Shared utilities | `ApiResponse.java`, `ErrorResponse.java`, `GlobalExceptionHandler.java`, all exception classes |

**Integration order:**
1. Member A finishes domain model first (others depend on it).
2. Member B finishes stores (Member C depends on them).
3. Member C finishes services (Member D depends on them).
4. Member D finishes controllers last.
5. Any member wires up the shared utilities at any time.

---

## 19. How to Run

```bash
# Option 1: Maven wrapper
./mvnw spring-boot:run

# Option 2: Maven installed
mvn spring-boot:run

# Option 3: Build jar and run
mvn clean package -DskipTests
java -jar target/decorator-api-0.0.1-SNAPSHOT.jar
```

**Access Swagger UI:**
```
http://localhost:8080/swagger-ui.html
```

**Test with curl:**
```bash
curl -X POST http://localhost:8080/api/v1/components \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: decorator-secret-2026" \
  -d '{"name":"Laptop","description":"Pro laptop","basePrice":999.99}'
```

---

## 20. Implementation Checklist (verify before submitting)

- [ ] All class names in English, PascalCase
- [ ] All method names in English, camelCase
- [ ] No `@Autowired` on fields — only constructor injection
- [ ] No SQL, no JPA, no `@Entity`
- [ ] `InMemoryComponentStore` uses `ConcurrentHashMap`
- [ ] `DataInitializer` seeds 4 decorator definitions at startup
- [ ] All 4 concrete decorators extend `AbstractDecorator`
- [ ] `DecoratorFactory.apply` uses a `switch` on the type string
- [ ] `ComponentService.rebuildChain` reconstructs domain from record
- [ ] All controllers return `ResponseEntity<ApiResponse<T>>`
- [ ] `GlobalExceptionHandler` handles at minimum: 404, 400, 409, 500
- [ ] `ApiKeyFilter` returns 403 when `X-API-KEY` is missing or wrong
- [ ] Swagger UI accessible at `/swagger-ui.html`
- [ ] Application starts with `mvn spring-boot:run` without errors

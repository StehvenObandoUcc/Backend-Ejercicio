# Error Report — Decorator Pattern API
> **Total errors found: 10 | Division: 5 errors per person**

---

## PERSON A — Compilation Errors + Domain Model Fixes

### ERROR A1 — CRITICAL (Won't Compile)
**File:** `component/factory/DecoratorFactory.java`
**Lines:** 3, 5, 9, 12, 22-27

**Problem:** The wildcard import `import com.example.decoratorapi.component.model.*` brings in the project's own `Component` interface. The import `import org.springframework.stereotype.Component` brings Spring's `@Component` annotation. Both are named `Component` — the compiler cannot resolve which one is meant in method signatures.

**Fix:** Remove `import org.springframework.stereotype.Component;` (line 5) and use the fully qualified annotation on line 9:
```java
@org.springframework.stereotype.Component
public class DecoratorFactory {
```

---

### ERROR A2 — CRITICAL (Won't Compile)
**File:** `component/model/BaseProductComponent.java`
**Lines:** 9, 13, 23-26

**Problem:** Lombok `@Getter` on the class generates a `getDescription()` method from the `description` field. But lines 23-26 also define an explicit `@Override getDescription()`. This creates a duplicate method — compilation fails.

**Fix:** Add `@Getter(AccessLevel.NONE)` on the `description` field to suppress Lombok's auto-generated getter:
```java
import lombok.AccessLevel;

@Getter(AccessLevel.NONE)
private final String description;
```

---

### ERROR A3 — HIGH (Logic Bug — Description Corruption)
**File:** `component/model/BaseProductComponent.java`
**Line:** 24-25

**Problem:** `getDescription()` prepends `"Base: "` every time it is called. When a component is saved to the store, its description is stored as `"Base: High performance laptop"`. When later rebuilt from the record (via `rebuildChain`), this stored value is passed to a new `BaseProductComponent`, and `getDescription()` is called again, producing `"Base: Base: High performance laptop"`. Each apply/remove cycle adds another `"Base: "` prefix.

**Fix:** Either:
- Option 1: Store the raw description in `ComponentRecord` and only add the `"Base: "` prefix in the mapper/response layer.
- Option 2: Strip the `"Base: "` prefix in `rebuildChain` before passing it to the constructor.
- Option 3: Keep `"Base: "` only in the response and make `getDescription()` return `this.description` without modification.

---

### ERROR A4 — MEDIUM (Missing Business Logic)
**File:** `component/service/ComponentService.java`
**Lines:** 50-59 (method `applyDecorator`)

**Problem:** The method never checks `DecoratorDefinition.canBeAppliedMultipleTimes`. A user can apply `INSURANCE` 10 times to the same component, each time adding $15.00. The field `canBeAppliedMultipleTimes` is set to `false` for all 4 decorators but is never read anywhere.

**Fix:** Add this check before applying:
```java
String typeUpper = request.getDecoratorType().toUpperCase();
boolean alreadyApplied = record.getAppliedDecoratorTypes().contains(typeUpper);
boolean canRepeat = decoratorStore.findByType(typeUpper)
        .map(DecoratorDefinition::isCanBeAppliedMultipleTimes).orElse(false);

if (alreadyApplied && !canRepeat) {
    throw new InvalidDecoratorException("Decorator " + typeUpper + " is already applied to this component");
}
```

---

### ERROR A5 — LOW (Best Practice — pom.xml)
**File:** `pom.xml`
**Lines:** 53-56

**Problem:** `spring-boot-maven-plugin` does not exclude Lombok from the packaged JAR. Lombok is compile-time only and should not end up in the runtime classpath.

**Fix:** Add this configuration to the plugin:
```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <excludes>
            <exclude>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </exclude>
        </excludes>
    </configuration>
</plugin>
```

---
---

## PERSON B — Service Logic Fixes + Controller/Filter Fixes

### ERROR B1 — HIGH (Logic Bug — Description Lost)
**File:** `component/service/ComponentService.java`
**Line:** 68 (`removeDecorator` method)

**Problem:** When rebuilding the `BaseProductComponent`, the third constructor argument (description) receives `record.getName()` instead of `record.getDescription()`. The component's description is silently replaced with its name.

```java
// WRONG (current code):
new BaseProductComponent(record.getId(), record.getName(), record.getName(), record.getBasePrice())
//                                                          ^^^^^^^^^^^^^^^^ should be getDescription()
```

**Fix:**
```java
new BaseProductComponent(record.getId(), record.getName(), record.getDescription(), record.getBasePrice())
```

---

### ERROR B2 — HIGH (Logic Bug — Same as B1 in different method)
**File:** `component/service/ComponentService.java`
**Line:** 86 (`rebuildChain` method)

**Problem:** Identical bug — `record.getName()` passed as description instead of `record.getDescription()`.

```java
// WRONG (current code):
Component base = new BaseProductComponent(record.getId(), record.getName(), record.getName(), record.getBasePrice());
//                                                                          ^^^^^^^^^^^^^^^^ should be getDescription()
```

**Fix:**
```java
Component base = new BaseProductComponent(record.getId(), record.getName(), record.getDescription(), record.getBasePrice());
```

---

### ERROR B3 — MEDIUM (Missing @Valid — Validation Not Enforced)
**File:** `component/controller/ComponentController.java`
**Line:** 74

**Problem:** The `simulateComposition` endpoint has `@RequestBody SimulateRequest body` but is **missing `@Valid`**. The inner class `SimulateRequest` has `@NotBlank` and `@NotNull` constraints on its fields (lines 63-68), but without `@Valid`, Spring never validates them. Users can send null/blank data.

**Fix:** Change line 74 to:
```java
public ResponseEntity<...> simulateComposition(@Valid @RequestBody SimulateRequest body) {
```

---

### ERROR B4 — MEDIUM (Swagger UI Blocked by API Key Filter)
**Files:** `config/ApiKeyFilter.java` (lines 28-34) + `config/FilterConfig.java` (line 14)

**Problem:** The API key filter applies to ALL URL patterns (`/*`), including Swagger UI paths (`/swagger-ui/**`, `/swagger-ui.html`) and OpenAPI docs (`/v3/api-docs/**`, `/api-docs/**`). A browser cannot add `X-API-KEY` headers on normal navigation, so the Swagger UI is completely inaccessible.

**Fix:** Add path exclusions at the beginning of `doFilter()` in `ApiKeyFilter.java`:
```java
String path = httpRequest.getRequestURI();
if (path.startsWith("/swagger-ui") || path.startsWith("/api-docs") || path.startsWith("/v3/api-docs")) {
    chain.doFilter(request, response);
    return;
}
```

---

### ERROR B5 — LOW (Latent Bug — removeDecorator removes only first occurrence)
**File:** `component/service/ComponentService.java`
**Line:** 67

**Problem:** `remaining.remove(typeUpper)` uses `List.remove(Object)`, which only removes the **first** occurrence. If duplicate decorators ever exist in the list, only one instance would be removed. Combined with Person A's fix for Error A4 (preventing duplicates), this becomes less likely, but is still incorrect defensively.

**Fix:** Either:
```java
// Remove all occurrences (safest):
remaining.removeAll(Collections.singleton(typeUpper));
```
Or ensure Error A4 is fixed first so duplicates never enter the list.

---
---

## Summary Table

| # | Assigned To | Severity | File | Short Description |
|---|-------------|----------|------|-------------------|
| A1 | **Person A** | CRITICAL | DecoratorFactory.java | `Component` name collision — won't compile |
| A2 | **Person A** | CRITICAL | BaseProductComponent.java | Lombok duplicate `getDescription()` — won't compile |
| A3 | **Person A** | HIGH | BaseProductComponent.java | `"Base: "` prefix accumulates on each rebuild |
| A4 | **Person A** | MEDIUM | ComponentService.java | `canBeAppliedMultipleTimes` never checked |
| A5 | **Person A** | LOW | pom.xml | Lombok not excluded from runtime JAR |
| B1 | **Person B** | HIGH | ComponentService.java:68 | `removeDecorator` passes name as description |
| B2 | **Person B** | HIGH | ComponentService.java:86 | `rebuildChain` passes name as description |
| B3 | **Person B** | MEDIUM | ComponentController.java:74 | Missing `@Valid` on simulate endpoint |
| B4 | **Person B** | MEDIUM | ApiKeyFilter.java | Swagger UI blocked by API key filter |
| B5 | **Person B** | LOW | ComponentService.java:67 | `remove()` only deletes first occurrence |

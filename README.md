# 📖 Access Control with Spring Expression Language (SpEL)

This project demonstrates a **rule-based access control system** using **Spring Boot** and **Spring Expression Language (SpEL)** for flexible, dynamic permission evaluation.

---

## Use Case Summary

We use `SpEL` to define **access rules dynamically** in configuration (`YAML`) for different personas and functions. These rules determine whether a given `persona` can access a `targetPersona` under specific conditions.

---

## Example Use Case

> “PERSONA1 is normally allowed to perform `READ_WRITE` on `USER_MANAGEMENT`, but a special rule restricts access to only `persona2` and `persona3`.”

```yaml
access-config:
  personaAccess:
    - persona: PERSONA1
      existingRule:
        functionName: "USER_MANAGEMENT"
        scope: "Global"
        action: "READ_WRITE"
      specialRule:
        functionName: "USER_MANAGEMENT"
        action: "READ_WRITE"
        conditions:
          - "#targetPersona == 'persona2'"
          - "#targetPersona == 'persona3'"
```

---

## How SpEL Works in This Project

SpEL (Spring Expression Language) is evaluated at runtime using a context (`EvaluationContext`) which is populated dynamically:

```java
StandardEvaluationContext context = new StandardEvaluationContext();
context.setVariable("targetPersona", targetPersona);
```

### Sample Expressions You Can Use

| Expression | Meaning |
|-----------|---------|
| `#targetPersona == 'persona2'` | Allow access if targetPersona is persona2 |
| `{ 'persona1', 'persona2' }.contains(#targetPersona)` | Allow if in list |
| `#user.type == 'C' and #user.age > 18` | Compound logic |
| `!{ 'banned', 'blocked' }.contains(#status)` | Negative match |

---

## Example API

You can test access with:

```http
GET /access/check?persona=PERSONA1&function=USER_MANAGEMENT&targetPersona=persona2
```

### Sample Response (Access Allowed)
```json
{
  "persona": "PERSONA1",
  "function": "USER_MANAGEMENT",
  "targetPersona": "persona2",
  "access": true,
  "action": "READ_WRITE"
}
```

### Sample Response (Access Denied)
```json
{
  "persona": "PERSONA1",
  "function": "USER_MANAGEMENT",
  "targetPersona": "personaX",
  "access": false
}
```

---

## Benefits of This Approach

-  Rules are **declarative** and **externalized** — no need to recompile code
-  Supports **dynamic condition checks** via SpEL
-  Easily extendable for new personas or business logic
-  YAML or database-driven rule structure


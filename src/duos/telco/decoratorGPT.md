# Decorator Pattern Task – Pricing Engine (Real‑World Example)

## Context (Real Life, Unambiguous)

You are implementing a **pricing engine for an online store**.

A product has a **base price**. Before checkout, the system must compute the **final payable price** by *applying pricing rules*. These rules vary per customer, per product, and per situation.

Examples of pricing rules:

* Tax (VAT)
* Percentage discount (sale)
* Fixed discount (coupon)
* Service fee
* Free‑shipping promotion

👉 These rules must be **composable**, **order‑dependent**, and **optional**.

This is a textbook **Decorator Pattern** use case.

---

## Core Requirement

You must calculate the **final price** of a product by dynamically wrapping pricing rules around a base price calculator.

Each pricing rule:

* Applies **one clear transformation** to the price
* Delegates to the wrapped component
* Does NOT know or care about other rules

---

## Mental Model (Single Interpretation)

> "Start from the base price, then apply pricing rules one by one, in the order they are wrapped."

No alternative interpretation. No side effects. No branching logic.

---

## Provided `main()` (DO NOT MODIFY)

```java
public class Main {

    public static void main(String[] args) {

        PriceCalculator calculator =
                new TaxDecorator(
                    new PercentageDiscountDecorator(
                        new FixedDiscountDecorator(
                            new BasePriceCalculator(100.0), // base price
                            10.0                              // $10 coupon
                        ),
                        0.20                                  // 20% discount
                    ),
                    0.18                                      // 18% VAT
                );

        System.out.println("Final price: " + calculator.calculate());

        System.out.println("\n---- Another scenario ----");

        PriceCalculator noDiscount =
                new TaxDecorator(
                    new BasePriceCalculator(50.0),
                    0.10
                );

        System.out.println("Final price: " + noDiscount.calculate());
    }
}
```

---

## Your Responsibilities

You must define:

1. `PriceCalculator` **interface**
2. `BasePriceCalculator`
3. Abstract decorator (e.g. `PriceDecorator`)
4. Concrete decorators:

    * `TaxDecorator`
    * `PercentageDiscountDecorator`
    * `FixedDiscountDecorator`

---

## Decorator Responsibilities (Strict)

| Class                         | Responsibility                              |
| ----------------------------- | ------------------------------------------- |
| `BasePriceCalculator`         | Returns base price                          |
| `TaxDecorator`                | Adds tax: `price + price * taxRate`         |
| `PercentageDiscountDecorator` | Applies discount: `price - price * percent` |
| `FixedDiscountDecorator`      | Subtracts fixed amount                      |

Each class does **exactly one thing**.

---

## Design Constraints (Mandatory)

* `PriceCalculator` must be an **interface**
* Decorators must:

    * Hold a `PriceCalculator` reference
    * Delegate via `calculate()`
* ❌ No `instanceof`
* ❌ No conditionals in `main()`
* ❌ No static pricing logic
* ❌ No inheritance chains like `DiscountedTaxedPrice`

---

## Expected Output (Approximate)

```text
Final price: 114.48

---- Another scenario ----
Final price: 55.0
```

*(Exact formatting is not graded — correctness is.)*

---

## Test Cases (You Must Pass These)

### 1. Base price only

```java
new BasePriceCalculator(100).calculate() → 100
```

### 2. Fixed discount only

```java
new FixedDiscountDecorator(
    new BasePriceCalculator(100), 10
).calculate() → 90
```

### 3. Percentage discount only

```java
new PercentageDiscountDecorator(
    new BasePriceCalculator(200), 0.25
).calculate() → 150
```

### 4. Tax only

```java
new TaxDecorator(
    new BasePriceCalculator(50), 0.10
).calculate() → 55
```

### 5. Order matters (IMPORTANT)

```java
PriceCalculator a = new TaxDecorator(
    new PercentageDiscountDecorator(
        new BasePriceCalculator(100), 0.10
    ), 0.20
);

PriceCalculator b = new PercentageDiscountDecorator(
    new TaxDecorator(
        new BasePriceCalculator(100), 0.20
    ), 0.10
);
```

👉 `a.calculate() != b.calculate()`

---

## Edge Cases (Must Handle Correctly)

### Fixed discount larger than price

```java
Base: 30, Fixed discount: 50 → Result: 0 (no negative prices)
```

### Zero values

* Tax rate = 0
* Discount = 0
* Base price = 0

All must return sensible results.

### Multiple decorators of same type

```java
new FixedDiscountDecorator(
    new FixedDiscountDecorator(
        new BasePriceCalculator(100), 10
    ), 5
).calculate() → 85
```

---

## What This Task Evaluates

* True understanding of the **Decorator Pattern**
* Delegation discipline
* Order‑dependent behavior
* Real‑world modeling
* Clean Java design

---

## Examiner’s Warning

If you:

* Merge logic into one class
* Use flags or conditionals to control behavior
* Break delegation

❌ You did NOT implement the Decorator Pattern.

---

If you want, after implementation, send **any one decorator** and I’ll review it like a senior engineer or exam grader.

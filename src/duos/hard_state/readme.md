# State Pattern Task – Order Lifecycle Management (Hard)

## Context (Real Life, Unambiguous)

You are implementing the **lifecycle management of an order** in a real e-commerce system.

An order is not just data — it has **behavior that changes over time**.

The *same user action* can:

* succeed
* fail
* do nothing
* or throw an error

depending **only on the current state of the order**.

There must be **no conditional logic** in the order itself that checks what state it is in.

---

## Mental Model (Single Interpretation)

> “An order delegates all behavior to its current state. The state decides what happens and whether a transition occurs.”

No flags. No `if (state == ...)`. No branching logic in the context.

---

## Domain Rules (These Are Absolute)

An order starts its life **before payment** and may end up either **successfully delivered** or **terminated early**.

### Actions that can be invoked on an order

The following operations may be called at any time:

* `pay()` – attempt to pay for the order
* `ship()` – attempt to ship the order
* `deliver()` – attempt to mark the order as delivered
* `cancel()` – attempt to cancel the order

Calling an operation that is **invalid in the current state** must:

* clearly signal failure (exception or message)
* NOT change the state

---

## State Transitions (The Core of the Task)

The lifecycle follows **real-world constraints**:

### Before payment

* `pay()` → payment succeeds → order moves forward
* `cancel()` → order is canceled
* `ship()` / `deliver()` → invalid

### After payment, before shipping

* `ship()` → order moves forward
* `cancel()` → order is canceled
* `pay()` → invalid
* `deliver()` → invalid

### After shipping

* `deliver()` → order is completed
* `cancel()` → invalid (cannot cancel shipped orders)
* `pay()` / `ship()` → invalid

### After completion

* All operations are invalid

### After cancellation

* All operations are invalid

No other transitions are allowed.

---

## Required Behavior (Non-Negotiable)

* The **Order object must not contain conditional logic** based on state
* All behavior differences must be achieved via **polymorphism**
* State transitions must be **explicit and controlled by states**
* The current state must be **replaceable at runtime**

---

## Provided `main()` (DO NOT MODIFY)

```java
public class Main {

    public static void main(String[] args) {

        Order order = new Order();

        order.pay();
        order.ship();
        order.deliver();

        System.out.println("--- Attempting invalid transition ---");
        try {
            order.cancel();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\n--- Second scenario ---");

        Order canceled = new Order();
        canceled.pay();
        canceled.cancel();

        try {
            canceled.ship();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
```

---

## Expected Output (Representative, Not Exact)

```text
Payment successful
Order shipped
Order delivered
--- Attempting invalid transition ---
Cannot cancel a delivered order

--- Second scenario ---
Payment successful
Order canceled
Cannot ship a canceled order
```

---

## Design Constraints (Strict)

* ❌ No `if` / `switch` on state inside the order

* ❌ No enums controlling logic

* ❌ No boolean flags like `isPaid`, `isShipped`

* ❌ No giant methods handling all cases

* ✔ Use dynamic dispatch

* ✔ Each state handles **only what it allows**

* ✔ Illegal operations must be rejected by the state

---

## Edge Cases You Must Handle

1. Calling `pay()` twice
2. Calling `ship()` before payment
3. Canceling after shipping
4. Any action after completion
5. Any action after cancellation

All must fail **without changing state**.

---

## Why This Is Harder Than the Pricing Engine

| Pricing Engine         | Order Lifecycle           |
| ---------------------- | ------------------------- |
| Linear transformations | Cyclic + terminal states  |
| Order-dependent        | State-dependent           |
| No invalid operations  | Many invalid operations   |
| Decorators stack       | States replace each other |

---

## What This Task Tests

* True understanding of the **State Pattern**
* Eliminating conditionals via polymorphism
* Correct ownership of transitions
* Real-world workflow modeling

---

## Examiner’s Warning

If your `Order` class:

* checks its own state with conditionals
* contains transition logic itself
* delegates behavior partially

❌ You did NOT implement the State Pattern.

---

When you’re done, send:

* the `Order` class
* one concrete state

and I’ll review it mercilessly.
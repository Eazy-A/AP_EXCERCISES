package duos.hard_state;

// Custom exception for cleaner error handling
class IllegalStateTransitionException extends RuntimeException {
    public IllegalStateTransitionException(String message) {
        super(message);
    }
}

interface ActionState {
    void pay();
    void ship();
    void deliver();
    void cancel();
}

/**
 * Abstract Base Class to handle default "Invalid" behavior.
 * This removes boilerplate from the individual state classes.
 */
abstract class BaseState implements ActionState {
    protected final Order order;

    public BaseState(Order order) {
        this.order = order;
    }

    @Override
    public void pay() { throw new IllegalStateTransitionException("Cannot pay in current state: " + this.getClass().getSimpleName()); }

    @Override
    public void ship() { throw new IllegalStateTransitionException("Cannot ship in current state: " + this.getClass().getSimpleName()); }

    @Override
    public void deliver() { throw new IllegalStateTransitionException("Cannot deliver in current state: " + this.getClass().getSimpleName()); }

    @Override
    public void cancel() { throw new IllegalStateTransitionException("Cannot cancel in current state: " + this.getClass().getSimpleName()); }
}

class Order implements ActionState {
    private ActionState state;

    public Order() {
        this.state = new Started(this);
    }

    public void setState(ActionState state) {
        this.state = state;
    }

    @Override public void pay() { state.pay(); }
    @Override public void ship() { state.ship(); }
    @Override public void deliver() { state.deliver(); }
    @Override public void cancel() { state.cancel(); }
}


class Started extends BaseState {
    public Started(Order order) { super(order); }

    @Override
    public void pay() {
        System.out.println("Payment processed.");
        order.setState(new Payment(order));
    }

    @Override
    public void cancel() {
        System.out.println("Order cancelled from Started state.");
        order.setState(new Canceled(order));
    }
}

class Payment extends BaseState {
    public Payment(Order order) { super(order); }

    @Override
    public void ship() {
        System.out.println("Order is being shipped.");
        order.setState(new Shipped(order));
    }

    @Override
    public void cancel() {
        System.out.println("Payment refunded. Order cancelled.");
        order.setState(new Canceled(order));
    }
}

class Shipped extends BaseState {
    public Shipped(Order order) { super(order); }

    @Override
    public void deliver() {
        System.out.println("Order is completed and delivered.");
        order.setState(new Completed(order));
    }
}

// Terminal states don't need to override anything anymore!
class Completed extends BaseState { public Completed(Order order) { super(order); } }
class Canceled extends BaseState { public Canceled(Order order) { super(order); } }


public class Main {
    public static void main(String[] args) {
        Order order = new Order();

        try {
            order.pay();      // OK
            order.ship();     // OK
            order.deliver();  // OK

            System.out.println("Attempting invalid cancel on completed order...");
            order.cancel();   // Will throw exception
        } catch (IllegalStateTransitionException e) {
            System.err.println("Error: " + e.getMessage());
        }

        System.out.println("\n--- Scenario: Early Cancel ---");
        Order newOrder = new Order();
        newOrder.cancel(); // OK

        try {
            newOrder.pay(); // Error
        } catch (IllegalStateTransitionException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
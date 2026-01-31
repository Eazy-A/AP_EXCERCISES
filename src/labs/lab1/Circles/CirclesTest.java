package labs.lab1.Circles;

import java.util.Scanner;

enum TYPE {POINT, CIRCLE}

enum DIRECTION {UP, DOWN, LEFT, RIGHT}

interface Movable {
    void moveUp() throws ObjectCanNotBeMovedException;

    void moveDown() throws ObjectCanNotBeMovedException;

    void moveRight() throws ObjectCanNotBeMovedException;

    void moveLeft() throws ObjectCanNotBeMovedException;

    int getCurrentXPosition();

    int getCurrentYPosition();
}


class ObjectCanNotBeMovedException extends Exception {
    public ObjectCanNotBeMovedException() {
        super();
    }

    public ObjectCanNotBeMovedException(String message) {
        super(message);
    }
}


class MovablePoint implements Movable {
    private int x, y, xSpeed, ySpeed;

    public MovablePoint(int x, int y, int xSpeed, int ySpeed) {
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    @Override
    public void moveUp() throws ObjectCanNotBeMovedException {
        if (y + ySpeed > MovablesCollection.getY_MAX())
            throw new ObjectCanNotBeMovedException(
                    "Point (" + x + "," + (y + ySpeed) + ") is out of bounds");
        y += ySpeed;
    }

    @Override
    public void moveDown() throws ObjectCanNotBeMovedException {
        if (y - ySpeed < 0)
            throw new ObjectCanNotBeMovedException(
                    "Point (" + x + "," + (y - ySpeed) + ") is out of bounds");
        y -= ySpeed;
    }

    @Override
    public void moveRight() throws ObjectCanNotBeMovedException {
        if (x + xSpeed > MovablesCollection.getX_MAX())
            throw new ObjectCanNotBeMovedException(
                    "Point (" + (x + xSpeed) + "," + y + ") is out of bounds");
        x += xSpeed;
    }

    @Override
    public void moveLeft() throws ObjectCanNotBeMovedException {
        if (x - xSpeed < 0)
            throw new ObjectCanNotBeMovedException(
                    "Point (" + (x - xSpeed) + "," + y + ") is out of bounds");
        x -= xSpeed;
    }

    @Override
    public int getCurrentXPosition() {
        return x;
    }

    @Override
    public int getCurrentYPosition() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("Movable point with coordinates (%d,%d) ", getCurrentXPosition(), getCurrentYPosition());
    }
}


class MovableCircle implements Movable {
    private int radius;
    private MovablePoint center;

    public MovableCircle(int radius, MovablePoint center) {
        this.radius = radius;
        this.center = center;
    }

    @Override
    public void moveUp() throws ObjectCanNotBeMovedException {
        center.moveUp();
    }

    @Override
    public void moveDown() throws ObjectCanNotBeMovedException {
        center.moveDown();
    }

    @Override
    public void moveRight() throws ObjectCanNotBeMovedException {
        center.moveRight();
    }

    @Override
    public void moveLeft() throws ObjectCanNotBeMovedException {
        center.moveLeft();
    }

    @Override
    public int getCurrentXPosition() {
        return center.getCurrentXPosition();
    }

    @Override
    public int getCurrentYPosition() {
        return center.getCurrentYPosition();
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return String.format(
                "Movable circle with center coordinates (%d,%d) and radius %d",
                getCurrentXPosition(), getCurrentYPosition(), radius);
    }
}


class MovableObjectNotFittableException extends Exception {
    public MovableObjectNotFittableException(String message) {
        super(message);
    }
}


class MovablesCollection {
    private Movable[] movable;
    private static int x_MAX;
    private static int y_MAX;
    private int count = 0;

    public MovablesCollection(int x_MAX, int y_MAX) {
        MovablesCollection.x_MAX = x_MAX;
        MovablesCollection.y_MAX = y_MAX;
        this.movable = new Movable[100];
    }

    public static int getX_MAX() {
        return x_MAX;
    }

    public static int getY_MAX() {
        return y_MAX;
    }

    public static void setxMax(int i) {
        x_MAX = i;
    }

    public static void setyMax(int i) {
        y_MAX = i;
    }

    void addMovableObject(Movable m) throws MovableObjectNotFittableException {
        if (m instanceof MovableCircle) {
            MovableCircle mc = (MovableCircle) m;
            int r = mc.getRadius();
            int cx = mc.getCurrentXPosition();
            int cy = mc.getCurrentYPosition();

            if (cx - r < 0 || cx + r > x_MAX || cy - r < 0 || cy + r > y_MAX) {
                throw new MovableObjectNotFittableException(
                        String.format("Movable circle with center (%d,%d) and radius %d can not be fitted into the collection",
                                cx, cy, r));
            }
        } else {
            int px = m.getCurrentXPosition();
            int py = m.getCurrentYPosition();

            if (px < 0 || px > x_MAX || py < 0 || py > y_MAX) {
                throw new MovableObjectNotFittableException(
                        "Movable point cannot fit in the defined space");
            }
        }

        if (count >= movable.length) {
            throw new MovableObjectNotFittableException("Collection is full");
        }
        movable[count++] = m;
    }

    void moveObjectsFromTypeWithDirection(TYPE type, DIRECTION direction) {
        for (int i = 0; i < count; i++) {
            Movable m = movable[i];
            if ((type == TYPE.POINT && m instanceof MovablePoint) ||
                    (type == TYPE.CIRCLE && m instanceof MovableCircle)) {
                try {
                    switch (direction) {
                        case UP:
                            m.moveUp();
                            break;
                        case DOWN:
                            m.moveDown();
                            break;
                        case LEFT:
                            m.moveLeft();
                            break;
                        case RIGHT:
                            m.moveRight();
                            break;
                    }
                } catch (ObjectCanNotBeMovedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Collection of movable objects with size %d:", count));
        for (int i = 0; i < count; i++) {
            sb.append("\n").append(movable[i].toString());
        }
        sb.append("\n");
        return sb.toString();
    }
}

public class CirclesTest {

    public static void main(String[] args) {

        System.out.println("===COLLECTION CONSTRUCTOR AND ADD METHOD TEST===");
        MovablesCollection collection = new MovablesCollection(100, 100);
        Scanner sc = new Scanner(System.in);
        int samples = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < samples; i++) {
            String inputLine = sc.nextLine();
            String[] parts = inputLine.split(" ");

            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int xSpeed = Integer.parseInt(parts[3]);
            int ySpeed = Integer.parseInt(parts[4]);
            try {
                if (Integer.parseInt(parts[0]) == 0) { //point
                    collection.addMovableObject(new MovablePoint(x, y, xSpeed, ySpeed));
                } else { //circle
                    int radius = Integer.parseInt(parts[5]);
                    collection.addMovableObject(new MovableCircle(radius, new MovablePoint(x, y, xSpeed, ySpeed)));
                }
            } catch (MovableObjectNotFittableException e) {
                System.out.println(e.getMessage());
            }

        }
        System.out.println(collection.toString());

        System.out.println("MOVE POINTS TO THE LEFT");
        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.LEFT);
        System.out.println(collection.toString());

        System.out.println("MOVE CIRCLES DOWN");
        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.DOWN);
        System.out.println(collection.toString());

        System.out.println("CHANGE X_MAX AND Y_MAX");
        MovablesCollection.setxMax(90);
        MovablesCollection.setyMax(90);

        System.out.println("MOVE POINTS TO THE RIGHT");
        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.RIGHT);
        System.out.println(collection.toString());

        System.out.println("MOVE CIRCLES UP");
        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.UP);
        System.out.println(collection.toString());


    }


}

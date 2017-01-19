

/**
 * Created by yay on 20.12.2016.
 * Simple class representing a node in a grid.
 * Static method provided to calculate the distance between two nodes.
 */
public class AntNode {
    private static int idCounter = 0;
    private final int id;
    int x;
    int y;

    public AntNode(int x, int y) {
        this.id = idCounter++;
        this.x = x;
        this.y = y;
    }

    public static double calculateDistance(AntNode n1, AntNode n2) {
        return Math.sqrt(Math.pow(n1.x - n2.x, 2) + Math.pow(n1.y - n2.y, 2));
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof AntNode) {
            AntNode n = (AntNode) o;
            if (n.x == this.x && n.y == this.y) {
                return true;
            }
            if(n.y == this.x && n.x == this.y) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        return x < y ? "(" + Integer.toString(x) + ", " + Integer.toString(y) + ")" : "(" + Integer.toString(y) + ", " + Integer.toString(x) + ")";
    }
}

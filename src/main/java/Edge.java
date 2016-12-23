
import java.util.HashSet;


/**
 * Created by yay on 20.12.2016.
 * Works the same as a HashSet with two values, but the hashCode, toString and equals methods depending on the two values.
 */

public class Edge extends HashSet<Integer> {

    Edge(Integer a, Integer b) {
        super(2);
        this.add(a);
        this.add(b);

    }

    public Integer[] getAsArray() {
        return this.toArray(new Integer[this.size()]);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        Integer[] nodes = this.toArray(new Integer[this.size()]);
        Integer a = nodes[0];
        Integer b = nodes[1];
        return a < b ? "(" + Integer.toString(a) + ", " + Integer.toString(b) + ")" : "(" + Integer.toString(b) + ", " + Integer.toString(a) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Edge)) return false;

        Edge e = (Edge) o;
        Integer[] nodes1 = this.toArray(new Integer[this.size()]);
        Integer a1 = nodes1[0];
        Integer b1 = nodes1[1];
        Integer[] nodes2 = e.toArray(new Integer[this.size()]);
        Integer a2 = nodes2[0];
        Integer b2 = nodes2[1];

        if (a1 == a2 && b1 == b2) return true;

        if (b1 == a2 && a1 == b2) return true;

        return false;
    }
}

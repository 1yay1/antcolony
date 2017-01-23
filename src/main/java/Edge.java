
import java.awt.geom.Point2D;
import java.util.HashSet;


/**
 * Created by yay on 20.12.2016.
 * Works the same as a HashSet with two values, but the hashCode, toString and equals methods depending on the two values.
 */

public class Edge  {
    final Integer[] arr;
    Edge(Integer a, Integer b) {
        arr = new Integer[2];
        if(a > b) {
            arr[0] = a;
            arr[1] = b;
        } else {
            arr[1] = a;
            arr[0] = b;
        }

    }

    public Integer getA() {
        return arr[0];
    }

    public Integer getB() {
        return arr[1];
    }

    public Integer[] getArr() {
        return arr;
    }

    /*@Override
    public int hashCode() {
        return toString().hashCode();
    }*/

    /**
     * Same Implementation as Point2D hashCode.
     * @return hashCode
     */
    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(getA());
        bits ^= java.lang.Double.doubleToLongBits(getB()) * 31;
        return (((int) bits) ^ ((int) (bits >> 32)));
    }


    @Override
    public String toString() {
        Integer a = arr[0];
        Integer b = arr[1];
        return "["+a.toString()+":"+b.toString()+"]";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Edge)) return false;
        Edge e = (Edge) o;
        if (getA() == e.getA() && getB() == e.getB()) return true;
        if (getB() == e.getA() && getA() == e.getB()) return true;
        return false;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Benutzer01
 */
@Deprecated
public class WeightedEdge extends Edge implements Comparable<WeightedEdge>{
    Double weightedWay;
    public WeightedEdge(Integer a, Integer b, Double weightedWay) {
        super(a, b);
        this.weightedWay=weightedWay;
    }
   

   
    @Override
    public int compareTo(WeightedEdge o) {
        return weightedWay.compareTo(o.weightedWay);
    }
    
}

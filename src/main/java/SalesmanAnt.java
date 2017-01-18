
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by yay on 21.12.2016.
 */
public class SalesmanAnt  extends Ant{

    public SalesmanAnt(Grid g, BlockingQueue blockingQueue, int tourNumber) {
        super(g, blockingQueue, tourNumber);
    }

    @Override
    protected void producePheromone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Integer chooseNextNode(double q0,double beta) {
        Map<Edge,EdgeInfo> possibleEdges =getPossibleNextEdgeInfoMap();
        double random=ThreadLocalRandom.current().nextDouble(1);
        Map<Edge,Double>weightedPathValue=new HashMap<>();
        for(Map.Entry<Edge,EdgeInfo> entry: possibleEdges.entrySet()){
            double distance=entry.getValue().getDistance();
            double pheromone=entry.getValue().getPheromone().getValue();
            weightedPathValue.put(entry.getKey(), Math.pow((1/distance),beta)*pheromone);
        }
        if(random<q0){
            Edge max=weightedPathValue.entrySet().stream().max((entry1, entry2) -> 
                    entry1.getValue()>entry2.getValue()  ? 1 : -1).get().getKey();
                 return (getPath().get(getPath().size()-1).equals(max.getAsArray()[0])) 
                         ?max.getAsArray()[1] : max.getAsArray()[0];
                     
        }
         double sumValues=0;
         for(Edge e: weightedPathValue.keySet()){
             sumValues+=weightedPathValue.get(e);
         }
         List<WeightedEdge> normalizedPathValue=new ArrayList<>();
         for(Edge e: weightedPathValue.keySet()){
             normalizedPathValue.add(new WeightedEdge((Integer)e.toArray()[0], (Integer)e.toArray()[1],sumValues));
             sumValues-=weightedPathValue.get(e);
         }
        double randomselectPath=ThreadLocalRandom.current().nextDouble(sumValues);
        for(WeightedEdge we: normalizedPathValue){
            if(we.weightedWay<randomselectPath) {
                return (getPath().get(getPath().size()-1).equals(we.getAsArray()[0])) 
                         ?we.getAsArray()[1] : we.getAsArray()[0];
            }
        }
        return Null;
    }

  

   

}

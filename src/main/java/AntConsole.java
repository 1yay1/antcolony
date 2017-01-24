import java.io.File;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yay on 20.12.2016.
 */

public class AntConsole {
	public static void main(String[] args) {
		String map_file = parseToString(args, "mapfile", true, "");
				Integer ants_per_round = parseToInteger(args, "ants", false, 25);
		Integer total_rounds = parseToInteger(args, "rounds", false, 20);
Integer use_ant=parseToInteger(args,"anttype", false, 0);
		File fp = new File(map_file);
		if (fp.exists() == false) {
			System.out.println("File " + map_file + "does not exist");
			System.exit(-1);
		}
		if (fp.canRead() == false) {
			System.out.println("Cannot read from file " + map_file);
			System.exit(-1);
		}
		Grid g = new Grid(fp);
		ArrayBlockingQueue<List<Integer>> pq = new ArrayBlockingQueue<>(total_rounds * ants_per_round);
		Ant ant_obj=null;
		if(use_ant==0)
			ant_obj=defaultAntInitializer(args, pq, g, ants_per_round, total_rounds);
		else
			ant_obj=acsAntInitializer(args, pq, g, ants_per_round, total_rounds);
		
			Thread t=new Thread(ant_obj);
		t.start();
		
		while (pq.remainingCapacity() > 0)
		{
			try
			{
			Thread.sleep(333);
			}
			catch(InterruptedException ex)
			{
				System.out.println("Thread aborted");
				System.exit(-1);
			}
		}
		ant_obj.stop();
			List<Integer>path=null;
			
			List<Integer> tmp_path=pq.poll();
			while(tmp_path!=null)
			{
				path=tmp_path;
				tmp_path=pq.poll();
			}
			
		pq.clear();

		System.out.println("Path consists of:");
		for (Integer item : path) {
			System.out.print(item.toString() + ";");
		}
		System.out.println();
		System.out.println("Path is " + g.calculateDistanceFromPath(path) + " long");

	}

	private static String parseToString(String[] args, String searchForKey, Boolean required, String defaultValue) {
		for (String check_value : args) {
			if (check_value.startsWith(searchForKey + "=")) {
				if (check_value.length() < searchForKey.length() + 1) {
					return "";
				}
				return check_value.substring(searchForKey.length() + 1).trim();
			}
		}
		if (required == true) {
			System.out.println("Argument " + searchForKey + " could not be found");
			System.exit(-1);
			return "";
		} else {
			return defaultValue;
		}
	}

	private static Integer parseToInteger(String[] args, String searchForKey, Boolean required, Integer defaultValue) {
		String ret = parseToString(args, searchForKey, required, defaultValue.toString());
		try {
			return Integer.parseInt(ret);
		} catch (NumberFormatException ex) {
			System.out.println(ret + " cannot be interpreted as number");
			System.exit(-1);
			return null;
		}
	}

	private static Double parseToDouble(String[] args, String searchForKey, Boolean required, Double defaultValue) {
		String ret = parseToString(args, searchForKey, required, defaultValue.toString());
		try {
			return Double.parseDouble(ret);
		} catch (NumberFormatException ex) {
			System.out.println(ret + " cannot be interpreted as number");
			System.exit(-1);
			return null;
		}
	}

	static Ant defaultAntInitializer(String[] args, BlockingQueue<List<Integer>> bq, Grid g, Integer antsPerRound, Integer rounds)
	{
		Double q0 = parseToDouble(args, "q0", false, 0.25);
		Double decay= parseToDouble(args, "decay", false, 0.2);
		Double alpha = parseToDouble(args, "alpha", false, 1.0);
		Double beta = parseToDouble(args, "beta", false, 2.0);
		return new SalesmanAnt(g, bq, antsPerRound, rounds, q0, alpha, beta, decay);		
	}
	static Ant acsAntInitializer(String[] args, BlockingQueue<List<Integer>> bq, Grid g, Integer antsPerRound, Integer rounds)
	{
		Double q0 = parseToDouble(args, "q0", false, 0.25);
		Double alpha = parseToDouble(args, "alpha", false, 1.0);
		Double beta = parseToDouble(args, "beta", false, 2.0);
		Double epsilon=parseToDouble(args, "epsilon", false, 0.2);
		Double p=parseToDouble(args, "p", false, 0.2);
		Double t0=parseToDouble(args, "t0", false, 0.1);
		return new SalesmanAntACS(g, bq, antsPerRound, rounds, q0, alpha, beta, epsilon, p, t0);	
	}
}
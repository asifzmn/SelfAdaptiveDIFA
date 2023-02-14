package IFM;
import java.util.Arrays;

//import ODD.ODDQLUtil;

public class Variant {
	//Static parameters Static flow (static dependencies), Flow Sensitivity, Context Sensitivity, Exceptional flow, ICFG
	//Dynamic parameters Branch coverage, Dynamic points-to sets, Buffer size, Method event, Method-level flow
	public static final int Framework_width = 64;
	public static final int Framework_height = 64;
	public static double[][] MAP = new double[Framework_width][Framework_height];

	public static double invalid = -1;

	public static void main(String[] args) {
		// Arrays.fill(myarray, 42);

		// System.out.println(MAP[0][0]);

		MAP[0][0] = invalid;

		// Zero context flow in invalid xx00xxxxxxxx
		for (int i = 0; i < Framework_width; i++) {
			boolean[] bits = new boolean[6];
			for (int k = 5; k >= 0; k--) {
				bits[k] = (i & (1 << k)) != 0;
			}

			if(!bits[2] && !bits[3]){
				System.out.println(i + " = " + Arrays.toString(bits));
				for (int j = 0; j < Framework_height; j++) {
					MAP[i][j] = invalid;
				}
			}
		}




		for (int i = 0; i < Framework_width; i++) {
			for (int j = 0; j < Framework_height; j++) {
				System.out.printf(MAP[i][j] + " ");
			}
			System.out.println();
		}
	}

//	public static void loadFromFile(String mazeFile) {
//		MAP=ODDQLUtil.getMAPFromFile(mazeFile, Framework_width, Framework_height);
//	}
}

package IFM;

//import ODD.ODDUtil;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Scanner;

//import ODD.ODDQLUtil;

public class AnalysisConfiguration {
    //Static parameters Static flow (static dependencies), Flow Sensitivity, Context Sensitivity, Exceptional flow, ICFG
    //Dynamic parameters Branch coverage, Dynamic points-to sets, Buffer size, Method event, Method-level flow
    public static final int Framework_width = 64;
    public static final int Framework_height = 64;
    public static double[][] MAP = new double[Framework_width][Framework_height];

    public static int n_configs = 8;
    public static int bufSize_bit_start=3;
    public static int bufSize_bit_end=bufSize_bit_start+2;
    public static int contextDepth_bit_start =bufSize_bit_end+1;
    public static int contextDepth_bit_end = contextDepth_bit_start +1;
    public static boolean[] staticDynamicSettings = new boolean[n_configs];

    public static double invalid = -1;
    public static boolean ICFG;
    public static boolean exceptionalFlow;
    public static boolean methodLevelFlow;

    public static int bufSize;
    public static int contextDepth;

    public static String configurations="";
    public static String readLastLine(String fileName) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Scanner sc = new Scanner(fileReader);
        String line = null;
        while ((sc.hasNextLine() && (line = sc.nextLine()) != null)) {
            if (!sc.hasNextLine()) {
                return line;
            }
        }
        sc.close();
        return "";
    }

    public static void getConfigurations(String configurationFile) {
        configurations= readLastLine(configurationFile);
        //logger.info("ODDMonitor configurations="+configurations);
        for (int i=0; i<staticDynamicSettings.length; i++)
        {
            staticDynamicSettings[i]=true;
        }
        String configurationFlag="";
        int flagSize=staticDynamicSettings.length;
        if	(configurations.length()<flagSize)
            flagSize=configurations.length();
        for (int i=0; i<configurations.length(); i++)
        {
            configurationFlag=configurations.substring(i,i+1);
            if (configurationFlag.equals("0") || configurationFlag.equals("f") ||configurationFlag.equals("F")) {
                staticDynamicSettings[i]=false;
            }
            else {
                staticDynamicSettings[i]=true;
            }
            ////logger.info("ODDMonitor staticDynamicSettings["+i+"]="+staticDynamicSettings[i]);
        }

    }

//    public AnalysisConfiguration() {
//        getConfigurations("IFM/configuration_adadifa.txt");
//        ICFG = staticDynamicSettings[0];
//        exceptionalFlow = staticDynamicSettings[1];
//    }

    static  {
        getConfigurations("IFM/configuration_adadifa.txt");
        ICFG = staticDynamicSettings[0];
        exceptionalFlow = staticDynamicSettings[1];
        methodLevelFlow = staticDynamicSettings[2];

        boolean[] boolArrayBuffer = subArray(staticDynamicSettings,bufSize_bit_start,bufSize_bit_end);
        bufSize = get_buffer_size(boolArrayBuffer);
        boolean[] boolArrayContext = subArray(staticDynamicSettings, contextDepth_bit_start, contextDepth_bit_end);
        contextDepth = boolearnArraytoInt(boolArrayContext);
    }

    public static boolean[] getAnalysisConfiguration(){
        getConfigurations("IFM/configuration_adadifa.txt");
        return staticDynamicSettings;
    }


    public static boolean[] subArray(boolean[] array, int beg, int end) {
        return Arrays.copyOfRange(array, beg, end + 1);
    }

    public static int boolearnArraytoInt(boolean[] boolArray){
        int n = 0;
        for (boolean b : boolArray) {
            n = (n << 1) + (b ? 1 : 0);
        }
        return n;
    }

    public static int get_buffer_size(boolean[] boolArray){
        return (1<<boolearnArraytoInt(boolArray))*1000;
    }

    public static void var_methods() {
        int bufSize_bit_start=0,bufSize_bit_end=2,context_bit_start=3,context_bit_end=4;
        boolean[] boolArray = new boolean[] {true,true,false,true,false};
        boolean[] boolArrayBuffer = subArray(boolArray,bufSize_bit_start,bufSize_bit_end);
        int bufSize = get_buffer_size(boolArrayBuffer);
        System.out.println(bufSize);
        boolean[] boolArrayContext = subArray(boolArray,context_bit_start,context_bit_end);
        int context = boolearnArraytoInt(boolArrayContext);
        System.out.println(context);
    }


    public static boolean isICFG() {
        return ICFG;
    }

    public static boolean isExceptionalFlow() {
        return exceptionalFlow;
    }

    public static boolean isNotExceptionalFlow() {
        return !isExceptionalFlow();
    }

    public static boolean isMethodLevelFlow() {
        return methodLevelFlow;
    }

    public static int getBufSize() {
        return bufSize;
    }

    public static int getContextDepth() {
        return contextDepth;
    }

    public static void main(String[] args) {
        AnalysisConfiguration analysisConfiguration = new AnalysisConfiguration();
        System.out.println(isICFG());
        System.out.println(isExceptionalFlow());
        System.out.println(isMethodLevelFlow());
        System.out.println(getBufSize());
        System.out.println(getContextDepth());
        System.exit(0);
        // Arrays.fill(myarray, 42);

        // System.out.println(MAP[0][0]);

        MAP[0][0] = invalid;

        // Zero context flow in invalid xx00xxxxxxxx
        for (int i = 0; i < Framework_width; i++) {
            boolean[] bits = new boolean[6];
            for (int k = 5; k >= 0; k--) {
                bits[k] = (i & (1 << k)) != 0;
            }

            if (!bits[2] && !bits[3]) {
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

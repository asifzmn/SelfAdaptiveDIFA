package IFM;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import static IFM.dtUtil.getListSet;

public class test {
    public static void main(String[] args) throws InterruptedException {

//        AnalysisConfiguration analysisConfiguration = new AnalysisConfiguration();
//        System.out.println(AnalysisConfiguration.isICFG());
//        System.out.println(AnalysisConfiguration.isExceptionalFlow());
//        System.out.println(AnalysisConfiguration.isMethodLevelFlow());
//        System.out.println("testing a java file");



        String listFile = "IFM/sourceSinkMethodPairDiffClass.txt";
        HashSet<String> ListSet = getListSet(listFile);
        System.out.println(ListSet.size());

        for (String set : ListSet)
        {
            System.out.println(set);
            TimeUnit.SECONDS.sleep(5);
        }
    }
}

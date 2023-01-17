package FLOWDIST;

import java.io.File;
import java.util.ArrayList;

public class OT4Inst {
	public static ArrayList coveredMethods = new ArrayList();
	public static ArrayList branchStmts = new ArrayList();
	public static void main(String []args) {
		coveredMethods=dtUtil.getArrayListFromTwo(System.getProperty("user.dir") + File.separator + "methodList.out",System.getProperty("user.dir") + File.separator + "coveredMethods.txt");
	    //branchStmts=dtUtil.getArrayList(System.getProperty("user.dir") + File.separator + "entitystmt.out.branch");
	    
	    //coveredStmts = (ArrayList<Integer>) dtStmtUtil.readStmtCoverageOTInt(System.getProperty("user.dir"), 1); 	
	    
	    //if (opts.debugOut()) 
	    {
	   	 System.out.println("coveredMethods.size()="+coveredMethods.size());  
	   	 //System.out.println("coveredMethods="+coveredMethods); 
	   	 //System.out.println("coveredStmts.size()="+branchStmts.size());  
	   	 //System.out.println("coveredStmts="+branchStmts); 
	   	 
	    }
	}
}

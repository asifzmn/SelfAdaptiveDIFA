package ODD;
//import java.io.File;


public class ODDToolUtil {
	public static void main(String []args) {
		//saveBudget("main() ; 2100");
		String s1=getFirstStr("main() ; 2100", " ; ");
		System.out.println("s1=" + s1);
		
	}
	public static String getFirstStr(String longStr, String splitStr)
    {
    	String firstStr=longStr;
    	if (longStr.indexOf(" ; ")>0)
        {
    		String[] longStrs=longStr.split(" ; ");
    		firstStr=longStrs[0];
        }
//    	//System.out.println("budgetStr="+budgetStr);
//    	ODDUtil.writeStringToFile(budgetStr,"budget.txt");
    	return firstStr;
    }
    public static void saveBudget(String longStr)
    {
    	String budgetStr=longStr;
    	if (longStr.indexOf(" ; ")>0)
        {
    		String[] longStrs=longStr.split(" ; ");
    		budgetStr=longStrs[1];
        }
    	//System.out.println("budgetStr="+budgetStr);
    	ODDUtil.writeStringToFile(budgetStr,"budget.txt");
    }
	
}

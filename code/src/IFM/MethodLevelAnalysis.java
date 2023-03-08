package IFM;

//import disttaint.DynTransferGraph;
//import disttaint.dtUtil;

import java.io.*;
import java.util.*;

import soot.toolkits.graph.ExceptionalUnitGraph;

public class MethodLevelAnalysis {
	static Set<String> changeSet = new LinkedHashSet<String>();
	//static Map<String, Set<String> > impactSets_Diver = new LinkedHashMap<String, Set<String>>();
	static HashMap<String, Integer> impactSet_dist = new LinkedHashMap<String, Integer> ( );
	
	//static int nExecutions = 1;  //Integer.MAX_VALUE;
	
	/* the dynamic transfer graph underneath the impact computation with all execution traces */
	static final DynTransferGraph dvtg = new DynTransferGraph();
	
	static boolean debugOut = true;

	// or compute impact sets for multiple queries at the same time when traversing the execution trace for only once
	static boolean matchingDynVTGForAllQueries = false;
	// include the pruning approach just for a comparison
	static boolean pruningDynVTGForAllQueries = false;
	
	/** if applying runtime statement coverage information to prune statements not executed, examined per test case */
	public static boolean applyStatementCoverage = false;
	/** prune non-covered/non-aliased nodes and edges prior to or after basic querying process: 
	 * both are equivalent in terms of eventual impact set but can be disparate in performance
	 */
	public static boolean postPrune = true;
	
	/** if applying runtime object alias checking to prune heap value edges on which the source 
	 * and target nodes are not dynamically aliased 
	 */
	public static boolean applyDynAliasChecking = false;
	/** if pruning based on the dynamic alias information at the method instance level, or just the method level */
	public static boolean instancePrune = true; 
	static Map<String, Set<String>> localImpactSets = new LinkedHashMap<String, Set<String>>();
	// distEA variables
	static boolean separateReport = true;
	static boolean reportCommon = false;
	static boolean strictComponent = false; // strictly two different components won't have common traces --- they have to communicate by message passing
	static boolean improvedPrec = false; // choose between the purely EA-based and precision-improved versions
	static boolean runDiver = false;
	static Set<String> impactSet = new LinkedHashSet<String>();

	public static void main_method(String args[]){
		if (!AnalysisConfiguration.isMethodLevelFlow())
		{
			return;
		}

		if (args.length < 3) {
			System.err.println("Too few arguments: \n\t " +
					"DiverAnalysis changedMethods traceDir binDir [numberTraces] [debugFlag]\n\n");
			return;
		}
		System.out.println("DTOTAnalysis 0th\n");
		String query = args[0]; // tell the changed methods, separated by comma if there are more than one
		String traceDir = args[1]; // tell the directory where execution traces can be accessed
		String binDir = args[2]; // tell the directory where the static value transfer graph binary can be found
		String sink="";

		String changedMethods = args[0];
		List<String> Pair = dua.util.Util.parseStringList(query, ';');
		if (Pair.size() < 1) {
			// nothing to do
			System.err.println("Empty query, nothing to do.");
			return;
		}
		changedMethods=Pair.get(0);
		if (Pair.size() > 1)
			sink=Pair.get(1);
		if (args.length > 3) {
			debugOut = args[3].equalsIgnoreCase("-debug");
		}

		if (args.length > 4) {
			improvedPrec  = args[4].startsWith("-prec");
		}

		//dist works
		try {
			// process the given query directly
			File funclist = new File(changedMethods);
			if (!funclist.isFile()) {
				if (separateReport) {
					if (debugOut)						System.out.println("separateReport");
					final long startTime = System.currentTimeMillis();
					separateParseTraces(changedMethods, traceDir,sink,query);
					final long stopTime = System.currentTimeMillis();
					System.out.println("	separateParseTraces took " + (startTime - stopTime ) + " ms");
				}

				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]){
		main_method(args);
	}
	
	public static int init(String binDir) {
		return 0;
	}

	@SuppressWarnings("unchecked")
	public static void mergeTraces(File loc, HashMap<String, Integer> F, HashMap<String, Integer> L)  throws IOException, ClassNotFoundException {
		if (loc.isFile() && loc.getName().endsWith(".em")) {
			FileInputStream fis = new FileInputStream(loc);
			ObjectInputStream ois = new ObjectInputStream(fis);
			HashMap<String, Integer> curF =  (HashMap<String, Integer>) ois.readObject();
			HashMap<String, Integer> curL =  (HashMap<String, Integer>) ois.readObject();
			
			F.putAll(curF);
			L.putAll(curL);
			
			fis.close();
			ois.close();
			
			return;
		}
		
		if (!loc.isDirectory()) { return; }
		
		for (String fname : loc.list()) {
			mergeTraces (new File(loc, fname), F, L);
		}
	}
	/** compute impacts from the trace dumped by one OS process with respect to a test input */ 
	private static int computeProcesTrace(List<String> Chglist, HashMap<String, Integer> F, 
			HashMap<String, Integer> L, List<String> localChgSet, Set<String> localImpactSet) {
		// determine the CLT (Change with Least Time-stamp in F)
		String CLT = "";
		Integer tsCLT = Integer.MAX_VALUE;
		for (String chg : Chglist) {
			for (String m : F.keySet()) {
				if ( !m.toLowerCase().contains(chg.toLowerCase()) && 
						!chg.toLowerCase().contains(m.toLowerCase()) ) {
					// unmatched change specified even with a very loose matching
					continue;
				}
				localChgSet.add(m);
				if (F.get(m) <= tsCLT) {
					tsCLT = F.get(m);
					CLT = m;
				}
			}
		}
		// compute the impact set with respect to this execution trace
		for (String m : L.keySet()) {
			if (L.get(m) >= tsCLT) {
				localImpactSet.add(m);
			}
		}
		return tsCLT;
	}

	public static void separateParseTraces(String changedMethods, String traceDir,String sinks,String query) {
		int tId;
		List<String> Chglist = dua.util.Util.parseStringList(changedMethods, ';');
		if (Chglist.size() < 1) {
			// nothing to do
			return;
		}
		String methodPairMsg="";
		//for (tId = 1; tId <= nExecutions; ++tId)
		for (tId = 1; tId <= 1; ++tId) 
		{
			methodPairMsg=query;
			//String CLT = "";
			Integer tsCLT = Integer.MAX_VALUE;
			try {
				String dnSource = traceDir  + File.separator + "test" + tId + File.separator;
				HashMap<String, Integer> F = new HashMap<String, Integer> ( );
				HashMap<String, Integer> L = new HashMap<String, Integer> ( );
				
				Map<String, HashMap<String, Integer>> f2L = new HashMap<String, HashMap<String, Integer>> ( );
				Map<String, HashMap<String, Integer>> f2F = new HashMap<String, HashMap<String, Integer>> ( );
				Map<String, HashMap<String, Integer>> f2S = new HashMap<String, HashMap<String, Integer>>();
				
				//Set<String> covered = new HashSet<String> ( );
								
				if (debugOut) {
					System.out.println("\nDeserializing event maps from " + dnSource);
				}
				// 1. distEA reconstructs the two event maps from the serialized execution trace associated with this test
				final long step1startTime = System.currentTimeMillis();
				mergeTraces(new File(dnSource), F, L);				
				final long step1stopTime = System.currentTimeMillis();
				System.out.println("	separateParseTraces_mergeTraces took " + (step1startTime - step1stopTime ) + " ms");
				// -- DEBUG
//				if (debugOut) {
//					System.out.println("\n[ First events ]\n" + F );
//					System.out.println("\n[ Last events ]\n" + L );
//				}				
				// dist
				File dn = new File(dnSource);
				// load trace of all processes into memory
				long fixtime = System.currentTimeMillis();
				for (String fname : dn.list()) {
					File loc = new File(dn, fname);
					if (loc.isFile() && loc.getName().endsWith(".em")) {
						FileInputStream fis = new FileInputStream(loc);
						ObjectInputStream ois = new ObjectInputStream(fis);
						HashMap<String, Integer> curF =  (HashMap<String, Integer>) ois.readObject();
						HashMap<String, Integer> curL =  (HashMap<String, Integer>) ois.readObject();
						
						if (improvedPrec) {
							HashMap<String, Integer> _curS =  (HashMap<String, Integer>) ois.readObject();
							HashMap<String, Integer> curS = new HashMap<String, Integer>();
							for (Map.Entry<String, Integer> entry : _curS.entrySet()) {
								String k = entry.getKey().trim().replace("\0", "");
								Integer v = entry.getValue();
								curS.put(k, v);
							}
							f2S.put(loc.getAbsolutePath(), curS);
						}
						
						f2L.put(loc.getAbsolutePath(), curL);
						f2F.put(loc.getAbsolutePath(), curF);
						fis.close();
						ois.close();
					}
				}
				if (improvedPrec && debugOut) {
					System.out.println("Content of Sender map:");
					for (String tr : f2S.keySet()) {
						System.out.println("in trace of " + tr);
						for (String sender : f2S.get(tr).keySet()) {
							System.out.println(sender + "\t" + f2S.get(tr).get(sender));
						}
					}
				}
				//fixtime = System.currentTimeMillis() - fixtime;
				
				final long step2stopTime = System.currentTimeMillis();
				System.out.println("	separateParseTraces_LoadTrace took " + (step2stopTime - step1stopTime ) + " ms");
				// compute impacts in local and external processes
				for (String tf : f2F.keySet()) {
					HashMap<String, Integer> curF =  f2F.get(tf);
					HashMap<String, Integer> curL = f2L.get(tf);
					
					String ProcessI = null;
					if (improvedPrec) {
						HashMap<String, Integer> curS = f2S.get(tf);
						assert curS.containsValue(Integer.MAX_VALUE);
						for (String pid : curS.keySet()) {
							if (curS.get(pid)==Integer.MAX_VALUE) {
								ProcessI = pid;
								break;
							}
						}
						assert ProcessI != null;
					}					
					List<String> localChgSet = new ArrayList<String>();
					Set<String> localImpactSet = new LinkedHashSet<String>();					
					tsCLT = computeProcesTrace(Chglist, curF, curL, localChgSet, localImpactSet);		
					// impacted methods in other processes
					Set<String> exImpactSet = new LinkedHashSet<String>();
					int excludedN = 0;
					for (String tl : f2L.keySet()) {
						if (tl.equalsIgnoreCase(tf)) { continue; }
						if (strictComponent) {
							Set<String> lptrace = new LinkedHashSet<String>(f2F.get(tf).keySet());
							lptrace.retainAll(f2L.get(tl).keySet());
							if (!lptrace.isEmpty()) continue;
						}
						for (String m : f2L.get(tl).keySet()) {
							if (f2L.get(tl).get(m) >= tsCLT) {
								if (improvedPrec) {
									HashMap<String, Integer> curS = f2S.get(tl);
									if (debugOut) {
										if (!curS.containsKey(ProcessI)) {
											System.out.println(ProcessI + " is NOT found in the sender map..., is that correct?");
											System.out.println(ProcessI + " is NOT in the set of " + curS.keySet());
										}
										else {
											System.out.println(ProcessI + " is INDEED found in the sender map, is that correct?");
										}
									}
									if (curS.containsKey(ProcessI) && f2L.get(tl).get(m)>=curS.get(ProcessI)) {
										exImpactSet.add(m);
									}
									else {
										excludedN ++;	
									}
								}
								else {
									exImpactSet.add(m);
								}
							}
						}

						changeSet.addAll(localChgSet);
						impactSet.addAll(localImpactSet);
					}
					

					
					if (debugOut)
					{
						System.out.println("[local impact Set ] size = " + localImpactSet.size());
						for (String m:localImpactSet) {
							System.out.println(m);
						}
						System.out.println(" tf= " + tf);
						System.out.println("[external impact set ] size = " + exImpactSet.size());
						for (String m:exImpactSet) {
							System.out.println(m);
						}
						System.out.println(excludedN + " methods were excluded from external impact set due to the precision improvement.");
					}
					if (exImpactSet!=null && !exImpactSet.isEmpty())
						impactSet.addAll(exImpactSet);
				}
//				final long step3stopTime = System.currentTimeMillis();
//				System.out.println("	separateParseTraces_Computation impactSet took " + (step3stopTime - step2stopTime ) + " ms");
				if (debugOut)  {
					System.out.println("[impact set ] size = " + impactSet.size());
					for (String m:impactSet) {
						System.out.println(m);
					}
					System.out.println("[L ] size = " + L.size());
				}	
				Iterator iter = L.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					if (debugOut) 
						System.out.println("L Ket ="+ entry.getKey()+" Value ="+ entry.getValue());
					String methodName=entry.getKey().toString();
					if (impactSet.contains(methodName) || impactSet.contains(methodName.toLowerCase()) || impactSet.contains(methodName.toUpperCase()))
					{
						impactSet_dist.put(methodName, Integer.parseInt(entry.getValue().toString()));
					}
				}
				if (debugOut)
				{	
					System.out.println("[impactSet_dist ] size = " + impactSet_dist.size());
					iter = impactSet_dist.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						System.out.println("impactSet_dist Ket ="+ entry.getKey()+" Value ="+ entry.getValue());
					}
				}
				//String messageFile=System.getProperty("user.dir") + File.separator + "distEAtrace_"+System.currentTimeMillis() + ".txt";
				List<Map.Entry<String, Integer>> resultList = new ArrayList<>();
		        for(Map.Entry<String, Integer> entry : impactSet_dist.entrySet()){
		             resultList.add(entry); 
		        }
		        //System.out.println("[List ] size = " + resultList.size());
		        resultList.sort(new Comparator<Map.Entry<String, Integer>>(){
		              public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
		                    return o1.getValue()-o2.getValue();} 
		        }); 
		       final long step4stopTime = System.currentTimeMillis();
			    System.out.println("	separateParseTraces_Computation impactSets took " + (step4stopTime - step2stopTime ) + " ms");
				List<String> sinklist = dua.util.Util.parseStringList(sinks, ';');
				if (sinklist.size() < 1) {
					System.out.println("\n [All results from source "+ changedMethods+" ]:" + resultList.size());
					for(Map.Entry<String, Integer> entry: resultList){
			              //System.out.println(entry);
			        	System.out.print(entry.getKey()+ " ->  ") ;
			        }
			        System.out.println(" ");			      
			        disttaint.dtUtil.writeListMap(resultList, System.getProperty("user.dir") + File.separator + "methodList.out","",true);
			        System.out.println("resultList="+resultList+" System.getProperty="+System.getProperty("user.dir") + File.separator + "methodList.out");	
			        disttaint.dtUtil.writeMethodPairListMap(methodPairMsg, resultList, System.getProperty("user.dir") + File.separator + "methodsInPair.out","",false);
			        System.out.println("methodPairMsg="+methodPairMsg+" resultList="+resultList+" System.getProperty="+System.getProperty("user.dir") + File.separator + "methodsInPair.out");	
			        
				}
				else
				{
					int pathSize=0;
					for(int i = 0 ; i < sinklist.size() ; i++) {
						System.out.println("\n [The result from source "+ changedMethods+ " to the sink "+ sinklist.get(i)+ "]:");
						if (!impactSet.contains(sinklist.get(i)))
						{
							System.out.println(" There is no flow path!\n");
							continue;
						}						
						for(Map.Entry<String, Integer> entry: resultList){
							//System.out.println(" entry.getKey()= "+ entry.getKey()+ " sinklist.get(i))= "+ sinklist.get(i));
							pathSize++;
							if (!entry.getKey().equals(sinklist.get(i)))
							{	
								System.out.print(entry.getKey()+ " ->  ") ;
							}
							else
							{
								System.out.print(entry.getKey());
								break;
							}
				        }
				        System.out.println("\n [The method path length]: "+pathSize+"\n");				       
						disttaint.dtUtil.writeListMap(resultList, System.getProperty("user.dir") + File.separator + "methodList.out",sinklist.get(i),true);
						System.out.println("resultList="+resultList+" System.getProperty="+System.getProperty("user.dir") + File.separator + "methodList.out sinklist.get(i)="+sinklist.get(i));	
				        dtUtil.writeMethodPairListMap(methodPairMsg, resultList, System.getProperty("user.dir") + File.separator + "methodsInPair.out",sinklist.get(i),false);
				        System.out.println("methodPairMsg="+methodPairMsg+" resultList="+resultList+" System.getProperty="+System.getProperty("user.dir") + File.separator + "methodsInPair.out sinklist.get(i)="+sinklist.get(i));	
				       
					}
					
				}
				final long step5stopTime = System.currentTimeMillis();
			    System.out.println("	separateParseTraces_OutputMethodPath took " + (step5stopTime - step4stopTime ) + " ms");

			}
			catch (FileNotFoundException e) {
				break;
			}
			catch (ClassCastException e) {
				System.err.println("Failed to cast the object deserialized to HashMap<String, Integer>!");
				return;
			}
			catch (IOException e) {
				throw new RuntimeException(e); 
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (debugOut) {
			System.out.println(--tId + " execution traces have been processed.");
		}

	}

}
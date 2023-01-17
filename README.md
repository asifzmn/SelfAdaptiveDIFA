# FLOWDIST: Multi-Staged Refinement-Based Dynamic Information Flow Analysis for Distributed Software Systems
-----------
Dynamic information flow analysis (DIFA) supports various security applications, such as malware analysis and vulnerability discovery. 
Yet traditional DIFA approaches have limited utility for distributed software due to applicability, portability, and scalability barriers. 
FLOWDIST is a DIFA for common distributed software that overcomes these challenges. FLOWDIST works at purely application level to avoid 
platform customizations hence achieve high portability. 
It infers implicit, interprocess dependencies from global partially ordered execution events to address applicability to distributed software, 
and utilizes various other forms of static and dynamic data for a good cost-effectiveness balance. 
Most of all, it introduces a multi-staged refinement-based scheme for application-level DIFA, where an otherwise expensive data flow analysis 
is reduced by method-level results from a cheap pre-analysis, to achieve high scalability while remaining effective. 

The complete artifact package of FLOWDIST works has been made available at https://bitbucket.org/wsucailab/flowdist/ (including the code, experimental scripts, and datasets). 	

-----------
### Explore the artifact
-----------
#### Directories:

>>>> The directory code includes two sub-directories "shell" and "src", including shell scripts and Java source code files, respectively.

>>>> The directory data includes the source file "Sources.txt" and sink file "Sinks.txt". 
 	
>>>> The directory JOANATest includes the source code for testing JOANA.

>>>> The directory newVulnerabilities includes new vulnerabilities found by FLOWDIST.   	

>>>> The directory PhosphorTest includes the source code for testing Phosphor.

>>>> The directory RawPaths includes (parts of) raw information flow paths computed by FLOWDIST.
 	
>>>> The directory tool includes all library files.
										
#### Files:

>>>> The file Message_PassingAPIList.txt is the list of message-passing APIs that FLOWDIST recognizes in order to monitor interprocess communication (i.e., message-passing) events.

>>>> The file RawTimeSpaceMinMaxMedian.xls includes the minimum, maximum, and median of executim time (ms) and space (KB) costs.
 	
>>>> The file README.md

-----------
### How to use the artifact of FLOWDIST
-----------
#### Install FLOWDIST

>>>> Download the FLOWDIST_Meterial zip file from https://bitbucket.org/wsucailab/flowdist/

>>>> Unzip the zip file 

>>>> Copy all library files from the directory ”tool” of FLOWDIST to a directory (e.g., "lib") defined by the user

>>>> For library subjects(Thrift, xSocket, and Netty), we compile corresponding applications developed by us. For example, we compile all java files in the directory ”data/#subject#/java” (data/Thrift/java).

#### Download and install one or multiple of following subjects

>>>> NIOEcho   			http://rox-xmlrpc.sourceforge.net/niotut/index.html#The%20code

>>>> MultiChat				https://code.google.com/p/multithread-chat-server/

>>>> ADEN     				https://sourceforge.net/projects/adenproject

>>>> Raining Sockets       http://freshmeat.sourceforge.net/projects/jniosocket 

>>>> OpenChord 			https://sourceforge.net/projects/open-chord/files/Open%20Chord%201.0/

>>>> Thrift	  			http://archive.apache.org/dist/thrift/

>>>> xSocket	  			https://mvnrepository.com/artifact/org.xsocket/xSocket

>>>> ZooKeeper 			https://github.com/apache/zookeeper/releases

>>>> RocketMQ				https://rocketmq.apache.org/

>>>> Voldemort 			https://github.com/apache/zookeeper/releases

>>>> Netty	  				https://bintray.com/netty/downloads/netty/

>>>> HSQLDB    			http://hsqldb.org/

#### Dynamic information flow analysis using FLOWDIST (and its alternative designs)

	1. Select one subject

>>>> NioEcho is a simple system whose server echoes any message from the clients. 

>>>> MultiChat is a chat system whose clients broadcast messages to all other clients through the server. 

>>>> ADEN offers UDP-based alternative to TCP sockets.

>>>> Raining Sockets is a non-blocking and sockets-based framework.

>>>> OpenChord uses distributed hash tables to provide peer-to-peer network services. 

>>>> Thrift is an application development framework that has a code generation engine for developing scalable cross-language services. For our illustrtion, we have installed Thrift (0.11.0).

>>>> xSocket is a framework based on non-blocking IO (NIO) for constructing high-performance, scalable software systems. 

>>>> ZooKeeper is a coordination system providing distributed synchronization and group services. 

>>>> RocketMQ is a distributed messaging platform.

>>>> Voldemort is a distributed key-value storage system used by LinkedIn.

>>>> Netty is an asynchronous NIO client-server framework used to rapidly develop server/client network applications. 

>>>> HSQLDB (HyperSQL DataBase) is a SQL relational database.		 
			 
	2. Use FLOWDIST (default design) to compute statement-level information flow paths 	
	
-  2.1	 Phase 1: Pre-analysis:

>>>> 2.1.1  Static analysis & instrumentation:

>>>>We use the exisiting source and sink files of FLOWDIST. For example, we copy the subdirectory "data"  of FLOWDIST including `Sources.txt` and `Sinks.txt` files to the subject directory (e.g., thrift) defined by the user. Then, we execute code/shell/#subject#/`OTBetterSourceSink.sh`, `OTBetterMethods.sh`, `OTBetterInstr.sh`, and `OTBranchInstr.sh` in sequence. For example, we execute code/shell/Thrift/`OTBetterSourceSink.sh`, `OTBetterMethods.sh`, `OTBetterInstr.sh`, and `OTBranchInstr.sh` one by one.

>>>> 2.1.2  Tracing: 						
	
>>>>We start server and client instances of the instrumented program. For example, for a Thrift integration test, we separately execute `./serverOT.sh` and `./clientOT.sh` to start a server and a client of the instrumented Thrift application (i.e., a calculator). The client sent some basic arithmetic operations (addition, subtraction, multiplication, and division of two numbers, in order) to the server and got the calculation results from the server.

>>>> 2.1.3  Method-level analysis:
    
>>>>We execute program/shell/#subject#/`OTAnalysisAll.sh` to compute method-level information flow paths as the output of Phase 1. For example, we execute program/shell/Thrift/`OTAnalysisAll.sh` to compute the method-level information flow paths of the Thrift application execution.

-  2.2	 Phase 2: Refinement:  	   
	
>>>> 2.2.1  Static analysis & 2.2.2  Coverage analysis:	
    
>>>>We execute program/shell/#subject#/`OT3Instr.sh` for the static analysis and coverage analysis of Phase 2. For example, we execute program/shell/Thrift/`OT3Instr.sh` for the static analysis and computing relevant statement coverage of the Thrift application execution.
 	
>>>> 2.2.3  Statement-level analysis:
    
>>>>We execute program/shell/#subject#/`OT3AnalysisAll.sh` to compute statement-level information flow paths as the final output of FLOWDIST. For example, we execute program/shell/Thrift/`OT3AnalysisAll.sh` to compute the statement-level information flow paths of the Thrift application execution.

	3. (Optional) Use FLOWDIST<sub>sim</sub> to compute statement-level information flow paths
	
-  3.1	 Phase 1: Pre-analysis:

>>>> 3.1.1  Static analysis & instrumentation:

>>>>We use the exisiting source and sink files of FLOWDIST. For example, we copy the subdirectory "data"  of FLOWDIST including `Sources.txt` and `Sinks.txt` files to the subject directory (e.g., Thrift) defined by the user. Then, we execute code/shell/#subject#/`OTBetterSourceSink.sh`, `OTBetterInstr.sh`, and `OTBranchInstr.sh` in sequence. For example, we execute code/shell/Thrift/`OTBetterSourceSink.sh`, `OTBetterInstr.sh`, and `OTBranchInstr.sh` one by one.

>>>> 3.1.2  Tracing: 						
	
>>>>We start server and client instances of the instrumented program. For example, for a Thrift integration test, we separately execute `./serverOT.sh` and `./clientOT.sh` to start a server and a client of the instrumented Thrift application (i.e., a calculator). The client sent some basic arithmetic operations (addition, subtraction, multiplication, and division of two numbers, in order) to the server and got the calculation results from the server.

>>>> 3.1.3  Method-level analysis:
    
>>>>We execute program/shell/#subject#/`OTAnalysisAll.sh` to compute method-level information flow paths as the output of Phase 1. For example, we execute program/shell/Thrift/`OTAnalysisAll.sh` to compute the method-level information flow paths of the Thrift application execution.

-  3.2	 Phase 2: Refinement:  	   
	
>>>> 3.2.1  Static analysis & 3.2.2  Coverage analysis:	
    
>>>>We execute program/shell/#subject#/`OT3Instr.sh` for the static analysis and coverage analysis of Phase 2. For example, we execute program/shell/Thrift/`OT3Instr.sh` for the static analysis and computing relevant statement coverage of the Thrift application execution.
 	
>>>> 3.2.3  Statement-level analysis:
    
>>>>We execute program/shell/#subject#/`OT3AnalysisAll.sh` to compute statement-level information flow paths as the final output of <sub>sim</sub>. For example, we execute program/shell/Thrift/`OT3AnalysisAll.sh` to compute the statement-level information flow paths of the Thrift application execution.

	4. (Optional) Use FLOWDIST<sub>mul</sub> to compute statement-level information flow paths
	
-  4.1	 Phase 1: Pre-analysis:

>>>> 4.1.1  Instrumentation:

>>>>We use the exisiting source and sink files. For example, we copy the subdirectory "data"  of FLOWDIST including `Sources.txt` and `Sinks.txt` files to the subject directory (e.g., Thrift) defined by the user. Then, we execute code/shell/#subject#/`DTSourceSink.sh` and `DTInstr.sh`. For example, we execute code/shell/Thrift/`DTSourceSink.sh` and `DTInstr.sh`.

>>>> 4.1.2  Tracing: 						
	
>>>>We start server and client instances of the instrumented program to record executed method events. For example, for a Thrift integration test, we separately execute `./serverDT.sh` and `./clientDT.sh` to start a server and a client of the instrumented Thrift application (i.e., a calculator). The client sent some basic arithmetic operations (addition, subtraction, multiplication, and division of two numbers, in order) to the server and got the calculation results from the server.

>>>> 4.1.3  Method-level analysis:
    
>>>>We execute program/shell/#subject#/`DTAnalysisAll.sh` to compute method-level information flow paths as the output of Phase 1. For example, we execute program/shell/Thrift/`DTAnalysisAll.sh` to compute the method-level information flow paths of the Thrift application execution.


-  4.2	 Phase 2: Coverage-analysis:

>>>> 4.2.1  Instrumentation:

>>>>We execute program/shell/#subject#/`DT2BranchPre.sh` for the static analysis and coverage analysis of Phase 2.

>>>> 4.2.2  Tracing: 						
	
>>>>We start server and client instances of the instrumented program. After the execution, the statements in covered branches are recorded. For example, for a Thrift integration test, we separately execute `./server2BrPre.sh` and `./client2BrPre.sh` to start a server and a client of the instrumented Thrift application (i.e., a calculator). The client sent some basic arithmetic operations (addition, subtraction, multiplication, and division of two numbers, in order) to the server and got the calculation results from the server.

-  4.3	 Phase 3: Refinement:  	   
	
>>>> 4.3.1  Instrumentation:
    
>>>>We execute program/shell/#subject#/`DT2Instr.sh` to instrument the program. For example, we execute program/shell/Thrift/`DT2Instr.sh` to instrument Thrift and its application.

>>>> 4.3.2  Tracing:

>>>>We start server and client instances of the instrumented program to record instance-level method-execution events. For example, for a Thrift integration test, we separately execute `./serverDT2.sh` and `./clientDT2.sh` to start a server and a client of the instrumented Thrift application (i.e., a calculator). The client sent some basic arithmetic operations (addition, subtraction, multiplication, and division of two numbers, in order) to the server and got the calculation results from the server.
 	
>>>> 4.3.3  Statement-level analysis:
    
>>>>We execute program/shell/#subject#/`DT2AnalysisAll.sh` to compute statement-level information flow paths as the final output of <sub>mul</sub>. For example, we execute program/shell/Thrift/`DT2AnalysisAll.sh` to compute the statement-level information flow paths of the Thrift application execution.


-----------
### Quick Start 
-----------
Using one subject system Thrift as an example, the procedure of gettting started with FlowDist quickly is the following:  
The procedure is described in README.md, as follows:

Step 1: We first download and install FLOWDIST.

Step 2: We download and install one example subject such as Apache Thrift, following "Dynamic information flow analysis using FLOWDIST (and its alternative designs)" in README.md.

Step 3: We copy all library files from the directory ”tool” of FLOWDIST to a directory (e.g., /home/xqfu or /home/xqfu/lib) defined by the user.

Step 4: We also need to copy the subdirectory "data" including 'Sources.txt' and 'Sinks.txt' files and the newest shell scripts from the directory "code/shell/Thrift" to the subject directory (e.g., /home/xqfu/thrift).

Step 5: We update the directory names (e.g. /home/xqfu) in the shell scripts.

Step 6: If we select the default design of FLOWDIST, we follow the phases (Phase 1: Pre-analysis and Phase 2: Refinement, and ) of "2. Use FLOWDIST (default design) to compute statement-level information flow paths" in the README.md.

Step 7: Otherwise, we follow the phases of "3." and/or "4.".

After the procedure, we would get raw information flow paths, as listed in the directory "RawPaths". From these paths, we could find relevant vulnerabilities.



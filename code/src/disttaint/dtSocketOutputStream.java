/**
 * File: src/distEA/distdistSocketOutputStream.java
 * -------------------------------------------------------------------------------------------
 * Date			Author      	Changes
 * -------------------------------------------------------------------------------------------
 * 02/11/15		hcai			Created; for intercepting socket outputs
*/
package disttaint;

import java.io.*;

import disttaint.dtMonitor.logicClock;
//import distEA.distThreadMonitor.logicClock;

/* customized Socket OutputStream where extraneous operations, for retrieving logic clocks, are added
 */
/** for replacing socket output stream objects */
public class dtSocketOutputStream extends OutputStream {
	public static void __link() { }
	
	public static boolean intercept = true;
	
	private OutputStream out;
	private logicClock lgclock;

	public dtSocketOutputStream(OutputStream out, logicClock clock) {
		this.out = out;
		this.lgclock = clock;
	}
	public dtSocketOutputStream(OutputStream out) {
		this(out, dtMonitor.g_lgclock);
		//this(out, distThreadMonitor.getCreateClock());
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void write(byte[] b) throws IOException {
		this.write(b, 0, b.length);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (intercept) {
			lgclock.packClock(out, len);
		}
		out.write(b, off, len);
	}

	@Override
	public void write(int b) throws IOException {
		if (intercept) {
			lgclock.packClock(out, 4);
		}
		out.write(b);
	}
}
	
/* vim :set ts=4 tw=4 tws=4 */


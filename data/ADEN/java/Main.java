/*
ADEN https://java.net/projects/networkservice/
Copyright (C) 2013 Mazen Banafa https://MBanafa.blogspot.com

This file is part of ADEN class library.

ADEN is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, 
or (at your option) any later version.

ADEN is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with ADEN. If not, see <http://www.gnu.org/licenses/>.
 */
package adentest;


import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * 
 */
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
   //   Logger logger=Logger.getLogger("logger");
   // logger.setLevel(Level.ALL);
    // System.setProperty("adenlogger", "logger");
        
      
    
       ExServer server=new ExServer(1000,new SessionHandler());
        server.start();
        ExClient c=new ExClient(InetAddress.getLocalHost().getHostAddress(),1000);
        c.start();
        c.join();
        server.close();
        server.join();
      //  System.exit(0);
    }
}

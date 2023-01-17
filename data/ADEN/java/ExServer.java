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

import com.aden.AdenSocket;
import com.aden.AdenUnicastServerSocket;
import com.aden.exception.AdenTimeoutException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * 
 */
public class ExServer extends Thread {

    private AdenUnicastServerSocket server;
    private static int MAX_SERVICE_THREADS = 20;
    private static long IDLE_TIME = 60L;
    ExecutorService worker = new ThreadPoolExecutor(0, MAX_SERVICE_THREADS,
            IDLE_TIME, java.util.concurrent.TimeUnit.SECONDS,
            new java.util.concurrent.SynchronousQueue<Runnable>(), new ThreadPoolExecutor.AbortPolicy());
    private boolean keepListening = true;
    private static int TIMEOUT = 20000;//20s
    private SessionHandler handler;

    public ExServer(int port, SessionHandler h) throws IOException {
        server = new AdenUnicastServerSocket();
        server.bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
        handler = h;
    }

    @Override
    public void run() {

        try {
            mainLoop();
        } finally {
            server.close();
            worker.shutdown();
        }
    }

    private void mainLoop() {

        while (keepListening) {
            AdenSocket so = null;
            try {
                //block untill a new connection is returned or timeout occurs
                so = server.accept(500);
                
                //dispatch the connection to a separate thread
                worker.execute(new ConnectionHandler(so));
            } catch (AdenTimeoutException ex) {
            } catch (RejectedExecutionException ex) {
                //Server is really busy right now!
                so.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                keepListening = false;
            } catch (InterruptedException ex) {
            }

        }
    }

    private class ConnectionHandler implements Runnable {

        private AdenSocket so;
        private ByteBuffer buffer = ByteBuffer.allocate(1000);
        private SocketAddress addr;
        private final MySession session;

        public ConnectionHandler(AdenSocket so) {
            this.so = so;
            session = new MySession(so);
        }

        public void run() {


            try {
                handler.sessionCreated(session);
                
                /*First thing to do is to receive a connection message
                  (For performance, it is recommended that you do not block). 
                 */
                if (!so.isReadable()) {
                    throw new IOException("no initial message");
                }

                so.read(buffer);
                buffer.flip();
                
                while (!session.isClosed()) {
                    buffer.clear();
                    
                    //do not make the service thread wait forever.
                   int s= so.read(buffer, TIMEOUT);
                   if(s<=0){
                   break;
                   }
                    buffer.flip();
                   
                    handler.messageReceived(session, buffer);
                }
                /*Alternative code:
                 * 
                while (!session.isClosed() && so.isReadable(TIMEOUT)) {
                
                //you can always check the message size before you receive it.
                if(so.getInputMessageSize()>buffer.capacity()){
                buffer=ByteBuffer.allocate(so.getInputMessageSize());
                }
                buffer.clear();
                int s=so.read(buffer);
                if(s<=0){
                  break;
                  }
                buffer.flip();
              
                handler.messageReceived(session, buffer);
                }*/
            } catch (Exception e) {
                handler.exceptionCaught(session, e);
            } finally {
                session.close();
                handler.sessionClosed(session);
            }
        }
    }

    public void close() {
        keepListening = false;

    }
}

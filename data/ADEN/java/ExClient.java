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
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * An example showing a typical ADEN server for request-response
 * conversations. 
 * 
 * Note that besides the actual conversation client need to send two messages 
 * one sent at the bigenning allows us to connect the server and 
 * initiate the session and another sent at the end to terminate the session.
 * 
 * The general policy for the initialization message and termination message 
 * is that each message is small enough to be sent in one packet.
 * The initialization message SHOLUD NOT be used to invoke a non-idempotent 
 * operation on the server. The  initialization message can hold data.
 * The termination message SHOLUD NOT hold data.
 * If sending the last message faild the client can ignore the error and 
 * continue normal execution.
 * 
 * Running a request-response conversation over ADEN requires
 * at least 8-way handshake (that is when request 
 * and response are single packets). 
 * 
 * 
 */
public class ExClient extends Thread {

    ByteBuffer buffer = ByteBuffer.allocate(2000);
    AdenSocket client;
    private String host;
    private int port;

    public ExClient(String host, int port) throws IOException {
        client = new AdenSocket();
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        
        try {
            
       
            System.out.println("client: initiating session");
            /*To send a message to the server, first you must
             initiate the socket with the server address.
             */
            client.open(new InetSocketAddress(InetAddress.getByName(host), port));
        
            buffer.put((byte)1).flip();
            
            /*Then Send a connection message and wait untill it is received.
              its content is not important, it's only used to perform
                 a handshake.*/  
            client.write(buffer, true);
            
            /*Now you can send application messages to the server */
            System.out.println("client: sending message");
            String message = "hello server";
            buffer.clear();
            buffer.put(message.getBytes()).flip();
            
            client.write(buffer);

            System.out.println("client: waiting for reply");
            buffer.clear();
            
            //Receive from server, block only for 20 sec.
            client.read(buffer, 20000);
            buffer.flip();

             System.out.println("client: sending message");
             message = "bye server";
            buffer.clear();
            buffer.put(message.getBytes()).flip();
            client.write(buffer, true);


            System.out.println("client: waiting for reply");
            buffer.clear();
            client.read(buffer, 20000);
            buffer.flip();

            System.out.println("client: closing session");
            buffer.clear();
            buffer.flip();
            /*Send an empty message to tell the server you are done*/
            client.write(buffer, true);


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {

            client.close();


        }


    }
}

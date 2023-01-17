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
import java.nio.ByteBuffer;

/**
 *
 * 
 */
public class SessionHandler {

    public void sessionCreated(MySession session) {
        System.out.println("server: session initiated with " + session.getAddress());
    }

    public void sessionClosed(MySession session) {
        System.out.println("server: session closed with " + session.getAddress());
    }

    public void exceptionCaught(MySession session, Exception e) {
        e.printStackTrace();
    }

    public void messageReceived(MySession session, ByteBuffer msg) throws IOException {
        //handle request here...
        System.out.println("server: processing request from " + session.getAddress());
        session.write(msg);
    }
}

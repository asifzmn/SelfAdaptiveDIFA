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
import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 *
 * 
 */
public class MySession {

    private AdenSocket connection;
    private SocketAddress addr;
    private boolean closed;

    public MySession(AdenSocket connection) {
        this.connection = connection;
        addr = connection.getRemoteSocketAddress();
    }

    public void setProperty(String key, Object value) {
    }

    public Object getProperty(String key) {
        return null;
    }

    public void write(ByteBuffer b) throws IOException {
        connection.write(b);
    }

    public SocketAddress getAddress() {
        return addr;
    }

    public void close() {
        connection.close();
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}

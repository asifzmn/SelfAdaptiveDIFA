/*
 * Copyright 2010 LinkedIn, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package voldemort.store.socket.clientrequest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import edu.kit.joana.ui.annotations.Level;
import edu.kit.joana.ui.annotations.Source;
import voldemort.VoldemortException;
import voldemort.client.protocol.RequestFormatType;
import voldemort.utils.ByteUtils;
import edu.kit.joana.ui.annotations.AnnotationPolicy;
import edu.kit.joana.ui.annotations.Level;
import edu.kit.joana.ui.annotations.Sink;
import edu.kit.joana.ui.annotations.Source;
public class ProtocolNegotiatorClientRequest extends AbstractClientRequest<String> {

    private final RequestFormatType requestFormatType;

    public ProtocolNegotiatorClientRequest(RequestFormatType requestFormatType) {
        this.requestFormatType = requestFormatType;
    }

    public boolean isCompleteResponse(ByteBuffer buffer) {
        return buffer.remaining() == 2;
    }

    @Override
    protected void formatRequestInternal(DataOutputStream outputStream) throws IOException {
        outputStream.write(ByteUtils.getBytes(requestFormatType.getCode(), "UTF-8"));
    }

    @Override
    protected String parseResponseInternal(DataInputStream inputStream) throws IOException {
    	@Source(level = Level.HIGH)
    	byte[] responseBytes = new byte[2];
        inputStream.readFully(responseBytes);
        String result = ByteUtils.getString(responseBytes, "UTF-8");

        if(result.equals("ok"))
            return result;

        if(result.equals("no"))
            throw new VoldemortException(requestFormatType.getDisplayName()
                                         + " is not an acceptable protcol for the server.");
        else
            throw new VoldemortException("Unknown server response: " + result);
    }

}
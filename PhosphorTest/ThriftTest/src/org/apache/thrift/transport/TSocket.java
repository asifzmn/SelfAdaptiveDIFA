/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.thrift.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Socket implementation of the TTransport interface. To be commented soon!
 *
 */
public class TSocket extends TIOStreamTransport {

  private static final Logger LOGGER = LoggerFactory.getLogger(TSocket.class.getName());

  /**
   * Wrapped Socket object
   */
  private Socket socket_ = null;

  /**
   * Remote host
   */
  private String host_  = null;

  /**
   * Remote port
   */
  private int port_ = 0;

  /**
   * Socket timeout
   */
  private int timeout_ = 0;

  /**
   * Constructor that takes an already created socket.
   *
   * @param socket Already created socket object
   * @throws TTransportException if there is an error setting up the streams
   */
  public TSocket(Socket socket) throws TTransportException {
    socket_ = socket;
    try {
      socket_.setSoLinger(false, 0);
      socket_.setTcpNoDelay(true);
      socket_.setKeepAlive(true);
    } catch (SocketException sx) {
      LOGGER.warn("Could not configure socket.", sx);
    }

    if (isOpen()) {
      try {
        inputStream_ = new BufferedInputStream(socket_.getInputStream(), 1024);
        outputStream_ = new BufferedOutputStream(socket_.getOutputStream(), 1024);
      } catch (IOException iox) {
        close();
        throw new TTransportException(TTransportException.NOT_OPEN, iox);
      }
    }
  }
  public TSocket() {
	  this("127,0,0,1", 2020, 0);
	  }
  /**
   * Creates a new unconnected socket that will connect to the given host
   * on the given port.
   *
   * @param host Remote host
   * @param port Remote port
   */
  public TSocket(String host, int port) {
    this(host, port, 0);
  }

  /**
   * Creates a new unconnected socket that will connect to the given host
   * on the given port.
   *
   * @param host    Remote host
   * @param port    Remote port
   * @param timeout Socket timeout
   */
  public TSocket(String host, int port, int timeout) {
    host_ = host;
    port_ = port;
    timeout_ = timeout;
    initSocket();
  }

  /**
   * Initializes the socket object
   */
  private void initSocket() {
    socket_ = new Socket();
    try {
      socket_.setSoLinger(false, 0);
      socket_.setTcpNoDelay(true);
      socket_.setKeepAlive(true);
      socket_.setSoTimeout(timeout_);
    } catch (SocketException sx) {
      LOGGER.error("Could not configure socket.", sx);
    }
  }

  /**
   * Sets the socket timeout
   *
   * @param timeout Milliseconds timeout
   */
  public void setTimeout(int timeout) {
    timeout_ = timeout;
    try {
      socket_.setSoTimeout(timeout);
    } catch (SocketException sx) {
      LOGGER.warn("Could not set socket timeout.", sx);
    }
  }

  /**
   * Returns a reference to the underlying socket.
   */
  public Socket getSocket() {
    if (socket_ == null) {
      initSocket();
    }
    return socket_;
  }

  /**
   * Checks whether the socket is connected.
   */
  public boolean isOpen() {
    if (socket_ == null) {
      return false;
    }
    return socket_.isConnected();
  }

  /**
   * Connects the socket, creating a new socket object if necessary.
   */
  public void open() throws TTransportException {
    if (isOpen()) {
      throw new TTransportException(TTransportException.ALREADY_OPEN, "Socket already connected.");
    }

    if (host_.length() == 0) {
      throw new TTransportException(TTransportException.NOT_OPEN, "Cannot open null host.");
    }
    if (port_ <= 0) {
      throw new TTransportException(TTransportException.NOT_OPEN, "Cannot open without port.");
    }

    if (socket_ == null) {
      initSocket();
    }

    try {
      socket_.connect(new InetSocketAddress(host_, port_), timeout_);
      inputStream_ = new BufferedInputStream(socket_.getInputStream(), 1024);
      outputStream_ = new BufferedOutputStream(socket_.getOutputStream(), 1024);
    } catch (IOException iox) {
      close();
      throw new TTransportException(TTransportException.NOT_OPEN, iox);
    }
  }

  /**
   * Closes the socket.
   */
  public void close() {
    // Close the underlying streams
    super.close();

    // Close the socket
    if (socket_ != null) {
      try {
        socket_.close();
      } catch (IOException iox) {
        LOGGER.warn("Could not close socket.", iox);
      }
      socket_ = null;
    }
  }

}

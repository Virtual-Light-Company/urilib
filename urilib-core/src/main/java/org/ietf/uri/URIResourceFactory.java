/*
 * Copyright (c) 1999 - 2012 The Virtual Light Company
 *                            http://www.vlc.com.au/
 *
 * This code is licensed under the GNU Library GPL v2.1. Please read docs/LICENSE.txt
 * for the full details. A copy of the LGPL may be found at
 *
 * http://www.gnu.org/copyleft/lgpl.html
 *
 * The code is distributed as-is and contains no warranty or guarantee for fitnesse of
 * purpose. Use it at your own risk.
 */

package org.ietf.uri;

import java.io.IOException;

/**
 * Factory interface for accessing a resource connection from the system.
 * <P>
 *
 * The idea is to present a consistent interface to any kind of resolver for
 * establishing a connection to the given type of resource. All that the
 * resolver needs to know is the host, port and type of protocol required and
 * request the system to look after it.
 * <P>
 *
 * The resource factory cannot be implemented by external third parties. It is
 * a mechanism for presenting a consistent interface to resolvers. The one and
 * only implementation of this factory is internal to the package.
 * <P>
 *
 * While this may be ignored by the resolver, for caching and efficiency
 * purposes, it should be used at all times. The hard work of locating protocol
 * handlers and content translators is all performed within the implementation.
 * <P>
 *
 * For details on URIs see the IETF working group:
 * <A HREF="http://www.ietf.org/html.charters/urn-charter.html">URN</A>
 * <P>
 *
 * This softare is released under the
 * <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <P>
 *
 * DISCLAIMER:<BR>
 * This software is the under development, incomplete, and is
 * known to contain bugs. This software is made available for
 * review purposes only. Do not rely on this software for
 * production-quality applications or for mission-critical
 * applications.
 * <P>
 *
 * Portions of the APIs for some new features have not
 * been finalized and APIs may change. Some features are
 * not fully implemented in this release. Use at your own risk.
 * <P>
 *
 * @author  Justin Couch
 * @version 0.7 (27 August 1999)
 */
public interface URIResourceFactory
{
  /**
   * Find the connection for a particular resource based on the protocol and
   * destination. If no resource connection can be made for the given protocol
   * type then null is returned. The resource connection is the raw connection,
   * at this stage, no physical connection has been made until the
   * <CODE>connect()</CODE> method is called on the connection.
   * <P>
   *
   * A host and protocol must always be specified. If the port number is -1
   * then the default port for that protocol will be used.
   * <P>
   *
   * The path parameter is a string encoding of whatever path is needed to
   * access the object using the nominated protocol. The values should be
   * taken from the {@link org.ietf.uri.URIConstants} class.
   *
   * @param protocol The protocol type eg "http" or "ftp"
   * @param host The name of the host to contact
   * @param port The port number or -1 for the default
   * @param path The path needed to access the resource using the given protocol
   * @return The connection or null if it cannot find one
   * @exception IllegalArgumentException One of the params were not specified
   * @exception UnknownProtocolException No handlers for the protocol type
   * @exception IOException Some I/O error during connect
   */
  public ResourceConnection requestResource(String protocol,
                                            String host,
                                            int port,
                                            String path)
    throws UnknownProtocolException, IllegalArgumentException, IOException;
}

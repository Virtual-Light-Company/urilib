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
 * Representation of a low level handler for the stream information coming from
 * a network.
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
public abstract class URIResourceStream
{

  /**
   * Create an instance of this resource handler
   */
  public URIResourceStream()
  {
  }

  /**
   * Open a connection for the given URI. The port number must always be a
   * valid value. It is up to the implementing class to ensure that the correct
   * default port is supplied.
   *
   * @param host The host name to connect to
   * @param port The port on the host
   * @param path The path needed to access the resource using the given protocol
   * @exception IOException I/O errors reading from the stream
   * @exception IllegalArgumentException host, port or URI were invalid
   */
  protected abstract ResourceConnection openConnection(String host,
                                                       int port,
                                                       String path)
    throws IOException, IllegalArgumentException;
}

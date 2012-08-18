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

import java.io.*;
import java.net.*;

/**
 * Creates <code>ResourceConnection</code> objects that wrap instances of
 * {@link java.net.URLConnection} in order to enabled compatibility with
 * handlers written for the Java API.
 * <p>
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
 * @author Andrzej Kapolka
 * @version 0.9
 */
class URLConnectionWrapperResourceStream extends URIResourceStream
{
    /** The protocol name. */
    private String protocol;


    /**
     * Construct a new wrapper for the protocol name
     *
     * @param protocol the protocol name
     */
    URLConnectionWrapperResourceStream(String protocol)
    {
        this.protocol = protocol;
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
    protected ResourceConnection openConnection(String host, int port, String path)
        throws IOException, IllegalArgumentException
    {
        try
        {
            java.net.URL url = new java.net.URL(protocol, host, port, path);
            org.ietf.uri.URI uri = new org.ietf.uri.URL(protocol, host, port, path);

            return new URLConnectionWrapper(url.openConnection(), uri);
        }
        catch(MalformedURLException mue)
        {
            throw new IllegalArgumentException(mue.toString());
        }
    }
}

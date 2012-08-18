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

package vlc.net.protocol.shttp;

import java.io.IOException;
import java.net.MalformedURLException;

import org.ietf.uri.ResourceConnection;
import org.ietf.uri.URIResourceStream;

/**
 * A http protocol handler.
 * <p/>
 * This implementation usess the Innovation HTTPClient code that is also released under the LGPL.
 * The original can be found at <A HREF="http://www.innovation.ch/java/HTTPClient/">
 * http://www.innovation.ch/java/HTTPClient/</A>.
 * <p/>
 * <p/>
 * The path for this is the directory structure as well as any query component that should be passed
 * to the HTTP server.
 * <p/>
 * For details on URIs see the IETF working group: <A HREF="http://www.ietf.org/html.charters/urn-charter.html">URN</A>
 * <p/>
 * <p/>
 * This softare is released under the <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <p/>
 * <p/>
 * DISCLAIMER:<BR> This software is the under development, incomplete, and is known to contain bugs.
 * This software is made available for review purposes only. Do not rely on this software for
 * production-quality applications or for mission-critical applications.
 * <p/>
 * <p/>
 * Portions of the APIs for some new features have not been finalized and APIs may change. Some
 * features are not fully implemented in this release. Use at your own risk.
 * <p/>
 *
 * @author Justin Couch
 * @version 0.7 (27 August 1999)
 */
public class Handler extends URIResourceStream
{
    /** Create a new instance of the handler. */
    public Handler()
    {
    }

    /**
     * Open a connection for the given URI. The host and port arguments for this stream type are
     * ignored. If a host is needed for a UNC name then that is included in the path.
     *
     * @param host The host name to connect to
     * @param port The port on the host
     * @param path The path needed to access the resource using the given protocol
     * @throws IOException I/O errors reading from the stream
     * @throws IllegalArgumentException host, port or URI were invalid
     */
    protected ResourceConnection openConnection(String host,
                                                int port,
                                                String path)
        throws IOException, IllegalArgumentException
    {
        ResourceConnection res = null;

        try
        {
            res = new SHttpConnection(host, port, path);
        }
        catch(MalformedURLException mue)
        {
            throw new IllegalArgumentException("Cannot construct host connection");
        }

        return res;
    }
}


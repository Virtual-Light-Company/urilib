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

package vlc.net.resolve.thttp;

// Standard imports

import java.io.*;

import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.HashMap;

// Application specific imports
import org.ietf.uri.*;

/**
 * Descriptor class for each server and the path and URL paths
 * <p/>
 * <p/>
 * The structures here are designed to match up with the requirements of the URIResourceFactory
 * class. No more, no less. The protocol is always implied to be HTTP anyway.
 * <p/>
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
class ResourceDescriptor
{
    /** The string name of the server */
    String server;

    /** The port on the server. Default value is -1 if it needs to be set */
    int port = -1;

    /** The full path including any query parts to the resource */
    StringBuffer path;

    /** The length of the path buffer that it should be reset to after use */
    int pathLength = 0;

    /**
     * String representation of the values held by this class
     *
     * @return A formatted string representation
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer("ResourceDescriptor:");
        buffer.append("\n  Server: ");
        buffer.append(server);
        buffer.append("\n  Port: ");
        buffer.append(port);
        buffer.append("\n  Path: ");
        buffer.append(path);

        return buffer.toString();
    }
}

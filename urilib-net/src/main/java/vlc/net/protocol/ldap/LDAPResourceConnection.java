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

package vlc.net.protocol.ldap;

// Standard imports

import javax.naming.*;
import javax.naming.directory.DirContext;
import javax.naming.ldap.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownServiceException;
import java.util.Hashtable;

// Application specific imports
import org.ietf.uri.URL;
import org.ietf.uri.URIConstants;
import org.ietf.uri.ResourceConnection;

/**
 * Representation of a LDAP resource.
 * <p/>
 * <p/>
 * An resource usually represents a particular distinguished name or the attribute value of the
 * system. The current implementation does not know how to deal with an attribute lookup :(
 * <p/>
 * <p/>
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
public class LDAPResourceConnection extends ResourceConnection
{
    /** The initial directory context that we are working in */
    private DirContext dir_context;

    /** How much data is to be read from the stream */
    private int content_length;

    /**
     * Create an instance of this connection to the named LDAP database
     *
     * @param host The name of the host to connect to
     * @param port The port number on the host to use or -1 for the default
     * @param path The distinguished name part of the path
     * @throws MalformedURLException We stuffed up something in the filename
     */
    protected LDAPResourceConnection(String host, int port, String path)
        throws MalformedURLException
    {
        super(new URL(URIConstants.LDAP_SCHEME, host, port, path));

        int real_port = (port == -1) ? URIConstants.LDAP_PORT : port;

        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + host + ':' + real_port);
    }

    /**
     * Get the input stream for this. Throws an UnknownServiceExeception if there is no stream
     * available.
     *
     * @return The stream
     */
    public InputStream getInputStream()
        throws IOException
    {
        throw new UnknownServiceException("protocol doesn't support input");
    }

    /**
     * Get the content type of the resource that this stream points to.
     *
     * @return The content type of this resource
     */
    public String getContentType()
    {
        return null;
    }

    /**
     * Connect to the named resource if not already connected. This is ignored by this
     * implementation.
     *
     * @throws IOException An error occurred during the connection process
     */
    public void connect()
        throws IOException
    {
    }

    /**
     * Get the length of the content that is to follow on the stream. If the length is unknown then -1
     * is returned. The content length could be the the length of the raw stream or the object. Don't
     * know yet????
     *
     * @return The length of the content in bytes or -1
     */
    public int getContentLength()
    {
        try
        {
            getInputStream();
        }
        catch(IOException ioe)
        {
            content_length = -1;
        }

        // we could do some intelligent guessing here by looking to see if the
        // getContent returns an array and then return that??

        return content_length;
    }

    /**
     * Fetch the nth header field. Returns the full length field - name and value with the '='
     * included.
     *
     * @param n The index of the header field
     * @return The value of header or null if the number is out of bounds
     */
    public String getHeaderField(int n)
    {
        String ret_val = null;

        return ret_val;
    }

    /**
     * Fetch a header field with the given string name. The name is treated using capitalisation. The
     * return value is just the value defined for that header, not the entire string.
     *
     * @param name The name of the header to fetch
     * @return The value of the header or null if not defined
     */
    public String getHeaderField(String name)
    {
        return null;
    }

/**
 *  public static void main(String[] args)
 *  {
 *    String[] paths = {
 *      ",A%20brief%20note",
 *      ";base64,A%20brief%20note",
 *      ";charset=iso-8649,A%20brief%20note",
 *      ";charset=iso-8649;base64,A%20brief%20note",
 *      ";charset=iso-8649;param=test1,A%20brief%20note",
 *      ";charset=iso-8649;param=test1;base64,A%20brief%20note",
 *    };
 *
 *    for(int i = 0; i < paths.length; i++)
 *    {
 *      try
 *      {
 *        System.out.println("Checking " + paths[i]);
 *        DataResourceConnection resc = new DataResourceConnection(paths[i]);
 *        System.out.println("  Content encoding is " + resc.getContentEncoding());
 *        System.out.println("  Content type is " + resc.getContentType());
 *        System.out.println("  Param field is " + resc.getHeaderField("param"));
 *        System.out.println("  Header field 2 is " + resc.getHeaderField(1));
 *        System.out.println("  Content is \"" + resc.getContent() + "\"");
 *      }
 *      catch(IOException ioe)
 *      {
 *        System.out.println(ioe);
 *      }
 *    }
 *  }
 **/
}

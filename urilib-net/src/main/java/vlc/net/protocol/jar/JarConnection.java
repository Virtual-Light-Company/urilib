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

package vlc.net.protocol.jar;

import java.io.*;

import java.util.jar.JarFile;
import java.net.UnknownServiceException;
import java.net.MalformedURLException;

import org.ietf.uri.*;

/**
 * Implementation of a JAR resource connection.
 * <p/>
 * <p/>
 * Allows the search and retrieval of the contents of the jar file. Jar files do not support output
 * streams.
 * <p/>
 * <p/>
 * There is an interesting trade off in this situation about when the connection should really take
 * place. There are two steps in the process: the connection to the real underlying JAR file, and
 * the connection to the entry in the JAR file (if you need to). Therefore, every time you want to
 * fetch a JAR file from a JAR URL, there are going to be two instances of this class - one to the
 * real jar file, and then one to the resource representing the JAR URL.
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
public class JarConnection extends JarResourceConnection
{
    /** The input stream to the file. Not created until requested */
    private InputStream input_stream = null;

    /** The content type of the file */
    private String content_type = null;

    /**
     * Create an instance of this connection.
     *
     * @param location The URI to establish the connection to
     * @param path The path of a sub object to fetch from the file
     * @throws MalformedURLException We stuffed up something in the filename
     */
    protected JarConnection(URI location, String path)
        throws MalformedURLException
    {
        super(location, path);
    }

    /**
     * Request the derived class to provide us with the parent JarFile that this resource references.
     * When this is called, it should automatically connect to the source and retrieve the file. Note
     * that caching and other techniques are permitted. The underlying methods assume that this file
     * is accessible and makes no checks for connection state.
     *
     * @return The jar file pointed to by the location
     * @throws IOException There was an error reading the file
     */
    protected JarFile getJarFile()
        throws IOException
    {
        if(!connected)
            connect();

        String content_type = jarFileResource.getContentType();

        if(!content_type.equals(JAR_CONTENT_TYPE))
            throw new IOException("Resource does not point to a JAR file");

        JarFile ret_val = null;

        try
        {
            ret_val = (JarFile)jarFileResource.getContent();
        }
        catch(ClassCastException cce)
        {
            // hmm.... oh well, ignore it and throw the exception shortly
        }

        // sanity check if the server returns something weird
        if(ret_val == null)
            throw new IOException("Cannot load content of JAR file");

        return ret_val;
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
        if(!connected)
            connect();

        if(input_stream == null)
        {
            // if we have an entry, get the stream to that, otherwise, just
            // return the stream that we've got to the JAR file
            if(jarEntryName != null)
            {
                if(jarEntry == null)
                    getJarEntry();

                input_stream = jarFile.getInputStream(jarEntry);
            }
            else
            {
                input_stream = jarFileResource.getInputStream();
            }
        }

        return input_stream;
    }

    /**
     * Get the content type of the resource that this stream points to. Returns a standard MIME type
     * string. If the content type is not known then <CODE>unknown/unknown</CODE> is returned (the
     * default implementation).
     *
     * @return The content type of this resource
     */
    public String getContentType()
    {
        try
        {
            if(!connected)
                connect();
        }
        catch(IOException ioe)
        {
        }

        if(content_type == null)
        {
            if(jarEntryName != null)
                content_type = findContentType(jarEntryName);
            else
                content_type = JAR_CONTENT_TYPE;
        }

        return content_type;
    }

    /**
     * Connect to the named JAR file if not already connected. This establishes the resource
     * connection to the JAR file and downloads that for this use. It sets the
     * <CODE>jarFileResource</CODE> protected variable in the base class.
     *
     * @throws An error occurred during the connection process
     */
    public void connect()
        throws IOException
    {
        if(connected)
            return;

        // really this should probably get a list of all the resources and attempt
        // every one of them to see if they contain the named entry.
        jarFileResource = jarFileURI.getResource();

        connected = true;
    }
}

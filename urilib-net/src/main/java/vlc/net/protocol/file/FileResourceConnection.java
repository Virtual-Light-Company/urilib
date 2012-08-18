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

package vlc.net.protocol.file;

import java.io.*;
import java.net.UnknownServiceException;
import java.net.MalformedURLException;
import java.net.URLDecoder;

import org.ietf.uri.URL;
import org.ietf.uri.URIUtils;
import org.ietf.uri.ResourceConnection;

/**
 * Representation of a file resource.
 * <p/>
 * <p/>
 * Implements the connection as a standard file input stream. Files do not have header information,
 * so the default return values are left as is.
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
public class FileResourceConnection extends ResourceConnection
{
    /** The pathname of the file */
    private File target_file;

    /** The input stream to the file. Not created until requested */
    private InputStream input_stream;

    /** The output stream from the file. Not created until requested */
    private FileOutputStream output_stream;

    /** The content type of the file */
    private String content_type;

    /** The path portion of the URI */
    protected String path;

    /** The query portion of the URI (typically null for files) */
    protected String query;

    /** The reference portion of the URI */
    protected String reference;

    /**
     * Create an instance of this connection.
     *
     * @param uri The URI to establish the connection to
     * @throws MalformedURLException We stuffed up something in the filename
     */
    protected FileResourceConnection(String uri)
        throws MalformedURLException
    {
        super(new URL("file://" + uri));

        // strip the query part from path to get the needed bits.

        try
        {
            String[] stripped_file = URIUtils.stripFile(URLDecoder.decode(uri, "UTF-8"));

            this.path = stripped_file[0];
            query = stripped_file[1];
            reference = stripped_file[2];
        }
        catch(UnsupportedEncodingException uee)
        {
            throw new MalformedURLException("Invalid Encoding. Not UTF-8");
        }

        // quick correction for win32 boxen if needed
        boolean is_win32 = System.getProperty("os.name").startsWith("Win") &&
            System.getProperty("os.arch").equals("x86");

        if(is_win32)
        {
            path = path.replace('|', ':');
            if(path.charAt(0) == '/')
                path = path.substring(1);
        }

        target_file = new File(path);
    }

    /**
     * Get the input stream for this. Throws an UnknownServiceExeception if there is no stream
     * available.
     *
     * @return The unbuffered stream to the file
     */
    public InputStream getInputStream()
        throws IOException
    {
        if(input_stream == null)
            input_stream = new FileInputStream(target_file);

        return input_stream;
    }

    /**
     * Get the output stream for this. Throws an UnknownServiceExeception if there is no stream
     * available.
     *
     * @return The unbuffered stream to the file
     */
    public OutputStream getOutputStream()
        throws IOException
    {
        if(output_stream == null)
        {
            output_stream = new FileOutputStream(target_file);
        }

        return output_stream;
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
        if(content_type == null)
        {
            content_type = findContentType(target_file.getName());
        }

        return content_type;
    }

    /**
     * Connect to the named resource if not already connected. If the file does not exist, a
     * FileNotFoundException is thrown.
     *
     * @throws IOException Could not find the named file.
     */
    public void connect()
        throws IOException
    {
        if(!target_file.exists())
            throw new FileNotFoundException("The file " + path + " does not exist");

        notifyConnectionEstablished("File \'" + path + "\' ready");
    }

    /**
     * Get the length of the content that is to follow on the stream. If the length is unknown then -1
     * is returned. The content length is the length of the file as returned by
     * </CODE>File.length()</CODE>.
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
            return -1;
        }

        return (int)(target_file.length());
    }

    /**
     * Get the time that this object was last modified. This information comes from the
     * <CODE>File.lastModified()</CODE>
     * <p/>
     * The time is in The result is the number of seconds since January 1, 1970 GMT.
     *
     * @return The time or 0 if unknown
     */
    public long getLastModified()
    {
        return target_file.lastModified();
    }
}

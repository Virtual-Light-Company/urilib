/*****************************************************************************
 *                The Virtual Light Company Copyright (c) 1999
 *                               Java Source
 *
 * This code is licensed under the GNU Library GPL. Please read license.txt
 * for the full details. A copy of the LGPL may be found at
 *
 * http://www.gnu.org/copyleft/lgpl.html
 *
 ****************************************************************************/

package org.ietf.uri;

// Standard imports
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;

// Application specific imports
// None

/**
 * Wraps a {@link java.net.URLConnection} in order to enable compatibility
 * with handlers written for the standard Java API.
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
class URLConnectionWrapper extends ResourceConnection
{
    /** The wrapped URL connection. */
    private URLConnection urlConnection;


    /**
     * Construct a new wrapper around the java.net class.
     *
     * @param conn the URL connection to wrap
     * @param uri the URI of the connected resource
     */
    URLConnectionWrapper(URLConnection conn, URI uri)
    {
        super(uri);

        urlConnection = conn;
    }

    /**
     * Get the input stream for this. Throws an UnknownServiceExeception if
     * there is no stream available.
     *
     * @return The stream
     */
    public InputStream getInputStream() throws IOException
    {
        return urlConnection.getInputStream();
    }

    /**
     * Get the output stream for this. Throws an UnknownServiceExeception if
     * there is no stream available.
     *
     * @return The stream
     */
    public OutputStream getOutputStream() throws IOException
    {
        return urlConnection.getOutputStream();
    }

    /**
     * Get the content type of the resource that this stream points to.
     * Returns a standard MIME type string. If the content type is not known then
     * <CODE>unknown/unknown</CODE> is returned (the default implementation).
     * Thie method should be overridden by all implementations.
     *
     * @return The content type of this resource
     */
    public String getContentType()
    {
        return urlConnection.getContentType();
    }

    /**
     * Connect to the named resource if not already connected.
     *
     * @exception An error occurred during the connection process
     */
    public void connect() throws IOException
    {
        urlConnection.connect();
    }

    /**
     * Get the length of the content that is to follow on the stream. If the
     * length is unknown then -1 is returned. This method returns -1 by default
     * and should be overridden by implementing classes.
     *
     * @return The length of the content in bytes or -1
     */
    public int getContentLength()
    {
        return urlConnection.getContentLength();
    }

    /**
     * Get the encoding type of the content. Allows for dealing with
     * multi-lingual content. If it cannot be determined then <CODE>null</CODE>
     * is returned (which is the default implementation).
     *
     * @return The content type
     */
    public String getContentEncoding()
    {
        return urlConnection.getContentEncoding();
    }

    /**
     * Get the nth header field from this connection. If the key name is not
     * understood or does not have a value, null will be returned. This is the
     * default behaviour and should be overrridden.
     *
     * @param n The index of the field to fetch
     * @return The header field value formated as a string
     */
    public String getHeaderField(int n)
    {
        return urlConnection.getHeaderField(n);
    }

    /**
     * Get the named header field from this connection. If the key name is not
     * understood or does not have a value, null will be returned. This is the
     * default behaviour and should be overrridden.
     *
     * @param n The index of the field to fetch
     * @return The header field value formated as a string
     */
    public String getHeaderField(String name)
    {
        return urlConnection.getHeaderField(name);
    }

    /**
     * Get the nth header field from this connection in its raw form as a
     * series of bytes. If the key is not known then the value returned is a
     * zero length array. This is the default behaviour and should be overridden
     * where necessary.
     *
     * @param n The index of the field to fetch
     * @return The header field value formated as a string
     */
    public byte[] getRawHeaderField(int n)
    {
        return getHeaderField(n).getBytes();
    }

    /**
     * Get the named header field from this connection in its raw form as a
     * series of bytes. If the key is not known then the value returned is a
     * zero length array. This is the default behaviour and should be overridden
     * where necessary.
     *
     * @param n The index of the field to fetch
     * @return The header field value formated as a string
     */
    public byte[] getRawHeaderField(String name)
    {
        return getHeaderField(name).getBytes();
    }

    /**
     * Get the key used by the nth header field from this connection. If the key
     * is not known or index is invalid, null is returned. The default
     * implementation always returns null and should be overridden.
     *
     * @param n The index of the key to fetch
     * @return The header field key value formated as a string
     */
    public String getHeaderFieldKey(int n)
    {
        return urlConnection.getHeaderFieldKey(n);
    }

    /**
     * Get the time that this object was last modified. This information may
     * come from protocol headers or any other means
     * <P>
     * The time is in The result is the number of seconds since January 1, 1970
     * GMT. The default implementation returns 0.
     *
     * @return The time or 0 if unknown
     */
    public long getLastModified()
    {
        return urlConnection.getLastModified();
    }
}
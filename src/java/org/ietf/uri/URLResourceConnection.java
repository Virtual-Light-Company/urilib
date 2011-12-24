/*****************************************************************************
 *                The Virtual Light Company Copyright (c) 1999
 *                               Java Source
 *
 * This code is licensed under the GNU Library GPL. Please read license.txt
 * for the full details. A copy of the LGPL may be found at
 *
 * http://www.gnu.org/copyleft/lgpl.html
 *
 * Project:    URI Class libs
 *
 * Version History
 * Date        TR/IWOR  Version  Programmer
 * ----------  -------  -------  ------------------------------------------
 *
 ****************************************************************************/

package org.ietf.uri;

import java.io.*;
import java.util.*;
import java.net.URLConnection;

/**
 * Representation of a connection to a net based resource used as a wrapper
 * for java.net.URLConnections.
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
public class URLResourceConnection extends ResourceConnection
{
  private URLConnection connection;

  /**
   * Create an instance of this connection.
   *
   * @param uri The URI to establish the connection to
   */
  public URLResourceConnection(URLConnection urlConnection)
  {
    super(new URL(urlConnection.getURL()));

    connection = urlConnection;
  }

  /**
   * Package level method to allow access to the real URL connection. Useful
   * for when we are accessing content handlers that are derived from
   * java.net.ContentHandler, rather than our own system.
   */
  URLConnection getConnection()
  {
    return connection;
  }

  /**
   * Get the content type of the resource that this stream points to.
   * Returns a standard MIME type string. If the content type is not known then
   * <CODE>unknown/unknown</CODE> is returned (the default implementation).
   *
   * @return The content type of this resource
   */
  public String getContentType()
  {
    return connection.getContentType();
  }

  /**
   * Connect to the named resource if not already connected.
   *
   * @exception IOException Something happened on the connection.
   */
  public void connect()
    throws IOException
  {
    connection.connect();
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
    return connection.getContentLength();
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
    return connection.getContentEncoding();
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
    return connection.getHeaderField(n);
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
    return connection.getHeaderField(name);
  }
}

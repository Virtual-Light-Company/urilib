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

import java.util.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.UnknownServiceException;
import java.net.URLConnection;
import java.net.MalformedURLException;

/**
 * Representation of a connection to a net based resource used as a wrapper
 * for java.net.URLConnections.
 * <P>
 *
 * This class is used primarily to deal with fooling the standard java.net
 * ContentHandler classes into thinking they have a real URL connection.
 * So, what we do here is form a wrapper class and deal with it that way.
 * <P>
 *
 * There is a potential problem here in that if the URI is a URN then some
 * wierdness could happen. The problem is that URLConnection needs a URL as
 * part of the constructor - otherwise it barfs big time. To get a URL from
 * the URN we call <CODE>getURL</CODE> then turn that into a
 * <CODE>java.net.URL</CODE> to pass to the constructor. If the URN resolves
 * the URL to one destination (I2L) and the resource resides at another (I2R)
 * then we've got serious inconsistencies happening. The only way to fix this
 * is make sure that everything operates under this system not java.net.
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
public class JavaNetURLConnectionWrapper extends URLConnection
{
  private ResourceConnection connection;

  private JavaNetURLConnectionWrapper(java.net.URL url,
                                      ResourceConnection rc)
  {
    super(url);

    connection = rc;
  }

  /**
   * Create an instance of this connection.
   *
   * @param uri The URI to establish the connection to
   * @exception IOException An error occured during the creation
   */
  public static URLConnection createWrapper(ResourceConnection resConnection)
    throws IOException
  {
    URI uri = resConnection.getURI();
    java.net.URL net_url = null;


    try
    {
      String url_str = uri.toExternalForm();
      net_url = new java.net.URL(url_str);
    }
    catch(MalformedURLException mue)
    {
    }

    return new JavaNetURLConnectionWrapper(net_url, resConnection);
  }

  /**
   * Get the input stream for this. Throws an UnknownServiceExeception if
   * there is no stream available.
   *
   * @return The stream
   */
  public InputStream getInputStream()
    throws IOException
  {
    return connection.getInputStream();
  }

  /**
   * Get the output stream for this. Throws an UnknownServiceExeception if
   * there is no stream available.
   *
   * @return The stream
   */
  public OutputStream getOutputStream()
    throws IOException
  {
    return connection.getOutputStream();
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
   * Get the content held by this resource connection. The content type
   * will be the content of the currently available object. For example, it
   * may be possible that a stream will be able to produce more than one
   * object during its lifetime.
   *
   * @return The object read from the stream
   * @exception IOException An error while reading the stream
   */
  public Object getContent()
    throws IOException
  {
    return connection.getContent();
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
  /**
   * Get the key used by the nth header field from this connection. If the key
   * is not known or index is invalid, null is returned. The default
   * implementation always returns null and should be overridden.
   *
   * @param n The index of the field to fetch
   * @return The header field value formated as a string
   */
  public String getHeaderFieldKey(int n)
  {
    return connection.getHeaderFieldKey(n);
  }
}

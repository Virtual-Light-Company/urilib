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

import java.io.IOException;
import java.net.URLConnection;

/**
 * Content handler wrapper implementation for dealing with cases where the only
 * option that is available is {@link java.net.ContentHandler} (and it doesn't
 * extend our own).
 * <P>
 *
 * This implementation does the basic wrapper and event sending used by the
 * content handler to provide events. Unfortunately, because we don't have as
 * much control, sometimes the events seem out of whack with what is really
 * happening - particular handlers that return a shell object and then process
 * the stream from there (ie Sun's image loaders).
 * <P>
 *
 * By default, we set the flag to prevent all streams from being closed by
 * this wrapper. We have no idea about the content, so we take the safe option
 * and let the handler decide for itself what it wants to do.
 * <P>
 *
 * <B>Further Information</B>
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
public final class JavaNetContentHandlerWrapper extends ContentHandler
{
  private java.net.ContentHandler real_handler;

  /**
   * Create an instance of the wrapper. If the handler is null, an exception
   * is thrown.
   *
   * @param handler the java.net.ContentHandler instance
   * @exception NullPointerException The handler instance was null
   */
  public JavaNetContentHandlerWrapper(java.net.ContentHandler handler)
  {
    if(handler == null)
      throw new NullPointerException("No content handler defined");

    real_handler = handler;
    requiresStream = true;
  }

  /**
   * Given a fresh stream from a ResourceConnection, read and create an object
   * instance.
   *
   * @param resc The resource connection to read the data from
   * @return The object read in by the content handler
   * @exception IOException The connection stuffed up.
   */
  public Object getContent(ResourceConnection resc)
    throws IOException
  {
    Object ret_val = null;

    if(resc instanceof URLResourceConnection)
    {
      URLConnection conn = ((URLResourceConnection)resc).getConnection();
      ret_val = real_handler.getContent(conn);
    }
    else
    {
      URLConnection conn = JavaNetURLConnectionWrapper.createWrapper(resc);
      ret_val = real_handler.getContent(conn);
    }

    return ret_val;
  }


  /**
   * Get the content held by this resource connection and the content object
   * returned must match one of the classes provided. No hard guarantee is
   * provided about which class will be chosen to match. The rough algorithm
   * will be to return the content that could be provided by the first content
   * handler that matches any of the classes. The content handler order
   * determination algorithm is found in the package overview documentation.
   * Within an individual content handler, the order used for priority is the
   * order provided in the given array.
   *
   * @param resc The resource connection to read the data from
   * @param classes The list of class types to check for compatibility
   * @return The object read from the stream
   * @exception IOException An error while reading the stream
   */
  public Object getContent(ResourceConnection resc, Class[] classes)
    throws IOException
  {
    Object ret_val = null;

    if(resc instanceof URLResourceConnection)
    {
      URLConnection conn = ((URLResourceConnection)resc).getConnection();
      ret_val = real_handler.getContent(conn, classes);
    }
    else
    {
      URLConnection conn = JavaNetURLConnectionWrapper.createWrapper(resc);
      ret_val = real_handler.getContent(conn, classes);
    }

    return ret_val;
  }

  /**
   * Implementation of the baseclass method that takes the URLConnection,
   * places a ResourceConnection wrapper around it and then calls the
   * other getContent method. You may still override this, but it really
   * isn't a good idea.
   *
   * @param urlc The URL connection to read the data from
   * @return The object read in by the content handler
   * @exception IOException The connection stuffed up.
   */
  public Object getContent(URLConnection urlc)
    throws IOException
  {
    return real_handler.getContent(urlc);
  }

  /**
   * Get the content held by this resource connection and the content object
   * returned must match one of the classes provided. No hard guarantee is
   * provided about which class will be chosen to match. The rough algorithm
   * will be to return the content that could be provided by the first content
   * handler that matches any of the classes. The content handler order
   * determination algorithm is found in the package overview documentation.
   * Within an individual content handler, the order used for priority is the
   * order provided in the given array.
   *
   * @param urlc The URL connection to read the data from
   * @param classes The list of class types to check for compatibility
   * @return The object read from the stream
   * @exception IOException An error while reading the stream
   */
  public Object getContent(URLConnection urlc, Class[] classes)
    throws IOException
  {
    return real_handler.getContent(urlc, classes);
  }
}

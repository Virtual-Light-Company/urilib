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

import org.ietf.uri.event.ProgressEvent;

/**
 * Content handler interface for classes that convert data from a URI resource
 * connection to useful objects.
 * <P>
 *
 * This class extends the basic <CODE>java.net.ContentHandler</CODE> to provide
 * the more generic requirements of the URI classes and their resource
 * handlers.
 * <P>
 *
 * <B>Stream Handling</B>
 * <P>
 *
 * Content handler implementations may need to work with the underlying stream
 * from the content handler sometimes after the resource connection itself has
 * been let go. As part of our aggressive garbage collection position, the
 * ResourceConnection will automatically close any underlying streams it holds
 * open. However, some content handlers, like Sun's AWT image loaders present
 * a fascade that then reads from the stream after we have consumed the
 * content. For these, closing the stream causes an error (in some cases we
 * have even seen it wipe the file from disk!). To deal with this we have a
 * flag that the implementing class can set to allow the use of stream even
 * after the resource connection has left. By default this does not allow the
 * stream to remain open. To override, the implementor should set the flag
 * preferably during the instantiation of the object.
 * <P>
 *
 * <B>Specifying Required Content Types</B>
 * <P>
 *
 * Introduced in JDK 1.3 was the concept of being able to ask for content of
 * specific class types. For example, loading an image, you want an instance
 * of BufferedImage in preference to an instance of Image. This can be
 * provided through one of the getContent methods that takes a list of Class
 * types in order of preference. If the method without this list is used then
 * the class will load whatever it's prefered type is.
 * <P>
 * There is one performance penalty that the standard JDK system uses. It
 * assumes a content handler will support at least one of the known class
 * types before actually asking for content. The result is a handler may get
 * created for a content type it supports, but not for the class type it
 * supports - a waste of CPU cycles. We've added an extra method here to
 * allow fetching the list of supported class types before even asking it to
 * prcess the content. The resource manager can then use this information to
 * rank the use of one content handler over another if they both support the
 * same MIME type, but different lists of content classes.
 * <P>
 *
 * <B>Further Information</B>
 * <P>
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
public abstract class ContentHandler extends java.net.ContentHandler
{
  /** The progress event that is used to send an update out */
  private ProgressUpdateEvent event = new ProgressUpdateEvent();

  /**
   * The flag indicating the stream is required after closing the resource
   * connection's finalise method. The implementing class should set this in
   * the constructor so that it is set for when queried. The default value is
   * <code>false</code>.
   */
  protected boolean requiresStream = false;

  /**
   * Send an update that says the download and handling process has
   * started.
   *
   * @param resc The resource connection that is generating the events
   * @param msg The message to send with the event. May be <CODE>null</CODE>
   */
  protected void notifyDownloadStarted(ResourceConnection resc, String msg)
  {
    event.setValues(resc, ProgressEvent.DOWNLOAD_START, -1, msg);
    resc.sendStartEvent(event);
  }

  /**
   * Update the progress of the content loading. The values should be
   * set according to the constants defined in this class. This method
   * has been synchronized to avoid sending multiple updates at once.
   *
   * @param resc The resource connection that is generating the events
   * @param msg The message to send with the event. May be <CODE>null</CODE>
   * @param val The value of the event.
   */
  protected synchronized void notifyDownloadProgress(ResourceConnection resc,
                                                     int value,
                                                     String msg)
  {
    event.setValues(resc, ProgressEvent.DOWNLOAD_UPDATE, value, msg);
    resc.sendUpdateEvent(event);
  }

  /**
   * Send an update that says the download has received some form of
   * terminal error. The value for the error code may be derived from the
   * {@link org.ietf.uri.event.ProgressEvent} or another error value.
   *
   * @param resc The resource connection that is generating the events
   * @param msg The message to send with the event. May be <CODE>null</CODE>
   * @param val The value of the error.
   */
  protected void notifyDownloadError(ResourceConnection resc,
                                     int value,
                                     String msg)
  {
    event.setValues(resc, ProgressEvent.DOWNLOAD_ERROR, value, msg);
    resc.sendErrorEvent(event);
  }

  /**
   * Send an update that says the download and handling process has
   * finished successfully.
   *
   * @param resc The resource connection that is generating the events
   * @param msg The message to send with the event. May be <CODE>null</CODE>
   */
  protected void notifyDownloadFinished(ResourceConnection resc, String msg)
  {
    event.setValues(resc, ProgressEvent.DOWNLOAD_END, -1, msg);
    resc.sendFinishEvent(event);
  }

  /**
   * Given a fresh stream from a ResourceConnection, read and create an object
   * instance.
   *
   * @param resc The resource connection to read the data from
   * @return The object read in by the content handler
   * @exception IOException The connection stuffed up.
   */
  public abstract Object getContent(ResourceConnection resc)
    throws IOException;

  /**
   * Fetch the content using as the given class type. The default
   * implementation always returns null. If the implementation supports
   * loading different class types, it should override this method to act
   * appropriately
   *
   * @param resc The resource connection to read the data from
   * @param cls The class type to return the content as
   * @return The object read in by the content handler as the given type
   * @exception IOException The connection stuffed up.
   */
  public Object getContent(ResourceConnection resc, Class cls)
    throws IOException
  {
    return null;
  }

  /**
   * Given a fresh stream and resource connection, Load the content in the
   * form of one of the connected class types (in order of preference). The
   * class list should never be null.
   *
   * @param resc The resource connection to read the data from
   * @param classes The list of class types to check for compatibility
   * @return The object read in by the content handler
   * @exception IOException The connection stuffed up.
   */
  public Object getContent(ResourceConnection resc, Class[] classes)
    throws IOException
  {
    // Check the
    Class[] supported_classes = getSupportedClasses();

    Object ret_val = null;
    int i, j;

    // If we don't know what types are supported. Force load everything and
    // see if the returned object matches one of the required types.
    if(supported_classes == null)
    {
      Object obj = getContent(resc);

      for(i = 0; i < classes.length; i++)
      {
        if(classes[i].isInstance(obj))
        {
          ret_val = obj;
          break;
        }
      }
    }
    else
    {
      // Compare class type lists. Check the order type from start to
      // end of the requested class types. This is a yucky 0(n^2)
      // algorithm to find matches.
      for(i = 0; i < classes.length; i++)
      {
        for(j = 0; j < supported_classes.length; j++)
        {
          if(supported_classes[j].isInstance(classes[i]))
          {
            ret_val = getContent(resc, classes[i]);
            break;
          }
        }
      }
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
    ResourceConnection conn = new URLResourceConnection(urlc);

    return getContent(conn);
  }

  /**
   * Implementation of the base class method that asks for content in the
   * given form. Takes the URL connection, adds a wrapper and then calls
   * the getContent() method.
   *
   * @param urlc The URL connection to read the data from
   * @param classes The list of class types to check for compatibility
   * @return The object read in by the content handler
   * @exception IOException The connection stuffed up.
   */
  public Object getContent(URLConnection urlc, Class[] classes)
    throws IOException
  {
    ResourceConnection conn = new URLResourceConnection(urlc);

    return getContent(conn, classes);
  }

  /**
   * Return the list of Class objects that are supported by this
   * implementation. These should be base type classes that you might find
   * in the core libraries such as ImageProducer, RenderedImage etc. If it is
   * unknown what form of objects are returned, the list will be null. Unlike
   * asking for content, this list does not need to be priority ordered.
   *
   * @return The list of supported class types that the content can generate
   */
  public Class[] getSupportedClasses()
  {
    return null;
  }

  /**
   * Check to see if this content handler requires the underyling stream after
   * the object has been returned from the <code>getContent()</code> methods.
   * Some content handlers will present an object that still reads from the
   * stream after they have returned the object. This flag is used to stop the
   * ResourceConnection from closing the stream on this object when it gets
   * garbage collected. The default implementation always returns false.
   *
   * @returns true if the stream is required after the resource connection
   *   closes
   */
  public boolean requiresStreamAfterClose()
  {
    return requiresStream;
  }
}

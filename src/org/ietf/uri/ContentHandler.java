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
}

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

package org.ietf.uri.event;

/**
 * A listener for progress updates with the resource connections.
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
public interface ProgressListener
{
  /**
   * A connection to the resource has been established. At this point, no data
   * has yet been downloaded.
   *
   * @param evt The event that caused this method to be called.
   */
  public void connectionEstablished(ProgressEvent evt);

  /**
   * The header information reading and handshaking is taking place. Reading
   * and intepreting of the data (a download started event) should commence
   * shortly. When that begins, you will be given the appropriate event.
   *
   * @param evt The event that caused this method to be called.
   */
  public void handshakeInProgress(ProgressEvent evt);

  /**
   * The download has started.
   *
   * @param evt The event that caused this method to be called.
   */
  public void downloadStarted(ProgressEvent evt);

  /**
   * The download has updated its status.
   *
   * @param evt The event that caused this method to be called.
   */
  public void downloadUpdate(ProgressEvent evt);

  /**
   * The download has ended.
   *
   * @param evt The event that caused this method to be called.
   */
  public void downloadEnded(ProgressEvent evt);

  /**
   * An error has occurred during the download.
   *
   * @param evt The event that caused this method to be called.
   */
  public void downloadError(ProgressEvent evt);
}

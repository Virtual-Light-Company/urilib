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

package org.ietf.uri.event;

// Standard imports
// none

// Application Specific imports
// none

/**
 * A class which implements efficient and thread-safe multi-cast event
 * dispatching for the events defined in this package.
 * <P>
 *
 * This class will manage an immutable structure consisting of a chain of
 * event listeners and will dispatch events to those listeners.  Because
 * the structure is immutable, it is safe to use this API to add/remove
 * listeners during the process of an event dispatch operation.
 * <P>
 *
 * An example of how this class could be used to implement a new
 * component which fires "action" events:
 *
 * <PRE><CODE>
 * public myComponent extends Component {
 *   ProgressListener progressListener = null;
 *
 *   public void addProgressListener(ProgressListener l) {
 *     progressListener = ProgressListenerMulticaster.add(progressListener, l);
 *   }
 *
 *   public void removeProgressListener(ProgressListener l) {
 *     progressListener = ProgressListenerMulticaster.remove(progressListener, l);
 *   }
 *
 *   public void processEvent(ProgressEvent e) {
 *     // when event occurs which causes "action" semantic
 *     if(progressListener != null) {
 *       progressListener.actionPerformed(new ActionEvent());
 *   }
 * }
 * </CODE></PRE>
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
public class ProgressListenerMulticaster
  implements ProgressListener
{
  private final ProgressListener a, b;

  /**
   * Creates an event multicaster instance which chains listener-a
   * with listener-b. Input parameters <code>a</code> and <code>b</code>
   * should not be <code>null</code>, though implementations may vary in
   * choosing whether or not to throw <code>NullPointerException</code>
   * in that case.
   * @param a listener-a
   * @param b listener-b
   */
  private ProgressListenerMulticaster(ProgressListener a,
                                      ProgressListener b)
  {
    this.a = a;
    this.b = b;
  }

  /**
   * Removes a listener from this multicaster and returns the
   * resulting multicast listener.
   * @param oldl the listener to be removed
   */
  private ProgressListener remove(ProgressListener oldl)
  {
    if(oldl == a)
      return b;

    if(oldl == b)
      return a;

    ProgressListener a2 = removeInternal(a, oldl);
    ProgressListener b2 = removeInternal(b, oldl);

    if (a2 == a && b2 == b)
    {
      return this;  // it's not here
    }

    return addInternal(a2, b2);
  }

  /**
   * Adds input-method-listener-a with input-method-listener-b and
   * returns the resulting multicast listener.
   * @param a input-method-listener-a
   * @param b input-method-listener-b
   */
  public static ProgressListener add(ProgressListener a,
                                     ProgressListener b)
  {
    return (ProgressListener)addInternal(a, b);
  }

  /**
   * Removes the old component-listener from component-listener-l and
   * returns the resulting multicast listener.
   * @param l component-listener-l
   * @param oldl the component-listener being removed
   */
  public static ProgressListener remove(ProgressListener l,
                                           ProgressListener oldl)
  {
    return (ProgressListener)removeInternal(l, oldl);
  }

  /**
   * A connection to the resource has been established. At this point, no data
   * has yet been downloaded.
   *
   * @param evt The event that caused this method to be called.
   */
  public void connectionEstablished(ProgressEvent evt)
  {
    try
    {
      a.connectionEstablished(evt);
    }
    catch(Throwable th)
    {
      System.out.println("Error sending connection event to: " + a);
      th.printStackTrace();
    }

    try
    {
      b.connectionEstablished(evt);
    }
    catch(Throwable th)
    {
      System.out.println("Error sending connection event to: " + b);
      th.printStackTrace();
    }
  }

  /**
   * The header information reading and handshaking is taking place. Reading
   * and intepreting of the data (a download started event) should commence
   * shortly. When that begins, you will be given the appropriate event.
   *
   * @param evt The event that caused this method to be called.
   */
  public void handshakeInProgress(ProgressEvent evt)
  {
    try
    {
      a.handshakeInProgress(evt);
    }
    catch(Throwable th)
    {
      System.out.println("Error sending handshake event to: " + a);
      th.printStackTrace();
    }

    try
    {
      b.handshakeInProgress(evt);
    }
    catch(Throwable th)
    {
      System.out.println("Error sending handshake event to: " + b);
      th.printStackTrace();
    }
  }

  /**
   * The download has started.
   *
   * @param evt The event that caused this method to be called.
   */
  public void downloadStarted(ProgressEvent evt)
  {
    try
    {
      a.downloadStarted(evt);
    }
    catch(Throwable th)
    {
      System.out.println("Error sending download started event to: " + a);
      th.printStackTrace();
    }

    try
    {
      b.downloadStarted(evt);
    }
    catch(Throwable th)
    {
      System.out.println("Error sending download started event to: " + b);
      th.printStackTrace();
    }
  }

  /**
   * The download has updated its status.
   *
   * @param evt The event that caused this method to be called.
   */
  public void downloadUpdate(ProgressEvent evt)
  {
    try
    {
      a.downloadUpdate(evt);
    }
    catch(Throwable th)
    {
      System.out.println("Error sending download update event to: " + a);
      th.printStackTrace();
    }

    try
    {
      b.downloadUpdate(evt);
    }
    catch(Throwable th)
    {
      System.out.println("Error sending download update event to: " + b);
      th.printStackTrace();
    }
  }

  /**
   * The download has ended.
   *
   * @param evt The event that caused this method to be called.
   */
  public void downloadEnded(ProgressEvent evt)
  {
    try
    {
      a.downloadEnded(evt);
    }
    catch(Throwable th)
    {
      System.out.println("Error sending download ended event to: " + a);
      th.printStackTrace();
    }

    try
    {
      b.downloadEnded(evt);
    }
    catch(Throwable th)
    {
      System.out.println("Error sending download ended event to: " + b);
      th.printStackTrace();
    }
  }

  /**
   * An error has occurred during the download.
   *
   * @param evt The event that caused this method to be called.
   */
  public void downloadError(ProgressEvent evt)
  {
    try
    {
      a.downloadError(evt);
    }
    catch(Throwable th)
    {
      System.out.println("Error sending download error event to: " + a);
      th.printStackTrace();
    }

    try
    {
      b.downloadError(evt);
    }
    catch(Throwable th)
    {
      System.out.println("Error sending download error event to: " + b);
      th.printStackTrace();
    }
  }

  /**
   * Returns the resulting multicast listener from adding listener-a
   * and listener-b together.
   * If listener-a is null, it returns listener-b;
   * If listener-b is null, it returns listener-a
   * If neither are null, then it creates and returns
   * a new ProgressListenerMulticaster instance which chains a with b.
   * @param a event listener-a
   * @param b event listener-b
   */
  private static ProgressListener addInternal(ProgressListener a,
                                              ProgressListener b)
  {
    if(a == null)
      return b;

    if(b == null)
      return a;

    return new ProgressListenerMulticaster(a, b);
  }

  /**
   * Returns the resulting multicast listener after removing the
   * old listener from listener-l.
   * If listener-l equals the old listener OR listener-l is null,
   * returns null.
   * Else if listener-l is an instance of ProgressListenerMulticaster,
   * then it removes the old listener from it.
   * Else, returns listener l.
   * @param l the listener being removed from
   * @param oldl the listener being removed
   */
  private static ProgressListener removeInternal(ProgressListener l,
                                                 ProgressListener oldl)
  {
    if (l == oldl || l == null)
    {
      return null;
    }
    else if (l instanceof ProgressListenerMulticaster)
    {
      return ((ProgressListenerMulticaster)l).remove(oldl);
    }
    else
    {
      return l;   // it's not here
    }
  }
}

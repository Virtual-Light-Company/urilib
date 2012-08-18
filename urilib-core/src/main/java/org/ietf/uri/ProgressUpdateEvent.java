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

package org.ietf.uri;

import java.util.EventObject;

import org.ietf.uri.ResourceConnection;
import org.ietf.uri.event.ProgressEvent;

/**
 * An extension of the ProgressEvent allowing reseting of the field values
 * without needing to create a new instance.
 * <P>
 *
 * This class is limited to package access only as we don't want the user
 * to be changing our values on us!
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
class ProgressUpdateEvent extends ProgressEvent
{
  /**
   * Create a new progress event that is used to inform the listeners of an
   * update by the data source. All fields are set to default in this.
   */
  ProgressUpdateEvent()
  {
    super(null, -1, null, -1);
  }

  /**
   * Fetch the value associated with this event.
   *
   * @param src The resource connection that is generating the events
   * @param type The type of event to generate
   * @param msg The message to send with the event. May be <CODE>null</CODE>
   * @param val The value of the event.
   */
  void setValues(ResourceConnection src, int type, int value, String msg)
  {
    this.source = src;
    this.type = type;
    this.msg = msg;
    this.value = value;
  }
}

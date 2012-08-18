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

import java.io.IOException;

/**
 * Thrown to indicate that the caller has attempted an action that is not
 * currently permitted.
 * <P>
 * An illegal action may be performed if the caller attempts to change the
 * setting of a resource connection while it is connected. Other similar
 * circumstances may generate this exception too.
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


public class IllegalActionException extends IOException
{
  /**
   * Constructs a <code>IllegalActionException</code> with no detail message.
   */
  public IllegalActionException()
  {
  }

  /**
   * Constructs a <code>IllegalActionException</code> with the
   * specified detail message.
   *
   * @param msg The detail message.
   */
  public IllegalActionException(String msg)
  {
    super(msg);
  }
}

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

/**
 * An interface to allow the mapping between file extension and MIME type as
 * well as the reverse process.
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
public interface FileNameMap extends java.net.FileNameMap
{
  /**
   * Get the standardised extension used for the given MIME type. This
   * provides a reverse mapping feature over the standard
   * {@link java.net.FileNameMap} that only supplies the opposite method.
   * <P>
   * There are many examples of where a number of extensions map to a single
   * MIME type. eg <CODE>.jpeg</CODE> and <CODE>.jpg</CODE> both map to
   * <CODE>image/jpeg</CODE>. The implementation of this method is free to
   * return either of these. The extension will not include the platform
   * specific extension separator (typically a '.' character);
   *
   * @param mimetype The mime type to check for
   * @return The extension or <CODE>null</CODE> if it cannot be resolved
   */
  public String getFileExtension(String mimetype);
}

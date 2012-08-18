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

// Standard imports
import java.util.Set;
import java.util.Iterator;

// Application specific imports
// none

/**
 * Local default filename map implementation.
 * <P>
 *
 * This map works by finding the last index of '.' and taking every
 * character after that as the extension to map it to a filename.
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
final class DefaultFileMap
  implements FileNameMap
{
  /** Hashmap to hold all of the ports to protocol mapping */
  private TwoWayHashMap extension_map;

  /**
   * Construct a basic filename map from the hashtable of given types. This
   * takes a reference to the hash table rather than copying it, so be
   * careful! The keys of the table must be the file extension - with no dots
   * on it and the values are the corresponding mime type.
   *
   * @param fileTypes The hashtable of extensions and types
   * @exception NullPointerException if the hashmap is null
   */
  DefaultFileMap(TwoWayHashMap fileTypes)
  {
    if(fileTypes == null)
      throw new NullPointerException("Null filename map");

    extension_map = fileTypes;
  }

  /**
   * Add an extract content type to the default map.
   *
   * @param contentType the content type to register as
   * @param ext The file extension that it relates to
   * @exception NullPointerException If either argument is null
   */
  void addContentType(String contentType, String ext)
  {
    if((contentType == null) || (ext == null))
      throw new NullPointerException("Erroneous input Sir!");

    extension_map.put(ext, contentType);
  }

  /**
   * Get the content type for the given filename
   *
   * @param filename The filename to determine the content type for
   * @return
   */
  public String getContentTypeFor(String filename)
  {
    int index = filename.lastIndexOf('.');
    String ext = filename.substring(index + 1);

    return (String)extension_map.get(ext.toLowerCase());
  }

  /**
   * Get the reverse mapping of the mime type for the file extension
   *
   * @param type The mime type for the extension
   * @return The file extension or <CODE>null</CODE> if not handled
   */
  public String getFileExtension(String mimetype)
  {
    Object basic_type = extension_map.reverseGet(mimetype);

    String ret_val = null;

    if(basic_type instanceof Set)
    {
      Iterator itr = ((Set)basic_type).iterator();
      ret_val = (String)itr.next();
    }
    else
      ret_val = (String)basic_type;

    return ret_val;
  }
}

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

import java.net.FileNameMap;
import java.util.HashMap;

/**
 * Local test filename map implementation.
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
 * @version 0.5 (25 Feb 1999)
 */
public final class TestFileMap
  implements FileNameMap
{
  /** Hashmap to hold all of the ports to protocol mapping */
  private HashMap extension_map;

  /**
   * Construct a basic filename map
   */
  public TestFileMap()
  {
    extension_map = new HashMap();
    extension_map.put("jar", "x-java/jar");
    extension_map.put("class", "x-java/class");
  }

  /**
   * Add an extract content type to the default map.
   *
   * @param contentType the content type to register as
   * @param ext The file extension that it relates to
   * @exception NullPointerException If either argument is null
   */
  public void addContentType(String contentType, String ext)
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

    return (String)extension_map.get(ext);
  }
}

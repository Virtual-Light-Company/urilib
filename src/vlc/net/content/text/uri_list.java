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

package vlc.net.content.text;

import java.io.*;
import java.util.LinkedList;

import org.ietf.uri.ContentHandler;
import org.ietf.uri.ResourceConnection;

/**
 * Content handler interface for the content type <code>text/uri-list</code>.
 * <P>
 *
 * The text/uri-list content type returns an array of strings
 * (<CODE>String[]</CODE>) representation each of the URIs that were returned
 * by the content type. A StringReader is used to interpret the contents of
 * the stream and return a String object in the local language encoding.
 * <P>
 *
 * The interpretation of this format is IAW
 * <A HREF="http://www.csl.sony.co.jp/rfc/mirror/rfc2169.txt">RFC2169</A>
 * This handler does not attempt to validate the URI strings apart from
 * removing comments as per the RFC.
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
public class uri_list extends ContentHandler
{
  /**
   * Public empty constructor as required by java reflection.
   */
  public uri_list()
  {
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
    notifyDownloadStarted(resc, null);

    InputStream is = resc.getInputStream();

    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader reader = new BufferedReader(isr);

    // Read the information line at a time. If the line starts with a '#' then
    // it is a comment. Embedded '#' in the line are ignored (may be a
    // reference in the URI. We do no checking to ensure that they are valid
    // URIs. The line is also trimmed() of whitespace too.
    LinkedList list = new LinkedList();
    String a_line;

    while((a_line = reader.readLine()) != null)
    {
      a_line = a_line.trim();
      if(a_line.charAt(0) != '#')
        list.add(a_line);
    }

    String[] ret_vals = new String[list.size()];
    list.toArray(ret_vals);

    notifyDownloadFinished(resc, null);

    return ret_vals;
  }
}

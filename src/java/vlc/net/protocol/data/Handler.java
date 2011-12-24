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

package vlc.net.protocol.data;

import java.io.IOException;
import java.net.MalformedURLException;

import org.ietf.uri.ResourceConnection;
import org.ietf.uri.URIResourceStream;

/**
 * A Data protocol handler.
 * <P>
 *
 * The basic connection handler for dealing with protcol of the
 * type <CODE>data:</CODE> that contains inlined data information.
 * <P>
 *
 * A data protocol handler is of the format:
 * <PRE>
 *   data:[<mediatype>][;base64],<data>
 * </PRE>
 * A full definition of the data URL encoding scheme may be found in
 * <A HREF="http://info.internet.isi.edu/in-notes/rfc/files/rfc2397.txt">
 * RFC 2397</A>.
 * <P>
 *
 * If the media type is known then the appropriate content handler is fetched
 * and used to intepret the stream from the DataResourceConnection.
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
public class Handler extends URIResourceStream
{
  /**
   * Explicit public constructor as required by java reflection.
   * Currently does nothing.
   */
  public Handler()
  {
  }

  /**
   * Open a connection for the given URI. The host and port arguments for
   * this stream type are ignored.
   * <P>
   * The path string is taken to include the characters that make up the
   * data information. This is turned into a character array and interpreted
   * directly.
   *
   * @param host The host name to connect to
   * @param port The port on the host
   * @param path The path needed to access the resource using the given protocol
   * @exception IOException I/O errors reading from the stream
   * @exception IllegalArgumentException host, port or path were invalid
   */
  protected ResourceConnection openConnection(String host,
                                              int port,
                                              String path)
    throws IOException, IllegalArgumentException
  {
    ResourceConnection res = null;

    try
    {
      res = new DataResourceConnection(path);
    }
    catch(MalformedURLException mue)
    {
      throw new IllegalArgumentException("Cannot construct connection");
    }

    return res;
  }
}

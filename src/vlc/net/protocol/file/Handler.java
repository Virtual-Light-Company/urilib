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

package vlc.net.protocol.file;

import java.io.IOException;
import java.net.MalformedURLException;

import org.ietf.uri.ResourceConnection;
import org.ietf.uri.URIResourceStream;

/**
 * A File protocol handler.
 * <P>
 *
 * The basic connection handler for dealing with URLs of the
 * type <CODE>file:///</CODE> and URNs that resolve to items on the
 * local filesystem.
 * <P>
 *
 * The path defines the object as needed from the root of the system.
 * It must be fully qualified - including either machine names for
 * UNC ennuciated paths or drive letters where appropriate to the
 * operating system.
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
   * this stream type are ignored. If a host is needed for a UNC name
   * then that is included in the path.
   *
   * @param host The host name to connect to
   * @param port The port on the host
   * @param path The path needed to access the resource using the given protocol
   * @exception IOException I/O errors reading from the stream
   * @exception IllegalArgumentException host, port or URI were invalid
   */
  protected ResourceConnection openConnection(String host,
                                              int port,
                                              String path)
    throws IOException, IllegalArgumentException
  {
    ResourceConnection res = null;

    try
    {
      res = new FileResourceConnection(path);
    }
    catch(MalformedURLException mue)
    {
      throw new IllegalArgumentException("Cannot construct URL");
    }

    return res;
  }
}

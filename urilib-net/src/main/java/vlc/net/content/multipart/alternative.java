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

package vlc.net.content.multipart;

import java.io.*;

import java.util.jar.JarFile;

import org.ietf.uri.URI;
import org.ietf.uri.URL;
import org.ietf.uri.ContentHandler;
import org.ietf.uri.ResourceConnection;

/**
 * Content handler interface for the content type multipart/alternative.
 * <p/>
 * <p/>
 * <p/>
 * <p/>
 * <p/>
 * For details on URIs see the IETF working group: <A HREF="http://www.ietf.org/html.charters/urn-charter.html">URN</A>
 * <p/>
 * <p/>
 * This softare is released under the <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <p/>
 * <p/>
 * DISCLAIMER:<BR> This software is the under development, incomplete, and is known to contain bugs.
 * This software is made available for review purposes only. Do not rely on this software for
 * production-quality applications or for mission-critical applications.
 * <p/>
 * <p/>
 * Portions of the APIs for some new features have not been finalized and APIs may change. Some
 * features are not fully implemented in this release. Use at your own risk.
 * <p/>
 *
 * @author Justin Couch
 * @version 0.5 (25 Feb 1999)
 */
public class alternative extends ContentHandler
{
    /** The read buffer size */
    private static final int BUFFER_SIZE = 1024;

    /** Public empty constructor as required by java reflection. */
    public alternative()
    {
    }

    /**
     * Given a fresh stream from a ResourceConnection, read and create an object instance. This
     * returns a <CODE>JarFile</CODE>
     *
     * @param resc The resource connection to read the data from
     * @return The object read in by the content handler
     * @throws IOException The connection stuffed up.
     */
    public Object getContent(ResourceConnection resc)
        throws IOException
    {
        return null;
/*
    URI uri = resc.getURI();

    Object ret_val = null;

    if(uri instanceof URL)
    {
      URL url = ((URL)uri);
      String scheme = url.getProtocol();

      if(scheme.equalsIgnoreCase(URI.FILE_SCHEME))
      {
        // create the file instance. If it chucks an IOException we exit
        // immediately anyway so just assume it all works.
        ret_val = new JarFile(url.getPath());
      }
    }

    if(ret_val != null)
      return ret_val;

    // setup the temporary file
    File jar_file = File.createTempFile("jar_cache", ".jar");
    jar_file.deleteOnExit();

    int data_len;
    byte[] buffer = new byte[BUFFER_SIZE];
    InputStream is = resc.getInputStream();
    BufferedInputStream bis = new BufferedInputStream(is, BUFFER_SIZE);
    FileOutputStream fos = new FileOutputStream(jar_file);
    BufferedOutputStream bos = new BufferedOutputStream(fos);

    while((data_len = bis.read(buffer)) != -1)
      bos.write(buffer, 0, data_len);

    bos.flush();
    bos.close();
    fos.close();
    bis.close();

    // just to make sure
    jar_file.setReadOnly();

    ret_val = new JarFile(jar_file);

    return ret_val;
*/
    }
}

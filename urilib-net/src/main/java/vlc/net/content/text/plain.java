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

package vlc.net.content.text;

import java.io.*;

import org.ietf.uri.ContentHandler;
import org.ietf.uri.ResourceConnection;

/**
 * Content handler interface for the content type text/plain.
 * <p/>
 * <p/>
 * The text/plain content type just returns a string representation of the contents of the file. A
 * StringReader is used to interpret the contents of the file and return a String object in the
 * local language encoding.
 * <p/>
 * <p/>
 * Unfortunately, the string class is limited to a maximum of 64K characters. This could cause
 * problems for really large text files. No fix is currently available.
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
 * @version 0.7 (27 August 1999)
 */
public class plain extends ContentHandler
{
    /** The buffer length to read */
    private static final int READ_BUFFER_LENGTH = 256;

    /** Public empty constructor as required by java reflection. */
    public plain()
    {
    }

    /**
     * Given a fresh stream from a ResourceConnection, read and create an object instance.
     *
     * @param resc The resource connection to read the data from
     * @return The object read in by the content handler
     * @throws IOException The connection stuffed up.
     */
    public Object getContent(ResourceConnection resc)
        throws IOException
    {
        notifyDownloadStarted(resc, null);

        InputStream is = resc.getInputStream();

        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isr);
        int data_length = resc.getContentLength();
        int bytes_read = 0;

        StringBuffer buffer =
            (data_length > 0) ? new StringBuffer(data_length) : new StringBuffer();

        // we read using the char array rather than readline, because readline
        // strips the \r\n from the stream. We don't want to loose this information
        // so we use this form instead. We keep reading until the end of the
        // stream is reached (read returns -1) and then exit.
        int size_read = 0;
        char[] buf = new char[READ_BUFFER_LENGTH];

        while((size_read = reader.read(buf, 0, READ_BUFFER_LENGTH)) != -1)
        {
            buffer.append(buf, 0, size_read);
            bytes_read += size_read;

            // every so often, send out an update on the amount we've read
            if((bytes_read % (READ_BUFFER_LENGTH * 4)) == 0)
                notifyDownloadProgress(resc, bytes_read, null);
        }

        notifyDownloadFinished(resc, null);

        return buffer.toString();
    }
}

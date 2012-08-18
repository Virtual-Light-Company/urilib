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

package vlc.net.protocol.data;

import java.io.*;
import java.util.*;

import java.net.URLDecoder;
import java.net.UnknownServiceException;
import java.net.MalformedURLException;

import org.ietf.uri.URL;
import org.ietf.uri.URIUtils;
import org.ietf.uri.ResourceConnection;

/**
 * Representation of a file resource.
 * <p/>
 * <p/>
 * Implements the connection as a standard byte input stream. The data protocol may include header
 * information as part of the parameter information. If the charset is given, then that is set as
 * the encoding type.
 * <p/>
 * <p/>
 * A data protocol handler is of the format: <PRE> data:[<mediatype>][;base64],<data> </PRE> A full
 * definition of the data URL encoding scheme may be found in <A HREF="http://info.internet.isi.edu/in-notes/rfc/files/rfc2397.txt>RFC
 * 2397</A>.
 * <p/>
 * <p/>
 * If the media type is known then the appropriate content handler is fetched and used to intepret
 * the stream from the DataResourceConnection.
 * <p/>
 * <p/>
 * This connection only supports input streams. The last modified time is not known and always
 * returns the default value.
 * <p/>
 * <p/>
 * Because multiple parameters may be put into the stream, we place each parameter into the header
 * info. That way we can use getHeaderField to return all of the details about it. The index of
 * these headers are just as they are declared in the path. If a parameter is <CODE>charset</CODE>
 * that is set to be the encoding type for this connection too. If the <CODE>base64</CODE> parameter
 * is available then this is set as the encoding type in preference. This is a string value.
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
public class DataResourceConnection extends ResourceConnection
{
    /** The default content type if none is supplied */
    private static final String DEFAULT_CONTENT_TYPE = "text/plain";

    /** The default character set (encoding) */
    private static final String DEFAULT_CHARSET = "US-ASCII";

    /** The basic charset BASE 64 string */
    private static final String BASE_64 = "base64";

    /**
     * The character set special parameter. If this is found in the parameter list of the path then it
     * is used to set the encoding type.
     */
    private static final String CHARSET_PARAM = "charset";

    /** The parsed data after having header data removed */
    private byte[] data;

    /** The list of header and parameter information */
    private ArrayList headers = null;

    /** The headers indexed by string as a hash table */
    private HashMap header_map = null;

    /** The content type of the data */
    private String content_type = DEFAULT_CONTENT_TYPE;

    /** The encoding type of this data */
    private String encoding = DEFAULT_CHARSET;

    /** The length of the content if we know what it is */
    private int content_length = -1;

    /**
     * Create an instance of this connection.
     *
     * @param uri The URI to establish the connection to
     * @throws MalformedURLException We stuffed up something in the filename
     */
    protected DataResourceConnection(String path)
        throws MalformedURLException
    {
        super(new URL("data:" + path));

        headers = new ArrayList();
        header_map = new HashMap();

        parseContent(path);
    }

    /**
     * Get the input stream for this. Throws an UnknownServiceExeception if there is no stream
     * available.
     *
     * @return The stream
     */
    public InputStream getInputStream()
        throws IOException
    {
        InputStream stream = new ByteArrayInputStream(data);

        return stream;
    }

    /**
     * Get the content type of the resource that this stream points to. Returns a standard MIME type
     * string. If the content type is not known then <CODE>text/plain</CODE> is returned (the default
     * for data protocol).
     *
     * @return The content type of this resource
     */
    public String getContentType()
    {
        return content_type;
    }

    /**
     * Connect to the named resource if not already connected. This is ignored by this
     * implementation.
     *
     * @throws An error occurred during the connection process
     */
    public void connect()
        throws IOException
    {
    }

    /**
     * Get the length of the content that is to follow on the stream. If the length is unknown then -1
     * is returned. The content length could be the the length of the raw stream or the object. Don't
     * know yet????
     *
     * @return The length of the content in bytes or -1
     */
    public int getContentLength()
    {
        try
        {
            getInputStream();
        }
        catch(IOException ioe)
        {
            content_length = -1;
        }

        // we could do some intelligent guessing here by looking to see if the
        // getContent returns an array and then return that??

        return content_length;
    }

    /**
     * Get the content encoding type of the information.
     *
     * @return A string describing the encoding type
     */
    public String getContentEncoding()
    {
        return encoding;
    }

    /**
     * Fetch the nth header field. Returns the full length field - name and value with the '='
     * included.
     *
     * @param n The index of the header field
     * @return The value of header or null if the number is out of bounds
     */
    public String getHeaderField(int n)
    {
        String ret_val = null;

        if(n < headers.size())
            ret_val = (String)headers.get(n);

        return ret_val;
    }

    /**
     * Fetch a header field with the given string name. The name is treated using capitalisation. The
     * return value is just the value defined for that header, not the entire string.
     *
     * @param name The name of the header to fetch
     * @return The value of the header or null if not defined
     */
    public String getHeaderField(String name)
    {
        return (String)header_map.get(name);
    }

    /** Add a parameter for the header. */
    private void addParam(String param)
    {
        headers.add(param);

        int pos = param.indexOf('=');

        if(param.startsWith(CHARSET_PARAM))
        {
            // find the value of the equals and set the param....?
            encoding = param.substring(pos + 1);
        }

        header_map.put(param.substring(0, pos), param.substring(pos + 1));
    }

    /**
     * Parse the content to break it into the appropriate pieces. The start of the acutal data is
     * represented by a comma
     *
     * @param path The path statement passed to this object
     */
    private void parseContent(String path)
    {
        int data_point = path.indexOf(',');

        // media type = [type "/" subtype] *(";" parameter) [";base64"]
        if(data_point != 0)
        {
            boolean found_base = false;
            String media_def = path.substring(0, data_point);
            StringTokenizer strtok = new StringTokenizer(media_def, ";");

            // first token may be content type. If it contains '=' it obviously
            // can't be. Also check that it isn't "base64".
            try
            {
                String param = strtok.nextToken();

                if(param.indexOf('=') == -1)
                {
                    if(param.equalsIgnoreCase(BASE_64))
                    {
                        encoding = BASE_64;
                        found_base = true;
                    }
                    else
                        content_type = param;
                }
                else
                {
                    // so it is a parameter. Add it to the list and keep going
                    addParam(param);
                }

                while(!found_base && strtok.hasMoreTokens())
                {
                    param = strtok.nextToken();

                    if(param.equalsIgnoreCase(BASE_64))
                    {
                        encoding = BASE_64;
                        // end of the line in processing terms !
                        found_base = true;
                    }
                    else
                        addParam(param);
                }
            }
            catch(NoSuchElementException nste)
            {
            }
        }

        // now lets strip and decode everything
        try
        {
            String decoded_string = path.substring(data_point + 1);
            decoded_string = URLDecoder.decode(decoded_string);

            data = decoded_string.getBytes();
        }
        catch(Exception e)
        {
            // hmmm.....
        }

        if(content_type.equals("text/plain"))
            // is this really correct? Should it be decoded_string.length() ?
            content_length = data.length;
    }

/**
 *  public static void main(String[] args)
 *  {
 *    String[] paths = {
 *      ",A%20brief%20note",
 *      ";base64,A%20brief%20note",
 *      ";charset=iso-8649,A%20brief%20note",
 *      ";charset=iso-8649;base64,A%20brief%20note",
 *      ";charset=iso-8649;param=test1,A%20brief%20note",
 *      ";charset=iso-8649;param=test1;base64,A%20brief%20note",
 *    };
 *
 *    for(int i = 0; i < paths.length; i++)
 *    {
 *      try
 *      {
 *        System.out.println("Checking " + paths[i]);
 *        DataResourceConnection resc = new DataResourceConnection(paths[i]);
 *        System.out.println("  Content encoding is " + resc.getContentEncoding());
 *        System.out.println("  Content type is " + resc.getContentType());
 *        System.out.println("  Param field is " + resc.getHeaderField("param"));
 *        System.out.println("  Header field 2 is " + resc.getHeaderField(1));
 *        System.out.println("  Content is \"" + resc.getContent() + "\"");
 *      }
 *      catch(IOException ioe)
 *      {
 *        System.out.println(ioe);
 *      }
 *    }
 *  }
 **/
}

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

import java.io.InputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.zip.GZIPOutputStream;

/**
 * A ResourceConnection with support for HTTP-specific features.
 * <P>
 *
 * Any implementation of a HTTP connection must extend this base class.
 * <P>
 *
 * Provides similar functionality to the standard java.netHttpUrlConnection
 * but in a form more acceptable to general URIs. It also understands many
 * more features that are common and require common implementations.
 * <P>
 *
 * On first instantiation of a derived version of this class it attempts
 * to load a number of properties to construct the default configuration.
 * The following system properties are understood:
 * <TABLE>
 * <TR><TH>Property Name <TH>Interpretation</TR>
 * <TR>
 *   <TD><CODE>http.proxyHost</CODE>
 *   <TD>The name or IP address of the host to use as the proxy server
 *     by all http connections
 * </TR>
 * <TR>
 *   <TD><CODE>http.proxyPort</CODE>
 *   <TD>The port number to connect to on the proxy host, if set.
 * </TR>
 * <TR>
 *   <TD><CODE>http.nonProxyHosts</CODE>
 *   <TD>A list of host names and IP addresses that should be contacted
 *     directly rather than through the proxy. Items in the list shall be
 *     separated with the pipe character '|'.
 * </TR>
 * <TR>
 *   <TD><CODE>http.agent</CODE>
 *   <TD>The name of the User-Agent field that should be passed in headers
 * </TR>
 * <TR>
 *   <TD><CODE>http.allowCookies</CODE>
 *   <TD>A boolean value indicating whether a general policy of allowing or
 *     disallowing the use of cookies should be applied.
 * </TR>
 * <TR>
 *   <TD><CODE>http.allowRedirects</CODE>
 *   <TD>A boolean value indicating whether a general policy of allowing or
 *     disallowing server redirects shall be permitted.
 * </TR>
 * <TR>
 *   <TD><CODE>http.maxRedirects</CODE>
 *   <TD>If redirection is allowed, this specifies the maximum number of
 *     redirects that should be allowed before causing an exception. Setting
 *     the value to negative will cause it to be ignored. A value of zero is
 *     effectively treated as not allowing redirects, although it does not
 *     effect the allowRedirects flag.
 * </TR>
 * <P>
 *
 * Implementation based on
 * <A HREF="http://www.w3.org/pub/WWW/Protocols/">the W3C spec</A>.
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
public abstract class HttpResourceConnection extends ResourceConnection
{
  public static final int INVALID_HTTP_RESPONSE = -1;

  /** 2XX: generally "OK" */
  public static final int HTTP_OK = 200;
  public static final int HTTP_CREATED = 201;
  public static final int HTTP_ACCEPTED = 202;
  public static final int HTTP_NOT_AUTHORITATIVE = 203;
  public static final int HTTP_NO_CONTENT = 204;
  public static final int HTTP_RESET = 205;
  public static final int HTTP_PARTIAL = 206;

  /** 3XX: relocation/redirect */
  public static final int HTTP_MULT_CHOICE = 300;
  public static final int HTTP_MOVED_PERM = 301;
  public static final int HTTP_MOVED_TEMP = 302;
  public static final int HTTP_SEE_OTHER = 303;
  public static final int HTTP_NOT_MODIFIED = 304;
  public static final int HTTP_USE_PROXY = 305;

  /** 4XX: client error */
  public static final int HTTP_BAD_REQUEST = 400;
  public static final int HTTP_UNAUTHORIZED = 401;
  public static final int HTTP_PAYMENT_REQUIRED = 402;
  public static final int HTTP_FORBIDDEN = 403;
  public static final int HTTP_NOT_FOUND = 404;
  public static final int HTTP_BAD_METHOD = 405;
  public static final int HTTP_NOT_ACCEPTABLE = 406;
  public static final int HTTP_PROXY_AUTH = 407;
  public static final int HTTP_CLIENT_TIMEOUT = 408;
  public static final int HTTP_CONFLICT = 409;
  public static final int HTTP_GONE = 410;
  public static final int HTTP_LENGTH_REQUIRED = 411;
  public static final int HTTP_PRECON_FAILED = 412;
  public static final int HTTP_ENTITY_TOO_LARGE = 413;
  public static final int HTTP_REQ_TOO_LONG = 414;
  public static final int HTTP_UNSUPPORTED_TYPE = 415;

  /** 5XX: server error */
  public static final int HTTP_SERVER_ERROR = 500;
  public static final int HTTP_INTERNAL_ERROR = 501;
  public static final int HTTP_BAD_GATEWAY = 502;
  public static final int HTTP_UNAVAILABLE = 503;
  public static final int HTTP_GATEWAY_TIMEOUT = 504;
  public static final int HTTP_VERSION = 505;

  /** Valid HTTP method types */
  public static final String GET_METHOD = "GET";
  public static final String POST_METHOD = "POST";
  public static final String HEAD_METHOD = "HEAD";
  public static final String OPTIONS_METHOD = "OPTIONS";
  public static final String PUT_METHOD = "PUT";
  public static final String DELETE_METHOD = "DELETE";
  public static final String TRACE_METHOD = "TRACE";

  public static final String INVALID_RESPONSE_MSG = "Invalid HTTP response";

  //

  /** Query for the header modification date */
  public static final String IF_MODIFIED_HEADER = "If-Modified-Since";

  /** Request for or return value of the MIME content type */
  public static final String CONTENT_TYPE_HEADER = "Content-Type";

  /** The length of the content returned */
  public static final String CONTENT_LENGTH_HEADER = "Content-Length";

  /** When recieving a relocation/redirect response, The new location */
  public static final String LOCATION_HEADER = "Location";

  // static constants for properties to be read
  private static final String PROXY_HOST_PROP = "http.proxyHost";
  private static final String PROXY_PORT_PROP = "http.proxyPort";
  private static final String NO_PROXY_PROP = "http.noProxyHosts";
  private static final String USER_AGENT_PROP = "http.agent";
  private static final String COOKIE_PROP = "http.allowCookies";
  private static final String REDIRECT_PROP = "http.allowRedirects";
  private static final String NUM_REDIRECT_PROP = "http.numRedirects";

  private static final int DEFAULT_MAX_REDIRECTS; // set in the initialiser

  // Variables!!!

  /** Do we automatically follow redirects. The default is true. */
  protected static boolean followRedirects = true;

  /**
   * The maximum number of redirects permitted before errors should be
   * generated. The default setting is 5. Unlike the follow redirects rule,
   * this may be set on a per instance basis.
   */
  protected int maxRedirects = 5;

  /** Flag to allow the use of cookies. The default is no. */
  protected static boolean allowCookies = false;

  /** The current proxy host or <CODE>null</CODE> if not set (the default) */
  protected static String proxyHost = null;

  /** The current port on the proxy server or -1 if not set */
  protected static int proxyPort = -1;

  /** The list of hosts name strings that should not be proxied for */
  protected static List nonProxiedHosts = new ArrayList();

  /**
   * The name of the user agent provided by this client to the server. If the
   * agent is not set, it is set to <CODE>null</CODE>.
   */
  protected static String userAgent = null;

  /**
   * The currently set access method. Defaults to <CODE>GET_METHOD</CODE>.
   */
  protected String method = GET_METHOD;

  /**
   * The message response code as found in the first header. This matches with
   * the list of valid codes defined as the public <CODE>HTTP_</CODE> variables
   * of this class. If no valid code is available, -1 is set.
   */
  protected int responseCode = INVALID_HTTP_RESPONSE;

  /**
   * The message that is returned along with the response code. If no message
   * is defined or there are errors, this may be null;
   */
  protected String responseMsg = null;

  /**
   * Variable to indicate if a proxy is valid and in use for this connection.
   * The default setting is false unless set otherwise by the derived class.
   */
  protected boolean proxyInUse = false;

  /** The content type */
  private String content_type = null;

  /** The content length */
  private int content_length = -1;

  /**
   * Static initialiser that reads the system properties and puts the
   * initial values somewhere.
   */
  static
  {
    allowCookies = Boolean.getBoolean(COOKIE_PROP);
    followRedirects = Boolean.getBoolean(REDIRECT_PROP);
    proxyPort = Integer.getInteger(PROXY_PORT_PROP, -1).intValue();

    DEFAULT_MAX_REDIRECTS =
      Integer.getInteger(NUM_REDIRECT_PROP, 5).intValue();

    try
    {
      userAgent = System.getProperty(USER_AGENT_PROP);
    }
    catch (SecurityException se)
    {
    }

    try
    {
      proxyHost = System.getProperty(PROXY_HOST_PROP);
    }
    catch (SecurityException se)
    {
    }

    try
    {
      String hosts = System.getProperty(NO_PROXY_PROP, "");

      StringTokenizer strtok = new StringTokenizer(hosts, "|");
      while(strtok.hasMoreTokens())
      {
        nonProxiedHosts.add(strtok.nextToken());
      }
    }
    catch(NoSuchElementException nse)
    {
    }
    catch (SecurityException se)
    {
    }
  }

  /**
   * Constructor for the URIStreamResource handler class.
   *
   * @param uri The URI that this resource handler uses.
   */
  protected HttpResourceConnection(URI uri)
  {
    super(uri);

    maxRedirects = DEFAULT_MAX_REDIRECTS;
  }

  /**
   * Close the connection to the server.
   */
  public abstract void disconnect();

  /**
   * Sets whether HTTP redirects  (requests with response code 3xx) should
   * be automatically followed by this class.  True by default.  Applets
   * cannot change this variable.
   * <p>
   * If there is a security manager, this method first calls
   * the security manager's <code>checkSetFactory</code> method
   * to ensure the operation is allowed.
   * This could result in a SecurityException.
   *
   * @exception SecurityException if a security manager exists and its
   *   <CODE>checkSetFactory</CODE> method doesn't allow the operation.
   */
  public static void setFollowRedirects(boolean redirect)
  {
    SecurityManager sec = System.getSecurityManager();

    if (sec != null)
    {
        // seems to be the best check here...
        sec.checkSetFactory();
    }

    followRedirects = redirect;
  }

  /**
   * Check to see whether this handler will be following redirects
   *
   * @return True if redirection is allowed
   */
  public static boolean getFollowRedirects()
  {
    return followRedirects;
  }

  /**
   * Set the maximum number of redirects that are allowed. If the connection
   * has already been made then an exception is generated. If the value is
   * negative an exception is generated. If the value is zero that is
   * effectively the same as disallowing the use of redirects for this
   * particular connection.
   *
   * @param num The number of redirects to allow
   * @exception IllegalActionException The connection is currently active
   *   preventing this value from being set
   * @exception IllegalArgumentException The number set was negative
   */
  public void setMaxRedirects(int num)
    throws IllegalActionException
  {
    if(connected)
      throw new IllegalActionException("Connection established");

    if(num < 0)
      throw new IllegalArgumentException("Negative max redirects");

    maxRedirects = num;
  }

  /**
   * Sets whether HTTP requests can use cookies or they are not permitted.
   * The simple binary approach is currently only available with this
   * implementation. Note that if this code is
   * <P>
   * A future implementation shall allow a finer grain detail method for
   * dealing with cookies such as notifying the user if a cookies wants to be
   * set and asking for approval.
   *
   * @param allowed True if cookies are allowed
   */
  public static void setAllowCookies(boolean allowed)
  {
    allowCookies = allowed;
  }

  /**
   * Get the current state of whether cookies are allowed.
   *
   * @return The current cookie permission state
   */
  public static boolean getAllowCookies()
  {
    return allowCookies;
  }

  /**
   * Indicates if the connection is going through a proxy.
   */
  public boolean usingProxy()
  {
    return proxyInUse;
  }

  /**
   * Set the proxy server that this code should be using to access http
   * requests. The security exception is not generated in the current
   * implementation, pending the design of a good set of values. No
   * checking is performed on the validity of this name. If the host
   * cannot be found at connect time an IOException shall be generated then.
   *
   * @param host The name of the host or <CODE>null</CODE> to clear
   * @exception SecurityException The proxy cannot be set
   */
  public static void setProxyHost(String host)
    throws SecurityException
  {
    proxyHost = host;
  }

  /**
   * Get the name of the proxy host that is currently being used
   *
   * @return The name of the proxy host or <CODE>null</CODE> if not set
   */
  public static String getProxyHost()
  {
    return proxyHost;
  }

  /**
   * Set the proxy server port that this code should be using to access http
   * requests. The security exception is not generated in the current
   * implementation, pending the design of a good set of values.
   *
   * @param port The port on the host -1 to clear
   * @exception SecurityException The proxy cannot be set
   */
  public static void setProxyPort(int port)
    throws SecurityException
  {
    proxyPort = port;
  }

  /**
   * Get the port of the proxy host that is currently being used
   *
   * @return The port on the proxy host or -1 if not set
   */
  public static int getProxyPort()
  {
    return proxyPort;
  }

  /**
   * Add a host name to the exclusion list of hosts that should be connected
   * directly, rather than through the proxy. No checking is done on this
   * host name. If the host cannot be found at connect time an IOException
   * shall be generated then.
   *
   * @param host The name of the host to add
   * @exception IllegalArgumentException The host name was <CODE>null</CODE>
   */
  public static void addNoProxyForHost(String host)
  {
    if(host == null)
      throw new IllegalArgumentException("Null host name for don't proxy req");

    nonProxiedHosts.add(host.toLowerCase());
  }

  /**
   * Add a list of hosts to the collection of hosts that should not be proxied.
   * If items in the list are null, they are ignored. All items must be strings
   * for the host name. Non string items are ignored.
   *
   * @param hosts The list of hosts to add
   */
  public static void addNoProxyForHost(List hosts)
  {
    if(hosts == null)
      return;

    // first clean out all non null entries
    int i;
    int size = hosts.size();

    for(i = 0; i < size; i++)
    {
      Object h = hosts.get(i);
      if((h == null) || !(h instanceof String))
        hosts.remove(i);

      // hmmm... what do we do about making sure everything is lowercase?
    }

    nonProxiedHosts.addAll(hosts);
  }

  /**
   * Remove a host name from the exclusion list of hosts that should be
   * connected directly, rather than through the proxy. Any requests for this
   * host will now be done through the proxy server.
   *
   * @param host The name of the host to remove
   */
  public static void removeNoProxyForHost(String host)
  {
    if(host == null)
      return;

    nonProxiedHosts.remove(host.toLowerCase());
  }

  /**
   * Set the method for the URL request, one of:
   * <UL>
   *  <LI>GET
   *  <LI>POST
   *  <LI>HEAD
   *  <LI>OPTIONS
   *  <LI>PUT
   *  <LI>DELETE
   *  <LI>TRACE
   * </UL>
   * are all legal, subject to protocol restrictions.  The default method is
   * GET.
   *
   * @param type The type of method to make this message into
   * @exception IllegalActionException if the method cannot be reset or
   *   if the requested method isn't valid for HTTP.
   */
  public void setRequestMethod(String type)
    throws IllegalActionException
  {
    if(connected)
      throw new IllegalActionException("Can't reset method: already connected");

    method = type;
  }

  /**
   * Get the currently set request method.
   *
   * @return The current method type
   */
  public String getRequestMethod()
  {
    return method;
  }

  /**
   * Gets HTTP response status.  From responses like:
   * <PRE>
   * HTTP/1.0 200 OK
   * HTTP/1.0 401 Unauthorized
   * </PRE>
   * Extracts the ints 200 and 401 respectively.
   * <P>
   * If no respons code can be determined, -1 is returned.
   *
   * @return One of the values inidicated by public response codes.
   * @exception IOException if an error occurred connecting to the server.
   */
  public int getResponseCode()
    throws IOException
  {
    if(responseCode != -1)
      return responseCode;

    // make sure we've gotten the headers
    getInputStream();

    String response = getHeaderField(0);

    // Strip the header based on the standard format
    // "HTTP/1.x<whitespace>XXX <something or other>"

    int ind;

    try
    {
      StringTokenizer strtok = new StringTokenizer(response);

      // throw away "HTTP/1.x"
      strtok.nextToken();

      responseCode = Integer.parseInt(strtok.nextToken());
      responseMsg = strtok.nextToken("");
    }
    catch (Exception e)
    {
    }

    return responseCode;
  }

  /**
   * Gets the HTTP response message, if any, returned along with the
   * response code from a server.  From responses like:
   * <PRE>
   * HTTP/1.0 200 OK
   * HTTP/1.0 404 Not Found
   * </PRE>
   *
   * Extracts the Strings "OK" and "Not Found" respectively.
   * <P>
   * Returns null if none could be discerned from the responses
   * (the result was not valid HTTP).
   *
   * @return The message as indicated
   * @throws IOException if an error occurred connecting to the server.
   */
  public String getResponseMessage()
    throws IOException
  {
    if(responseMsg != null)
      return responseMsg;

    // well, let the response code handler do this....
    getResponseCode();

    return responseMsg;
  }

  /**
   * Returns the error stream if the connection failed but the server sent
   * useful data nonetheless. The typical example is when an HTTP server
   * responds with a 404, which will cause a FileNotFoundException to be thrown
   * in connect, but the server sent an HTML help page with suggestions as to
   * what to do.
   * <P>
   *
   * This method will not cause a connection to be initiated. If the connection
   * was not connected, or if the server did not have an error while connecting
   * or if the server did have an error but there no error data was sent, this
   * method will return null. This is the default.
   *
   * @return an error stream if any, null if there have been no errors, the
   *   connection is not connected or the server sent no useful data.
   */
  public InputStream getErrorStream()
  {
    return null;
  }

  /**
   * Get the content type of the http stream. Determines it by asking for the
   * CONTENT_TYPE_HEADER field.
   *
   * @return The content type as defined by the stream
   */
  public String getContentType()
  {
    if(content_type == null)
      content_type = getHeaderField(CONTENT_TYPE_HEADER);

    return content_type;
  }

  /**
   * Get the content length of the http stream. Determines it by asking for
   * the CONTENT_LENGTH_HEADER field.
   *
   * @return The content length as defined by the stream
   */
  public int getContentLength()
  {
    if(content_length == -1)
    {
      String length_str = getHeaderField(CONTENT_LENGTH_HEADER);

      try
      {
        content_length = Integer.parseInt(length_str);
      }
      catch(NumberFormatException nfe)
      {
        // ignore as the default will remain at -1
      }
    }

    return content_length;
  }
}

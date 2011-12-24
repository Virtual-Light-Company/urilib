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

package org.ietf.uri;

import java.io.InputStream;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * A ResourceConnection with support for FTP-specific features.
 * <P>
 *
 * Provides a general FTP connection handler. Allows the placement of
 * bidirectional FTP implementations if needed.
 * <P>
 *
 * Any implementation of a FTP connection must extend this base class
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
public abstract class FtpResourceConnection extends ResourceConnection
{
  public static final int INVALID_FTP_RESPONSE = -1;

  // 1XX: general operation

  /** Restart marker reply */
  public static final int RESTART = 110;

  /** The system will be ready in N minutes */
  public static final int SYS_READY_SOON = 120;

  /** Data connection already open; transfer starting */
  public static final int OPEN_ADDITIONAL_TX = 125;

  /** File status OK. About to open data connection */
  public static final int FILE_OK = 150;

  // 2XX: generally everything is ok

  /** Command is OK */
  public static final int CMD_OK = 200;

  /** The command is not implemented because it is superfluous at this site */
  public static final int CMD_SUPERFLOUS = 202;

  /** System status or system help reply */
  public static final int SYS_STAT = 211;

  /** Directory status */
  public static final int DIR_STAT = 212;

  /** The file status */
  public static final int FILE_STATUS = 213;

  /** A help message on how to use the server etc */
  public static final int HELP_MSG = 214;

  /** The name of the system type */
  public static final int NAME_SYS_TYPE = 215;

  /** The service is ready for a new user */
  public static final int SERVICE_READY = 220;

  /** The service is closing the control connection */
  public static final int SERVICE_CLOSING = 221;

  /** Data connection open; no transfer in process */
  public static final int DATA_NO_TX = 225;

  /** Closing connection. Transfer successful */
  public static final int DATA_GOOD_TX = 226;

  /** Entering passive mode */
  public static final int PASSIVE_MODE = 227;

  /** User logged in. Keep going. */
  public static final int USER_LOGGED_IN = 230;

  /** Requested file action OK and completed */
  public static final int FILE_ACTION_OK = 250;

  /** Requested PATHNAME has been created */
  public static final int PATHNAME_CREATED = 257;

  // 3XX: User interaction commands

  /** Username OK. Need password */
  public static final int NEED_PASSWD = 331;

  /** Need account for login */
  public static final int NEED_USER_ACCT = 332;

  /** Requested file action pending further info */
  public static final int NEED_MORE_INFO = 350;

  // 4XX: No, piss off commands

  /**
   * Service is not available, closing the control connection. This may be a
   * reply to any command if the service knows it must shut down.
   */
  public static final int SERVICE_DYING = 421;

  /** Can't open data connection */
  public static final int DATA_CANT_OPEN = 425;

  /** Connection closed, transfer aborted */
  public static final int DATA_TX_ABORT = 426;

  /** Requested file action not taken. File unavailable (may be busy etc) */
  public static final int DATA_ACTION_UNAVAILABLE = 450;

  /** Requested action aborted, local error in processing */
  public static final int DATA_ACTION_ABORT = 451;

  /** Requested action not taken. Insufficient storage space */
  public static final int DATA_ABORT_SPACE_NEEDED = 452;


  // 5XX: Error commands

  /**
   * Syntax error: command is unrecognized. This may include errors such as
   * the command line being too long.
   */
  public static final int CMD_UNRECOGNIZED = 500;

  /** Syntax error in parameters or arguments */
  public static final int CMD_SYNTAX_ERROR = 501;

  /** Command is not implemented at all */
  public static final int CMD_NOT_IMPL = 502;

  /** Bad sequence of commands */
  public static final int CMD_BAD_SEQUENCE = 503;

  /** The command is not implemented for that parameter */
  public static final int CMD_NOT_IMPL_PARAM = 504;

  /** Not logged in */
  public static final int NOT_LOGGED_IN = 530;

  /** Need account for storing files */
  public static final int NEED_LOCAL_ACCT = 532;

  /**
   * Requested action not taken. File unavailable (file not found or
   * access restrictions).
   */
  public static final int DATA_NOT_FOUND = 550;

  /** Requested action aborted; page type unknown */
  public static final int PAGE_TYPE_UNKNOWN = 551;

  /** Requested action aborted. Exceeded storage allocation (quotas). */
  public static final int DATA_ABORT_SPACE_OVERRUN = 552;

  /** Requested action aborted. File name not found */
  public static final int FILENAME_NOT_FOUND = 553;

  //
  // The list of commands
  //

  public static final String USER = "USER";
  public static final String PASS = "PASS";
  public static final String ACCT = "ACCT";
  public static final String CWD = "CWD";
  public static final String CDUP = "CDUP";
  public static final String SMNT = "SMNT";
  public static final String QUIT = "QUIT";
  public static final String REIN = "REIN";
  public static final String PORT = "PORT";
  public static final String PASV = "PASV";
  public static final String TYPE = "TYPE";
  public static final String STRU = "STRU";
  public static final String MODE = "MODE";
  public static final String RETR = "RETR";
  public static final String STOR = "STOR";
  public static final String STOU = "STOU";
  public static final String APPE = "APPE";
  public static final String ALLO = "ALLO";
  public static final String REST = "REST";
  public static final String RNFR = "RNFR";
  public static final String RNTO = "RNTO";
  public static final String ABOR = "ABOR";
  public static final String DELE = "DELE";
  public static final String RMD = "RMD";
  public static final String MKD = "MKD";
  public static final String PWD = "PWD";
  public static final String LIST = "LIST";
  public static final String SITE = "SITE";
  public static final String SYST = "SYST";
  public static final String STAT = "STAT";
  public static final String HELP = "HELP";
  public static final String NOOP = "NOOP";


  /**
   * The message response code as found in the first header. This matches with
   * the list of valid codes defined as the public <CODE>FTP_</CODE> variables
   * of this class. If no valid code is available, INVALID_FTP_RESPONSE is set.
   */
  protected int responseCode = INVALID_FTP_RESPONSE;

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

  /**
   * Constructor for the URIStreamResource handler class.
   *
   * @param uri The URI that this resource handler uses.
   */
  protected FtpResourceConnection(URI uri)
  {
    super(uri);
  }

  /**
   * Close the connection to the server.
   */
  public abstract void disconnect();

  /**
   * Indicates if the connection is going through a proxy.
   *
   * @return true if a proxy is being used.
   */
  public boolean usingProxy()
  {
    return proxyInUse;
  }

  /**
   * Gets the FTP response status.  From responses like:
   * <PRE>
   * 250 Command OK
   * </PRE>
   * Extracts the int 250
   * <P>
   * If no response code can be determined, -1 is returned.
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

/*
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
*/
    return responseCode;
  }

  /**
   * Gets the FTP response message, if any, returned along with the
   * response code from a server.  From responses like:
   * <PRE>
   * 250 OK
   * </PRE>
   *
   * Extracts the String "OK".
   * <P>
   * Returns null if none could be discerned from the responses
   * (the result was not valid FTP).
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
}

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
import java.io.Serializable;
import java.io.IOException;
import java.util.HashMap;
import java.net.FileNameMap;

/**
 * Constants that can be used to form and create URIs of many forms.
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
public interface URIConstants
{
  // The names of all the protocol strings usable in URLs
  public static final String COFFEE_SCHEME = "coffee";
  public static final String DATA_SCHEME = "data";
  public static final String FILE_SCHEME = "file";
  public static final String FTP_SCHEME = "ftp";
  public static final String GOPHER_SCHEME = "gopher";
  public static final String HNEWS_SCHEME = "hnews";
  public static final String HTTP_SCHEME = "http";
  public static final String HTTP_NG_SCHEME = "http-ng";
  public static final String HTTPS_SCHEME = "https";
  public static final String IMAP_SCHEME = "imap";
  public static final String IRC_SCHEME = "irc";
  public static final String JAR_SCHEME = "jar";
  public static final String LDAP_SCHEME = "ldap";
  public static final String MAILTO_SCHEME = "mailto:";
  public static final String NEWS_SCHEME = "news";
  public static final String NFS_SCHEME = "nfs";
  public static final String NNTP_SCHEME = "nntp";
  public static final String POP_SCHEME = "pop";
  public static final String PROSPERO_SCHEME = "prospero";
  public static final String RWHOIS_SCHEME = "rwhois";
  public static final String SHTTP_SCHEME = "shttp";
  public static final String SMTP_SCHEME = "smtp";
  public static final String SNEWS_SCHEME = "snews";
  public static final String TELNET_SCHEME = "telnet";
  public static final String URN_SCHEME = "urn";
  public static final String VEMMI_SCHEME = "vemmi";
  public static final String VIDEOTEX_SCHEME = "videotex";
  public static final String WAIS_SCHEME = "wais";
  public static final String WHOIS_SCHEME = "whois";
  public static final String WHOIS_PLUS_SCHEME = "whois++";
  public static final String Z3950R_SCHEME = "z39.50r";
  public static final String Z3950S_SCHEME = "z39.50s";

  // The default port numbers
  public static final int COFFEE_PORT = 80;
  public static final int FTP_PORT = 21;
  public static final int GOPHER_PORT = 70;
  public static final int HNEWS_PORT = 80;
  public static final int HTTP_PORT =  80;
  public static final int HTTPS_PORT = 443;
  public static final int HTTP_NG_PORT = 80;
  public static final int IMAP_PORT = 143;
  public static final int IRC_PORT = 194;
  public static final int LDAP_PORT = 389;
  public static final int NNTP_PORT = 119;
  public static final int NEWS_PORT = 119;
  public static final int NFS_PORT = 2049;
  public static final int POP_PORT = 110;
  public static final int PROSPERO_PORT = 1525;
  public static final int RWHOIS_PORT = 4321;
  public static final int SHTTP_PORT =  80;
  public static final int SMTP_PORT = 25;
  public static final int SNEWS_PORT = 563;
  public static final int TELNET_PORT = 23;
  public static final int VEMMI_PORT = 575;
  public static final int VIDEOTEX_PORT = 516;
  public static final int WAIS_PORT = 210;
  public static final int WHOIS_PORT = 43 ;
  public static final int WHOIS_PLUS_PORT = 63;
  public static final int Z3950R_PORT = 210;
  public static final int Z3950S_PORT = 210;
}

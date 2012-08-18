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
import java.io.Serializable;
import java.io.IOException;
import java.util.HashMap;

/**
 * Class <code>URN</code> represents a Uniform Resource Indicator (URI).
 * <P>
 *
 * A URI is the basic class of descriptors used to locate and identify
 * resources.
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
public abstract class URI
  implements URIConstants, Serializable
{
  /**
   * Create a string representation of the URI.
   *
   * @return The string representation of this object
   */
  public abstract String toExternalForm();

  /**
   * Establish a connection to the named resource. This partially maps the
   * I2R service request in providing a representation of the connection to
   * the resource without actually supply the resource itself. The resource
   * itself can be then obtained using the methods of the ResouceConnection
   * class.
   *
   * @return A reference to the connection to the resource.
   * @exception UnsupportedServiceException Resolution of the requested service
   *   type for this URN is not available
   * @see ResourceConnection
   */
  public abstract ResourceConnection getResource()
    throws UnsupportedServiceException, IOException;

  /**
   * Establish a connection to all possible resolutions of this resource. This
   * partially maps to the I2Rs service request in providing all possible
   * representations of the URI without actually fetching the resource itself.
   * The resources themselves may be obtained using the methods of the
   * ResourceConnection class.
   *
   * @return The list of connections to resources
   * @exception UnsupportedServiceException Resolution of the requested service
   *   type for this URN is not available
   * @see ResourceConnection
   */
  public abstract ResourceConnection[] getResourceList()
    throws UnsupportedServiceException, IOException;

  /**
   * I2L service request. Get the URL that represents this URI.
   * If a URL representation of this URI cannot be found through the resolver
   * service then this method returns null.
   *
   * @return The URL representing this URI or null
   * @exception UnsupportedServiceException Resolution of the requested service
   *   type for this URI is not available
   */
  public abstract URL getURL()
    throws UnsupportedServiceException, IOException;

  /**
   * I2Ls service request. Get the list of possible URLs that represents this
   * URN. Whether this results in a legal URL that really represents the URI
   * is dependent on the confirm property.
   * <P>
   * If a URL representation of this URI cannot be found through the resolver
   * service then this method returns null.
   *
   * @return The list of URL representing this URI or null
   * @exception UnsupportedServiceException Resolution of the requested service
   *   type for this URI is not available
   */
  public abstract URL[] getURLList()
    throws UnsupportedServiceException, IOException;

  /**
   * I2C service request. Get the first URC that describes this URI.
   * If a URC cannot be determined for this URI then null is returned.
   *
   * @return The URC describing this URI.
   * @exception UnsupportedServiceException Resolution of the requested service
   *   type for this URI is not available
   */
  public abstract URC getURC()
    throws UnsupportedServiceException, IOException;

  /**
   * I2C service request. Get the list of URCs that describes this URI.
   * If a URC cannot be determined for this URI then null is returned.
   *
   * @return The URC describing this URN.
   * @exception UnsupportedServiceException Resolution of the requested service
   *   type for this URN is not available
   */
  public abstract URC[] getURCList()
    throws UnsupportedServiceException, IOException;

  /**
   * I2N service request. Get the first URN that describes this URI.
   * If a URN cannot be determined for this URI then null is returned.
   *
   * @return The list of equivalent URNs
   * @exception UnsupportedServiceException Resolution of the requested service
   *   type for this URN is not available
   */
  public abstract URN getURN()
    throws UnsupportedServiceException, IOException;

  /**
   * I2Ns service request. Get the list of possible URNs that are also
   * equivalent descriptors of this resource. If no alternate representations
   * are available, then null is returned.
   *
   * @return The list of equivalent URNs
   * @exception UnsupportedServiceException Resolution of the requested service
   *   type for this URN is not available
   */
  public abstract URN[] getURNList()
    throws UnsupportedServiceException, IOException;

  /**
   * Set the RDS factory to be used by the application. This method may be
   * called multiple times during the life of the application. By passing a
   * value of <CODE>null</CODE> you can clear the currently set factory.
   *
   * @param fac The desired factory
   * @exception SecurityException if the security manager exists and the
   *   set factory operation is not allowed.
   */
  public static void setURIResolverServiceFactory(URIResolverServiceFactory fac)
  {
    RDSManager.setURIResolverServiceFactory(fac);
  }

  /**
   * Fetch the currently set factory. In general this is discouraged from being
   * called because an application as a whole should only ever know about
   * setting one factory. However, it is provided in the case where two
   * separate applications somehow end up in the same VM and Classloader
   * instance and they want to play nice with each other. In this case one would
   * query for the existance of the resolver service and then build a
   * delegation model into their own implementation to deal with the existing
   * instance. Then this compound factory is re-set to the manager.
   *
   * @return The currently used factory or <CODE>null</CODE> if none set
   */
  public static URIResolverServiceFactory getURIResolverServiceFactory()
  {
      return RDSManager.getURIResolverServiceFactory();
  }
  
  /**
   * Set the ResourceStream factory that will be used to generate various
   * protocol handlers for use by URIs. The factory may be set multiple times,
   * or removed by passing <CODE>null</CODE> as a parameter.
   *
   * @param fac The factory to be used.
   * @exception SecurityException if the security manager exists and the
   *   set factory operation is not allowed.
   */
  public static void setURIResourceStreamFactory(URIResourceStreamFactory fac)
  {
    ResourceManager.setProtocolHandlerFactory(fac);
  }

  /**
   * Get the currently set resource stream factory. If none is set then
   * <CODE>null</CODE> is returned.
   *
   * @return The currently set factory
   */
  public static URIResourceStreamFactory getURIResourceStreamFactory()
  {
    return ResourceManager.getProtocolHandlerFactory();
  }

  /**
   * Set the Content handler factory that will be used to intepret the content
   * coming from the connection. The factory may be set multiple times
   * or removed by passing <CODE>null</CODE> as a parameter.
   *
   * @param fac The factory to be used.
   * @exception SecurityException if the security manager exists and the
   *   set factory operation is not allowed.
   */
  public static void setContentHandlerFactory(ContentHandlerFactory fac)
  {
    ResourceManager.setContentHandlerFactory(fac);
  }

  /**
   * Get the currently set content handler factory. If none is set then
   * <CODE>null</CODE> is returned.
   *
   * @return The currently set factory
   */
  public static ContentHandlerFactory getContentHandlerFactory()
  {
    return ResourceManager.getContentHandlerFactory();
  }

  /**
   * Set the Filename map factory that will be used to determine MIME types
   * from filenames. The factory may be set multiple times or removed by
   * passing <CODE>null</CODE> as a parameter.
   *
   * @param map The factory to be used.
   * @exception SecurityException if the security manager exists and the
   *   set factory operation is not allowed.
   */
  public static void setFileNameMap(FileNameMap map)
  {
    ResourceManager.setFileNameMap(map);
  }
  /**
   * Get the currently set filename factory. If none is set then
   * <CODE>null</CODE> is returned.
   *
   * @return The currently set factory
   */
  public static FileNameMap getFileNameMap()
  {
    return ResourceManager.getFileNameMap();
  }

  /**
   * Get the scheme from the url string that we've been given. The scheme is
   * determined by the regex (([^:/?#]+):)?. The scheme may be one of the
   * predefined types that are defined as constants for this class.
   *
   * @param uri The uri as a string
   * @return A string representing the scheme, or null if it can't be found
   */
  public static String getScheme(String uri)
  {
    // we do this the hard way using char arrays rather than using a
    // regex package to reduce core dependencies
    String scheme = null;
    int size = uri.length();
    char[] uri_chars = new char[size];

    uri.getChars(0, size, uri_chars, 0);

    int index = 0;

    while((index < size)  &&
          (uri_chars[index] != ':')  &&
          (uri_chars[index] != '/')  &&
          (uri_chars[index] != '?')  &&
          (uri_chars[index] != '#'))
        index++;

    if((index > 0) && (index < size)  &&  (uri_chars[index] == ':'))
      scheme = new String(uri_chars, 0, index).toLowerCase();

    return scheme;
  }

  /**
   * Return the default port used by a given protocol. Just calls
   * <CODE>URIUtils.getDefaultPort()</CODE>
   *
   * @param protocol the protocol
   * @return the port number, or 0 if unknown
   * @deprecated Used URIUtils.getDefaultPort();
   */
  public static int getDefaultPort(String protocol)
  {
    return URIUtils.getDefaultPort(protocol);
  }
}

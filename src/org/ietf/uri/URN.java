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

import java.io.*;
import java.util.*;

/**
 * The embodiment of a Uniform Resource Name (URN) as a class.
 *
 * <b><i>URN SYNTAX:</i></b>
 * <P>
 * URNs take the form:
 * <PRE>
 *   identifier:namespace:namespace specific string</code>
 *   urn:&lt;NID&gt;:&lt;NSS&gt;
 * </PRE>
 * <P>
 * URN example:
 * <PRE>
 *   urn:vrml:umel:texture/wood/oak.gif
 * </PRE>
 * <P>
 * For details on URNs see the IETF working group:
 * <A HREF="http://www.ietf.org/html.charters/urn-charter.html">URN</A>
 * <P>
 *
 * A URN is used to define a resource without specifying where it is to be
 * found. A Resovler Discovery Service is used to discover a resolver that
 * can take the URN and turn it into a particular service. A URN is not
 * always used to extract that actual resource. Other services as defined
 * <A HREF=http://info.internet.isi.edu/in-notes/rfc/files/rfc2168.txt">RFC 2168</A>
 * are available.
 * <P>
 * The system property <CODE>urn.resolve.confirm</CODE> is used to determine
 * the behaviour from here. If the property is <CODE>false</CODE> then it will
 * return the first found resolver without testing it. If the property is
 * set to <CODE>true</CODE> then the URN class will attempt to open the name
 * resource to confirm that it may be fetched from the nominated source. If it
 * cannot, the search continues until one can be or options run out, in which
 * case <CODE>null</CODE> is returned. The default value is <CODE>false</CODE>.
 * It may be overridden on a case by case basis using the appropriate flag in
 * the constructor.
 * <P>
 *
 * <B>Services</B>
 * <P>
 *
 * The methods match the services requests that are defined for URNs in
 * RFC 2168 (although there is a draft update). However, there is some
 * adaptations of the behaviour. RFC2168 defines list services that assume
 * only a single RDS is available for a URN. However, this implementation
 * allows for multiple RDS implementations to be consulted. When requesting
 * lists of things (I2Xs services) the implementation firsts requests the RDSs
 * for all I2Xs resolutions. Following this, it then asks all the resolvers
 * for the singleton versions (I2X services) and adds any found items to the
 * list to return.
 * <P>
 * The idea of this implementation is that although a particular RDS may not
 * be able to return a list of items, a list may be constructed from a
 * collection of single instances - even if this list is only one item long.
 * This raises some fundamental questions regarding resolver services and
 * the differences between a list and a single value (which is a list of
 * length one).
 * <P>
 *
 * <B>Returning Values</B>
 * <P>
 *
 * In a number of the methods it is possible to return a null value and also
 * generate a <CODE>NotsupportedServiceException</CODE>. The exception is
 * generated when no RDS's support the requested service type. If one or more
 * RDS supports the service type but none of them are able to resolve the URN
 * into something more solid, then null is returned.
 *
 * DISCLAIMER:<BR>
 * This software is the under development, incomplete, and is
 * known to contain bugs. This software is made available for
 * review purposes only. Do not rely on this software for
 * production-quality applications or for mission-critical
 * applications.
 * <P>
 * Portions of the APIs for some new features have not
 * been finalized and APIs may change. Some features are
 * not fully implemented in this release. Use at your own risk.
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
public final class URN extends URI
{
  // Variables to hold parts of a parsed URN follow:

  /** The fully urn as passed to this class with whitespace stripped */
  private final String urn;

  /**
   * The name of the organization which defines
   * the rules on name allocation for this URN.
   * Example: "vrml"
   */
  private String namespace;

  /**
   * The namespace specific string.
   * Example: "texture/wood/oak.gif"
   */
  private String reference;

  /**
   * Creates a URN object from the string representation.
   * <P>
   * The default implementation is to not confirm that the request can be
   * satisfied by the first available RDS.
   *
   * @param spec the <code>String</code> to parse as a URN.
   * @exception MalformedURNException  If the spec String specifies an
   *   incomplete or invalid URN.
   */
  public URN (String spec)
    throws MalformedURNException
  {
    this(spec, false);
  }

  /**
   * Create a URN object from the string representation with the ability
   * to control whether the implementation should attempt to confirm that
   * the request can be satisfied.
   *
   * @param spec the <code>String</code> to parse as a URN.
   * @param confirm true if the implentation should confirm access.
   * @exception MalformedURNException  If the spec String specifies an
   *   incomplete or invalid URN.
   */
  public URN(String spec, boolean confirm)
    throws MalformedURNException
  {
    urn = spec.trim();

    try
    {
      // this would be an ideal place to create a URITokenizer class :(

      // first grab the "urn:" part
      if(!urn.regionMatches(true, 0, "urn:", 0, 4))
        throw new MalformedURNException("URN does not start with \"urn:\".");

      // Now the namespace
      int nid_pos = urn.indexOf(':', 4);

      namespace  = urn.substring(4, nid_pos);

      reference = urn.substring(nid_pos + 1);
    }
    catch(NoSuchElementException  nse)
    {
      throw new MalformedURNException("Incomplete URN specified.");
    }
  }

  /**
   * Create a new URN instance from the component parts. Checks to make sure
   * that the NID and NSS are not null.
   *
   * @param nid The namespace identifier portion of this URN
   * @param nss The namespace specific string portion of this URN
   * @throws NullPointerException One of the parameters was null
   */
  public URN(String nid, String nss)
  {
    if((nid == null) || (nss == null))
      throw new NullPointerException("Null portion of URN supplied");

    this.namespace = nid;
    this.reference = nss;

    this.urn = "urn:" + nid + ':' + nss;
  }

  /**
   * Returns the namespace of this <code>URN</code>.
   *
   * @return  the namespace of this <code>URN</code>.
   */
  public String getNamespace()
  {
    return namespace;
  }

  /**
   * Returns the namespace specific portion of this <code>URN</code>.
   *
   * @return  the NSS of this <code>URN</code>.
   */
  public String getNSS()
  {
    return reference;
  }

  /**
   * N2L service request. Get the URL that represents this URN. Whether this
   * results in a legal URL that really represents the URN is dependent on the
   * confirm property.
   * <P>
   * If a URL representation of this URN cannot be found through the resolver
   * service then this method returns null.
   *
   * @return The URL representing this URN or null
   * @exception UnsupportedServiceException Resolution of the requested service
   *   type for this URN is not available
   */
  public URL getURL()
    throws UnsupportedServiceException, IOException
  {
    URNResolverService service =
      RDSManager.getFirstResolver(namespace, URIResolverService.I2L);

    if(service == null)
      throw new UnsupportedServiceException("No URL resolution is available");

    return (URL)service.decode(this, URIResolverService.I2L);
  }

  /**
   * I2Ls service request. Get the list of possible URLs that represents this
   * URN. Whether this results in a legal URL that really represents the URN
   * is dependent on the confirm property.
   * <P>
   * If a URL representation of this URN cannot be found through the resolver
   * service then this method returns null.
   *
   * @return The list of URL representing this URN or null
   * @exception UnsupportedServiceException Resolution of the requested service
   *   type for this URN is not available
   */
  public URL[] getURLList()
    throws UnsupportedServiceException, IOException
  {
    Enumeration services =
      RDSManager.getAllResolvers(namespace, URIResolverService.I2Ls);

    int i;
    boolean list_supported = (services != null);
    URNResolverService resolver;
    Object[] urls;
    LinkedList found_resources = new LinkedList();
    HashMap res_map = new HashMap();

    // Loop through all of the returned services and see if they can
    // resolve this particular URN. If they can then this is added to the
    // list of items. If not, then it is ignored and the next call made.
    while(services.hasMoreElements())
    {
      try
      {
        resolver = (URNResolverService)services.nextElement();
      }
      catch(NoSuchElementException nsee)
      {
        // we've reached the end of the list so there's no point continuing
        // the search.
        break;
      }

      try
      {
        urls = resolver.decodeList(this, URIResolverService.I2Ls);

        if(urls != null)
        {
          // Add all of the found resources. We don't want to duplicate
          // the outputs of two resolvers giving the same resource, so
          // we check them before adding it to the list.
          //
          // This works nicely for URIs, but ResourceConnections and URCs
          // are a bit of an unknown quantity at the moment.
          for(int j = 0; j < urls.length; j++)
          {
            if(!res_map.containsKey(urls[j].toString()))
            {
              found_resources.add(urls[j]);
              res_map.put(urls[j].toString(), urls[j]);
            }
          }
        }
      }
      catch(UnsupportedServiceException use)
      {
        // ignore and move on.
      }
    }


    // Now check to see if we can add extra using the single resource.
    services = RDSManager.getAllResolvers(namespace, URIResolverService.I2L);

    if((services == null) && !list_supported)
      throw new UnsupportedServiceException("URL lists are not available");

    Object a_url;

    // This time we're looking through the list of single resolvers available
    while(services.hasMoreElements())
    {
      try
      {
        resolver = (URNResolverService)services.nextElement();
      }
      catch(NoSuchElementException nsee)
      {
        // we've reached the end of the list so there's no point continuing
        // the search.
        break;
      }

      try
      {
        a_url = resolver.decode(this, URIResolverService.I2L);
        if((a_url != null) && !res_map.containsKey(a_url.toString()))
        {
          found_resources.add(a_url);
          res_map.put(a_url.toString(), a_url);
        }
      }
      catch(UnsupportedServiceException use)
      {
        // ignore and move on.
      }
    }

    // Now this is all done, just turn the linked list into an array and
    // return the items.
    URL[] ret_val = new URL[found_resources.size()];
    found_resources.toArray(ret_val);

    return ret_val;
  }

  /**
   * I2C service request. Get the first URC that describes this URN.
   * If a URC cannot be determined for this URN then null is returned.
   *
   * @return The URC describing this URN.
   * @exception UnsupportedServiceException Resolution of the requested service
   *   type for this URN is not available
   */
  public URC getURC()
    throws UnsupportedServiceException, IOException
  {
    URNResolverService service =
      RDSManager.getFirstResolver(namespace, URIResolverService.I2C);

    if(service == null)
      throw new UnsupportedServiceException("No URL resolution is available");

    return (URC)service.decode(this, URIResolverService.I2C);
  }

  /**
   * N2C service request. Get the list of URCs that describes this URN.
   * If a URC cannot be determined for this URN then null is returned.
   *
   * @return The URC describing this URN.
   * @exception UnsupportedServiceException Resolution of the requested service
   *   type for this URN is not available
   */
  public URC[] getURCList()
    throws UnsupportedServiceException, IOException
  {
    Enumeration services =
      RDSManager.getAllResolvers(namespace, URIResolverService.I2Cs);

    int i;
    boolean list_supported = (services != null);
    URNResolverService resolver;
    Object[] urcs;
    LinkedList found_resources = new LinkedList();

    // Loop through all of the returned services and see if they can
    // resolve this particular URN. If they can then this is added to the
    // list of items. If not, then it is ignored and the next call made.
    while(services.hasMoreElements())
    {
      try
      {
        resolver = (URNResolverService)services.nextElement();
      }
      catch(NoSuchElementException nsee)
      {
        // we've reached the end of the list so there's no point continuing
        // the search.
        break;
      }

      try
      {
        urcs = resolver.decodeList(this, URIResolverService.I2Cs);

        if(urcs != null)
        {
          // GRRRR. Need to write an ArrayCollection to make this easy.
          for(int j = 0; j < urcs.length; j++)
            found_resources.add(urcs[j]);
        }
      }
      catch(UnsupportedServiceException use)
      {
        // ignore and move on.
      }
    }

    // Now check to see if we can add extra using the single resource.
    services = RDSManager.getAllResolvers(namespace, URIResolverService.I2C);

    if((services == null) && !list_supported)
      throw new UnsupportedServiceException("URC lists are not available");

    Object a_urc;

    // This time we're looking through the list of single resolvers available
    while(services.hasMoreElements())
    {
      try
      {
        resolver = (URNResolverService)services.nextElement();
      }
      catch(NoSuchElementException nsee)
      {
        // we've reached the end of the list so there's no point continuing
        // the search.
        break;
      }

      try
      {
        a_urc = resolver.decode(this, URIResolverService.I2C);
        if((a_urc != null) && !found_resources.contains(a_urc))
          found_resources.add(a_urc);
      }
      catch(UnsupportedServiceException use)
      {
        // ignore and move on.
      }
    }

    // Now this is all done, just turn the linked list into an array and
    // return the items.
    URC[] ret_val = new URC[found_resources.size()];
    found_resources.toArray(ret_val);

    return ret_val;
  }

  /**
   * I2N service request. Get the first URN that describes this URI.
   * If a URN cannot be determined for this URI then null is returned. Returns
   * a reference to itself.
   *
   * @return The list of equivalent URNs
   * @exception UnsupportedServiceException Resolution of the requested service
   *   type for this URN is not available
   */
  public URN getURN()
    throws UnsupportedServiceException, IOException
  {
    return this;
  }

  /**
   * N2Ns service request. Get the list of possible URNs that are also
   * equivalent descriptors of this resource. If no alternate representations
   * are available, then null is returned.
   *
   * @return The list of equivalent URNs
   * @exception UnsupportedServiceException Resolution of the requested service
   *   type for this URN is not available
   */
  public URN[] getURNList()
    throws UnsupportedServiceException, IOException
  {
    Enumeration services =
      RDSManager.getAllResolvers(namespace, URIResolverService.I2Ns);

    int i;
    boolean list_supported = (services != null);
    URNResolverService resolver;
    Object[] urns;
    LinkedList found_resources = new LinkedList();

    // Loop through all of the returned services and see if they can
    // resolve this particular URN. If they can then this is added to the
    // list of items. If not, then it is ignored and the next call made.
    while(services.hasMoreElements())
    {
      try
      {
        resolver = (URNResolverService)services.nextElement();
      }
      catch(NoSuchElementException nsee)
      {
        // we've reached the end of the list so there's no point continuing
        // the search.
        break;
      }

      try
      {
        urns = resolver.decodeList(this, URIResolverService.I2Ns);
        if(urns != null)
        {
          // GRRRR. Need to write an ArrayCollection to make this easy.
          for(int j = 0; j < urns.length; j++)
            found_resources.add(urns[j]);
        }
      }
      catch(UnsupportedServiceException use)
      {
        // ignore and move on.
      }
    }

    // Now check to see if we can add extra using the single resource.
    services = RDSManager.getAllResolvers(namespace, URIResolverService.I2N);

    if((services == null) && !list_supported)
      throw new UnsupportedServiceException("URN lists are not available");

    Object a_urn;

    // This time we're looking through the list of single resolvers available
    while(services.hasMoreElements())
    {
      try
      {
        resolver = (URNResolverService)services.nextElement();
      }
      catch(NoSuchElementException nsee)
      {
        // we've reached the end of the list so there's no point continuing
        // the search.
        break;
      }

      try
      {
        a_urn = resolver.decode(this, URIResolverService.I2N);
        if(a_urn != null)
          found_resources.add(a_urn);
      }
      catch(UnsupportedServiceException use)
      {
        // ignore and move on.
      }
    }

    // Now this is all done, just turn the linked list into an array and
    // return the items.
    URN[] ret_val = new URN[found_resources.size()];
    found_resources.toArray(ret_val);

    return ret_val;
  }

  /**
   * Establish a connection to the named resource. This partially maps the
   * N2R service request in providing a representation of the connection to
   * the resource without actually supply the resource itself. The resource
   * itself can be then obtained using the methods of the ResouceConnection
   * class.
   *
   * @return A reference to the connection to the resource.
   * @exception UnsupportedServiceException Resolution of the requested service
   *   type for this URN is not available
   * @see ResourceConnection
   */
  public ResourceConnection getResource()
    throws UnsupportedServiceException, IOException
  {
    URNResolverService service =
      RDSManager.getFirstResolver(namespace, URIResolverService.I2R);

    if(service == null)
      throw new UnsupportedServiceException("No URL resolution is available");

    return (ResourceConnection)service.decode(this, URIResolverService.I2R);
  }

  /**
   * Establish a connection to all possible resolutions of this resource. This
   * partially maps to the N2Rs service request in providing all possible
   * representations of the URN without actually fetching the resource itself.
   * The resources themselves may be obtained using the methods of the
   * ResourceConnection class.
   *
   * @return The list of connections to resources
   * @exception UnsupportedServiceException Resolution of the requested service
   *   type for this URN is not available
   * @see ResourceConnection
   */
  public ResourceConnection[] getResourceList()
    throws UnsupportedServiceException, IOException
  {
    Enumeration services =
      RDSManager.getAllResolvers(namespace, URIResolverService.I2Rs);

    int i;
    boolean list_supported = (services != null);
    URNResolverService resolver;
    Object[] res_list;
    LinkedList found_resources = new LinkedList();

    // Loop through all of the returned services and see if they can
    // resolve this particular URN. If they can then this is added to the
    // list of items. If not, then it is ignored and the next call made.
    while(services.hasMoreElements())
    {
      try
      {
        resolver = (URNResolverService)services.nextElement();
      }
      catch(NoSuchElementException nsee)
      {
        // we've reached the end of the list so there's no point continuing
        // the search.
        break;
      }

      try
      {
        res_list = resolver.decodeList(this, URIResolverService.I2Rs);
        if(res_list != null)
        {
          // GRRRR. Need to write an ArrayCollection to make this easy.
          for(int j = 0; j < res_list.length; j++)
            found_resources.add(res_list[j]);
        }
      }
      catch(UnsupportedServiceException use)
      {
        // ignore and move on.
      }
    }

    // Now check to see if we can add extra using the single resource.
    services = RDSManager.getAllResolvers(namespace, URIResolverService.I2R);

    if((services == null) && !list_supported)
      throw new UnsupportedServiceException("Resource lists are not available");

    Object a_res;

    // This time we're looking through the list of single resolvers available
    while(services.hasMoreElements())
    {
      try
      {
        resolver = (URNResolverService)services.nextElement();
      }
      catch(NoSuchElementException nsee)
      {
        // we've reached the end of the list so there's no point continuing
        // the search.
        break;
      }

      try
      {
        a_res = resolver.decode(this, URIResolverService.I2R);
        if(a_res != null)
          found_resources.add(a_res);
      }
      catch(UnsupportedServiceException use)
      {
        // ignore and move on.
      }
    }

    // Now this is all done, just turn the linked list into an array and
    // return the items.
    ResourceConnection[] ret_val =
      new ResourceConnection[found_resources.size()];

    found_resources.toArray(ret_val);

    return ret_val;
  }

  /**
   * Create a string representation of the URI.
   *
   * @return The string representation of this object
   */
  public String toExternalForm()
  {
    return urn;
  }

  /**
   * Test for equality between this URN and any other URI. Implements the I=I
   * service request. If the object is not a URI then false is immediately
   * returned otherwise the alternate version of this method is called.
   *
   * @param o The object to compare against
   * @return true if they are equivalent URI
   */
  public boolean equals(Object o)
  {
    if(o instanceof URI)
      return equals((URI)o);
    else
      return false;
  }

  /**
   * Test for equality between this URN and any other URI. Implements the I=I
   * service request.
   *
   * @param uri The URI to compare against
   * @return true if they are equivalent URI
   */
  public boolean equals(URI uri)
  {
    // tbd!  should use I=I service
    if(!(uri instanceof URN))
      return false;

    URN other = (URN)uri;

    // very simplistic implementation for the moment.
    return (other.namespace.equals(namespace) &&
            other.reference.equals(reference));
  }

  /**
   * Provide a String representation of this URN. Calls toExternalForm()
   *
   * @return A string representation of the urn
   */
  public String toString()
  {
    return toExternalForm();
  }

  /**
   * Add a specific URN resolver to the system. If the resolver is already
   * registered, the request is ignored. Note that this is appended to the
   * list so registration order is important.
   *
   * @param resolver The resolver to add
   */
  public static void addResolver(URNResolverService resolver)
  {
    RDSManager.addURNResolver(resolver);
  }

  /**
   * Add a specific URN resolver to the system. If the resolver is already
   * registered, the request is ignored. Note that this is appended to the
   * list so registration order is important.
   *
   * @param resolver The resolver to add
   */
  public static void removeResolver(URNResolverService resolver)
  {
      RDSManager.removeURNResolver(resolver);
  }
}

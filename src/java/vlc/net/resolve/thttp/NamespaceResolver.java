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

package vlc.net.resolve.thttp;

// Standard imports
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;

// Application specific imports
import org.ietf.uri.*;

import org.ietf.uri.resolve.UnknownNIDException;

/**
 * Interface describing a generic resolver for a specific URN namespace.
 * <P>
 *
 * For each namespace we need to be able to resolve the particular resource.
 * This interface is used to describe a generic, per namespace resolver that
 * lies between the full URN RDS resolver and the individual resource
 * connection.
 * <P>
 * It assumes that there is only one resolver provided per namespace per
 * URIResovlerService. That is, there may be multiple ways of resolving
 * a particular namespace but there is only one instance of this class per
 * resolver implementation.
 * <P>
 * Provided mainly as a convenience interface if needed for a particular
 * URNResolver server. Not used directly within this package.
 *
 * <P>
 * For details on URIs see the IETF working group:
 * <A HREF="http://www.ietf.org/html.charters/urn-charter.html">URN</A>
 * The specification off a THTTP based RDS implementation may be found at
 * <A HREF="http://www.vlc.com.au/~justin/java/urn/thttp_based_resolver.html">
 * http://www.vlc.com.au/~justin/java/urn/thttp_based_resolver.html</A>
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
public class NamespaceResolver
{
  private static final String URI_RES_QUERY = "/uri-res/";

  private static final int[] ALL_SERVICES = {
    URIResolverService.I2R,
    URIResolverService.I2Rs,
    URIResolverService.I2L,
    URIResolverService.I2Ls,
    URIResolverService.I2N,
    URIResolverService.I2Ns,
    URIResolverService.I2C,
    URIResolverService.I2Cs,
    URIResolverService.II
  };

  /** The namespace ID that this resolver is used with */
  private String nid;

  // Temporary maps used in the building process
  private ArrayList i2l_map = new ArrayList();
  private ArrayList i2ls_map = new ArrayList();

  private ArrayList i2n_map = new ArrayList();
  private ArrayList i2ns_map = new ArrayList();

  private ArrayList i2c_map = new ArrayList();
  private ArrayList i2cs_map = new ArrayList();

  private ArrayList i2r_map = new ArrayList();
  private ArrayList i2rs_map = new ArrayList();

  private ArrayList ii_map = new ArrayList();

  // The real list of resolvers once the setup is complete
  private ResourceDescriptor[] i2l_resolvers;
  private ResourceDescriptor[] i2ls_resolvers;

  private ResourceDescriptor[] i2n_resolvers;
  private ResourceDescriptor[] i2ns_resolvers;

  private ResourceDescriptor[] i2c_resolvers;
  private ResourceDescriptor[] i2cs_resolvers;

  private ResourceDescriptor[] i2r_resolvers;
  private ResourceDescriptor[] i2rs_resolvers;

  private ResourceDescriptor[] ii_resolvers;

  /** Flag to keep track of whether the setup method has been called */
  private boolean setup_done = false;

  /**
   * Create a resolver for the given namespace.
   */
  public NamespaceResolver(String namespace)
  {
    this.nid = namespace;
  }

  /**
   * Get the namespace that this resolver handles
   *
   * @return The namespace identifier
   */
  String getNID()
  {
    return nid;
  }

  /**
   * Add a resource to the namespace ID. Resources are added in order of
   * preference starting with the highest preference first. If the services
   * list is null it is assumed that all services are provided by that host.
   * <P>
   * Any invalid services in the string are ignored, but does not effect
   * the registering of this url, unless there are no valid services.
   *
   * @param hostPort The host and port definition
   * @param services The list of services
   */
  void addResource(String hostPort, String services)
  {
    int[] real_service;

    if(services != null)
      real_service = parseServices(services.toUpperCase());
    else
      real_service = ALL_SERVICES;

    // Parse the host and port information for later use.
    String server = null;
    int port = -1;

    int index = hostPort.lastIndexOf(':');

    if(index == -1)
    {
      server = hostPort;
    }
    else
    {
      server = hostPort.substring(0, index);
      try
      {
        port = Integer.parseInt(hostPort.substring(index + 1));
      }
      catch(NumberFormatException nfe)
      {
        // let it go because it defaults to -1
      }
    }

    // now lets go through that list of services and build up descriptors.
    int i;
    for(i = 0; i < real_service.length; i++)
    {
      ResourceDescriptor desc = new ResourceDescriptor();
      desc.server = server;
      desc.port = port;
      desc.path = new StringBuffer(URI_RES_QUERY);

      switch(real_service[i])
      {
        case URIResolverService.I2R:
            desc.path.append("N2R?");
            i2r_map.add(desc);
            break;

        case URIResolverService.I2Rs:
            desc.path.append("N2RS?");
            i2rs_map.add(desc);
            break;

        case URIResolverService.I2L:
            desc.path.append("N2L?");
            i2l_map.add(desc);
            break;

        case URIResolverService.I2Ls:
            desc.path.append("N2LS?");
            i2ls_map.add(desc);
            break;

        case URIResolverService.I2N:
            desc.path.append("N2N?");
            i2n_map.add(desc);
            break;

        case URIResolverService.I2Ns:
            desc.path.append("N2Ns?");
            i2ns_map.add(desc);
            break;

        case URIResolverService.I2C:
            desc.path.append("N2C?");
            i2c_map.add(desc);
            break;

        case URIResolverService.I2Cs:
            desc.path.append("N2CS?");
            i2cs_map.add(desc);
            break;

        case URIResolverService.II:
            desc.path.append("II?");
            ii_map.add(desc);
            break;
      }

      // A few cleanup issues
      desc.pathLength = desc.path.length();
    }
  }

  /**
   * Parse the services string and return the list of services that these
   * describe. The ints are the values of the constants defined in the
   * URIResovlerService interface. Assumes the services list contains at least
   * one service definition. If there is a definition that we do not understand
   * then we let it fall through the cracks and clean up any mess at the end.
   *
   * @param services The + encoded list of services
   * @return The list of services
   */
  private int[] parseServices(String services)
  {
    StringTokenizer strtok = new StringTokenizer(services, "+");

    int index = 0;
    String token;
    int[] ret_vals = new int[strtok.countTokens()];

    while(strtok.hasMoreTokens())
    {
      try
      {
        token = strtok.nextToken();

        if(token.equals("N2R") || token.equals("I2R"))
          ret_vals[index++] = URIResolverService.I2R;
        else if(token.equals("N2RS") || token.equals("I2RS"))
          ret_vals[index++] = URIResolverService.I2Rs;
        else if(token.equals("N2L") || token.equals("I2L"))
          ret_vals[index++] = URIResolverService.I2L;
        else if(token.equals("N2LS") || token.equals("I2LS"))
          ret_vals[index++] = URIResolverService.I2Ls;
        else if(token.equals("N2N") || token.equals("I2N"))
          ret_vals[index++] = URIResolverService.I2N;
        else if(token.equals("N2NS") || token.equals("I2NS"))
          ret_vals[index++] = URIResolverService.I2Ns;
        else if(token.equals("N2C") || token.equals("I2C"))
          ret_vals[index++] = URIResolverService.I2C;
        else if(token.equals("N2CS") || token.equals("I2CS"))
          ret_vals[index++] = URIResolverService.I2Cs;
        else if(token.equals("II"))
          ret_vals[index++] = URIResolverService.II;

        // the default case does not add anything at all.
      }
      catch(NoSuchElementException nse)
      {
        // ignore and continue
      }
    }

    if(ret_vals.length != index)
    {
      int[] tmp = new int[index];
      System.arraycopy(ret_vals, 0, tmp, 0, index);
      ret_vals = tmp;
    }

    return ret_vals;
  }

  /**
   * Indicate that the startup routines have completed and to deal with the
   * optomisation issues.
   */
  void setupFinished()
  {
    if(setup_done)
      return;

    ResourceDescriptor[] template = new ResourceDescriptor[0];

    i2l_resolvers = (ResourceDescriptor[])i2l_map.toArray(template);
    i2ls_resolvers = (ResourceDescriptor[])i2ls_map.toArray(template);

    i2n_resolvers = (ResourceDescriptor[])i2n_map.toArray(template);
    i2ns_resolvers = (ResourceDescriptor[])i2ns_map.toArray(template);

    i2c_resolvers = (ResourceDescriptor[])i2c_map.toArray(template);
    i2cs_resolvers = (ResourceDescriptor[])i2cs_map.toArray(template);

    i2r_resolvers = (ResourceDescriptor[])i2r_map.toArray(template);
    i2rs_resolvers = (ResourceDescriptor[])i2rs_map.toArray(template);

    ii_resolvers = (ResourceDescriptor[])ii_map.toArray(template);

    i2l_map = null;
    i2ls_map = null;

    i2n_map = null;
    i2ns_map = null;

    i2c_map = null;
    i2cs_map = null;

    i2r_map = null;
    i2rs_map = null;

    ii_map = null;

    setup_done = true;
  }

  /**
   *
   *
   * @param urn The URN that this service should decode.
   * @return A description of the resource needed
   * @exception UnknownNIDException The urn's namespace does not match the
   *   one assigned for this class.
   */
  public ResourceDescriptor decode(URN urn, int type)
    throws UnknownNIDException,
           UnresolvableURIException
  {
    String namespace = urn.getNamespace();

    if(!nid.equals(namespace))
      throw new UnknownNIDException();

    ResourceDescriptor ret_val;

    switch(type)
    {
      case URIResolverService.I2R:
          ret_val = i2r_resolvers[0];
          break;

      case URIResolverService.I2Rs:
          ret_val = i2rs_resolvers[0];
          break;

      case URIResolverService.I2L:
          ret_val = i2l_resolvers[0];
          break;

      case URIResolverService.I2Ls:
          ret_val = i2ls_resolvers[0];
          break;

      case URIResolverService.I2N:
          ret_val = i2n_resolvers[0];
          break;

      case URIResolverService.I2Ns:
          ret_val = i2ns_resolvers[0];
          break;

      case URIResolverService.I2C:
          ret_val = i2c_resolvers[0];
          break;

      case URIResolverService.I2Cs:
          ret_val = i2cs_resolvers[0];
          break;

      case URIResolverService.II:
          ret_val = ii_resolvers[0];
          break;

      default:
          throw new UnresolvableURIException("Invalid service type");
    }

    return ret_val;
  }

  /**
   * Decode the namespace specific string into a list of resources and
   * retrieve them
   *
   * @param urn The URN that this service should decode.
   * @return The list of URLs describing the URN
   * @exception UnknownNIDException The urn's namespace does not match the
   *   one assigned for this class.
   * @exception UnresolvableURIException The type supplied was invalid
   */
  public ResourceDescriptor[] decodeList(URN urn, int type)
    throws UnknownNIDException, UnresolvableURIException
  {
    String namespace = urn.getNamespace();

    if(!nid.equals(namespace))
      throw new UnsupportedServiceException();

    ResourceDescriptor[] ret_val;

    switch(type)
    {
      case URIResolverService.I2R:
          ret_val = i2r_resolvers;
          break;

      case URIResolverService.I2Rs:
          ret_val = i2rs_resolvers;
          break;

      case URIResolverService.I2L:
          ret_val = i2l_resolvers;
          break;

      case URIResolverService.I2Ls:
          ret_val = i2ls_resolvers;
          break;

      case URIResolverService.I2N:
          ret_val = i2n_resolvers;
          break;

      case URIResolverService.I2Ns:
          ret_val = i2ns_resolvers;
          break;

      case URIResolverService.I2C:
          ret_val = i2c_resolvers;
          break;

      case URIResolverService.I2Cs:
          ret_val = i2cs_resolvers;
          break;

      case URIResolverService.II:
          ret_val = ii_resolvers;
          break;

      default:
          throw new UnresolvableURIException("Invalid service type");
    }

    return ret_val;
  }

  /**
   * String representation of this class
   *
   * @return A string representation of this class
   */
  public String toString()
  {
    return "Namespace resolver for " + nid;
  }
}

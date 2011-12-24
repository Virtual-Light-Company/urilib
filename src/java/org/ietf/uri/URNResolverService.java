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

/**
 * Class <code>URN</code> represents a URN Resolver service (RS).
 * <P>
 *
 * A resolver service is used to turn a namespace specific string into a
 * resource. Thus for the current implementation it only implements the N2R
 * scheme.
 * <P>
 *
 * All resolvers must implement this interface regardless of the how the process
 * is implemented.
 * <P>
 *
 * This interface supplements the basic list of URI services that are
 * required with a list of new types supported by a URN resolver.
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
public interface URNResolverService extends URIResolverService
{
  /**
   * Check that this RDS can resolve the given namespace identifier. If if can
   * then return true.
   *
   * @param nid The namespace identifier to check
   * @return true if this RDS can resolve the given NID.
   */
  public boolean canResolve(String nid);

  /**
   * Decode the namespace specific string into a particular resource
   * and retrieve it. The service may not be able to resolve the requested
   * service type, although before asking a check should be made of the base
   * class.
   *
   * @param urn The URN that this service should decode.
   * @param service The type of object that should be returned
   * @return A URC, URI or ResourceConnection as appropriate
   * @exception UnsupportedServiceException The service requested is not
   *   available from this resolver.
   */
  public Object decode(URN urn, int service)
    throws UnsupportedServiceException;

  /**
   * Decode the namespace specific string into a list of resources and
   * retrieve them. The service may not be able to resolve the requested
   * service type, although before asking a check should be made of the base
   * class.
   * <P>
   * If the implementation cannot resolve the URN then it should return
   * <CODE>null</CODE> to the caller. If it can find a list then the 
   * return value is an array of that type (ie <CODE>URC[]</CODE>) allowing
   * it to be cast directly to the correct type.
   *
   * @param urn The URN that this service should decode.
   * @param service The type of object that should be returned
   * @return A URC, URI or ResourceConnections as appropriate
   * @exception UnsupportedServiceException The service requested is not
   *   available from this resolver.
   */
  public Object[] decodeList(URN urn, int service)
    throws UnsupportedServiceException;
}

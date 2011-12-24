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
 * Factory interface for finding Resolver Discovery Service implementations.
 * <P>
 *
 * A resolver discovery service is used to find a resolver for a particular
 * name space. This is found through a process of query and analysis on a bunch
 * of resources until a particular resolver may be found. This class is used to
 * provide a lookup facility for a given RDS type.
 * <P>
 *
 * The format of the string does not follow any particular type, but for
 * convention should be the name of the service type. For example the type
 * &quot;dns&quot; would be used to request the DNS RDS implementation.
 * <P>
 *
 * All types are case insensitive.
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
public interface URIResolverServiceFactory
{
  /**
   * Discover the RDS implementation for the given keyword type. The type
   * should be matched in a case insensitive fashion. The factory implementation
   * should <I>not</I> call the <CODE>init()</CODE> method of the 
   * {@link org.ietf.uri.URIResolverService} before it is returned here
   * because the RDSManager takes care of that for us.
   *
   * @param type The RDS type to find
   * @return The resolver service or null if it cannot find one.
   */
  public URIResolverService findResolverService(String type);
}

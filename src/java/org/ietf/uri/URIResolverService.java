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

import org.ietf.uri.resolve.ConfigErrorException;

/**
 * A representation of a URI Resolver service (RS).
 * <P>
 *
 * A resolver service is used to turn a namespace specific string into a
 * resource. Thus for the current implementation it only implements the N2R
 * scheme.
 *
 * All resolvers must implement this interface regardless of the how the process
 * is implemented.
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
public interface URIResolverService
{
  /** URI to URL service request type */
  public static final int I2L =  1;

  /** URI to list of URLs service request type */
  public static final int I2Ls = 2;

  /** URI to resource service request type */
  public static final int I2R =  3;

  /** URI to list of resources service request type */
  public static final int I2Rs = 4;

  /** URI to URC service request type */
  public static final int I2C =  5;

  /** URL to list of URCs service request type */
  public static final int I2Cs = 6;

  /** URI to URN service request type */
  public static final int I2N =  7;

  /** URI to list of URNs service request type */
  public static final int I2Ns = 8;

  /** Compatibility test for URI == URI service request type */
  public static final int II =   9;

  /**
   * Perform any initialisation that all instances of this implementation
   * would need to perform. This method is called exactly once for all
   * instances of a particular implementation and is designed to replace
   * the <CODE>static</CODE> initialiser block. The reason for this is that
   * we want to trap certain errors and know that there were startup problems
   * rather than having static throw random exceptions that we can't handle.
   *
   * @throws ConfigErrorException Something was stuffed in the global
   *   configuration for this resolver type (eg syntax errors in the config
   *   file)
   */
  public void init()
    throws ConfigErrorException;
  
  /**
   * Set the resource factory to be used by the resolver. This will be called
   * once only during the life of this class instance. If the implementation
   * wishes to ignore this then it shall not throw any errors.
   *
   * @param fac The factory to be set.
   */
  public void setResourceFactory(URIResourceFactory fac);

  /**
   * Check that the list of services that this resolver provides matches the
   * requested one.
   *
   * @param type The type of service this RDS requires
   * @return true if the RDS can handle the nominated type.
   */
  public boolean checkService(int type);
}

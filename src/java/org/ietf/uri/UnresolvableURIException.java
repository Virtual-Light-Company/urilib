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

import java.io.IOException;

/**
 * Thrown to indicate that the file describing the URI could not be resolved to
 * a real, existing file as a URL.
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
public class UnresolvableURIException extends IOException
{
    /**
     * Constructs a <code>UnresolvableURIException</code> with no detail message.
     */
    public UnresolvableURIException()
    {
    }

    /**
     * Constructs a <code>UnresolvableURIException</code> with the
     * specified detail message.
     *
     * @param   msg   the detail message.
     */
    public UnresolvableURIException(String msg)
    {
        super(msg);
    }
}
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

package org.ietf.uri.resolve;

/**
 * Thrown to indicate that the resolver had some problem with it's startup
 * or processing of its configuration that prevents it from operating
 * properly.
 * <P>
 * Helpful utility exception used in the resolver internals.
 * <P>
 *
 * For details on URIs see the IETF working group:
 * <A HREF="http://www.ietf.org/html.charters/urn-charter.html">URN</A>
 * The specification on a File based RDS implementation may be found at
 * <A HREF="http://www.vlc.com.au/~justin/java/urn/file_based_resolver.html">
 * http://www.vlc.com.au/~justin/java/urn/file_based_resolver.html</A>
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
public class ConfigErrorException extends Exception
{
    /**
     * Constructs a <code>ConfigErrorException</code> with no detail message.
     */
    public ConfigErrorException()
    {
    }

    /**
     * Constructs a <code>ConfigErrorException</code> with the
     * specified detail message.
     *
     * @param   msg   the detail message.
     */
    public ConfigErrorException(String msg)
    {
        super(msg);
    }
}

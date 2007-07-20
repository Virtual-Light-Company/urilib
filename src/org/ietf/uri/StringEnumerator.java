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

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * A convenience implementation of an Enumeration for an array of strings.
 * <P>
 *
 * Being an enumeration, it is a one shot at the list of items. Once set, it
 * cannot be re-used to create a new list.
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
class StringEnumerator
  implements Enumeration
{
  /** The array of strings that we are dealing with */
  private String[] values;

  /** The current position in the array */
  private int current_pos = 0;

  /**
   * Create a new instance of this class with the given array of strings.
   * Assumes that the array is non-null.
   *
   * @param values The array of strings to enumerate through
   */
  StringEnumerator(String[] values)
  {
    this.values = values;
  }

  /**
   * Test to see if the enumeration has any more values left to be read.
   *
   * @return True if there are more values to be read. False if not
   */
  public boolean hasMoreElements()
  {
    return current_pos < values.length;
  }

  /**
   * Request for the next element in the list. If there is nothing left then
   * throw the exception.
   *
   * @return The next element (String) in the list
   * @throws NoSuchElementException Nothing left in the list
   */
  public Object nextElement()
  {
    if(!hasMoreElements())
      throw new NoSuchElementException();

    current_pos++;

    return values[current_pos - 1];
  }
}

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

package vlc.net.resolve.file;

// Standard imports
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import gnu.regexp.*;

// Application specific imports
import org.ietf.uri.*;

/**
 * A data holder class for dealing with a single resource.
 * <P>
 *
 * The holder class does all the manipulation required for each resource.
 * On setup, it does validity checking of all data. On query, it generates
 * the completed URL as required.
 * <P>
 * There currently are implementation problems with the regex handling. It
 * does not like handling standard punctuation chars in the replacement string
 * with the substitute in there. It attempts to read them as regex expressions.
 * Even when '\' escaped it does not seem to like it :(
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
class Resource
{
  /** The base URL that we are dealing with */
  private String url;

  /** The regular expression to change regular expression to something useful */
  private RE url_regex;

  /** The replacement string */
  private String replacement;

  /**
   * Create a new resource and check to see everything is OK. If not, barf
   *
   * @param url The source URL to work with
   * @param regex The regular expression to associate with this URL
   * @exception InvalidRegexException The format of the regex was not correct
   */
  Resource(String url, String regex)
    throws InvalidRegexException
  {
    this.url = url;

    checkRegex(regex);
  }

  /**
   * Check the regular expression needed to extract deal with a URL. If it is
   * not of the correct format then barf. Otherwise exit quietly.
   *
   * @param regexp The regular expression to set.
   * @exception InvalidRegexException The Regexp did not fit the required
   *   pattern.
   */
  private void checkRegex(String regex)
    throws InvalidRegexException
  {
    int i;
    RE     re;
    REMatch[] matches;
    boolean check_case = true;

    char delimiter = regex.charAt(0);
    String tokenizer_regex =
      "^" + delimiter + "(.*)" + delimiter + "(.*)" + delimiter + "(.*)" + "$";

    try
    {
      re = new RE(tokenizer_regex, 0, RESyntax.RE_SYNTAX_POSIX_EXTENDED);

      // Check the syntax to first make sure that the regex that has been
      // passed in is in fact of the correct form. If not, then barf.
      matches = re.getAllMatches(regex);

      if(matches.length == 0)
      {
        System.out.println("Invalid RE syntax");
        throw new InvalidRegexException("Invalid pattern");
      }

      regex = matches[0].substituteInto("$1");
      replacement = matches[0].substituteInto("$2");
      String flag_str = matches[0].substituteInto("$3");

      boolean case_sensitive = (flag_str != null) && (flag_str.equals("i"));

      try
      {
        if(case_sensitive)
          url_regex = new RE(regex, 0, RESyntax.RE_SYNTAX_POSIX_EXTENDED);
        else
          url_regex = new RE(regex,
                             RE.REG_ICASE,
                             RESyntax.RE_SYNTAX_POSIX_EXTENDED);
      }
      catch(Exception e)
      {
          // means there was something stuffed in the regular expression
          // definition.
          throw new InvalidRegexException("Invalid substitution style");
      }

      // Check the repl string for '\' characters and replace them with '$'
      // We do this the hard way in the raw character array as it is much
      // faster and less likely to get the RE stuff confused.
      int len = replacement.length();
      char[] repl_chars = new char[len];
      replacement.getChars(0, len, repl_chars, 0);

      len--;  // stop one before the end with the checks

      for(i = 0; i < len; i++)
      {
        if(repl_chars[i] == '\\')
        {
          // quick check for legality
          if((!Character.isDigit(repl_chars[i + 1])) ||
             (repl_chars[i + 1] == '0'))
            throw new InvalidRegexException("Illegal Back reference of \'" +
                                            repl_chars[i + 1] + "\'");

          repl_chars[i] = '$';
        }
      }

//      System.out.println("repl string is now " + String.valueOf(repl_chars));
      replacement = String.valueOf(repl_chars);
    }
    catch(REException e)
    {
      System.out.println("Exception: " + e);
    }
  }

  /**
   * Resolve the URL into something meaningful.
   *
   * @param urn The urn to use as the source
   * @return The fully qualified URL
   * @exception MalformedURLException The source URL is stuffed
   */
  String resolve(String urn)
    throws MalformedURLException
  {
    // strip out the domain name info from the URI based on the pattern
    REMatch uri_match = url_regex.getMatch(urn);

//  System.out.println("Checking against url_regex " + url_regex);

    if(uri_match == null)
      throw new MalformedURLException("Cannot generate a match");

//    System.out.println("Match (" + uri_match.getStartIndex()
//         + "," + uri_match.getEndIndex() + "): "
//         + uri_match);

    String tail = uri_match.substituteInto(replacement);
    StringBuffer buffer = new StringBuffer(url);
    buffer.append(tail);

//  System.out.println("Final version is " + buffer.toString());
    
    return buffer.toString();
  }
}

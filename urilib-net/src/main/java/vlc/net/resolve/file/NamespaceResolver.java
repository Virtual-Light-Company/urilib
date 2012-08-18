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

package vlc.net.resolve.file;

// Standard imports

import java.net.MalformedURLException;
import java.util.HashMap;

import gnu.regexp.*;

// Application specific imports
import org.ietf.uri.*;
import org.ietf.uri.resolve.UnknownNIDException;

/**
 * Interface describing a generic resolver for a specific URN namespace.
 * <p/>
 * <p/>
 * For each namespace we need to be able to resolve the particular resource. This interface is used
 * to describe a generic, per namespace resolver that lies between the full URN RDS resolver and the
 * individual resource connection.
 * <p/>
 * It assumes that there is only one resolver provided per namespace per URIResovlerService. That
 * is, there may be multiple ways of resolving a particular namespace but there is only one instance
 * of this class per resolver implementation.
 * <p/>
 * Provided mainly as a convenience interface if needed for a particular URNResolver server. Not
 * used directly within this package.
 * <p/>
 * <p/>
 * For details on URIs see the IETF working group: <A HREF="http://www.ietf.org/html.charters/urn-charter.html">URN</A>
 * The specification on a File based RDS implementation may be found at <A
 * HREF="http://www.vlc.com.au/~justin/java/urn/file_based_resolver.html">
 * http://www.vlc.com.au/~justin/java/urn/file_based_resolver.html</A>
 * <p/>
 * <p/>
 * This softare is released under the <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <p/>
 * <p/>
 * DISCLAIMER:<BR> This software is the under development, incomplete, and is known to contain bugs.
 * This software is made available for review purposes only. Do not rely on this software for
 * production-quality applications or for mission-critical applications.
 * <p/>
 * <p/>
 * Portions of the APIs for some new features have not been finalized and APIs may change. Some
 * features are not fully implemented in this release. Use at your own risk.
 * <p/>
 *
 * @author Justin Couch
 * @version 0.7 (27 August 1999)
 */
public class NamespaceResolver
{
    private String nid;

    // stuff for the regular expression handling.
    private RE grp_regex;
    private String replacement;

    private HashMap groups_list;

    private GroupResource current_group;

    /** Create a resolver for the given namespace. */
    public NamespaceResolver(String namespace)
    {
        this.nid = namespace;
        groups_list = new HashMap();
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
     * Set the regular expression needed to extract a group ID from the given URL.
     *
     * @param regex The regular expression to set.
     * @throws InvalidRegexException The Regexp did not fit the required pattern.
     */
    void setNidRegex(String regex)
        throws InvalidRegexException
    {
        int i;
        RE re;
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

            if(case_sensitive)
                grp_regex = new RE(regex, 0, RESyntax.RE_SYNTAX_POSIX_EXTENDED);
            else
                grp_regex = new RE(regex, RE.REG_ICASE, RESyntax.RE_SYNTAX_POSIX_EXTENDED);

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
                    if((!Character.isDigit(repl_chars[i + 1])) || (repl_chars[i + 1] == '0'))
                        throw new InvalidRegexException(
                            "Illegal Back reference of \'" + repl_chars[i + 1] + "\'");

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
     * Add a group to the namespace resolver. This now becomes the current group that any resources
     * add from here on become part of. We create a group that is empty and does not contain any
     * resources at this stage.
     *
     * @param gid The group identifier to add
     */
    void addGroup(String gid)
    {
        if(gid == null)
            throw new NullPointerException("Empty group list");

        GroupResource grp = new GroupResource(gid);
        groups_list.put(gid, grp);
        current_group = grp;
    }

    /**
     * Add a resource to the currently set group. If no group has been set then an error is generated.
     * Resources are added in order of preference starting with the highest preference first.
     *
     * @param url The core Url string to use
     * @param regex The regular expression for this resource
     * @throws NoSuchGroupException No group has been set yet
     * @throws InvalidRegexException The Regexp did not fit the required pattern.
     */
    void addResource(String url, String regex)
        throws NoSuchGroupException, InvalidRegexException
    {
        if(current_group == null)
            throw new NoSuchGroupException();

//    System.out.println("Setting resource: ");
//    System.out.println("  URL: " + url);
//    System.out.println("  regex: " + regex);

        current_group.addResource(url, regex);
    }

    /**
     * Decode the namespace specific string into a particular resource and retrieve the URL describing
     * it.
     *
     * @param urn The URN that this service should decode.
     * @return A URL describing the URN
     * @throws UnknownNIDException The urn's namespace does not match the one assigned for this
     * class.
     * @throws UnresolvableURIException Error resolving the URN
     */
    public String decode(URN urn)
        throws UnknownNIDException,
               UnresolvableURIException
    {
        String namespace = urn.getNamespace();

        if(!nid.equals(namespace))
            throw new UnsupportedServiceException();

        namespace = extractGroupName(urn.toExternalForm());

        GroupResource res = (GroupResource)groups_list.get(namespace);

        if(res == null)
            throw new UnknownNIDException();

        String ret_val = null;

        try
        {
            ret_val = res.resolveSingle(urn.toExternalForm());
        }
        catch(MalformedURLException mue)
        {
            throw new UnresolvableURIException();
        }

        return ret_val;
    }

    /**
     * Decode the namespace specific string into a list of resources and retrieve them
     *
     * @param urn The URN that this service should decode.
     * @return The list of URLs describing the URN
     * @throws UnknownNIDException The urn's namespace does not match the one assigned for this
     * class.
     */
    public String[] decodeList(URN urn)
        throws UnknownNIDException
    {
        String namespace = urn.getNamespace();

        if(!nid.equals(namespace))
            throw new UnsupportedServiceException();

        namespace = extractGroupName(urn.toExternalForm());

        GroupResource res = (GroupResource)groups_list.get(namespace);

        if(res == null)
            throw new UnknownNIDException();

        String[] ret_val = res.resolveAll(urn.toExternalForm());

        return ret_val;
    }

    /**
     * Fetch the group space from the urn using the given rules
     *
     * @param urn The urn to extract the group space from
     * @return The group name according to the regex
     */
    private String extractGroupName(String urn)
    {
        // strip out the domain name info from the URI based on the pattern
        REMatch grp_match = grp_regex.getMatch(urn);

//      System.out.println("Match (" + grp_match.getStartIndex()
//         + "," + grp_match.getEndIndex() + "): "
//         + grp_match);

        return grp_match.substituteInto(replacement);
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

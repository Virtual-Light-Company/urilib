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
import java.util.ArrayList;

import gnu.regexp.*;

// Application specific imports
import org.ietf.uri.*;

/**
 * A data holder class for dealing with resources on a group level.
 * <p/>
 * <p/>
 * The holder class does all the manipulation required for each group. On setup, it does validity
 * checking of all data. On query, it generates the completed URLs as required.
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
class GroupResource
{
    private String group;

    private ArrayList resource_list;

    /**
     * Create a resource holder for the nominated group.
     *
     * @param grp The name of the group
     */
    GroupResource(String grp)
    {
        this.group = grp;

        resource_list = new ArrayList();
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
        throws InvalidRegexException
    {
//    System.out.println("Setting resource: ");
//    System.out.println("  URL: " + url);
//    System.out.println("  regex: " + regex);

        // check the regex. If it is invalid this will throw an exception and
        // exit the method directly
        resource_list.add(new Resource(url, regex));
    }

    /**
     * Fetch just a single version of the resource. This takes the resource from the top of the list,
     * generates the appropriate fully qualified URL and returns that value.
     *
     * @param urn The urn that we need to rip apart
     * @return The fully qualified URL matching this URN.
     * @throws MalformedURLException An erroroneous URL was created
     */
    String resolveSingle(String urn)
        throws MalformedURLException
    {
        Resource res = (Resource)resource_list.get(0);

        return res.resolve(urn);
    }

    /**
     * Fetch all the resources that have been generated for this URN. This takes all of the set
     * resources and generates complete URLs for them.
     *
     * @param urn The urn that we need to rip apart
     * @return The fully qualified URLs matching this URN.
     */
    String[] resolveAll(String urn)
    {
        int i, size;
        int ret_cnt = 0;

        size = resource_list.size();
        String[] ret_vals = new String[size];
        String url;
        Resource res;

        for(i = 0; i < size; i++)
        {
            try
            {
                res = (Resource)resource_list.get(i);
                url = res.resolve(urn);
                ret_vals[ret_cnt++] = url;
            }
            catch(MalformedURLException mue)
            {
                // ignore it because we'll move to the next one
            }
        }

        // quick check to see if we need to reallocate the size of the array
        // that is being returned.
        if(ret_cnt != size)
        {
            String[] tmp = new String[ret_cnt];
            System.arraycopy(ret_vals, 0, tmp, 0, ret_cnt);
            ret_vals = tmp;
        }

        return ret_vals;
    }
}

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

package vlc.net.resolve.thttp;

// Standard imports

import java.io.*;

import java.util.HashMap;

// Application specific imports
import org.ietf.uri.*;

import org.ietf.uri.resolve.ConfigErrorException;

/**
 * A file parser for the raw URN bindings information.
 * <p/>
 * <p/>
 * For details on URIs see the IETF working group: <A HREF="http://www.ietf.org/html.charters/urn-charter.html">URN</A>
 * The specification on a HTTP based RDS implementation may be found at <A
 * HREF="http://www.vlc.com.au/~justin/java/urn/thttp_based_resolver.html">
 * http://www.vlc.com.au/~justin/java/urn/thttp_based_resolver.html</A>
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
class FileParser
{
    // Strings representing parts of the file
    private static final String NID_TOKEN_STR = "NID:";
    private static final String RES_TOKEN_STR = "RES:";

    // State constants for reading the file
    private static final int NID_TOKEN = 1;
    private static final int NID_ID = 2;
    private static final int RES_TOKEN = 3;
    private static final int RES_URL_STR = 4;
    private static final int RES_SERVICE_STR = 5;

    /**
     * Parse the contents of the file and return a collection of namespace resolvers that do the job.
     *
     * @param is The stream providing the raw bytes
     * @return A list of the namespace resolvers found
     * @throws IOException An error occurred parsing the file
     * @throws ConfigErrorException There was a syntax error reading the configuration file.
     */
    public static HashMap parseBindingsFile(InputStream is)
        throws IOException, ConfigErrorException
    {
        InputStreamReader isr = new InputStreamReader(is);
        return parseBindingsFile(isr);
    }

    /**
     * Parse the bindings file that is available on the nominated reader.
     *
     * @param rdr The reader to source the file contents from
     * @return A list of the namespace resolvers found
     * @throws IOException Some I/O error occurred
     * @throws ConfigErrorException There was a syntax error reading the configuration file.
     */
    public static HashMap parseBindingsFile(Reader rdr)
        throws IOException, ConfigErrorException
    {
        StreamTokenizer strtok = new StreamTokenizer(rdr);

        // just some setup stuff
        strtok.eolIsSignificant(false);
        strtok.wordChars('+', '+');
        strtok.wordChars(':', ':');
        strtok.commentChar('#');

        String token;
        String res_url_string = null;
        int state = NID_TOKEN;
        NamespaceResolver resolver = null;
        boolean quote_expected = false;
        boolean resolver_added = false;

        HashMap namespace_list = new HashMap();

        // Typically state driven looping parser to deal with the file.
        // Build up namespace resolvers one at a time based on the contents of
        // the file.
        //
        // A valid resolver is one that contains at least the namespace ID, and at
        // least one resource. If we find this, then the resolver is added to the
        // list of current resolvers that are returned to the caller. If we don't
        // meet these conditions, then the resolver is not added, and may well be
        // replaced by the next resolver instance that is created.
        while(strtok.nextToken() != StreamTokenizer.TT_EOF)
        {
            if(strtok.ttype != StreamTokenizer.TT_WORD)
            {
                System.out.println("Invalid token (" + strtok.nval +
                                       ") found in file on line " + strtok.lineno());
                continue;
            }

            token = strtok.sval;

            switch(state)
            {
                // If we've read the token, and it is the correct string, then
                // change state to read the identifier. If not, don't change state
                // but toss what we've done. Hopefully the next token might be
                // the correct one. If not, effectively this skips everything until
                // a new namespace can be found
                case NID_TOKEN:
                    if(token.equalsIgnoreCase(NID_TOKEN_STR))
                        state = NID_ID;
                    else
                    {
                        System.out.println("Invalid token for NID def " + token +
                                               " on line " + strtok.lineno());
                        clearToNextNID(strtok);
                    }
                    break;

                // Just take whatever we are given as a string and make that into
                // the namespace that we're dealing with. This could cause some
                // problems if the string read is one of the standard tokens. We
                // should check and barf if it is.
                case NID_ID:
                    // cleanup the old one if needed
                    if(resolver != null)
                        resolver.setupFinished();
                    resolver = new NamespaceResolver(token);
                    resolver_added = false;
                    state = RES_TOKEN;
                    break;

                // Read a resource. Now a resource may well start a new group
                // or even namespace so we need to check and take the appropriate
                // actions here. Basically all we do is push the item back onto
                // the tokeniser and set the state. We don't try to deal with it
                // ourselves because that could lead to maintenance problems.
                case RES_TOKEN:
                    if(NID_TOKEN_STR.equals(token))
                    {
                        strtok.pushBack();
                        state = NID_TOKEN;
                    }
                    else
                    {
                        if(token.equalsIgnoreCase(RES_TOKEN_STR))
                        {
                            state = RES_URL_STR;
                        }
                        else
                        {
                            System.out.println("Invalid token for Resource def " + token +
                                                   " on line " + strtok.lineno());
                            clearToNextResource(strtok);
                        }
                    }
                    break;

                // Read a URL from the stream. This could be interesting because
                // hopefully things don't interpret quotes the wrong way!
                case RES_URL_STR:
                    res_url_string = token;
                    state = RES_SERVICE_STR;
                    break;

                // Now add the completed resource to the namespace resolver. If the
                // resolver has not yet been added to the list, then add it to it now.
                case RES_SERVICE_STR:
                    String services = null;

                    if(NID_TOKEN_STR.equals(token))
                    {
                        strtok.pushBack();
                        state = NID_TOKEN;
                    }
                    else
                        services = token;

                    try
                    {
                        resolver.addResource(res_url_string, services);
                        if(!resolver_added)
                        {
                            namespace_list.put(resolver.getNID(), resolver);
                            resolver_added = true;
                        }
                    }
                    catch(Exception e)
                    {
                        // hmmm... shouldn't have anything happen here
                        String msg = "unexpected error in THTTP parser " + e;
                        throw new ConfigErrorException(msg);
                    }
                    finally
                    {
                        state = RES_TOKEN;
                    }
                    break;

                default: // WAAAHHH!!! what am I going to do here????
            }
        }

        // make sure the last one has been cleaned up
        if(resolver != null)
            resolver.setupFinished();

        return namespace_list;
    }

    /**
     * Check to see if the given string is the same as one of the keywords. If so, return true.
     *
     * @param The word to check
     * @return True if it is a keyword, false if not
     */
    private static boolean checkIsKeyword(String word)
    {
        // there are probably much faster ways of doing this!
        return (NID_TOKEN_STR.equalsIgnoreCase(word) ||
                    RES_TOKEN_STR.equalsIgnoreCase(word));
    }

    /**
     * Clear the tokenizer until we come to the next NID keyword or end of file.
     *
     * @param strtok The stream tokenizer instance to clear
     */
    private static void clearToNextNID(StreamTokenizer strtok)
        throws IOException
    {
        while(strtok.nextToken() != StreamTokenizer.TT_EOF)
            if(strtok.sval.equals(NID_TOKEN_STR))
            {
                strtok.pushBack();
                break;
            }
    }

    /**
     * Clear the tokenizer until we come to the next NID, GROUP or RES keyword or end of file.
     *
     * @param strtok The stream tokenizer instance to clear
     * @param token The token that we should stop clearing the input on
     */
    private static void clearToNextResource(StreamTokenizer strtok)
        throws IOException
    {
        while(strtok.nextToken() != StreamTokenizer.TT_EOF)
            if(strtok.sval.equals(RES_TOKEN_STR) ||
                strtok.sval.equals(NID_TOKEN_STR))
            {
                strtok.pushBack();
                break;
            }
    }

/*****

 private static final String URN_BINDINGS_FILE = "thttp_urn.conf";

 public static void main(String[] args)
 {
 // read in and process the urn config file
 InputStream is = ClassLoader.getSystemResourceAsStream(URN_BINDINGS_FILE);

 if(is == null)
 {
 System.out.println("Couldn't find config file");
 System.exit(1);
 }

 try
 {
 FileParser.parseBindingsFile(is);
 }
 catch(IOException ioe)
 {
 System.out.println("Shit " + ioe);
 }
 }
 ******/

}


                         URN implementation V0.7

                   (c) The Virtual Light Company 1999


This is the third public, widely announced release of the URI code.
As such, there are still a few things missing functionality-wise. Except
for the missing FTP support you should now be able to replace all your
java.net.calls with this code. It is almost feature complete and is
running in a commercial test environment. I want as much feedback as
possible - good, bad or otherwise.


Licensing
-----------

This code is released under the GNU LGPL. If you wish to redistribute
this code then you must include the license.txt file as is.

The original copy of this code can be found at

http://www.vlc.com.au/urn/

This code makes use of other 3rd party GNU libraries.

GNU Regexp v1.0.6 can be found at:
  http://www.cacas.org/~wes/java/

The HTTP Client code v0.3-1 can be found at
  http://www.innovation.ch/java/HTTPClient/
(note that the code included this distribution is modified - see below).

The GNU LGPL can be found in license.txt or at http://www.gnu.org/lgpl.html

#include <std_disclaimer.h>


Installing
-----------

Just extract everything from the zip file. You will find that there are
three JAR files and a bunch of other files (including this one!).

uri.jar
gun_regexp_106.jar
httpclient.jar

Place all of these into your classpath.

You will also find 3 files:

urn.conf
urn_bindings
thttp_conf

These _must_ be placed somewhere in your classpath so that they can be found
when the application executes. If they are not there, any time that you
attempt to call a method on a URI, an exception will be generated.

Once you've done this, then you may import the classes under
org.ietf.uri.* and run code with them. The source for the libraries are
kept in the individual JAR files.

This code makes very heavy use of JDK 1.2 so you need that to be running
for this library to work.

Add your own naming schemes to the urn_bindings file. Comments indicate
where to find the syntax definition.

To generate javadocs run make_docs.[sh|bat] for your platform. The
unix shell script is untested though!

Image loading:

As a cross plug, if you want a lot of image loading of files through URLs that
actually returns you an image, you might want to check out the imageloader
classes that I also have available. These work as both standard java.net
content handlers and content handlers for this system.

http://www.vlc.com.au/~justin/java/images/


Philosophy
-----------

The idea of these classes is to completely replace everything in java.net.
In your application, everywhere that you reference java.net.URL change that
to org.ietf.uri.URL. There are some other differences that are better
in behaviour, so it is not a completely pluggable replacement.

At the bottom end, a lot more things are configurable and perform the way
that you expect them to behave. You can put multiple FileNameMaps and
multiple factory implemenations for content and protocol handler (some parts
not implemented yet). You can fetch the currently set factory and I don't
do anything stupid like return an inner class that points to the internal
reference instead of the actual reference (you get lovely circular method
call loops when you try to build a new class that takes the old as a parameter
and fallback lookup).

I've made the best attempt possible at seamless integration with the
java.net classes to ease migration hassles. You can use the same content
handlers and filename maps. Unfortunately you cannot use the protocol
handlers due to a design decision by Sun stopping an external class from
fetching URLConnections. There are public wrapper classes for everything
that it is possible to use.

There are a number of minor and potentially troubling aspects with this
that may not allow full use of the java.net stuff. Read the comments at
the top of JavaNetURLConnectionWrapper for when the code deals with URNs.

Changes
--------

Absolutely heaps - See changes.txt

Changes to HTTPClient code:

In order to work nicely, there are two minor mods that I did to the standard
HTTPClient implementation that means it won't run (or runs with errors) from
the standard distributions. Both of these have been submitted back to the
maintainer but his next release won't be for a few months yet.
 - Made the RedirectionModule public where it was package private before
 - Added a dontProxyForHosts(String[]) method to HTTPConnection to bulk add
   a number of non-proxiable hosts.

TODO
-----

- Build lots of content handlers! The default one is for text/plain. I
will shortly have image loaders available. Stay tuned.
  o Class loaders to deal with URNs. Need a class loader to extract
    and build classes from the downloaded array of bytes.
  o handler for HTML needed
  o handler for application/multipart needed

- Protocol handlers for:
  o FTP
  o fix SHTTP and HTTPS for authentication

- Resolvers:
 o Complete the DNS based resolver based on NetSol code (Problem is that
   its license doesn't allow commercial use - which is a major pain)
 o JINI based resolver

- Authorisation callback scheme needed for dealing with password/authenticated
sites

- Cookie callback scheme for HTTP handler

Justin Couch, 27 August 1999
couch@ccis.adisys.com.au (work)
justin@vlc.com.au (home)

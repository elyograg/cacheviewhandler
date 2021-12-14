# cacheviewhandler

As of mid-December 2021, this handler requires changes to Solr source code in order to function.

If you want to edit this code, it imports into eclipse without trouble.  It will probably also import into IntelliJ idea and other IDEs that can handle Java gradle projects.

## Tools needed to build customized Solr:

* A POSIX operating system.  Linux is known to work.
* Perl 5.x.
* Ant.  RPM-based distros may require downloading a separate ant install.
* Git.  A recent version is strongly recommended.
* A recent 8.x source code tree pulled from git.

## Building this package:

* If you're using Solr 8.11.x, you can probably skip the next step and its sub-steps:
* Obtain correct solr-core jar:
    * Delete the included solr-core jar from the lib directory.
    * Build custom Solr as outlined below.
    * Copy custom solr-core jar from the freshly built dist directory to this tree's lib directory.
* Use "./gradlew clean assemble" to build.

## Installing this package:

* Build the package as outlined above.
* Create a directory called lib in the Solr Home.  This is typically where solr.xml and core directories live.  Copy the jar ending in -plain from the build/libs directory to that new lib directory.
* In a core's solrconfig.xml, add this line next to other requestHandler definitions:
    * <requestHandler name="/admin/info/cache" class="org.elyograg.solr.handler.CacheViewHandler" />
* To use, load the following URL, substiting correct values for the all-uppercase parts:
    * http://SERVER:PORT/sorl/CORE/admin/info/cache?cache=filter
    * So far, the only cache that is available is filter.

## Building a custom Solr:

* Change to a suitable directory to contain the lucene-solr directory.
* Type the following command.  This will download over 500MB:
    * git clone https://github.com/apache/lucene-solr.git
* Change into the newly created directory with "cd lucene-solr".
* Pick a branch to build, and use "git checkout XXX" where XXX is the branch name.
    * For the latest dev branch, use branch_8_11 for XXX.
    * For version 8.10.0, use releases/lucene-solr/8.10.0 for XXX.
    * If you want to see what branches are available, run "git ls-remote | less" to see branch names.  The "refs/XXXXX" part of the branch name should be omitted when using the "git checkout" command.
    * Apply the patch to make Solr work with this handler:
        * git apply -3 /path/to/solr-cache-add-keyset.patch
        * If used with older Solr 8.x code branches, the patch may refuse to apply.  Manual editing of the code may be required.
* Change into the solr directory with "cd solr".
* Build the package.  If you're not building 8.11.1, substitute the correct version.  If the version is omitted, the files will all have "-SNAPSHOT" appended to the version number.  That may be perfectly fine for your setup, and if it is, you can omit the -Dversion parameter entirely:
    * ant -Dversion=8.11.1 clean package
* If the build succeeds, you will find packages in the "package" directory that can be used and installed just like an official Solr download.
* <mark>The only part of a Solr install that is changed from stock with this patch is the solr-core jar.  If you need to update an existing install (particularly a docker setup) without completely reinstalling, you can take the new solr-core jar from dist and replace the one in server/solr-webapp/webapp/WEB-INF/lib</mark>

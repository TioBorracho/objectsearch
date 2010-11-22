# Object Search

Object Search it's a Java framework that enables you to "google" your application objects.

This framework is designed for:
- Ease of use: indexing and retrieval in less than 10 lines of code
- Speed: in memory retrieval exceeds 20k queries/sec. on commodity hardware
- Integration: automatic Hibernate & iBATIS mirroring to indexes via interceptors
- Customization: it's easy to do custom sorting, filtering, faceted search & relevance formulas


## Were do I start?
1. Download the latest release (JAR) or build it from the source code.
2. Check out the tests or one of the 3 samples webapps that shows basic usage.

Soon: there will be a Maven dependency on public repos and a complete documentation with examples. Please be patient (or help ;)! )

## Im using Solr, Lucene, Hibernate Search, Compass Project, what are the biggest differences?

The biggest differences are:
* Object vs. Documents: while Lucene is *document* oriented, object search is *object* oriented (we index objects and it's relationships).
* Only One Framework: this framework is autocontained from one end to the other.

On the other side, all of the above frameworks are based on Lucene and sooner or later you'll end up working with 2 frameworks: Lucene and it's wrapper.
* Object Query: the query doens't have to be plain text, you can provide a domain object like the ones you've indexed.

When comparing to Compass or Hibernate Search, you'll find that we are not as mature as those projects, that means that some features aren't implemented yet.

## Community

Join the mailing-list at [http://groups.google.com/group/objectsearch](http://groups.google.com/group/objectsearch)

## Ok.. so what can I do with Object Search?
All of the following are supported:

Mapping capabilities:
* Annotation or programatic mapping
* Can class hierarchies, collections & associations (referencing container, contained or both objects)
* Language can be specified per class, instance or field (this allows you process texts using the correct stemmer)

Searching capabilities:
* Boolean search (AND, OR & NOT)
* Vector Model search (simple TF-IDF)
* Custom sorting, windowing & filtering

Indexing capabilities:
* Memory & disk indexing (via BerkeleyDB)
* Offline/Distributed Indexing via JMS
* Hibernate & iBATIS integration via plugins
* Text Processing: word splitting, snowball stemming, weird character removal, etc.
* Index can store fields for sorting, filtering or similar

## How do I use it?

You can download the latests build at: 
    http://github.com/jklas/objectsearch

Or use it as a maven dependency (sorry for the 'snapshot'... it'll become a release soon):

	<dependency>
		<groupId>com.jklas</groupId>
		<artifactId>search</artifactId>
		<version>1.1-SNAPSHOT</version>
	</dependency>


To use it (minimalistic demo):


	public class ObjectSearchSample {

	  	@Indexable
   		public static class Foo {
			@SearchId public long id ;

			@SearchField public String bar;

			public Foo(int id, String bar) {
				this.id = id ; this.bar = bar;
			}
			//.. al you'd expect
   		}
	
   		public static void main(String[] args) throws Exception {
			// map objects to search engine
			SearchLibrary.configureAndMap(Foo.class);

			// create indexer w/memory index
			IndexerService indexerService = new DefaultIndexerService(new DefaultIndexingPipeline(), MemoryIndexWriterFactory.getInstance());

			// create your stuff..
			Foo foo = new Foo(1,"The quick brown fox jumps over the lazy dog");

			// index it!
			indexerService.create(foo);

			// it's time to create a search query
			VectorQuery query = new VectorQueryParser("lazy").getQuery();		

			// and a search over your memory index
			VectorSearch search = new VectorSearch( query, MemoryIndexReaderFactory.getInstance() );
		
			// do it!
			List<VectorRankedResult> results = search.search(new DefaultVectorRanker());

			// finally... let's see what comes out
			System.out.println("Your query retrieved the object ID: "+results.get(0).getKey().getId());	
   		}
	}    

For more usage examples check the tests.

## I want to contribute!

That is great! Just fork the project in github. Create a topic branch, write some tests and the feature that you wish to contribute.

To run the tests:

	mvn test

or you can use eclipse

Thanks for helping!

## License

  Object Search Framework
 
  Copyright (C) 2010 Julian Klas
 
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.
 
  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.
 
  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

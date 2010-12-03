OegyScroll
==========

From LaughingPanda
------------------

OegyScroll (or org.laughingpanda.LazyLoadScrollableList) is a Wicket
component for displaying long lists of data in a scrollable view. The
idea is that at first only the first 100 rows are loaded to the browser
and more rows are loaded while you scroll. This way a list of 20000
items can easily be shown in a browser, because the initial markup of
the page contains only some hundreds of elements. Still, the page
behaves as if all the data was loaded immediately. The user can freely
scroll through the data and will only experience a short delay before
the actual data appears.

New: JQuery client-side library
-------------------------------

Using the client-side library, you can enjoy OegyScrolling without
Wicket, too. Have a look at [this humble demo page][demo] to get the
idea!

[demo]: http://juhajasatu.com/oegydemo/

How To Install It?
------------------

If you're using Maven 2, just add the following dependency block into your POM file:

	<code>
	  <dependency>
	    <groupId>org.laughingpanda.oegyscroll</groupId>
	    <artifactId>oegyscroll</artifactId>
	    <version>1.0-SNAPSHOT</version>
	  </dependency>
	</code>

..or you can just clone the source and build it yourself.

Dependencies
------------

* Wicket 1.4 (can easily be backported to 1.3.x)
* Tested on Firefox 3.0, IE 6.0, IE 7.0

How To Use It?
--------------

It's designed to be an easy replacement for [DataView][]. You supply
your data using an implementation of [data provider][IDataProvider] such
as [ListDataProvider][]. 

Like with DataView, you have to override the populateRow method where
you add the components for each actual data row.

You need some custom HTML in your markup too, where you specify the
markup for the placeholders and the data rows. You should use CSS styles
to ensure that the height of the placeholder equals row height times
block size.

Please have a look at the [OegyScroll demo][] and I'm sure you'll get
the idea. If you do a full checkout of OegyScroll, you'll also get the
runnable demo, which has a "run" script for running it in Jetty.

[DataView]: http://wicket.apache.org/docs/1.4/org/apache/wicket/markup/repeater/data/DataView.html
[IDataProvider]: http://wicket.apache.org/docs/1.4/org/apache/wicket/markup/repeater/data/IDataProvider.html
[ListDataProvider]: http://wicket.apache.org/docs/1.4/org/apache/wicket/markup/repeater/data/ListDataProvider.html
[OegyScroll demo]: https://github.com/reaktor/oegyscroll/tree/master/oegyscroll-demo

How Does It Work?
-----------------

It splits your data into blocks of 100 items (yes, you can specify block
size too) and at first, only shows a bunch of rows (actually the
remainder of rowcount divided by block size). For the rest of the rows,
it puts placeholder components in the markup and replaces them with
actual rows when the placeholder gets visible in the browser. The
visibility check is done using a piece of javascript that is run once a
second and checks if there are placeholders that should be replaced with
data rows. The javascript invokes the onclick behaviour attached to the
placeholder, and the behavior replaces the placeholder with row data
using Ajax.

Developers
----------

* Juha Paananen
* Antti Viljakainen

Version Control
---------------

* Github: [https://github.com/reaktor/oegyscroll](https://github.com/reaktor/oegyscroll)
* git: `git://github.com/reaktor/oegyscroll.git`


License
-------

Copyright © 2009 original author or authors

OegyScroll is Licensed under the
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

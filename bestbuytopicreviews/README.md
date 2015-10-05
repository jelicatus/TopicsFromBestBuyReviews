# bestbuytopicreviews

## About Topics from best buy reviews

On the first page, user types in the form the product for which he wants to find out topics mostly discussed in reviews, and after clicking the "Search for product" button, BestBuy API is called to retrieve all the product that contain in their longDescription section the key word user typed in the form and have more than 300 reviews, and those products and basic information about them are shown on the screen.

For each product that is shown on the screen, there is an option to find out topics from their reviews. After clicking the "Find out topics" button, BestBuy API is called to retrieve all the reviews for selected product and after this, reviews that were collected are transformed into form needed to perform NMF method and extract the topics. After this, NMF method is performed and 4 topics are shown in tag cloud (where the size of word indicates their relevance to the topic) with 15 words per topic.

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## Application realization

This application was written in programming language Clojure using [Luminus](http://www.luminusweb.net/) framework. LightTable was used as code editor and run and stop commands to the server were issued via OS command line.

## Acknowledgements

This application has been developed as a part of the project assignment for the course [Tools and methods of software engineering](http://ai.fon.bg.ac.rs/master/alati-i-metode-softverskog-inzenjerstva/) in master degree program Software engineering at the [Faculty of Organization Sciences](http://www.fon.bg.ac.rs/), University of Belgrade, Serbia.

/*
 * Copyright 2021-2021 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cdevreeze.tqajavaexamples.console;

import eu.cdevreeze.tqa.base.dom.TaxonomyElem;
import eu.cdevreeze.tqa.base.taxonomy.BasicTaxonomy;
import eu.cdevreeze.tqa.base.taxonomybuilder.TaxonomyBuilder;
import scala.jdk.javaapi.CollectionConverters;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

/**
 * Program that shows the memory footprint of a loaded taxonomy, showing the number of taxonomy files and
 * relationships as well.
 * <p>
 * This program gets a taxonomy package ZIP file path as first parameter (requiring a META-INF/catalog.xml
 * file, but not needing the taxonomyPackage.xml file), and one or more entrypoint URIs as the remaining
 * parameters.
 * <p>
 * Boolean system properties useSaxon (default true) and lenient (default false) can be used to influence
 * the TQA taxonomy bootstrapping.
 * <p>
 * This program then calls method "showTaxonomyMemoryFootprint" to show the memory footpring before and
 * after loading the TQA taxonomy. See the documentation of that method for more specific information
 * about its parameters.
 *
 * @author Chris de Vreeze
 */
public class ShowTaxonomyMemoryFootprint {

    private static Logger logger = Logger.getGlobal(); // Typically use slf4j instead

    private ShowTaxonomyMemoryFootprint() {
        // Cannot be instantiated
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            throw new RuntimeException("Usage: ShowDimensions <taxonomy package ZIP file> <entry point URI 1> ...");
        }

        boolean useSaxon = Boolean.parseBoolean(System.getProperty("useSaxon", "true"));
        boolean lenient = Boolean.parseBoolean(System.getProperty("lenient", "false"));

        try (ZipFile zipFile = new ZipFile(new File(args[0]))) {
            Set<URI> entryPointUris = Arrays.stream(args).skip(1).map(u -> URI.create(u)).collect(Collectors.toSet());

            showTaxonomyMemoryFootprint(entryPointUris, zipFile, useSaxon, lenient);
        }
    }

    /**
     * Loads a taxonomy as TQA BasicTaxonomy, and logs the heap memory footprint before and after loading
     * the taxonomy. The latter logging gives an idea of the TQA taxonomy footprint.
     * <p>
     * This method has as parameters a set of entrypoint URIs (typically containing just one entrypoint URI),
     * a ZIP file containing the taxonomy, a useSaxon boolean and a lenient boolean.
     * <p>
     * The ZIP file must be closed under DTS discovery rules, starting with the entrypoint. Like a taxonomy
     * package, it must at least have a META-INF/catalog.xml file mapping the original URIs to local file
     * URIs. Taxonomy packages must also have a taxonomyPackage.xml file, but this method does not use that
     * file, but it does require the META-INF/catalog file.
     * <p>
     * Boolean useSaxon determines if Saxon (tiny tree) backed DOM trees are used, or native yaidom indexed ones.
     * The former ones are far more efficient with respect to memory usage.
     * <p>
     * Boolean lenient determines if strict relationship computation is done during TQA BasicTaxonomy creation.
     */
    public static void showTaxonomyMemoryFootprint(Set<URI> entryPointUris, ZipFile taxonomyPackage, boolean useSaxon, boolean lenient) {
        String entryPointUrisString = entryPointUris.stream().map(s -> s.toString()).collect(Collectors.joining(", "));
        logger.info(String.format("Starting building the DTS with entry point(s) %s", entryPointUrisString));

        showHeapMemoryUsage("Heap memory usage before loading the taxonomy: ");

        TaxonomyBuilder taxoBuilder = ConsoleUtil.createTaxonomyBuilder(taxonomyPackage, useSaxon, lenient);
        BasicTaxonomy basicTaxo = taxoBuilder.build(CollectionConverters.asScala(entryPointUris).toSet());

        showHeapMemoryUsage("Heap memory usage just after loading the taxonomy: ");

        List<TaxonomyElem> rootElems = CollectionConverters.asJava(basicTaxo.taxonomyBase().rootElems());

        logger.info(String.format("The taxonomy has %s taxonomy root elements", rootElems.size()));
        logger.info(String.format(
                "The taxonomy has %s taxonomy XML elements in total",
                rootElems.stream().map(e -> e.findAllElemsOrSelf().size()).reduce(0, Integer::sum)));
        logger.info(String.format(
                "The taxonomy has %s relationships",
                basicTaxo.relationships().size()));
        logger.info(String.format(
                "The taxonomy has %s dimensional relationships",
                basicTaxo.findAllDimensionalRelationships().size()));

        showHeapMemoryUsage("Heap memory usage after querying the taxonomy: ");

        assert basicTaxo.findAllDimensionalRelationships().size() <= basicTaxo.findAllInterConceptRelationships().size();

        logger.info("Ready");
    }

    private static void showHeapMemoryUsage(String messagePrefix) {
        MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = mbean.getHeapMemoryUsage();

        logger.info(messagePrefix + memoryUsage.toString());
    }
}

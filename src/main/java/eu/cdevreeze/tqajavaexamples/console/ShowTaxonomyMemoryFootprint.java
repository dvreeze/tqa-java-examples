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

        ZipFile zipFile = new ZipFile(new File(args[0]));

        Set<URI> entryPointUris = Arrays.stream(args).skip(1).map(u -> URI.create(u)).collect(Collectors.toSet());

        boolean useSaxon = Boolean.parseBoolean(System.getProperty("useSaxon", "true"));
        boolean lenient = Boolean.parseBoolean(System.getProperty("lenient", "false"));

        String entryPointUrisString = entryPointUris.stream().map(s -> s.toString()).collect(Collectors.joining(", "));
        logger.info(String.format("Starting building the DTS with entry point(s) %s", entryPointUrisString));

        showHeapMemoryUsage("Heap memory usage before loading the taxonomy: ");

        TaxonomyBuilder taxoBuilder = ConsoleUtil.createTaxonomyBuilder(zipFile, useSaxon, lenient);
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

        zipFile.close(); // Not robust
    }

    public static void showHeapMemoryUsage(String messagePrefix) {
        MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = mbean.getHeapMemoryUsage();

        logger.info(messagePrefix + memoryUsage.toString());
    }
}

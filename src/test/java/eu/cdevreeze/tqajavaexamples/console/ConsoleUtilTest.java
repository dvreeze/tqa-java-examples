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

import eu.cdevreeze.tqa.base.taxonomy.BasicTaxonomy;
import eu.cdevreeze.tqa.base.taxonomybuilder.TaxonomyBuilder;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import scala.jdk.javaapi.CollectionConverters;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipFile;

public class ConsoleUtilTest extends TestCase {

    public ConsoleUtilTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ConsoleUtilTest.class);
    }

    public void testApp() throws URISyntaxException, IOException {
        try (ZipFile zipFile = new ZipFile(new File(ConsoleUtilTest.class.getResource("/taxo-nt12-kvk.zip").toURI()))) {
            boolean useSaxon = true;
            boolean lenient = false;
            TaxonomyBuilder taxonomyBuilder = ConsoleUtil.createTaxonomyBuilder(zipFile, useSaxon, lenient);

            // The more convenient method Set.of requires Java 9+, apparently
            Set<URI> entrypointUris = new HashSet<>();
            entrypointUris.add(URI.create("http://www.nltaxonomie.nl/nt12/kvk/20171213/entrypoints/kvk-rpt-jaarverantwoording-2017-nlgaap-klein-publicatiestukken.xsd"));

            BasicTaxonomy taxonomy = taxonomyBuilder.build(CollectionConverters.asScala(entrypointUris).toSet());

            assertTrue(taxonomy.relationships().size() >= 70000);
            assertTrue(taxonomy.findAllDimensionalRelationships().size() >= 550);
        }
    }
}

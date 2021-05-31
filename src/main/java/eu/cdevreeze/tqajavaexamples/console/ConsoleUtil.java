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

import eu.cdevreeze.tqa.base.relationship.jvm.DefaultParallelRelationshipFactory;
import eu.cdevreeze.tqa.base.taxonomybuilder.TaxonomyBuilder;
import eu.cdevreeze.tqa.base.taxonomybuilder.jvm.TaxonomyBuilderSupport;
import net.sf.saxon.s9api.Processor;

import java.util.zip.ZipFile;

/**
 * Taxonomy bootstrapping utility for the console programs.
 *
 * @author Chris de Vreeze
 */
public class ConsoleUtil {

    private ConsoleUtil() {
        // Cannot be instantiated
    }

    public static TaxonomyBuilder createTaxonomyBuilder(ZipFile taxonomyPackage, boolean useSaxon, boolean lenient) {
        // Exploiting parallelism, in DTS collection and relationship creation.

        Processor processor = new Processor(false);

        // Easy (and fast parallel) TaxonomyBuilder creation using object TaxonomyBuilderSupport

        TaxonomyBuilder rawTaxonomyBuilder =
                (useSaxon) ? TaxonomyBuilderSupport.forTaxonomyPackage(taxonomyPackage, processor) : TaxonomyBuilderSupport.forTaxonomyPackageUsingIndexedDocuments(taxonomyPackage, processor);

        TaxonomyBuilder taxonomyBuilder =
                (lenient) ? rawTaxonomyBuilder.withRelationshipFactory(DefaultParallelRelationshipFactory.LenientInstance()) : rawTaxonomyBuilder;

        return taxonomyBuilder;
    }
}

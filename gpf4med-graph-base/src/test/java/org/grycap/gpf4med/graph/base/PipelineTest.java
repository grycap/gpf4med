/*
 * Copyright 2013 Institute for Molecular Imaging Instrumentation (I3M)
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by 
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 *   http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 * 
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 */

package org.grycap.gpf4med.graph.base;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.DocumentFetcher;
import org.grycap.gpf4med.Statistics;
import org.grycap.gpf4med.TemplateManager;
import org.grycap.gpf4med.data.GraphDatabaseHandler;
import org.grycap.gpf4med.graph.base.util.TestUtils;
import org.grycap.gpf4med.graph.base.visual.GraphvizPrinter;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests pipelining of common operations: file load, graph creation, etc.
 * @author Erik Torres <ertorser@upv.es>
 */
public class PipelineTest {
	
	public static final String MAMO_REPORTS_PATH = "/opt/trencadis/files/reports_test/MAMO";
	public static final String ECO_REPORTS_PATH  = "/opt/trencadis/files/reports_test/ECO";
	public static final String RESO_REPORTS_PATH = "/opt/trencadis/files/reports_test/RESO";
	
	@Test
	public void test() throws IOException {
		System.out.println("PipelineTest.test()");
		File graphvizFile = null;
		try {
			// load example templates
			final Collection<File> templateFiles = TestUtils.getTestTemplateFiles();
			final Collection<URL> templateURLs = Arrays.asList(FileUtils.toURLs(templateFiles.toArray(new File[templateFiles.size()])));
			TemplateManager.INSTANCE.setup(templateURLs);
			TemplateManager.INSTANCE.preload();

			// load example reports in the graph			
			GraphDatabaseHandler.INSTANCE.restart();
			GraphDatabaseHandler.INSTANCE.setConnector(new BaseGraphConnector());
			final Collection<File> reportFiles = TestUtils.getTestReportFiles(MAMO_REPORTS_PATH);
			final ImmutableList<URL> urls = new ImmutableList.Builder<URL>().add(FileUtils.toURLs(reportFiles.toArray(new File[reportFiles.size()]))).build();
			Statistics.INSTANCE.setTotalSubmitted(reportFiles.size());
			// start asynchronously load
			DocumentFetcher.INSTANCE.fetch(urls);
			graphvizFile = GraphvizPrinter.print(GraphDatabaseHandler.INSTANCE.service(),
					RandomStringUtils.random(8, true, true) + ".dot");
			assertThat("graphviz file is not null", graphvizFile, notNullValue());
			final String graphvizStr = FileUtils.readFileToString(graphvizFile);
			assertThat("graphviz string is not null", graphvizStr, notNullValue());
			assertThat("graphviz string is not empty", StringUtils.isNotBlank(graphvizStr));
			/* uncomment for additional output
			System.out.println(" >> Graphviz\n" + graphvizStr + "\n"); */
			
		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail("PipelineTest.test() failed: " + e.getMessage());
		} finally {
			GraphDatabaseHandler.INSTANCE.close();
			//FileUtils.deleteQuietly(graphvizFile);
			System.out.println("PipelineTest.test() has finished");
		}
	}

}
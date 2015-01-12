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

package org.grycap.gpf4med.graph.base.visual;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.graph.base.model.RelTypes;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.grycap.gpf4med.data.GraphDatabaseHandler;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;
import org.neo4j.visualization.asciidoc.AsciidocHelper;
import org.neo4j.visualization.graphviz.AsciiDocSimpleStyle;
import org.neo4j.visualization.graphviz.GraphvizWriter;
import org.neo4j.walk.Walker;

/**
 * Saves graphs in Graphviz format.
 * @author Erik Torres <ertorser@upv.es>
 * @see <a href="http://www.graphviz.org/">Graphviz - Graph Visualization Software</a>
 */
@SuppressWarnings("deprecation")
public class GraphvizPrinter {

	public static final String CACHE_DIRNAME = "graphviz";
	
	public static void print(final Node node) throws IOException {		
		try (final Transaction tx = GraphDatabaseHandler.INSTANCE.service().beginTx()) {
			final TraversalDescription td = Traversal.description()
					.depthFirst()
					.relationships(RelTypes.IS, Direction.BOTH)
					.evaluator(Evaluators.all());
			final GraphvizWriter writer = new GraphvizWriter();
			final OutputStream outputStream = new ByteArrayOutputStream();
			writer.emit(outputStream, Walker.crosscut(td.traverse(node).nodes(), RelTypes.IS));
			System.out.println(outputStream.toString());			
			tx.success();
		}
	}

	public static File print(final GraphDatabaseService graphDb, final String filename) throws IOException {
		checkArgument(graphDb != null, "Uninitialized database");
		checkArgument(StringUtils.isNotBlank(filename), "Uninitialized or invalid filename");
		final File file = new File(ConfigurationManager.INSTANCE.getLocalCacheDir(), 
				CACHE_DIRNAME + File.pathSeparator + filename);
		try (final Transaction tx = graphDb.beginTx()) {
			final GraphvizWriter writer = new GraphvizWriter();			
			writer.emit(file, Walker.fullGraph(graphDb));		
			tx.success();
		}
		return file;
	}
	
	public static File print3(final GraphDatabaseService graphDb, final String filename) throws IOException {
		checkArgument(graphDb != null, "Uninitialized database");
		checkArgument(StringUtils.isNotBlank(filename), "Uninitialized or invalid filename");
		final File file = new File(ConfigurationManager.INSTANCE.getLocalCacheDir(), 
				CACHE_DIRNAME + File.pathSeparator + filename);
		try (final Transaction tx = graphDb.beginTx()) {
			final GraphvizWriter writer = new GraphvizWriter(AsciiDocSimpleStyle.withoutColors());
			writer.emit(file, Walker.fullGraph(graphDb));		
			tx.success();
		}
		return file;
	}
	
	public static File print2(final GraphDatabaseService graphDb, final String filename) throws IOException {
		final String dotString = AsciidocHelper.createGraphViz("title", graphDb, "identifier");
		final File file = new File(filename);
		FileUtils.write(file, dotString);		
		return file;
	}	

}
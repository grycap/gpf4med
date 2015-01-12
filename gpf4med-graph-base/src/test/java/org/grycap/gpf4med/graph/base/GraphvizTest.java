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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;
import org.neo4j.visualization.graphviz.GraphvizWriter;
import org.neo4j.walk.Walker;

/**
 * Tests Graphviz visualization of graphs.
 * @author Erik Torres <ertorser@upv.es>
 */
public class GraphvizTest {

	private static final String DB_PATH = "target/neo4j-hello-db";

	@Test
	public void test() {
		System.out.println("GraphvizTest.test()");
		try {
			
			// dot -Tpng -O neo4j.dot
			
			GraphDatabaseService graphDb;
			clearDb(); 
			graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
			registerShutdownHook(graphDb);			
			try (final Transaction tx = graphDb.beginTx();) {
				final Node emil = graphDb.createNode();
				emil.setProperty( "name", "Emil Eifrém" );
				emil.setProperty( "age", 30 );
				final Node tobias = graphDb.createNode();
				tobias.setProperty( "name", "Tobias \"thobe\" Ivarsson" );
				tobias.setProperty( "age", 23 );
				tobias.setProperty( "hours", new int[] { 10, 10, 4, 4, 0 } );
				final Node johan = graphDb.createNode();
				johan.setProperty( "!<>)", "!<>)" );
				johan.setProperty( "name", "!<>Johan '\\n00b' !<>Svensson" );
				final Relationship emilKNOWStobias = emil.createRelationshipTo(tobias, RelTypes.KNOWS);
				emilKNOWStobias.setProperty( "since", "2003-08-17" );
				final Relationship johanKNOWSemil = johan.createRelationshipTo(emil, RelTypes.KNOWS);
				final Relationship tobiasKNOWSjohan = tobias.createRelationshipTo(johan, RelTypes.KNOWS);
				final Relationship tobiasWORKS_FORemil = tobias.createRelationshipTo(emil, RelTypes.WORKS_FOR);
				final OutputStream out = new ByteArrayOutputStream();
				final GraphvizWriter writer = new GraphvizWriter();
				/* writer.emit(out, Walker.crosscut(emil.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, 
						ReturnableEvaluator.ALL, RelTypes.KNOWS, Direction.BOTH, RelTypes.WORKS_FOR, Direction.BOTH), 
						RelTypes.KNOWS, RelTypes.WORKS_FOR)); */
				writer.emit(out, Walker.fullGraph(graphDb));
				tx.success();
				System.out.println(out.toString());
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail("GraphvizTest.test() failed: " + e.getMessage());
		} finally {            
			System.out.println("GraphvizTest.test() has finished");
		}
	}

	private static enum RelTypes implements RelationshipType {
		KNOWS,
		WORKS_FOR
	}

	private void clearDb() {
		try {
			FileUtils.deleteRecursively(new File(DB_PATH));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		} );
	}

}
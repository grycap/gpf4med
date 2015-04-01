package org.grycap.gpf4med.xml;

import static javax.xml.bind.JAXBContext.newInstance;
import static javax.xml.bind.JAXBIntrospector.getValue;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;

/**
 * Provides a template for implementing XML binding classes.
 */
public abstract class XmlBinder {

	private final static Logger LOGGER = getLogger(XmlBinder.class);

	protected final JAXBContext context;
	protected final JAXBIntrospector introspector;

	public XmlBinder(final Class<?>[] supportedClasses) {
		JAXBContext context2 = null;
		JAXBIntrospector introspector2 = null;
		try {
			// context
			context2 = newInstance(supportedClasses, new HashMap<String, Object>());
			// introspector
			introspector2 = context2.createJAXBIntrospector();
		} catch (Exception e) {
			LOGGER.error("Failed to configure XML binding", e);
		}
		context = context2;
		introspector = introspector2;
	}

	protected abstract <T> JAXBElement<T> createType(final T obj);

	public <T> String typeToXml(final T obj) throws IOException {
		try (final StringWriter writer = new StringWriter()) {
			final Marshaller marshaller = context.createMarshaller();
			if (null == introspector.getElementName(obj)) {
				marshaller.marshal(createType(obj), writer);
			} else {
				marshaller.marshal(obj, writer);
			}
			return writer.toString();
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T typeFromXml(final String payload) throws IOException {
		try (final StringReader reader = new StringReader(payload)) {
			return (T) getValue(context.createUnmarshaller().unmarshal(reader));
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public <T> void typeToFile(final T obj, final File file) throws IOException {
		try (final FileWriter writer = new FileWriter(file, false)) {
			final Marshaller marshaller = context.createMarshaller();			
			if (null == introspector.getElementName(obj)) {
				marshaller.marshal(createType(obj), writer);
			} else {
				marshaller.marshal(obj, writer);
			}			
		} catch (JAXBException e) {
			throw new IOException(e);
		}		
	}

	@SuppressWarnings("unchecked")
	public <T> T typeFromFile(final File file) throws IOException {
		try (final FileReader reader = new FileReader(file)) {
			return (T) getValue(context.createUnmarshaller().unmarshal(reader));			
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T typeFromInputStream(final InputStream is) throws IOException {
		try (final InputStreamReader reader = new InputStreamReader(is)) {
			return (T) getValue(context.createUnmarshaller().unmarshal(reader));
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public <T> void typeToOutputStream(final T obj, final OutputStream os) throws IOException {
		try (final OutputStreamWriter writer = new OutputStreamWriter(os)) {
			final Marshaller marshaller = context.createMarshaller();			
			if (null == introspector.getElementName(obj)) {
				marshaller.marshal(createType(obj), writer);
			} else {
				marshaller.marshal(obj, writer);
			}			
		} catch (JAXBException e) {
			throw new IOException(e);
		}		
	}

}
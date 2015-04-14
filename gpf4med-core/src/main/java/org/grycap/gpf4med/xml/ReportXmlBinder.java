package org.grycap.gpf4med.xml;

import javax.xml.bind.JAXBElement;

import org.grycap.gpf4med.xml.report.ObjectFactory;
import org.grycap.gpf4med.xml.report.Document;
import org.grycap.gpf4med.xml.report.Documents;

public class ReportXmlBinder extends XmlBinder {

	private static final Class<?>[] SUPPORTED_CLASSES = {
		Document.class,
		Documents.class
	};

	public static final ObjectFactory REPORT_XML_FACTORY = new ObjectFactory();	

	public static final ReportXmlBinder REPORT_XMLB = new ReportXmlBinder();

	public ReportXmlBinder() {
		super(SUPPORTED_CLASSES);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> JAXBElement<T> createType(final T obj) {
		Object element = null;
		Class<? extends Object> clazz = obj.getClass();
		if (clazz.equals(Document.class)) {
			element = REPORT_XML_FACTORY.createDICOMSR((Document)obj);
		} else if (clazz.equals(Documents.class)) {
			element = REPORT_XML_FACTORY.createDICOMREPORTS((Documents)obj);
		} else {
			throw new IllegalArgumentException("Unsupported type: " + clazz.getCanonicalName());
		}
		return (JAXBElement<T>) element;
	}

}
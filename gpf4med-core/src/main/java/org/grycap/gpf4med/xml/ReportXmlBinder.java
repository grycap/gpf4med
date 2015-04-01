package org.grycap.gpf4med.xml;

import javax.xml.bind.JAXBElement;

import org.grycap.gpf4med.xml.report.ObjectFactory;
import org.grycap.gpf4med.xml.report.ReportType;
import org.grycap.gpf4med.xml.report.ReportsType;

public class ReportXmlBinder extends XmlBinder {

	private static final Class<?>[] SUPPORTED_CLASSES = {
		ReportType.class,
		ReportsType.class
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
		if (clazz.equals(ReportType.class)) {
			element = REPORT_XML_FACTORY.createDICOMSR((ReportType)obj);
		} else if (clazz.equals(ReportsType.class)) {
			element = REPORT_XML_FACTORY.createDICOMREPORTS((ReportsType)obj);
		} else {
			throw new IllegalArgumentException("Unsupported type: " + clazz.getCanonicalName());
		}
		return (JAXBElement<T>) element;
	}

}
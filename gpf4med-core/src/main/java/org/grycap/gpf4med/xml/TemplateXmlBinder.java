package org.grycap.gpf4med.xml;

import javax.xml.bind.JAXBElement;

import org.grycap.gpf4med.model.template.ObjectFactory;
import org.grycap.gpf4med.model.template.Template;

public class TemplateXmlBinder extends XmlBinder {

	private static final Class<?>[] SUPPORTED_CLASSES = {
		Template.class,
	};

	public static final ObjectFactory TEMPLATE_XML_FACTORY = new ObjectFactory();	

	public static final TemplateXmlBinder TEMPLATE_XMLB = new TemplateXmlBinder();

	public TemplateXmlBinder() {
		super(SUPPORTED_CLASSES);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> JAXBElement<T> createType(final T obj) {
		Object element = null;
		Class<? extends Object> clazz = obj.getClass();
		if (clazz.equals(Template.class)) {
			element = TEMPLATE_XML_FACTORY.createDICOMSR((Template)obj);
		} else {
			throw new IllegalArgumentException("Unsupported type: " + clazz.getCanonicalName());
		}
		return (JAXBElement<T>) element;
	}

}
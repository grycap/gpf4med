//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2015.04.15 a las 01:04:05 PM CEST 
//


package org.grycap.gpf4med.model.template;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.grycap.gpf4med.model.template package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _DICOMSR_QNAME = new QName("", "DICOM_SR");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.grycap.gpf4med.model.template
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Template }
     * 
     */
    public Template createTemplate() {
        return new Template();
    }

    /**
     * Create an instance of {@link Cardinality }
     * 
     */
    public Cardinality createCardinality() {
        return new Cardinality();
    }

    /**
     * Create an instance of {@link Condition }
     * 
     */
    public Condition createCondition() {
        return new Condition();
    }

    /**
     * Create an instance of {@link DefaultValue }
     * 
     */
    public DefaultValue createDefaultValue() {
        return new DefaultValue();
    }

    /**
     * Create an instance of {@link CodeValues }
     * 
     */
    public CodeValues createCodeValues() {
        return new CodeValues();
    }

    /**
     * Create an instance of {@link DefaultCodeValue }
     * 
     */
    public DefaultCodeValue createDefaultCodeValue() {
        return new DefaultCodeValue();
    }

    /**
     * Create an instance of {@link UnitMeasurementTemplate }
     * 
     */
    public UnitMeasurementTemplate createUnitMeasurementTemplate() {
        return new UnitMeasurementTemplate();
    }

    /**
     * Create an instance of {@link NumTemplate }
     * 
     */
    public NumTemplate createNumTemplate() {
        return new NumTemplate();
    }

    /**
     * Create an instance of {@link CodeTemplate }
     * 
     */
    public CodeTemplate createCodeTemplate() {
        return new CodeTemplate();
    }

    /**
     * Create an instance of {@link DateTemplate }
     * 
     */
    public DateTemplate createDateTemplate() {
        return new DateTemplate();
    }

    /**
     * Create an instance of {@link TextTemplate }
     * 
     */
    public TextTemplate createTextTemplate() {
        return new TextTemplate();
    }

    /**
     * Create an instance of {@link ChildrenTemplate }
     * 
     */
    public ChildrenTemplate createChildrenTemplate() {
        return new ChildrenTemplate();
    }

    /**
     * Create an instance of {@link ConceptNameTemplate }
     * 
     */
    public ConceptNameTemplate createConceptNameTemplate() {
        return new ConceptNameTemplate();
    }

    /**
     * Create an instance of {@link Properties }
     * 
     */
    public Properties createProperties() {
        return new Properties();
    }

    /**
     * Create an instance of {@link ContainerTemplate }
     * 
     */
    public ContainerTemplate createContainerTemplate() {
        return new ContainerTemplate();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Template }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DICOM_SR")
    public JAXBElement<Template> createDICOMSR(Template value) {
        return new JAXBElement<Template>(_DICOMSR_QNAME, Template.class, null, value);
    }

}

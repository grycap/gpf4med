//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2015.04.15 a las 01:04:04 PM CEST 
//


package org.grycap.gpf4med.model.document;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.grycap.gpf4med.model.document package. 
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
    private final static QName _DICOMREPORTS_QNAME = new QName("", "DICOM_REPORTS");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.grycap.gpf4med.model.document
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Document }
     * 
     */
    public Document createDocument() {
        return new Document();
    }

    /**
     * Create an instance of {@link Documents }
     * 
     */
    public Documents createDocuments() {
        return new Documents();
    }

    /**
     * Create an instance of {@link UnitMeasurement }
     * 
     */
    public UnitMeasurement createUnitMeasurement() {
        return new UnitMeasurement();
    }

    /**
     * Create an instance of {@link Value }
     * 
     */
    public Value createValue() {
        return new Value();
    }

    /**
     * Create an instance of {@link Num }
     * 
     */
    public Num createNum() {
        return new Num();
    }

    /**
     * Create an instance of {@link Code }
     * 
     */
    public Code createCode() {
        return new Code();
    }

    /**
     * Create an instance of {@link Date }
     * 
     */
    public Date createDate() {
        return new Date();
    }

    /**
     * Create an instance of {@link Text }
     * 
     */
    public Text createText() {
        return new Text();
    }

    /**
     * Create an instance of {@link Children }
     * 
     */
    public Children createChildren() {
        return new Children();
    }

    /**
     * Create an instance of {@link ConceptName }
     * 
     */
    public ConceptName createConceptName() {
        return new ConceptName();
    }

    /**
     * Create an instance of {@link Container }
     * 
     */
    public Container createContainer() {
        return new Container();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Document }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DICOM_SR")
    public JAXBElement<Document> createDICOMSR(Document value) {
        return new JAXBElement<Document>(_DICOMSR_QNAME, Document.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Documents }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DICOM_REPORTS")
    public JAXBElement<Documents> createDICOMREPORTS(Documents value) {
        return new JAXBElement<Documents>(_DICOMREPORTS_QNAME, Documents.class, null, value);
    }

}

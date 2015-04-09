//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2015.04.09 a las 10:28:13 AM CEST 
//


package org.grycap.gpf4med.xml.report;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.grycap.gpf4med.xml.report package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.grycap.gpf4med.xml.report
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ReportType }
     * 
     */
    public ReportType createReportType() {
        return new ReportType();
    }

    /**
     * Create an instance of {@link ReportsType }
     * 
     */
    public ReportsType createReportsType() {
        return new ReportsType();
    }

    /**
     * Create an instance of {@link UnitMeasurementType }
     * 
     */
    public UnitMeasurementType createUnitMeasurementType() {
        return new UnitMeasurementType();
    }

    /**
     * Create an instance of {@link ValueType }
     * 
     */
    public ValueType createValueType() {
        return new ValueType();
    }

    /**
     * Create an instance of {@link NumType }
     * 
     */
    public NumType createNumType() {
        return new NumType();
    }

    /**
     * Create an instance of {@link CodeType }
     * 
     */
    public CodeType createCodeType() {
        return new CodeType();
    }

    /**
     * Create an instance of {@link DateType }
     * 
     */
    public DateType createDateType() {
        return new DateType();
    }

    /**
     * Create an instance of {@link TextType }
     * 
     */
    public TextType createTextType() {
        return new TextType();
    }

    /**
     * Create an instance of {@link ChildrenTypeDepth3 }
     * 
     */
    public ChildrenTypeDepth3 createChildrenTypeDepth3() {
        return new ChildrenTypeDepth3();
    }

    /**
     * Create an instance of {@link ContainerTypeDepth3 }
     * 
     */
    public ContainerTypeDepth3 createContainerTypeDepth3() {
        return new ContainerTypeDepth3();
    }

    /**
     * Create an instance of {@link ChildrenTypeDepth2 }
     * 
     */
    public ChildrenTypeDepth2 createChildrenTypeDepth2() {
        return new ChildrenTypeDepth2();
    }

    /**
     * Create an instance of {@link ContainerTypeDepth2 }
     * 
     */
    public ContainerTypeDepth2 createContainerTypeDepth2() {
        return new ContainerTypeDepth2();
    }

    /**
     * Create an instance of {@link ChildrenTypeDepth1 }
     * 
     */
    public ChildrenTypeDepth1 createChildrenTypeDepth1() {
        return new ChildrenTypeDepth1();
    }

    /**
     * Create an instance of {@link ContainerTypeDepth1 }
     * 
     */
    public ContainerTypeDepth1 createContainerTypeDepth1() {
        return new ContainerTypeDepth1();
    }

    /**
     * Create an instance of {@link ChildrenTypeDepth0 }
     * 
     */
    public ChildrenTypeDepth0 createChildrenTypeDepth0() {
        return new ChildrenTypeDepth0();
    }

    /**
     * Create an instance of {@link ConceptNameType }
     * 
     */
    public ConceptNameType createConceptNameType() {
        return new ConceptNameType();
    }

    /**
     * Create an instance of {@link ContainerTypeDepth0 }
     * 
     */
    public ContainerTypeDepth0 createContainerTypeDepth0() {
        return new ContainerTypeDepth0();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReportType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DICOM_SR")
    public JAXBElement<ReportType> createDICOMSR(ReportType value) {
        return new JAXBElement<ReportType>(_DICOMSR_QNAME, ReportType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReportsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DICOM_REPORTS")
    public JAXBElement<ReportsType> createDICOMREPORTS(ReportsType value) {
        return new JAXBElement<ReportsType>(_DICOMREPORTS_QNAME, ReportsType.class, null, value);
    }

}

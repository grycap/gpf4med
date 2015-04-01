//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2015.03.30 a las 10:04:03 AM CEST 
//


package org.grycap.gpf4med.xml.report;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.grycap.threads.xml.report package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.grycap.threads.xml.report
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

//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2015.04.15 a las 01:04:04 PM CEST 
//


package org.grycap.gpf4med.model.document;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * <p>Clase Java para Document complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Document"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CONTAINER" type="{}Container"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="IDReport" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="IDOntology" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="DateTimeStart" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="DateTimeEnd" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="IDTRENCADISReport" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Document", propOrder = {
    "container"
})
@XmlRootElement(name = "DICOM_SR")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
public class Document {

    @XmlElement(name = "CONTAINER", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    protected Container container;
    @XmlAttribute(name = "IDReport")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    protected String idReport;
    @XmlAttribute(name = "IDOntology")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    protected String idOntology;
    @XmlAttribute(name = "DateTimeStart")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    protected String dateTimeStart;
    @XmlAttribute(name = "DateTimeEnd")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    protected String dateTimeEnd;
    @XmlAttribute(name = "IDTRENCADISReport")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    protected String idtrencadisReport;

    /**
     * Obtiene el valor de la propiedad container.
     * 
     * @return
     *     possible object is
     *     {@link Container }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public Container getCONTAINER() {
        return container;
    }

    /**
     * Define el valor de la propiedad container.
     * 
     * @param value
     *     allowed object is
     *     {@link Container }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public void setCONTAINER(Container value) {
        this.container = value;
    }

    /**
     * Obtiene el valor de la propiedad idReport.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public String getIDReport() {
        return idReport;
    }

    /**
     * Define el valor de la propiedad idReport.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public void setIDReport(String value) {
        this.idReport = value;
    }

    /**
     * Obtiene el valor de la propiedad idOntology.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public String getIDOntology() {
        return idOntology;
    }

    /**
     * Define el valor de la propiedad idOntology.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public void setIDOntology(String value) {
        this.idOntology = value;
    }

    /**
     * Obtiene el valor de la propiedad dateTimeStart.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public String getDateTimeStart() {
        return dateTimeStart;
    }

    /**
     * Define el valor de la propiedad dateTimeStart.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public void setDateTimeStart(String value) {
        this.dateTimeStart = value;
    }

    /**
     * Obtiene el valor de la propiedad dateTimeEnd.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public String getDateTimeEnd() {
        return dateTimeEnd;
    }

    /**
     * Define el valor de la propiedad dateTimeEnd.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public void setDateTimeEnd(String value) {
        this.dateTimeEnd = value;
    }

    /**
     * Obtiene el valor de la propiedad idtrencadisReport.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public String getIDTRENCADISReport() {
        return idtrencadisReport;
    }

    /**
     * Define el valor de la propiedad idtrencadisReport.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public void setIDTRENCADISReport(String value) {
        this.idtrencadisReport = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public Document withCONTAINER(Container value) {
        setCONTAINER(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public Document withIDReport(String value) {
        setIDReport(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public Document withIDOntology(String value) {
        setIDOntology(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public Document withDateTimeStart(String value) {
        setDateTimeStart(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public Document withDateTimeEnd(String value) {
        setDateTimeEnd(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public Document withIDTRENCADISReport(String value) {
        setIDTRENCADISReport(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:04+02:00", comments = "JAXB RI v2.2.11")
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}

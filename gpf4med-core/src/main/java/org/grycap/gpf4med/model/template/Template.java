//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2015.04.15 a las 01:04:05 PM CEST 
//


package org.grycap.gpf4med.model.template;

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
 * <p>Clase Java para Template complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Template"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CONTAINER" type="{}ContainerTemplate"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="Description" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="IDOntology" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Template", propOrder = {
    "container"
})
@XmlRootElement(name = "DICOM_SR")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
public class Template {

    @XmlElement(name = "CONTAINER", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    protected ContainerTemplate container;
    @XmlAttribute(name = "Description")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    protected String description;
    @XmlAttribute(name = "IDOntology")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    protected String idOntology;

    /**
     * Obtiene el valor de la propiedad container.
     * 
     * @return
     *     possible object is
     *     {@link ContainerTemplate }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public ContainerTemplate getCONTAINER() {
        return container;
    }

    /**
     * Define el valor de la propiedad container.
     * 
     * @param value
     *     allowed object is
     *     {@link ContainerTemplate }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public void setCONTAINER(ContainerTemplate value) {
        this.container = value;
    }

    /**
     * Obtiene el valor de la propiedad description.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public String getDescription() {
        return description;
    }

    /**
     * Define el valor de la propiedad description.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Obtiene el valor de la propiedad idOntology.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public void setIDOntology(String value) {
        this.idOntology = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public Template withCONTAINER(ContainerTemplate value) {
        setCONTAINER(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public Template withDescription(String value) {
        setDescription(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public Template withIDOntology(String value) {
        setIDOntology(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}

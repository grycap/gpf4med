//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2015.04.14 a las 12:27:01 PM CEST 
//


package org.grycap.gpf4med.xml.report;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * <p>Clase Java para Code complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Code"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CONCEPT_NAME" type="{}ConceptName"/&gt;
 *         &lt;element name="VALUE" type="{}Value"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Code", propOrder = {
    "conceptname",
    "value"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T12:27:01+02:00", comments = "JAXB RI v2.2.11")
public class Code {

    @XmlElement(name = "CONCEPT_NAME", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T12:27:01+02:00", comments = "JAXB RI v2.2.11")
    protected ConceptName conceptname;
    @XmlElement(name = "VALUE", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T12:27:01+02:00", comments = "JAXB RI v2.2.11")
    protected Value value;

    /**
     * Obtiene el valor de la propiedad conceptname.
     * 
     * @return
     *     possible object is
     *     {@link ConceptName }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T12:27:01+02:00", comments = "JAXB RI v2.2.11")
    public ConceptName getCONCEPTNAME() {
        return conceptname;
    }

    /**
     * Define el valor de la propiedad conceptname.
     * 
     * @param value
     *     allowed object is
     *     {@link ConceptName }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T12:27:01+02:00", comments = "JAXB RI v2.2.11")
    public void setCONCEPTNAME(ConceptName value) {
        this.conceptname = value;
    }

    /**
     * Obtiene el valor de la propiedad value.
     * 
     * @return
     *     possible object is
     *     {@link Value }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T12:27:01+02:00", comments = "JAXB RI v2.2.11")
    public Value getVALUE() {
        return value;
    }

    /**
     * Define el valor de la propiedad value.
     * 
     * @param value
     *     allowed object is
     *     {@link Value }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T12:27:01+02:00", comments = "JAXB RI v2.2.11")
    public void setVALUE(Value value) {
        this.value = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T12:27:01+02:00", comments = "JAXB RI v2.2.11")
    public Code withCONCEPTNAME(ConceptName value) {
        setCONCEPTNAME(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T12:27:01+02:00", comments = "JAXB RI v2.2.11")
    public Code withVALUE(Value value) {
        setVALUE(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T12:27:01+02:00", comments = "JAXB RI v2.2.11")
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T12:27:01+02:00", comments = "JAXB RI v2.2.11")
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T12:27:01+02:00", comments = "JAXB RI v2.2.11")
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}

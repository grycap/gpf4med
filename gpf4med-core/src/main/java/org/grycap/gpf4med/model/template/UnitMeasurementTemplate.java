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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * <p>Clase Java para UnitMeasurementTemplate complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="UnitMeasurementTemplate"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CONCEPT_NAME" type="{}ConceptNameTemplate"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnitMeasurementTemplate", propOrder = {
    "conceptname"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
public class UnitMeasurementTemplate {

    @XmlElement(name = "CONCEPT_NAME", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    protected ConceptNameTemplate conceptname;

    /**
     * Obtiene el valor de la propiedad conceptname.
     * 
     * @return
     *     possible object is
     *     {@link ConceptNameTemplate }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public ConceptNameTemplate getCONCEPTNAME() {
        return conceptname;
    }

    /**
     * Define el valor de la propiedad conceptname.
     * 
     * @param value
     *     allowed object is
     *     {@link ConceptNameTemplate }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public void setCONCEPTNAME(ConceptNameTemplate value) {
        this.conceptname = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public UnitMeasurementTemplate withCONCEPTNAME(ConceptNameTemplate value) {
        setCONCEPTNAME(value);
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

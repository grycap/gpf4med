//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2015.04.14 a las 09:38:00 AM CEST 
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
 * <p>Clase Java para ContainerDepth2 complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ContainerDepth2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CONCEPT_NAME" type="{}ConceptName"/&gt;
 *         &lt;element name="CHILDREN" type="{}ChildrenDepth2"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContainerDepth2", propOrder = {
    "conceptname",
    "children"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
public class ContainerDepth2 {

    @XmlElement(name = "CONCEPT_NAME", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    protected ConceptName conceptname;
    @XmlElement(name = "CHILDREN", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    protected ChildrenDepth2 children;

    /**
     * Obtiene el valor de la propiedad conceptname.
     * 
     * @return
     *     possible object is
     *     {@link ConceptName }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    public void setCONCEPTNAME(ConceptName value) {
        this.conceptname = value;
    }

    /**
     * Obtiene el valor de la propiedad children.
     * 
     * @return
     *     possible object is
     *     {@link ChildrenDepth2 }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenDepth2 getCHILDREN() {
        return children;
    }

    /**
     * Define el valor de la propiedad children.
     * 
     * @param value
     *     allowed object is
     *     {@link ChildrenDepth2 }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    public void setCHILDREN(ChildrenDepth2 value) {
        this.children = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    public ContainerDepth2 withCONCEPTNAME(ConceptName value) {
        setCONCEPTNAME(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    public ContainerDepth2 withCHILDREN(ChildrenDepth2 value) {
        setCHILDREN(value);
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}

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
 * <p>Clase Java para Properties complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Properties"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CARDINALITY" type="{}Cardinality"/&gt;
 *         &lt;element name="CONDITION_TYPE" type="{}Condition"/&gt;
 *         &lt;element name="INTEGRITY_RESTRICTIONS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DEFAULT_VALUE" type="{}DefaultValue" minOccurs="0"/&gt;
 *         &lt;element name="CODE_VALUES" type="{}CodeValues" minOccurs="0"/&gt;
 *         &lt;element name="DEFAULT_CODE_VALUE" type="{}DefaultCodeValue" minOccurs="0"/&gt;
 *         &lt;element name="UNIT_MEASUREMENT" type="{}UnitMeasurementTemplate" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Properties", propOrder = {
    "cardinality",
    "conditiontype",
    "integrityrestrictions",
    "defaultvalue",
    "codevalues",
    "defaultcodevalue",
    "unitmeasurement"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
public class Properties {

    @XmlElement(name = "CARDINALITY", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    protected Cardinality cardinality;
    @XmlElement(name = "CONDITION_TYPE", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    protected Condition conditiontype;
    @XmlElement(name = "INTEGRITY_RESTRICTIONS")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    protected String integrityrestrictions;
    @XmlElement(name = "DEFAULT_VALUE")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    protected DefaultValue defaultvalue;
    @XmlElement(name = "CODE_VALUES")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    protected CodeValues codevalues;
    @XmlElement(name = "DEFAULT_CODE_VALUE")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    protected DefaultCodeValue defaultcodevalue;
    @XmlElement(name = "UNIT_MEASUREMENT")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    protected UnitMeasurementTemplate unitmeasurement;

    /**
     * Obtiene el valor de la propiedad cardinality.
     * 
     * @return
     *     possible object is
     *     {@link Cardinality }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public Cardinality getCARDINALITY() {
        return cardinality;
    }

    /**
     * Define el valor de la propiedad cardinality.
     * 
     * @param value
     *     allowed object is
     *     {@link Cardinality }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public void setCARDINALITY(Cardinality value) {
        this.cardinality = value;
    }

    /**
     * Obtiene el valor de la propiedad conditiontype.
     * 
     * @return
     *     possible object is
     *     {@link Condition }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public Condition getCONDITIONTYPE() {
        return conditiontype;
    }

    /**
     * Define el valor de la propiedad conditiontype.
     * 
     * @param value
     *     allowed object is
     *     {@link Condition }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public void setCONDITIONTYPE(Condition value) {
        this.conditiontype = value;
    }

    /**
     * Obtiene el valor de la propiedad integrityrestrictions.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public String getINTEGRITYRESTRICTIONS() {
        return integrityrestrictions;
    }

    /**
     * Define el valor de la propiedad integrityrestrictions.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public void setINTEGRITYRESTRICTIONS(String value) {
        this.integrityrestrictions = value;
    }

    /**
     * Obtiene el valor de la propiedad defaultvalue.
     * 
     * @return
     *     possible object is
     *     {@link DefaultValue }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public DefaultValue getDEFAULTVALUE() {
        return defaultvalue;
    }

    /**
     * Define el valor de la propiedad defaultvalue.
     * 
     * @param value
     *     allowed object is
     *     {@link DefaultValue }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public void setDEFAULTVALUE(DefaultValue value) {
        this.defaultvalue = value;
    }

    /**
     * Obtiene el valor de la propiedad codevalues.
     * 
     * @return
     *     possible object is
     *     {@link CodeValues }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public CodeValues getCODEVALUES() {
        return codevalues;
    }

    /**
     * Define el valor de la propiedad codevalues.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeValues }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public void setCODEVALUES(CodeValues value) {
        this.codevalues = value;
    }

    /**
     * Obtiene el valor de la propiedad defaultcodevalue.
     * 
     * @return
     *     possible object is
     *     {@link DefaultCodeValue }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public DefaultCodeValue getDEFAULTCODEVALUE() {
        return defaultcodevalue;
    }

    /**
     * Define el valor de la propiedad defaultcodevalue.
     * 
     * @param value
     *     allowed object is
     *     {@link DefaultCodeValue }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public void setDEFAULTCODEVALUE(DefaultCodeValue value) {
        this.defaultcodevalue = value;
    }

    /**
     * Obtiene el valor de la propiedad unitmeasurement.
     * 
     * @return
     *     possible object is
     *     {@link UnitMeasurementTemplate }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public UnitMeasurementTemplate getUNITMEASUREMENT() {
        return unitmeasurement;
    }

    /**
     * Define el valor de la propiedad unitmeasurement.
     * 
     * @param value
     *     allowed object is
     *     {@link UnitMeasurementTemplate }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public void setUNITMEASUREMENT(UnitMeasurementTemplate value) {
        this.unitmeasurement = value;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public Properties withCARDINALITY(Cardinality value) {
        setCARDINALITY(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public Properties withCONDITIONTYPE(Condition value) {
        setCONDITIONTYPE(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public Properties withINTEGRITYRESTRICTIONS(String value) {
        setINTEGRITYRESTRICTIONS(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public Properties withDEFAULTVALUE(DefaultValue value) {
        setDEFAULTVALUE(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public Properties withCODEVALUES(CodeValues value) {
        setCODEVALUES(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public Properties withDEFAULTCODEVALUE(DefaultCodeValue value) {
        setDEFAULTCODEVALUE(value);
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public Properties withUNITMEASUREMENT(UnitMeasurementTemplate value) {
        setUNITMEASUREMENT(value);
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

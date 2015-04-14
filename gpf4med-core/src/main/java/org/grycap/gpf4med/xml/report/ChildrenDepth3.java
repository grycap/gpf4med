//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2015.04.14 a las 09:38:00 AM CEST 
//


package org.grycap.gpf4med.xml.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
 * <p>Clase Java para ChildrenDepth3 complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ChildrenDepth3"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TEXT" type="{}Text" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="CODE" type="{}Code" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="NUM" type="{}Num" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChildrenDepth3", propOrder = {
    "text",
    "code",
    "num"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
public class ChildrenDepth3 {

    @XmlElement(name = "TEXT")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    protected List<Text> text;
    @XmlElement(name = "CODE")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    protected List<Code> code;
    @XmlElement(name = "NUM")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    protected List<Num> num;

    /**
     * Gets the value of the text property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the text property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTEXT().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Text }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    public List<Text> getTEXT() {
        if (text == null) {
            text = new ArrayList<Text>();
        }
        return this.text;
    }

    /**
     * Gets the value of the code property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the code property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCODE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Code }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    public List<Code> getCODE() {
        if (code == null) {
            code = new ArrayList<Code>();
        }
        return this.code;
    }

    /**
     * Gets the value of the num property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the num property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNUM().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Num }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    public List<Num> getNUM() {
        if (num == null) {
            num = new ArrayList<Num>();
        }
        return this.num;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenDepth3 withTEXT(Text... values) {
        if (values!= null) {
            for (Text value: values) {
                getTEXT().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenDepth3 withTEXT(Collection<Text> values) {
        if (values!= null) {
            getTEXT().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenDepth3 withCODE(Code... values) {
        if (values!= null) {
            for (Code value: values) {
                getCODE().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenDepth3 withCODE(Collection<Code> values) {
        if (values!= null) {
            getCODE().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenDepth3 withNUM(Num... values) {
        if (values!= null) {
            for (Num value: values) {
                getNUM().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-14T09:38:00+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenDepth3 withNUM(Collection<Num> values) {
        if (values!= null) {
            getNUM().addAll(values);
        }
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

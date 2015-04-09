//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2015.04.09 a las 10:28:13 AM CEST 
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
 * <p>Clase Java para ChildrenTypeDepth0 complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ChildrenTypeDepth0"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TEXT" type="{}TextType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DATE" type="{}DateType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="CONTAINER" type="{}ContainerTypeDepth1" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChildrenTypeDepth0", propOrder = {
    "text",
    "date",
    "container"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
public class ChildrenTypeDepth0 {

    @XmlElement(name = "TEXT")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    protected List<TextType> text;
    @XmlElement(name = "DATE")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    protected List<DateType> date;
    @XmlElement(name = "CONTAINER", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    protected List<ContainerTypeDepth1> container;

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
     * {@link TextType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public List<TextType> getTEXT() {
        if (text == null) {
            text = new ArrayList<TextType>();
        }
        return this.text;
    }

    /**
     * Gets the value of the date property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the date property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDATE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DateType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public List<DateType> getDATE() {
        if (date == null) {
            date = new ArrayList<DateType>();
        }
        return this.date;
    }

    /**
     * Gets the value of the container property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the container property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCONTAINER().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContainerTypeDepth1 }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public List<ContainerTypeDepth1> getCONTAINER() {
        if (container == null) {
            container = new ArrayList<ContainerTypeDepth1>();
        }
        return this.container;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTypeDepth0 withTEXT(TextType... values) {
        if (values!= null) {
            for (TextType value: values) {
                getTEXT().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTypeDepth0 withTEXT(Collection<TextType> values) {
        if (values!= null) {
            getTEXT().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTypeDepth0 withDATE(DateType... values) {
        if (values!= null) {
            for (DateType value: values) {
                getDATE().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTypeDepth0 withDATE(Collection<DateType> values) {
        if (values!= null) {
            getDATE().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTypeDepth0 withCONTAINER(ContainerTypeDepth1 ... values) {
        if (values!= null) {
            for (ContainerTypeDepth1 value: values) {
                getCONTAINER().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTypeDepth0 withCONTAINER(Collection<ContainerTypeDepth1> values) {
        if (values!= null) {
            getCONTAINER().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}

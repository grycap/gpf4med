//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2015.04.15 a las 01:04:05 PM CEST 
//


package org.grycap.gpf4med.model.template;

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
 * <p>Clase Java para ChildrenTemplate complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ChildrenTemplate"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TEXT" type="{}TextTemplate" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DATE" type="{}DateTemplate" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="CONTAINER" type="{}ContainerTemplate" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="CODE" type="{}CodeTemplate" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="NUM" type="{}NumTemplate" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChildrenTemplate", propOrder = {
    "text",
    "date",
    "container",
    "code",
    "num"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
public class ChildrenTemplate {

    @XmlElement(name = "TEXT")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    protected List<TextTemplate> text;
    @XmlElement(name = "DATE")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    protected List<DateTemplate> date;
    @XmlElement(name = "CONTAINER")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    protected List<ContainerTemplate> container;
    @XmlElement(name = "CODE")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    protected List<CodeTemplate> code;
    @XmlElement(name = "NUM")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    protected List<NumTemplate> num;

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
     * {@link TextTemplate }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public List<TextTemplate> getTEXT() {
        if (text == null) {
            text = new ArrayList<TextTemplate>();
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
     * {@link DateTemplate }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public List<DateTemplate> getDATE() {
        if (date == null) {
            date = new ArrayList<DateTemplate>();
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
     * {@link ContainerTemplate }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public List<ContainerTemplate> getCONTAINER() {
        if (container == null) {
            container = new ArrayList<ContainerTemplate>();
        }
        return this.container;
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
     * {@link CodeTemplate }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public List<CodeTemplate> getCODE() {
        if (code == null) {
            code = new ArrayList<CodeTemplate>();
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
     * {@link NumTemplate }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public List<NumTemplate> getNUM() {
        if (num == null) {
            num = new ArrayList<NumTemplate>();
        }
        return this.num;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTemplate withTEXT(TextTemplate... values) {
        if (values!= null) {
            for (TextTemplate value: values) {
                getTEXT().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTemplate withTEXT(Collection<TextTemplate> values) {
        if (values!= null) {
            getTEXT().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTemplate withDATE(DateTemplate... values) {
        if (values!= null) {
            for (DateTemplate value: values) {
                getDATE().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTemplate withDATE(Collection<DateTemplate> values) {
        if (values!= null) {
            getDATE().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTemplate withCONTAINER(ContainerTemplate... values) {
        if (values!= null) {
            for (ContainerTemplate value: values) {
                getCONTAINER().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTemplate withCONTAINER(Collection<ContainerTemplate> values) {
        if (values!= null) {
            getCONTAINER().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTemplate withCODE(CodeTemplate... values) {
        if (values!= null) {
            for (CodeTemplate value: values) {
                getCODE().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTemplate withCODE(Collection<CodeTemplate> values) {
        if (values!= null) {
            getCODE().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTemplate withNUM(NumTemplate... values) {
        if (values!= null) {
            for (NumTemplate value: values) {
                getNUM().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-15T01:04:05+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTemplate withNUM(Collection<NumTemplate> values) {
        if (values!= null) {
            getNUM().addAll(values);
        }
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

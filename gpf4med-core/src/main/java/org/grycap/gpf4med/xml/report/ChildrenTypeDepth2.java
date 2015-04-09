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
 * <p>Clase Java para ChildrenTypeDepth2 complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ChildrenTypeDepth2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TEXT" type="{}TextType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="CODE" type="{}CodeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="NUM" type="{}NumType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="CONTAINER" type="{}ContainerTypeDepth3" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChildrenTypeDepth2", propOrder = {
    "text",
    "code",
    "num",
    "container"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
public class ChildrenTypeDepth2 {

    @XmlElement(name = "TEXT")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    protected List<TextType> text;
    @XmlElement(name = "CODE")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    protected List<CodeType> code;
    @XmlElement(name = "NUM")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    protected List<NumType> num;
    @XmlElement(name = "CONTAINER", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    protected List<ContainerTypeDepth3> container;

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
     * {@link CodeType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public List<CodeType> getCODE() {
        if (code == null) {
            code = new ArrayList<CodeType>();
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
     * {@link NumType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public List<NumType> getNUM() {
        if (num == null) {
            num = new ArrayList<NumType>();
        }
        return this.num;
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
     * {@link ContainerTypeDepth3 }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public List<ContainerTypeDepth3> getCONTAINER() {
        if (container == null) {
            container = new ArrayList<ContainerTypeDepth3>();
        }
        return this.container;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTypeDepth2 withTEXT(TextType... values) {
        if (values!= null) {
            for (TextType value: values) {
                getTEXT().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTypeDepth2 withTEXT(Collection<TextType> values) {
        if (values!= null) {
            getTEXT().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTypeDepth2 withCODE(CodeType... values) {
        if (values!= null) {
            for (CodeType value: values) {
                getCODE().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTypeDepth2 withCODE(Collection<CodeType> values) {
        if (values!= null) {
            getCODE().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTypeDepth2 withNUM(NumType... values) {
        if (values!= null) {
            for (NumType value: values) {
                getNUM().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTypeDepth2 withNUM(Collection<NumType> values) {
        if (values!= null) {
            getNUM().addAll(values);
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTypeDepth2 withCONTAINER(ContainerTypeDepth3 ... values) {
        if (values!= null) {
            for (ContainerTypeDepth3 value: values) {
                getCONTAINER().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-04-09T10:28:13+02:00", comments = "JAXB RI v2.2.11")
    public ChildrenTypeDepth2 withCONTAINER(Collection<ContainerTypeDepth3> values) {
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

//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2015.03.30 a las 10:04:03 AM CEST 
//


package org.grycap.gpf4med.xml.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * <p>Clase Java para ReportsType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ReportsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DICOM_SR" type="{}ReportType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportsType", propOrder = {
    "dicomsr"
})
@XmlRootElement(name = "DICOM_REPORTS")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2015-03-30T10:04:03+02:00", comments = "JAXB RI v2.2.11")
public class ReportsType {

    @XmlElement(name = "DICOM_SR")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-03-30T10:04:03+02:00", comments = "JAXB RI v2.2.11")
    protected List<ReportType> dicomsr;

    /**
     * Gets the value of the dicomsr property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dicomsr property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDICOMSR().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReportType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-03-30T10:04:03+02:00", comments = "JAXB RI v2.2.11")
    public List<ReportType> getDICOMSR() {
        if (dicomsr == null) {
            dicomsr = new ArrayList<ReportType>();
        }
        return this.dicomsr;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-03-30T10:04:03+02:00", comments = "JAXB RI v2.2.11")
    public ReportsType withDICOMSR(ReportType... values) {
        if (values!= null) {
            for (ReportType value: values) {
                getDICOMSR().add(value);
            }
        }
        return this;
    }

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-03-30T10:04:03+02:00", comments = "JAXB RI v2.2.11")
    public ReportsType withDICOMSR(Collection<ReportType> values) {
        if (values!= null) {
            getDICOMSR().addAll(values);
        }
        return this;
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-03-30T10:04:03+02:00", comments = "JAXB RI v2.2.11")
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-03-30T10:04:03+02:00", comments = "JAXB RI v2.2.11")
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-03-30T10:04:03+02:00", comments = "JAXB RI v2.2.11")
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
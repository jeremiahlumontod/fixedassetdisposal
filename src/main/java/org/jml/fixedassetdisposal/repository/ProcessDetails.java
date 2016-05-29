package org.jml.fixedassetdisposal.repository;

import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Basic;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name="processdetails")
public class ProcessDetails {


    @Id
    @GeneratedValue
    private Long id;

    private String procid;

    private String procidinstance;

    //@Lob
    //@Column(name="cbody", columnDefinition="LONGTEXT")
    @Lob @Basic(fetch=LAZY)
    private String cbody;

    public ProcessDetails() {

    }

    public ProcessDetails(String procid, String procidinstance, String cbody) {
        this.procid = procid;
        this.procidinstance = procidinstance;
        this.cbody = cbody;
    }

    public String getProcid() {
        return procid;
    }

    public void setProcid(String procid) {
        this.procid = procid;
    }

    public String getProcidinstance() {
        return procidinstance;
    }

    public void setProcidinstance(String procidinstance) {
        this.procidinstance = procidinstance;
    }

    public String getCbody() {
        return cbody;
    }

    public void setCbody(String cbody) {
        this.cbody = cbody;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
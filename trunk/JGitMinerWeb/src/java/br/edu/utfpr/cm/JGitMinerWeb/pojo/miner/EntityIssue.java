/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.pojo.miner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Douglas
 */
@Entity
@Table(name = "gitIssue")
@NamedQueries({
    @NamedQuery(name = "Issue.findByIdIssue", query = "SELECT i FROM EntityIssue i WHERE i.idIssue = :idIssue"),
    @NamedQuery(name = "Issue.findByNumberAndRepository", query = "SELECT i FROM EntityIssue i WHERE i.number = :number AND i.repository = :repository"),
    @NamedQuery(name = "Issue.findByRepository", query = "SELECT i FROM EntityIssue i WHERE i.repository = :repository")
})
public class EntityIssue implements InterfaceEntity, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date mineredAt;
    @Column(unique = true)
    private long idIssue;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date closedAt;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createdAt;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updatedAt;
    private Integer commentsCount;
    private Integer number;
    @OneToMany
    private List<EntityLabel> labels;
    @ManyToOne
    private EntityMilestone milestone;
    @OneToOne(mappedBy = "issue")
    private EntityPullRequest pullRequest;
    @Column(columnDefinition = "text")
    private String body;
    @Column(columnDefinition = "text")
    private String bodyHtml;
    @Column(columnDefinition = "text")
    private String bodyText;
    @Column(columnDefinition = "text")
    private String htmlUrl;
    private String stateIssue;
    @Column(columnDefinition = "text")
    private String title;
    @Column(columnDefinition = "text")
    private String url;
    @ManyToOne
    private EntityUser assignee;
    @ManyToOne
    private EntityUser userIssue;
    @OneToMany
    private List<EntityComment> comments;
    @ManyToOne
    private EntityRepository repository;
    @OneToMany(mappedBy = "issue")
    private List<EntityIssueEvent> events;

    public EntityIssue() {
        comments = new ArrayList<EntityComment>();
        labels = new ArrayList<EntityLabel>();
        mineredAt = new Date();
        events = new ArrayList<EntityIssueEvent>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getMineredAt() {
        return mineredAt;
    }

    public void setMineredAt(Date mineredAt) {
        this.mineredAt = mineredAt;
    }

    public EntityUser getAssignee() {
        return assignee;
    }

    public void setAssignee(EntityUser assignee) {
        this.assignee = assignee;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public Date getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Date closedAt) {
        this.closedAt = closedAt;
    }

    public List<EntityComment> getComments() {
        return comments;
    }

    public void setComments(List<EntityComment> comments) {
        this.comments = comments;
    }

    public EntityRepository getRepository() {
        return repository;
    }

    public void setRepository(EntityRepository repository) {
        this.repository = repository;
    }

    public List<EntityIssueEvent> getEvents() {
        return events;
    }

    public void setEvents(List<EntityIssueEvent> events) {
        this.events = events;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public long getIdIssue() {
        return idIssue;
    }

    public void setIdIssue(long idIssue) {
        this.idIssue = idIssue;
    }

    public List<EntityLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<EntityLabel> labels) {
        this.labels = labels;
    }

    public EntityMilestone getMilestone() {
        return milestone;
    }

    public void setMilestone(EntityMilestone milestone) {
        this.milestone = milestone;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public EntityPullRequest getPullRequest() {
        return pullRequest;
    }

    public void setPullRequest(EntityPullRequest pullRequest) {
        this.pullRequest = pullRequest;
    }

    public String getStateIssue() {
        return stateIssue;
    }

    public void setStateIssue(String stateIssue) {
        this.stateIssue = stateIssue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public EntityUser getUserIssue() {
        return userIssue;
    }

    public void setUserIssue(EntityUser userIssue) {
        this.userIssue = userIssue;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EntityIssue)) {
            return false;
        }
        EntityIssue other = (EntityIssue) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.edu.utfpr.cm.JGitMiner.pojo.EntityIssue[ id=" + id + " ]";
    }

    public void addComment(EntityComment comment) {
        if (!getComments().contains(comment) || comment.getId() == null || comment.getId().equals(new Long(0))) {
            getComments().add(comment);
        }
    }

    public void addLabel(EntityLabel label) {
        if (!labels.contains(label)) {
            labels.add(label);
        }
    }

    public void addEvent(EntityIssueEvent issueEvent) {
        if (!events.contains(issueEvent)) {
            events.add(issueEvent);
        }
        issueEvent.setIssue(this);
    }
}

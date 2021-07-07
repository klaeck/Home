package com.softserveinc.ita.homeproject.homedata.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "votes")
@SequenceGenerator(name = "sequence", sequenceName = "votes_sequence")
public class Vote extends BaseEntity {

    @Column(name = "poll_id")
    private Long pollId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.PERSIST)
    private List<QuestionVote> questionVotes;
}
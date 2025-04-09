package ldbc.snb.datagen.entities.dynamic;

import ldbc.snb.datagen.generator.DatagenParams;
import ldbc.snb.datagen.entities.dynamic.person.PersonSummary;
import ldbc.snb.datagen.entities.dynamic.relations.ForumMembership;

import java.util.ArrayList;
import java.util.List;

public class Forum implements DynamicActivity {
    public enum ForumType {
        WALL,
        ALBUM,
        GROUP
    }

    private boolean isExplicitlyDeleted;
    private long id;
    private PersonSummary moderator;
    private long moderatorDeletionDate;
    private long creationDate;
    private long deletionDate;
    private String title;
    private List<Integer> tags;
    private int placeId;
    private int language;
    private List<ForumMembership> memberships;
    private ForumType forumType;


    public Forum(long id, long creationDate, long deletionDate, PersonSummary moderator, long moderatorDeletionDate, String title, int placeId, int language, ForumType forumType, boolean isExplicitlyDeleted) {
        assert (moderator.getCreationDate() + DatagenParams.delta) <= creationDate : "Moderator's creation date is less than or equal to the Forum creation date";
        memberships = new ArrayList<>();
        tags = new ArrayList<>();
        this.id = id;
        this.creationDate = creationDate;
        this.deletionDate = deletionDate;
        this.title = title;
        this.placeId = placeId;
        this.moderator = new PersonSummary(moderator);
        this.moderatorDeletionDate = moderatorDeletionDate;
        this.language = language;
        this.forumType = forumType;
        this.isExplicitlyDeleted = isExplicitlyDeleted;
    }

    public void addMember(ForumMembership member) {
        memberships.add(member);
    }

    public boolean isExplicitlyDeleted() {
        return isExplicitlyDeleted;
    }

    public void setExplicitlyDeleted(boolean explicitlyDeleted) {
        isExplicitlyDeleted = explicitlyDeleted;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PersonSummary getModerator() {
        return moderator;
    }

    public void setModerator(PersonSummary moderator) {
        this.moderator = moderator;
    }

    public long getModeratorDeletionDate() {
        return moderatorDeletionDate;
    }

    public void setModeratorDeletionDate(long moderatorDeletionDate) {
        this.moderatorDeletionDate = moderatorDeletionDate;
    }

    @Override
    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public long getDeletionDate() {
        return deletionDate;
    }

    public void setDeletionDate(long deletionDate) {
        this.deletionDate = deletionDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public List<ForumMembership> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<ForumMembership> memberships) {
        this.memberships = memberships;
    }

    public ForumType getForumType() {
        return forumType;
    }

    public void setForumType(ForumType forumType) {
        this.forumType = forumType;
    }
}

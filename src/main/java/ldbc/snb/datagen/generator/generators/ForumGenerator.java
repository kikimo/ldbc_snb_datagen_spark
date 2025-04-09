
package ldbc.snb.datagen.generator.generators;

import ldbc.snb.datagen.generator.DatagenParams;
import ldbc.snb.datagen.generator.dictionary.Dictionaries;
import ldbc.snb.datagen.entities.dynamic.Forum;
import ldbc.snb.datagen.entities.dynamic.person.Person;
import ldbc.snb.datagen.entities.dynamic.person.PersonSummary;
import ldbc.snb.datagen.entities.dynamic.relations.ForumMembership;
import ldbc.snb.datagen.entities.dynamic.relations.Knows;
import ldbc.snb.datagen.util.RandomGeneratorFarm;
import ldbc.snb.datagen.util.StringUtils;
import ldbc.snb.datagen.generator.vocabulary.SN;

import java.util.*;

/**
 * This class generates Forums (Walls, Groups and Albums).
 */
public class ForumGenerator {

    /**
     * Creates a personal wall for a given Person. All friends become members
     *
     * @param randomFarm randomFarm
     * @param forumId    forumID
     * @param person     Person
     * @return Forum
     */
    Forum createWall(RandomGeneratorFarm randomFarm, long forumId, Person person, long blockId) {

        int language = person.getLanguages().get(randomFarm.get(RandomGeneratorFarm.Aspect.LANGUAGE).nextInt(person.getLanguages().size()));

        // Check moderator can be added
        if (person.getDeletionDate() - person.getCreationDate() + DatagenParams.delta < 0){
            // what to return?
            return null;
        }

        Forum forum = new Forum(SN.formId(SN.composeId(forumId, person.getCreationDate() + DatagenParams.delta), blockId),
                person.getCreationDate() + DatagenParams.delta,
                person.getDeletionDate(),
                new PersonSummary(person),
                person.getDeletionDate(),
                StringUtils.clampString("Wall of " + person.getFirstName() + " " + person.getLastName(), 256),
                person.getCityId(),
                language,
                Forum.ForumType.WALL,
                false);

        // wall inherits tags from person
        List<Integer> forumTags = new ArrayList<>(person.getInterests());
        forum.setTags(forumTags);

        // adds all friends as members of wall
        List<Knows> knows = person.getKnows();

        // for each friend generate hasMember edge
        for (Knows know : knows) {
            long hasMemberCreationDate = know.getCreationDate() + DatagenParams.delta;
            long hasMemberDeletionDate = Math.min(forum.getDeletionDate(), know.getDeletionDate());
            if (hasMemberDeletionDate - hasMemberCreationDate < 0){
                continue;
            }
            forum.addMember(new ForumMembership(forum.getId(), hasMemberCreationDate, hasMemberDeletionDate, know.to(), Forum.ForumType.WALL, false));
        }
        return forum;
    }

    /**
     * Creates a Group with the Person as the moderator. 30% membership come from friends the rest are random.
     *
     * @param randomFarm random number generator
     * @param forumId    forumID
     * @param moderator  moderator
     * @param block      person block
     * @return Group
     */
    Forum createGroup(RandomGeneratorFarm randomFarm, long forumId, Person moderator, List<Person> block, long blockId) {

        // creation date
        long groupMinCreationDate = moderator.getCreationDate() + DatagenParams.delta;
        long groupMaxCreationDate = Math.min(moderator.getDeletionDate(), Dictionaries.dates.getSimulationEnd());
        long groupCreationDate = Dictionaries.dates.randomDate(randomFarm.get(RandomGeneratorFarm.Aspect.DATE), groupMinCreationDate, groupMaxCreationDate);

        // deletion date
        long groupDeletionDate;
        boolean isExplicitlyDeleted;
        if (randomFarm.get(RandomGeneratorFarm.Aspect.DELETION_FORUM).nextDouble() < DatagenParams.probForumDeleted) {
            isExplicitlyDeleted = true;
            long groupMinDeletionDate = groupCreationDate + DatagenParams.delta;
            long groupMaxDeletionDate = Dictionaries.dates.getSimulationEnd();
            groupDeletionDate = Dictionaries.dates.randomDate(randomFarm.get(RandomGeneratorFarm.Aspect.DATE), groupMinDeletionDate, groupMaxDeletionDate);
        } else {
            isExplicitlyDeleted = false;
            groupDeletionDate = Dictionaries.dates.getNetworkCollapse();
        }

        // the hasModerator edge is deleted if either the Forum (group) or the Person (moderator) is deleted
        long moderatorDeletionDate = Math.min(groupDeletionDate, moderator.getDeletionDate());

        int language = moderator.getLanguages().get(randomFarm.get(RandomGeneratorFarm.Aspect.LANGUAGE).nextInt(moderator.getLanguages().size()));

        Iterator<Integer> iter = moderator.getInterests().iterator();
        int idx = randomFarm.get(RandomGeneratorFarm.Aspect.FORUM_INTEREST).nextInt(moderator.getInterests().size());
        for (int i = 0; i < idx; i++) {
            iter.next();
        }
        int interestId = iter.next();
        List<Integer> interest = new ArrayList<>();
        interest.add(interestId);

        // Create group
        Forum forum = new Forum(SN.formId(SN.composeId(forumId, groupCreationDate), blockId),
                groupCreationDate,
                groupDeletionDate,
                new PersonSummary(moderator),
                moderatorDeletionDate,
                StringUtils.clampString("Group for " + Dictionaries.tags.getName(interestId)
                        .replace("\"", "\\\"") + " in " + Dictionaries.places
                        .getPlaceName(moderator.getCityId()), 256),
                moderator.getCityId(),
                language,
                Forum.ForumType.GROUP,
                isExplicitlyDeleted
        );

        // Set tags of this forum
        forum.setTags(interest);

        // Add members
        TreeSet<Long> groupMembers = new TreeSet<>();
        List<Knows> moderatorKnows = new ArrayList<>(moderator.getKnows());
        int numModeratorKnows = moderatorKnows.size();
        int groupSize = randomFarm.get(RandomGeneratorFarm.Aspect.NUM_USERS_PER_FORUM).nextInt(DatagenParams.maxGroupSize);
        int numLoop = 0;
        while ((forum.getMemberships().size() < groupSize) && (numLoop < DatagenParams.blockSize)) {
            double prob = randomFarm.get(RandomGeneratorFarm.Aspect.KNOWS_LEVEL).nextDouble(); // controls the proportion of members that are friends
            if (prob < 0.3 && numModeratorKnows > 0) {
                // pick random knows edge from friends
                int knowsIndex = randomFarm.get(RandomGeneratorFarm.Aspect.MEMBERSHIP_INDEX).nextInt(numModeratorKnows);
                Knows knows = moderatorKnows.get(knowsIndex);
                if (!groupMembers.contains(knows.to().getAccountId())) { // if friend not already member of group

                    long minCreationDate = Math.max(forum.getCreationDate(), knows.to().getCreationDate()) + DatagenParams.delta;
                    long maxCreationDate = Collections.min(Arrays.asList(forum.getDeletionDate(), knows.to().getDeletionDate(), Dictionaries.dates.getSimulationEnd()));

                    if (maxCreationDate - minCreationDate > 0) {

                        Random random = randomFarm.get(RandomGeneratorFarm.Aspect.MEMBERSHIP_INDEX);
                        long hasMemberCreationDate = Dictionaries.dates.randomDate(random, minCreationDate, maxCreationDate);

                        long hasMemberDeletionDate;
                        boolean isHasMemberExplicitlyDeleted;
                        if (randomFarm.get(RandomGeneratorFarm.Aspect.DELETION_MEMB).nextDouble() < DatagenParams.probMembDeleted) {
                            isHasMemberExplicitlyDeleted = true;
                            long minDeletionDate = hasMemberCreationDate + DatagenParams.delta;
                            long maxDeletionDate = Collections.min(Arrays.asList(knows.to().getDeletionDate(), forum.getDeletionDate(), Dictionaries.dates.getSimulationEnd()));
                            if (maxDeletionDate - minDeletionDate < 0) {
                                continue;
                            }
                            hasMemberDeletionDate = Dictionaries.dates.randomDate(random, minDeletionDate, maxDeletionDate);
                        } else {
                            isHasMemberExplicitlyDeleted = false;
                            hasMemberDeletionDate = Collections.min(Arrays.asList(knows.to().getDeletionDate(), forum.getDeletionDate()));
                        }
                        ForumMembership hasMember = new ForumMembership(forum.getId(), hasMemberCreationDate, hasMemberDeletionDate, knows.to(), Forum.ForumType.GROUP, isHasMemberExplicitlyDeleted);
                        forum.addMember(hasMember);
                        groupMembers.add(knows.to().getAccountId());
                    }
                }
            } else { // pick from the person block
                int candidateIndex = randomFarm.get(RandomGeneratorFarm.Aspect.MEMBERSHIP_INDEX)
                        .nextInt(block.size());
                Person member = block.get(candidateIndex);
                prob = randomFarm.get(RandomGeneratorFarm.Aspect.MEMBERSHIP).nextDouble();
                if ((prob < 0.1) && !groupMembers.contains(member.getAccountId())) {

                    long minHasMemberCreationDate = Math.max(forum.getCreationDate(), member.getCreationDate()) + DatagenParams.delta;
                    long maxHasMemberCreationDate = Collections.min(Arrays.asList(forum.getDeletionDate(), member.getDeletionDate(), Dictionaries.dates.getSimulationEnd()));

                    if (maxHasMemberCreationDate - minHasMemberCreationDate > 0) {

                        Random random = randomFarm.get(RandomGeneratorFarm.Aspect.MEMBERSHIP_INDEX);
                        long hasMemberCreationDate = Dictionaries.dates.randomDate(random, minHasMemberCreationDate, maxHasMemberCreationDate);

                        long hasMemberDeletionDate;
                        boolean isHasMemberExplicitlyDeleted;
                        if (randomFarm.get(RandomGeneratorFarm.Aspect.DELETION_MEMB).nextDouble() < DatagenParams.probMembDeleted) {
                            isHasMemberExplicitlyDeleted = true;
                            long minHasMemberDeletionDate = hasMemberCreationDate + DatagenParams.delta;
                            long maxHasMemberDeletionDate = Collections.min(Arrays.asList(member.getDeletionDate(), forum.getDeletionDate(), Dictionaries.dates.getSimulationEnd()));
                            if (maxHasMemberCreationDate - minHasMemberDeletionDate < 0) {
                                continue;
                            }
                            hasMemberDeletionDate = Dictionaries.dates.randomDate(random, minHasMemberDeletionDate, maxHasMemberDeletionDate);
                        } else {
                            isHasMemberExplicitlyDeleted = false;
                            hasMemberDeletionDate = Collections.min(Arrays.asList(member.getDeletionDate(), forum.getDeletionDate()));
                        }
                        forum.addMember(new ForumMembership(forum.getId(), hasMemberCreationDate, hasMemberDeletionDate, new PersonSummary(member), Forum.ForumType.GROUP, isHasMemberExplicitlyDeleted));
                        groupMembers.add(member.getAccountId());
                    }
                }
            }
            numLoop++;
        }
        return forum;
    }

    /**
     * Creates an album for a given Person.
     *
     * @param randomFarm random farm
     * @param forumId    forumId
     * @param person     Person who the album belongs it
     * @param numAlbum   number of album e.g. Album 10
     * @return Album
     */
    Forum createAlbum(RandomGeneratorFarm randomFarm, long forumId, Person person, int numAlbum, long blockId) {

        long minAlbumCreationDate = person.getCreationDate() + DatagenParams.delta;
        long maxAlbumCreationDate = Math.min(person.getDeletionDate(), Dictionaries.dates.getSimulationEnd());
        long albumCreationDate = Dictionaries.dates.randomDate(randomFarm.get(RandomGeneratorFarm.Aspect.DATE), minAlbumCreationDate, maxAlbumCreationDate);

        long albumDeletionDate;
        boolean isExplicitlyDeleted;
        if (randomFarm.get(RandomGeneratorFarm.Aspect.DELETION_FORUM).nextDouble() < DatagenParams.probForumDeleted) {
            isExplicitlyDeleted = true;
            long minAlbumDeletionDate = albumCreationDate + DatagenParams.delta;
            long maxAlbumDeletionDate = Math.min(person.getDeletionDate(), Dictionaries.dates.getSimulationEnd());
            if (maxAlbumDeletionDate - minAlbumCreationDate < 0) {
                return null;
            }
            albumDeletionDate = Dictionaries.dates.randomDate(randomFarm.get(RandomGeneratorFarm.Aspect.DATE), minAlbumDeletionDate, maxAlbumDeletionDate);
        } else {
            isExplicitlyDeleted = false;
            albumDeletionDate = person.getDeletionDate();
        }

        int language = person.getLanguages().get(randomFarm.get(RandomGeneratorFarm.Aspect.LANGUAGE).nextInt(person.getLanguages().size()));
        Forum forum = new Forum(SN.formId(SN.composeId(forumId, albumCreationDate), blockId),
                albumCreationDate,
                albumDeletionDate,
                new PersonSummary(person),
                person.getDeletionDate(),
                StringUtils.clampString("Album " + numAlbum + " of " + person.getFirstName() + " " + person
                        .getLastName(), 256),
                person.getCityId(),
                language,
                Forum.ForumType.ALBUM,
                isExplicitlyDeleted
        );

        Iterator<Integer> iter = person.getInterests().iterator();
        int idx = randomFarm.get(RandomGeneratorFarm.Aspect.FORUM_INTEREST).nextInt(person.getInterests().size());
        for (int i = 0; i < idx; i++) {
            iter.next();
        }
        int interestId = iter.next();
        List<Integer> interest = new ArrayList<>();
        interest.add(interestId);
        forum.setTags(interest);

        List<Integer> countries = Dictionaries.places.getCountries();
        int randomCountry = randomFarm.get(RandomGeneratorFarm.Aspect.COUNTRY).nextInt(countries.size());
        forum.setPlaceId(countries.get(randomCountry));

        List<Knows> friends = new ArrayList<>(person.getKnows());
        for (Knows knows : friends) {
            double prob = randomFarm.get(RandomGeneratorFarm.Aspect.ALBUM_MEMBERSHIP).nextDouble();
            if (prob < 0.7) {
                long hasMemberCreationDate = Math.max(knows.to().getCreationDate(), forum.getCreationDate()) + DatagenParams.delta;
                long hasMemberDeletionDate = Collections.min(Arrays.asList(knows.to().getDeletionDate(), forum.getDeletionDate()));
                if (hasMemberDeletionDate - hasMemberCreationDate > 0) {
                    forum.addMember(new ForumMembership(forum.getId(), hasMemberCreationDate, hasMemberDeletionDate, knows.to(), Forum.ForumType.ALBUM, false));
                }
            }
        }
        return forum;
    }
}

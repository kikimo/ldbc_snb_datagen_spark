package ldbc.snb.datagen.generator.generators.postgenerators;

import ldbc.snb.datagen.generator.DatagenParams;
import ldbc.snb.datagen.generator.dictionary.Dictionaries;
import ldbc.snb.datagen.entities.dynamic.Forum;
import ldbc.snb.datagen.entities.dynamic.relations.ForumMembership;
import ldbc.snb.datagen.entities.statictype.tag.FlashMobTag;
import ldbc.snb.datagen.generator.generators.CommentGenerator;
import ldbc.snb.datagen.generator.generators.LikeGenerator;
import ldbc.snb.datagen.generator.generators.textgenerators.TextGenerator;
import ldbc.snb.datagen.util.Distribution;

import java.util.*;

import static ldbc.snb.datagen.generator.DatagenParams.maxNumTagPerFlashmobPost;

public class FlashmobPostGenerator extends PostGenerator {

    private Distribution dateDistribution;
    private FlashMobTag[] forumFlashmobTags = null;
    private long flashmobSpan;
    private long currentForum = -1;

    public FlashmobPostGenerator(TextGenerator generator, CommentGenerator commentGenerator, LikeGenerator likeGenerator) {
        super(generator, commentGenerator, likeGenerator);
        dateDistribution = new Distribution(DatagenParams.flashmobDistFile);
        long hoursToMillis = 60 * 60 * 1000;
        flashmobSpan = 72 * hoursToMillis; // flash mobs last 3 days
        dateDistribution.initialize(); // create flashmob distribution
    }

    /**
     * Selects a random tag from a given index.
     * @param randomFlashmobTag random number generator
     * @param tags The array of sorted tags to select from.
     * @param index The first tag to consider.
     * @return The index of a random tag.
     */
    private int selectRandomTag(Random randomFlashmobTag, FlashMobTag[] tags, int index) {
        int upperBound = tags.length - 1;
        int lowerBound = index;
        double prob = randomFlashmobTag
                .nextDouble() * (tags[upperBound].prob - tags[lowerBound].prob) + tags[lowerBound].prob;
        int midPoint = (upperBound + lowerBound) / 2;
        while (upperBound > (lowerBound + 1)) {
            if (tags[midPoint].prob > prob) {
                upperBound = midPoint;
            } else {
                lowerBound = midPoint;
            }
            midPoint = (upperBound + lowerBound) / 2;
        }
        return midPoint;
    }



    /**
     * Selects the earliest flashmob tag index from a given date.
     * @param tags flashmob tags
     * @param membership forum members
     * @return The index to the earliest flashmob tag.
     */
    private int searchEarliest(FlashMobTag[] tags, ForumMembership membership) {
        long fromDate = membership.getCreationDate() + flashmobSpan / 2 + DatagenParams.delta;
        int lowerBound = 0;
        int upperBound = tags.length - 1;
        int midPoint = (upperBound + lowerBound) / 2;
        while (upperBound > (lowerBound + 1)) {
            if (tags[midPoint].date > fromDate) {
                upperBound = midPoint;
            } else {
                lowerBound = midPoint;
            }
            midPoint = (upperBound + lowerBound) / 2;
        }
        if (tags[midPoint].date < fromDate) return -1;
        return midPoint;
    }

    /**
     *
     * @param randomNumPost random number generator
     * @param forum forum
     */
    private void populateForumFlashmobTags(Random randomNumPost, Forum forum) {

        TreeSet<Integer> tags = new TreeSet<>(); // empty set
        List<FlashMobTag> temp = Dictionaries.flashmobs.generateFlashmobTags(randomNumPost, tags, forum.getCreationDate());

        forumFlashmobTags = new FlashMobTag[temp.size()];

        Iterator<FlashMobTag> it = temp.iterator();
        int index = 0;
        int sumLevels = 0;
        while (it.hasNext()) {
            FlashMobTag flashmobTag = new FlashMobTag();
            it.next().copyTo(flashmobTag);
            forumFlashmobTags[index] = flashmobTag;
            sumLevels += flashmobTag.level;
            ++index;
        }
        Arrays.sort(forumFlashmobTags);
        double currentProb = 0.0;
        for (FlashMobTag forumFlashmobTag : forumFlashmobTags) {
            forumFlashmobTag.prob = currentProb;
            currentProb += (double) (forumFlashmobTag.level) / (double) (sumLevels);
        }
    }

    /**
     * Generate flashmob post information
     * @param randomTag random number generator
     * @param randomDate random number generator
     * @param forum forum
     * @param membership forum member
     * @return core information for a post during a flashmob
     */
    protected PostCore generatePostInfo(Random randomDeletePost, Random randomTag, Random randomDate, final Forum forum, final ForumMembership membership, int numComments) {

        if (currentForum != forum.getId()) {
            populateForumFlashmobTags(randomTag, forum);
            currentForum = forum.getId();
        }
        if (forumFlashmobTags.length < 1) return null;

        PostCore postCore = new PostCore();

        int index = searchEarliest(forumFlashmobTags, membership);
        if (index < 0) return null;
        index = selectRandomTag(randomTag, forumFlashmobTags, index);
        FlashMobTag flashmobTag = forumFlashmobTags[index];
        postCore.getTags().add(flashmobTag.tag); // add flashmob tag

        for (int i = 0; i < maxNumTagPerFlashmobPost - 1; ++i) {
            if (randomTag.nextDouble() < 0.05) {
                int tag = Dictionaries.tagMatrix.getRandomRelated(randomTag, flashmobTag.tag);
                postCore.getTags().add(tag);
            }
        }

        // add creation date
        long minCreationDate = membership.getCreationDate() + DatagenParams.delta;
        long maxCreationDate = Math.min(membership.getDeletionDate(),Dictionaries.dates.getSimulationEnd());
        if (maxCreationDate - minCreationDate < 0) {
            return null;
        }
        double prob = dateDistribution.nextDouble(randomDate);
        long creationDate = flashmobTag.date - flashmobSpan / 2 + (long) (prob * flashmobSpan);

        if (creationDate <= minCreationDate || creationDate >= maxCreationDate) {
            return null;
        }
        postCore.setCreationDate(creationDate);

        // add deletion date
        long postDeletionDate;
        if (membership.getPerson().isMessageDeleter() && randomDeletePost.nextDouble() < DatagenParams.postMapping[numComments]) {
            postCore.setExplicitlyDeleted(true);
            long minDeletionDate = creationDate + DatagenParams.delta;
            long maxDeletionDate = Math.min(membership.getDeletionDate(), Dictionaries.dates.getSimulationEnd());
            if (maxDeletionDate - minDeletionDate < 0) {
                return null;
            }
            postDeletionDate = Dictionaries.dates.powerLawDeleteDate(randomDate, minDeletionDate, maxDeletionDate);
        } else {
            postCore.setExplicitlyDeleted(false);
            postDeletionDate = Math.min(membership.getDeletionDate(), Dictionaries.dates.getSimulationEnd());
        }
        postCore.setDeletionDate(postDeletionDate);


        return postCore;
    }
}

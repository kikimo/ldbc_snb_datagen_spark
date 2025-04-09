package ldbc.snb.datagen.generator.generators;

import ldbc.snb.datagen.generator.DatagenParams;
import ldbc.snb.datagen.generator.dictionary.Dictionaries;
import ldbc.snb.datagen.entities.dynamic.Forum;
import ldbc.snb.datagen.entities.dynamic.messages.Comment;
import ldbc.snb.datagen.entities.dynamic.messages.Photo;
import ldbc.snb.datagen.entities.dynamic.messages.Post;
import ldbc.snb.datagen.entities.dynamic.person.Person;
import ldbc.snb.datagen.entities.dynamic.person.PersonSummary;
import ldbc.snb.datagen.entities.dynamic.relations.ForumMembership;
import ldbc.snb.datagen.entities.dynamic.relations.Like;
import ldbc.snb.datagen.generator.generators.postgenerators.FlashmobPostGenerator;
import ldbc.snb.datagen.generator.generators.postgenerators.UniformPostGenerator;
import ldbc.snb.datagen.generator.generators.textgenerators.LdbcSnbTextGenerator;
import ldbc.snb.datagen.generator.generators.textgenerators.TextGenerator;
import ldbc.snb.datagen.util.Iterators;
import ldbc.snb.datagen.util.RandomGeneratorFarm;
import ldbc.snb.datagen.util.Streams;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class PersonActivityGenerator {

    private long startForumId = 0;
    private Iterator<Long> messageIdIterator;

    private RandomGeneratorFarm randomFarm;
    private ForumGenerator forumGenerator;
    private UniformPostGenerator uniformPostGenerator;
    private FlashmobPostGenerator flashmobPostGenerator;
    private PhotoGenerator photoGenerator;

    public PersonActivityGenerator() {

        randomFarm = new RandomGeneratorFarm();
        forumGenerator = new ForumGenerator();

        TextGenerator generator = new LdbcSnbTextGenerator(randomFarm.get(RandomGeneratorFarm.Aspect.LARGE_TEXT), Dictionaries.tags);
        LikeGenerator likeGenerator = new LikeGenerator();
        CommentGenerator commentGenerator = new CommentGenerator(generator, likeGenerator);
        uniformPostGenerator = new UniformPostGenerator(generator, commentGenerator, likeGenerator);
        flashmobPostGenerator = new FlashmobPostGenerator(generator, commentGenerator, likeGenerator);
        photoGenerator = new PhotoGenerator(likeGenerator);

        messageIdIterator = Iterators.numbers(0);
    }

    private GenActivity generateActivity(Person person, List<Person> block, long blockId) throws AssertionError {
        try {
            return new GenActivity(
                    generateWall(person, blockId),
                    generateGroups(person, block, blockId),
                    generateAlbums(person, blockId)
            );

        } catch (AssertionError e) {
            System.out.println("Assertion error when generating activity!");
            System.out.println(e.getMessage());
            throw e;
        }
    }

    /**
     * Generates the personal wall for a Person. Note, only this Person creates Posts in the wall.
     *
     * @param person Person
     */
    private GenWall<Triplet<Post, Stream<Like>, Stream<Pair<Comment, Stream<Like>>>>> generateWall(Person person, long blockId) {

        // Generate wall
        Forum wall = forumGenerator.createWall(randomFarm, startForumId++, person, blockId);

        // Could be null as moderator can't be added
        if (wall == null)
            return new GenWall<>(Stream.empty());

        // creates a forum membership for the moderator
        // only the moderator can post on their wall
        ForumMembership moderator = new ForumMembership(wall.getId(),
                wall.getCreationDate() + DatagenParams.delta,
                wall.getDeletionDate(),
                new PersonSummary(person),
                Forum.ForumType.WALL,
                false);
        // list of members who can post on the wall - only moderator of wall can post on it
        List<ForumMembership> memberships = new ArrayList<>();
        memberships.add(moderator);

        Stream<Triplet<Post, Stream<Like>, Stream<Pair<Comment, Stream<Like>>>>> uniform = uniformPostGenerator.createPosts(
                randomFarm, wall, memberships,
                numPostsPerGroup(randomFarm, wall, DatagenParams.maxNumPostPerMonth, DatagenParams.maxNumFriends),
                messageIdIterator, blockId);

        Stream<Triplet<Post, Stream<Like>, Stream<Pair<Comment, Stream<Like>>>>> flashMob = flashmobPostGenerator.createPosts(
                randomFarm, wall, memberships,
                numPostsPerGroup(randomFarm, wall, DatagenParams.maxNumFlashmobPostPerMonth, DatagenParams.maxNumFriends),
                messageIdIterator, blockId);

        return new GenWall<>(Stream.of(
                new Triplet<>(wall, wall.getMemberships().stream(), Stream.concat(uniform, flashMob)))
        );
    }

    /**
     * Generates the Groups for a Person. Has 5% chance of becoming a moderator of some group(s).
     *
     * @param person persons
     * @param block  block for persons
     */
    private Stream<GenWall<Triplet<Post, Stream<Like>, Stream<Pair<Comment, Stream<Like>>>>>> generateGroups(Person person, List<Person> block, long blockId) {

        // generate person created groups
        double moderatorProb = randomFarm.get(RandomGeneratorFarm.Aspect.FORUM_MODERATOR).nextDouble();
            int numGroup = randomFarm.get(RandomGeneratorFarm.Aspect.NUM_FORUM)
                    .nextInt(DatagenParams.maxNumGroupCreatedPerPerson) + 1;

        return Streams.stream(Iterators.forIterator(0, i -> i < numGroup, i -> ++i, i -> {
            if (moderatorProb >= DatagenParams.groupModeratorProb)
                return Iterators.ForIterator.CONTINUE();

            Forum group = forumGenerator.createGroup(randomFarm, startForumId++, person, block, blockId);

            Stream<Triplet<Post, Stream<Like>, Stream<Pair<Comment, Stream<Like>>>>> uniform = uniformPostGenerator.createPosts(
                    randomFarm,
                    group,
                    group.getMemberships(),
                    numPostsPerGroup(randomFarm, group, DatagenParams.maxNumGroupPostPerMonth, DatagenParams.maxGroupSize),
                    messageIdIterator, blockId);
            Stream<Triplet<Post, Stream<Like>, Stream<Pair<Comment, Stream<Like>>>>> flashMob  = flashmobPostGenerator.createPosts(
                    randomFarm,
                    group,
                    group.getMemberships(),
                    numPostsPerGroup(randomFarm, group, DatagenParams.maxNumGroupFlashmobPostPerMonth, DatagenParams.maxGroupSize),
                    messageIdIterator, blockId);

            return Iterators.ForIterator.RETURN(new GenWall<>(Stream.of(
                    new Triplet<>(group, group.getMemberships().stream(), Stream.concat(uniform, flashMob)))
            ));
        }));
    }

    /**
     * Generates the albums for a Person.
     *
     * @param person person
     */
    private GenWall<Pair<Photo, Stream<Like>>> generateAlbums(Person person, long blockId) {

        // work out number of albums to generate
        int numberOfMonths = (int) Dictionaries.dates.numberOfMonths(person.getCreationDate());
        int numberOfPhotoAlbums = randomFarm.get(RandomGeneratorFarm.Aspect.NUM_PHOTO_ALBUM).nextInt(DatagenParams.maxNumPhotoAlbumsPerMonth + 1);
        int numberOfPhotoAlbumsForMonths = numberOfPhotoAlbums == 0
                ? numberOfPhotoAlbums
                : numberOfMonths * numberOfPhotoAlbums;

        return new GenWall<>(Streams.stream(Iterators.forIterator(0, i -> i < numberOfPhotoAlbumsForMonths, i -> ++i, i -> {
            Forum album = forumGenerator.createAlbum(randomFarm, startForumId++, person, i, blockId);
            if (album == null) {
                return Iterators.ForIterator.CONTINUE();
            }

            // number of photos to generate
            int numPhotosInAlbum = randomFarm.get(RandomGeneratorFarm.Aspect.NUM_PHOTO)
                    .nextInt(DatagenParams.maxNumPhotoPerAlbums + 1);
            // create photos

            Stream<Pair<Photo, Stream<Like>>> photos = photoGenerator.createPhotos(randomFarm, album, numPhotosInAlbum, messageIdIterator, blockId);

            return Iterators.ForIterator.RETURN(new Triplet<>(
                 album, album.getMemberships().stream(), photos
            ));
        })));
    }

    private int numPostsPerGroup(RandomGeneratorFarm randomFarm, Forum forum, int maxPostsPerMonth, int maxMembersPerForum) {
        Random random = randomFarm.get(RandomGeneratorFarm.Aspect.NUM_POST);
        int numOfMonths = (int) Dictionaries.dates.numberOfMonths(forum.getCreationDate());
        int numberPost;
        if (numOfMonths == 0) {
            numberPost = random.nextInt(maxPostsPerMonth + 1);
        } else {
            numberPost = random.nextInt(maxPostsPerMonth * numOfMonths + 1);
        }
        return (numberPost * forum.getMemberships().size()) / maxMembersPerForum;
    }

    public Stream<GenActivity> generateActivityForBlock(int blockId, List<Person> block) {
        randomFarm.resetRandomGenerators(blockId);
        startForumId = 0;
        messageIdIterator = Iterators.numbers(0);
        return block.stream().map(p -> generateActivity(p, block, blockId));
    }
}

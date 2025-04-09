package ldbc.snb.datagen.generator;

import ldbc.snb.datagen.entities.dynamic.person.Person;
import ldbc.snb.datagen.entities.dynamic.person.similarity.GeoDistanceSimilarity;
import ldbc.snb.datagen.entities.dynamic.person.similarity.InterestsSimilarity;
import ldbc.snb.datagen.generator.distribution.DegreeDistribution;
import ldbc.snb.datagen.generator.distribution.FacebookDegreeDistribution;
import ldbc.snb.datagen.generator.distribution.ZipfDistribution;
import ldbc.snb.datagen.util.GeneratorConfiguration;

public class DatagenParams {

    // Files and folders
    public static final String DICTIONARY_DIRECTORY = "/dictionaries/";
    public static final String IPZONE_DIRECTORY = "/ipaddrByCountries";
    public static final String PERSON_FACTORS_FILE = "personFactors.csv";
    public static final String POSTS_PER_COUNTRY_FACTOR_FILE = "postsPerCountryFactors.csv";
    public static final String TAGCLASS_FACTOR_FILE = "tagClassCountFactors.csv";
    public static final String TAG_FACTOR_FILE = "tagCountFactors.csv";
    public static final String FIRSTNAME_FACTOR_FILE = "firstNameCountFactors.csv";
    public static final String MISC_FACTOR_FILE = "miscFactors.csv";

    // Dictionaries dataset files
    public static final String browserDictionaryFile = DICTIONARY_DIRECTORY + "browsersDic.txt";
    public static final String companiesDictionaryFile = DICTIONARY_DIRECTORY + "companiesByCountry.txt";
    public static final String countryAbbrMappingFile = DICTIONARY_DIRECTORY + "countryAbbrMapping.txt";
    public static final String popularTagByCountryFile = DICTIONARY_DIRECTORY + "popularTagByCountry.txt";
    public static final String countryDictionaryFile = DICTIONARY_DIRECTORY + "dicLocations.txt";
    public static final String tagsFile = DICTIONARY_DIRECTORY + "tags.txt";
    public static final String emailDictionaryFile = DICTIONARY_DIRECTORY + "email.txt";
    public static final String nameDictionaryFile = DICTIONARY_DIRECTORY + "givennameByCountryBirthPlace.txt.freq.full";
    public static final String universityDictionaryFile = DICTIONARY_DIRECTORY + "universities.txt";
    public static final String cityDictionaryFile = DICTIONARY_DIRECTORY + "citiesByCountry.txt";
    public static final String languageDictionaryFile = DICTIONARY_DIRECTORY + "languagesByCountry.txt";
    public static final String popularDictionaryFile = DICTIONARY_DIRECTORY + "popularPlacesByCountry.txt";
    public static final String agentFile = DICTIONARY_DIRECTORY + "smartPhonesProviders.txt";
    public static final String surnamDictionaryFile = DICTIONARY_DIRECTORY + "surnameByCountryBirthPlace.txt.freq.sort";
    public static final String tagClassFile = DICTIONARY_DIRECTORY + "tagClasses.txt";
    public static final String tagClassHierarchyFile = DICTIONARY_DIRECTORY + "tagClassHierarchy.txt";
    public static final String tagTextFile = DICTIONARY_DIRECTORY + "tagText.txt";
    public static final String tagMatrixFile = DICTIONARY_DIRECTORY + "tagMatrix.txt";
    public static final String personDeleteFile = DICTIONARY_DIRECTORY + "personDelete.txt";
    public static final String flashmobDistFile = DICTIONARY_DIRECTORY + "flashmobDist.txt";
    public static final String fbSocialDegreeFile = DICTIONARY_DIRECTORY + "facebookBucket100.dat";
    public static final String powerLawActivityDeleteFile = DICTIONARY_DIRECTORY + "powerLawActivityDeleteDate.txt";
    //private parameters
    private enum ParameterNames {
        BASE_CORRELATED("generator.baseProbCorrelated"),
        BEST_UNIVERSTY_RATIO("generator.probTopUniv"),
        BLOCK_SIZE("generator.blockSize"),
        COMPANY_UNCORRELATED_RATIO("generator.probUnCorrelatedCompany"),
        DIFFERENT_IP_IN_TRAVEL_RATIO("generator.probDiffIPinTravelSeason"),
        DIFFERENT_IP_NOT_TRAVEL_RATIO("generator.probDiffIPnotTravelSeason"),
        ENGLISH_RATIO("generator.probEnglish"),
        FLASHMOB_TAGS_PER_MONTH("generator.flashmobTagsPerMonth"),
        FLASHMOB_TAG_DIST_EXP("generator.flashmobTagDistExp"),
        FLASHMOB_TAG_MAX_LEVEL("generator.flashmobTagMaxLevel"),
        FLASHMOB_TAG_MIN_LEVEL("generator.flashmobTagMinLevel"),
        GROUP_MAX_POST_MONTH("generator.maxNumGroupPostPerMonth"),
        GROUP_MODERATOR_RATIO("generator.groupModeratorProb"),
        LARGE_COMMENT_RATIO("generator.ratioLargeComment"),
        LARGE_POST_RATIO("generator.ratioLargePost"),
        LIMIT_CORRELATED("generator.limitProCorrelated"),
        MAX_COMMENT_POST("generator.maxNumComments"),
        MAX_COMMENT_SIZE("generator.maxCommentSize"),
        MAX_COMPANIES("generator.maxCompanies"),
        MAX_EMAIL("generator.maxEmails"),
        MAX_FRIENDS("generator.maxNumFriends"),
        MAX_GROUP_MEMBERS("generator.maxNumMemberGroup"),
        MAX_LARGE_COMMENT_SIZE("generator.maxLargeCommentSize"),
        MAX_LARGE_POST_SIZE("generator.maxLargePostSize"),
        MAX_NUM_FLASHMOB_POST_PER_MONTH("generator.maxNumFlashmobPostPerMonth"),
        MAX_NUM_GROUP_FLASHMOB_POST_PER_MONTH("generator.maxNumGroupFlashmobPostPerMonth"),
        MAX_NUM_TAG_PER_FLASHMOB_POST("generator.maxNumTagPerFlashmobPost"),
        MAX_PHOTOALBUM("generator.maxNumPhotoAlbumsPerMonth"),
        MAX_PHOTO_PER_ALBUM("generator.maxNumPhotoPerAlbums"),
        MAX_POPULAR_PLACES("generator.maxNumPopularPlaces"),
        MAX_TEXT_SIZE("generator.maxTextSize"),
        MIN_COMMENT_SIZE("generator.minCommentSize"),
        MIN_LARGE_COMMENT_SIZE("generator.minLargeCommentSize"),
        MIN_LARGE_POST_SIZE("generator.minLargePostSize"),
        MIN_TEXT_SIZE("generator.minTextSize"),
        MISSING_RATIO("generator.missingRatio"),
        OTHER_BROWSER_RATIO("generator.probAnotherBrowser"),
        POPULAR_PLACE_RATIO("generator.probPopularPlaces"),
        PROB_INTEREST_FLASHMOB_TAG("generator.probInterestFlashmobTag"),
        PROB_RANDOM_PER_LEVEL("generator.probRandomPerLevel"),
        REDUCE_TEXT_RATIO("generator.ratioReduceText"),
        SECOND_LANGUAGE_RATIO("generator.probSecondLang"),
        TAG_UNCORRELATED_COUNTRY("generator.tagCountryCorrProb"),
        UNIVERSITY_UNCORRELATED_RATIO("generator.probUnCorrelatedOrganisation"),
        MAX_NUM_LIKE("generator.maxNumLike"),
        USER_MAX_GROUP("generator.maxNumGroupCreatedPerPerson"),
        USER_MAX_POST_MONTH("generator.maxNumPostPerMonth"),
        USER_MAX_TAGS("generator.maxNumTagsPerPerson"),
        USER_MIN_TAGS("generator.minNumTagsPerPerson"),

//        PROB_PERSON_DELETED("generator.probPersonDeleted"),
        PROB_FORUM_DELETED("generator.probForumDeleted"),
        PROB_PHOTO_DELETED("generator.probPhotoDeleted"),
        PROB_COMMENT_DELETED("generator.probCommentDeleted"),
        PROB_KNOWS_DELETED("generator.probKnowsDeleted"),
        PROB_MEMB_DELETED("generator.probMembDeleted"),
        PROB_LIKE_DELETED("generator.probLikeDeleted");

        private final String name;

        ParameterNames(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    public static double baseProbCorrelated = 0.0; // the base probability to create a correlated edge between two persons
    public static double flashmobTagDistExp = 0.0; // the flashmob tag distribution exponent
    public static double flashmobTagMaxLevel = 0.0; // the flashmob tag max activity volume level
    public static double flashmobTagMinLevel = 0.0; // the flashmob tag min activity volume level
    public static double groupModeratorProb = 0.0;
    public static double limitProCorrelated = 0.0;
    public static double missingRatio = 0.0;
    public static double probAnotherBrowser = 0.0;
    public static double probDiffIPinTravelSeason = 0.0; // in travel season
    public static double probDiffIPnotTravelSeason = 0.0; // not in travel season
    public static double probEnglish = 0.0;
    public static double probInterestFlashmobTag = 0.0;
    public static double probPopularPlaces = 0.0; //probability of taking a photo at popular place
    public static double probRandomPerLevel = 0.0;
    public static double probSecondLang = 0.0;
    public static double probTopUniv = 0.0; // 90% persons go to top university
    public static double probUnCorrelatedCompany = 0.0;
    public static double probUnCorrelatedOrganisation = 0.0;
    public static double ratioLargeComment = 0.0;
    public static double ratioLargePost = 0.0;
    public static double ratioReduceText = 0.0; // 80% text has size less than 1/2 max size
    public static double tagCountryCorrProb = 0.0;
    public static int blockSize = 0;
    public static int flashmobTagsPerMonth = 0;
    public static int maxCommentSize = 0;
    public static int maxCompanies = 0;
    public static int maxEmails = 0;
    public static int maxLargeCommentSize = 0;
    public static int maxLargePostSize = 0;
    public static int maxNumComments = 0;
    public static int maxNumFlashmobPostPerMonth = 0;
    public static int maxNumFriends = 0;
    public static int maxNumGroupCreatedPerPerson = 0;
    public static int maxNumGroupFlashmobPostPerMonth = 0;
    public static int maxNumGroupPostPerMonth = 0;
    public static int maxGroupSize = 0;
    public static int maxNumLike = 0;
    public static int maxNumPhotoAlbumsPerMonth = 0;
    public static int maxNumPhotoPerAlbums = 0;
    public static int maxNumPopularPlaces = 0;
    public static int maxNumPostPerMonth = 0;
    public static int maxNumTagPerFlashmobPost = 0;
    public static int maxNumTagsPerPerson = 0;
    public static int maxTextSize = 0;
    public static int minCommentSize = 0;
    public static int minLargeCommentSize = 0;
    public static int minLargePostSize = 0;
    public static int minNumTagsPerPerson = 0;
    public static int minTextSize = 0;

    // deletion probs.
//    public static double probPersonDeleted = 0.0;
    public static double probForumDeleted = 0.0;
    public static double probPhotoDeleted = 0.0;
    public static double probCommentDeleted = 0.0;
    public static double probKnowsDeleted = 0.0;
    public static double probMembDeleted = 0.0;
    public static double probLikeDeleted = 0.0;
//    public static double probCommentLikeDeleted = 0.0;
    public static double[] postMapping = {
            0.18, 0.152,
            0.142, 0.1, 0.1, 0.1, 0.1,
            0.05, 0.05, 0.05,
            0.01, 0.01, 0.01, 0.01, 0.01,
            0.01, 0.01, 0.01, 0.01, 0.01, 0.01};


    public static final int startMonth = 1;
    public static final int startDay = 1;

    public static final double alpha = 0.4; // used for the power law distribution

    public static String degreeDistributionName;
    public static String knowsGeneratorName;
    public static String personSimularity;

    public static int delta = 10000;
    public static long numPersons = 10000;
    public static int startYear = 2010;
    public static int numYears = 3;
    public static boolean exportText = true;

    private static Integer intConf(GeneratorConfiguration conf, ParameterNames param) {
        return Integer.parseInt(conf.get(param.toString()));
    }

    private static Double doubleConf(GeneratorConfiguration conf, ParameterNames param) {
        return Double.parseDouble(conf.get(param.toString()));
    }

    public static void readConf(GeneratorConfiguration conf) {
        try {
            ParameterNames[] values = ParameterNames.values();
            for (ParameterNames value : values)
                if (conf.get(value.toString()) == null)
                    throw new IllegalStateException("Missing " + value.toString() + " parameter");

//            probPersonDeleted = doubleConf(conf, ParameterNames.PROB_PERSON_DELETED);
            probForumDeleted = doubleConf(conf, ParameterNames.PROB_FORUM_DELETED);
            probPhotoDeleted = doubleConf(conf, ParameterNames.PROB_PHOTO_DELETED);
            probCommentDeleted = doubleConf(conf, ParameterNames.PROB_COMMENT_DELETED);
            probKnowsDeleted = doubleConf(conf, ParameterNames.PROB_KNOWS_DELETED);
            probMembDeleted = doubleConf(conf, ParameterNames.PROB_MEMB_DELETED);
            probLikeDeleted = doubleConf(conf, ParameterNames.PROB_LIKE_DELETED);

            maxNumFriends = intConf(conf, ParameterNames.MAX_FRIENDS);
            minNumTagsPerPerson = intConf(conf, ParameterNames.USER_MIN_TAGS);
            maxNumTagsPerPerson = intConf(conf, ParameterNames.USER_MAX_TAGS);
            maxNumPostPerMonth = intConf(conf, ParameterNames.USER_MAX_POST_MONTH);
            maxNumComments = intConf(conf, ParameterNames.MAX_COMMENT_POST);
            limitProCorrelated = doubleConf(conf, ParameterNames.LIMIT_CORRELATED);
            baseProbCorrelated = doubleConf(conf, ParameterNames.BASE_CORRELATED);
            maxEmails = intConf(conf, ParameterNames.MAX_EMAIL);
            maxCompanies = intConf(conf, ParameterNames.MAX_EMAIL);
            probEnglish = doubleConf(conf, ParameterNames.MAX_EMAIL);
            probSecondLang = doubleConf(conf, ParameterNames.MAX_EMAIL);
            probAnotherBrowser = doubleConf(conf, ParameterNames.OTHER_BROWSER_RATIO);
            minTextSize = intConf(conf, ParameterNames.MIN_TEXT_SIZE);
            maxTextSize = intConf(conf, ParameterNames.MAX_TEXT_SIZE);
            minCommentSize = intConf(conf, ParameterNames.MIN_COMMENT_SIZE);
            maxCommentSize = intConf(conf, ParameterNames.MAX_COMMENT_SIZE);
            ratioReduceText = doubleConf(conf, ParameterNames.REDUCE_TEXT_RATIO);
            minLargePostSize = intConf(conf, ParameterNames.MIN_LARGE_POST_SIZE);
            maxLargePostSize = intConf(conf, ParameterNames.MAX_LARGE_POST_SIZE);
            minLargeCommentSize = intConf(conf, ParameterNames.MIN_LARGE_COMMENT_SIZE);
            maxLargeCommentSize = intConf(conf, ParameterNames.MAX_LARGE_COMMENT_SIZE);
            ratioLargePost = doubleConf(conf, ParameterNames.LARGE_POST_RATIO);
            ratioLargeComment = doubleConf(conf, ParameterNames.LARGE_COMMENT_RATIO);
            maxNumLike = intConf(conf, ParameterNames.MAX_NUM_LIKE);
            maxNumPhotoAlbumsPerMonth = intConf(conf, ParameterNames.MAX_PHOTOALBUM);
            maxNumPhotoPerAlbums = intConf(conf, ParameterNames.MAX_PHOTO_PER_ALBUM);
            maxNumGroupCreatedPerPerson = intConf(conf, ParameterNames.USER_MAX_GROUP);
            maxGroupSize = intConf(conf, ParameterNames.MAX_GROUP_MEMBERS);
            groupModeratorProb = doubleConf(conf, ParameterNames.GROUP_MODERATOR_RATIO);
            maxNumGroupPostPerMonth = intConf(conf, ParameterNames.GROUP_MAX_POST_MONTH);
            missingRatio = doubleConf(conf, ParameterNames.MISSING_RATIO);
            probDiffIPinTravelSeason = doubleConf(conf, ParameterNames.DIFFERENT_IP_IN_TRAVEL_RATIO);
            probDiffIPnotTravelSeason = doubleConf(conf, ParameterNames.DIFFERENT_IP_NOT_TRAVEL_RATIO);
            probUnCorrelatedCompany = doubleConf(conf, ParameterNames.COMPANY_UNCORRELATED_RATIO);
            probUnCorrelatedOrganisation = doubleConf(conf, ParameterNames.UNIVERSITY_UNCORRELATED_RATIO);
            probTopUniv = doubleConf(conf, ParameterNames.BEST_UNIVERSTY_RATIO);
            maxNumPopularPlaces = intConf(conf, ParameterNames.MAX_POPULAR_PLACES);
            probPopularPlaces = doubleConf(conf, ParameterNames.POPULAR_PLACE_RATIO);
            tagCountryCorrProb = doubleConf(conf, ParameterNames.TAG_UNCORRELATED_COUNTRY);
            flashmobTagsPerMonth = intConf(conf, ParameterNames.FLASHMOB_TAGS_PER_MONTH);
            probInterestFlashmobTag = doubleConf(conf, ParameterNames.PROB_INTEREST_FLASHMOB_TAG);
            probRandomPerLevel = doubleConf(conf, ParameterNames.PROB_RANDOM_PER_LEVEL);
            maxNumFlashmobPostPerMonth = intConf(conf, ParameterNames.MAX_NUM_FLASHMOB_POST_PER_MONTH);
            maxNumGroupFlashmobPostPerMonth = intConf(conf, ParameterNames.MAX_NUM_GROUP_FLASHMOB_POST_PER_MONTH);
            maxNumTagPerFlashmobPost = intConf(conf, ParameterNames.MAX_NUM_TAG_PER_FLASHMOB_POST);
            flashmobTagMinLevel = doubleConf(conf, ParameterNames.FLASHMOB_TAG_MIN_LEVEL);
            flashmobTagMaxLevel = doubleConf(conf, ParameterNames.FLASHMOB_TAG_MAX_LEVEL);
            flashmobTagDistExp = doubleConf(conf, ParameterNames.FLASHMOB_TAG_DIST_EXP);
            blockSize = intConf(conf, ParameterNames.BLOCK_SIZE);

            numPersons = Long.parseLong(conf.get("generator.numPersons"));
            startYear = Integer.parseInt(conf.get("generator.startYear"));
            numYears = Integer.parseInt(conf.get("generator.numYears"));
            delta = Integer.parseInt(conf.get("generator.delta"));
            knowsGeneratorName = conf.get("generator.knowsGenerator");
            personSimularity = conf.get("generator.person.similarity");
            degreeDistributionName = conf.get("generator.degreeDistribution");

            System.out.println(" ... Num Persons " + numPersons);
            System.out.println(" ... Start Year " + startYear);
            System.out.println(" ... Num Years " + numYears);
        } catch (Exception e) {
            System.out.println("Error reading scale factors or conf");
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static double scale(long numPersons, double mean) {
        return Math.log10(mean * numPersons / 2 + numPersons);
    }

    public static DegreeDistribution getDegreeDistribution() {

        DegreeDistribution output;
        switch (degreeDistributionName) {
            case "Facebook":
                output = new FacebookDegreeDistribution();
                break;
            case "Zipf":
                output = new ZipfDistribution();
                break;
            default:
                throw new IllegalStateException("Unexpected degree distribution: " + degreeDistributionName);
        }

        return output;
    }

    public static String getKnowsGenerator() {
        String output;
        if ("Distance".equals(knowsGeneratorName)) {
            output = "ldbc.snb.datagen.generator.generators.knowsgenerators.DistanceKnowsGenerator";
        } else {
            throw new IllegalStateException("Unexpected degree distribution: " + knowsGeneratorName);
        }

        return output;
    }

    public static Person.PersonSimilarity getPersonSimularity() {

        Person.PersonSimilarity output;
        switch (personSimularity) {
            case "GeoDistance":
                output = new GeoDistanceSimilarity();
                break;
            case "Interests":
                output = new InterestsSimilarity();
                break;
            default:
                throw new IllegalStateException("Unexpected person simularity: " + personSimularity);
        }

        return output;
    }

    public static int getNumberOfMonths() {
        return numYears * 12;
    }

}

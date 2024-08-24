package com.example.test.fixture;

import com.example.car.entity.Car;
import com.github.curiousoddman.rgxgen.RgxGen;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CarTestFixture {
    private static final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
            .build();

    private static final RgxGen nameRgxGen = RgxGen.parse(Car.NAME_REG_EXP);

    private static String createBadWordRegex(List<String> badWords) {
        final String joinedBadWords = badWords.stream()
                .map(badWord -> badWord + "[a-zA-Z0-9]{0," + (Car.NAME_MAX_LENGTH - badWord.length()) + "}") // 비속어로 시작하고 남은 길이만큼 임의의 문자열 생성
                .collect(Collectors.joining("|"));
        return "(" + joinedBadWords + ")";
    }

    public static String getBadWordNameString(List<String> badWords) {
        final String regex = createBadWordRegex(badWords);
        final RgxGen rgxGen = RgxGen.parse(regex);

        return fixtureMonkey.giveMeBuilder(String.class)
                .set((Supplier<String>) rgxGen::generate)
                .sample();
    }

    // 비속어로 시작하는 문자열 리스트 생성
    public static List<String> getBadWordNameStrings(List<String> badWords, int size) {
        final String regex = createBadWordRegex(badWords);
        final RgxGen rgxGen = RgxGen.parse(regex);

        return fixtureMonkey.giveMeBuilder(String.class)
                .set((Supplier<String>) rgxGen::generate)
                .sampleList(size);
    }

    public static String getNameString() {
        return fixtureMonkey.giveMeBuilder(String.class)
                .set((Supplier<String>) nameRgxGen::generate)
                .sample();
    }

    public static List<String> getNameStrings(int size) {
        return fixtureMonkey.giveMeBuilder(String.class)
                .set((Supplier<String>) nameRgxGen::generate)
                .sampleList(size);
    }


    public static Car getCar() {
        return fixtureMonkey.giveMeBuilder(Car.class)
                .set("name", (Supplier<String>) nameRgxGen::generate)
                .sample();
    }

    public static List<Car> getCars(int size) {
        return getNameStrings(size).stream()
                .map(name -> Car.builder().name(name).build())
                .collect(Collectors.toList());
    }

    public static Car getNotSavedCar() {
        return fixtureMonkey.giveMeBuilder(Car.class)
                .set("id", null)
                .set("createdAt", null)
                .set("updatedAt", null)
                .set("deletedAt", null)
                .set("name", (Supplier<String>) nameRgxGen::generate)
                .sample();
    }

    public static List<Car> getNotSavedCars(int size) {
        return fixtureMonkey.giveMeBuilder(Car.class)
                .set("id", null)
                .set("createdAt", null)
                .set("updatedAt", null)
                .set("deletedAt", null)
                .set("name", (Supplier<String>) nameRgxGen::generate)
                .sampleList(size);
    }
}

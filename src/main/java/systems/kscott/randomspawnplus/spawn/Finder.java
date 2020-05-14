package systems.kscott.randomspawnplus.spawn;

import lombok.Getter;

public class Finder {

    @Getter
    private static Finder instance;

    public static void initialize() {
        instance = new Finder();
    }

}

package systems.kscott.randomspawnplus.spawn;

import lombok.Getter;

public class Cacher {

    @Getter
    private static Cacher instance;

    public static void initialize() {
        instance = new Cacher();
    }


}

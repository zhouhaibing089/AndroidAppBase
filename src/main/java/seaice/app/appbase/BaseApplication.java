package seaice.app.appbase;

import android.app.Application;

import dagger.ObjectGraph;

public abstract class BaseApplication extends Application {

    private static ObjectGraph objectGraph;

    public static void inject(Object obj) {
        objectGraph.inject(obj);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        objectGraph = ObjectGraph.create(getModules());
    }

    public abstract Object[] getModules();
}

package de.mkristian.gwt.rails;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceController.Delegate;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

public class BaseModule extends BaseGinModule {
    @Override
    protected void configure() {
        super.configure();
        bind(PlaceController.class).toProvider(
                PlaceControllerProvider.class).in(Singleton.class);
        //bind(Delegate.class).to(DefaultDelegate.class);
        bind(PlaceHistoryHandler.class).toProvider(
                PlaceHistoryHandlerProvider.class).in(Singleton.class);
        bind(ActivityManager.class).toProvider(
                ActivityManagerProvider.class).in(Singleton.class);
    }
    
    public static class PlaceControllerProvider implements Provider<PlaceController> {

        private final EventBus eventBus;
        private final Delegate delegate;

        @Inject
        public PlaceControllerProvider(EventBus eventBus, Delegate delegate) {
            this.eventBus = eventBus;
            this.delegate = delegate;
        }

        public PlaceController get() {
            return new PlaceController((com.google.web.bindery.event.shared.EventBus)eventBus, delegate);
        }
    }

    public static class ActivityManagerProvider implements Provider<ActivityManager> {

        private final EventBus eventBus;
        private final ActivityMapper activityMapper;

        @Inject
        public ActivityManagerProvider(ActivityMapper activityMapper, EventBus eventBus) {
            this.eventBus = eventBus;
            this.activityMapper = activityMapper;
        }

        public ActivityManager get() {
            return new ActivityManager(activityMapper, eventBus);
        }
    }

    public static class PlaceHistoryHandlerProvider implements Provider<PlaceHistoryHandler> {

        private final PlaceHistoryMapper mapper;
        private final PlaceController placeController;
        private final EventBus eventBus;

        @Inject
        public PlaceHistoryHandlerProvider(PlaceHistoryMapper mapper, PlaceController placeController,
                EventBus eventBus) {
            this.mapper = mapper;
            this.placeController = placeController;
            this.eventBus = eventBus;
        }

        public PlaceHistoryHandler get() {
            PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(mapper);
            historyHandler.register(placeController, (com.google.web.bindery.event.shared.EventBus)eventBus, Place.NOWHERE);
            return historyHandler;
        }
    }

}
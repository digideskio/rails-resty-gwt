package <%= places_package %>;

import <%= base_package %>.ActivityPlace;
import <%= managed_package %>.ActivityFactory;

import com.google.gwt.activity.shared.Activity;

public class LoginPlace extends ActivityPlace<Void> {

    public static final LoginPlace LOGIN = new LoginPlace();

    private LoginPlace() {
        super(null, null);
    }

    @Override
    public Activity create(ActivityFactory factory) {
        return factory.create(this);
    }
}

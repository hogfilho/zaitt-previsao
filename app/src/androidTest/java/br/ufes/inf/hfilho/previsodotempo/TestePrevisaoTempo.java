package br.ufes.inf.hfilho.previsodotempo;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

import br.ufes.inf.hfilho.previsodotempo.ui.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class TestePrevisaoTempo {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(MainActivity.class, true, true);

    @Test
    public void testConfig() {
        while (true) {
            try {
                onView(withText("Atualizando Dados")).perform(click());
            } catch (NoMatchingViewException e) {
                break;
            }
        }

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText("Configurações")).perform(click());
    }

    @Test
    public void testDetails() {
        while (true) {
            try {
                onView(withText("Atualizando Dados")).perform(click());
            } catch (NoMatchingViewException e) {
                break;
            }
        }
        onView(nthChildOf(withId(R.id.l_dias), 0)).perform(click());
    }


    public static Matcher<View> nthChildOf(final Matcher<View> parentMatcher, final int childPosition) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with " + childPosition + " child view of type parentMatcher");
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view.getParent() instanceof ViewGroup)) {
                    return parentMatcher.matches(view.getParent());
                }

                ViewGroup group = (ViewGroup) view.getParent();
                return parentMatcher.matches(view.getParent()) && group.getChildAt(childPosition).equals(view);
            }
        };
    }

}

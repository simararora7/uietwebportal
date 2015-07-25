package uietwebportal.simararora.uietwebportal;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.desarrollodroide.libraryfragmenttransactionextended.FragmentTransactionExtended;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerFragment extends Fragment {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private String title = "Result";
    private int currentFragment;
    private FragmentManager fragmentManager;

    public void setUp(Toolbar toolbar, final DrawerLayout drawerLayout, final ActionBar actionBar) {
        this.drawerLayout = drawerLayout;
        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                MainActivity.closeKeyboard(getActivity(), drawerLayout.getWindowToken());
                try {
                    title = actionBar.getTitle().toString();
                } catch (Exception ignored) {

                }
                actionBar.setTitle("UIET Web Portal");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!title.isEmpty())
                    actionBar.setTitle(title);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                actionBarDrawerToggle.syncState();
            }
        });
//        drawerLayout.openDrawer(Gravity.START);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentManager = getActivity().getFragmentManager();
        if (savedInstanceState == null) {
            currentFragment = 0;
        }else{
            currentFragment = savedInstanceState.getInt("currentFragment");
        }
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        NavigationDrawerAdapter navigationDrawerAdapter = new NavigationDrawerAdapter(getActivity(), getData(), currentFragment);
        recyclerView.setAdapter(navigationDrawerAdapter);
        final ArrayList<NavigationDrawerIndividualView> views = navigationDrawerAdapter.getAllViews();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (currentFragment == position) {
                    closeDrawer();
                    return;
                }
                currentFragment = position;
                Fragment f = null, f1=null;
                switch (position) {
                    case 0:
                        f = new ResultFragment();
                        title = "Result";
                        views.get(0).getImageView().setColorFilter(Color.parseColor("#F44336"));
                        views.get(1).getImageView().clearColorFilter();
                        views.get(0).getTextView().setTextColor(Color.parseColor("#F44336"));
                        views.get(1).getTextView().setTextColor(Color.GRAY);
                        break;
                    case 1:
                        f = new AttendanceFragment();
                        title = "Attendance";
                        views.get(1).getImageView().setColorFilter(Color.parseColor("#F44336"));
                        views.get(0).getImageView().clearColorFilter();
                        views.get(1).getTextView().setTextColor(Color.parseColor("#F44336"));
                        views.get(0).getTextView().setTextColor(Color.GRAY);
                        break;
                }
                f1 = ((MainActivity)getActivity()).getCurrentFragment();
                ((MainActivity)getActivity()).setCurrentFragment(f);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(getActivity(), fragmentTransaction, f1, f, R.id.llDisplay);
                fragmentTransactionExtended.addTransition(FragmentTransactionExtended.SCALEXY);
                fragmentTransactionExtended.commit();
                closeDrawer();
            }

            @Override
            public void onLongClick(View view, int position) {
                //Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();

            }
        }));
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentFragment", currentFragment);
        super.onSaveInstanceState(outState);
    }

    public int getCurrentFragment(){
        return  currentFragment;
    }

    public static List<NavigationDrawerItem> getData() {
        List<NavigationDrawerItem> data = new ArrayList<>();
        data.add(new NavigationDrawerItem(R.drawable.perm_group_user_dictionary_write, "Result"));
        data.add(new NavigationDrawerItem(R.drawable.perm_group_calendar, "Attendance"));
        return data;
    }

    public void closeDrawer() {
        drawerLayout.closeDrawers();
    }

    public DrawerLayout getDrawer() {
        return drawerLayout;
    }

    private class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }
    }

    private static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }


}

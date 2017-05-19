package com.example.yuzelli.yiai.uitls;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollectorUtil {
    public static List<Activity> activities = new ArrayList<Activity>();


    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static void removeTwoActivity(){

       for(int i = activities.size()-1;i>activities.size()-3;i--){
           if (!activities.get(i).isFinishing()) {
               activities.get(i).finish();
           }
       }
    }
}

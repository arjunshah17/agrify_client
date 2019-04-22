package com.example.agrify.activity.Utils;

import com.example.agrify.activity.order.model.Rating;

import java.util.ArrayList;
import java.util.List;

public class RatingUtils {
    public static double getAverageRating(ArrayList<Rating> ratings) {
        double sum = 0.0;

        for (Rating rating : ratings) {
            sum += rating.getRating();
        }

        return sum / ratings.size();
    }
}

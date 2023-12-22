package dev.xernas.menulib.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaticSlots {

    public final static List<Integer> NONE = new ArrayList<>();
    public final static List<Integer> MIDDLE_BUTTONS = new ArrayList<>();
    public final static List<Integer> SPREAD_BUTTONS = new ArrayList<>();
    public final static List<Integer> BOTTOM = new ArrayList<>();
    public final static List<Integer> TOP = new ArrayList<>();
    public final static List<Integer> RIGHT = new ArrayList<>();
    public final static List<Integer> LEFT = new ArrayList<>();
    public final static List<Integer> STANDARD = new ArrayList<>();

    static {
        // MIDDLE BUTTONS
        MIDDLE_BUTTONS.add(48);
        MIDDLE_BUTTONS.add(49);
        MIDDLE_BUTTONS.add(50);
        // SPREAD BUTTONS
        SPREAD_BUTTONS.add(45);
        SPREAD_BUTTONS.add(49);
        SPREAD_BUTTONS.add(53);
        // BOTTOM
        for (int i = 45; i < 54; i++) {
            BOTTOM.add(i);
        }
        // TOP
        for (int i = 0; i < 9; i++) {
            TOP.add(i);
        }
        // RIGHT
        RIGHT.add(0);
        RIGHT.add(9);
        RIGHT.add(18);
        RIGHT.add(27);
        RIGHT.add(36);
        RIGHT.add(45);
        // LEFT
        LEFT.add(8);
        LEFT.add(17);
        LEFT.add(26);
        LEFT.add(35);
        LEFT.add(44);
        LEFT.add(53);
        // STANDARD
        STANDARD.addAll(combine(combine(RIGHT, LEFT), combine(TOP, BOTTOM)));
    }

    public static List<Integer> combine(List<Integer> list1, List<Integer> list2) {
        List<Integer> finalList = new ArrayList<>();
        finalList.addAll(list1);
        finalList.addAll(list2);
        return removeRecurringIntegers(finalList);
    }

    public static List<Integer> getStaticSlots(Integer... slots) {
        return new ArrayList<>(Arrays.asList(slots));
    }

    public static List<Integer> removeRecurringIntegers(List<Integer> list) {
        List<Integer> finalList = new ArrayList<>();
        for (Integer integer : list) {
            if (!finalList.contains(integer)) {
                if (integer >= 0 && integer < 54) {
                    finalList.add(integer);
                }
            }
        }
        return finalList;
    }

}

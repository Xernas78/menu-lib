package dev.xernas.menulib.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The StaticSlots class provides predefined static lists of integers representing
 * various slot configurations and utility methods for managing and manipulating slot lists.
 * <p>
 * The predefined slot lists include:<br>
 * - {@code BOTTOM}: A predefined list of slots located at the bottom.<br>
 * - {@code TOP}: A predefined list of slots located at the top.<br>
 * - {@code RIGHT}: A predefined list of slots located on the right side.<br>
 * - {@code LEFT}: A predefined list of slots located on the left side.<br>
 * - {@code STANDARD}: A combined list of slots from RIGHT, LEFT, TOP, and BOTTOM with duplicate removal.
 * <p>
 * Utility methods provided include:<br>
 * - combine: Combines two slot lists into one, ensuring no duplicates and filtering slots to a valid range.<br>
 * - getStaticSlots: Creates a new list from specified slot integers.<br>
 * - removeRecurringIntegers: Removes duplicate integers from a list and ensures numbers fall within a valid range.
 */
public class StaticSlots {
    
    /**
     * Generates a list of integers representing the top slots of an inventory based on its size.
     *
     * @param size the inventory size to consider for slot generation
     * @return a list of integers representing the top slots of the inventory
     */
    public static List<Integer> getTopSlots(InventorySize size) {
        List<Integer> top = new ArrayList<>();
        for (int i = 0; i < 9 && i < size.getSize(); i++) {
            top.add(i);
        }
        return top;
    }
    
    /**
     * Generates a list of integers representing the bottom slots of an inventory based on its size.
     *
     * @param size the inventory size to consider for slot generation
     * @return a list of integers representing the bottom slots of the inventory
     */
    public static List<Integer> getBottomSlots(InventorySize size) {
        List<Integer> bottom = new ArrayList<>();
        int sizeValue = size.getSize();
        int start = sizeValue - 9;
        if (start < 0) return bottom;
        for (int i = start; i < sizeValue; i++) {
            bottom.add(i);
        }
        return bottom;
    }
    
    /**
     * Generates a list of integers representing the left slots of an inventory based on its size.
     *
     * @param size the inventory size to consider for slot generation
     * @return a list of integers representing the left slots of the inventory
     */
    public static List<Integer> getLeftSlots(InventorySize size) {
        List<Integer> left = new ArrayList<>();
        int rows = size.getSize() / 9;
        for (int i = 0; i < rows; i++) {
            left.add(i * 9);
        }
        return left;
    }
    
    /**
     * Generates a list of integers representing the right slots of an inventory based on its size.
     *
     * @param size the inventory size to consider for slot generation
     * @return a list of integers representing the right slots of the inventory
     */
    public static List<Integer> getRightSlots(InventorySize size) {
        List<Integer> right = new ArrayList<>();
        int rows = size.getSize() / 9;
        for (int i = 0; i < rows; i++) {
            right.add(i * 9 + 8);
        }
        return right;
    }
    
    /**
     * Generates a list of integers representing the standard slots of an inventory based on its size.
     * The standard slots are a combination of top, bottom, left, and right slots with duplicates removed.
     *
     * @param size the inventory size to consider for slot generation
     * @return a list of integers representing the standard slots of the inventory
     */
    public static List<Integer> getStandardSlots(InventorySize size) {
        return combine(combine(getTopSlots(size), getBottomSlots(size)),
                combine(getLeftSlots(size), getRightSlots(size)));
    }
    
    /**
     * Combines two lists of integers into a single list, removing any duplicate integers and ensuring
     * all integers are within the range [0, 54).
     *
     * @param list1 the first list of integers to combine
     * @param list2 the second list of integers to combine
     * @return a new list of integers containing all unique values from both input lists
     * within the range [0, 54)
     */
    public static List<Integer> combine(List<Integer> list1, List<Integer> list2) {
        Set<Integer> combined = new LinkedHashSet<>();
        combined.addAll(list1);
        combined.addAll(list2);
        return new ArrayList<>(combined);
    }
    
    /**
     * Get a new list of integers from the provided slot integers.
     *
     * @param size the inventory size to consider for slot validation
     * @param type the type of static slots to retrieve
     * @return a new list of integers containing the specified slot integers
     */
    public static List<Integer> getStaticSlots(InventorySize size, Type type) {
        return switch (type) {
            case TOP -> getTopSlots(size);
            case BOTTOM -> getBottomSlots(size);
            case LEFT -> getLeftSlots(size);
            case RIGHT -> getRightSlots(size);
            case STANDARD -> getStandardSlots(size);
        };
    }
    
    /**
     * Removes duplicate integers from the provided list, ensuring all integers are unique,
     * and only includes integers within the range [0, size).
     *
     * @param list the input list containing integers, which may include duplicates
     * @param size the maximum slot index (exclusive)
     * @return a new list of integers that contains only unique values within the range [0, size)
     */
    public static List<Integer> removeRecurringIntegers(List<Integer> list, int size) {
        List<Integer> finalList = new ArrayList<>();
        for (Integer integer : list) {
            if (integer != null && integer >= 0 && integer < size && ! finalList.contains(integer)) {
                finalList.add(integer);
            }
        }
        return finalList;
    }
    
    public enum Type {
        TOP,
        BOTTOM,
        RIGHT,
        LEFT,
        STANDARD
    }
}
